package core.math;

import java.util.Arrays;

/** 
 * 열우선(행렬의 원소를 세로로 연결하여 메모리에 저장하는 형식) 4x4 열벡터행렬이며 OpenGL의 기본 
 * 행렬형식이다. OpenGL이 요구하는 데이터(셰이더의 uniform 등)를 전달하는 수단으로써, 주로 카메라의 
 * view 매트릭스와 projection 매트릭스 및 이들의 결합을 구현하기 위해 사용된다.</p>
 * 
 * android.opengl.Matrix를 확장한다.</p>
 * 
 * @author 김현우
 */
public class Matrix4 extends android.opengl.Matrix {
	
	public static final int M00 = 0;
	public static final int M01 = 4;
	public static final int M02 = 8;
	public static final int M03 = 12;

	public static final int M10 = 1;
	public static final int M11 = 5;
	public static final int M12 = 9;
	public static final int M13 = 13;

	public static final int M20 = 2;
	public static final int M21 = 6;
	public static final int M22 = 10;
	public static final int M23 = 14;

	public static final int M30 = 3;
	public static final int M31 = 7;
	public static final int M32 = 11;
	public static final int M33 = 15;
	
	private static final Matrix4 MATRIX = new Matrix4();
	
	public float[] value = new float[16];
	
	public Matrix4() {
	}
	
	public Matrix4(Matrix3 matrix) {
		set(matrix);
	}
	
	public Matrix4(Matrix4 matrix) {
		set(matrix);
	}
	
	public Matrix4 reset() {
		setIdentityM(value, 0);
		return this;
	}

	/** 행우선 3x3행렬을 열우선 4x4행렬로 채운다. */ 
	public Matrix4 set(Matrix3 matrix) {
		final float[] tmp = Matrix3.TMP_VALUE;
		matrix.getValues(tmp);
		
		float[] m = value;
		m[M00] = tmp[Matrix3.M00];
    	m[M01] = tmp[Matrix3.M01];
    	m[M02] = 0f;
    	m[M03] = tmp[Matrix3.M02];
		
    	m[M10] = tmp[Matrix3.M10];
    	m[M11] = tmp[Matrix3.M11];
    	m[M12] = 0f;
    	m[M13] = tmp[Matrix3.M12];
		
    	m[M20] = 0f;
    	m[M21] = 0f;
    	m[M22] = 1f;
    	m[M23] = 0f;
		
    	m[M30] = tmp[Matrix3.M20];
    	m[M31] = tmp[Matrix3.M21];
    	m[M32] = 0f;
    	m[M33] = tmp[Matrix3.M22];
    	
    	return this;
	}
	
	public Matrix4 set(Matrix4 matrix) {
		System.arraycopy(matrix.value, 0, value, 0, 16/*4 by 4*/);
		return this;
	}
	
	/** this = this * matrix */
	public Matrix4 preConcat(Matrix4 matrix) {
		multiplyMM(MATRIX.value, 0, value, 0, matrix.value, 0);
		System.arraycopy(MATRIX.value, 0, value, 0, 16/*4 by 4*/);
		return this;
	}
	
	/** this = matrix * this */
	public Matrix4 postConcat(Matrix4 matrix) {
		multiplyMM(MATRIX.value, 0, matrix.value, 0, value, 0);
		System.arraycopy(MATRIX.value, 0, value, 0, 16/*4 by 4*/);
		return this;
	}
	
	public Matrix4 setRotate(float angle, float x, float y, float z) {
		setRotateM(value, 0, angle, x, y, z);
		return this;
	}
	
	public Matrix4 preRotate(float angle, float x, float y, float z) {
		rotateM(value, 0, angle, x, y, z);
		return this;
	}
	
	public Matrix4 setTranslate(float x, float y, float z) {
		reset();
		value[M03]= x;
		value[M13]= y;
		value[M23]= z;
		return this;
	}
	
	public Matrix4 preTranslate(float x, float y, float z) {
		translateM(value, 0, x, y, z);
		return this;
	}
	
	public Matrix4 setScale(float x, float y, float z) {
		reset();
		value[M00]= x;
		value[M11]= y;
		value[M22]= z;
		return this;
	}
	
	public Matrix4 preScale(float x, float y, float z) {
		scaleM(value, 0, x, y, z);
		return this;
	}
	
	public Matrix4 transpose() {
		transposeM(MATRIX.value, 0, value, 0);
		System.arraycopy(MATRIX.value, 0, value, 0, 16/*4 by 4*/);
		return this;
	}
	
	public Matrix4 transpose(Matrix4 matrix) {
		transposeM(matrix.value, 0, value, 0);
		return this;
	}
	
	public Matrix4 setLookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, 
			float upX, float upY, float upZ) {
		setLookAtM(value, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		return this;
	}
	
	public Matrix4 ortho(float left, float right, float bottom, float top, float near, float far) {
		orthoM(value, 0, left, right, bottom, top, near, far);
		return this;
	}
	
	public boolean invert(Matrix4 matrix) {
		return invertM(matrix.value, 0, value, 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Matrix4 other = (Matrix4) obj;
		if(!Arrays.equals(value, other.value)) return false;
		return true;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Matrix4 {");
        sb.append(toShortString());
        sb.append("}");
        return sb.toString();      
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder(128);
        toShortString(sb);
        return sb.toString();
    }

    private void toShortString(StringBuilder sb) {
        sb.append('[');
        sb.append(value[0]).append(", ").append(value[1]).append(", ");
        sb.append(value[2]).append(", ").append(value[3]).append("][");
        sb.append(value[4]).append(", ").append(value[5]).append(", ");
        sb.append(value[6]).append(", ").append(value[7]).append("][");
        sb.append(value[8]).append(", ").append(value[9]).append(", ");
        sb.append(value[10]).append(", ").append(value[11]).append("][");
        sb.append(value[12]).append(", ").append(value[13]).append(", ");
        sb.append(value[14]).append(", ").append(value[15]);
        sb.append(']');
    }

}
