package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.ExtraGroup;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.SizeTo;
import core.scene.stage.actor.drawable.TileDrawable;
import core.scene.stage.actor.widget.Image;

@SuppressWarnings("rawtypes")
public class DrawableTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);
		
		
		TileDrawable tile = new TileDrawable(mApartmentRegion);
		tile.setWidth(50f);
		tile.setHeight(50f);
		

		mTestImage = new Image(tile)
				.setFlipX(true)
				.setFlipY(true)
				.sizeTo(470f, 370f)
				.moveTo(0, 0)
				.addAction(new SizeTo(25f, 30f, 2000).setRepeatCount(Action.INFINITE).setRepeatMode(Action.REVERSE));
		
		ExtraGroup group = new ExtraGroup();
		group.rotateBy(20f);
		group.addChild(mTestImage);
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(group)
			.debugAll();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}
}
