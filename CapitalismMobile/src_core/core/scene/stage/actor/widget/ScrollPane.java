package core.scene.stage.actor.widget;

import java.util.Collections;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.utils.Scissor;
import core.math.MathUtils;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.utils.Layout;
import core.scene.stage.actor.widget.utils.OverScroller;
import core.utils.SnapshotArrayList;

/**
 * ScrollPane은 하나의 content를 자신의 사각영역을 통해 보여주는 일종의 창이다. 일반적으로 
 * content는 ScrollPane보다 크기 때문에 스크롤을 통해 content의 원하는 일부분을 볼 수 있다.</p>
 * 
 * content가 Layout를 구현하는 경우 선호 사이즈를 사용하는데, 필요한 경우 
 * {@link Layout#setPrefSize(Float, Float)} 등으로 직접 설정할 수 있다.</p> 
 * 
 * 내부적으로 사각영역으로 잘라내기 위해 OpenGL의 scissor을 사용하기 때문에 회전을 하면 
 * 제대로 출력되지 않는다. 또한, 스크롤 bar 및 knob은 스케일을 고려하지 않고 출력되기 때문에 
 * 스크롤 bar 및 knob 이미지를 출력하는 동시에 ScrollPane에 스케일을 적용하면 제대로 
 * 출력되지 않는다.</p>
 * 
 * @see android.view.View
 * @see android.widget.ScrollView
 * @see http://stackoverflow.com/questions/9334011/how-to-overscroll-a-custom-view-surfaceview
 * @author 김현우
 */
public class ScrollPane extends WidgetGroup<ScrollPane>{
	
	/** content의 너비나 높이가 ScrollPane보다 작을 때 취하는 조치 */
	public enum FillType {
		/** ScrollPane의 길이만큼 늘린다. 디폴트로 사용된다. */
		STRETCH,
		/** 길이를 수정하지 않고 가운데 정렬되도록 한다. */
		CENTER, 
		/** 어떠한 조치도 취하지 않는다. 좌측 상단을 기준으로 출력된다. */
		NONE, 
	}
	
	private static final int INVALID_POINTER_ID  = -1;
	
	private FillType mFillType = FillType.STRETCH;
	
	private OverScroller mScroller;
	
	private Rectangle mHScrollKnobRectangle = new Rectangle();
	private Rectangle mHScrollBarRectangle = new Rectangle();
	
	private Rectangle mVScrollKnobRectangle = new Rectangle();
	private Rectangle mVScrollBarRectangle = new Rectangle();
	
	private boolean mManualScrolling;
	private boolean mFlinging;
    
    private float mScrollX;
    private float mScrollY;
    
    private float mScrollRangeX;
    private float mScrollRangeY;
    
    private float mOverScrollDistance = 0f;
    
    private boolean mScrollFadeEnabled = true;
    private boolean mFadeJustStarted;
    private long mFadeDelay = 500;
    private long mFadeDuration = 300;
    private long mFadeStartTime;
    private long mFadeEndTime;
    private float mAlpha;
    
    private boolean mScrollable = true;
    
    private boolean mScrollAppear = true;
	
	private Actor<?>	mContent;
	
	private float mContentX;
	private float mContentY;
	
	private boolean mConstructed;

	/** 
	 * 스크롤 bar 및 knob 등의 이미지를 사용하지 않는 ScrollPane을 생성한다. content는 
	 * null이 허용된다.
	 */
	public ScrollPane(Actor<?> content) {
		this(content, null);
	}
	
