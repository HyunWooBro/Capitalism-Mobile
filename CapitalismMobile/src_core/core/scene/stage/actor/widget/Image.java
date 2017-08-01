package core.scene.stage.actor.widget;

import core.framework.graphics.Sprite;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.RectangleAlignable;

public class Image extends Widget<Image> implements RectangleAlignable<Image> {
	
	public enum ScaleType {
		/** 
		 * 이미지의 종횡비를 유지하며 지정한 사각 영역을 벗어나지 않는 선에서 
		 * 최대한 맞춘다. 한 축이 지정한 사이즈보다 작을 수 있다.
		 */
		FIT, 
		/** 
		 * 이미지의 종횡비를 유지하며 지정한 사각 영역을 가득 채운다. 한 축이 
		 * 지정한 사이즈보다 클 수 있다. 
		 */
		FILL, 
		/** 
		 * 이미지의 종횡비를 유지하며 지정한 y축을 채운다. x축의 경우, 지정한 
		 * 사이즈보다 크거나 작을 수 있다.
		 */
		FILL_X, 
		/** 
		 * 이미지의 종횡비를 유지하며 지정한 x축을 채운다. y축의 경우, 지정한 
		 * 사이즈보다 크거나 작을 수 있다.
		 */
		FILL_Y, 
		/** 이미지의 종횡비와는 상관없이 지정한 사이즈로 늘린다. */
		STRETCH,
		/** 이미지의 종횡비와는 상관없이 x축만 지정한 사이즈로 늘린다. */
		STRETCH_X, 
		/** 이미지의 종횡비와는 상관없이 y축만 지정한 사이즈로 늘린다. */
		STRETCH_Y, 
		/** 지정한 사이즈와는 상관없이 원본 사이즈로 항상 출력된다. */
		NONE;
		
		public void apply(Image image) {
			
			float srcWidth = image.mDrawable.getWidth();
			float srcHeight = image.mDrawable.getHeight();
			
			float dstWidth = image.getWidth();
			float dstHeight = image.getHeight();
			
			switch(this) {
				case FIT: {
					float widthScale = dstWidth / srcWidth;
					float heightScale = dstHeight / srcHeight;
					float scale = (widthScale > heightScale)? heightScale : widthScale;
					image.mImageWidth = srcWidth * scale;
					image.mImageHeight = srcHeight * scale;
					break;
				}
				case FILL: {
					float widthScale = dstWidth / srcWidth;
					float heightScale = dstHeight / srcHeight;
					float scale = (widthScale < heightScale)? heightScale : widthScale;
					image.mImageWidth = srcWidth * scale;
					image.mImageHeight = srcHeight * scale;
					break;
				}
				case FILL_X: {
					float scale = dstWidth / srcWidth;
					image.mImageWidth = srcWidth * scale;
					image.mImageHeight = srcHeight * scale;
					break;
				}
				case FILL_Y: {
					float scale = dstHeight / srcHeight;
					image.mImageWidth = srcWidth * scale;
					image.mImageHeight = srcHeight * scale;
					break;
				}
				case STRETCH:
					image.mImageWidth = dstWidth;
					image.mImageHeight = dstHeight;
					break;
				case STRETCH_X:
					image.mImageWidth = dstWidth;
					image.mImageHeight = srcHeight;
					break;
				case STRETCH_Y:
					image.mImageWidth = srcWidth;
					image.mImageHeight = dstHeight;
					break;
				case NONE:
					image.mImageWidth = srcWidth;
					image.mImageHeight = srcHeight;
					break;
			}
		}
	}
	
	protected Align mAlign = new Align();
	
	protected ScaleType mScaleType = ScaleType.STRETCH;
	
	protected float mImageX;
	protected float mImageY;
	
	protected float mImageWidth;
	protected float mImageHeight;
	
	private boolean mDisableSizeChanged;
	
	public Image() {
	}
	
	public Image(TextureRegion region) {
		this(Drawable.newDrawable(region));
	}
	
	public Image(Animation animation) {
		this(Drawable.newDrawable(animation));
	}
	
	public Image(Sprite sprite) {
		this(Drawable.newDrawable(sprite));
	}
	
