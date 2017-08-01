package core.scene.stage.actor.event;

import core.scene.stage.actor.Actor;
import core.utils.pool.Poolable;

/**
 * 
 * @author 김현우
 * @see http://catcode.com/domcontent/events/capture.html
 */
public class Event implements Poolable {
	
	private boolean mBubbling = true;
	private boolean mHandled;
	private boolean mStopped;
	private boolean mCanceled;
	
	private Actor<?> mTargetActor;
	private Actor<?> mListenerActor;
	
	public Event() {
	}

	public boolean isBubbling() {
		return mBubbling;
	}

	public boolean isHandled() {
		return mHandled;
	}

	public boolean isStopped() {
		return mStopped;
	}

	public boolean isCanceled() {
		return mCanceled;
	}

	public Actor<?> getTargetActor() {
		return mTargetActor;
	}

	public Actor<?> getListenerActor() {
		return mListenerActor;
	}
	
	public boolean isTargetActor() {
		return mTargetActor == mListenerActor;
	}

	public void setBubbling(boolean bubbling) {
		mBubbling = bubbling;
	}

	public void handle() {
		mHandled = true;
	}

	public void stop() {
		mStopped = true;
	}

	public void cancel() {
		mStopped = true;
		mCanceled = true;
	}

	public void setTargetActor(Actor<?> targetActor) {
		mTargetActor = targetActor;
	}

	public void setListenerActor(Actor<?> listenerActor) {
		mListenerActor = listenerActor;
	}

	@Override
	public void recycle() {
		mBubbling = true;
		mHandled = false;
		mStopped = false;
		mCanceled = false;
		mTargetActor = null;
		mListenerActor = null;
	}
}
