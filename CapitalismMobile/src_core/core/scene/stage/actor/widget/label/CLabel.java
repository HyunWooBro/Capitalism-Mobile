package core.scene.stage.actor.widget.label;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.FontTexture;
import core.framework.graphics.texture.Texture;
import core.scene.stage.actor.drawable.TextureRegionDrawable;
import core.scene.stage.actor.widget.utils.Align.HAlign;

/**
 * CLabel은 CombinedLabel의 약자이다. StringArray에 정의된 범위 안에서 자유롭게 문자를 조합할 수 있다. 
 * 정의되지 않은 문자를 사용할 시에는 에러가 발생한다. DLabel처럼 자유롭지는 않지만 SLabel처럼 미리 
 * 정의된 문자를 조합하여 출력하므로 많은 텍스쳐의 용량이 필요하지 않는다. 즉, SLabel보다는 동적이고 
 * DLabel보다는 정적이다. 지속적으로 갱신이 필요한 문자 세트를 출력할 때 적합하다.</p>
 * 
 * @see Label
 * @author 김현우
 */
public class CLabel extends Label<CLabel> {
	
	public static final int MAX_CLABEL_LENGTH = 10000;
	
	private static final int[] TEMP_INT_ARRAY = new int[MAX_CLABEL_LENGTH];
	
	private static final int NEW_LINE = -1;
	
	private static final int SPACE_NOT_FOUND = -1;
	
	private static final SparseArray<TextureRegionDrawable[]> STRING_ARRAY_MAP = new SparseArray<TextureRegionDrawable[]>();
	
	private int mStringArrayID;
	
	private String[] mSupportedStrings;
	
	private int mLength;
	private int[] mIndices = new int[0];
	private int mSpaceIndex = SPACE_NOT_FOUND;
	
	private float[] mCharWidths = new float[1];
	private float[] mLineWidths = new float[1];
	private float mLineHeight;
	
	private Texture mTexture;
	
	private boolean mWrapEnabled;
	private float mWrapWidth;
	
	private boolean mDisableSizeChanged;

	public CLabel(String text, int stringArrayID, Texture texture) {
		mStringArrayID = stringArrayID;
		mTexture = texture;
		mSupportedStrings = Core.APP.getResources().getStringArray(stringArrayID);
		findSpaceIndex();
		addStringArrayMap(stringArrayID);
		setText(text);
	}
	
	public CLabel(CLabel label) {
		this(label, label.getText());
	}
	
	public CLabel(CLabel label, String text) {
		super(label);
		mStringArrayID = label.mStringArrayID;
		mTexture = label.mTexture;
		mSupportedStrings = label.mSupportedStrings;
		mSpaceIndex = label.mSpaceIndex;
		mWrapEnabled = label.mWrapEnabled;
		mWrapWidth = label.mWrapWidth;
		// setText에서 mText와 text가 같으면 updateText()를 하지 않으므로 
		// 일단 null로 설정한다.
		mText = null;
		setText(text);
	}
	
	private void findSpaceIndex() {
		String[] supportedStrings = mSupportedStrings;
		int n = supportedStrings.length;
		for(int i=0; i<n; i++) {
			if(supportedStrings[i].isEmpty()) mSpaceIndex = i;
		}
	}

	private void addStringArrayMap(int stringArrayID) {
		if(STRING_ARRAY_MAP.get(stringArrayID) != null) return;
		
		String[] strings = Core.APP.getResources().getStringArray(stringArrayID);
		TextureRegionDrawable[] drawables = new TextureRegionDrawable[strings.length];
		for(int i=0; i<strings.length; i++) {
			drawables[i] = new TextureRegionDrawable(mTexture.getTextureRegion(stringArrayID, i));
		}
		
		STRING_ARRAY_MAP.put(stringArrayID, drawables);
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		float maxWidth = 0f;
		float[] lineWidths = mLineWidths;
		for(int i=0; i<mNumLines; i++) {
			maxWidth = Math.max(maxWidth, lineWidths[i]);
		}
		return maxWidth;
	}
	
	@Override
	protected float getDefaultPrefHeight() {
		return mNumLines * mLineHeight;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(mText == null) return;
		
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
		float charPivotX = width * pivotX;
		float charPivotY = height * pivotY;
		
		HAlign lineAlign = mLineAlign;
		
		float padding = FontTexture.sFontPadding * widthScale;
		
		TextureRegionDrawable[] drawables = STRING_ARRAY_MAP.get(mStringArrayID);
		
		float[] charWidths = mCharWidths;
		float[] lineWidths = mLineWidths;
		int[] indices = mIndices;
		int lineCount = 0;
		boolean startNewLine = false;
		
		mDisableSizeChanged = true;
		
		float scaledLineHeight = mLineHeight * heightScale;
		setHeight(scaledLineHeight);

		for(int i=0; i<mLength; i++) {
			if(indices[i] == NEW_LINE) {
				mLabelY += scaledLineHeight;
				mLabelX = labelX;
				lineCount++;
				startNewLine = true;
				charPivotX = width * pivotX;
				continue;
			}
			
			float deltaX = 0;
			if(startNewLine) {
				startNewLine = false;
				switch(lineAlign) {
					case LEFT:
						break;
					case CENTER:
						deltaX = (width - lineWidths[lineCount]*widthScale)/2;
						break;
					case RIGHT:
						deltaX = width - lineWidths[lineCount]*widthScale;
						break;
				}
				mLabelX += deltaX;
				charPivotX -= deltaX;
				setPivotY(charPivotY / scaledLineHeight);
				charPivotY -= scaledLineHeight;
			}

			float scaledCharWidth = charWidths[i] * widthScale;
			
			setPivotX(charPivotX / scaledCharWidth);
			setWidth(scaledCharWidth);
			
			mDrawable = drawables[indices[i]];
			drawSelf(batch, parentAlpha);
			
			mLabelX += scaledCharWidth - padding;
			charPivotX -= scaledCharWidth - padding;
		}
		
		pivotTo(pivotX, pivotY);
		sizeTo(width, height);
		
		mDisableSizeChanged = false;
		
		mLabelX = labelX;
		mLabelY = labelY;
	}
	
