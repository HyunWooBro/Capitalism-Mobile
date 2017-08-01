package core.scene.stage.actor.action.relative;

public class ScaleBy extends RelativeAction {

    private float mScaleDeltaX;
    private float mScaleDeltaY;
    
    public ScaleBy(float scaleDeltaX, float scaleDeltaY) {
    	this(scaleDeltaX, scaleDeltaY, 0);
    }
    
    public ScaleBy(float scaleDeltaX, float scaleDeltaY, long duration) {
    	mScaleDeltaX = scaleDeltaX;
    	mScaleDeltaY = scaleDeltaY;
        setDuration(duration);
    }
    
    @Override
    protected void apply(float interpolatedTime) {
    	float sx = mScaleDeltaX * interpolatedTime;
    	float sy = mScaleDeltaY * interpolatedTime;
        mActor.scaleBy(sx, sy);
    }
    
    @Override
    protected void restore() {
    	float sx = mScaleDeltaX * -mLastInterpolatedTime;
    	float sy = mScaleDeltaY * -mLastInterpolatedTime;
        mActor.scaleBy(sx, sy);
    }
}
