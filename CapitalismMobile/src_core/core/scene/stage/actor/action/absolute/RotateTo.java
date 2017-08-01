package core.scene.stage.actor.action.absolute;

public class RotateTo extends AbsoluteAction {
	
	private float mFromAngle;
    private float mToAngle;
    
    public RotateTo(float toAngle) {
    	this(toAngle, 0);
    }
    
    public RotateTo(float toAngle, long duration) {
    	mToAngle = toAngle;
        setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mFromAngle = mActor.getRotation();
    }
    
    @Override
    protected void apply(float interpolatedTime) {
    	float fromAngle = mFromAngle;
    	float toAngle = mToAngle;
    	float angle = fromAngle; 
        if(fromAngle != toAngle)	angle = fromAngle + ((toAngle - fromAngle) * interpolatedTime);
    	mActor.rotateTo(angle);
    }
    
    @Override
    protected void restore() {
    	mActor.rotateTo(mFromAngle);
    }

}
