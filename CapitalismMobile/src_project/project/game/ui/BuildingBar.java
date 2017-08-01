package project.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.framework.R;

import project.framework.Utils;
import project.game.building.Building;
import project.game.building.BuildingManager;
import project.game.building.BuildingManager.BuildingDescription;
import project.game.cell.CellManager;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.Vector2;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.Label;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.utils.pool.Pools;

public class BuildingBar extends Table<BuildingBar> {

	public static BuildingDescription sSelectedBuildingDescription = null;

	private Map<String, ListBox> mTypeToDetailBuildingBarMap = new HashMap<String, ListBox>();

	private Map<String, Label<?>> mTypeToLabelMap = new HashMap<String, Label<?>>();

	private ListBox mCurrentDetail;

	private PushButton mCurrentButton;

	private DialogWindow mDialog;
	private Vector2 mDialogCenterPos = new Vector2(540f, 260f);

	public BuildingBar() {

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		TextureRegion backgroundRegion = imageTexture.getTextureRegion("ui_toolbar_building1");
		NinePatch backgroundPatch = new NinePatch(backgroundRegion, 12, 33, 25, 132);
		setDrawable(Drawable.newDrawable(backgroundPatch));

		createBuildingBar();

		createDetailBuildingBars();
	}

	private void createBuildingBar() {

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		addCell(new SLabel(R.string.label_building_types, fontTexture)).padTop(-20f);
		row();

		int n;

		List<String> typeList = BuildingManager.getInstance().getBuildingTypeList();

		// String[] types = (String[]) clazz;

		// BuildingType[] types = Building.BuildingType.values();

		ChangeListener listener = new ChangeListener() {

			@Override
			public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
				PushButton button = (PushButton) listener;

				if(mCurrentButton == button) {
					mCurrentButton = null;
					button.getChildList().get(0).setColor(Color4.WHITE4);
					removeChild(mCurrentDetail);
					onTypeSelectionChanged();
					return;
				}

				if(mCurrentButton != null)
					mCurrentButton.getChildList().get(0).setColor(Color4.WHITE4);
				button.getChildList().get(0).setColor(Color4.LTRED4);
				mCurrentButton = button;

				removeChild(mCurrentDetail);
				mCurrentDetail = mTypeToDetailBuildingBarMap.get(button.getTag());
				mCurrentDetail.setX(getX() + getWidth() - 25f);

				float bottomUIHeight = UIManager.BOTTOM_UI_HEIGHT;
				float virtualHeight = Core.APP.getAppConfig().virtualHeight;
				float space = virtualHeight - bottomUIHeight;

				float buttonY = button.getY();
				float height = mCurrentDetail.getHeight();

				// 리스트박스가 하단 UI를 침범하면 위치를 적절히 조정한다.
				if(buttonY + height > space) {
					mCurrentDetail.setY(space - height);
				} else {
					mCurrentDetail.setY(buttonY);
				}

				mCurrentDetail.getSelector().clear();
				addChild(mCurrentDetail);
				// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
				mCurrentDetail.getScroll().startScrollFade();

				onTypeSelectionChanged();
			}
		};

		n = typeList.size();
		for(int i = 0; i < n; i++) {
			String type = typeList.get(i);
			List<BuildingDescription> descriptionList = BuildingManager.getInstance()
					.getBDescListByType(type);
			if(descriptionList.isEmpty()) continue;

			LayoutTable table = new LayoutTable();

			boolean buildable = false;
			int m = descriptionList.size();
			for(int j = 0; j < m; j++) {
				BuildingDescription description = descriptionList.get(j);
				if(description.buildable) {
					buildable = true;
					break;
				}
			}
			if(!buildable) continue;

			String name = BuildingManager.getInstance().getNameByType(type);

			PushButton button = cd.cast(PushButton.class, "dynamic_text", name).setTag(type)
					.addEventListener(listener);

			// DLabel label = new DLabel(name, fontTexture);
			// DLabel label = (DLabel) button.getChildList().get(0);
			// mTypeToLabelMap.put(type, label);
			// button.addCell(label);

			addCell(button).width(80f).padBottom(-4f);
			row();
		}

		PushButton returnButton = cd.cast(PushButton.class, "static_text", R.string.label_return);
		// SLabel returnLabel = new SLabel(R.string.label_return, fontTexture);
		// TouchActionPushButton returnButton = new TouchActionPushButton();
		returnButton.addEventListener(new GestureTouchListener() {
			@Override
			public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {
				cancel();
			}
		});
		returnButton.getChildList().get(0).setColor(Color4.GREEN4);
		// returnButton.addCell(returnLabel);

