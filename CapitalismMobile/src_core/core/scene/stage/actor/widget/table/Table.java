package core.scene.stage.actor.widget.table;

import java.util.ArrayList;
import java.util.List;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.batch.Batch;
import core.math.Rectangle;
import core.math.Vector2;
import core.scene.stage.Floor;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.WidgetGroup;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.RectangleAlignable;
import core.scene.stage.actor.widget.utils.Layout;

/**
 * Table은 HTML의 Table처럼 테이블 형태로 Actor를 배치한다. Table은 Layout의 결정체로써 
 * 삽입된 TableCell의 Actor가 Layout을 구현하는 경우 Layout의 선호, 최소, 최대 사이즈를 모두 
 * 고려하여 각 Actor의 사이즈를 설정한다.</p>
 * 
 * TableCell의 expand는 컨테이너의 사이즈와 Table의 사이즈에 차이가 존재할 때, 남은 공간을 
 * TableCell이 점유하기 위해 사용된다. 여러 TableCell이 expand를 지정할 경우 expand 값에 따라 
 * 균등하게 분배된다.</p>
 * 
 * TableCell의 fill은 TableCell의 사이즈와 포함된 Actor의 사이즈에 차이가 존재할 때, 남은 공간을 
 * Actor가 점유하기 위해 사용된다. {@link TableCell#actorMaxSize(float, float)}가 설정되어 있다면 
 * 그 이상 점유하지 않는다.</p>
 * 
 * 부모가 WidgetGroup이 아닌 단순 Group일 경우 Table의 사이즈를 명시적으로 지정하지 않으면 
 * {@link #layout()}에서 최소 사이즈로 설정되는 것을 주의하라.</p>
 * 
 * TableCell에 삽입되는 Actor가 Layout을 구현하는 경우 getWidth()와 getHeight()로 얻어지는 
 * 실제 사이즈는 무시되며 Layout과 관련된 최소, 선호, 최대 사이즈만 고려된다. 이들 사이즈를 
 * 수정하기 위해 재정의하기 보다는 TableCell의 관련 메서드를 이용하면 손쉽게 조정할 수 있다.</p>
 * 
 * 선호 사이즈의 경우 {@link Layout#setPrefSize(Float, Float)} 등으로 직접 입력할 수도 있는데 
 * TableCell을 통해 선호 사이즈를 설정한 경우 TableCell에서 설정한 값이 우선적으로 적용된다. 
 * 따라서, Table에 삽입할 경우에는 TableCell의 선호 사이즈 설정을 이용하는 것이 바람직하다.</p>
 * 
 * 기본적으로 Widget 또는 WidgetGroup의 최소 사이즈는 선호 사이즈를 다시 호출하도록 되어 있는데, 
 * 셀을 이용하여 선호 사이즈를 설정한 경우 최소 사이즈를 참조하는 과정에서 셀에 설정한 선호 사이즈가 
 * 아닌 Widget 또는 WidgetGroup 자체의 선호 사이즈를 참조한다는 것에 주의하라. 이것을 피하기 위해서는 
 * 셀의 선호 사이즈를 설정할 때 최소 사이즈도 함께 설정해 주는 방법이 있을 수 있다.</p>
 * 
 * 한편, 패딩에 음수를 적용하여 경계를 넘어 출력되도록 하는 특수한 활용이 가능하다. 특히, 
 * TableCell의 경우 전체 레이아웃에 영향을 끼치지 않으면서 내부 Actor를 자유롭게 배치할 수 
 * 있다.</p>
 * 
 * @author 김현우
 */
@SuppressWarnings("unchecked")
public abstract class Table<T extends Table<T>> extends WidgetGroup<T> implements RectangleAlignable<T> {
	
	public static Color4 sTableContainerDebugColor = Color4.yellow();
	public static Color4 sTableCellDebugColor = Color4.red();
	public static Color4 sTableActorDebugColor = Color4.green();
	
	private static final List<TableCell> TMP_CELL_LIST = new ArrayList<TableCell>();
	
	private TableCell mDefaultCell = new TableCell(this);
	private TableCell mDefaultRowCell = new TableCell(this);
	private List<TableCell> mDefaultColCellList = new ArrayList<TableCell>();
	
	private List<TableCell> mCellList = new ArrayList<TableCell>();
	
	/** 행의 개수 */
	private int mRows;
	/** 열의 개수 */
	private int mColumns;
	
	/** 현재 행의 인덱스이며 행의 개수보다 작거나 같다. */
	private int mCurrentRow;
	
	/** 테이블 내의 컨테이너의 정렬 */
	private Align mAlign = new Align();
	
	/** 테이블 내의 컨테이너의 상단 패딩 */
	private float mPadTop;
	/** 테이블 내의 컨테이너의 좌측 패딩 */
	private float mPadLeft;
	/** 테이블 내의 컨테이너의 우측 패딩 */
	private float mPadRight;
	/** 테이블 내의 컨테이너의 하단 패딩 */
	private float mPadBottom;
	
	private Rectangle mContainerRectangle = new Rectangle();
	
	/*package*/ float[] mRowMinHeights;
	/*package*/ float[] mRowPrefHeights;
	/*package*/ float[] mRowHeights;
	
	/*package*/ float[] mColMinWidths;
	/*package*/ float[] mColPrefWidths;
	/*package*/ float[] mColWidths;
	
	/*package*/ float[] mExpandWidths;
	/*package*/ float[] mExpandHeights; 
	
	/*package*/ float mContainerMinWidth;
	/*package*/ float mContainerMinHeight;
	
	/*package*/ float mContainerPrefWidth;
	/*package*/ float mContainerPrefHeight;
	
	/** TableCell의 점유 공간을 기록하는 맵 */
	private boolean[][] mOccupyMap;
	
	private boolean mSizeInvalidated = true;
	
	private int mTableDebug;

	public Table() {
		this(null);
	}
	
	public Table(Costume costume) {
		super(costume);
		mDefaultCell.reset();
	}
	
	/** Actor가 없는 빈 TableCell을 추가한다. 공간을 점유하는 용도 등에 사용할 수 있다. */
	public TableCell addCell() {
		return add(new TableCell(this, null));
	}
	
	public TableCell addCell(Actor<?> actor) {
		return add(new TableCell(this, actor));
	}
	
	public TableCell addCell(TableCell cell) {
		return add(cell);
	}
	
