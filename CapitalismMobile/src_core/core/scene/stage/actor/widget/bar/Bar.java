package core.scene.stage.actor.widget.bar;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.math.MathUtils;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.widget.Widget;
import core.utils.pool.Pools;

@SuppressWarnings("unchecked")
public abstract class Bar<T extends Bar<T>> extends Widget<T> {
	
	public static final float DEFAULT_VERTICAL_PREF_WIDTH = 40f;
	public static final float DEFAULT_VERTICAL_PREF_HEIGHT = 160f;
	public static final float DEFAULT_HORIZONTAL_PREF_WIDTH = 160f;
	public static final float DEFAULT_HORIZONTAL_PREF_HEIGHT = 40f;
	
	protected float mMax = 100f;
	protected float mMin = 0f;
	protected float mStep = 1f;
	
	protected float mAnimationStartValue;
	protected float mValue;
	
	protected boolean mVertical;
	
	protected Interpolator mInterpolator;
	
	protected long mAnimationTimer;
	protected long mAnimationDuration;
	
	protected long mDefaultAnimationDuration;
	
	protected float mBackgroundWidth;
	protected float mBackgroundHeight;
	protected float mBackgroundOffsetX;
	protected float mBackgroundOffsetY;
	
	protected float mKnobWidth;
	protected float mKnobHeight;
	protected float mKnobOffsetX;
	protected float mKnobOffsetY;
	
	protected float mProgressWidth;
	protected float mProgressHeight;
	protected float mProgressOffsetX;
	protected float mProgressOffsetY;
	
	private boolean mDisableSizeChanged;
	
	/** 비활성 상태 */
	protected boolean mDisabled;
	
	public Bar(BarCostume costume) {
		super(costume);
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new BarCostume(costume) : new BarCostume();
	}

	/** 수정한 Drawable이 사이즈에 반영되길 원한다면 {@link #pack()}을 호출해야 한다. */
	@Override
	public BarCostume getCostume() {
		return (BarCostume) mCostume;
	}
	
	@Override
	public void costumeChanged() {
		invalidateHierarchy();
		pack();
	}
	
	@Override
	public void layout() {
		
		float width = getWidth();
		float height = getHeight();
		
		float prefWidth = getPrefWidth();
		float prefHeight = getPrefHeight();
		
		float widthScale = width / prefWidth;
		float heightScale = height / prefHeight;
		
		BarCostume costume = getCostume();
		
		if(costume.background != null) {
			mBackgroundWidth = costume.background.getWidth() * widthScale;
			mBackgroundHeight = costume.background.getHeight() * heightScale;
			mBackgroundOffsetX = (width-mBackgroundWidth)/2;
			mBackgroundOffsetY = (height-mBackgroundHeight)/2;
		}
		
		if(costume.knob != null) {
			mKnobWidth = costume.knob.getWidth() * widthScale;
			mKnobHeight = costume.knob.getHeight() * heightScale;
			mKnobOffsetX = (width-mKnobWidth)/2;
			mKnobOffsetY = (height-mKnobHeight)/2;
		}
		
		if(costume.progress != null) {
			mProgressWidth = costume.progress.getWidth() * widthScale;
			mProgressHeight = costume.progress.getHeight() * heightScale;
			mProgressOffsetY = (width-mProgressWidth)/2;
			mProgressOffsetY = (height-mProgressHeight)/2;
		}
	}
	
	@Override
	public float getMinWidth() {
		return getPrefWidth()/2;
	}
	
	@Override
	public float getMinHeight() {
		return getPrefHeight()/2;
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		BarCostume costume = getCostume();
		float width = 0f;
		if(costume.background != null) {
			width = costume.background.getWidth();
		} else {
			if(mVertical)	width = DEFAULT_VERTICAL_PREF_WIDTH;
			else				width = DEFAULT_HORIZONTAL_PREF_WIDTH;
		}
		if(mVertical) {
			if(costume.knob != null) width = Math.max(width, costume.knob.getWidth());
		} else
			width = Math.max(width, getPrefHeight()*4);
		return width;
	}

