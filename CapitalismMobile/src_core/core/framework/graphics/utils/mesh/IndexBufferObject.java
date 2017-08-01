package core.framework.graphics.utils.mesh;

import android.opengl.GLES20;

import core.framework.Core;
import core.framework.graphics.utils.GLResource;

public class IndexBufferObject extends IndexData implements GLResource {
	
	private static final int[] TMP_BUFFERS = new int[1];
	
	public static final IBOUpdate IBO_UPDATE = new IBOUpdate();
	
	private int mBufferID;
	
	private boolean mDynamic;
	
	private boolean mDirty;
	private boolean mBound;
	
	public IndexBufferObject(int maxIndices, boolean dynamic) {
		super(maxIndices);
		mDynamic = dynamic;
		createIndexBufferObject();
		Core.GRAPHICS.addGLReloadable(this);
	}
	
	private void createIndexBufferObject() {
		GLES20.glGenBuffers(1, TMP_BUFFERS, 0);
		mBufferID = TMP_BUFFERS[0];
		bind();
		load();
		unbind();
	}
	
	@Override
	public void bind() {
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mBufferID);
		mBound = true;
		if(mDirty) {
			mIndexBuffer.limit(mIndexBuffer.capacity());
			update(IBO_UPDATE.set(0, getNumIndices(), 0));
			mDirty = false;
		}
	}
	
	@Override
	public void unbind() {
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		mBound = false;
	}
	
	@Override
	public void load() {
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 
				mIndexBuffer.capacity() * 2/*bytes of short*/, 
        		null, 
        		mDynamic ? GLES20.GL_DYNAMIC_DRAW : GLES20.GL_STATIC_DRAW);
	}
	
	@Override
	public void reload() {
		if(GLES20.glIsBuffer(mBufferID)) return;
		createIndexBufferObject();
		mDirty = true;
	}
	
	@Override
	public void update(Object content) {
		if(!mBound) return;
		if(!(content instanceof IBOUpdate))
			throw new IllegalArgumentException("Only IBOUpdate instance is allowed");
		
		IBOUpdate update = (IBOUpdate) content;
		
		GLES20.glBufferSubData(
				GLES20.GL_ELEMENT_ARRAY_BUFFER, 
				update.destOffset * 2/*bytes of short*/, 
				update.srcCount * 2/*bytes of short*/, 
				mIndexBuffer.position(update.srcOffset));
		
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
	public void setIndices(short[] src) {
		super.setIndices(src);
		mDirty = true;
		update(IBO_UPDATE.set(0, src.length, 0));
	}
	
	@Override
	public void setIndices(short[] src, int srcOffset, int count) {
		super.setIndices(src, srcOffset, count);
		mDirty = true;
		update(IBO_UPDATE.set(srcOffset, count, 0));
	}
	
	public static class IBOUpdate {
		private int srcOffset;
		private int srcCount; 
		private int destOffset;
		
		public IBOUpdate set(int srcOffset, int srcCount, int destOffset) {
			this.srcOffset = srcOffset;
			this.srcCount = srcCount;
			this.destOffset = destOffset;
			return this;
		}
	}

}
