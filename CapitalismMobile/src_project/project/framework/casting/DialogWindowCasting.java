package project.framework.casting;

import org.framework.R;

import project.framework.Utils;
import project.game.cell.CellManager;
import project.game.ui.BuildingBar;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.stage.actor.widget.table.window.Window.WindowCostume;

public class DialogWindowCasting extends BaseCasting<DialogWindow> {

	@Override
	public DialogWindow cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}
		
		if(style.equals("build")) {
			return style_build(args);
		}

		throw new IllegalArgumentException("No such style found.");
	}

	private DialogWindow style_default(Object[] args) {
		ensureArgs(args.length, 0);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture windowTexture = tm.getTexture(R.drawable.atlas_window);

		TextureRegion black = imageTexture.getTextureRegion("department_commodity_empty");

		final TextureRegion windowRegion = windowTexture.getTextureRegion("window_basic");
		NinePatch windowPatch = new NinePatch(windowRegion, 5, 31, 5, 5);

		WindowCostume costume = new WindowCostume();
		costume.background = Drawable.newDrawable(windowPatch);
		costume.floorBackground = Drawable.newDrawable(black);

		DialogWindow dialog = new DialogWindow(costume).setMovable(true).setTitleHeight(26f);

		return dialog;
	}

	private DialogWindow style_build(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture windowTexture = tm.getTexture(R.drawable.atlas_window);

		final TextureRegion windowRegion = windowTexture.getTextureRegion("window_basic");
		NinePatch windowPatch = new NinePatch(windowRegion, 5, 31, 5, 5);

		WindowCostume costume = new WindowCostume();
		costume.background = Drawable.newDrawable(windowPatch);

		DialogWindow dialog = new BuildDialogWindow(costume, (CellManager) args[0])
				.setMovable(true).setTitleHeight(26f);

		return dialog;
	}

	private static class BuildDialogWindow extends DialogWindow {

		private CellManager mCellManager;

		private DLabel mDisabledLabel;

		private DLabel mPositionLabel;

		private SLabel mBuildCostSLabel;
		private SLabel mLandValueSLabel;
		private SLabel mHousePurchaseSLabel;
		private SLabel mTotalBuildCostSLabel;
		private SLabel mBuildingMaintenanceSLabel;

		private CLabel mBuildCostCLabel;
		private CLabel mLandValueCLabel;
		private CLabel mHousePurchaseCLabel;
		private CLabel mTotalBuildCostCLabel;
		private CLabel mBuildingMaintenanceCLabel;

		private Image mDividerImage;

		public BuildDialogWindow(WindowCostume costume, CellManager manager) {
			super(costume);
			mCellManager = manager;
			init();
		}

		private void init() {
			TextureManager tm = Core.GRAPHICS.getTextureManager();

			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mDisabledLabel = new DLabel("그곳에 건설할 수 없습니다.", fontTexture, Utils.sOutlineWhite15);

			mPositionLabel = new DLabel("건설할 위치를 지정하십시오.", fontTexture, Utils.sOutlineWhite15);

			mBuildCostSLabel = new SLabel(R.string.label_build_cost, fontTexture);
			mLandValueSLabel = new SLabel(R.string.label_landvalue, fontTexture);
			mHousePurchaseSLabel = new SLabel(R.string.label_house_purchase, fontTexture);
			mTotalBuildCostSLabel = new SLabel(R.string.label_total_build_cost, fontTexture)
					.setColor(Color4.LTRED4);
			mBuildingMaintenanceSLabel = new SLabel(R.string.label_building_maintenance, fontTexture)
					.setColor(Color4.DKYELLOW4);

			mBuildCostCLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mLandValueCLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mHousePurchaseCLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mTotalBuildCostCLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mBuildingMaintenanceCLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			mDividerImage = new Image(imageTexture.getTextureRegion("department_content_bar"));

			getContentCell().padLeft(5f).padRight(5f).padTop(5f).fillX();

			LayoutTable table = getContentTable();
			table.col(0).left();
			table.col(1).cellWidth(100f).right();
			table.addCell(mBuildCostSLabel);
			table.addCell(mBuildCostCLabel);
			table.row();
			table.addCell(mLandValueSLabel);
			table.addCell(mLandValueCLabel);
			table.row();
			table.addCell(mHousePurchaseSLabel);
			table.addCell(mHousePurchaseCLabel);
			table.row();
			table.addCell(mDividerImage).colSpan(2).prefSize(10f, 2f).padTop(5f).padBottom(5f)
					.fillX();
			table.row();
			table.addCell(mTotalBuildCostSLabel);
			table.addCell(mTotalBuildCostCLabel);
			table.row().padTop(5f);
			table.addCell(mBuildingMaintenanceSLabel);
			table.addCell(mBuildingMaintenanceCLabel);

			// table.debugAll();
		}

		@Override
		public void update(long time) {
			super.update(time);

			if(CellManager.getInstance().hasSelectedCell()
					&& mCellManager.getCellSelector().isVisible()) {
				if(mCellManager.getCellSelector().canBuild()) {

					if(BuildingBar.sSelectedBuildingDescription == null) return;

					getContentTable().setVisible(true);

					PushButton button = (PushButton) getButtonTable().getCellList().get(0)
							.getActor();
					button.setDisabled(false);
					button.getChildList().get(0).setColor(Color4.WHITE4);
					button.setColor(Color4.WHITE4);

					double buildCost = BuildingBar.sSelectedBuildingDescription.setupCost;
					mBuildCostCLabel.setText(Utils.toCash(buildCost));
					
					mCellManager.calculateTotalConstructionPrice();
					double landValue = mCellManager.getTotalLandValue();
					mLandValueCLabel.setText(Utils.toCash(landValue));

					double housePurchase = mCellManager.getTotalPurchase();
					mHousePurchaseCLabel.setText(Utils.toCash(housePurchase));

					mTotalBuildCostCLabel.setText(Utils.toCash(buildCost + landValue + housePurchase));

					mBuildingMaintenanceCLabel.setText(Utils
							.toCash(BuildingBar.sSelectedBuildingDescription.maintenance));

				} else {
					getContentTable().setVisible(false);

					PushButton button = (PushButton) getButtonTable().getCellList().get(0)
							.getActor();
					button.setDisabled(true);
					button.getChildList().get(0).setColor(Color4.GRAY4);
					button.setColor(Color4.GRAY4);
				}
			} else {
				getContentTable().setVisible(false);

				PushButton button = (PushButton) getButtonTable().getCellList().get(0).getActor();
				button.setDisabled(true);
				button.getChildList().get(0).setColor(Color4.GRAY4);
				button.setColor(Color4.GRAY4);
			}
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			super.drawChildren(batch, parentAlpha);

			if(CellManager.getInstance().hasSelectedCell()
					&& mCellManager.getCellSelector().isVisible()) {
				if(!mCellManager.getCellSelector().canBuild()) {

					float width = getWidth();
					float pureWidth = width - getPadLeft() - getPadRight();

					if(mDisabledLabel.getWidth() > pureWidth)
						mDisabledLabel.setWidth(pureWidth);
					mDisabledLabel.moveTo(width / 2 - mDisabledLabel.getWidth() / 2, 60f);
					mDisabledLabel.draw(batch, parentAlpha * getAlpha());
				}
			} else {

				float width = getWidth();
				float pureWidth = width - getPadLeft() - getPadRight();

				if(mPositionLabel.getWidth() > pureWidth)
					mPositionLabel.setWidth(pureWidth);
				mPositionLabel.moveTo(width / 2 - mPositionLabel.getWidth() / 2, 60f);
				mPositionLabel.draw(batch, parentAlpha * getAlpha());
			}
		}

		@Override
		public void disposeAll() {
			super.disposeAll();
			mDisabledLabel.dispose();
			mPositionLabel.dispose();
		}
	}

}
