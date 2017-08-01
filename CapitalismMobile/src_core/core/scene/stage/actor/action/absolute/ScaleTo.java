package core.scene.stage.actor.action.absolute;

public class ScaleTo extends AbsoluteAction {

	private float mScaleFromX;
	private float mScaleFromY;
    private float mScaleToX;
    private float mScaleToY;
    
    public ScaleTo(float scaleToX, float scaleToY) {
    	this(scaleToX, scaleToY, 0);
    }
    
    public ScaleTo(float scaleToX, float scaleToY, long duration) {
    	mScaleToX = scaleToX;
    	mScaleToY = scaleToY;
        setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mScaleFromX = mActor.getScaleX();
    	mScaleFromY = mActor.getScaleY();
    }

    @Override
    protected void apply(float interpolatedTime) {
    	float scaleFromX = mScaleFromX;
    	float scaleFromY = mScaleFromY;
    	float scaleToX = mScaleToX;
    	float scaleToY = mScaleToY;
    	float sx = scaleFromX;
    	float sy = scaleFromY;
    	if(scaleFromX != scaleToX) sx = scaleFromX + ((scaleToX - scaleFromX) * interpolatedTime);
        if(scaleFromY != scaleToY) sy = scaleFromY + ((scaleToY - scaleFromY) * interpolatedTime);
        mActor.scaleTo(sx, sy);
    }

    @Override
    protected void restore() {
    	mActor.scaleTo(mScaleFromX, mScaleFromY);
    }

}
