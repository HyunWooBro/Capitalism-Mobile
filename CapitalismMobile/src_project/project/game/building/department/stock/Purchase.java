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
import project.game.building.BuildingManager;
import project.game.building.department.DepartmentManager;
import project.game.cell.CellManager;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductData;
import project.game.product.ProductManager.ProductDescription;
import project.game.ui.UIManager;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.ScrollPane;
import core.scene.stage.actor.widget.box.DropDownBox;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.VerticalTable;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.stage.actor.widget.utils.Align.HAlign;

public abstract class Purchase extends StockDepartment {

	private static int sIndex1;
	private static int sIndex2;

	public Building mLinkedBuilding;
	public double mDistanceFromLinkedBuilding; // 단위는 km
	
	
	public double mFreight;
	
	// 활용 0 ~ 100%
	public int[] mUtilizationArray = new int[Time.NUM_DAYS];
	
	public float mAvgUtilization;
	
	public double mDailyPurchase;
	
	public double mMonthlyPurchase; 
	
	private DisplayProduct mDisplay;
	
	/** 거래중인가? */
	private boolean mDealing = true;

	private static PushButton sConnentionButton;
	
	private static PushButton sSupplierButton;
	
	private static PushButton sDealingButton;
	private static SLabel sSuspendLabel;
	private static SLabel sResumeLabel;

	private DialogWindow mDialog;

	private List<ProductProposal> mProductPurchaseList = new ArrayList<ProductProposal>();

	private ListBoxUpdater<ProductProposal> mProductProposalUpdater = new ListBoxUpdater<ProductProposal>() {
		
		@Override
		public void createPreList() {
			SupplierFilter sf = getSupplierFilter(sIndex1);
			ProductFilter pf = getProductFilter(sIndex2);
			tempPreList.clear();
			Corporation playerCorp = CorporationManager.getInstance().getPlayerCorporation();
	
			// 현재 모든 도시의 건물에 대해 제안을 받게 되어 있는데 현재 도시와 모든 도시를 
			// 구분할 수 있는 버튼과 같은 것이 필요
			List<Building> buildingList = BuildingManager.getInstance().getBuildingList();
			int n = buildingList.size();
			for(int i = 0; i < n; i++) {
				Building building = buildingList.get(i);
				// 기업에 대한 판매를 하지 않으면 무시
				if(!building.isForSaleToCorporation()) continue;
				// 구매부가 속한 건물은 무시
				if(building == mDepartmentManager.getBuilding()) continue;
				
				List<DisplayProduct> displayProductList = building.getDisplayProductList();
				if(displayProductList == null || displayProductList.isEmpty()) continue;
				
				Corporation corp = building.getCorporation();
				
				int m = displayProductList.size();
				for(int j=0; j<m; j++) {
					DisplayProduct display = displayProductList.get(j);
					// 다른 기업에서 내부판매를 한다면 무시한다.
					if(corp != playerCorp && display.internalSales) continue;
					
					if(sf.filter(corp) && pf.filter(display.desc)) {
						ProductProposal productPurchase = new ProductProposal();
						productPurchase.building = building;
						productPurchase.distance = CellManager.getInstance().getDistanceBetweenBuildings(mDepartmentManager.getBuilding(), productPurchase.building);
						productPurchase.display = display;
						tempPreList.add(productPurchase);
					}
				}
			}
		}
	
		@Override
		public boolean compare() {
			if(currPreList.size() != tempPreList.size()) return false;
			int n = currPreList.size();
			for(int i=0; i<n; i++) {
				if(currPreList.get(i).building != tempPreList.get(i).building) return false;
				if(currPreList.get(i).display != tempPreList.get(i).display) return false;
			}
			return true;
		}
	
		@Override
		public List<Actor<?>> createItemList() {
			CastingDirector cd = CastingDirector.getInstance();
			
			List<Actor<?>> itemList = new ArrayList<Actor<?>>();
			for(int i = 0; i < tempPreList.size(); i++) {
				LayoutTable table = cd.cast(LayoutTable.class, "product_proposal_list_item",
						tempPreList.get(i));
				table.setUserObject(tempPreList.get(i));
				itemList.add(table);
			}
	
			Utils.sort(itemList);
			
			return itemList;
		}
	
		@Override
		public void updateListBox(List<Actor<?>> itemList) {
			// 이유를 알 수 없지만 필터를 설정한 경우 null이 되기 때문에 다음의 조치를 취한다. 
			if(mDialog == null) return;
			
			ListBox listBox = (ListBox) mDialog.getContentTable().getCellList().get(1).getActor();
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
			tempPreList = new ArrayList<ProductProposal>();
		}
	};

