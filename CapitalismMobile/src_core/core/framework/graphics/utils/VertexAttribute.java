package core.framework.graphics.utils;

public class VertexAttribute {
	
	private String mName;
	int mNumComponents;
	int mOffset;
	
	public VertexAttribute(String name, int numComponents) {
		mName = name;
		mNumComponents = numComponents;
	}

	public String getName() {
		return mName;
	}

	public int getNumComponents() {
		return mNumComponents;
	}

	public int getOffset() {
		return mOffset;
	}
}