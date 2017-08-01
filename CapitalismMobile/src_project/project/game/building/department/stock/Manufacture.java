package project.game.building.department.stock;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.framework.casting.ListBoxCasting.ListBoxUpdater;
import project.game.Time;
import project.game.building.department.Department;
import project.game.building.department.DepartmentManager;
import project.game.corporation.Corporation;
import project.game.product.Product;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ManufacturingDescription;
import project.game.product.ProductManager.ProductData;
import project.game.product.ProductManager.ProductDescription;
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

public class Manufacture extends StockDepartment {

	private static final List<String> TMP_STRING_LIST = new ArrayList<String>();

	public int[] mInputQuantities;
	
	public int[] mInputCurrStocks;
	public int[] mInputMaxStocks;
	
	public int[] mInputQualities;
	public double[] mInputCosts;
	public double[] mInputFreights;
	
	private ManufacturingDescription mDesc;

	private boolean mMore;

	private static PushButton sSelectButton;

	private List<ProductDescription> mAvailableDescriptionList;
	
	private boolean mChanged;
	
	private int[] mUtilizationArray = new int[Time.NUM_DAYS];
	
	private float mAvgUtilization;

	private DialogWindow mDialog;
	
	private ListBoxUpdater<ProductDescription> mMDescriptionUpdater = new ListBoxUpdater<ProductDescription>() {
		
		@Override
		public void init() {
		}

		@Override
		public void createPreList() {
			if(currPreList == null) {
				currPreList = mAvailableDescriptionList;
			}
		}

		@Override
		public boolean compare() {
			if(currPreList.size() != mAvailableDescriptionList.size()) return false;
			int n = currPreList.size();
			for(int i=0; i<n; i++) {
				if(currPreList.get(i) != mAvailableDescriptionList.get(i)) return false;
			}
			return true;
		}

		@Override
		public List<Actor<?>> createItemList() {
			CastingDirector cd = CastingDirector.getInstance();

			List<Actor<?>> itemList = new ArrayList<Actor<?>>();
			for(int i = 0; i < mAvailableDescriptionList.size(); i++) {
				ManufacturingDescription desc = ProductManager.getInstance().getMDescByOutputCode(mAvailableDescriptionList.get(i).code);
				LayoutTable table = cd.cast(LayoutTable.class, "manufacture_select_list_item", desc);
				table.setUserObject(mAvailableDescriptionList.get(i));
				itemList.add(table);
			}

			Utils.sort(itemList);
			
			return itemList;
		}

		@Override
		public void updateListBox(List<Actor<?>> itemList) {
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
			
			currPreList = mAvailableDescriptionList;
		}
		
	};

	public Manufacture(int index, DepartmentManager department_manager) {
		super(index, department_manager);
		mNumEmployees = 5;
		mMaxStock = 10000;
		mMaxWorkPerDay = 2000;

		initSelectButton();
	}

