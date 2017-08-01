package core.scene.stage.actor.widget.utils;

import java.util.ArrayList;
import java.util.List;

import core.math.MathUtils;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.widget.table.button.Button;

/**
 * {@link Selector}는 다수의 항목에 대해 일부 항목을 선택하거나 선택을 취소할 수 있다.</p>
 * 
 * 최소 또는 최대 선택할 수 있는 개수를 지정할 수 있다.</p>
 * 
 * {@link #select(Actor)}를 제외한 {@link #add(Actor)}, {@link #remove(Actor)} 등의 메서드는 
 * 최소, 최대 개수 등의 제약을 받지 않고 무조건 선택하거나 선택을 취소한다.</p>
 * 
 * @author 김현우
 */
public class Selector<T extends Actor<?>> {
	
	public static final int MAX_SELECTION_UNLIMITED = -1;
	
	private List<Snapshot<T>> mSnapshotList = new ArrayList<Snapshot<T>>(3);
	private int mSnapshotCount;
	
	private List<T> mSelectionList = new ArrayList<T>(1);
	private T mLastSelection;
	
	private boolean mRemoveLastSelection = true;
	
	private int mMinSelections = 1;
	private int mMaxSelections = 1;
	
	private Actor<?> mActor;
	
	public Selector() {
	}
	
	public Selector(Actor<?> actor) {
		mActor = actor;
	}
	
	private void snapshot() {
		mSnapshotCount++;
		if(mSnapshotList.size() < mSnapshotCount) 
			mSnapshotList.add(new Snapshot<T>());
		
		Snapshot<T> snapshot = mSnapshotList.get(mSnapshotCount-1);
		snapshot.selectiontList.clear();
		snapshot.selectiontList.addAll(mSelectionList);
		snapshot.lastSelection = mLastSelection;
	}

	private void restore() {
		Snapshot<T> snapshot = mSnapshotList.get(mSnapshotCount-1);
		mSelectionList.clear();
		mSelectionList.addAll(snapshot.selectiontList);
		mLastSelection = snapshot.lastSelection;
	}

	/** 제약 환경(최소, 최대 개수 등)을 고려하여 selection을 선택하거나 선택을 취소한다. */
	public boolean select(T selection) {
		List<T> selectionList = mSelectionList;
		int size = selectionList.size();
		
		// 이미 선택되어 있다면
		if(selectionList.contains(selection)) {
			
			if(mMinSelections >= size) return false;
			snapshot();
			selectionList.remove(selection);
			size--;
			if(selectionList.isEmpty())	mLastSelection = null;
			else									mLastSelection = selectionList.get(size-1);

		} else { // 이미 선택되어 있지 않다면
			
			if((0 < mMaxSelections && mMaxSelections < size) || mMaxSelections == 0) return false;
			snapshot();
			if(mMaxSelections == size) {
				if(mRemoveLastSelection) {
					selectionList.remove(mLastSelection);
				} else {
					mSnapshotCount--;
					return false;
				}
			}
			selectionList.add(selection);
			mLastSelection = selection;
		}
		
		if(mActor != null && !fireChangeEvent()) {
			onChangeEventCanceled();
			mSnapshotCount--;
			return false;
		}
		
		mSnapshotCount--;
		return true;
	}

