package core.framework.graphics.texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.SparseArray;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.TextureRegion.ImmutableTextureRegion;
import core.framework.graphics.utils.GLResource;
import core.math.Vector2;
import core.utils.ResUtils;

/**
 * OpenGL의 텍스쳐를 랩핑한다. Texture에 정의된 각각의 이미지를 TextureRegion맵으로 
 * 관리하는 역할도 한다.</p>
 * 
 * Texture은 2D그래픽에서 개발의 시초라고 할 수 있는 부분이다. 2D그래픽에서는 보통 
 * 사격형 영역을 사용하는데 여기에 입힐 옷이 바로 Texture이기 때문이다. 이 Texture을 
 * 어떻게 화면에 렌더링할 것인가가 다음 단계인 것이다.</p>
 * 
 * Texture의 소스 타입은 비트맵과 커스텀으로 구분된다.</p>
 * 
 * @author 김현우
 */
public abstract class Texture implements GLResource{
	
	public enum TextureFilter {
		LINEAR(GLES20.GL_LINEAR), 
		NEAREST(GLES20.GL_NEAREST);
		
		private int mFilter;
		TextureFilter(int filter) {mFilter = filter;}
		public int getGLConstant() {return mFilter;}
	}
	
	public enum TextureWrap {
		MIRRORED_REPEAT(GLES20.GL_MIRRORED_REPEAT), 
		CLAMP_TO_EDGE(GLES20.GL_CLAMP_TO_EDGE), 
		REPEAT(GLES20.GL_REPEAT);
		
		private int mWrap;
		TextureWrap(int wrap) {mWrap = wrap;}
		public int getGLConstant() {return mWrap;}
	}
	
	public enum TextureSrcType {
		BITMAP, 
		CUSTOM, 
	}
	
	public static Color4 sRegionDebugColor = Color4.blue();
	
	public static final TextureFilter DEFAULT_MAG_FILTER = TextureFilter.LINEAR;
	public static final TextureFilter DEFAULT_MIN_FILTER = TextureFilter.LINEAR;
	
	public static final TextureWrap DEFAULT_WRAP_S = TextureWrap.REPEAT;
	public static final TextureWrap DEFAULT_WRAP_T = TextureWrap.REPEAT;
	
	private static final List<String> TEMP_STRING_LIST = new ArrayList<String>();
	
	private static final int[] TMP_TEXTURES = new int[1];
	
	public static final int MAX_TEXTURE_WIDTH = 2048;
	public static final int MAX_TEXTURE_HEIGHT = 2048;
	
	/** 각 영역 사이의 패딩 */
	public static final int PADDING = 2;
	
	public static final TextureUpdate TEXTURE_UPDATE = new TextureUpdate();
	
	protected static final Vector2 VECTOR = new Vector2();
	
	/** OpenGL을 통해 생성한 텍스쳐 아이디 */
	private int mTextureID;
	/** R 클래스에 정의된 이미지 아이디 */
	private int mResID;
	
	/** 텍스쳐의 출처 종류 */
	protected TextureSrcType mTextureSrcType;
	
	/** 텍스쳐의 너비 */
	protected int mWidth;
	/** 텍스쳐의 높이 */
	protected int mHeight;
	
	/** 텍스쳐 확대 필터 */
	private TextureFilter mTextureMagFilter;
	/** 텍스쳐 축소 필터 */
	private TextureFilter mTextureMinFilter;
	
	/** 텍스쳐 가로 Wrap */
	private TextureWrap mTextureWrapS;
	/** 텍스쳐 세로 Wrap */
	private TextureWrap mTextureWrapT;
	
	/** TextureRegion과 관련된 String과 그 영역 ID의 맵핑 */
	private Map<String, Integer> mStringToIDMap = new HashMap<String, Integer>();
	
	/** TextureRegion의 ID와 그 영역의 맵핑 */
	/*package*/ SparseArray<TextureRegion[]> mTextureRegionsMap = new SparseArray<TextureRegion[]>();
	
	private List<Runnable> mRunnableList = new ArrayList<Runnable>();
	
	private TextureRegion mTextureRegion;
	
	private boolean mBound;
	
	private boolean mParsing;
	private int mTextureMapResID;
	
	/** 리소스 아이디에 해당하는 이미지로 텍스쳐를 생성한다. */
	/*package*/ Texture(int resID) {
		mResID = resID;
		mTextureSrcType = TextureSrcType.BITMAP;
		createTexture();
		Core.GRAPHICS.addGLReloadable(this);
	}

	/** 지정한 사이즈로 빈 텍스쳐를 생성한다. */
	/*package*/ Texture(int width, int height) {
		mWidth = width;
		mHeight = height;
		mTextureSrcType = TextureSrcType.CUSTOM;
		createTexture();
		Core.GRAPHICS.addGLReloadable(this);
	}
	
