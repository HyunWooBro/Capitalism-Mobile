package core.scene.stage.actor.widget.label;

import android.graphics.Paint;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.FontTexture;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureRegion;
import core.framework.graphics.texture.TextureRegion.ImmutableTextureRegion;
import core.framework.graphics.utils.GLReloadable;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.drawable.TextureRegionDrawable;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.utils.Disposable;

/**
 * DLabel은 DynamicLabel의 약자이다. 실시간으로 String을 Bitmap에 그린 후 텍스쳐에 올리는 과정을 거친다. 
 * 어떤 문자든 출력할 수 있다는 장점이 있지만 문자열을 수정할 때마다 많은 연산이 필요하다는 단점이 있다. 
 * 퍼포먼스를 고려하지 않는다면 가장 간편한 방식이다. 문자열을 수정하지 않는다면 SLabel처럼 사용할 수도 
 * 있다. 사용한 후 dispose()를 호출해야 Texture에서 점유한 공간을 다시 재활용할 수 있다.
 * 
 * @see Label
 * @author 김현우
 */
public class DLabel extends Label<DLabel> implements GLReloadable, Disposable {
	
	public static Paint sDefaultPaint = new Paint();
	
	private Texture mTexture;
	
	private Paint mPaint = new Paint();
	
	private int mRegionID;
	
	private float[] mLineWidths = new float[1];
	private float mLineHeight;
	
	private boolean mWrapEnabled;
	private float mWrapWidth;
	
	private boolean mDisableSizeChanged;
	
	private Drawable[] mDrawables = new Drawable[0];
	
	private boolean mDisposable = true;
	
	private boolean mCopied;
	
	public DLabel(String text, Texture texture) {
		this(text, texture, null);
	}
	
	public DLabel(int stringID, Texture texture) {
		this(Core.APP.getResources().getString(stringID), texture, null);
	}
	
	public DLabel(int stringID, Texture texture, Paint paint) {
		this(Core.APP.getResources().getString(stringID), texture, paint);
	}
	
	public DLabel(int stringArrayID, int index, Texture texture) {
		this(Core.APP.getResources().getStringArray(stringArrayID)[index], texture, null);
	}
	
	public DLabel(int stringArrayID, int index, Texture texture, Paint paint) {
		this(Core.APP.getResources().getStringArray(stringArrayID)[index], texture, paint);
	}
	
	public DLabel(String text, Texture texture, Paint paint) {
		mTexture = texture;
		mPaint.set((paint != null)? paint : sDefaultPaint);
		if(text != null) setText(text);
		Core.GRAPHICS.addGLReloadable(this);
	}
	
	/** 
	 * 복사본은 Texture 자체를 수정할 수 없다. 즉, {@link #setText(DLabel)} 등과 같은 메서드를 호출할 경우 
	 * 예외가 발생한다. Texture와 관계 없는 정렬 등의 수정은 허용된다. 이후에 원본이 수정된 경우 복사본은 
	 * 더 이상 유효하지 않을 수 있다. 
	 */
	public DLabel(DLabel label) {
		super(label);
		mTexture = label.mTexture;
		mPaint = label.mPaint;
		mRegionID = label.mRegionID;
		mLineWidths = label.mLineWidths;
		mLineHeight = label.mLineHeight;
		mWrapEnabled = label.mWrapEnabled;
		mWrapWidth = label.mWrapWidth;
		mDrawables = label.mDrawables;
		mDrawable = mDrawables[0];
		mDisposable = label.mDisposable;
		mCopied = true;
		pack();
	}

	public DLabel setText(String text) {
		if(mText != null && mText.equals(text)) return this;
		mText = text;
		updateText();
		return this;
	}
	
	private void updateText() {
		if(mCopied) throw new IllegalStateException("Copied DLabel can't be modified.");
			
		if(mText == null) {
			if(mRegionID == 0) return;
			FontTexture fontTexture = (FontTexture) mTexture;
			fontTexture.removeTextureRegion(mRegionID);
			mRegionID = 0;
			mNumLines = 0;
			return;
		}
		
		FontTexture fontTexture = (FontTexture) mTexture;
		int regionID = mRegionID;
		fontTexture.removeTextureRegion(regionID);
		if(mWrapEnabled)	regionID = fontTexture.addStringWrapRegions(mText, mWrapWidth, mPaint);
		else						regionID = fontTexture.addStringMultiLineRegions(mText, mPaint);
		mNumLines = fontTexture.getTextureRegionCount(regionID);
		int numLines = mNumLines;
		ensureDrawableSize(numLines);
		Drawable[] drawables = mDrawables;
		for(int i=0; i<numLines; i++) {
			TextureRegion region = mTexture.getTextureRegion(regionID, i);
			if(drawables[i] == null) {
				drawables[i] = new TextureRegionDrawable(region);
			} else {
				TextureRegionDrawable drawable = (TextureRegionDrawable) drawables[i];
				drawable.setRegion((ImmutableTextureRegion) region);
			}
		}
		if(numLines == 1) mDrawable = drawables[0];
		mRegionID = regionID;
		
		computeSize();
		pack();
		onTextChanged();
	}
	
