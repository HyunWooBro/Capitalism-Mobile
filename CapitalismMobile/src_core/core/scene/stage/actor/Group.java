package core.scene.stage.actor;

import java.util.List;
import java.util.ListIterator;

import core.framework.graphics.Color4;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.batch.Batch;
import core.math.Matrix3;
import core.math.Vector2;
import core.scene.stage.Floor;
import core.utils.Disposable;
import core.utils.SnapshotArrayList;

/**
 * Group은 Actor을 자식으로 추가할 수 있는 Actor이다.
 * 
 * @author 김현우
 */
@SuppressWarnings("unchecked")
public abstract class Group<T extends Group<T>> extends Actor<T> {
	
	protected SnapshotArrayList<Actor<?>> mChildList;
	
	protected boolean mTransformMatrixApplied = true;
	protected Matrix3 mTransformMatrix;
	
	protected boolean mColorToChildren;
	protected Color4 mTransformColor;
	
	protected boolean mAlphaToChildren = true;
	
	public Group() {
		this(null);
	}
	
	public Group(Costume costume) {
		super(costume);
	}
	
	@Override
	protected void prepare() {
		super.prepare();
		mChildList = new SnapshotArrayList<Actor<?>>();
		mTransformMatrix = new Matrix3();
		mTransformColor = new Color4();
	}
	
	public T addChild(Actor<?> child) {
		add(mChildList.size(), child);
		return (T) this;
	}
	
	public T addChild(int index, Actor<?> child) {
		add(index, child);
		return (T) this;
	}

	public T addChildBefore(Actor<?> childBefore, Actor<?> newChild) {
		int index = mChildList.indexOf(childBefore);
		add(index, newChild);
		return (T) this;
	}
	
	public T addChildAfter(Actor<?> childAfter, Actor<?> newChild) {
		int index = mChildList.indexOf(childAfter);
		add(index+1, newChild);
		return (T) this;
	}
	
	protected void add(int index, Actor<?> child) {
		if(mChildList.contains(child)) return;
		child.removeFromParent();
		child.setParent(this);
		child.setFloor(getFloor());
		mChildList.add(index, child);
		onChildrenChanged();
	}
	
	public T swap(int index1, int index2) {
		return swap(mChildList.get(index1), mChildList.get(index2));
	}
	
	public T swap(Actor<?> child1, Actor<?> child2) {
		if(child1 == null) throw new IllegalArgumentException("child1 can't be null.");
		if(child2 == null) throw new IllegalArgumentException("child2 can't be null.");
		
		int index1 = mChildList.indexOf(child1);
		if(index1 == -1) throw new IllegalArgumentException("child1 doesn't belong to Group.");
		
		int index2 = mChildList.indexOf(child2);
		if(index2 == -1) throw new IllegalArgumentException("child2 doesn't belong to Group.");
		
		List<Actor<?>> childList = mChildList;
		childList.remove(child1);
		childList.remove(child2);
		// 순서가 빠른 자식부터 추가한다.
		if(index1 < index2) {
			childList.add(index1, child2);
			childList.add(index2, child1);
		} else {
			childList.add(index2, child1);
			childList.add(index1, child2);
		}
		
		onChildrenChanged();
		return (T) this;
	}
	
	public boolean hasChild(Actor<?> actor) {
		return mChildList.contains(actor);
	}
	
	public boolean hasChildren() {
		return !mChildList.isEmpty();
	}
	
	public Actor<?> getChildByClass(Class<?> clazz) {
		return getChildByClass(clazz, true);
	}
	
	public Actor<?> getChildByClass(Class<?> clazz, boolean recursive) {
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		for(int i=0; i<n; i++) {
			Actor<?> child = childList.get(i);
			if(child.getClass().equals(clazz)) return child;
			if(recursive && child instanceof Group) {
				Group<?> group = (Group<?>) child;
				child = group.getChildByClass(clazz);
				if(child != null) return child;
			}
		}
		return null;
	}
	