	private void createTexture() {
		GLES20.glGenTextures(1, TMP_TEXTURES, 0);
		mTextureID = TMP_TEXTURES[0];
		bind();
		load();
		unbind();
	}
	
	/** 
	 * SpriteSheetPacker을 통해 생성된 텍스쳐맵 정보를 파싱하여 연관된 Texture에 
	 * 각 TextureRegion의 정보를 채운다. 
	 */ 
	public void parseTextureMap(int textureMapResID) {
		if(mTextureSrcType != TextureSrcType.BITMAP)
			throw new IllegalStateException("Only Bitmap Texture can call this method");
		
		mParsing = true;
		mTextureMapResID = textureMapResID;
		
		Texture texture = this;
		
		// sprite sheet에서의 개별 비트맵의 위치 정보를 구한다.
		String input = ResUtils.openRawResourceAsString(textureMapResID);
			
		// input을 토큰으로 구분  
		StringTokenizer s = new StringTokenizer(input, " =\r\n\t");
		
		while(s.hasMoreTokens()) {
			// 각 이미지의 이름
			String name = s.nextToken();
			
			// 각 이미지의 위치, 너비, 높이
			int x = Integer.parseInt(s.nextToken());
			int y = Integer.parseInt(s.nextToken());
			int w = Integer.parseInt(s.nextToken());
			int h = Integer.parseInt(s.nextToken());

			TextureRegion textureRegion = new ImmutableTextureRegion(texture, x, y, w, h);
			
			int id = Core.APP.genCustomID();
			texture.addTextureRegion(id, textureRegion);
			texture.mapStringToID(name, id);
		}
	}

