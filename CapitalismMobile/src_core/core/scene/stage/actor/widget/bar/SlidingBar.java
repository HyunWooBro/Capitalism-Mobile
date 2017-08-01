package core.scene.stage.actor.widget.bar;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;

public class SlidingBar extends Bar<SlidingBar> {
	
	private static final int INVALID_POINTER_ID  = -1;
	
	private boolean mMoving;
	
	private Action mStartTouchAction;
	private Action mFinalTouchAction;
	
	private int mActivePointerID = INVALID_POINTER_ID;

	public SlidingBar(SlidingBarCostume costume) {
		super(costume);
		
		addEventListener(new TouchListener() {
			
			@Override
			public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
				if(mActivePointerID != INVALID_POINTER_ID) return false;
				mActivePointerID = event.getPointerID();
				calculateValue(x, y);
				if(mStartTouchAction != null) addAction(mStartTouchAction);
				return true;
			}
			
			@Override
			public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
				calculateValue(x, y);
				mMoving = true;
			}
			
			private void calculateValue(float x, float y) {
				float width = getWidth();
				float height = getHeight();
				
				SlidingBarCostume costume = getCostume();
				
				float padTop = 0f;
				float padLeft = 0f;
				float padRight = 0f;
				float padBottom = 0f;
				if(costume.background != null) {
					Drawable background = costume.background;
					padTop = background.getPadTop();
					padLeft = background.getPadLeft();
					padRight = background.getPadRight();
					padBottom = background.getPadBottom();
				}
				
				float value;
				if(mVertical) {
					float pureHeight = height - padTop - padBottom;

					float valuePos = y-padTop-mKnobHeight/2;
					float valueRange = pureHeight-mKnobHeight;
					
					value = mMin + (valuePos/valueRange)*(mMax-mMin);
				} else {
					float pureWidth = width - padLeft - padRight;

					float valuePos = x-padLeft-mKnobWidth/2;
					float valueRange = pureWidth-mKnobWidth;
					
					value = mMin + (valuePos/valueRange)*(mMax-mMin);
				}
				setValue(value, mDefaultAnimationDuration);
			}
			
			@Override
			public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
				mActivePointerID = INVALID_POINTER_ID;
				if(mStartTouchAction != null) mStartTouchAction.cancel();
				if(mFinalTouchAction != null) addAction(mFinalTouchAction);
				mMoving = false;
			}
		});
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new SlidingBarCostume(costume) : new SlidingBarCostume();
	}
	
	@Override
	public SlidingBarCostume getCostume() {
		return (SlidingBarCostume) mCostume;
	}
	
	@Override
	protected Drawable getKnobDrawable() {
		if(mActivePointerID != INVALID_POINTER_ID) {
			SlidingBarCostume costume = getCostume();
			if(costume.highlightedKnob != null) return costume.highlightedKnob;
		}
		return super.getKnobDrawable();
	}

	public boolean isMoving() {
		return mMoving;
	}
	
	public Action getStartTouchAction() {
		return mStartTouchAction;
	}

	public Action getFinalTouchAction() {
		return mFinalTouchAction;
	}

	public SlidingBar setStartTouchAction(Action startTouchAction) {
		mStartTouchAction = startTouchAction;
		return this;
	}

	public SlidingBar setFinalTouchAction(Action finalTouchAction) {
		mFinalTouchAction = finalTouchAction;
		return this;
	}

	public static class SlidingBarCostume extends BarCostume {
		
		/** SlidingBar을 터치 중에 강조되는 knob을 출력한다. 기본 knob과 크기가 같아야 한다. */
		public Drawable highlightedKnob;
		
		public SlidingBarCostume() {
		}
		
		public SlidingBarCostume(Costume costume) {
			super(costume);
		}
		
		@Override
		public void set(Costume costume) {
			if(!(costume instanceof SlidingBarCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			super.set(costume);
			SlidingBarCostume c = (SlidingBarCostume) costume;
			highlightedKnob = c.highlightedKnob;
		}
		
		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof SlidingBarCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			super.merge(costume);
			SlidingBarCostume c = (SlidingBarCostume) costume;
			if(c.highlightedKnob != null) highlightedKnob = c.highlightedKnob;
		}
		
		@Override
		public void update(long time) {
			super.update(time);
			if(highlightedKnob != null) highlightedKnob.update(time);
		}
	}

}
