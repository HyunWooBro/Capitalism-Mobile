package core.framework.graphics.texture;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;

import core.framework.Core;
import core.framework.graphics.texture.TextureRegion.ImmutableTextureRegion;
import core.math.Vector2;
import core.utils.Couple;

public class FontTexture extends Texture {
	
	public static int sFontSrcHeight = 50;
	public static int sFontSrcBaseline = 40;
	
	public static int sFontDestHeight = 25;
	public static int sFontDestBaseline = 20;
	
	public static float sFontPadding = 2f;
	
	public static int sFontDrawCount = 8;
	
	private static final List<String> TEMP_STRING_LIST = new ArrayList<String>();
	
	private static final int NOT_DEFINED = -1;
	
	/** 
	 * String영역 위치배정에 사용하는 보조 리스트(사용 여부, 텍스쳐에서의 위치) 
	 * 이미지를 기준으로 좌상단이 아니라 우상단의 좌표를 저장한다. 
	 */
	private List<List<Couple<Boolean, Vector2>>> mRegionPosList = new ArrayList<List<Couple<Boolean, Vector2>>>();
	
	public FontTexture(int resID) {
		super(resID);
	}

	public FontTexture(int width, int height) {
		super(width, height);
	}
	
	public void addStringRegion(int stringID, Paint p) {
		int drawCount = sFontDrawCount;
		float padding = sFontPadding;
		int bottomHeight = sFontSrcHeight - sFontSrcBaseline;
		int maxHeight = sFontSrcHeight;
		float oldTextSize = p.getTextSize();
		float scale = (float) FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		p.setTextSize(oldTextSize * scale);
		Bitmap bitmap = Core.GRAPHICS.createStringBitmap(stringID, drawCount, padding, bottomHeight, maxHeight, p);
		p.setTextSize(oldTextSize);
		
		TextureRegion textureRegion = load(bitmap, NOT_DEFINED);
		addTextureRegion(stringID, textureRegion);
	}
	
	public int addStringRegion(String string, Paint p) {
		int drawCount = sFontDrawCount;
		float padding = sFontPadding;
		int bottomHeight = sFontSrcHeight - sFontSrcBaseline;
		int maxHeight = sFontSrcHeight;
		float oldTextSize = p.getTextSize();
		float scale = (float) FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		p.setTextSize(oldTextSize * scale);
		Bitmap bitmap = Core.GRAPHICS.createStringBitmap(string, drawCount, padding, bottomHeight, maxHeight, p);
		p.setTextSize(oldTextSize);
		
		TextureRegion textureRegion = load(bitmap, NOT_DEFINED);
		
		int id = Core.APP.genCustomID();
		addTextureRegion(id, textureRegion);
		return id;
	}

	public void addStringMultiLineRegions(int stringID, Paint p) {
		int drawCount = sFontDrawCount;
		float padding = sFontPadding;
		int bottomHeight = sFontSrcHeight - sFontSrcBaseline;
		int maxHeight = sFontSrcHeight;
		float oldTextSize = p.getTextSize();
		float scale = (float) FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		final List<String> stringList = TEMP_STRING_LIST;
		stringList.clear();
		String string = Core.APP.getResources().getString(stringID);
		for(String token : string.split("\n")) stringList.add(token);
		
		int n = stringList.size();
		TextureRegion[] textureRegions = new TextureRegion[n];
		Bitmap[] bitmaps = new Bitmap[n];
		
		p.setTextSize(oldTextSize * scale);
		
		int regionHeight = 0;
		for(int i=0; i<n; i++) {
			bitmaps[i] = Core.GRAPHICS.createStringBitmap(stringList.get(i), drawCount, padding, bottomHeight, maxHeight, p);
			int height = bitmaps[i].getHeight();
			if(regionHeight < height) regionHeight = height;
		}

		p.setTextSize(oldTextSize);
		
		for(int i=0; i<n; i++) {
			TextureRegion textureRegion = load(bitmaps[i], regionHeight);
			textureRegions[i] = textureRegion;
		}
		
		addTextureRegions(stringID, textureRegions);
	}
	
