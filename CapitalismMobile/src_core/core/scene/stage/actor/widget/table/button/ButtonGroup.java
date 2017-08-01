package core.scene.stage.actor.widget.table.button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.math.MathUtils;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.utils.Selector;
import core.utils.SnapshotArrayList;

/**
 * ButtonGroup은 {@link #addButton(Button)}으로 삽입된 Button의 checked된 상태를 
 * minChecks 이상이면서 maxChecks 이하로 유지한다.</p>
 * 
 * {@link #addButton(Button)}으로 삽입하면 Button의 checked 상태는 false로 세팅되므로 
 * 필요한 Button을 모두 추가한 후 {@link #check(int)}나 {@link #check(Button)} 등을 통해  
 * 다시 지정해야 한다.</p>
 * 
 * ButtonGroup에 속한 Button이 {@link ChangeEvent}를 취소해도 checked 상태는 이벤트가 
 * 발생하기 이전 상태로 복구되지 않는다는 것을 주의하라. ButtonGroup에 의해 관리되는 
 * 여러 Button 중 하나이므로 ButtonGroup의 통제를 따르는 것이 당연하기 때문이다.</p>
 * 
 * @author 김현우
 */
public class ButtonGroup extends Table<ButtonGroup> {
	
	public static final int MAX_CHECK_UNLIMITED = -1;
	
	private List<Button<?>> mButtonList = new ArrayList<Button<?>>();
	
	private boolean mTableLayoutUsed;
	private boolean mVertical;
	private float mPadding;

	private ButtonSelector mSelector = new ButtonSelector(this);
	
	/** 
	 * 자유롭게 Button을 배치한다. Table 레이아웃을 사용하지 않는다. 따라서 각 Button은 
	 * 자신의 위치 및 사이즈를 스스로 결정해야 한다. 
	 */
	public ButtonGroup() {
		setupListener();
	}
	
	/** Table 레이아웃을 사용하여 Button을 수직 또는 수평으로 배치한다. */
	public ButtonGroup(boolean vertical, float padding) {
		mVertical = vertical;
		mPadding = padding;
		mTableLayoutUsed = true;
		setupListener();
	}
	
