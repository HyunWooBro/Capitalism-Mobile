package core.scene.stage.actor.widget;

import java.util.Collections;

import core.framework.Core;
import core.math.Vector2;
import core.scene.stage.Floor;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.utils.Layout;
import core.utils.SnapshotArrayList;

public class SlidingDrawer extends WidgetGroup<SlidingDrawer> {
	
	// TODO 구현 필요
	
	public enum AttachedSide {
		TOP, 
		BOTTOM, 
		LEFT, 
		RIGHT, 
	}
	
	private enum DrawerState {
		CLOSED, 
		MOVING, 
		OPENED, 
	}
	
	private static final int INVALID_POINTER_ID  = -1;
	
	private AttachedSide mSide = AttachedSide.LEFT;
	
	private DrawerState mDrawerState = DrawerState.CLOSED;

	private Actor<?> mContent;
	private Actor<?> mHandle;
	
	private float mRelativeContentPos;
	private float mRelativeHandlePos;
	
	private float mContentClosedX;
	private float mContentClosedY;
	private float mContentOpenedX;
	private float mContentOpenedY;
	
	private float mHandleClosedX;
	private float mHandleClosedY;
	private float mHandleOpenedX;
	private float mHandleOpenedY;
	
	private float mVelocityX;
	private float mVelocityY;
	
	private boolean mManuallyMoving;
	
	private GestureTouchListener mHandleListener;
	
	private boolean mToOpen;
	
	//private long mDuration = 1000;

	private boolean mFlinging;
	private long mFlingTimer;

	public SlidingDrawer(final Actor<?> content, final Actor<?> handle) {
		setupListener();
		setContent(content);
		setHandle(handle);
	}
	
	private void setupListener() {
		/*addEventListener(new TouchListener() {
			@Override
			public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
				return true;
			}
			
			@Override
			public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
				super.onMove(event, x, y, listener);
			}
		});*/
		
		mHandleListener = new GestureTouchListener() {
			
			private float mX;
			private float mY;
			
			private int mActivePointerID = INVALID_POINTER_ID;

			@Override
			public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
				if(mActivePointerID != INVALID_POINTER_ID) return false;
				mActivePointerID = event.getPointerID();
				
				// x와 y좌표는 handle의 상대 좌표인데 handle이 계속 움직이기 때문에 
				// Floor 좌표계로 변환하여 계산한다.
				VECTOR.set(event.getScreenX(), event.getScreenY());
				Floor floor = listener.getFloor();
				if(floor != null) floor.screenToFloorCoordinates(VECTOR);
				
				mX = VECTOR.x;
				mY = VECTOR.y;
				return true;
			}

			@Override
			public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
				mManuallyMoving = true;
				
				Core.APP.debug("~moving~~~~");

				//VECTOR.set(x, y);

				final float contentWidth = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefWidth() : mContent.getWidth();
				final float contentHeight = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefHeight() : mContent.getHeight();
				
				final float handleWidth = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefWidth() : mHandle.getWidth();
				final float handleHeight = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefHeight() : mHandle.getHeight();
				
				final float viewportWidth = getFloor().getCamera().getViewportWidth();
				final float viewportHeight = getFloor().getCamera().getViewportHeight();
				
				final float contentRangeX = (viewportWidth >= contentWidth)? viewportWidth - contentWidth : 0f;
				final float contentRangeY = (viewportHeight >= contentHeight)? viewportHeight - contentHeight : 0f;
				
				final float handleRangeX = (contentWidth >= handleWidth)? contentWidth - handleWidth : 0f;
				final float handleRangeY = (contentHeight >= handleHeight)? contentHeight - handleHeight : 0f;
				
				float contentX = mContent.getX();
				float contentY = mContent.getY();
				
				float handleX = mHandle.getX();
				float handleY = mHandle.getY();
				
				VECTOR.set(event.getScreenX(), event.getScreenY());
				Floor floor = listener.getFloor();
				if(floor != null) floor.screenToFloorCoordinates(VECTOR);
				
				Core.APP.debug(" x : " + VECTOR.x + " mX : " + mX);
				

				float newX = VECTOR.x - mX;
				mX = VECTOR.x;
				//if(newX < 0f) newX = 0f;
				//if(newX > contentWidth) newX = contentWidth;
				
				Core.APP.debug(" x2 : " + newX + " X : " + VECTOR.x + " X : " + x);
				
				switch(mSide) {
					case BOTTOM:
						contentX = contentRangeX * mRelativeContentPos;
						contentY = viewportHeight;
						handleX = contentX + handleRangeX * mRelativeHandlePos;
						handleY = contentY - handleHeight;
						break;
					case LEFT:
						contentX += newX;
						//contentY = contentRangeY * mRelativeContentPos;
						
						if(contentX < mContentClosedX) contentX = mContentClosedX;
						if(contentX > mContentOpenedX) contentX = mContentOpenedX;
						
						handleX = contentX + contentWidth;
						//handleY = contentY + handleRangeY * mRelativeHandlePos;
						if(contentX == mContentClosedX) {
							contentX = mContentClosedX;
							mDrawerState = DrawerState.CLOSED;
						} else if(contentX == mContentOpenedX) {
							
							mDrawerState = DrawerState.OPENED;
						} else 
							mDrawerState = DrawerState.MOVING;
						
						
						break;
					case RIGHT:
						//mContent.moveTo(viewportWidth, rangeY * mRelativeContentPos);
						break;
					case TOP:
						//mContent.moveTo(rangeX * mRelativeContentPos, -height);
						break;
				}
				
				
				//mDrawerState = DrawerState.MOVING;
				
				mContent.moveTo(contentX, contentY);
				mHandle.moveTo(handleX, handleY);
				
				Core.APP.debug("opop");
			}