	@Override
	protected float getDefaultPrefHeight() {
		BarCostume costume = getCostume();
		float height = 0f;
		if(costume.background != null) {
			height = costume.background.getHeight();
		} else {
			if(mVertical)	height = DEFAULT_VERTICAL_PREF_HEIGHT;
			else				height = DEFAULT_HORIZONTAL_PREF_HEIGHT;
		}
		if(!mVertical) {
			if(costume.knob != null) height = Math.max(height, costume.knob.getHeight());
		} else
			height = Math.max(height, getPrefWidth()*4);
		return height;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		Drawable background = getBackgroundDrawable();
		Drawable knob = getKnobDrawable();
		Drawable progress = getProgressDrawable();
		
		if(background == null && knob == null && progress == null) return;
		
		validate();
		
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		
		float pivotX = getPivotX();
		float pivotY = getPivotY();
		float barPivotX = width * pivotX;
		float barPivotY = height * pivotY;
		
		float padTop = 0f;
		float padLeft = 0f;
		float padRight = 0f;
		float padBottom = 0f;
		if(background != null) {
			padTop = background.getPadTop();
			padLeft = background.getPadLeft();
			padRight = background.getPadRight();
			padBottom = background.getPadBottom();
		}
		
		float value = getVisualValue();
		
		mDisableSizeChanged = true;
		
		if(mVertical) {
			
			float pureY = y + padTop;
			float pureHeight = height - padTop - padBottom;
			
			float knobHeight = 0f;
			if(knob != null) knobHeight = mKnobHeight;
			float valuePos = (pureHeight-knobHeight) * (value-mMin)/(mMax-mMin);
			
			mDrawable = background;
			if(mDrawable != null) {
				moveTo(x+mBackgroundOffsetX, y);
				sizeTo(mBackgroundWidth, height);
				setPivotX((barPivotX-mBackgroundOffsetX)/mBackgroundWidth);
				drawSelf(batch, parentAlpha);
			}
			
			mDrawable = progress;
			if(mDrawable != null) {
				moveTo(x+mProgressOffsetX, pureY);
				sizeTo(mProgressWidth, valuePos+knobHeight/2);
				pivotTo((barPivotX-mProgressOffsetX)/mProgressWidth, (barPivotY-padTop)/(valuePos+knobHeight/2));
				drawSelf(batch, parentAlpha);
			}
			
			mDrawable = getKnobDrawable();
			if(mDrawable != null) {
				float knobDeltaY = padTop+valuePos;
				moveTo(x+mKnobOffsetX, y+knobDeltaY);
				sizeTo(mKnobWidth, knobHeight);
				pivotTo((barPivotX-mKnobOffsetX)/mKnobWidth, (barPivotY-knobDeltaY)/knobHeight);
				drawSelf(batch, parentAlpha);
			}
		} else { // horizontal
			
			float pureX = x + padLeft;
			float pureWidth = width - padLeft - padRight;
			
			float knobWidth = 0f;
			if(knob != null) knobWidth = mKnobWidth;
			float valuePos = (pureWidth-knobWidth) * (value-mMin)/(mMax-mMin);
			
			mDrawable = background;
			if(mDrawable != null) {
				moveTo(x, y+mBackgroundOffsetY);
				sizeTo(width, mBackgroundHeight);
				setPivotY((barPivotY-mBackgroundOffsetY)/mBackgroundHeight);
				drawSelf(batch, parentAlpha);
			}
			
			mDrawable = progress;
			if(mDrawable != null) {
				moveTo(pureX, y+mProgressOffsetY);
				sizeTo(valuePos+knobWidth/2, mProgressHeight);
				pivotTo((barPivotX-padLeft)/(valuePos+knobWidth/2), (barPivotY-mProgressOffsetY)/mProgressHeight);
				drawSelf(batch, parentAlpha);
			}
			
			mDrawable = knob;
			if(mDrawable != null) {
				float knobDeltaX = padLeft+valuePos;
				moveTo(x+knobDeltaX, y+mKnobOffsetY);
				sizeTo(knobWidth, mKnobHeight);
				pivotTo((barPivotX-knobDeltaX)/knobWidth, (barPivotY-mKnobOffsetY)/mKnobHeight);
				drawSelf(batch, parentAlpha);
			}
		}

		moveTo(x, y);
		sizeTo(width, height);
		pivotTo(pivotX, pivotY);
		
		mDisableSizeChanged = false;
	}
	
	protected Drawable getBackgroundDrawable() {
		return (mDisabled)? getCostume().disabledBackground : getCostume().background;
	}
	
	protected Drawable getProgressDrawable() {
		return (mDisabled)? getCostume().disabledProgress : getCostume().progress;
	}
	
	protected Drawable getKnobDrawable() {
		return (mDisabled)? getCostume().disabledKnob : getCostume().knob;
	}

	@Override
	public Actor<?> contact(float x, float y) {
		if(mDisabled) return null;
		return super.contact(x, y);
	}
	
	@Override
	protected void onSizeChanged() {
		if(mDisableSizeChanged) return;
		super.onSizeChanged();
	}
	
