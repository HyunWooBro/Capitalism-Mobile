package core.scene.stage.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import core.framework.graphics.Color4;
import core.framework.graphics.Form;
import core.framework.graphics.OrthoCamera.Targetable;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.batch.Batch;
import core.math.Matrix3;
import core.math.Vector2;
import core.scene.stage.Floor;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.MoveTo;
import core.scene.stage.actor.action.relative.RotateBy;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.Event;
import core.scene.stage.actor.event.EventListener;
import core.scene.stage.actor.event.TouchListener;
import core.utils.SnapshotArrayList;
import core.utils.pool.Pools;

/**
 * Scene에서 렌더링 또는 터치이벤트 처리를 위해서는 기본적으로 Stage를 거치게 되는데, Actor은 
 * Stage에 참여할 수 있는 가장 기본적인 수단이다. Actor은 가능한 범용적으로 구현되었다.</p>
 * 
 * Actor는 위치, 회전, 스케일 등을 통해 표현되는 <b>좌표계</b> 및 ARGB를 표현하는 <b>Color4</b>로 
 * 구성된 {@link Form}을 갖는다. Actor는 자신의 고유 좌표계를 통해 터치이벤트와 출력을 처리하게 된다.</p>
 * 
 * Actor의 다양한 특성을 <b>업데이트</b>하기 위해서 {@link Action}을 수행할 수 있다. 미리 정의된 
 * {@link MoveTo}, {@link RotateBy} 등은 앞에서 언급한 좌표계와 관련된 특성을 업데이트하기 위해 
 * 사용되는데, 원하는 경우 Action을 재정의하여 Actor의 고유한 특성을 업데이트하도록 할 수도 있다. 
 * Action은 정해진 지속시간동안 수행되도록 하는 것이 일반적이지만 원하는 경우 즉시 완료되도록 할 수도 
 * 있다. 후자의 경우 Actor의 관련 메서드를 직접 호출하는 것과 유사하다.</p>
 * 
 * Actor는 TextureRegion, Aniamtion 등의 구체적인 형식에 관계없이 <b>출력</b>할 수 있는 일반화된 
 * 수단인 {@link Drawable}을 이용한다. 즉, 원하는 출력 방식을 Drawable이라는 독립된 객체를 통해 제어할 
 * 수 있다.</p>
 * 
 * Actor는 어떠한 <b>이벤트</b>도 다룰 수 있도록 특정 {@link Event}를 처리할 수 있는 적절한 
 * {@link EventListener}를 등록하는 방식을 채용하여 일반화된 이벤트처리 시스템을 구현하고 있다. 
 * {@link EventListener}를 확장한 {@link TouchListener}, {@link ActionListener},  {@link ChangeListener}가 
 * 가장 기본적인 예이다. {@link #fire(Event)}을 통해 이벤트가 전해지면 관련 리스너를 찾아 처리하게 된다. 
 * 한편, 이벤트의 전달 흐름에 따라 capture 리스너와 일반 리스너로 나뉘어진다. 우선, fire하는 Actor를 
 * 기준으로 가장 최상위 부모에서부터 capture 리스너가 차례대로 호출되고 마지막으로 fire하는 Actor의 
 * capture 리스너가 호출된다. 그다음 이제 반대로 fire하는 Actor부터 시작하여 최상위 부모까지 차례대로 
 * 일반 리스너가 호출된다. 이것을 bubbling이라고 한다.</p>
 * 
 * 한편, Actor는 Group을 부모로 가질 수 있다. Actor의 위치는 부모를 기준으로 표현된다.</p>
 * 
 * @author 김현우
 * @see Group
 */
@SuppressWarnings("unchecked")
public abstract class Actor<T extends Actor<T>> implements Targetable {
	
	/** 
	 * Actor의 가시성 상태의 유형. CHILDREN_ONLY는 Group에서만 적용되며, 자식이 없는 기본 
	 * Actor에서는 ENABLED와 차이가 없다.
	 */
	public enum Visible {
		ENABLED,
		DISABLED,
		CHILDREN_ONLY, 
	}
	
