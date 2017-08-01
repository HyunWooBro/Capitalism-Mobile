package core.scene.stage.actor.widget.box;

import java.util.List;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.WidgetGroup;
import core.scene.stage.actor.widget.utils.Selector;

/**
 * Box는 삽입된 다수의 항목(아이템)을 출력한다. 각 항목은 기본적인 Actor에서부터 
 * 복잡한 구조를 가진 Table까지 가능하다.</p>
 * 
 * Box는 최소 하나 이상의 항목을 선택할 수 있는 기능을 제공한다.</p>
 * 
 * @author 김현우
 */
public abstract class Box<T extends Box<T>> extends WidgetGroup<T> {
	
	public Box(Costume costume) {
		super(costume);
	}

	/** 기존 아이템 대신 아이템 배열을 전체 아이템으로 교체한다. */
	public abstract T set(Actor<?>[] items);
	
	/** 기존 아이템 대신 아이템 리스트를 전체 아이템으로 교체한다. */
	public abstract T set(List<Actor<?>> itemList);
	
	/** 지정한 인덱스의 아이템을 얻는다. */
	public abstract Actor<?> getItem(int index);
	
	/** 모든 아이템의 리스트를 얻는다. */
	public abstract List<Actor<?>> getItemList();
	
	/** 아이템을 뒤에 추가한다. */
	public abstract T addItem(Actor<?> item);
	
	/** 아이템을 지정한 인덱스에 추가한다. */
	public abstract T addItem(int index, Actor<?> item);
	
	/** 아이템 리스트를 뒤에 추가한다. */
	public abstract T addAll(List<Actor<?>> itemList);
	
	/** 아이템 리스트를 지정한 인덱스에 추가한다. */
	public abstract T addAll(int index, List<Actor<?>> itemList);
	
	/** 지정한 인덱스의 아이템을 제거한다. */
	public abstract T removeItem(int index);
	
	/** 지정한 아이템을 제거한다. */
	public abstract T removeItem(Actor<?> item);
	
	/** 지정한 인덱스 사이의 모든 아이템을 제거한다. */
	public abstract T removeAll(int startIndex, int endIndex);
	
	/** 아이템 리스트에 속한 모든 아이템을 제거한다. */
	public abstract T removeAll(List<Actor<?>> itemList);
	
	/** 모든 아이템을 제거한다. */
	public abstract T clearItems();
	
	/** 지정한 인덱스의 두 아이템을 교체한다. */
	public abstract T swapItems(int index1, int index2);
	
	/** 지정한 두 아이템을 교체한다. */
	public abstract T swapItems(Actor<?> item1, Actor<?> item2);
	
	/** 선택을 반전한다. */
	public abstract T reverseAll(List<Actor<?>> itemList);
	
	/** 지정한 인덱스의 아이템을 선택한다. */
	public abstract T select(int index);
	
	/** 지정한 아이템을 선택한다. */
	public abstract T select(Actor<?> item);
	
	/** 선택된 아이템의 인덱스를 얻는다. */
	public abstract int getSelectedIndex();
	
	/** 선택된 아이템을 얻는다. */
	public abstract Actor<?> getSelectedItem();
	
	/** {@link Selector}을 얻는다. */
	public abstract Selector<?> getSelector();

	/** @deprecated */
	@Deprecated
	@Override
	public T addChild(Actor<?> child) {
		throw new UnsupportedOperationException("Invoking this method is restricted.");
	}
	
	/** @deprecated */
	@Deprecated
	@Override
	public T addChild(int index, Actor<?> child) {
		throw new UnsupportedOperationException("Invoking this method is restricted.");
	}
	
	/** @deprecated */
	@Deprecated
	@Override
	public T addChildBefore(Actor<?> childBefore, Actor<?> newChild) {
		throw new UnsupportedOperationException("Invoking this method is restricted.");
	}
	
	/** @deprecated */
	@Deprecated
	@Override
	public T addChildAfter(Actor<?> childAfter, Actor<?> newChild) {
		throw new UnsupportedOperationException("Invoking this method is restricted.");
	}

	/** @deprecated */
	@Deprecated
	@Override
	public boolean removeChild(Actor<?> child) {
		throw new UnsupportedOperationException("Invoking this method is restricted.");
	}
	
	/** @deprecated */
	@SuppressWarnings("unchecked")
	@Deprecated
	@Override
	public T clearChildren() {
		return (T) this;
	}
	
}