	public Actor<?> getChildByTag(String tag) {
		return getChildByTag(tag, true);
	}
	
	public Actor<?> getChildByTag(String tag, boolean recursive) {
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		for(int i=0; i<n; i++) {
			Actor<?> child = childList.get(i);
			if(child.getTag() != null && child.getTag().equals(tag)) return child;
			if(recursive && child instanceof Group) {
				Group<?> group = (Group<?>) child;
				child = group.getChildByTag(tag);
				if(child != null) return child;
			}
		}
		return null;
	}
	
	public Actor<?> getChild(int index) {
		return mChildList.get(index);
	}
	
	public SnapshotArrayList<Actor<?>> getChildList() {
		return mChildList;
	}
	
	public boolean removeChild(Actor<?> child) {
		if(child == null) return false;
		if(mChildList.remove(child)) {
			child.setParent(null);
			child.setFloor(null);
			onChildrenChanged();
			return true;
		}
		return false;
	}
	
	/** 자식 리스트가 변경되는 경우에 호출된다. */
	protected void onChildrenChanged() {
	}
	
	public T clearChildren() {
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		for(int i=n-1; i>-1; i--) {
			Actor<?> child = childList.get(i);
			removeChild(child);
		}
		return (T) this;
	}
	
	@Override
	public T clear() {
		clearChildren();
		return super.clear();
	}
	
	@Override
	protected void setFloor(Floor floor) {
		super.setFloor(floor);
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		for(int i=0; i<n; i++) {
			Actor<?> child = childList.get(i);
			child.setFloor(floor);
		}
	}
	
	@Override
	public void update(long time) {
		super.update(time);
		ListIterator<Actor<?>> it = mChildList.begin();
		while(it.hasNext()) {
			Actor<?> child = it.next();
			child.update(time);
		}
		mChildList.end(it);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(mVisible == Visible.ENABLED)
			super.draw(batch, parentAlpha);
		
		if(mVisible != Visible.DISABLED) {
			pushTransformation(batch);
			drawChildren(batch, parentAlpha);
			popTransformation(batch);
		}
	}

	protected void pushTransformation(Batch batch) {
		if(mTransformMatrixApplied) {
			Matrix3 m = mTransformMatrix;
			m.set(getMatrix());
			
			Matrix3 tempMatrix = batch.peekTransformMatrix();
			if(tempMatrix != null) m.postConcat(tempMatrix);
			
			batch.pushTransformMatrix(m);
		}
		
		if(mColorToChildren) {
			Color4 c = mTransformColor;
			c.set(getColor());
			
			Color4 tempColor = batch.peekTransformColor();
			if(tempColor != null) c.mul(tempColor);
			
			batch.pushTransformColor(c);
		}
	}
	
	protected void drawChildren(Batch batch, float parentAlpha) {
		float alpha = parentAlpha;
		if(mAlphaToChildren) alpha *= getAlpha();
		float x = getX();
		float y = getY();
		ListIterator<Actor<?>> it = mChildList.begin();
		while(it.hasNext()) {
			Actor<?> child = it.next();
			if(child.getVisible() == Visible.DISABLED) continue;
			if(!mTransformMatrixApplied) child.moveBy(x, y);
			Floor floor = getFloor();
			if(floor != null && floor.willDebug() && child.willDebug()) 
				child.drawDebug(batch, Floor.getDebugRenderer());
			child.draw(batch, alpha);
			if(!mTransformMatrixApplied) child.moveBy(-x, -y);
		}
		mChildList.end(it);
	}
	
	protected void popTransformation(Batch batch) {
		if(mTransformMatrixApplied) batch.popTransformMatrix();
		if(mColorToChildren) batch.popTransformColor();
	}
	
	@Override
	public Actor<?> contact(float x, float y) {
		if(mTouchable == Touchable.DISABLED) return null;
		
		final Vector2 v = VECTOR;
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		for(int i=n-1; i>-1; i--) {
			Actor<?> child = childList.get(i);
			if(child.getVisible() == Visible.DISABLED) continue;
			child.parentToLocalCoordinates(v.set(x, y)); 
			Actor<?> contact = child.contact(v.x, v.y);
			if(contact != null) return contact;
		}
		return super.contact(x, y);
	}