	/** content 및 costume은 null이 허용된다. */
	public ScrollPane(Actor<?> content, ScrollPaneCostume costume) {
		super(costume);
		mScroller = new OverScroller(Core.APP.getActivity());
		setContent(content);
		pack();
		mConstructed = true;
        
        addEventListener(new GestureTouchListener() {
        	
        	private int mActivePointerID = INVALID_POINTER_ID;
        	
        	@Override
        	public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
        		if(!mScrollable || mContent == null) return false;
        		if(mActivePointerID != INVALID_POINTER_ID) return false;
        		mActivePointerID = event.getPointerID();
        		if(!mScroller.isFinished()) mScroller.abortAnimation();
        		if(mFlinging) mFlinging = false;
        		return true;
        	}
        	
        	@Override
        	public void onScroll(TouchEvent event, float distanceX, float distanceY, float x, float y, Actor<?> listener) {
        		mManualScrolling = true;
        		
				float abs = 0f;
				if(mScrollRangeX>0f) abs += Math.abs(distanceX);
				if(mScrollRangeY>0f) abs += Math.abs(distanceY);
				if(abs > 0f) mFadeJustStarted = true;

				overScrollBy(distanceX, distanceY);
        	}
        	
        	@Override
        	public void onFling(TouchEvent event, float velocityX, float velocityY, float x, float y, Actor<?> listener) {
        		mFlinging = true;

        		int startX = (int) mScrollX;
        		int startY = (int) mScrollY;
        		int contentWidth = (int) mContent.getWidth();
        		int contentHeight = (int) mContent.getHeight();
        		int maxX = (int) mScrollRangeX;
        		int maxY = (int) mScrollRangeY;
        		
        		int vX = (int) -velocityX;
        		int vY = (int) -velocityY;
        		
        		mScroller.fling(
        				startX, startY, 
        				vX, vY, 
        				0, maxX, 
        				0, maxY, 
        				contentWidth/2, contentHeight/2);
        	}
        	
        	@Override
        	public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
        		mActivePointerID = INVALID_POINTER_ID;
        		
        		if(mManualScrolling) mManualScrolling = false;
        		
        		// fling이 발생하지 않았으므로 범위를 벗어난 overScroll 상태일 경우 되돌아 가도록 한다.
        		if(!mFlinging) {
	        		int startX = (int) mScrollX;
					int startY = (int) mScrollY;
					int maxX = (int) mScrollRangeX;
					int maxY = (int) mScrollRangeY;
					mScroller.springBack(
							startX, startY, 
							0, maxX, 
							0, maxY);
        		}
        	}
		});
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new ScrollPaneCostume(costume) : new ScrollPaneCostume();
	}

	/** 수정한 Drawable이 사이즈에 반영되길 원한다면 {@link #pack()}을 호출해야 한다. */
	@Override
	public ScrollPaneCostume getCostume() {
		return (ScrollPaneCostume) mCostume;
	}
	
	@Override
	public void costumeChanged() {
		invalidateHierarchy();
		if(mConstructed) pack();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SnapshotArrayList<Actor<?>> getChildList() {
		return (SnapshotArrayList<Actor<?>>) mChildList.clone();
	}
	
	@Override
	public boolean removeChild(Actor<?> child) {
		if(child == null || child != mContent) return false;
		setContent(null);
		return true;
	}
	
	@Override
	public void update(long time) {
		super.update(time);
		
		if(!mScrollable || mContent == null) return;

		computeScroll();
		
		if(mScrollFadeEnabled && (mScrollRangeX > 0f || mScrollRangeY > 0f)) {
			if(mFadeJustStarted) {
				mFadeJustStarted = false;
				mFadeStartTime = time + mFadeDelay;
				mFadeEndTime = mFadeStartTime + mFadeDuration;
				mAlpha = 1f;
			} else {
				if(mFadeStartTime < time && time < mFadeEndTime ) {
					float x = time - mFadeStartTime;
					x /= mFadeDuration;
					x = 1f - x;
					mAlpha = x;
				}
			}
		}
	}
	
	private void computeScroll() {
		if(mScroller.computeScrollOffset()) {
			float oldX = mScrollX;
			float oldY = mScrollY;
			float x = mScroller.getCurrX( );
			float y = mScroller.getCurrY( );
           if(oldX != x || oldY != y) overScrollBy(x - oldX, y - oldY);
		} else {
			if(mFlinging) mFlinging = false;
		}
	}
	
	private void overScrollBy(float deltaX, float deltaY) {
		float scrollX = mScrollX;
		float scrollY = mScrollY;
		float scrollRangeX = mScrollRangeX;
		float scrollRangeY = mScrollRangeY;
		float maxOverScrollX = mOverScrollDistance;
	    float maxOverScrollY = mOverScrollDistance;
		
		final boolean canScrollX = scrollRangeX > 0f;
	    final boolean canScrollY = scrollRangeY > 0f;
	    
	    float newScrollX = scrollX + deltaX;
	    if(!canScrollX) maxOverScrollX = 0;
	
	    float newScrollY = scrollY + deltaY;
	    if(!canScrollY) maxOverScrollY = 0;
	
	    // Clamp values if at the limits and record
	    final float left = -maxOverScrollX;
	    final float right = maxOverScrollX + scrollRangeX;
	    final float top = -maxOverScrollY;
	    final float bottom = maxOverScrollY + scrollRangeY;
	
	    boolean clampedX = false;
	    boolean clampedY = false;
	    
	    if(newScrollX > right) {
	        newScrollX = right;
	        clampedX = true;
	    } else if(newScrollX < left) {
	        newScrollX = left;
	        clampedX = true;
	    }
	
	    if(newScrollY > bottom) {
	        newScrollY = bottom;
	        clampedY = true;
	    } else if(newScrollY < top) {
			newScrollY = top;
			clampedY = true;
	    }
	    
	    scrollTo(newScrollX, newScrollY);
	
	    if(!mScroller.isFinished()) {
			if(clampedX) {
				int startX = (int) newScrollX;
				int maxX = (int) scrollRangeX;
				mScroller.springBackX(startX, 0, maxX);
	        }
			if(clampedY) {
				int startY = (int) newScrollY;
				int maxY = (int) scrollRangeY;
				mScroller.springBackY(startY, 0, maxY);
	        }
	    }
	}
	
	public void setScrollX(float x) {
		if(mScrollX == x) return;
	    mScrollX = x;
	    onScrollChanged();
	}
	
	public void setScrollY(float y) {
		if(mScrollY == y) return;
	    mScrollY = y;
	    onScrollChanged();
	}

	public void scrollTo(float x, float y) {
		if(mScrollX == x && mScrollY == y) return;
	    mScrollX = x;
	    mScrollY = y;
	    onScrollChanged();
	}

	protected void onScrollChanged() {
	}

	@Override
	public void layout() {
		
		ScrollPaneCostume costume = getCostume();
		
		float padTop = 0f;
		float padLeft = 0f;
		float padRight = 0f;
		float padBottom = 0f;
		if(costume.background != null) {
			mDrawable = costume.background;
			padTop = costume.background.getPadTop();
			padLeft = costume.background.getPadLeft();
			padRight = costume.background.getPadRight();
			padBottom = costume.background.getPadBottom();
		}
		
		if(mContent == null) return;
		
		float width = getWidth();
		float height = getHeight();
		
		float pureWidth = width - padLeft - padRight;
		float pureHeight = height - padTop - padBottom;
		
		float contentWidth;
		float contentHeight;
		if(mContent instanceof Layout) {
			Layout<?> layout = (Layout<?>) mContent;
			contentWidth = layout.getPrefWidth();
			contentHeight = layout.getPrefHeight();
		} else {
			contentWidth = mContent.getWidth();
			contentHeight = mContent.getHeight();
		}
		
		switch(mFillType) {
			case STRETCH:
				contentWidth = Math.max(pureWidth, contentWidth);
				contentHeight = Math.max(pureHeight, contentHeight);
				break;
			case CENTER:
				mContentX = Math.max(0f, (pureWidth - contentWidth)/2f);
				mContentY = Math.max(0f, (pureHeight - contentHeight)/2f);
				break;
			default:
				break;
		}
		
		mContent.sizeTo(contentWidth, contentHeight);
		
		mScrollRangeX = Math.max(0f, contentWidth - pureWidth);
		mScrollRangeY = Math.max(0f, contentHeight - pureHeight);
		
		final boolean canScrollX = mScrollRangeX > 0f;
        final boolean canScrollY = mScrollRangeY > 0f;
        
        if(!canScrollX && !canScrollY) return;
		
		float hScrollBarHeight = 0f;
		if(canScrollX) {
			if(costume.hScrollBar != null) hScrollBarHeight = costume.hScrollBar.getHeight();
			if(costume.hScrollKnob != null) hScrollBarHeight = Math.max(hScrollBarHeight, costume.hScrollKnob.getHeight());
		}
		
		float vScrollBarWidth = 0f;
		if(canScrollY) {
			if(costume.vScrollBar != null) vScrollBarWidth = costume.vScrollBar.getWidth();
			if(costume.vScrollKnob != null) vScrollBarWidth = Math.max(vScrollBarWidth, costume.vScrollKnob.getWidth());
		}
		
		if(canScrollX) {
			// 가로 스크롤바를 지정하지 않더라도 knob이나 corner에서 사용할 수 있으므로 영역을 계산한다.
			mHScrollBarRectangle.set(0f, pureHeight - hScrollBarHeight, pureWidth - vScrollBarWidth, hScrollBarHeight);
			
			if(costume.hScrollKnob != null) {
				float hScrollKnobHeight = costume.hScrollKnob.getHeight();
				float y = Math.max(0f, (hScrollBarHeight-hScrollKnobHeight)/2);
				float barWidth = mHScrollBarRectangle.width;
				mHScrollKnobRectangle.set(0f, pureHeight - hScrollBarHeight + y, barWidth * (barWidth / contentWidth), hScrollKnobHeight);
			}
		}
		
		if(canScrollY) {
			// 세로 스크롤바를 지정하지 않더라도 knob이나 corner에서 사용할 수 있으므로 영역을 계산한다.
			mVScrollBarRectangle.set(pureWidth - vScrollBarWidth, 0f, vScrollBarWidth, pureHeight - hScrollBarHeight);
			
			if(costume.vScrollKnob != null) {
				float vScrollKnobWidth = costume.vScrollKnob.getWidth(); 
				float x = Math.max(0f, (vScrollBarWidth-vScrollKnobWidth)/2);
				float barHeight = mVScrollBarRectangle.height;
				mVScrollKnobRectangle.set(pureWidth - vScrollBarWidth + x, 0f, vScrollKnobWidth, barHeight * (barHeight / contentHeight));
			}
		}
	}
	
	@Override
	public float getMinWidth() {
		return 0f;
	}
	
	@Override
	public float getMinHeight() {
		return 0f;
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		ScrollPaneCostume costume = getCostume();
		if(mContent != null) {
			float width = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefWidth() : mContent.getWidth();
			if(costume.background != null) {
				float padLeft = costume.background.getPadLeft();
				float padRight = costume.background.getPadRight();
				width += padLeft + padRight;
			}
			return width;
		}
		if(costume.background != null) return costume.background.getWidth();
		return 0f;
	}

	@Override
	protected float getDefaultPrefHeight() {
		ScrollPaneCostume costume = getCostume();
		if(mContent != null) {
			float height = (mContent instanceof Layout)? ((Layout<?>) mContent).getPrefHeight() : mContent.getHeight();
			if(costume.background != null) {
				float padTop = costume.background.getPadTop();
				float padBottom = costume.background.getPadBottom();
				height += padTop + padBottom;
			}
			return height;
		}
		if(costume.background != null) return costume.background.getHeight();
		return 0f;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		
		if(mVisible == Visible.ENABLED) {
			if(mDrawable != null) drawSelf(batch, parentAlpha);
		}
		
		if(mContent == null) return;
		
		if(mVisible != Visible.DISABLED) {
			ScrollPaneCostume costume = getCostume();
			
			float padTop = 0f;
			float padLeft = 0f;
			float padRight = 0f;
			float padBottom = 0f;
			if(costume.background != null) {
				mDrawable = costume.background;
				padTop = costume.background.getPadTop();
				padLeft = costume.background.getPadLeft();
				padRight = costume.background.getPadRight();
				padBottom = costume.background.getPadBottom();
			}
			
			float width = getWidth();
			float height = getHeight();
			
			float pureWidth = width - padLeft - padRight;
			float pureHeight = height - padTop - padBottom;
			
			float contentWidth = mContent.getWidth();
			float contentHeight = mContent.getHeight();
			
			float contentX = (mFillType == FillType.CENTER)? mContentX : 0f;
			float contentY = (mFillType == FillType.CENTER)? mContentY : 0f;
			
			float scrollX = mScrollX;
			float scrollY = mScrollY;
			
			mContent.moveTo(contentX-scrollX+padLeft, contentY-scrollY+padTop);
			
			batch.flush();
			pushTransformation(batch);
			
			Scissor.begin(getFloor().getCamera(), batch.peekTransformMatrix(), padLeft, padTop, pureWidth, pureHeight);
			drawChildren(batch, parentAlpha);
			batch.flush();
			Scissor.end();
			
			popTransformation(batch);
			
			float scrollRangeX = mScrollRangeX;
    		float scrollRangeY = mScrollRangeY;
    		
			final boolean canScrollX = scrollRangeX > 0f;
	        final boolean canScrollY = scrollRangeY > 0f;
	        
	        if(!canScrollX && !canScrollY) return;
	
			if(mScrollAppear && mAlpha > 0f) {
				
				float x = getX();
				float y = getY();
				
				float pureX = x + padLeft;
				float pureY = y + padTop;
				
		        float alpha = mAlpha * parentAlpha;
				batch.setColor(alpha, 1f, 1f, 1f);
				
				if(canScrollX && canScrollY) {
		        	if(costume.corner != null) {
		        		batch.setAlpha(alpha * costume.corner.getAlpha());
		        		costume.corner.draw(
								batch, 
								mHScrollBarRectangle.x + mHScrollBarRectangle.width + pureX, 
								mHScrollBarRectangle.y + pureY, 
								mVScrollBarRectangle.width, 
								mHScrollBarRectangle.height, 
								false, false);
		        	}
		        }
				
				// knob이 ScrollPane을 벗어나지 않도록 clamp한다.
				float clampedScrollX = MathUtils.clamp(scrollX, 0f, scrollRangeX);
				float clampedScrollY = MathUtils.clamp(scrollY, 0f, scrollRangeY);
				
				if(canScrollX) {
					if(costume.hScrollBar != null) {
						batch.setAlpha(alpha * costume.hScrollBar.getAlpha());
						costume.hScrollBar.draw(
								batch, 
								mHScrollBarRectangle.x + pureX, 
								mHScrollBarRectangle.y + pureY, 
								mHScrollBarRectangle.width, 
								mHScrollBarRectangle.height, 
								false, false);
					}
					if(costume.hScrollKnob != null) {
						batch.setAlpha(alpha * costume.hScrollKnob.getAlpha());
						costume.hScrollKnob.draw(
								batch, 
								mHScrollKnobRectangle.x + pureX + clampedScrollX/scrollRangeX*(mHScrollBarRectangle.width-mHScrollKnobRectangle.width), 
								mHScrollKnobRectangle.y + pureY, 
								mHScrollKnobRectangle.width, 
								mHScrollKnobRectangle.height, 
								false, false);
					}
				}
				if(canScrollY) {
					if(costume.vScrollBar != null) {
						batch.setAlpha(alpha * costume.vScrollBar.getAlpha());
						costume.vScrollBar.draw(
								batch, 
								mVScrollBarRectangle.x + pureX, 
								mVScrollBarRectangle.y + pureY, 
								mVScrollBarRectangle.width, 
								mVScrollBarRectangle.height, 
								false, false);
					}
					if(costume.vScrollKnob != null) {
						batch.setAlpha(alpha * costume.vScrollKnob.getAlpha());
						costume.vScrollKnob.draw(
								batch, 
								mVScrollKnobRectangle.x + pureX, 
								mVScrollKnobRectangle.y + pureY + clampedScrollY/scrollRangeY*(mVScrollBarRectangle.height-mVScrollKnobRectangle.height), 
								mVScrollKnobRectangle.width, 
								mVScrollKnobRectangle.height, 
								false, false);
					}
				}
			}
		}
	}

	@Override
	public Actor<?> contact(float x, float y) {
		// ScrollPane을 벗아나는 위치는 무시한다.
		if(x < 0f || x > getWidth() || y < 0f || y > getHeight()) return null;
		return super.contact(x, y);
	}
	
	public ScrollPane scroll(int fromX, int fromY, int toX, int toY, int duration) {
    	mScroller.startScroll(fromX, fromY, toX, toY, duration);
    	return this;
    }
    
    public boolean isManualScrolling() {
    	return mManualScrolling;
    }
    
    public boolean isFlinging() {
    	return mFlinging;
    }

	public float getOverScrollDistance() {
		return mOverScrollDistance;
	}

	public ScrollPane setOverScrollDistance(float overScrollDistance) {
		mOverScrollDistance = overScrollDistance;
		return this;
	}

	public boolean isScrollFadeEnabled() {
		return mScrollFadeEnabled;
	}

	public ScrollPane setScrollFadeEnabled(boolean scrollFadeEnabled) {
		mScrollFadeEnabled = scrollFadeEnabled;
		return this;
	}
	
	public ScrollPane setFadeTime(long fadeDelay, long fadeDuration) {
		mFadeDelay = fadeDelay;
		mFadeDuration = fadeDuration;
		return this;
	}

	/** fling상태에서의 마찰계수를 지정한다. 디폴트는 0.015f이다. */
	public ScrollPane setFriction(float friction) {
		mScroller.setFriction(friction);
		return this;
	}
	
	public Actor<?> getContent() {
		return mContent;
	}

	public void setContent(Actor<?> content) {
		super.removeChild(mContent);
		if(content != null) super.addChild(content);
		mContent = content;
	}

	public FillType getFillType() {
		return mFillType;
	}

	public ScrollPane setFillType(FillType fillType) {
		mFillType = fillType;
		return this;
	}

	public float getScrollX() {
		return mScrollX;
	}

	public float getScrollY() {
		return mScrollY;
	}

	public float getScrollRangeX() {
		return mScrollRangeX;
	}

	public float getScrollRangeY() {
		return mScrollRangeY;
	}

	public OverScroller getScroller() {
		return mScroller;
	}

	public boolean isScrollable() {
		return mScrollable;
	}

	public ScrollPane setScrollable(boolean scrollable) {
		mScrollable = scrollable;
		return this;
	}

	public boolean isScrollAppear() {
		return mScrollAppear;
	}

	public ScrollPane setScrollAppear(boolean scrollAppear) {
		mScrollAppear = scrollAppear;
		return this;
	}
	
	public void startScrollFade() {
		if(mScrollFadeEnabled) mFadeJustStarted = true;
	}

	/** @deprecated setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public ScrollPane addChild(Actor<?> child) {
		throw new UnsupportedOperationException("Use setContent(Actor).");
	}
	
	/** @deprecated setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public ScrollPane addChild(int index, Actor<?> child) {
		throw new UnsupportedOperationException("Use setContent(Actor).");
	}
	
	/** @deprecated setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public ScrollPane addChildBefore(Actor<?> childBefore, Actor<?> newChild) {
		throw new UnsupportedOperationException("Use setContent(Actor).");
	}
	
	/** @deprecated setContent(Actor)를 이용할 것 */
	@Deprecated
	@Override
	public ScrollPane addChildAfter(Actor<?> childAfter, Actor<?> newChild) {
		throw new UnsupportedOperationException("Use setContent(Actor).");
	}

	public static class ScrollPaneCostume implements Costume {
		
		public Drawable background;
		public Drawable corner;
		
		public Drawable vScrollBar;
		public Drawable vScrollKnob;
		
		public Drawable hScrollBar;
		public Drawable hScrollKnob;
		
		public ScrollPaneCostume() {
		}
		
		public ScrollPaneCostume(Costume costume) {
			set(costume);
		}

		@Override
		public void set(Costume costume) {
			if(!(costume instanceof ScrollPaneCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			ScrollPaneCostume c = (ScrollPaneCostume) costume;
			background = c.background;
			corner = c.corner;
			vScrollBar = c.vScrollBar;
			vScrollKnob = c.vScrollKnob;
			hScrollBar = c.hScrollBar;
			hScrollKnob = c.hScrollKnob;
		}

		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof ScrollPaneCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			ScrollPaneCostume c = (ScrollPaneCostume) costume;
			if(c.background != null) background = c.background;
			if(c.corner != null) corner = c.corner;
			if(c.vScrollBar != null) vScrollBar = c.vScrollBar;
			if(c.vScrollKnob != null) vScrollKnob = c.vScrollKnob;
			if(c.hScrollBar != null) hScrollBar = c.hScrollBar;
			if(c.hScrollKnob != null) hScrollKnob = c.hScrollKnob;
		}

		@Override
		public void update(long time) {
			if(background != null) background.update(time);
			if(corner != null) corner.update(time);
			if(vScrollBar != null) vScrollBar.update(time);
			if(vScrollKnob != null) vScrollKnob.update(time);
			if(hScrollBar != null) hScrollBar.update(time);
			if(hScrollKnob != null) hScrollKnob.update(time);
		}
		
	}

}
