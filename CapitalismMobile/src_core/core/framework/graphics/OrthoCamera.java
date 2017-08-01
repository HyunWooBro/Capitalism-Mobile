package core.framework.graphics;

import android.opengl.GLU;

import core.framework.Core;
import core.framework.graphics.utils.Viewport;
import core.math.Matrix4;
import core.math.Rectangle;
import core.math.Vector2;
import core.math.Vector3;

/**
 * 직교투영 카메라를 설정한다. OpenGL의 좌표계와는 달리 안드로이드의 Canvas처럼 y좌표가 
 * 아래를 향하는 좌표계를 사용한다.
 * 
 * @author 김현우
 */
public class OrthoCamera {
	
	public enum TargetMode {
		/** 타겟의 위치로 바로 점프한다. */
		JUMP, 
		/** 타겟을 추적한다. 거리가 가까워 질수록 카메라의 추적 속도는 느려진다. */
		CHASE, 
	}
	
	private static final Vector3 VECTOR = new Vector3();
	
	/** 카메라의 뷰포트 너비 */
	protected float mViewportWidth;
	/** 카메라의 뷰포트 높이 */
	protected float mViewportHeight;
	
	private boolean mProjectionInvalidated = true;
	private boolean mViewInvalidated = true;
	
	private Vector3 mPosition = new Vector3();
	private Vector3 mDirection = new Vector3();
	private Vector3 mUp = new Vector3();
	
	private float mRotation;
	
	private Matrix4 mProjectionMatrix = new Matrix4();
	private Matrix4 mViewMatrix = new Matrix4();
	protected Matrix4 mCombinedMatrix = new Matrix4();
	
	private float mNear	= -1;
	private float mFar		= 1;

	private float mZoom = 1f;
	
	private Rectangle mVisibleRectangle = new Rectangle();
	
	private Targetable mTarget;
	private TargetMode mTargetMode = TargetMode.JUMP;
	private float mChaseSpeed = 1f;

	public OrthoCamera() {
		this(Core.GRAPHICS.getVirtualWidth(), Core.GRAPHICS.getVirtualHeight());
	}
	
	public OrthoCamera(float viewportWidth, float viewportHeight) {
		setViewportSize(viewportWidth, viewportHeight);
		mPosition.set(viewportWidth/2 * mZoom, viewportHeight/2 * mZoom, 0f);
		mDirection.set(0f, 0f, 1f);
		// y축이 아래를 향하도록 한다.
		mUp.set(0f, -1f, 0f);
	}
	
	public void update() {
		if(mTarget != null) {
			switch(mTargetMode) {
				case CHASE: chase();
					break;
				case JUMP: setPos(mTarget.getTargetX(), mTarget.getTargetY());
					break;
			}
		}
		
		updateMatrix();
	}
	
	private void chase() {
		final Vector3 v = VECTOR;
		v.set(mTarget.getTargetX(), mTarget.getTargetY(), 0f);
		if(!v.equals(mPosition, 0.01f)) {
			float normalizedTime = Core.GRAPHICS.getDeltaTime() / 1000f;
			mPosition.lerp(v, Math.min(1f, normalizedTime * mChaseSpeed));
			mViewInvalidated = true;
		}
	}
	
	public void updateMatrix() {
		if(!isInvalidated()) return;
		
		Matrix4 projectionMatrix = mProjectionMatrix;
		Matrix4 viewMatrix = mViewMatrix;
		
		float zoomedViewportWidth = mViewportWidth * mZoom;
		float zoomedViewportHeight = mViewportHeight * mZoom;
		float zoomedViewportHalfWidth = zoomedViewportWidth / 2;
		float zoomedViewportHalfHeight = zoomedViewportHeight / 2;

		if(mProjectionInvalidated) {
			// 직교투영행렬을 세팅한다.
			projectionMatrix.ortho(
					-zoomedViewportHalfWidth, 
					zoomedViewportHalfWidth, 
					-zoomedViewportHalfHeight, 
					zoomedViewportHalfHeight, 
		    		mNear, mFar);
		    
		    mProjectionInvalidated = false;
		}
	    
		if(mViewInvalidated) {
			final Vector3 target = VECTOR;
			target.set(mPosition).add(mDirection);
		    
		    // 카메라(뷰)행렬을 세팅한다.
			viewMatrix.setLookAt(
					mPosition.x, mPosition.y, mPosition.z, 
		    		target.x, target.y, target.z, 
		    		mUp.x, mUp.y, mUp.z);
		    
		    mViewInvalidated = false;
		}

        // 최종 행렬을 계산한다. 매트릭스 계산이 오른쪽에서 왼쪽으로 향하므로, 월드행렬에서 카메라행렬로 
	    // 변환하는 view 행렬을 오른쪽에 배치하여 정점과 먼저 계산되도록 하고 왼쪽에 투영행렬을 배치한다.
	    Matrix4.multiplyMM(mCombinedMatrix.value, 0, projectionMatrix.value, 0, viewMatrix.value, 0);
	    
	    // 스크린에 보이는 카메라 뷰포트의 사각영역을 세팅한다.
	    mVisibleRectangle.set(
				mPosition.x - zoomedViewportHalfWidth, 
				mPosition.y - zoomedViewportHalfHeight, 
				zoomedViewportWidth, 
				zoomedViewportHeight);
	}
	
