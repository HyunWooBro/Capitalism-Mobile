package core.math;

import android.view.animation.Interpolator;

public class Vector3 implements Vector<Vector3> {
	
	public static final float[] TMP_ARRAY = new float[4];
	
	private static final float[] TMP = new float[8];
	private static final Matrix4 MATRIX = new Matrix4();
	
	public float x;
	public float y;
	public float z;

	public Vector3() {
	}

	public Vector3(float x, float y, float z) {
		set(x, y, z);
	}

	public Vector3(Vector3 v) {
		set(v);
	}
	
	@Override
	public Vector3 reset() {
		this.x = 0f;
		this.y = 0f;
		this.z = 0f;
		return this;
	}

	@Override
	public Vector3 set(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
		return this;
	}
	
	public Vector3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public Vector3 cpy() {
		return new Vector3(this);
	}

	@Override
	public Vector3 sub(Vector3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public Vector3 sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	@Override
	public Vector3 nor() {
		float length = len();
		if (length != 0) {
			x /= length;
			y /= length;
			z /= length;
		}
		return this;
	}

	@Override
	public Vector3 add(Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public Vector3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public float dot(Vector3 v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}

	public float dot(float x, float y, float z) {
		return (this.x * x) + (this.y * y) + (this.z * z);
	}
	
	public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (x1 * x2) + (y1 * y2) + (z1 * z2);
	}
	
	@Override
	public float len() {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}

	public static float len(float x, float y, float z) {
		return (float) Math.sqrt((x * x) + (y * y) + (z * z));
	}
	
	@Override
	public float len2() {
		return (x * x) + (y * y) + (z * z);
	}

	public static float len2(float x, float y, float z) {
		return (x * x) + (y * y) + (z * z);
	}
	
	@Override
	public float dst(Vector3 v) {
		final float dx = v.x - x;
		final float dy = v.y - y;
		final float dz = v.z - z;
		return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}

	public float dst(float x, float y, float z) {
		final float dx = x - this.x;
		final float dy = y - this.y;
		final float dz = z - this.z;
		return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	public static float dst(float x1, float y1, float z1, float x2, float y2, float z2) {
		final float dx = x2 - x1;
		final float dy = y2 - y1;
		final float dz = z2 - z1;
		return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}
	
	@Override
	public float dst2(Vector3 v) {
		final float dx = v.x - x;
		final float dy = v.y - y;
		final float dz = v.z - z;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}

	public float dst2(float x, float y, float z) {
		final float dx = x - this.x;
		final float dy = y - this.y;
		final float dz = z - this.z;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}
	
	public static float dst2(float x1, float y1, float z1, float x2, float y2, float z2) {
		final float dx = x2 - x1;
		final float dy = y2 - y1;
		final float dz = z2 - z1;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}
	
	@Override
	public Vector3 scl(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
	
	@Override
	public Vector3 scl(Vector3 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		return this;
	}

	public Vector3 scl(float a, float b, float c) {
		x *= a;
		y *= b;
		z *= c;
		return this;
	}
	
	@Override
	public Vector3 clamp(float min, float max) {
		float len2 = len2();
		if(len2 == 0f) return this;
		if(len2 < min*min) nor().scl(min);
		if(len2 > max*max) nor().scl(max);
		return this;
	}

	public Vector3 mul(Matrix4 matrix) {
		TMP[0] = x;
		TMP[1] = y;
		TMP[2] = z;
		TMP[3] = 1f;
		android.opengl.Matrix.multiplyMV(TMP, 4, matrix.value, 0, TMP, 0);
		x = TMP[4];
		y = TMP[5];
		z = TMP[6];
		return this;
	}

	public Vector3 rot(float angle, Vector3 v) {
		mul(MATRIX.setRotate(angle, v.x, v.y, v.z));
		return this;
	}
	
	public Vector3 rot(float angle, float x, float y, float z) {
		mul(MATRIX.setRotate(angle, x, y, z));
		return null;
	}
	
	public Vector3 crs(Vector3 v) {
		set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
		return this;
	}

	public Vector3 crs(float x, float y, float z) {
		set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
		return this;
	}

	@Override
	public Vector3 lerp(Vector3 target, float normalizedTime) {
		float inverseNormalizedTime = 1f - normalizedTime;
		x = x*inverseNormalizedTime + target.x*normalizedTime;
		y = y*inverseNormalizedTime + target.y*normalizedTime;
		z = z*inverseNormalizedTime + target.z*normalizedTime;
		return this;
	}

	@Override
	public Vector3 interpolate(Vector3 target, float normalizedTime, Interpolator interpolator) {
		return lerp(target, interpolator.getInterpolation(normalizedTime));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Vector3 other = (Vector3) obj;
		if(Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
		if(Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
		if(Float.floatToIntBits(z) != Float.floatToIntBits(other.z)) return false;
		return true;
	}

	@Override
	public boolean equals(Vector3 v, float epsilon) {
		if(!MathUtils.isEqual(x, v.x, epsilon)) return false;
		if(!MathUtils.isEqual(y, v.y, epsilon)) return false;
		if(!MathUtils.isEqual(z, v.z, epsilon)) return false;
		return true;
	}

	@Override
	public boolean isUnit() {
		return MathUtils.isEqual(len2(), 1f);
	}
	
	@Override
	public boolean isZero() {
		return MathUtils.isZero(len2());
	}

	@Override
	public boolean isAcuteWith(Vector3 v) {
		return dot(v) > 0;
	}

	@Override
	public boolean isObtuseWith(Vector3 v) {
		return dot(v) < 0;
	}

	@Override
	public boolean isPerpendicularWith(Vector3 v) {
		return MathUtils.isZero(dot(v));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vector3 [")
				.append(x).append(", ")
				.append(y).append(", ")
				.append(z)
				.append("]");
		return builder.toString();
	}

}