	/** 
	 * Actor의 터치 가능 상태의 유형. CHILDREN_ONLY는 Group에서만 적용되며, 자식이 없는 기본 
	 * Actor에서는 ENABLED와 차이가 없다.
	 */
	public enum Touchable{
		ENABLED,
		DISABLED,
		CHILDREN_ONLY, 
	}
	
	public static Color4 sActorDebugColor = Color4.blue();
	
	protected static final Matrix3 MATRIX = new Matrix3();
	protected static final Vector2 VECTOR = new Vector2();
	
	/** Actor를 구별하는 태그 */
	private String mTag;
	
	/** Actor가 속해있는 Floor */
	private Floor mFloor;
	
	/** Actor의 가시성 상태. 비가시성 상태이면 터치도 불가능 */
	protected Visible mVisible = Visible.ENABLED;
	/** Actor의 터치 가능 상태 */
	protected Touchable mTouchable = Touchable.ENABLED;
	
	/** Actor의 부모 */
	protected Group<?> mParent;

	/** Actor을 렌더링하기 위한 Drawable */
	protected Drawable mDrawable;
    
	/** 현재 진행중인 Action 리스트 */
    protected SnapshotArrayList<Action> mActionList;
	
	protected SnapshotArrayList<EventListener> mEventCaptureListenerList;
	protected SnapshotArrayList<EventListener> mEventListenerList;
	
	/** Actor의 위치, 회전, 스케일, 색을 처리 */
	protected Form mForm;
	
	/** 필요에 따라 유저가 원하는 어떤 Object이든 등록 및 이용 가능  */
	protected Object mUserObject;
	
	/** Actor를 꾸며주기 위한 의상 */
	protected Costume mCostume;
	
	protected boolean mDebug;
	
	public Actor() {
		this(null);
	}
	
	public Actor(Costume costume) {
		mCostume = createCostume(costume);
		prepare();
		if(mCostume != null) costumeChanged();
	}
	
	protected void prepare() {
		mActionList = new SnapshotArrayList<Action>();
		mEventCaptureListenerList = new SnapshotArrayList<EventListener>();
		mEventListenerList = new SnapshotArrayList<EventListener>();
		mForm = new Form();
	}
	
	protected Costume createCostume(Costume costume) {
		return null;
	}

	/** 수정한 Drawable이 사이즈에 반영되길 원한다면 {@link #pack()}을 호출해야 한다. */
	public Costume getCostume() {
		return mCostume;
	}
	
	/** 
	 * 이 메서드를 재정의하는 경우 주의해야할 점은 하위 클래스가 초기화되기 전에 재정의된  
	 * 메서드가 호출된다는 점이다.
	 */
	public void costumeChanged() {
	}

	public T addAction(Action action) {
		action.reset();
		action.start();
		if(!mActionList.contains(action)) {
			action.setActor(this);
			mActionList.add(action);
		}
		return (T) this;
	}
	
	public boolean hasAction(Action action) {
		return mActionList.contains(action);
	}
	
	public boolean hasActions() {
		return !mActionList.isEmpty();
	}
	
	public SnapshotArrayList<Action> getActionList() {
		return mActionList;
	}
	
	public void cancelActions() {
		List<Action> actionList = mActionList;
		int n = actionList.size();
		for(int i=0; i<n; i++) {
			Action action = actionList.get(i);
			action.cancel();
		}
	}
	
	public void finishActions() {
		List<Action> actionList = mActionList;
		int n = actionList.size();
		for(int i=0; i<n; i++) {
			Action action = actionList.get(i);
			action.finish();
		}
	}
	
	public boolean removeAction(Action action) {
		if(mActionList.remove(action)) {
			action.setActor(null);
			return true;
		}
		return false;
	}
	
