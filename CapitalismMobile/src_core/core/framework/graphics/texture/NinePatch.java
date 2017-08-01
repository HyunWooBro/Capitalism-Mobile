package core.framework.graphics.texture;

import core.framework.graphics.Form;
import core.framework.graphics.batch.Batch;

/**
 * NinePatch는 9개의 TextureRegion으로 이루어진다. 대각선 외각 영역을 제외하고 십자가
 * 모양으로 내부의 5개의 TextureRegion은 정점의 위치를 변환하여 임의의 크기로 원본
 * 영역을 표현할 수 있다. 원본을 스케일한 것과는 달리 원본의 형태를 유지하면서 크기를 
 * 조정할 수 있어 다양한 상황에 사용할 수 있다.
 * 
 * @author 김현우
 */
public class NinePatch {
	
	private static final TextureRegion[] TMP_PATCHES = new TextureRegion[9];
	
	private TextureRegion[] mPatches = new TextureRegion[9];
	
	private float mDefaultWidth;
	private float mDefaultHeight;
	
	private float mLeftWidth;
	private float mRightWidth;
	private float mTopHeight;
	private float mBottomHeight;

	public NinePatch(TextureRegion textureRegion, int leftWidth, int topHeight, int rightWdith, int bottomHeight) {
		int width = textureRegion.getRegionWidth();
		int height = textureRegion.getRegionHeight();
		int centerWidth = width - leftWidth - rightWdith;
		int centerHeight = height - topHeight - bottomHeight;
		
		int leftX = textureRegion.getRegionX1();
		int topY = textureRegion.getRegionY1();
		int centerX = leftX + leftWidth;
		int centerY = topY + topHeight;
		int rightX = centerX + centerWidth;
		int bottomY = centerY + centerHeight;
		
		Texture texture = textureRegion.getTexture();
		
		TextureRegion[] patches = mPatches;
		patches[0] = new TextureRegion(texture, leftX, topY, leftWidth, topHeight);
		patches[1] = new TextureRegion(texture, centerX, topY, centerWidth, topHeight);
		patches[2] = new TextureRegion(texture, rightX, topY, rightWdith, topHeight);
		
		patches[3] = new TextureRegion(texture, leftX, centerY, leftWidth, centerHeight);
		patches[4] = new TextureRegion(texture, centerX, centerY, centerWidth, centerHeight);
		patches[5] = new TextureRegion(texture, rightX, centerY, rightWdith, centerHeight);
		
		patches[6] = new TextureRegion(texture, leftX, bottomY, leftWidth, bottomHeight);
		patches[7] = new TextureRegion(texture, centerX, bottomY, centerWidth, bottomHeight);
		patches[8] = new TextureRegion(texture, rightX, bottomY, rightWdith, bottomHeight);
		
		mDefaultWidth = width;
		mDefaultHeight = height;
		
		mLeftWidth = leftWidth;
		mRightWidth = rightWdith;
		mTopHeight = topHeight;
		mBottomHeight = bottomHeight;
	}
	
	public NinePatch(NinePatch ninePatch) {
		for(int i=0; i<9; i++)
			mPatches[i] = new TextureRegion(ninePatch.mPatches[i]);
		
		mDefaultWidth = ninePatch.mDefaultWidth;
		mDefaultHeight = ninePatch.mDefaultHeight;
		
		mLeftWidth = ninePatch.mLeftWidth;
		mRightWidth = ninePatch.mRightWidth;
		mTopHeight = ninePatch.mTopHeight;
		mBottomHeight = ninePatch.mBottomHeight;
	}
	
	public void draw(Batch batch, float dstX, float dstY, float dstWidth, float dstHeight) {
		draw(batch, dstX, dstY, dstWidth, dstHeight, false, false);
	}
	
	public void draw(Batch batch, float dstX, float dstY, float dstWidth, float dstHeight, 
			boolean flipX, boolean flipY) {
		float leftWidth = mLeftWidth;
		float topHeight = mTopHeight;
		float rightWidth = mRightWidth;
		float bottomHeight = mBottomHeight;
		float centerWidth = dstWidth - leftWidth - rightWidth;
		float centerHeight = dstHeight - topHeight - bottomHeight;
		
		float leftX = dstX;
		float topY = dstY;
		float centerX = leftX + leftWidth;
		float centerY = topY + topHeight;
		float rightX = centerX + centerWidth;
		float bottomY = centerY + centerHeight;
		
		TextureRegion[] patches = TMP_PATCHES;
		
		if(!flipX && !flipY) {
			patches = mPatches;
		} else {
			int left;
			int top;
			int right;
			int bottom;
			
			if(flipX) {
				left = 2;
				right = -2;
			} else {
				left = 0;
				right = 0;
			}
			
			if(flipY) {
				top = 6;
				bottom = -6;
			} else {
				top = 0;
				bottom = 0;
			}
			
			patches[0] = mPatches[0 + top + left];
			patches[1] = mPatches[1 + top];
			patches[2] = mPatches[2 + top + right];
			
			patches[3] = mPatches[3 + left];
			patches[4] = mPatches[4];
			patches[5] = mPatches[5 + right];
			
			patches[6] = mPatches[6 + bottom + left];
			patches[7] = mPatches[7 + bottom];
			patches[8] = mPatches[8 + bottom + right];
		}

		batch.draw(patches[0], leftX, topY, leftWidth, topHeight, flipX, flipY);
		batch.draw(patches[1], centerX, topY, centerWidth, topHeight, flipX, flipY);
		batch.draw(patches[2], rightX, topY, rightWidth, topHeight, flipX, flipY);
		
		batch.draw(patches[3], leftX, centerY, leftWidth, centerHeight, flipX, flipY);
		batch.draw(patches[4], centerX, centerY, centerWidth, centerHeight, flipX, flipY);
		batch.draw(patches[5], rightX, centerY, rightWidth, centerHeight, flipX, flipY);
		
		batch.draw(patches[6], leftX, bottomY, leftWidth, bottomHeight, flipX, flipY);
		batch.draw(patches[7], centerX, bottomY, centerWidth, bottomHeight, flipX, flipY);
		batch.draw(patches[8], rightX, bottomY, rightWidth, bottomHeight, flipX, flipY);
	}
	
