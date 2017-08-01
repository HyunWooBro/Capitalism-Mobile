package core.scene.stage.actor.drawable;

import core.framework.graphics.Form;
import core.framework.graphics.Sprite;
import core.framework.graphics.batch.Batch;

public class SpriteDrawable extends Drawable {
	
	private Sprite mSprite;

	public SpriteDrawable(Sprite sprite) {
		setSprite(sprite);
	}
	
	public void setSprite(Sprite sprite) {
		mSprite = new Sprite(sprite);
		setWidth(sprite.getRegionWidth());
		setHeight(sprite.getRegionHeight());
	}
	
	public Sprite getSprite() {
		return mSprite;
	}

	@Override
	public void draw(Batch batch, float dstX, float dstY, float dstWidth, float dstHeight, 
			boolean flipX, boolean flipY) {
		mSprite.getForm().moveTo(dstX, dstY).sizeTo(dstWidth, dstHeight).setFlipX(flipX).setFlipY(flipY);
		mSprite.draw(batch);
	}

	@Override
	public void draw(Batch batch, Form dstForm) {
		mSprite.setForm(dstForm);
		mSprite.draw(batch);
	}
	
	@Override
	public final float getDefaultWidth() {
		return mSprite.getRegionWidth();
	}

	@Override
	public final float getDefaultHeight() {
		return mSprite.getRegionHeight();
	}

}