	public void clearActions() {
		List<Action> actionList = mActionList;
		int n = actionList.size();
		for(int i=0; i<n; i++) {
			Action action = actionList.get(i);
			action.setActor(null);
		}
		mActionList.clear();
	}
	
	public T addEventCaptureListener(EventListener listener) {
		if(listener == null) return (T) this;
		if(!mEventCaptureListenerList.contains(listener)) mEventCaptureListenerList.add(listener);
		return (T) this;
	}
	
	public T addEventListener(EventListener listener) {
		if(listener == null) return (T) this;
		if(!mEventListenerList.contains(listener)) mEventListenerList.add(listener);
		return (T) this;
	}
	
	public boolean hasEventCaptureListener(EventListener listener) {
		return mEventCaptureListenerList.contains(listener);
	}
	
	public boolean hasEventListener(EventListener listener) {
		return mEventListenerList.contains(listener);
	}

	public SnapshotArrayList<EventListener> getEventCaptureListenerList() {
		return mEventCaptureListenerList;
	}
	
	public SnapshotArrayList<EventListener> getEventListenerList() {
		return mEventListenerList;
	}

	public boolean removeEventCaptureListener(EventListener listener) {
		return mEventCaptureListenerList.remove(listener);
	}
	
	public boolean removeEventListener(EventListener listener) {
		return mEventListenerList.remove(listener);
	}

	public T clearEventCaptureListeners() {
		mEventCaptureListenerList.clear();
		return (T) this;
	}
	
	public T clearEventListeners() {
		mEventListenerList.clear();
		return (T) this;
	}
	
	public T clearListeners() {
		mEventCaptureListenerList.clear();
		mEventListenerList.clear();
		return (T) this;
	}
	
	public T clear() {
		clearListeners();
		clearActions();
		return (T) this;
	}
	
	/** 
	 * Actor의 특성을 시간에 기반해서 업데이트한다. 기본적으로 Action을 처리하고 
	 * Costume이 존재하면 업데이트한다.</p>
	 * 
	 * 출력과 밀접한 관련을 갖는 경우는 update보다는 {@link #draw(Batch, float)}에서 
	 * 처리하도록 한다. Floor에 소속되어 있다면 update는 기본적으로 호출되는데 반해 
	 * draw는 visible일 경우에만 호출이 되는데, 만약 invisible인 상황에서 update에서 
	 * 출력 관련 작업을 처리한다면 어떤 효과도 얻지 못하게 될 것이다.</p>
	 */
	public void update(long time) {
		act(time);
		updateDrawable(time);
		if(mCostume != null) mCostume.update(time);
	}
	
	/** Action을 처리한다. */
	protected void act(long time) {
		ListIterator<Action> it = mActionList.begin();
		while(it.hasNext()) {
			Action action = it.next();
			if(!action.act(time)) removeAction(action);
		}
		mActionList.end(it);
	}

	protected void updateDrawable(long time) {
	}
	
	public void draw(Batch batch, float parentAlpha) {
		if(mDrawable != null) drawSelf(batch, parentAlpha);
	}
	
	protected void drawSelf(Batch batch, float parentAlpha) {
		float a = getAlpha();
		setAlpha(a * parentAlpha * mDrawable.getAlpha());
		
		if(isTransformApplied()) {
			mDrawable.draw(batch, getForm());
		} else {
			float x = getX();
			float y = getY();
			float width = getWidth();
			float height = getHeight();
			boolean flipX = isFlipX();
			boolean flipY = isFlipY();
			batch.setColor(getColor());
			
			mDrawable.draw(
					batch, 
					x, y, 
					width, height, 
					flipX, flipY);
		}
		
		setAlpha(a);
	}
	
	/** 
	 * Actor와 접촉을 시도한다. Actor의 boundry 내에 x, y의 위치가 포함되면 자신을 리턴한다. 
	 * 일반적으로 자신의 좌표계에서 boundary 안에 터치가 들어왔는지를 체크하기 위해 
	 * 사용된다.</p>
	 * 
	 * Group의 경우 가장 깊숙히 위치한 자식을 리턴한다.</p>
	 */
	public Actor<?> contact(float x, float y) {
		return contactSelf(x, y);
	}
	
