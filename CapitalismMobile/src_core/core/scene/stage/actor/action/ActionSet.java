package core.scene.stage.actor.action;

import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.view.animation.Interpolator;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.ActionListener;
import core.utils.SnapshotArrayList;

/**
 * 안드로이드의 AnimationSet 클래스를 바탕으로한 ActionSet 클래스. 대부분의 소스를
 * 가져와 사용했고 일부 내용(initialize(...), mHasColor 등)을 수정했다. 상속을 사용할 수도 있지만 일부 멤버를
 * 접근할 수 있는 방법이 없어서 이런 방식을 사용한다. 한편, 불필요한 메서드는 제거하였다.</p>
 * <ul>
 * 		<li>initializeInvalidateRegion(...)</li>
 * 	</ul>
 * 
 * Represents a group of Actions that should be played together.
 * The transformation of each individual action are composed 
 * together into a single transform. 
 * If ActionSet sets any properties that its children also set
 * (for example, duration or fillBefore), the values of ActionSet
 * override the child values.
 *
 * <p>The way that ActionSet inherits behavior from Action is important to
 * understand. Some of the Action attributes applied to ActionSet affect the
 * ActionSet itself, some are pushed down to the children, and some are ignored,
 * as follows:
 * <ul>
 *     <li>duration, repeatMode, fillBefore, fillAfter: These properties, when set
 *     on an AnimationSet object, will be pushed down to all child `animations.</li>
 *     <li>repeatCount, fillEnabled: These properties are ignored for AnimationSet.</li>
 *     <li>startOffset, shareInterpolator: These properties apply to the AnimationSet itself.</li>
 * </ul>
 * Starting with {@link android.os.Build.VERSION_CODES#ICE_CREAM_SANDWICH},
 * the behavior of these properties is the same in XML resources and at runtime (prior to that
 * release, the values set in XML were ignored for ActionSet). That is, calling
 * <code>setDuration(500)</code> on an ActionSet has the same effect as declaring
 * <code>android:duration="500"</code> in an XML resource for an ActionSet object.</p>
 *
 * 
 * @see android.view.animation.AnimationSet
 *
 */
public class ActionSet extends Action {	
	private static final int PROPERTY_FILL_AFTER_MASK         = 0x1;
    private static final int PROPERTY_FILL_BEFORE_MASK        = 0x2;
    private static final int PROPERTY_REPEAT_MODE_MASK        = 0x4;
    private static final int PROPERTY_START_OFFSET_MASK       = 0x8;
    private static final int PROPERTY_SHARE_INTERPOLATOR_MASK = 0x10;
    private static final int PROPERTY_DURATION_MASK           = 0x20;

    private int mFlags = 0;
    //private boolean mDirty;
    //private boolean mHasAlpha;

    private SnapshotArrayList<Action> mActions = new SnapshotArrayList<Action>();

    //private Form mTempTransformation = new Form();

    //private long mLastEnd;

    private long[] mStoredOffsets;
    
    private boolean mInitialized;
    
    /** 순차적으로 처리할 것인가? */
    private boolean mSequence;
    
    /**
     * Constructor to use when building an ActionSet from code
     * 
     * @param shareInterpolator Pass true if all of the actions in this set
     *        should use the interpolator assocciated with this ActionSet.
     *        Pass false if each action should use its own interpolator.
     */
    public ActionSet(boolean sequence) {
		this(sequence, false);
    }
    
    public ActionSet(boolean sequence, boolean shareInterpolator) {
    	mSequence = sequence;
        setFlag(PROPERTY_SHARE_INTERPOLATOR_MASK, shareInterpolator);
        //init();
    }

    private void setFlag(int mask, boolean value) {
        if (value) {
            mFlags |= mask;
        } else {
            mFlags &= ~mask;
        }
    }

    /*private void init() {
        mStartTime = 0;
    }*/

    @Override
    public ActionSet setFillAfter(boolean fillAfter) {
    	if(mInitialized) throw new IllegalStateException("This method can only be invoked before initialize()");
    	
        mFlags |= PROPERTY_FILL_AFTER_MASK;
        super.setFillAfter(fillAfter);
        return this;
    }

