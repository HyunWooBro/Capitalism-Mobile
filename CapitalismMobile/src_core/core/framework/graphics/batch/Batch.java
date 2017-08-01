package core.framework.graphics.batch;

import core.framework.graphics.Color4;
import core.framework.graphics.Form;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureRegion;
import core.framework.graphics.utils.ShaderProgram;
import core.math.Matrix3;
import core.math.Matrix4;
import core.utils.Disposable;

/** 
 * 렌더링의 과정에서 성능의 이점을 얻기 위해 한번에 batch 처리를 하는 것이 기본적이다.</p>
 * 
 * 이 인터페이스는 텍스쳐의 영역을 batch 렌더링하기 위한 기본 메서드의 원형을 정의한다.</p>
 * 
 * @author 김현우
 */
public interface Batch extends Disposable {
	
	/**
	 * 배치 렌더링을 시작한다. 배치 렌더링을 이미 시작한 경우 {@link #end()}를 호출하기 
	 * 전까지 다시 호출할 수 없다.
	 */
	public void begin();
	
	/**
	 * 배치 렌더링을 종료한다. {@link #begin()}을 호출하기 전에 이 메서드를 호출할 수 없다.
	 */
	public void end();
	
	/**
	 * 축적했던 버텍스관련 데이터를 바탕으로 실제로 렌더링을 한다.
	 */
	public void flush();
	
	public float getAlpha();
	
	public void setAlpha(float alpha);
	
	public Color4 getColor();

	public void setColor(Color4 color);
	
	public void setColor(float a, float r, float g, float b);
	
	public void setColor(int a, int r, int g, int b);
	
	public void setColor(int color);
	
	public void draw(Texture texture, float srcX, float srcY, float srcWidth, float srcHeight, 
			float dstX, float dstY, float dstWidth, float dstHeight);
	
	public void draw(Texture texture, float srcX, float srcY, float srcWidth, float srcHeight, 
			float dstX, float dstY, float dstWidth, float dstHeight, boolean flipX, boolean flipY);

	public void draw(TextureRegion textureRegion, float dstX, float dstY);
	
	public void draw(TextureRegion textureRegion, float dstX, float dstY, 
			float dstWidth, float dstHeight);
	
	public void draw(TextureRegion textureRegion, float dstX,
			float dstY, float dstWidth, float dstHeight, boolean flipX, boolean flipY);
	
	public void draw(TextureRegion textureRegion, float dstX,
			float dstY, float dstWidth, float dstHeight, boolean flipX, boolean flipY, boolean clockwise);
	
	public void draw(TextureRegion textureRegion, Form dstForm);
	
	/** 
	 * 변환 매트릭스를 스택에 넣는다. 레퍼런스를 스택에 넣기 때문에 외부에서 수정하면 
	 * 영향을 받는다. 
	 */
	public void pushTransformMatrix(Matrix3 matrix);
	
	public Matrix3 peekTransformMatrix();
	
	public Matrix3 popTransformMatrix();
	
	public void pushTransformColor(Color4 color);
	
	public Color4 peekTransformColor();
	
	public Color4 popTransformColor();
	
	public Matrix4 getProjectionMatrix();

	public void setProjectionMatrix(Matrix4 projectionMatrix);
	
	public boolean isBlendEnabled();
	
	public void setBlendEnabled(boolean blendEnabled);
	
	public int getBlendSrcFactor();
	
	public int getBlendDstFactor();
	
	public void setBlendFunc(int blendSrcFactor, int blendDstFactor);
	
	public ShaderProgram getShaderProgram();
	
	public void setShaderProgram(ShaderProgram program);
	
	public int getMaxSprites();

	public int getSpriteCount();

}