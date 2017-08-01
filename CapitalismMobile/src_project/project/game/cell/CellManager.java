package project.game.cell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import org.framework.R;

import project.framework.Constant;
import project.framework.Utils;
import project.framework.Utils.MessageDialogData;
import project.game.CPU;
import project.game.GameOptions;
import project.game.GameScene.GameScreenListener;
import project.game.GameScene.GameScreenType;
import project.game.Time;
import project.game.building.Building;
import project.game.building.BuildingManager;
import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.department.Department;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.DepartmentManager;
import project.game.building.department.stock.Purchase;
import project.game.cell.Cell.CellType;
import project.game.cell.Cell.LandValue;
import project.game.city.CityListener;
import project.game.city.CityManager;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.news.NewsItem;
import project.game.news.NewsManager;
import project.game.product.Product.DisplayProduct;
import project.game.ui.BuildingBar;
import project.game.ui.CityIconButtons;
import project.game.ui.CityIconButtons.CityIconType;
import project.game.ui.UIManager;

import android.graphics.Point;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.OrthoCamera;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.ShapeRenderer.ShapeType;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.PlayMode;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.GeometryMath;
import core.math.MathUtils;
import core.math.Rectangle;
import core.math.Vector2;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.Event;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.Label;
import core.scene.stage.actor.widget.label.SLabel;
import core.utils.Disposable;
import core.utils.ResUtils;
import core.utils.pool.Pools;

/**
 * 셀과 관련된 모든 기능을 관리하고 조정하는 클래스
 * 
 * @author 김현우
 */