	public boolean isTransformMatrixApplied() {
		return mTransformMatrixApplied;
	}

	public void setTransformMatrixApplied(boolean transformMatrixApplied) {
		mTransformMatrixApplied = transformMatrixApplied;
	}

	@Override
	protected boolean isTransformApplied() {
		return super.isTransformApplied() && mTransformMatrixApplied;
	}

	public boolean isColorToChildren() {
		return mColorToChildren;
	}

	public void setColorToChildren(boolean colorToChildren) {
		mColorToChildren = colorToChildren;
	}

	public boolean isAlphaToChildren() {
		return mAlphaToChildren;
	}

	public void setAlphaToChildren(boolean parentAlphaApplied) {
		mAlphaToChildren = parentAlphaApplied;
	}
	
	/** 
	 * 현재 Group을 포함하여 {@link Disposable}를 구현한 자식이 있다면 모두에 대해 
	 * {@link Disposable#dispose()}를 호출한다. 
	 */
	public void disposeAll() {
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		for(int i=0; i<n; i++) {
			Actor<?> child = childList.get(i);
			// Group인 경우에는 자식까지 정리한다.
			if(child instanceof Group) {
				((Group<?>) child).disposeAll();
			// Group이 아니며 Disposable을 구현한다면 child를 정리한다.
			} else if(child instanceof Disposable) 
				((Disposable) child).dispose();
		}
		
		// 마지막으로 현재 Group에 대해 적용한다.
		if(this instanceof Disposable) ((Disposable) this).dispose();
	}
	
	public T setDebug(boolean debug, boolean recursive) {
		return setDebug(debug, (recursive)? Integer.MAX_VALUE : 1);
	}
	
	public T setDebug(boolean debug, int recursionDepth) {
		setDebug(debug);
		
		if(recursionDepth <= 0) return (T) this;
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		for(int i=0; i<n; i++) {
			Actor<?> child = childList.get(i);
			if(child instanceof Group) {
				Group<?> group = (Group<?>) child;
				group.setDebug(debug, recursionDepth-1);
			} else
				child.setDebug(debug);
		}
		return (T) this;
	}
	
	public T debugAll() {
		return setDebug(true, true);
	}
	
	@Override
	protected void drawDebugBounds(Batch batch, ShapeRenderer renderer) {
		renderer.setColor(sActorDebugColor);
		MATRIX.set(batch.peekTransformMatrix());
		if(mTransformMatrixApplied) {
			MATRIX.preConcat(mForm.getMatrix());
		} else
			MATRIX.preTranslate(getX(), getY());
		renderer.setTransformMatrix(MATRIX);
		renderer.drawRect(0f, 0f, mForm.getWidth(), mForm.getHeight());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(printActor());
		builder.append(printChildren("\n"));
		return builder.toString();
	}
	
	protected String printChildren(String indent) {
		StringBuilder builder = new StringBuilder();
		List<Actor<?>> childList = mChildList;
		int n = childList.size();
		int last = n - 1;
		for(int i=0; i<n; i++) {
			Actor<?> child = childList.get(i);
			// 마지막 자식이 아닌 경우
			if(i != last) 	builder.append(indent + "├ ");
			// 마지막 자식인 경우
			else				builder.append(indent + "└ ");
			printChild(builder, child);
			
			// 자식이 Group일 경우에 그 자식에 대해서도 재귀적으로 처리
			if(child instanceof Group) {
				Group<?> group = (Group<?>) child;
				// 마지막 자식이 아닌 경우
				if(i != last) 	builder.append(group.printChildren(indent + "│ "));
				// 마지막 자식인 경우
				else				builder.append(group.printChildren(indent + "： "));
			}
		}
		return builder.toString();
	}
	
	protected void printChild(StringBuilder builder, Actor<?> child) {
		builder.append(child.printActor());
	}

}
