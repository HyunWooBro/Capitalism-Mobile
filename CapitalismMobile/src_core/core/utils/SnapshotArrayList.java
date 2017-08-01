package core.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArraySet;

import core.framework.Core;
import core.utils.pool.Pool;
import core.utils.pool.Poolable;

/**
 * 리스트를 순회 중에 발생하는 어떠한 수정(삽입, 삭제)에 대해 사본을 이용하여 
 * 
 * 단순한 for루프 등에서 {@link #get(int)}을 통한 순회가 아닌, 
 * {@link #iterator()}, {@link #listIterator()} 등을 사용해야 이러한 효과를 거둘 수 있다.</p>
 * 
 * CopyOnWriteArrayList의 코드를 재사용했으며 성능 향상을 위해 동기화는 모두 제거했다.</p>
 * 
 * @author 김현우
 * @see java.util.concurrent.CopyOnWriteArrayList
 */
public class SnapshotArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable {

    private static final long serialVersionUID = 8673264195747942595L;
    
    // EmptyArray.OBJECT을 대체할 상수
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * Holds the latest snapshot of the list's data. This field is volatile so
     * that data can be read without synchronization. As a consequence, all
     * writes to this field must be atomic; it is an error to modify the
     * contents of an array after it has been assigned to this field.
     *
     * Synchronization is required by all update operations. This defends
     * against one update clobbering the result of another operation. For
     * example, 100 threads simultaneously calling add() will grow the list's
     * size by 100 when they have completed. No update operations are lost!
     *
     * Maintainers should be careful to read this field only once in
     * non-blocking read methods. Write methods must be synchronized to avoid
     * clobbering concurrent writes.
     */
    private transient volatile Object[] elements;
    
    private Object[] mLastSnapshot;
    
    private Pool<CowIterator<E>> mPool = new Pool<CowIterator<E>>() {

		@Override
		protected CowIterator<E> newObject() {
			return new CowIterator<E>();
		}
	};

    /**
     * Creates a new empty instance.
     */
    public SnapshotArrayList() {
    	//elements = EmptyArray.OBJECT;
        elements = EMPTY_OBJECT_ARRAY;
    }

    /**
     * Creates a new instance containing the elements of {@code collection}.
     */
    @SuppressWarnings("unchecked")
    public SnapshotArrayList(Collection<? extends E> collection) {
        this((E[]) collection.toArray());
    }

    /**
     * Creates a new instance containing the elements of {@code array}.
     */
    public SnapshotArrayList(E[] array) {
        this.elements = Arrays.copyOf(array, array.length, Object[].class);
    }

    @Override public Object clone() {
        try {
            SnapshotArrayList<?> result = (SnapshotArrayList<?>) super.clone();
            result.elements = result.elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
    
    public ListIterator<E> begin() {
    	return begin(0);
    }
    
    public ListIterator<E> begin(int index) {
    	if(mLastSnapshot == elements) 	elements = elements.clone();
    	
    	Object[] snapshot = elements;
    	mLastSnapshot = snapshot;
    	
    	CowIterator<E> it = mPool.obtain();
    	it.snapshot = snapshot;
    	it.from = 0;
    	it.to = snapshot.length;
    	it.index = index;
    	return it;
    }
    
    public void end(ListIterator<E> iterator) {
    	CowIterator<E> it = (CowIterator<E>) iterator;
    	mPool.recycle(it);
    	mLastSnapshot = null;
    }

    @Override
	public int size() {
        return elements.length;
    }

    @Override
	@SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) elements[index];
    }

    @Override
	public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
	public boolean containsAll(Collection<?> collection) {
        Object[] snapshot = elements;
        return containsAll(collection, snapshot, 0, snapshot.length);
    }

