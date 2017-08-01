package core.scene.stage.actor.action.absolute;

public class PivotTo extends AbsoluteAction {
    
    private float mPivotFromX;
    private float mPivotFromY;
    private float mPivotToX;
    private float mPivotToY;

    public PivotTo(float PivotToX, float PivotToY) {
    	this(PivotToX, PivotToY, 0);
    }
    
    public PivotTo(float PivotToX, float PivotToY, long duration) {
    	mPivotToX = PivotToX;
    	mPivotToY = PivotToY;
    	setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mPivotFromX = mActor.getPivotX();
        mPivotFromY = mActor.getPivotY();
    }

    @Override
    protected void apply(float interpolatedTime) {
        float PivotFromX = mPivotFromX;
        float PivotFromY = mPivotFromY;
        float PivotToX = mPivotToX;
        float PivotToY = mPivotToY;
        float px = PivotFromX; 
        float py = PivotFromY;
        if(PivotFromX != PivotToX)	 px = PivotFromX + ((PivotToX - PivotFromX) * interpolatedTime);
        if(PivotFromY != PivotToY) py = PivotFromY + ((PivotToY - PivotFromY) * interpolatedTime);
        mActor.pivotTo(px, py);
    }

    @Override
    protected void restore() {
    	mActor.pivotTo(mPivotFromX, mPivotFromY);
    }
    

}
