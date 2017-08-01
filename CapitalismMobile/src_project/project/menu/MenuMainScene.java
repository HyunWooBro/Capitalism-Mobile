package project.menu;

import org.framework.R;

import project.framework.Utils;
import project.framework.Utils.MessageDialogData;
import project.game.GameScene;

import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.button.ButtonGroup;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.transition.TransitionDelay;

public class MenuMainScene extends Scene<MenuAct> {

	private ButtonGroup mButtonGroup;

	private DLabel mVersionLabel;

	public MenuMainScene() {
	}

	@Override
	protected void create() {

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas_mainmenu);
		Texture fontTexture = tm.getTexture(R.drawable.font_menu);

		Image backGroundImage = new Image(imageTexture.getTextureRegion("mainmenu_background"));

		Image signImage = new Image(imageTexture.getTextureRegion("mainmenu_sign"))
				.moveTo(-10f, 0f)
				.setAlpha(0f)
				.addAction(
						Utils.createMoveInAction(120f, 0f, 50, 450, new DecelerateInterpolator(),
								new AccelerateInterpolator()));

		PushButton singleplayButton = cd
				.cast(PushButton.class, "menu_item", R.string.label_menu_singleplay)
				.moveTo(660f, 40f)
				.setAlpha(0f)
				.addAction(
						Utils.createMoveInAction(-230f, 0f, 350, 300, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton multiplayButton = cd
				.cast(PushButton.class, "menu_item", R.string.label_menu_multiplay)
				.moveTo(640f, 110f)
				.setAlpha(0f)
				.addAction(
						Utils.createMoveInAction(-230f, 0f, 400, 300, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton tutorialButton = cd
				.cast(PushButton.class, "menu_item", R.string.label_menu_tutorial)
				.moveTo(620f, 180f)
				.setAlpha(0f)
				.addAction(
						Utils.createMoveInAction(-230f, 0f, 450, 300, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton optionButton = cd
				.cast(PushButton.class, "menu_item", R.string.label_menu_option)
				.moveTo(600f, 250f)
				.setAlpha(0f)
				.addAction(
						Utils.createMoveInAction(-230f, 0f, 500, 300, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton exitButton = cd
				.cast(PushButton.class, "menu_item", R.string.label_menu_exit)
				.moveTo(580f, 320f)
				.setAlpha(0f)
				.addAction(
						Utils.createMoveInAction(-230f, 0f, 550, 300, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton infoButton = cd
				.cast(PushButton.class, "menu_info")
				.moveTo(-25f, 15f)
				.setAlpha(0f)
				.addAction(
						Utils.createMoveInAction(50f, 0f, 800, 300, new BounceInterpolator(),
								new LinearInterpolator()));

		mButtonGroup = new ButtonGroup();
		mButtonGroup.addButton(singleplayButton);
		mButtonGroup.addButton(multiplayButton);
		mButtonGroup.addButton(tutorialButton);
		mButtonGroup.addButton(optionButton);
		mButtonGroup.addButton(exitButton);
		mButtonGroup.addButton(infoButton);
		mButtonGroup.addEventListener(new ChangeListener() {

			@Override
			public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
				if(!event.isTargetActor())
					return;

				switch(mButtonGroup.getCheckedIndex()) {
				// 싱글플레이
					case 0:
						single();
						break;
					// 멀티플레이
					case 1:
						preparing();
						break;
					// 튜토리얼
					case 2:
						preparing();
						break;
					// 옵션
					case 3:
						fireChangeScene(4);
						break;
					// 나가기
					case 4:
						Utils.exit(getStage());
						break;
					// 크레딧
					case 5:
						credit();
						break;
				}

				// 모든 버튼의 checked 상태를 리셋하여 항상 ChangeEvent를 fire하도록 한다.
				mButtonGroup.uncheckAll();
			}
		});

		Paint p = new Paint();
		p.setTextSize(12);
		p.setAntiAlias(true);
		p.setShadowLayer(2, 0, 0, Color4.BLACK);
		p.setColor(Color4.WHITE);

		String version = "ver.   " + String.format("%.3f", (Core.APP.getVersionCode() / 1000f));
		mVersionLabel = new DLabel(version, fontTexture, p).setColor(Color4.YELLOW).moveTo(25f,
				370f);

		getStage().addFloor(/* 0 */).addChild(backGroundImage).addChild(signImage)
				.addChild(mButtonGroup).addChild(mVersionLabel);
	}

	@Override
	protected void destroy(boolean lifeCycle) {
		mVersionLabel.dispose();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.onBackKey(getStage());
	}

	private void single() {
		MessageDialogData data = new MessageDialogData();
		data.title = "데모버전입니다.";
		data.content = "당신은 후계자 수업의 일환으로 다음 과제를 통해 자신의 경영능력을 증명해야한다.\n\n"
				+ "이 시나리오에서는 경쟁기업은 없지만 지역의 소규모 업체와의 경쟁에서 이겨낼 수 있는가가 관건이다.\n\n"
				+ "당신의 부친은 2년 내에 5백만 달러 이상의 순이익을 낸다면 만족할 것이다.";
		data.titleColor = Color4.LTRED4;
		data.okListener = new ChangeListener() {

			@Override
			public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {

				// 30 프레임을 기다리는 이유는 GameScene을 초기화하는 데 시간이 오래걸리기 때문에
				// MessageDialog가 닫히는 애니메이션이 제대로 출력되지 않기 때문이다. 따라서 Dialog를
				// 닫고 나서 GameScene을 초기화한다.
				Core.APP.runOnGLThread(30, new Runnable() {

					@Override
					public void run() {
						Director.getInstance().changeScene(
								new TransitionDelay(250, new GameScene()));
					}
				});

				// 그동안 ButtonGroup의 터치를 불능으로 만든다.
				mButtonGroup.setTouchable(false);
			}
		};
		// data.icon = R.drawable.ic_launcher;
		Utils.showMessageDialog(getStage(), "credit", data);
	}

	private void credit() {
		MessageDialogData data = new MessageDialogData();
		data.title = Core.APP.getResources().getString(R.string.app_name);
		data.content = "기획 : 김현우\n" + "프로그래밍 : 김현우\n" + "그래픽 : 김현우\n"
				+ "메일 : kis7385@naver.com\n\n" + "원작 : 캐피탈리즘2(PC)";
		data.titleColor = Color4.LTRED4;
		// data.contentColor = Color4.BLACK4;
		// data.icon = R.drawable.ic_launcher;
		Utils.showMessageDialog(getStage(), "credit", data);
	}

	private void preparing() {
		MessageDialogData data = new MessageDialogData();
		data.title = "알림";
		data.content = "준비중입니다.";
		Utils.showMessageDialog(getStage(), "preparing", data);
	}

	private void fireChangeScene(int toWhichScene) {
		switch(toWhichScene) {
			case 1:
				Director.getInstance().changeScene(
						new TransitionDelay(250, new MenuSinglePlayScene()));
				break;
			case 2:
				Director.getInstance().changeScene(
						new TransitionDelay(250, new MenuMultiPlayScene()));
				break;
			case 3:
				break;
			case 4:
				Director.getInstance().changeScene(new TransitionDelay(250, new MenuOptionScene()));
				break;
		}
	}

}
