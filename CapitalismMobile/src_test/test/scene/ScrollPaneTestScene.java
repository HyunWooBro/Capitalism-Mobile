package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.ScrollPane;

@SuppressWarnings("rawtypes")
public class ScrollPaneTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		CastingDirector cd = CastingDirector.getInstance();
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion).setPrefSize(100f, 100f);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);
		
		
		ScrollPane scroller = cd.cast(ScrollPane.class, "default", mBackGroundImage)
				.moveTo(200, 100).sizeTo(240, 200);
		
		
		getStage().addFloor()
			.addChild(scroller);
		
		getStage().debugAll();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}
}
