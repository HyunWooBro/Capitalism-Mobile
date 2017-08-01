package core.framework.graphics.utils;

import android.opengl.GLES20;

import core.framework.Core;
import core.framework.graphics.texture.ImageTexture;
import core.framework.graphics.texture.Texture;

public class FrameBufferObject implements GLResource {
	
	private static final int[] TMP_BUFFERS = new int[1];
	
	/** OpenGL을 통해 생성한 프레임버퍼 아이디 */
	private int mFrameBufferID;
	
	/** 프레임버퍼와 연결된 Texture */
	private Texture mTexture;
	
	/** 프레임버퍼의 너비 */
	private int mWidth;
	/** 프레임버퍼의 높이 */
	private int mHeight;

	public FrameBufferObject(int width, int height) {
		mWidth = width;
		mHeight = height;
		mTexture = new ImageTexture(mWidth, mHeight);
		createFrameBufferObject();
		Core.GRAPHICS.addGLReloadable(this);
	}
	
	private void createFrameBufferObject() {
		GLES20.glGenFramebuffers(1, TMP_BUFFERS, 0);
		mFrameBufferID = TMP_BUFFERS[0];

		bind();
		load();		
		int result = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		unbind();
		
		if(result != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			dispose();
			switch(result) {
				case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
					throw new IllegalStateException("frame buffer couldn't be constructed: incomplete attachment");
				case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
					throw new IllegalStateException("frame buffer couldn't be constructed: incomplete dimensions");
				case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
					throw new IllegalStateException("frame buffer couldn't be constructed: missing attachment");
				case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
					throw new IllegalStateException("frame buffer couldn't be constructed: unsupported combination of formats");
			}
		}
	}

	@Override
	public void bind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferID);
	}
	
	@Override
	public void unbind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
	@Override
	public void load() {
		GLES20.glFramebufferTexture2D(
				GLES20.GL_FRAMEBUFFER, 
				GLES20.GL_COLOR_ATTACHMENT0, 
				GLES20.GL_TEXTURE_2D,
				mTexture.getTextureID(), 
				0);
	}

	@Override
	public void reload() {
		if(GLES20.glIsFramebuffer(mFrameBufferID)) return;
		createFrameBufferObject();
	}
	
	@Override
	public void update(Object content) {
	}

	@Override
	public void dispose() {
		mTexture.dispose();
		unbind();
		TMP_BUFFERS[0] = mFrameBufferID;
		GLES20.glDeleteFramebuffers(1, TMP_BUFFERS, 0);
		Core.GRAPHICS.removeGLReloadable(this);
	}

	public void begin() {
		bind();
		GLES20.glViewport(0, 0, mWidth, mHeight);
	}
	
	public void end() {
		unbind();
		GLES20.glViewport(0, 0, Core.GRAPHICS.getWidth(), Core.GRAPHICS.getHeight());
	}
	
	public Texture getTexture() {
		return mTexture;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

}