	public void draw(Batch batch, Form dstForm) {
		float width = dstForm.getWidth();
		float height = dstForm.getHeight();
		float leftWidth = mLeftWidth;
		float topHeight = mTopHeight;
		float rightWidth = mRightWidth;
		float bottomHeight = mBottomHeight;
		float centerWidth = width - leftWidth - rightWidth;
		float centerHeight = height - topHeight - bottomHeight;
		
		float leftX = dstForm.getX();
		float topY = dstForm.getY();
		float centerX = leftX + leftWidth;
		float centerY = topY + topHeight;
		float rightX = centerX + centerWidth;
		float bottomY = centerY + centerHeight;
		
		boolean flipX = dstForm.isFlipX();
		boolean flipY = dstForm.isFlipY();

		float pivotX = dstForm.getPivotX();
		float pivotY = dstForm.getPivotY();
		float patchPivotX = width * pivotX;
		float patchPivotY = height * pivotY;
		float leftPivotX = patchPivotX / leftWidth;
		float topPivotY = patchPivotY / topHeight;
		float centerPivotX = (patchPivotX-leftWidth)/centerWidth;
		float centerPivotY = (patchPivotY-topHeight)/centerHeight;
		float rightPivotX = (patchPivotX-(leftWidth+centerWidth))/rightWidth;
		float bottomPivotY = (patchPivotY-(topHeight+centerHeight))/bottomHeight;
		
		TextureRegion[] patches = TMP_PATCHES;
		
		if(!flipX && !flipY) {
			patches = mPatches;
		} else {
			int left;
			int top;
			int right;
			int bottom;
			
			if(flipX) {
				left = 2;
				right = -2;
			} else {
				left = 0;
				right = 0;
			}
			
			if(flipY) {
				top = 6;
				bottom = -6;
			} else {
				top = 0;
				bottom = 0;
			}
			
			patches[0] = mPatches[0 + top + left];
			patches[1] = mPatches[1 + top];
			patches[2] = mPatches[2 + top + right];
			
			patches[3] = mPatches[3 + left];
			patches[4] = mPatches[4];
			patches[5] = mPatches[5 + right];
			
			patches[6] = mPatches[6 + bottom + left];
			patches[7] = mPatches[7 + bottom];
			patches[8] = mPatches[8 + bottom + right];
		}
		
		dstForm.setBounds(leftX, topY, leftWidth, topHeight).pivotTo(leftPivotX, topPivotY);
		batch.draw(patches[0], dstForm);
		dstForm.setBounds(centerX, topY, centerWidth, topHeight).pivotTo(centerPivotX, topPivotY);
		batch.draw(patches[1], dstForm);
		dstForm.setBounds(rightX, topY, rightWidth, topHeight).pivotTo(rightPivotX, topPivotY);
		batch.draw(patches[2], dstForm);
		
		dstForm.setBounds(leftX, centerY, leftWidth, centerHeight).pivotTo(leftPivotX, centerPivotY);
		batch.draw(patches[3], dstForm);
		dstForm.setBounds(centerX, centerY, centerWidth, centerHeight).pivotTo(centerPivotX, centerPivotY);
		batch.draw(patches[4], dstForm);
		dstForm.setBounds(rightX, centerY, rightWidth, centerHeight).pivotTo(rightPivotX, centerPivotY);
		batch.draw(patches[5], dstForm);
		
		dstForm.setBounds(leftX, bottomY, leftWidth, bottomHeight).pivotTo(leftPivotX, bottomPivotY);
		batch.draw(patches[6], dstForm);
		dstForm.setBounds(centerX, bottomY, centerWidth, bottomHeight).pivotTo(centerPivotX, bottomPivotY);
		batch.draw(patches[7], dstForm);
		dstForm.setBounds(rightX, bottomY, rightWidth, bottomHeight).pivotTo(rightPivotX, bottomPivotY);
		batch.draw(patches[8], dstForm);
		
		dstForm.setBounds(leftX, topY, width, height).pivotTo(pivotX, pivotY);
	}

	public TextureRegion[] getPatches() {
		return mPatches;
	}

	public float getDefaultWidth() {
		return mDefaultWidth;
	}

	public float getDefaultHeight() {
		return mDefaultHeight;
	}

	public float getLeftWidth() {
		return mLeftWidth;
	}

	public float getRightWidth() {
		return mRightWidth;
	}

	public float getTopHeight() {
		return mTopHeight;
	}

	public float getBottomHeight() {
		return mBottomHeight;
	}

}
