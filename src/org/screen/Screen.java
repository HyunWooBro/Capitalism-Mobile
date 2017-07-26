package org.screen;

import android.graphics.*;
import android.util.*;

/**
 * layer을 여기서 관리하도록 수정해야 한다.
 * @author 김현준
 *
 */
public class Screen {
	private static int mScreenWidth;
	private static int mScreenHeight; 
	private static int mVirtualScreenWidth;
	private static int mVirtualScreenHeight;
	private static DisplayMetrics mDisplayMetircs;
	
	private static float mRatioX;
	private static float mRatioY;
	
	private static float mZoom = 1;
	//private static float mOneOverZoom = 1;
	
	// 싱글턴
	private static Screen s_instance;
	private Screen() {}
	public static Screen GetInstance(){
		if(s_instance == null){
			s_instance = new Screen();
		}
		return s_instance;
	}

	/**
	 * 문제는 실행할때마다 이 기준이 바뀌는 경우가 많다. 그래서 일단 긴 쪽을 찾아서 적절히
	 * 계산하도록 했다.
	 * @param screenWidth 화면 방향을 기준으로한 가로 방향의 길이
	 * @param screenHeight
	 * @param virtualWidth
	 * @param virtualHeight
	 * @param displayMetircs
	 */
	public void Init(int screenWidth , int screenHeight, int virtualWidth, int virtualHeight, DisplayMetrics displayMetircs){
		mScreenWidth 	= screenWidth;
		mScreenHeight 	= screenHeight;
		mVirtualScreenWidth = virtualWidth; 
		mVirtualScreenHeight = virtualHeight;
		mDisplayMetircs = displayMetircs;
		
		if(mScreenWidth > mScreenHeight)
		{
			mRatioX = (float)mScreenWidth/mVirtualScreenWidth;
			mRatioY = (float)mScreenHeight/mVirtualScreenHeight;
		}
		else
		{
			mRatioX = (float)mScreenHeight/mVirtualScreenWidth;
			mRatioY = (float)mScreenWidth/mVirtualScreenHeight;
		}
	}	
	
	public static void setSize(int width, int height){
		mVirtualScreenWidth = width;
		mVirtualScreenHeight = height;
		
		mRatioX = mScreenWidth/mVirtualScreenWidth;
		mRatioY = mScreenHeight/mVirtualScreenHeight;
	}	
	
	public static void setZoom(float zoom)
	{
		mZoom = zoom;
		//mOneOverZoom = 1/zoom;
	}
	
	public static float getZoom()
	{
		return mZoom;
	}
	
	public static void beginZoomRender(Canvas canvas)
	{
		canvas.save();
		if(mZoom != 1.0f)
			canvas.scale(0.7f, 0.7f);
	}
	
	public static void endZoomRender(Canvas canvas)
	{
		canvas.restore();
	}
	
	public static int touchX(int x){
		//return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x * mRatioY, mDisplayMetircs);
		//float Scale_X = 640f / 1280;
		
		return (int)(x * 1/mRatioX);
	}	
	
	public static int touchY(int y){
		//return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y * mRatioX, mDisplayMetircs);
		//float Scale_Y = 400f / 800;
		
		return (int)(y * 1/mRatioY);
	}
	
	public static int reverseTouchX(int x){
		//return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x * mRatioY, mDisplayMetircs);
		//float Scale_X = 640f / 1280;
		
		return (int)(x * mRatioX);
	}	
	
	public static int reverseTouchY(int y){
		//return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y * mRatioX, mDisplayMetircs);
		//float Scale_Y = 400f / 800;
		
		return (int)(y * mRatioY);
	}
	
	public static int zoomX(int x){
		//float Scale_X = 640f / 1280;

		return (int)(x * 1/mRatioX * mZoom);
	}	
	
	public static int zoomY(int y){
		//float Scale_Y = 400f / 800;

		return (int)(y * 1/mRatioY * mZoom);
	}
	
	public static float getRatioX(){
		return mRatioX;
	}
	
	public static float getRatioY(){
		return mRatioY;
	}
	
	public static int getVirtualScreenWidth() {
		return mVirtualScreenWidth;
	}
	
	public static int getVirtualScreenHeight() {
		return mVirtualScreenHeight;
	}

}
