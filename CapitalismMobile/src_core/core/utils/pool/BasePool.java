package core.utils.pool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * {@link Pool}을 확장하여 {@link #newObject()}에서 리플렉션을 이용하여 대상이 되는 객체의 매개변수 없는 
 * 기본 생성자를 통해 생성된 인스턴스를 반환한다. 따라서 기본 생성자가 제공되어야 한다.
 * 
 * @author 김현우
 */
public class BasePool<T> extends Pool<T> {
	
	private Class<?> mClazz;
	
	public BasePool(Class<?> clazz) {
		this(clazz, 10, Integer.MAX_VALUE);
	}
	
	public BasePool(Class<?> clazz, int capacity, int max) {
		super(capacity, max);
		mClazz = clazz;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T newObject() {
		try {
			return (T) mClazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new RuntimeException("Exception has occured during newObject().");
	}

}
