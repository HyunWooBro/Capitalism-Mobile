package org.framework;

import android.graphics.Rect;

// 지우기
public class CollisionManager {
	public static boolean CheckBoxToBox(Rect _rt1,Rect _rt2){
		if(_rt1.intersect(_rt2))
			return true;
		return false;
	}
	
}
