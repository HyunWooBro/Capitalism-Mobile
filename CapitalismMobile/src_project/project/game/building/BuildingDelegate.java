package project.game.building;

import org.framework.R;

import project.framework.Constant;
import project.framework.Utils;
import project.game.GameScene;
import project.game.GameScene.GameScreenListener;
import project.game.GameScene.GameScreenType;
import project.game.building.department.DepartmentManager;
import project.game.cell.CellManager;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.utils.Disposable;

public class BuildingDelegate extends Group<BuildingDelegate> implements GameScreenListener,
		Disposable {

	public enum BuildingTabType {
		DISPLAY, 
		DEPARTMENT, 
	}

	private BuildingTabType mBuildingTabType = BuildingTabType.DEPARTMENT;

	private DepartmentManager mCurrentManager;

	private Rectangle mDisplayTabRectangle;
	private Rectangle mDepartmentTabRectangle;

	private static final int DISPLAY_TAB_LEFT = 0;
	private static final int DISPLAY_TAB_TOP = 323;
	private static final int DISPLAY_TAB_RIGHT = 75 - DISPLAY_TAB_LEFT;
	private static final int DISPLAY_TAB_BOTTOM = 345 - DISPLAY_TAB_TOP;

	private static final int DEPARTMENT_TAB_LEFT = 77;
	private static final int DEPARTMENT_TAB_TOP = 323;
	private static final int DEPARTMENT_TAB_RIGHT = 152 - DEPARTMENT_TAB_LEFT;
	private static final int DEPARTMENT_TAB_BOTTOM = 345 - DEPARTMENT_TAB_TOP;

	private Image mDisplayTabImage;
	private Image mDepartmentTabmage;

	private PushButton mReturnToMapButton;

	/** 싱글턴 인스턴스 */
	private volatile static BuildingDelegate sInstance;

	private BuildingDelegate() {
	}

	public static BuildingDelegate getInstance() {
		if(sInstance == null) {
			synchronized(BuildingDelegate.class) {
				if(sInstance == null)
					sInstance = new BuildingDelegate();
			}
		}
		return sInstance;
	}

	public void init() {
		setVisible(false);

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		TextureRegion displayTab = imageTexture
				.getTextureRegion("building_screen_with_display_tab");
		TextureRegion departmentTab = imageTexture
				.getTextureRegion("building_screen_with_department_tab");

		mDisplayTabImage = new Image(displayTab) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				super.draw(batch, parentAlpha);
				batch.flush();
			}
		};
		mDepartmentTabmage = new Image(departmentTab) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				super.draw(batch, parentAlpha);
				batch.flush();
			}
		};

		mReturnToMapButton = cd.cast(PushButton.class, "static_text", R.string.label_return)
				.moveTo(500f, 322f).pack()
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						changeGameScreenToMap();
					}
				});

		addChild(mDisplayTabImage);
		addChild(mDepartmentTabmage);
		addChild(mReturnToMapButton);

		mDisplayTabRectangle = new Rectangle(DISPLAY_TAB_LEFT, DISPLAY_TAB_TOP, DISPLAY_TAB_RIGHT,
				DISPLAY_TAB_BOTTOM);
		mDepartmentTabRectangle = new Rectangle(DEPARTMENT_TAB_LEFT, DEPARTMENT_TAB_TOP,
				DEPARTMENT_TAB_RIGHT, DEPARTMENT_TAB_BOTTOM);

		// 터치이벤트를 받기 위해 카메라의 크기와 같도록 사이즈를 조정
		sizeTo(Core.GRAPHICS.getVirtualWidth(), Core.GRAPHICS.getVirtualHeight());

		addEventCaptureListener(new GestureTouchListener() {

			@Override
			public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {

				if(mDisplayTabRectangle.contains(x, y)) {
					switchTabType();
					DepartmentManager manager = (DepartmentManager) getChildList().get(2);
					manager.setTapType(mBuildingTabType);
				}

				if(mDepartmentTabRectangle.contains(x, y)) {
					switchTabType();
					DepartmentManager manager = (DepartmentManager) getChildList().get(2);
					manager.setTapType(mBuildingTabType);
				}
			}

			@Override
			public void onScroll(TouchEvent event, float distanceX, float distanceY, float x,
					float y, Actor<?> listener) {
				if(event.getTargetActor() != mCurrentManager)
					return;

				float base = Constant.BASE_GESTURE_DISTANCE;

				if(-distanceY > base && Math.abs(distanceX) < base / 2) {
					changeGameScreenToMap();
					return;
				}

				if(mBuildingTabType == BuildingTabType.DISPLAY && distanceX > base) {
					switchTabType();
					DepartmentManager manager = (DepartmentManager) getChildList().get(2);
					manager.setTapType(mBuildingTabType);
				}

				if(mBuildingTabType == BuildingTabType.DEPARTMENT && -distanceX > base) {
					switchTabType();
					DepartmentManager manager = (DepartmentManager) getChildList().get(2);
					manager.setTapType(mBuildingTabType);
				}
			}

		});

	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		pushTransformation(batch);
		drawChildren(batch, parentAlpha);
		batch.flush();
		popTransformation(batch);
	}

	private void changeGameScreenToMap() {
		GameScene.chnageGameScreenType(GameScreenType.MAP);
		DepartmentManager.reset();
	}

	private void switchTabType() {
		switch(mBuildingTabType) {
			case DEPARTMENT:
				if(!mCurrentManager.getBuilding().canViewDisplayTab()) break;
				mBuildingTabType = BuildingTabType.DISPLAY;
				mDepartmentTabmage.setVisible(false);
				mDisplayTabImage.setVisible(true);
				break;
			case DISPLAY:
				if(!mCurrentManager.getBuilding().canViewDepartmentTab()) break;
				mBuildingTabType = BuildingTabType.DEPARTMENT;
				mDepartmentTabmage.setVisible(true);
				mDisplayTabImage.setVisible(false);
				break;
		}
	}

	public DepartmentManager getCurrentManager() {
		return mCurrentManager;
	}
	
	private void validateTab() {
		switch(mBuildingTabType) {
			case DEPARTMENT:
				if(!mCurrentManager.getBuilding().canViewDepartmentTab()) {
					mBuildingTabType = BuildingTabType.DISPLAY;
					mDepartmentTabmage.setVisible(false);
					mDisplayTabImage.setVisible(true);
				}
				break;
			case DISPLAY:
				if(!mCurrentManager.getBuilding().canViewDisplayTab()) {
					mBuildingTabType = BuildingTabType.DEPARTMENT;
					mDepartmentTabmage.setVisible(true);
					mDisplayTabImage.setVisible(false);
				}
				break;
		}
	}

	@Override
	public void onGameScreenChanged(GameScreenType screenType) {
		switch(screenType) {
			case BUILDING:
				setVisible(true);
				setTouchable(true);
				moveTo(0f, 400f);
				cancelActions();
				addAction(new MoveBy(0f, -400f, 200));
				removeChild(mCurrentManager);
				// Scene<?> screen = Director.getInstance().getCurrentScene();
				CellManager mCellManager = CellManager.getInstance();
				mCurrentManager = mCellManager.getSelectedBuilding().getDepartmentManager();
				// 요약탭과 부서탭 다음에 Manager을 위치시킨다.
				// 요약탭 - 부서탭 - 매니저 - 버튼
				addChild(2, mCurrentManager);
				validateTab();
				mCurrentManager.setTapType(mBuildingTabType);
				break;
			case MAP:
				setTouchable(false);
				moveTo(0f, 0f);
				cancelActions();
				addAction(new MoveBy(0f, 400f, 200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						setVisible(false);
					}
				}));
				break;
			case REPORT:
				setTouchable(false);
				moveTo(0f, 0f);
				cancelActions();
				addAction(new MoveBy(0f, 400f, 200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						setVisible(false);
					}
				}));
				break;
		}
	}

	public BuildingTabType getBuildingTabType() {
		return mBuildingTabType;
	}

	@Override
	public void dispose() {
		sInstance = null;
	}
}
