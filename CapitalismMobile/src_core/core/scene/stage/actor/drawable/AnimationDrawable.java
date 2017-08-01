package core.scene.stage.actor.drawable;

import java.util.List;

import core.framework.graphics.Form;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.FrameData;
import core.framework.graphics.texture.TextureRegion;

public class AnimationDrawable extends Drawable {
	
	private Animation mAnimation;
	
	private boolean mWidthFixed;
	private boolean mHeightFixed;

	public AnimationDrawable(Animation animation) {
		setAnimation(animation);
	}
	
	public void setAnimation(Animation animation) {
		mAnimation = new Animation(animation);
		mAnimation.init();
		if(mAnimation.isSizeFixed()) {
			int i = 0;
			TextureRegion region = null;
			List<FrameData> frameDataList = mAnimation.getFrameDataList();
			while(true) {
				region = frameDataList.get(i++).getKeyFrame();
				if(region == null) continue;
				break;
			}
			setWidth(region.getRegionWidth());
			setHeight(region.getRegionHeight());
		}
	}
	
	public Animation getAnimation() {
		return mAnimation;
	}

	@Override
	public void update(long time) {
		mAnimation.update(time);
	}

	@Override
	public void draw(Batch batch, float dstX, float dstY, float dstWidth, float dstHeight, 
			boolean flipX, boolean flipY) {
		TextureRegion region = mAnimation.getKeyFrame();
		if(region != null) batch.draw(region, dstX, dstY, dstWidth, dstHeight, flipX, flipY);
	}

	@Override
	public void draw(Batch batch, Form dstForm) {
		TextureRegion region = mAnimation.getKeyFrame();
		if(region != null) batch.draw(region, dstForm);
	}

	@Override
	public final float getDefaultWidth() {
		TextureRegion region = mAnimation.getKeyFrame();
		return (region != null)? region.getRegionWidth() : 0f;
	}

	@Override
	public final float getDefaultHeight() {
		TextureRegion region = mAnimation.getKeyFrame();
		return (region != null)? region.getRegionHeight() : 0f;
	}

	@Override
	public float getWidth() {
		if(mWidthFixed) return super.getWidth();
		return getDefaultWidth();
	}

	@Override
	public float getHeight() {
		if(mHeightFixed) return super.getHeight();
		return getDefaultHeight();
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		mWidthFixed = true;
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		mHeightFixed = true;
	}

	@Override
	public boolean isSizeFixed() {
		return mWidthFixed && mHeightFixed;
	}

}