	public boolean set(T selection) {
		if(selection == null) throw new IllegalArgumentException("selection can't be null.");
		snapshot();
		boolean changed = false;
		if(mSelectionList.contains(selection)) {
			if(mSelectionList.size() > 2) changed = true;
		} else
			changed = true;
		if(changed) {
			mSelectionList.clear();
			mSelectionList.add(selection);
			mLastSelection = selection;
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean setAll(List<T> selectionList) {
		if(selectionList.contains(null)) throw new IllegalArgumentException("selection can't be null.");
		snapshot();
		boolean changed = false;
		if(mSelectionList.containsAll(selectionList)) {
			if(mSelectionList.size() > selectionList.size()) changed = true;
		} else
			changed = true;
		if(changed) {
			mSelectionList.clear();
			mSelectionList.addAll(selectionList);
			mLastSelection = mSelectionList.get(mSelectionList.size()-1);
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean add(T selection) {
		if(selection == null) throw new IllegalArgumentException("selection can't be null.");
		if(mSelectionList.contains(selection)) return false;
		snapshot();
		mSelectionList.add(selection);
		mLastSelection = selection;
		if(mActor != null && !fireChangeEvent()) {
			onChangeEventCanceled();
			mSnapshotCount--;
			return false;
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean addAll(List<T> selectionList) {
		snapshot();
		boolean added = false;
		int n = selectionList.size();
		for(int i=0; i<n; i++) {
			T selection = selectionList.get(i);
			if(selection == null) throw new IllegalArgumentException("selection can't be null.");
			if(mSelectionList.contains(selection)) continue;
			mSelectionList.add(selection);
			added = true;
		}
		if(added) {
			mLastSelection = mSelectionList.get(mSelectionList.size()-1);
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean remove(T selection) {
		if(selection == null) throw new IllegalArgumentException("selection can't be null.");
		snapshot();
		if(mSelectionList.remove(selection)) {
			if(mSelectionList.isEmpty())	mLastSelection = null;
			else										mLastSelection = mSelectionList.get(mSelectionList.size()-1);
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean removeAll(List<T> selectionList) {
		if(selectionList.contains(null)) throw new IllegalArgumentException("selection can't be null.");
		snapshot();
		if(mSelectionList.removeAll(selectionList)) {
			if(mSelectionList.isEmpty())	mLastSelection = null;
			else										mLastSelection = mSelectionList.get(mSelectionList.size()-1);
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean clear() {
		snapshot();
		if(!mSelectionList.isEmpty()) {
			mSelectionList.clear();
			mLastSelection = null;
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}

	public boolean retain(T selection) {
		if(selection == null) throw new IllegalArgumentException("selection can't be null.");
		snapshot();
		boolean changed = false;
		if(mSelectionList.contains(selection)) {
			if(mSelectionList.size() > 2) {
				mSelectionList.clear();
				mSelectionList.add(selection);
				mLastSelection = selection;
				changed = true;
			}
		} else {
			if(mSelectionList.size() > 1) {
				mSelectionList.clear();
				mLastSelection = null;
				changed = true;
			}
		}
		if(changed) {
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean retainAll(List<T> selectionList) {
		if(selectionList.contains(null)) throw new IllegalArgumentException("selection can't be null.");
		snapshot();
		if(mSelectionList.retainAll(selectionList)) {
			mLastSelection = mSelectionList.get(mSelectionList.size()-1);
			if(mActor != null && !fireChangeEvent()) {
				onChangeEventCanceled();
				mSnapshotCount--;
				return false;
			}
		}
		mSnapshotCount--;
		return true;
	}
	
	public boolean reverseAll(List<T> selectionList) {
		snapshot();
		int n = selectionList.size();
		for(int i=0; i<n; i++) {
			T selection = selectionList.get(i);
			if(selection == null) throw new IllegalArgumentException("selection can't be null.");
			if(mSelectionList.contains(selection)) {
				mSelectionList.remove(selection);
			} else
				mSelectionList.add(selection);
		}
		if(mSelectionList.isEmpty())	mLastSelection = null;
		else										mLastSelection = mSelectionList.get(mSelectionList.size()-1);
		if(mActor != null && !fireChangeEvent()) {
			onChangeEventCanceled();
			mSnapshotCount--;
			return false;
		}
		mSnapshotCount--;
		return true;
	}

	private boolean fireChangeEvent() {
		if(mActor.fire(new ChangeEvent())) return true;
		return false;
	}

	protected void onChangeEventCanceled() {
		restore();
	}
	
	public boolean contains(T selection) {
		return mSelectionList.contains(selection);
	}
	
	public boolean containsAll(List<T> selectionList) {
		return mSelectionList.containsAll(selectionList);
	}

	public boolean hasSelections() {
		return !mSelectionList.isEmpty();
	}

	public T first(){
		return (mSelectionList.isEmpty())? null : mSelectionList.get(0);
	}

	public List<T> getSelectionList() {
		return mSelectionList;
	}
	
	public int getSelectionCount() {
		return mSelectionList.size();
	}

	public T getLastSelection() {
		return mLastSelection;
	}

	public boolean willRemoveLastSelection() {
		return mRemoveLastSelection;
	}

	public void setRemoveLastSelection(boolean removeLastSelection) {
		mRemoveLastSelection = removeLastSelection;
	}
	
	public int getMinSelections() {
		return mMinSelections;
	}

	public int getMaxSelections() {
		return mMaxSelections;
	}

	public void setMinSelections(int minSelections) {
		mMinSelections = MathUtils.clamp(minSelections, 0, Integer.MAX_VALUE);
	}

	public void setMaxSelections(int maxSelections) {
		if(maxSelections < 0)	mMaxSelections = MAX_SELECTION_UNLIMITED;
		else								mMaxSelections = maxSelections;
	}

	public Actor<?> getActor() {
		return mActor;
	}

	public void setActor(Actor<?> actor) {
		mActor = actor;
	}
	
	private static class Snapshot<T> {
		public List<T> selectiontList = new ArrayList<T>(1);
		public T lastSelection;
	}
	
}
