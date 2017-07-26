package org.screen.layer.component;

import javax.microedition.khronos.opengles.*;

import org.framework.*;
import org.framework.openGL.*;
import org.game.*;
import org.screen.*;
import org.screen.layer.component.Button.*;

import android.graphics.*;
import android.view.*;

public class CheckBox implements IComponentBasis{
	
	private boolean mChecked; 
	
	private boolean mVisiable;							// 보이는가?
	private boolean mDisabled;							// 비활성화 상태인가?
	private boolean mTouched;							// 터치되고 있는가?
	private boolean mTouchedEnabled;				// 터치가 가능한가?
	private boolean mPushed;							// 푸쉬된 상태인가?
	private boolean mPushedEnabled;				// 푸쉬가 가능한가?
	private boolean mStringEnabled;					// 문자를 포함할 수 있는가?
	private boolean mFlashEnabled;					// 플래쉬가 활성화 상태인가?
	
	private long mReplyStartTime;
	private long mReplyDelay;
	
	private Point mPos;
	private Rect mTouchRect;
	
	private int mNormalBitmap;						// 기본 상태의 이미지
	private int mTouchedBitmap;					// 터치 상태의 이미지
	private int mPushedBitmap;						// 푸쉬 상태의 이미지 
	private int mDisabledBitmapID;					// 비활성화 상태의 이미지
	private SpriteAnimation mNormalFlashSprite;	// 기본 상태의 플래쉬
	private SpriteAnimation mTouchedFlashSprite;	// 터치 상태의 플래쉬
	private SpriteAnimation mPushedFlashSprite;	// 푸쉬 상태의 플래쉬
	
	private String mString;									// 버튼에 표시할 글자
	private Point mStringNormalPos;					// 기본 상태의 글자 위치
	private Point mStringTouchedPos;					// 터치 상태의 글자 위치
	private Point mStringPushedPos;					// 
	private Point mStringDisabledPos;					// 비활성 상태의 글자 위치
	private Paint mNormalPaint;
	private Paint mTouchedPaint;
	private Paint mPushedPaint;
	private Paint mDisabledPaint;
	
	private int mCheckedBitmap;
	private int mUncheckedBitmap;
	
	public CheckBox(Point pos, int checkedBitmap, int uncheckedBitmap, boolean checked) {
		// TODO Auto-generated constructor stub
		
		mChecked = checked;
		
		mVisiable = false;
		mDisabled = false;
		mTouched = false;
		mTouchedEnabled = false;
		mPushed = false;
		mPushedEnabled = false;
		mStringEnabled = false;
		mFlashEnabled = false;
		
		mPos = pos;
		
		BitmapFactory.Options options;
		options = new BitmapFactory.Options();
		options.inDensity=160;
		options.inTargetDensity = 160;
		Bitmap bitmap = AppManager.getInstance().getBitmap(checkedBitmap, options);
		mTouchRect = new Rect(pos.x, pos.y, pos.x + bitmap.getWidth(), pos.y + bitmap.getHeight());
		bitmap.recycle();
		
		mCheckedBitmap = checkedBitmap;
		mUncheckedBitmap = uncheckedBitmap;
		
		mString = null;
		mStringNormalPos = null;
		mStringTouchedPos = null;
		mStringPushedPos = null;
		mStringDisabledPos = null;
		mNormalPaint = null;
		mTouchedPaint = null;
		mPushedPaint = null;
		mDisabledPaint = null;
	}

	public CheckBox(Point pos, Rect touchRect, int checkedBitmap, int uncheckedBitmap, boolean checked) {
		// TODO Auto-generated constructor stub
		
		mChecked = checked;
		
		mVisiable = false;
		mDisabled = false;
		mTouched = false;
		mTouchedEnabled = false;
		mPushed = false;
		mPushedEnabled = false;
		mStringEnabled = false;
		mFlashEnabled = false;
		
		mPos = pos;
		mTouchRect = touchRect;
		
		mCheckedBitmap = checkedBitmap;
		mUncheckedBitmap = uncheckedBitmap;
		
		mString = null;
		mStringNormalPos = null;
		mStringTouchedPos = null;
		mStringPushedPos = null;
		mStringDisabledPos = null;
		mNormalPaint = null;
		mTouchedPaint = null;
		mPushedPaint = null;
		mDisabledPaint = null;
	}


