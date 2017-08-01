package core.scene.stage.actor.action.absolute;

public class MoveTo extends AbsoluteAction {
    
    private float mFromX;
    private float mFromY;
    private float mToX;
    private float mToY;

    public MoveTo(float toX, float toY) {
    	this(toX, toY, 0);
    }
    
    public MoveTo(float toX, float toY, long duration) {
    	mToX = toX;
    	mToY = toY;
    	setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mFromX = mActor.getX();
        mFromY = mActor.getY();
    }

    @Override
    protected void apply(float interpolatedTime) {
        float fromX = mFromX;
        float fromY = mFromY;
        float toX = mToX;
        float toY = mToY;
        float dx = fromX; 
        float dy = fromY;
        if(fromX != toX)	 dx = fromX + ((toX - fromX) * interpolatedTime);
        if(fromY != toY) dy = fromY + ((toY - fromY) * interpolatedTime);
        mActor.moveTo(dx, dy);
    }

    @Override
    protected void restore() {
    	mActor.moveTo(mFromX, mFromY);
    }
    
}
