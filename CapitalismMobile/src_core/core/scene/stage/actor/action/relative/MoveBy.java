package core.scene.stage.actor.action.relative;

public class MoveBy extends RelativeAction {
    
    private float mDeltaX;
    private float mDeltaY;

    public MoveBy(float dx, float dy) {
    	this(dx, dy, 0);
    }
    
    public MoveBy(float dx, float dy, long duration) {
    	mDeltaX = dx;
    	mDeltaY = dy;
    	setDuration(duration);
    }

    @Override
    protected void apply(float interpolatedTime) {
		float dx = mDeltaX * interpolatedTime;
		float dy = mDeltaY * interpolatedTime;
		mActor.moveBy(dx, dy);
    }
    
    @Override
    protected void restore() {
    	float dx = mDeltaX * -mLastInterpolatedTime;
    	float dy = mDeltaY * -mLastInterpolatedTime;
		mActor.moveBy(dx, dy);
    }

}
