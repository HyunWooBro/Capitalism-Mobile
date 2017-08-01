package core.scene.stage.actor.action.absolute;

public class FadeOut extends AbsoluteAction {

	private float mFromAlpha;
    private float mToAlpha;
    
    private boolean mAlphaFromForm;

    public FadeOut() {
    	this(true, 0);
    }
    
    public FadeOut(long duration) {
    	this(true, duration);
    }
    
    public FadeOut(boolean fromOne, long duration) {
    	if(!fromOne) mAlphaFromForm = true;
    	mToAlpha = 0f;
    	setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mFromAlpha = (!mAlphaFromForm)? 1f : mActor.getAlpha();
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
