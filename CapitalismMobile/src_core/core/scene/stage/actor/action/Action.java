package core.scene.stage.actor.action;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionEvent.ActionType;
import core.scene.stage.actor.event.ActionListener;

/**
 * 안드로이드의 Animation 클래스를 바탕으로한 Action 클래스. 전체 소스를 가져와 
 * 사용했고 일부 내용(initialize(...), cancel(), mFillEnabled, 리턴 타입 등)을 수정했다. 
 * 상속을 사용할 수도 있지만 일부 멤버를 접근할 수 있는 방법이 없어서 이런 방식을 
 * 사용한다. 한편, 불필요한 메서드는 제거하였다.</p>
 * 	<ul>
 * 		<li>getBackgroundColor()
 * 		<li>setBackgroundColor(...)
 * 		<li>getZAdjustment()
 * 		<li>setZAdjustment(...)
 * 		<li>getDetachWallpaper()
 * 		<li>setDetachWallpaper(...)
 * 		<li>getInvalidateRegion(...)
 * 		<li>initializeInvalidateRegion(...)
 * 		<li>detach()
 * 		<li>setListenerHandler(...)
 * 		<li>resolveSize(...)
 * 		<li>getTransformation(long, Form, float)
 * 		<li>getScaleFactor()
 * 		<li>startNow()
 * 		<li>isInitialized()
 * 		<li>willChangeTransformationMatrix()
 * 		<li>willChangeBounds()
 * 		<li>hasAlpha()
 * 	</ul>
 * 안드로이드 소스를 가져오게 된 계기는 intro에서 애니메이션을 사용한 경험때문이다.
 * 처음에는 overridePendingTransition(..)를 사용하려고 했지만 잘 안되서 View의 애니메이션을
 * 사용했었는데 Actor의 애니메이션을 구현하려고 하다가 생각이 났다. 그래서 참고하다가
 * 애니메이션 객체는 외부에서 정의하고 View에서는 단순히 StartAnimation(Animation)을 통해서
 * 애니메이션이 실행되는 것을 보고 이 메서드를 역추적해서 사용법을 알면 내 소스에도 바로
 * 적용하면 되겠다는 생각을 했다.</p>
 * 
 * 모든 Actor에 대해 MoveBy, SizeTo와 같은 Action을 적용할 수 있고, 다양한 Action을 한번에 적용하기 
 * 위해서는 ActionSet을 이용하면 된다. 한편, Action에 대한 리스너를 등록할 수 있다. Action 자체에 
 * 리스너를 지정할 수도 있고, Actor에 지정하여 모든 Action에 대해서도 처리할 수 있다. 일반적으로 전자는 
 * Action에 종속적인 경우, 후자는 모든 Action에 적용되는 경우에 사용한다.</p>
 * 
 * - Fill에 대한 설명
 * <ul>
 * 		<li>{@link #setFillAfter(boolean)} : 디폴트 true. false인 경우, Action의 효과를 무시하고 Action이 시작하기 
 * 			전 상황으로 돌아간다. 
 * 		<li>{@link #setFillBefore(boolean)} : 디폴트 false. true인 경우, Action이 시작하기 전부터 적용을 시작한다.
 * 		<li>{@link #setFillEnabled(boolean)} : 디폴트 true.  false인 경우, 다른 Fill의 지정된 상태를 무시하고 언제나 
 * 			Action을 적용한다. 특히, 반복을 할 때 중간의 공백을 메우기 위해 사용한다.
 * </ul>
 * 
 * @see android.view.animation.Animation
 */
public abstract class Action implements Cloneable {
	
	/**
     * Repeat the action indefinitely.
     */
    public static final int INFINITE = -1;

    /**
     * When the action reaches the end and the repeat count is INFINTE_REPEAT
     * or a positive TMP_ARRAY, the action restarts from the beginning.
     */
    public static final int RESTART = 1;

    /**
     * When the action reaches the end and the repeat count is INFINTE_REPEAT
     * or a positive TMP_ARRAY, the action plays backward (and then forward again).
     */
    public static final int REVERSE = 2;

    /**
     * Can be used as the start time to indicate the start time should be the current
     * time when {@link #act(long)} is invoked for the
     * first action frame. This can is useful for short actions.
     */
    public static final int START_ON_FIRST_FRAME = -1;
    
