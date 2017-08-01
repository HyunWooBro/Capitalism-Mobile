package core.scene.stage.actor.drawable;

import core.framework.graphics.Form;
import core.framework.graphics.Sprite;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.TextureRegion;

public abstract class Drawable {
	
	private float mWidth;
	private float mHeight;
	
	private float mPadTop;
	private float mPadLeft;
	private float mPadRight;
	private float mPadBottom;
	
	private float mAlpha = 1f;

	public Drawable() {
	}
	
	public void update(long time) {
	}
	
	public abstract void draw(Batch batch, float dstX, float dstY, float dstWidth, float dstHeight, 
			boolean flipX, boolean flipY);
	
	public abstract void draw(Batch batch, Form dstForm);
	
	/** Drawable의 초기 기본 너비를 구한다. 너비를 수정하는 경우에 대해 기준값으로 사용된다. */
	public abstract float getDefaultWidth();
	
	/** Drawable의 초기 기본 높이를 구한다. 높이를 수정하는 경우에 대해 기준값으로 사용된다. */
	public abstract float getDefaultHeight();
	
	public float getWidth() {
		return mWidth;
	}
	
	public float getHeight() {
		return mHeight;
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	public void setHeight(float height) {
		mHeight = height;
	}

	public float getPadTop() {
		return mPadTop;
	}

	public float getPadLeft() {
		return mPadLeft;
	}

	public float getPadRight() {
		return mPadRight;
	}

	public float getPadBottom() {
		return mPadBottom;
	}

	public void setPadTop(float padTop) {
		mPadTop = padTop;
	}

	public void setPadLeft(float padLeft) {
		mPadLeft = padLeft;
	}

	public void setPadRight(float padRight) {
		mPadRight = padRight;
	}

	public void setPadBottom(float padBottom) {
		mPadBottom = padBottom;
	}
	
	public float getAlpha() {
		return mAlpha;
	}
	
	public void setAlpha(float alpha) {
		mAlpha = alpha;
	}

	public boolean isSizeFixed() {
		return true;
	}
	
	public static Drawable newDrawable(Object object) {
		if(object == null) return null;
		
		if(object instanceof TextureRegion) return new TextureRegionDrawable((TextureRegion) object);
		if(object instanceof Animation) return new AnimationDrawable((Animation) object);
		if(object instanceof NinePatch) return new NinePatchDrawable((NinePatch) object);
		if(object instanceof Sprite) return new SpriteDrawable((Sprite) object);
		
		throw new IllegalArgumentException("No relevent Drawable could be found");
	}

}