	private void setupListener() {
		addEventListener(new ChangeListener() {
			
			@Override
			public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
				if(!event.isTargetActor()) return;
				applySelector();
			}
		});
	}
	
	private void applySelector() {
		List<Button<?>> selectionList = mSelector.getSelectionList();
		List<Button<?>> buttonList = mButtonList;
		int n = buttonList.size();
		for(int i=0; i<n; i++) {
			Button<?> button = buttonList.get(i);
			if(selectionList.contains(button)) {
				if(!button.isChecked()) button.forceChecked(true);
			} else {
				if(button.isChecked()) button.forceChecked(false);
			}
		}
	}
	
	public ButtonGroup addButton(Button<?> button) {
		if(button.mButtonGroup != null) button.mButtonGroup.removeButton(button);
		button.mButtonGroup = this;
		mButtonList.add(button);
		button.forceChecked(false);
		if(mTableLayoutUsed) {
			if(mChildList.isEmpty()) {
				super.addCell(button);
			} else {
				if(mVertical) {
					row();
					super.addCell(button).padTop(mPadding);
				} else
					super.addCell(button).padLeft(mPadding);
			}
		} else
			super.addChild(button);
		
		return  this;
	}
	
	/** 
	 * 반환된 버튼 리스트를 수정하는 것은 어떤 효과도 없으므로 {@link #addButton(Button)} 
	 * 또는 {@link #removeButton(Button)}을 이용하라. 
	 */
	public List<Button<?>> getButtonList() {
		return mButtonList;
	}
	
	public boolean removeButton(Button<?> button) {
		if(button == null) return false;
		button.mButtonGroup = null;
		mButtonList.remove(button);
		mSelector.remove(button);
		return super.removeChild(button);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SnapshotArrayList<Actor<?>> getChildList() {
		return (SnapshotArrayList<Actor<?>>) mChildList.clone();
	}
	
	@Override
	public boolean removeChild(Actor<?> child) {
		if(!(child instanceof Button)) return false;
		return removeButton((Button<?>) child);
	}
	
	@Override
	public List<TableCell> getCellList() {
		return Collections.unmodifiableList(super.getCellList());
	}
	
	public ButtonGroup check(int index) {
		return check(mButtonList.get(index));
	}
	
	public ButtonGroup check(Button<?> button) {
		if(!mButtonList.contains(button))
			throw new IllegalArgumentException("button doesn't belong to this ButtonGroup.");
		
		if(button.isChecked()) return this;
		button.setChecked(true);
		return this;
	}
	
	public ButtonGroup checkAll() {
		mSelector.setAll(mButtonList);
		return this;
	}
	
	public ButtonGroup uncheck(int index) {
		return uncheck(mButtonList.get(index));
	}
	
	public ButtonGroup uncheck(Button<?> button) {
		if(!mButtonList.contains(button))
			throw new IllegalArgumentException("button doesn't belong to this ButtonGroup.");
		
		if(!button.isChecked()) return this;
		button.setChecked(false);
		return this;
	}
	
	public ButtonGroup uncheckAll() {
		mSelector.clear();
		return this;
	}
	
	/*package*/ boolean select(Button<?> button) {
		return mSelector.select(button);
	}
	
	/** 첫번째 checked된 Button의 인덱스를 얻는다. checked된 버튼이 없으면 -1을 리턴한다. */
	public int getCheckedIndex() {
		return mButtonList.indexOf(mSelector.first());
	}

	/** 첫번째 checked된 Button을 얻는다. checked된 버튼이 없으면 null을 리턴한다 */
	public Button<?> getCheckedButton() {
		return mSelector.first();
	}
	
	public List<Button<?>> getCheckedButtonList() {
		return mSelector.getSelectionList();
	}

	public boolean isVertical() {
		return mVertical;
	}

	public int getMinChecks() {
		return mSelector.getMinSelections();
	}

	public int getMaxChecks() {
		return mSelector.getMaxSelections();
	}

	public ButtonGroup setMinChecks(int minChecks) {
		mSelector.setMinSelections(minChecks);
		return this;
	}

	/** 최대 체크 가능한 개수를 정한다. 음수를 입력하면 최대 개수의 제한이 없다. */
	public ButtonGroup setMaxChecks(int maxChecks) {
		mSelector.setMaxSelections(maxChecks);
		return this;
	}

	public boolean isLastButtonUnchecked() {
		return mSelector.willRemoveLastSelection();
	}

	public void setLastButtonUnchecked(boolean lastButtonUnchecked) {
		mSelector.setRemoveLastSelection(lastButtonUnchecked);
	}
	
	public Selector<Button<?>> getSelector() {
		return mSelector;
	}

	private static class ButtonSelector extends Selector<Button<?>> {
		
		public ButtonSelector(Actor<?> actor) {
			super(actor);
		}
		
		@Override
		protected void onChangeEventCanceled() {
			super.onChangeEventCanceled();
			ButtonGroup group = (ButtonGroup) getActor();
			// 취소된 Selector의 상태에 따라 각 Button의 checked 상태를 수정한다.
			group.applySelector();
		}
	}

	/** @deprecated addButton(Button<?>)을 이용할 것 */
	@Deprecated
	@Override
	public ButtonGroup addChild(Actor<?> child) {
		throw new UnsupportedOperationException("Use addButton(Button<?>).");
	}
	
	/** @deprecated addButton(Button<?>)을 이용할 것 */
	@Deprecated
	@Override
	public ButtonGroup addChild(int index, Actor<?> child) {
		throw new UnsupportedOperationException("Use addButton(Button<?>).");
	}
	
	/** @deprecated addButton(Button<?>)을 이용할 것 */
	@Deprecated
	@Override
	public ButtonGroup addChildBefore(Actor<?> childBefore, Actor<?> newChild) {
		throw new UnsupportedOperationException("Use addButton(Button<?>).");
	}
	
	/** @deprecated addButton(Button<?>)을 이용할 것 */
	@Deprecated
	@Override
	public ButtonGroup addChildAfter(Actor<?> childAfter, Actor<?> newChild) {
		throw new UnsupportedOperationException("Use addButton(Button<?>).");
	}
	
	/** @deprecated addButton(Button<?>)을 이용할 것 */
	@Deprecated
	@Override
	public TableCell addCell() {
		throw new UnsupportedOperationException("use addButton(Button<?>)");
	}
	
	/** @deprecated addButton(Button<?>)을 이용할 것 */
	@Deprecated
	@Override
	public TableCell addCell(Actor<?> actor) {
		throw new UnsupportedOperationException("use addButton(Button<?>)");
	}
	
	/** @deprecated addButton(Button<?>)을 이용할 것 */
	@Deprecated
	@Override
	public TableCell addCell(TableCell cell) {
		throw new UnsupportedOperationException("use addButton(Button<?>)");
	}

}
