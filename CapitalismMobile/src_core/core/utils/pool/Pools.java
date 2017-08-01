package core.utils.pool;

import java.util.HashMap;
import java.util.Map;

/**
 * Pool을 간편하게 사용할 수 있는 유틸 클래스</p>
 * 
 * @author 김현우
 */
public class Pools {
	
	private static Map<Class<?>, Pool<?>> sClassToPoolMap = new HashMap<Class<?>, Pool<?>>();
	
	private Pools() {
	}
	
	/** 
	 * clazz에 대한 객체 및 기본 생성자에 접근할 수 있어야 한다. 다른 클래스에 속한 
	 * 중첩된 클래스의 경우 반드시 static으로 정의해야 한다.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T obtain(Class<T> clazz) {
		Pool<?> pool = sClassToPoolMap.get(clazz);
		if(pool == null) {
			pool = new BasePool<T>(clazz);
			sClassToPoolMap.put(clazz, pool);
		}
		return (T) pool.obtain();
	}
	
	public static <T> void recycle(T element) {
		if(element == null) throw new IllegalArgumentException("element can't be null.");
		@SuppressWarnings("unchecked")
		Pool<T> pool = (Pool<T>) sClassToPoolMap.get(element.getClass());
		if(pool == null) return;
		pool.recycle((T) element);
	}

}