			@Override
			public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
				mActivePointerID = INVALID_POINTER_ID;
				
				final float contentWidth = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefWidth() : mContent.getWidth();
				
				mManuallyMoving = false;
				
				Core.APP.debug("nonono");
				
				mToOpen = false;
				
				float contentX = 0f;
				float contentY = 0f;
				
				float handleX = 0f;
				float handleY = 0f;
				
				mVelocityX = 0f;
				mVelocityY = 0f;
				
				contentX = mContent.getX();
				contentY = mContent.getY();
				handleX = mHandle.getX();
				handleY = mHandle.getY();
				
				switch(mSide) {
					case BOTTOM:
						
						break;
					case LEFT:
						if(contentX == mContentClosedX) return;
						if(contentX == mContentOpenedX) return;
						if(mHandleOpenedX/2 < handleX) mToOpen = true;
						break;
					case RIGHT:
						//mContent.moveTo(viewportWidth, rangeY * mRelativeContentPos);
						break;
					case TOP:
						//mContent.moveTo(rangeX * mRelativeContentPos, -height);
						break;
				}
				
				animate(mToOpen);
			}
			
			@Override
			public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {
				float handleX = mHandle.getX();
				float handleY = mHandle.getY();
				switch(mSide) {
				case BOTTOM:
						break;
					case LEFT:
						if(mHandleOpenedX/2 < handleX) 	close();
						else												open();
						break;
					case RIGHT:
						//mContent.moveTo(viewportWidth, rangeY * mRelativeContentPos);
						break;
					case TOP:
						//mContent.moveTo(rangeX * mRelativeContentPos, -height);
						break;
				}
			}
			
			@Override
			public void onFling(TouchEvent event, float velocityX, float velocityY, float x, 
					float y, Actor<?> listener) {

				boolean toOpen = false;
				
				float velocity = 0f;
				
				float interpolatedLength = 0;
				
				mFlinging = true;
				
				switch(mSide) {
					case BOTTOM:
						
						break;
					case LEFT:
						velocity = velocityX;
						mVelocityX = velocity;
						mVelocityY = 0f;
						mFlingTimer = (long) Vector2.len(mVelocityX, mVelocityY);
						if(velocityX >= 0) {
							mToOpen = true;
							interpolatedLength = (mHandleOpenedX - mHandle.getX()) / (mHandleOpenedX - mHandleClosedX);
						} else {
							mToOpen = false;
							interpolatedLength = (mHandle.getX() - mHandleClosedX) / (mHandleOpenedX - mHandleClosedX);
						}
						
						//if(mHandleOpenedX/2 < handleX) toOpen = true;
						break;
					case RIGHT:
						//mContent.moveTo(viewportWidth, rangeY * mRelativeContentPos);
						break;
					case TOP:
						//mContent.moveTo(rangeX * mRelativeContentPos, -height);
						break;
				}
				
				//fling(toOpen, velocity, interpolatedLength);
				
				//animate(toOpen);
			}
			
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SnapshotArrayList<Actor<?>> getChildList() {
		return (SnapshotArrayList<Actor<?>>) mChildList.clone();
	}
	
	@Override
	public boolean removeChild(Actor<?> child) {
		if(child == null) return false;
		if(child == mContent) {
			setContent(null);
			return true;
		} else if(child == mHandle) {
			setHandle(null);
			return true;
		}
		return false;
	}

	private void fling(boolean toOpen, float velocity, float interpolatedLength) {
		//mDuration = (long) (1000 * interpolatedLength / (velocity/1000));
		animate(toOpen);
		//mDuration = 1000;
	}
	
	private void animate(boolean toOpen) {
		mToOpen = toOpen;
		
		if(toOpen) {

		} else {

		}
	}
	
	public SlidingDrawer open() {
		mToOpen = true;
		mDrawerState = DrawerState.MOVING;
		Core.APP.debug("@@open");
		return this;
	}
	
	public SlidingDrawer close() {
		mToOpen = false;
		mDrawerState = DrawerState.MOVING;
		return this;
	}
	
	private void calculateDrawerPosition() {
		long delta = Core.GRAPHICS.getDeltaTime();
		
		final float contentWidth = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefWidth() : mContent.getWidth();
		final float contentHeight = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefHeight() : mContent.getHeight();
		
		final float handleWidth = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefWidth() : mHandle.getWidth();
		final float handleHeight = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefHeight() : mHandle.getHeight();
		
		final float viewportWidth = getFloor().getCamera().getViewportWidth();
		final float viewportHeight = getFloor().getCamera().getViewportHeight();
		
		final float contentRangeX = (viewportWidth >= contentWidth)? viewportWidth - contentWidth : 0f;
		final float contentRangeY = (viewportHeight >= contentHeight)? viewportHeight - contentHeight : 0f;
		
		final float handleRangeX = (contentWidth >= handleWidth)? contentWidth - handleWidth : 0f;
		final float handleRangeY = (contentHeight >= handleHeight)? contentHeight - handleHeight : 0f;
		
		switch(mDrawerState) {
			case CLOSED:
				break;
			case MOVING:
				float contentX = mContent.getX();
				float contentY = mContent.getY();
				
				float handleX = mHandle.getX();
				float handleY = mHandle.getY();

				float newX = 3f;
				if(!mToOpen) newX = -newX;
				
				switch(mSide) {
					case BOTTOM:

						break;
					case LEFT:
						/*if(mVelocityX > 0f) {
							newX = Math.abs(newX);
						}
						if(mVelocityX < 0f) {
							Core.APP.debug("back");
							newX = -Math.abs(newX);
						}*/
						contentX += newX + mVelocityX* delta/1000;
						if(mVelocityX > 0f) {
							mVelocityX -= 3f;
							if(mVelocityX < 0f) mVelocityX = 0f;
						}
						
						if(mVelocityX < 0f) {
							mVelocityX += 3f;
							if(mVelocityX > 0f) mVelocityX = 0f;
						}
						
						if(contentX < mContentClosedX) {
							contentX = mContentClosedX;
							handleX = mHandleClosedX;
						}
						if(contentX > mContentOpenedX) {
							contentX = mContentOpenedX;
							handleX = mHandleOpenedX;
						}
						//contentY = contentRangeY * mRelativeContentPos;
						handleX = contentX + contentWidth;
						//handleY = contentY + handleRangeY * mRelativeHandlePos;
						if(contentX == mContentClosedX) {
							mVelocityX = 0f;
							mDrawerState = DrawerState.CLOSED;
						}
						else if(contentX == mContentOpenedX) {
							mVelocityX = 0f;
							mDrawerState = DrawerState.OPENED;
						}
						else mDrawerState = DrawerState.MOVING;
						break;
					case RIGHT:
						//mContent.moveTo(viewportWidth, rangeY * mRelativeContentPos);
						break;
					case TOP:
						//mContent.moveTo(rangeX * mRelativeContentPos, -height);
						break;
				}
				
				mContent.moveTo(contentX, contentY);
				mHandle.moveTo(handleX, handleY);
				
				//Core.APP.debug("x : " + contentX + " y : " + contentY + " width : " + contentWidth);
				break;
			case OPENED:
				break;
		}
	}
	
	public SlidingDrawer openNow() {
		mContent.moveTo(mContentOpenedX, mContentOpenedY);
		mHandle.moveTo(mHandleOpenedX, mHandleOpenedY);
		mDrawerState = DrawerState.OPENED;
		return this;
	}
	
	public SlidingDrawer closeNow() {
		mContent.moveTo(mContentClosedX, mContentClosedY);
		mHandle.moveTo(mHandleClosedX, mHandleClosedY);
		mDrawerState = DrawerState.CLOSED;
		return this;
	}

	@Override
	public void update(long time) {
		super.update(time);

		long delta = Core.GRAPHICS.getDeltaTime();
		if(mFlinging) {
			mFlingTimer -= delta;
			if(mFlingTimer < 0) {
				mFlinging = false;
				mVelocityX = 0f;
				mVelocityY = 0f;
			}
		}
		
		/*if(mDrawerState == DrawerState.OPENED)
			Core.APP.debug("~opened");
		
		if(mDrawerState == DrawerState.CLOSED)
			Core.APP.debug("~closed");
		
		if(mDrawerState == DrawerState.MOVING)
			Core.APP.debug("~moving");*/
		
		if(mManuallyMoving) return;
		
		final float contentWidth = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefWidth() : mContent.getWidth();
		final float contentHeight = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefHeight() : mContent.getHeight();
		
		final float handleWidth = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefWidth() : mHandle.getWidth();
		final float handleHeight = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefHeight() : mHandle.getHeight();
		
		final float viewportWidth = getFloor().getCamera().getViewportWidth();
		final float viewportHeight = getFloor().getCamera().getViewportHeight();
		
		final float contentRangeX = (viewportWidth >= contentWidth)? viewportWidth - contentWidth : 0f;
		final float contentRangeY = (viewportHeight >= contentHeight)? viewportHeight - contentHeight : 0f;
		
		final float handleRangeX = (contentWidth >= handleWidth)? contentWidth - handleWidth : 0f;
		final float handleRangeY = (contentHeight >= handleHeight)? contentHeight - handleHeight : 0f;
		
		switch(mDrawerState) {
			case CLOSED:
				break;
			case MOVING:
				
				Core.APP.debug("@@MOVING");
				
				float contentX = mContent.getX();
				float contentY = mContent.getY();
				
				float handleX = mHandle.getX();
				float handleY = mHandle.getY();

				float newX = 3f;
				if(!mToOpen) newX = -newX;
				
				switch(mSide) {
					case BOTTOM:

						break;
					case LEFT:
						/*if(mVelocityX > 0f) {
							newX = Math.abs(newX);
						}
						if(mVelocityX < 0f) {
							Core.APP.debug("back");
							newX = -Math.abs(newX);
						}*/
						contentX += newX + mVelocityX* delta/1000;
						if(mVelocityX > 0f) {
							mVelocityX -= 3f;
							if(mVelocityX < 0f) mVelocityX = 0f;
						}
						
						if(mVelocityX < 0f) {
							mVelocityX += 3f;
							if(mVelocityX > 0f) mVelocityX = 0f;
						}
						
						if(contentX < mContentClosedX) {
							contentX = mContentClosedX;
							handleX = mHandleClosedX;
						}
						if(contentX > mContentOpenedX) {
							contentX = mContentOpenedX;
							handleX = mHandleOpenedX;
						}
						//contentY = contentRangeY * mRelativeContentPos;
						handleX = contentX + contentWidth;
						//handleY = contentY + handleRangeY * mRelativeHandlePos;
						if(contentX == mContentClosedX) {
							mVelocityX = 0f;
							mDrawerState = DrawerState.CLOSED;
						}
						else if(contentX == mContentOpenedX) {
							mVelocityX = 0f;
							mDrawerState = DrawerState.OPENED;
						}
						else mDrawerState = DrawerState.MOVING;
						break;
					case RIGHT:
						//mContent.moveTo(viewportWidth, rangeY * mRelativeContentPos);
						break;
					case TOP:
						//mContent.moveTo(rangeX * mRelativeContentPos, -height);
						break;
				}
				
				mContent.moveTo(contentX, contentY);
				mHandle.moveTo(handleX, handleY);
				
				//Core.APP.debug("x : " + contentX + " y : " + contentY + " width : " + contentWidth);
				break;
			case OPENED:
				break;
		}
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		float width = 0;
		if(mContent != null) {
			width += (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefWidth() : mContent.getWidth();
		}
		if(mHandle != null) {
			width += (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefWidth() : mHandle.getWidth();
		}
		//if(mDrawable != null) return mDrawable.getWidth();
		return width;
	}

	@Override
	protected float getDefaultPrefHeight() {
		float height = 0;
		if(mContent != null) {
			height += (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefHeight() : mContent.getHeight();
		}
		if(mHandle != null) {
			height += (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefHeight() : mHandle.getHeight();
		}
		//if(mDrawable != null) return mDrawable.getWidth();
		return height;
	}
	
	@Override
	public void layout() {
		
		final float contentWidth = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefWidth() : mContent.getWidth();
		final float contentHeight = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefHeight() : mContent.getHeight();
		
		final float handleWidth = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefWidth() : mHandle.getWidth();
		final float handleHeight = (mHandle instanceof Layout)? ((Layout<?>) mHandle).getPrefHeight() : mHandle.getHeight();
		
		final float viewportWidth = getFloor().getCamera().getViewportWidth();
		final float viewportHeight = getFloor().getCamera().getViewportHeight();
		
		final float contentRangeX = (viewportWidth >= contentWidth)? viewportWidth - contentWidth : 0f;
		final float contentRangeY = (viewportHeight >= contentHeight)? viewportHeight - contentHeight : 0f;
		
		final float handleRangeX = (contentWidth >= handleWidth)? contentWidth - handleWidth : 0f;
		final float handleRangeY = (contentHeight >= handleHeight)? contentHeight - handleHeight : 0f;
		
		float contentX = 0f;
		float contentY = 0f;
		
		float handleX = 0f;
		float handleY = 0f;
		
		switch(mSide) {
			case BOTTOM:
				contentX = contentRangeX * mRelativeContentPos;
				contentY = viewportHeight;
				handleX = contentX + handleRangeX * mRelativeHandlePos;
				handleY = contentY - handleHeight;
				break;
			case LEFT:
				contentX = -contentWidth;
				contentY = contentRangeY * mRelativeContentPos;
				handleX = 0f;
				handleY = contentY + handleRangeY * mRelativeHandlePos;
				mContentClosedX = contentX;
				mContentClosedY = contentY;
				mContentOpenedX = 0f;
				mContentOpenedY = contentY;
				mHandleClosedX = handleX;
				mHandleClosedY = handleY;
				mHandleOpenedX = contentWidth;
				mHandleOpenedY = handleY;
				break;
			case RIGHT:
				//mContent.moveTo(viewportWidth, rangeY * mRelativeContentPos);
				break;
			case TOP:
				//mContent.moveTo(rangeX * mRelativeContentPos, -height);
				break;
		}
		
		mContent.moveTo(contentX, contentY);
		mHandle.moveTo(handleX, handleY);
	}
	
	public AttachedSide getAttachedSide() {
		return mSide;
	}
	
	public boolean isOpened() {
		return (mDrawerState == DrawerState.OPENED)? true : false;
	}
	
	public boolean isMoving() {
		return (mDrawerState == DrawerState.MOVING)? true : false;
	}
	
	public boolean isClosed() {
		return (mDrawerState == DrawerState.CLOSED)? true : false;
	}

	public float getRelativeContentPos() {
		return mRelativeContentPos;
	}
	
	public float getRelativeHandlePos() {
		return mRelativeHandlePos;
	}

	public SlidingDrawer setRelativeContentPos(float relativeContentPos) {
		mRelativeContentPos = relativeContentPos;
		return this;
	}

	public SlidingDrawer setRelativeHandlePos(float relativeHandlePos) {
		mRelativeHandlePos = relativeHandlePos;
		return this;
	}
	
	public Actor<?> getHandle() {
		return mHandle;
	}

	public Actor<?> getContent() {
		return mContent;
	}

	public void setHandle(Actor<?> handle) {
		if(mHandle != null) {
			mHandle.removeEventListener(mHandleListener);
			super.removeChild(mHandle);
		}
		handle.addEventListener(mHandleListener);
		super.addChild(handle);
		mHandle = handle;
	}
	
	public void setContent(Actor<?> content) {
		super.removeChild(mContent);
		super.addChild(content);
		mContent = content;
	}
	
	/** @deprecated setHandle(Actor) 또는 setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public SlidingDrawer addChild(Actor<?> child) {
		throw new UnsupportedOperationException("Use setHandle(Actor) or setContent(Actor).");
	}
	
	/** @deprecated setHandle(Actor) 또는 setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public SlidingDrawer addChild(int index, Actor<?> child) {
		throw new UnsupportedOperationException("Use setHandle(Actor) or setContent(Actor).");
	}
	
	/** @deprecated setHandle(Actor) 또는 setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public SlidingDrawer addChildBefore(Actor<?> childBefore, Actor<?> newChild) {
		throw new UnsupportedOperationException("Use setHandle(Actor) or setContent(Actor).");
	}
	
	/** @deprecated setHandle(Actor) 또는 setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public SlidingDrawer addChildAfter(Actor<?> childAfter, Actor<?> newChild) {
		throw new UnsupportedOperationException("Use setHandle(Actor) or setContent(Actor).");
	}

}