	protected TableCell add(TableCell cell) {
		getNextCellPos(VECTOR);
		cell.mRow = (int) VECTOR.x;
		cell.mColumn = (int) VECTOR.y;
		
		applyDefaults(cell);
		
		List<TableCell> cellList = mCellList;
		if(!cellList.isEmpty()) {
			TableCell prevCell = cellList.get(cellList.size()-1); 
			prevCell.mNextCell = cell;
			cell.mPrevCell = prevCell; 
		}
		
		cellList.add(cell);
		// super을 사용하는 이유는 addChild(...)를 재정의하여 예외처리를 하는 경우 
		// 메서드를 통해서도 간접적으로 호출될 수 있기 때문이다.
		if(cell.mActor != null) super.addChild(cell.mActor);
		
		updateTableState();
		invalidate();
		
		return cell;
	}
	
	protected void applyDefaults(TableCell cell) {
		// 모든 TableCell에 대한 디폴트 설정으로 세팅한다.
		cell.set(mDefaultCell);
		// 그다음, 열에 대한 디폴트 설정이 있으면 추가한다.
		if(mDefaultColCellList.size() > cell.mColumn) {
			TableCell defaultColCell = mDefaultColCellList.get(cell.mColumn);
			// 디폴트 열의 설정을 추가한다.
			if(defaultColCell != null) cell.merge(defaultColCell);
		}
		// 마지막으로, 행에 대한 디폴트 설정을 추가한다.
		cell.merge(mDefaultRowCell);
	}
	
	private void getNextCellPos(Vector2 pos) {
		if(mCellList.isEmpty()) {
			pos.set(0, 0);
			return;
		}
		
		int currentRow = mCurrentRow;
		// 현재 행이 이미 존재하는 행을 넘어설 경우
		if(mRows < currentRow+1) {
			pos.set(currentRow, 0);
			return;
		}
		
		int columns = mColumns;
		// 점유맵에서 현재 행의 첫번째 열에서 시작하여 비어있는 공간을 찾는다.
		for(int i=0; i<columns; i++) {
			if(!mOccupyMap[currentRow][i]) {
				pos.set(currentRow, i);
				return;
			}
		}
		
		// 비어있는 공간이 없으면 현재 행에서 새로운 열에 배치한다.
		pos.set(currentRow, columns);
	}
	