    @Override
    public ActionSet setFillBefore(boolean fillBefore) {
    	if(mInitialized) throw new IllegalStateException("This method can only be invoked before initialize()");
    	
        mFlags |= PROPERTY_FILL_BEFORE_MASK;
        super.setFillBefore(fillBefore);
        return this;
    }

    @Override
    public ActionSet setRepeatMode(int repeatMode) {
    	if(mInitialized) throw new IllegalStateException("This method can only be invoked before initialize()");
    	
        mFlags |= PROPERTY_REPEAT_MODE_MASK;
        super.setRepeatMode(repeatMode);
        return this;
    }

    @Override
    public ActionSet setStartOffset(long startOffset) {
    	if(mInitialized) throw new IllegalStateException("This method can only be invoked before initialize()");
    	
        mFlags |= PROPERTY_START_OFFSET_MASK;
        super.setStartOffset(startOffset);
        return this;
    }

    /**
     * <p>Sets the duration of every child action.</p>
     *
     * @param durationMillis the duration of the action, in milliseconds, for
     *        every child in this set
     */
    @Override
    public ActionSet setDuration(long durationMillis) {
    	if(mInitialized) throw new IllegalStateException("This method can only be invoked before initialize()");
    	
        mFlags |= PROPERTY_DURATION_MASK;
        super.setDuration(durationMillis);
        //mLastEnd = mStartOffset + mDuration;
        return this;
    }

    /**
     * Add a child action to this action set.
     * The transforms of the child actions are applied in the order
     * that they were added
     * @param a Action to add.
     */
    public ActionSet addAction(Action a) {
    	if(mInitialized) throw new IllegalStateException("This method can only be invoked before initialize()");
    	
        mActions.add(a);

        /*if ((mFlags & PROPERTY_DURATION_MASK) == PROPERTY_DURATION_MASK) {
            mLastEnd = mStartOffset + mDuration;
        } else {
            if (mActions.size() == 1) {
                mDuration = a.getStartOffset() + a.getDuration();
                mLastEnd = mStartOffset + mDuration;
            } else {
                mLastEnd = Math.max(mLastEnd, a.getStartOffset() + a.getDuration());
                mDuration = mLastEnd - mStartOffset;
            }
        }*/

        //mDirty = true;
        return this;
    }
    
    /**
     * Sets the start time of this action and all child actions
     * @return 
     * 
     * @see android.view.animation.Animation#setStartTime(long)
     */
    @Override
    public ActionSet setStartTime(long startTimeMillis) {
        //super.setStartTime(startTimeMillis);

        final int count = mActions.size();
        final List<Action> actions = mActions;

        for (int i = 0; i < count; i++) {
        	Action a = actions.get(i);
            a.setStartTime(startTimeMillis);
        }
        return this;
    }

    @Override
    public long getStartTime() {
        long startTime = Long.MAX_VALUE;

        final int count = mActions.size();
        final List<Action> actions = mActions;

        for (int i = 0; i < count; i++) {
        	Action a = actions.get(i);
            startTime = Math.min(startTime, a.getStartTime());
        }

        return startTime;
    }

    @Override
    public ActionSet restrictDuration(long durationMillis) {
        //super.restrictDuration(durationMillis);

        final List<Action> actions = mActions;
        int count = actions.size();

        for (int i = 0; i < count; i++) {
            actions.get(i).restrictDuration(durationMillis);
        }
        return this;
    }
    
    /**
     * The duration of an ActionSet is defined to be the 
     * duration of the longest child action.
     * 
     * @see android.view.animation.Animation#getDuration()
     */
    @Override
    public long getDuration() {
        final List<Action> actions = mActions;
        final int count = actions.size();
        long duration = 0;

        boolean durationSet = (mFlags & PROPERTY_DURATION_MASK) == PROPERTY_DURATION_MASK;
        if(mSequence) {
        	if (durationSet) {
	            duration = mDuration * count;
	        } else {
	            for (int i = 0; i < count; i++) {
	                duration +=  actions.get(i).getDuration();
	            }
	        }
        } else {
	        if (durationSet) {
	            duration = mDuration;
	        } else {
	            for (int i = 0; i < count; i++) {
	                duration = Math.max(duration, actions.get(i).getDuration());
	            }
	        }
        }

        return duration;
    }

