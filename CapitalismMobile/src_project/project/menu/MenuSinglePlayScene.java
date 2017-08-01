package project.menu;

import java.util.Arrays;

import org.framework.R;

import project.framework.Utils;
import project.game.GameAct;
import project.game.GameScene;

import android.view.KeyEvent;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import core.framework.Core;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.PlayMode;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.transition.TransitionDelay;
import core.scene.transition.TransitionDissolve;

public class MenuSinglePlayScene extends Scene<MenuAct> {

	private PushButton sItemButton1;
	private PushButton sItemButton2;
	private PushButton sItemButton3;

	private Image mBackGroundImage;

	private PushButton mReturnButton;

	public MenuSinglePlayScene() {
	}

	@Override
	protected void create() {

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		TextureRegion backgroundRegion = imageTexture.getTextureRegion("singleplaymenu_background");
		TextureRegion returnRegion = imageTexture.getTextureRegion("menu_item_return_n");

		Texture imageTexture2 = tm.getTexture(R.drawable.temp);

		Animation animation = new Animation(30, Arrays.asList(null,
				imageTexture2.getTextureRegion("menu_item_flash_n_01"),
				imageTexture2.getTextureRegion("menu_item_flash_n_02"),
				imageTexture2.getTextureRegion("menu_item_flash_n_03"),
				imageTexture2.getTextureRegion("menu_item_flash_n_04"),
				imageTexture2.getTextureRegion("menu_item_flash_n_05"),
				imageTexture2.getTextureRegion("menu_item_flash_n_06"),
				imageTexture2.getTextureRegion("menu_item_flash_n_07"),
				imageTexture2.getTextureRegion("menu_item_flash_n_08"), null))
				.setPlayMode(PlayMode.REPEAT).setFrameDuration(0, 2000).setFrameDuration(9, 3000)
				.setPatternIndex(1);

		mBackGroundImage = new Image(backgroundRegion);

		mReturnButton = cd
				.cast(PushButton.class, "up", returnRegion)
				.moveTo(660f, 335f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addChild(new Image(animation).setFillParent(true))
				.addAction(
						Utils.createMoveInAction(-230f, 0f, 300, 300, new OvershootInterpolator(),
								new LinearInterpolator())).addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						fireChangeScene();
					}
				});

		getStage().addFloor().addChild(mBackGroundImage).addChild(mReturnButton);

	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
			fireChangeScene();

		if(keyCode == KeyEvent.KEYCODE_MENU) {
			Director.getInstance().setCurrentAct(new GameAct());
			Director.getInstance().changeScene(new TransitionDissolve(250, 500, new GameScene()));
		}
	}

	private void fireChangeScene() {
		Director.getInstance().changeScene(new TransitionDelay(250, new MenuMainScene()));
	}

}
