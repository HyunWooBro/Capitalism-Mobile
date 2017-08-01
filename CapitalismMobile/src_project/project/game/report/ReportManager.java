package project.game.report;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.framework.R;

import project.framework.Constant;
import project.game.GameScene;
import project.game.GameScene.GameScreenListener;
import project.game.GameScene.GameScreenType;
import project.game.Time.DayListener;
import project.game.Time.MonthListener;
import project.game.ui.UIManager;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.table.VerticalTable;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.utils.Disposable;
import core.utils.pool.Pools;

/**
 * menu 너비 150(양쪽으로 패딩 10씩 들어가므로 실제로는 130)
 * 
 * content 너비 490(양쪽으로 패딩 10씩 들어가므로 실제로는 470)
 * 
 * @author 김현우
 */
public class ReportManager extends Table<ReportManager> implements GameScreenListener, DayListener,
		MonthListener, Disposable {

	public static final float MENU_WIDTH = 150f;
	public static final float CONTENT_WIDTH = 490f;

	/*package*/ PushButton mCurrentButton;

	/*package*/ List<Report> mReportList = new ArrayList<Report>();

	private TableCell mContentCell;

	private Map<Class<?>, Integer> mClazzToIndexMap = new HashMap<Class<?>, Integer>();

	/** 화면에 출력될 경우에만 true가 되며, 최적화를 위해 사용된다 */
	private boolean mActive;

	/** 싱글턴 인스턴스 */
	private volatile static ReportManager sInstance;

	private ReportManager() {
	}

	public static ReportManager getInstance() {
		if(sInstance == null) {
			synchronized(ReportManager.class) {
				if(sInstance == null) {
					sInstance = new ReportManager();
				}
			}
		}
		return sInstance;
	}

	public void init() {
		setVisible(false);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		setDrawable(Drawable.newDrawable(imageTexture.getTextureRegion("report_info_basic")));

		mReportList.add(new CorporationReport());
		mReportList.add(new EmployeeReport());
		// mReportList.add(new ProductReport());
		mReportList.add(new FinancialReport());
		// mReportList.add(new PersonReport());

		mReportList.add(new DividerReport());

		mReportList.add(new GoalReport());
		mReportList.add(new GuideReport());
		mReportList.add(new FinancialDealingReport());

		mReportList.add(new DividerReport());

		createClazzToIndexMap();

		createMenu();
		createContent();

		showReport(0);

		north();

		// 터치이벤트를 받기 위해 카메라의 크기와 같도록 사이즈를 조정
		sizeTo(Core.GRAPHICS.getVirtualWidth(), Core.GRAPHICS.getVirtualHeight());

		addEventListener(new GestureTouchListener() {

			@Override
			public void onScroll(TouchEvent event, float distanceX, float distanceY, float x,
					float y, Actor<?> listener) {
				if(event.getTargetActor().getParent() != ReportManager.this) return;

				if(distanceX > Constant.BASE_GESTURE_DISTANCE) {
					GameScene.chnageGameScreenType(GameScreenType.MAP);
				}
			}
		});

		// debugTableCells();
	}

	private void createClazzToIndexMap() {
		int n = mReportList.size();
		for(int i = 0; i < n; i++) {
			Report report = mReportList.get(i);
			mClazzToIndexMap.put(report.getClass(), i);
		}
	}

	private void createMenu() {
		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		ChangeListener listener = new ChangeListener() {

			@Override
			public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
				if(mCurrentButton != null) {
					mCurrentButton.getChildList().get(0).setColor(Color4.WHITE4);
					hideReport(Integer.parseInt(listener.getTag()));
				}
				PushButton button = (PushButton) listener;
				button.getChildList().get(0).setColor(Color4.LTRED4);
				mCurrentButton = button;
				showReport(Integer.parseInt(listener.getTag()));
			}
		};

		VerticalTable table = new VerticalTable().north().padTop(5f);

		table.col(0).padBottom(-4f);

		TextureRegion dividerRegion = imageTexture.getTextureRegion("department_content_bar");

		List<Report> reportList = mReportList;
		int n = reportList.size();
		for(int i = 0; i < n; i++) {
			Report report = reportList.get(i);
			PushButton button = report.getMenuButton();
			if(button != null) {
				button.addEventListener(listener);
				button.setTag("" + i);
				table.addCell(button);
			} else {
				table.addCell(new Image(dividerRegion)).size(130f, 5f).padTop(4f).padBottom(0f);
			}
		}

		PushButton returnButton = cd.cast(PushButton.class, "static_text", R.string.label_return)
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						GameScene.chnageGameScreenType(GameScreenType.MAP);
					}
				});
		returnButton.getChildList().get(0).setColor(Color4.GREEN4);

		table.addCell(returnButton);

		addCell(table).size(MENU_WIDTH,
				Core.GRAPHICS.getVirtualHeight() - UIManager.BOTTOM_UI_HEIGHT);
	}

	private void createContent() {
		// 내용을 교체할 수 있도록 셀을 보관한다.
		mContentCell = addCell().size(CONTENT_WIDTH,
				Core.GRAPHICS.getVirtualHeight() - UIManager.BOTTOM_UI_HEIGHT);
	}

	public void selectButton(int index) {
		Report report = (Report) mReportList.get(index);
		report.getMenuButton().fire(Pools.obtain(ChangeEvent.class));
	}

	public void selectButton(Class<?> clazz) {
		selectButton(mClazzToIndexMap.get(clazz));
	}

	private void showReport(int index) {
		mContentCell.setActor(mReportList.get(index).getContent());
		mReportList.get(index).onShow();
	}

	private void hideReport(int index) {
		mReportList.get(index).onHide();
	}

	protected void onHide() {
		if(mCurrentButton == null) return;
		hideReport(Integer.parseInt(mCurrentButton.getTag()));
	}

	@Override
	public void update(long time) {
		if(!mActive) return;
		super.update(time);
	}

	@Override
	public void onGameScreenChanged(GameScreenType screenType) {
		switch(screenType) {
			case BUILDING:
				setTouchable(false);
				moveTo(0f, 0f);
				cancelActions();
				addAction(new MoveBy(-640f, 0f, 200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						onHide();
						setVisible(false);
						mActive = false;
					}
				}));
				break;
			case MAP:
				setTouchable(false);
				moveTo(0f, 0f);
				cancelActions();
				addAction(new MoveBy(-640f, 0f, 200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						onHide();
						setVisible(false);
						mActive = false;
					}
				}));
				break;
			case REPORT:
				setVisible(true);
				setTouchable(true);
				moveTo(-640f, 0f);
				cancelActions();
				addAction(new MoveBy(640f, 0f, 200));
				mActive = true;
				break;
		}
	}

	@Override
	public void dispose() {
		sInstance = null;
	}

	@Override
	public void disposeAll() {
		List<Report> reportList = mReportList;
		int n = reportList.size();
		for(int i = 0; i < n; i++) {
			Report report = reportList.get(i);
			// Group인 경우에는 자식까지 정리한다.
			if(report instanceof Group) {
				((Group<?>) report).disposeAll();
				// Group이 아니며 Disposable을 구현한다면 child를 정리한다.
			} else if(report instanceof Disposable) {
				((Disposable) report).dispose();
			}
		}

		super.disposeAll();
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
		if(GameScene.mGameScreenType != GameScreenType.REPORT) return;

		List<Report> reportList = mReportList;
		int n = reportList.size();
		for(int i = 0; i < n; i++) {
			Report report = reportList.get(i);
			if(report instanceof DayListener) {
				DayListener listener = (DayListener) report;
				listener.onDayChanged(calendar, year, month, day);
			}
		}
	}

	@Override
	public void onMonthChanged(GregorianCalendar calendar, int year, int month, int day) {
		if(GameScene.mGameScreenType != GameScreenType.REPORT) return;

		List<Report> reportList = mReportList;
		int n = reportList.size();
		for(int i = 0; i < n; i++) {
			Report report = reportList.get(i);
			if(report instanceof MonthListener) {
				MonthListener listener = (MonthListener) report;
				listener.onMonthChanged(calendar, year, month, day);
			}
		}
	}

	private static class DividerReport implements Report {

		@Override
		public void onShow() {
		}

		@Override
		public void onHide() {
		}

		@Override
		public PushButton getMenuButton() {
			return null;
		}

		@Override
		public Actor<?> getContent() {
			return null;
		}
	}
}