	/** 카메라의 x축 위치를 얻는다. y축이 위를 향하는 OpenGL의 기본 좌표계를 기준으로 한다. */
	public float getPosX() {
		return mPosition.x;
	}
	
	/** 카메라의 y축 위치를 얻는다. y축이 위를 향하는 OpenGL의 기본 좌표계를 기준으로 한다. */
	public float getPosY() {
		return mPosition.y;
	}
	
	/** 카메라를 이동한다. 물체는 카메라의 반대방향으로 이동하는 것으로 보인다. */
	public OrthoCamera translate(float x, float y) {
		if(x != 0f || y != 0f) {
			mPosition.add(x, y, 0f);
			mViewInvalidated = true;
		}
		return this;
	}
	
	/** 카메라를 이동한다. 물체는 카메라의 반대방향으로 이동하는 것으로 보인다. */
	public OrthoCamera setPos(float x, float y) {
		if(mPosition.x != x || mPosition.y != y) {
			mPosition.set(x, y, 0f);
			mViewInvalidated = true;
		}
		return this;
	}
	
	public float getRotation() {
		return mRotation;
	}
	
	/** 카메라를 회전한다. 물체는 카메라의 반대방향으로 회전하는 것으로 보인다. */
	public OrthoCamera rotate(float angle) {
		if(angle != 0f) {
			mRotation += angle;
			mUp.rot(angle, 0f, 0f, 1f);
			mViewInvalidated = true;
		}
		return this;
	}
	
	/** 카메라를 회전한다. 물체는 카메라의 반대방향으로 회전하는 것으로 보인다. */
	public OrthoCamera setRotation(float angle) {
		mRotation = 0f;
		mUp.set(0f, -1f , 0f);
		rotate(angle);
		return this;
	}
	
	public float getZoom() {
		return mZoom;
	}

	public OrthoCamera setZoom(float zoom) {
		if(mZoom != zoom) {
			mZoom = zoom;
			mProjectionInvalidated = true;
		}
		return this;
	}
	
	/*package*/ boolean isInvalidated() {
		return mProjectionInvalidated || mViewInvalidated;
	}
	
	public Targetable getTarget() {
		return mTarget;
	}
	
	public boolean hasTarget() {
		return mTarget != null;
	}
	
	public TargetMode getTargetMode() {
		return mTargetMode;
	}
	
	public OrthoCamera setTarget(Targetable target) {
		mTarget = target;
		return this;
	}
	
	public OrthoCamera setTargetMode(TargetMode mode) {
		mTargetMode = mode;
		return this;
	}
	
	public float getChaseSpeed() {
		return mChaseSpeed;
	}

	/** {@link TargetMode#CHASE}인 경우, 추적속도를 설정한다. 기본 값은 1이다. */
	public OrthoCamera setChaseSpeed(float chaseSpeed) {
		mChaseSpeed = chaseSpeed;
		return this;
	}
	
	/** projection매트릭스와 view매트릭스를 결합한 매트릭스를 얻는다. */
	public Matrix4 getCombinedMatrix() {
		return mCombinedMatrix;
	}

	public float getViewportWidth() {
		return mViewportWidth;
	}

	public float getViewportHeight() {
		return mViewportHeight;
	}

	public OrthoCamera setViewportSize(float viewportWidth, float viewportHeight) {
		if(mViewportWidth != viewportWidth || mViewportHeight != viewportHeight) {
			mViewportWidth = viewportWidth;
			mViewportHeight = viewportHeight;
			mProjectionInvalidated = true;
		}
		return this;
	}
	
	public Rectangle getVisibleRectangle() {
		return mVisibleRectangle;
	}
	
	/** 
	 * 모델 좌표계에서 윈도우 좌표계로 변경한다. 입력과 출력 좌표계는 모두 OpenGL의 좌표계를 
	 * 기준으로 한다. 
	 */
	public Vector2 project(Vector2 pos) {
		final float[] tmp = Vector3.TMP_ARRAY;
		GLU.gluProject(
				pos.x, pos.y, 0f, 
				mViewMatrix.value, 0, 
				mProjectionMatrix.value, 0, 
				Viewport.VIEWPORT_RECT_ARRAY, 0, 
				tmp, 0);
		pos.x = tmp[0];
		pos.y = tmp[1];
		return pos;
	}
	
	/** 
	 * 윈도우 좌표계에서 모델 좌표계로 변경한다. 입력과 출력 좌표계는 모두 OpenGL의 좌표계를 
	 * 기준으로 한다. 
	 */
	public Vector2 unproject(Vector2 pos) {
		final float[] tmp = Vector3.TMP_ARRAY;
		GLU.gluUnProject(
				pos.x, pos.y, 0f, 
				mViewMatrix.value, 0, 
				mProjectionMatrix.value, 0, 
				Viewport.VIEWPORT_RECT_ARRAY, 0, 
				tmp, 0);
		pos.x = tmp[0];
		pos.y = tmp[1];
		return pos;
	}
	
	/**
	 * 카메라의 타겟이 되기 위해서는 Targetable을 구현해야 한다.
	 * 
	 * @author 김현우
	 */
	public static interface Targetable {
		
		public float getTargetX();
		
		public float getTargetY();
	}

}
