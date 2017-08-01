package core.framework.input;

import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import core.framework.Core;
import core.math.Vector2;

/**
 * 안드로이드의 GestureDetector을 바탕으로 onPinch(...) 및 onZoom(...)을 추가하고 UI스레드에서의 
 * 생성을 보장한다.</p>
 * 
 * onPinch(...)와 onZoom(...)를 제외한 다른 모든 제스처는 터치가 동시에 하나만 입력될 경우에만 
 * 처리된다는 점에 주의하라.</p>
 * 
 * @see android.view.GestureDetector
 * @author 김현우
 */
public class GestureDetector {
	
	private android.view.GestureDetector mNativeDetector;
	
	private GestureListener mGestureListener;
	
	private boolean mPinching;
	private Vector2 mOldPoint1 = new Vector2();
	private Vector2 mOldPoint2 = new Vector2();
	private Vector2 mPoint1 = new Vector2();
	private Vector2 mPoint2 = new Vector2();
	
	private MotionEvent mCurrentDownEvent;
	
	/** UI스레드에서 GestureDetector을 생성한다. */
	public GestureDetector(final GestureListener listener) {
		mGestureListener = listener;
		Core.APP.runOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				mNativeDetector = new android.view.GestureDetector(Core.APP.getActivity(), listener);
			}
		});
	}

	public GestureDetector setDoubleTapListener(final DoubleTapListener doubleTapListener) {
		Core.APP.runOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				mNativeDetector.setOnDoubleTapListener((OnDoubleTapListener) doubleTapListener);
			}
		});
		return this;
	}

	public GestureDetector setLongPressEnabled(final boolean longPressEnabled) {
		Core.APP.runOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				mNativeDetector.setIsLongpressEnabled(longPressEnabled);
			}
		});
		return this;
	}

	public boolean isLongPressEnabled() {
		return (mNativeDetector != null)? mNativeDetector.isLongpressEnabled() : false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if(mNativeDetector == null) return false;
		int pointerCount = event.getPointerCount();
		if(pointerCount > 2) return false;
		
		boolean handled = false;
		
		if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			if(mCurrentDownEvent != null) mCurrentDownEvent.recycle();
			mCurrentDownEvent = MotionEvent.obtain(event);
		}
		
		// count가 2일 경우 onPinch(...)와 onZoom(...)만 처리한다. 이들 메서드는 액션의 종류와 
		// 관계없이 처리할 수 있다. 즉, ACTION_MOVE만으로도 처리할 수 있다.
		if(pointerCount == 2) {
			if(!mPinching) {
				mPinching = true;
				mOldPoint1.set(event.getX(0), event.getY(0));
				mOldPoint2.set(event.getX(1), event.getY(1));
			} else {
				float x0 = event.getX(0);
				float y0 = event.getY(0);
				float x1 = event.getX(1);
				float y1 = event.getY(1);
				mPoint1.set(x0, y0);
				mPoint2.set(x1, y1);
				handled |= mGestureListener.onPinch(mCurrentDownEvent, event, mOldPoint1, mOldPoint2, mPoint1, mPoint2);
				handled |= mGestureListener.onZoom(mCurrentDownEvent, event, mOldPoint1.dst(mOldPoint2), mPoint1.dst(mPoint2));
				mOldPoint1.set(x0, y0);
				mOldPoint2.set(x1, y1);
			}
		} else { // count가 1일 경우 그 밖의 모든 제스처를 처리한다.
			mPinching = false;
			handled |= mNativeDetector.onTouchEvent(event);
		}
		
		return handled;
	}
	
	public static abstract class DoubleTapListener implements OnDoubleTapListener {
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}
		
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}
	}
	
	public static abstract class GestureListener implements OnGestureListener {
		
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}

		public boolean onPinch(MotionEvent e1, MotionEvent e2, Vector2 initPoint1, Vector2 initPoint2, 
				Vector2 point1, Vector2 point2) {
			return false;
		}
		
		public boolean onZoom(MotionEvent e1, MotionEvent e2, float initDistance, float distance) {
			return false;
		}
	}

}
