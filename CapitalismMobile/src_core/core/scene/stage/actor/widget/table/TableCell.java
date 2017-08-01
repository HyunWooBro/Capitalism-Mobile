package core.scene.stage.actor.widget.table;

import java.util.List;

import core.math.MathUtils;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.PointAlignable;
import core.scene.stage.actor.widget.utils.Align.RectangleAlignable;

public class TableCell implements RectangleAlignable<TableCell> {
	
	// TODO 값을 절대값이 아닌 상대값(%)으로도 받을 수 있도록 하자.

	/*package*/ Table<?> mTable;
	
	/*package*/ Actor<?> mActor;

	/*package*/ int mRow;
	/*package*/ int mColumn;
	
	/*package*/ Integer mColSpan;
	/*package*/ Integer mRowSpan;
	
	/*package*/ Align mAlign;
	
	/*package*/ Float mCellMinWidth;
	/*package*/ Float mCellMinHeight;
	/*package*/ Float mCellPrefWidth;
	/*package*/ Float mCellPrefHeight;
	
	/*package*/ Float mActorMinWidth;
	/*package*/ Float mActorMinHeight;
	/*package*/ Float mActorPrefWidth;
	/*package*/ Float mActorPrefHeight;
	/*package*/ Float mActorMaxWidth;
	/*package*/ Float mActorMaxHeight;
	
	/*package*/ Float mPadTop;
	/*package*/ Float mPadLeft;
	/*package*/ Float mPadRight;
	/*package*/ Float mPadBottom;
	
	/*package*/ Float mFillX;
	/*package*/ Float mFillY;
	
	/*package*/ Float mExpandX;
	/*package*/ Float mExpandY;
	
	/*package*/ boolean mEndRow;
	
	/*package*/ Rectangle mCellRectangle;
	
	/*package*/ TableCell mPrevCell;
	/*package*/ TableCell mNextCell;
	
	/*package*/ TableCell(Table<?> table) {
		mTable = table;
	}

	public TableCell(Table<?> table, Actor<?> actor) {
		mTable = table;
		mActor = actor;
		ignorePointAlign(actor);
		mCellRectangle = new Rectangle();
	}
	
	/** 셀 자체에서 정렬을 수행하므로 PointAlignable일 경우 정렬은 무시한다. */
	private void ignorePointAlign(Actor<?> actor) {
		if(!(actor instanceof PointAlignable)) return;
		
		PointAlignable<?> alignable = (PointAlignable<?>) actor;
		// 기본 원점인 좌측 상단으로 강제한다.   
		alignable.northWest();
	}

	/** 
	 * 셀에 Actor를 지정한다. 기본적으로 Table에서 Actor을 가진 가장 가까운 이전 셀의 Actor가 
	 * 위치한 바로 뒤에 추가된다. 
	 */
	public TableCell setActor(Actor<?> actor) {
		if(mActor == actor) return this;
	
		if(mActor != null) mTable.removeChild(mActor);
		if(actor == null) return this;
		
		mActor = actor;
		ignorePointAlign(actor);
		
		// 앞선 셀에서 가장 가까운 Actor을 구한다.
		TableCell cell = mPrevCell;
		Actor<?> prevActor = null;
		while(cell != null) {
			if(cell.mActor != null) {
				prevActor = cell.mActor;
				break;
			}
			cell = cell.mPrevCell;
		}
		
		// Actor를 찾았다면 바로 그다음 위치에 추가한다.
		if(prevActor != null) {
			mTable.addChildAfter(prevActor, actor);
		// 못 찾았다면 그냥 추가한다.
		} else
			mTable.addChild(actor);
		
		return this;
	}

	/*package*/ void set(TableCell cell) {
		mColSpan = cell.mColSpan;
		mRowSpan = cell.mRowSpan;
		mAlign = new Align(cell.mAlign);
		mCellMinWidth = cell.mCellMinWidth;
		mCellMinHeight = cell.mCellMinHeight;
		mCellPrefWidth = cell.mCellPrefWidth;
		mCellPrefHeight = cell.mCellPrefHeight;
		mActorMinWidth = cell.mActorMinWidth;
		mActorMinHeight = cell.mActorMinHeight;
		mActorPrefWidth = cell.mActorPrefWidth;
		mActorPrefHeight = cell.mActorPrefHeight;
		mActorMaxWidth = cell.mActorMaxWidth;
		mActorMaxHeight = cell.mActorMaxHeight;
		mPadTop = cell.mPadTop;
		mPadLeft = cell.mPadLeft;
		mPadRight = cell.mPadRight;
		mPadBottom = cell.mPadBottom;
		mFillX = cell.mFillX;
		mFillY = cell.mFillY;
		mExpandX = cell.mExpandX;
		mExpandY = cell.mExpandY;
	}
	
