package core.scene.stage.actor.action.absolute;

public class FadeIn extends AbsoluteAction {
	
	private float mFromAlpha;
    private float mToAlpha;
    
    private boolean mAlphaFromForm;
    
    public FadeIn() {
    	this(true, 0);
    }
    
    public FadeIn(long duration) {
    	this(true, duration);
    }
    
    public FadeIn(boolean fromZero, long duration) {
    	if(!fromZero) mAlphaFromForm = true;
    	mToAlpha = 1f;
    	setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mFromAlpha = (!mAlphaFromForm)? 0f : mActor.getAlpha();
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
