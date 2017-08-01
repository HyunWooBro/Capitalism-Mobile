package core.scene.stage.actor.action.absolute;

public class AlphaTo extends AbsoluteAction {

	private float mFromAlpha;
    private float mToAlpha;
    
    public AlphaTo(float toAlpha) {
        this(toAlpha, 0);
    }
    
    public AlphaTo(float toAlpha, long duration) {
    	mToAlpha = toAlpha;
    	setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mFromAlpha = mActor.getAlpha();
    }
    
    @Override
    protected void apply(float interpolatedTime) {
    	float fromAlpha = mFromAlpha;
    	float toAlpha = mToAlpha;
    	float alpha = fromAlpha;
    	if(fromAlpha != toAlpha) alpha = fromAlpha + ((toAlpha - fromAlpha) * interpolatedTime);
		mActor.setAlpha(alpha);
    }
    
    @Override
    protected void restore() {
    	mActor.setAlpha(mFromAlpha);
    }
    
}
