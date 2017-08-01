package project.game.ui;

import org.framework.R;

import project.framework.Utils;
import project.framework.Utils.YesOrNoDialogData;
import project.game.GameScene;
import project.game.GameScene.GameScreenType;
import project.game.Time;
import project.game.cell.CellManager;
import project.game.report.CorporationReport;
import project.game.report.EmployeeReport;
import project.game.report.FinancialReport;
import project.game.report.ReportManager;
import project.menu.MenuMainScene;

import core.framework.Core;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.bar.SlidingBar;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.transition.TransitionDelay;

public class ToolBar extends Table<ToolBar> {

	private PushButton mVictoryButton;
	
	private DialogWindow mDialog;

	private DLabel mSpeedLabel;

	public ToolBar() {

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas_toolbar);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		TextureRegion constructionRegion = imageTexture.getTextureRegion("ui_toolbar_construction");
		TextureRegion magnifierRegion = imageTexture.getTextureRegion("ui_toolbar_magnifier");
		TextureRegion optionRegion = imageTexture.getTextureRegion("ui_toolbar_option");
		TextureRegion corporationReportRegion = imageTexture.getTextureRegion("ui_toolbar_c");
		TextureRegion employeeReportRegion = imageTexture.getTextureRegion("ui_toolbar_e");
		TextureRegion productAnalysisReportRegion = imageTexture.getTextureRegion("ui_toolbar_p");
		TextureRegion financialReportRegion = imageTexture.getTextureRegion("ui_toolbar_f");
		TextureRegion speedGaugeRegion = imageTexture.getTextureRegion("ui_toolbar_gauge");