    /** 즉시 완료 */
    public static final int FINISH_ON_FIRST_FRAME = -2;

    /**
     * Group by {@link #act(long)} when the action ends.
     */
    boolean mEnded = false;

    /**
     * Group by {@link #act(long)} when the action starts.
     */
    boolean mStarted = false;
    
    /** 취소되었는가? */
    boolean mCanceled;
    
    /** Action을 구별하는 태그 */
    public String tag;
    
    /** Action이 적용되는 Actor */
    protected Actor<?> mActor;

    /**
     * Group by {@link #act(long)} when the action repeats
     * in REVERSE mode.
     */
    boolean mCycleFlip = false;

    /**
     * Indicates whether the action transformation should be applied before the
     * action starts. The TMP_ARRAY of this variable is only relevant if mFillEnabled is true;
     * otherwise it is assumed to be true.
     */
    boolean mFillBefore = false;

    /**
     * Indicates whether the action transformation should be applied after the
     * action ends.
     */
    boolean mFillAfter = true;

    /**
     * Indicates whether fillBefore should be taken into account.
     */
    boolean mFillEnabled = true;

    /**
     * The time in milliseconds at which the action must start;
     */
    long mStartTime = START_ON_FIRST_FRAME;

    /**
     * The delay in milliseconds after which the action must start. When the
     * start offset is > 0, the start time of the action is startTime + startOffset.
     */
    long mStartOffset;

    /**
     * The duration of one action cycle in milliseconds.
     */
    long mDuration;

    /**
     * The number of times the action must repeat. By default, an action repeats
     * indefinitely.
     */
    int mRepeatCount = 0;

    /**
     * Indicates how many times the action was repeated.
     */
    int mRepeated = 0;

    /**
     * The behavior of the action when it repeats. The repeat mode is either
     * {@link #RESTART} or {@link #REVERSE}.
     *
     */
    int mRepeatMode = RESTART;

    /**
     * The interpolator used by the action to smooth the movement.
     */
    protected Interpolator mInterpolator;

    /**
     * The action listener to be notified when the action starts, ends or repeats.
     */
    ActionListener mListener;

    private boolean mMore = true;
    //private boolean mOneMoreTime = true;
    
    private boolean mStartAfter;
    protected List<Action> mAfterActionList;

    /**
     * Creates a new action with a duration of 0ms, the default interpolator, with
     * fillBefore set to true and fillAfter set to false
     */
    public Action() {
        //ensureInterpolator();
    }

    /**
     * Reset the initialization state of this action.
     *
     * @see #initialize(Actor)
     */
    public void reset() {
		mCycleFlip = false;
		mRepeated = 0;
		mMore = true;
        //mOneMoreTime = true;
        
		// 추가
		mStarted = mEnded = false;
		mCanceled = false;
    }

    /**
     * Cancel the action. Cancelling an action invokes the action
     * listener, if set, to notify the end of the action.
     * 
     * If you cancel an action manually, you must call {@link #reset()}
     * before starting the action again.
     * 
     * @see #reset() 
     * @see #start() 
     * @see #startNow() 
     */
    public void cancel() {
    	// 추가
        mCanceled = true;
        fireActionCancel();
        
        if (mStarted && !mEnded) {
        	mEnded = true;
        	if(!mFillAfter) restore();
            fireActionEnd();
        }
        // Make sure we move the animation to the end
        //mStartTime = Long.MIN_VALUE;
        //mMore = mOneMoreTime = false;
    }

    /**
     * Initialize this action with the dimensions of the object being
     * animated as well as the objects parents. (This is to support action
     * sizes being specified relative to these dimensions.)
     *
     * <p>Objects that interpret actions should call this method when
     * the sizes of the object being animated and its parent are known, and
     * before calling {@link #getTransformation}.
     * 
     * 
     * @param width Width of the object being animated
     * @param height Height of the object being animated
     * @param parentWidth Width of the animated object's parent
     * @param parentHeight Height of the animated object's parent
     */
    protected void initialize() {
    }

