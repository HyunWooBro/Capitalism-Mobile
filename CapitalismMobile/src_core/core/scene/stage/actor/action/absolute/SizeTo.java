package core.scene.stage.actor.action.absolute;

public class SizeTo extends AbsoluteAction {
	
	private float mFromWidth;
    private float mFromHeight;
    private float mToWidth;
    private float mToHeight;
    
    public SizeTo(float toWidth, float toHeight) {
    	this(toWidth, toHeight, 0);
    }
    
    public SizeTo(float toWidth, float toHeight, long duration) {
    	mToWidth = toWidth;
    	mToHeight = toHeight;
        setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
		mFromWidth = mActor.getWidth();
		mFromHeight = mActor.getHeight();
    }
    
    @Override
    protected void apply(float interpolatedTime) {
    	float fromWidth = mFromWidth;
    	float fromHeight = mFromHeight;
    	float toWidth = mToWidth;
    	float toHeight = mToHeight;
    	float width = fromWidth;
    	float height = fromHeight;
    	if(fromWidth != toWidth)	width = fromWidth + ((toWidth - fromWidth) * interpolatedTime);
    	if(fromHeight != toHeight) height = fromHeight + ((toHeight - fromHeight) * interpolatedTime);
    	mActor.sizeTo(width, height);
    }
    
    @Override
    protected void restore() {
    	mActor.sizeTo(mFromWidth, mFromHeight);
    }

}
