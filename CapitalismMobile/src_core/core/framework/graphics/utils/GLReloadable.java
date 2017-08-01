package core.framework.graphics.utils;

public interface GLReloadable {
	
	/**
	 * Context lost에 대해 복구하기 위해 호출된다. 사용자가 직접 호출할 경우는 없다.
	 */
	public void reload();
}