	private void initSelectButton() {
		if(sSelectButton != null) return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		sSelectButton = cd.cast(PushButton.class, "static_text", R.string.label_select)
				.moveTo(140f, 110f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Manufacture manufacture = (Manufacture) listener.getParent();
						manufacture.bringUpProductSelectDialog();
					}
				})
				.pack().setWidth(60f);
	}
	
	@Override
	protected double work() {

		if(mChanged) {
			computeAvailDesc();
			mChanged = false;
		}
		
		// 생산라인이 확정되어 있는 경우
		if(mProduct != null) {
			
			// 제조
			
			TMP_STOCK_DEPARTMENT_LIST.clear();
			
			int production = 0;
			
			int min = getMinResult(mInputCurrStocks);
			if(min != 0) {
				
				int ouputQuantity = mDesc.outputQuantity;

				int n = mDesc.getNumInputs();
				for(int i=0; i<n; i++) {
					mInputCurrStocks[i] -= (min * mInputQuantities[i]);
				}
				
				// 기술에 따른 품질
				int qualityWeight = mDesc.inputQuality1 + mDesc.inputQuality2 + mDesc.inputQuality3;
				int techWeight = Product.MAX_QUALITY - qualityWeight;
				int maxTech = ProductManager.getInstance().getMaxTechByCode(mDesc.outputCode);
				int quality = techWeight * mDepartmentManager.getBuilding().getCorporation().getProductGroupByCode(mDesc.outputCode).tech / maxTech;
				
				// 각 원료에 따른 품질
				for(int i=0; i<n; i++) {
					quality += mInputQualities[i] * mDesc.getInputQuality(i) / 100;
				}
				
				double cost = 0;
				for(int i=0; i<n; i++) {
					float scale = (float) mDesc.getInputQuantity(i) / ouputQuantity;
					cost += mInputCosts[i] * scale;
				}
				
				double freight = 0;
				for(int i=0; i<n; i++) {
					float scale = (float) mDesc.getInputQuantity(i) / ouputQuantity;
					freight += mInputFreights[i] * scale;
				}
				
				production = min * ouputQuantity;
				
				if(mProduct.quality != quality) {
					mProduct.quality = mProduct.quality*mCurrStock + quality*production; 
					mProduct.quality /= (mCurrStock + production);
				}
				
				if(mProduct.cost != cost) {
					mProduct.cost = mProduct.cost*mCurrStock + cost*production; 
					mProduct.cost /= (mCurrStock + production);
				}
				
				if(mProduct.freight != freight) {
					mProduct.freight = mProduct.freight*mCurrStock + freight*production; 
					mProduct.freight /= (mCurrStock + production);
				}
				
				mCurrStock += production;
			}
			
			computeUtilization(production);
			
			// 원료 공급
			transferStock();
			
		// 생산라인이 아직 확정되지 않은 경우
		} else {
			searchSupplier();
		}

		return 0;
	}
	
	@Override
	protected List<StockDepartment> getSupplyDepartmentList() {
		List<StockDepartment> stockDepartmentList = TMP_STOCK_DEPARTMENT_LIST;
		stockDepartmentList.clear();
		
		List<Department> departmentList = getConnectedDepartmentList();
		int n = departmentList.size();
out:
		for(int i=0; i<n; i++) {
			Department department = departmentList.get(i);
			if(department instanceof Sales) continue;
			
			if(department instanceof StockDepartment) {
				StockDepartment stock = (StockDepartment) department;
				Product product = stock.getProduct();
				if(product == null) continue;
				
				int index = getInputIndex(stock.mProduct.desc.code);
				if(index != -1) {
					
					int m = stockDepartmentList.size();
					for(int j=0; j<m; j++) {
						StockDepartment tmpStock = stockDepartmentList.get(j);
						// 이미 같은 종류의 원료가 리스트에 포함된 경우 재고가 많은 것을 선택한다.
						if(tmpStock.mProduct.desc == stock.mProduct.desc 
								&& tmpStock.mCurrStock < stock.mCurrStock) {
							stockDepartmentList.remove(j);
							stockDepartmentList.add(stock);
							continue out;
						}
					}
					
					stockDepartmentList.add(stock);
				}
			}
		}
		
		return stockDepartmentList;
	}
	
	@Override
	protected void transferStock() {
		
		boolean empty = false;
		
		// 비어있는 원료가 있는지 체크한다.
		int n = mDesc.getNumInputs();
		for(int i=0; i<n; i++) {
			if(mInputCurrStocks[i] == 0) {
				empty = true;
			}
		}
		
		// 한 종류의 원료라도 비어있지 않다면 무시한다.
		if(!empty) return;
		
		for(int i=0; i<n; i++) {
			if(mInputCurrStocks[i] != 0) continue;
			
			List<StockDepartment> supplyDepartmentList = getSupplyDepartmentList();
			if(supplyDepartmentList.isEmpty()) return;
			
			int m = supplyDepartmentList.size();
			for(int j=0; j<m; j++) {
				StockDepartment stock = supplyDepartmentList.get(j);
				if(mDesc.getInputCode(i).equals(stock.mProduct.desc.code)) {
					if(stock.mCurrStock > 0) {
						mInputQualities[i] = stock.mProduct.quality;
						mInputCosts[i] = stock.mProduct.cost;
						mInputFreights[i] = stock.mProduct.freight;
						mInputMaxStocks[i] = computeInputMaxStock(stock.mProduct.desc);
						
						int transfer = Math.min(mInputMaxStocks[i], stock.mCurrStock);

						mInputCurrStocks[i] += transfer;
						stock.mCurrStock -= transfer;
					}
				}
			}
			
			
		}
		
	}
	
	@Override
	protected void searchSupplier() {
		computeAvailDesc();
	}

	@SuppressWarnings("incomplete-switch")
	private void computeAvailDesc() {
		TMP_STRING_LIST.clear();

		List<Department> departmentList = mConnectedDepartmentList;
		int n = departmentList.size();
		for(int i = 0; i < n; i++) {
			Department department = departmentList.get(i);
			switch(department.getDepartmentType()) {
				case MANUFACTURE:
					Manufacture manufacture = (Manufacture) mConnectedDepartmentList.get(i);
					if(manufacture.mProduct != null) {
						TMP_STRING_LIST.add(manufacture.mProduct.desc.code);
					}
					break;
				case PURCHASE:
					Purchase purchase = (Purchase) mConnectedDepartmentList.get(i);
					if(purchase.mProduct != null) {
						TMP_STRING_LIST.add(purchase.mProduct.desc.code);
					}
					break;
			}
		}

		mAvailableDescriptionList = ProductManager.getInstance()
				.getOutputPDescListByInputCodes(TMP_STRING_LIST);

		//if(mAvailableDescriptionList.contains(mDesc)) {
		//}

		if(mAvailableDescriptionList.size() <= 1) {
			mMore = false;
			if(!mAvailableDescriptionList.isEmpty()) {
				if(mDesc == null) {
					confirmProductionLine(mAvailableDescriptionList.get(0));
				} else {
					ManufacturingDescription mdesc = ProductManager.getInstance().getMDescByOutputCode(mAvailableDescriptionList.get(0).code);
					if(mDesc != mdesc) {
						confirmProductionLine(mAvailableDescriptionList.get(0));
					}
				}
			}
			clearChildren();
		} else {
			mMore = true;
			if(isSelected()) addChild(sSelectButton);
		}
	}

	@Override
	protected void onChanged() {
		mChanged = true;
	}

	private void bringUpProductSelectDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible()) return;
		}
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		CastingDirector cd = CastingDirector.getInstance();
		
		final ListBox list = cd.cast(ListBox.class, "updatable", mMDescriptionUpdater, HAlign.LEFT, 0f)
				.setItemPadding(2f, 5f, 5f, 1f).select(0);
		
		// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
		list.getScroll().startScrollFade();
		
		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "확인")
				.addEventListener(	new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						confirmProductionLine((ProductDescription) list.getSelectedItem()
								.getUserObject());

						closeDialog();
					}
				});

		PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "취소")
				.addEventListener(	new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog();
					}
				});
		
		LayoutTable headTable = new LayoutTable();

		headTable.col(0).cellPrefWidth(100f);
		headTable.col(1).cellPrefWidth(175f);

		headTable.addCell(new DLabel("제품", fontTexture));
		headTable.addCell(new DLabel("원료", fontTexture));

		mDialog = cd.cast(DialogWindow.class, "default")
				.setTitle(new DLabel("어떤 제품을 제조하시겠습니까?", fontTexture)).setModal(true)
				.addButton(button1).addButton(button2);

		LayoutTable table = mDialog.getContentTable();

		table.addCell(headTable);
		table.row();
		table.addCell(list).actorSize(275f, 150f);
		
		mDialog.pack().moveCenterTo(320f, 200f);

		UIManager.getInstance().addChild(mDialog);
		mDialog.open();

		// mDialog.debugAll();

		// Core.APP.debug(mDialog.toString());
	}

	/** 매개변수로 넘어온 {@link ProductDescription}을 제조하도록 생산라인을 확정한다. */
	private void confirmProductionLine(ProductDescription desc) {
		confirmProduct(new Product(desc), mDepartmentManager.getBuilding().getCorporation());
		mProduct.producer = mDepartmentManager.getBuilding().getCorporation();
		ProductManager.getInstance().addProduct(mProduct, 
				mDepartmentManager.getBuilding().getCorporation());
		mDesc = ProductManager.getInstance().getMDescByOutputCode(desc.code);
		int numInputs = mDesc.getNumInputs();
		mInputCurrStocks = new int[numInputs];
		mInputQuantities = new int[numInputs];
		mInputMaxStocks = new int[numInputs];
		
		mInputQualities = new int[numInputs];
		mInputCosts = new double[numInputs];
		mInputFreights = new double[numInputs];
		
		ProductDescription pdesc;
		switch(numInputs) {
			case 3:
				mInputQuantities[2] = mDesc.inputQuantity3;
				pdesc = ProductManager.getInstance().getPDescByCode(mDesc.inputCode3);
				mInputMaxStocks[2] = computeInputMaxStock(pdesc);
			case 2:
				mInputQuantities[1] = mDesc.inputQuantity2;
				pdesc = ProductManager.getInstance().getPDescByCode(mDesc.inputCode2);
				mInputMaxStocks[1] = computeInputMaxStock(pdesc);
			case 1:
				mInputQuantities[0] = mDesc.inputQuantity1;
				pdesc = ProductManager.getInstance().getPDescByCode(mDesc.inputCode1);
				mInputMaxStocks[0] = computeInputMaxStock(pdesc);
				break;
		}
	}
	
	private int computeInputMaxStock(ProductDescription desc) {
			int base = getBaseStock();
			base += ((mLevel-1)*base*LEVEL_UP_EXPANTION_SCALE);
			base /= desc.price;
			return base;
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
		if(!mMore) return;
		addChild(sSelectButton);
	}

	private int getMinResult(int[] inputs) {
		
		int min = Integer.MAX_VALUE;
		
		min = Math.min(mMaxWorkPerDay, mMaxStock - mCurrStock);
		min /= mDesc.outputQuantity;
		
		int n = mDesc.getNumInputs();
		for(int i = 0; i < n; i++) {
			min = Math.min(min, mInputCurrStocks[i] / mInputQuantities[i]);
		}
		
		return min;
	}

	private int getInputIndex(String input) {
		if(mDesc.inputCode1.equals(input)) return 0;
		if(mDesc.inputCode2.equals(input)) return 1;
		if(mDesc.inputCode3.equals(input)) return 2;
		return -1;
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
			
			float length;
			switch(mDesc.getNumInputs()) {
				case 3:
					length = 80 * mInputCurrStocks[2] / mInputMaxStocks[2];
					batch.draw(sDepartmentBrandBarRegion,
							DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
							DepartmentManager.sDepartmentRectangles[mIndex].top() + 140 / 2,
							length, sDepartmentBrandBarRegion.getRegionWidth(), false,
							false, true);
				case 2:
					length = 80 * mInputCurrStocks[1] / mInputMaxStocks[1];
					batch.draw(sDepartmentBrandBarRegion,
							DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
							DepartmentManager.sDepartmentRectangles[mIndex].top() + 125 / 2,
							length, sDepartmentBrandBarRegion.getRegionWidth(), false,
							false, true);
				case 1:
					length = 80 * mInputCurrStocks[0] / mInputMaxStocks[0];
					batch.draw(sDepartmentBrandBarRegion,
							DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
							DepartmentManager.sDepartmentRectangles[mIndex].top() + 110 / 2,
							length, sDepartmentBrandBarRegion.getRegionWidth(), false,
							false, true);
					break;
			}
			
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

	@Override
	public DepartmentType getDepartmentType() {
		return DepartmentType.MANUFACTURE;
	}
	
	@Override
	protected int getBaseStock() {
		return 100000;
	}
	
}
