package core.scene.stage.actor;

import core.scene.stage.actor.drawable.Drawable;

public class ExtraGroup extends Group<ExtraGroup> {
	
	public Drawable getDrawable() {
		return mDrawable;
	}
	
	public ExtraGroup setDrawable(Drawable drawable) {
		return setDrawable(drawable, true);
	}
	
	public ExtraGroup setDrawable(Drawable drawable, boolean update) {
		mDrawable = drawable;
		if(drawable != null && update) sizeTo(drawable.getWidth(), drawable.getHeight());
		return this;
	}
}
