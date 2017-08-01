package core.framework.graphics.texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.util.SparseArray;

import core.framework.graphics.GraphicsManager;

/**
 * 앱에서 사용하는 텍스쳐를 관리한다. {@link #addTexture(int, Texture)}을 통해 Texture을 
 * 등록한 후 {@link #getTexture(int)}을 통해 얻을 수 있다. 필요한 경우마다 Texture을 생성하는 것이 
 * 아닌, {@link GraphicsManager#getTextureManager()}을 통해 어디서든 TextureManager을 얻은 후 
 * Texture을 등록하거나 불러올 수 있기 때문에 Texture 관리가 효율적이다.
 * 
 * @author 김현우
 */
public class TextureManager {
	
	private static final List<String> TEMP_STRING_LIST = new ArrayList<String>();
	
	private SparseArray<Texture> mTextureMap = new SparseArray<Texture>();
	
	private Map<String, Integer> mStringToIDMap = new HashMap<String, Integer>();
	
	public TextureManager() {
	}
	
	public Texture addTexture(int id, Texture texture) {
		if(mTextureMap.get(id) != null)
			throw new IllegalStateException("texture with the same id arleay exist");

		mTextureMap.put(id, texture);
		return texture;
	}
	
	/** id와 관련된 Texture를 얻는다. */
	public Texture getTexture(int id) {
		return mTextureMap.get(id);
	}
	
	public Texture getTexture(String string) {
		return getTexture(getIDByString(string));
	}
	
	/** 
	 * id와 관련된 Texture을 제거한다. 관리하는 대상에서 제거할 뿐이지 
	 * Texture 자체를 제거하는 것은 아니다. 
	 */
	public void reomoveTexture(int id) {
		mTextureMap.delete(id);
	}
	
	public void reomoveTexture(String string) {
		reomoveTexture(getIDByString(string));
	}
	
	public int getIDByString(String string) {
		return mStringToIDMap.get(string);
	}
	
	public void mapStringToID(String string, int id) {
		if(mTextureMap.get(id) == null) 
			throw new IllegalStateException("No such id in StringToIDMap");
		
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
	
	public void disposeAll() {
		SparseArray<Texture> textureMap = mTextureMap;
		int n = textureMap.size();
		for(int i=0; i<n; i++) {
			Texture texture = textureMap.valueAt(i);
			texture.dispose();
		}
	}
	
	public void clear() {
		mTextureMap.clear();
		mStringToIDMap.clear();
	}

}