    /**
     * Sets the acceleration curve for this action. The interpolator is loaded as
     * a resource from the specified context.
     *
     * @param context The application environment
     * @param resID The resource identifier of the interpolator to load
     * @attr ref android.R.styleable#action_interpolator
     */
    public Action setInterpolator(Context context, int resID) {
        setInterpolator(AnimationUtils.loadInterpolator(context, resID));
        return this;
    }

    /**
     * Sets the acceleration curve for this action. Defaults to a linear
     * interpolation.
     *
     * @param i The interpolator which defines the acceleration curve
     * @attr ref android.R.styleable#action_interpolator
     */
    public Action setInterpolator(Interpolator i) {
        mInterpolator = i;
        return this;
    }

    /**
     * When this action should start relative to the start time. This is most
     * useful when composing complex actions using an {@link actionSet }
     * where some of the actions components start at different times.
     *
     * @param startOffset When this action should start, in milliseconds from
     *                    the start time of the root actionSet.
     * @attr ref android.R.styleable#action_startOffset
     */
    public Action setStartOffset(long startOffset) {
        mStartOffset = startOffset;
        return this;
    }

    /**
     * How long this action should last. The duration cannot be negative.
     * 
     * @param durationMillis Duration in milliseconds
     *
     * @throws java.lang.IllegalArgumentException if the duration is < 0
     *
     * @attr ref android.R.styleable#action_duration
     */
    public Action setDuration(long durationMillis) {
        if (durationMillis < 0) {
            throw new IllegalArgumentException("action duration cannot be negative");
        }
        mDuration = durationMillis;
        return this;
    }

    /**
     * Ensure that the duration that this action will run is not longer
     * than <var>durationMillis</var>.  In addition to adjusting the duration
     * itself, this ensures that the repeat count also will not make it run
     * longer than the given time.
     * 
     * @param durationMillis The maximum duration the action is allowed
     * to run.
     */
    public Action restrictDuration(long durationMillis) {
        // If we start after the duration, then we just won't run.
        if (mStartOffset > durationMillis) {
            mStartOffset = durationMillis;
            mDuration = 0;
            mRepeatCount = 0;
            return this;
        }
        
        long dur = mDuration + mStartOffset;
        if (dur > durationMillis) {
            mDuration = durationMillis-mStartOffset;
            dur = durationMillis;
        }
        // If the duration is 0 or less, then we won't run.
        if (mDuration <= 0) {
            mDuration = 0;
            mRepeatCount = 0;
            return this;
        }
        // Reduce the number of repeats to keep below the maximum duration.
        // The comparison between mRepeatCount and duration is to catch
        // overflows after multiplying them.
        if (mRepeatCount < 0 || mRepeatCount > durationMillis
                || (dur*mRepeatCount) > durationMillis) {
            // Figure out how many times to do the action.  Subtract 1 since
            // repeat count is the number of times to repeat so 0 runs once.
            mRepeatCount = (int)(durationMillis/dur) - 1;
            if (mRepeatCount < 0) {
                mRepeatCount = 0;
            }
        }
        return this;
    }
    
    /**
     * How much to scale the duration by.
     *
     * @param scale The amount to scale the duration.
     */
    public Action scaleCurrentDuration(float scale) {
        mDuration = (long) (mDuration * scale);
        mStartOffset = (long) (mStartOffset * scale);
        return this;
    }

    /**
     * When this action should start. When the start time is set to
     * {@link #START_ON_FIRST_FRAME}, the action will start the first time
     * {@link #act(long)} is invoked. The time passed
     * to this method should be obtained by calling
     * {@link actionUtils#currentactionTimeMillis()} instead of
     * {@link System#currentTimeMillis()}.
     *
     * @param startTimeMillis the start time in milliseconds
     */
    public Action setStartTime(long startTimeMillis) {
        mStartTime = startTimeMillis;
		//mStarted = mEnded = false;
		//mCycleFlip = false;
		//mRepeated = 0;
		//mMore = true;
        return this;
    }

    /**
     * Convenience method to start the action the first time
     * {@link #act(long)} is invoked.
     */
    public void start() {
        setStartTime(START_ON_FIRST_FRAME);
    }
    
    /** Action을 재시작한다. 기본적으로 reset()과 start()를 호출한다. */
    public void restart() {
    	reset();
    	start();
    }
    
    /** 즉시 완료한다. */
    public void finish() {
    	setStartTime(FINISH_ON_FIRST_FRAME);
    }

