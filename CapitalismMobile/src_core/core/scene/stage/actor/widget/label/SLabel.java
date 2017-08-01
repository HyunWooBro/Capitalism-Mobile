package core.scene.stage.actor.widget.label;

import core.framework.Core;
import core.framework.graphics.texture.FontTexture;
import core.framework.graphics.texture.Texture;
import core.scene.stage.actor.drawable.AnimationDrawable;
import core.scene.stage.actor.drawable.Drawable;

/**
 * SLabel은 StaticLabel의 약자이다. 미리 정의된 문자열을 출력하는 데 사용한다. 문자열을 수정할 수는 
 * 없지만 정적인 문자열을 출력하는 가장 빠른 방식이다.</p>
 * 
 * 한편, SLabel만 어떤 {@link Drawable}이든 출력할 수 있다.</p>
 * 
 * @see Label
 * @author 김현우
 */
public class SLabel extends Label<SLabel> {
	
	public SLabel(int stringID, Texture texture) {
		this(Drawable.newDrawable(texture.getTextureRegion(stringID)), 
				Core.APP.getResources().getString(stringID));
	}
	
	public SLabel(int stringArrayID, int index, Texture texture) {
		this(Drawable.newDrawable(texture.getTextureRegion(stringArrayID, index)),
				Core.APP.getResources().getStringArray(stringArrayID)[index]);
	}
	
	/** {@link #getText()}는 null을 리턴한다. */
	public SLabel(Drawable drawable) {
		this(drawable, null);
	}
	
	public SLabel(Drawable drawable, String text) {
		setDrawable(drawable, text);
	}
	
	public SLabel(SLabel label) {
		super(label);
		setDrawable(label.getDrawable(), label.getText());
	}
	
	@Override
	protected void updateDrawable(long time) {
		if(mDrawable != null) {
			mDrawable.update(time);
			if(!mDrawable.isSizeFixed()) pack();
		}
	}
	
	@Override
	protected float getDefaultPrefHeight() {
		if(mDrawable == null) return 0f;
		float scale = FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		return mDrawable.getHeight( ) / scale;
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		if(mDrawable == null) return 0f;
		float scale = FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		return mDrawable.getWidth() / scale;
	}
	
	public Drawable getDrawable() {
		return mDrawable;
	}
	
	/** {@link #getText()}는 null을 리턴한다. */
	public SLabel setDrawable(Drawable drawable) {
		return setDrawable(drawable, null);
	}
	
	/** SLabel은 동적으로 다른 Drawable로 교체한다. */
	public SLabel setDrawable(Drawable drawable, String text) {
		if(mDrawable == drawable) return this;
		mDrawable = drawable;
		mText = text;
		onTextChanged();
		pack();
		return this;
	}
	
	@Override
	public int getNumLines() {
		return 1;
	}
	
}
