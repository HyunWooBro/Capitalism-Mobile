package core.scene.stage.actor;

import core.scene.stage.actor.drawable.Drawable;

public class Extra extends Actor<Extra> {
	
	public Drawable getDrawable() {
		return mDrawable;
	}
	
	public Extra setDrawable(Drawable drawable) {
		return setDrawable(drawable, true);
	}
	
	public Extra setDrawable(Drawable drawable, boolean update) {
		mDrawable = drawable;
		if(drawable != null && update) sizeTo(drawable.getWidth(), drawable.getHeight());
		return this;
	}
}