    /**
     * Defines what this action should do when it reaches the end. This
     * setting is applied only when the repeat count is either greater than
     * 0 or {@link #INFINITE}. Defaults to {@link #RESTART}. 
     *
     * @param repeatMode {@link #RESTART} or {@link #REVERSE}
     * @attr ref android.R.styleable#action_repeatMode
     */
    public Action setRepeatMode(int repeatMode) {
        mRepeatMode = repeatMode;
        return this;
    }

    /**
     * Sets how many times the action should be repeated. If the repeat
     * count is 0, the action is never repeated. If the repeat count is
     * greater than 0 or {@link #INFINITE}, the repeat mode will be taken
     * into account. The repeat count is 0 by default.
     *
     * @param repeatCount the number of times the action should be repeated
     * @attr ref android.R.styleable#action_repeatCount
     */
    public Action setRepeatCount(int repeatCount) {
        if (repeatCount < 0) {
            repeatCount = INFINITE;
        }
        mRepeatCount = repeatCount;
        return this;
    }

    /**
     * If fillEnabled is true, this action will apply the TMP_ARRAY of fillBefore.
     *
     * @return true if the action will take fillBefore into account
     * @attr ref android.R.styleable#action_fillEnabled
     */
    public boolean isFillEnabled() {
        return mFillEnabled;
    }

    /**
     * If fillEnabled is true, the action will apply the TMP_ARRAY of fillBefore.
     * Otherwise, fillBefore is ignored and the action
     * transformation is always applied until the action ends.
     *
     * @param fillEnabled true if the action should take the TMP_ARRAY of fillBefore into account
     * @attr ref android.R.styleable#action_fillEnabled
     *
     * @see #setFillBefore(boolean)
     * @see #setFillAfter(boolean)
     */
    public Action setFillEnabled(boolean fillEnabled) {
        mFillEnabled = fillEnabled;
        return this;
    }

    /**
     * If fillBefore is true, this action will apply its transformation
     * before the start time of the action. Defaults to true if
     * {@link #setFillEnabled(boolean)} is not set to true.
     * Note that this applies when using an {@link
     * android.view.action.actionSet actionSet} to chain
     * actions. The transformation is not applied before the actionSet
     * itself starts.
     *
     * @param fillBefore true if the action should apply its transformation before it starts
     * @attr ref android.R.styleable#action_fillBefore
     *
     * @see #setFillEnabled(boolean)
     */
    public Action setFillBefore(boolean fillBefore) {
        mFillBefore = fillBefore;
        return this;
    }

    /**
     * If fillAfter is true, the transformation that this action performed
     * will persist when it is finished. Defaults to false if not set.
     * Note that this applies to individual actions and when using an {@link
     * android.view.action.actionSet actionSet} to chain
     * actions.
     *
     * @param fillAfter true if the action should apply its transformation after it ends
     * @attr ref android.R.styleable#action_fillAfter
     *
     * @see #setFillEnabled(boolean) 
     */
    public Action setFillAfter(boolean fillAfter) {
        mFillAfter = fillAfter;
        return this;
    }

