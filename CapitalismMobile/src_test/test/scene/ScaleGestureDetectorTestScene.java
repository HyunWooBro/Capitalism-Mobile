package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.widget.Image;

@SuppressWarnings("rawtypes")
public class ScaleGestureDetectorTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	private ScaleGestureDetector mDetector;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);
		
		mDetector = new ScaleGestureDetector(Core.APP.getActivity(), 
				new OnScaleGestureListener() {
			
			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				//detector.
			}
			
			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				return true;
			}
			
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				
				Core.APP.debug("current span(터치 된 두 포인트 사이의 거리)" + detector.getCurrentSpan());
				/*Core.APP.debug("current spanX" + detector.getCurrentSpanX());
				Core.APP.debug("current spanY" + detector.getCurrentSpanY());
				
				Core.APP.debug("previous span(제스처가 일어나기 이전의 두 포인트 사이 거리)" + detector.getPreviousSpan());
				Core.APP.debug("previous spanX" + detector.getPreviousSpanX());
				Core.APP.debug("previous spanY" + detector.getPreviousSpanY());
				
				Core.APP.debug("focusX" + detector.getFocusX());
				Core.APP.debug("focusY" + detector.getFocusY());*/
				
				return false;
			}
		});
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage);
	}
	
	@Override
	public void handleTouchEvent(MotionEvent event, int action, float screenX,
			float screenY, int pointerID) {
		super.handleTouchEvent(event, action, screenX, screenY, pointerID);
		mDetector.onTouchEvent(event);
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
