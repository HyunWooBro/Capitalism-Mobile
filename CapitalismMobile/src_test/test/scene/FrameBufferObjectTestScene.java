package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;
import android.view.MotionEvent;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.framework.graphics.utils.FrameBufferObject;
import core.scene.Scene;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.widget.Image;

@SuppressWarnings("rawtypes")
public class FrameBufferObjectTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	private FrameBufferObject mFrame;
	
	private boolean mStarted;
	
	private int mCount;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100)
				.addAction(new MoveBy(300f, 0f, 4000).setRepeatCount(Action.INFINITE));
		
		mFrame = new FrameBufferObject(Core.GRAPHICS.getWidth(), Core.GRAPHICS.getHeight());
		
		getStage().addFloor()
			.addChild(new Extra() {
				
				@Override
				public void update(long time) {
					if(!mStarted) {
						mBackGroundImage.update(time);
						mTestImage.update(time);
					}
				}
				
				@Override
				public void draw(Batch batch, float parentAlpha) {
					if(mStarted)
						batch.draw(mFrame.getTexture().getAsTextureRegion(), 0, 0, 640, 400, false, true);
					else {
					//if(!mStarted) {
						mBackGroundImage.draw(batch, 1f);
						mTestImage.draw(batch, 1f);
					}
				}
			});
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			mCount++;
			//if(mCount == 1)
			//	Core.GRAPHICS.frame = mFrame;
			
			if(mCount == 2)
				mStarted = true;
			
			if(mCount == 3)
				mStarted = false;
			/*
			if(!mStarted) {
				mStarted = true;
				Core.GRAPHICS.frame = mFrame;
				mFrame.begin();	
			} else {
				mStarted = false;
				mFrame.end();
			}*/
		}
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
