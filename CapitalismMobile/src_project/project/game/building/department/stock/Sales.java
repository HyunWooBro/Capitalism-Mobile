package project.game.building.department.stock;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.framework.casting.ListBoxCasting.ListBoxUpdater;
import project.game.GameScene;
import project.game.GameScene.GameScreenType;
import project.game.Time;
import project.game.building.Building;
import project.game.building.department.DepartmentManager;
import project.game.cell.CellManager;
import project.game.city.City;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductData;
import project.game.ui.UIManager;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.ScrollPane;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.VerticalTable;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.stage.actor.widget.utils.Align.HAlign;

public class Sales extends StockDepartment {

	public double mNewPrice;
	public double mTempNewPrice;

	protected double mSalesEarning;
	
	public DisplayProduct mDisplay;
	
	public double[] monthlyRevenueArray = new double[Time.NUM_MONTHS];
	public double[] monthlyProfitArray = new double[Time.NUM_MONTHS];
	
	public double mProfit;
	
	private DialogWindow mDialog;

	private static PushButton sClearingPushButton;
	
	private static PushButton sClientsPushButton;
	
	private ListBoxUpdater<ClientCorporation> mClientCorporationUpdater = new ListBoxUpdater<ClientCorporation>() {
		
		@Override
		public void createPreList() {
			tempPreList.clear();
			
			DisplayProduct display = mDisplay;
			List<Purchase> purchaseList = display.purchaseList;
			int n = purchaseList.size();
			for(int i=0; i<n; i++) {
				Purchase purchase = purchaseList.get(i);
				
				ClientCorporation client = new ClientCorporation();
				client.clientBuilding = purchase.getDepartmentManager().getBuilding();
				client.purchase = purchase;
				client.distance = CellManager.getInstance().getDistanceBetweenBuildings(mDepartmentManager.getBuilding(), client.clientBuilding);
				client.city = display.city;
				tempPreList.add(client);
			}
		}
		
		@Override
		public boolean compare() {
			if(currPreList.size() != tempPreList.size()) return false;
			int n = currPreList.size();
			for(int i=0; i<n; i++) {
				if(currPreList.get(i).clientBuilding != tempPreList.get(i).clientBuilding) return false;
				if(currPreList.get(i).purchase != tempPreList.get(i).purchase) return false;
			}
			return true;
		}
		
		@Override
		public List<Actor<?>> createItemList() {
			CastingDirector cd = CastingDirector.getInstance();

			List<Actor<?>> itemList = new ArrayList<Actor<?>>();
			for(int i = 0; i < tempPreList.size(); i++) {
				LayoutTable table = cd.cast(LayoutTable.class, "client_list_item",
						tempPreList.get(i));
				table.setUserObject(tempPreList.get(i));
				itemList.add(table);
			}
			
			return itemList;
		}
		
		@Override
		public void updateListBox(List<Actor<?>> itemList) {
			ListBox listBox = (ListBox) mDialog.getContentTable().getCellList().get(0).getActor();
			listBox.disposeAll();
			listBox.set(itemList);
			if(!itemList.isEmpty() && listBox.getSelectedIndex() == -1) {
				listBox.select(0);
			}
			
			// 스크롤 보정
			ScrollPane scroll = listBox.getScroll();
			// VerticalTable 자체의 높이를 이용하지 않는 이유는 ScrollPane보다 작은 경우 확장되기 때문이다. 
			// 항상 정확한 높이를 구하기 위해서는 컨테이너의 높이를 이용해야 한다.
			float containerHeight = ((VerticalTable) scroll.getContent()).getContainerHeight();
			// 컨테이너의 높이보다 리스트박스의 높이가 크면
			if(listBox.getHeight() > containerHeight) {
				scroll.setScrollY(0f);
			} else {
				float diff = listBox.getHeight() - (containerHeight - scroll.getScrollY());
				// 컨테이너의 하단 y값이 리스트박스보다 위에 위치하면
				if(diff > 0f) {
					scroll.setScrollY(scroll.getScrollY() + diff);
				}
			}
			// 아이템의 시작 위치가 리스트박스보다 아래에 위치하면
			if(scroll.getScrollY() < 0) {
				scroll.setScrollY(0f);
			}
			
			currPreList = tempPreList;
			tempPreList = new ArrayList<ClientCorporation>();
		}
	};

	public Sales(int index, DepartmentManager department_manager) {
		super(index, department_manager);
		mNumEmployees = 3;
		
		initClearingPushButton();
		initClientsPushButton();
	}
	
