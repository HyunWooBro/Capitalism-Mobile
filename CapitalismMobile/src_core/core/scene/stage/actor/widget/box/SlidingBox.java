package core.scene.stage.actor.widget.box;

import java.util.List;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.utils.Selector;

/**
 * 
 * 
 * @author 김현우
 */
public class SlidingBox extends Box<SlidingBox> {
	
	// TODO 구현 필요
	
	private boolean mVertical;

	public SlidingBox(Costume costume) {
		super(costume);
	}

	@Override
	public SlidingBox set(Actor<?>[] items) {
		return null;
	}

	@Override
	public SlidingBox set(List<Actor<?>> itemList) {
		return null;
	}

	@Override
	public Actor<?> getItem(int index) {
		return null;
	}

	@Override
	public List<Actor<?>> getItemList() {
		return null;
	}

	@Override
	public SlidingBox addItem(Actor<?> item) {
		return null;
	}

	@Override
	public SlidingBox addItem(int index, Actor<?> item) {
		return null;
	}

	@Override
	public SlidingBox addAll(List<Actor<?>> itemList) {
		return null;
	}

	@Override
	public SlidingBox addAll(int index, List<Actor<?>> itemList) {
		return null;
	}

	@Override
	public SlidingBox removeItem(int index) {
		return null;
	}

	@Override
	public SlidingBox removeItem(Actor<?> item) {
		return null;
	}

	@Override
	public SlidingBox removeAll(int startIndex, int endIndex) {
		return null;
	}

	@Override
	public SlidingBox removeAll(List<Actor<?>> itemList) {
		return null;
	}

	@Override
	public SlidingBox clearItems() {
		return null;
	}

	@Override
	public SlidingBox swapItems(int index1, int index2) {
		return null;
	}

	@Override
	public SlidingBox swapItems(Actor<?> item1, Actor<?> item2) {
		return null;
	}

	@Override
	public SlidingBox reverseAll(List<Actor<?>> itemList) {
		return null;
	}

	@Override
	public SlidingBox select(int index) {
		return null;
	}

	@Override
	public SlidingBox select(Actor<?> item) {
		return null;
	}

	@Override
	public int getSelectedIndex() {
		return 0;
	}

	@Override
	public Actor<?> getSelectedItem() {
		return null;
	}

	@Override
	public Selector<?> getSelector() {
		return null;
	}

	@Override
	protected float getDefaultPrefWidth() {
		return 0;
	}

	@Override
	protected float getDefaultPrefHeight() {
		return 0;
	}

	@Override
	public void layout() {
	}
	
}
