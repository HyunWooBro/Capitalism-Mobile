package core.framework.graphics;

import java.util.List;
import java.util.ListIterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;

import core.framework.Core;
import core.framework.app.AppConfig;
import core.framework.app.AppManager.LifeCycleListener;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.utils.GLReloadable;
import core.framework.graphics.utils.Viewport;
import core.math.Rectangle;
import core.math.Vector2;
import core.utils.SnapshotArrayList;

/**
 * 유저에게 스크린을 통해 출력하는 그래픽 작업을 전반적으로 관리한다.
 *  <ul>
 * 		<li>GLSurface의 렌더러를 담당하며 렌더링 처리
 * 		<li>입력이벤트 등의 동기화 제공
 * 		<li>렌더링관련 시간정보 및 FPS 제공
 * 		<li>비트맵 생성 및 정보 제공
 * 		<li>OpenGL 자원의 reload 관리
 * 		<li>뷰포트 관리
 * 	</ul>
 * 
 * @author 김현우
 */
public class GraphicsManager implements Renderer, LifeCycleListener {
	
	public static final String GL_NO_ERROR = "GL_NO_ERROR";
	public static final String GL_INVALID_ENUM = "GL_INVALID_ENUM";
	public static final String GL_INVALID_VALUE = "GL_INVALID_VALUE";
	public static final String GL_INVALID_OPERATION = "GL_INVALID_OPERATION";
	public static final String GL_INVALID_FRAMEBUFFER_OPERATION = "GL_INVALID_FRAMEBUFFER_OPERATION";
	public static final String GL_OUT_OF_MEMORY = "GL_OUT_OF_MEMORY";
	
	private static final String TAG = GraphicsManager.class.getSimpleName();
	
	private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();
	
	static {
		OPTIONS.inScaled = false;
	}
	
	private static final int[] TMP_PARAMS = new int[1];
	
	private boolean mRunning = true;
	
	/** 앱을 시작한 후 각 프레임마다 1씩 증가되는 카운터 */
	private int mAccumulatedFrameCount;
	
	/** FPS측정을 위해 1초동안 증가되고 리셋되는 프레임 카운터 */
	private int mFrameCount;
	/** FPS(Frame Per Second) */
	private int mFPS;
	private long mNextFPSTime;
	
	private long mCurrentTime;
	private long mLastTime;
	private long mDeltaTime;
	
	private SnapshotArrayList<GLReloadable> mGLReloadableList = new SnapshotArrayList<GLReloadable>();
	
	/** 이전 프레임에서 호출된 glDraw...(...) 개수 */
	private int mLastDrawCount;
	private int mDrawCount;
	
	/** 텍스쳐 관리자 */
	private TextureManager mTextureManager = new TextureManager();
	
	private Viewport mViewport;
	
	private boolean mFirstCreated = true;
	
	private Canvas mCanvas = new Canvas();

	public GraphicsManager() {
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// 이 메서드는 GLSurfaceView의 surfaceCreated(SurfaceHolder)가 호출되어야 
		// 불려진다. 안드로이드의 내부 구조가 숨겨져 있어 surfaceCreated(SurfaceHolder)가 
		// 언제 호출되는지는 정확하게는 모르겠지만 Activity의 onResume() 이후에 호출되는 
		// 것은 명확하다. 예전에 고민했던 Activity의 onCreate(..)와는 직접적인 관련이 없다.
		
		Core.APP.debug(TAG, "onSurfaceCreated called");
		
		// 이 기기의 openGL 지원 버전을 출력
		logGLInfo();
		
		// 기본적으로 검은색으로 바탕을 초기화한다.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// surface가 생성되면 자원을 다시 로드한다. 기본적으로 GLSurfaceView의 
		// setPreserveEGLContextOnPause를 true로 세팅하기는 했지만 적용되지 않는 
		// 예외적인 상황에서는 다시 로드해야 한다.
		reload();
		
		mLastTime = SystemClock.uptimeMillis();
		
		Core.APP.warn(TAG, "error in onSurfaceCreated : " + getErrorCode());
	}
	
