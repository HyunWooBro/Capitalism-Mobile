package org.screen.layer.component;

import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;
import org.screen.layer.window.*;

import android.graphics.*;
import android.view.*;

public class Text  implements IWindowElement{
	private boolean mVisiable;
	private boolean mRenderRectEnabled;
	
	private Point mPos;
	
	private String mString;
	
	private Rect mRenderRect;
	
	private Paint mPaint;

	public Text(Point pos, String string, Paint paint) {
		// TODO Auto-generated constructor stub
		
		mVisiable = false;
		mRenderRectEnabled = false;
		
		mPos = pos;
		
		mRenderRect = null;
		
		mString = string;
		
		mPaint = paint;
	}
	
	public Text(Rect renderRect, String string, Paint paint) {
		// TODO Auto-generated constructor stub
		
		mVisiable = false;
		mRenderRectEnabled = true;
		
		mPos = null;
		
		mRenderRect = renderRect;
		
		mString = string;
		
		mPaint = paint;
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		
		if(mVisiable == true)
		{
			if(mRenderRectEnabled == true)
			{
				int start = 0;
				int end = 0;
				int line = 0;
				
				do{
					end = mPaint.breakText(mString.substring(start), true, mRenderRect.width(), null);
					
					line++;
					
					if(mRenderRect.height() < mPaint.getTextSize()*line)
						break;
					
					if(end != 0) 
					{
						canvas.drawText(mString, start, start+end, mRenderRect.left, mRenderRect.top + mPaint.getTextSize()*line, mPaint);
						
						start = start+end;
					}
					else
						canvas.drawText(mString.substring(start), mRenderRect.left, mRenderRect.top + mPaint.getTextSize()*line, mPaint);

				}while(end != 0);
				
			}
			else
				canvas.drawText(mString, mPos.x, mPos.y, mPaint);
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setVisiable(boolean visiable) {
		mVisiable = visiable;
	}

	public void setString(String string) {
		mString = string;
	}

	public void setRenderRectEnabled(boolean renderRectEnabled) {
		mRenderRectEnabled = renderRectEnabled;
	}

	@Override
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
	}

}
