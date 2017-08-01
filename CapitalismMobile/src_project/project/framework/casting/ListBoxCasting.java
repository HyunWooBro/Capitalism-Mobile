package project.framework.casting;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.game.cell.CellManager;
import project.game.cell.CellSelector;
import project.game.ui.BuildingBar;
import project.game.ui.BuildingBar.DetailBuildingTable;
import project.game.ui.UIManager;

import core.framework.Core;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.ScrollPane.ScrollPaneCostume;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.box.ListBox.ListBoxCostume;
import core.scene.stage.actor.widget.utils.Align.HAlign;

public class ListBoxCasting extends BaseCasting<ListBox> {

	@Override
	public ListBox cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}
		
		if(style.equals("updatable")) {
			return style_updatable(args);
		}
		
		if(style.equals("detail_building")) {
			return style_detail_building(args);
		}

		throw new IllegalArgumentException("No such style found.");
	}

	private ListBox style_default(Object[] args) {
		ensureArgs(args.length, 3);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		TextureRegion departmentContentBarRegion = imageTexture
				.getTextureRegion("department_content_bar");

		Texture texture = tm.getTexture(R.drawable.temp);

		TextureRegion hScrollKnobRegion = texture.getTextureRegion("scrollknob_horizontal");
		NinePatch hScrolKnobPatch = new NinePatch(hScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion vScrollKnobRegion = texture.getTextureRegion("scrollknob_vertical");
		NinePatch vScrolKnobPatch = new NinePatch(vScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion backgroundRegion = imageTexture
				.getTextureRegion("department_commodity_empty");

		ScrollPaneCostume scrollPaneCostume = new ScrollPaneCostume();

		NinePatch backgroundPatch = new NinePatch(backgroundRegion, 1, 1, 1, 1);
		scrollPaneCostume.background = Drawable.newDrawable(backgroundPatch);

		scrollPaneCostume.hScrollKnob = Drawable.newDrawable(hScrolKnobPatch);
		scrollPaneCostume.hScrollKnob.setAlpha(0.8f);
		scrollPaneCostume.vScrollKnob = Drawable.newDrawable(vScrolKnobPatch);
		scrollPaneCostume.vScrollKnob.setAlpha(0.8f);

		ListBoxCostume boxCostume = new ListBoxCostume();
		boxCostume.divider = Drawable.newDrawable(departmentContentBarRegion);
		boxCostume.selector = Drawable.newDrawable(departmentContentBarRegion);
		boxCostume.scroll = scrollPaneCostume;

		@SuppressWarnings("unchecked")
		ListBox list = new ListBox(boxCostume).setDividerHeight(2f).setItemFillX((Float) args[2])
				.setItemHAlign((HAlign) args[1]).set((List<Actor<?>>) args[0]);

		// ScrollPane의 컨테츠인 Table을 다음과 같이 현재 사이즈를 선호 사이즈로 사용
		// 이것 왜 필요?
		// list.getTable().validate();
		// list.getTable().setCurrSizeAsPrefSize(true);

		return list;
	}
	
	private ListBox style_updatable(final Object[] args) {
		ensureArgs(args.length, 3);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		TextureRegion departmentContentBarRegion = imageTexture
				.getTextureRegion("department_content_bar");

		Texture texture = tm.getTexture(R.drawable.temp);

		TextureRegion hScrollKnobRegion = texture.getTextureRegion("scrollknob_horizontal");
		NinePatch hScrolKnobPatch = new NinePatch(hScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion vScrollKnobRegion = texture.getTextureRegion("scrollknob_vertical");
		NinePatch vScrolKnobPatch = new NinePatch(vScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion backgroundRegion = imageTexture
				.getTextureRegion("department_commodity_empty");

		ScrollPaneCostume scrollPaneCostume = new ScrollPaneCostume();

		NinePatch backgroundPatch = new NinePatch(backgroundRegion, 1, 1, 1, 1);
		scrollPaneCostume.background = Drawable.newDrawable(backgroundPatch);

		scrollPaneCostume.hScrollKnob = Drawable.newDrawable(hScrolKnobPatch);
		scrollPaneCostume.hScrollKnob.setAlpha(0.8f);
		scrollPaneCostume.vScrollKnob = Drawable.newDrawable(vScrolKnobPatch);
		scrollPaneCostume.vScrollKnob.setAlpha(0.8f);

		ListBoxCostume boxCostume = new ListBoxCostume();
		boxCostume.divider = Drawable.newDrawable(departmentContentBarRegion);
		boxCostume.selector = Drawable.newDrawable(departmentContentBarRegion);
		boxCostume.scroll = scrollPaneCostume;
		
		final ListBoxUpdater<?> updater = (ListBoxUpdater<?>) args[0];
		updater.createPreList();

		ListBox list = new ListBox(boxCostume) {
			
			@Override
			public void update(long time) {
				super.update(time);
				updater.update();
			};
		}.setDividerHeight(2f).setItemFillX((Float) args[2])
				.setItemHAlign((HAlign) args[1]).set(updater.createItemList());

		// ScrollPane의 컨테츠인 Table을 다음과 같이 현재 사이즈를 선호 사이즈로 사용
		// 이것 왜 필요?
		// list.getTable().validate();
		// list.getTable().setCurrSizeAsPrefSize(true);

		return list;
	}

	private ListBox style_detail_building(Object[] args) {
		ensureArgs(args.length, 2);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		TextureRegion departmentContentBarRegion = imageTexture
				.getTextureRegion("department_content_bar");

		Texture texture = tm.getTexture(R.drawable.temp);

		TextureRegion hScrollKnobRegion = texture.getTextureRegion("scrollknob_horizontal");
		NinePatch hScrolKnobPatch = new NinePatch(hScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion vScrollKnobRegion = texture.getTextureRegion("scrollknob_vertical");
		NinePatch vScrolKnobPatch = new NinePatch(vScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion backgroundRegion = imageTexture
				.getTextureRegion("department_commodity_empty");

		ScrollPaneCostume scrollPaneCostume = new ScrollPaneCostume();

		NinePatch backgroundPatch = new NinePatch(backgroundRegion, 1, 1, 1, 1);
		scrollPaneCostume.background = Drawable.newDrawable(backgroundPatch);

		scrollPaneCostume.hScrollKnob = Drawable.newDrawable(hScrolKnobPatch);
		scrollPaneCostume.hScrollKnob.setAlpha(0.8f);
		scrollPaneCostume.vScrollKnob = Drawable.newDrawable(vScrolKnobPatch);
		scrollPaneCostume.vScrollKnob.setAlpha(0.8f);

		ListBoxCostume boxCostume = new ListBoxCostume();
		boxCostume.divider = Drawable.newDrawable(departmentContentBarRegion);
		boxCostume.selector = Drawable.newDrawable(departmentContentBarRegion);
		boxCostume.scroll = scrollPaneCostume;

		@SuppressWarnings("unchecked")
		ListBox list = new ListBox(boxCostume) {
			@Override
			protected void onItemSelected() {
				DetailBuildingTable table = (DetailBuildingTable) getSelector().first();
				BuildingBar.sSelectedBuildingDescription = table.buildingDescription;
				CellSelector selector = CellManager.getInstance().getCellSelector();
				selector.setSelectorSize((int) table.buildingDescription.size);
				selector.reset();
				UIManager.getInstance().getBuildingBar().bringUpBuildDialog();
			}
		}.setDividerHeight(2f).setItemHAlign((HAlign) args[1]).set((List<Actor<?>>) args[0]).setItemFillX(1f);

		// ScrollPane의 컨테츠인 Table을 다음과 같이 현재 사이즈를 선호 사이즈로 사용
		// list.getTable().validate();
		// list.getTable().setCurrSizeAsPrefSize(true);

		return list;
	}
	
	public static abstract class ListBoxUpdater<T> {
		
		protected List<T> currPreList;
		protected List<T> tempPreList;
		
		public ListBoxUpdater() {
			init();
		}
		
		public void update() {
			createPreList();
			if(!compare()) {
				updateListBox(createItemList());
			}
		}
		
		public void init() {
			this.currPreList = new ArrayList<T>();
			this.tempPreList = new ArrayList<T>();
		}
		
		/** Actor 리스트를 만들기 전, 후보가 될 임시리스트를 생선한다. */
		public abstract void createPreList();
		
		/** 현재 ListBox의 임시리스트와 후보 임시리스트를 비교한다. */
		public abstract boolean compare();
		
		/** 실제 ListBox에 삽입될 Actor 리스트를 생성한다. */
		public abstract List<Actor<?>> createItemList();
		
		/** 매개변수로 넘어온 Actor 리스트로 ListBox를 업데이트한다. */
		public abstract void updateListBox(List<Actor<?>> itemList);
	}

}