    /**
     * Gets the acceleration curve clazz for this action.
     *
     * @return the {@link Interpolator} associated to this action
     * @attr ref android.R.styleable#action_interpolator
     */
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * When this action should start. If the action has not startet yet,
     * this method might return {@link #START_ON_FIRST_FRAME}.
     *
     * @return the time in milliseconds when the action should start or
     *         {@link #START_ON_FIRST_FRAME}
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * How long this action should last
     *
     * @return the duration in milliseconds of the action
     * @attr ref android.R.styleable#action_duration
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * When this action should start, relative to StartTime
     *
     * @return the start offset in milliseconds
     * @attr ref android.R.styleable#action_startOffset
     */
    public long getStartOffset() {
        return mStartOffset;
    }

    /**
     * Defines what this action should do when it reaches the end.
     *
     * @return either one of {@link #REVERSE} or {@link #RESTART}
     * @attr ref android.R.styleable#action_repeatMode
     */
    public int getRepeatMode() {
        return mRepeatMode;
    }

    /**
     * Defines how many times the action should repeat. The default TMP_ARRAY
     * is 0.
     *
     * @return the number of times the action should repeat, or {@link #INFINITE}
     * @attr ref android.R.styleable#action_repeatCount
     */
    public int getRepeatCount() {
        return mRepeatCount;
    }

    /**
     * If fillBefore is true, this action will apply its transformation
     * before the start time of the action. If fillBefore is false and
     * {@link #isFillEnabled() fillEnabled} is true, the transformation will not be applied until
     * the start time of the action.
     *
     * @return true if the action applies its transformation before it starts
     * @attr ref android.R.styleable#action_fillBefore
     */
    public boolean getFillBefore() {
        return mFillBefore;
    }

    /**
     * If fillAfter is true, this action will apply its transformation
     * after the end time of the action.
     *
     * @return true if the action applies its transformation after it ends
     * @attr ref android.R.styleable#action_fillAfter
     */
    public boolean getFillAfter() {
        return mFillAfter;
    }

    /**
     * <p>Binds an action listener to this action. The action listener
     * is notified of action events such as the end of the action or the
     * repetition of the action.</p>
     *
     * @param listener the action listener to be notified
     */
    public Action setActionListener(ActionListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * Gurantees that this action has an interpolator. Will use
     * a AccelerateDecelerateInterpolator is nothing else was specified.
     */
    protected void ensureInterpolator() {
        if (mInterpolator == null) {
            mInterpolator = new AccelerateDecelerateInterpolator();
        }
    }

    /**
     * Compute a hint at how long the entire action may last, in milliseconds.
     * actions can be written to cause themselves to run for a different
     * duration than what is computed here, but generally this should be
     * accurate.
     */
    public long computeDurationHint() {
        return (getStartOffset() + getDuration()) * (getRepeatCount() + 1);
    }
    
    protected float getInterpolatedTime(float normalizedTime) {
    	if(mInterpolator != null) return mInterpolator.getInterpolation(normalizedTime);
    	return normalizedTime;
    }
    
    /**
     * Gets the transformation to apply at a specified point in time. Implementations of this
     * method should always replace the specified Form or document they are doing
     * otherwise.
     *
     * @param currentTime Where we are in the action. This is wall clock time.
     * @return True if the action is still running
     */
    public boolean act(long currentTime) {
		if(mEnded || mCanceled) return false;
    	if(!canStartAfter()) return true;
    	
        if (mStartTime == START_ON_FIRST_FRAME) {
            mStartTime = currentTime;
        }
        
        if (mStartTime == FINISH_ON_FIRST_FRAME) {
            mStartTime = currentTime - mStartOffset - mDuration;
        }

        final long startOffset = mStartOffset;
        final long duration = mDuration;
        float normalizedTime;
        if (duration != 0) {
            normalizedTime = ((float) (currentTime - (mStartTime + startOffset))) /
                    (float) duration;
        } else {
            // time is a step-change with a zero duration
        	// 기존 안드로이드 Animation 클래스에서는 startOffset이 빠졌는데 
        	// 버그로 보인다. 그리고 하한값 0f를 -1f로 수정한다.
            normalizedTime = (currentTime < mStartTime + startOffset)? -1.0f : 1.0f;
        }

        final boolean expired = normalizedTime >= 1.0f;
        mMore = !expired;

        if (!mFillEnabled) normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);

        if ((normalizedTime >= 0.0f || mFillBefore) && (normalizedTime <= 1.0f || mFillAfter)) {
            if (!mStarted) {
            	fireActionStart();
            	initialize();
                mStarted = true;
            }

            if (mFillEnabled) normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);

            if (mCycleFlip) {
                normalizedTime = 1.0f - normalizedTime;
            }

            final float interpolatedTime = getInterpolatedTime(normalizedTime);
            apply(interpolatedTime);
        }

        if (expired) {
            if (mRepeatCount == mRepeated) {
                if (!mEnded) {
                    mEnded = true;
                    if(!mFillAfter) restore();
                    fireActionEnd();
                }
            } else {
                if (mRepeatCount > 0) {
                    mRepeated++;
                }

                if (mRepeatMode == REVERSE) {
                    mCycleFlip = !mCycleFlip;
                }

                mStartTime = START_ON_FIRST_FRAME;
                mMore = true;

                fireActionRepeat();
            }
        }
        
        /*if (!mMore && mOneMoreTime) {
            mOneMoreTime = false;
            return true;
        }*/

        return mMore;
    }
    
    protected boolean canStartAfter() {
    	if(!mStartAfter) return true;
    	
    	List<Action> afterActionList = mAfterActionList;
		int n = afterActionList.size();
		for(int i=n-1; i>-1; i--) {
			Action action = afterActionList.get(i);
			int index = mActor.getActionList().indexOf(action);
			if(index == -1) afterActionList.remove(i);
		}
		return (afterActionList.isEmpty())? true : false;
    }
    
	/**
	 * Helper for getTransformation. Subclasses should implement this to apply
	 * their transforms given an interpolation TMP_ARRAY.  Implementations of this
	 * method should always replace the specified Form or document
	 * they are doing otherwise.
	 * 
	 * @param interpolatedTime The TMP_ARRAY of the normalized time (0.0 to 1.0)
	 *        after it has been run through the interpolation function.
	 */
	protected void apply(float interpolatedTime) {
	}
    
    protected void restore() {
    }

    /*package*/ void fireActionStart() {
    	ActionEvent e = new ActionEvent(ActionType.ACTION_START, this);
    	if(mActor != null) mActor.fire(e);
        if(mListener != null) mListener.onStart(null, this, mActor);
    }

    /*package*/ void fireActionRepeat() {
    	ActionEvent e = new ActionEvent(ActionType.ACTION_REPEAT, this);
    	if(mActor != null) mActor.fire(e);
        if(mListener != null) mListener.onRepeat(null, this, mActor);
    }

    /*package*/ void fireActionEnd() {
    	ActionEvent e = new ActionEvent(ActionType.ACTION_END, this);
    	if(mActor != null) mActor.fire(e);
        if(mListener != null) mListener.onEnd(null, this, mActor);
    }
    
    /*package*/ void fireActionCancel() {
    	ActionEvent e = new ActionEvent(ActionType.ACTION_CANCEL, this);
    	if(mActor != null) mActor.fire(e);
        if(mListener != null) mListener.onCancel(null, this, mActor);
    }

    /**
     * <p>Indicates whether this action has started or not.</p>
     *
     * @return true if the action has started, false otherwise
     */
    public boolean hasStarted() {
        return mStarted;
    }

    /**
     * <p>Indicates whether this action has ended or not.</p>
     *
     * @return true if the action has ended, false otherwise
     */
    public boolean hasEnded() {
        return mEnded;
    }
    
    public boolean isCanceled() {
        return mCanceled;
    }
    
    /** Action을 구분하기 위한 mTag 지정 */
    public Action setTag(String tag) {
    	this.tag = tag;
    	return this;
    }

	public Actor<?> getActor() {
		return mActor;
	}
	
	/** 
	 * Action이 수행되는 대상 Actor을 지정한다. Action이 Actor에 삽입되거나 제거되는 순간에 
	 * 시스템에 의해 호출되므로 유저가 호출할 경우는 없다.
	 */
	public Action setActor(Actor<?> actor) {
		if(actor != null && mStartAfter) {
			List<Action> actionList = actor.getActionList();
			if(mAfterActionList == null) mAfterActionList = new ArrayList<Action>();
			mAfterActionList.clear();
			mAfterActionList.addAll(actionList);
		}
		mActor = actor;
		return this;
	}
	
	public boolean willStartAfter() {
		return mStartAfter;
	}

	public Action setStartAfter(boolean startAfter) {
		mStartAfter = startAfter;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(getActionName());
		builder.append(">");
		builder.append(" [");
		if(mActor != null) builder.append("mActor=").append(mActor.getActorName()).append(", ");
		builder.append("mStartTime=").append(mStartTime)
				.append(", mStartOffset=").append(mStartOffset)
				.append(", mDuration=").append(mDuration)
				.append(", mRepeatCount=").append(mRepeatCount)
				.append(", mRepeatMode=").append(mRepeatMode).append("]");
		return builder.toString();
	}
	
	public String getActionName() {
		String name = getClass().getSimpleName();
		if(name.isEmpty()) name = getClass().getSuperclass().getSimpleName() + "[anonymous]";
		return name;
	}

}
