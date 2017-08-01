package core.framework.app;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import core.framework.Core;
import core.framework.graphics.GraphicsManager;
import core.utils.SnapshotArrayList;

/**
 * 플랫폼인 안드로이드와 관련한 앱에 대한 전반적인 관리 및 유용한 기능(로그, 진동, 토스트 등)의 
 * 래퍼를 제공한다.</p>
 * 	<ul>
 * 		<li>Activity, View 등의 앱의 기본 자원 관리
 * 		<li>res폴더의 자원을 관리하는 Resources 제공
 * 		<li>Preference 관리
 * 		<li>현재 기기의 안드로이드 API버전 제공
 * 		<li>앱버전 확인 제공
 * 		<li>UI스레드와 렌더링스레드 관리
 * 		<li>커스텀ID 생성 및 관리
 * 		<li>라이프사이클리스너 관리
 * 		<li>진동, 토스트, 다이얼로그 래퍼 제공
 * 		<li>로그 래퍼 제공
 * 		<li>앱종료
 * 	</ul>
 * 
 * @author 김현우
 */
public class AppManager {
	
	public static final int INIT_CUSTOM_ID = 1000;
	
	/*package*/ AppListener mListener;
	
	/*package*/ AppConfig mAppConfig;

	/*package*/ View mView;
	/*package*/ Activity mActivity;
	/*package*/ Resources mResources;
	
	/*package*/ Thread mUIThread;
	/*package*/ Thread mGLThread;
	
	private PackageInfo mPackageInfo;
	
	private List<ReservedEvent> mReservedEventList = new ArrayList<ReservedEvent>();
	
	private SnapshotArrayList<LifeCycleListener> mLifeCycleListenerList = new SnapshotArrayList<LifeCycleListener>();
	
	private Vibrator mVibrator;

	private Toast mToast;
	private ToastOptions mDefaultToastOptions;
	
	/** 현재 커스텀 아이디. */
	private int mCustomID = INIT_CUSTOM_ID;
	
	/** 로그 레벨 */
	private int mLogLevel = LogLevel.DEBUG;
	private String mTag = Core.class.getSimpleName();

	public AppManager() {
	}
	
	/** 앱의 리스너를 얻는다. */
	public AppListener getAppListener(){
		return mListener;
	}
	
	/** 앱의 Config를 얻는다. */
	public AppConfig getAppConfig() {
		return mAppConfig;
	}
	
	/** 앱의 View를 얻는다. 필요에 따라 하위 클래스(GLSurfaceView 등)로 캐스팅 할 수 있다. */
	public View getView(){
		return mView;
	}
	
	/** 앱의 Activity를 얻는다. 필요에 따라 하위 클래스(AppMain 등)로 캐스팅 할 수 있다. */
	public Activity getActivity(){
		return mActivity;
	}
	
	/** 앱의 {@link Resources}를 얻는다. */
	public Resources getResources(){
		return mResources;
	}

	/** 
	 * 디폴트("{@link Preference}") 이름으로 앱의 {@link Preference}를 얻는다. 
	 * 메서드를 호출할 때마다 객체가 생성되므로 필요에 따라 리턴된 인스턴스를 
	 * 보관하라. 여러 객체가 생성되더라도 내부 내용은 서로 공유된다.
	 */
	public Preference getPreference() {
		return new Preference();
	}
	
	/** 
	 * 지정한 이름으로 앱의 {@link Preference}를 얻는다. 메서드를 호출할 때마다 
	 * 객체가 생성되므로 필요에 따라 리턴된 인스턴스를 보관하라. 여러 객체가 
	 * 생성되더라도 이름이 같은 객체들의 내부 내용은 서로 공유된다.
	 */
	public Preference getPreference(String name) {
		return new Preference(name);
	}
	
	/** 현재 기기의 API버전을 구한다. */
	public int getAPIVersion() {
		return Build.VERSION.SDK_INT;
	}
	
