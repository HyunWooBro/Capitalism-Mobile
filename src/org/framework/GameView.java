package org.framework; 

//import org.game.BackGround;
import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;
import org.game.*;
import org.menu.*;
import org.screen.*;

import android.content.*;
import android.graphics.*;
import android.opengl.*;
import android.util.*;
import android.view.*;
//import org.game.Player;


public class GameView extends GLSurfaceView implements Drawer{
	
	private SpriteBatcher mRenderer;
	
	private IState mState;
	
	public long mIntroDelay;
	
    public GameView(Context context) {
        super(context);
        
        Log.i("abc", "GameView 진입");
        
    	// 키입력처리를 받기위해서
    	setFocusable(true);
      	
    	AppManager.getInstance().setGameView(this);
    	AppManager.getInstance().setSize(getWidth(), getHeight());
  
    	// 기기의 스크린 정보 처리
    	Screen.GetInstance().Init(AppManager.getInstance().getResources().getDisplayMetrics().widthPixels, 
				AppManager.getInstance().getResources().getDisplayMetrics().heightPixels, 640, 400,
				AppManager.getInstance().getResources().getDisplayMetrics());
    	
    	/* onDrawFrame에서 그리는 순서와는 상관없이 bitmapIDs에서 가장 먼저 나올수록 가장 먼저 그려진다. 
    	 * 예를 들어, ID를 A, B순으로 선언하고 그릴 때 A와 B가 겹쳐지는 경우, onDrawFrame에서 A를 먼저
    	 * 호출하든 B를 먼저 호출하든 A부터 그려지고 그 위에 B가 그려진다. SpriteBatcher에는 이 순서를
    	 * 동적으로 조정하고 싶은 경우 batchDraw를 중간에 호출하라고 설명한다.
    	 */
    	 int[] bitmapIDs = new int[] 
    			 {
				 R.drawable.canvas,
    			 };

    	 // GLSurfaceView의 렌더링 스레드로 SpriteBatcher을 지정
    	 mRenderer = new SpriteBatcher(context, bitmapIDs, this);
    	 setRenderer(mRenderer);
    	
    	 // 메뉴 상태로 게임 시작
    	 ChangeState(new MenuState());

    }
	
	public void Update() {
		mState.Update();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mState.onKeyDown(keyCode, event);
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mState.onTouchEvent(event);
		return true;

	}
	
	public void ChangeState(IState _state){
		if(mState!=null)
			mState.Destroy();
		_state.Init();
		mState = _state;
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		Log.i("abc", "view attached");
			
		mIntroDelay = System.currentTimeMillis() + 3000;
	}

	@Override
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
		mState.onDrawFrame(gl, spriteBatcher);   
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public SpriteBatcher getRenderer() {
		return mRenderer;
	}
	

}