	public int addStringMultiLineRegions(String string, Paint p) {
		int drawCount = sFontDrawCount;
		float padding = sFontPadding;
		int bottomHeight = sFontSrcHeight - sFontSrcBaseline;
		int maxHeight = sFontSrcHeight;
		float oldTextSize = p.getTextSize();
		float scale = (float) FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		final List<String> stringList = TEMP_STRING_LIST;
		stringList.clear();
		for(String token : string.split("\n")) stringList.add(token);
		
		int n = stringList.size();
		TextureRegion[] textureRegions = new TextureRegion[n];
		Bitmap[] bitmaps = new Bitmap[n];
		
		p.setTextSize(oldTextSize * scale);
		
		int regionHeight = 0;
		for(int i=0; i<n; i++) {
			bitmaps[i] = Core.GRAPHICS.createStringBitmap(stringList.get(i), drawCount, padding, bottomHeight, maxHeight, p);
			int height = bitmaps[i].getHeight();
			if(regionHeight < height) regionHeight = height;
		}

		p.setTextSize(oldTextSize);
		
		for(int i=0; i<n; i++) {
			TextureRegion textureRegion = load(bitmaps[i], regionHeight);
			textureRegions[i] = textureRegion;
		}
		
		int id = Core.APP.genCustomID();
		addTextureRegions(id, textureRegions);
		return id;
	}
	
	public int addStringWrapRegions(String string, float width, Paint p) {
		int drawCount = sFontDrawCount;
		float padding = sFontPadding;
		int bottomHeight = sFontSrcHeight - sFontSrcBaseline;
		int maxHeight = sFontSrcHeight;
		float oldTextSize = p.getTextSize();
		float scale = (float) FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		final List<String> stringList = TEMP_STRING_LIST;
		stringList.clear();
		
		int start = 0;
		int end = string.length();
out1:
		while(true) {
			int length = p.breakText(string, start, end, true, width, null);
			if(length == 0) break;

			int n = start + length;
			for(int i=start; i<n; i++) {
				if(string.charAt(i) == '\n') {
					stringList.add(string.substring(start, i));
					start = i + 1;
					continue out1;
				}
			}
			
			stringList.add(string.substring(start, n));
			start = n;
		}
		
		int n = stringList.size();
		TextureRegion[] textureRegions = new TextureRegion[n];
		Bitmap[] bitmaps = new Bitmap[n];
		
		p.setTextSize(oldTextSize * scale);
		
		int regionHeight = 0;
		for(int i=0; i<n; i++) {
			bitmaps[i] = Core.GRAPHICS.createStringBitmap(stringList.get(i), drawCount, padding, bottomHeight, maxHeight, p);
			int height = bitmaps[i].getHeight();
			if(regionHeight < height) regionHeight = height;
		}

		p.setTextSize(oldTextSize);
		
		for(int i=0; i<n; i++) {
			TextureRegion textureRegion = load(bitmaps[i], regionHeight);
			textureRegions[i] = textureRegion;
		}
		
		int id = Core.APP.genCustomID();
		addTextureRegions(id, textureRegions);
		return id;
	}
	
	public void addStringArrayRegions(int stringArrayID, Paint p) {
		int drawCount = sFontDrawCount;
		float padding = sFontPadding;
		int bottomHeight = sFontSrcHeight - sFontSrcBaseline;
		int maxHeight = sFontSrcHeight;
		float oldTextSize = p.getTextSize();
		float scale = (float) FontTexture.sFontSrcHeight / FontTexture.sFontDestHeight;
		
		p.setTextSize(oldTextSize * scale);
		Bitmap[] bitmaps = Core.GRAPHICS.createStringArrayBitmaps(stringArrayID, drawCount, padding, bottomHeight, maxHeight, p);
		p.setTextSize(oldTextSize);
		
		TextureRegion[] textureRegions = new TextureRegion[bitmaps.length];
		
		int regionHeight = 0;
		for(int i=0; i<bitmaps.length; i++) {
			int height = bitmaps[i].getHeight();
			if(regionHeight < height) regionHeight = height;
		}
		
		for(int i=0; i<bitmaps.length; i++) {
			TextureRegion textureRegion = load(bitmaps[i], regionHeight);
			textureRegions[i] = textureRegion;
		}
		
		addTextureRegions(stringArrayID, textureRegions);
	}
	