	public Image(NinePatch patch) {
		this(Drawable.newDrawable(patch));
	}

	public Image(Drawable drawable) {
		setDrawable(drawable);
	}
	
	@Override
	public void layout() {
		if(mDrawable == null) return;
		
		mScaleType.apply(this);
		
		float width = getWidth();
		float height = getHeight();
		
		switch(mAlign.getHAlign()) {
			case LEFT:		mImageX = 0f;
				break;
			case CENTER:	mImageX = (width - mImageWidth)/2;
				break;
			case RIGHT:		mImageX = width - mImageWidth;
				break;
		}
		
		switch(mAlign.getVAlign()) {
			case TOP:		mImageY = 0f;
				break;
			case CENTER:	mImageY = (height - mImageHeight)/2;
				break;
			case BOTTOM:	mImageY = height - mImageHeight;
				break;
		}
	}
	
	@Override
	public float getMinWidth() {
		return getPrefWidth()/10;
	}
	
	@Override
	public float getMinHeight() {
		return getPrefHeight()/10;
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		if(mDrawable != null) return mDrawable.getWidth();
		return 0f;
	}

	@Override
	protected float getDefaultPrefHeight() {
		if(mDrawable != null) return mDrawable.getHeight();
		return 0f;
	}

	@Override
	protected void updateDrawable(long time) {
		if(mDrawable != null) {
			mDrawable.update(time);
			if(!mDrawable.isSizeFixed()) pack();
		}
	}

	@Override
	protected void drawSelf(Batch batch, float parentAlpha) {
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		
		mDisableSizeChanged = true;
		
		moveTo(x+mImageX, y+mImageY);
		sizeTo(mImageWidth, mImageHeight);
		super.drawSelf(batch, parentAlpha);
		sizeTo(width, height);
		moveTo(x, y);
		
		mDisableSizeChanged = false;
	}
	
	public Drawable getDrawable() {
		return mDrawable;
	}

	public Image setDrawable(Drawable drawable) {
		if(mDrawable == drawable) return this;
		mDrawable = drawable;
		if(drawable != null)	pack();
		else							sizeTo(0f, 0f);
		invalidateHierarchy();
		return this;
	}

	@Override
	protected void onSizeChanged() {
		if(mDisableSizeChanged) return;
		super.onSizeChanged();
	}

	@Override
	public Image center() {
		mAlign.center();
		invalidate();
		return this;
	}
	
	@Override
	public Image top() {
		mAlign.top();
		invalidate();
		return this;
	}

	@Override
	public Image left() {
		mAlign.left();
		invalidate();
		return this;
	}
	
	@Override
	public Image right() {
		mAlign.right();
		invalidate();
		return this;
	}
	
	@Override
	public Image bottom() {
		mAlign.bottom();
		invalidate();
		return this;
	}

	@Override
	public Image north() {
		mAlign.north();
		invalidate();
		return this;
	}

	@Override
	public Image west() {
		mAlign.west();
		invalidate();
		return this;
	}
	
	@Override
	public Image east() {
		mAlign.east();
		invalidate();
		return this;
	}
	
	@Override
	public Image south() {
		mAlign.south();
		invalidate();
		return this;
	}

	@Override
	public Image northWest() {
		mAlign.northWest();
		invalidate();
		return this;
	}
	
	@Override
	public Image northEast() {
		mAlign.northEast();
		invalidate();
		return this;
	}
	
	@Override
	public Image southWest() {
		mAlign.southWest();
		invalidate();
		return this;
	}
	
	@Override
	public Image southEast() {
		mAlign.southEast();
		invalidate();
		return this;
	}
	
	@Override
	public Align getAlign() {
		return mAlign;
	}

	@Override
	public Image setAlign(Align align) {
		mAlign.set(align);
		invalidate();
		return this;
	}

	public ScaleType getScaleType() {
		return mScaleType;
	}

	public Image setScaleType(ScaleType scaleType) {
		mScaleType = scaleType;
		invalidate();
		return this;
	}

	public float getImageX() {
		return mImageX;
	}

	public float getImageY() {
		return mImageY;
	}

	public float getImageWidth() {
		return mImageWidth;
	}

	public float getImageHeight() {
		return mImageHeight;
	}
	
}
