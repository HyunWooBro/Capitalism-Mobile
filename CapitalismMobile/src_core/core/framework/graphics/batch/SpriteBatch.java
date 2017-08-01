package core.framework.graphics.batch;

import java.util.Stack;

import android.opengl.GLES20;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.Form;
import core.framework.graphics.Sprite;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureRegion;
import core.framework.graphics.utils.ShaderProgram;
import core.framework.graphics.utils.VertexAttribute;
import core.framework.graphics.utils.mesh.Mesh;
import core.math.Matrix3;
import core.math.Matrix4;

/**
 * Texture 또는 TextureRegion을 batch 렌더링 한다.</p>
 * 
 * 안드로이드는 Bitmap을 로드할 때 RGB_565이 아니면서 알파값을 갖고 있다면 premultiplied alpha가 
 * 자동으로 적용하기 때문에 GLES20.GL_ONE을 기본 blend source로 사용한다.</p>
 * 
 * @author 김현우
 */
public class SpriteBatch implements Batch {
	
	public static final int DEFAULT_MAX_SPRITES = 800;
	
	private static final String TAG = SpriteBatch.class.getSimpleName();
	
	private static final float[] POINTS = new float[8];
	
	private static final String VERTEX_SHADER_CODE =    
			"uniform mat4 " + ShaderProgram.UNIFORM_PROJECTION_MATRIX + ";" +
			"attribute vec4 " + ShaderProgram.ATTRIBUTE_POSITION +";" +
			"attribute vec2 " + ShaderProgram.ATTRIBUTE_TEXCOORD + ";" +
			"attribute vec4 " + ShaderProgram.ATTRIBUTE_COLOR + ";" +
			"varying vec2 v_texCoord;" +
			"varying vec4 v_color;" + 
			"void main() {" +
			"  gl_Position = " + ShaderProgram.UNIFORM_PROJECTION_MATRIX + " * " + ShaderProgram.ATTRIBUTE_POSITION + ";" +
		    "  v_texCoord = " + ShaderProgram.ATTRIBUTE_TEXCOORD + ";" +
		    "  v_color = " + ShaderProgram.ATTRIBUTE_COLOR + ";" +
		    "}";
	
	private static final String FRAGMENT_SHADER_CODE =    
			"precision mediump float;" +
			"uniform sampler2D " + ShaderProgram.UNIFORM_TEXTURE + ";" +
			"varying vec4 v_color;" + 
		    "varying vec2 v_texCoord;" +
		    "void main() {" +
		    "  gl_FragColor = v_color * texture2D(" + ShaderProgram.UNIFORM_TEXTURE + ", v_texCoord);" +
		    "}";

	/** {@link #begin()}과 {@link #end()}의 쌍을 맞추기 위해 사용 */
	private boolean mDrawing;
	
	/** 배치 렌더링할 정점의 배열 */
	private float[] mVertices;
	/** 배치 렌더링할 정점의 최대 인덱스 */
	int mVertexIndex;
	
	private Mesh mMesh;
	
	/** SpriteBatch가 한번에 그릴 수 있는 최대 Sprite개수 */ 
	private int mMaxSprites;
	/** 한 프레임에서 렌더링할 sprite 개수 */
	private int mSpriteCount;
	
	/** 현재 SpriteBatch에 지정된 텍스쳐 */
	private Texture mCurrentTexture;
	private float mInverseTextureWidth;
	private float mInverseTextureHeight;
	
	private boolean mBlendEnabled = true;
	private int mBlendSrcFactor = GLES20.GL_ONE;
	private int mBlendDstFactor = GLES20.GL_ONE_MINUS_SRC_ALPHA;
	
	private Color4 mColor = new Color4();
	
	private Stack<Matrix3> mTransformMatrixStack = new Stack<Matrix3>();
	private Stack<Color4> mTransformColorStack = new Stack<Color4>();
	private Matrix4 mProjectionMatrix = new Matrix4();
	
	private ShaderProgram mProgram;