    /**
     * The duration hint of an action set is the maximum of the duration
     * hints of all of its component actions.
     * 
     * @see android.view.animation.Animation#computeDurationHint
     */
    @Override
	public long computeDurationHint() {
    	if(!mInitialized) throw new IllegalStateException("This method can only be invoked after initialize()");
    	
        long duration = 0;
        final int count = mActions.size();
        final List<Action> actions = mActions;
        
        if(mSequence) {
        	duration = actions.get(count-1).computeDurationHint();
        } else {
	        for (int i = count - 1; i >= 0; --i) {
	            final long d = actions.get(i).computeDurationHint();
	            if (d > duration) duration = d;
	        }
        }
        return duration;
    }

    /**
	 * @see android.view.animation.Animation#scaleCurrentDuration(float)
	 */
	@Override
	public ActionSet scaleCurrentDuration(float scale) {
	    final List<Action> actions = mActions;
	    int count = actions.size();
	    for (int i = 0; i < count; i++) {
	        actions.get(i).scaleCurrentDuration(scale);
	    }
	    return this;
	}

	/**
     * The transformation of an action set is the concatenation of all of its
     * component actions.
     * 
     * @see android.view.animation.Animation#getTransformation
     */
    @Override
    public boolean act(long currentTime) {
    	if(mEnded || mCanceled) return false;
    	if(!canStartAfter()) return true;
    	
        //final int count = mActions.size();
        //final List<Action> actions = mActions;
        //final Form temp = mTempTransformation;

        boolean more = false;
        boolean started = false;
        boolean ended = true;

        //t.reset();
        
        if(!mInitialized) {
        	mInitialized = true;
        	initialize();
        }
        
        ListIterator<Action> it = mActions.begin();
		while(it.hasNext()) {
			Action action = it.next();
			
			more = action.act(currentTime) || more;
			
			// 중간에 ActionSet이 제거된 경우
            if(mActor == null) return false;

            started = started || action.hasStarted();
            ended = action.hasEnded() && ended;
		}
		mActions.end(it);

        //for (int i = count - 1; i >= 0; --i) {
        /*for(int i=0; i<count; i++) {
            final Action a = actions.get(i);

            //temp.reset();
            more = a.act(currentTime) || more;
            //t.compose(temp);
            
            // 중간에 ActionSet이 제거된 경우
            if(mActor == null) return false;

            started = started || a.hasStarted();
            ended = a.hasEnded() && ended;
        }*/

        if (started && !mStarted) {
        	/*if (mListener != null) {
                mListener.onActionStart(this);
            }*/
        	fireActionStart();
            mStarted = true;
        }

        if (ended != mEnded) {
        	/*if (mListener != null) {
                mListener.onActionEnd(this);
            }*/
        	fireActionEnd();
            mEnded = ended;
        }

        return more;
    }
    
