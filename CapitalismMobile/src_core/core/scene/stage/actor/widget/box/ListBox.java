package core.scene.stage.actor.widget.box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.math.MathUtils;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.ScrollPane;
import core.scene.stage.actor.widget.ScrollPane.ScrollPaneCostume;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.table.VerticalTable;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.scene.stage.actor.widget.utils.Layout;
import core.scene.stage.actor.widget.utils.Selector;
import core.utils.SnapshotArrayList;

/**
 * {@link #set(Actor[])}, {@link #addItem(Actor)} 등으로 추가하기 전에 정렬, 아이템 높이 등을 먼저 
 * 설정하는 것이 좋다. 그렇지 않으면 다시 테이블을 재생성해야 하기 때문이다.
 * 
 * @author 김현우
 */
public class ListBox extends Box<ListBox> {
	
	private static final List<Actor<?>> TMP_ITEM_LIST = new ArrayList<Actor<?>>();
	
	private static final int INVALID_INDEX  = -1;

	protected ScrollPane mScroll;
	
	protected VerticalTable mTable;
	
	protected int mInstantSelectedIndex = INVALID_INDEX;
	
	protected Image mSelectorImage = new SelectorImage(this);
	
	protected Image mInstantSelectorImage = new Image();
	
	protected List<Actor<?>> mItemList = new ArrayList<Actor<?>>();
	
	protected float mItemHeight;
	protected boolean mItemHeightSetByUser;
	
	protected float mDividerHeight = 1f;
	
	protected Align mItemAlign = new Align();
	
	protected float mItemPadTop;
	protected float mItemPadLeft;
	protected float mItemPadRight;
	protected float mItemPadBottom;
	
	protected float mItemFillX;
	
	protected ListBoxSelector mSelector = new ListBoxSelector(this);
	
	private boolean mConstructed;
	