	public Actor<?> contactSelf(float x, float y) {
		if(mTouchable == Touchable.DISABLED) return null;
		if(x >= 0f && x <= getWidth() && y >= 0f && y <= getHeight()) return this;
		return null;
	}
	
	/** 
	 * Event를 가장 최상위 부모로부터 자신까지 caputure리스너로 처리한 후 자신부터 
	 * 가장 최상위 부모까지 일반 리스너로 처리한다.</p>
	 * 
	 * Event가 취소되면 false를 리턴하고 완료되면 true를 리턴한다.</p>
	 */
	public boolean fire(Event event) {
		event.setTargetActor(this);
		
		List<Actor<?>> ancestors = Pools.obtain(ArrayList.class);
		Actor<?> parent = mParent;
		while(parent != null) {
			ancestors.add(parent);
			parent = parent.mParent;
		}
		
		// 조상의 capture 리스너 처리
		int n = ancestors.size();
		for(int i=n-1; i>-1; i--) {
			parent = ancestors.get(i);
			parent.notify(event, true);
			if(event.isStopped()) return !event.isCanceled();
		}
		
		// 현재 Actor의 capture 리스너 처리
		notify(event, true);
		if(event.isStopped()) return !event.isCanceled();
		
		// 현재 Actor의 일반 리스너 처리
		notify(event, false);
		if(event.isStopped() || !event.isBubbling()) return !event.isCanceled();
		
		// 조상의 일반 리스너 처리
		for(int i=0; i<n; i++) {
			parent = ancestors.get(i);
			parent.notify(event, false);
			if(event.isStopped()) return !event.isCanceled();
		}
		
		ancestors.clear();
		Pools.recycle(ancestors);
		return !event.isCanceled();
	}
	
	public void notify(Event event, boolean capture) {
		event.setListenerActor(this);
		SnapshotArrayList<EventListener> listenerList = (capture)? mEventCaptureListenerList : mEventListenerList;
		ListIterator<EventListener> it = listenerList.begin();
		while(it.hasNext()) {
			EventListener listener = it.next();
			if(listener.handle(event)) event.handle();
		}
		listenerList.end(it);
	}
	
	public boolean isVisible() {
		return mVisible == Visible.ENABLED;
	}
	
	public Visible getVisible() {
		return mVisible;
	}
	
	public T setVisible(boolean visible) {
		mVisible = (visible)? Visible.ENABLED : Visible.DISABLED;
		return (T) this;
	}

	public T setVisible(Visible visible) {
		mVisible = visible;
		return (T) this;
	}
	
	public boolean isTouchable() {
		return mTouchable == Touchable.ENABLED;
	}
	
	public Touchable getTouchable() {
		return mTouchable;
	}
	
	public T setTouchable(boolean touchable) {
		mTouchable = (touchable)? Touchable.ENABLED : Touchable.DISABLED;
		return (T) this;
	}

	public T setTouchable(Touchable touchable) {
		mTouchable = touchable;
		return (T) this;
	}

	public boolean hasFloor() {
		return mFloor != null;
	}

	/** 
	 * Actor가 속해 있는 Floor을 얻는다. Actor와 Floor가 연관되는 시점에 내부적으로 
	 * Floor가 설정된다. 따라서 Floor가 설정되기 이전에 이 메서드를 호출하면 null을 리턴한다.
	 */
	public Floor getFloor() {
		return mFloor;
	}

	/** Actor의 Floor을 지정한다. 내부적으로 사용된다. */
	protected void setFloor(Floor floor) {
		mFloor = floor;
		if(willDebug() && floor != null) floor.setDebug(true);
	}

	public boolean hasParent() {
		return mParent != null;
	}
	