	/*package*/ void merge(TableCell cell) {
		if(cell.mColSpan != null) mColSpan = cell.mColSpan;
		if(cell.mRowSpan != null) mRowSpan = cell.mRowSpan;
		if(cell.mAlign != null) mAlign.set(cell.mAlign);
		if(cell.mCellMinWidth != null) mCellMinWidth = cell.mCellMinWidth;
		if(cell.mCellMinHeight != null) mCellMinHeight = cell.mCellMinHeight;
		if(cell.mCellPrefWidth != null) mCellPrefWidth = cell.mCellPrefWidth;
		if(cell.mCellPrefHeight != null) mCellPrefHeight = cell.mCellPrefHeight;
		if(cell.mActorMinWidth != null) mActorMinWidth = cell.mActorMinWidth;
		if(cell.mActorMinHeight != null) mActorMinHeight = cell.mActorMinHeight;
		if(cell.mActorPrefWidth != null) mActorPrefWidth = cell.mActorPrefWidth;
		if(cell.mActorPrefHeight != null) mActorPrefHeight = cell.mActorPrefHeight;
		if(cell.mActorMaxWidth != null) mActorMaxWidth = cell.mActorMaxWidth;
		if(cell.mActorMaxHeight != null) mActorMaxHeight = cell.mActorMaxHeight;
		if(cell.mPadTop != null) mPadTop = cell.mPadTop;
		if(cell.mPadLeft != null) mPadLeft = cell.mPadLeft;
		if(cell.mPadRight != null) mPadRight = cell.mPadRight;
		if(cell.mPadBottom != null) mPadBottom = cell.mPadBottom;
		if(cell.mFillX != null) mFillX = cell.mFillX;
		if(cell.mFillY != null) mFillY = cell.mFillY;
		if(cell.mExpandX != null) mExpandX = cell.mExpandX;
		if(cell.mExpandY != null) mExpandY = cell.mExpandY;
	}
	
	/*package*/ void reset() {
		mColSpan = 1;
		mRowSpan = 1;
		mAlign = new Align();
		mPadTop = 0f;
		mPadLeft = 0f;
		mPadRight = 0f;
		mPadBottom = 0f;
		mFillX = 0f;
		mFillY = 0f;
		mExpandX = 0f;
		mExpandY = 0f;
	}
	
	/*package*/ void clear() {
		mColSpan = null;
		mRowSpan = null;
		mAlign = null;
		mCellMinWidth = null;
		mCellMinHeight = null;
		mCellPrefWidth = null;
		mCellPrefHeight = null;
		mActorMinWidth = null;
		mActorMinHeight = null;
		mActorPrefWidth = null;
		mActorPrefHeight = null;
		mActorMaxWidth = null;
		mActorMaxHeight = null;
		mPadTop = null;
		mPadLeft = null;
		mPadRight = null;
		mPadBottom = null;
		mFillX = null;
		mFillY = null;
		mExpandX = null;
		mExpandY = null;
	}
	
	public TableCell colSpan(int span) {
		if(mColSpan != null && mColSpan > span)
			throw new IllegalArgumentException("new span must be larger than current one.");
		
		mColSpan = span;
		mTable.updateTableState();
		mTable.invalidate();
		return this;
	}
	
	public TableCell rowSpan(int span) {
		if(mRowSpan != null && mRowSpan > span)
			throw new IllegalArgumentException("new span must be larger than current one.");
		
		mRowSpan = span;
		mTable.updateTableState();
		mTable.invalidate();
		return this;
	}
	
	private void ensureAlign() {
		if(mAlign == null) mAlign = new Align();
	}
	
	@Override
	public TableCell center() {
		ensureAlign();
		mAlign.center();
		mTable.invalidate();
		return this;
	}
	
