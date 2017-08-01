package core.framework.app;

import android.view.InputEvent;

import core.framework.app.AppManager.LifeCycleListener;
import core.scene.Director;

/**
 * 앱의 실행 과정에서 요구되는 콜백 메서드(생성, 렌더링, 이벤트 처리, 라이프사이클 처리)를 
 * 정의하기 위해서는 AppListener을 구현하여 {@link AppConfig#listener}에 등록해야 한다.</p>
 * 
 * 어떤 listener도 지정하지 않는다면 디폴트로 {@link Director}가 등록된다.</p> 
 * 
 * @author 김현우
 */
public interface AppListener extends LifeCycleListener {
	
	/** 앱리스너를 초기화할 때 */
	public void onCreate();
	
	@Override
	public void onResume();
	
	@Override
	public void onPause();
	
	@Override
	public void onDestroy();

	/** 앱리스너의 렌더링을 처리할 때 */
	public void onRender();
	
	/** 앱리스너가 입력이벤트를 받을 때 */
	public void onInputEvent(InputEvent event);
}