	public Group<?> getParent() {
		return mParent;
	}

	protected void setParent(Group<?> parent) {
		mParent = parent;
	}
	
	/** 부모로부터 현재 Actor을 제거한다. 성공적으로 제거되면 true를 리턴한다. */
	public boolean removeFromParent() {
		if(mParent != null) return mParent.removeChild(this);
		return false;
	}

	public boolean isDescendantOf(Actor<?> actor) {
		if(actor == null) return false;
		Actor<?> current = this;
		while(true) {
			if(current == null) return false;
			if(current == actor) return true;
			current = current.mParent;
		}
	}
	
	public boolean isAscendantOf(Actor<?> actor) {
		if(actor == null) return false;
		Actor<?> current = actor;
		while(true) {
			if(current == null) return false;
			if(current == this) return true;
			current = current.mParent;
		}
	}
	
	/** 
	 * Actor의 Form을 얻는다. Form을 직접 수정할 경우 관련된 작업(예를 들어, 
	 * 사이즈를 수정할 경우 호출되는 {@link #onSizeChanged()} 등)이 자동으로 수행되지 
	 * 않으므로 반환된 Form의 set메서드를 사용할 때는 주의해야 한다.
	 */
	protected Form getForm() {
		return mForm;
	}
	
	/** Actor의 Form의 Matrix를 얻는다. */
	public final Matrix3 getMatrix() {
		return mForm.getMatrix();
	}

	public final float getAlpha() {
		return mForm.getAlpha();
	}

	public final float getRed() {
		return mForm.getRed();
	}

	public final float getGreen() {
		return mForm.getGreen();
	}

	public final float getBlue() {
		return mForm.getBlue();
	}

	public final Color4 getColor() {
		return mForm.getColor();
	}

	public final T setAlpha(float alpha) {
		mForm.setAlpha(alpha);
		return (T) this;
	}

	public final T setRed(float red) {
		mForm.setRed(red);
		return (T) this;
	}

	public final T setGreen(float green) {
		mForm.setGreen(green);
		return (T) this;
	}

	public final T setBlue(float blue) {
		mForm.setBlue(blue);
		return (T) this;
	}
	
	public final T setAlpha(int alpha) {
		mForm.setAlpha(alpha);
		return (T) this;
	}

	public final T setRed(int red) {
		mForm.setRed(red);
		return (T) this;
	}

	public final T setGreen(int green) {
		mForm.setGreen(green);
		return (T) this;
	}

	public final T setBlue(int blue) {
		mForm.setBlue(blue);
		return (T) this;
	}

	public final T setColor(Color4 color) {
		mForm.setColor(color);
		return (T) this;
	}

	public final T setColor(float alpha, float red, float green, float blue) {
		mForm.setColor(alpha, red, green, blue);
		return (T) this;
	}

	public final T setColor(int alpha, int red, int green, int blue) {
		mForm.setColor(alpha, red, green, blue);
		return (T) this;
	}

	public final T setColor(int color) {
		mForm.setColor(color);
		return (T) this;
	}
	
	public final T addColor(Color4 color) {
		mForm.addColor(color);
		return (T) this;
	}
	
	public final T addColor(float alpha, float red, float green, float blue) {
		mForm.addColor(alpha, red, green, blue);
		return (T) this;
	}
	
	public final T subColor(Color4 color) {
		mForm.subColor(color);
		return (T) this;
	}
	
	public final T subColor(float alpha, float red, float green, float blue) {
		mForm.subColor(alpha, red, green, blue);
		return (T) this;
	}
	
	public final T mulColor(Color4 color) {
		mForm.mulColor(color);
		return (T) this;
	}
	
	public final T mulColor(float alpha, float red, float green, float blue) {
		mForm.subColor(alpha, red, green, blue);
		return (T) this;
	}
	
	public final T clampColor() {
		mForm.clampColor();
		return (T) this;
	}

	public final float getX() {
		return mForm.getX();
	}

