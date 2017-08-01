package core.framework.graphics.texture;

/**
 * Texture의 일정 영역을 관리한다. Texture는 일반적으로 여러 이미지가 세트로 이루어진 경우가 
 * 대부분이므로 특정 이미지의 영역을 관리하는 역할을 담당한다.</p>
 * 
 * <b>주의</b> : 안드로이드의 Bitmap과 OpenGL의 텍스쳐는 원점이 다르다. 전자는 좌측 상단이고 
 * 후자는 좌측 하단이다. 따라서 Bitmap의 이미지가 텍스쳐에는 거꾸로 로드된다. 하지만 텍스쳐의 
 * 원점이 좌측 상단이라고 생각하면 뒤집히지 않은 처음의 이미지로써 사용할 수도 있다. 따라서 
 * 여기서는 OpenGL처럼 좌측 하단이 아닌 Bitmap의 좌측 상단을 원점으로 여기고 모든 주석을 이에 맞춰 
 * 설명한다.</p>
 * 
 * @author 김현우
 */
public class TextureRegion {
	
	/** TextureRegion의 소속 Texture */
	private Texture mTexture;
	
	/** 텍셀 단위의 좌측 상단 u좌표 */
	private float mU1;
	/** 텍셀 단위의 좌측 상단 v좌표 */
	private float mV1;
	/** 텍셀 단위의 우측 하단 u좌표 */
	private float mU2;
	/** 텍셀 단위의 우측 하단 v좌표 */
	private float mV2;
	/** 픽셀 단위의 가로 길이 */
	private int mRegionWidth;
	/** 픽셀 단위의 세로 길이 */
	private int mRegionHeight;
	/** 픽셀 단위의 좌측 상단 x좌표 */
	private int mRegionX1;
	/** 픽셀 단위의 좌측 상단 y좌표 */
	private int mRegionY1;
	/** 픽셀 단위의 우측 하단 x좌표 */
	private int mRegionX2;
	/** 픽셀 단위의 우측 하단 y좌표 */
	private int mRegionY2;
	
	private TextureRegion() {
	}
	
	public TextureRegion(Texture texture) {
		setTexture(texture);
	}
	
	public TextureRegion(TextureRegion textureRegion) {
		setRegion(textureRegion);
	}
	
	public TextureRegion(Texture texture, int x, int y, int width	, int height) {
		setTexture(texture);
		setRegion(x, y, width, height);
	}
	
	public TextureRegion(Texture texture, float u1, float v1, float u2, float v2) {
		setTexture(texture);
		setRegion(u1, v1, u2, v2);
	}
	
	public void setRegion(TextureRegion textureRegion) {
		mTexture = textureRegion.mTexture;
		mU1 = textureRegion.mU1;
		mV1 = textureRegion.mV1;
		mU2 = textureRegion.mU2;
		mV2 = textureRegion.mV2;
		mRegionWidth = textureRegion.mRegionWidth;
		mRegionHeight = textureRegion.mRegionHeight;
		mRegionX1 = textureRegion.mRegionX1;
		mRegionY1 = textureRegion.mRegionY1;
		mRegionX2 = textureRegion.mRegionX2;
		mRegionY2 = textureRegion.mRegionY2;
	}
	
	public void setRegion(int x, int y, int width, int height) {
		float textureWidth = mTexture.getWidth();
		float textureHeight = mTexture.getHeight();
		mU1 = x / textureWidth;
		mV1	= y / textureHeight;
		mU2 = (x + width) / textureWidth;
		mV2 = (y + height) / textureHeight;
		mRegionX1 = x;
		mRegionY1 = y;
		mRegionX2 = x + width;
		mRegionY2 = y + height;
		mRegionWidth = width;
		mRegionHeight = height;
	}
	
	public void setRegion(float u1, float v1, float u2, float v2) {
		int textureWidth = mTexture.getWidth();
		int textureHeight = mTexture.getHeight();
		mU1 = u1;
		mV1 = v1;
		mU2 = u2;
		mV2 = v2;
		mRegionX1 = Math.round(mU1 * textureWidth);
		mRegionY1 = Math.round(mV1 * textureHeight);
		mRegionX2 = Math.round(mU2 * textureWidth);
		mRegionY2 = Math.round(mV2 * textureHeight);
		mRegionWidth = mRegionX2 - mRegionX1;
		mRegionHeight = mRegionY2 - mRegionY1;
	}
	
	/** TextureRegion의 좌측 상단 위치의 텍셀 u좌표 */
	public float getU1() {
		return mU1;
	}
	
	/** TextureRegion의 좌측 상단 위치의 텍셀 v좌표 */
	public float getV1() {
		return mV1;
	}
	
	/** TextureRegion의 우측 하단 위치의 텍셀 u좌표 */
	public float getU2() {
		return mU2;
	}
	
	/** TextureRegion의 우측 하단 위치의 텍셀 v좌표 */
	public float getV2() {
		return mV2;
	}

	/** TextureRegion의 가로 길이를 픽셀 단위로 구한다. */
	public int getRegionWidth() {
		return mRegionWidth;
	}

	/** TextureRegion의 세로 길이를 픽셀 단위로 구한다. */
	public int getRegionHeight() {
		return mRegionHeight;
	}
	
	/** TextureRegion의 좌측 상단 위치의 픽셀 x좌표 */
	public int getRegionX1() {
		return mRegionX1;
	}
	
	/** TextureRegion의 좌측 상단 위치의 픽셀 y좌표 */
	public int getRegionY1() {
		return mRegionY1;
	}
	
	/** TextureRegion의 우측 하단 위치의 픽셀 x좌표 */
	public int getRegionX2() {
		return mRegionX2;
	}
	
	/** TextureRegion의 우측 하단 위치의 픽셀 y좌표 */
	public int getRegionY2() {
		return mRegionY2;
	}
	
	public Texture getTexture() {
		return mTexture;
	}

	public void setTexture(Texture texture) {
		mTexture = texture;
	}
	
	public static class ImmutableTextureRegion extends TextureRegion {

		public ImmutableTextureRegion(Texture texture) {
			super.setTexture(texture);
		}
		
		public ImmutableTextureRegion(Texture texture, int x, int y, int width	, int height) {
			super.setTexture(texture);
			super.setRegion(x, y, width, height);
		}
		
		public ImmutableTextureRegion(Texture texture, float u1, float v1, float u2, float v2) {
			super.setTexture(texture);
			super.setRegion(u1, v1, u2, v2);
		}
		
		public ImmutableTextureRegion(TextureRegion textureRegion) {
			super.setRegion(textureRegion);
		}
		
		@Override
		public void setRegion(int x, int y, int width, int height) {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void setRegion(float u1, float v1, float u2, float v2) {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void setRegion(TextureRegion textureRegion) {
			throw new UnsupportedOperationException("This instance is immutable");
		}
		
		@Override
		public void setTexture(Texture texture) {
			throw new UnsupportedOperationException("This instance is immutable");
		}
	}

}
