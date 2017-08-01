package project.intro;

import org.framework.R;

import project.framework.ResLoader;
import project.menu.MenuAct;
import project.menu.MenuMainScene;
import test.scene.ActorTestScene;
import test.scene.DataTableTestScene;
import test.scene.LabelTestScene;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.texture.ImageTexture;
import core.framework.graphics.texture.Texture;
import core.math.MathUtils;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.action.ActionSet;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.action.absolute.FadeIn;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.widget.Image;
import core.scene.transition.TransitionFade;

@SuppressWarnings("rawtypes")
public class IntroScene extends Scene {
	
	private Texture mIntroTexture;
	
	@Override
	protected void create() {

		// 게임 리소스 초기화
		ResLoader.load();

		// 인트로 배경을 얻는다.
		mIntroTexture = getIntroTexture();

		Image splash = new Image(mIntroTexture.getAsTextureRegion()).setAlpha(0f).addAction(
				new ActionSet(true).setStartOffset(400).addAction(new FadeIn(400))
						.addAction(new FadeOut(400).setStartOffset(1600))
						.addAction(new Run(new Runnable() {
							@Override
							public void run() {
								Director.getInstance().setCurrentAct(new MenuAct());
								Director.getInstance().changeScene(
										new TransitionFade(400, 
												new MenuMainScene()));
							}
						})));

		getStage().addFloor().addChild(splash);
	}

	private Texture getIntroTexture() {
		int id = 0;
		// splash 이미지를 랜덤하게 선택
		switch(MathUtils.random(6)) {
			case 0:
				id = R.drawable.intro_background1;
				break;
			case 1:
				id = R.drawable.intro_background2;
				break;
			case 2:
				id = R.drawable.intro_background3;
				break;
			case 3:
				id = R.drawable.intro_background4;
				break;
			case 4:
				id = R.drawable.intro_background5;
				break;
			case 5:
				id = R.drawable.intro_background6;
				break;
			case 6:
				id = R.drawable.intro_background7;
				break;
		}

		return new ImageTexture(id);
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Core.APP.exit();
	}

	@Override
	protected void destroy(boolean lifeCycle) {
		mIntroTexture.dispose();
	}

}
