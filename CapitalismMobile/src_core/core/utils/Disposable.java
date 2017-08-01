package core.utils;

public interface Disposable {
	
	/**
	 * 사용자가 직접 자원을 해제한다. 일반적으로 한번 버린 자원은 재사용할 수 없다.
	 */
	public void dispose ();
}