	public TableCell getCellByActor(Actor<?> actor) {
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			if(cell.mActor == actor) return cell;
		}
		return null;
	}
	
	public TableCell getCell(int index) {
		return mCellList.get(index);
	}
	
	public TableCell getFirstRowCell(int row) {
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			if(cell.mRow == row) return cell;
		}
		return null;
	}
	
	public TableCell getFirstColCell(int column) {
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			if(cell.mColumn == column) return cell;
		}
		return null;
	}
	
	public List<TableCell> getCellList() {
		return mCellList;
	}
	
	public Actor<?> getCellActor(int index) {
		return mCellList.get(index).getActor();
	}
	
	@Override
	public boolean removeChild(Actor<?> child) {
		if(!super.removeChild(child)) return false;
		TableCell cell = getCellByActor(child);
		if(cell != null) cell.mActor = null;
		return true;
	}
	
	@Override
	public T clearChildren() {
		super.clearChildren();
		mCellList.clear();
		mRows = 0;
		mColumns = 0;
		mCurrentRow = 0;
		return (T) this;
	}
	
	/** {@link HorizontalTable}과 {@link VerticalTable}에서 Table을 재생성할 때 사용된다. */
	/*package*/ void rebuild() {
		final List<TableCell> cellList = TMP_CELL_LIST;
		cellList.clear();
		cellList.addAll(mCellList);
		
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			Actor<?> actor = getCellActor(i);
			super.removeChild(actor);
		}
		
		mCellList.clear();
		mRows = 0;
		mColumns = 0;
		mCurrentRow = 0;

		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			cell.mRow = 0;
			cell.mColumn = 0;
			cell.mEndRow = false;
			addCell(cellList.get(i));
		}
	}
	
	/** 
	 * 현재 행의 인덱스를 1증가시켜 다음 행을 시작한다. 그리고 추가된 행에 대한 
	 * 디폴트 TableCell을 반환한다. 
	 */
	public TableCell row() {
		if(!mCellList.isEmpty()) {
			mCurrentRow++;
			mCellList.get(mCellList.size()-1).mEndRow = true;
		}
		mDefaultRowCell.clear();
		return mDefaultRowCell;
	}

	/** index 열에 대한 디폴트 TableCell을 얻는다. TableCell을 추가하기 전에만 호출할 수 있다. */
	public TableCell col(int index) {
		if(!mCellList.isEmpty())
			throw new IllegalStateException("This method must be called before adding cells.");
		
		List<TableCell> defaultColCellList = mDefaultColCellList;
		int n = defaultColCellList.size();
		TableCell cell = (n > index)? defaultColCellList.get(index) : null;
		if(cell == null) {
			cell = new TableCell(this);
			if(n <= index) {
				for(int i=n; i<index; i++)
					defaultColCellList.add(null);
				defaultColCellList.add(cell);
			} else
				defaultColCellList.set(index, cell);
		}
		return cell;
	}

	/** 모든 TableCell에 대한 디폴트 TableCell을 얻는다. TableCell을 추가하기 전에만 호출할 수 있다. */
	public TableCell all() {
		if(!mCellList.isEmpty())
			throw new IllegalStateException("This method must be called before adding cells.");
		
		return mDefaultCell;
	}
	
	/*package*/ void updateTableState() {
		int rows = 0;
		int columns = 0;
		
		// 현재의 행과 열의 개수를 구한다.
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			if(rows < cell.mRow + getCellRowSpan(cell))
				rows = cell.mRow + getCellRowSpan(cell);
			if(columns < cell.mColumn + getCellColSpan(cell))
				columns = cell.mColumn + getCellColSpan(cell);
		}
		
		boolean dirty = false;

		// 행과 열의 개수에 변화가 있다면 관련 변수를 적절히 수정한다.
		if(mRows != rows) {
			mRows = rows;
			mRowMinHeights = new float[rows];
			mRowPrefHeights = new float[rows];
			mRowHeights = new float[rows];
			mExpandHeights = new float[rows];
			mSizeInvalidated = true;
			dirty = true;
		} else {
			for(int i=0; i<rows; i++) {
				mRowMinHeights[i] = 0f;
				mRowPrefHeights[i] = 0f;
				mRowHeights[i] = 0f;
				mExpandHeights[i] = 0f;
			}
		}
		
		if(mColumns != columns) {
			mColumns = columns;
			mColMinWidths = new float[columns];
			mColPrefWidths = new float[columns];
			mColWidths = new float[columns];
			mExpandWidths = new float[columns];
			mSizeInvalidated = true;
			dirty = true;
		} else {
			for(int i=0; i<columns; i++) {
				mColMinWidths[i] = 0f;
				mColPrefWidths[i] = 0f;
				mColWidths[i] = 0f;
				mExpandWidths[i] = 0f;
			}
		}
		
		// 행이나 열의 개수에 변화가 있다면 점유맵을 새로 만든다.
		if(dirty) {
			mOccupyMap = new boolean[rows][columns];
		} else { // 변화가 없다면 점유맵을 초기화한다.
			for(int i=0; i<rows; i++) {
				for(int j=0; j<columns; j++) {
					mOccupyMap[i][j] = false;
				}
			}
		}
		
		// 점유맵 업데이트
		for(int k=0; k<n; k++) {
			TableCell cell = cellList.get(k);
			for(int i=cell.mRow; i<cell.mRow + getCellRowSpan(cell); i++) {
				for(int j=cell.mColumn; j<cell.mColumn + getCellColSpan(cell); j++) {
					if(mOccupyMap[i][j]) 
						throw new IllegalStateException("mOccupyMap[" + i + "][" + j + "] is already occupied.");
					mOccupyMap[i][j] = true;
				}
			}
		}
	}
	
	@Override
	public T center() {
		mAlign.center();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T top() {
		mAlign.top();
		invalidate();
		return (T) this;
	}

	@Override
	public T left() {
		mAlign.left();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T right() {
		mAlign.right();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T bottom() {
		mAlign.bottom();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T north() {
		mAlign.north();
		invalidate();
		return (T) this;
	}

	@Override
	public T west() {
		mAlign.west();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T east() {
		mAlign.east();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T south() {
		mAlign.south();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T northWest() {
		mAlign.northWest();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T northEast() {
		mAlign.northEast();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T southWest() {
		mAlign.southWest();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T southEast() {
		mAlign.southEast();
		invalidate();
		return (T) this;
	}
	
	@Override
	public Align getAlign() {
		return mAlign;
	}
	
	@Override
	public T setAlign(Align align) {
		mAlign.set(align);
		invalidate();
		return (T) this;
	}
	
	public Drawable getDrawable() {
		return mDrawable;
	}
	
	public T setDrawable(Drawable drawable) {
		return setDrawable(drawable, true);
	}
	
	public T setDrawable(Drawable drawable, boolean padding) {
		if(drawable != null && padding) {
			padTop(drawable.getPadTop());
			padLeft(drawable.getPadLeft());
			padRight(drawable.getPadRight());
			padBottom(drawable.getPadBottom());
		}
		mDrawable = drawable;
		invalidateHierarchy();
		return (T) this;
	}

	public T pad(float pad) {
		mPadTop = pad;
		mPadLeft = pad;
		mPadRight = pad;
		mPadBottom = pad;
		invalidate();
		return (T) this;
	}
	
	public T padTop(float pad) {
		mPadTop = pad;
		invalidate();
		return (T) this;
	}
	
	public T padLeft(float pad) {
		mPadLeft = pad;
		invalidate();
		return (T) this;
	}
	
	public T padRight(float pad) {
		mPadRight = pad;
		invalidate();
		return (T) this;
	}
	
	public T padBottom(float pad) {
		mPadBottom = pad;
		invalidate();
		return (T) this;
	}
	
	/** 
	 * 모든 행 또는 열에 대해 해당 행 또는 열에 대한 인덱스를 가진 Cell이 존재하지 않으면 
	 * 그 행 또는 열은 제거한다.
	 */
	private void trim() {
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		if(n == 0) return;
		
out1:
		// Cell이 위치(span이 아닌)하지 않는 행을 하단 행에서부터 제거한다.
		for(int i=mRows-1; i>-1; i--) {
			for(int j=n-1; j>-1; j--) {
				TableCell cell = cellList.get(j);
				if(i == cell.mRow) continue out1;
			}
			removeRow(i);
		}
		
out2:
		// Cell이 위치(span이 아닌)하지 않는 열을 우측 열에서부터 제거한다.
		for(int i=mColumns-1; i>-1; i--) {
			for(int j=n-1; j>-1; j--) {
				TableCell cell = cellList.get(j);
				if(i == cell.mColumn) continue out2;
			}
			removeCol(i);
		}
		
		// 현재 행의 인덱스를 마지막 TableCell의 행으로 수정한다.
		mCurrentRow = cellList.get(n-1).mRow;
		
		updateTableState();
	}
	
	private void removeRow(int row) {
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			// 삭제할 행보다 상단에 위치하면서 전체 행의 span 영역에 삭제할 행이 포함되면
			// 행의 span을 1감소시킨다.
			if(cell.mRow < row && row <= (cell.mRow+getCellRowSpan(cell)-1))
				cell.mRowSpan--;
			// 삭제할 행보다 하단에 위치해 있으면 행의 인덱스를 1감소시킨다.
			if(cell.mRow > row) cell.mRow--;
		}
	}
	
	private void removeCol(int col) {
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			// 삭제할 열보다 좌측에 위치하면서 전체 열의 span 영역에 삭제할 열이 포함되면
			// 열의 span을 1감소시킨다.
			if(cell.mColumn < col && col <= (cell.mColumn+getCellColSpan(cell)-1))
				cell.mColSpan--;
			// 삭제할 열보다 우측에 위치해 있으면 열의 인덱스를 1감소시킨다.
			if(cell.mColumn > col) cell.mColumn--;
		}
	}
	
	@Override
	public void invalidate() {
		mSizeInvalidated = true;
		super.invalidate();
	}
	
	@Override
	public float getMinWidth() {
		if(mSizeInvalidated) computeSize();
		return mContainerMinWidth;
	}
	
	@Override
	public float getMinHeight() {
		if(mSizeInvalidated) computeSize();
		return mContainerMinHeight;
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		if(mSizeInvalidated) computeSize();
		float prefWidth = mContainerPrefWidth;
		if(mDrawable != null) prefWidth = Math.max(prefWidth, mDrawable.getWidth());
		return prefWidth;
	}
	
	@Override
	protected float getDefaultPrefHeight() {
		if(mSizeInvalidated) computeSize();
		float prefHeight = mContainerPrefHeight;
		if(mDrawable != null) prefHeight = Math.max(prefHeight, mDrawable.getHeight());
		return prefHeight;
	}
	
	protected void computeSize() {
		mSizeInvalidated = false;
		
		// 테이블을 정리한다.
		trim();
		
		float[] rowMinHeights = mRowMinHeights;
		float[] rowPrefHeights = mRowPrefHeights;
		
		float[] colMinWidths = mColMinWidths;
		float[] colPrefWidths = mColPrefWidths;
		
		float[] expandHeights = mExpandHeights;
		float[] expandWidths = mExpandWidths;
		
		final int rows = mRows;
		final int cols = mColumns;
		
		float containerPadTop = mPadTop;
		float containerPadLeft = mPadLeft;
		float containerPadRight = mPadRight;
		float containerPadBottom = mPadBottom;
		
		float containerPadHeight = containerPadTop + containerPadBottom;
		float containerPadWidth = containerPadLeft + containerPadRight;
		
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		
		//********************************************************************
		// span이 1인 경우에 대해 각 행의 높이와 각 열의 너비를 구한다. expand도 포함한다.
		//************************************************************************************
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			
			int rowSpan = getCellRowSpan(cell);
			int colSpan = getCellColSpan(cell);
			// 행과 열에 대한 span이 모두 2 이상인 경우는 일단 무시한다.
			if(rowSpan > 1 && colSpan > 1) continue;
			
			// 행의 span이 1인 경우에만 높이를 계산한다. span이 2이상인 경우는 
			// 이후에 계산한다. span이 1인 경우에 우선권을 주기 위해서이다.
			if(rowSpan == 1) {
				int row = cell.mRow;
				float padHeight = cell.mPadTop + cell.mPadBottom;
				float expandY = cell.mExpandY;
				
				float cellMinHeight = getCellMinHeight(cell);
				float cellPrefHeight = getCellPrefHeight(cell);
				if(cellPrefHeight < cellMinHeight) cellPrefHeight = cellMinHeight;
				
				float actorMinHeight = getActorMinHeight(cell);
				float actorPrefHeight = getActorPrefHeight(cell);
				Float actorMaxHeight = getActorMaxHeight(cell);
				if(actorPrefHeight < actorMinHeight) actorPrefHeight = actorMinHeight;
				if(actorMaxHeight != null && actorPrefHeight > actorMaxHeight) actorPrefHeight = actorMaxHeight;

				float minHeight = Math.max(cellMinHeight, actorMinHeight);
				float prefHeight = Math.max(cellPrefHeight, actorPrefHeight);
				
				rowMinHeights[row] = Math.max(rowMinHeights[row], minHeight+padHeight);
				rowPrefHeights[row] = Math.max(rowPrefHeights[row], prefHeight+padHeight);
				expandHeights[row] = Math.max(expandHeights[row], expandY);
			}
	
			// 열의 span이 1인 경우에만 너비를 저장한다. span이 2이상인 경우는 
			// 이후에 계산한다. span이 1인 경우에 우선권을 주기 위해서이다.
			if(colSpan == 1) {
				int col = cell.mColumn;
				float padWidth = cell.mPadLeft + cell.mPadRight;
				float expandX = cell.mExpandX;
				
				float cellMinWidth = getCellMinWidth(cell);
				float cellPrefWidth = getCellPrefWidth(cell);
				if(cellPrefWidth < cellMinWidth) cellPrefWidth = cellMinWidth;
				
				float actorMinWidth = getActorMinWidth(cell);
				float actorPrefWidth = getActorPrefWidth(cell);
				Float actorMaxWidth = getActorMaxWidth(cell);
				if(actorPrefWidth < actorMinWidth) actorPrefWidth = actorMinWidth;
				if(actorMaxWidth != null && actorPrefWidth > actorMaxWidth) actorPrefWidth = actorMaxWidth;
				
				float minWidth = Math.max(cellMinWidth, actorMinWidth);
				float prefWidth = Math.max(cellPrefWidth, actorPrefWidth);
				
				colMinWidths[col] = Math.max(colMinWidths[col], minWidth+padWidth);
				colPrefWidths[col] = Math.max(colPrefWidths[col], prefWidth+padWidth);
				expandWidths[col] = Math.max(expandWidths[col], expandX);
			}
		}
		
		//********************************************************************
		// span이 2이상인 경우에 대해 각 행의 expand 높이와 각 열의 expand 너비를 추가적으로 계산한다.
		//************************************************************************************
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			
			int rowSpan = getCellRowSpan(cell);
			int colSpan = getCellColSpan(cell);
			// 행과 열에 대한 span이 모두 1인 경우는 무시한다. 이미 앞에서 계산이 끝났기 때문이다.
			if(rowSpan == 1 && colSpan == 1) continue;
			
			float expandX = cell.mExpandX;
			float expandY = cell.mExpandY;
			// expand가 지정되지 않았으면 무시한다.
			if(expandX == 0f && expandY == 0f) continue;
			
			// 행의 span이 2이상인 경우
			if(rowSpan > 1) {
				int row = cell.mRow;
				float extraExpand = expandY;
				
				float spannedExpandHeight = 0f;
				for(int j=row, m=row+rowSpan; j<m; j++)
					spannedExpandHeight += expandHeights[j];
				
				extraExpand -= spannedExpandHeight;
				// span된 총 expand값이 현재 TableCell의 expand값보다 작다면 그 차이값을 span된 각 TableCell에 분배한다.
				if(extraExpand > 0f) {
					// span된 총 expand값에 대한 각 행의 expand비율만큼 차이값을 부여한다. 만약, span 영역에 
					// expand값이 전혀 없다면 균등분배한다.
					for(int j=row, m=row+rowSpan; j<m; j++) {
						float weight = (spannedExpandHeight == 0f)? (1f/rowSpan) : (expandHeights[j]/spannedExpandHeight);
						expandHeights[j] += extraExpand * weight;
					}
				}
			}
			
			// 열의 span이 2이상인 경우
			if(colSpan > 1) {
				int col = cell.mColumn;
				float extraExpand = expandX;
				
				float spannedExpandWidth = 0f;
				for(int j=col, m=col+colSpan; j<m; j++)
					spannedExpandWidth += expandWidths[j];
				
				extraExpand -= spannedExpandWidth;
				// span된 총 expand값이 현재 TableCell의 expand값보다 작다면 그 차이값을 span된 각 TableCell에 분배한다.
				if(extraExpand > 0f) {
					// span된 총 expand값에 대한 각 열의 expand비율만큼 차이값을 부여한다. 만약, span 영역에 
					// expand값이 전혀 없다면 균등분배한다.
					for(int j=col, m=col+colSpan; j<m; j++) {
						float weight = (spannedExpandWidth == 0f)? (1f/colSpan) : (expandWidths[j]/spannedExpandWidth);
						expandWidths[j] += extraExpand * weight;
					}
				}
			}
		}
		
		//********************************************************************
		//  span이 2이상인 경우에 대해 각 행의 높이와 각 열의 너비를 추가적으로 계산한다.
		//************************************************************************************
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			
			int rowSpan = getCellRowSpan(cell);
			int colSpan = getCellColSpan(cell);
			// 행과 열에 대한 span이 모두 1인 경우는 무시한다. 이미 앞에서 계산이 끝났기 때문이다.
			if(rowSpan == 1 && colSpan == 1) continue;

			// 행의 span이 2이상인 경우
			if(rowSpan > 1) {
				int row = cell.mRow;
				float padHeight = cell.mPadTop + cell.mPadBottom;
				
				float cellMinHeight = getCellMinHeight(cell);
				float cellPrefHeight = getCellPrefHeight(cell);
				if(cellPrefHeight < cellMinHeight) cellPrefHeight = cellMinHeight;
				
				float actorMinHeight = getActorMinHeight(cell);
				float actorPrefHeight = getActorPrefHeight(cell);
				Float actorMaxHeight = getActorMaxHeight(cell);
				if(actorPrefHeight < actorMinHeight) actorPrefHeight = actorMinHeight;
				if(actorMaxHeight != null && actorPrefHeight > actorMaxHeight) actorPrefHeight = actorMaxHeight;

				float minHeight = Math.max(cellMinHeight, actorMinHeight);
				float prefHeight = Math.max(cellPrefHeight, actorPrefHeight);
				
				float spannedMinHeight = 0;
				float spannedPrefHeight = 0;
				for(int j=row, m=row+rowSpan; j<m; j++) {
					spannedMinHeight += rowMinHeights[j];
					spannedPrefHeight += rowPrefHeights[j];
				}
				
				float extraMinHeight = Math.max(0f, minHeight + padHeight - spannedMinHeight);
				float extraPrefHeight = Math.max(0f, prefHeight + padHeight - spannedPrefHeight);
				// span된 높이의 총합이 Actor와 패딩의 높이의 합보다 작다면 그 차이값을 span된 각 TableCell에 분배한다.
				if(extraMinHeight > 0f || extraPrefHeight > 0f) {
					float spannedExpandHeight = 0f;
					for(int j=row, m=row+rowSpan; j<m; j++)
						spannedExpandHeight += expandHeights[j];
					
					// span된 총 expand값에 대한 각 행의 expand비율만큼 차이값을 부여한다. 만약, span 영역에 
					// expand값이 전혀 없다면 균등분배한다.
					for(int j=row, m=row+rowSpan; j<m; j++) {
						float weight = (spannedExpandHeight == 0f)? (1f/rowSpan) : (expandHeights[j]/spannedExpandHeight);
						rowMinHeights[j] += extraMinHeight * weight;
						rowPrefHeights[j] += extraPrefHeight * weight;
					}
				}
			}
			
			// 열의 span이 2이상인 경우
			if(colSpan > 1) {
				int col = cell.mColumn;
				float padWidth = cell.mPadLeft + cell.mPadRight;
				
				float cellMinWidth = getCellMinWidth(cell);
				float cellPrefWidth = getCellPrefWidth(cell);
				if(cellPrefWidth < cellMinWidth) cellPrefWidth = cellMinWidth;
				
				float actorMinWidth = getActorMinWidth(cell);
				float actorPrefWidth = getActorPrefWidth(cell);
				Float actorMaxWidth = getActorMaxWidth(cell);
				if(actorPrefWidth < actorMinWidth) actorPrefWidth = actorMinWidth;
				if(actorMaxWidth != null && actorPrefWidth > actorMaxWidth) actorPrefWidth = actorMaxWidth;
				
				float minWidth = Math.max(cellMinWidth, actorMinWidth);
				float prefWidth = Math.max(cellPrefWidth, actorPrefWidth);
				
				float spannedMinWidth = 0;
				float spannedPrefWidth = 0;
				for(int j=col, m=col+colSpan; j<m; j++) {
					spannedMinWidth += colMinWidths[j];
					spannedPrefWidth += colPrefWidths[j];
				}
				
				float extraMinWidth = Math.max(0f, minWidth + padWidth - spannedMinWidth);
				float extraPrefWidth = Math.max(0f, prefWidth + padWidth - spannedPrefWidth);
				// span된 너비의 총합이 Actor와 패딩의 너비의 합보다 작다면 그 차이값을 span된 각 TableCell에 분배한다.
				if(extraMinWidth > 0f || extraPrefWidth > 0f) {
					float spannedExpandWidth = 0f;
					for(int j=col, m=col+colSpan; j<m; j++)
						spannedExpandWidth += expandWidths[j];
					
					// span된 총 expand값에 대한 각 열의 expand비율만큼 차이값을 부여한다. 만약, span 영역에 
					// expand값이 전혀 없다면 균등분배한다.
					for(int j=col, m=col+colSpan; j<m; j++) {
						float weight = (spannedExpandWidth == 0f)? (1f/colSpan) : (expandWidths[j]/spannedExpandWidth);
						colMinWidths[j] += extraMinWidth * weight;
						colPrefWidths[j] += extraPrefWidth * weight;
					}
				}
			}
		}
		
		//********************************************************************
		// 컨테이너의 min, pref 사이즈를 계산한다.
		//************************************************************************************

		mContainerMinWidth = containerPadWidth;
		mContainerPrefWidth = containerPadWidth;
		for(int i=0; i<cols; i++) {
			mContainerMinWidth += colMinWidths[i];
			mContainerPrefWidth += colPrefWidths[i];
		}
		
		mContainerMinHeight = containerPadHeight;
		mContainerPrefHeight = containerPadHeight;
		for(int i=0; i<rows; i++) {
			mContainerMinHeight += rowMinHeights[i];
			mContainerPrefHeight += rowPrefHeights[i];
		}
	}

	@Override
	public void layout() {

		if(mSizeInvalidated) computeSize();
		
		float width = getWidth();
		float height = getHeight();
		
		float[] rowMinHeights = mRowMinHeights;
		float[] rowPrefHeights = mRowPrefHeights;
		float[] rowHeights = mRowHeights;
		
		float[] colMinWidths = mColMinWidths;
		float[] colPrefWidths = mColPrefWidths;
		float[] colWidths = mColWidths;
		
		float[] expandHeights = mExpandHeights;
		float[] expandWidths = mExpandWidths;
		
		final int rows = mRows;
		final int cols = mColumns;
		
		float containerPadTop = mPadTop;
		float containerPadLeft = mPadLeft;
		float containerPadRight = mPadRight;
		float containerPadBottom = mPadBottom;
		
		float containerPadHeight = containerPadTop + containerPadBottom;
		float containerPadWidth = containerPadLeft + containerPadRight;
		
		float containerMinHeight = mContainerMinHeight;
		float containerPrefHeight = mContainerPrefHeight;
		
		float containerMinWidth = mContainerMinWidth;
		float containerPrefWidth = mContainerPrefWidth;
		
		Rectangle containerRectangle = mContainerRectangle;
		
		//********************************************************************
		// 선호 사이즈와 최소 사이즈와의 차이를 적절히 보정하여 각 TableCell의 너비와 높이를 계산한다.
		//************************************************************************************

		float totalGrowHeight = containerPrefHeight - containerMinHeight;
		if(totalGrowHeight == 0f) {
			for(int i=0; i<rows; i++)
				rowHeights[i] = rowMinHeights[i];
		} else {
			// 선호 높이와 최소 높이의 차이 또는 Table의 현재 높이와 최소 높이의 차이 중에서 작은 값을 사용한다.
			// Table의 현재 높이와 최소 높이의 차가 선호 높이와 최소 높이 너비의 차보다 크더라도 그만큼은 
			// 빈 공간으로 남기 때문이다.
			float extraHeight = Math.min(totalGrowHeight, Math.max(0f, height - containerMinHeight));
			for(int i=0; i<rows; i++) {
				float weight = (rowPrefHeights[i] - rowMinHeights[i]) / totalGrowHeight;
				rowHeights[i] = rowMinHeights[i] + (extraHeight * weight);
			}
		}
		
		float totalGrowWidth = containerPrefWidth - containerMinWidth;
		if(totalGrowWidth == 0f) {
			for(int i=0; i<cols; i++)
				colWidths[i] = colMinWidths[i];
		} else {
			// 선호 너비와 최소 너비의 차이 또는 Table의 현재 너비와 최소 너비의 차이 중에서 작은 값을 사용한다.
			// Table의 현재 너비와 최소 너비의 차가 선호 너비와 최소 너비 너비의 차보다 크더라도 그만큼은 
			// 빈 공간으로 남기 때문이다.
			float extraWidth = Math.min(totalGrowWidth, Math.max(0f, width - containerMinWidth));
			for(int i=0; i<cols; i++) {
				float weight = (colPrefWidths[i] - colMinWidths[i]) / totalGrowWidth;
				colWidths[i] = colMinWidths[i] + (extraWidth * weight);
			}
		}
		
		
		//********************************************************************
		// expand를 적용하여 각 TableCell의 너비와 높이를 업데이트한다. 
		//************************************************************************************
		
		float totalExpandHeight = 0f;
		for(int i=0; i<rows; i++)
			totalExpandHeight += expandHeights[i];
		
		float totalExpandWidth = 0f;
		for(int i=0; i<cols; i++)
			totalExpandWidth += expandWidths[i];
		
		// 높이에 대한 expand가 존재한다면
		if(totalExpandHeight > 0f) {
			float containerHeight = 0f;
			for(int j=0 ; j<rows; j++)
				containerHeight += rowHeights[j];
			
			float extraHeight = height - (containerHeight + containerPadHeight);
			// 테이블과 컨테이너의 높이의 차가 있다면 각 행의 expand 높이값을 고려하여 행의 높이를 수정한다.
			if(extraHeight > 0f) {
				float weight = 0f;
				// totalExpandHeight가 1보다 작으면 extraHeight의 일부를 전체 expand에 대한 각 행의 expand의 비율만큼 채운다.
				if(totalExpandHeight <= 1f) {
					weight = extraHeight * totalExpandHeight;
				} else // totalExpandHeight가 1보다 크면 extraHeight를 전체 expand에 대한 각 행의 expand의 비율만큼 채운다.
					weight = extraHeight / totalExpandHeight;
				for(int j=0; j<rows; j++) {
					if(expandHeights[j] > 0f) rowHeights[j] += expandHeights[j] * weight;
				}
			}
		}
		
		// 너비에 대한 expand가 존재한다면
		if(totalExpandWidth > 0f) {
			float containerWidth = 0f;
			for(int j=0 ; j<cols; j++)
				containerWidth += colWidths[j];
			
			float extraWidth = width - (containerWidth + containerPadWidth);
			// 테이블과 컨테이너의 너비의 차가 있다면 각 열의 expand 너비값을 고려하여 열의 너비를 수정한다.
			if(extraWidth > 0f) {
				float weight = 0f;
				// totalExpandHeight가 1보다 작으면 extraWidth의 일부를 전체 expand에 대한 각 열의 expand의 비율만큼 채운다.
				if(totalExpandWidth <= 1f)	{
					weight = extraWidth * totalExpandWidth;
				} else // totalExpandHeight가 1보다 크면 extraWidth를 전체 expand에 대한 각 열의 expand의 비율만큼 채운다.
					weight = extraWidth / totalExpandWidth;
				for(int j=0; j<cols; j++) {
					if(expandWidths[j] > 0f) colWidths[j] += expandWidths[j] * weight;
				}
			}
		}
		
		//********************************************************************
		// 테이블 및 컨테이너의 사이즈를 계산하고, 컨테이너의 위치도 구한다.
		//************************************************************************************
		
		float containerWidth = 0f;
		for(int i=0; i<cols; i++)
			containerWidth += colWidths[i];
		
		float containerHeight = 0f;
		for(int i=0; i<rows; i++)
			containerHeight += rowHeights[i];
		
		containerRectangle.setSize(containerWidth, containerHeight);
		
		// Table의 현재 너비가 최소 너비보다 작으면 컨테이너의 너비와 패딩을 합한 값으로 변경한다.
		if(width <= containerMinWidth) {
			width = containerMinWidth;
			setWidth(width);
		}
		
		// Table의 현재 높이가 최소 높이보다 작으면 컨테이너의 높이와 패딩을 합한 값으로 변경한다.
		if(height <= containerMinHeight) {
			height = containerMinHeight;
			setHeight(height);
		}
		
		// 정렬에 따른 컨테이너의 x와 y좌표 계산
		switch(mAlign.getHAlign()) {
			case LEFT:
				containerRectangle.x = containerPadLeft;
				break;
			case CENTER:
				containerRectangle.x = (width - containerWidth + containerPadLeft - containerPadRight)/2;
				break;
			case RIGHT:
				containerRectangle.x = width - containerWidth - containerPadRight;
				break;
		}
		
		switch(mAlign.getVAlign()) {
			case TOP:
				containerRectangle.y = containerPadTop;
				break;
			case CENTER:
				containerRectangle.y = (height - containerHeight + containerPadTop - containerPadBottom)/2;
				break;
			case BOTTOM:
				containerRectangle.y = height - containerHeight - containerPadBottom;
				break;
		}
		
		float containerX = containerRectangle.x;
		float containerY = containerRectangle.y;
		
		//********************************************************************
		// 컨테이너의 위치를 바탕으로 각 TableCell과 Actor의 위치와 사이즈를 계산한다.
		//************************************************************************************
		
		// 컨테이너의 x와 y좌표를 시작으로 
		float currentX = containerX;
		float currentY = containerY;
		
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);

			cell.mCellRectangle.setPostion(currentX, currentY);
			
			int row = cell.mRow;
			int col = cell.mColumn;

			int rowSpan = getCellRowSpan(cell);
			int colSpan = getCellColSpan(cell);
			
			float spannedWidth = 0f;
			for(int j=col, m=col+colSpan; j<m; j++)
				spannedWidth += colWidths[j];
			
			float spannedHeight = 0f;
			for(int j=row, m=row+rowSpan; j<m; j++)
				spannedHeight += rowHeights[j];
			
			cell.mCellRectangle.setSize(spannedWidth, spannedHeight);
				
			if(cell.mActor != null) {
				float padTop = cell.mPadTop;
				float padLeft = cell.mPadLeft;
				float padRight = cell.mPadRight;
				float padBottom = cell.mPadBottom;
				
				float padHeight = padTop + padBottom;
				float padWidth = padLeft + padRight;
				
				float actorX = 0f;
				float actorY = 0f;
				
				float fillX = cell.mFillX;
				float fillY = cell.mFillY;
				
				float actorMinWidth = getActorMinWidth(cell);
				float actorPrefWidth = getActorPrefWidth(cell);
				Float actorMaxWidth = getActorMaxWidth(cell);
				if(actorPrefWidth < actorMinWidth) actorPrefWidth = actorMinWidth;
				if(actorMaxWidth != null && actorPrefWidth > actorMaxWidth) actorPrefWidth = actorMaxWidth;
				
				float actorMinHeight = getActorMinHeight(cell);
				float actorPrefHeight = getActorPrefHeight(cell);
				Float actorMaxHeight = getActorMaxHeight(cell);
				if(actorPrefHeight < actorMinHeight) actorPrefHeight = actorMinHeight;
				if(actorMaxHeight != null && actorPrefHeight > actorMaxHeight) actorPrefHeight = actorMaxHeight;
				
				float actorWidth = actorPrefWidth;
				float actorHeight = actorPrefHeight;
				
				actorWidth = Math.min(actorWidth, spannedWidth - padWidth);
				actorHeight = Math.min(actorHeight, spannedHeight - padHeight);
				
				// fill을 지정했다면
				if(fillX > 0f) {
					float extraWidth = spannedWidth - (actorWidth + padWidth);
					if(extraWidth > 0f) {
						actorWidth += extraWidth*fillX;
						Float maxWidth = getActorMaxWidth(cell);
						if(maxWidth != null) actorWidth = Math.min(actorWidth, maxWidth);
					}
				}
				
				if(fillY > 0f) {
					float extraHeight = spannedHeight - (actorHeight + padHeight);
					if(extraHeight > 0f) {
						actorHeight += extraHeight*fillY;
						Float maxHeight = getActorMaxHeight(cell);
						if(maxHeight != null) actorHeight = Math.min(actorHeight, maxHeight);
					}
				}
	
				Align align = cell.mAlign;
				
				// 각 TableCell의 정렬을 고려하여 위치를 구한다.
				switch(align.getHAlign()) {
					case LEFT:
						actorX = currentX + padLeft;
						break;
					case CENTER:
						actorX = currentX + (spannedWidth - actorWidth + padLeft - padRight)/2;
						break;
					case RIGHT:
						actorX = currentX + spannedWidth - actorWidth - padRight;
						break;
				}
				
				switch(align.getVAlign()) {
					case TOP:
						actorY = currentY + padTop;
						break;
					case CENTER:
						actorY = currentY + (spannedHeight - actorHeight + padTop - padBottom)/2;
						break;
					case BOTTOM:
						actorY = currentY + spannedHeight - actorHeight - padBottom;
						break;
				}

				cell.mActor.setBounds(actorX, actorY, actorWidth, actorHeight);
			}
			
			// 마지막 TableCell이면 아래 계산은 할 필요없다.
			if(n == i+1) return;
				
			col = cellList.get(i+1).mColumn;
			float spannedBeforeWidth = 0f;
			for(int j=0 ; j<col; j++)
				spannedBeforeWidth += colWidths[j];

			currentX = containerX + spannedBeforeWidth;
			if(cell.mEndRow) currentY += rowHeights[row];
		}

	}
	
	private float getCellMinHeight(TableCell cell) {
		Float minHeight = cell.mCellMinHeight;
		if(minHeight != null) return minHeight;
		return 0f;
	}
	
	private float getCellMinWidth(TableCell cell) {
		Float minWidth = cell.mCellMinWidth;
		if(minWidth != null) return minWidth;
		return 0f;
	}
	
	private float getCellPrefHeight(TableCell cell) {
		Float prefHeight = cell.mCellPrefHeight;
		if(prefHeight != null) return prefHeight;
		return 0f;
	}
	
	private float getCellPrefWidth(TableCell cell) {
		Float prefWidth = cell.mCellPrefWidth;
		if(prefWidth != null) return prefWidth;
		return 0f;
	}
	
	private float getActorMinHeight(TableCell cell) {
		if(cell.mActor == null) return 0f;
		Float minHeight = cell.mActorMinHeight;
		if(minHeight != null) return minHeight;
		if(!(cell.mActor instanceof Layout)) return cell.mActor.getHeight();
		Layout<?> layout = (Layout<?>) cell.mActor;
		return layout.getMinHeight();
	}
	
	private float getActorMinWidth(TableCell cell) {
		if(cell.mActor == null) return 0f;
		Float minWidth = cell.mActorMinWidth;
		if(minWidth != null) return minWidth;
		if(!(cell.mActor instanceof Layout)) return cell.mActor.getWidth();
		Layout<?> layout = (Layout<?>) cell.mActor;
		return layout.getMinWidth();
	}
	
	private float getActorPrefHeight(TableCell cell) {
		if(cell.mActor == null) return 0f;
		Float prefHeight = cell.mActorPrefHeight;
		if(prefHeight != null) return prefHeight;
		if(!(cell.mActor instanceof Layout)) return cell.mActor.getHeight();
		Layout<?> layout = (Layout<?>) cell.mActor;
		return layout.getPrefHeight();
	}
	
	private float getActorPrefWidth(TableCell cell) {
		if(cell.mActor == null) return 0f;
		Float prefWidth = cell.mActorPrefWidth;
		if(prefWidth != null) return prefWidth;
		if(!(cell.mActor instanceof Layout)) return cell.mActor.getWidth();
		Layout<?> layout = (Layout<?>) cell.mActor;
		return layout.getPrefWidth();
	}
	
	private Float getActorMaxHeight(TableCell cell) {
		if(cell.mActor == null) return null;
		Float maxHeight = cell.mActorMaxHeight;
		if(maxHeight != null) return maxHeight;
		if(!(cell.mActor instanceof Layout)) return null;
		Layout<?> layout = (Layout<?>) cell.mActor;
		return layout.getMaxHeight();
	}
	
	private Float getActorMaxWidth(TableCell cell) {
		if(cell.mActor == null) return null;
		Float maxWidth = cell.mActorMaxWidth;
		if(maxWidth != null) return maxWidth;
		if(!(cell.mActor instanceof Layout)) return null;
		Layout<?> layout = (Layout<?>) cell.mActor;
		return layout.getMaxWidth();
	}
	
	protected int getCellRowSpan(TableCell cell) {
		return cell.mRowSpan;
	}
	
	protected int getCellColSpan(TableCell cell) {
		return cell.mColSpan;
	}
	
	public int getRows() {
		return mRows;
	}

	public int getColumns() {
		return mColumns;
	}

	public float getPadTop() {
		return mPadTop;
	}

	public float getPadLeft() {
		return mPadLeft;
	}

	public float getPadRight() {
		return mPadRight;
	}

	public float getPadBottom() {
		return mPadBottom;
	}
	
	public float getPadWidth() {
		return mPadLeft + mPadRight;
	}
	
	public float getPadHeight() {
		return mPadTop + mPadBottom;
	}

	public float getContainerX() {
		if(isInvalidated()) validate();
		return mContainerRectangle.x;
	}

	public float getContainerY() {
		if(isInvalidated()) validate();
		return mContainerRectangle.y;
	}

	public float getContainerWidth() {
		if(isInvalidated()) validate();
		return mContainerRectangle.width;
	}

	public float getContainerHeight() {
		if(isInvalidated()) validate();
		return mContainerRectangle.height;
	}
	
	@Override
	protected void printChild(StringBuilder builder, Actor<?> child) {
		super.printChild(builder, child);
		TableCell cell = getCellByActor(child);
		if(cell != null) {
			builder.append(" [");
			builder.append(cell.getClass().getSimpleName());
			builder.append("]");
		}
	}

	@Override
	public boolean willDebug() {
		return super.willDebug() || mTableDebug != TableDebug.NONE;
	}
	
	@Override
	public T setDebug(boolean debug) {
		setTableDebug(debug);
		return super.setDebug(debug);
	}

	public int getTableDebug() {
		return mTableDebug;
	}

	public T setTableDebug(boolean debug) {
		if(debug) {
			debugTableContainer();
			debugTableCells();
			debugTableActors();
		} else
			mTableDebug = TableDebug.NONE;
		return (T) this;
	}
	
	public T debugTable() {
		return setTableDebug(true);
	}

	public T debugTableContainer() {
		mTableDebug |= TableDebug.CONTAINER;
		Floor floor = getFloor();
		if(floor != null) floor.setDebug(true);
		return (T) this;
	}
	
	public T debugTableCells() {
		mTableDebug |= TableDebug.CELL;
		Floor floor = getFloor();
		if(floor != null) floor.setDebug(true);
		return (T) this;
	}

	public T debugTableActors() {
		mTableDebug |= TableDebug.ACTOR;
		Floor floor = getFloor();
		if(floor != null) floor.setDebug(true);
		return (T) this;
	}

	@Override
	public void drawDebug(Batch batch, ShapeRenderer renderer) {
		validate();
		// 기본 디버그
		if(super.willDebug()) drawDebugBounds(batch, renderer);
		// 테이블 디버그
		drawTableDebug(batch, renderer);
	}

	/** 
	 * Table의 컨테이너, Cell, Cell에 속한 Actor의 디버깅라인을 출력한다. 특히, Actor의 디버깅라인은 
	 * {@link #drawDebugBounds(Batch, ShapeRenderer)}에 의해 출력되는 디버깅라인과는 달리 
	 * Actor의 스케일 또는 회전변환을 적용하지 않는다.
	 */
	private void drawTableDebug(Batch batch, ShapeRenderer renderer) {
		if(mTableDebug == TableDebug.NONE) return;
		
		// super.getDebug()가 ture인 경우 drawDebugBounds(...) 내에서
		// renderer의 Matrix를 세팅하기 때문에 중복 설정을 피한다.
		if(!super.willDebug()) {
			MATRIX.set(batch.peekTransformMatrix());
			MATRIX.preConcat(getMatrix());
			renderer.setTransformMatrix(MATRIX);
		}
		
		int tableDebug = mTableDebug;
		
		List<TableCell> cellList = mCellList;
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			
			// TableCell에 속한 Actor의 디버깅라인 출력
			if((tableDebug & TableDebug.ACTOR) != 0 && cell.mActor != null) {
				renderer.setColor(sTableActorDebugColor);
				Actor<?> actor = cell.mActor;
				renderer.drawRect(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
			}
			
			// TableCell의 디버깅라인 출력
			if((tableDebug & TableDebug.CELL) != 0) {
				renderer.setColor(sTableCellDebugColor);
				renderer.drawRect(cell.getCellX(), cell.getCellY(), cell.getCellWidth(), cell.getCellHeight());
			}
		}
		
		// 마지막으로 모든 TableCell을 감싸는 컨테이너의 디버깅라인 출력
		if((tableDebug & TableDebug.CONTAINER) != 0) {
			renderer.setColor(sTableContainerDebugColor);
			renderer.drawRect(
					mContainerRectangle.x, 
					mContainerRectangle.y, 
					mContainerRectangle.width, 
					mContainerRectangle.height);
		}
	}

	private static class TableDebug {
		private static final int NONE 			= 0;
		private static final int CONTAINER 	= 1 << 0;
		private static final int CELL 				= 1 << 1;
		private static final int ACTOR 			= 1 << 2;
	}
}