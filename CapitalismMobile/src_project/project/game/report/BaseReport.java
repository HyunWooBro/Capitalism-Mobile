package project.game.report;

import java.util.List;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.box.DropDownBox;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.utils.Disposable;

public abstract class BaseReport extends Table<BaseReport> implements Report {

	private TableCell mPureContentCell;

	private float mPadContent = 10f;

	private float mDropDownWidth = 150f;
	private float mDropDownPadBottom = 10f;

	private List<Actor<?>> mItemList;

	public BaseReport() {
	}

	/**
	 * DropDownBox에 삽입될 아이템 리스를 정의한다. 각 아이템는 setUserObject를 통해 Report를 구현한
	 * Actor를 전달해야 한다.
	 */
	protected void init(List<Actor<?>> itemList) {
		mItemList = itemList;

		CastingDirector cd = CastingDirector.getInstance();

		final DropDownBox dropDownBox = cd.cast(DropDownBox.class, "default").setDividerHeight(2f)
				.set(itemList).select(0).setItemHAlign(HAlign.LEFT).setItemPadding(2f, 5f, 0f, 1f)
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						if(event.isTargetActor()) {
							((Report) mPureContentCell.getActor()).onHide();

							DropDownBox box = (DropDownBox) listener;
							mPureContentCell.setActor((Actor<?>) box.getSelectedItem()
									.getUserObject());

							((Report) mPureContentCell.getActor()).onShow();
						}
					}
				});

		pad(mPadContent);
		north();

		addCell(dropDownBox).width(mDropDownWidth).padBottom(mDropDownPadBottom);
		row();
		mPureContentCell = addCell((Actor<?>) itemList.get(0).getUserObject());
	}

	@Override
	public void onShow() {
		((Report) mPureContentCell.getActor()).onShow();
	}

	@Override
	public void onHide() {
		((Report) mPureContentCell.getActor()).onHide();
	}

	public float getPadContent() {
		return mPadContent;
	}

	public float getDropDownWidth() {
		return mDropDownWidth;
	}

	public float getDropDownPadBottom() {
		return mDropDownPadBottom;
	}

	public List<Actor<?>> getItemList() {
		return mItemList;
	}

	@Override
	public void disposeAll() {
		List<Actor<?>> itemList = mItemList;
		int n = itemList.size();
		for(int i = 0; i < n; i++) {
			// DropDownBox의 Label을 정리한다.
			Actor<?> item = itemList.get(i);
			// Group인 경우에는 자식까지 정리한다.
			if(item instanceof Group) {
				((Group<?>) item).disposeAll();
				// Group이 아니며 Disposable을 구현한다면 child를 정리한다.
			} else if(item instanceof Disposable)
				((Disposable) item).dispose();

			// Label의 userObject로 속해 있는 실제 content도 정리한다.
			Actor<?> content = (Actor<?>) item.getUserObject();
			// Group인 경우에는 자식까지 정리한다.
			if(content instanceof Group) {
				((Group<?>) content).disposeAll();
				// Group이 아니며 Disposable을 구현한다면 child를 정리한다.
			} else if(content instanceof Disposable)
				((Disposable) content).dispose();
		}

		super.disposeAll();
	}
}