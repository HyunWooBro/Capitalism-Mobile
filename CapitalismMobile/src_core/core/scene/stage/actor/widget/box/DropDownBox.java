package core.scene.stage.actor.widget.box;

import java.util.List;

import core.framework.graphics.batch.Batch;
import core.math.Rectangle;
import core.math.Vector2;
import core.scene.stage.Floor;
import core.scene.stage.Stage;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.FadeIn;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.action.relative.ScaleBy;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.ClickTouchListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.box.ListBox.ListBoxCostume;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.scene.stage.actor.widget.utils.Align.RectangleAlignable;
import core.scene.stage.actor.widget.utils.Selector;

public class DropDownBox extends Box<DropDownBox> implements RectangleAlignable<DropDownBox> {
	
	public static final int SHOW_ITEMS_AS_MANY_AS_POSSIBLE = -1;
	
	private DropDownListBox mListBox;
	
	private Actor<?> mSelectedActor;
	
	private int mSelectedIndex;
	
	private Align mAlign = new Align(Align.CENTER);
	
	private float mSelectedActorX;
	private float mSelectedActorY;
	
	private int mMaxVisibleItems = SHOW_ITEMS_AS_MANY_AS_POSSIBLE;
	
	private long mAnimationDuration = 200;
	
	private ClickTouchListener mClickTouchListener;
	
	private int mNumVisibleItems;
	
	/** 비활성 상태 */
	private boolean mDisabled;
	
	private boolean mConstructed;

	public DropDownBox(Costume costume) {
		super(costume);
		mListBox = new DropDownListBox(getCostume().listBox, this);
		mConstructed = true;
		
		addEventListener(mClickTouchListener = new ClickTouchListener() {
			
			@Override
			public void click(TouchEvent event, float x, float y, Actor<?> listener) {
				mListBox.open();
			}
		});
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new DropDownBoxCostume(costume) : new DropDownBoxCostume();
	}

	/** 수정한 Drawable이 사이즈에 반영되길 원한다면 {@link #pack()}을 호출해야 한다. */
	@Override
	public DropDownBoxCostume getCostume() {
		return (DropDownBoxCostume) mCostume;
	}
	