	/**
	 * 텍스쳐를 바인딩한다. 기본적으로 Texture의 다른 메서드는 바인딩을 내부적으로 하지 않기 
	 * 때문에 명시적으로 이 메서드를 호출해야 한다.
	 */
	@Override
	public void bind() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
		mBound = true;
	}
	
	/** 텍스쳐의 바인딩을 해제한다. */
	@Override
	public void unbind() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		mBound = false;
	}
	
	@Override
	public void load() {
		switch(mTextureSrcType) {
			case BITMAP:
				Bitmap bitmap = Core.GRAPHICS.getBitmap(mResID);
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				mWidth = bitmap.getWidth();
				mHeight = bitmap.getHeight();
				bitmap.recycle();
				break;
			case CUSTOM:
				GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 
						mWidth, mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
				break;
		}
		
		setFilter(DEFAULT_MAG_FILTER, DEFAULT_MIN_FILTER);
		setWrap(DEFAULT_WRAP_S, DEFAULT_WRAP_T);
	}

	@Override
	public void reload() {
		if(GLES20.glIsTexture(mTextureID)) return;
		createTexture();
		clear();
		if(mParsing) parseTextureMap(mTextureMapResID);
		runList();
	}

	/** 미리 로드된 텍스쳐에 텍스쳐의 크기 또는 이보다 작은 Bitmap을 offset위치에 로드한다. */
	@Override
	public void update(Object content) {
		if(!mBound)
			throw new IllegalStateException("bind() must be called before update");
		if(!(content instanceof TextureUpdate))
			throw new IllegalArgumentException("Only TextureUpdate instance is allowed");
		
		TextureUpdate update = (TextureUpdate) content;
	
		GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, update.offsetX, update.offsetY, update.bitmap);
	}

	@Override
	public void dispose() {
		unbind();
		TMP_TEXTURES[0] = mTextureID;
		GLES20.glDeleteFramebuffers(1, TMP_TEXTURES, 0);
		Core.GRAPHICS.removeGLReloadable(this);
	}

	/** 확대 필터를 얻는다. */
	public TextureFilter getMagFilter() {
		return mTextureMagFilter;
	}
	
	/** 축소 필터를 얻는다. */
	public TextureFilter getMinFilter() {
		return mTextureMinFilter;
	}
	
	/** 텍스쳐의 필터를 지정한다. 사용하기 전에 적절한 텍스쳐 바인딩이 요구된다. */
	public void setFilter(TextureFilter glTextureMagFilter, TextureFilter glTextureMinFilter) {
		mTextureMagFilter = glTextureMagFilter;
		mTextureMinFilter = glTextureMinFilter;
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 
				glTextureMagFilter.getGLConstant());
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, 
				glTextureMinFilter.getGLConstant());
	}
	
	/** WrapS를 얻는다. */
	public TextureWrap getWrapS() {
		return mTextureWrapS;
	}
	
	/** WrapT를 얻는다. */
	public TextureWrap getWrapT() {
		return mTextureWrapT;
	}
	
	/** 텍스쳐의 Wrap을 지정한다. 사용하기 전에 적절한 텍스쳐 바인딩이 요구된다. */
	public void setWrap(TextureWrap glTextureWrapS, TextureWrap glTextureWrapT) {
		mTextureWrapS = glTextureWrapS;
		mTextureWrapT = glTextureWrapT;
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				glTextureWrapS.getGLConstant());
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				glTextureWrapT.getGLConstant());
	}
	
	/** 텍스쳐를 전부 지우고 등록된 모든 TextureRegion을 관리하는 변수들을 초기화한다. */
	public void clear() {
		removeRegionImage(0, 0, mWidth, mHeight);
		mStringToIDMap.clear();
		mTextureRegionsMap.clear();
	}

	private void runList() {
		List<Runnable> runnableList = mRunnableList;
		int n = mRunnableList.size();
		for(int i=0; i<n; i++) {
			Runnable reloadable = mRunnableList.get(i);
			reloadable.run();
		}
	}

	public void draw(Bitmap bitmap, int offsetX, int offsetY) {
		update(TEXTURE_UPDATE.set(bitmap, offsetX, offsetY));
	}
	
	/**
	 * 내부적으로 생성한 id와 textureRegion을 맵핑한다. 생성된 id는 리턴 값으로 
	 * 반환된다. 삽입되는 객체는 수정할 수 없는 사본으로 저장된다.
	 */
	public int addTextureRegion(TextureRegion textureRegion) {
		int id = Core.APP.genCustomID();
		TextureRegion[] textureRegions = new TextureRegion[1];
		textureRegions[0] = new ImmutableTextureRegion(textureRegion);
		mTextureRegionsMap.put(id, textureRegions);
		return id;
	}

	/**
	 * 입력된 id와 textureRegion을 맵핑한다. 이미 맵핑한 id를 또 사용하면 예외처리가 
	 * 발생한다. 삽입되는 객체는 수정할 수 없는 사본으로 저장된다.
	 */
	public void addTextureRegion(int id, TextureRegion textureRegion) {
		if(mTextureRegionsMap.get(id) != null)
			throw new IllegalStateException("textureRegion with the same id arleay exist");
		
		TextureRegion[] textureRegions = new TextureRegion[1];
		textureRegions[0] = new ImmutableTextureRegion(textureRegion);
		mTextureRegionsMap.put(id, textureRegions);
	}
	
	public void addTextureRegions(int id, TextureRegion[] textureRegions) {
		if(mTextureRegionsMap.get(id) != null)
			throw new IllegalStateException("textureRegions with the same id arleay exist");
		
		TextureRegion[] regions = textureRegions;
		for(int i=0; i<regions.length; i++)
			regions[i] = new ImmutableTextureRegion(textureRegions[i]);
		
		mTextureRegionsMap.put(id, regions);
	}
	
	/**
	 * 텍스쳐의 전체 크기만큼의 영역을 TextureRegion으로 반환한다. 반환된 객체는 수정할 
	 * 수 없다.
	 */
	public TextureRegion getAsTextureRegion() {
		if(mTextureRegion == null)
			mTextureRegion = new ImmutableTextureRegion(this, 0, 0, mWidth, mHeight);
		
		return mTextureRegion;
	}

	/**
	 * 텍스쳐에 삽입된 TextureRegion을 고유 ID와 맵핑된 String을 통해 얻는다. 내부적으로 
	 * {@link #getIDByString(String)}를 통해 고유 ID을 얻어 {@link #getTextureRegion(int)}을 
	 * 호출한다.
	 */
	public TextureRegion getTextureRegion(String string) {
		return getTextureRegion(getIDByString(string));
	}
	
	public TextureRegion getTextureRegion(String string, int index) {
		return getTextureRegion(getIDByString(string), index);
	}
	
	public TextureRegion[] getTextureRegions(String string) {
		return getTextureRegions(getIDByString(string));
	}
	
	/**
	 * 텍스쳐에 삽입된 TextureRegion을 고유 ID을 통해 얻는다. 반환된 객체는
	 * 수정할 수 없다.
	 */
	public TextureRegion getTextureRegion(int id) {
		return mTextureRegionsMap.get(id)[0];
	}
	
	/**
	 * 텍스쳐에 삽입된 TextureRegion을 고유 ID와 인덱스를 통해 얻는다. 삽입된 
	 * TextureRegion이 배열인 경우에 사용한다. 반환된 객체는 수정할 수 없다.
	 */
	public TextureRegion getTextureRegion(int id, int index) {
		return mTextureRegionsMap.get(id)[index];
	}
	
	public TextureRegion[] getTextureRegions(int id) {
		return mTextureRegionsMap.get(id);
	}
	
	public int getTextureRegionCount(int id) {
		return mTextureRegionsMap.get(id).length;
	}
	
	public void removeTextureRegion(String string) {
		removeTextureRegion(getIDByString(string));
	}

	public void removeTextureRegion(int id) {
		TextureRegion[] regions = mTextureRegionsMap.get(id);
		if(regions == null) return;
		
		int n = regions.length;
		for(int i=0; i<n; i++)
			removeTextureRegion(regions[i]);
			
		mTextureRegionsMap.delete(id);
	}
	
	/** 
	 * 개별 TextureRegion을 제거하면서 필요한 작업을 처리한다. 기본적으로 
	 * {@link #removeRegionImage(int, int, int, int)}를 통해 기존의 이미지를 완전히 
	 * 제거하며 추가적으로 필요한 작업을 정의한다.</p>
	 * 
	 * 기존에 남아있는 이미지를 완전히 지우는 이유는 남아있는 이미지가 이후에 
	 * 간섭할 여지가 있기 때문이다.</p>
	 */
	protected void removeTextureRegion(TextureRegion region) {
	}
	
	/** Texture에서 지정한 사각영역에 남아있는 이미지를 제거한다. */
	public void removeRegionImage(int x, int y, int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		
		bind();
		update(TEXTURE_UPDATE.set(bitmap, x, y));
		bitmap.recycle();
		unbind();
	}
	
	public int getTextureID() {
		return mTextureID;
	}

	/** 텍스쳐의 리로스 아이디를 얻는다. 커스텀 텍스쳐인 경우 0을 리턴한다. */
	public int getResID() {
		return mResID;
	}

	/** 텍스쳐의 너비를 얻는다. */
	public int getWidth() {
		return mWidth;
	}

	/** 텍스쳐의 높이를 얻는다. */
	public int getHeight() {
		return mHeight;
	}

	public int getIDByString(String string) {
		return mStringToIDMap.get(string);
	}
	
	/**
	 * 입력된 string와 TextureRegion의 고유 ID를 맵핑한다. 고유 ID는 TextureRegion과 
	 * 미리 맵핑되었다고 가정하며, 그렇지 않은 경우에는 예외가 발생한다.</p>
	 * 
	 * 이 메서드를 이용하여 고유 ID에 여러 string을 맵핑할 수 있다.</p>
	 */
	public void mapStringToID(String string, int id) {
		if(mTextureRegionsMap.get(id) == null)
			throw new IllegalStateException("no such id in StringToIDMap");

		mStringToIDMap.put(string, id);
	}
	
	public void removeStringsToIDMap(int id) {
		final List<String> stringList = TEMP_STRING_LIST;
		stringList.clear();
		
		Map<String, Integer> stringToIdMap = mStringToIDMap;
		Set<Entry<String, Integer>> set = stringToIdMap.entrySet();
		for(Entry<String, Integer> entry : set) {
			if(entry.getValue() == id) stringList.add(entry.getKey());
		}
		
		int n = stringList.size();
		for(int i=0; i<n; i++)
			stringToIdMap.remove(stringList.get(i));
	}
	
	public TextureSrcType getTextureSrcType() {
		return mTextureSrcType;
	}
	
	public void addRunnable(Runnable runnable) {
		mRunnableList.add(runnable);
	}
	
	public void drawDebug(Batch batch, ShapeRenderer renderer) {
		drawDebug(batch, renderer, Core.GRAPHICS.getVirtualWidth(), Core.GRAPHICS.getVirtualHeight());
	}
	
	public void drawDebug(Batch batch, ShapeRenderer renderer, float width, float height) {
		batch.draw(getAsTextureRegion(), 0f, 0f, width, height);
		
		renderer.setColor(sRegionDebugColor);
		
		float widthScale = width / mWidth;
		float heightScale = height / mHeight;
		
 		SparseArray<TextureRegion[]> textureRegionsMap = mTextureRegionsMap;
 		int n = textureRegionsMap.size();
		for(int i=0; i<n; i++) {
			TextureRegion[] regions = textureRegionsMap.valueAt(i);
			int m = regions.length;
			for(int j=0; j<m; j++) {
				TextureRegion region = regions[j];
				renderer.drawRect(region.getRegionX1()*widthScale, region.getRegionY1()*heightScale, 
						region.getRegionWidth()*widthScale, region.getRegionHeight()*heightScale);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName() + " [mTextureID=").append(mTextureID)
				.append(", mResID=").append(mResID)
				.append(", mTextureSrcType=").append(mTextureSrcType)
				.append(", mWidth=").append(mWidth)
				.append(", mHeight=").append(mHeight).append("]");
		return builder.toString();
	}
	
	public static class TextureUpdate {
		private Bitmap bitmap; 
		private int offsetX;
		private int offsetY;
		
		public TextureUpdate set(Bitmap bitmap, int offsetX, int offsetY) {
			this.bitmap = bitmap;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			return this;
		}
	}
	
}
