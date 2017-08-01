package core.scene.stage.actor.action.relative;

public class AlphaBy extends RelativeAction {

	private float mDeltaAlpha;
    
    public AlphaBy(float deltaAlpha) {
        this(deltaAlpha, 0);
    }
    
    public AlphaBy(float deltaAlpha, long duration) {
    	mDeltaAlpha = deltaAlpha;
    	setDuration(duration);
    }
    
    @Override
    protected void apply(float interpolatedTime) {
    	float alpha = mDeltaAlpha * interpolatedTime;
		mActor.addColor(alpha, 0f, 0f, 0f);
    }
    
    @Override
    protected void restore() {
    	float alpha = mDeltaAlpha * -mLastInterpolatedTime;
    	mActor.addColor(alpha, 0f, 0f, 0f);
    }
	
}