	@Override
	public void costumeChanged() {
		mDrawable = getCostume().background;
		invalidateHierarchy();
		if(mConstructed) {
			mListBox.costumeChanged();
			pack();
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
	public void layout() {
		
		float padTop = 0f;
		float padLeft = 0f;
		float padRight = 0f;
		float padBottom = 0f;
		DropDownBoxCostume costume = getCostume();
		if(costume.background != null) {
			padTop = costume.background.getPadTop();
			padLeft = costume.background.getPadLeft();
			padRight = costume.background.getPadRight();
			padBottom = costume.background.getPadBottom();
		}
		
		float width = getWidth();
		float height = getHeight();
		
		float contentWidth = width - padLeft - padRight;
		float contentHeight = height - padTop - padBottom;
		
		float childWidth = 0f;
		float childHeight = 0f;
		if(mSelectedActor != null) {
			childWidth = mSelectedActor.getWidth();
			childHeight = mSelectedActor.getHeight();
		}
		
		switch(mAlign.getHAlign()) {
			case LEFT:		mSelectedActorX = 0f;
				break;
			case CENTER:	mSelectedActorX = (contentWidth - childWidth)/2;
				break;
			case RIGHT:		mSelectedActorX = contentWidth - childWidth;
				break;
		}
		
		switch(mAlign.getVAlign()) {
			case TOP:		mSelectedActorY = 0f;
				break;
			case CENTER:	mSelectedActorY = (contentHeight - childHeight)/2;
				break;
			case BOTTOM:	mSelectedActorY = contentHeight - childHeight;
				break;
		}
		
		mListBox.setWidth(width);
	}
	
	@Override
	protected float getDefaultPrefWidth() {
		float width = 0f;
		float padLeft = 0f;
		float padRight = 0f;
		DropDownBoxCostume costume = getCostume();
		if(costume.background != null) {
			width = costume.background.getWidth();
			padLeft = costume.background.getPadLeft();
			padRight = costume.background.getPadRight();
		}
		width = Math.max(width, mListBox.getTable().getPrefWidth() + padLeft + padRight);
		return width;
	}

	@Override
	protected float getDefaultPrefHeight() {
		float height = 0f;
		float padTop = 0f;
		float padBottom = 0f;
		DropDownBoxCostume costume = getCostume();
		if(costume.background != null) {
			height = costume.background.getHeight();
			padTop = costume.background.getPadTop();
			padBottom = costume.background.getPadBottom();
		}
		Float itemHeight = mListBox.getItemHeight();
		if(itemHeight != null) height = Math.max(height, itemHeight + padTop + padBottom);
		return height;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		DropDownBoxCostume costume = getCostume();
		if(mDisabled)														mDrawable = costume.disabledBackground;
		else if(mClickTouchListener.isVisualPressed())	mDrawable = costume.pressedBackground;
		else																	mDrawable = costume.background;
		super.draw(batch, parentAlpha);
	}
	
	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if(mSelectedActor == null) return;
		
		float padTop = 0f;
		float padLeft = 0f;
		DropDownBoxCostume costume = getCostume();
		if(costume.background != null) {
			padTop = costume.background.getPadTop();
			padLeft = costume.background.getPadLeft();
		}
		
		float x = mSelectedActor.getX();
		float y = mSelectedActor.getY();
		mSelectedActor.moveTo(mSelectedActorX+padLeft, mSelectedActorY+padTop);
		mSelectedActor.draw(batch, parentAlpha);
		mSelectedActor.moveTo(x, y);
	}
	
	@Override
	public Actor<?> contact(float x, float y) {
		if(mDisabled) return null;
		return super.contact(x, y);
	}

	public void openList() {
		mListBox.open();
	}

	public void closeList() {
		mListBox.close();
	}
	
	public int getMaxVisibleItems() {
		return mMaxVisibleItems;
	}

	public DropDownBox setMaxVisibleItems(int maxVisibleItems) {
		if(maxVisibleItems < 0)		mMaxVisibleItems = SHOW_ITEMS_AS_MANY_AS_POSSIBLE;
		else									mMaxVisibleItems = maxVisibleItems;
		calculateNumVisibleItems();
		return this;
	}

	private void calculateNumVisibleItems() {
		int itemSize = mListBox.getItemList().size();
		mNumVisibleItems = (mMaxVisibleItems == SHOW_ITEMS_AS_MANY_AS_POSSIBLE)? 
				itemSize : Math.min(mMaxVisibleItems, itemSize);
	}

	@Override
	public DropDownBox center() {
		mAlign.center();
		return this;
	}

	@Override
	public DropDownBox top() {
		mAlign.top();
		return null;
	}

	@Override
	public DropDownBox left() {
		mAlign.left();
		return this;
	}

	@Override
	public DropDownBox right() {
		mAlign.right();
		return this;
	}

	@Override
	public DropDownBox bottom() {
		mAlign.bottom();
		return this;
	}

	@Override
	public DropDownBox north() {
		mAlign.north();
		return this;
	}

	@Override
	public DropDownBox west() {
		mAlign.west();
		return this;
	}

	@Override
	public DropDownBox east() {
		mAlign.east();
		return this;
	}

	@Override
	public DropDownBox south() {
		mAlign.south();
		return this;
	}

	@Override
	public DropDownBox northWest() {
		mAlign.northWest();
		return this;
	}

	@Override
	public DropDownBox northEast() {
		mAlign.northEast();
		return this;
	}

	@Override
	public DropDownBox southWest() {
		mAlign.southWest();
		return this;
	}

	@Override
	public DropDownBox southEast() {
		mAlign.southEast();
		return this;
	}

	@Override
	public Align getAlign() {
		return mAlign;
	}

	@Override
	public DropDownBox setAlign(Align align) {
		mAlign.set(align);
		return this;
	}

	public long getAnimationDuration() {
		return mAnimationDuration;
	}

	public DropDownBox setAnimationDuration(long animationDuration) {
		mAnimationDuration = animationDuration;
		return this;
	}
	
	public Float getItemHeight() {
		return mListBox.getItemHeight();
	}

	public DropDownBox setItemHeight(float height) {
		mListBox.setItemHeight(height);
		return this;
	}

	public ListBox getListBox() {
		return mListBox;
	}

	public DropDownBox setDividerHeight(float height) {
		mListBox.setDividerHeight(height);
		return this;
	}

	public boolean isDisabled() {
		return mDisabled;
	}

	public DropDownBox setDisabled(boolean disabled) {
		mDisabled = disabled;
		return this;
	}
	
	public DropDownBox setItemHAlign(HAlign align) {
		mListBox.setItemHAlign(align);
		return this;
	}
	
	public DropDownBox setItemPadding(float top, float left, float right, float bottom) {
		mListBox.setItemPadding(top, left, right, bottom);
		return this;
	}
	
	@Override
	public void disposeAll() {
		super.disposeAll();
		mListBox.disposeAll();
	}

	private static class DropDownListBox extends ListBox {
		
		private DropDownBox mDropDownBox;
		
		private Floor mTempFloor;
		
		private boolean mOpened;
	
		public DropDownListBox(ListBoxCostume costume, DropDownBox dropDownBox) {
			super(costume);
			
			this.mDropDownBox = dropDownBox;
			
			addEventListener(new ChangeListener() {
				
				@Override
				public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
					if(!event.isTargetActor()) return;
					
					if(mDropDownBox.fire(new ChangeEvent())) {
						mDropDownBox.mSelectedActor = mSelector.first();
						// 선택된 아이템을 정렬하기 위해 무효화한다.
						mDropDownBox.invalidate();
					} else
						event.cancel();
				}
			});
			
			addEventListener(new GestureTouchListener() {
				
				@Override
				public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
					if(contactSelf(x, y) == null) onItemSelected();
					return false;
				}
			});
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
		protected void onItemChanged() {
			mDropDownBox.calculateNumVisibleItems();
		}
		
		private void computeBounds() {

			Floor floor = getFloor();
			float viewportHeight = floor.getCamera().getViewportHeight();
			Rectangle rectangle = floor.getCamera().getVisibleRectangle();
			
			final Vector2 v = VECTOR;
			mDropDownBox.localToFloorCoordinates(v.set(0f, 0f));
			
			float dropDownBoxHeight = mDropDownBox.getHeight();
			
			float heightAbove = v.y - rectangle.y;
			if(heightAbove <= 0f) heightAbove = 0f;
			
			float heightBelow = viewportHeight - heightAbove - dropDownBoxHeight;
			if(heightBelow <= 0f) heightBelow = 0f;
			
			
			int numVisibleItems = mDropDownBox.mNumVisibleItems;
			float height = numVisibleItems * (mItemHeight + mItemPadTop + mItemPadBottom);
			if(mDividerHeight > 0f) height += (numVisibleItems-1) * mDividerHeight;
			
			
			boolean below = true;
			
			if(height > heightBelow) {
				if(heightBelow >= heightAbove) {
					height = heightBelow;
				} else {
					below = false;
					height = Math.min(height, heightAbove);
				}
			}
			
			setHeight(height);
			mScroll.setHeight(height);
			mTable.setHeight(height);
			
			float offsetX = 0f;
			float offsetY = 0f;
			DropDownBoxCostume costume = mDropDownBox.getCostume();
			if(below) {
				if(costume.offsetBelow != null) {
					offsetX = costume.offsetBelow.x;
					offsetY = costume.offsetBelow.y;
				}
			} else {
				if(costume.offsetAbove != null) {
					offsetX = costume.offsetAbove.x;
					offsetY = costume.offsetAbove.y;
				}
			}
			
			setX(v.x + offsetX);
			
			if(below)		setY(v.y + dropDownBoxHeight + offsetY);
			else				setY(v.y - height + offsetY);
			
			pivotTo(0f, (below)? 0f : 1f);
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			batch.flush();
			computeBounds();
			super.draw(batch, parentAlpha);
		}
		
		@Override
		public Actor<?> contact(float x, float y) {
			Actor<?> contact = super.contact(x, y);
			if(contact == null && mTouchable == Touchable.ENABLED) return this;
			return contact;
		}
	
		@Override
		protected void onItemSelected() {
			close();
			setTouchable(false);
		}
	
		public void open() {
			if(mOpened) return;
			mOpened = true;
			
			Floor floor = mDropDownBox.getFloor();
			if(floor == null) return;
			Stage stage = floor.getStage();
			mTempFloor = stage.addFloor().addChild(this);
			mTempFloor.setCamera(floor.getCamera());
			
			// 선택된 아이템이 중앙에 배치되도록 한다.
			scrollToCenter(getSelectedIndex());
			// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
			mScroll.startScrollFade();
			
			setTouchable(false);
			scaleTo(0.85f);
			long duration = mDropDownBox.mAnimationDuration;
			addAction(new ScaleBy(0.15f, 0.15f, duration));
			addAction(new FadeIn(duration)
					.setActionListener(new ActionListener() {
						@Override
						public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
							setTouchable(true);
						}
					}));
		}
		
