package core.scene.stage.actor.widget;

import core.framework.Core;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.batch.Batch;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.widget.utils.Layout;

/**
 * WidgetGroup은 {@link Layout}을 구현하는 {@link Group}이다. 즉, WidgetGroup은 자신 또는 자식을 
 * Stage내에서 어떻게 배치할 것인지를 결정할 수 있는 Group이다. WidgetGroup을 확장하는 
 * 하위 클래스에서는 {@link #layout()}을 재정의하여 자신 및 자식을 어떻게 배치할 지를 구현해야 한다.</p>
 * 
 * 일반적으로 WidgetGroup은 자식의 현재 사이즈가 아닌 Layout과 관련된 최소, 선호, 최대 사이즈를 
 * 이용해 자식을 배치하게 된다. 물론, 자식이 Layout을 구현하지 않는다면 현재 사이즈를 이용한다.</p>
 * 
 * @author 김현우
 */
@SuppressWarnings("unchecked")
public abstract class WidgetGroup<T extends WidgetGroup<T>> extends Group<T> implements Layout<T> {
	
	protected boolean mInvalidated = true;
	protected boolean mFillParent;
	protected boolean mLayoutEnabled = true;
	
	protected Float mPrefWidth;
	protected Float mPrefHeight;
	
	public WidgetGroup() {
		this(null);
	}
	
	public WidgetGroup(Costume costume) {
		super(costume);
	}
	
	@Override
	public abstract void layout();
	
	@Override
	public void clampSize() {
		float width = getWidth();
		float minWidth = getMinWidth();
		width = Math.max(minWidth, width);
		Float maxWidth = getMaxWidth();
		if(maxWidth != null) width = Math.min(maxWidth, width);
		
		float height = getHeight();
		float minHeight = getMinHeight();
		height = Math.max(minHeight, height);
		Float maxHeight = getMaxHeight();
		if(maxHeight != null) height = Math.min(maxHeight, height);
		
		sizeTo(width, height);
	}

	@Override
	public void validate() {
		if(!mLayoutEnabled) return;
		
		if(mFillParent) {
			float width;
			float height;
			
			// 부모가 존재할 경우
			if(hasParent()) {
				width = mParent.getWidth();
				height = mParent.getHeight();
				sizeTo(width, height);
				
			// 부모가 없는 경우
			} else {
				if(hasFloor()) {
					width = getFloor().getCamera().getViewportWidth();
					height = getFloor().getCamera().getViewportHeight();
				} else {
					width = Core.GRAPHICS.getVirtualWidth();
					height = Core.GRAPHICS.getVirtualHeight();
				}
				sizeTo(width, height);
			}
		}
		
		if(mInvalidated) {
			mInvalidated = false;
			layout();
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		super.draw(batch, parentAlpha);
	}

	@Override
	public void invalidate() {
		mInvalidated = true;
	}
	
	@Override
	public void invalidateHierarchy() {
		invalidate();
		Group<?> parent = mParent;
		if(parent instanceof Layout) {
			Layout<?> layoutParent = (Layout<?>) parent;
			layoutParent.invalidateHierarchy();
		}
	}
	
	@Override
	protected void onSizeChanged() {
		invalidateHierarchy();
	}
	
	@Override
	protected void onChildrenChanged() {
		invalidateHierarchy();
	}
	
	@Override
	public T setFillParent(boolean fillParent) {
		mFillParent = fillParent;
		return (T) this;
	}
	
	@Override
	public T setLayoutEnabled(boolean enabled) {
		mLayoutEnabled = enabled;
		if(enabled) invalidateHierarchy();
		return (T) this;
	}
	
	@Override
	public boolean isInvalidated() {
		return mInvalidated;
	}
	
	@Override
	public boolean getFillParent() {
		return mFillParent;
	}
	
	@Override
	public boolean isLayoutEnabled() {
		return mLayoutEnabled;
	}
	
	@Override
	public float getMinWidth() {
		return getPrefWidth();
	}

	@Override
	public float getMinHeight() {
		return getPrefHeight();
	}
	
	@Override
	public float getPrefWidth() {
		// 유저가 선호 너비를 입력한 경우 그 값이 바로 사용되며 
		if(mPrefWidth != null) return mPrefWidth;
		// 그렇지 않으면 디폴트 값이 사용된다.
		return getDefaultPrefWidth();
	}
	
	/** 직접 선호 너비를 입력하지 않은 경우 적절한 수치를 계산하여 디폴트로 사용한다. */
	protected abstract float getDefaultPrefWidth();

	@Override
	public float getPrefHeight() {
		// 유저가 선호 높이를 입력한 경우 그 값이 바로 사용되며 
		if(mPrefHeight != null) return mPrefHeight;
		// 그렇지 않으면 디폴트 값이 사용된다.
		return getDefaultPrefHeight();
	}
	
	/** 직접 선호 높이를 입력하지 않은 경우 적절한 수치를 계산하여 디폴트로 사용한다. */
	protected abstract float getDefaultPrefHeight();
	
	@Override
	public Float getMaxWidth() {
		return null;
	}
	
	@Override
	public Float getMaxHeight() {
		return null;
	}

	@Override
	public T setPrefWidth(Float width) {
		mPrefWidth = width;
		return (T) this;
	}
	
	@Override
	public T setPrefHeight(Float height) {
		mPrefHeight = height;
		return (T) this;
	}
	
	@Override
	public T setPrefSize(Float width, Float height) {
		mPrefWidth = width;
		mPrefHeight = height;
		return (T) this;
	}
	
	@Override
	public boolean isPrefWidthSet() {
		return mPrefWidth != null;
	}
	
	@Override
	public boolean isPrefHeightSet() {
		return mPrefHeight != null;
	}
	
	@Override
	public T pack() {
		sizeTo(getPrefWidth(), getPrefHeight());
		return (T) this;
	}

	@Override
	public void drawDebug(Batch batch, ShapeRenderer renderer) {
		validate();
		super.drawDebug(batch, renderer);
	}
	
}