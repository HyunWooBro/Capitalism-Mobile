package project.game.building.department;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.framework.Utils.MessageDialogData;
import project.framework.Utils.OverallScoreBar;
import project.game.CPU;
import project.game.Time;
import project.game.building.Building;
import project.game.building.BuildingDelegate;
import project.game.building.BuildingDelegate.BuildingTabType;
import project.game.building.Factory;
import project.game.building.Port;
import project.game.building.Retail;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.stock.Sales;
import project.game.building.department.stock.StockDepartment;
import project.game.cell.CellManager;
import project.game.city.City;
import project.game.city.MarketOverseer;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.corporation.PublicCorporation;
import project.game.product.Product;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductGroup;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductData;
import project.game.product.ProductManager.ProductDescription;
import project.game.ui.UIManager;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.ShapeRenderer.ShapeType;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.MathUtils;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.button.Button.ButtonCostume;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.HAlign;

public class DepartmentManager extends Group<DepartmentManager> {

	public static final int NUM_LINKS = 16;
	public static final int NUM_DEPARTMENTS = 9;

	public static int sSelectedDepartmentIndex = -1; // 선택한 부서
	public static int sSelectedDisplayIndex = -1;

	private static int sNumDisplayItems;

	private static CLabel sNumberLabel;

	private DialogWindow mDialog;

	// 각 부서 위치
	public static Rectangle sDepartmentRectangles[] = new Rectangle[NUM_DEPARTMENTS];

	// 각 링크 위치
	private static Rectangle sLinkRectangles[] = new Rectangle[NUM_LINKS];

	//private static int[] sSalesIndices = new int[4];
	
	private static SLabel sDisplaytabLabel;
	private static SLabel sDepartmenttabLabel;
	
	private static SLabel sProductBrandLabel;
	private static SLabel sProductQualityLabel;
	private static SLabel sProductPriceLabel;
	private static SLabel sProductOverallLabel;
	
	private static SLabel sCurrentProductLabel;
	private static SLabel sCityAverageLabel;
	
	private static OverallScoreBar sOverScoreBar;

	private static PushButton sDoublePlusButton;
	private static PushButton sOnePlusButton;
	private static PushButton sOneMinusButton;
	private static PushButton sDoubleMinusButton;
	private static PushButton sConfirmButton;
	
	private static int sShiftIndex;
	private static PushButton sLeftShiftButton;
	private static PushButton sRightShiftButton;
	
	private static TextureRegion horizontalLink;
	private static TextureRegion verticallLink;
	private static TextureRegion diagonalLink1;
	private static TextureRegion diagonalLink2;
	private static TextureRegion diagonalLink3;

	private static TextureRegion sLockRegion;

	private static TextureRegion sDepartmentSelector;

	private static TextureRegion sDisplaySelector;

	public static ShapeRenderer sShapeRenderer;

	private static SLabel sMarketShareLabel;
	private static SLabel sTotalPurchasePriceLabel;
	private static SLabel sSalePriceLabel;
	private static SLabel sNewSalePriceLabel;

	static {
		
		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);
		Texture buttonTexture = tm.getTexture(R.drawable.button_default);

		ButtonCostume costume = new ButtonCostume();

		costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_plus_button"));
		sDoublePlusButton = new PricePushButton(costume).moveTo(150, 275).pack()
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						DepartmentManager manager = BuildingDelegate.getInstance()
								.getCurrentManager();
						
