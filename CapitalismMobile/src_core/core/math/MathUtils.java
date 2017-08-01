package core.math;

import java.util.Random;

/**
 * java.lang.Math에서 제공하지 않는 수학메서드를 제공한다.
 * 
 * @author 김현우
 */
public class MathUtils {
	
	public static final float EPSILON = 0.0001f;
	
	private static Random sRandom = new Random();
	
	private static int mUniqueStart;
	private static int mUniqueEnd;
	private static int[] mUniqueInts;
	private static boolean[] mUniqueUsed;
	
	private MathUtils() {
	}

	/** 범위 : [0 ~ range] */
	public static int random(int range) {
		return sRandom.nextInt(range + 1);
	}
	
	/** 범위 : [start ~ end] */
	public static int random(int start, int end) {
		return start + sRandom.nextInt(end - start + 1);
	}

	/** 범위 : [0.0 ~ 1.0) */
	public static float randomFloat() {
		return sRandom.nextFloat();
	}

	/** 범위 : [0.0 ~ range) */
	public static float randomFloat(float range) {
		return sRandom.nextFloat() * range;
	}

	/** 범위 : [start ~ end) */
	public static float randomFloat(float start, float end) {
		return start + sRandom.nextFloat() * (end - start);
	}

	/** true or false */
	public static boolean randomBoolean() {
		return sRandom.nextBoolean();
	}

	/** true or false (chance의 범위 : 0 ~ 1) */
	public static boolean randomBoolean(float chance) {
		return sRandom.nextFloat() < chance;
	}

	public static double randomGaussian() {
		return sRandom.nextGaussian();
	}
	
	public static void beginRandomUnique(int start, int end) {
		mUniqueStart = start;
		mUniqueEnd = end;
		int n = end - start + 1;
		mUniqueInts = new int[n];
		mUniqueUsed = new boolean[n];
		for(int i=0; i<n; i++) {
			mUniqueInts[i] = start + i;
			mUniqueUsed[i] = false;
		}
	}
	
	public static boolean isRandomUniqueEnded() {
		int n = mUniqueEnd - mUniqueStart + 1;
		for(int i=0; i<n; i++) {
			if(!mUniqueUsed[i]) return false;
		}
		return true;
	}
	
	public static int getRandomUnique() {
		int rand;
		while(true) {
			rand = random(mUniqueStart, mUniqueEnd);
			if(mUniqueUsed[rand]) continue;
			break;
		}
		
		mUniqueUsed[rand] = true;
		return mUniqueInts[rand];
	}

	public static int clamp(int value, int min, int max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	public static float clamp(float value, float min, float max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	public static double clamp(double value, double min, double max) {
		if(value < min) return min;
		if(value > max) return max;
		return value;
	}

	public static boolean isEqual(float a, float b) {
		return isEqual(a, b, EPSILON);
	}
	
	public static boolean isEqual(float a, float b, float epsilon) {
		return Math.abs(a - b) <= epsilon;
	}
	
	public static boolean isEqual(double a, double b) {
		return isEqual(a, b, EPSILON);
	}
	
	public static boolean isEqual(double a, double b, float epsilon) {
		return Math.abs(a - b) <= epsilon;
	}
	
	public static boolean isZero(float value) {
		return isZero(value, EPSILON);
	}
	
	public static boolean isZero(float value, float epsilon) {
		return Math.abs(value) <= epsilon;
	}
	
	public static boolean isZero(double value) {
		return isZero(value, EPSILON);
	}
	
	public static boolean isZero(double value, float epsilon) {
		return Math.abs(value) <= epsilon;
	}

}
