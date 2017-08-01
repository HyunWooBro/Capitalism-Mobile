package core.scene.stage.actor.widget.table.window;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.FloatAction;
import core.scene.stage.actor.action.absolute.FadeIn;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.action.relative.ScaleBy;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.label.Label;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.table.button.Button;
import core.scene.stage.actor.widget.utils.Align;
import core.utils.Disposable;

/**
 * 
 * @author 김현우
 */
@SuppressWarnings("unchecked")
public abstract class Window<T extends Window<T>> extends Table<T> {

	public enum WindowState {
		MINIMIZED, 
		NORMAL, 
		MAXIMIZED, 
	}
	
	private static final int INVALID_POINTER_ID  = -1;
	
	protected WindowState mWindowState;
	
	protected LayoutTable mTitleTable;
	protected LayoutTable mButtonTable;
	
	protected boolean mModal;
	
	private boolean mMoveWithinFloor = true;
	
	protected boolean mMovable;
	protected boolean mMoving;
	
	protected boolean mResizable;
	protected boolean mResizing;
	
	protected boolean mFocusable = true;
	
	protected Label<?> mTitleLabel;
	
	protected float mTitleHeight;
	
	private long mAnimationDuration = 200;
	
	private float mFloorBackgroundAlpha = 0f;
	
	private boolean mConstructed;
	
	private boolean mOpening;
	private boolean mClosing;