	public void Update() {
		if(mFlashEnabled == true)
		{
			long GameTime = System.currentTimeMillis();

			if(mReplyStartTime + mReplyDelay < GameTime)
			{
				if(mNormalFlashSprite != null)
				{
					mNormalFlashSprite.setReply(true);
					mNormalFlashSprite.setEnd(false);
				}
				if(mTouchedFlashSprite != null)
				{
					mTouchedFlashSprite.setReply(true);
					mTouchedFlashSprite.setEnd(false);
				}
				if(mPushedFlashSprite != null)
				{
					mPushedFlashSprite.setReply(true);
					mPushedFlashSprite.setEnd(false);
				}
				mReplyStartTime += mReplyDelay; 
			}
			else
			{
				if(mNormalFlashSprite != null)
					mNormalFlashSprite.setReply(false);
				if(mTouchedFlashSprite != null)
					mTouchedFlashSprite.setReply(false);
				if(mPushedFlashSprite != null)
					mPushedFlashSprite.setReply(false);
			}
			
			if(mNormalFlashSprite != null)
				mNormalFlashSprite.Update(GameTime);
			if(mTouchedFlashSprite != null)
				mTouchedFlashSprite.Update(GameTime);
			if(mPushedFlashSprite != null)
				mPushedFlashSprite.Update(GameTime);
		}
	}
	
	@Override
	public void Render(Canvas canvas)
	{
		// TODO Auto-generated method stub
		/*
		if(mVisiable == true)
		{
			if(mDisabled == false)
			{
				if(mPushed == false)
				{
					if(mTouched == false)
					{
						if(mChecked)
							canvas.drawBitmap(mCheckedBitmap, mPos.x, mPos.y, null);
						else
							canvas.drawBitmap(mUncheckedBitmap, mPos.x, mPos.y, null);
						
						if(mStringEnabled == true && mStringNormalPos != null && mNormalPaint != null)
						{
							canvas.drawText(mString, mPos.x+mStringNormalPos.x, mPos.y+mStringNormalPos.y, mNormalPaint);
						}
						
						if(mFlashEnabled == true && mNormalFlashSprite != null)
						{
							Rect dest = new Rect(mPos.x, 
									mPos.y,
									mPos.x + mNormalFlashSprite.m_bitmap.getWidth()/8,
									mPos.y + mNormalFlashSprite.m_bitmap.getHeight());
							
							canvas.drawBitmap(mNormalFlashSprite.m_bitmap, mNormalFlashSprite.mSRectangle, dest, null);	
						}
						
					}
					else // mTouched == true
					{
						if(mChecked)
							canvas.drawBitmap(mCheckedBitmap, mPos.x, mPos.y, null);
						else
							canvas.drawBitmap(mUncheckedBitmap, mPos.x, mPos.y, null);
						
						if(mStringEnabled == true && mStringTouchedPos != null && mTouchedPaint != null)
						{
							canvas.drawText(mString, mPos.x+mStringTouchedPos.x, mPos.y+mStringTouchedPos.y, mTouchedPaint);
						}
						
						if(mFlashEnabled == true  && mTouchedFlashSprite != null)
						{
							Rect dest = new Rect(mPos.x, 
									mPos.y,
									mPos.x + mTouchedFlashSprite.m_bitmap.getWidth()/8,
									mPos.y + mTouchedFlashSprite.m_bitmap.getHeight());
							
							canvas.drawBitmap(mTouchedFlashSprite.m_bitmap, mTouchedFlashSprite.mSRectangle, dest, null);	
						}
						
					}
				}
				else // mPushed == true
				{
					if(mChecked)
						canvas.drawBitmap(mCheckedBitmap, mPos.x, mPos.y, null);
					else
						canvas.drawBitmap(mUncheckedBitmap, mPos.x, mPos.y, null);
					
					if(mStringEnabled == true && mStringPushedPos != null && mPushedPaint != null)
					{
						canvas.drawText(mString, mPos.x+mStringPushedPos.x, mPos.y+mStringPushedPos.y, mPushedPaint);
					}
					
					if(mFlashEnabled == true && mPushedFlashSprite != null)
					{
						Rect dest = new Rect(mPos.x,
								mPos.y,
								mPos.x + mPushedFlashSprite.m_bitmap.getWidth()/8,
								mPos.y + mPushedFlashSprite.m_bitmap.getHeight());
						
						canvas.drawBitmap(mPushedFlashSprite.m_bitmap, mPushedFlashSprite.mSRectangle, dest, null);	
					}
				}
			}
			else // mDisabled == true
			{
				canvas.drawBitmap(mDisabledBitmapID, mPos.x, mPos.y, null);
				
				if(mStringEnabled == true && mStringDisabledPos != null && mDisabledPaint != null)
				{
					canvas.drawText(mString, mPos.x+mStringDisabledPos.x, mPos.y+mStringDisabledPos.y, mDisabledPaint);
				}
			}
		}*/

	}
	