	@Override
	public TableCell top() {
		ensureAlign();
		mAlign.top();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell left() {
		ensureAlign();
		mAlign.left();
		mTable.invalidate();
		return this;
	}
	
	@Override
	public TableCell right() {
		ensureAlign();
		mAlign.right();
		mTable.invalidate();
		return this;
	}
	
	@Override
	public TableCell bottom() {
		ensureAlign();
		mAlign.bottom();
		mTable.invalidate();
		return this;
	}
	
	@Override
	public TableCell north() {
		ensureAlign();
		mAlign.north();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell west() {
		ensureAlign();
		mAlign.west();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell east() {
		ensureAlign();
		mAlign.east();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell south() {
		ensureAlign();
		mAlign.south();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell northWest() {
		ensureAlign();
		mAlign.northWest();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell northEast() {
		ensureAlign();
		mAlign.northEast();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell southWest() {
		ensureAlign();
		mAlign.southWest();
		mTable.invalidate();
		return this;
	}

	@Override
	public TableCell southEast() {
		ensureAlign();
		mAlign.southEast();
		mTable.invalidate();
		return this;
	}
	
	@Override
	public Align getAlign() {
		return mAlign;
	}

	@Override
	public TableCell setAlign(Align align) {
		ensureAlign();
		mAlign.set(align);
		mTable.invalidate();
		return this;
	}
	
	public TableCell cellMinWidth(float width) {
		mCellMinWidth = width;
		mTable.invalidate();
		return this;
	}
	
	public TableCell cellMinHeight(float height) {
		mCellMinHeight = height;
		mTable.invalidate();
		return this;
	}
	
	public TableCell cellMinSize(float width, float height) {
		cellMinWidth(width);
		cellMinHeight(height);
		return this;
	}

	public TableCell cellPrefWidth(float width) {
		mCellPrefWidth = width;
		mTable.invalidate();
		return this;
	}
	
	public TableCell cellPrefHeight(float height) {
		mCellPrefHeight = height;
		mTable.invalidate();
		return this;
	}
	
	public TableCell cellPrefSize(float width, float height) {
		cellPrefWidth(width);
		cellPrefHeight(height);
		return this;
	}
	
	public TableCell cellWidth(float width) {
		cellMinWidth(width);
		cellPrefWidth(width);
		return this;
	}
	
	public TableCell cellHeight(float height) {
		cellMinHeight(height);
		cellPrefHeight(height);
		return this;
	}
	
	public TableCell cellSize(float width, float height) {
		cellWidth(width);
		cellHeight(height);
		return this;
	}
	
	public TableCell actorMinWidth(float width) {
		mActorMinWidth = width;
		mTable.invalidate();
		return this;
	}
	
	public TableCell actorMinHeight(float height) {
		mActorMinHeight = height;
		mTable.invalidate();
		return this;
	}
	
	public TableCell actorMinSize(float width, float height) {
		actorMinWidth(width);
		actorMinHeight(height);
		return this;
	}
	
	public TableCell actorPrefWidth(float width) {
		mActorPrefWidth = width;
		mTable.invalidate();
		return this;
	}
	
	public TableCell actorPrefHeight(float height) {
		mActorPrefHeight = height;
		mTable.invalidate();
		return this;
	}
	
	public TableCell actorPrefSize(float width, float height) {
		actorPrefWidth(width);
		actorPrefHeight(height);
		return this;
	}
	
	public TableCell actorMaxWidth(float width) {
		mActorMaxWidth = width;
		mTable.invalidate();
		return this;
	}
	
	public TableCell actorMaxHeight(float height) {
		mActorMaxHeight = height;
		mTable.invalidate();
		return this;
	}
	
	public TableCell actorMaxSize(float width, float height) {
		actorMaxWidth(width);
		actorMaxHeight(height);
		return this;
	}
	
	public TableCell actorWidth(float width) {
		actorMinWidth(width);
		actorPrefWidth(width);
		actorMaxWidth(width);
		return this;
	}
	
	public TableCell actorHeight(float height) {
		actorMinHeight(height);
		actorPrefHeight(height);
		actorMaxHeight(height);
		return this;
	}
	
	public TableCell actorSize(float width, float height) {
		actorWidth(width);
		actorHeight(height);
		return this;
	}
	
	public TableCell minWidth(float width) {
		actorMinWidth(width);
		cellMinWidth(width);
		return this;
	}
	
	public TableCell minHeight(float height) {
		actorMinHeight(height);
		cellMinHeight(height);
		return this;
	}
	
	public TableCell minSize(float width, float height) {
		actorMinSize(width, height);
		cellMinSize(width, height);
		return this;
	}
	
	public TableCell prefWidth(float width) {
		actorPrefWidth(width);
		cellPrefWidth(width);
		return this;
	}
	
	public TableCell prefHeight(float height) {
		actorPrefHeight(height);
		cellPrefHeight(height);
		return this;
	}
	
	public TableCell prefSize(float width, float height) {
		actorPrefSize(width, height);
		cellPrefSize(width, height);
		return this;
	}
	
	public TableCell width(float width) {
		actorWidth(width);
		cellWidth(width);
		return this;
	}
	
	public TableCell height(float height) {
		actorHeight(height);
		cellHeight(height);
		return this;
	}
	
	public TableCell size(float width, float height) {
		width(width);
		height(height);
		return this;
	}
	
	public TableCell pad(float pad) {
		mPadTop = pad;
		mPadLeft = pad;
		mPadRight = pad;
		mPadBottom = pad;
		mTable.invalidate();
		return this;
	}
	
	public TableCell padTop(float pad) {
		mPadTop = pad;
		mTable.invalidate();
		return this;
	}
	
	public TableCell padLeft(float pad) {
		mPadLeft = pad;
		mTable.invalidate();
		return this;
	}
	
	public TableCell padRight(float pad) {
		mPadRight = pad;
		mTable.invalidate();
		return this;
	}
	
	public TableCell padBottom(float pad) {
		mPadBottom = pad;
		mTable.invalidate();
		return this;
	}
	
	public TableCell fill() {
		mFillX = 1f;
		mFillY = 1f;
		mTable.invalidate();
		return this;
	}
	
	public TableCell fill(float fillX, float fillY) {
		mFillX = fillX;
		mFillY = fillY;
		mFillX = MathUtils.clamp(mFillX, 0f, 1f);
		mFillY = MathUtils.clamp(mFillY, 0f, 1f);
		mTable.invalidate();
		return this;
	}
	
	public TableCell fillX() {
		mFillX = 1f;
		mTable.invalidate();
		return this;
	}
	
	public TableCell fillX(float fillX) {
		mFillX = fillX;
		mFillX = MathUtils.clamp(mFillX, 0f, 1f);
		mTable.invalidate();
		return this;
	}
	
	public TableCell fillY() {
		mFillY = 1f;
		mTable.invalidate();
		return this;
	}
	
	public TableCell fillY(float fillY) {
		mFillY = fillY;
		mFillY = MathUtils.clamp(mFillY, 0f, 1f);
		mTable.invalidate();
		return this;
	}
	
	public TableCell expand() {
		mExpandX = 1f;
		mExpandY = 1f;
		mTable.invalidate();
		return this;
	}
	
	public TableCell expand(float expandX, float expandY) {
		mExpandX = expandX;
		mExpandY = expandY;
		mExpandX = MathUtils.clamp(mExpandX, 0f, 1f);
		mExpandY = MathUtils.clamp(mExpandY, 0f, 1f);
		mTable.invalidate();
		return this;
	}
	
	public TableCell expandX() {
		mExpandX = 1f;
		mTable.invalidate();
		return this;
	}
	
	public TableCell expandX(float expandX) {
		mExpandX = expandX;
		mExpandX = MathUtils.clamp(mExpandX, 0f, 1f);
		mTable.invalidate();
		return this;
	}
	
	public TableCell expandY() {
		mExpandY = 1f;
		mTable.invalidate();
		return this;
	}
	
	public TableCell expandY(float expandY) {
		mExpandY = expandY;
		mExpandY = MathUtils.clamp(mExpandY, 0f, 1f);
		mTable.invalidate();
		return this;
	}

	public Table<?> getTable() {
		return mTable;
	}

	public Actor<?> getActor() {
		return mActor;
	}

	public int getRow() {
		return mRow;
	}

	public int getColumn() {
		return mColumn;
	}

	public int getRowSpan() {
		return mRowSpan;
	}

	public int getColSpan() {
		return mColSpan;
	}
	
	public Float getMinWidth() {
		return mActorMinWidth;
	}
	
	public Float getMinHeight() {
		return mActorMinHeight;
	}
	
	public Float getPrefWidth() {
		return mActorPrefWidth;
	}
	
	public Float getPrefHeight() {
		return mActorPrefHeight;
	}
	
	public Float getMaxWidth() {
		return mActorMaxWidth;
	}
	
	public Float getMaxHeight() {
		return mActorMaxHeight;
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

	public float getFillX() {
		return mFillX;
	}

	public float getFillY() {
		return mFillY;
	}

	public float getExpandX() {
		return mExpandX;
	}

	public float getExpandY() {
		return mExpandY;
	}

	public boolean isEndRow() {
		return mEndRow;
	}
	
	public float getCellX() {
		return mCellRectangle.x;
	}

	public float getCellY() {
		return mCellRectangle.y;
	}

	public float getCellWidth() {
		return mCellRectangle.width;
	}

	public float getCellHeight() {
		return mCellRectangle.height;
	}

	/** 반환된 객체를 수정하면 테이블 레이아웃에 부작용이 발생할 수 있다. */
	public Rectangle getCellRectangle() {
		return mCellRectangle;
	}
	
}
