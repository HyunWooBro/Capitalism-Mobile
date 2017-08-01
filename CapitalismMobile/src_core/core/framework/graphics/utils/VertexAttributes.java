package core.framework.graphics.utils;

public class VertexAttributes {

	private VertexAttribute[] mAttributes;
	
	private int mSingleVertexSize;
	private int mNumSingleVertexComponents;
	
	public VertexAttributes(VertexAttribute... attributes) {
		mAttributes = new VertexAttribute[attributes.length];
		for(int i=0; i<attributes.length; i++)
			mAttributes[i] = attributes[i];
		mNumSingleVertexComponents = calculateOffsets();
		mSingleVertexSize = mNumSingleVertexComponents * 4/*bytes of float*/;
	}
	
	private int calculateOffsets() {
		int offset = 0;
		for(int i=0; i<mAttributes.length; i++) {
			mAttributes[i].mOffset = offset;
			offset += mAttributes[i].mNumComponents;
		}
		return offset;
	}
	
	public VertexAttribute get(int index) {
		return mAttributes[index];
	}
	
	public int size() {
		return mAttributes.length;
	}
	
	public int getNumSingleVertexComponents() {
		return mNumSingleVertexComponents;
	}
	
	public int getSingleVertexSize() {
		return mSingleVertexSize;
	}
}