	public ListBox(ListBoxCostume costume) {
		super(costume);
		mTable = new VerticalTable()
				.top()
				.addChild(mSelectorImage)
				.addChild(mInstantSelectorImage);
		mTable.all().expandX();
		mScroll = new ScrollPane(mTable, getCostume().scroll);
		add(0, mScroll);
		mConstructed = true;
		
		addEventListener(new GestureTouchListener() {
			
			@Override
			public void onShowPress(TouchEvent event, float x, float y, Actor<?> listener) {
				float padTop = 0f;
				float padLeft = 0f;
				ScrollPaneCostume costume = mScroll.getCostume();
				if(costume.background != null) {
					padTop = costume.background.getPadTop();
					padLeft = costume.background.getPadLeft();
				}
				
				List<TableCell> cellList = mTable.getCellList();
				int n = cellList.size();
				for(int i=0; i<n; i++) {
					TableCell cell = cellList.get(i);
					if(cell.getCellRectangle().contains(x+mScroll.getScrollX()-padLeft, y+mScroll.getScrollY()+padTop)) {
						// BoxItem이 아닌 divider인 경우는 무시한다.
						if(!(cell instanceof BoxItem)) return;
						mInstantSelectedIndex = i;
						break;
					}
				}
			}
			
			@Override
			public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {
				float padTop = 0f;
				float padLeft = 0f;
				ScrollPaneCostume costume = mScroll.getCostume();
				if(costume.background != null) {
					padTop = costume.background.getPadTop();
					padLeft = costume.background.getPadLeft();
				}
				
				List<TableCell> cellList = mTable.getCellList();
				int n = cellList.size();
				for(int i=0; i<n; i++) {
					TableCell cell = cellList.get(i);
					if(cell.getCellRectangle().contains(x+mScroll.getScrollX()-padLeft, y+mScroll.getScrollY()+padTop)) {
						// BoxItem이 아닌 divider인 경우는 무시한다.
						if(!(cell instanceof BoxItem)) return;
						BoxItem boxItem = (BoxItem) cell;
						select(boxItem.index);
						break;
					}
				}
			}
			
			@Override
			public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
				mInstantSelectedIndex = INVALID_INDEX;
			}

		});
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new ListBoxCostume(costume) : new ListBoxCostume();
	}

	/** 수정한 Drawable이 사이즈에 반영되길 원한다면 {@link #pack()}을 호출해야 한다. */
	@Override
	public ListBoxCostume getCostume() {
		return (ListBoxCostume) mCostume;
	}
	
	@Override
	public void costumeChanged() {
		//mDrawable = getCostume().background;
		invalidateHierarchy();
		if(mConstructed) {
			mScroll.costumeChanged();
			pack();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SnapshotArrayList<Actor<?>> getChildList() {
		return (SnapshotArrayList<Actor<?>>) mChildList.clone();
	}
	
	private void validateItem(Actor<?> item) {
		if(item == null) throw new IllegalArgumentException("item can't be null.");
	}
	
	/** 아이템 중에 null이 있는지 검증한다. */
	private void validateItemList(List<Actor<?>> itemList) {
		int n = itemList.size();
		for(int i=0; i<n; i++) {
			Actor<?> item = itemList.get(i);
			if(item == null) throw new IllegalArgumentException("item can't be null.");
		}
	}

	@Override
	public ListBox set(Actor<?>[] items) {
		return set(Arrays.asList(items));
	}
	
	@Override
	public ListBox set(List<Actor<?>> itemList) {
		validateItemList(itemList);
		clearItems();
		
		int n = itemList.size();
		for(int i=0; i<n; i++) {
			Actor<?> item = itemList.get(i);
			_add(i, item);
		}
		
		rearrangeItemIndex();
		onItemChanged();
		return this;
	}
	
	@Override
	public Actor<?> getItem(int index) {
		return mItemList.get(index);
	}
	
	@Override
	public List<Actor<?>> getItemList() {
		return mItemList;
	}
	
	@Override
	public ListBox addItem(Actor<?> item) {
		return addItem(mItemList.size(), item);
	}

	@Override
	public ListBox addItem(int index, Actor<?> item) {
		validateItem(item);
		if(!_add(index, item)) return this;
		
		rearrangeItemIndex();
		onItemChanged();
		return this;
	}
	
	@Override
	public ListBox addAll(List<Actor<?>> itemList) {
		return addAll(mItemList.size(), itemList);
	}

	@Override
	public ListBox addAll(int index, List<Actor<?>> itemList) {
		validateItemList(itemList);
		
		boolean changed = true;
		int n = itemList.size();
		for(int i=0; i<n; i++) {
			Actor<?> item = itemList.get(i);
			if(!_add(index, item)) changed = false;
		}
		
		if(!changed) return this;
		
		rearrangeItemIndex();
		onItemChanged();
		return this;
	}

	private boolean _add(int index, Actor<?> item) {
		if(mItemList.contains(item)) return false;
		
		validateItemHeight(item);
		
		mItemList.add(index, item);

		boolean begin = mItemList.size() == 1;
		boolean end = mItemList.size() == index+1;
		float dividerHeight = mDividerHeight;
		
		BoxItem boxItem = createBoxItem(mTable, item);
		boxItem.index = index;
		
		int realIndex = index;
		if(dividerHeight > 0f) realIndex += index;
		if(!begin && end) {
			realIndex--;
			if(dividerHeight > 0f) addDivider(realIndex++);
		}
		mTable.addCell(realIndex, boxItem)
				.height(mItemHeight).fillX(mItemFillX)
				.padTop(mItemPadTop).padLeft(mItemPadLeft).padRight(mItemPadRight).padBottom(mItemPadBottom)
				.setAlign(mItemAlign);
		if(!end && dividerHeight > 0f) addDivider(realIndex+1);
		return true;
	}
	
	private void validateItemHeight(Actor<?> item) {
		if(mItemHeightSetByUser) return;
		
		float itemHeight = (item instanceof Layout)? ((Layout<?>) item).getPrefHeight() : item.getHeight();
		if(mItemHeight < itemHeight) {
			mItemHeight = itemHeight;
			rebuild();
		}
	}

	private void addDivider(int index) {
		if(getCostume().divider != null) {
			// 선호 너비를 1f로 지정한 이유는 divider가 너무 큰 경우 pack() 했을 때 divider의 너비를
			// 기준으로 ListBox의 너비가 지정되기 때문이다.
			mTable.addCell(index, new Image(getCostume().divider)).actorPrefWidth(1f).height(mDividerHeight).fillX();
		} else
			mTable.addCell(index).height(mDividerHeight);
	}

	@Override
	public ListBox removeItem(int index) {
		return removeItem(mItemList.get(index));
	}

	@Override
	public ListBox removeItem(Actor<?> item) {
		if(!_remove(item)) return this;
		
		mSelector.ignoreCancel = true;
		mSelector.remove(item);
		mSelector.ignoreCancel = false;
		
		rearrangeItemIndex();
		onItemChanged();
		return this;
	}
	
	@Override
	public ListBox removeAll(int startIndex, int endIndex) {
		if(startIndex > endIndex)
			throw new IllegalArgumentException("startIndex can't be bigger than endIndex.");
		
		int n = endIndex - startIndex + 1;
		final List<Actor<?>> itemList = TMP_ITEM_LIST;
		itemList.clear();
		for(int i=0; i<n; i++) itemList.add(mItemList.get(startIndex + i));
		return removeAll(itemList);
	}

	@Override
	public ListBox removeAll(List<Actor<?>> itemList) {
		boolean changed = true;
		int n = itemList.size();
		for(int i=0; i<n; i++) {
			Actor<?> item = itemList.get(i);
			if(!_remove(item)) changed = false;
		}
		
		if(!changed) return this;
		
		// 선택취소를 cancel하는 경우를 방지하기 위해 임시적으로 무시한다.
		mSelector.ignoreCancel = true;
		mSelector.removeAll(itemList);
		mSelector.ignoreCancel = false;
		
		rearrangeItemIndex();
		onItemChanged();
		return this;
	}

	@Override
	public ListBox clearItems() {
		boolean changed = true;
		List<Actor<?>> itemList = mItemList;
		int n = itemList.size();
		for(int i=n-1; i>-1; i--) {
			Actor<?> item = itemList.get(i);
			if(!_remove(item)) changed = false;
		}
		
		if(!changed) return this;
		
		// 선택취소를 cancel하는 경우를 방지하기 위해 임시적으로 무시한다.
		mSelector.ignoreCancel = true;
		mSelector.clear();
		mSelector.ignoreCancel = false;
		
		rearrangeItemIndex();
		onItemChanged();
		return this;
	}

	private boolean _remove(Actor<?> item) {
		int index = mItemList.indexOf(item);
		if(index == -1) return false;
		mItemList.remove(item);
		
		mTable.removeChild(item);
		
		// divider도 제거
		int realIndex = index;
		if(mDividerHeight > 0f) realIndex += index;
		removeDivider(realIndex);
		return true;
	}
	
	private void removeDivider(int index) {
		if(mTable.getCellList().size() > index) {
			if(mDividerHeight > 0f) 
				mTable.removeChild(mTable.getCellActor(index));
		} else {
			if(mDividerHeight > 0f && index > 0) 
				mTable.removeChild(mTable.getCellActor(index-1));
		}
	}

	@Override
	public ListBox swapItems(int index1, int index2) {
		return swapItems(mItemList.get(index1), mItemList.get(index2));
	}

	@Override
	public ListBox swapItems(Actor<?> item1, Actor<?> item2) {
		if(item1 == null) throw new IllegalArgumentException("item1 can't be null.");
		if(item2 == null) throw new IllegalArgumentException("item2 can't be null.");
		
		int index1 = mItemList.indexOf(item1);
		if(index1 == INVALID_INDEX) throw new IllegalArgumentException("item1 doesn't belong to ListBox.");
		
		int index2 = mItemList.indexOf(item2);
		if(index2 == INVALID_INDEX) throw new IllegalArgumentException("item2 doesn't belong to ListBox.");

		_remove(item1);
		_remove(item2);
		if(index1 < index2) {
			_add(index1, item2);
			_add(index2, item1);
		} else {
			_add(index2, item1);
			_add(index1, item2);
		}
		
		rearrangeItemIndex();
		onItemChanged();
		return this;
	}

	private void rearrangeItemIndex() {
		int index = 0;
		List<TableCell> cellList = mTable.getCellList();
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			TableCell cell = cellList.get(i);
			if(cell instanceof BoxItem) {
				BoxItem boxItem = (BoxItem) cell;
				boxItem.index = index++;
			}
		}
	}

	/** 아이템 리스트가 변경되는 경우에 호출된다. */
	protected void onItemChanged() {
	}

	@Override
	public ListBox reverseAll(List<Actor<?>> itemList) {
		mSelector.reverseAll(itemList);
		return this;
	}

	@Override
	public ListBox select(int index) {
		if(index >= 0) {
			if(mItemList.isEmpty()) return this;
			mSelector.select(mItemList.get(index));
			onItemSelected();
		} else
			mSelector.clear();
		
		return this;
	}

	@Override
	public ListBox select(Actor<?> item) {
		if(item != null) {
			if(!mItemList.contains(item)) 
				throw new IllegalArgumentException("item doesn't belong to ListBox.");
			
			mSelector.select(item);
			onItemSelected();
		} else
			mSelector.clear();
		
		return this;
	}
	
	/** 아이템이 선택되면 호출된다. 이미 선택된 아이템인지와는 관련 없다. */
	protected void onItemSelected() {
	}

	@Override
	public int getSelectedIndex() {
		return mItemList.indexOf(mSelector.first());
	}

	@Override
	public Actor<?> getSelectedItem() {
		return mSelector.first();
	}

	@Override
	public Selector<Actor<?>> getSelector() {
		return mSelector;
	}
	
	@Override
	public ListBox clear() {
		clearItems();
		return super.clear();
	}
	
	protected BoxItem createBoxItem(Table<?> table, Actor<?> item) {
		return new BoxItem(mTable, item);
	}
	
	@Override
	public void layout() {
		mScroll.sizeTo(getWidth(), getHeight());
	}

	@Override
	protected float getDefaultPrefWidth() {
		return mScroll.getPrefWidth();
	}

	@Override
	protected float getDefaultPrefHeight() {
		return mScroll.getPrefHeight();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		ListBoxCostume costume = getCostume();
		
		if(costume.selector != null) {
			mSelectorImage.setVisible(true);
			mSelectorImage.setDrawable(costume.selector);
		} else
			mSelectorImage.setVisible(false);
		
		if(costume.instantSelector != null && mInstantSelectedIndex != INVALID_INDEX) {
			mInstantSelectorImage.setVisible(true);
			mInstantSelectorImage.setDrawable(costume.instantSelector);
			TableCell cell = mTable.getCellList().get(mInstantSelectedIndex);
			mInstantSelectorImage.setBounds(cell.getCellX(), cell.getCellY(), cell.getCellWidth(), cell.getCellHeight());
		} else 
			mInstantSelectorImage.setVisible(false);
		
		super.draw(batch, parentAlpha);
	}
	
	public ListBox scrollToCenter(int itemIndex) {
		if(itemIndex == INVALID_INDEX) return this;
		
		float itemHeight = mItemHeight;
		
		float padTop = 0f;
		float padBottom = 0f;
		ScrollPaneCostume costume = mScroll.getCostume();
		if(costume.background != null) {
			mDrawable = costume.background;
			padTop = costume.background.getPadTop();
			padBottom = costume.background.getPadBottom();
		}
		
		float scrollHeight = mScroll.getHeight();
		float pureScrollHeight = scrollHeight - padTop - padBottom;
		
		
		float height = itemIndex*itemHeight + itemHeight/2;
		if(mDividerHeight > 0f) height += itemIndex*mDividerHeight;
		height -= pureScrollHeight/2;
		
		float scrollY = MathUtils.clamp(height, 0f, mScroll.getScrollRangeY());
		
		mScroll.scrollTo(mScroll.getScrollX(), scrollY);
		
		return this;
	}

	public VerticalTable getTable() {
		return mTable;
	}

	public ScrollPane getScroll() {
		return mScroll;
	}

	public ListBox setDividerHeight(float height) {
		if(mDividerHeight == height) return this;
		mDividerHeight = height;
		rebuild();
		return this;
	}

	public float getDividerHeight() {
		return mDividerHeight;
	}
	
	public float getItemHeight() {
		return mItemHeight;
	}

	public ListBox setItemHeight(float height) {
		if(mItemHeightSetByUser && mItemHeight == height) return this;
		mItemHeight = height;
		mItemHeightSetByUser = true;
		rebuild();
		return this;
	}

	public ListBox setItemHAlign(HAlign align) {
		if(mItemAlign.getHAlign() == align) return this;
		mItemAlign.set(align);
		rebuild();
		return this;
	}
	
	public ListBox setItemPadding(float top, float left, float right, float bottom) {
		if(mItemPadTop == top && mItemPadLeft == left && mItemPadRight == right && mItemPadBottom == bottom) return this;
		mItemPadTop = top;
		mItemPadLeft = left;
		mItemPadRight = right;
		mItemPadBottom = bottom;
		rebuild();
		return this;
	}
	
	/** 아이템을 포함하는 셀에 대한 {@link TableCell#fillX(float)}를 지정한다. */
	public ListBox setItemFillX(float fillX) {
		if(mItemFillX == fillX) return this;
		mItemFillX = fillX;
		rebuild();
		return this;
	}
	
	private void rebuild() {
		if(mItemList.isEmpty()) return;
		
		final List<Actor<?>> itemList = TMP_ITEM_LIST;
		itemList.clear();
		itemList.addAll(mItemList);
		int n = itemList.size();
		
		for(int i=0; i<n; i++) {
			Actor<?> item = itemList.get(i);
			_remove(item);
		}
		
		for(int i=0; i<n; i++) {
			Actor<?> item = itemList.get(i);
			_add(i, item);
		}
	}

	/*package*/ static class BoxItem extends TableCell {
		public int index;
		
		public BoxItem(Table<?> table, Actor<?> item) {
			super(table, item);
		}
	}
	
	/*package*/ static class ListBoxSelector extends Selector<Actor<?>> {
		
		public boolean ignoreCancel;
		
		public ListBoxSelector(Actor<?> actor) {
			super(actor);
		}
		
		@Override
		protected void onChangeEventCanceled() {
			if(ignoreCancel) return;
			super.onChangeEventCanceled();
		}
	}

	private static class SelectorImage extends Image {
		
		private ListBox mListBox;
		
		public SelectorImage(ListBox listBox) {
			mListBox = listBox;
		}
		
		@Override
		protected void drawSelf(Batch batch, float parentAlpha) {
			Selector<?> selector = mListBox.getSelector();
			Table<?> table = mListBox.getTable();
			
			List<?> selectionList = selector.getSelectionList();
			int n = selectionList.size();
			for(int i=0; i<n; i++) {
				Actor<?> item = (Actor<?>) selectionList.get(i);
				TableCell cell = table.getCellByActor(item);
				setBounds(cell.getCellX(), cell.getCellY(), cell.getCellWidth(), cell.getCellHeight());
				super.drawSelf(batch, parentAlpha);
			}
		}
	}
	
	public static class ListBoxCostume implements Costume {
		
		//public Drawable background;
		public Drawable selector;
		public Drawable instantSelector;
		public Drawable divider;
		
		public ScrollPaneCostume scroll;
		
		public ListBoxCostume() {
		}
		
		public ListBoxCostume(Costume costume) {
			set(costume);
		}

		@Override
		public void set(Costume costume) {
			if(!(costume instanceof ListBoxCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			ListBoxCostume c = (ListBoxCostume) costume;
			//background = c.background;
			selector = c.selector;
			instantSelector = c.instantSelector;
			divider = c.divider;
			scroll = c.scroll;
		}

		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof ListBoxCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			ListBoxCostume c = (ListBoxCostume) costume;
			//if(c.background != null) background = c.background;
			if(c.selector != null) selector = c.selector;
			if(c.instantSelector != null) instantSelector = c.instantSelector;
			if(c.divider != null) divider = c.divider;
			if(c.scroll != null) scroll = c.scroll;
		}

		@Override
		public void update(long time) {
			//if(background != null) background.update(time);
			if(selector != null) selector.update(time);
			if(instantSelector != null) instantSelector.update(time);
			if(divider != null) divider.update(time);
		}
		
	}

}
