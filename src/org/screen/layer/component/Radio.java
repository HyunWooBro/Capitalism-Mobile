package org.screen.layer.component;

import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;

import android.graphics.*;
import android.view.*;

public class Radio {
	private ArrayList<Button> mButtonList;
	
	private int mSelectIndex;

	public Radio() {
		// TODO Auto-generated constructor stub
		mButtonList = new ArrayList<Button>();
	}
	
	public Radio Add(Button button)
	{
		button.setPushedEnabled(true);
		mButtonList.add(button);
		return this;
	}
	
	public void Render(Canvas canvas)
	{
		for(int i=0; i<mButtonList.size(); i++)
			mButtonList.get(i).Render(canvas);
	}
	
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher)
	{
		for(int i=0; i<mButtonList.size(); i++)
			mButtonList.get(i).onDrawFrame(gl, spriteBatcher);
	}
	
	public boolean onTouchEvent(MotionEvent event) 
	{
		for(int i=0; i<mButtonList.size(); i++)
		{
			if(mButtonList.get(i).onTouchEvent(event))
			{
				setSelectIndex(i);
				return true;
			}
		}
		
		return false;
	}

	public int getSelectIndex() {
		return mSelectIndex;
	}

	public Radio setSelectIndex(int selectIndex) {
		mSelectIndex = selectIndex;
		
		for(int i=0; i<mButtonList.size(); i++)
		{
			mButtonList.get(i).setPushed(false);
		}
		
		mButtonList.get(selectIndex).setPushed(true);
		
		return this;
	}

}
