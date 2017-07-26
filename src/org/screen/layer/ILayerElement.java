package org.screen.layer;

import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;

import android.graphics.*;
import android.view.*;

public interface ILayerElement {
	
	public void Render(Canvas canvas);
	
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher);
	
	public boolean onTouchEvent(MotionEvent event);
	
	// public Layer GetLayer();

}