		public void close() {
			if(!mOpened) return;
			
			long duration = mDropDownBox.mAnimationDuration;
			addAction(new ScaleBy(-0.15f, -0.15f, duration));
			addAction(new FadeOut(duration)
					.setActionListener(new ActionListener() {
						@Override
						public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
							mOpened = false;
							
							removeFromParent();
							
							/*Floor floor = getFloor();
							Stage stage = floor.getStage();
							stage.removeFloor(mTempFloor);*/
							
							
						}
					}));
		}

		@Override
		protected BoxItem createBoxItem(Table<?> table, Actor<?> item) {
			return new UntouchableBoxItem(mTable, item);
		}
		
		private static class UntouchableBoxItem extends BoxItem {
	
			public UntouchableBoxItem(Table<?> table, Actor<?> item) {
				super(table, item);
				setUntouchable(item);
			}
			
			@Override
			public TableCell setActor(Actor<?> actor) {
				setUntouchable(actor);
				return super.setActor(actor);
			}
			
			private void setUntouchable(Actor<?> actor) {
				if(actor != null) actor.setTouchable(false);
			}
			
		}
		
	}

	public static class DropDownBoxCostume implements Costume {
		
		public Drawable background;
		public Drawable pressedBackground;
		public Drawable disabledBackground;
		
		public ListBoxCostume listBox;
		
		public Vector2 offsetAbove;
		public Vector2 offsetBelow;
		
		public DropDownBoxCostume() {
		}
		
		public DropDownBoxCostume(Costume costume) {
			set(costume);
		}
	
		@Override
		public void set(Costume costume) {
			if(!(costume instanceof DropDownBoxCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			DropDownBoxCostume c = (DropDownBoxCostume) costume;
			background = c.background;
			pressedBackground = (c.pressedBackground == null)? c.background : c.pressedBackground;
			disabledBackground = (c.disabledBackground == null)? c.background : c.disabledBackground;
			listBox = c.listBox;
			
			if(c.offsetAbove != null) {
				if(offsetAbove != null) {
					offsetAbove.set(c.offsetAbove);
				} else
					offsetAbove = new Vector2(c.offsetAbove);
			} else
				offsetAbove = null;
			
			if(c.offsetBelow != null) {
				if(offsetBelow != null) {
					offsetBelow.set(c.offsetBelow);
				} else
					offsetBelow = new Vector2(c.offsetBelow);
			} else
				offsetBelow = null;
		}
	
		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof DropDownBoxCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			DropDownBoxCostume c = (DropDownBoxCostume) costume;
			if(c.background != null) background = c.background;
			if(c.pressedBackground != null) pressedBackground = c.pressedBackground;
			if(c.disabledBackground != null) disabledBackground = c.disabledBackground;
			if(c.listBox != null) listBox = c.listBox;
			
			if(c.offsetAbove != null) {
				if(offsetAbove != null) {
					offsetAbove.set(c.offsetAbove);
				} else
					offsetAbove = new Vector2(c.offsetAbove);
			}
			
			if(c.offsetBelow != null) {
				if(offsetBelow != null) {
					offsetBelow.set(c.offsetBelow);
				} else
					offsetBelow = new Vector2(c.offsetBelow);
			}
		}
	
		@Override
		public void update(long time) {
		}
		
	}

	@Override
	public DropDownBox set(Actor<?>[] items) {
		mListBox.set(items);
		return this;
	}

	@Override
	public DropDownBox set(List<Actor<?>> itemList) {
		mListBox.set(itemList);
		return this;
	}

	@Override
	public Actor<?> getItem(int index) {
		mListBox.getItem(index);
		return this;
	}

	@Override
	public List<Actor<?>> getItemList() {
		return mListBox.getItemList();
	}

	@Override
	public DropDownBox addItem(Actor<?> item) {
		mListBox.addItem(item);
		return this;
	}

	@Override
	public DropDownBox addItem(int index, Actor<?> item) {
		mListBox.addItem(index, item);
		return this;
	}

	@Override
	public DropDownBox addAll(List<Actor<?>> itemList) {
		mListBox.addAll(itemList);
		return this;
	}

	@Override
	public DropDownBox addAll(int index, List<Actor<?>> itemList) {
		mListBox.addAll(index, itemList);
		return this;
	}

	@Override
	public DropDownBox removeItem(int index) {
		mListBox.removeItem(index);
		return this;
	}

	@Override
	public DropDownBox removeItem(Actor<?> item) {
		mListBox.removeItem(item);
		return this;
	}

	@Override
	public DropDownBox removeAll(int startIndex, int endIndex) {
		mListBox.removeAll(startIndex, endIndex);
		return this;
	}

	@Override
	public DropDownBox removeAll(List<Actor<?>> itemList) {
		mListBox.removeAll(itemList);
		return this;
	}

	@Override
	public DropDownBox clearItems() {
		mListBox.clearItems();
		return this;
	}

	@Override
	public DropDownBox swapItems(int index1, int index2) {
		mListBox.swapItems(index1, index2);
		return this;
	}

	@Override
	public DropDownBox swapItems(Actor<?> item1, Actor<?> item2) {
		mListBox.swapItems(item1, item2);
		return this;
	}

	@Override
	public DropDownBox reverseAll(List<Actor<?>> itemList) {
		mListBox.reverseAll(itemList);
		return this;
	}

	@Override
	public DropDownBox select(int index) {
		mListBox.select(index);
		return this;
	}

	@Override
	public DropDownBox select(Actor<?> item) {
		mListBox.select(item);
		return this;
	}

	@Override
	public int getSelectedIndex() {
		return mListBox.getSelectedIndex();
	}

	@Override
	public Actor<?> getSelectedItem() {
		return mListBox.getSelectedItem();
	}

	@Override
	public Selector<?> getSelector() {
		return mListBox.getSelector();
	}

}
