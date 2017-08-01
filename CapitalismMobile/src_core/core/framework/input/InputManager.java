package core.framework.input;

import java.util.ArrayList;
import java.util.List;

import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

import core.framework.Core;
import core.framework.input.TextInput.TextInputListener;

/**
 * 유저로부터 입력받는 기능을 전반적으로 관리한다. 
 *  <ul>
 * 		<li>입력이벤트 동기화 및 관리
 * 		<li>렌더링과정에서 참조할 수 있도록 터치이벤트의 정보 제공
 * 		<li>유저로부터 문자열을 입력받을 수 있는 TextInput 제공
 * 		<li>스크린의 회전 각도 제공
 * 	</ul>
 * 
 * 입력은 기본적으로 UI스레드에서 이루어지는데, 발생한 입력이벤트를 수집해서 렌더링스레드에서 
 * 처리하도록 하여 두 스레드를 동기화한다.</p>
 * 
 * 터치는 최대 {@value #MAX_TOUCHES}개까지만 입력받으며 초과할 시에는 터치 전부가 무시된다.</p>
 * 
 * @author 김현우
 */
public class InputManager implements OnTouchListener, OnKeyListener {
	
	/** {@link InputManager}에서 처리할 수 있는 최대 터치 가능한 개수 */
	public static final int MAX_TOUCHES = 5;
	
	private boolean mInputEnabled = true;
	private boolean mTouchEventEnabled = true;
	private boolean mKeyEventEnabled = true;
	
	private List<InputEvent> mInputEventList = new ArrayList<InputEvent>(6);
	
	private long mRecentEventTime;
	private long mRecentDownTime;
	
	private float[] mScreenX = new float[MAX_TOUCHES];
	private float[] mScreenY = new float[MAX_TOUCHES];
	private float[] mScreenDeltaX = new float[MAX_TOUCHES];
	private float[] mScreenDeltaY = new float[MAX_TOUCHES];
	private boolean[] mTouched = new boolean[MAX_TOUCHES];
	private boolean mJustTouched;
	private int mTouchCount;
	
	private TextInput mTextInput;
	
	public InputManager() {
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(!mInputEnabled || !mTouchEventEnabled) return true;
		
		// 터치는 미리 정의된 상수의 개수만큼만 동시에 입력받을 수 있다.
		if(event.getPointerCount() > MAX_TOUCHES) return true;
		
		// 터치이벤트를 수집한다.
		synchronized(this) {
			mInputEventList.add(MotionEvent.obtain(event));
		}

		return true; 
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(!mInputEnabled || !mKeyEventEnabled) return true;
		
		// 키를 누르는 이벤트 이외는 무시한다.
		if(event.getAction() != KeyEvent.ACTION_DOWN) return true;

		// 키이벤트를 수집한다.
		synchronized(this) {
			mInputEventList.add(new KeyEvent(event));
		}

		return true;
	}
	
	/** 
	 * UI스레드에서 수집한 입력이벤트를 렌더링스레드에서 처리한다. 렌더링스레드에서 
	 * 렌더링하기 직전에 자동으로 호출되기 때문에 유저가 호출할 경우는 없다. 
	 */
	public void processInputEvents() {
		synchronized(this) {
			mJustTouched = false;
			mTouchCount = 0;
			
			List<InputEvent> eventList = mInputEventList;
			int n = eventList.size();
			for(int i=0; i<n; i++) {
				InputEvent event = eventList.get(i);
				
				if(event instanceof MotionEvent) {
	    			MotionEvent motionEvent = (MotionEvent) event;
	    			int action = motionEvent.getActionMasked();
	    			int count = motionEvent.getPointerCount();
	    			int index = motionEvent.getActionIndex();
	    			int id = motionEvent.getPointerId(index);	    			
	    			if(id >= MAX_TOUCHES) continue;
	    				
	    			switch(action) {
		    			case MotionEvent.ACTION_DOWN:
		    			case MotionEvent.ACTION_POINTER_DOWN:
		    				mScreenX[id] = motionEvent.getX(index);
			    			mScreenY[id] = motionEvent.getY(index);
			    			mScreenDeltaX[id] = 0;
		    				mScreenDeltaY[id] = 0;
		    				mTouched[id] = true;
		    				mJustTouched = true;
		    				break;
		    			case MotionEvent.ACTION_MOVE:
		    				for(int j=0; j<count; j++) {
		    					id = motionEvent.getPointerId(j);
		    					if(id >= MAX_TOUCHES) continue;
		    					
			    				float x = motionEvent.getX(j);
				    			float y = motionEvent.getY(j);
			    				mScreenDeltaX[id] = x - mScreenX[id];
			    				mScreenDeltaY[id] = y - mScreenY[id];
			    				mScreenX[id] = x;
				    			mScreenY[id] = y;
		    				}
		    				break;
		    			case MotionEvent.ACTION_UP:
		    			case MotionEvent.ACTION_POINTER_UP:
		    			case MotionEvent.ACTION_CANCEL:
		    			case MotionEvent.ACTION_OUTSIDE:
		    				mScreenX[id] = motionEvent.getX(index);
			    			mScreenY[id] = motionEvent.getY(index);
			    			mScreenDeltaX[id] = 0;
		    				mScreenDeltaY[id] = 0;
		    				mTouched[id] = false;
		    				break;
	    			}
	    			
	    			mTouchCount = count;
	    			mRecentDownTime = motionEvent.getDownTime();
				} else {
					KeyEvent keyEvent = (KeyEvent) event;
					mRecentDownTime = keyEvent.getDownTime();
				}
				mRecentEventTime = event.getEventTime();
				
				Core.APP.getAppListener().onInputEvent(event);
			}
			eventList.clear();
		}
	}
	