	private void initClearingPushButton() {
		if(sClearingPushButton != null) return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		
		sClearingPushButton = cd.cast(PushButton.class, "static_text", R.string.label_clear)
				.moveTo(140f, 110f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Sales purchase = (Sales) listener.getParent();
						purchase.mCurrStock = 0;
					}
				})
				.pack().setWidth(70f);
	}
	
	private void initClientsPushButton() {
		if(sClientsPushButton != null) return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		
		sClientsPushButton = cd.cast(PushButton.class, "static_text", R.string.label_clients)
				.moveTo(250f, 110f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Sales purchase = (Sales) listener.getParent();
						purchase.bringUpClientsDialog();
					}
				})
				.pack().setWidth(70f);
	}
	
	public int trade(int amount) {
		int trade = 0;
		
		if(mCurrStock > 0) {
			double price = mDepartmentManager.getBuilding().getDisplayProduct(mProduct.desc, mProduct.producer).price;
			
			trade = amount;
			if(trade > mMaxWorkPerDay) trade = mMaxWorkPerDay;
			if(trade > mCurrStock) trade = mCurrStock;
			
			mProfit = trade * price;
			mCurrStock -= trade;
		}
		return amount - trade;
	}
	
	@Override
	protected double work() {
		
		if(mProduct != null) {
			transferStock();
			if(isSelected()) {
				addChild(sClearingPushButton);
				if(mDepartmentManager.getBuilding().isForSaleToCorporation() 
						&& mDisplay != null && !mDisplay.purchaseList.isEmpty()) {
					addChild(sClientsPushButton);
				}
			}
		} else {
			searchSupplier();
		}
		
		return mProfit;
	}
	
	@Override
	protected void drawPanel(Batch batch, float parentAlpha) {
		super.drawPanel(batch, parentAlpha);

		if(mProduct != null) {
			ProductData data = ProductManager.getInstance().getProductDataByCode(mProduct.desc.code);
			DLabel label = data.getNameLabel();
			
			label.southWest();
			label.moveTo(
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 60 / 2);
			label.draw(batch, parentAlpha);

			float stockLength = (float) 80 * mCurrStock / mMaxStock;
			int n = (int) (stockLength / 10);
			if(n == 0 && mCurrStock != 0) {
				n = 1;
			}
			for(int i = 0; i < n; i++) {
				batch.draw(sDepartmentStockRegion,
						DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2 + i * 10,
						DepartmentManager.sDepartmentRectangles[mIndex].top() + 100 / 2,
						sDepartmentStockRegion.getRegionWidth(),
						sDepartmentStockRegion.getRegionHeight());
			}

			float supplyLength;
			float demandLength;
			
			DisplayProduct display = mDepartmentManager.getBuilding().getDisplayProduct(mProduct.desc, mProduct.producer);
			
			if(display.supplyDemand >= 0) {
				supplyLength = 80f;
				demandLength = 80f * display.supplyDemand;
			} else {
				supplyLength = -80f * display.supplyDemand;
				demandLength = 80f;
			}

			// 공급
			batch.draw(sDepartmentSupplyBarRegion,
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 120 / 2, 
					supplyLength, 
					sDepartmentSupplyBarRegion.getRegionWidth(), 
					false, false, true);

			// 수요
			batch.draw(sDepartmentDemandBarRegion,
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 135 / 2, 
					demandLength,
					sDepartmentDemandBarRegion.getRegionWidth(), 
					false, false, true);

			// 활용
			float UtilizationLength = 80 * display.avgUtilization;
			batch.draw(sDepartmentUtilizationBarRegion,
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 155 / 2,
					UtilizationLength, sDepartmentUtilizationBarRegion.getRegionWidth(), false,
					false, true);
		}
	}

	@Override
	protected void drawContent(Batch batch, float parentAlpha) {
		
		ProductData data = null;
		if(mProduct != null) {
			data = ProductManager.getInstance().getProductDataByCode(mProduct.desc.code);
		}
		
		if(data == null) {
			batch.draw(sDepartmentEmptyRegion, 30 / 2, 50 / 2);
		} else {
			batch.draw(data.getImageRegion(), 30 / 2, 50 / 2);
		}
		
		batch.draw(sDepartmentContentBarRegion, 300 / 2, 50 / 2);
		
		if(mProduct != null) {
			DLabel label = data.getNameLabel();
			label.south();
			label.moveTo(300 / 2 + 163 / 2, 50 / 2 + 30 / 2);
			label.draw(batch, parentAlpha);
		}
		
		// 하단 출력
		super.drawContent(batch, parentAlpha);

		// 제품이 지정되어 있다면
		if(mProduct != null) {
			sDepartmentStockLabel.moveTo(350 / 2, 450 / 2);
			sDepartmentStockLabel.draw(batch, parentAlpha);
			sCountLabel.setConcatLabel(sDepartmentStockLabel);
			sCountLabel.setText(" : " + (int) (mCurrStock/* / mProduct.desc.price*/));
			sCountLabel.draw(batch, parentAlpha);

			float stockLength = (float) 80 * mCurrStock / mMaxStock;
			int n = (int) (stockLength / 10);
			if(n == 0 && mCurrStock != 0) {
				n = 1;
			}
			for(int i = 0; i < n; i++) {
				batch.draw(sDepartmentStockRegion, 350 / 2 + 65 + i * 10, 450 / 2 - 6,
						sDepartmentStockRegion.getRegionWidth(),
						sDepartmentStockRegion.getRegionHeight());
			}
			
			DisplayProduct display = mDepartmentManager.getBuilding().getDisplayProduct(mProduct.desc, mProduct.producer);

			sDepartmentUtilizationLabel.moveTo(350 / 2, 480 / 2);
			sDepartmentUtilizationLabel.draw(batch, parentAlpha);
			sCountLabel.setConcatLabel(sDepartmentUtilizationLabel);
			sCountLabel.setText(" : " + Utils.toPercent2(display.avgUtilization));
			sCountLabel.draw(batch, parentAlpha);
			
			float UtilizationLength = 80 * display.avgUtilization;
			batch.draw(sDepartmentUtilizationBarRegion, 350 / 2 + 65, 480 / 2 - 6,
					UtilizationLength, sDepartmentUtilizationBarRegion.getRegionWidth(), false,
					false, true);
		}
	}
	
	@Override
	protected void resetDaily() {
		mProfit = 0;
	}
	
	@Override
	protected void resetMonthly() {
		int index = Time.getInstance().getMonthlyArrayIndex();// + 1;
		monthlyRevenueArray[index] = 0;
	}
	
	private void bringUpClientsDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible()) return;
		}
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		CastingDirector cd = CastingDirector.getInstance();
		
		final ListBox list = cd.cast(ListBox.class, "updatable", mClientCorporationUpdater, HAlign.LEFT, 1f)
				.select(0)
				.setPrefSize(250f, 250f);
		
		// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
		list.getScroll().startScrollFade();

		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "고객사 보기")
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Actor<?> item = list.getSelectedItem();
						if(item == null) return;

						ClientCorporation client = (ClientCorporation) item.getUserObject();
						Building building = client.clientBuilding;

						GameScene.chnageGameScreenType(GameScreenType.MAP);

						CellManager manager = CellManager.getInstance();

						manager.moveCameraToCell((int) building.mFirstCellPos.x,
								(int) building.mFirstCellPos.y);

						manager.selectCell((int) building.mFirstCellPos.x,
								(int) building.mFirstCellPos.y);

						closeDialog();
					}
				});
		
		PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "확인").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog();
					}
				});

		mDialog = cd.cast(DialogWindow.class, "default")
				.setTitle(new DLabel("고객사", fontTexture)).setModal(true)
				.addContent(list)
				.addButton(button1).addButton(button2)
				.pack().moveCenterTo(320f, 200f);

		UIManager.getInstance().addChild(mDialog);
		mDialog.open();

		// Core.APP.debug(mDialog.toString());
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
	protected void onSelected() {
		if(mProduct == null) return;
		addChild(sClearingPushButton);
		if(mDepartmentManager.getBuilding().isForSaleToCorporation() 
				&& mDisplay != null && !mDisplay.purchaseList.isEmpty()) {
			addChild(sClientsPushButton);
		}
	}

	@Override
	public DepartmentType getDepartmentType() {
		return DepartmentType.SALES;
	}
	
	@Override
	protected int getBaseStock() {
		return 200000;
	}
	
	@Override
	protected float getMaxWorkScale() {
		return 0.2f;
	}
	
	public static class ClientCorporation {
		
		public Building clientBuilding;
		
		public Purchase purchase;
		
		public float distance;
		
		public City city;
	}

}
