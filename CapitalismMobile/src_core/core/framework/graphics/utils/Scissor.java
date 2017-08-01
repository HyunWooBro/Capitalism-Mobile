package core.framework.graphics.utils;

import android.opengl.GLES20;

import core.framework.graphics.OrthoCamera;
import core.math.Matrix3;
import core.math.Rectangle;
import core.math.Vector2;

public class Scissor {
	
	private static final Vector2 VECTOR = new Vector2();

	private Scissor() {
	}
	
	public static void begin(OrthoCamera camera, Matrix3 transformation, float x, float y, float width, float height) {
		final float[] rect = Rectangle.TMP_ARRAY;
		rect[0] = x;
		rect[1] = y + height;
		rect[2] = x + width;
		rect[3] = y;
		
		transformation.mapPoints(rect, 0, rect, 0, 2/*pair(s)*/);
		final Vector2 v = VECTOR;
		
		v.set(rect[0], rect[1]);
		camera.project(v);
		float sX = v.x;
		float sY = v.y;
		
		v.set(rect[2], rect[3]);
		camera.project(v);
		float sWidth = v.x - sX;
		float sHeight = v.y - sY;

		GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
		// glScissor은 다른 OpenGL 좌표계와 마찬가지로 좌측 하단을 원점으로 한다.
		GLES20.glScissor((int) sX, (int) sY, (int) sWidth, (int) sHeight);
	}
	
	public static void end() {
		GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
	}

}
