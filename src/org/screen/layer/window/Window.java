package org.screen.layer.window;

import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;
import org.game.*;
import org.screen.*;
import org.screen.layer.*;
import org.screen.layer.component.*;

import android.graphics.*;
import android.util.*;
import android.view.*;

public class Window implements ILayerElement{
	private boolean mVisiable;
	private boolean mDisabled;
	private boolean mTouched;
	private boolean mMovable;
	private boolean mOutsideTouchable;
	private boolean mBackGroundColorApplied;
	
	private int mBackGroundColor;
	
	private int mTouchedComponent;
	
	private Point mPos;
	private Rect mTouchRect;
	
	private int oldX;
	private int oldY;
	
	private int moveX;
	private int moveY;
	
	private Bitmap mBitmap;
	
	private ArrayList<IWindowElement> mWindowElementList;

	public Window(Point pos, Rect touchRect, Bitmap bitmap) {
		// TODO Auto-generated constructor stub
		
		mVisiable = false;
		mMovable = false;
		mOutsideTouchable = false;
		
		mTouchedComponent = -1;
		
		mPos = pos;
		mTouchRect = touchRect;
		
		mBitmap = bitmap;
		
		mWindowElementList = new ArrayList<IWindowElement>();
		
	}
	
	public void Add(IWindowElement component)
	{
		mWindowElementList.add(component);
	}
	
	public void moveTo(int Tx, int Ty)
	{
		mPos.x = Tx;
		mPos.y = Ty;
		
		// rect도
	}
	
	public void moveRelative(int Rx, int Ry)
	{
		mPos.x += Rx;
		mPos.y += Ry;
		
		int width = mTouchRect.width();
		int height = mTouchRect.height();
		
		if(mPos.x < 0)
			mPos.x = 0;
		if(mPos.y < 0)
			mPos.y = 0;
		if(mPos.x > Screen.getVirtualScreenWidth() - width)
			mPos.x = Screen.getVirtualScreenWidth() - width;
		if(mPos.y > Screen.getVirtualScreenHeight() - height)
			mPos.y = Screen.getVirtualScreenHeight() - height;
		
		mTouchRect.left += Rx;
		mTouchRect.top += Ry;
		mTouchRect.right += Rx;
		mTouchRect.bottom += Ry;
		
		if(mTouchRect.left < 0)
		{
			mTouchRect.left = 0;
			mTouchRect.right = width;
		}
		if(mTouchRect.top < 0)
		{
			mTouchRect.top = 0;
			mTouchRect.bottom = height;
		}
		if(mTouchRect.right > Screen.getVirtualScreenWidth())
		{
			mTouchRect.left = Screen.getVirtualScreenWidth() - width;
			mTouchRect.right = Screen.getVirtualScreenWidth();
		}
		if(mTouchRect.bottom > Screen.getVirtualScreenHeight())
		{
			mTouchRect.top = Screen.getVirtualScreenHeight() - height;
			mTouchRect.bottom = Screen.getVirtualScreenHeight();
		}

	}
	
	@Override
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
		if(mVisiable == true)
		{
			//if(mBackGroundColorApplied == true)
			//	canvas.drawColor(mBackGroundColor);
			
			//if(mBitmap != null)
			//	canvas.drawBitmap(mBitmap, mPos.x, mPos.y, null);
			
			gl.glPushMatrix();
			gl.glTranslatef(mPos.x, mPos.y, 0);
			
			for(int i=0; i<mWindowElementList.size(); i++)
			{
				mWindowElementList.get(i).onDrawFrame(gl, spriteBatcher);
			}
			
			spriteBatcher.batchDraw(gl);
			gl.glPopMatrix();
		}
		
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		
		/*
		if(mVisiable == true)
		{
			if(mBackGroundColorApplied == true)
				canvas.drawColor(mBackGroundColor);
			
			if(mBitmap != null)
				canvas.drawBitmap(mBitmap, mPos.x, mPos.y, null);
			
			canvas.save();
			canvas.translate(mPos.x, mPos.y);
			
			for(int i=0; i<mWindowElementList.size(); i++)
			{
				mWindowElementList.get(i).Render(canvas);
			}
			
			canvas.restore();
		}*/
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		int originalX = (int)event.getX();
		int originalY = (int)event.getY();
		
		event.setLocation(originalX - Screen.reverseTouchX(mPos.x), originalY - Screen.reverseTouchY(mPos.y));
		
		for(int i=0; i<mWindowElementList.size(); i++)
		{
			if(mWindowElementList.get(i).onTouchEvent(event))
			{
				Log.i(Utility.TAG, "window");
				mTouchedComponent = i;
				return true;
			}
		}
		
		event.setLocation(originalX, originalY);
		
		
		boolean moveAllowed = true;
		if(event.getEdgeFlags() == 1)
		{
			event.setEdgeFlags(0);
			moveAllowed = false;
		}
		
		if(mMovable == true && moveAllowed == true)
		{
			int x = Screen.touchX((int)event.getX());
			int y = Screen.touchY((int)event.getY());
			
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				if(mTouchRect.contains(x, y))
				{
					oldX =  Screen.touchX((int)event.getX());
					oldY =  Screen.touchY((int)event.getY());
					
					mTouched = true;
					
					return true;
				}
			}
			else if(event.getAction() == MotionEvent.ACTION_MOVE)
			{
				if(mTouched == true)
				{
					Log.i(Utility.TAG, "window move");
					moveX =  Screen.touchX((int)event.getX());
					moveY =  Screen.touchY((int)event.getY());
					
					moveRelative(moveX-oldX, moveY-oldY);
					 
					oldX =  moveX;
					oldY =  moveY;
					
					return true;
				}
			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				mTouched = false;
				
				return true;
			}
		}
		
		if(mOutsideTouchable == false)
			return true;
		
		return false;
	}

	public boolean isDisabled() {
		return mDisabled;
	}

	public boolean isMovable() {
		return mMovable;
	}

	public ArrayList<IWindowElement> getWindowElementList() {
		return mWindowElementList;
	}

	public int getTouchedComponent() {
		return mTouchedComponent;
	}

	public void setVisiable(boolean visiable) {
		mVisiable = visiable;
	}

	public void setDisabled(boolean disabled) {
		mDisabled = disabled;
	}

	public void setMovable(boolean movable) {
		mMovable = movable;
	}

	public void setOutsideTouchable(boolean outsideTouchable) {
		mOutsideTouchable = outsideTouchable;
	}

	public void setBackGroundColorApplied(boolean backGroundColorApplied) {
		mBackGroundColorApplied = backGroundColorApplied;
	}

	public void setBackGroundColor(int backGroundColor) {
		mBackGroundColor = backGroundColor;
	}

	public void setTouchedComponent(int touchedComponent) {
		mTouchedComponent = touchedComponent;
	}

}
