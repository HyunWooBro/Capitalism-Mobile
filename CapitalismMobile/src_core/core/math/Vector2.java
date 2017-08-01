package core.math;

import android.view.animation.Interpolator;

public class Vector2 implements Vector<Vector2> {
	
	public static final float[] TMP_ARRAY = new float[3];
	
	private static final Matrix3 MATRIX = new Matrix3();
	
	public float x;
	public float y;

	public Vector2() {
	}

	public Vector2(float x, float y) {
		set(x, y);
	}

	public Vector2(Vector2 v) {
		set(v);
	}
	
	@Override
	public Vector2 reset() {
		this.x = 0f;
		this.y = 0f;
		return this;
	}

	@Override
	public Vector2 set(Vector2 v) {
		x = v.x;
		y = v.y;
		return this;
	}
	
	public Vector2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public Vector2 cpy() {
		return new Vector2(this);
	}

	@Override
	public Vector2 sub(Vector2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Vector2 sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	@Override
	public Vector2 nor() {
		float length = len();
		if (length != 0) {
			x /= length;
			y /= length;
		}
		return this;
	}

	@Override
	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	@Override
	public float dot(Vector2 v) {
		return (x * v.x) + (y * v.y);
	}

	public float dot (float x, float y) {
		return (this.x * x) + (this.y * y);
	}
	
	public static float dot(float x1, float y1, float x2, float y2) {
		return (x1 * x2) + (y1 * y2);
	}
	
	@Override
	public float len() {
		return (float) Math.sqrt((x * x) + (y * y));
	}

	public static float len(float x, float y) {
		return (float) Math.sqrt((x * x) + (y * y));
	}
	
	@Override
	public float len2() {
		return (x * x) + (y * y);
	}
	
	public static float len2(float x, float y) {
		return (x * x) + (y * y);
	}
	
	@Override
	public float dst(Vector2 v) {
		final float dx = v.x - x;
		final float dy = v.y - y;
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}

	public float dst(float x, float y) {
		final float dx = x - this.x;
		final float dy = y - this.y;
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}
	
	public static float dst(float x1, float y1, float x2, float y2) {
		final float dx = x2 - x1;
		final float dy = y2 - y1;
		return (float) Math.sqrt((dx * dx) + (dy * dy));
	}
	
	@Override
	public float dst2(Vector2 v) {
		final float dx = v.x - x;
		final float dy = v.y - y;
		return (dx * dx) + (dy * dy);
	}
	
	public float dst2(float x, float y) {
		final float dx = x - this.x;
		final float dy = y - this.y;
		return (dx * dx) + (dy * dy);
	}
	
	public static float dst2(float x1, float y1, float x2, float y2) {
		final float dx = x2 - x1;
		final float dy = y2 - y1;
		return (dx * dx) + (dy * dy);
	}
	
	@Override
	public Vector2 scl(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}
	
	@Override
	public Vector2 scl(Vector2 v) {
		x *= v.x;
		y *= v.y;
		return this;
	}

	public Vector2 scl(float a, float b) {
		x *= a;
		y *= b;
		return this;
	}
	
	@Override
	public Vector2 clamp(float min, float max) {
		float len2 = len2();
		if(len2 == 0f) return this;
		if(len2 < min*min) nor().scl(min);
		if(len2 > max*max) nor().scl(max);
		return this;
	}

	public Vector2 mul(Matrix3 matrix) {
		TMP_ARRAY[0] = x;
		TMP_ARRAY[1] = y;
		matrix.mapPoints(TMP_ARRAY, 0, TMP_ARRAY, 0, 1/* pair(s)*/);
		this.x = TMP_ARRAY[0];
		this.y = TMP_ARRAY[1];
		return null;
	}
	
	public Vector2 rot(float angle) {
		MATRIX.setRotate(angle);
		TMP_ARRAY[0] = x;
		TMP_ARRAY[1] = y;
		MATRIX.mapPoints(TMP_ARRAY, 0, TMP_ARRAY, 0, 1/* pair(s)*/);
		this.x = TMP_ARRAY[0];
		this.y = TMP_ARRAY[1];
		return null;
	}

	@Override
	public Vector2 lerp(Vector2 target, float normalizedTime) {
		float inverseNormalizedTime = 1f - normalizedTime;
		x = x*inverseNormalizedTime + target.x*normalizedTime;
		y = y*inverseNormalizedTime + target.y*normalizedTime;
		return this;
	}

	@Override
	public Vector2 interpolate(Vector2 target, float normalizedTime, Interpolator interpolator) {
		return lerp(target, interpolator.getInterpolation(normalizedTime));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Vector2 other = (Vector2) obj;
		if(Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
		if(Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
		return true;
	}

	@Override
	public boolean equals(Vector2 v, float epsilon) {
		if(!MathUtils.isEqual(x, v.x, epsilon)) return false;
		if(!MathUtils.isEqual(y, v.y, epsilon)) return false;
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
	public boolean isAcuteWith(Vector2 v) {
		return dot(v) > 0;
	}

	@Override
	public boolean isObtuseWith(Vector2 v) {
		return dot(v) < 0;
	}

	@Override
	public boolean isPerpendicularWith(Vector2 v) {
		return MathUtils.isZero(dot(v));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vector2 [")
					.append(x).append(", ")
					.append(y)
					.append("]");
		return builder.toString();
	}

}