	private void ensureDrawableSize(int length) {
		if(mDrawables.length >= length) return;
		mDrawables = new Drawable[length];
	}
	
	private void computeSize() {
		if(mNumLines > mLineWidths.length) mLineWidths = new float[mNumLines];
		
		float[] lineWidths = mLineWidths;
		Drawable[] drawables = mDrawables;
		float scale = FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		for(int i=0; i<mNumLines; i++)
			lineWidths[i] = drawables[i].getWidth() / scale;
		
		mLineHeight = drawables[0].getHeight() / scale;
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		float maxWidth = 0f;
		float[] lineWidths = mLineWidths;
		for(int i=0; i<mNumLines; i++)
			maxWidth = Math.max(maxWidth, lineWidths[i]);
		return maxWidth;
	}
	
	@Override
	protected float getDefaultPrefHeight() {
		return mNumLines * mLineHeight;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(mText == null) return;
		if(mNumLines == 1)	super.draw(batch, parentAlpha);
		else							drawMultiLine(batch, parentAlpha);
	}

	private void drawMultiLine(Batch batch, float parentAlpha) {
		validate();
		
		float labelX = mLabelX;
		float labelY = mLabelY;
		
		float width = getWidth();
		float height = getHeight();
		float prefWidth = getPrefWidth();
		float prefHeight = getPrefHeight();
		float widthScale = width / prefWidth;
		float heightScale = height / prefHeight; 
		
		float pivotX = getPivotX();
		float pivotY = getPivotY();
		float linePivotX = width * pivotX;
		float linePivotY = height * pivotY;
		
		HAlign lineAlign = mLineAlign;
		
		Drawable[] drawables = mDrawables;
		float[] lineWidths = mLineWidths;
		
		mDisableSizeChanged = true;
		
		float scaledLineHeight = mLineHeight * heightScale;
		setHeight(scaledLineHeight);
		
		for(int i=0; i<mNumLines; i++) {
			float scaledLineWidth =  lineWidths[i] * widthScale;
			
			float deltaX = 0f;
			switch(lineAlign) {
				case LEFT:
					break;
				case CENTER:	deltaX = (width - scaledLineWidth)/2;
					break;
				case RIGHT:		deltaX = width - scaledLineWidth;
					break;
			}
			mLabelX += deltaX;
			
			setPivotX((linePivotX-deltaX) / scaledLineWidth);
			setPivotY(linePivotY / scaledLineHeight);
			setWidth(scaledLineWidth);
			
			mDrawable = drawables[i];
			drawSelf(batch, parentAlpha);
			
			mLabelX = labelX;
			mLabelY += scaledLineHeight;
			linePivotY -= scaledLineHeight;
		}
		
		pivotTo(pivotX, pivotY);
		sizeTo(width, height);
		
		mDisableSizeChanged = false;
		
		mLabelY = labelY;
	}
	
	@Override
	protected void onSizeChanged() {
		if(mDisableSizeChanged) return;
		super.onSizeChanged();
	}

	public Paint getPaint() {
		return mPaint;
	}

	public DLabel setPaint(Paint paint) {
		mPaint.set(paint);
		updateText();
		return this;
	}

	public float getTextSize() {
		return mPaint.getTextSize();
	}

	public DLabel setTextSize(float size) {
		if(mPaint.getTextSize() == size) return this;
		mPaint.setTextSize(size);
		updateText();
		return this;
	}

	public boolean isUnderlineText() {
		return mPaint.isUnderlineText();
	}

	public DLabel setUnderlineText(boolean underlineText) {
		if(mPaint.isUnderlineText() == underlineText) return this;
		mPaint.setUnderlineText(underlineText);
		updateText();
		return this;
	}

	public boolean isStrikeThruText() {
		return mPaint.isStrikeThruText();
	}

	public DLabel setStrikeThruText(boolean strikeThruText) {
		if(mPaint.isStrikeThruText() == strikeThruText) return this;
		mPaint.setStrikeThruText(strikeThruText);
		updateText();
		return this;
	}

	public boolean isWrapEnabled() {
		return mWrapEnabled;
	}

	public float getWrapWidth() {
		return mWrapWidth;
	}

	public DLabel disableWrap() {
		if(mWrapEnabled) return this;
		mWrapEnabled = false;
		updateText();
		return this;
	}

	public DLabel enableWrap(float wrapWidth) {
		if(mWrapEnabled && mWrapWidth == wrapWidth) return this;
		mWrapEnabled = true;
		mWrapWidth = wrapWidth;
		updateText();
		return this;
	}

	@Override
	public void reload() {
		updateText();
	}

	@Override
	public void dispose() {
		if(!mDisposable || mCopied) return;
		FontTexture fontTexture = (FontTexture) mTexture;
		if(mText != null) fontTexture.removeTextureRegion(mRegionID);
		Core.GRAPHICS.removeGLReloadable(this);
	}

	public boolean isDisposable() {
		return mDisposable;
	}

	public DLabel setDisposable(boolean disposable) {
		mDisposable = disposable;
		return this;
	}

}