	public float getVisualValue() {
		if(mAnimationTimer > 0) {
			mAnimationTimer -= Core.GRAPHICS.getDeltaTime();
			
			float normalizedTime = 1f - (float) mAnimationTimer/mAnimationDuration;
			float interpolatedTime = getInterpolatedTime(normalizedTime);
			
			return mAnimationStartValue*(1f - interpolatedTime) + mValue*interpolatedTime;
		}
		return mValue;
	}

	private float getInterpolatedTime(float normalizedTime) {
		if(mInterpolator == null) mInterpolator = new LinearInterpolator();
    	return mInterpolator.getInterpolation(normalizedTime);
    }

	public float getValue() {
		return mValue;
	}
	
	public T setValue(float value) {
		return setValue(value, mDefaultAnimationDuration);
	}
	
	public T setValue(float value, long animationDuration) {
		float roundedValue = mMin + Math.round((value-mMin)/mStep)*mStep;
		float clampedValue = MathUtils.clamp(roundedValue, mMin, mMax);
		if(mValue != clampedValue) {
			float oldValue = mValue;
			float oldVisualValue = getVisualValue();
			mValue = clampedValue;
			onValueChanged(oldValue, oldVisualValue, animationDuration);
		}
		return (T) this;
	}
	
	protected boolean onValueChanged(float oldValue, float oldVisualValue, long animationDuration) {
		boolean changed = true;
		ChangeEvent event = Pools.obtain(ChangeEvent.class);
		if(fire(event)) {
			if(animationDuration > 0) {
				mAnimationStartValue = oldVisualValue;
				mAnimationDuration = animationDuration;
				mAnimationTimer = animationDuration;
			}
		} else {
			mValue = oldValue;
			changed = false;
		}
		Pools.recycle(event);
		return changed;
	}
	
	public T setRange(float min, float max) {
		if(min > max) throw new IllegalArgumentException("max can't be smaller than min.");
		mMax = max;
		mMin = min;
		mValue = MathUtils.clamp(mValue, min, max);
		return (T) this;
	}
	
	public T setStep(float step) {
		if(step <= 0) throw new IllegalArgumentException("step can't be equal to or less than 0.");
		mStep = step;
		return (T) this;
	}

	public boolean isVertical() {
		return mVertical;
	}

	public T setVertical(boolean vertical) {
		mVertical = vertical;
		return (T) this;
	}

	public long getDefaultAnimationDuration() {
		return mDefaultAnimationDuration;
	}

	public T setDefaultAnimationDuration(long duration) {
		mDefaultAnimationDuration = duration;
		return (T) this;
	}

	public Interpolator getInterpolator() {
		return mInterpolator;
	}

	public T setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
		return (T) this;
	}

	public float getMax() {
		return mMax;
	}

	public float getMin() {
		return mMin;
	}

	public float getStep() {
		return mStep;
	}

	public T setMax(float max) {
		return setRange(mMin, max);
	}

	public T setMin(float min) {
		return setRange(min, mMax);
	}
	
	public boolean isDisabled() {
		return mDisabled;
	}

	public T setDisabled(boolean disabled) {
		mDisabled = disabled;
		return (T) this;
	}

	public static class BarCostume implements Costume {
		
		/** 바탕 Drawable */
		public Drawable background;
		public Drawable knob;
		public Drawable progress;
		
		public Drawable disabledBackground;
		public Drawable disabledKnob;
		public Drawable disabledProgress;
		
		public BarCostume() {
		}
		
		public BarCostume(Costume costume) {
			set(costume);
		}

		@Override
		public void set(Costume costume) {
			if(!(costume instanceof BarCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			BarCostume c = (BarCostume) costume;
			background = c.background;
			knob = c.knob;
			progress = c.progress;
			disabledBackground = (c.disabledBackground == null)? c.background : c.disabledBackground;
			disabledKnob = (c.disabledKnob == null)? c.knob : c.disabledKnob;
			disabledProgress = (c.disabledProgress == null)? c.progress : c.disabledProgress;
		}

		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof BarCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			BarCostume c = (BarCostume) costume;
			if(c.background != null) background = c.background;
			if(c.knob != null) knob = c.knob;
			if(c.progress != null) progress = c.progress;
			if(c.disabledBackground != null) disabledBackground = c.disabledBackground;
			if(c.disabledKnob != null) disabledKnob = c.disabledKnob;
			if(c.disabledProgress != null) disabledProgress = c.disabledProgress;
		}

		@Override
		public void update(long time) {
			if(background != null) background.update(time);
			if(knob != null) knob.update(time);
			if(progress != null) progress.update(time);
			if(disabledBackground != null) disabledBackground.update(time);
			if(disabledKnob != null) disabledKnob.update(time);
			if(disabledProgress != null) disabledProgress.update(time);
		}
		
	}

}
