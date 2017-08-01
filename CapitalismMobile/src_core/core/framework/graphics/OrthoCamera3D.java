package core.framework.graphics;

import android.graphics.Camera;

import core.math.Matrix3;
import core.math.Matrix4;
import core.math.Vector2;
import core.math.Vector3;

public class OrthoCamera3D extends OrthoCamera {
	
	private static final Matrix3 MATRIX3 = new Matrix3();
	private static final Matrix4 MATRIX4 = new Matrix4();
	
	private Camera mCamera = new Camera();
	
	private Matrix4 mCombinedMatrix = new Matrix4();
	
	private boolean m3DInvalidated = true;
	
	private Vector2 mPivot = new Vector2(0.5f, 0.5f);
	private Vector3 mRotation = new Vector3();
	
	public OrthoCamera3D() {
		super();
	}
	
	public OrthoCamera3D(float viewportWidth, float viewportHeight) {
		super(viewportWidth, viewportHeight);
	}
	
	@Override
	public void updateMatrix() {
		boolean invalidated = isInvalidated();
		super.updateMatrix();
		
		if(!m3DInvalidated && !invalidated) return;
		m3DInvalidated = true;
		
		mCamera.getMatrix(MATRIX3);

		float pivotWidth = mPivot.x * mViewportWidth;
		float pivotHeight = mPivot.y * mViewportHeight;
		MATRIX3.preTranslate(-pivotWidth, -pivotHeight);
		MATRIX3.postTranslate(pivotWidth, pivotHeight);
		
		MATRIX4.set(MATRIX3);
		mCombinedMatrix.set(super.mCombinedMatrix).preConcat(MATRIX4);
	}
	
	@Override
	public Matrix4 getCombinedMatrix() {
		return mCombinedMatrix;
	}
	
	public void setPivot(float px, float py) {
		if(mPivot.x != px || mPivot.y != py) {
			mPivot.x = px;
			mPivot.y = py;
			m3DInvalidated = true;
		}
	}
	
	public float getDistance() {
		return mCamera.getLocationZ();
	}
	
	/** 카메라와의 거리를 지정하여 원근 효과를 변경한다. 디폴트 거리는 -8이다. */
	public void setDistance(float distance) {
		if(mCamera.getLocationZ() != distance) {
			mCamera.setLocation(0, 0, distance);
			m3DInvalidated = true;
		}
	}
	
	public float getRotationX() {
		return mRotation.x;
	}
	
	public float getRotationY() {
		return mRotation.y;
	}
	
	public float getRotationZ() {
		return mRotation.z;
	}
	
	public void rotateX(float angle) {
		if(angle != 0f) {
			mRotation.x += angle;
			mCamera.rotateX(angle);
			m3DInvalidated = true;
		}
	}
	
	public void rotateY(float angle) {
		if(angle != 0f) {
			mRotation.y += angle;
			mCamera.rotateY(angle);
			m3DInvalidated = true;
		}
	}
	
	/** 
	 * {@link #rotate(float)}와 독립적으로 동작하기 때문에 다른 축에 대한 회전에 대한 일관성을 위해 
	 * 이 메서드를 이용하라.
	 */
	public void rotateZ(float angle) {
		if(angle != 0f) {
			mRotation.x += angle;
			mCamera.rotateZ(angle);
			m3DInvalidated = true;
		}
	}
	
	public void rotate(float angleX, float angleY, float angleZ) {
		if(angleX != 0f || angleY != 0f || angleZ != 0f) {
			mRotation.x += angleX;
			mRotation.x += angleY;
			mRotation.x += angleZ;
			mCamera.rotate(angleX, angleY, angleZ);
			m3DInvalidated = true;
		}
	}
	
	public void setRotationX(float angle) {
		if(mRotation.x != angle) {
			mCamera.rotateX(-mRotation.x + angle);
			mRotation.x = angle;
			m3DInvalidated = true;
		}
	}
	
	public void setRotationY(float angle) {
		if(mRotation.y != angle) {
			mCamera.rotateX(-mRotation.y + angle);
			mRotation.y = angle;
			m3DInvalidated = true;
		}
	}
	
	public void setRotationZ(float angle) {
		if(mRotation.z != angle) {
			mCamera.rotateX(-mRotation.z + angle);
			mRotation.z = angle;
			m3DInvalidated = true;
		}
	}
	
	public void setRotation(float angleX, float angleY, float angleZ) {
		if(mRotation.x != angleX || mRotation.y != angleY || mRotation.z != angleZ) {
			mCamera.rotateX(-mRotation.x + angleX);
			mCamera.rotateY(-mRotation.y + angleY);
			mCamera.rotateZ(-mRotation.z + angleZ);
			mRotation.x = angleX;
			mRotation.y = angleY;
			mRotation.z = angleZ;
			m3DInvalidated = true;
		}
	}
	
	public void save() {
		mCamera.save();
		m3DInvalidated = true;
	}

	public void restore() {
		mCamera.restore();
		m3DInvalidated = true;
	}
	
	/** {@link #getRotationZ()}를 대신 사용할 것 */
	@Deprecated
	@Override
	public float getRotation() {
		throw new UnsupportedOperationException("call getRotationZ() instead");
	}
	
	/** {@link #rotateZ(float)}를 대신 사용할 것 */
	@Deprecated
	@Override
	public OrthoCamera rotate(float angle) {
		throw new UnsupportedOperationException("call rotateZ(float) instead");
	}
	
	/** {@link #setRotationZ(float)}를 대신 사용할 것 */
	@Deprecated
	@Override
	public OrthoCamera setRotation(float angle) {
		throw new UnsupportedOperationException("call setRotationZ(float) instead");
	}

}
