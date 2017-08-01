package core.scene.stage.actor.event;

import core.framework.Core;
import core.scene.stage.Floor;
import core.scene.stage.actor.Actor;

public abstract class ClickTouchListener extends TouchListener {
	
	public static final int VISUAL_PRESSED_DURATION = 100;
	
	private static final int  INVALID_POINTER_ID  = -1;
	private static final int  INVALID_TIME  = -1;
	
	private boolean mPressed;
	
	private long mVisualPressedTime = INVALID_TIME;
	
	private int mActivePointerID = INVALID_POINTER_ID;
	
	@Override
	public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
		if(mActivePointerID != INVALID_POINTER_ID) return false;
		mActivePointerID = event.getPointerID();
		mPressed = true;
		mVisualPressedTime = Core.GRAPHICS.getCurrentTime() + VISUAL_PRESSED_DURATION;
		return true;
	}
	
	@Override
	public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
		boolean contact = (listener.contactSelf(x, y) == listener);
		if(!contact) {
			mPressed = false;
			mActivePointerID = INVALID_POINTER_ID;
			Floor floor = listener.getFloor();
			if(floor != null) floor.removeTouchFocus(this, listener, event.getTargetActor(), event.getPointerID());
		}
	}
	
	@Override
	public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
		mActivePointerID = INVALID_POINTER_ID;
		if(mPressed) {
			mPressed = false;
			click(event, x, y, listener);
		}
	}
	
	public abstract void click(TouchEvent event, float x, float y, Actor<?> listener);
	
	public boolean isVisualPressed() {
		if(mPressed) return true;
		if(mVisualPressedTime >= Core.GRAPHICS.getCurrentTime()) return true;
		if(mVisualPressedTime == INVALID_TIME) return false;
		mVisualPressedTime = INVALID_TIME;
		return false;
	}

	public boolean isPressed() {
		return mPressed;
	}

	public int getActivePointerID() {
		return mActivePointerID;
	}

}
