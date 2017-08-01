package core.scene.stage.actor.event;

import android.view.MotionEvent;

import core.framework.Core;
import core.framework.input.GestureDetector;
import core.framework.input.GestureDetector.DoubleTapListener;
import core.framework.input.GestureDetector.GestureListener;
import core.math.Vector2;
import core.scene.stage.Floor;
import core.scene.stage.actor.Actor;

/**
 * {@link TouchListener}을 확장하여 제스처를 처리하는 이벤트 리스너이다. {@link TouchListener}의 
 * 메서드가 먼저 처리된 후 조건이 성립하는 제스처 리스너의 메서드가 호출된다.</p>
 * 
 * 호출되는 메서드의 순서를 정리하면 다음과 같다.<br>
 * onTouch(...) -> onDown(...) or onMove(...) or onInterceptUp(...) -> [onSingleTapUp(...) or ...] -> [onUp(...)]</p>
 * 
 * TouchListener의 handle()이 false를 리턴하면 제스처 리스너의 메서드는 처리되지 않는다.</p>
 * 
 * @author 김현우
 */
public abstract class GestureTouchListener extends TouchListener {
	
	private GestureDetector mDetector;

	private TouchEvent mTouchEvent;
	
	private float mX;
	private float mY;
	
	private boolean mInterceptUp;
	
	public GestureTouchListener() {
		mDetector = new GestureDetector(new GestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				GestureTouchListener.this.onSingleTapUp(mTouchEvent, mX, mY, mTouchEvent.getListenerActor());
				return true;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				GestureTouchListener.this.onShowPress(mTouchEvent, mX, mY, mTouchEvent.getListenerActor());
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Actor<?> listener = mTouchEvent.getListenerActor();
				float widthScale = listener.getFloor().getCamera().getViewportWidth() / Core.GRAPHICS.getWidth();
				float heightScale = listener.getFloor().getCamera().getViewportHeight() / Core.GRAPHICS.getHeight();
				GestureTouchListener.this.onScroll(mTouchEvent, distanceX*widthScale, distanceY*heightScale, 
						mX, mY, listener);
				return true;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				GestureTouchListener.this.onLongPress(mTouchEvent, mX, mY, mTouchEvent.getListenerActor());
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				Actor<?> listener = mTouchEvent.getListenerActor();
				float widthScale = listener.getFloor().getCamera().getViewportWidth() / Core.GRAPHICS.getWidth();
				float heightScale = listener.getFloor().getCamera().getViewportHeight() / Core.GRAPHICS.getHeight();
				GestureTouchListener.this.onFling(mTouchEvent, velocityX*widthScale, velocityY*heightScale, 
						mX, mY, listener);
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				// 무시한다.
				return true;
			}
			
			@Override
			public boolean onPinch(MotionEvent e1, MotionEvent e2, Vector2 initPoint1,
					Vector2 initPoint2, Vector2 point1, Vector2 point2) {
				Actor<?> listener = mTouchEvent.getListenerActor();
				listener.screenToLocalCoordinates(initPoint1);
				listener.screenToLocalCoordinates(initPoint2);
				listener.screenToLocalCoordinates(point1);
				listener.screenToLocalCoordinates(point2);
				GestureTouchListener.this.onPinch(mTouchEvent, initPoint1, initPoint2, point1, point2, 
						mX, mY, listener);
				return true;
			}
			
			@Override
			public boolean onZoom(MotionEvent e1, MotionEvent e2, float initDistance, float distance) {
				GestureTouchListener.this.onZoom(mTouchEvent, initDistance, distance, mTouchEvent.getListenerActor());
				return true;
			}
		});
	}
	
	public GestureTouchListener setLongPressEnabled(boolean longPressEnabled) {
		mDetector.setLongPressEnabled(longPressEnabled);
		return this;
	}
	
	public GestureTouchListener setDoubleTapEnabled(boolean doubleTapEnabled) {
		mDetector.setDoubleTapListener((!doubleTapEnabled)? null : new DoubleTapListener() {
			
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				GestureTouchListener.this.onSingleTapConfirmed(mTouchEvent, mX, mY, mTouchEvent.getListenerActor());
				return true;
			}
			
			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				GestureTouchListener.this.onDoubleTapEvent(mTouchEvent, mX, mY, mTouchEvent.getListenerActor());
				return true;
			}
			
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				GestureTouchListener.this.onDoubleTap(mTouchEvent, mX, mY, mTouchEvent.getListenerActor());
				return true;
			}
		});
		return this;
	}
	
	@Override
	public boolean handle(Event event) {
		if(!super.handle(event)) return false;
		TouchEvent e = (TouchEvent) event;
		
		// 터치오버이벤트의 경우 네이티브 MotionEvent가 null이다.
		if(e.getNativeMotionEvent() != null) {
			mTouchEvent = e;
			mDetector.onTouchEvent(e.getNativeMotionEvent());
		}
		
		if(mInterceptUp) {
			mInterceptUp = false;
			Actor<?> listenerActor = e.getListenerActor();
			onUp(e, mX, mY, listenerActor);
			Floor floor = listenerActor.getFloor();
			if(floor != null) floor.removeTouchFocus(this, listenerActor, e.getTargetActor(), e.getPointerID());
		}
		return true;
	}
	
	@Override
	protected Vector2 getLocalPos(TouchEvent event, Actor<?> listener) {
		final Vector2 v = super.getLocalPos(event, listener);
		mX = v.x;
		mY = v.y;
		return v;
	}
	
	@Override
	protected void onInterceptUp(TouchEvent event, float x, float y, Actor<?> listener) {
		mInterceptUp = true;
	}
	
	public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	public void onShowPress(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	/** 
	 * 스크롤하면 호출된다. distanceX는 마지막 터치의 x좌표에서 바로 이전 터치의 x좌표의 
	 * 차이를, distanceY는 마지막 터치의 y좌표에서 바로 이전 터치의 y좌표의 차이를 반환한다.  
	 */
	public void onScroll(TouchEvent event, float distanceX, float distanceY, float x, float y, 
			Actor<?> listener) {
	}
	
	public void onLongPress(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	/**
	 * 스크롤 후 급격하게 손을 떼면 호출된다. velocityX는 마지막 터치시점에서의 x좌표의 속도를, 
	 * velocityY는 마지막 터치시점에서의 y좌표의 속도를 반환한다. 부호는 속도의 방향을 나타낸다.
	 */
	public void onFling(TouchEvent event, float velocityX, float velocityY, float x, float y, 
			Actor<?> listener) {
	}
	
	public void onPinch(TouchEvent event, Vector2 initPoint1, Vector2 initPoint2, 
			Vector2 point1, Vector2 point2, float x, float y, Actor<?> listener) {
	}
	
	public void onZoom(TouchEvent event, float initDistance, float distance, Actor<?> listener) {
	}

	public void onSingleTapConfirmed(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	public void onDoubleTapEvent(TouchEvent event, float x, float y, Actor<?> listener) {
	}
	
	public void onDoubleTap(TouchEvent event, float x, float y, Actor<?> listener) {
	}
}
