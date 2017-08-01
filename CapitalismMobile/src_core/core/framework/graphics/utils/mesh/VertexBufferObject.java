package core.framework.graphics.utils.mesh;

import android.opengl.GLES20;

import core.framework.Core;
import core.framework.graphics.utils.GLResource;
import core.framework.graphics.utils.ShaderProgram;
import core.framework.graphics.utils.VertexAttribute;

public class VertexBufferObject extends VertexData implements GLResource {
	
	private static final int[] TMP_BUFFERS = new int[1];
	
	public static final VBOUpdate VBO_UPDATE = new VBOUpdate();
	
	private int mBufferID;
	
	private boolean mDynamic;
	
	private boolean mDirty;
	private boolean mBound;
	
	public VertexBufferObject(int maxVertices, boolean dynamic, VertexAttribute... attributes) {
		super(maxVertices, attributes);
		mDynamic = dynamic;
		createVertexBufferObject();
		Core.GRAPHICS.addGLReloadable(this);
	}
	
	private void createVertexBufferObject() {
		GLES20.glGenBuffers(1, TMP_BUFFERS, 0);
		mBufferID = TMP_BUFFERS[0];
		bind();
		load();
		unbind();
	}

	@Override
	public void bind() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferID);
		mBound = true;
		if(mDirty) {
			mVertexBuffer.limit(mVertexBuffer.capacity());
			update(VBO_UPDATE.set(0, getNumVertices(), 0));
			mDirty = false;
		}
	}
	
	@Override
	public void unbind() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		mBound = false;
	}
	
	@Override
	public void load() {
		GLES20.glBufferData(
				GLES20.GL_ARRAY_BUFFER, 
				mVertexBuffer.capacity() * 4/*bytes of float*/, 
				null, 
        		mDynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);
	}
	
	@Override
	public void reload() {
		if(GLES20.glIsBuffer(mBufferID)) return;
		createVertexBufferObject();
		mDirty = true;
	}
	
	@Override
	public void update(Object content) {
		if(!mBound) return;
		if(!(content instanceof VBOUpdate))
			throw new IllegalArgumentException("Only VBOUpdate instance is allowed");
		
		VBOUpdate update = (VBOUpdate) content;
		
		GLES20.glBufferSubData(
				GLES20.GL_ARRAY_BUFFER, 
				update.destOffset * 4/*bytes of float*/, 
				update.srcCount * 4/*bytes of float*/, 
				mVertexBuffer.position(update.srcOffset));
		
		mDirty = false;
	}
	
	@Override
	public void dispose() {
		unbind();
		TMP_BUFFERS[0] = mBufferID;
		GLES20.glDeleteFramebuffers(1, TMP_BUFFERS, 0);
		Core.GRAPHICS.removeGLReloadable(this);
	}
	
	@Override
	public void setVertices(float[] src) {
		super.setVertices(src);
		mDirty = true;
		update(VBO_UPDATE.set(0, src.length, 0));
	}
	
	@Override
	public void setVertices(float[] src, int srcOffset, int count) {
		super.setVertices(src, srcOffset, count);
		mDirty = true;
		update(VBO_UPDATE.set(srcOffset, count, 0));
	}
	
	@Override
	public void updateVertices(float[] src, int srcOffset, int count, int destOffset) {
		super.updateVertices(src, srcOffset, count, destOffset);
		mDirty = true;
		update(VBO_UPDATE.set(srcOffset, count, destOffset));
	}
	
	@Override
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
		    		attribute.getOffset() * 4/*bytes of float*/);
		}
	}

	public static class VBOUpdate {
		private int srcOffset;
		private int srcCount; 
		private int destOffset;
		
		public VBOUpdate set(int srcOffset, int srcCount, int destOffset) {
			this.srcOffset = srcOffset;
			this.srcCount = srcCount;
			this.destOffset = destOffset;
			return this;
		}
	}
	
}
