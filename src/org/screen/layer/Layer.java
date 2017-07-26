package org.screen.layer;

import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;

import android.graphics.*;
import android.view.*;

public class Layer implements ILayerElement{
	private boolean mVisiable;
	private boolean mEventHandlingEnabled;
	private boolean mEventPassingEnabled;
	private boolean mBGColorEnabled;
	
	private int mBackGroundColor;
	
	private ArrayList<ILayerElement> mLayerElementList;

	public Layer() {
		// TODO Auto-generated constructor stub
		
		mLayerElementList = new ArrayList<ILayerElement>();
		
		mVisiable = false;
		mEventHandlingEnabled = true;
		mEventPassingEnabled = true;
	}
	
	public void AddElement(ILayerElement layerElement)
	{
		mLayerElementList.add(layerElement);
	}
	
	@Override
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
		for(int i=0; i<mLayerElementList.size(); i++)
			mLayerElementList.get(i).onDrawFrame(gl, spriteBatcher);
		
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		
		for(int i=0; i<mLayerElementList.size(); i++)
			mLayerElementList.get(i).Render(canvas);
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		for(int i=0; i<mLayerElementList.size(); i++)
			if(mLayerElementList.get(i).onTouchEvent(event))
				return true;
		
		return false;
	}

	public boolean isVisiable() {
		return mVisiable;
	}

	public void setVisiable(boolean visiable) {
		mVisiable = visiable;
	}

	public boolean isEventHandlingEnabled() {
		return mEventHandlingEnabled;
	}

	public void setEventHandlingEnabled(boolean eventHandlingEnabled) {
		mEventHandlingEnabled = eventHandlingEnabled;
	}

	public boolean isEventPassingEnabled() {
		return mEventPassingEnabled;
	}

	public void setEventPassingEnabled(boolean eventPassingEnabled) {
		mEventPassingEnabled = eventPassingEnabled;
	}
	
}
