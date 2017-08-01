package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;
import android.view.MotionEvent;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.framework.input.GestureDetector;
import core.framework.input.GestureDetector.DoubleTapListener;
import core.framework.input.GestureDetector.GestureListener;
import core.math.Vector2;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;

@SuppressWarnings("rawtypes")
public class GestureTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	private GestureDetector mDetector;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);

		mDetector = new GestureDetector(new GestureListener() {
			
					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						//Core.APP.info("onSingleTapUp");
						return false;
					}
					
					@Override
					public void onShowPress(MotionEvent e) {
						//Core.APP.info("onShowPress");
					}
					
					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
							float distanceY) {
						//Core.APP.info("onScroll");
						return false;
					}
					
					@Override
					public void onLongPress(MotionEvent e) {
						Core.APP.debug("onLongPress");
					}
					
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							float velocityY) {
						//Core.APP.info("onFling");
						return false;
					}
					
					@Override
					public boolean onDown(MotionEvent e) {
						return false;
					}
					
					@Override
					public boolean onPinch(MotionEvent old, MotionEvent e, Vector2 initPoint1, Vector2 initPoint2, Vector2 point1, Vector2 point2) {
						Core.APP.debug("onPinch" + initPoint1.toString() + initPoint2.toString() + point1.toString() + point2.toString());
						return false;
					}
					
					@Override
					public boolean onZoom(MotionEvent e1, MotionEvent e2, float initDistance, float distance) {
						return false;
					}
				})
				.setDoubleTapListener(new DoubleTapListener() {
					
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						Core.APP.debug("onSingleTapConfirmed");
						return false;
					}
					
					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						return false;
					}
					
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						Core.APP.debug("onDoubleTap");
						return false;
					}
				})
				.setLongPressEnabled(true);
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage)
			.addChild(new Extra() {
				
				@Override
				protected void prepare() {
					super.prepare();
					addEventListener(new TouchListener() {
						@Override
						public void onTouch(TouchEvent event, float x, float y, Actor<?> listener) {
							mDetector.onTouchEvent(event.getNativeMotionEvent());
						}
					});
				}

			});
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
