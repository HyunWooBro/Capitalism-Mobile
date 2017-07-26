package org.framework;

import java.io.*;
import java.net.*;

import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;

import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;

public interface IState {
	public void Init();
		// 이상태로 바뀌었을때 실행될것들
	public void Destroy();
		// 다른상태로 바뀔때 실행될것들
	public void Update();
		// 지속적으로 수행할것들
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher);
		// 그려줘야할것들
	public boolean onKeyDown(int keyCode, KeyEvent event);
		// 키입력처리
	public boolean onTouchEvent(MotionEvent event);
		// 터치입력처리
}
