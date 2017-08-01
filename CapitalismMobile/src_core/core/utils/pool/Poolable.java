package core.utils.pool;

/**
 * {@link Pool#recycle(Object)}를 처리할 때 매개변수로 전달되는 객체가 Poolable을 구현하는 경우, 
 * 그 객체의 {@link #recycle()}메서드를 호출한다. 그렇다고 Pool을 사용하기 위해 Poolable을 반드시 
 * 구현해야 하는 것은 아니다. 단지 {@link #recycle()}를 자동으로 호출하느냐의 차이일 뿐이다.
 * 
 * @author 김현우
 */
public interface Poolable {

	/**
	 * Pool에서 다시 사용할 수 있도록 재활용한다. 새로 객체가 생성된 상태로 초기화한다. 
	 * Pool에서 직접 호출하기 때문에 유저가 직접 호출할 경우는 없다.
	 */
	public void recycle();
}