	private void logGLInfo() {
		Core.APP.info("OpenGL vendor : " + GLES20.glGetString(GLES20.GL_VENDOR));
		Core.APP.info("OpenGL renderer : " + GLES20.glGetString(GLES20.GL_RENDERER));
		Core.APP.info("supported OpenGL version : " + GLES20.glGetString(GLES20.GL_VERSION));
		Core.APP.info("supported GLSL version : " + GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION));
		Core.APP.info("supported extenstions : " + GLES20.glGetString(GLES20.GL_EXTENSIONS));
	}
	
	private void reload() {
		ListIterator<GLReloadable> it = mGLReloadableList.begin();
		while(it.hasNext()) {
			GLReloadable reloadable = it.next();
			reloadable.reload();
		}
		mGLReloadableList.end(it);
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		Core.APP.debug(TAG, "onSurfaceChanged called");
		
		// onSurfaceChanged는 surface가 생성될 때 또는 화면이 회전될 때 호출된다.
		// 화면의 방향을 처음 정한 다음에 변하지 않는다고 가정하고 있기 때문에 
		// Viewport가 한번 생성된 이후에는 더 이상 신경쓰지 않는다.
		if(mViewport == null) {
			AppConfig config = Core.APP.getAppConfig();
			float cameraWidth = (config.virtualWidth < 0f)? width : config.virtualWidth;
			float cameraHeight = (config.virtualHeight < 0f)? height : config.virtualHeight;
			mViewport = new Viewport(cameraWidth, cameraHeight);
			mViewport.update(width, height);
		}
		
		if(mFirstCreated) {
			mFirstCreated = false;
			Core.APP.getAppListener().onCreate();
		}
		
		Core.APP.debug(TAG, "error in onSurfaceChanged : " + getErrorCode());
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		
		// 렌더링 및 입력이벤트를 처리하기 전에 이번 프레임에서 
		// 실행할 이벤트 Runnable을 하나씩 처리한다.
		while(true) {
			Runnable runnable = Core.APP.getCurrentFrameEvent();
			if(runnable == null) break;
			runnable.run();
		}
		
		mCurrentTime = SystemClock.uptimeMillis();
		mDeltaTime = mCurrentTime - mLastTime;
		mLastTime = mCurrentTime;
		
		if(mRunning) {
			// 동기화를 위해 수집한 입력이벤트를 렌더링스레드에서 처리
			Core.INPUT.processInputEvents();
			
			mLastDrawCount = mDrawCount;
			mDrawCount = 0;
			
			//Profiler.beginElapsedTime();
			Core.APP.getAppListener().onRender();
			//Profiler.EndElapsedTime();
		}
		
		// 앱을 시작한 후 각 프레임마다 프레임 카운트를 1씩 증가시킨다.
		mAccumulatedFrameCount++;
		mFrameCount++;
		if(mCurrentTime > mNextFPSTime) {
			mNextFPSTime = mCurrentTime + 1000;
			mFPS = mFrameCount;
			mFrameCount = 0;
		}
	}
	
	/** 누적된 프레임 카운트를 얻는다. */
	public int getAccumulatedFrameCount() {
		return mAccumulatedFrameCount;
	}
	
	/** 
	 * glDraw...(...)를 호출할 때마다 이 메서드를 호출하여 count를 하나 증가시킨다. 
	 * 사용자가 호출할 경우는 없다. 
	 */
	public void incrementDrawCount() {
		mDrawCount++;
	}

	/** 이전 프레임에서 호출된 glDraw...(...)의 개수를 얻는다. */
	public int getLastDrawCount() {
		return mLastDrawCount;
	}
	
	public long getCurrentTime() {
		return mCurrentTime;
	}
	
	/** 이전 프레임과 현재 프레임의 시작 시간의 차이를 얻는다. */ 
	public long getDeltaTime() {
		return mDeltaTime;
	}
	
	/** FPS를 얻는다. 막 시작한 초기에는 올바르지 않은 값이 리턴될 수 있다. */
	public int getFPS() {
		return mFPS;
	}

	@Override
	public void onResume() {
		mRunning = true;
	}
	
	@Override
	public void onPause() {
		mRunning = false;
	}
	
	@Override
	public void onDestroy() {
	}
	
	/** 
	 * 리소스 아이디에 해당하는 비트맵을 메모리에 로드한다. 내부적으로 {@link #getBitmap(int, Options)}를 
	 * 호출하는데 스크린의 dpi에 맞춰 이미지를 자동으로 스케일을 하지 않도록 하는 옵션이 사용된다. 
	 */
	public Bitmap getBitmap(int resID) {
		return getBitmap(resID, OPTIONS);
	}
	
	public Bitmap getBitmap(int resID, Options opt) {
		return BitmapFactory.decodeResource(Core.APP.getResources(), resID, opt);
	}
	
	/** 리소스 아이디에 해당하는 비트맵의 사이즈 정보를 얻는다. 스크린의 dpi에 따른 스케일은 무시한다. */
	public void getBitmapSize(int resID, Vector2 size) {
		final BitmapFactory.Options options = OPTIONS;
		// 실제 비트맵을 메모리에 로드하지 않으면서 비트맵의 사이즈만을 구한다.
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(Core.APP.getResources(), resID, options);
		options.inJustDecodeBounds = false;
		size.x = options.outWidth;
		size.y = options.outHeight;
	}
	
	public Bitmap createStringBitmap(int stringID, int drawCount, float padding, int bottomHeight, int maxHeight, Paint p) {
		String string = Core.APP.getResources().getString(stringID);
		return createStringBitmap(string, drawCount, padding, bottomHeight, maxHeight, p);
	}
	
	public Bitmap createStringBitmap(String string, int drawCount, float padding, int bottomHeight, int maxHeight, Paint p) {
		// 패딩은 Paint의 shade효과 등으로 인해 필요하다. 없으면 일부가 잘려나갈 수 있다.
		// font의 양 측면과 상단 및 하단(선택적)에 적용된다.
		
		float width = p.measureText(string);
		// 공백인 경우 Paint의 TextSize에 루트를 씌운 값을 사용
		if(width == 0f) width = (float) Math.sqrt(p.getTextSize());
		width += padding*2;
		
		final Rect rect = Rectangle.TMP_RECT;
		p.getTextBounds(string, 0, string.length(), rect);
		
		// 높이 = 상단(기준점 위의 높이 + 패딩) + 하단(기준점 아래의 높이 + 패딩 또는 고정값)
		int top = (int) (-rect.top + padding);
		int bottom = (bottomHeight < 0)? (int) (rect.bottom + padding) : bottomHeight;
		int height = top + bottom;
		if(maxHeight != -1 && height > maxHeight)
			throw new IllegalArgumentException("Height of Bitmap must be less than maxHeight. (current height : " + height +")");
		
		Bitmap bitmap = Bitmap.createBitmap((int) width, height, Config.ARGB_8888);
		
		// 비트맵에 텍스트를 출력
		Canvas canvas = mCanvas;
		canvas.setBitmap(bitmap);
		for(int i=0; i<drawCount; i++)
			canvas.drawText(string, padding, top, p);

		return bitmap;
	}
	
	public Bitmap[] createStringArrayBitmaps(int stringArrayID, int drawCount, float padding, int bottomHeight, int maxHeight, Paint p) {
		String[] strings = Core.APP.getResources().getStringArray(stringArrayID);
		Bitmap[] bitmaps = new Bitmap[strings.length];
		for(int i=0; i<strings.length; i++)
			bitmaps[i] = createStringBitmap(strings[i], drawCount, padding, bottomHeight, maxHeight, p);
		return bitmaps;
	}
	
	/** 텍스쳐 관리자를 얻는다. */
	public TextureManager getTextureManager() {
		return mTextureManager;
	}
	
	public int getSupportedMaxTextureSize() {
		final int[] params = TMP_PARAMS;
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE , params, 0);
		return params[0];
	}
	
	public boolean addGLReloadable(GLReloadable reloadable) {
		if(!mGLReloadableList.contains(reloadable)) return mGLReloadableList.add(reloadable);
		return false;
	}
	
	public boolean removeGLReloadable(GLReloadable reloadable) {
		return mGLReloadableList.remove(reloadable);
	}
	
	public int getWidth() {
		return mViewport.getScreenViewportWidth();
	}

	public int getHeight() {
		return mViewport.getScreenViewportHeight();
	}
	
	public float getVirtualWidth() {
		return mViewport.getCameraViewportWidth();
	}

	public float getVirtualHeight() {
		return mViewport.getCameraViewportHeight();
	}

	public Viewport getViewport() {
		return mViewport;
	}
	
	public OrthoCamera getDefaultCamera() {
		return mViewport.getDefaultCamera();
	}
	
	/** 
	 * OpenGL에서 발생한 에러를 리턴한다. 각 에러마다 flag를 가지고 있는데, 이 메서드를 
	 * 호출할 때마다 하나의 에러만 리턴되고 flag가 리셋되기 때문에 모든 에러를 체크하기 
	 * 위해서는 {@value #GL_NO_ERROR}를 리턴할 때까지 루프를 돌려야 한다.
	 * @return
	 */
	public String getErrorCode() {
		int error = GLES20.glGetError();
		switch(error) {
			case GLES20.GL_NO_ERROR:
				return GL_NO_ERROR;
			case GLES20.GL_INVALID_ENUM:
				return GL_INVALID_ENUM;
			case GLES20.GL_INVALID_VALUE:
				return GL_INVALID_VALUE; 
			case GLES20.GL_INVALID_OPERATION:
				return GL_INVALID_OPERATION;	  
			case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
				return GL_INVALID_FRAMEBUFFER_OPERATION;	 
			case GLES20.GL_OUT_OF_MEMORY:
			default:
				return GL_OUT_OF_MEMORY;
		}
	}
	
}