	public final float getY() {
		return mForm.getY();
	}
	
	public final T setX(float x) {
		if(mForm.getX() == x) return (T) this;
		mForm.setX(x);
		onPositionChanged();
		return (T) this;
	}
	
	public final T setY(float y) {
		if(mForm.getY() == y) return (T) this;
		mForm.setY(y);
		onPositionChanged();
		return (T) this;
	}

	public final T moveTo(float x, float y) {
		if(mForm.getX() == x && mForm.getY() == y) return (T) this;
		mForm.moveTo(x, y);
		onPositionChanged();
		return (T) this;
	}

	public final T moveBy(float x, float y) {
		if(x == 0f && y == 0f) return (T) this;
		mForm.moveBy(x, y);
		onPositionChanged();
		return (T) this;
	}

	public final float getScaleX() {
		return mForm.getScaleX();
	}

	public final float getScaleY() {
		return mForm.getScaleY();
	}
	
	public final T setScaleX(float scaleX) {
		mForm.setScaleX(scaleX);
		return (T) this;
	}

	public final T setScaleY(float scaleY) {
		mForm.setScaleY(scaleY);
		return (T) this;
	}

	public final T scaleTo(float scale) {
		mForm.scaleTo(scale);
		return (T) this;
	}

	public final T scaleTo(float scaleX, float scaleY) {
		mForm.scaleTo(scaleX, scaleY);
		return (T) this;
	}

	public final T scaleBy(float scaleX, float scaleY) {
		mForm.scaleBy(scaleX, scaleY);
		return (T) this;
	}

	public final T scaleBy(float scale) {
		mForm.scaleBy(scale);
		return (T) this;
	}

	public final float getRotation() {
		return mForm.getRotation();
	}

	public final T rotateTo(float rotation) {
		mForm.rotateTo(rotation);
		return (T) this;
	}

	public final T rotateBy(float rotation) {
		mForm.rotateBy(rotation);
		return (T) this;
	}

	public final float getPivotX() {
		return mForm.getPivotX();
	}

	public final float getPivotY() {
		return mForm.getPivotY();
	}

	public final T setPivotX(float pivotX) {
		mForm.setPivotX(pivotX);
		return (T) this;
	}

	public final T setPivotY(float pivotY) {
		mForm.setPivotY(pivotY);
		return (T) this;
	}

	public final T pivotToCenter() {
		mForm.pivotToCenter();
		return (T) this;
	}

	public final T pivotTo(float pivotX, float pivotY) {
		mForm.pivotTo(pivotX, pivotY);
		return (T) this;
	}

	public final boolean isTransformed() {
		return mForm.isTransformed();
	}
	
	protected boolean isTransformApplied() {
		return isTransformed();
	}
	
	/** Actor의 가로 길이를 얻는다. */
	public final float getWidth() {
		return mForm.getWidth();
	}
	
	/** Actor의 세로 길이를 얻는다. */
	public final float getHeight() {
		return mForm.getHeight();
	}

	public final T setWidth(float width) {
		if(mForm.getWidth() == width) return (T) this;
		mForm.setWidth(width);
		onSizeChanged();
		return (T) this;
	}

	public final T setHeight(float height) {
		if(mForm.getHeight() == height) return (T) this;
		mForm.setHeight(height);
		onSizeChanged();
		return (T) this;
	}

	public final T sizeTo(float width, float height) {
		if(mForm.getWidth() == width && mForm.getHeight() == height) return (T) this;
		mForm.sizeTo(width, height);
		onSizeChanged();
		return (T) this;
	}

	public final T sizeBy(float width, float height) {
		if(width == 0f && height == 0f) return (T) this;
		mForm.sizeBy(width, height);
		onSizeChanged();
		return (T) this;
	}
	
	public final T flipSize() {
		if(mForm.getWidth() == mForm.getHeight()) return (T) this;
		mForm.flipSize();
		onSizeChanged();
		return (T) this;
	}
	