	@Override
	protected void onSizeChanged() {
		if(mDisableSizeChanged) return;
		super.onSizeChanged();
	}
	
	public CLabel setText(String text) {
		if(mText != null && mText.equals(text)) return this;
		mText = text;
		updateText();
		return this;
	}
	
	private void updateText() {
		if(mText == null) {
			mNumLines = 0;
			return;
		}
		
		boolean wrapEnabled = mWrapEnabled;
		float wrapWidth = mWrapWidth;
		
		int stringArrayID = mStringArrayID;
		int length = mText.length();
		String[] supportedStrings = mSupportedStrings;
		if(!wrapEnabled) ensureIndexSize(length);
		int[] indices = (wrapEnabled)? TEMP_INT_ARRAY : mIndices;
		
		float padding = FontTexture.sFontPadding;
		float scale = FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		if(length > 0) mNumLines = 1;
		
		int totalCharCount = 0;
		int lineCharCount = 0;
		
		float lineWidth = 0f;
		boolean nextLine = false;
out:
		for(int i=0; i<length; i++) {
			if(nextLine) {
				nextLine = false;
				mNumLines++;
				if(wrapEnabled) {
					lineWidth = 0f;
					lineCharCount = 0;
				}
			}
			
			char c = mText.charAt(i);
			switch(c) {
				case ' ':
					if(mSpaceIndex != SPACE_NOT_FOUND) {
						if(wrapEnabled) {
							lineCharCount++;
							float width = mTexture.getTextureRegion(stringArrayID, mSpaceIndex).getRegionWidth();
							if((lineWidth + width)/scale - (lineCharCount-1)*padding <= wrapWidth) {
								lineWidth += width;
							} else {
								indices[totalCharCount++] = NEW_LINE;
								i--;
								nextLine = true;
								continue;
							}
						}
						indices[totalCharCount++] = mSpaceIndex;
						continue;
					}
					break;
				case '\n': // \n은 한 글자이다.
					indices[totalCharCount++] = NEW_LINE;
					nextLine = true;
					continue;
			}
			
			int n = supportedStrings.length;
			for(int j=0; j<n; j++) {
				if(supportedStrings[j].isEmpty()) continue;
				if(supportedStrings[j].charAt(0) == c) {
					if(wrapEnabled) {
						lineCharCount++;
						float width = mTexture.getTextureRegion(stringArrayID, j).getRegionWidth();
						if((lineWidth + width)/scale - (lineCharCount-1)*padding <= wrapWidth) {
							lineWidth += width;
						} else {
							indices[totalCharCount++] = NEW_LINE;
							i--;
							nextLine = true;
							continue out;
						}
					}
					indices[totalCharCount++] = j;
					continue out;
				}
			}
			
			throw new IllegalArgumentException("Some of mText are not defined");
		}
		
		if(wrapEnabled) {
			ensureIndexSize(totalCharCount);
			System.arraycopy(indices, 0, mIndices, 0, totalCharCount);
			mLength = totalCharCount;
		} else
			mLength = length;
		
		computeSize(padding, scale);
		pack();
		onTextChanged();
	}

	private void ensureIndexSize(int length) {
		if(mIndices.length >= length) return;
		mIndices = new int[length];
	}

	private void computeSize(float padding, float scale) {
		int length = mLength;
		if(length < 1) return;
		
		int stringArrayID = mStringArrayID;
		float lineWidth = 0f;
		int lineCount = 0;
		int count = 0;
		
		if(mNumLines > mLineWidths.length) mLineWidths = new float[mNumLines];
		if(length > mCharWidths.length) mCharWidths = new float[length];
		
		float[] charWidths = mCharWidths;
		float[] lineWidths = mLineWidths;
		int[] indices = mIndices;
		
		for(int i=0; i<length; i++) {
			if(indices[i] == NEW_LINE) {
				lineWidth -= (count-1) * padding;
				lineWidths[lineCount++] = lineWidth;
				lineWidth = 0f;
				count = 0;
				continue;
			}
			
			float width = mTexture.getTextureRegion(stringArrayID, indices[i]).getRegionWidth();
			charWidths[i] = width / scale;
			lineWidth += charWidths[i];
			count++;
		}
		
		if(indices[length-1] != NEW_LINE) {
			lineWidth -= (count-1) * padding;
			lineWidths[lineCount] = lineWidth;
			lineWidth = 0f;
			count = 0;
		}
		
		// 모든 문자의 높이가 같으므로 첫번째 문자의 높이를 사용한다.
		mLineHeight = mTexture.getTextureRegion(stringArrayID, indices[0]).getRegionHeight() / scale;
	}

	public boolean isWrapEnabled() {
		return mWrapEnabled;
	}

	public float getWrapWidth() {
		return mWrapWidth;
	}

	public CLabel disableWrap() {
		if(!mWrapEnabled) return this;
		mWrapEnabled = false;
		updateText();
		return this;
	}

	public CLabel enableWrap(float wrapWidth) {
		if(mWrapEnabled && mWrapWidth == wrapWidth) return this;
		mWrapEnabled = true;
		mWrapWidth = wrapWidth;
		updateText();
		return this;
	}

}
