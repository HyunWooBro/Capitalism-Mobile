package test.scene;

import java.util.ListIterator;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;
import core.utils.SnapshotArrayList;

@SuppressWarnings("rawtypes")
public class SnapshotArrayListTestScene extends Scene {
	
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
				.moveTo(0, 100)
				.addEventListener(new TouchListener() {
					@Override
					public boolean onDown(TouchEvent event, float x, float y,
							Actor<?> listener) {
						listener.clearListeners();
						return true;
					}
				})
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
					}
				});
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage);
		
		SnapshotArrayList<String> list = new SnapshotArrayList<String>();
		list.add("abc");
		list.add("bbc");

		ListIterator<String> it = list.begin();
		while(it.hasNext()) {
			Core.APP.debug(it.next());
			list.add("ddd");
		}
		list.end(it);
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