	public final T setBounds(float x, float y, float width, float height) {
		if(mForm.getX() != x || mForm.getY() != y) {
			mForm.moveTo(x, y);
			onPositionChanged();
		}
		if(mForm.getWidth() != width || mForm.getHeight() != height) {
			mForm.sizeTo(width, height);
			onSizeChanged();
		}
		return (T) this;
	}
	
	/** Actor의 위치(x 또는 y)가 변경되는 경우에 호출된다. */
	protected void onPositionChanged() {
	}
	
	/** Actor의 사이즈(너비 또는 높이)가 변경되는 경우에 호출된다. */
	protected void onSizeChanged() {
	}

	public final T moveCenterTo(float x, float y) {
		mForm.moveCenterTo(x, y);
		return (T) this;
	}
	
	public final float getCenterX() {
		return mForm.getCenterX();
	}
	
	public final float getCenterY() {
		return mForm.getCenterY();
	}
	
	public final boolean isFlipX() {
		return mForm.isFlipX();
	}

	public final boolean isFlipY() {
		return mForm.isFlipY();
	}

	public final T setFlipX(boolean flipX) {
		mForm.setFlipX(flipX);
		return (T) this;
	}

	public final T setFlipY(boolean flipY) {
		mForm.setFlipY(flipY);
		return (T) this;
	}
	
	@Override
	public float getTargetX() {
		return getCenterX();
	}
	
	@Override
	public float getTargetY() {
		return getCenterY();
	}
	
	/** 부모가 존재한다면 자식들 중에서 가장 전면에 출력되도록 한다. */
	public int toFront() {
		return setZ(Integer.MAX_VALUE);
	}
	
	/** 부모가 존재한다면 자식들 중에서 가장 후면에 출력되도록 한다. */
	public int toBack() {
		return setZ(0);
	}
	
	/** 
	 * 부모가 존재한다면 자식 리스트에서 현재 Actor의 인덱스를 얻는다. 부모가 존재하지 않으면 
	 * -1을 리턴한다. 
	 */
	public int getZ() {
		Group<?> parent = mParent;
		if(parent != null) return parent.mChildList.indexOf(this);
		return -1;
	}
	
	/** 
	 * 부모가 존재한다면 지정한 인덱스에 현재 Actor가 위치하도록 한다. 리턴 값은 리스트에서 
	 * 실제로 위치하는 인덱스이다. 부모가 존재하지 않으면 -1을 리턴한다.
	 */
	public int setZ(int index) {
		if(index < 0) throw new IllegalArgumentException("Negative index not allowed.");
		
		int realIndex = -1;
		Group<?> parent = mParent;
		if(parent != null) {
			List<Actor<?>> childList = parent.mChildList;
			if(childList.size() == 1) return 0;
			int oldIndex = childList.indexOf(this);
			childList.remove(this);
			int n = childList.size();
			realIndex = (index >= n)? n : index;
			childList.add(realIndex, this);
			// 인덱스에 변화가 있다면
			if(realIndex != oldIndex) parent.onChildrenChanged();
		}
		return realIndex;
	}
	
	public String getTag() {
		return mTag;
	}

	/** Actor에 태그를 부여한다. */
	public T setTag(String tag) {
		mTag = tag;
		return (T) this;
	}
	
	public Object getUserObject() {
		return mUserObject;
	}

	public T setUserObject(Object userObject) {
		mUserObject = userObject;
		return (T) this;
	}
	
	public Vector2 parentToLocalCoordinates(Vector2 pos) {
		final float[] tmp = Vector2.TMP_ARRAY;
		tmp[0] = pos.x;
		tmp[1] = pos.y;
		if(isTransformApplied()) {
			// [Tr](Local_p) = (Parent_p)의 양변에 
			// [Tr] = [역P][T][R][S][P]의 역행렬을 곱하여 
			// (Local_p) = [역Tr](Parent_p)를 통해 부모좌표계에서 로컬좌표계로 변환한다. 
			getMatrix().invert(MATRIX);
			MATRIX.mapPoints(tmp, 0, tmp, 0, 1/*pair(s)*/);
		} else {
			tmp[0] -= getX();
			tmp[1] -= getY();
		}
		pos.set(tmp[0], tmp[1]);
		return pos;
	}
	
