package core.scene.stage.actor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Stage에 등장할 Actor를 캐스팅한다. 복잡한 스타일의 Actor을 미리 정의하여 반복적으로 
 * 사용하고자 할 때 유용하다.  
 * 
 * @author 김현우
 */
public class CastingDirector {
	
	private String mLocation;
	
	private Map<String, Casting<?>> mStringToCastingMap = new HashMap<String, Casting<?>>();
	
	/** 싱글턴 인스턴스 */
	private volatile static CastingDirector sInstance;
	private CastingDirector() {}
	public static CastingDirector getInstance() {
		if(sInstance == null) {
			synchronized(CastingDirector.class) {
				if(sInstance == null) sInstance = new CastingDirector();
			}
		}
		return sInstance;
	}
	
	public void setLocation(String location) {
		mLocation = location;
	}
	
	public <T extends Actor<?>> T cast(Class<T> clazz, String style, Object... args) {
		if(mLocation == null)
			throw new IllegalArgumentException("mLocation must be set.");
		
		String name = clazz.getSimpleName();
		
		StringBuilder builder = new StringBuilder();
		builder.append(mLocation).append(".").append(name).append("Casting");

		try {
			Class<?> c = Class.forName(builder.toString());
			Method m = c.getMethod("cast", String.class, Object[].class);
			Casting<?> casting = mStringToCastingMap.get(name);
			if(casting == null) {
				casting = (Casting<?>) c.newInstance();
				mStringToCastingMap.put(name, casting);
			}
			return clazz.cast(m.invoke(casting, style, args));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new RuntimeException("Exception has occured during cast(...).");
	}
	
	public static interface Casting<T extends Actor<?>> {
		public T cast(String style, Object[] args);
	}

}
