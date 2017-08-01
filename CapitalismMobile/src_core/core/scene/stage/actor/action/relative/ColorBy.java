package core.scene.stage.actor.action.relative;

import core.framework.graphics.Color4;
import core.framework.graphics.Color4.ColorType;

public class ColorBy extends RelativeAction {

	private Color4 mDeltaColor = new Color4();

    public ColorBy(Color4 deltaColor) {
    	this(ColorType.FULL, null, 0);
    	mDeltaColor.set(deltaColor);
    }
    
    public ColorBy(Color4 deltaColor, long duration) {
    	this(ColorType.FULL, null, duration);
    	mDeltaColor.set(deltaColor);
    }
    
    public ColorBy(ColorType type, Float deltaColor) {
    	this(type, deltaColor, 0);
    }
    
    public ColorBy(ColorType type, Float deltaColor, long duration) {
    	float color = 0f;
    	if(deltaColor != null) color = deltaColor;
    	switch(type) {
	    	case FULL:
	    		if(deltaColor != null)
	    			mDeltaColor.set(color, color, color, color);
				break;
	    	case ALPHA:
	    		mDeltaColor.set(color, 0, 0, 0);
	    		break;
	    	case RED:
	    		mDeltaColor.set(0, color, 0, 0);
	    		break;
	    	case GREEN:
	    		mDeltaColor.set(0, 0, color, 0);
	    		break;
	    	case BLUE:
	    		mDeltaColor.set(0, 0, 0, color);
	    		break;
		}
    	setDuration(duration);
    }
	
	@Override
    protected void apply(float interpolatedTime) {
        Color4 deltaColor = mDeltaColor;
        float alpha = deltaColor.a * interpolatedTime;
        float red = deltaColor.r * interpolatedTime;
        float green = deltaColor.g * interpolatedTime;
        float blue = deltaColor.b * interpolatedTime;
        mActor.addColor(alpha, red, green, blue);
    }
	
	@Override
	protected void restore() {
		Color4 deltaColor = mDeltaColor;
        float alpha = deltaColor.a * -mLastInterpolatedTime;
        float red = deltaColor.r * -mLastInterpolatedTime;
        float green = deltaColor.g * -mLastInterpolatedTime;
        float blue = deltaColor.b * -mLastInterpolatedTime;
        mActor.addColor(alpha, red, green, blue);
    }

}
