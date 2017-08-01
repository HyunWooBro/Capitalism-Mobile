package core.framework.graphics.utils.mesh;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import core.framework.graphics.utils.ShaderProgram;
import core.framework.graphics.utils.VertexAttribute;
import core.framework.graphics.utils.VertexAttributes;

public abstract class VertexData {
	
	protected FloatBuffer mVertexBuffer;
	
	protected VertexAttributes mAttributes;
	
	/*package*/ VertexData(int maxVertices, VertexAttribute... attributes) {
		mAttributes = new VertexAttributes(attributes);
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(maxVertices * mAttributes.getNumSingleVertexComponents() * 4/*bytes of float*/);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
	}
	
	public void setVertices(float[] src) {
		mVertexBuffer.position(0);
		mVertexBuffer.limit(src.length);
		mVertexBuffer.put(src);
	}
	
	public void setVertices(float[] src, int srcOffset, int count) {
		mVertexBuffer.position(0);
		mVertexBuffer.limit(count);
		mVertexBuffer.put(src, srcOffset, count);
	}
	
	public void updateVertices(float[] src, int srcOffset, int count, int destOffset) {
		mVertexBuffer.position(destOffset);
		mVertexBuffer.limit(destOffset + count);
		mVertexBuffer.put(src, srcOffset, count);
	}
	
	public void setVertexAttribPointer(ShaderProgram program) {
		for(int i=0; i<mAttributes.size(); i++) {
			VertexAttribute attribute = mAttributes.get(i);
			int location = program.getLocationByName(attribute.getName());
			if(location < 0) continue;
			GLES20.glEnableVertexAttribArray(location);
			GLES20.glVertexAttribPointer(
					location, 
					attribute.getNumComponents(), 
					GLES20.GL_FLOAT, 
		    		false, 
		    		mAttributes.getSingleVertexSize(), 
		    		mVertexBuffer.position(attribute.getOffset()));
		}
	}
	
	public void resetVertexAttribPointer(ShaderProgram program) {
		for(int i=0; i<mAttributes.size(); i++) {
			VertexAttribute attribute = mAttributes.get(i);
			int location = program.getLocationByName(attribute.getName());
			if(location < 0) continue;
			GLES20.glDisableVertexAttribArray(location);
		}
	}
	
	public int getNumVertices() {
		return mVertexBuffer.limit() / mAttributes.getNumSingleVertexComponents();
	}
	
	public int getMaxVertices() {
		return mVertexBuffer.capacity() / mAttributes.getNumSingleVertexComponents();
	}
	
	public Buffer getBuffer() {
		return mVertexBuffer;
	}

	public VertexAttributes getAttributes() {
		return mAttributes;
	}
}