	public Window(WindowCostume costume) {
		super(costume);
		mConstructed = true;
		
		addEventCaptureListener(new GestureTouchListener() {
			
			private int mActivePointerID = INVALID_POINTER_ID;
			
			@Override
			public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
				if(mActivePointerID != INVALID_POINTER_ID) return false;
				
				if(mFocusable) toFront();
				
				float width = getWidth();
				float height = getPadTop();
				if(mMovable && (x >= 0 && x <= width && y >= 0 && y <= height)) {
					mActivePointerID = event.getPointerID();
					mMoving = true;
					return true;
				}
				return false;
			}
			
			@Override
			public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
				mActivePointerID = INVALID_POINTER_ID;
				mMoving = false;
			}
			
			@Override
			public void onScroll(TouchEvent event, float distanceX, float distanceY, float x, float y, Actor<?> listener) {
				if(!mMoving) return;
				moveBy(-distanceX, -distanceY);
				moveWithinFloor();
			}
			
			private void moveWithinFloor() {
				if(!mMoveWithinFloor) return;
				
				Rectangle rectangle = getFloor().getCamera().getVisibleRectangle();
				float left = rectangle.left();
				float right = rectangle.right();
				float top = rectangle.top();
				float bottom = rectangle.bottom();
				
				float x = getX();
				float y = getY();
				float width = getWidth();
				float height = getHeight();
				
				if(x < left) 					x = left;
				if(x > right - width) 		x = right - width;
				if(y < top) 					y = top;
				if(y > bottom - height) 	y = bottom - height;
				
				moveTo(x, y);
			}
		});
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new WindowCostume(costume) : new WindowCostume();
	}

	@Override
	public WindowCostume getCostume() {
		return (WindowCostume) mCostume;
	}
	
	@Override
	public void costumeChanged() {
		Drawable background = getCostume().background;
		mDrawable = background;
		if(background != null) {
			padTop(background.getPadTop());
			padLeft(background.getPadLeft());
			padRight(background.getPadRight());
			padBottom(background.getPadBottom());
		}
		invalidateHierarchy();
		if(mConstructed) pack();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		// 폰트가 윈도우 위에 출력되는 것을 방지한다.
		batch.flush();
		// Floor 백그라운드 이미지를 출력한다.
		drawFloorBackground(batch, parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	private void drawFloorBackground(Batch batch, float parentAlpha) {
		if(!mModal) return;
		
		Drawable background = getCostume().floorBackground;
		if(background == null) return;
		
		Rectangle rectangle = getFloor().getCamera().getVisibleRectangle();
		
		batch.setColor(mFloorBackgroundAlpha * parentAlpha * background.getAlpha(), 0f, 0f, 0f);
		background.draw(batch, rectangle.x, rectangle.y, rectangle.width, rectangle.height, false, false);
	}
	
	@Override
	public Actor<?> contact(float x, float y) {
		Actor<?> contact = super.contact(x, y);
		if(contact == null && mModal && mTouchable == Touchable.ENABLED) return this;
		return contact;
	}

	@Override
	public void layout() {
		super.layout();
		
		float width = getWidth();
		float padLeft = getPadLeft();
		float padRight = getPadRight();
		float padTop = getPadTop();
		mTitleTable.setBounds(padLeft, padTop-mTitleHeight, width-padLeft-padRight, mTitleHeight);
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		float width = super.getDefaultPrefWidth();
		if(mTitleLabel == null) return width;
		width = Math.max(width, getPadLeft() + mTitleLabel.getMinWidth() + getPadRight());
		return width;
	}
	
	public Label<?> getTitle(){
		return mTitleLabel;
	}
	
	public T setTitle(Label<?> titleLabel) {
		return setTitle(titleLabel, Align.WEST);
	}
	
	public T setTitle(Label<?> titleLabel, Align align) {
		if(mTitleTable == null) {
			mTitleTable = new LayoutTable();
			mTitleTable.setTouchable(false);
			mTitleTable.addCell();
			addChild(mTitleTable);
		}
		
		if(mTitleLabel != null) mTitleTable.removeChild(mTitleLabel);
		TableCell cell = mTitleTable.getCellList().get(0);
		cell.setActor(titleLabel);
		mTitleLabel = titleLabel;
		mTitleTable.setAlign(align);
		return (T) this;
	}
	
	public T setTitleHeight(float height) {
		mTitleHeight = height;
		return (T) this;
	}

	public boolean isMovable() {
		return mMovable;
	}

	public T setMovable(boolean movable) {
		mMovable = movable;
		return (T) this;
	}
	
	public T open() {
		mOpening = true;
		setVisible(true);
		final boolean oldStrageTouchable = getFloor().getStage().isTouchable();
		final Touchable oldTouchable = getTouchable();
		if(mModal) 	getFloor().getStage().setTouchable(false);
		else				setTouchable(false);
		scaleTo(0.85f);
		addAction(new ScaleBy(0.15f, 0.15f, mAnimationDuration));
		addAction(new FadeIn(mAnimationDuration)
				.setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						mOpening = false;
						if(mModal) 	getFloor().getStage().setTouchable(oldStrageTouchable);
						else				setTouchable(oldTouchable);
					}
				}));
		if(mModal) addAction(new FadeAction(true));
		return (T) this;
	}
	
	public T close() {
		mClosing = true;
		final boolean oldStageTouchable = getFloor().getStage().isTouchable();
		final Touchable oldTouchable = getTouchable();
		if(mModal) 	getFloor().getStage().setTouchable(false);
		else				setTouchable(false);
		addAction(new ScaleBy(-0.15f, -0.15f, mAnimationDuration));
		addAction(new FadeOut(mAnimationDuration)
				.setActionListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						mClosing = false;
						setVisible(false);
						if(mModal)	getFloor().getStage().setTouchable(oldStageTouchable);
						else				setTouchable(oldTouchable);
					}
				}));
		if(mModal) addAction(new FadeAction(false));
		return (T) this;
	}

	public boolean isModal() {
		return mModal;
	}

	public boolean isMoving() {
		return mMoving;
	}

	public boolean isResizable() {
		return mResizable;
	}

	public boolean isResizing() {
		return mResizing;
	}

	public boolean isFocusable() {
		return mFocusable;
	}

	public float getTitleHeight() {
		return mTitleHeight;
	}
	
	public T addButton(Button<?> button) {
		mButtonTable.addCell(button);
		return (T) this;
	}

	public LayoutTable getButtonTable() {
		return mButtonTable;
	}

	public T setModal(boolean modal) {
		mModal = modal;
		return (T) this;
	}

	public T setResizable(boolean resizable) {
		mResizable = resizable;
		return (T) this;
	}

	public T setFocusable(boolean focusable) {
		mFocusable = focusable;
		return (T) this;
	}

	public long getAnimationDuration() {
		return mAnimationDuration;
	}

	public void setAnimationDuration(long duration) {
		mAnimationDuration = duration;
	}

	public LayoutTable getTitleTable() {
		return mTitleTable;
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
	
	private static class FadeAction extends FloatAction {
	    
	    public FadeAction(boolean open) {
	    	mTo = (open)? 0.4f : 0f;
			setDuration(250);
		}
		
		@Override
		protected void initialize() {
			Window<?> window = (Window<?>) mActor;
			mFrom = window.mFloorBackgroundAlpha;
		}
		
		@Override
		protected void apply(float interpolatedTime) {
			super.apply(interpolatedTime);
			Window<?> window = (Window<?>) mActor;
	    	window.mFloorBackgroundAlpha = mValue;
		}
	}
	
	public static class WindowCostume implements Costume {
		
		public Drawable background;
		public Drawable floorBackground;
		
		public WindowCostume() {
		}
		
		public WindowCostume(Costume costume) {
			set(costume);
		}
		
		@Override
		public void set(Costume costume) {
			if(!(costume instanceof WindowCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			WindowCostume c = (WindowCostume) costume;
			background = c.background;
			floorBackground = c.floorBackground;
		}
		
		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof WindowCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			WindowCostume c = (WindowCostume) costume;
			if(c.background != null) background = c.background;
			if(c.floorBackground != null) floorBackground = c.floorBackground;
		}

		@Override
		public void update(long time) {
			if(background != null) background.update(time);
			if(floorBackground != null) floorBackground.update(time);
		}
	}

	public boolean isOpening() {
		return mOpening;
	}

	public boolean isClosing() {
		return mClosing;
	}

}
