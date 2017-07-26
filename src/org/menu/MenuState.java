/**
 * 
 */
package org.menu;

import javax.microedition.khronos.opengles.*;

import org.etc.*;
import org.framework.*;
import org.framework.openGL.*;
import org.game.*;
import org.game.GameState.*;
import org.screen.*;
import org.screen.layer.component.*;
import org.screen.layer.component.Button.*;

import android.graphics.*;
import android.util.*;
import android.view.*;

/**
 * @author 김현우
 *
 */
public class MenuState implements IState {
	
	public enum AnimationTypes {OPENING, NONE, CLOSING};
	private static AnimationTypes sAnimationType = AnimationTypes.OPENING;
	
	private static IState sState;			// 메뉴의 상태(Menu_XXXState)를 저장

	public MenuState() {
		// TODO Auto-generated constructor stub
		
		// 전역변수처럼 등록
		AppManager.getInstance().m_state = this;
	}

	@Override
	public void Init() {
		// TODO Auto-generated method stub

		ChangeState(new Menu_MainState());
	}

	@Override
	public void Destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Update() {
		// TODO Auto-generated method stub
		
		sState.Update();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		return sState.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		return sState.onTouchEvent(event);
	}
	
	public static void ChangeState(IState _state) {
		if(sState!=null)
			sState.Destroy();
		_state.Init();
		sState = _state;
	}

	public static AnimationTypes getAnimationType() {
		return sAnimationType;
	}

	public static void setAnimationType(AnimationTypes animationType) {
		sAnimationType = animationType;
	}

	@Override
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
		//spriteBatcher.setMaxFPS(45);
		
		sState.onDrawFrame(gl, spriteBatcher);
		
		spriteBatcher.drawText("fps : " + Debug.LogFPS(), 50, 50, new Paint());
	}

}
