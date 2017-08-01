package core.framework.app;

import android.opengl.GLSurfaceView;

import core.scene.Director;

/**
 * 앱의 설정을 지정한다.
 * 
 * @author 김현우
 */
public class AppConfig {
	
	public enum ScreenOrientation{
		LANDSCAPE, 
		PORTRAIT, 
		REVERSE_LANDSCAPE, 
		REVERSE_PORTRAIT, 
	}
	
	/** 
	 * 앱의 타이틀바를 숨길 것인지? (디폴트 : true) 
	 */
	public boolean hideTitleBar = true;
	
	/** 
	 * 앱이 전체화면인가? (디폴트 : true) 
	 */
	public boolean isFullScreen = true;
	
	/** 
	 * 앱이 전면에서 실행되는 동안 화면이 항상 밝도록 유지할 것인가? (디폴트 : true) 
	 */
	public boolean keepScreenOn = true;
	
	/** 
	 * 앱의 방향. (디폴트 : ScreenOrientation.PORTRAIT) 
	 */
	public ScreenOrientation screenOrientation = ScreenOrientation.PORTRAIT;
	
	/** 
	 * 앱의 가상 화면 너비. (디폴트 : -1f)</p>
	 * 
	 * 디폴트 값을 그대로 사용할 경우 기기의 너비가 대신 사용된다.</p>
	 */
	public float virtualWidth = -1f;
	/** 
	 * 앱의 가상 화면 높이. (디폴트 : -1f)</p>
	 * 
	 * 디폴트 값을 그대로 사용할 경우 기기의 높이가 대신 사용된다.</p>
	 */
	public float virtualHeight = -1f;
	
	/** 
	 * 커스텀 AppListener 유무. (디폴트 : null)</p> 
	 * 
	 * null이면 {@link Director}가 사용된다.</p>
	 */
	public AppListener listener;
	
	/** 
	 * 커스텀 GLSurfaceView 유무. (디폴트 : null)</p>
	 * 
	 * null이면 기본 {@link GLSurfaceView}가 사용된다.</p>
	 */
	public GLSurfaceView view;
}