	private void ensurePackageInfo() {
		if(mPackageInfo == null) {
			Activity activity = mActivity;
			try {
				mPackageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** AndroidManifest.xml에 정의한 버전코드를 구한다. */
	public int getVersionCode() {
		ensurePackageInfo();
		return mPackageInfo.versionCode;
	}

	/** AndroidManifest.xml에 정의한 버전이름을 구한다. */
	public String getVersionName() {
		ensurePackageInfo();
		return mPackageInfo.versionName;
	}
	
	/** 현재 스레드가 UI스레드인지 조사한다. */
	public boolean isUIThread() {
		return mUIThread == Thread.currentThread();
	}
	
	/** 현재 스레드가 렌더링스레드인지 조사한다. */
	public boolean isGLThread() {
		return mGLThread == Thread.currentThread();
	}
	
	/**
	 * 다른 스레드로부터 UI스레드에서 실행할 작업을 전달한다. 내부적으로  
	 * {@link Activity#runOnUiThread(Runnable)}을 호출한다.
	 * 
	 * @param runnable
	 */
	public void runOnUIThread(Runnable runnable) {
		mActivity.runOnUiThread(runnable);
	}
	
	/**
	 * 다음 렌더링 프레임에 실행할 이벤트를 지정한다. 일반적으로 렌더링스레드가 아닌 
	 * 다른 스레드에서 렌더링스레드와 통신하기 위해 사용된다.</p>
	 * 
	 * 이 메서드는 반드시 렌더링스레드가 시작된 후 호출할 수 있다. 내부적으로 
	 * {@link GLSurfaceView#queueEvent(Runnable)}를 호출한다. 등록된 이벤트는 
	 * {@link GraphicsManager#onDrawFrame(GL10)}가 호출되기 직전에 처리된다.</p>
	 * 
	 * surface가 생성되기 전에도 Runnable이 실행될 수 있다는 것을 주의해야 한다.</p>
	 * 
	 * Runnable에서 또 다른 {@link #runOnGLThread(Runnable)}를 호출하는 경우 
	 * 다음 렌더링 프레임이 아닌 같은 프레임에서 처리된다.</p>
	 * 
	 * @param runnable
	 */
	public void runOnGLThread(Runnable runnable) {
		((GLSurfaceView) mView).queueEvent(runnable);
	}
	
	/**
	 * 현재 렌더링 프레임으로부터 {@code nextFrameCount}의 프레임 만큼 이후에 실행할 
	 * 이벤트를 지정한다. 일반적으로 렌더링스레드가 아닌 다른 스레드에서 렌더링스레드와 
	 * 통신하기 위해 사용된다.</p>
	 * 
	 * {@link #runOnGLThread(Runnable)}과는 달리 렌더링스레드가 시작하지 않아도 
	 * 호출할 수 있다. 등록된 이벤트는 {@link GraphicsManager#onDrawFrame(GL10)}가 호출된 
	 * 직후에 처리된다. 따라서 surface가 생성된 이후에만 Runnable이 실행되는 것을 보장한다.</p>
	 * 
	 * @param nextFrameCount
	 * @param runnable
	 */
	public void runOnGLThread(int nextFrameCount, Runnable runnable) {
		runOnGLThread(mReservedEventList.size(), nextFrameCount, runnable);
	}
	
	/*package*/ void runOnGLThread(int index, int nextFrameCount, Runnable runnable) {
		synchronized(mReservedEventList) {
			mReservedEventList.add(index, new ReservedEvent(
					nextFrameCount + Core.GRAPHICS.getAccumulatedFrameCount(), 
					runnable));
		}
	}
	
	/** 
	 * 현재 프레임에 실행될 이벤트 {@link Runnable}객체를 얻는다. 이 메서드를 유저가 
	 * 직접 호출할 경우는 없다. 
	 */
	public Runnable getCurrentFrameEvent() {
		List<ReservedEvent> eventList = mReservedEventList;
		int n = eventList.size();
		for(int i=0; i<n ;i++) {
			ReservedEvent e = eventList.get(i);
			if(e.frameCount <= Core.GRAPHICS.getAccumulatedFrameCount()) {
				Runnable runnable = e.runnable;
				synchronized(eventList) {
					eventList.remove(i);
				}
				return runnable;
			}
		}
		return null;
	}
	
	/** 
	 * 다용도로 사용할 수 있는 커스텀 아이디를 생성하여 리턴한다. 생성한 아이디는 
	 * 중복되지 않는다. 어느 스레드에서나 호출 가능하다.
	 */
	public int genCustomID() {
		synchronized(this) {
			return mCustomID++;
		}
	}
	
	public boolean addLifeCycleListener(LifeCycleListener listener) {
		synchronized(mLifeCycleListenerList) {
			if(!mLifeCycleListenerList.contains(listener)) return mLifeCycleListenerList.add(listener);
			return false;
		}
	}
	
	/** 라이프사이클리스너의 콜백 메서드에서 이 메서드를 호출할 경우 문제가 발생할 수 있다. */
	public boolean removeLifeCycleListener(LifeCycleListener listener) {
		synchronized(mLifeCycleListenerList) {
			return mLifeCycleListenerList.remove(listener);
		}
	}
	
	public SnapshotArrayList<LifeCycleListener> getLifeCycleListenerList(){
		return mLifeCycleListenerList;
	}
	
	private void ensureVibrator() {
		if(mVibrator == null)
			mVibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	/**
	 * 지정한 지속시간 동안 기기를 진동시킨다. 호출하기 전에 
	 * {@link android.Manifest.permission#VIBRATE} 퍼미션이 요구된다.
	 */
	public void vibrate(long millisec) {
		ensureVibrator(); 
		mVibrator.vibrate(millisec);
	}
	
	/** 반복 패턴을 지정하여 진동시킨다. */
	public void vibrate(long[] pattern, int repeat) {
		ensureVibrator();
		mVibrator.vibrate(pattern, repeat);
	}
	
	/** 진동을 취소한다. */
	public void cancelVibration() {
		if(mVibrator != null) mVibrator.cancel();
	}

	/**
	 * 입력된 text을 duration만큼 출력한다. 정해진 duration이 지나기 이전에 이 메서드를 
	 * 다시 한번 호출하면 새로운 text로 바로 갱신된다. UI스레드에서 실행된다.</p>
	 * 
	 * 내부적으로 {@link #showToast(String, int, ToastOptions)}를 호출하며 옵션은 
	 * 모두 디폴트 값을 사용한다.</p>
	 */
	public void showToast(String text, int duration) {
		showToast(text, duration, null);
	}
	
	/**
	 * 입력된 text을 duration만큼 출력한다. 정해진 duration이 지나기 이전에 이 메서드를 
	 * 다시 한번 호출하면 새로운 text로 바로 갱신된다. UI스레드에서 실행된다.</p>
	 * 
	 * text와 duration 이외에 gravity, offset, margin, view 등의 옵션을 {@link ToastOptions}을 통해 
	 * 추가적으로 설정할 수 있다. 지정하지 않은 옵션은 디폴트 값이 사용되므로 관심 있는 옵션만 
	 * 설정할 수 있다.</p>
	 */
	public void showToast(final String text, final int duration, final ToastOptions options) {
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(mToast == null) {
					mToast = Toast.makeText(mActivity, "", Toast.LENGTH_SHORT);
					mDefaultToastOptions = new ToastOptions();
					mDefaultToastOptions.gravity = mToast.getGravity();
					mDefaultToastOptions.offsetX = mToast.getXOffset();
					mDefaultToastOptions.offsetY = mToast.getYOffset();
					mDefaultToastOptions.horizontalMargin = mToast.getHorizontalMargin();
					mDefaultToastOptions.verticalMargin = mToast.getVerticalMargin();
					mDefaultToastOptions.view = mToast.getView();
				}
				
				Toast toast = mToast;
				toast.setText(text);
				toast.setDuration(duration);
				
				ToastOptions defaultOptions = mDefaultToastOptions;
				if(options != null) {
					toast.setGravity(
							(options.gravity == null)? defaultOptions.gravity : options.gravity, 
							(options.offsetX == null)? defaultOptions.offsetX : options.offsetX, 
							(options.offsetY == null)? defaultOptions.offsetY : options.offsetY);
					toast.setMargin(
							(options.horizontalMargin == null)? defaultOptions.horizontalMargin : options.horizontalMargin, 
							(options.verticalMargin == null)? defaultOptions.verticalMargin : options.verticalMargin);
					toast.setView((options.view == null)? defaultOptions.view : options.view);
				} else {
					toast.setGravity(defaultOptions.gravity, defaultOptions.offsetX, defaultOptions.offsetY);
					toast.setMargin(defaultOptions.horizontalMargin, defaultOptions.verticalMargin);
					toast.setView(defaultOptions.view);
				}
				toast.show();
			}
		});
	}

	/** 
	 * {@link AlertDialog}를 출력한다. 내부적으로 매개변수로 넘어온 builder의 
	 * {@link AlertDialog.Builder#show()}를 호출한다. UI스레드에서 실행된다.</p>
	 * 
	 * AlertDialog에 대한 리스너를 등록하는 경우, 명시적으로 렌더링 스레드에서 
	 * 실행되도록 하지 않는다면 UI스레드에서 실행되므로 주의해야 한다.</p>
	 */
	public void showAlertDialog(final AlertDialog.Builder builder) {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				builder.show();
			}
		});
	}
	
	public String getDefaultLogTag() {
		return mTag;
	}
	
	public void setDefaultLogTag(String tag) {
		mTag = tag;
	}
	
	public int getMinLogLevel() {
		return mLogLevel;
	}
	
	/** 
	 * 최소 로그레벨을 지정한다. 로그레벨은 DEBUG, INFO, WARN, ERROR 순으로 낮아진다.
	 * 지정한 최소 로그레벨보다 같거나 낮은 로그만 출력된다. 기본적으로 DEBUG 수준으로 
	 * 지정되어 있다. 어떠한 로그도 출력되지 않기를 원한다면 NONE 수준으로 설정하라.
	 * 
	 * @see LogLevel
	 */
	public void setMinLogLevel(int logLevel) {
		mLogLevel = logLevel;
	}
	
	public void debug(String content) {
		debug(mTag, content);
	}
	
	public void debug(String tag, String content) {
		if(mLogLevel >= LogLevel.DEBUG) Log.d(tag, content);
	}
	
	public void info(String content) {
		info(mTag, content);
	}
	
	public void info(String tag, String content) {
		if(mLogLevel >= LogLevel.INFO) Log.i(tag, content);
	}
	
	public void warn(String content) {
		warn(mTag, content);
	}
	
	public void warn(String tag, String content) {
		if(mLogLevel >= LogLevel.WARN) Log.w(tag, content);
	}
	
	public void error(String content) {
		error(mTag, content);
	}
	
	public void error(String tag, String content) {
		if(mLogLevel >= LogLevel.ERROR) Log.e(tag, content);
	}
	
	/** 
	 * App을 종료한다. AppMain의 onPause()와 onDestroy()가 호출되어 모든 리소스를
	 * 정리한 후 종료된다. 
	 */
	public void exit() {
		mActivity.moveTaskToBack(true);
		mActivity.finish();
	}
	
	/**
	 * 앱의 라이프사이클({@link Activity#onResume()}과 {@link Activity#onPause()})에 
	 * 대한 리스너의 메서드의 원형을 정의한다. resume()은 등록된 순서대로, pause()와  
	 * destroy()는 등록된 순서의 반대로 호출된다.
	 *  
	 * @author 김현우
	 */
	public static interface LifeCycleListener {
		
		/** AppMain의 onResume()이 호출될 때(앱시작 직후는 제외) */
		public void onResume();
		
		/** AppMain의 onPaure()가 호출될 때 */
		public void onPause();
		
		/** AppMain의 onPaure()가 호출되고 isFinishing()이 true일 때 */
		public void onDestroy();
	}
	
	/** 토스트에 대한 옵션을 지정한다. 설정하지 않는 옵션은 디폴트 값이 사용된다. */
	public static class ToastOptions {
		public Integer gravity;
		public Integer offsetX;
		public Integer offsetY;
		public Float horizontalMargin;
		public Float verticalMargin;
		public View view;
	}

	private static class ReservedEvent {
		public int frameCount;
		public Runnable runnable;
		
		public ReservedEvent(int frameCount, Runnable runnable) {
			this.frameCount = frameCount;
			this.runnable = runnable;
		}
	}
	
	public static class LogLevel {
		public static final int DEBUG 	= 4;
	    public static final int INFO 		= 3;
	    public static final int WARN 		= 2;
	    public static final int ERROR 	= 1;
	    public static final int NONE 		= 0;
	}

}
