package core.utils.pool;

import java.util.ArrayList;
import java.util.List;

public abstract class Pool<T> {
	
	private List<T> mPool;
	
	private int mMax;
	
	private int mCreationCount;
	
	private int mMaxSize;

	public Pool() {
		this(10, Integer.MAX_VALUE);
	}
	
	public Pool(int capacity, int max) {
		mPool = new ArrayList<T>(capacity);
		mMax = max;
	}
	
	public T obtain() {
		if(mPool.isEmpty()) {
			mCreationCount++;
			return newObject();
		} else
			return mPool.remove(mPool.size()-1);
	}
	
	/** Pool이 비어있는 경우 새로운 인스턴스를 생성하여 반환한다. */
	protected abstract T newObject();

	public boolean recycle(T element) {
		if(element == null) throw new IllegalArgumentException("element can't be null.");
		if(mPool.size() < mMax) {
			mPool.add(element);
			mMaxSize = Math.max(mMaxSize, mPool.size());
		}
		if(element instanceof Poolable) ((Poolable) element).recycle();
		return false;
	}
	
	public int size() {
		return mPool.size();
	}

	public int getCreationCount() {
		return mCreationCount;
	}

	public int getMaxSize() {
		return mMaxSize;
	}
}
