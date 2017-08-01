package project.game.ui;

import java.util.GregorianCalendar;

import org.framework.R;

import project.framework.Constant;
import project.framework.Utils;
import project.game.GameScene.GameScreenListener;
import project.game.GameScene.GameScreenType;
import project.game.Time.DayListener;
import project.game.Time.MonthListener;
import project.game.city.CityManager;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.corporation.PlayerCorporation;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.FadeIn;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.SlidingDrawer;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.utils.Disposable;

public class UIManager extends Group<UIManager> implements GameScreenListener, DayListener,
		MonthListener, Disposable {

	public static final float BOTTOM_UI_HEIGHT = 38f;

	public enum BarType {
		TOOL_BAR, 
		BUILDING_BAR, 
	}

	private boolean mBarHidden = false;

	private BarType mBarType = BarType.TOOL_BAR;

	private CityIconButtons mCityiconButtons;

	private ToolBar mToolBar;
	private SlidingDrawer mToolBarDrawer;

	private BuildingBar mBuildingBar;
	private SlidingDrawer mBuildingBarDrawer;

	private CLabel mCashLabel;
	private CLabel mNetprofitLabel;

	private CLabel mMonthLabel;
	private CLabel mDayLabel;
	private CLabel mYearLabel;

	private CityInfo mCityInfo;

	private CellSelectionUI mCellRelatedUI;

	/** 싱글턴 인스턴스 */
	private volatile static UIManager sInstance;

	private UIManager() {
	}

	public static UIManager getInstance() {
		if(sInstance == null) {
			synchronized(UIManager.class) {
				if(sInstance == null)
					sInstance = new UIManager();
			}
		}
		return sInstance;
	}

	public void init() {

		mBarType = BarType.TOOL_BAR;

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture tempTexture = tm.getTexture(R.drawable.temp);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		Image bottomUIImage = new Image(imageTexture.getTextureRegion("ui_bottom"))
				.moveTo(0f, 362f);

		addChild(bottomUIImage);

		mToolBar = new ToolBar() {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				// validate();
				if(mBarType == BarType.TOOL_BAR && !mBarHidden)
					super.draw(batch, parentAlpha);
			}

			@Override
			public Actor<?> contact(float x, float y) {
				if(mBarType != BarType.TOOL_BAR || mBarHidden)
					return null;
				return super.contact(x, y);
			}
		}.moveTo(-77f, 0f).addAction(new MoveBy(77f, 0f, 300).setStartOffset(800));
		/*
		 * mToolBarDrawer = new SlidingDrawer(mToolBar, new
		 * Image(tempTexture.getTextureRegion("scrollbar"))) {
		 * 
		 * @Override public void draw(Batch batch, float parentAlpha) {
		 * //validate(); if(mBarType == BarType.TOOL_BAR && !mBarHidden)
		 * super.draw(batch, parentAlpha); }
		 * 
		 * public Actor<?> contact(float x, float y) { if(mBarType !=
		 * BarType.TOOL_BAR || mBarHidden) return null; return super.contact(x,
		 * y); } } .setRelativeHandlePos(0.1f) .addAction(new Run(new Runnable()
		 * {
		 * 
		 * @Override public void run() { mToolBarDrawer.open(); }
		 * }).setStartOffset(800));
		 */

		addChild(mToolBar);

		mBuildingBar = new BuildingBar() {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				// 처음에는
				// validate();
				if(mBarType == BarType.BUILDING_BAR && !mBarHidden)
					super.draw(batch, parentAlpha);
			}

			@Override
			public Actor<?> contact(float x, float y) {
				if(mBarType != BarType.BUILDING_BAR || mBarHidden)
					return null;
				return super.contact(x, y);
			}
		};
		/*
		 * mBuildingBarDrawer = new SlidingDrawer(mBuildingBar, new
		 * Image(tempTexture.getTextureRegion("scrollbar"))) {
		 * 
		 * @Override public void draw(Batch batch, float parentAlpha) { // 처음에는
		 * validate(); if(mBarType == BarType.BUILDING_BAR && !mBarHidden)
		 * super.draw(batch, parentAlpha); }
		 * 
		 * public Actor<?> contact(float x, float y) { if(mBarType !=
		 * BarType.BUILDING_BAR || mBarHidden) return null; return
		 * super.contact(x, y); } } .setRelativeHandlePos(0.1f);
		 */

		addChild(mBuildingBar);

		mCityInfo = new CityInfo();

		addChild(mCityInfo);

		mCellRelatedUI = new CellSelectionUI();

		addChild(mCellRelatedUI);

		// '현금' 레이블
		SLabel cashLabel = new SLabel(R.string.label_cash, fontTexture).moveTo(170f, 390f);
		// ':' 레이블
		SLabel colonLabel1 = new SLabel(R.array.label_array_outline_white_15,
				Constant.LABEL_INDEX_COLON, fontTexture).setConcatLabel(cashLabel);

		mCashLabel = new CLabel("$0", R.array.label_array_outline_white_15, fontTexture)
				.setConcatLabel(colonLabel1);

		addChild(cashLabel);
		addChild(colonLabel1);
		addChild(mCashLabel);

		// '순이익' 레이블
		SLabel netprofitLabel = new SLabel(R.string.label_netprofit, fontTexture)
				.moveTo(325f, 390f);
		// ':' 레이블
		SLabel colonLabel2 = new SLabel(R.array.label_array_outline_white_15,
				Constant.LABEL_INDEX_COLON, fontTexture).setConcatLabel(netprofitLabel);

		mNetprofitLabel = new CLabel("$0", R.array.label_array_outline_white_15, fontTexture)
				.setConcatLabel(colonLabel2);

		addChild(netprofitLabel);
		addChild(colonLabel2);
		addChild(mNetprofitLabel);

		// 날짜 표시
		mMonthLabel = new CLabel("", R.array.label_array_black_15, fontTexture).moveTo(555f, 390f);
		// '/' 레이블
		SLabel slashLabel = new SLabel(R.array.label_array_black_15, Constant.LABEL_INDEX_SLASH,
				fontTexture).setConcatLabel(mMonthLabel);

		mDayLabel = new CLabel("", R.array.label_array_black_15, fontTexture)
				.setConcatLabel(slashLabel);

		mYearLabel = new CLabel("", R.array.label_array_red_15, fontTexture).moveTo(600f, 390f);

		addChild(mMonthLabel);
		addChild(slashLabel);
		addChild(mDayLabel);
		addChild(mYearLabel);

		mCityiconButtons = new CityIconButtons();

		// mCityiconButtons.debugAll();

		addChild(mCityiconButtons);
	}
	
	public void updateCash() {
		FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
				.getFinancialData();
		updateCash(financialData.cash);
	}
	
	public UIManager updateCash(double cash) {
		mCashLabel.setText(Utils.toCash(cash));
		if(cash >= 0) {
			mCashLabel.setColor(Color4.GREEN);
		} else
			mCashLabel.setColor(Color4.RED);
		return this;
	}
	
	public UIManager updateNetprofit(double netprofit) {
		mNetprofitLabel.setText(Utils.toCash(netprofit));
		if(netprofit >= 0) {
			mNetprofitLabel.setColor(Color4.GREEN);
		} else
			mNetprofitLabel.setColor(Color4.RED);
		return this;
	}
	
	public UIManager updateDate(int year, int month, int day) {
		// if(oldMonth != month)
		mYearLabel.setText(Integer.toString(year));
		mMonthLabel.setText(Integer.toString(month + 1));
		mDayLabel.setText(Integer.toString(day));
		return this;
	}
	
	public void AddCityIcon(int resource_id) {

	}

	public void switchBarType() {
		switch(mBarType) {
			case BUILDING_BAR:
				// .openNow();
				mBarType = BarType.TOOL_BAR;
				mBuildingBar.onHide();
				break;
			case TOOL_BAR:
				// mBuildingBarDrawer.openNow();
				mBarType = BarType.BUILDING_BAR;
				mBuildingBar.onShow();
				break;
		}
	}

	public void switchBarVisiability() {
		mBarHidden = !mBarHidden;
	}

	@Override
	public void onGameScreenChanged(GameScreenType screenType) {
		switch(screenType) {
			case BUILDING:
				mCityiconButtons.cancelActions();
				mCityiconButtons.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mBuildingBar.cancelActions();
				mBuildingBar.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mToolBar.cancelActions();
				mToolBar.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mCityInfo.cancelActions();
				mCityInfo.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mCellRelatedUI.cancelActions();
				mCellRelatedUI.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));
				break;
			case MAP:
				mCityiconButtons.cancelActions();
				mCityiconButtons.setVisible(true).addAction(new FadeIn(200));

				mBuildingBar.cancelActions();
				mBuildingBar.setVisible(true).addAction(new FadeIn(200));

				mToolBar.cancelActions();
				mToolBar.setVisible(true).addAction(new FadeIn(200));

				mCityInfo.cancelActions();
				mCityInfo.setVisible(true).addAction(new FadeIn(200));

				mCellRelatedUI.cancelActions();
				mCellRelatedUI.setVisible(true).addAction(new FadeIn(200));
				break;
			case REPORT:
				mCityiconButtons.cancelActions();
				mCityiconButtons.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mBuildingBar.cancelActions();
				mBuildingBar.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mToolBar.cancelActions();
				mToolBar.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mCityInfo.cancelActions();
				mCityInfo.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));

				mCellRelatedUI.cancelActions();
				mCellRelatedUI.addAction(new FadeOut(200).setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						action.getActor().setVisible(false);
					}
				}));
				break;
		}
	}

	public BuildingBar getBuildingBar() {
		return mBuildingBar;
	}

	public boolean isBarHidden() {
		return mBarHidden;
	}

	public BarType getBarType() {
		return mBarType;
	}

	@Override
	public void dispose() {
		sInstance = null;
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
		FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
				.getFinancialData();
		updateCash(financialData.cash);
		updateNetprofit(financialData.annualNetprofit);
		updateDate(year, month, day);
		mCityInfo.updateDailyInfo();
	}

	@Override
	public void onMonthChanged(GregorianCalendar calendar, int year, int month, int day) {
		mCityInfo.updateMonthlyInfo();
	}

}