	private TextureRegion load(Bitmap bitmap, int regionHeight) {
		final Vector2 v = VECTOR;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int offset = sFontSrcHeight - height;

		getNextRegionPos(v, width);
		
		bind();
		update(TEXTURE_UPDATE.set(bitmap, (int) v.x, (int) v.y + offset));
		bitmap.recycle();
		unbind();
		
		int regionOffset;
		if(regionHeight == NOT_DEFINED) {
			regionOffset = offset;
		} else
			regionOffset = sFontSrcHeight - regionHeight;
		
		TextureRegion textureRegion = new ImmutableTextureRegion(
				this,
				(int) v.x, 
				(int) v.y + regionOffset, 
				width, 
				(regionHeight == NOT_DEFINED)? height : regionHeight);
		
		return textureRegion;
	}
	
	/** 
	 * 다음 사용 가능한 pos을 얻는다. startPos를 통해 시작 위치가 반환된다. 
	 * 더 이상 공간이 없다면 false를 리턴한다. 
	 */
	private boolean getNextRegionPos(Vector2 startPos, int width) {
		List<List<Couple<Boolean, Vector2>>> regionPosList = mRegionPosList;
		
		// 비어 있다면
		if(regionPosList.isEmpty()) {
			// 필요한 너비가 텍스쳐의 너비보다 작다면
			float space = mWidth;
			if(width <= space) {
				// 첫행 추가
				regionPosList.add(createNewRow());
				// 첫행의 첫번째 pos 추가
				regionPosList.get(0).add(createNewColumn(width, 0f));
				startPos.set(0f, 0f);
				return true;
			}

			// 더 이상 추가할 공간이 없을 때
			return false;
		}
		
		// 행의 개수
		int n = regionPosList.size();
		for(int i=0; i<n; i++) {
			List<Couple<Boolean, Vector2>> rowRegionPosList = mRegionPosList.get(i);
			
			// 열의 개수
			int m = rowRegionPosList.size();
out2:
			for(int j=0; j<m; j++) {
				Couple<Boolean, Vector2> regionPos = rowRegionPosList.get(j);
				
				// first가 false인 열을 찾을 때까지 순환
				while(regionPos.first) {
					// 만약 마지막 열까지 first가 true이면 이제 빠져나간다.
					if(j == m-1) break;
					continue out2;
				}
				
				// 행의 마지막 열일 때
				if(j == m-1) {
					// 마지막 열의 first가 false인 경우(처음 열일 수도 있음)
					if(!regionPos.first) {
						if(insert(startPos, width, i, j)) return true;
					} else {
						float space = mWidth - (regionPos.second.x + PADDING);
						if(width <= space) {
							rowRegionPosList.add(createNewColumn(
									regionPos.second.x + PADDING + width, 
									regionPos.second.y));
							startPos.set(regionPos.second.x + PADDING, regionPos.second.y);
							return true;
						}
					}
				} else // 행의 마지막 열이 아니면서 처음 또는 중간 열일 때
					if(insert(startPos, width, i, j)) return true;
			}
		}
		
		// 이미 존재하는 모든 행에 충분한 공간이 없을 때
		if(regionPosList.get(n-1).get(0).second.y + (sFontSrcHeight + PADDING)*2 <= mHeight) {
			float space = mWidth;
			if(width <= space) {
				regionPosList.add(createNewRow());
				regionPosList.get(n).add(createNewColumn(width, regionPosList.get(n-1).get(0).second.y + PADDING + sFontSrcHeight));
				startPos.set(0f, regionPosList.get(n).get(0).second.y);
				return true;
			}
		}
		
		// 더 이상 추가할 공간이 없을 때
		return false;
	}
	
