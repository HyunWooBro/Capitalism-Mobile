package org.screen.layer;

import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.framework.openGL.*;
import org.game.*;
import org.screen.layer.component.*;
import org.screen.layer.window.*;

import android.graphics.*;
import android.util.*;
import android.view.*;

/**
 * 스크린에 그려지거나 이벤트를 받는 과정은 LayerManager을 통해서 이루어진다.
 * 그려지는 과정은 가장 낮은 단계의 layer부터 그려지며, 이벤트는 가장 높은 단계의 
 * layer부터 순서대로 전달된다.
 * 
 * @author 김현준
 *
 */
public class LayerManager {
	private Vector<Layer> mLayerVector;

	public LayerManager(int size) {
		// TODO Auto-generated constructor stub
		
		mLayerVector = new Vector<Layer>();

		for(int i=0; i<size; i++)
			mLayerVector.add(new Layer());
		
	}
	
	public void AddElement(int index, ILayerElement layerElement) {
		
		mLayerVector.get(index).AddElement(layerElement);
	}
	
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
		for(int i=0; i<mLayerVector.size(); i++)
		{
			// Layer의 기타 속성 처리
			if(mLayerVector.get(i).isVisiable() == true)
			{
				//Log.e(Utility.TAG, "inter onDrawFrame reached");
				
				mLayerVector.get(i).onDrawFrame(gl, spriteBatcher);
			}
		}
		
	}
	
	public void Render(Canvas canvas)
	{
		for(int i=0; i<mLayerVector.size(); i++)
		{
			// Layer의 기타 속성 처리
			if(mLayerVector.get(i).isVisiable() == true)
			{
				mLayerVector.get(i).Render(canvas);
			}
		}
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		for(int i=mLayerVector.size()-1; i>-1; i--)
		{
			if(mLayerVector.get(i).isEventHandlingEnabled() == true)
				if(mLayerVector.get(i).onTouchEvent(event))
					return true;
			
			// event를 여기서 정지시키면 더 이상 전달하지 않고 리턴
			if(mLayerVector.get(i).isEventPassingEnabled() == false)
				return true;
		}
		
		return false;
	}

	public Vector<Layer> getLayerVector() {
		return mLayerVector;
	}

}
