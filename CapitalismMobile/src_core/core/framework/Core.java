package core.framework;

import core.framework.app.AppManager;
import core.framework.audio.AudioManager;
import core.framework.graphics.GraphicsManager;
import core.framework.input.InputManager;
import core.framework.network.NetworkManager;

/**
 * core.framework를 구성하는 각 모듈(앱, 오디오, 그래픽, 입력, 네트워크)관리자의 
 * 정적상수 인스턴스를 제공한다.
 * 
 * @author 김현우
 */
public class Core {
	
	/** 코어 버전 */
	public static final float VERSION = 1.0f;
	

	/** 앱 관리자 */
	public static final AppManager APP = new AppManager();
	
	/** 오디오 관리자 */
	public static final AudioManager AUDIO = new AudioManager();
	
	/** 그래픽 관리자 */
	public static final GraphicsManager GRAPHICS = new GraphicsManager();
	
	/** 입력 관리자 */
	public static final InputManager INPUT = new InputManager();
	
	/** 네트워크 관리자 */
	public static final NetworkManager NETWORK = new NetworkManager();
}