	private ArrayList<Couple<Boolean,Vector2>> createNewRow() {
		return new ArrayList<Couple<Boolean,Vector2>>();
	}
	
	private Couple<Boolean,Vector2> createNewColumn(float x, float y) {
		return Couple.create(true, new Vector2(x, y));
	}
	
	private boolean insert(Vector2 startPos, int width, int i, int j) {
		List<Couple<Boolean, Vector2>> rowRegionPosList = mRegionPosList.get(i);
		boolean first = true;
		float space = rowRegionPosList.get(j).second.x;
		if(j != 0) {
			space -= rowRegionPosList.get(j-1).second.x + PADDING; 
			first = false;
		}
		
		if(width <= space) {
			if(width < space) {
				if(first) {
					rowRegionPosList.add(0, createNewColumn(width, rowRegionPosList.get(j).second.y));
				} else {
					rowRegionPosList.add(j, createNewColumn(
							rowRegionPosList.get(j-1).second.x + PADDING + width, 
							rowRegionPosList.get(j-1).second.y));
				}
			} else
				rowRegionPosList.get(j).first = true;
			
			if(first) {
				startPos.set(0f, rowRegionPosList.get(j).second.y);
			} else
				startPos.set(rowRegionPosList.get(j-1).second.x + PADDING, rowRegionPosList.get(j-1).second.y);
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void removeTextureRegion(TextureRegion region) {
		int x = region.getRegionX1();
		int y = region.getRegionY2() - sFontSrcHeight;
		int width = region.getRegionWidth();
		removeRegionImage(x, y, width, sFontSrcHeight);
		// 우상단의 좌표를 이용하여 Pos를 제거한다.
		removeRegionPos(x + width, y);
	}

	private void removeRegionPos(int x, int y) {
		List<List<Couple<Boolean, Vector2>>> regionPosList = mRegionPosList;
		// 행의 개수
		int n = regionPosList.size();
		for(int i=0; i<n; i++) {
			List<Couple<Boolean, Vector2>> rowRegionPosList = regionPosList.get(i);
			
			if(rowRegionPosList.get(0).second.y != y)
				continue;
			
			// 열의 개수
			int m = rowRegionPosList.size();
			for(int j=0; j<m; j++) {
				Couple<Boolean, Vector2> regionPos = rowRegionPosList.get(j);
				
				if(regionPos.second.x != x)
					continue;
				
				// 행의 열이 하나일 때
				// 행을 지우는 대신 열의 가로 길이를 텍스쳐의 너비로 설정한다.
				if(m == 1) regionPos.second.x = mWidth;
				// 해당 열을 사용하지 않음으로 표시
				regionPos.first = false;
				
				// 해당 행에 대한 정리작업 실시
				cleanPosRow(rowRegionPosList);
				return;
			}
		}
	}

	private void cleanPosRow(List<Couple<Boolean, Vector2>> rowRegionPosList) {
		int n;
		
		n = rowRegionPosList.size();
		if(n == 1) return;
		
		// 행의 마지막 열부터 사용되지 않는 경우 지우기 시작
		int k = 1;
		while(!rowRegionPosList.get(n-k).first) {
			rowRegionPosList.remove(n-k);
			k++;
			// 열이 하나가 남으면
			if(k == n) {
				if(!rowRegionPosList.get(0).first)
					rowRegionPosList.get(0).second.x = mWidth;
				return;
			}
		}
		
		n = rowRegionPosList.size();
		if(n == 1) return;
		
		for(int j=n-1; j>0; j--) {
			boolean first = rowRegionPosList.get(j).first;
			boolean second = rowRegionPosList.get(j-1).first;
			
			// 두 개의 열이 연속으로 사용되지 않는 경우 앞의 열을 제거한다.
			if(!first && !second)
				rowRegionPosList.remove(j-1);
		}
	}
	
	@Override
	public void clear() {
		super.clear();
		mRegionPosList.clear();
	}

}
