package core.scene.stage.actor.drawable;

import core.framework.graphics.Form;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.NinePatch;

public class NinePatchDrawable extends Drawable {
	
	private NinePatch mNinePatch;

	public NinePatchDrawable(NinePatch ninePatch) {
		setPatch(ninePatch);
	}
	
	public void setPatch(NinePatch ninePatch) {
		mNinePatch = new NinePatch(ninePatch);
		setWidth(ninePatch.getDefaultWidth());
		setHeight(ninePatch.getDefaultHeight());
		setPadTop(ninePatch.getTopHeight());
		setPadLeft(ninePatch.getLeftWidth());
		setPadRight(ninePatch.getRightWidth());
		setPadBottom(ninePatch.getBottomHeight());
	}
	
	public NinePatch getPatch() {
		return mNinePatch;
	}

	@Override
	public void draw(Batch batch, float dstX, float dstY, float dstWidth, float dstHeight, boolean flipX, boolean flipY) {
		mNinePatch.draw(batch, dstX, dstY, dstWidth, dstHeight, flipX, flipY);
	}

	@Override
	public void draw(Batch batch, Form dstForm) {
		mNinePatch.draw(batch, dstForm);
	}

	@Override
	public final float getDefaultWidth() {
		return mNinePatch.getDefaultWidth();
	}

	@Override
	public final float getDefaultHeight() {
		return mNinePatch.getDefaultHeight();
	}

}
