package core.framework.graphics.batch;

import core.framework.graphics.Color4;
import core.framework.graphics.Form;
import core.framework.graphics.texture.ImageTexture;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureRegion;
import core.framework.graphics.utils.ShaderProgram;
import core.math.Matrix3;
import core.math.Matrix4;

/**
 * 이미지와 폰트를 위한 2개의 SpriteBatch를 병렬로 작업한다. 이미지와 폰트를 동시에 사용하면서 
 * Texture 전환에 따른 flush를 줄여 성능향상을 얻을 수 있는 반면에 적절하게 사용하지 않으면 
 * 의도하지 않는 출력 결과를 얻을 수도 있다는 것을 주의하라. 이같은 문제는 {@link #flush()}를 적절하게 
 * 사용하여 극복할 수 있다.</p>
 * 
 * get 류의 메서드는 기본적으로 이미지 SpriteBatch에 대해서만 적용된다. 따라서 get 류로 반환된 
 * 객체를 수정할 경우 의도하지 않는 결과를 얻을 수 있다.</p>
 * 
 * @author 김현우
 */
public class ParallelBatch implements Batch {
	
	private Batch mImageBatch;
	private Batch mFontBatch;

	public ParallelBatch() {
		mImageBatch = new SpriteBatch();
		mFontBatch = new SpriteBatch(400);
	}
	
	public ParallelBatch(int maxImageSprites, int maxFontSprites) {
		mImageBatch = new SpriteBatch(maxImageSprites);
		mFontBatch = new SpriteBatch(maxFontSprites);
	}
	
	@Override
	public void begin() {
		mImageBatch.begin();
		mFontBatch.begin();
	}

	@Override
	public void end() {
		mImageBatch.end();
		mFontBatch.end();
	}

	@Override
	public void flush() {
		mImageBatch.flush();
		mFontBatch.flush();
	}
	
	@Override
	public float getAlpha() {
		return mImageBatch.getAlpha();
	}

	@Override
	public void setAlpha(float alpha) {
		mImageBatch.setAlpha(alpha);
		mFontBatch.setAlpha(alpha);
	}

	@Override
	public Color4 getColor() {
		return mImageBatch.getColor();
	}

	@Override
	public void setColor(Color4 color) {
		mImageBatch.setColor(color);
		mFontBatch.setColor(color);
	}

	@Override
	public void setColor(float a, float r, float g, float b) {
		mImageBatch.setColor(a, r, g, b);
		mFontBatch.setColor(a, r, g, b);
	}

	@Override
	public void setColor(int a, int r, int g, int b) {
		mImageBatch.setColor(a, r, g, b);
		mFontBatch.setColor(a, r, g, b);
	}
	
	@Override
	public void setColor(int color) {
		mImageBatch.setColor(color);
		mFontBatch.setColor(color);
	}

	@Override
	public void draw(Texture texture, float srcX, float srcY, float srcWidth,float srcHeight, 
			float dstX, float dstY, float dstWidth, float dstHeight) {
		if(texture instanceof ImageTexture) {
			mImageBatch.draw(texture, srcX, srcY, srcWidth, srcHeight, 
					dstX, dstY, dstWidth, dstHeight);
		} else
			mFontBatch.draw(texture, srcX, srcY, srcWidth, srcHeight, 
					dstX, dstY, dstWidth, dstHeight);
	}
	
	@Override
	public void draw(Texture texture, float srcX, float srcY, float srcWidth,float srcHeight, 
			float dstX, float dstY, float dstWidth, float dstHeight, boolean flipX, boolean flipY) {
		if(texture instanceof ImageTexture) {
			mImageBatch.draw(texture, srcX, srcY, srcWidth, srcHeight, 
					dstX, dstY, dstWidth, dstHeight, flipX, flipY);
		} else
			mFontBatch.draw(texture, srcX, srcY, srcWidth, srcHeight, 
					dstX, dstY, dstWidth, dstHeight, flipX, flipY);
	}

	@Override
	public void draw(TextureRegion textureRegion, float dstX, float dstY) {
		Texture texture = textureRegion.getTexture();
		if(texture instanceof ImageTexture) {
			mImageBatch.draw(textureRegion, dstX, dstY);
		} else
			mFontBatch.draw(textureRegion, dstX, dstY);
	}
	
