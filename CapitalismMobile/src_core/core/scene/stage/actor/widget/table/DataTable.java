package core.scene.stage.actor.widget.table;

import java.util.ArrayList;
import java.util.List;

import core.framework.graphics.Color4;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.ShapeRenderer.ShapeType;
import core.framework.graphics.batch.Batch;
import core.math.MathUtils;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;

/**
 * Data를 출력하기 위한 Table이다. Data를 구분하기 위해 선 또는 배경색을 이용한다.</p>
 *  
 * {@link ExtraLine} 또는 {@link ExtraBackground}을 사용하여 다양한 효과를 표현할 수 
 * 있다. range의 경우 너비나 높이가 0인 경우에는 마지막 셀까지를 포함하며 음수일 경우에는 
 * 마지막 셀부터의 오프셋을 의미한다.</p>
 * 
 * @author 김현우
 */
public class DataTable extends Table<DataTable> {
	
	private static final Rectangle TMP_RECTANGLE = new Rectangle();
	
	private static ShapeRenderer sTableRenderer;
	
	/** DataTable의 배경색과 라인을 정의한다 */
	private TableStyle mTableStyle;
	
	public DataTable() {
		this(null);
	}
	
	public DataTable(Costume costume) {
		super(costume);
		if(sTableRenderer == null) sTableRenderer = new ShapeRenderer(1000);
	}
	
