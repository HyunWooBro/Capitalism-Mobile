package core.framework.graphics.utils.mesh;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public abstract class IndexData {
	
	protected ShortBuffer mIndexBuffer;
	
	/*package*/ IndexData(int maxIndicese) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(maxIndicese * 2/*bytes of short*/);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
	}
	
	public void setIndices(short[] src) {
		mIndexBuffer.position(0);
		mIndexBuffer.limit(src.length);
		mIndexBuffer.put(src);
	}
	
	public void setIndices(short[] src, int srcOffset, int count) {
		mIndexBuffer.position(0);
		mIndexBuffer.limit(count);
		mIndexBuffer.put(src, srcOffset, count);
	}
	
	public int getNumIndices() {
		return mIndexBuffer.limit();
	}
	
	public int getMaxIndices() {
		return mIndexBuffer.capacity();
	}
	
	public Buffer getBuffer() {
		return mIndexBuffer;
	}
	
	public int getType() {
		return GLES20.GL_UNSIGNED_SHORT;
	}
}
