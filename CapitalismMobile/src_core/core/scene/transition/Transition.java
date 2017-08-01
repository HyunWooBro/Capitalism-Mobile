package core.scene.transition;

import android.opengl.GLES20;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import core.framework.Core;
import core.framework.graphics.OrthoCamera;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.utils.FrameBufferObject;
import core.scene.Director;
import core.scene.Scene;

/**
 * Scene을 전환할 때 애니메이션을 추가할 수 있다. 구체적인 애니메이션은 
 * 이 클래스를 상속하여 {@link #renderToScreen(Batch, Texture, Texture, float)}를 
 * 통해 구현해야 한다.</p>
 * 
 * Transition은 Director의 changeScene(...), pushScene(...) 그리고 popScene(...)에서 
 * 적용할 수 있다. popScene(...)에서는 nextScene이 없는 Transition을 제공해야 한다.</p>
 * 
 * Transition은 디폴트 카메라를 기준으로 적용된다.</p>
 * 
 * @author 김현우
 */
@SuppressWarnings("rawtypes")
public abstract class Transition extends Scene {
	
	private static final int START_ON_FIRST_FRAME = -1;
	
	private static FrameBufferObject sCurrentSceneFrameBuffer;
	private static FrameBufferObject sNextSceneFrameBuffer;
	
	private static OrthoCamera sCamera;
	
	private Scene<?> mCurrentScene;
	private Scene<?> mNextScene;
	
	private long mDuration;
	
	private long mStartTime = START_ON_FIRST_FRAME;
	
	protected long mStartOffset;
	
	private boolean mStarted;
	
	private boolean mEnded;
	
	private Interpolator mInterpolator;
	
	public Transition(Scene<?> nextScene, Interpolator interpolator) {
		mNextScene = nextScene;
		mInterpolator = interpolator;
		ensureScene(nextScene);
		ensureInterpolator();
		ensureFrameBuffer();
		ensureCamera();
	}

	private void ensureScene(Scene<?> scene) {
		if(scene instanceof Transition)
			throw new IllegalArgumentException("double Transition not allowed");
	}
	
	private void ensureInterpolator() {
        if(mInterpolator == null) mInterpolator = new LinearInterpolator();
    }
	
	private void ensureFrameBuffer() {
		int width = Core.GRAPHICS.getWidth();
		int height = Core.GRAPHICS.getHeight();
		if(sCurrentSceneFrameBuffer == null)
			sCurrentSceneFrameBuffer = new FrameBufferObject(width, height);
		if(sNextSceneFrameBuffer == null)
			sNextSceneFrameBuffer = new FrameBufferObject(width, height);
	}
	
	private void ensureCamera() {
		if(sCamera == null) {
			sCamera = new OrthoCamera(1f, 1f);
			sCamera.updateMatrix();
		}
	}
	
	@Override
	protected void create() {
	}

	public Scene<?> getNextScene() {
		return mNextScene;
	}

	public long getDuration() {
		return mDuration;
	}
	
	public long getStartTime() {
		return mStartTime;
	}

	public long getStartOffset() {
		return mStartOffset;
	}
	
	protected void setDuration(long duration) {
		if(duration < 0)
            throw new IllegalArgumentException("Transition duration cann't be negative");
		
		mDuration = duration;
	}

	public boolean hasStarted() {
		return mStarted;
	}
	
	public boolean hasEnded() {
		return mEnded;
	}
	
	/** 
	 * Transition을 실제로 실행할 조건이 되었으면 true를 리턴한다. 시작시간 + 
	 * 오프셋시간을 현재시간이 넘어서면 true가 리턴된다.
	 *  
	 * @param time
	 * @return
	 */
	public boolean isReady(long time) {
		if(mStartTime == START_ON_FIRST_FRAME)
			mStartTime = time;
		
		if(mStartTime + mStartOffset <= time) {
			if(mCurrentScene == null)
				mCurrentScene = Director.getInstance().getCurrentScene();
			if(mNextScene == null)
				mNextScene = Director.getInstance().getPreviousScene();
			return true;
		}

		return false;
	}
	
	/** 
	 * {@link #renderToFBO(Batch, long)}와 
	 * {@link #renderToScreen(Batch, Texture, Texture, float)}를 순서대로 호출한다.
	 * 
	 * @param batch
	 * @param time
	 */
	public void render(Batch batch, long time) {
		// 현재 Scene과 다음 Scene을 FBO에 렌더링
		renderToFBO(batch, time);

		// 정규화된 카메라 적용
		batch.setProjectionMatrix(sCamera.getCombinedMatrix());
		
		// FBO에 그린 Texture을 실제로 화면에 어떻게 배치할 지를 결정
		renderToScreen(
				batch, 
				sCurrentSceneFrameBuffer.getTexture(), 
				sNextSceneFrameBuffer.getTexture(), 
				getInterpolatedTime(time));
	}

	/** 현재 Scene과 다음 Scene을 FBO에 렌더링한다. */
	protected void renderToFBO(Batch batch, long time) {
		sCurrentSceneFrameBuffer.begin();
    	//GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    	GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    	mCurrentScene.update(time);
    	mCurrentScene.draw(batch);
    	sCurrentSceneFrameBuffer.end();
    	
    	sNextSceneFrameBuffer.begin();
    	//GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    	GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    	mNextScene.update(time);
    	mNextScene.draw(batch);
    	sNextSceneFrameBuffer.end();
	}
	
	private float getInterpolatedTime(long time) {
		
        long startTime = mStartTime;
        long startOffset = mStartOffset;
        long duration = mDuration;
        
        if(!mStarted) mStarted = true;
        
        float normalizedTime;
        if (duration != 0) {
            normalizedTime = ((float) (time - (startTime + startOffset))) /
                    (float) duration;
        } else 
        	normalizedTime = 1.0f;
        
        // isReady(...)에 의해 normalizedTime은 무조건 0f 이상이므로 상한값만 검사한다.
        normalizedTime = Math.min(normalizedTime, 1.0f);
        float interpolatedTime = mInterpolator.getInterpolation(normalizedTime);
        
        if(normalizedTime == 1f) mEnded = true;
        
        return interpolatedTime;
	}
	
	/**
	 * FBO에 그린 Texture을 실제로 화면에 어떻게 배치할 지를 결정한다.</p>
	 *  
	 * batch에 [0, 0, 1, 1]크기를 가진 카메라의 매트릭스가 적용되기 때문에 Scene의 
	 * 너비와 높이는 1f로 정규화하여 그려야 한다.</p>
	 * 
	 * <b>주의</b> : 렌더링할 때 y축을 뒤집어 주어야 한다. 텍스쳐가 마치 비트맵에서 로드된 것과 같은 
	 * 상황을 만들어 주기 위해서이다. Batch는 텍스쳐에 이미지가 뒤집혀 입력되는 것을 가정하고 정점의 
	 * 위치를 배치하기 때문이다.</p>
	 */
	protected abstract void renderToScreen(Batch batch, Texture currentScene, Texture nextScene, 
			float interpolatedTime);
}