public class CellManager extends Group<CellManager> implements GameScreenListener, CityListener,
		Disposable {

	private static final Vector2 VECTOR = new Vector2();

	private List<Actor<?>> mDrawLastList = new ArrayList<Actor<?>>();

	private List<CellMap> mCellMapList = new ArrayList<CellMap>();
	private int mCurrentCellMapIndex;

	private CellSelector mSelector = new CellSelector();

	private CityIconButtons mCityIconButtons;

	private double mTotalLandValue;
	private double mTotalPurchase;

	private boolean mZoomOnce;

	private float mVelocityX;
	private float mVelocityY;

	private long mFlingDuration;
	private long mFlingTimer;
	private boolean mFlinging;

	private boolean mInitZoom;
	private float mInitZoomInterpolator = 0.015f;

	private TextureRegion[] mLandValueRegions;

	private SLabel portName;
	private SLabel retailName;
	private SLabel factoryName;
	private SLabel farmName;
	private SLabel rndName;

	private long mPinchTime;

	private ShapeRenderer mRenderer;

	private TextureRegion mProfitPositiveHigh;
	private TextureRegion mProfitPositiveMedium;
	private TextureRegion mProfitPositiveLow;

	private TextureRegion mProfitNegativeHigh;
	private TextureRegion mProfitNegativeMedium;
	private TextureRegion mProfitNegativeLow;

	private TextureRegion mProfitNone;

	private CLabel mDistanceLabel;

	private Texture mCloudTexture;
	private Vector2 mWindVector = new Vector2();
	private float mCloudAlpha = 1f;
	private long mCloudTime;

	private int mOldQuadrantSide = -1;
	private int mQuadrantSide;

	/** 1.0f를 기준으로 원하는 확대 크기 - 1.5배 */
	public static final float SCREEN_ZOOM_IN = 1.0f / (1.5000f);

	/** 1.0f으로 고정 */
	public static final float SCREEN_ZOOM_NORMAL = 1.0f;

	/** 1.0f를 기준으로 원하는 축소 크기 - 0.6666배 */
	public static final float SCREEN_ZOOM_OUT = 1.0f / (0.6666f);

	/** 싱글턴 인스턴스 */
	private volatile static CellManager sInstance;

	private CellManager() {
	}

	public static CellManager getInstance() {
		if(sInstance == null) {
			synchronized(CellManager.class) {
				if(sInstance == null) {
					sInstance = new CellManager();
				}
			}
		}
		return sInstance;
	}

	public void init() {

		mRenderer = new ShapeRenderer();

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);
		Texture tempTexture = tm.getTexture(R.drawable.temp);

		mCloudTexture = tm.getTexture(R.drawable.cloud);

		mProfitPositiveHigh = imageTexture.getTextureRegion("cell_profit_positive_high");
		mProfitPositiveMedium = imageTexture.getTextureRegion("cell_profit_positive_medium");
		mProfitPositiveLow = imageTexture.getTextureRegion("cell_profit_positive_low");

		mProfitNegativeHigh = imageTexture.getTextureRegion("cell_profit_negative_high");
		mProfitNegativeMedium = imageTexture.getTextureRegion("cell_profit_negative_medium");
		mProfitNegativeLow = imageTexture.getTextureRegion("cell_profit_negative_low");

		mProfitNone = imageTexture.getTextureRegion("cell_profit_none");

		mDistanceLabel = new CLabel("", R.array.label_array_outline_yellow_12, fontTexture);

		buildMap();

		portName = new SLabel(R.string.label_building_port, fontTexture).center();
		retailName = new SLabel(R.string.label_building_retail, fontTexture).center();
		factoryName = new SLabel(R.string.label_building_factory, fontTexture).center();
		farmName = new SLabel(R.string.label_building_farm, fontTexture).center();
		rndName = new SLabel(R.string.label_building_rnd, fontTexture).center();

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		cellMap.zoom = 2.5f;

		addEventListener(new GestureTouchListener() {

			@Override
			public boolean handle(Event event) {
				boolean handled = super.handle(event);
				if(Core.INPUT.getTouchCount() < 2) {
					mZoomOnce = false;
				}
				return handled;
			}

			@Override
			public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
				mFlinging = false;
				mOldQuadrantSide = -1;
				return true;
			}

			@Override
			public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {
				selectCell(x, y);
			}

			@Override
			public void onScroll(TouchEvent event, float distanceX, float distanceY, float x,
					float y, Actor<?> listener) {
				// if(Core.INPUT.getTouchCount() > 1) return;
				if(mPinchTime > 0) return;
				scrollWithinMap(distanceX, distanceY);
			}

			@Override
			public void onZoom(TouchEvent event, float initDistance, float distance,
					Actor<?> listener) {
				if(mZoomOnce) return;

				CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
				float zoom = cellMap.zoom;

				if(initDistance - distance > Constant.BASE_GESTURE_DISTANCE * zoom) {
					zoom /= 2;
					if(zoom <= 0.5f) zoom = 0.5f;
					zoom(zoom);
				} else if(distance - initDistance > Constant.BASE_GESTURE_DISTANCE * zoom) {
					zoom *= 2;
					if(zoom > 2f) zoom = 2f;
					zoom(zoom);
				}

				cellMap.zoom = zoom;
			}

			@Override
			public void onFling(TouchEvent event, float velocityX, float velocityY, float x,
					float y, Actor<?> listener) {
				if(mPinchTime > 0) return;

				if(Math.signum(velocityX) == Math.signum(mVelocityX)) {
					mVelocityX += velocityX;
				} else {
					mVelocityX = velocityX;
				}

				if(Math.signum(velocityY) == Math.signum(mVelocityY)) {
					mVelocityY += velocityY;
				} else {
					mVelocityY = velocityY;
				}

				mFlingDuration = (long) Vector2.len(mVelocityX, mVelocityY);
				mFlingTimer = mFlingDuration;
				mFlinging = true;
			}

		});
		
		// 현재 도시모드를 조사하기 위한 준비
		mCityIconButtons = (CityIconButtons) UIManager.getInstance().getChildByClass(
				CityIconButtons.class);
	}

	public void selectCell(float x, float y) {
		mSelector.select(x, y);
		calculateTotalConstructionPrice();
	}

	public void selectCell(int row, int col) {
		mSelector.select(row, col);
		calculateTotalConstructionPrice();
	}

	public void calculateTotalConstructionPrice() {
		if(BuildingBar.sSelectedBuildingDescription == null) return;
		if(!mSelector.hasSelectedCell()) return;

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		int mapSize = cellMap.size;

		int row = mSelector.getSelectedCell().mRow;
		int col = mSelector.getSelectedCell().mColumn;
		int size = mSelector.getSelectorSize();

		if(mapSize < row + size || mapSize < col + size) return;

		int totalLandValue = 0;
		int totalPurchase = 0;

		for(int i = row; i < row + size; i++) {
			for(int j = col; j < col + size; j++) {
				Cell cell = cells[i][j];

				totalLandValue += cell.mLandValue.GetValue();

				if(cell.m_celltype == CellType.HOUSE_1X1) {
					totalPurchase += cell.mLandValue.GetValue() * 0.5;
				}
			}
		}
		mTotalLandValue = totalLandValue;
		mTotalPurchase = totalPurchase;
	}

	private void zoom(float zoom) {
		getFloor().getCamera().setZoom(zoom);
		mZoomOnce = true;
		mPinchTime = 350;
		mCloudTime = 2000;
	}

	private void buildMap() {

		mCellMapList.clear();
		mCellMapList.add(new CellMap());

		CellMap cellMap = mCellMapList.get(0);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		String map = ResUtils.openRawResourceAsString(R.raw.map_25by25);

		// input을 토큰으로 구분
		StringTokenizer s = new StringTokenizer(map, " |\r\n\t");
		// 앞으로 버전과 캐피탈리즘 맵이 맞는지 확인작업까지
		// if(s.hasMoreTokens(" \r"))
		cellMap.size = Integer.parseInt(s.nextToken());
		int mapSize = cellMap.size;

		cellMap.cells = new Cell[mapSize][mapSize];
		Cell[][] cells = cellMap.cells;

		for(int i = 0; i < mapSize; i++) {
			for(int j = 0; j < mapSize; j++) {
				Cell cell = null;

				// if(s.hasMoreTokens())
				int type = Integer.parseInt(s.nextToken());

				if(type == 1) {
					if(MathUtils.randomBoolean(0.3333f)) {
						cell = new Cell(Drawable.newDrawable(imageTexture
								.getTextureRegion("cell_house2")));
						// cell.mDrawable =
						// Drawable.newDrawable(imageTexture.getTextureRegion("cell_house2"));
					} else {
						cell = new Cell(Drawable.newDrawable(imageTexture
								.getTextureRegion("cell_house1")));
						// cell.mDrawable =
						// Drawable.newDrawable(imageTexture.getTextureRegion("cell_house1"));
					}
					cell.m_celltype = Cell.CellType.HOUSE_1X1;
				}

				if(type == 2) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_ground1")));
					cell.m_celltype = Cell.CellType.GROUND1;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_ground1"));
				}

				if(type == 3) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_ground2")));
					cell.m_celltype = Cell.CellType.GROUND2;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_ground2"));
				}

				if(type == 5) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_road1")));
					cell.m_celltype = Cell.CellType.ROAD;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_road1"));
				}

				if(type == 9) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_2x2_house1")));
					cell.m_celltype = Cell.CellType.HOUSE_2X2;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_2x2_house1"));
					cell.m_maxlink = 4;
				}

				if(type == 10) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_2x2_apratment1")));
					cell.m_celltype = Cell.CellType.APAPTMENT_2X2;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_2x2_apratment1"));
					cell.m_maxlink = 4;
				}

				if(type == 80) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_southeast_river1")));
					cell.m_celltype = Cell.CellType.RIVER;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_southeast_river1"));
				}

				if(type == 81) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_southeast_river2")));
					cell.m_celltype = Cell.CellType.RIVER;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_southeast_river2"));
				}

				if(type == 82) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_southbridge_river1")));
					cell.m_celltype = Cell.CellType.RIVER;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_southbridge_river1"));
				}

				if(type == 83) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_southbridge_river2")));
					cell.m_celltype = Cell.CellType.RIVER;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_southbridge_river2"));
				}

				if(type == 84) {
					cell = new Cell(Drawable.newDrawable(imageTexture
							.getTextureRegion("cell_southwest_river2")));
					cell.m_celltype = Cell.CellType.RIVER;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_southwest_river2"));
				}

				if(type == 99) {
					cell = new Cell(Drawable.newDrawable(imageTexture.getTextureRegion("cell_port")));
					cell.m_celltype = Cell.CellType.PORT;
					// cell.mDrawable =
					// Drawable.newDrawable(imageTexture.getTextureRegion("cell_port"));
					cell.m_maxlink = 9;
				}

				String landValue = s.nextToken();
				if(landValue.equalsIgnoreCase("a")) {
					cell.mLandValue = LandValue.CLASS_A;
				} else if(landValue.equalsIgnoreCase("b")) {
					cell.mLandValue = LandValue.CLASS_B;
				} else if(landValue.equalsIgnoreCase("c")) {
					cell.mLandValue = LandValue.CLASS_C;
				} else if(landValue.equalsIgnoreCase("d")) {
					cell.mLandValue = LandValue.CLASS_D;
				} else if(landValue.equalsIgnoreCase("e")) {
					cell.mLandValue = LandValue.CLASS_E;
				} else if(landValue.equalsIgnoreCase("f")) {
					cell.mLandValue = LandValue.CLASS_F;
				}

				int curlink = Integer.parseInt(s.nextToken());
				cell.m_currentlink = curlink;

				// cell.mBuildingType = null;

				addChild(cell);

				cell.mRow = i;
				cell.mColumn = j;

				cell.m_cellpoint.x = (mapSize - 1) * Cell.CELL_WIDTH / 2 + j * Cell.CELL_WIDTH / 2
						- i * Cell.CELL_WIDTH / 2 + Cell.CELL_WIDTH / 2;
				cell.m_cellpoint.y = j * Cell.CELL_HEIGHT / 2 + i * Cell.CELL_HEIGHT / 2
						+ Cell.CELL_HEIGHT / 2;

				cell.mCellRectangle.set(cell.m_cellpoint.x - Cell.CELL_WIDTH / 2,
						cell.m_cellpoint.y - Cell.CELL_HEIGHT / 2, Cell.CELL_WIDTH,
						Cell.CELL_HEIGHT);

				if(cell.m_celltype == Cell.CellType.HOUSE_1X1) {
					cell.m_imagepoint.x = (mapSize - 1) * Cell.CELL_WIDTH / 2 + j * Cell.CELL_WIDTH
							/ 2 - i * Cell.CELL_WIDTH / 2;
					cell.m_imagepoint.y = j * Cell.CELL_HEIGHT / 2 + i * Cell.CELL_HEIGHT / 2
							- (Cell.CELL_HEIGHT - 1);

				} else if(cell.m_celltype == Cell.CellType.GROUND2) {
					cell.m_imagepoint.x = (mapSize - 1) * Cell.CELL_WIDTH / 2 + j * Cell.CELL_WIDTH
							/ 2 - i * Cell.CELL_WIDTH / 2;
					cell.m_imagepoint.y = j * Cell.CELL_HEIGHT / 2 + i * Cell.CELL_HEIGHT / 2
							- (Cell.CELL_HEIGHT - 1);

				} else if(cell.m_celltype == Cell.CellType.HOUSE_2X2) {
					if(cell.m_currentlink == cell.m_maxlink) {
						cell.mBuildingPoint.x = cell.m_cellpoint.x - Cell.CELL_WIDTH / 2 * 2;
						cell.mBuildingPoint.y = cell.m_cellpoint.y - (Cell.CELL_HEIGHT - 1) * 3
								- (Cell.CELL_HEIGHT - 1) / 2;
						addCellGroup(i - 1, j - 1, 2);
					}
				} else if(cell.m_celltype == Cell.CellType.APAPTMENT_2X2) {
					if(cell.m_currentlink == cell.m_maxlink) {
						cell.mBuildingPoint.x = cell.m_cellpoint.x - Cell.CELL_WIDTH / 2 * 2;
						cell.mBuildingPoint.y = cell.m_cellpoint.y - (Cell.CELL_HEIGHT - 1) * 5
								- (Cell.CELL_HEIGHT - 1) / 2;
						addCellGroup(i - 1, j - 1, 2);
					}
				} else if(cell.m_celltype == Cell.CellType.PORT) {
					if(cell.m_currentlink == cell.m_maxlink) {
						Core.APP.info("abc", ""
								+ (-(Cell.CELL_HEIGHT - 1) * 3 - (Cell.CELL_HEIGHT) / 2));
						cell.mBuildingPoint.x = cell.m_cellpoint.x - Cell.CELL_WIDTH / 2 * 3;
						cell.mBuildingPoint.y = cell.m_cellpoint.y - (Cell.CELL_HEIGHT - 1) * 3
								- (Cell.CELL_HEIGHT - 1) / 2;
						addCellGroup(i - 2, j - 2, 3);
					}
				} else {
					cell.m_imagepoint.x = (mapSize - 1) * Cell.CELL_WIDTH / 2 + j * Cell.CELL_WIDTH
							/ 2 - i * Cell.CELL_WIDTH / 2;
					cell.m_imagepoint.y = j * Cell.CELL_HEIGHT / 2 + i * Cell.CELL_HEIGHT / 2;
				}

				cell.m_cell_imagepoint.x = (mapSize - 1) * Cell.CELL_WIDTH / 2 + j
						* Cell.CELL_WIDTH / 2 - i * Cell.CELL_WIDTH / 2;
				cell.m_cell_imagepoint.y = j * Cell.CELL_HEIGHT / 2 + i * Cell.CELL_HEIGHT / 2;

				cells[i][j] = cell;
			}
		}

		// 항구인 경우 m_first_link_point를 설정
		for(int i = 0; i < mapSize; i++) {
			for(int j = 0; j < mapSize; j++) {
				if(cells[i][j].m_celltype == Cell.CellType.PORT && cells[i][j].m_currentlink == 1) {
					BuildingDescription desc = BuildingManager.getInstance().getBDescByCode(
							"PORT1");

					// 맵이 2개 이상일 경우에는 적절한 도시를 설정해야 한다.
					Building building = BuildingManager.getInstance().newBuilding(desc, CityManager.getInstance().getCurrentCity());
					Corporation publicCorp = CorporationManager.getInstance()
							.getPublicCorporation();
					BuildingManager.getInstance().addBuilding(building, publicCorp);

					building.mFirstCellPos.x = i;
					building.mFirstCellPos.y = j;

					for(int i2 = i; i2 < i + 3; i2++) {
						for(int j2 = j; j2 < j + 3; j2++) {
							cells[i2][j2].m_first_link_point.x = i;
							cells[i2][j2].m_first_link_point.y = j;

							// cells[i2][j2].mBuildingType = BuildingType.PORT;
							cells[i2][j2].mBuilding = building;
						}
					}
				}
			}
		}

		cellMap.drawingMap = new boolean[mapSize][mapSize];
		
		// 차량관지라 초기화
		cellMap.vehicleManager.init(cellMap.cells);

		// 맵 정보를 모두 읽은 후 CellGroup을 정리

		int length = LandValue.values().length;
		mLandValueRegions = new TextureRegion[length];
		mLandValueRegions[0] = imageTexture.getTextureRegion("cell_class_a");
		mLandValueRegions[1] = imageTexture.getTextureRegion("cell_class_b");
		mLandValueRegions[2] = imageTexture.getTextureRegion("cell_class_c");
		mLandValueRegions[3] = imageTexture.getTextureRegion("cell_class_d");
		mLandValueRegions[4] = imageTexture.getTextureRegion("cell_class_e");
		mLandValueRegions[5] = imageTexture.getTextureRegion("cell_class_f");
	}

	private boolean scrollWithinMap(float dx, float dy) {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		
		float sensitivity = GameOptions.getInstance().getScrollSensitivity();
		sensitivity = 1f + (sensitivity - 50f) / 100f;
		sensitivity *= cellMap.zoom;
		
		getFloor().getCamera().translate(dx * sensitivity, dy * sensitivity);
		getFloor().getCamera().updateMatrix();

		final Vector2 v = VECTOR;
		screenToLocalCoordinates(v.set(Core.GRAPHICS.getWidth() / 2, Core.GRAPHICS.getHeight() / 2));

		// 스크롤 중에 화면의 중앙 지점이 맵을 벗어나면 가장 가까운 맵의 모서리 위치로 조정한다.
		if(isOutside(v)) {
			// v를 통해 가장 가까운 모서리 위치가 반환된다.
			getFloor().getCamera().setPos(v.x, v.y);
			return false;
		}
		return true;
	}

	/**
	 * 카메라의 중앙점이 맵을 벗어나면 true을 리턴하고 scroll을 통해 맵과 가장 가까운 위치가 반환된다.
	 */
	private boolean isOutside(Vector2 center) {

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		int mapSize = cellMap.size;

		// 화면의 중점(새로 이동한 위치)
		Vector2 centerVector = new Vector2(center);

		// 화면의 중점을 지나는 선과 맵의 모서리와의 만나는 점을 구하기 위해
		// 보조적 점. 화면의 중점을 지나는 선은 이 점도 지나게 된다.
		Vector2 assistVector = new Vector2();

		// 맵의 사분면의 중점(맵의 중앙 위치)
		// +---------- x
		// | 3 | 4
		// | --중점--
		// | 2 | 1
		// y
		Vector2 quadrantPoint = new Vector2(cells[0][0].m_cellpoint.x, cells[0][0].m_cellpoint.y
				+ (mapSize - 1) * Cell.CELL_HEIGHT / 2);

		mQuadrantSide = -1;

		// 4 사분면
		if(quadrantPoint.x <= centerVector.x && quadrantPoint.y >= centerVector.y) {

			mQuadrantSide = 4;

			// 보조적 점의 x좌표는 맵의 중앙 위치에서 1만큼 다른 사분면으로 이동한 지점을 사용한다.
			// 1만큼 다른 사분면으로 굳이 이동한 이유는 이 점이 center.x와 같은 값을 가질 수 없게 하기
			// 위해서이다. x가 같으면 y값도 같게되어 center와 assist의 위치가 동일하게 되기 때문이다.
			// 한편, 보조적 점과 화면의 중앙 점을 지나는 선의 기울기는 이 사분면의 기울기와 직교된다.
			assistVector.set(quadrantPoint.x - 1, GeometryMath.getYOnLine(-1 / Cell.CELL_SLOPE_UP,
					centerVector.x, centerVector.y, quadrantPoint.x - 1));

			// 화면의 중점이 이 사분면에서의 맵의 모서리보다 왼쪽에 있는지를 체크.
			// 그렇다면 맵 외부에 화면이 이동한 것이다.
			// 4사분면 외부
			if(GeometryMath.determineWhichSidePointOn(new Vector2(quadrantPoint.x + Cell.CELL_WIDTH
					/ 2 * mapSize, quadrantPoint.y), new Vector2(quadrantPoint.x, quadrantPoint.y
					- Cell.CELL_HEIGHT / 2 * mapSize), centerVector) > 0) {
				// 맵의 모서리가 화면의 중점과 보조적 점이 지나는 선과 만나는 지점을 구한다
				GeometryMath.intersectTwoLines(assistVector.x, assistVector.y, centerVector.x,
						centerVector.y, quadrantPoint.x + Cell.CELL_WIDTH / 2 * mapSize,
						quadrantPoint.y, quadrantPoint.x, quadrantPoint.y - Cell.CELL_HEIGHT / 2
								* mapSize, center);

			} else {
				// 4사분면 내부
				return false;
			}
		}

		// 3 사분면
		if(quadrantPoint.x >= centerVector.x && quadrantPoint.y >= centerVector.y) {

			mQuadrantSide = 3;

			assistVector
					.set(quadrantPoint.x + 1, GeometryMath.getYOnLine(-1 / Cell.CELL_SLOPE_DOWN,
							centerVector.x, centerVector.y, quadrantPoint.x + 1));

			// 3사분면 외부
			if(GeometryMath.determineWhichSidePointOn(new Vector2(quadrantPoint.x, quadrantPoint.y
					- Cell.CELL_HEIGHT / 2 * mapSize), new Vector2(quadrantPoint.x
					- Cell.CELL_WIDTH / 2 * mapSize, quadrantPoint.y), centerVector) > 0) {
				GeometryMath.intersectTwoLines(assistVector.x, assistVector.y, centerVector.x,
						centerVector.y, quadrantPoint.x - Cell.CELL_WIDTH / 2 * mapSize,
						quadrantPoint.y, quadrantPoint.x, quadrantPoint.y - Cell.CELL_HEIGHT / 2
								* mapSize, center);

			} else {
				// 3사분면 내부
				return false;
			}
		}

		// 2 사분면
		if(quadrantPoint.x >= centerVector.x && quadrantPoint.y <= centerVector.y) {

			mQuadrantSide = 2;

			assistVector.set(quadrantPoint.x + 1, GeometryMath.getYOnLine(-1 / Cell.CELL_SLOPE_UP,
					centerVector.x, centerVector.y, quadrantPoint.x + 1));

			// 2사분면 외부
			if(GeometryMath.determineWhichSidePointOn(new Vector2(quadrantPoint.x - Cell.CELL_WIDTH
					/ 2 * mapSize, quadrantPoint.y), new Vector2(quadrantPoint.x, quadrantPoint.y
					+ Cell.CELL_HEIGHT / 2 * mapSize), centerVector) > 0) {
				GeometryMath.intersectTwoLines(assistVector.x, assistVector.y, centerVector.x,
						centerVector.y, quadrantPoint.x - Cell.CELL_WIDTH / 2 * mapSize,
						quadrantPoint.y, quadrantPoint.x, quadrantPoint.y + Cell.CELL_HEIGHT / 2
								* mapSize, center);

			} else {
				// 2사분면 내부
				return false;
			}
		}

		// 1 사분면
		if(quadrantPoint.x <= centerVector.x && quadrantPoint.y <= centerVector.y) {

			mQuadrantSide = 1;

			assistVector
					.set(quadrantPoint.x - 1, GeometryMath.getYOnLine(-1 / Cell.CELL_SLOPE_DOWN,
							centerVector.x, centerVector.y, quadrantPoint.x - 1));

			// 1사분면 외부
			if(GeometryMath.determineWhichSidePointOn(new Vector2(quadrantPoint.x, quadrantPoint.y
					+ Cell.CELL_HEIGHT / 2 * mapSize), new Vector2(quadrantPoint.x
					+ Cell.CELL_WIDTH / 2 * mapSize, quadrantPoint.y), centerVector) > 0) {
				GeometryMath.intersectTwoLines(assistVector.x, assistVector.y, centerVector.x,
						centerVector.y, quadrantPoint.x + Cell.CELL_WIDTH / 2 * mapSize,
						quadrantPoint.y, quadrantPoint.x, quadrantPoint.y + Cell.CELL_HEIGHT / 2
								* mapSize, center);

			} else {
				// 1사분면 내부
				return false;
			}
		}

		// 새로운 위치는 맵의 모서리를 따라 지정되는데, 이동한 위치가
		// 맵 외부로 나가서 확장된 가상의 맵의 모서리와 만나는 경우에
		// 새로운 위치를 맵의 모서리 내에 존재하도록 보정한다.

		if(center.x > quadrantPoint.x + Cell.CELL_WIDTH / 2 * mapSize) {
			center.set(quadrantPoint.x + Cell.CELL_WIDTH / 2 * mapSize, quadrantPoint.y);
		}

		if(center.x < quadrantPoint.x - Cell.CELL_WIDTH / 2 * mapSize) {
			center.set(quadrantPoint.x - Cell.CELL_WIDTH / 2 * mapSize, quadrantPoint.y);
		}

		if(center.y < quadrantPoint.y - Cell.CELL_HEIGHT / 2 * mapSize) {
			center.set(quadrantPoint.x, quadrantPoint.y - Cell.CELL_HEIGHT / 2 * mapSize);
		}

		if(center.y > quadrantPoint.y + Cell.CELL_HEIGHT / 2 * mapSize) {
			center.set(quadrantPoint.x, quadrantPoint.y + Cell.CELL_HEIGHT / 2 * mapSize);
		}

		return true;
	}

	@Override
	public void update(long time) {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		getFloor().setCamera(cellMap.camera);

		super.update(time);

		// 구름 이동 업데이트
		if(!Time.getInstance().isPaused()) {
			mWindVector.add(0.1f, 0.15f);
		}

		mSelector.update(time);

		if(!cellMap.initialized) {
			cellMap.initialized = true;
			moveCameraToCenter();
		}

		// 초기 축소 애니메이션
		if(!mInitZoom) {
			cellMap.zoom -= mInitZoomInterpolator;
			mInitZoomInterpolator += 0.0001;
			if(cellMap.zoom < 1f) {
				mInitZoom = true;
				cellMap.zoom = 1f;
			}
			getFloor().getCamera().setZoom(cellMap.zoom);
		}
		
		cellMap.vehicleManager.update(time);

		updateFling(time);
	}

	private void updateFling(long time) {
		long delta = Core.GRAPHICS.getDeltaTime();
		if(mFlinging) {
			mFlingTimer -= delta;
			if(mFlingTimer < 0) {
				mFlinging = false;
				mOldQuadrantSide = -1;
				mVelocityX = 0f;
				mVelocityY = 0f;
			}
		}

		float fling = (float) mFlingTimer / mFlingDuration;

		final float COE = 7.5f;

		float dx = 0f;
		float dy = 0f;

		if(Math.abs(mVelocityX) > 0) {
			float temp = mVelocityX * delta / 1000f;
			dx = temp * fling;
			mVelocityX -= temp * COE;
			if(Math.abs(mVelocityX) < 0.001f) {
				mVelocityX = 0f;
			}
		}

		if(Math.abs(mVelocityY) > 0) {
			float temp = mVelocityY * delta / 1000f;
			dy = temp * fling;
			mVelocityY -= temp * COE;
			if(Math.abs(mVelocityY) < 0.001f) {
				mVelocityY = 0f;
			}
		}

		if(mFlinging) {
			// 맵의 범위 내에서 fling한다. 외부로 향하는 경우 false를 리턴하는데
			// 맵의 모서리를 따라 움직이도록 한다.
			if(!scrollWithinMap(-dx, -dy)) {
				final Vector2 v = VECTOR;
				switch(mQuadrantSide) {
					case 1:
					case 3:
						v.set(1f, Cell.CELL_SLOPE_DOWN).nor();
						break;
					case 2:
					case 4:
						v.set(1f, Cell.CELL_SLOPE_UP).nor();
						break;
				}

				if(mQuadrantSide == 1 && mOldQuadrantSide == 4)
					mVelocityX = 0f;
				if(mQuadrantSide == 4 && mOldQuadrantSide == 1)
					mVelocityX = 0f;
				if(mQuadrantSide == 2 && mOldQuadrantSide == 3)
					mVelocityX = 0f;
				if(mQuadrantSide == 3 && mOldQuadrantSide == 2)
					mVelocityX = 0f;

				float dot = v.dot(mVelocityX, mVelocityY);
				mVelocityX = v.x * dot;
				mVelocityY = v.y * dot;

				mOldQuadrantSide = mQuadrantSide;
			}
		}

		mPinchTime -= delta;
		if(mPinchTime < 0) {
			mPinchTime = 0;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void draw(Batch batch, float parentAlpha) {

		// 셀을 출력한다.
		drawCells(batch);

		switch(mCityIconButtons.getIconType()) {
			// 지가 모드
			case LAND_VALUE:
				drawLandValueMode(batch);
				break;
			// 수익 모드
			case PROFIT:
				drawProfitMode(batch);
				break;
			// 거래 모드
			case TRANSACTION:
				drawTransactionMode(batch);
				break;
		}

		// CellSelector을 출력한다.
		mSelector.draw(batch, parentAlpha);
		// Building의 이름을 출력한다.
		drawBuildingName(batch);
		// (가장 축소했을 경우에만) 구름을 출력한다.
		drawCloud(batch);
	}

	private void drawCells(Batch batch) {

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		List<CellGroup> cellGroupList = cellMap.cellGroupList;
		int n = cellMap.size;
		boolean[][] drawingMap = cellMap.drawingMap;

		// 중복되어 그리는 것을 방지하기 위해 맵의 크기가 같은 boolean형 배열을 이용
		/*for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				drawingMap[i][j] = false;
			}
		}*/

		if(isBackgroundCellDark()) {
			batch.setColor(0xFF666666);
		} else {
			batch.setColor(0xFFFFFFFF);
		}

		// 가장 먼저, 도로와 차량부터 그린다. 도로부터 그리는 이유는 차량때문이다.
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				Cell cell = cells[i][j];
				if(cell.m_celltype == Cell.CellType.ROAD) {
					// 어차피 도로는 항상 true이므로 미리 지정하는 것이 좋겠다.
					drawingMap[i][j] = true;
					cell.getDrawable().draw(batch, cell.m_imagepoint.x, cell.m_imagepoint.y,
							cell.getDrawable().getWidth(), cell.getDrawable().getHeight(), false,
							false);
				} else {
					drawingMap[i][j] = false;
				}
			}
		}

		// 도로를 그린 후, 이제 차량을 그린다.
		cellMap.vehicleManager.draw(batch);
		//VehicleManager.GetInstance().Render(canvas, off_x, off_y, this);

		// 그다음, 셀그룹(2X2 이상의 셀의 그룹)을 기준으로 화면을 그린다.
		int m = cellGroupList.size();
		for(int k = 0; k < m; k++) {
			Point point = cellGroupList.get(k).m_point;
			for(int i = 0; i <= point.x; i++) {
				for(int j = 0; j <= point.y; j++) {
					if(!drawingMap[i][j]) {
						drawingMap[i][j] = true;
						Cell cell = cells[i][j];

						// 셀이 다른 셀과 독립적이면
						if(cell.m_maxlink == 0) {
							cell.getDrawable().draw(batch, cell.m_imagepoint.x,
									cell.m_imagepoint.y, cell.getDrawable().getWidth(),
									cell.getDrawable().getHeight(), false, false);
						} else { // 셀이 다른 셀과 연결되어 있다면
							// 셀의 m_maxlink과 m_currentlink가 같은 셀에 한하여
							// 즉, 건물의 최하단 셀에서
							if(cell.m_maxlink == cell.m_currentlink) {
								// 셀에 건물이 지어져 있다면
								if(cell.mBuilding != null) {
									// +1은 지금 현재 셀 배치에 약간의 공간이 남기 때문에 추가했다.
									// 셀의 높이계산에서 문제가 있는 것 같다. 이것을 통해 2X2셀 이상을
									// 점유하는
									// 건물을 그리더라도 공백이 크게 느껴지지 않는다.

									if(!cell.mDrawLast) {
										cell.moveTo(cell.mBuildingPoint.x,
												cell.mBuildingPoint.y - 1);
										cell.draw(batch, 1f);
									}

									if(isBackgroundCellDark()) {
										batch.setColor(0xFF666666);
									} else {
										batch.setColor(0xFFFFFFFF);
									}

								} else {
									cell.getDrawable().draw(batch, cell.mBuildingPoint.x,
											cell.mBuildingPoint.y - 1,
											cell.getDrawable().getWidth(),
											cell.getDrawable().getHeight(), false, false);
								}
							}
						}
					}
				}
			}
		}

		// 이제 남은 셀을 채운다. 남은 셀은 모두 1X1셀 뿐이다.
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(!drawingMap[i][j]) {
					drawingMap[i][j] = true;
					Cell cell = cells[i][j];
					cell.getDrawable().draw(batch, cell.m_imagepoint.x, cell.m_imagepoint.y,
							cell.getDrawable().getWidth(), cell.getDrawable().getHeight(), false,
							false);
				}
			}
		}

		// 애니메이션을 적용하는 셀은 가장 마지막에 그려준다.
		List<Actor<?>> drawLastList = mDrawLastList;
		int l = drawLastList.size();
		for(int i = 0; i < l; i++) {
			Cell cell = (Cell) drawLastList.get(i);
			cell.moveTo(cell.mBuildingPoint.x, cell.mBuildingPoint.y - 1);
			cell.draw(batch, 1f);
		}

		batch.setColor(0xFFFFFFFF);
	}

	public boolean isBackgroundCellDark() {
		CityIconType type = mCityIconButtons.getIconType();
		return type == CityIconType.PROFIT || type == CityIconType.TRANSACTION;
	}

	@SuppressWarnings("incomplete-switch")
	private void drawLandValueMode(Batch batch) {

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		int n = cellMap.size;

		// 하나의 큰 텍스쳐로 만들자.
		TextureRegion region = null;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				Cell cell = cells[i][j];
				switch(cell.mLandValue) {
					case CLASS_A:
						region = mLandValueRegions[0];
						break;
					case CLASS_B:
						region = mLandValueRegions[1];
						break;
					case CLASS_C:
						region = mLandValueRegions[2];
						break;
					case CLASS_D:
						region = mLandValueRegions[3];
						break;
					case CLASS_E:
						region = mLandValueRegions[4];
						break;
					case CLASS_F:
						region = mLandValueRegions[5];
						break;
				}
				batch.draw(region, cell.m_cell_imagepoint.x, cell.m_cell_imagepoint.y);
			}
		}
	}

	private void drawProfitMode(Batch batch) {

		int index = Time.getInstance().getMonthlyArrayIndex();

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		int n = cellMap.size;

		// 사실 sCitiyiconSelection == 2, 3은 이렇게 온 맵을 순환하는 것이 아니고 리스트로 해당 지역만
		// 확인하도록 만들어야 한다.
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				Cell cell = cells[i][j];
				if(cell.mBuilding != null && !cell.mBuilding.getDescription().type.equals("Port")) {
					double temp = cell.mBuilding.mMonthlyNetprofit[index];

					float x = cell.m_cell_imagepoint.x;
					float y = cell.m_cell_imagepoint.y;

					if(temp >= 0) {
						temp /= CPU.m_max_daily_positive_profit;
						if(temp > 0.666) {
							batch.draw(mProfitPositiveHigh, x, y);
						} else if(temp > 0.333) {
							batch.draw(mProfitPositiveMedium, x, y);
						} else
							batch.draw(mProfitPositiveLow, x, y);
					} else {
						temp /= CPU.m_max_daily_negative_profit;
						if(temp > 0.666) {
							batch.draw(mProfitNegativeHigh, x, y);
						} else if(temp > 0.333) {
							batch.draw(mProfitNegativeMedium, x, y);
						} else
							batch.draw(mProfitNegativeLow, x, y);
					}
				}
				/*
				 * else { batch.draw(mProfitNone,
				 * mCells[i][j].m_cell_imagepoint.x,
				 * mCells[i][j].m_cell_imagepoint.y);
				 * canvas.drawBitmap(m_cell_profit_none_bitmap,
				 * mCells[i][j].m_cell_imagepoint.x - off_x,
				 * mCells[i][j].m_cell_imagepoint.y - off_y, null); }
				 */
			}
		}
	}

	private void drawTransactionMode(Batch batch) {

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		//int n = cellMap.size;

		batch.flush();

		mRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		mRenderer.begin(ShapeType.FILLED);
		mRenderer.setColor(0xFFFFFFFF);

		// 선택한 건물을 기준으로 표시
		if(mSelector.hasSelectedBuilding()) {
			Building building = mSelector.getSelectedBuilding();
			int i = (int) building.mFirstCellPos.x;
			int j = (int) building.mFirstCellPos.y;
			Cell cell = cells[i][j];

			for(int k = 0; k < DepartmentManager.NUM_DEPARTMENTS; k++) {
				Department department = cell.mBuilding.getDepartmentManager().getDepartments()[k];
				if(department != null) {

					// 구매부를 기준으로
					if(department.getDepartmentType() == DepartmentType.PURCHASE) {
						Purchase purchase = (Purchase) department;
						if(purchase.mLinkedBuilding != null) {
							Building srcBuilding = purchase.mLinkedBuilding;
							Building dstBuilding = cell.mBuilding;
							
							boolean dealing = purchase.isDealing();

							int source_size = srcBuilding.getDescription().size;
							int destination_size = dstBuilding.getDescription().size;

							int x = (int) srcBuilding.mFirstCellPos.x;
							int y = (int) srcBuilding.mFirstCellPos.y;
							float srcX = cells[x][y].m_cellpoint.x;
							float srcY = cells[x][y].m_cellpoint.y;
							srcY += Cell.CELL_HEIGHT / 2 * (source_size - 1);

							float dstX = cell.m_cellpoint.x;
							float dstY = cell.m_cellpoint.y;
							dstY += Cell.CELL_HEIGHT / 2 * (destination_size - 1);

							final Vector2 pos = VECTOR;
							calculateDistanceLabelPos(srcX, srcY, dstX, dstY, pos);

							/*if(dealing) {
								mRenderer.setColor(Color4.WHITE4);
							} else {
								mRenderer.setColor(Color4.LTRED4);
							}*/
							mRenderer.drawRectLine(srcX, srcY, dstX, dstY, 2 * cellMap.zoom);
							//mRenderer.setColor(Color4.WHITE4);

							drawDistanceLabel(batch, srcX, srcY, dstX, dstY,
									purchase.mDistanceFromLinkedBuilding, pos);
						}
					}

				}
			}
			
			// 진열제품을 기준으로
			// 현재 건물이 중복되어 그려지므로 최적화가 필요하다.
			List<DisplayProduct> displayProductList = building.getDisplayProductList();
			if(displayProductList != null) {
				int m = displayProductList.size();
				for(int s=0; s<m; s++) {
					DisplayProduct display = displayProductList.get(s);
					List<Purchase> purchaseList = display.purchaseList;
					int l = purchaseList.size();
					for(int p=0; p<l; p++) {
						Purchase purchase = purchaseList.get(p);
						
						Building srcBuilding = cell.mBuilding;
						Building dstBuilding = purchase.getDepartmentManager().getBuilding();
						
						boolean dealing = purchase.isDealing();

						int source_size = srcBuilding.getDescription().size;
						int destination_size = dstBuilding.getDescription().size;

						float srcX = cell.m_cellpoint.x;
						float srcY = cell.m_cellpoint.y;
						srcY += Cell.CELL_HEIGHT / 2 * (source_size - 1);

						int x = (int) dstBuilding.mFirstCellPos.x;
						int y = (int) dstBuilding.mFirstCellPos.y;
						float dstX = cells[x][y].m_cellpoint.x;
						float dstY = cells[x][y].m_cellpoint.y;
						dstY += Cell.CELL_HEIGHT / 2 * (destination_size - 1);

						final Vector2 pos = VECTOR;
						calculateDistanceLabelPos(srcX, srcY, dstX, dstY, pos);

						/*if(dealing) {
							mRenderer.setColor(Color4.WHITE4);
						} else {
							mRenderer.setColor(Color4.LTRED4);
						}*/
						mRenderer.drawRectLine(srcX, srcY, dstX, dstY, 2 * cellMap.zoom);
						//mRenderer.setColor(Color4.WHITE4);

						//int dstIndex = sales.m_linked_department_indexlist.get(t);
						//double distance = ((Purchase) dstBuilding.getDepartmentManager()
						//		.getDepartments()[dstIndex]).mDistanceFromLinkedBuilding;
						double distance = purchase.mDistanceFromLinkedBuilding;
						drawDistanceLabel(batch, srcX, srcY, dstX, dstY, distance, pos);
					}
				}
			}
			
		} else { // 선택한 건물이 없을 때, 모든 연결선을 표시
			
			List<Building> buildingList = BuildingManager.getInstance().getBuildingList();
			int n = buildingList.size();
			for(int i=0; i<n; i++) {
				Building building = buildingList.get(i);
	
				for(int k = 0; k < DepartmentManager.NUM_DEPARTMENTS; k++) {
					Department department = building.getDepartmentManager().getDepartments()[k];
					if(department != null) {

						if(department.getDepartmentType() == DepartmentType.PURCHASE) {
							Purchase purchase = (Purchase) department;
							if(purchase.mLinkedBuilding != null) {
								Building srcBuilding = purchase.mLinkedBuilding;
								Building dstBuilding = building;

								int source_size = srcBuilding.getDescription().size;
								int destination_size = dstBuilding.getDescription().size;

								int x;
								int y;
								
								x = (int) srcBuilding.mFirstCellPos.x;
								y = (int) srcBuilding.mFirstCellPos.y;
								float srcX = cells[x][y].m_cellpoint.x;
								float srcY = cells[x][y].m_cellpoint.y;
								srcY += Cell.CELL_HEIGHT / 2 * (source_size - 1);

								x = (int) building.mFirstCellPos.x;
								y = (int) building.mFirstCellPos.y;
								float dstX = cells[x][y].m_cellpoint.x;
								float dstY = cells[x][y].m_cellpoint.y;
								dstY += Cell.CELL_HEIGHT / 2 * (destination_size - 1);

								final Vector2 pos = VECTOR;
								calculateDistanceLabelPos(srcX, srcY, dstX, dstY, pos);

								mRenderer.drawRectLine(srcX, srcY, dstX, dstY,
										2 * cellMap.zoom);

								drawDistanceLabel(batch, srcX, srcY, dstX, dstY,
										purchase.mDistanceFromLinkedBuilding, pos);
							}
						}
					}
				}
			}
		}

		mRenderer.end();
	}

	private void calculateDistanceLabelPos(float srcX, float srcY, float dstX, float dstY,
			Vector2 pos) {
		float slope = GeometryMath.getInvLineSlope(srcX, srcY, dstX, dstY);
		if(slope == Float.POSITIVE_INFINITY) {
			pos.set(0f, 1f);
		} else {
			pos.set(1f, slope);
		}
		pos.nor();
		pos.scl(7f);
	}

	private void drawDistanceLabel(Batch batch, float srcX, float srcY, float dstX, float dstY,
			double distance, Vector2 pos) {
		if(pos.y > 0) {
			mDistanceLabel.top();
		} else {
			mDistanceLabel.bottom();
		}
		mDistanceLabel.setText(String.format("%.2f", distance) + " km");
		mDistanceLabel.moveTo((srcX + dstX) / 2 + pos.x, (srcY + dstY) / 2 + pos.y);
		mDistanceLabel.draw(batch, 1f);
	}

	private void drawBuildingName(Batch batch) {

		batch.flush();
		
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;

		mRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		mRenderer.begin(ShapeType.FILLED);

		float width = 0f;
		float height = 0f;

		int color = 0;

		Label<?> label = null;
		
		List<Building> buildingList = BuildingManager.getInstance().getBuildingList();
		int n = buildingList.size();
		
		for(int i=0; i<n; i++) {
			Building building = buildingList.get(i);
			
			String type = building.getDescription().type;

			if(type.equals("Factory")) {
				label = factoryName;
				color = Color4.RED;
			} else if(type.equals("Farm")) {
				label = farmName;
				color = Color4.RED;
			} else if(type.equals("Retail")) {
				label = retailName;
				color = Color4.RED;
			} else if(type.equals("RND")) {
				label = rndName;
				color = Color4.RED;
			} else if(type.equals("Port")) {
				label = portName;
				color = Color4.GRAY;
			}

			width = label.getWidth();
			height = label.getHeight();
			
			int x = (int) building.mFirstCellPos.x;
			int y = (int) building.mFirstCellPos.y;
			final Vector2 v = VECTOR;
			v.set(cells[x][y].m_cellpoint.x, cells[x][y].m_cellpoint.y + Cell.CELL_HEIGHT / 2
					* (building.getDescription().size - 1));

			float zoom = cellMap.zoom;

			mRenderer.setColor(Color4.WHITE);
			mRenderer.drawRect(v.x - width / 2 * zoom - 1.5f * zoom, v.y - height / 2 * zoom
					- 1.5f * zoom, width * zoom + 3 * zoom, height * zoom + 3 * zoom);
			mRenderer.setColor(color);
			mRenderer.drawRect(v.x - width / 2 * zoom, v.y - height / 2 * zoom, width * zoom,
					height * zoom);
		}

		mRenderer.end();

		for(int i=0; i<n; i++) {
			Building building = buildingList.get(i);
			
			String type = building.getDescription().type;

			if(type.equals("Factory")) {
				label = factoryName;
			} else if(type.equals("Farm")) {
				label = farmName;
			} else if(type.equals("Retail")) {
				label = retailName;
			} else if(type.equals("RND")) {
				label = rndName;
			} else if(type.equals("Port")) {
				label = portName;
			}
			
			width = label.getWidth();
			height = label.getHeight();
			
			int x = (int) building.mFirstCellPos.x;
			int y = (int) building.mFirstCellPos.y;
			final Vector2 v = VECTOR;
			v.set(cells[x][y].m_cellpoint.x, cells[x][y].m_cellpoint.y + Cell.CELL_HEIGHT / 2
					* (building.getDescription().size - 1));

			float zoom = cellMap.zoom;

			label.moveTo(v.x, v.y);
			label.sizeTo(width * zoom, height * zoom);
			label.draw(batch, 1f);

			label.sizeTo(width, height);
		}
	}

	private void drawCloud(Batch batch) {
		long deltaTime = Core.GRAPHICS.getDeltaTime();

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		float zoom = cellMap.zoom;

		if(zoom >= 2f) {
			mCloudAlpha += (float) deltaTime / 500;
			if(mCloudAlpha >= 1f) {
				mCloudAlpha = 1f;
			}
		} else {
			mCloudAlpha -= (float) deltaTime / 500;
			if(mCloudAlpha <= 0f) {
				mCloudAlpha = 0f;
				return;
			}
		}

		OrthoCamera camera = getFloor().getCamera();

		float cx = camera.getPosX();
		float cy = camera.getPosY();

		float width = camera.getViewportWidth();
		float height = camera.getViewportHeight();

		float zoomedWidth = width * zoom;
		float zoomedHeight = height * zoom;

		Rectangle rectangle = getFloor().getCamera().getVisibleRectangle();

		batch.setColor(mCloudAlpha, 1f, 1f, 1f);
		batch.draw(mCloudTexture, cx / 1.5f - zoomedWidth / 4 + mWindVector.x, cy / 1.5f
				- zoomedHeight / 4 + mWindVector.y, zoomedWidth / 2, zoomedHeight / 2, rectangle.x,
				rectangle.y, rectangle.width, rectangle.height);
	}

	@Override
	public Actor<?> contact(float x, float y) {
		if(mTouchable != Touchable.ENABLED) return null;
		return this;
	}

	/**
	 * 2X2이상을 점유하는 건물의 최하단 셀의 정보를 CellGroup에 추가한다. value가 높은 순서대로 앞에 배치시킨다.
	 */
	private void addCellGroup(int x, int y, int size) {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		List<CellGroup> cellGroupList = cellMap.cellGroupList;
		int mapSize = cellMap.size;

		int index;
		int distance = Math.abs((mapSize - 1) - (x + size - 1))
				+ Math.abs((mapSize - 1) - (y + size - 1));
		int value = distance + size;
		for(index = 0; index < cellGroupList.size(); index++) {
			if(value >= cellGroupList.get(index).m_value) break;
		}
		cellGroupList.add(index, new CellGroup(new Point(x + (size - 1), y + (size - 1)), value));
	}

	public Cell getCell(int row, int col) {
		return getCell(mCurrentCellMapIndex, row, col);
	}

	public Cell getCell(int index, int row, int col) {
		CellMap cellMap = mCellMapList.get(index);
		return cellMap.cells[row][col];
	}

	public Cell[][] getCells() {
		return getCells(mCurrentCellMapIndex);
	}

	public Cell[][] getCells(int index) {
		CellMap cellMap = mCellMapList.get(index);
		return cellMap.cells;
	}

	public int getSize() {
		return getSize(mCurrentCellMapIndex);
	}

	public int getSize(int index) {
		return mCellMapList.get(index).size;
	}

	public Building build() {

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture tempTexture = tm.getTexture(R.drawable.temp);

		BuildingDescription description = BuildingBar.sSelectedBuildingDescription;

		double totalCost = description.setupCost + mTotalLandValue + mTotalPurchase;

		FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
				.getFinancialData();

		if(financialData.cash >= totalCost) {
			financialData.cash -= totalCost;
			UIManager.getInstance().updateCash();
		} else {
			MessageDialogData data = new MessageDialogData();
			data.title = "알림";
			data.content = "건물 건설을 위한 현금이 부족합니다.";
			Utils.showMessageDialog(getFloor().getStage(), "build", data);
			return null;
		}

		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		Utils.m_click_sound.start();

		Building building = BuildingManager.getInstance().newBuilding(description, CityManager.getInstance().getCurrentCity());
		Corporation playerCorp = CorporationManager.getInstance().getPlayerCorporation();
		BuildingManager.getInstance().addBuilding(building, playerCorp);

		Drawable drawable = null;
		if(description.frame > 0) {

			@SuppressWarnings("unchecked")
			List<TextureRegion> animationList = Pools.obtain(ArrayList.class);

			for(int i = 0; i < description.frame; i++)
				animationList.add(tempTexture.getTextureRegion(description.image + "_0" + (i + 1)));

			Animation animation = new Animation(150, animationList).setPlayMode(PlayMode.REPEAT);

			drawable = Drawable.newDrawable(animation);
		} else
			drawable = Drawable.newDrawable(imageTexture.getTextureRegion(description.image));

		// BuildingManager.getInstance().addBuilding(building);

		/*
		 * switch(clazz) { case FACTORY: player.m_factorylist.add((Factory)
		 * building); drawable = Drawable.newDrawable(mFactoryAnimation);
		 * if(((AppMain)Core.APP.getActivity()).isReadyClient) {
		 * ((AppMain)Core.APP
		 * .getActivity()).sendFromClient(1+";"+row+";"+col+";"); } break; case
		 * FARM: player.m_farmlist.add((Farm) building); drawable =
		 * Drawable.newDrawable(imageTexture.getTextureRegion("cell_farm1"));
		 * if(((AppMain)Core.APP.getActivity()).isReadyClient) {
		 * ((AppMain)Core.APP
		 * .getActivity()).sendFromClient(2+";"+row+";"+col+";"); } break; case
		 * RETAIL: player.m_retaillist.add((Retail) building); drawable =
		 * Drawable.newDrawable(imageTexture.getTextureRegion("cell_retail1"));
		 * if(((AppMain)Core.APP.getActivity()).isReadyClient) {
		 * ((AppMain)Core.APP
		 * .getActivity()).sendFromClient(0+";"+row+";"+col+";"); } break; case
		 * RND: player.m_RnDlist.add((RND) building); drawable =
		 * Drawable.newDrawable
		 * (imageTexture.getTextureRegion("cell_laboratory1"));
		 * if(((AppMain)Core.APP.getActivity()).isReadyClient) {
		 * ((AppMain)Core.APP
		 * .getActivity()).sendFromClient(3+";"+row+";"+col+";"); } break; }
		 */

		int row = mSelector.getSelectedCell().mRow;
		int col = mSelector.getSelectedCell().mColumn;
		build(row, col, building, drawable);

		// GameScene screen = (GameScene)
		// Director.getInstance().getCurrentScene();
		// NewsManager manager = screen.mNewsManager;

		String name = description.name;

		NewsManager.getInstance().Insert(
				new NewsItem(new GregorianCalendar(Time.getInstance().getCalendar()
						.get(Calendar.YEAR), Time.getInstance().getCalendar().get(Calendar.MONTH),
						Time.getInstance().getCalendar().get(Calendar.DAY_OF_MONTH)), "Player1이 "
						+ name + "을(를) 건설(서울)", new Vector2(
						mSelector.getSelectedCell().m_cellpoint.x,
						mSelector.getSelectedCell().m_cellpoint.y + Cell.CELL_HEIGHT / 2
								+ (mSelector.getSelectorSize() - 1) * 2)));

		BuildingBar.sSelectedBuildingDescription = null;
		UIManager.getInstance().switchBarType();

		return building;
	}

	private Building build(int row, int col, Building building, Drawable drawable) {

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;

		int count = 1;
		int size = building.getDescription().size;
		for(int i = row; i < row + size; i++) {
			for(int j = col; j < col + size; j++) {
				Cell cell = cells[i][j];

				cell.m_maxlink = size * size;
				cell.m_first_link_point.x = row;
				cell.m_first_link_point.y = col;
				cell.m_currentlink = count++;
				// cell.mBuildingType = building.mBuildingType;
				cell.mBuilding = building;
				if(cell.m_currentlink == cell.m_maxlink) {
					cell.mBuildingPoint.x = cell.m_cellpoint.x - Cell.CELL_WIDTH / 2 * size;
					cell.mBuildingPoint.y = cell.m_cellpoint.y - (Cell.CELL_HEIGHT - 1) * 3
							- (Cell.CELL_HEIGHT - 1) / 2;
					cell.setDrawable(drawable);
				}

				building.mAverageLandvalue += cell.mLandValue.GetValue();
			}
		}
		building.mAverageLandvalue /= size * size;
		building.mFirstCellPos.set(row, col);
		addCellGroup(row, col, size);
		return building;
	}

	public void BuildFromSocket(String strReceiveText) {

		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;

		Core.APP.error(Utils.TAG, "1");

		Core.APP.error(Utils.TAG, strReceiveText);

		String a = strReceiveText.substring(3, 4);

		Core.APP.error(Utils.TAG, a);

		int type = Integer.parseInt(a);

		// Core.APP.error(Utility.TAG, strReceiveText.substring(3, 3));

		String b = strReceiveText.substring(5, 6);

		int x = Integer.parseInt(b);

		// Core.APP.error(Utility.TAG, strReceiveText.substring(5, 5));

		String c = strReceiveText.substring(7, 8);

		int y = Integer.parseInt(c);

		// Core.APP.error(Utility.TAG, strReceiveText.substring(7, 7));

		Core.APP.error(Utils.TAG, "11");

		int size = 2;

		if(type == 2) size = 3;

		int count = 1;

		if(type == 0) {
			for(int i = x; i < x + size; i++) {
				for(int j = y; j < y + size; j++) {
					Core.APP.error(Utils.TAG, "2");
					cells[i][j].m_maxlink = size * size;
					cells[i][j].m_first_link_point.x = x;
					cells[i][j].m_first_link_point.y = y;
					cells[i][j].m_currentlink = count++;
					// cells[i][j].mBuildingType = BuildingType.RETAIL;
					// cells[i][j].m_construction =
					// player.m_retaillist.get(player.m_retaillist.size()-1);

					Core.APP.info("abc", "ConstructionSelect 3");
					// player.m_retaillist.get(player.m_retaillist.size()-1).m_average_landvalue
					// += cells[i][j].m_landvalue.GetValue();
					if(cells[i][j].m_currentlink == cells[i][j].m_maxlink) {
						cells[i][j].mBuildingPoint.x = cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH;
						cells[i][j].mBuildingPoint.y = cells[i][j].m_cellpoint.y
								- (Cell.CELL_HEIGHT - 1) * 3 - (Cell.CELL_HEIGHT - 1) / 2;
					}
				}
			}

			addCellGroup(x, y, size);
		}

		if(type == 1) {
			for(int i = x; i < x + size; i++) {
				for(int j = y; j < y + size; j++) {
					cells[i][j].m_maxlink = size * size;
					cells[i][j].m_first_link_point.x = x;
					cells[i][j].m_first_link_point.y = y;
					cells[i][j].m_currentlink = count++;
					// cells[i][j].mBuildingType = BuildingType.FACTORY;
					// cells[i][j].m_construction =
					// player.m_retaillist.get(player.m_retaillist.size()-1);

					Core.APP.info("abc", "ConstructionSelect 3");
					// player.m_retaillist.get(player.m_retaillist.size()-1).m_average_landvalue
					// += cells[i][j].m_landvalue.GetValue();
					if(cells[i][j].m_currentlink == cells[i][j].m_maxlink) {
						cells[i][j].mBuildingPoint.x = cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH;
						cells[i][j].mBuildingPoint.y = cells[i][j].m_cellpoint.y
								- (Cell.CELL_HEIGHT - 1) * 3 - (Cell.CELL_HEIGHT - 1) / 2;
					}
				}
			}

			addCellGroup(x, y, size);
		}

	}

	public void moveCameraToCenter() {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		int mapSize = cellMap.size;

		Cell firstCell = cells[0][0];
		float distanceToCenter = (mapSize - 1) * Cell.CELL_HEIGHT / 2;
		getFloor().getCamera().setPos(firstCell.m_cellpoint.x,
				firstCell.m_cellpoint.y + distanceToCenter);
	}

	public void moveCameraToCell(int row, int col) {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = cellMap.cells;
		int mapSize = cellMap.size;

		Cell cell = cells[row][col];
		getFloor().getCamera().setPos(cell.m_cellpoint.x, cell.m_cellpoint.y);
	}

	@Override
	public void onGameScreenChanged(GameScreenType screenType) {
		switch(screenType) {
			case BUILDING:
				setTouchable(false);
				addAction(new Run(new Runnable() {

					@Override
					public void run() {
						setVisible(false);
					}
				}).setStartOffset(200));
				break;
			case MAP:
				cancelActions();
				setTouchable(true);
				setVisible(true);
				break;
			case REPORT:
				setTouchable(false);
				addAction(new Run(new Runnable() {

					@Override
					public void run() {
						setVisible(false);
					}
				}).setStartOffset(200));
				break;
		}
	}

	public double getTotalLandValue() {
		return mTotalLandValue;
	}

	public double getTotalPurchase() {
		return mTotalPurchase;
	}

	public int getMapSize() {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		return cellMap.size;
	}

	public float getZoom() {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		return cellMap.zoom;
	}

	public Building getSelectedBuilding() {
		return mSelector.getSelectedBuilding();
	}

	public Cell getSelectedCell() {
		return mSelector.getSelectedCell();
	}

	public boolean hasSelectedBuilding() {
		return mSelector.hasSelectedBuilding();
	}

	public boolean hasSelectedCell() {
		return mSelector.hasSelectedCell();
	}

	public CellSelector getCellSelector() {
		return mSelector;
	}

	public int getCurrentCellMapIndex() {
		return mCurrentCellMapIndex;
	}

	public void applyMagnifier() {
		CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		float zoom = cellMap.zoom;

		zoom /= 2;
		if(zoom < 0.5f)
			zoom = 2f;

		zoom(zoom);
		cellMap.zoom = zoom;
	}

	public void addDrawLast(Actor<?> building) {
		mDrawLastList.add(building);
	}

	public void removeDrawLast(Actor<?> building) {
		mDrawLastList.remove(building);
	}
	
	public float getDistanceBetweenBuildings(Building building1, Building building2) {
		
		Vector2 pos1 = new Vector2(building1.mFirstCellPos);
		Vector2 pos2 = new Vector2(building2.mFirstCellPos);
		
		float offset;
		
		offset = building1.getDescription().size / 2f;
		pos1.add(offset, offset);
		
		offset = building2.getDescription().size / 2f;
		pos2.add(offset, offset);
		
		return pos1.dst(pos2);
	}

	@Override
	public void onCityChanged(int index) {
		mCurrentCellMapIndex = index;

		// 아직 검증은 안했지만 다음과 같이 해주어야 할 것 같다.
		mDrawLastList.clear();
		mSelector.reset();
	}

	@Override
	public void dispose() {
		sInstance = null;
	}

	/**
	 * 2X2 이상의 셀을 점유하는 건물을 올바르게 그리기 위해 필요한 정보를 보관하는 클래스. static인 이유는 외부 클래스의
	 * 인스턴스 없이 내부 클래스의 인스턴스를 만들 수 있기 때문
	 * 
	 * @author 김현우
	 */
	public static class CellGroup {

		/** 건물의 마지막 셀 위치 */
		/*package*/ Point m_point;

		/** 최하단 셀로부터의 거리와 건물의 크기를 더한 값. 이 값이 큰 것부터 먼저 그려야 한다. */
		/*package*/ int m_value;

		public CellGroup(Point point, int value) {
			m_point = point;
			m_value = value;
		}
	}
	
	private static class CellMap {
		public boolean initialized;
		public float zoom;
		public int size;
		public Cell[][] cells;
		public boolean[][] drawingMap;
		public List<CellGroup> cellGroupList = new ArrayList<CellGroup>();
		public VehicleManager vehicleManager = new VehicleManager();
		// 각각의 CellMap은 고유의 카메라를 갖는다.
		public OrthoCamera camera = new OrthoCamera();
	}
}
