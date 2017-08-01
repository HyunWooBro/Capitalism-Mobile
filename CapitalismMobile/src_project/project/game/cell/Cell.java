package project.game.cell;

import project.game.Time;
import project.game.building.Building;

import android.graphics.Point;

import core.math.GeometryMath;
import core.math.Rectangle;
import core.math.Vector2;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.Image;

/**
 * 맵을 구성하는 최소단위인 셀을 표현한다.
 * 
 * @author 김현우
 */
public class Cell extends Image {
	/** 지가 */
	public enum LandValue {
		CLASS_A(160000), 
		CLASS_B(140000), 
		CLASS_C(120000), 
		CLASS_D(100000), 
		CLASS_E(85000), 
		CLASS_F(70000), 
		CLASS_G(60000), 
		CLASS_H(50000), 
		CLASS_Z(0);

		private long m_value;

		LandValue(long value) {
			m_value = value;
		}

		public long GetValue() {
			return m_value;
		}
	}

	public enum CellType {
		ROAD, 
		RIVER, 
		HOUSE_1X1, 
		HOUSE_2X2, 
		GROUND1, 
		GROUND2, 
		APAPTMENT_2X2, 
		PORT, 
	}

	/** 한 셀의 기본 너비. */
	public static final int CELL_WIDTH = 64;

	/** 한 셀의 기본 높이. 이미지로는 31이지만 맵의 구조상 실질적으로 32이다. */
	public static final int CELL_HEIGHT = 32;

	/** cell의 양의 기울기. y좌표가 아래이므로 우측 상단과 좌측 하단의 기울기를 나타낸다. */
	public static final float CELL_SLOPE_UP = GeometryMath.getLineSlope(0, 0, CELL_WIDTH / 2,
			CELL_HEIGHT / 2);

	/** cell의 음의 기울기. y좌표가 아래이므로 우측 하단과 좌측 상단의 기울기를 나타낸다. */
	public static final float CELL_SLOPE_DOWN = GeometryMath.getLineSlope(0, 0, CELL_WIDTH / 2,
			-CELL_HEIGHT / 2);

	/*package*/ int mRow;
	/*package*/ int mColumn;

	/*package*/ LandValue mLandValue;
	/*package*/ CellType m_celltype;
	/*package*/ int m_maxlink;
	/*package*/ int m_currentlink;
	/*package*/ Point m_first_link_point = new Point(-1, -1);
	/*package*/ Vector2 m_cellpoint = new Vector2();
	/*package*/ Rectangle mCellRectangle = new Rectangle();
	/*package*/ Point m_imagepoint = new Point();
	/*package*/ Point m_cell_imagepoint = new Point();
	/*package*/ Vector2 mBuildingPoint = new Vector2();
	/*package*/ // BuildingType mBuildingType;
	/*package*/ Building mBuilding;
	/*package*/ boolean mDrawLast;

	// public Rect m_imagerect = new Rect();
	// public Rect m_constructionrect = new Rect();

	public Cell(Drawable drawable) {
		super(drawable);
	}

	@Override
	protected void updateDrawable(long time) {
		if(Time.getInstance().isPaused())
			return;
		super.updateDrawable(time);
	}

}
