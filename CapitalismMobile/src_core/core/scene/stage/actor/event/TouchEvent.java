package core.scene.stage.actor.event;

import android.view.MotionEvent;

import core.scene.stage.actor.Actor;

public class TouchEvent extends Event {
	
	public enum TouchType {
		TOUCH_DOWN, 
		TOUCH_MOVE, 
		TOUCH_UP, 
		TOUCH_ENTER, 
		TOUCH_EXIT, 
	}
	
	private TouchType mTouchType;
	
	private float mScreenX;
	private float mScreenY;
	private int mPointerID;
	
	private Actor<?> mOverActor;
	
	private MotionEvent mNativeMotionEvent;
	
	public TouchEvent(TouchType touchType, float screenX, float screenY, int pointerID, MotionEvent nativeMotionEvent) {
		mTouchType = touchType;
		mScreenX = screenX;
		mScreenY = screenY;
		mPointerID = pointerID;
		mNativeMotionEvent = nativeMotionEvent;
	}

	public TouchType getTouchType() {
		return mTouchType;
	}
	
	public float getScreenX() {
		return mScreenX;
	}

	public float getScreenY() {
		return mScreenY;
	}

	public int getPointerID() {
		return mPointerID;
	}

	public Actor<?> getOverActor() {
		return mOverActor;
	}

	public void setOverActor(Actor<?> overActor) {
		mOverActor = overActor;
	}

	public MotionEvent getNativeMotionEvent() {
		return mNativeMotionEvent;
	}

}