		PushButton constructionButton = cd.cast(PushButton.class, "up", constructionRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						UIManager.getInstance().switchBarType();
					}
				});

		PushButton magnifierButton = cd.cast(PushButton.class, "up", magnifierRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						CellManager.getInstance().applyMagnifier();
					}
				});

		PushButton corporationReportButton = cd
				.cast(PushButton.class, "up", corporationReportRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						GameScene.chnageGameScreenType(GameScreenType.REPORT);
						ReportManager.getInstance().selectButton(CorporationReport.class);
					}
				});

		PushButton employeeReportButton = cd.cast(PushButton.class, "up", employeeReportRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						GameScene.chnageGameScreenType(GameScreenType.REPORT);
						ReportManager.getInstance().selectButton(EmployeeReport.class);
					}
				});

		/*
		 * PushButton productAnalysisReportButton = cd.cast(PushButton.class,
		 * "up", productAnalysisReportRegion)
		 * .setStartTouchAction(Utils.createButtonStartTouchAction())
		 * .setFinalTouchAction(Utils.createButtonFinalTouchAction())
		 * .addEventListener(new ChangeListener() {
		 * 
		 * @Override public void onChanged(ChangeEvent event, Actor<?> target,
		 * Actor<?> listener) {
		 * GameScene.chnageGameScreenType(GameScreenType.REPORT);
		 * ReportManager.getInstance().selectButton(ProductReport.class); } });
		 */

		PushButton financialReportButton = cd.cast(PushButton.class, "up", financialReportRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						GameScene.chnageGameScreenType(GameScreenType.REPORT);
						ReportManager.getInstance().selectButton(FinancialReport.class);
					}
				});

		PushButton optionButton = cd.cast(PushButton.class, "up", optionRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						YesOrNoDialogData data = new YesOrNoDialogData();
						data.title = "선택";
						data.content = "메뉴로 돌아가겠습니까?";
						data.yesListener = new ChangeListener() {

							@Override
							public void onChanged(ChangeEvent event, Actor<?> target,
									Actor<?> listener) {
								Director.getInstance().changeScene(
										new TransitionDelay(250, new MenuMainScene()));
							}
						};
						Utils.showYesOrNoDialog(getFloor().getStage(), "returnToMenu", data);
					}
				});

		PushButton speedGaugeButton = cd.cast(PushButton.class, "up", speedGaugeRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						bringUpSpeedSelectDialog();
					}
				});

		/*
		 * 
		 * // 서버에 연결하기 if(new Rect(5, 46, 61, 98).contains(x, y)) {
		 * ((AppMain)Core.APP.getActivity()).Connect(); }
		 * 
		 * // 서버 생성하기 if(new Rect(5, 215, 61, 235).contains(x, y)) {
		 * ((AppMain)Core.APP.getActivity()).createSocket();
		 * ((AppMain)Core.APP.getActivity()).getLocalIpAddr(); }
		 */

		NinePatch backgroundPatch = new NinePatch(imageTexture.getTextureRegion("ui_toolbar"), 11,
				11, 13, 15);
		setDrawable(Drawable.newDrawable(backgroundPatch));

		// setDrawable(Drawable.newDrawable(imageTexture.getTextureRegion("ui_toolbar")));
		// pack();

		left().top().padLeft(5f).padTop(8f);

		addCell(constructionButton);
		row();
		addCell(magnifierButton);
		row();
		addCell(corporationReportButton);
		row();
		addCell(employeeReportButton);
		row();
		// addCell(productAnalysisReportButton);
		// row();
		addCell(financialReportButton);
		row();
		addCell(optionButton);
		row();
		addCell(speedGaugeButton);

		pack();

		// debugAll();

		// validate();

		mSpeedLabel = new DLabel("", fontTexture);// .setDisposable(false);
	}

	private void bringUpSpeedSelectDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible())
				return;
		}

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		int speed = Time.getInstance().getSpeed();

		switch(speed) {
			case 4:
				mSpeedLabel.setText("가장 빠름");
				break;
			case 3:
				mSpeedLabel.setText("빠름");
				break;
			case 2:
				mSpeedLabel.setText("보통");
				break;
			case 1:
				mSpeedLabel.setText("느림");
				break;
			case 0:
				mSpeedLabel.setText("정지");
				break;
		}

		final SlidingBar bar = cd.cast(SlidingBar.class, "default").setRange(0f, 4f)
				.setValue(speed).addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						int speed = (int) ((SlidingBar) target).getValue();

						switch(speed) {
							case 4:
								mSpeedLabel.setText("가장 빠름");
								break;
							case 3:
								mSpeedLabel.setText("빠름");
								break;
							case 2:
								mSpeedLabel.setText("보통");
								break;
							case 1:
								mSpeedLabel.setText("느림");
								break;
							case 0:
								mSpeedLabel.setText("정지");
								break;
						}

						mDialog.getContentTable().invalidateHierarchy();

					}
				});

		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "확인").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {

						int speed = (int) bar.getValue();

						Time.getInstance().setSpeed(speed);
						
						closeDialog();
					}
				});

		PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "취소").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog();
					}
				});

		mDialog = cd.cast(DialogWindow.class, "default")
				.setTitle(new DLabel("게임속도를 조절하세요.", fontTexture)).setModal(true).addContent(bar)
				.addContent(mSpeedLabel).addButton(button1).addButton(button2);

		mDialog.getContentTable().getCellByActor(bar).prefSize(bar.getWidth(), bar.getHeight());
		mDialog.pack().moveCenterTo(320f, 200f);

		// Scene screen = Director.getInstance().getCurrentScene();
		// UIManager ui = ((GameScene) screen).mUI;
		UIManager.getInstance().addChild(mDialog);
		mDialog.open();
	}

	private void closeDialog() {
		if(mDialog == null)
			return;
		mDialog.close();
		mDialog.addAction(new Run(new Runnable() {

			@Override
			public void run() {
				// Scene screen = Director.getInstance().getCurrentScene();
				// UIManager ui = ((GameScene) screen).mUI;
				UIManager.getInstance().removeChild(mDialog);
				mDialog.disposeAll();
				mDialog = null;
			}
		}).setStartOffset(mDialog.getAnimationDuration()));
	}

	@Override
	public Actor<?> contactSelf(float x, float y) {
		return null;
	}

}
