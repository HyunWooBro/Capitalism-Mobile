package org.framework;

public class Collision {

	
	public static boolean CollisionCheckPointToBox(float px,float py,int bleft,int btop,int bright,int bbottom){
		if(bleft<px && bright > px && btop < py && bbottom > py)
			return true;
		
		return false;
	}

}