	public void setTableStyle(TableStyle style) {
		mTableStyle = style;
	}
	
	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		batch.flush();
		// Table을 drawSelf와 drawChildren 사이에서 출력한다.
		drawTable(batch, parentAlpha);
		super.drawChildren(batch, parentAlpha);
	}

	private void drawTable(Batch batch, float parentAlpha) {
		
		// 정의된 스타일이 없으면 디폴트로 생성하여 사용한다.
		if(mTableStyle == null) mTableStyle = new TableStyle();
		TableStyle style = mTableStyle;
		
		ShapeRenderer renderer = sTableRenderer;
		renderer.setProjectionMatrix(getFloor().getCamera().getCombinedMatrix());
		
		MATRIX.set(batch.peekTransformMatrix());
		
		// 배경색 출력
		if(style.showBackground) {
			renderer.begin(ShapeType.FILLED);
			renderer.setTransformMatrix(MATRIX);
			
			// 기본 배경색 출력
			if(style.backgroundColor != null) {
				renderer.setColor(style.backgroundColor);
				renderer.drawRect(getContainerX(), getContainerY(), getContainerWidth(), getContainerHeight());
			}
			
			// 추가 배경색 출력
			List<ExtraBackground> extraList = style.extraBackgroundList;
			if(extraList != null) {
				int m = extraList.size();
				for(int j=0; j<m; j++) {
					ExtraBackground extra = extraList.get(j);
					if(extra.color != null) drawExtraBackground(extra);
				}
			}
			
			renderer.end();
		}
		
		// 라인 출력
		if(style.showLine) {
			renderer.begin(ShapeType.LINE);
			renderer.setTransformMatrix(MATRIX);
			
			// 기본 라인 출력
			if(style.lineColor != null) {
				renderer.setLineWidth(style.lineWidth);
				
				Color4 color = style.lineColor;
				List<TableCell> cellList = getCellList();
				int n = cellList.size();
				for(int i=0; i<n; i++) {
					TableCell cell = cellList.get(i);
					renderer.setColor(color);
					renderer.drawRect(cell.getCellX(), cell.getCellY(), cell.getCellWidth(), cell.getCellHeight());
				}
				
				// 선의 굵기를 적용하기 위해
				renderer.flush();
			}
			
			// 추가 라인 출력
			List<ExtraLine> extraList = style.extraLineList;
			if(extraList != null) {
				int m = extraList.size();
				for(int j=0; j<m; j++) {
					ExtraLine extra = extraList.get(j);
					if(extra.color != null) drawExtraLine(extra);
				}
			}
			
			renderer.end();
		}
	}
	
	private void drawExtraLine(ExtraLine extra) {
		
		ShapeRenderer renderer = sTableRenderer;
		
		renderer.setLineWidth(extra.width);
		renderer.setColor(extra.color);
		
		int style = extra.style;
		
		Rectangle range = extra.range;
		if(range != null) {
			
			int rangeStartRow = (int) range.y;
			rangeStartRow = MathUtils.clamp(rangeStartRow, 0, getRows()-1);
					
			int rangeEndRow;
			if(range.height == 0f) 		rangeEndRow = getRows() - 1;
			else if(range.height < 0f) 	rangeEndRow = (int) (getRows() + range.height - 1);
			else 									rangeEndRow = (int) (range.y + range.height - 1);
			rangeEndRow = MathUtils.clamp(rangeEndRow, rangeStartRow, getRows()-1);
			
			int rangeStartCol = (int) range.x;
			rangeStartCol = MathUtils.clamp(rangeStartCol, 0, getColumns()-1);
			
			int rangeEndCol;
			if(range.width == 0f) 			rangeEndCol = getColumns() - 1;
			else if(range.width < 0f) 	rangeEndCol = (int) (getColumns() + range.width - 1);
			else 									rangeEndCol = (int) (range.x + range.width - 1);
			rangeEndCol = MathUtils.clamp(rangeEndCol, rangeStartCol, getColumns()-1);
			
			final Rectangle rectangle = TMP_RECTANGLE;
			rectangle.x = rangeStartCol;
			rectangle.y =rangeStartRow;
			rectangle.width = rangeEndCol - rangeStartCol;
			rectangle.height = rangeEndRow - rangeStartRow;
			
			List<TableCell> cellList = getCellList();
			int n = cellList.size();
			for(int i=0; i<n; i++) {
				TableCell cell = cellList.get(i);
				
				boolean included = true;
				int m = cell.mColumn + cell.mColSpan;
				int l = cell.mRow + cell.mRowSpan;
out:
				for(int j=cell.mColumn; j<m; j++) {
					for(int k=cell.mRow; k<l; k++) {
						if(rectangle.contains(j, k)) {
							if(extra.autoComplete) {
								included = true;
								break out;
							}
						} else {
							if(!extra.autoComplete) {
								included = false;
								break out;
							}
							included = false;
						}
						
					}
				}
				
				if(!included) continue;
				
				drawCellLine(cell, style, rangeStartRow, rangeEndRow, rangeStartCol, rangeEndCol);
			}
			
		} else {
			
			List<TableCell> cellList = getCellList();
			int n = cellList.size();
			for(int i=0; i<n; i++) {
				TableCell cell = cellList.get(i);
				drawCellLine(cell, style, 0, getRows()-1, 0, getColumns()-1);
			}
		}
		
		// 선의 굵기를 적용하기 위해
		renderer.flush();
	}
	
	private void drawCellLine(TableCell cell, int style, int rangeStartRow,int rangeEndRow, int rangeStartCol, int rangeEndCol) {
		
		ShapeRenderer renderer = sTableRenderer;
		
		float top = cell.getCellY();
		float left = cell.getCellX();
		float right = left + cell.getCellWidth();
		float bottom = top + cell.getCellHeight();
		
		boolean hasMoreThanTwoRows = rangeStartRow != rangeEndRow;
		boolean hasMoreThanTwoCols = rangeStartCol != rangeEndCol;
		
		// 상단
		if(hasMoreThanTwoRows) {
			if(cell.mRow > rangeStartRow) {
				if((style & ExtraLine.EL_HORIZONTAL) != 0) renderer.drawLine(left, top, right, top);
				
			} else if(cell.mRow < rangeStartRow) {
				if((style & ExtraLine.EL_TOP) != 0) renderer.drawLine(left, top, right, top);
				
			} else
				if((style & ExtraLine.EL_TOP) != 0) renderer.drawLine(left, top, right, top);
			
		} else
			if((style & ExtraLine.EL_TOP) != 0) renderer.drawLine(left, top, right, top);
		
		// 좌측
		if(hasMoreThanTwoCols) {
			if(cell.mColumn > rangeStartCol) {
				if((style & ExtraLine.EL_VERTICAL) != 0) renderer.drawLine(left, top, left, bottom);
				
			} else if(cell.mColumn < rangeStartCol) {
				if((style & ExtraLine.EL_LEFT) != 0) renderer.drawLine(left, top, left, bottom);
				
			} else
				if((style & ExtraLine.EL_LEFT) != 0) renderer.drawLine(left, top, left, bottom);
			
		} else
			if((style & ExtraLine.EL_LEFT) != 0) renderer.drawLine(left, top, left, bottom);
		
		// 우측
		if(hasMoreThanTwoRows) {
			if(cell.mColumn+cell.mColSpan-1 > rangeEndCol) {
				if((style & ExtraLine.EL_RIGHT) != 0) renderer.drawLine(right, top, right, bottom);
				
			} else if(cell.mColumn+cell.mColSpan-1 < rangeEndCol) {
				if((style & ExtraLine.EL_VERTICAL) != 0) renderer.drawLine(right, top, right, bottom);
				
			} else
				if((style & ExtraLine.EL_RIGHT) != 0) renderer.drawLine(right, top, right, bottom);
				
		} else
			if((style & ExtraLine.EL_RIGHT) != 0) renderer.drawLine(right, top, right, bottom);
		
		// 하단
		if(hasMoreThanTwoCols) {
			if(cell.mRow+cell.mRowSpan-1 > rangeEndRow) {
				if((style & ExtraLine.EL_BOTTOM) != 0) renderer.drawLine(left, bottom, right, bottom);
				
			} else if(cell.mRow+cell.mRowSpan-1 < rangeEndRow) {
				if((style & ExtraLine.EL_HORIZONTAL) != 0) renderer.drawLine(left, bottom, right, bottom);
				
			} else {
				if((style & ExtraLine.EL_BOTTOM) != 0) renderer.drawLine(left, bottom, right, bottom);
				
			}
		} else
			if((style & ExtraLine.EL_BOTTOM) != 0) renderer.drawLine(left, bottom, right, bottom);
		
	}

	private void drawExtraBackground(ExtraBackground extra) {
		
		ShapeRenderer renderer = sTableRenderer;
		
		int rangeStartRow;
		int rangeEndRow;
		float rangeX = getContainerX();
		float rangeWidth = getContainerWidth();
		
		int rangeStartCol;
		int rangeEndCol;
		float rangeY = getContainerY();
		float rangeHeight = getContainerHeight();
		
		boolean horizontal = extra.horizontal;
		
		Rectangle range = extra.range;
		if(range != null) {
			
			rangeStartRow = (int) range.y;
			rangeStartRow = MathUtils.clamp(rangeStartRow, 0, getRows()-1);
			
			if(range.height == 0f) 		rangeEndRow = getRows() - 1;
			else if(range.height < 0f) 	rangeEndRow = (int) (getRows() + range.height - 1);
			else 									rangeEndRow = (int) (rangeStartRow + range.height - 1);
			rangeEndRow = MathUtils.clamp(rangeEndRow, rangeStartRow, getRows()-1);
			
			rangeStartCol = (int) range.x;
			rangeStartCol = MathUtils.clamp(rangeStartCol, 0, getColumns()-1);
			
			if(range.width == 0f) 			rangeEndCol = getColumns() - 1;
			else if(range.width < 0f) 	rangeEndCol = (int) (getColumns() + range.width - 1);
			else 									rangeEndCol = (int) (rangeStartCol + range.width - 1);
			rangeEndCol = MathUtils.clamp(rangeEndCol, rangeStartCol, getColumns()-1);
			
			if(horizontal) {
				for(int i=0; i<rangeStartCol; i++) 
					rangeX += mColWidths[i];
				
				if(rangeEndCol != getColumns()-1) {
					rangeWidth = 0f;
					int n = rangeEndCol + 1;
					for(int i=rangeStartCol; i<n; i++)
						rangeWidth += mColWidths[i];
				}
				
			} else {
				for(int i=0; i<rangeStartRow; i++)
					rangeY += mRowHeights[i];
				
				if(rangeEndRow == getRows()-1) {
					rangeHeight = 0f;
					int n = rangeEndRow + 1;
					for(int i=rangeStartRow; i<n; i++)
						rangeHeight += mRowHeights[i];
				}
			}
		} else {
			rangeStartRow = 0;
			rangeEndRow = getRows() - 1;
			
			rangeStartCol = 0;
			rangeEndCol = getColumns() - 1;
		}
		
		int start = (horizontal)? rangeStartRow : rangeStartCol;
		int end = (horizontal)? rangeEndRow : rangeEndCol;
		
		int index = start + extra.offset;
		int span = extra.span;
		int hop= extra.hop;
		
		float pos = (horizontal)? getContainerY() : getContainerX();
		for(int i=0; i<start; i++)
			pos += (horizontal)? mRowHeights[i] : mColWidths[i];
		
		int n = end + 1;
		for(int i=start; i<n; i++) {
			if(i == index) {
				index += (span + hop);
				
				Color4 topLeftColor;
				Color4 topRightColor;
				Color4 bottomLeftColor;
				Color4 bottomRightColor;
				
				// 그라데이션을 적용하여 배경 출력
				if(extra.hasGradation()) {
					ExtraGradationBackground gradation = (ExtraGradationBackground) extra;
					topLeftColor = gradation.topLeftColor;
					topRightColor = gradation.topRightColor;
					bottomLeftColor = gradation.bottomLeftColor;
					bottomRightColor = gradation.bottomRightColor;
					
				// 단색 배경 출력
				} else {
					topLeftColor = extra.color;
					topRightColor = extra.color;
					bottomLeftColor = extra.color;
					bottomRightColor = extra.color;
				}
				
				if(horizontal) {
					float height = 0f;
					int m = i + span;
					if(m > n) m = n;
					for(int j=i; j<m; j++)
						height += mRowHeights[j];
					
					renderer.drawRect(rangeX, pos, rangeWidth, height, topLeftColor, bottomLeftColor, bottomRightColor, topRightColor);
				} else {
					float width = 0f;
					int m = i + span;
					if(m > n) m = n;
					for(int j=i; j<m; j++)
						width += mColWidths[j];
					
					renderer.drawRect(pos, rangeY, width, rangeHeight, topLeftColor, bottomLeftColor, bottomRightColor, topRightColor);
				}
				
			}
			
			pos += (horizontal)? mRowHeights[i] : mColWidths[i];
		}
	}
	
	public static class TableStyle {
		public boolean showLine = true;
		public Color4 lineColor = Color4.BLACK4;
		public float lineWidth = 3f;
		public List<ExtraLine> extraLineList;
		
		public void addExtraLine(ExtraLine line) {
			if(extraLineList == null) extraLineList = new ArrayList<ExtraLine>();
			extraLineList.add(line);
		}
		
		public boolean showBackground = true;
		public Color4 backgroundColor = Color4.WHITE4;
		public List<ExtraBackground> extraBackgroundList;
		
		public void addExtraBackground(ExtraBackground background) {
			if(extraBackgroundList == null) extraBackgroundList = new ArrayList<ExtraBackground>();
			extraBackgroundList.add(background);
		}
	}
	
	public static class ExtraLine {
		public static int EL_NONE 				= 0;
		public static int EL_TOP 					= 1 << 0;
		public static int EL_LEFT 				= 1 << 1;
		public static int EL_RIGHT 				= 1 << 2;
		public static int EL_BOTTOM 			= 1 << 3;
		public static int EL_HORIZONTAL 	= 1 << 4;
		public static int EL_VERTICAL 		= 1 << 5;
		
		public Rectangle range;
		/** Word에서 표의 선을 설정하는 것과 비슷하게 라인을 세밀하게 설정할 수 있다 */
		public int style = EL_TOP | EL_LEFT | EL_RIGHT | EL_BOTTOM | EL_HORIZONTAL | EL_VERTICAL;
		public Color4 color = Color4.BLACK4;
		public float width = 3f;
		/** true일 경우, range에 셀의 일부가 포함되더라도 그 셀 전체에 적용한다. 디폴트는 true이다. */
		public boolean autoComplete = true;
	}
	
	/**
	 * range, offset, span, hop 등을 통해 추가적인 배경색을 출력할 수 있는 양식
	 * 
	 * @author 김현우
	 */
	public static class ExtraBackground {
		public Rectangle range;
		public Color4 color = Color4.LTGRAY4;
		/** offset은 range이 없을 경우 처음 셀을 기준으로, range가 존재하면 range를 기준으로 한다 */
		public int offset;
		/** 배경색이 출력될 길이를 지정한다. */
		public int span = 1;
		/** 배경색이 출력되는 사이의 공간의 길이를 지정한다 */
		public int hop = 1;
		/** true일 경우 하단으로 나아가며 수평으로 배경색을 그리며, false라면 우측으로 나아가며 수직으로 배경색을 그린다 */
		public boolean horizontal = true;
		
		protected boolean hasGradation() {
			return false;
		}
	}
	
	/**
	 * 그라데이션이 추가된 배경색 양식. 네 모퉁이의 색을 각각 지정할 수 있다.
	 * 
	 * @author 김현우
	 */
	public static class ExtraGradationBackground extends ExtraBackground {
		public Color4 topLeftColor;
		public Color4 topRightColor;
		public Color4 bottomLeftColor;
		public Color4 bottomRightColor;
		
		@Override
		protected boolean hasGradation() {
			return true;
		}
	}

}
