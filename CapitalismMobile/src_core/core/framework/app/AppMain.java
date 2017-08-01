package core.framework.app;

import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import core.framework.Core;
import core.framework.app.AppManager.LifeCycleListener;
import core.scene.Director;
import core.utils.SnapshotArrayList;

/** 
 * C언어에서 모든 코드가 메인(main)함수에서 시작하듯이 이 클래스를 상속한 후 
 * {@link #onCreate(Bundle)}를 재정의하여 앱의 메인엔트리(main entry)로 
 * 사용한다. {@link #init(AppConfig)}을 통해 렌더링 스레드를 시작할 수 있으며 
 * {@link AppConfig}을 통해 앱의 다양한 옵션도 설정할 수 있다.
 * 
 * @author 김현우
 */
public class AppMain extends Activity {
	
	private static final String TAG = AppMain.class.getSimpleName();
	
	private boolean mFirstResume = true;
	
	/** 동기화용 객체 */
	private Object mSync = new Object();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	Core.APP.mActivity = this;
    	Core.APP.mResources = getResources();
    	Core.APP.mUIThread = Thread.currentThread();
    	
    	Core.APP.addLifeCycleListener(Core.GRAPHICS);
    	Core.APP.addLifeCycleListener(Core.AUDIO);
    }
	
	/** 지정한 config에 따라 앱을 설정하고 렌더링스레드를 시작한다. */
	protected void init(AppConfig config) {
		Core.APP.mAppConfig = config;
		
		Window window = getWindow();
		
		if(config.hideTitleBar)
			window.requestFeature(Window.FEATURE_NO_TITLE);
		
		if(config.isFullScreen) 
			window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		if(config.keepScreenOn) 
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		switch(config.screenOrientation) {
			case LANDSCAPE:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case PORTRAIT:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
			case REVERSE_LANDSCAPE:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				break;
			case REVERSE_PORTRAIT:
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				break;
		}
		
		AppListener listener = (config.listener == null)? Director.getInstance() : config.listener;
		Core.APP.mListener = listener;
		Core.APP.addLifeCycleListener(listener);
		
		startRenderer(config.view);
	}
	
	/** 
	 * 지정한 view로 렌더링스레드를 시작한다. 지정한 view가 null일 경우 
	 * 기본 {@link GLSurfaceView}를 생성하여 사용한다.
	 */
	private void startRenderer(GLSurfaceView view) {
		GLSurfaceView GLview = (view == null)? new GLSurfaceView(this) : view;
		Core.APP.mView = GLview;

    	// 키입력처리를 받기위해서
		GLview.setFocusable(true);
		GLview.setFocusableInTouchMode(true);
		GLview.requestFocus();
		GLview.requestFocusFromTouch();

    	// View에 대한 입력이벤트 처리는 Core.input이 담당한다.
		GLview.setOnKeyListener(Core.INPUT);
		GLview.setOnTouchListener(Core.INPUT);
    	
    	// openGL ES 2.0 사용
		GLview.setEGLContextClientVersion(2);
		
		// 2D 전용이므로 깊이 버퍼를 사용하지 않는다. 
		GLview.setEGLConfigChooser(false);
		
		// 앱이 pause되더라도 EGLContext를 유지하도록 한다. 유지된다면 앱이 resume될 때 
		// GraphicsManager의 onSurfaceCreated(...)가 호출되지 않고, 유지되지 않는다면 
		// onSurfaceCreated(...)가 호출되어 다시 자원을 로드해야 한다.
		GLview.setPreserveEGLContextOnPause(true);
    	
    	// View에 대한 렌더링은 Core.graphics가 담당한다.
		GLview.setRenderer(Core.GRAPHICS);
		
		Core.APP.runOnGLThread(new Runnable() {
			
			@Override
			public void run() {
				// 렌더링 스레드에서 스레드의 인스턴스를 얻는다.
				Core.APP.mGLThread = Thread.currentThread();
			}
		});

		setContentView(GLview);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		Core.APP.debug(TAG, "onResume called");
		
		if(mFirstResume) {
			mFirstResume = false;
			return;
		}

		Core.APP.runOnGLThread(0, Integer.MIN_VALUE, new Runnable() {
			
			@Override
			public void run() {
				SnapshotArrayList<LifeCycleListener> lifeCycleList = Core.APP.getLifeCycleListenerList();
				synchronized(lifeCycleList) {
					ListIterator<LifeCycleListener> it = lifeCycleList.begin();
					while(it.hasNext()) {
						LifeCycleListener listener = it.next();
						listener.onResume();
					}
					lifeCycleList.end(it);
				}
			}
		});
		
		// 렌더링스레드를 resume하기 위해 반드시 불러야 한다.
		((GLSurfaceView) Core.APP.getView()).onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Core.APP.debug(TAG, "onPause called");
		
		synchronized(mSync) {
			Core.APP.runOnGLThread(0, Integer.MIN_VALUE, new Runnable() {
				
				@Override
				public void run() {
					SnapshotArrayList<LifeCycleListener> lifeCycleList = Core.APP.getLifeCycleListenerList();
					synchronized(lifeCycleList) {
						ListIterator<LifeCycleListener> it = lifeCycleList.begin(lifeCycleList.size());
						while(it.hasPrevious()) {
							LifeCycleListener listener = it.previous();
							listener.onPause();
						}
						lifeCycleList.end(it);
					}

					// 앱이 종료되는 경우라면 destroy까지 호출한다.
					if(isFinishing()) {
						synchronized(lifeCycleList) {
							ListIterator<LifeCycleListener> it = lifeCycleList.begin(lifeCycleList.size());
							while(it.hasPrevious()) {
								LifeCycleListener listener = it.previous();
								listener.onDestroy();
							}
							lifeCycleList.end(it);
						}
					}
					
					// 렌더링스레드에서 모든 처리가 마무리 되면 UI스레드를 깨운다.
					synchronized(mSync) {
						mSync.notifyAll();
					}
				}
			});
			
			// GlsurfaceView의 onPause()를 호출하기 전에 렌더링스레드에서 이벤트가 
			// 처리되도록 대기한다.
			try {
				mSync.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// 렌더링스레드를 pause하기 위해 반드시 불러야 한다.
		((GLSurfaceView) Core.APP.getView()).onPause();
		
		// 앱을 종료할 경우 리소스를 완전히 해제한다.
		if(isFinishing()) {
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(getPackageName());
			System.exit(0);
		}
	}

}
