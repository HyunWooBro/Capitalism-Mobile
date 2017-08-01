package core.scene.stage.actor.action.relative;

public class RotateBy extends RelativeAction {
	
    private float mDeltaAngle;
    
    public RotateBy(float deltaAngle) {
    	this(deltaAngle, 0);
    }
    
    public RotateBy(float deltaAngle, long duration) {
        mDeltaAngle = deltaAngle;
        setDuration(duration);
    }

    @Override
    protected void apply(float interpolatedTime) {
        float angle =  mDeltaAngle * interpolatedTime;
    	mActor.rotateBy(angle);
    }
    
    @Override
    protected void restore() {
    	float angle = mDeltaAngle * -mLastInterpolatedTime;
    	mActor.rotateBy(angle);
    }

}