	@Override
	public void draw(TextureRegion textureRegion, float dstX, float dstY, 
			float dstWidth, float dstHeight) {
		Texture texture = textureRegion.getTexture();
		if(texture instanceof ImageTexture) {
			mImageBatch.draw(textureRegion, dstX, dstY, dstWidth, dstHeight);
		} else
			mFontBatch.draw(textureRegion, dstX, dstY, dstWidth, dstHeight);
	}
	
	@Override
	public void draw(TextureRegion textureRegion, float dstX, float dstY, 
			float dstWidth, float dstHeight, boolean flipX, boolean flipY) {
		Texture texture = textureRegion.getTexture();
		if(texture instanceof ImageTexture) {
			mImageBatch.draw(textureRegion, dstX, dstY, dstWidth, dstHeight, flipX, flipY);
		} else
			mFontBatch.draw(textureRegion, dstX, dstY, dstWidth, dstHeight, flipX, flipY);
	}
	
	@Override
	public void draw(TextureRegion textureRegion, float dstX, float dstY,
			float dstWidth, float dstHeight, boolean flipX, boolean flipY, boolean clockwise) {
		Texture texture = textureRegion.getTexture();
		if(texture instanceof ImageTexture) {
			mImageBatch.draw(textureRegion, dstX, dstY, dstWidth, dstHeight, flipX, flipY, clockwise);
		} else
			mFontBatch.draw(textureRegion, dstX, dstY, dstWidth, dstHeight, flipX, flipY, clockwise);
	}
	
	@Override
	public void draw(TextureRegion textureRegion, Form dstForm) {
		Texture texture = textureRegion.getTexture();
		if(texture instanceof ImageTexture) {
			mImageBatch.draw(textureRegion, dstForm);
		} else
			mFontBatch.draw(textureRegion, dstForm);
	}

	@Override
	public void pushTransformMatrix(Matrix3 matrix) {
		mImageBatch.pushTransformMatrix(matrix);
		mFontBatch.pushTransformMatrix(matrix);
	}
	
	@Override
	public Matrix3 peekTransformMatrix() {
		return mImageBatch.peekTransformMatrix();
	}

	@Override
	public Matrix3 popTransformMatrix() {
		mImageBatch.popTransformMatrix();
		return mFontBatch.popTransformMatrix();
	}
	
	@Override
	public void pushTransformColor(Color4 color) {
		mImageBatch.pushTransformColor(color);
		mFontBatch.pushTransformColor(color);
	}
	
	@Override
	public Color4 peekTransformColor() {
		return mImageBatch.peekTransformColor();
	}
	
	@Override
	public Color4 popTransformColor() {
		mImageBatch.popTransformColor();
		return mFontBatch.popTransformColor();
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		return mImageBatch.getProjectionMatrix();
	}

	@Override
	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		mImageBatch.setProjectionMatrix(projectionMatrix);
		mFontBatch.setProjectionMatrix(projectionMatrix);
	}
	
	@Override
	public boolean isBlendEnabled() {
		return mImageBatch.isBlendEnabled();
	}

	@Override
	public void setBlendEnabled(boolean blendEnabled) {
		mImageBatch.setBlendEnabled(blendEnabled);
		mFontBatch.setBlendEnabled(blendEnabled);
	}
	
	@Override
	public int getBlendSrcFactor() {
		return mImageBatch.getBlendSrcFactor();
	}

	@Override
	public int getBlendDstFactor() {
		return mImageBatch.getBlendDstFactor();
	}

	@Override
	public void setBlendFunc(int srcFactor, int dstFactor) {
		mImageBatch.setBlendFunc(srcFactor, dstFactor);
		mFontBatch.setBlendFunc(srcFactor, dstFactor);
	}
	
	@Override
	public ShaderProgram getShaderProgram() {
		return mImageBatch.getShaderProgram();
	}

	@Override
	public void setShaderProgram(ShaderProgram program) {
		mImageBatch.setShaderProgram(program);
		mFontBatch.setShaderProgram(program);
	}

	@Override
	public int getMaxSprites() {
		return Math.max(mImageBatch.getMaxSprites(), mFontBatch.getMaxSprites());
	}

	@Override
	public int getSpriteCount() {
		return Math.max(mImageBatch.getSpriteCount(), mFontBatch.getSpriteCount());
	}

	@Override
	public void dispose() {
		mImageBatch.dispose();
		mFontBatch.dispose();
	}

}
