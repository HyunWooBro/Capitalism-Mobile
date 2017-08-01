package core.scene.stage.actor.widget.table.button;

import core.framework.graphics.batch.Batch;
import core.math.Vector2;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ClickTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.table.Table;

@SuppressWarnings("unchecked")
public abstract class Button<T extends Button<T>> extends Table<T> {
	
	protected Action mStartTouchAction;
	protected Action mFinalTouchAction;
	
	protected boolean mChecked;
	
	protected boolean mTouchActionStarted;
	
	/** 비활성 상태 */
	protected boolean mDisabled;
	
	private boolean mConstructed;
	
	protected ClickTouchListener mClickTouchListener;
	
	/*package*/ ButtonGroup mButtonGroup;
	
	public Button(ButtonCostume costume) {
		super(costume);
		mConstructed = true;
		
		addEventListener(mClickTouchListener = new ClickTouchListener() {
			@Override
			public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
				// 외부에 있는 자식에 의한 호출을 방지
				if(contactSelf(x, y) == null) return false;
				if(!super.onDown(event, x, y, listener)) return false;
				if(mStartTouchAction != null) addAction(mStartTouchAction);
				mTouchActionStarted = true;
				return true;
			}
			
			@Override
			public void click(TouchEvent event, float x, float y, Actor<?> listener) {
				setChecked(!mChecked);
			}
		});
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new ButtonCostume(costume) : new ButtonCostume();
	}

	/** 수정한 Drawable이 사이즈에 반영되길 원한다면 {@link #pack()}을 호출해야 한다. */
	@Override
	public ButtonCostume getCostume() {
		return (ButtonCostume) mCostume;
	}
	
	@Override
	public void costumeChanged() {
		ButtonCostume c = getCostume();
		Drawable drawable = ((c.up != null)? c.up : ((c.down != null)? c.down : c.checked));
		if(drawable != null) {
			padTop(drawable.getPadTop());
			padLeft(drawable.getPadLeft());
			padRight(drawable.getPadRight());
			padBottom(drawable.getPadBottom());
		}
		invalidateHierarchy();
		if(mConstructed) pack();
	}
	
	@Override
	public float getMinHeight() {
		float height = super.getMinHeight();
		height = Math.max(height, getPrefHeight()/2);
		return height;
	}

	@Override
	public float getMinWidth() {
		float width = super.getMinWidth();
		width = Math.max(width, getPrefWidth()/2);
		return width;
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		float width = super.getDefaultPrefWidth();
		ButtonCostume costume = getCostume();
		if(costume.up != null) width = Math.max(width, costume.up.getWidth());
		if(costume.down != null) width = Math.max(width, costume.down.getWidth());
		if(costume.checked != null) width = Math.max(width, costume.checked.getWidth());
		return width;
	}
	
	@Override
	protected float getDefaultPrefHeight() {
		float height = super.getDefaultPrefHeight();
		ButtonCostume costume = getCostume();
		if(costume.up != null) height = Math.max(height, costume.up.getHeight());
		if(costume.down != null) height = Math.max(height, costume.down.getHeight());
		if(costume.checked != null) height = Math.max(height, costume.checked.getHeight());
		return height;
	}

	protected void setDrawable() {
		ButtonCostume costume = getCostume();
		if(mDisabled && mChecked)			mDrawable = costume.disabledChecked;
		else if(mDisabled)							mDrawable = costume.disabled;
		else if(mChecked && isPressed())	mDrawable = costume.downChecked;
		else if(mChecked) 						mDrawable = costume.checked;
		else if(isPressed()) 						mDrawable = costume.down;
		else 												mDrawable = costume.up;
	}
	
	@Override
	public void update(long time) {
		super.update(time);
		if(mTouchActionStarted && !isPressed()) {
			if(mStartTouchAction != null) mStartTouchAction.cancel();
			if(mFinalTouchAction != null) addAction(mFinalTouchAction);
			mTouchActionStarted = false;
		}
	}
	
	@Override
	protected void updateDrawable(long time) {
		if(mDrawable != null) {
			mDrawable.update(time);
			if(!mDrawable.isSizeFixed()) pack();
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setDrawable();
		ButtonCostume costume = getCostume();
		if((isPressed() || isChecked()) && costume.downOffset != null) {
			float x = getX();
			float y = getY();
			moveTo(x+costume.downOffset.x, y+costume.downOffset.y);
			super.draw(batch, parentAlpha);
			moveTo(x, y);
		} else
			super.draw(batch, parentAlpha);
	}
	
	@Override
	public Actor<?> contact(float x, float y) {
		if(mDisabled) return null;
		return super.contact(x, y);
	}

	public Action getStartTouchAction() {
		return mStartTouchAction;
	}

	public Action getFinalTouchAction() {
		return mFinalTouchAction;
	}

	public T setStartTouchAction(Action startTouchAction) {
		mStartTouchAction = startTouchAction;
		return (T) this;
	}

	public T setFinalTouchAction(Action finalTouchAction) {
		mFinalTouchAction = finalTouchAction;
		return (T) this;
	}
	
	public boolean isChecked() {
		return mChecked;
	}
	
	public T setChecked(boolean checked) {
		if(mChecked == checked) return (T) this;
		// ButtonGroup에 속한 경우에는 ButtonGroup을 통해 처리한다.
		if(mButtonGroup != null) {
			mButtonGroup.select(this);
		} else { // 그렇지 않은 경우에는 자신이 직접 처리한다.
			mChecked = checked;
			onCheckChanged();
		}
		return (T) this;
	}
	
	/** ButtonGroup에서 checked 상태를 강제하기 위해 호출된다. */
	/*package*/ void forceChecked(boolean checked) {
		if(mChecked == checked) return;
		mChecked = checked;
		onCheckChanged();
	}
	
	protected boolean onCheckChanged() {
		// ButtonGroup에 속한 경우에는 각 Button이 fire하는 ChangeEvent를 취소해도 mChecked 상태를 
		// 되돌릴 수 없고 ButtonGroup에서 fire되는 ChangeEvent를 취소해야 모든 Button이 원상태로 복구된다. 
		if(!fire(new ChangeEvent()) && mButtonGroup == null) {
			mChecked = !mChecked;
			return false;
		}
		return true;
	}

	public T toggle() {
		return setChecked(!mChecked);
	}

	public boolean isPressed() {
		return mClickTouchListener.isVisualPressed();
	}
	
	public boolean isDisabled() {
		return mDisabled;
	}

	public T setDisabled(boolean disabled) {
		mDisabled = disabled;
		return (T) this;
	}
	
	/** @deprecated {@link #getCostume()}을 이용할 것 */
	@Deprecated
	@Override
	public final T setDrawable(Drawable drawable) {
		throw new UnsupportedOperationException("Use getCostume().");
	}
	
	/** @deprecated {@link #getCostume()}을 이용할 것 */
	@Deprecated
	@Override
	public final T setDrawable(Drawable drawable, boolean padding) {
		throw new UnsupportedOperationException("Use getCostume().");
	}
	
	/** @deprecated {@link #getCostume()}을 이용할 것 */
	@Deprecated
	@Override
	public final Drawable getDrawable() {
		throw new UnsupportedOperationException("Use getCostume().");
	}
	
	public static class ButtonCostume implements Costume {
		
		public Drawable up;
		public Drawable down;
		public Drawable checked;
		public Drawable downChecked;
		public Drawable disabled;
		public Drawable disabledChecked;
		
		public Vector2 downOffset;
		
		public ButtonCostume() {
		}
		
		public ButtonCostume(Costume costume) {
			set(costume);
		}
		
		@Override
		public void set(Costume costume) {
			if(!(costume instanceof ButtonCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			ButtonCostume c = (ButtonCostume) costume;
			up = c.up;
			down = (c.down == null)? c.up : c.down;
			checked = (c.checked == null)? down : c.checked;
			downChecked = (c.downChecked == null)? 
					((c.checked == null)? down : c.checked) : c.downChecked;
			disabled = (c.disabled == null)? c.up : c.disabled;
			disabledChecked = (c.disabledChecked == null)? 
					((c.checked == null)? disabled : c.checked) : c.disabledChecked;
			if(c.downOffset != null) {
				if(downOffset != null) {
					downOffset.set(c.downOffset);
				} else
					downOffset = new Vector2(c.downOffset);
			} else
				downOffset = null;
		}
		
		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof ButtonCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			ButtonCostume c = (ButtonCostume) costume;
			if(c.up != null) up = c.up;
			if(c.down != null) down = c.down;
			if(c.checked != null) checked = c.checked;
			if(c.downChecked != null) downChecked = c.downChecked;
			if(c.disabled != null) disabled = c.disabled;
			if(c.disabledChecked != null) disabledChecked = c.disabledChecked;
			if(c.downOffset != null) {
				if(downOffset != null) {
					downOffset.set(c.downOffset);
				} else
					downOffset = new Vector2(c.downOffset);
			}
		}

		@Override
		public void update(long time) {
		}
	}

}
