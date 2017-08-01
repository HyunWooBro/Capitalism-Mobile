package core.scene.stage.actor.action.relative;

import core.scene.stage.actor.action.Action;

/**
 * RelativeAction은 Form의 특정 속성을 설정된 변화량만큼 변경한다.</p>
 * 
 * 기본적으로 {@link #initialize()}에서 {@link #ensureInterpolator()}를 호출하여 Interpolator을 
 * 보장한다.</p>
 * 
 * @author 김현우
 */
public abstract class RelativeAction extends Action {
	
	 protected float mLastInterpolatedTime;
	 
	 @Override
	protected void initialize() {
		ensureInterpolator();
	}
	 
	 @Override
	public void reset() {
		super.reset();
		mLastInterpolatedTime = 0f;
	}

	@Override
	protected float getInterpolatedTime(float normalizedTime) {
        float interpolatedTime = super.getInterpolatedTime(normalizedTime);
        float delta = interpolatedTime - mLastInterpolatedTime;
        mLastInterpolatedTime = interpolatedTime;
		return delta;
	}
}