	public Purchase(int index, DepartmentManager departmentManager) {
		super(index, departmentManager);
		mNumEmployees = 5;

		initConnectionButton();
		initDealingButton();
		initSupplierButton();
	}

	private void initConnectionButton() {
		if(sConnentionButton != null) return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		
		sConnentionButton = cd.cast(PushButton.class, "static_text", R.string.label_connect)
				.moveTo(140f, 110f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Purchase purchase = (Purchase) listener.getParent();
						purchase.bringUpProductProposalDialog();
					}
				})
				.pack().setWidth(60f);
	}
	
	private void initDealingButton() {
		if(sDealingButton != null) return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		sDealingButton = cd.cast(PushButton.class, "default")
				.moveTo(195f, 110f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Purchase purchase = (Purchase) listener.getParent();
						purchase.mDealing = !purchase.mDealing;
						if(purchase.mDealing) {
							sDealingButton.getCellList().get(0).setActor(sSuspendLabel);
						} else {
							sDealingButton.getCellList().get(0).setActor(sResumeLabel);
						}
					}
				});

		sSuspendLabel = new SLabel(R.string.label_suspend, fontTexture);
		sResumeLabel = new SLabel(R.string.label_resume, fontTexture);

		sDealingButton.addCell(sSuspendLabel);
		sDealingButton.pack().setWidth(60f);
	}
	
	private void initSupplierButton() {
		if(sSupplierButton != null) return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		
		sSupplierButton = cd.cast(PushButton.class, "static_text", R.string.label_supplier)
				.moveTo(250f, 110f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Purchase purchase = (Purchase) listener.getParent();
						purchase.bringUpSupplierDialog();
					}
				})
				.pack().setWidth(70f);
	}
	
	public int trade(DisplayProduct display, int amount) {
		// 할당량이 이미 소지되었거나 거래중단인 경우 빠져나간다.
		if(amount == 0 || !mDealing) {
			mDailyPurchase = 0;
			return amount;
		}
		
		int index = Time.getInstance().getDailyArrayIndex();
		int actual_work = 0;
		
		actual_work = mMaxWorkPerDay;
		
		int space = mMaxStock - mCurrStock;
		if(actual_work > space) actual_work = space;
		if(actual_work > amount) actual_work = amount;
		
		int supply = actual_work;
		if(amount > actual_work && amount < mMaxWorkPerDay) {
			supply = amount;
		} else if(amount > mMaxWorkPerDay) {
			supply = mMaxWorkPerDay;
		}
		
		Corporation corp = mDepartmentManager.getBuilding().getCorporation();
		
		double price = (display.price + mFreight) * actual_work;// / display.desc.price;
		if(corp.getFinancialData().cash < price) {
			// 일단 사지 않는다. 이것 수정해야 함
			actual_work = 0;
			
		} else {
			
			if(mProduct.quality != display.quality) {
				mProduct.quality = mProduct.quality*mCurrStock + display.quality*actual_work; 
				mProduct.quality /= (mCurrStock + actual_work);
			}
			
			if(mProduct.cost != display.price) {
				mProduct.cost = mProduct.cost*mCurrStock + display.price*actual_work; 
				mProduct.cost /= (mCurrStock + actual_work);
			}
			
			if(mProduct.freight != mFreight) {
				mProduct.freight = mProduct.freight*mCurrStock + mFreight*actual_work; 
				mProduct.freight /= (mCurrStock + actual_work);
			}
			
			mCurrStock += actual_work;
			mDailyPurchase = price;
			mMonthlyPurchase += price;
		}
		
		computeUtilization(actual_work);
		
		return amount - actual_work;
	}

	@Override
	protected double work() {
		if(mProduct != null) {
			if(isSelected()) {
				addChild(sSupplierButton);
				if(!hasChild(sDealingButton)) {
					addChild(sDealingButton);
					if(mDealing) {
						sDealingButton.getCellList().get(0).setActor(sSuspendLabel);
					} else {
						sDealingButton.getCellList().get(0).setActor(sResumeLabel);
					}
				}
			}
			return mDailyPurchase;
		} else {
			return 0;
		}
	}
	
	@Override
	protected void onSelected() {
		addChild(sConnentionButton);
		if(mProduct == null) return;
		addChild(sSupplierButton);
		addChild(sDealingButton);
		if(mDealing) {
			sDealingButton.getCellList().get(0).setActor(sSuspendLabel);
		} else {
			sDealingButton.getCellList().get(0).setActor(sResumeLabel);
		}
	}
	
	@Override
	protected void drawPanel(Batch batch, float parentAlpha) {
		super.drawPanel(batch, parentAlpha);

		if(mProduct != null) {
			ProductData data = ProductManager.getInstance().getProductDataByCode(mProduct.desc.code);
			DLabel label = data.getNameLabel()
					.southWest()
					.moveTo(
						DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
						DepartmentManager.sDepartmentRectangles[mIndex].top() + 60 / 2);
			
			if(!mDealing) label.setColor(Color4.LTRED4);
			label.draw(batch, parentAlpha);
			if(!mDealing) {
				label.setColor(Color4.WHITE4);
				batch.setColor(Color4.WHITE4);
			}

			float stockLength = (float) 80 * mCurrStock / mMaxStock;
			int n = (int) (stockLength / 10);
			if(n == 0 && mCurrStock != 0) {
				n = 1;
			}

			TextureRegion region = sDepartmentStockRegion;
			float width = region.getRegionWidth();
			float height = region.getRegionHeight();

			for(int i = 0; i < n; i++) {
				batch.draw(region, DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2
						+ i * 10, DepartmentManager.sDepartmentRectangles[mIndex].top() + 100 / 2,
						width, height);
			}

			float supplyLength;
			float demandLength;
			if(mDisplay.supplyDemand >= 0) {
				supplyLength = 80;
				demandLength = 80 * mDisplay.supplyDemand;
			} else {
				supplyLength = -80 * mDisplay.supplyDemand;
				demandLength = 80;
			}

			// 공급
			batch.draw(sDepartmentSupplyBarRegion,
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 120 / 2, supplyLength,
					sDepartmentUtilizationBarRegion.getRegionWidth(), false, false, true);

			// 수요
			batch.draw(sDepartmentDemandBarRegion,
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 135 / 2, demandLength,
					sDepartmentUtilizationBarRegion.getRegionWidth(), false, false, true);

			// 활용
			float UtilizationLength = 80 * mAvgUtilization;
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

		if(data != null) {
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

			sDepartmentUtilizationLabel.moveTo(350 / 2, 480 / 2);
			sDepartmentUtilizationLabel.draw(batch, parentAlpha);
			sCountLabel.setConcatLabel(sDepartmentUtilizationLabel);
			sCountLabel.setText(" : " + Utils.toPercent2(mAvgUtilization));
			sCountLabel.draw(batch, parentAlpha);

			float UtilizationLength = 80 * mAvgUtilization;
			batch.draw(sDepartmentUtilizationBarRegion, 350 / 2 + 65, 480 / 2 - 6,
					UtilizationLength, sDepartmentUtilizationBarRegion.getRegionWidth(), false,
					false, true);
		}
	}
	
	private void computeUtilization(int work) {
		int index = Time.getInstance().getDailyArrayIndex();
		mUtilizationArray[index] = work;
		
		float totalUtilization = 0;
		for(int i=0; i<Time.NUM_DAYS; i++) {
			totalUtilization += mUtilizationArray[i];
		}
		mAvgUtilization = totalUtilization / (mMaxWorkPerDay*Time.NUM_DAYS);
	}
	
	@Override
	protected void confirm(ProductDescription desc, Corporation producer, int quality, double cost, double freight) {
		super.confirm(desc, producer, quality, cost, freight);
		mUtilizationArray = new int[Time.NUM_DAYS];
	}
	
	@Override
	protected void resetDaily() {
		int index = Time.getInstance().getDailyArrayIndex();
		mUtilizationArray[index] = 0;
	}
	
	@Override
	protected void resetMonthly() {
		mMonthlyPurchase = 0;
	}
	
	public boolean isDealing() {
		return mDealing;
	}
	
	private void bringUpSupplierDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible()) return;
		}

		buildSupplier();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		CastingDirector cd = CastingDirector.getInstance();

		final LayoutTable table = cd.cast(LayoutTable.class, "product_proposal_list_item",
				mProductPurchaseList.get(0));

		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "공급사 보기")
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Building building = mLinkedBuilding;
						if(building == null) {
							closeDialog();
							return;
						}

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
				.setTitle(new DLabel("공급사", fontTexture)).setModal(true)
				.addContent(table)
				.addButton(button1).addButton(button2)
				.pack().moveCenterTo(320f, 200f);

		UIManager.getInstance().addChild(mDialog);
		mDialog.open();

		// Core.APP.debug(mDialog.toString());
	}
	
	private void buildSupplier() {
		mProductPurchaseList.clear();
		Corporation playerCorp = CorporationManager.getInstance().getPlayerCorporation();
		Building building = mLinkedBuilding;
		Corporation corp = building.getCorporation();
		
		DisplayProduct display = mDisplay;
		// 다른 기업에서 내부판매를 한다면 무시한다.
		if(corp != playerCorp && display.internalSales) return;
		
		ProductProposal productPurchase = new ProductProposal();
		productPurchase.building = building;
		productPurchase.distance = CellManager.getInstance().getDistanceBetweenBuildings(mDepartmentManager.getBuilding(), productPurchase.building);
		productPurchase.display = display;
		mProductPurchaseList.add(productPurchase);
	}
	
	private void bringUpProductProposalDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible()) return;
		}
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		CastingDirector cd = CastingDirector.getInstance();
		
		final ListBox list = cd.cast(ListBox.class, "updatable", mProductProposalUpdater, HAlign.LEFT, 1f)
				.select(0)
				.setPrefSize(370f, 250f);
		
		// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
		list.getScroll().startScrollFade();
		
		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "거래 사업체 보기")
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Actor<?> item = list.getSelectedItem();
						if(item == null) return;

						ProductProposal productPurchase = (ProductProposal) item.getUserObject();
						Building building = productPurchase.building;

						GameScene.chnageGameScreenType(GameScreenType.MAP);

						CellManager manager = CellManager.getInstance();

						manager.moveCameraToCell((int) building.mFirstCellPos.x,
								(int) building.mFirstCellPos.y);

						manager.selectCell((int) building.mFirstCellPos.x,
								(int) building.mFirstCellPos.y);

						closeDialog();
					}
				});

		PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "거래 체결").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						
						Actor<?> item = list.getSelectedItem();
						if(item == null) return;
						
						Purchase purchase = Purchase.this;
						
						Building currBuilding = mDepartmentManager.getBuilding();
						
						ProductProposal productPurchase = (ProductProposal) item.getUserObject();
						
						float distance = CellManager.getInstance().getDistanceBetweenBuildings(productPurchase.building, currBuilding);
						
						// 기존 진열제품에서 현재의 구매부를 제거한다.
						if(mDisplay != null) {
							mDisplay.purchaseList.remove(purchase);
						}
						
						mFreight = ProductManager.getInstance().calculateFreight(distance, productPurchase.display.desc);
						
						purchase.mLinkedBuilding = productPurchase.building;
						confirmProduct(productPurchase.display.desc, 
								productPurchase.display.producer, 
								productPurchase.display.quality, 
								productPurchase.display.price, 
								mFreight);
						purchase.mDistanceFromLinkedBuilding = distance;
						
						mDisplay = productPurchase.display;
						
						productPurchase.display.purchaseList.add(purchase);
						
						DepartmentManager.sSelectedDepartmentIndex = -1;
						
						// 거래가 중단되었으면 재개한다.
						mDealing = true;
						
						// 제품계약이 새로 체결되었으므로 연결된 모든 부서에 알린다.
						notifyDepartmentChange();
						
						closeDialog();
					}
				});
		
		PushButton button3 = cd.cast(PushButton.class, "dynamic_text", "거래 취소").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog();
					}
				});

		LayoutTable dropdownTable = new LayoutTable();

		DropDownBox dropDownBox1 = cd.cast(DropDownBox.class, "default").setDividerHeight(2f)
				.set(getSupplierList()).setMaxVisibleItems(10).select(0).setItemHAlign(HAlign.LEFT)
				.setItemPadding(2f, 5f, 0f, 1f).addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						if(event.isTargetActor()) {
							DropDownBox box = (DropDownBox) listener;
							sIndex1 = box.getSelectedIndex();
							mProductProposalUpdater.createPreList();
							mProductProposalUpdater.updateListBox(mProductProposalUpdater.createItemList());
						}
					}
				});

		DropDownBox dropDownBox2 = cd.cast(DropDownBox.class, "default").setDividerHeight(2f)
				.set(getProductList()).setMaxVisibleItems(10).select(0).setItemHAlign(HAlign.LEFT)
				.setItemPadding(2f, 5f, 0f, 1f).addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						if(event.isTargetActor()) {
							DropDownBox box = (DropDownBox) listener;
							sIndex2 = box.getSelectedIndex();
							mProductProposalUpdater.createPreList();
							mProductProposalUpdater.updateListBox(mProductProposalUpdater.createItemList());
						}
					}
				});

		dropdownTable.addCell(dropDownBox1).width(150f).pad(5f);
		dropdownTable.addCell(dropDownBox2).width(150f).pad(5f);

		mDialog = cd.cast(DialogWindow.class, "default")
				.setTitle(new DLabel("거래할 제품을 선택하세요", fontTexture))
				.setModal(true)
				.addContent(dropdownTable).addContent(list)
				.addButton(button1).addButton(button2).addButton(button3)
				.pack().moveCenterTo(320f, 200f);

		UIManager.getInstance().addChild(mDialog);
		mDialog.open();

		// Core.APP.debug(mDialog.toString());
	}

	private List<Actor<?>> getSupplierList() {

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> nameList = new ArrayList<Actor<?>>();

		nameList.add(new DLabel("모든 공급자", fontTexture));

		// 각 기업 이름을
		List<Corporation> corpList = CorporationManager.getInstance().getCorporationList();
		int n = corpList.size();
		for(int i = 0; i < n; i++) {
			Corporation corp = corpList.get(i);
			nameList.add(new DLabel(corp.getName(), fontTexture));
		}

		return nameList;
	}

	protected abstract List<Actor<?>> getProductList();

	private SupplierFilter getSupplierFilter(int index) {
		return new SupplierFilter(index, mDepartmentManager);
	}

	protected abstract ProductFilter getProductFilter(int index);
	
	private void closeDialog() {
		if(mDialog == null) return;
		
		mDialog.close();
		mDialog.addAction(new Run(new Runnable() {

			@Override
			public void run() {
				sIndex1 = 0;
				sIndex2 = 0;

				UIManager.getInstance().removeChild(mDialog);
				mDialog.disposeAll();
				mDialog = null;
			}
		}).setStartOffset(mDialog.getAnimationDuration()));
	}
	
	public static class ProductProposal {
		
		public Building building;
		
		public float distance;
		
		public DisplayProduct display;
	}

	public static abstract class ProductFilter {

		public abstract boolean filter(ProductDescription description);
	}

	public static class SupplierFilter {

		private DepartmentManager mManager;

		private int mIndex;

		public SupplierFilter(int index, DepartmentManager manager) {
			mIndex = index;
			mManager = manager;
		}

		public boolean filter(Corporation corporation) {
			List<Corporation> corpList = CorporationManager.getInstance().getCorporationList();
			if(mIndex != 0) {
				corpList = new ArrayList<Corporation>(corpList);
				Corporation filter = corpList.get(mIndex - 1);
				corpList.clear();
				corpList.add(filter);
			}
			return corpList.contains(corporation);
		}
	}

	@Override
	public DepartmentType getDepartmentType() {
		return DepartmentType.PURCHASE;
	}
	
	@Override
	protected float getMaxWorkScale() {
		return 0.2f;
	}

}
