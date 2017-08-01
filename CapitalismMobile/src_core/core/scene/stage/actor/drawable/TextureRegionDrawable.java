package core.scene.stage.actor.drawable;

import core.framework.graphics.Form;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.TextureRegion;
import core.framework.graphics.texture.TextureRegion.ImmutableTextureRegion;

public class TextureRegionDrawable extends Drawable {
	
	private TextureRegion mTextureRegion;

	public TextureRegionDrawable(TextureRegion textureRegion) {
		setRegion(textureRegion);
	}
	
	/** 사본을 저장한다. */
	public void setRegion(TextureRegion region) {
		mTextureRegion = new TextureRegion(region);
		setWidth(region.getRegionWidth());
		setHeight(region.getRegionHeight());
	}
	
	public void setRegion(ImmutableTextureRegion region) {
		mTextureRegion = region;
		setWidth(region.getRegionWidth());
		setHeight(region.getRegionHeight());
	}
	
	public TextureRegion getRegion() {
		return mTextureRegion;
	}

	@Override
	public void draw(Batch batch, float dstX, float dstY, float dstWidth, float dstHeight, 
			boolean flipX, boolean flipY) {
		batch.draw(mTextureRegion, dstX, dstY, dstWidth, dstHeight, flipX, flipY);
	}

	@Override
	public void draw(Batch batch, Form dstForm) {
		batch.draw(mTextureRegion, dstForm);
	}

	@Override
	public final float getDefaultWidth() {
		return mTextureRegion.getRegionWidth();
	}

	@Override
	public final float getDefaultHeight() {
		return mTextureRegion.getRegionHeight();
	}

}