						List<DisplayProduct> displayProductList = manager.mBuilding.getDisplayProductList();
						DisplayProduct display = displayProductList.get(sSelectedDisplayIndex);
						double salesPrice = display.desc.price / 5;
						display.tempPrice += salesPrice;
						if(display.tempPrice > display.desc.price*8) {
							display.tempPrice = display.desc.price*8;
						}
					}
				});

		costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("one_plus_button"));
		sOnePlusButton = new PricePushButton(costume).moveTo(185, 275).pack()
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						DepartmentManager manager = BuildingDelegate.getInstance()
								.getCurrentManager();
						
						List<DisplayProduct> displayProductList = manager.mBuilding.getDisplayProductList();
						DisplayProduct display = displayProductList.get(sSelectedDisplayIndex);
						double salesPrice = display.desc.price / 20;
						display.tempPrice += salesPrice;
						if(display.tempPrice > display.desc.price*8) {
							display.tempPrice = display.desc.price*8;
						}
					}
				});

		costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("one_minus_button"));
		sOneMinusButton = new PricePushButton(costume).moveTo(220, 275).pack()
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						DepartmentManager manager = BuildingDelegate.getInstance()
								.getCurrentManager();
						
						List<DisplayProduct> displayProductList = manager.mBuilding.getDisplayProductList();
						DisplayProduct display = displayProductList.get(sSelectedDisplayIndex);
						double salesPrice = display.desc.price / 20;
						display.tempPrice -= salesPrice;
						if(display.tempPrice < display.desc.price / 20) {
							display.tempPrice = display.desc.price / 20;
						}
					}
				});

		costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_minus_button"));
		sDoubleMinusButton = new PricePushButton(costume).moveTo(255, 275).pack()
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						DepartmentManager manager = BuildingDelegate.getInstance()
								.getCurrentManager();
						
						List<DisplayProduct> displayProductList = manager.mBuilding.getDisplayProductList();
						DisplayProduct display = displayProductList.get(sSelectedDisplayIndex);
						double salesPrice = display.desc.price / 5;
						display.tempPrice -= salesPrice;
						if(display.tempPrice < display.desc.price / 20) {
							display.tempPrice = display.desc.price / 20;
						}
					}
				});
		

		SLabel buttonLabel = new SLabel(R.string.label_confirm, fontTexture);

		TextureRegion region = buttonTexture.getAsTextureRegion();
		NinePatch patch = new NinePatch(region, 10, 10, 11, 10);

		costume.up = Drawable.newDrawable(patch);
		sConfirmButton = new PricePushButton(costume)
				.moveTo(280, 265).pack().setWidth(70f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						DepartmentManager manager = BuildingDelegate.getInstance()
								.getCurrentManager();
						
						List<DisplayProduct> displayProductList = manager.mBuilding.getDisplayProductList();
						DisplayProduct display = displayProductList.get(sSelectedDisplayIndex);
						display.price = display.tempPrice;
					}
				});
		sConfirmButton.addCell(buttonLabel);
		
		sLeftShiftButton = cd.cast(PushButton.class, "dynamic_text", "<")
				.moveTo(20f, 60f)
				.pack()
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						sShiftIndex--;
						if(sShiftIndex < 0) sShiftIndex = 0;
					}
				});
		
		sRightShiftButton = cd.cast(PushButton.class, "dynamic_text", ">")
				.moveTo(580f, 60f)
				.pack()
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						DepartmentManager manager = BuildingDelegate.getInstance()
								.getCurrentManager();
						
						List<DisplayProduct> displayProductList = manager.mBuilding.getDisplayProductList();
						if(displayProductList == null) return;
						
						int n = displayProductList.size();
						if(n-sShiftIndex > 4) {
							sShiftIndex++;
						}
					}
				});
		
		sOverScoreBar = new OverallScoreBar();

		Resources resources = Core.APP.getResources();

		for(int i = 0; i < sLinkRectangles.length; i++)
			sLinkRectangles[i] = new Rectangle(resources.getIntArray(R.array.LINK_0 + i)[0],
					resources.getIntArray(R.array.LINK_0 + i)[1],
					resources.getIntArray(R.array.LINK_0 + i)[2]
							- resources.getIntArray(R.array.LINK_0 + i)[0],
					resources.getIntArray(R.array.LINK_0 + i)[3]
							- resources.getIntArray(R.array.LINK_0 + i)[1]);

		for(int i = 0; i < sDepartmentRectangles.length; i++)
			sDepartmentRectangles[i] = new Rectangle(
					resources.getIntArray(R.array.DEPARTMENT_0 + i)[0],
					resources.getIntArray(R.array.DEPARTMENT_0 + i)[1],
					resources.getIntArray(R.array.DEPARTMENT_0 + i)[2]
							- resources.getIntArray(R.array.DEPARTMENT_0 + i)[0],
					resources.getIntArray(R.array.DEPARTMENT_0 + i)[3]
							- resources.getIntArray(R.array.DEPARTMENT_0 + i)[1]);

		sNumberLabel = new CLabel("", R.array.label_array_outline_white_15, fontTexture);
		
		sDisplaytabLabel = new SLabel(R.string.label_display_tab, fontTexture);
		sDepartmenttabLabel = new SLabel(R.string.label_department_tab, fontTexture);
		
		sProductBrandLabel = new SLabel(R.string.label_product_brand, fontTexture);
		sProductQualityLabel = new SLabel(R.string.label_product_quality, fontTexture);
		sProductPriceLabel = new SLabel(R.string.label_product_price, fontTexture);
		sProductOverallLabel = new SLabel(R.string.label_product_overall, fontTexture);
		
		sCurrentProductLabel = new SLabel(R.string.label_current_product, fontTexture);
		sCityAverageLabel = new SLabel(R.string.label_city_average, fontTexture);

		horizontalLink = new TextureRegion(
				imageTexture.getTextureRegion("department_horizontal_link"));
		verticallLink = new TextureRegion(imageTexture.getTextureRegion("department_vertical_link"));
		diagonalLink1 = new TextureRegion(
				imageTexture.getTextureRegion("department_diagonal_link_1"));
		diagonalLink2 = new TextureRegion(
				imageTexture.getTextureRegion("department_diagonal_link_2"));
		diagonalLink3 = new TextureRegion(
				imageTexture.getTextureRegion("department_diagonal_link_3"));

		sLockRegion = tm.getTexture(R.drawable.lock).getAsTextureRegion();

		sShapeRenderer = new ShapeRenderer();
		sShapeRenderer.setBlendEnabled(true);

		sDepartmentSelector = new TextureRegion(
				imageTexture.getTextureRegion("department_selector"));
		sDisplaySelector = new TextureRegion(imageTexture.getTextureRegion("display_selector"));

		sMarketShareLabel = new SLabel(R.string.label_market_share, fontTexture);
		sTotalPurchasePriceLabel = new SLabel(R.string.label_total_purchase_price, fontTexture);
		sSalePriceLabel = new SLabel(R.string.label_sale_price, fontTexture);
		sNewSalePriceLabel = new SLabel(R.string.label_new_sale_price, fontTexture);

	}

	private Department mPreviousDepartment;

	private boolean mDisabled;

	private Department[] mDepartments = new Department[NUM_DEPARTMENTS];

	private int[] mLinks = new int[NUM_LINKS];

	private boolean[] mLinkLocks = new boolean[NUM_LINKS];

	private Building mBuilding;

	public DepartmentManager(Building building) {
		mBuilding = building;

		// 터치이벤트를 받기 위해 카메라의 크기와 같도록 사이즈를 조정
		sizeTo(Core.GRAPHICS.getVirtualWidth(), Core.GRAPHICS.getVirtualHeight());

		addEventListener(new GestureTouchListener() {
			@Override
			public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {

				switch(BuildingDelegate.getInstance().getBuildingTabType()) {
					case DEPARTMENT:
						// 부서탭인 경우

						if(mBuilding.getDescription().type.equals("Port")) return;

						int n;

						n = sLinkRectangles.length;
						for(int i = 0; i < n; i++) {
							if(sLinkRectangles[i].contains(x, y)) {
								Utils.m_click_sound.start();

								if(mLinkLocks[i]) {
									onLockedLinkSelected();
									return;
								}

								mLinks[i]++;

								if(i == 3 || i == 5 || i == 10 || i == 12) {
									if(mLinks[i] > 3) mLinks[i] = 0;
								} else if(mLinks[i] > 1) {
									mLinks[i] = 0;
								}

								onLinkChanged(i);
								return;
							}
						}

						n = sDepartmentRectangles.length;
						for(int i = 0; i < n; i++) {
							if(sDepartmentRectangles[i].contains(x, y)) {
								if(canModifyDepartment(i)) {
									bringUpDepartmentSelectDialog();
								}
								Utils.m_click_sound.start();
								selectDepartment(i);
								break;
							}
						}
						break;
					case DISPLAY:
						for(int i = 0; i < 4; i++) {
							if(new Rect(0 + 161 * i, 15, 157 + 161 * i, 145).contains((int) x,
									(int) y)) {
								Utils.m_click_sound.start();
								sSelectedDisplayIndex = i + sShiftIndex;
								break;
							}
						}
						break;
				}
			}

		});
	}

	private void onLockedLinkSelected() {
		MessageDialogData data = new MessageDialogData();
		data.title = "알림";
		data.content = "링크가 잠겨있어 변경할 수 없습니다.";
		Utils.showMessageDialog(getFloor().getStage(), "onLockedLinkSelected", data);
	}

	public void selectDepartment(int index) {
		sSelectedDepartmentIndex = index;
		if(mDepartments[index] == null) return;
		mDepartments[index].onSelected();
	}

	private void bringUpDepartmentSelectDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible()) return;
		}

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();

		List<DepartmentType> typeList = mBuilding.getAvailableDepartmentList();
		int n = typeList.size();
		for(int i = 0; i < n; i++) {
			DepartmentType type = typeList.get(i);
			LayoutTable table = cd.cast(LayoutTable.class, "department_list_item", type);
			itemList.add(table);
		}

		final ListBox list = cd.cast(ListBox.class, "default", itemList, HAlign.LEFT, 1f).select(0)
				.setPrefSize(270f, 250f);

		// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
		list.getScroll().startScrollFade();

		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "설치").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						int index = list.getSelectedIndex();

						DepartmentType departmentType = mBuilding.getAvailableDepartmentList().get(
								index);
						changeDepartment(departmentType);
						// Department의 onSelected()를 호출하기 위해
						selectDepartment(sSelectedDepartmentIndex);
						closeDialog();
					}
				});

		PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "취소").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog();
					}
				});

		mDialog = cd.cast(DialogWindow.class, "default")
				.setTitle(new DLabel("설치할 부서를 선택하세요.", fontTexture)).setModal(true)
				.addContent(list).addButton(button1).addButton(button2).pack()
				.moveCenterTo(320f, 200f);

		// sDialog.debugAll();

		UIManager.getInstance().addChild(mDialog);
		mDialog.open();

		// sDialog.debugTableContainer();
	}

	private boolean canModifyDepartment(int index) {
		if(sSelectedDepartmentIndex != index) {
			return false;
		}
		if(mDepartments[index] != null && !mDepartments[index].isModifiable()) {
			return false;
		}
		return true;
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

	private void onLinkChanged(int index) {
		switch(index) {
			case 0:
				onSingleLinkChanged(index, 0, 1);
				break;
			case 1:
				onSingleLinkChanged(index, 1, 2);
				break;
			case 2:
				onSingleLinkChanged(index, 0, 3);
				break;
			case 3:
				onDoubleLinkChanged(index, 0, 4, 1, 3);
				break;
			case 4:
				onSingleLinkChanged(index, 1, 4);
				break;
			case 5:
				onDoubleLinkChanged(index, 1, 5, 2, 4);
				break;
			case 6:
				onSingleLinkChanged(index, 2, 5);
				break;
			case 7:
				onSingleLinkChanged(index, 3, 4);
				break;
			case 8:
				onSingleLinkChanged(index, 4, 5);
				break;
			case 9:
				onSingleLinkChanged(index, 3, 6);
				break;
			case 10:
				onDoubleLinkChanged(index, 3, 7, 4, 6);
				break;
			case 11:
				onSingleLinkChanged(index, 4, 7);
				break;
			case 12:
				onDoubleLinkChanged(index, 4, 8, 5, 7);
				break;
			case 13:
				onSingleLinkChanged(index, 5, 8);
				break;
			case 14:
				onSingleLinkChanged(index, 6, 7);
				break;
			case 15:
				onSingleLinkChanged(index, 7, 8);
				break;
		}
	}

	private void onSingleLinkChanged(int linkIndex, int deptIndex1, int deptIndex2) {
		Department department1 = mDepartments[deptIndex1];
		Department department2 = mDepartments[deptIndex2];

		if(department1 == null || department2 == null) return;

		switch(mLinks[linkIndex]) {
			case 1:
				onLinkChanged(true, department1, department2);
				break;
			case 0:
				onLinkChanged(false, department1, department2);
				break;
		}
	}

	private void onDoubleLinkChanged(int linkIndex, int deptIndex1, int deptIndex2, int deptIndex3,
			int deptIndex4) {
		Department department1 = mDepartments[deptIndex1];
		Department department2 = mDepartments[deptIndex2];
		Department department3 = mDepartments[deptIndex3];
		Department department4 = mDepartments[deptIndex4];

		// department3, department4 부서의 경우에는 mLinks[linkIndex]가 1 또는 3일 경우에는 콜백
		// 메서드 호출에서 배제한다.
		// 왜냐하면 두 부서의 경우, mLinks[linkIndex]가 0에서 1로, 2에서 3로 변경되어도 링크의 변화가 없기
		// 때문이다.
		switch(mLinks[linkIndex]) {
			case 1:
				if(department1 != null && department2 != null) {
					onLinkChanged(true, department1, department2);
				}
				break;
			case 2:
				if(department3 != null && department4 != null) {
					onLinkChanged(true, department3, department4);
				}

				if(department1 != null && department2 != null) {
					onLinkChanged(false, department1, department2);
				}
				break;
			case 3:
				if(department1 != null && department2 != null) {
					onLinkChanged(true, department1, department2);
				}
				break;
			case 0:
				if(department1 != null && department2 != null) {
					onLinkChanged(false, department1, department2);
				}

				if(department3 != null && department4 != null) {
					onLinkChanged(false, department3, department4);
				}
				break;
		}
	}

	private void onLinkChanged(boolean add, Department department1, Department department2) {
		if(add) {
			department1.mConnectedDepartmentList.add(department2);
			department2.mConnectedDepartmentList.add(department1);
		} else {
			department1.mConnectedDepartmentList.remove(department2);
			department2.mConnectedDepartmentList.remove(department1);
		}
		department1.onLinkChanged();
		department2.onLinkChanged();
	}

	private void changeDepartment(DepartmentType departmentType) {
		// Department의 종류가 기존과 같으면 무시한다.
		if(mDepartments[sSelectedDepartmentIndex] != null
				&& mDepartments[sSelectedDepartmentIndex].getDepartmentType() == departmentType)
			return;

		FinancialData financialData = mBuilding.getCorporation().getFinancialData();

		double cost = departmentType.getCost();
		if(cost == 0 || financialData.cash >= cost) {
			financialData.cash -= cost;
			UIManager.getInstance().updateCash();
		} else {
			MessageDialogData data = new MessageDialogData();
			data.title = "알림";
			data.content = "부서 계획을 위한 현금이 부족합니다.";
			Utils.showMessageDialog(getFloor().getStage(), "insertDepartment", data);
			return;
		}

		mPreviousDepartment = mDepartments[sSelectedDepartmentIndex];
		removeChild(mDepartments[sSelectedDepartmentIndex]);

		Department department = mBuilding.newDepartment(sSelectedDepartmentIndex, departmentType);
		mDepartments[sSelectedDepartmentIndex] = department;
		if(department != null) {
			addChild(mDepartments[sSelectedDepartmentIndex]);
		}

		onDepartmentChanged();
	}

	private void onDepartmentChanged() {

		int deptindex = sSelectedDepartmentIndex;

		switch(deptindex) {
			case 0:
				onSingleLinkedDeaprtmentChanged(0, deptindex, 1);
				onSingleLinkedDeaprtmentChanged(2, deptindex, 3);
				onDoubleLinkedDeaprtmentChanged(3, 1, deptindex, 4);
				break;
			case 1:
				onSingleLinkedDeaprtmentChanged(0, deptindex, 0);
				onSingleLinkedDeaprtmentChanged(1, deptindex, 2);
				onDoubleLinkedDeaprtmentChanged(3, 2, deptindex, 3);
				onSingleLinkedDeaprtmentChanged(4, deptindex, 4);
				onDoubleLinkedDeaprtmentChanged(5, 1, deptindex, 5);
				break;
			case 2:
				onSingleLinkedDeaprtmentChanged(1, deptindex, 1);
				onDoubleLinkedDeaprtmentChanged(5, 2, deptindex, 4);
				onSingleLinkedDeaprtmentChanged(6, deptindex, 5);
				break;
			case 3:
				onSingleLinkedDeaprtmentChanged(2, deptindex, 0);
				onDoubleLinkedDeaprtmentChanged(3, 2, deptindex, 1);
				onSingleLinkedDeaprtmentChanged(7, deptindex, 4);
				onSingleLinkedDeaprtmentChanged(9, deptindex, 6);
				onDoubleLinkedDeaprtmentChanged(10, 1, deptindex, 7);
				break;
			case 4:
				onDoubleLinkedDeaprtmentChanged(3, 1, deptindex, 0);
				onSingleLinkedDeaprtmentChanged(4, deptindex, 1);
				onDoubleLinkedDeaprtmentChanged(5, 2, deptindex, 2);
				onSingleLinkedDeaprtmentChanged(7, deptindex, 3);
				onSingleLinkedDeaprtmentChanged(8, deptindex, 5);
				onDoubleLinkedDeaprtmentChanged(10, 2, deptindex, 6);
				onSingleLinkedDeaprtmentChanged(11, deptindex, 7);
				onDoubleLinkedDeaprtmentChanged(12, 1, deptindex, 8);
				break;
			case 5:
				onDoubleLinkedDeaprtmentChanged(5, 1, deptindex, 1);
				onSingleLinkedDeaprtmentChanged(6, deptindex, 2);
				onSingleLinkedDeaprtmentChanged(8, deptindex, 4);
				onDoubleLinkedDeaprtmentChanged(12, 2, deptindex, 7);
				onSingleLinkedDeaprtmentChanged(13, deptindex, 8);
				break;
			case 6:
				onSingleLinkedDeaprtmentChanged(9, deptindex, 3);
				onDoubleLinkedDeaprtmentChanged(10, 2, deptindex, 4);
				onSingleLinkedDeaprtmentChanged(14, deptindex, 7);
				break;
			case 7:
				onDoubleLinkedDeaprtmentChanged(10, 1, deptindex, 3);
				onSingleLinkedDeaprtmentChanged(11, deptindex, 4);
				onDoubleLinkedDeaprtmentChanged(12, 2, deptindex, 5);
				onSingleLinkedDeaprtmentChanged(14, deptindex, 6);
				onSingleLinkedDeaprtmentChanged(15, deptindex, 8);
				break;
			case 8:
				onDoubleLinkedDeaprtmentChanged(12, 1, deptindex, 4);
				onSingleLinkedDeaprtmentChanged(13, deptindex, 5);
				onSingleLinkedDeaprtmentChanged(15, deptindex, 7);
				break;
		}
	}

	private void onSingleLinkedDeaprtmentChanged(int linkIndex, int deptIndex1, int deptIndex2) {

		Department department1 = mDepartments[deptIndex1];
		Department department2 = mDepartments[deptIndex2];

		if(department1 != null) {
			if(mLinks[linkIndex] == 1 && department2 != null) {
				department1.mConnectedDepartmentList.add(department2);
				department2.mConnectedDepartmentList.add(department1);
				department1.onLinkedDepartmentChanged();
				department2.onLinkedDepartmentChanged();
			}
		} else { // department == null
			if(mLinks[linkIndex] == 1 && department2 != null) {
				department2.mConnectedDepartmentList.remove(mPreviousDepartment);
				department2.onLinkedDepartmentChanged();
			}
		}
	}

	private void onDoubleLinkedDeaprtmentChanged(int linkIndex, int minLinkValue, int deptIndex1,
			int deptIndex2) {

		Department department1 = mDepartments[deptIndex1];
		Department department2 = mDepartments[deptIndex2];

		if(department1 != null) {
			if((mLinks[linkIndex] == minLinkValue || mLinks[linkIndex] == 3) && department2 != null) {
				department1.mConnectedDepartmentList.add(department2);
				department2.mConnectedDepartmentList.add(department1);
				department1.onLinkedDepartmentChanged();
				department2.onLinkedDepartmentChanged();
			}
		} else { // department == null
			if((mLinks[linkIndex] == minLinkValue || mLinks[linkIndex] == 3) && department2 != null) {
				department2.mConnectedDepartmentList.remove(mPreviousDepartment);
				department2.onLinkedDepartmentChanged();
			}
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		ShapeRenderer renderer = DepartmentManager.sShapeRenderer;
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.begin(ShapeType.FILLED);
		renderer.setTransformMatrix(batch.peekTransformMatrix());

		switch(BuildingDelegate.getInstance().getBuildingTabType()) {
		/** 부서탭 */
			case DEPARTMENT:
				drawDepartmentTab(batch, parentAlpha);
				break;
			/** 요약탭 */
			case DISPLAY:
				drawDisplayTab(batch, parentAlpha);
				break;
		}

		super.draw(batch, parentAlpha);

		renderer.end();
	}

	private void drawDepartmentTab(Batch batch, float parentAlpha) {

		Resources resources = Core.APP.getResources();

		// 부서간 연결 상태
		int[] links = mLinks;
		int n = links.length;
		for(int i = 0; i < n; i++) {
			if(links[i] > 0) {

				TextureRegion region;

				if(i == 2 || i == 4 || i == 6 || i == 9 || i == 11 || i == 13) {
					region = verticallLink;
				} else if(i == 3 || i == 5 || i == 10 || i == 12) {
					switch(links[i]) {
						case 1:
							region = diagonalLink1;
							break;
						case 2:
							region = diagonalLink2;
							break;
						// case 3:
						default:
							region = diagonalLink3;
							break;
					}
				} else {
					region = horizontalLink;
				}

				batch.draw(region, resources.getIntArray(R.array.LINK_0 + i)[0],
						resources.getIntArray(R.array.LINK_0 + i)[1]);

				if(mLinkLocks[i]) {
					batch.setColor(Color4.YELLOW4);
					batch.draw(
							sLockRegion,
							resources.getIntArray(R.array.LINK_0 + i)[0],
							resources.getIntArray(R.array.LINK_0 + i)[1],
							resources.getIntArray(R.array.LINK_0 + i)[2]
									- resources.getIntArray(R.array.LINK_0 + i)[0],
							resources.getIntArray(R.array.LINK_0 + i)[3]
									- resources.getIntArray(R.array.LINK_0 + i)[1]);
					batch.setColor(Color4.WHITE4);
				}
			}
		}

		// 부서 선택 표시
		if(sSelectedDepartmentIndex > -1) {
			batch.draw(sDepartmentSelector, sDepartmentRectangles[sSelectedDepartmentIndex].left(),
					sDepartmentRectangles[sSelectedDepartmentIndex].top());
		}
		
		// 탭 레이블 출력
		sDisplaytabLabel.moveTo(33f, 330f);
		sDisplaytabLabel.setAlign(Align.CENTER);
		if(mBuilding.canViewDisplayTab()) {
			sDisplaytabLabel.setColor(Color4.WHITE4);
		} else {
			sDisplaytabLabel.setColor(Color4.GRAY4);
		}
		sDisplaytabLabel.draw(batch, parentAlpha);
		
		sDepartmenttabLabel.moveTo(109f, 330f);
		sDepartmenttabLabel.setAlign(Align.CENTER);
		sDepartmenttabLabel.setColor(Color4.GREEN4);
		sDepartmenttabLabel.draw(batch, parentAlpha);
	}

	private void drawDisplayTab(Batch batch, float parentAlpha) {
		
		List<DisplayProduct> displayProductList = mBuilding.getDisplayProductList();
		if(displayProductList == null) return;
		
		int numDisplayItems = 0;
		
		int n;
		
		n = displayProductList.size();
		if(n > 4) {
			sLeftShiftButton.setVisible(true);
			sRightShiftButton.setVisible(true);
		} else {
			sShiftIndex = 0;
			sLeftShiftButton.setVisible(false);
			sRightShiftButton.setVisible(false);
		}
		
		n = Math.min(n, sShiftIndex+4);
		for(int i = sShiftIndex; i < n; i++) {
			DisplayProduct display = displayProductList.get(i);
			ProductData data = ProductManager.getInstance().getProductDataByCode(display.desc.code);
			
			batch.draw(data.getImageRegion(), 35 / 2 + (161 * numDisplayItems), 20);
			
			float supplyLength;
			float demandLength;
			
			if(display.supplyDemand >= 0) {
				supplyLength = 120f;
				demandLength = 120f * display.supplyDemand;
			} else {
				supplyLength = -120f * display.supplyDemand;
				demandLength = 120f;
			}

			// 공급
			// float length = 80*mAvgUtilization/100;
			batch.draw(StockDepartment.sDepartmentSupplyBarRegion,
					(161 * numDisplayItems) + 4, 20 + (120f - supplyLength), 
					StockDepartment.sDepartmentSupplyBarRegion.getRegionWidth(), supplyLength);

			float width = StockDepartment.sDepartmentSupplyBarRegion.getRegionWidth();
			
			// 수요
			// float length = 80*mAvgUtilization/100;
			batch.draw(StockDepartment.sDepartmentDemandBarRegion,
					(161 * numDisplayItems) + 4 + width, 20 + (120f - demandLength), 
					StockDepartment.sDepartmentDemandBarRegion.getRegionWidth(), demandLength);
			
			sOverScoreBar.set(display)
				.setMaxHeight(120f)
				.pack()
				.moveTo((161 * numDisplayItems) + 142, 20);
			sOverScoreBar.draw(batch, parentAlpha);
			
			City city = mBuilding.getCity();
			
			if(display.desc.isConsumable()) {
				List<Corporation> corporationList = CorporationManager.getInstance().getCorporationList();
				int m = corporationList.size();
				for(int j=0; j<m; j++) {
					Corporation corporation = corporationList.get(j);
					if(corporation instanceof PublicCorporation) continue;
					ProductGroup group = corporation.getProductGroupByCode(display.desc.code);
					
					double avg = group.marketShareList.get(city.index).getAvg();
					
					float marketShareLength = (float) (120f * avg);
					
					batch.draw(StockDepartment.sDepartmentUtilizationBarRegion,
							(161 * numDisplayItems) + 142 + width, 20 + (120f - marketShareLength), 
							StockDepartment.sDepartmentUtilizationBarRegion.getRegionWidth(), marketShareLength);
				}
			}
			
			
			numDisplayItems++;

			if(sSelectedDisplayIndex == i) {

				batch.draw(Department.sDepartmentContentBarRegion, 50 / 2, 320 / 2);

				batch.setColor(Color4.BLACK);
				DLabel label = data.getNameLabel();
				label.south();
				label.moveTo(50 / 2 + 163 / 2, 330 / 2 + 25 / 2);
				label.draw(batch, parentAlpha);
				batch.setColor(Color4.WHITE);

				// 시장 점유율
				sMarketShareLabel.moveTo(40, 200);
				sMarketShareLabel.draw(batch, parentAlpha);

				// 총 구입 가격
				if(!(mBuilding instanceof Port)) {
					sTotalPurchasePriceLabel.moveTo(150, 200);
					sTotalPurchasePriceLabel.draw(batch, parentAlpha);
					
					double totalPurchasePrice = display.getTotalCost();
					
					sNumberLabel.setText(Utils.toPrice(totalPurchasePrice));
					sNumberLabel.moveTo(530 / 2, 400 / 2);
					sNumberLabel.draw(batch, parentAlpha);
				}

				// 판매 가격
				sSalePriceLabel.moveTo(150, 220);
				sSalePriceLabel.draw(batch, parentAlpha);
				
				double salesPrice = display.desc.price
						+ display.desc.price * 1.2;

				sNumberLabel.setText(Utils.toPrice(display.price));
				sNumberLabel.moveTo(530 / 2, 440 / 2);
				sNumberLabel.draw(batch, parentAlpha);

				// 새로운 판매 가격
				if(!(mBuilding instanceof Port)) {
					sNewSalePriceLabel.moveTo(150, 250);
					sNewSalePriceLabel.draw(batch, parentAlpha);
					
					double newPrice = display.price;

					sNumberLabel.setText(Utils.toPrice(display.tempPrice));
					sNumberLabel.moveTo(530 / 2, 500 / 2);
					sNumberLabel.draw(batch, parentAlpha);
				}

				drawMarketShareGraph(batch, display);
				
				
				sCurrentProductLabel.moveTo(350f, 200f);
				sCurrentProductLabel.draw(batch, parentAlpha);
				
				if(display.desc.isConsumable()) {
					sProductBrandLabel.moveTo(350f, 220f);
					sProductBrandLabel.draw(batch, parentAlpha);
					
					sNumberLabel.setText("" + display.getBrand().getTotalBrand());
					sNumberLabel.moveTo(400f, 220f);
					sNumberLabel.draw(batch, parentAlpha);
				}
				
				sProductQualityLabel.moveTo(350f, 240f);
				sProductQualityLabel.draw(batch, parentAlpha);
				
				sNumberLabel.setText("" + display.quality);
				sNumberLabel.moveTo(400f, 240f);
				sNumberLabel.draw(batch, parentAlpha);
				
				sProductPriceLabel.moveTo(350f, 260f);
				sProductPriceLabel.draw(batch, parentAlpha);
				
				sNumberLabel.setText(Utils.toPrice(display.price));
				sNumberLabel.moveTo(400f, 260f);
				sNumberLabel.draw(batch, parentAlpha);
				
				sProductOverallLabel.moveTo(350f, 280f);
				sProductOverallLabel.draw(batch, parentAlpha);
				
				sNumberLabel.setText(Utils.toString(ProductManager.getInstance().getOverallScore(display)));
				sNumberLabel.moveTo(400f, 280f);
				sNumberLabel.draw(batch, parentAlpha);
				
				sOverScoreBar.set(display)
					.setMaxHeight(90f)
					.pack()
					.moveTo(470f, 200f);
				sOverScoreBar.draw(batch, parentAlpha);
				
				if(!display.desc.isConsumable()) continue;
				
				sCityAverageLabel.moveTo(500f, 200f);
				sCityAverageLabel.draw(batch, parentAlpha);
				
				sProductBrandLabel.moveTo(500f, 220f);
				sProductBrandLabel.draw(batch, parentAlpha);
				
				MarketOverseer overseer = city.getMarketOverseerByCode(display.desc.code);
				
				sNumberLabel.setText("" + overseer.getAvgBrand());
				sNumberLabel.moveTo(550f, 220f);
				sNumberLabel.draw(batch, parentAlpha);
				
				sProductQualityLabel.moveTo(500f, 240f);
				sProductQualityLabel.draw(batch, parentAlpha);
				
				sNumberLabel.setText("" + overseer.getAvgQuality());
				sNumberLabel.moveTo(550f, 240f);
				sNumberLabel.draw(batch, parentAlpha);
				
				sProductPriceLabel.moveTo(500f, 260f);
				sProductPriceLabel.draw(batch, parentAlpha);
				
				sNumberLabel.setText(Utils.toPrice(overseer.getAvgPrice()));
				sNumberLabel.moveTo(550f, 260f);
				sNumberLabel.draw(batch, parentAlpha);
				
				sProductOverallLabel.moveTo(500f, 280f);
				sProductOverallLabel.draw(batch, parentAlpha);
				
				sNumberLabel.setText(Utils.toString(ProductManager.getInstance().getOverallScore(display.desc, overseer.getAvgBrand(), overseer.getAvgQuality(), overseer.getAvgPrice())));
				sNumberLabel.moveTo(550f, 280);
				sNumberLabel.draw(batch, parentAlpha);
				
				sOverScoreBar.set(display.desc, overseer.getAvgBrand(), overseer.getAvgQuality(), overseer.getAvgPrice())
					.setMaxHeight(90f)
					.pack()
					.moveTo(620f, 200f);
				sOverScoreBar.draw(batch, parentAlpha);
				
			}
		}
		
		sNumDisplayItems = numDisplayItems;

		batch.draw(sDisplaySelector, 0 + (161 * (sSelectedDisplayIndex-sShiftIndex)), 15);
		
		// 탭 레이블 출력
		sDisplaytabLabel.moveTo(33f, 330f);
		sDisplaytabLabel.setAlign(Align.CENTER);
		sDisplaytabLabel.setColor(Color4.GREEN4);
		sDisplaytabLabel.draw(batch, parentAlpha);
		
		sDepartmenttabLabel.moveTo(109f, 330f);
		sDepartmenttabLabel.setAlign(Align.CENTER);
		if(mBuilding.canViewDepartmentTab()) {
			sDepartmenttabLabel.setColor(Color4.WHITE4);
		} else {
			sDepartmenttabLabel.setColor(Color4.GRAY4);
		}
		sDepartmenttabLabel.draw(batch, parentAlpha);
	}

	private void drawMarketShareGraph(Batch batch, Product product) {
		ShapeRenderer renderer = sShapeRenderer;

		renderer.setColor(Color.argb(255, 40, 40, 140));
		renderer.drawCircle(155 / 2, 525 / 2, 80 / 2);
		
		City city = mBuilding.getCity();
		
		float startAngle = -90f;
		
		List<Corporation> corporationList = CorporationManager.getInstance().getCorporationList();
		int n = corporationList.size();
		for(int i=0; i<n; i++) {
			Corporation corporation = corporationList.get(i);
			if(corporation instanceof PublicCorporation) continue;
			ProductGroup group = corporation.getProductGroupByCode(product.desc.code);
			
			double avg = group.marketShareList.get(city.index).getAvg();
			avg *= 100f;
			
			float sweepAngle = (float) (360f * avg / 100f);
			if(sweepAngle == 0f) continue;
			
			renderer.setColor(Color4.RED);
			renderer.drawArc(150 / 2, 520 / 2, (80 - 1) / 2, startAngle, sweepAngle);
			
			startAngle += sweepAngle;
		}
		
		// 기업의 시장점유율 합이 360도를 채우지 못하면 나머지 부분은 지역 상인들의 
		// 시장점유율로 출력한다.
		if(startAngle < 270f) {
			renderer.setColor(Color4.WHITE);
			float sweepAngle = 270f - startAngle;
			renderer.drawArc(150 / 2, 520 / 2, (80 - 1) / 2, startAngle, sweepAngle);
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	public double process() {
		double result = 0;
		double value = 0;

		double player_cost = 0;
		double player_earning = 0;
		double player_net_profit = 0;
		int maximum_day = 0;

		FinancialData financialData = mBuilding.getCorporation().getFinancialData();

		int index = Time.getInstance().getMonthlyArrayIndex();

		Department[] departments = mDepartments;
		int n = departments.length;
		// 각 Department에 우선순위를 부여하여 queue에 넣고 처리할까?

		// 구매부 또는 사육부를 처리한다.
		for(int i=0; i<n; i++) {
			if(departments[i] != null) {
				switch(departments[i].getDepartmentType()) {
					case LIVESTOCK:
						departments[i].work();
						break;
					case PURCHASE:
						value = departments[i].work();
						result -= value;
						financialData.monthlySalesCostArray[index] += value;
						financialData.accumulatedSalesCost += value;
						break;
				}
			}
		}

		// 제조부 또는 가공부를 처리한다.
		for(int i = 0; i < n; i++) {
			if(departments[i] != null) {
				switch(departments[i].getDepartmentType()) {
					case MANUFACTURE:
					case PROCESS:
						departments[i].work();
						break;
				}
			}
		}

		// 나머지 부서를 처리한다.
		for(int i = 0; i < n; i++) {
			if(departments[i] != null) {
				switch(departments[i].getDepartmentType()) {
					case ADVERTISE:
					case LABORATORY:
						departments[i].work();
						break;
					case SALES:
						value = departments[i].work();
						result += value;
						financialData.monthlyOperatingRevenueArray[index] += value;
						financialData.accumulatedOperatingRevenue += value;
						break;
				}
			}
		}
		
		updateDisplayProducts();

		// 일단 항구일 경우 무시
		if(mBuilding instanceof Port) {
			return player_net_profit;
		}

		// 현재 달의 최대 일 수
		maximum_day = Time.getInstance().getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);

		// 유지비 계산
		value = mBuilding.getDescription().maintenance / maximum_day;
		player_cost += value;
		financialData.monthlyOperatingOverheadArray[index] += value;
		financialData.accumulatedOperatingOverhead += value;
		
		// Core.APP.debug("유지비 : " + mBuilding.getDescription().maintenance/maximum_day);
		
		// 임금 계산
		int num_employee = getTotalEmployees();
		value = (num_employee * CPU.getInstance().mDailySalariesExpenses);
		player_cost += value;
		financialData.monthlySalariesExpensesArray[index] += value;
		financialData.accumulatedSalariesExpenses += value;
		
		// 하루 순이익
		player_net_profit = player_earning - player_cost + result;

		// Core.APP.debug("하루 순이익 : " + player_net_profit);

		// PlayerCorporation.mMoney += player_net_profit;

		mBuilding.mMonthlyNetprofit[index] += player_net_profit;

		// PlayerCorporation.m_monthly_netprofit[index] += player_net_profit;

		mBuilding.mAnnualNetprofit = 0;
		for(int i = 0; i < 12; i++) {
			mBuilding.mAnnualNetprofit += mBuilding.mMonthlyNetprofit[i];
		}

		// 사실 이것도 미리 계산할 필요는 없다.
		mBuilding.calculateMaxGraphPoint();

		if(player_net_profit > 0) {
			if(CPU.m_max_daily_positive_profit < mBuilding.mMonthlyNetprofit[index]) {
				CPU.m_max_daily_positive_profit = mBuilding.mMonthlyNetprofit[index];
			}
		} else {
			if(CPU.m_max_daily_negative_profit > mBuilding.mMonthlyNetprofit[index]) {
				CPU.m_max_daily_negative_profit = mBuilding.mMonthlyNetprofit[index];
			}
		}

		return player_net_profit;
	}
	
	private void updateDisplayProducts() {
		
		List<DisplayProduct> displayProductList = mBuilding.getDisplayProductList();
		if(displayProductList == null) return;
		
		int m;
		
		// 시작하기에 앞서 DisplayProduct의 update를 모두 false로 설정한다.
		m = displayProductList.size();
		for(int i=0; i<m; i++) {
			DisplayProduct display = displayProductList.get(i);
			display.update = false;
			display.quality = 0;
			display.cost = 0;
			display.freight = 0;
			display.tempStock = 0;
			display.salesList.clear();
		}
		
		City city = mBuilding.getCity();
		
		Department[] departments = mDepartments;
		int n = departments.length;
		for(int i = 0; i < n; i++) {
			if(departments[i] != null
					&& departments[i].getDepartmentType() == DepartmentType.SALES
					&& ((Sales) departments[i]).getProduct() != null) {

				Sales sales = (Sales) departments[i];
				Product product = sales.getProduct();
				ProductDescription desc = product.desc;
				Corporation producer = product.producer;
				
				DisplayProduct display = mBuilding.getDisplayProduct(desc, producer);
				if(display == null) {
					display = new DisplayProduct(product, city);
					// 일단 진열제품의 가격을 다음과 같이 세팅
					calculateDisplayPrice(display, desc);
					displayProductList.add(display);
				} else {
					// 재고를 곱해서 누적하는데 마지막에 누적된 재고로 나누어 평균을 구하게 된다.
					if(sales.mCurrStock != 0) {
						display.quality += product.quality * sales.mCurrStock;
						display.cost += product.cost * sales.mCurrStock;
						display.freight += product.freight * sales.mCurrStock;
						display.tempStock += sales.mCurrStock;
					}
				}
				
				display.update = true;
				display.salesList.add(sales);
				sales.mDisplay = display;
			}
		}
		
		// 여전히 update가 false인 DisplayProduct를 제거한다. 관련 부서가 
		// 존재하지 않기 때문이다.
		m = displayProductList.size();
		for(int i=m-1; i>-1; i--) {
			DisplayProduct display = displayProductList.get(i);
			if(!display.update) {
				displayProductList.remove(i);
			} else {
				
				// 진열제품의 누적된 재고가 없다는 것은 관련 판매부의 재고가 없다는 것이므로 
				// 판매부의 개수로 나누어 평균을 구한다.
				if(display.tempStock == 0) {
					display.quality = 0;
					for(int j=0; j<display.salesList.size(); j++) {
						display.quality += display.salesList.get(j).getProduct().quality;
					}
					display.quality /= display.salesList.size();
					
					display.cost = 0;
					for(int j=0; j<display.salesList.size(); j++) {
						display.cost += display.salesList.get(j).getProduct().cost;
					}
					display.cost /= display.salesList.size();
					
					display.freight = 0;
					for(int j=0; j<display.salesList.size(); j++) {
						display.freight += display.salesList.get(j).getProduct().freight;
					}
					display.freight /= display.salesList.size();
				// 진열제품의 누적된 재고로 나누어 평균을 구한다.
				} else {
					display.quality /= display.tempStock;
					
					display.cost /= display.tempStock;
					
					display.freight /= display.tempStock;
				}
				
			}
		}
		
	}
	
	/** 
	 * 소매점에서의 최종 제품의 가격은 기본적으로 기본 가격으로 설정되며 
	 * 비용(구입+운송)보다 20%이상이 아닐 경우 비용의 20%높은 가격으로 
	 * 책정한다.
	 */
	private void calculateDisplayPrice(DisplayProduct display, ProductDescription description) {
		
		if(mBuilding instanceof Port) {
			// 확률은 가우시안을 사용한다. 
			// 30% ~ 50% 사이로 뽑아내며 중간값인 40%에 몰려있다.
			double price = MathUtils.randomGaussian() + 3;
			price = MathUtils.clamp(price, 0, 6);
			price = ((int) (20/6*price + 30))/5*5;
			display.price = price/100 * display.desc.price;
		} else if(mBuilding instanceof Retail) {
			display.price = description.price;
			// 현재 가격이 원료의 120%보다 작다면 120%으로 다시 설정해야 한다.
		} else if(mBuilding instanceof Factory) {
			display.price = description.price * 0.75;
			// 현재 가격이 원료의 120%보다 작다면 120%으로 다시 설정해야 한다.
		}
		
		display.tempPrice = display.price;
	}

	/** 모든 부서의 {@link Department#mWorkDone}을 false로 리셋한다. */
	public void resetDaily() {
		Department[] departments = mDepartments;
		int n = departments.length;
		for(int i = 0; i < n; i++) {
			if(departments[i] != null) {
				departments[i].resetDaily();
			}
		}
	}
	
	public void resetMonthly() {
		Department[] departments = mDepartments;
		int n = departments.length;
		for(int i = 0; i < n; i++) {
			if(departments[i] != null) {
				departments[i].resetMonthly();
			}
		}
	}

	public void setTapType(BuildingTabType tabType) {
		clearChildren();
		switch(tabType) {
			case DEPARTMENT:
				// 부서탭이 접근불가능일 경우 진열탭으로 수정한다.
				// 두 탭이 동시에 접근불가능일 경우에는 이 메서드를 호출하지 
				// 않는 것을 전제로 한다. 그렇지 않으면 스택오버플로우가 발생
				if(!mBuilding.canViewDepartmentTab()) {
					setTapType(BuildingTabType.DISPLAY);
					break;
				}
				Department[] departments = mDepartments;
				int n = departments.length;
				for(int i = 0; i < n; i++) {
					if(departments[i] != null) {
						addChild(departments[i]);
					}
				}
				break;
			case DISPLAY:
				// 진열탭이 접근불가능일 경우 부서탭으로 수정한다.
				// 두 탭이 동시에 접근불가능일 경우에는 이 메서드를 호출하지 
				// 않는 것을 전제로 한다. 그렇지 않으면 스택오버플로우가 발생
				if(!mBuilding.canViewDisplayTab()) {
					setTapType(BuildingTabType.DEPARTMENT);
					break;
				}
				addChild(sDoublePlusButton);
				addChild(sOnePlusButton);
				addChild(sOneMinusButton);
				addChild(sDoubleMinusButton);
				addChild(sConfirmButton);
				addChild(sRightShiftButton);
				addChild(sLeftShiftButton);
				break;
		}
	}

	public Building getBuilding() {
		return mBuilding;
	}

	public Department[] getDepartments() {
		return mDepartments;
	}

	public void lockLink(int index) {
		mLinkLocks[index] = true;
	}

	public void unlockLink(int index) {
		mLinkLocks[index] = false;
	}

	public boolean isLinkLocked(int index) {
		return mLinkLocks[index];
	}

	public void lockAllLinks() {
		for(int i = 0; i < NUM_LINKS; i++) {
			mLinkLocks[i] = true;
		}
	}

	public void unlockAllLinks() {
		for(int i = 0; i < NUM_LINKS; i++) {
			mLinkLocks[i] = false;
		}
	}

	public int getLinkIndex(Department department1, Department department2) {
		return getLinkIndex(department1.mIndex, department2.mIndex);
	}

	public int getLinkIndex(int departmentIndex1, int departmentIndex2) {
		int index1 = (departmentIndex1 > departmentIndex2) ? departmentIndex2 : departmentIndex1;
		int index2 = (departmentIndex1 > departmentIndex2) ? departmentIndex1 : departmentIndex2;

		switch(index1) {
			case 0:
				switch(index2) {
					case 1:		return 0;
					case 3:		return 2;
					case 4:	return 3;
				}
				return -1;
			case 1:
				switch(index2) {
					case 2:		return 1;
					case 3:		return 3;
					case 4:	return 4;
					case 5:		return 5;
				}
				return -1;
			case 2:
				switch(index2) {
					case 4:	return 5;
					case 5:		return 6;
				}
				return -1;
			case 3:
				switch(index2) {
					case 4:	return 7;
					case 6:		return 9;
					case 7:		return 10;
				}
				return -1;
			case 4:
				switch(index2) {
					case 5:		return 8;
					case 6:		return 10;
					case 7:		return 11;
					case 8:		return 12;
				}
				return -1;
			case 5:
				switch(index2) {
					case 7:		return 12;
					case 8:		return 13;
				}
				return -1;
			case 6:
				switch(index2) {
					case 7:		return 14;
				}
				return -1;
			case 7:
				switch(index2) {
					case 8:		return 15;
				}
				return -1;
		}

		return -1;
	}

	/** 모든 부서의 사원의 수를 얻는다. */
	public int getTotalEmployees() {
		int total = 0;
		Department[] departments = mDepartments;
		int n = departments.length;
		for(int i = 0; i < n; i++) {
			if(departments[i] != null) {
				total += departments[i].mNumEmployees;
			}
		}
		return total;
	}

	public static void reset() {
		sSelectedDepartmentIndex = -1;
		sSelectedDisplayIndex = -1;
		sShiftIndex = 0;
	}

	private static class PricePushButton extends PushButton {
	
		public PricePushButton(ButtonCostume costume) {
			super(costume);
		}
	
		private boolean isReady() {
			// 진열탭이고
			if(BuildingDelegate.getInstance().getBuildingTabType() == BuildingTabType.DISPLAY
					// 선택된 인덱스가 -1가 아니고
					&& sSelectedDisplayIndex != -1 
					&& sSelectedDisplayIndex <= sNumDisplayItems - 1
					// 선택된 건물이 유저의 것이라면
					&& CorporationManager.getInstance().getPlayerCorporation().getBuildingList().contains(CellManager.getInstance().getCellSelector().getSelectedBuilding())) {
				return true;
			}
			return false;
		}
	
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if(isReady()) super.draw(batch, parentAlpha);
		}
	
		@Override
		public Actor<?> contact(float x, float y) {
			if(!isReady()) return null;
			return super.contact(x, y);
		}
	}
	
}
