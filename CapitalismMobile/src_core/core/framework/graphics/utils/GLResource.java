package core.framework.graphics.utils;

import core.framework.graphics.GraphicsManager;
import core.utils.Disposable;

/**
 * GPU 메모리에 상주하는 GL 자원의 기본적인 메서드의 원형을 정의한다. 이들 자원은 
 * 유저가 중간에 홈으로 가거나 다른 앱을 띄울 경우 자동 해제되는데 
 * {@link GraphicsManager}에 의해 {@link #reload()}를 호출하여 복구한다.
 * 
 * @author 김현우
 */
public interface GLResource extends GLReloadable, Disposable {

	/**
	 * GL자원을 바인딩한다. 처음 바인딩하는 경우에 해당 자원의 아이디와 
	 * 빈 자원 객체가 생성되어 연결된다. 
	 */
	public void bind();
	
	/**
	 * GL자원의 바인딩을 해제한다.
	 */
	public void unbind();
	
	/**                                     
	 * CPU 메모리(RAM)에 있는 자원을 GPU 메모리(VRAM)에 로드하거나, 앞으로의 업데이트를 
	 * 위해 미리 작업공간을 GPU 메모리에 생성한다. 호출하기 전에 적절한 바인딩이 요구된다.
	 */
	public void load();
	
	/** 
	 * GPU 메모리에 만들어진 작업공간을 업데이트한다. 업데이할 내용은 자원마다 다르므로 
	 * Object를 통해 전달한다.
	 */
	public void update(Object content);
	
	@Override
	public void reload();
	
	@Override
	public void dispose();
}