    static boolean containsAll(Collection<?> collection, Object[] snapshot, int from, int to) {
        for (Object o : collection) {
            if (indexOf(o, snapshot, from, to) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Searches this list for {@code object} and returns the index of the first
     * occurrence that is at or after {@code from}.
     *
     * @return the index or -1 if the object was not found.
     */
    public int indexOf(E object, int from) {
        Object[] snapshot = elements;
        return indexOf(object, snapshot, from, snapshot.length);
    }

    @Override
	public int indexOf(Object object) {
        Object[] snapshot = elements;
        return indexOf(object, snapshot, 0, snapshot.length);
    }

    /**
     * Searches this list for {@code object} and returns the index of the last
     * occurrence that is before {@code to}.
     *
     * @return the index or -1 if the object was not found.
     */
    public int lastIndexOf(E object, int to) {
        Object[] snapshot = elements;
        return lastIndexOf(object, snapshot, 0, to);
    }

    @Override
	public int lastIndexOf(Object object) {
        Object[] snapshot = elements;
        return lastIndexOf(object, snapshot, 0, snapshot.length);
    }

    @Override
	public boolean isEmpty() {
        return elements.length == 0;
    }

    /**
     * Returns an {@link Iterator} that iterates over the elements of this list
     * as they were at the time of this method call. Changes to the list made
     * after this method call will not be reflected by the iterator, nor will
     * they trigger a {@link ConcurrentModificationException}.
     *
     * <p>The returned iterator does not support {@link Iterator#remove()}.
     */
    @Override
	public Iterator<E> iterator() {
        Object[] snapshot = elements;
        return new CowIterator<E>(snapshot, 0, snapshot.length);
    }

    /**
     * Returns a {@link ListIterator} that iterates over the elements of this
     * list as they were at the time of this method call. Changes to the list
     * made after this method call will not be reflected by the iterator, nor
     * will they trigger a {@link ConcurrentModificationException}.
     *
     * <p>The returned iterator does not support {@link ListIterator#add},
     * {@link ListIterator#set} or {@link Iterator#remove()},
     */
    @Override
	public ListIterator<E> listIterator(int index) {
        Object[] snapshot = elements;
        if (index < 0 || index > snapshot.length) {
            throw new IndexOutOfBoundsException("index=" + index + ", length=" + snapshot.length);
        }
        CowIterator<E> result = new CowIterator<E>(snapshot, 0, snapshot.length);
        result.index = index;
        return result;
    }

    /**
     * Equivalent to {@code listIterator(0)}.
     */
    @Override
	public ListIterator<E> listIterator() {
        Object[] snapshot = elements;
        return new CowIterator<E>(snapshot, 0, snapshot.length);
    }

    @Override
	public List<E> subList(int from, int to) {
        Object[] snapshot = elements;
        if (from < 0 || from > to || to > snapshot.length) {
            throw new IndexOutOfBoundsException("from=" + from + ", to=" + to +
                    ", list size=" + snapshot.length);
        }
        return new CowSubList(snapshot, from, to);
    }

    @Override
	public Object[] toArray() {
        return elements.clone();
    }

    @Override
	@SuppressWarnings({"unchecked"})
    public <T> T[] toArray(T[] contents) {
        Object[] snapshot = elements;
        if (snapshot.length > contents.length) {
            return (T[]) Arrays.copyOf(snapshot, snapshot.length, contents.getClass());
        }
        System.arraycopy(snapshot, 0, contents, 0, snapshot.length);
        if (snapshot.length < contents.length) {
            contents[snapshot.length] = null;
        }
        return contents;
    }

    @Override public boolean equals(Object other) {
        if (other instanceof SnapshotArrayList) {
            return this == other
                    || Arrays.equals(elements, ((SnapshotArrayList<?>) other).elements);
        } else if (other instanceof List) {
            Object[] snapshot = elements;
            Iterator<?> i = ((List<?>) other).iterator();
            for (Object o : snapshot) {
                //if (!i.hasNext() || !Objects.equal(o, i.next())) {
            	if(!i.hasNext() || (o != null && !o.equals(i.next()))) {
                    return false;
                }
            }
            return !i.hasNext();
        } else {
            return false;
        }
    	//return false;
    }

    @Override public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override public String toString() {
        return Arrays.toString(elements);
    }

    @Override
	public boolean add(E e) {
        Object[] newElements = new Object[elements.length + 1];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        newElements[elements.length] = e;
        elements = newElements;
        return true;
    }

    @Override
	public void add(int index, E e) {
        Object[] newElements = new Object[elements.length + 1];
        System.arraycopy(elements, 0, newElements, 0, index);
        newElements[index] = e;
        System.arraycopy(elements, index, newElements, index + 1, elements.length - index);
        elements = newElements;
    }

    @Override
	public boolean addAll(Collection<? extends E> collection) {
        return addAll(elements.length, collection);
    }

    @Override
	public boolean addAll(int index, Collection<? extends E> collection) {
        Object[] toAdd = collection.toArray();
        Object[] newElements = new Object[elements.length + toAdd.length];
        System.arraycopy(elements, 0, newElements, 0, index);
        System.arraycopy(toAdd, 0, newElements, index, toAdd.length);
        System.arraycopy(elements, index,
                newElements, index + toAdd.length, elements.length - index);
        elements = newElements;
        return toAdd.length > 0;
    }

    /**
     * Adds the elements of {@code collection} that are not already present in
     * this list. If {@code collection} includes a repeated value, at most one
     * occurrence of that value will be added to this list. Elements are added
     * at the end of this list.
     *
     * <p>Callers of this method may prefer {@link CopyOnWriteArraySet}, whose
     * API is more appropriate for set operations.
     */
    public int addAllAbsent(Collection<? extends E> collection) {
        Object[] toAdd = collection.toArray();
        Object[] newElements = new Object[elements.length + toAdd.length];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        int addedCount = 0;
        for (Object o : toAdd) {
            if (indexOf(o, newElements, 0, elements.length + addedCount) == -1) {
                newElements[elements.length + addedCount++] = o;
            }
        }
        if (addedCount < toAdd.length) {
            newElements = Arrays.copyOfRange(
                    newElements, 0, elements.length + addedCount); // trim to size
        }
        elements = newElements;
        return addedCount;
    }

    /**
     * Adds {@code object} to the end of this list if it is not already present.
     *
     * <p>Callers of this method may prefer {@link CopyOnWriteArraySet}, whose
     * API is more appropriate for set operations.
     */
    public boolean addIfAbsent(E object) {
        if (contains(object)) {
            return false;
        }
        add(object);
        return true;
    }

    @Override public void clear() {
    	//elements = EmptyArray.OBJECT;
        elements = EMPTY_OBJECT_ARRAY;
    }

    @Override
	public E remove(int index) {
        @SuppressWarnings("unchecked")
        E removed = (E) elements[index];
        removeRange(index, index + 1);
        return removed;
    }

    @Override
	public boolean remove(Object o) {
        int index = indexOf(o);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
	public boolean removeAll(Collection<?> collection) {
        return removeOrRetain(collection, false, 0, elements.length) != 0;
    }

    @Override
	public boolean retainAll(Collection<?> collection) {
        return removeOrRetain(collection, true, 0, elements.length) != 0;
    }

    /**
     * Removes or retains the elements in {@code collection}. Returns the number
     * of elements removed.
     */
    private int removeOrRetain(Collection<?> collection, boolean retain, int from, int to) {
        for (int i = from; i < to; i++) {
            if (collection.contains(elements[i]) == retain) {
                continue;
            }

            /*
             * We've encountered an element that must be removed! Create a new
             * array and copy in the surviving elements one by one.
             */
            Object[] newElements = new Object[elements.length - 1];
            System.arraycopy(elements, 0, newElements, 0, i);
            int newSize = i;
            for (int j = i + 1; j < to; j++) {
                if (collection.contains(elements[j]) == retain) {
                    newElements[newSize++] = elements[j];
                }
            }

            /*
             * Copy the elements after 'to'. This is only useful for sub lists,
             * where 'to' will be less than elements.length.
             */
            System.arraycopy(elements, to, newElements, newSize, elements.length - to);
            newSize += (elements.length - to);

            if (newSize < newElements.length) {
                newElements = Arrays.copyOfRange(newElements, 0, newSize); // trim to size
            }
            int removed = elements.length - newElements.length;
            elements = newElements;
            return removed;
        }

        // we made it all the way through the loop without making any changes
        return 0;
    }

    @Override
	public E set(int index, E e) {
        Object[] newElements = elements.clone();
        @SuppressWarnings("unchecked")
        E result = (E) newElements[index];
        newElements[index] = e;
        elements = newElements;
        return result;
    }

    private void removeRange(int from, int to) {
        Object[] newElements = new Object[elements.length - (to - from)];
        System.arraycopy(elements, 0, newElements, 0, from);
        System.arraycopy(elements, to, newElements, from, elements.length - to);
        elements = newElements;
    }

    static int lastIndexOf(Object o, Object[] data, int from, int to) {
        if (o == null) {
            for (int i = to - 1; i >= from; i--) {
                if (data[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = to - 1; i >= from; i--) {
                if (o.equals(data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    static int indexOf(Object o, Object[] data, int from, int to) {
        if (o == null) {
            for (int i = from; i < to; i++) {
                if (data[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = from; i < to; i++) {
                if (o.equals(data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    final Object[] getArray() {
        // CopyOnWriteArraySet needs this.
        return elements;
    }

    /**
     * The sub list is thread safe and supports non-blocking reads. Doing so is
     * more difficult than in the full list, because each read needs to examine
     * four fields worth of state:
     *  - the elements array of the full list
     *  - two integers for the bounds of this sub list
     *  - the expected elements array (to detect concurrent modification)
     *
     * This is accomplished by aggregating the sub list's three fields into a
     * single snapshot object representing the current slice. This permits reads
     * to be internally consistent without synchronization. This takes advantage
     * of Java's concurrency semantics for final fields.
     */
    class CowSubList extends AbstractList<E> {

        /*
         * An immutable snapshot of a sub list's state. By gathering all three
         * of the sub list's fields in an immutable object,
         */
        private volatile Slice slice;

        public CowSubList(Object[] expectedElements, int from, int to) {
            this.slice = new Slice(expectedElements, from, to);
        }

        @Override public int size() {
            Slice slice = this.slice;
            return slice.to - slice.from;
        }

        @Override public boolean isEmpty() {
            Slice slice = this.slice;
            return slice.from == slice.to;
        }

        @SuppressWarnings("unchecked")
        @Override public E get(int index) {
            Slice slice = this.slice;
            Object[] snapshot = elements;
            slice.checkElementIndex(index);
            slice.checkConcurrentModification(snapshot);
            return (E) snapshot[index + slice.from];
        }

        @Override public Iterator<E> iterator() {
            return listIterator(0);
        }

        @Override public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override public ListIterator<E> listIterator(int index) {
            Slice slice = this.slice;
            Object[] snapshot = elements;
            slice.checkPositionIndex(index);
            slice.checkConcurrentModification(snapshot);
            CowIterator<E> result = new CowIterator<E>(snapshot, slice.from, slice.to);
            result.index = slice.from + index;
            return result;
        }

        @Override public int indexOf(Object object) {
            Slice slice = this.slice;
            Object[] snapshot = elements;
            slice.checkConcurrentModification(snapshot);
            int result = SnapshotArrayList.indexOf(object, snapshot, slice.from, slice.to);
            return (result != -1) ? (result - slice.from) : -1;
        }

        @Override public int lastIndexOf(Object object) {
            Slice slice = this.slice;
            Object[] snapshot = elements;
            slice.checkConcurrentModification(snapshot);
            int result = SnapshotArrayList.lastIndexOf(object, snapshot, slice.from, slice.to);
            return (result != -1) ? (result - slice.from) : -1;
        }

        @Override public boolean contains(Object object) {
            return indexOf(object) != -1;
        }

        @Override public boolean containsAll(Collection<?> collection) {
            Slice slice = this.slice;
            Object[] snapshot = elements;
            slice.checkConcurrentModification(snapshot);
            return SnapshotArrayList.containsAll(collection, snapshot, slice.from, slice.to);
        }

        @Override public List<E> subList(int from, int to) {
            Slice slice = this.slice;
            if (from < 0 || from > to || to > size()) {
                throw new IndexOutOfBoundsException("from=" + from + ", to=" + to +
                        ", list size=" + size());
            }
            return new CowSubList(slice.expectedElements, slice.from + from, slice.from + to);
        }

        @Override public E remove(int index) {
            synchronized (SnapshotArrayList.this) {
                slice.checkElementIndex(index);
                slice.checkConcurrentModification(elements);
                E removed = SnapshotArrayList.this.remove(slice.from + index);
                slice = new Slice(elements, slice.from, slice.to - 1);
                return removed;
            }
        }

        @Override public void clear() {
            synchronized (SnapshotArrayList.this) {
                slice.checkConcurrentModification(elements);
                SnapshotArrayList.this.removeRange(slice.from, slice.to);
                slice = new Slice(elements, slice.from, slice.from);
            }
        }

        @Override public void add(int index, E object) {
            synchronized (SnapshotArrayList.this) {
                slice.checkPositionIndex(index);
                slice.checkConcurrentModification(elements);
                SnapshotArrayList.this.add(index + slice.from, object);
                slice = new Slice(elements, slice.from, slice.to + 1);
            }
        }

        @Override public boolean add(E object) {
            synchronized (SnapshotArrayList.this) {
                add(slice.to - slice.from, object);
                return true;
            }
        }

        @Override public boolean addAll(int index, Collection<? extends E> collection) {
            synchronized (SnapshotArrayList.this) {
                slice.checkPositionIndex(index);
                slice.checkConcurrentModification(elements);
                int oldSize = elements.length;
                boolean result = SnapshotArrayList.this.addAll(index + slice.from, collection);
                slice = new Slice(elements, slice.from, slice.to + (elements.length - oldSize));
                return result;
            }
        }

        @Override public boolean addAll(Collection<? extends E> collection) {
            synchronized (SnapshotArrayList.this) {
                return addAll(size(), collection);
            }
        }

        @Override public E set(int index, E object) {
            synchronized (SnapshotArrayList.this) {
                slice.checkElementIndex(index);
                slice.checkConcurrentModification(elements);
                E result = SnapshotArrayList.this.set(index + slice.from, object);
                slice = new Slice(elements, slice.from, slice.to);
                return result;
            }
        }

        @Override public boolean remove(Object object) {
            synchronized (SnapshotArrayList.this) {
                int index = indexOf(object);
                if (index == -1) {
                    return false;
                }
                remove(index);
                return true;
            }
        }

        @Override public boolean removeAll(Collection<?> collection) {
            synchronized (SnapshotArrayList.this) {
                slice.checkConcurrentModification(elements);
                int removed = removeOrRetain(collection, false, slice.from, slice.to);
                slice = new Slice(elements, slice.from, slice.to - removed);
                return removed != 0;
            }
        }

        @Override public boolean retainAll(Collection<?> collection) {
            synchronized (SnapshotArrayList.this) {
                slice.checkConcurrentModification(elements);
                int removed = removeOrRetain(collection, true, slice.from, slice.to);
                slice = new Slice(elements, slice.from, slice.to - removed);
                return removed != 0;
            }
        }
    }

    static class Slice {
        private final Object[] expectedElements;
        private final int from;
        private final int to;

        Slice(Object[] expectedElements, int from, int to) {
            this.expectedElements = expectedElements;
            this.from = from;
            this.to = to;
        }

        /**
         * Throws if {@code index} doesn't identify an element in the array.
         */
        void checkElementIndex(int index) {
            if (index < 0 || index >= to - from) {
                throw new IndexOutOfBoundsException("index=" + index + ", size=" + (to - from));
            }
        }

        /**
         * Throws if {@code index} doesn't identify an insertion point in the
         * array. Unlike element index, it's okay to add or iterate at size().
         */
        void checkPositionIndex(int index) {
            if (index < 0 || index > to - from) {
                throw new IndexOutOfBoundsException("index=" + index + ", size=" + (to - from));
            }
        }

        void checkConcurrentModification(Object[] snapshot) {
            if (expectedElements != snapshot) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Iterates an immutable snapshot of the list.
     */
    static class CowIterator<E> implements ListIterator<E>, Poolable {
        private Object[] snapshot;
        private int from;
        private int to;
        private int index = 0;
        
        CowIterator() {
        }

        CowIterator(Object[] snapshot, int from, int to) {
            this.snapshot = snapshot;
            this.from = from;
            this.to = to;
            this.index = from;
        }

        @Override
		public void add(E object) {
            throw new UnsupportedOperationException();
        }

        @Override
		public boolean hasNext() {
            return index < to;
        }

        @Override
		public boolean hasPrevious() {
            return index > from;
        }

        @Override
		@SuppressWarnings("unchecked")
        public E next() {
            if (index < to) {
                return (E) snapshot[index++];
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
		public int nextIndex() {
            return index;
        }

        @Override
		@SuppressWarnings("unchecked")
        public E previous() {
            if (index > from) {
                return (E) snapshot[--index];
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
		public int previousIndex() {
            return index - 1;
        }

        @Override
		public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
		public void set(E object) {
            throw new UnsupportedOperationException();
        }

		@Override
		public void recycle() {
		}
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Object[] snapshot = elements;
        out.defaultWriteObject();
        out.writeInt(snapshot.length);
        for (Object o : snapshot) {
            out.writeObject(o);
        }
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Object[] snapshot = new Object[in.readInt()];
        for (int i = 0; i < snapshot.length; i++) {
            snapshot[i] = in.readObject();
        }
        elements = snapshot;
    }
}
