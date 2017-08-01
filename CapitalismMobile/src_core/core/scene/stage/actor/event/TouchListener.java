package core.scene.stage.actor.event;

import core.math.Vector2;
import core.scene.stage.Floor;
import core.scene.stage.Floor.TouchFocus;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.TouchEvent.TouchType;

/**
 * TouchEvent를 처리하는 이벤트 리스너이다. 우선, {@link TouchType}의 종류와는 상관없이 
 * {@link #onTouch(TouchEvent, float, float, Actor)}가 항상 호출된 후, 타입에 따라 적절한 
 * 메서드가 호출된다.
 * 
 * @author 김현우
 */
public abstract class TouchListener implements EventListener {
	
	protected static final Vector2 VECTOR = new Vector2();

	@Override
	public boolean handle(Event event) {
		if(!(event instanceof TouchEvent)) return false;
		TouchEvent e = (TouchEvent) event;
		
		Actor<?> listenerActor = event.getListenerActor();
		final Vector2 v = getLocalPos(e, listenerActor);
		float x = v.x;
		float y = v.y;
		
		onTouch(e, x, y, listenerActor);
		switch(e.getTouchType()) {
			case TOUCH_DOWN:
				if(onDown(e, x, y, listenerActor)) {
					Floor floor = listenerActor.getFloor();
					if(floor != null) {
						TouchFocus focus = new TouchFocus(this, listenerActor, e.getTargetActor(), e.getPointerID());
						floor.addTouchFocus(focus);
						return true;
					}
				}
				return false;
			case TOUCH_MOVE:
				onMove(e, x, y, listenerActor);
				return true;
			case TOUCH_UP:
				onInterceptUp(e, x, y, listenerActor);
				return true;
			case TOUCH_ENTER:
				onEnter(e, x, y, e.getOverActor(), listenerActor);
				return true;
			case TOUCH_EXIT:
				onExit(e, x, y, e.getOverActor(), listenerActor);
				return true;
		}
		
		return false;
	}
	
	protected Vector2 getLocalPos(TouchEvent event, Actor<?> listener) {
		final Vector2 v = VECTOR;
		listener.screenToLocalCoordinates(v.set(event.getScreenX(), event.getScreenY()));
		return v;
	}
	
	protected void onInterceptUp(TouchEvent event, float x, float y, Actor<?> listener) {
		onUp(event, x, y, listener);
		Floor floor = listener.getFloor();
		if(floor != null) floor.removeTouchFocus(this, listener, event.getTargetActor(), event.getPointerID());
	}

	public void onTouch(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
		return true;
	}
	
	public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	/** 어떤 터치든 유형에 상관없이 Actor의 범위안에 처음 들어올 경우에 호출된다. */
	public void onEnter(TouchEvent event, float x, float y, Actor<?> fromActor, Actor<?> listener) {
	}
	
	/** 
	 * {@link #onEnter(TouchEvent, float, float, Actor, Actor)}이 호출된 후 터치가 
	 * Actor의 범위를 벗어나면 호출된다.
	 */ 
	public void onExit(TouchEvent event, float x, float y, Actor<?> toActor, Actor<?> listener) {
	}

}
