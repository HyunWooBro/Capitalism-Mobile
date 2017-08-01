package core.scene.stage.actor.action.absolute;

import core.framework.graphics.Color4;
import core.framework.graphics.Color4.ColorType;

public class ColorTo extends AbsoluteAction {
	
	private ColorType mType;

	private Color4 mFromColor = new Color4();
    private Color4 mToColor = new Color4();
    
    private Float mToPartialColor;
    
    public ColorTo(Color4 toColor) {
    	this(ColorType.FULL, null, 0);
    	mToColor.set(toColor);
    }
    
    public ColorTo(Color4 toColor, long duration) {
    	this(ColorType.FULL, null, duration);
    	mToColor.set(toColor);
    }
    
    public ColorTo(ColorType type, Float toColor) {
    	this(type, toColor, 0);
    }
    
    public ColorTo(ColorType type, Float toColor, long duration) {
    	mType = type;
    	mToPartialColor = toColor;
    	setDuration(duration);
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
    	mFromColor.set(mActor.getColor());
    	
    	float toPartialColor = 0f;
    	if(mToPartialColor != null) toPartialColor = mToPartialColor;
    	switch(mType) {
	    	case FULL:
	    		if(mToPartialColor != null)
	    			mToColor.set(toPartialColor, toPartialColor, toPartialColor, toPartialColor);
				break;
	    	case ALPHA:
	    		mToColor.set(mFromColor);
	    		mToColor.a = toPartialColor;
	    		break;
	    	case RED:
	    		mToColor.set(mFromColor);
	    		mToColor.r = toPartialColor;
	    		break;
	    	case GREEN:
	    		mToColor.set(mFromColor);
	    		mToColor.g = toPartialColor;
	    		break;
	    	case BLUE:
	    		mToColor.set(mFromColor);
	    		mToColor.b = toPartialColor;
	    		break;
		}
    }
    
    @Override
    protected void apply(float interpolatedTime) {
        Color4 fromColor = mFromColor;
        Color4 toColor = mToColor;
        float fromAlpha = fromColor.a;
        float fromRed = fromColor.r;
        float fromGreen = fromColor.g;
        float fromBlue = fromColor.b;
        float toAlpha = toColor.a;
        float toRed = toColor.r;
        float toGreen = toColor.g;
        float toBlue = toColor.b;
        float alpha = fromAlpha;
        float red = fromRed;
        float green = fromGreen;
        float blue = fromBlue;
        if(fromAlpha != toAlpha) alpha = fromAlpha + ((toAlpha - fromAlpha) * interpolatedTime);
        if(fromRed != toRed) red = fromRed + ((toRed - fromRed) * interpolatedTime);
        if(fromGreen != toGreen) green = fromGreen + ((toGreen - fromGreen) * interpolatedTime);
        if(fromBlue != toBlue) blue = fromBlue + ((toBlue - fromBlue) * interpolatedTime);	
		switch(mType) {
	    	case FULL:
	    		mActor.setColor(alpha, red, green, blue);
				break;
	    	case ALPHA:
	    		mActor.setAlpha(alpha);
	    		break;
	    	case RED:
	    		mActor.setRed(red);
	    		break;
	    	case GREEN:
	    		mActor.setGreen(green);
	    		break;
	    	case BLUE:
	    		mActor.setBlue(blue);
	    		break;
		}
    }
    
    @Override
    protected void restore() {
    	mActor.setColor(mFromColor);
    }

}
