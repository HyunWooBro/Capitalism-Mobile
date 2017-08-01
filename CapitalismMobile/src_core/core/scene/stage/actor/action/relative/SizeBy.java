package core.scene.stage.actor.action.relative;

public class SizeBy extends RelativeAction {
    
    private float mDeltaWidth;
    private float mDeltaHeight;
    
    public SizeBy(float deltaWidth, float deltaHeight) {
    	this(deltaWidth, deltaHeight, 0);
    }
    
    public SizeBy(float deltaWidth, float deltaHeight, long duration) {
    	mDeltaWidth = deltaWidth;
    	mDeltaHeight = deltaHeight;
        setDuration(duration);
    }
    
    @Override
    protected void apply(float interpolatedTime) {
    	float width = mDeltaWidth * interpolatedTime;
    	float height = mDeltaHeight * interpolatedTime;
    	mActor.sizeBy(width, height);
    }
    
    @Override
    protected void restore() {
    	float width = mDeltaWidth * -mLastInterpolatedTime;
    	float height = mDeltaHeight * -mLastInterpolatedTime;
    	mActor.sizeBy(width, height);
    }

}
