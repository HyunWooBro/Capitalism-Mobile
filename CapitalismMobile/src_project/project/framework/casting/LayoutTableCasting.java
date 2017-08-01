package project.framework.casting;

import java.util.Comparator;

import org.framework.R;

import project.framework.Utils;
import project.framework.Utils.OverallScoreBar;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.stock.Purchase.ProductProposal;
import project.game.building.department.stock.Sales.ClientCorporation;
import project.game.building.department.stock.StockDepartment;
import project.game.city.CityManager;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductGroup;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ManufacturingDescription;
import project.game.product.ProductManager.ProductData;
import project.game.product.ProductManager.ProductDescription;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.Image.ScaleType;
import core.scene.stage.actor.widget.Widget;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.LayoutTable;

public class LayoutTableCasting extends BaseCasting<LayoutTable> {

	@Override
	public LayoutTable cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}
		
		if(style.equals("department_list_item")) {
			return style_department_list_item(args);
		}
		
		if(style.equals("product_proposal_list_item")) {
			return style_product_proposal_list_item(args);
		}
		
		if(style.equals("client_list_item")) {
			return style_client_list_item(args);
		}
		
		if(style.equals("rnd_list_item")) {
			return style_rnd_list_item(args);
		}
		
		if(style.equals("manufacture_select_list_item")) {
			return style_manufacture_select_list_item(args);
		}

		if(style.equals("manufacture_type_list_item")) {
			return style_manufacture_type_list_item(args);
		}
		
		if(style.equals("manufacture_output_list_item")) {
			return style_manufacture_output_list_item(args);
		}

		throw new IllegalArgumentException("No such style found.");
	}

	private LayoutTable style_default(Object[] args) {
		return null;
	}

	private LayoutTable style_department_list_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		DepartmentType type = (DepartmentType) args[0];

		LayoutTable table = new LayoutTable();
		table.left();

		if(type.getImageRegion() != null) {
			Image image = new Image(type.getImageRegion()).setScaleType(ScaleType.FIT);
			table.addCell(image).actorPrefWidth(80f).prefHeight(70f).padLeft(5f);
		}
		table.addCell(new SLabel((SLabel) type.getLabel())).padLeft(5f);
		table.addCell(
				new CLabel(Utils.toCash(type.getCost()), R.array.label_array_outline_white_12,
						fontTexture)).expandX().right().padRight(20f);

		// table.debugAll();

		return table;
	}

	private LayoutTable style_product_proposal_list_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		ProductProposal proposal = (ProductProposal) args[0];
		final DisplayProduct display = proposal.display;
		String name = display.desc.name;
		
		ProductData data = ProductManager.getInstance().getProductDataByCode(display.desc.code);
		
		double freight = ProductManager.getInstance().calculateFreight(proposal.distance, display.desc);
		
		final DLabel priceDLabel = new DLabel("가격 : ", fontTexture);
		final CLabel priceCLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture)
				.setConcatLabel(priceDLabel);
		
		final DLabel qualityDLabel = new DLabel("품질 : ", fontTexture);
		final CLabel qualityCLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture)
				.setConcatLabel(qualityDLabel);
		
		final DLabel brandDLabel = new DLabel("브랜드 : ", fontTexture);
		final CLabel brandCLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture)
				.setConcatLabel(brandDLabel);
		
		final DLabel overallDLabel = new DLabel("총점 : ", fontTexture);
		final CLabel overallCLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture)
				.setConcatLabel(overallDLabel);
		
		LayoutTable table = new LayoutTable() {
			
			@Override
			public void update(long time) {
				super.update(time);
				priceCLabel.setText(Utils.toPrice(display.price));
				qualityCLabel.setText(Utils.toString(display.quality));
				brandCLabel.setText(Utils.toString(display.getBrand().getTotalBrand()));
				int overall = ProductManager.getInstance().getOverallScore(display);
				overallCLabel.setText(Utils.toString(overall));
			}
		};
		table.left();

		table.col(2).padLeft(2f).cellPrefWidth(100f).left();
		table.col(3).cellPrefWidth(110f).padLeft(10f).left();
		table.col(4).expandX().bottom().padRight(15f).padBottom(5f);

		table.addCell(new SupplyDemandBar(display, 90f)).padLeft(5f).rowSpan(5);
		table.addCell(new Image(data.getImageRegion())).actorPrefSize(100f, 100f).padLeft(1f)
				.rowSpan(5);
		table.addCell(new DLabel(name, fontTexture).setColor(Color4.GREEN4)).colSpan(2);
		table.addCell(new OverallScoreBar(display, 90f)).rowSpan(5);
		table.row();
		table.addCell(new DLabel("공급자 : " + proposal.building.getCorporation().getName(), fontTexture).setColor(proposal.building.getCorporation().getColor()));
		table.addCell(priceDLabel);
		table.row();
		table.addCell(new DLabel("도시 : " + CityManager.getInstance().getCurrentCity().mName, fontTexture));
		table.addCell(qualityDLabel);
		table.addCell();
		table.row();
		table.addCell(new DLabel("거리 : " + String.format("%.2f", proposal.distance) + "km", fontTexture));
		
		table.addCell(brandDLabel);
		table.addCell();
		table.row();
		table.addCell(new DLabel("운송비 : " + Utils.toPrice(freight), fontTexture));
		table.addCell(overallDLabel);
		
		table.addChild(priceCLabel);
		table.addChild(qualityCLabel);
		table.addChild(brandCLabel);
		table.addChild(overallCLabel);

		table.setTag(name);

		//table.debugAll();

		return table;
	}
	
	private LayoutTable style_client_list_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		final ClientCorporation client = (ClientCorporation) args[0];
		
		final DLabel monthlyPurchaseDLabel = new DLabel("이번달 구입 총액 : ", fontTexture);
		final CLabel monthlyPurchaseCLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture)
				.setConcatLabel(monthlyPurchaseDLabel);

		LayoutTable table = new LayoutTable() {
			
			@Override
			public void update(long time) {
				super.update(time);
				monthlyPurchaseCLabel.setText(Utils.toCash(client.purchase.mMonthlyPurchase));
			}
		};
		table.left();
		
		Corporation corp = client.clientBuilding.getCorporation();
		
		table.col(0).padLeft(5f).left();

		table.addCell(new DLabel("고객사 : " + corp.getName(), fontTexture).setColor(corp.getColor()));
		table.row();
		table.addCell(new DLabel("종류 : " + client.clientBuilding.getDescription().name, fontTexture));
		table.row();
		table.addCell(new DLabel("도시 : " + client.city.mName, fontTexture));
		table.row();
		table.addCell(new DLabel("거리 : " + String.format("%.2f", client.distance) + "km", fontTexture));
		table.row();
		table.addCell(monthlyPurchaseDLabel);
		
		table.addChild(monthlyPurchaseCLabel);

		//table.setTag(name);

		//table.debugAll();

		return table;
	}

	private LayoutTable style_rnd_list_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		final Corporation playerCorp = CorporationManager.getInstance().getPlayerCorporation();

		final ManufacturingDescription mdesc = (ManufacturingDescription) args[0];

		final ProductDescription pdesc = ProductManager.getInstance().getPDescByCode(
				mdesc.outputCode);

		String name = pdesc.name;

		TextureRegion region = ProductManager.getInstance().getProductDataByCode(mdesc.outputCode).getImageRegion();
		
		final CLabel currTechLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
		final CLabel maxTechLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);

		LayoutTable table = new LayoutTable() {
			
			@Override
			public void update(long time) {
				super.update(time);
				int currTech = playerCorp.getProductGroupByCode(pdesc.code).tech;
				int maxTech = ProductManager.getInstance().getMaxTechByCode(mdesc.outputCode);
				currTechLabel.setText("" + currTech);
				maxTechLabel.setText("" + maxTech);
			}
		};
		table.left();

		table.col(0).cellWidth(30f);
		table.col(1).cellWidth(70f).actorMaxWidth(70f).left();
		table.col(2).cellWidth(70f);
		table.col(3).expandX();

		Color4 color = Color4.GREEN4;
		ProductGroup group = playerCorp.getProductGroupByCode(mdesc.outputCode);
		if(group.researching) color = Color4.YELLOW4;
		
		table.addCell(new Image(region)).actorPrefSize(30f, 30f);
		table.addCell(new DLabel(name, fontTexture).setColor(color));
		table.addCell(currTechLabel);
		table.addCell(maxTechLabel);

		table.setTag(name);

		// table.debugAll();

		return table;
	}
	
	private LayoutTable style_manufacture_select_list_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		ManufacturingDescription mdesc = (ManufacturingDescription) args[0];

		ProductDescription pdesc = ProductManager.getInstance().getPDescByCode(
				mdesc.outputCode);

		String name = pdesc.name;

		TextureRegion region = ProductManager.getInstance().getProductDataByCode(mdesc.outputCode).getImageRegion();
		
		LayoutTable table = new LayoutTable();
		table.left();

		table.col(0).cellWidth(30f);
		table.col(1).cellWidth(70f).actorMaxWidth(70f).left().padRight(8f);
		table.col(2).cellWidth(50f).actorMaxWidth(50f).left().padRight(3f);
		table.col(3).cellWidth(50f).actorMaxWidth(50f).left().padRight(3f);
		table.col(4).cellWidth(50f).actorMaxWidth(50f).left();

		table.addCell(new Image(region)).actorPrefSize(30f, 30f);
		table.addCell(new DLabel(name, fontTexture).setColor(Color4.GREEN4));
		int n = mdesc.getNumInputs();
		for(int i=0; i<n; i++) {
			ProductDescription desc = ProductManager.getInstance().getPDescByCode(
					mdesc.getInputCode(i));
			String rawName = desc.name;
			if(i != n-1) rawName += ",";
			table.addCell(new DLabel(rawName, fontTexture));
		}
		
		table.setTag(name);

		//table.debugAll();

		return table;
	}

	private LayoutTable style_manufacture_type_list_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture fontTexture = tm.getTexture(R.drawable.font);

		Corporation playerCorp = CorporationManager.getInstance().getPlayerCorporation();

		ManufacturingDescription mdesc = (ManufacturingDescription) args[0];

		ProductDescription pdesc = ProductManager.getInstance().getPDescByCode(
				mdesc.outputCode);

		String name = pdesc.name;

		TextureRegion region = ProductManager.getInstance().getProductDataByCode(mdesc.outputCode).getImageRegion();

		int currTech = playerCorp.getProductGroupByCode(pdesc.code).tech;

		LayoutTable table = new LayoutTable();
		table.left();

		table.col(0).cellPrefWidth(30f);
		table.col(1).cellPrefWidth(50f).left();
		table.col(2).cellPrefWidth(80f);
		table.col(3).cellPrefWidth(80f);

		Color4 color = Color4.GREEN4;

		ProductGroup group = playerCorp.getProductGroupByCode(mdesc.outputCode);
		if(group.researching)
			color = Color4.YELLOW4;

		table.addCell(new Image(region)).actorPrefSize(30f, 30f);
		table.addCell(new DLabel(name, fontTexture).setColor(color));
		table.addCell(new DLabel("" + currTech, fontTexture));
		table.addCell(new DLabel("" + 100, fontTexture));

		// table.debugAll();

		return table;
	}

	private LayoutTable style_manufacture_output_list_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture fontTexture = tm.getTexture(R.drawable.font);

		Corporation playerCorp = CorporationManager.getInstance().getPlayerCorporation();

		ManufacturingDescription mdesc = (ManufacturingDescription) args[0];

		ProductDescription pdesc = ProductManager.getInstance().getPDescByCode(
				mdesc.outputCode);

		String name = pdesc.name;

		TextureRegion region = ProductManager.getInstance().getProductDataByCode(mdesc.outputCode).getImageRegion();

		int currTech = playerCorp.getProductGroupByCode(pdesc.code).tech;

		LayoutTable table = new LayoutTable();
		table.left();

		table.col(0).cellPrefWidth(30f);
		table.col(1).cellPrefWidth(50f).left();
		table.col(2).cellPrefWidth(80f);
		table.col(3).cellPrefWidth(80f);

		Color4 color = Color4.GREEN4;

		ProductGroup group = playerCorp.getProductGroupByCode(mdesc.outputCode);
		if(group.researching)
			color = Color4.YELLOW4;

		table.addCell(new Image(region)).actorPrefSize(30f, 30f);
		table.addCell(new DLabel(name, fontTexture).setColor(color));
		table.addCell(new DLabel("" + currTech, fontTexture));
		table.addCell(new DLabel("" + 100, fontTexture));

		// table.debugAll();

		return table;
	}
	
	public static class SupplyDemandBar extends Widget<SupplyDemandBar> {
		
		private static final int MAX_SCORE = 100;
		
		private float mMaxHeight;
		
		private TextureRegion mSupplyRegion;
		private TextureRegion mDemandRegion;
		
		private DisplayProduct mDisplay;
		
		public SupplyDemandBar(DisplayProduct display, float maxHeight) {
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture barTexture = tm.getTexture(R.drawable.atlas_department_bar_graph);

			mSupplyRegion = StockDepartment.sDepartmentSupplyBarRegion;
			mDemandRegion = StockDepartment.sDepartmentDemandBarRegion;
			
			mMaxHeight = maxHeight;
			
			set(display);
		}
		
		public SupplyDemandBar set(DisplayProduct display) {
			mDisplay = display;
			return this;
		}

		@Override
		public void layout() {
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			
			batch.setColor(Color4.WHITE4);
			
			
			float supplyLength;
			float demandLength;
			if(mDisplay.supplyDemand >= 0) {
				supplyLength = mMaxHeight;
				demandLength = mMaxHeight * mDisplay.supplyDemand;
			} else {
				supplyLength = -mMaxHeight * mDisplay.supplyDemand;
				demandLength = mMaxHeight;
			}
			
			
			float width = mSupplyRegion.getRegionWidth();
			
			float x = getX();
			float y = getY();
			

			// 공급
			// float length = 80*mAvgUtilization/100;
			batch.draw(StockDepartment.sDepartmentSupplyBarRegion,
					x, y + (mMaxHeight - supplyLength), 
					StockDepartment.sDepartmentSupplyBarRegion.getRegionWidth(), supplyLength);

			// 수요
			// float length = 80*mAvgUtilization/100;
			batch.draw(StockDepartment.sDepartmentDemandBarRegion,
					x + width, y + (mMaxHeight - demandLength), 
					StockDepartment.sDepartmentDemandBarRegion.getRegionWidth(), demandLength);
			
			
		}

		@Override
		protected float getDefaultPrefWidth() {
			return mSupplyRegion.getRegionWidth() * 2f;
		}

		@Override
		protected float getDefaultPrefHeight() {
			return mMaxHeight;
		}
		
		@Override
		public Float getMaxHeight() {
			return mMaxHeight;
		}

		public SupplyDemandBar setMaxHeight(float maxHeight) {
			mMaxHeight = maxHeight;
			return this;
		}
	}
	
}
