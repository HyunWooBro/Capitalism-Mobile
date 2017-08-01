package core.utils;

import android.util.Pair;

/**
 * 안드로이드 {@link Pair}의 변경 가능한 버전이다.
 * 
 * @author 김현우
 * @see Pair
 */
public class Couple<F, S> {
	
    public F first;
    public S second;

    public Couple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Couple<A, B> create(A a, B b) {
        return new Couple<A, B>(a, b);
    }
}