	public Vector2 localToParentCoordinates(Vector2 pos) {
		final float[] tmp = Vector2.TMP_ARRAY;
		tmp[0] = pos.x;
		tmp[1] = pos.y;
		if(isTransformApplied()) {
			getMatrix().mapPoints(tmp, 0, tmp, 0, 1/*pair(s)*/);
		} else {
			tmp[0] += getX();
			tmp[1] += getY();
		}
		pos.set(tmp[0], tmp[1]);
		return pos;
	}
	
	public Vector2 floorToLocalCoordinates(Vector2 pos) {
		if(!hasFloor()) throw new IllegalStateException("Assign Floor before calling this method.");
		if(mParent != null) mParent.floorToLocalCoordinates(pos);
		return parentToLocalCoordinates(pos);
	}
	
	public Vector2 localToFloorCoordinates(Vector2 pos) {
		if(!hasFloor()) throw new IllegalStateException("Assign Floor before calling this method.");
		if(mParent != null) mParent.localToFloorCoordinates(pos);
		return localToParentCoordinates(pos);
	}
	
	public Vector2 screenToLocalCoordinates(Vector2 pos) {
		Floor floor = getFloor();
		if(floor == null) throw new IllegalStateException("Assign Floor before calling this method.");
		return floorToLocalCoordinates(floor.screenToFloorCoordinates(pos));
	}
	
	public Vector2 localToScreenCoordinates(Vector2 pos) {
		Floor floor = getFloor();
		if(floor == null) throw new IllegalStateException("Assign Floor before calling this method.");
		return floor.floorToScreenCoordinates(localToFloorCoordinates(pos));
	}

	public boolean willDebug() {
		return mDebug;
	}

	public T setDebug(boolean debug) {
		mDebug = debug;
		Floor floor = getFloor();
		if(debug && floor != null) floor.setDebug(true);
		return (T) this;
	}
	
	public T debug() {
		setDebug(true);
		return (T) this;
	}
	
	public void drawDebug(Batch batch, ShapeRenderer renderer) {
		drawDebugBounds(batch, renderer);
	}
	
	/** Actor의 사각영역의 경계를 지정된 색으로 출력한다. Actor의 스케일 또는 회전변환이 적용된다. */
	protected void drawDebugBounds(Batch batch, ShapeRenderer renderer) {
		renderer.setColor(sActorDebugColor);
		MATRIX.set(batch.peekTransformMatrix());
		MATRIX.preConcat(mForm.getMatrix());
		renderer.setTransformMatrix(MATRIX);
		renderer.drawRect(0f, 0f, mForm.getWidth(), mForm.getHeight());
	}
	
	@Override
	public String toString() {
		return printActor();
	}
	
	/** 
	 * Actor의 정보를 출력한다. 기본적으로 인스턴스의 클래스명과 함께 태그를 출력하지만,  
	 * 재정의하여 추가적인 정보를 출력할 수 있다.</p> 
	 * 
	 * 이름없는 객체일 경우 "[anonymous]"를 덧붙인 후 출력한다.</p>
	 */
	protected String printActor() {
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(getActorName());
		builder.append(">");
		if(mTag != null) {
			builder.append(" [mTag=");
			builder.append(mTag);
			builder.append("]");
		}
		return builder.toString();
	}
	
	public String getActorName() {
		String name = getClass().getSimpleName();
		if(name.isEmpty()) name = getClass().getSuperclass().getSimpleName() + "[anonymous]";
		return name;
	}
	
	public interface Costume {
		
		public void set(Costume costume);

		public void merge(Costume costume);

		public void update(long time);
	}
	
}