    /**
     * @see android.view.animation.Animation#initialize(int, int, int, int)
     */
    @Override
    protected void initialize() {
        //super.initialize();

        boolean durationSet = (mFlags & PROPERTY_DURATION_MASK) == PROPERTY_DURATION_MASK;
        boolean fillAfterSet = (mFlags & PROPERTY_FILL_AFTER_MASK) == PROPERTY_FILL_AFTER_MASK;
        boolean fillBeforeSet = (mFlags & PROPERTY_FILL_BEFORE_MASK) == PROPERTY_FILL_BEFORE_MASK;
        boolean repeatModeSet = (mFlags & PROPERTY_REPEAT_MODE_MASK) == PROPERTY_REPEAT_MODE_MASK;
        boolean shareInterpolator = (mFlags & PROPERTY_SHARE_INTERPOLATOR_MASK)
                == PROPERTY_SHARE_INTERPOLATOR_MASK;
        boolean startOffsetSet = (mFlags & PROPERTY_START_OFFSET_MASK)
                == PROPERTY_START_OFFSET_MASK;

        if (shareInterpolator) {
            ensureInterpolator();
        }

        final List<Action> children = mActions;
        final int count = children.size();

        final long duration = mDuration;
        final boolean fillAfter = mFillAfter;
        final boolean fillBefore = mFillBefore;
        final int repeatMode = mRepeatMode;
        final Interpolator interpolator = mInterpolator;
        final long startOffset = mStartOffset;


        long[] storedOffsets = mStoredOffsets;
        if (startOffsetSet) {
            if (storedOffsets == null || storedOffsets.length != count) {
                storedOffsets = mStoredOffsets = new long[count];
            }
        } else if (storedOffsets != null) {
            storedOffsets = mStoredOffsets = null;
        }

        long sequenceOffset = 0;
        
        for (int i = 0; i < count; i++) {
        	Action a = children.get(i);
            if (durationSet) {
                a.setDuration(duration);
            }
            if (fillAfterSet) {
                a.setFillAfter(fillAfter);
            }
            if (fillBeforeSet) {
                a.setFillBefore(fillBefore);
            }
            if (repeatModeSet) {
                a.setRepeatMode(repeatMode);
            }
            if (shareInterpolator) {
                a.setInterpolator(interpolator);
            }
            //if (startOffsetSet) {
                long offset = a.getStartOffset();
                a.setStartOffset(startOffset + sequenceOffset + offset);
                if(startOffsetSet) storedOffsets[i] = offset;
                if(mSequence) sequenceOffset += offset + a.mDuration;
           // }
            //a.initialize();
        }
    }

    @Override
    public void reset() {
        super.reset();
        mInitialized = false;
        restoreChildrenStartOffset();
        List<Action> children = mActions;
    	int n = children.size();
    	for(int i=0; i<n; i++) {
    		Action a = children.get(i);
     		a.reset();
    	}
    }
    
	/**
	 * @hide
	 */
	void restoreChildrenStartOffset() {
	    final long[] offsets = mStoredOffsets;
	    if (offsets == null) return;
	
	    final List<Action> children = mActions;
	    final int count = children.size();
	
	    for (int i = 0; i < count; i++) {
	        children.get(i).setStartOffset(offsets[i]);
	    }
	}

	@Override
	public void cancel() {
		super.cancel();
		List<Action> children = mActions;
		int n = children.size();
		for(int i=0; i<n; i++) {
			Action a = children.get(i);
	 		a.cancel();
		}
	}
    
    @Override
    public ActionSet setActor(Actor<?> actor) {
    	super.setActor(actor);
    	List<Action> children = mActions;
    	int n = children.size();
    	for(int i=0; i<n; i++) {
    		Action a = children.get(i);
     		a.setActor(actor);
    	}
    	return this;
    }

    /**
     * @return All the child actions in this ActionSet. Note that
     * this may include other ActionSets, which are not expanded.
     */
    public SnapshotArrayList<Action> getActionList() {
        return mActions;
    }

    @Override
    public ActionSet setInterpolator(Interpolator i) {
    	super.setInterpolator(i);
    	return this;
    }
    
    @Override
    public ActionSet setInterpolator(Context context, int resID) {
    	super.setInterpolator(context, resID);
    	return this;
    }
    
    @Override
    public ActionSet setActionListener(ActionListener listener) {
    	super.setActionListener(listener);
    	return this;
    }
    
    @Override
	public ActionSet setTag(String tag) {
		super.setTag(tag);
		return this;
	}

	@Override
	public ActionSet setStartAfter(boolean startAfter) {
		super.setStartAfter(startAfter);
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
		List<Action> children = mActions;
    	int n = children.size();
    	for(int i=0; i<n; i++) {
    		Action a = children.get(i);
    		builder.append("\n").append(a.toString());
    	}
		return builder.toString();
	}

	@Deprecated
	@Override
	public ActionSet setRepeatCount(int repeatCount) {
		return this;
	}

	@Deprecated
	@Override
	public ActionSet setFillEnabled(boolean fillEnabled) {
		return this;
	}

}