	public void setInputEnabled(boolean inputEnabled) {
		synchronized(this) {
			mInputEnabled = inputEnabled;
		}
	}
	
	public void setTouchEventEnabled(boolean touchEventEnabled) {
		synchronized(this) {
			mTouchEventEnabled = touchEventEnabled;
		}
	}
	
	public void setKeyEventEnabled(boolean keyEventEnabled) {
		synchronized(this) {
			mKeyEventEnabled = keyEventEnabled;
		}
	}
	
	public boolean isInputEnabled() {
		synchronized(this) {
			return mInputEnabled;
		}
	}
	
	public boolean isTouchEventEnabled() {
		synchronized(this) {
			return mTouchEventEnabled;
		}
	}
	
	public boolean isKeyEventEnabled() {
		synchronized(this) {
			return mKeyEventEnabled;
		}
	}
	
	public long getRecentEventTime() {
		synchronized(this) {
			return mRecentEventTime;
		}
	}

	public long getRecentDownTime() {
		synchronized(this) {
			return mRecentDownTime;
		}
	}

	/** 첫번째 포인터ID(0)에 의해 가장 최근에 터치한 스크린의 x좌표를 얻는다. */
	public float getScreenX() {
		synchronized(this) {
			return mScreenX[0];
		}
	}
	
	public float getScreenX(int pointerID) {
		synchronized(this) {
			return mScreenX[pointerID];
		}
	}
	
	/** 첫번째 포인터ID(0)에 의해 가장 최근에 터치한 스크린의 y좌표를 얻는다. */
	public float getScreenY() {
		synchronized(this) {
			return mScreenY[0];
		}
	}
	
	public float getScreenY(int pointerID) {
		synchronized(this) {
			return mScreenY[pointerID];
		}
	}
	
	/** 첫번째 포인터ID(0)에 의해 가장 최근에 터치한 스크린의 x좌표 2개의 차이를 얻는다. */
	public float getScreenDeltaX() {
		synchronized(this) {
			return mScreenDeltaX[0];
		}
	}
	
	public float getScreenDeltaX(int pointerID) {
		synchronized(this) {
			return mScreenDeltaX[pointerID];
		}
	}
	
	/** 첫번째 포인터ID(0)에 의해 가장 최근에 터치한 스크린의 y좌표 2개의 차이를 얻는다. */
	public float getScreenDeltaY() {
		synchronized(this) {
			return mScreenDeltaY[0];
		}
	}
	
	public float getScreenDeltaY(int pointerID) {
		synchronized(this) {
			return mScreenDeltaY[pointerID];
		}
	}
	
	/** 첫번째 포인터ID(0)에 의한 터치가 스크린에 닿에 있는 경우에 true를 리턴한다. */
	public boolean isTouched() {
		synchronized(this) {
			return mTouched[0];
		}
	}
	
	public boolean isTouched(int pointerID) {
		synchronized(this) {
			return mTouched[pointerID];
		}
	}
	
	/** 어떤 터치든 스크린에 처음 닿는 순간(프레임)에만 true를 리턴한다. */
	public boolean isJustTouched() {
		synchronized(this) {
			return mJustTouched;
		}
	}

	/** 
	 * 동시에 터치되는 개수를 얻는다. {@value #MAX_TOUCHES}가 최대값이며, 터치하지 않으면
	 * 0이 리턴된다. 
	 */
	public int getTouchCount() {
		synchronized(this) {
			return mTouchCount;
		}
	}

	/**
	 * 유저에게 입력을 받을 수 있는 TextInput을 띄운다.
	 * 
	 * @param title				타이틀 문자열. null 가능
	 * @param maxLength	입력 받는 문자열 최고 길이. 음수인 경우 무한대
	 * @param listener			완료되거나 취소될 경우에 호출되는 리스너
	 */
	public void bringUpTextInput(String title, int maxLength, TextInputListener listener) {
		bringUpTextInput(title, null, false, maxLength, listener);
	}

	/**
	 * 유저에게 입력을 받을 수 있는 TextInput을 띄운다.
	 * 
	 * @param title				타이틀 문자열. null 가능
	 * @param text				기본 문자열. textAsHint 참조
	 * @param textAsHint	true인 경우 기본 문자열을 힌트로 인식
	 * @param maxLength	입력 받는 문자열 최고 길이. 음수인 경우 무한대
	 * @param listener			완료되거나 취소될 경우에 호출되는 리스너
	 */
	public void bringUpTextInput(final String title, final String text, final boolean textAsHint, 
			final int maxLength, final TextInputListener listener) {
		Core.APP.runOnUIThread(new Runnable() {
			
			@Override
			public void run() {
				if(mTextInput == null) mTextInput = new TextInput();
				TextInput input = mTextInput;
				input.setTextInputListener(listener);
				input.show(title, text,textAsHint, maxLength);
			}
		});
	}

	public int getScreenRotation () {
		int orientation = Core.APP.getActivity().getWindowManager().getDefaultDisplay().getRotation();
		switch (orientation) {
			case Surface.ROTATION_270:
				return 270;
			case Surface.ROTATION_180:
				return 180;
			case Surface.ROTATION_90:
				return 90;
			case Surface.ROTATION_0:
			default:
				return 0;
		}
	}
}