	//@Override
		public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
			
			if(mVisiable == true)
			{
				if(mDisabled == false)
				{
					if(mPushed == false)
					{
						if(mTouched == false)
						{
							if(mChecked)
								spriteBatcher.drawBitmap2(mCheckedBitmap, mPos.x, mPos.y, null);
							else
								spriteBatcher.drawBitmap2(mUncheckedBitmap, mPos.x, mPos.y, null);
							
							//if(mStringEnabled == true && mStringNormalPos != null && mNormalPaint != null)
								//canvas.drawText(mString, mPos.x+mStringNormalPos.x, mPos.y+mStringNormalPos.y, mNormalPaint);
							
							if(mFlashEnabled == true && mNormalFlashSprite != null)
							{
								Rect dest = new Rect(mPos.x, 
										mPos.y,
										mPos.x + mNormalFlashSprite.m_bitmap.getWidth()/8,
										mPos.y + mNormalFlashSprite.m_bitmap.getHeight());
								
								spriteBatcher.drawBitmap2(mNormalFlashSprite.mBitmapID, mNormalFlashSprite.mSRectangle, dest, null);
							}
							
						}
						else // mTouched == true
						{
							if(mChecked)
								spriteBatcher.drawBitmap2(mCheckedBitmap, mPos.x, mPos.y, null);
							else
								spriteBatcher.drawBitmap2(mUncheckedBitmap, mPos.x, mPos.y, null);
							
							//if(mStringEnabled == true && mStringTouchedPos != null && mTouchedPaint != null)
								//canvas.drawText(mString, mPos.x+mStringTouchedPos.x, mPos.y+mStringTouchedPos.y, mTouchedPaint);
							
							if(mFlashEnabled == true  && mTouchedFlashSprite != null)
							{
								Rect dest = new Rect(mPos.x, 
										mPos.y,
										mPos.x + mTouchedFlashSprite.m_bitmap.getWidth()/8,
										mPos.y + mTouchedFlashSprite.m_bitmap.getHeight());
								
								spriteBatcher.drawBitmap2(mTouchedFlashSprite.mBitmapID, mTouchedFlashSprite.mSRectangle, dest, null);	
							}
							
						}
					}
					else // mPushed == true
					{
						if(mChecked)
							spriteBatcher.drawBitmap2(mCheckedBitmap, mPos.x, mPos.y, null);
						else
							spriteBatcher.drawBitmap2(mUncheckedBitmap, mPos.x, mPos.y, null);
						
						//if(mStringEnabled == true && mStringPushedPos != null && mPushedPaint != null)
							//canvas.drawText(mString, mPos.x+mStringPushedPos.x, mPos.y+mStringPushedPos.y, mPushedPaint);
						
						if(mFlashEnabled == true && mPushedFlashSprite != null)
						{
							Rect dest = new Rect(mPos.x,
									mPos.y,
									mPos.x + mPushedFlashSprite.m_bitmap.getWidth()/8,
									mPos.y + mPushedFlashSprite.m_bitmap.getHeight());
							
							spriteBatcher.drawBitmap2(mPushedFlashSprite.mBitmapID, mPushedFlashSprite.mSRectangle, dest, null);	
						}
					}
				}
				else // mDisabled == true
				{
					spriteBatcher.drawBitmap2(mDisabledBitmapID, mPos.x, mPos.y, null);
					
					//if(mStringEnabled == true && mStringDisabledPos != null && mDisabledPaint != null)
						//canvas.drawText(mString, mPos.x+mStringDisabledPos.x, mPos.y+mStringDisabledPos.y, mDisabledPaint);
				}
			}
			
		}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(mDisabled == true)
			return false;
		
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
			
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if(mTouchRect.contains(x, y))
			{
				Utility.m_click_sound.start();
				event.setEdgeFlags(1);
				
				if(mTouchedEnabled == true && mPushedEnabled == false)
				{
					mChecked = !mChecked;
					
					mTouched = true;
					return true;
				}
				else if(mTouchedEnabled == true && mPushedEnabled == true)
				{
					mTouched = true;
					return false;
				}
				else if(mTouchedEnabled == false && mPushedEnabled == true)
				{
					mPushed = !mPushed;
					return true;
				}
				else // mTouchedEnabled == false && mPushedEnabled == false
					return true;
			}
			else
				mTouched = false;
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			if(!mTouchRect.contains(x, y))
				mTouched = false;
			else
				event.setEdgeFlags(1);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP)
		{
			if(mTouchRect.contains(x, y))
			{
				event.setEdgeFlags(1);
				
				if(mTouched == true)
				{
					if( mPushedEnabled == true)
						mPushed = !mPushed;
					mTouched = false;
					return true;
				}
			}
			else
				mTouched = false;
		}
		
		return false;
	}

	public boolean isPushed() {
		return mPushed;
	}

	public boolean isPushedEnabled() {
		return mPushedEnabled;
	}

	public Point getPos() {
		return mPos;
	}
	
	public boolean isFlashEnabled() {
		return mFlashEnabled;
	}

	public CheckBox setVisiable(boolean visiable) {
		mVisiable = visiable;
		return this;
	}

	public void setDisabled(boolean disabled) {
		mDisabled = disabled;
	}
	
	public CheckBox setTouchedEnabled(boolean touchedEnabled) {
		mTouchedEnabled = touchedEnabled;
		return this;
	}

	public void setPushed(boolean pushed) {
		mPushed = pushed;
	}
	
	public void setPushedEnabled(boolean pushedEnabled) {
		mPushedEnabled = pushedEnabled;
	}

	public void setTouchedBitmap(int touchedBitmap) {
		mTouchedBitmap = touchedBitmap;
	}

	public void setPushedBitmap(int pushedBitmap) {
		mPushedBitmap = pushedBitmap;
	}

	public void setDisabledBitmap(int disabledBitmap) {
		mDisabledBitmapID = disabledBitmap;
	}

	public void setPos(Point pos) {
		mPos = pos;
	}

	public void setString(String string) {
		mString = string;
	}

	public void setStringEnabled(boolean stringEnabled) {
		mStringEnabled = stringEnabled;
	}

	public void setStringNormalPos(Point stringNormalPos) {
		mStringNormalPos = stringNormalPos;
	}

	public void setStringTouchedPos(Point stringTouchedPos) {
		mStringTouchedPos = stringTouchedPos;
	}

	public void setStringPushedPos(Point stringPushedPos) {
		mStringPushedPos = stringPushedPos;
	}

	public void setStringDisabledPos(Point stringDisabledPos) {
		mStringDisabledPos = stringDisabledPos;
	}

	public void setNormalPaint(Paint normalPaint) {
		mNormalPaint = normalPaint;
	}

	public void setTouchedPaint(Paint touchedPaint) {
		mTouchedPaint = touchedPaint;
	}

	public void setPushedPaint(Paint pushedPaint) {
		mPushedPaint = pushedPaint;
	}

	public void setDisabledPaint(Paint disabledPaint) {
		mDisabledPaint = disabledPaint;
	}

	public void setTouchRect(Rect touchRect) {
		mTouchRect = touchRect;
	}

	public void setFlashEnabled(boolean flashEnabled) {
		mFlashEnabled = flashEnabled;
	}

}
