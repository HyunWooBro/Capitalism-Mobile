package core.framework.graphics.utils.mesh;

import android.opengl.GLES20;

import core.framework.graphics.utils.ShaderProgram;
import core.framework.graphics.utils.VertexAttribute;
import core.framework.graphics.utils.VertexAttributes;
import core.utils.Disposable;

public class Mesh implements Disposable {
	
	public enum VertexDataType {
		VERTEX_ARRAY,
		VERTEX_BUFFER_OBJECT, 
	}
	
	private VertexDataType mType;
	
	private VertexData mVertexData;
	private IndexData mIndexData;
	
	public Mesh(boolean dynamic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
		this(VertexDataType.VERTEX_BUFFER_OBJECT, dynamic, dynamic, maxVertices, maxIndices, attributes);
	}
	
	public Mesh(boolean vertexDynamic, boolean indexDynamic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
		this(VertexDataType.VERTEX_BUFFER_OBJECT, vertexDynamic, indexDynamic, maxVertices, maxIndices, attributes);
	}
	
	public Mesh(VertexDataType type, boolean dynamic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
		this(type, dynamic, dynamic, maxVertices, maxIndices, attributes);
	}
	
	public Mesh(VertexDataType type, boolean vertexDynamic, boolean indexDynamic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
		mType = type;
		switch(type) {
			case VERTEX_ARRAY:
				mVertexData = new VertexArray(maxVertices, attributes);
				mIndexData = new IndexArray(maxIndices);
				break;
			case VERTEX_BUFFER_OBJECT:
				mVertexData = new VertexBufferObject(maxVertices, vertexDynamic, attributes);
				mIndexData = new IndexBufferObject(maxIndices, indexDynamic);
				break;
		}
	}
	
	public void setVertices(float[] src) {
		mVertexData.setVertices(src);
	}
	
	public void setVertices(float[] src, int srcOffset, int count) {
		mVertexData.setVertices(src, srcOffset, count);
	}
	
	public void updateVertices(float[] src, int srcOffset, int count, int destOffset) {
		mVertexData.updateVertices(src, srcOffset, count, destOffset);
	}
	
	public void setIndices(short[] src) {
		mIndexData.setIndices(src);
	}
	
	public void setIndices(short[] src, int srcOffset, int count) {
		mIndexData.setIndices(src, srcOffset, count);
	}
	
	public void begin(ShaderProgram program) {
		if(mType == VertexDataType.VERTEX_BUFFER_OBJECT) {
			VertexBufferObject vbo = (VertexBufferObject) mVertexData;
			IndexBufferObject ibo = (IndexBufferObject) mIndexData;
			vbo.bind();
			ibo.bind();
		}
		mVertexData.setVertexAttribPointer(program);
	}

	public void end(ShaderProgram program) {
		if(mType == VertexDataType.VERTEX_BUFFER_OBJECT) {
			VertexBufferObject vbo = (VertexBufferObject) mVertexData;
			IndexBufferObject ibo = (IndexBufferObject) mIndexData;
			vbo.unbind();
			ibo.unbind();
		}
		mVertexData.resetVertexAttribPointer(program);
	}
	
	public void render(int mode) {
		render(
				mode, 
				(getMaxIndices() > 0)? getNumIndices() : getNumVertices(), 
				mIndexData.getType(), 
				0);
	}
	
	public void render(int mode, int count) {
		render(mode, count, mIndexData.getType(), 0);
	}
	
	/**
	 * 
	 * @param mode
	 * @param count glDrawElements의 경우는 index의 개수, glDrawArrays의 경우는 vertex의 개수
	 * @param clazz
	 * @param offset
	 */
	public void render(int mode, int count, int type, int offset) {
		if(count == 0) return;
		
		switch(mType) {
			case VERTEX_ARRAY:
				if(getMaxIndices() > 0)
					GLES20.glDrawElements(mode, count, type, mIndexData.getBuffer().position(offset));
				else
					GLES20.glDrawArrays(mode, offset, count);
				break;
			case VERTEX_BUFFER_OBJECT:
				if(getMaxIndices() > 0)
					GLES20.glDrawElements(mode, count,	type, offset * 2/*bytes of short*/);
				else
					GLES20.glDrawArrays(mode, offset, count);
				break;
		}
	}

	public VertexAttributes getAttributes() {
		return mVertexData.getAttributes();
	}

	public int getNumVertices() {
		return mVertexData.getNumVertices();
	}

	public int getMaxVertices() {
		return mVertexData.getMaxVertices();
	}

	public int getNumIndices() {
		return mIndexData.getNumIndices();
	}

	public int getMaxIndices() {
		return mIndexData.getMaxIndices();
	}

	@Override
	public void dispose() {
		if(mType == VertexDataType.VERTEX_BUFFER_OBJECT) {
			VertexBufferObject vbo = (VertexBufferObject) mVertexData;
			vbo.dispose();
			IndexBufferObject ibo = (IndexBufferObject) mIndexData;
			ibo.dispose();
		}
	}
}
