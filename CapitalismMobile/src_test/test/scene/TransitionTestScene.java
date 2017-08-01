package test.scene;

import org.framework.*;

import project.framework.*;
import android.view.*;

import core.framework.*;
import core.framework.graphics.texture.*;
import core.scene.*;
import core.scene.stage.actor.widget.*;
import core.scene.transition.*;

@SuppressWarnings("rawtypes")
public class TransitionTestScene extends Scene{

	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage);
		
		Director.getInstance().changeScene(
				new TransitionWipeOutUp(500, 1500, 
						new WindowTestScene()));
		
		Director.getInstance().overlapSceneOnTop(new ActionTestScene());
		
		Director.getInstance().changeScene(
				new TransitionWipeOutLeft(500, 2500, 
						new TransitionTestScene()));

	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU)
			Director.getInstance().changeScene(new TransitionTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
