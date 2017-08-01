package core.framework.graphics.utils;

import android.opengl.GLES20;

import core.framework.graphics.OrthoCamera;

/**
 * 기기의 전체 스크린을 그대로 앱의 전체 화면으로 사용하는 뷰포트를 설정한다. 또한, 
 * 디폴트 카메라 및 가상 화면의 크기를 관리한다.
 * 
 * @author 김현우
 */
public class Viewport {
	
	public static final int[] VIEWPORT_RECT_ARRAY = new int[4];
	
	/** 스크린의 뷰포트 너비 */
	private int mScreenViewportWidth;
	/** 스크린의 뷰포트 높이 */
	private int mScreenViewportHeight; 
	
	/** 카메라의 디폴트 뷰포트 너비 */
	private float mCameraViewportWidth;
	/** 카메라의 디폴트 뷰포트 높이 */
	private float mCameraViewportHeight;
	
	/** Floor에서 사용할 디폴트 카메라. 따로 지정하지 않는 경우에 사용. */
	private OrthoCamera mDefaultCamera;
	
	public Viewport(float cameraWidth, float cameraHeight) {
		mCameraViewportWidth = cameraWidth;
		mCameraViewportHeight = cameraHeight;
		mDefaultCamera = new OrthoCamera(cameraWidth, cameraHeight);
	}
	
	public void update(int screenWidth, int screenHeight) {
		mScreenViewportWidth = screenWidth;
		mScreenViewportHeight = screenHeight;
		VIEWPORT_RECT_ARRAY[0] = 0;
		VIEWPORT_RECT_ARRAY[1] = 0;
		VIEWPORT_RECT_ARRAY[2] = screenWidth;
		VIEWPORT_RECT_ARRAY[3] = screenHeight;
		
		// 기기의 가로, 세로 길이를 그대로를 전체화면으로 사용한다. 원점은 기기의 좌측 하단이다.
		// 기기의 크기보다 작게 설정할 경우, 기기의 일부분 만큼을 앱 전체 화면으로 사용한다.
		// 기기의 크기보다 크게 설정할 경우, 앱 전체 화면의 일부를 기기의 화면이 처리한다. 
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		
		mDefaultCamera.update();
	}

	public int getScreenViewportWidth() {
		return mScreenViewportWidth;
	}

	public int getScreenViewportHeight() {
		return mScreenViewportHeight;
	}
	
	public float getCameraViewportWidth() {
		return mCameraViewportWidth;
	}

	public float getCameraViewportHeight() {
		return mCameraViewportHeight;
	}

	public OrthoCamera getDefaultCamera() {
		return mDefaultCamera;
	}
}