		addCell(returnButton).width(80f).padTop(5f);

		pack();

		// debugAll();

		// validate();
	}

	private void createDetailBuildingBars() {

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture texture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		Texture tempTexture = tm.getTexture(R.drawable.temp);

		// BuildingType[] types = Building.BuildingType.values();

		List<String> typeList = BuildingManager.getInstance().getBuildingTypeList();

		int n = typeList.size();
		for(int i = 0; i < n; i++) {
			String type = typeList.get(i);
			List<BuildingDescription> descriptionList = BuildingManager.getInstance()
					.getBDescListByType(type);
			if(descriptionList.isEmpty()) continue;

			LayoutTable table = new LayoutTable();

			boolean buildable = false;
			int m;
			m = descriptionList.size();
			for(int j = 0; j < m; j++) {
				BuildingDescription description = descriptionList.get(j);
				if(description.buildable) {
					buildable = true;
					break;
				}
			}
			if(!buildable) continue;

			List<Actor<?>> itemList = new ArrayList<Actor<?>>();

			m = descriptionList.size();
			for(int j = 0; j < m; j++) {

				BuildingDescription description = descriptionList.get(j);

				if(description.image.isEmpty()) continue;

				TextureRegion buildingRegion = null;

				String regionName = description.image;
				if(description.frame > 0) {
					regionName += "_01";
				}
				if(description.frame == 0) {
					buildingRegion = texture.getTextureRegion(regionName);
				}
				if(description.frame > 0) {
					buildingRegion = tempTexture.getTextureRegion(regionName);
				}

				final DetailBuildingTable detailBuildingTable = new DetailBuildingTable(description)
						.pad(2f);

				detailBuildingTable.col(1).expandX().right();

				detailBuildingTable.addCell(new Image(buildingRegion)).size(45f, 45f).rowSpan(3);
				detailBuildingTable.addCell(new DLabel(description.name, fontTexture)
						.setColor(Color4.GREEN4));
				detailBuildingTable.row();
				detailBuildingTable.addCell(new CLabel(Utils.toCash(description.setupCost),
						R.array.label_array_outline_white_15, fontTexture));
				detailBuildingTable.row();
				detailBuildingTable.addCell(new CLabel(Utils.toCash(description.maintenance),
						R.array.label_array_outline_white_15, fontTexture)
						.setColor(Color4.DKYELLOW4));

				itemList.add(detailBuildingTable);
			}

			final ListBox list = cd.cast(ListBox.class, "detail_building", itemList, HAlign.CENTER)
					.pack().setWidth(135f);

			if(list.getHeight() > 220f) list.setHeight(220f);

			// list.debugAll();

			mTypeToDetailBuildingBarMap.put(type, list);

		}

	}

	/*package*/ void onShow() {

	}

	/*package*/ void onHide() {
		if(mCurrentButton != null) {
			mCurrentButton.getChildList().get(0).setColor(Color4.WHITE4);
			mCurrentButton = null;
		}
	}

	private void onTypeSelectionChanged() {
		sSelectedBuildingDescription = null;
		CellManager.getInstance().getCellSelector().reset();
		closeDialog();
	}

	public void bringUpBuildDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible()) {
				// closeDialog();
				TextureManager tm = Core.GRAPHICS.getTextureManager();
				Texture fontTexture = tm.getTexture(R.drawable.font);
				DLabel title = (DLabel) mDialog.getTitle();
				title.dispose();
				mDialog.setTitle(new DLabel(changeDialogTitle(), fontTexture));
				mDialog.pack();
				return;
			}
		}

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		// String[] departments =
		// Core.APP.getResources().getStringArray(construction_type);

		// List<DepartmentType> typeList =
		// mBuildingDelegate.getAvailableDepartmentList();

		// List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		/*
		 * for(int j=0; j<departments.length; j++) { LayoutTable table =
		 * cd.cast(LayoutTable.class, "department_list_item", departments[j]);
		 * itemList.add(table); }
		 */

		/*
		 * int n = typeList.size(); for(int i=0; i<n; i++) { DepartmentType
		 * clazz = typeList.get(i); LayoutTable table =
		 * cd.cast(LayoutTable.class, "department_list_item", clazz);
		 * itemList.add(table); }
		 */

		/*
		 * final ListBox list = cd.cast(ListBox.class, "default", itemList,
		 * HAlign.LEFT, 0f) .setCurrSizeAsPrefSize(true) .sizeTo(140f, 120f);
		 */

		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "설치").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {

						/*
						 * m_select = list.getSelectedIndex(); DepartmentType
						 * departmentType =
						 * mBuildingDelegate.getAvailableDepartmentList
						 * ().get(m_select); m_previous_department =
						 * mDepartments[sDepartmentSelectedIndex];
						 * insertDepartment(departmentType);
						 */

						// GameScene scene = (GameScene)
						// Director.getInstance().getCurrentScene();
						Building building = CellManager.getInstance().build();
						// CellManager.getInstance().setSelectedBuilding(null);
						// CellManager.getInstance().selectCell(CellManager.getInstance().getCellSelector().getSelectedCell());
						// CellManager.getInstance().updateSelectorState();
						CellManager.getInstance().getCellSelector().reset();
						removeChild(mCurrentDetail);

						closeDialog();
					}
				});

		PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "취소").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						cancel();
					}
				});

		CellManager manager = CellManager.getInstance();

		mDialog = cd.cast(DialogWindow.class, "build", manager)
				.setTitle(new DLabel(changeDialogTitle(), fontTexture))
				// .addContent(mBuildCostLabel)
				// .addContent(mLandValueLabel)
				.addButton(button1).addButton(button2).pack()
				.moveCenterTo(mDialogCenterPos.x, mDialogCenterPos.y);

		// float pureWidth = mDialog.getWidth() - mDialog.getPadLeft() -
		// mDialog.getPadRight();
		// mDialog.getContentCell().width(pureWidth);

		// mDialog.debugAll();

		// Core.APP.debug(mDialog.toString());

		UIManager.getInstance().addChild(mDialog);
		mDialog.open();
	}

	private void cancel() {
		sSelectedBuildingDescription = null;
		removeChild(mCurrentDetail);
		UIManager.getInstance().switchBarType();
		CellManager.getInstance().getCellSelector().reset();
		closeDialog();
	}

	private String changeDialogTitle() {
		String typeName = BuildingManager.getInstance().getNameByType(
				sSelectedBuildingDescription.type);
		String codeName = sSelectedBuildingDescription.name;
		return typeName + "(" + codeName + ")";
	}

	private void closeDialog() {
		if(mDialog == null) return;
		mDialog.close();
		mDialog.addAction(new Run(new Runnable() {

			@Override
			public void run() {
				UIManager.getInstance().removeChild(mDialog);
				mDialog.disposeAll();
				mDialog = null;
			}
		}).setStartOffset(mDialog.getAnimationDuration()));
	}

	@Override
	public void update(long time) {
		super.update(time);

		if(mDialog != null) {
			float cX = mDialog.getX() + mDialog.getWidth() / 2;
			float cY = mDialog.getY() + mDialog.getHeight() / 2;
			mDialogCenterPos.set(cX, cY);
		}
	}

	private void drawName(Batch batch, float parentAlpha) {
		Label<?> label = mTypeToLabelMap.get(sSelectedBuildingDescription.type);
		float x = label.getX();
		float y = label.getY();
		Color4 color = Pools.obtain(Color4.class);
		color.set(label.getColor());
		label.setColor(Color4.WHITE4);
		label.moveTo(15, 230);
		label.bottom();
		label.draw(batch, parentAlpha);
		label.moveTo(x, y);
		label.top();
		label.setColor(color);
		Pools.recycle(color);
	}

	@Override
	public Actor<?> contactSelf(float x, float y) {
		return null;
	}

	@Override
	public void disposeAll() {
		Map<String, ListBox> typeToDetailBuildingBarMap = mTypeToDetailBuildingBarMap;
		Object[] keys = typeToDetailBuildingBarMap.keySet().toArray();
		int n = keys.length;
		for(int i = 0; i < n; i++) {
			String type = (String) keys[i];
			ListBox listBox = typeToDetailBuildingBarMap.get(type);
			listBox.disposeAll();
		}

		super.disposeAll();
	}

	public static class DetailBuildingTable extends Table<DetailBuildingTable> {

		public BuildingDescription buildingDescription;

		public DetailBuildingTable(BuildingDescription description) {
			super();
			buildingDescription = description;
		}

	}

}
