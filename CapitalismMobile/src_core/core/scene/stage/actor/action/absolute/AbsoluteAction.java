package core.scene.stage.actor.action.absolute;

import core.scene.stage.actor.action.Action;

/**
 * AbsoluteAction은 Form의 특정 속성을 정해진 시작 값과 끝 값을 보간하여 얻어진 값으로 변경한다. 
 * 따라서 이 Action이 적용되는 중간에는 관련 속성을 수정해도 제대로 적용되지 않을 수 있다.</p>
 * 
 * 기본적으로 {@link #initialize()}에서 {@link #ensureInterpolator()}를 호출하여 Interpolator을 
 * 보장한다.</p>  
 * 
 * @author 김현우
 */
public abstract class AbsoluteAction extends Action {
	
	@Override
	protected void initialize() {
		ensureInterpolator();
	}

}
