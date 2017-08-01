package core.math;

import android.view.animation.Interpolator;

public interface Vector<T extends Vector<T>> {
	
	public T reset();

	public T set(T v);
	
	public T cpy();

	public T sub(T v);
	
	public T nor();

	public T add(T v);

	public float dot(T v);
	
	public float len();
	
	public float len2();
	
	public float dst(T v);
	
	public float dst2(T v);
	
	public T scl(float scalar);
	
	public T scl(T v);
	
	public T clamp(float min, float max);
	
	/** 선형 보간법(Linear Interpolation) */
	public T lerp(T target, float normalizedTime);
	
	public T interpolate (T target, float normalizedTime, Interpolator interpolator);
	
	public boolean equals(T v, float epsilon);
	
	public boolean isUnit();
	
	public boolean isZero();
	
	public boolean isAcuteWith(T v);
	
	public boolean isObtuseWith(T v);
	
	public boolean isPerpendicularWith(T v);
}