	public SpriteBatch() {
		this(DEFAULT_MAX_SPRITES, new ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE));
	}
	
	public SpriteBatch(int maxSprites) {
		this(maxSprites, new ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE));
	}
	
	public SpriteBatch(int maxSprites, ShaderProgram program) {
		mMaxSprites = maxSprites;
		
		mMesh = new Mesh(
				true, 
				false, 
				maxSprites * Sprite.NUM_VERTICES, 
				maxSprites * Sprite.NUM_INDICES, 
				new VertexAttribute(ShaderProgram.ATTRIBUTE_POSITION, 2),
				new VertexAttribute(ShaderProgram.ATTRIBUTE_TEXCOORD, 2), 
				new VertexAttribute(ShaderProgram.ATTRIBUTE_COLOR, 4));
		
		short[] indices = new short[maxSprites* Sprite.NUM_INDICES];
		for(int i=0, j=0, k=0; i<maxSprites; i++, k++) {
			indices[j++] = (short) (0+4*k);
			indices[j++] = (short) (1+4*k);
			indices[j++] = (short) (2+4*k);
			indices[j++] = (short) (0+4*k);
			indices[j++] = (short) (2+4*k);
			indices[j++] = (short) (3+4*k);
		}
		mMesh.setIndices(indices);
		
		mVertices = new float[mMesh.getAttributes().getNumSingleVertexComponents() * Sprite.NUM_VERTICES * maxSprites];
		
		setShaderProgram(program);
	}
	
	@Override
	public void begin() {
		if(mDrawing)
			throw new IllegalStateException("begin() must be ended with end() before another begin()");
		
		mDrawing = true;
		mTransformMatrixStack.clear();
	}

	@Override
	public void end() {
		if(!mDrawing)
			throw new IllegalStateException("begin() must be called before end()");
		
		flush();
		mCurrentTexture = null;
		mDrawing = false;
	}

	@Override
	public void flush() {
		if(mSpriteCount == 0) return;
		
		beginBlend();
		mProgram.begin();
		mCurrentTexture.bind();
		
		setUniform();
		mMesh.begin(mProgram);
		mMesh.setVertices(mVertices, 0, mVertexIndex);
		mMesh.render(GLES20.GL_TRIANGLES, mSpriteCount*Sprite.NUM_INDICES);
		mMesh.end(mProgram);
		
		mCurrentTexture.unbind();
		mProgram.end();
		endBlend();
		
		Core.GRAPHICS.incrementDrawCount();
		
		reset();
	}
	
	private void setUniform() {
		int location;
		
		location = mProgram.getLocationByName(ShaderProgram.UNIFORM_PROJECTION_MATRIX);
		if(location > -1) {
			GLES20.glUniformMatrix4fv(
					location, 
	        		1, 
	        		false, 
	        		mProjectionMatrix.value, 
	        		0);
		}

		location = mProgram.getLocationByName(ShaderProgram.UNIFORM_TEXTURE);
		if(location > -1) GLES20.glUniform1i(location, 0);
	}
	
	private void beginBlend() {
		if(mBlendEnabled)	{
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(mBlendSrcFactor, mBlendDstFactor);
		} else
			GLES20.glDisable(GLES20.GL_BLEND);
	}
	
	private void endBlend() {
		if(mBlendEnabled) GLES20.glDisable(GLES20.GL_BLEND);
	}

	private void reset() {
		mVertexIndex = 0;
		mSpriteCount = 0;
	}
	
	@Override
	public float getAlpha() {
		return mColor.a;
	}

	@Override
	public void setAlpha(float alpha) {
		mColor.a = alpha;
	}

	@Override
	public Color4 getColor() {
		return mColor;
	}

	@Override
	public void setColor(Color4 color) {
		mColor.set(color);
	}

	@Override
	public void setColor(float a, float r, float g, float b) {
		mColor.set(a, r, g, b);
	}

	@Override
	public void setColor(int a, int r, int g, int b) {
		mColor.set(a, r, g, b);
	}
	
	@Override
	public void setColor(int color) {
		mColor.set(color);
	}

	

	// ----------- DRAW METHODS --------------------
	
	@Override
	public void draw(Texture texture, float srcX, float srcY, float srcWidth, float srcHeight, 
			float dstX, float dstY, float dstWidth, float dstHeight) {
		draw(texture, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight, false, false);
	}
	
	@Override
	public void draw(Texture texture, float srcX, float srcY, float srcWidth, float srcHeight, 
			float dstX, float dstY, float dstWidth, float dstHeight, boolean flipX, boolean flipY) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw.");

		checkTexture(texture);
		checkSpriteCount();
		
		final float[] points = POINTS;
		points[0] = dstX;
		points[1] = dstY;
		
		points[2] = dstX;
		points[3] = dstY + dstHeight;
		
		points[4] = dstX + dstWidth;
		points[5] = dstY + dstHeight;
		
		points[6] = dstX + dstWidth;
		points[7] = dstY;
		
		if(!mTransformMatrixStack.isEmpty())
			mTransformMatrixStack.peek().mapPoints(points, 0, points, 0, 4/*pair(s)*/);
		
		float u1 = srcX * mInverseTextureWidth;
		float v1 = srcY * mInverseTextureHeight;
		float u2 = (srcX + srcWidth) * mInverseTextureWidth;
		float v2 = (srcY + srcHeight) * mInverseTextureHeight;
		
		if(flipX) {
			float tmp = u1;
			u1 = u2;
			u2 = tmp;
		}
		
		if(flipY) {
			float tmp = v1;
			v1 = v2;
			v2 = tmp;
		}
		
		float a = mColor.a;
		float r = mColor.r;
		float g = mColor.g;
		float b = mColor.b;
		
		if(a != 1f) {
			r *= a;
			g *= a;
			b *= a;
		}
		
		if(!mTransformColorStack.isEmpty()) {
			Color4 c = mTransformColorStack.peek();
			r *= c.r;
			g *= c.g;
			b *= c.b;
		}
		
		float[] vertices = mVertices;
		int vertexIndex = mVertexIndex;
		vertices[vertexIndex++] = points[0];
		vertices[vertexIndex++] = points[1];
		vertices[vertexIndex++] = u1;
		vertices[vertexIndex++] = v1;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[2];
		vertices[vertexIndex++] = points[3];
		vertices[vertexIndex++] = u1;
		vertices[vertexIndex++] = v2;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[4];
		vertices[vertexIndex++] = points[5];
		vertices[vertexIndex++] = u2;
		vertices[vertexIndex++] = v2;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[6];
		vertices[vertexIndex++] = points[7];
		vertices[vertexIndex++] = u2;
		vertices[vertexIndex++] = v1;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		mVertexIndex = vertexIndex;
	}
	
	@Override
	public void draw(TextureRegion textureRegion, float dstX, float dstY) {
		draw(textureRegion, dstX, dstY, textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), false, false);
	}
	
	@Override 
	public void draw(TextureRegion textureRegion, float dstX, float dstY, float dstWidth, float dstHeight) {
		draw(textureRegion, dstX, dstY, dstWidth, dstHeight, false, false);
	}
	
	@Override 
	public void draw(TextureRegion textureRegion, float dstX, float dstY, float dstWidth, float dstHeight, boolean flipX, boolean flipY) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw.");
		
		checkTexture(textureRegion.getTexture());
		checkSpriteCount();
		
		final float[] points = POINTS;
		points[0] = dstX;
		points[1] = dstY;
		
		points[2] = dstX;
		points[3] = dstY + dstHeight;
		
		points[4] = dstX + dstWidth;
		points[5] = dstY + dstHeight;
		
		points[6] = dstX + dstWidth;
		points[7] = dstY;
		
		if(!mTransformMatrixStack.isEmpty())
			mTransformMatrixStack.peek().mapPoints(points, 0, points, 0, 4/*pair(s)*/);
		
		float u1 = textureRegion.getU1();
		float v1 = textureRegion.getV1();
		float u2 = textureRegion.getU2();
		float v2 = textureRegion.getV2();
		
		if(flipX) {
			float tmp = u1;
			u1 = u2;
			u2 = tmp;
		}
		
		if(flipY) {
			float tmp = v1;
			v1 = v2;
			v2 = tmp;
		}
		
		float a = mColor.a;
		float r = mColor.r;
		float g = mColor.g;
		float b = mColor.b;
		
		if(a != 1f) {
			r *= a;
			g *= a;
			b *= a;
		}
		
		if(!mTransformColorStack.isEmpty()) {
			Color4 c = mTransformColorStack.peek();
			r *= c.r;
			g *= c.g;
			b *= c.b;
		}
		
		float[] vertices = mVertices;
		int vertexIndex = mVertexIndex;
		vertices[vertexIndex++] = points[0];
		vertices[vertexIndex++] = points[1];
		vertices[vertexIndex++] = u1;
		vertices[vertexIndex++] = v1;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[2];
		vertices[vertexIndex++] = points[3];
		vertices[vertexIndex++] = u1;
		vertices[vertexIndex++] = v2;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[4];
		vertices[vertexIndex++] = points[5];
		vertices[vertexIndex++] = u2;
		vertices[vertexIndex++] = v2;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[6];
		vertices[vertexIndex++] = points[7];
		vertices[vertexIndex++] = u2;
		vertices[vertexIndex++] = v1;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		mVertexIndex = vertexIndex;
	}
	
	@Override
	public void draw(TextureRegion textureRegion, float dstX, float dstY,
			float dstWidth, float dstHeight, boolean flipX, boolean flipY, boolean clockwise) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw.");
		
		checkTexture(textureRegion.getTexture());
		checkSpriteCount();
		
		final float[] points = POINTS;
		points[0] = dstX;
		points[1] = dstY;
		
		points[2] = dstX;
		points[3] = dstY + dstHeight;
		
		points[4] = dstX + dstWidth;
		points[5] = dstY + dstHeight;
		
		points[6] = dstX + dstWidth;
		points[7] = dstY;
		
		if(!mTransformMatrixStack.isEmpty())
			mTransformMatrixStack.peek().mapPoints(points, 0, points, 0, 4/*pair(s)*/);
		
		float u1;
		float v1;
		float u2;
		float v2;
		float u3;
		float v3;
		float u4;
		float v4;
		
		if(clockwise) {
			u1 = textureRegion.getU1();
			v1 = textureRegion.getV2();
			u2 = textureRegion.getU2();
			v2 = textureRegion.getV2();
			u3 = textureRegion.getU2();
			v3 = textureRegion.getV1();
			u4 = textureRegion.getU1();
			v4 = textureRegion.getV1();
		} else {
			u1 = textureRegion.getU2();
			v1 = textureRegion.getV1();
			u2 = textureRegion.getU1();
			v2 = textureRegion.getV1();
			u3 = textureRegion.getU1();
			v3 = textureRegion.getV2();
			u4 = textureRegion.getU2();
			v4 = textureRegion.getV2();
		}
		
		float tmp;
		
		if(flipX) {
			tmp =v1;
			v1 = v4;
			v4 = tmp;
			
			tmp = v2;
			v2 = v3;
			v3 = tmp;
		}
		
		if(flipY) {
			tmp = u1;
			u1 = u2;
			u2 = tmp;
			
			tmp = u4;
			u4 = u3;
			u3 = tmp;
		}
		
		float a = mColor.a;
		float r = mColor.r;
		float g = mColor.g;
		float b = mColor.b;
		
		if(a != 1f) {
			r *= a;
			g *= a;
			b *= a;
		}
		
		if(!mTransformColorStack.isEmpty()) {
			Color4 c = mTransformColorStack.peek();
			r *= c.r;
			g *= c.g;
			b *= c.b;
		}
		
		float[] vertices = mVertices;
		int vertexIndex = mVertexIndex;
		vertices[vertexIndex++] = points[0];
		vertices[vertexIndex++] = points[1];
		vertices[vertexIndex++] = u1;
		vertices[vertexIndex++] = v1;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[2];
		vertices[vertexIndex++] = points[3];
		vertices[vertexIndex++] = u2;
		vertices[vertexIndex++] = v2;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[4];
		vertices[vertexIndex++] = points[5];
		vertices[vertexIndex++] = u3;
		vertices[vertexIndex++] = v3;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[6];
		vertices[vertexIndex++] = points[7];
		vertices[vertexIndex++] = u4;
		vertices[vertexIndex++] = v4;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		mVertexIndex = vertexIndex;
	}
	
	@Override
	public void draw(TextureRegion textureRegion, Form dstForm) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw.");
		
		checkTexture(textureRegion.getTexture());
		checkSpriteCount();

		float destWidth = dstForm.getWidth();
		float destHeight = dstForm.getHeight();
		
		final float[] points = POINTS;
		points[0] = 0f;
		points[1] = 0f;
		
		points[2] = 0f;
		points[3] = destHeight;
		
		points[4] = destWidth;
		points[5] = destHeight;
		
		points[6] = destWidth;
		points[7] = 0f;
		
		dstForm.getMatrix().mapPoints(points, 0, points, 0, 4/*pair(s)*/);
		
		if(!mTransformMatrixStack.isEmpty())
			mTransformMatrixStack.peek().mapPoints(points, 0, points, 0, 4/*pair(s)*/);
		
		float u1 = textureRegion.getU1();
		float v1 = textureRegion.getV1();
		float u2 = textureRegion.getU2();
		float v2 = textureRegion.getV2();
		
		float tmp;
		
		if(dstForm.isFlipX()) {
			tmp = u1;
			u1 = u2;
			u2 = tmp;
		}
		
		if(dstForm.isFlipY()) {
			tmp = v1;
			v1 = v2;
			v2 = tmp;
		}
		
		float a = dstForm.getAlpha();
		float r = dstForm.getRed();
		float g = dstForm.getGreen();
		float b = dstForm.getBlue();
		
		if(a != 1f) {
			r *= a;
			g *= a;
			b *= a;
		}
		
		if(!mTransformColorStack.isEmpty()) {
			Color4 c = mTransformColorStack.peek();
			r *= c.r;
			g *= c.g;
			b *= c.b;
		}
		
		final float[] vertices = mVertices;
		int vertexIndex = mVertexIndex;
		vertices[vertexIndex++] = points[0];
		vertices[vertexIndex++] = points[1];
		vertices[vertexIndex++] = u1;
		vertices[vertexIndex++] = v1;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[2];
		vertices[vertexIndex++] = points[3];
		vertices[vertexIndex++] = u1;
		vertices[vertexIndex++] = v2;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[4];
		vertices[vertexIndex++] = points[5];
		vertices[vertexIndex++] = u2;
		vertices[vertexIndex++] = v2;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		
		vertices[vertexIndex++] = points[6];
		vertices[vertexIndex++] = points[7];
		vertices[vertexIndex++] = u2;
		vertices[vertexIndex++] = v1;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		mVertexIndex = vertexIndex;
	}
	
	private void checkTexture(Texture texture) {
		if(mCurrentTexture == texture) return;
		
		flush();
		mCurrentTexture = texture;
		mInverseTextureWidth = 1.0f / texture.getWidth();
		mInverseTextureHeight = 1.0f / texture.getHeight();
	}
	
	private void checkSpriteCount() {
		if(mMaxSprites < ++mSpriteCount) {
			flush();
			mSpriteCount = 1;
		}
	}

	@Override
	public void pushTransformMatrix(Matrix3 matrix) {
		mTransformMatrixStack.push(matrix);
	}
	
	@Override
	public Matrix3 peekTransformMatrix() {
		if(mTransformMatrixStack.isEmpty()) return null;
		return mTransformMatrixStack.peek();
	}

	@Override
	public Matrix3 popTransformMatrix() {
		return mTransformMatrixStack.pop();
	}
	
	@Override
	public void pushTransformColor(Color4 color) {
		mTransformColorStack.push(color);
	}
	
	@Override
	public Color4 peekTransformColor() {
		if(mTransformColorStack.isEmpty()) return null;
		return mTransformColorStack.peek();
	}
	
	@Override
	public Color4 popTransformColor() {
		return mTransformColorStack.pop();
	}

	@Override
	public Matrix4 getProjectionMatrix() {
		return mProjectionMatrix;
	}

	@Override
	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		if(mDrawing) flush();
		mProjectionMatrix.set(projectionMatrix);
	}
	
	@Override
	public boolean isBlendEnabled() {
		return mBlendEnabled;
	}

	@Override
	public void setBlendEnabled(boolean blendEnabled) {
		if(mBlendEnabled == blendEnabled) return;
		if(mDrawing) flush();
		mBlendEnabled = blendEnabled;
	}
	
	@Override
	public int getBlendSrcFactor() {
		return mBlendSrcFactor;
	}

	@Override
	public int getBlendDstFactor() {
		return mBlendDstFactor;
	}

	@Override
	public void setBlendFunc(int srcFactor, int dstFactor) {
		if(mBlendSrcFactor == srcFactor && mBlendDstFactor == dstFactor) return;
		if(mDrawing) flush();
		mBlendSrcFactor = srcFactor;
		mBlendDstFactor = dstFactor;
	}
	
	@Override
	public ShaderProgram getShaderProgram() {
		return mProgram;
	}

	@Override
	public void setShaderProgram(ShaderProgram program) {
		if(mDrawing) flush();
		mProgram = program;
	}

	@Override
	public int getMaxSprites() {
		return mMaxSprites;
	}

	@Override
	public int getSpriteCount() {
		return mSpriteCount;
	}

	@Override
	public void dispose() {
		mMesh.dispose();
		mProgram.dispose();
	}

}
