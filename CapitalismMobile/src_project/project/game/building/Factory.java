package project.game.building;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.department.Department;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.DepartmentManager;
import project.game.building.department.stock.Manufacture;
import project.game.building.department.stock.Purchase;
import project.game.building.department.stock.Sales;
import project.game.building.department.stock.StockDepartment;
import project.game.city.City;
import project.game.product.Product;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductDescription;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.label.DLabel;

public class Factory extends Building {
	
	// 진열대
	private List<DisplayProduct> mDisplayProductList = new ArrayList<DisplayProduct>(6);

	/*package*/ Factory(BuildingDescription description, City city) {
		super(description, city);
	}

	public static Building newBuilding(BuildingDescription description, City city) {
		return new Factory(description, city);
	}

	@Override
	protected void setAvailableDepartmentList(List<DepartmentType> availableDepartmentList) {
		availableDepartmentList.add(DepartmentType.PURCHASE);
		availableDepartmentList.add(DepartmentType.MANUFACTURE);
		availableDepartmentList.add(DepartmentType.SALES);
		availableDepartmentList.add(DepartmentType.EMPTY);
	}

	@Override
	public Department newDepartment(int index, DepartmentType type) {
		switch(type) {
			case PURCHASE:
				return new FactoryPurchase(index, getDepartmentManager());
			case MANUFACTURE:
				return new Manufacture(index, getDepartmentManager());
			case SALES:
				return new FactorySales(index, getDepartmentManager());
			case EMPTY:
				return null;
			default:
				throw new IllegalArgumentException(type.toString() + " of "
						+ DepartmentType.class.getSimpleName() + " is not allowed.");
		}
	}
	
	@Override
	public List<DisplayProduct> getDisplayProductList() {
		return mDisplayProductList;
	}
	
	@Override
	public boolean isForSaleToCorporation() {
		return true;
	}

	private static class FactoryPurchase extends Purchase {

		FactoryPurchase(int index, DepartmentManager department_manager) {
			super(index, department_manager);
		}
		
		@Override
		protected ProductFilter getProductFilter(int index) {
			return new FactoryPurchaseFilter(index);
		}

		private static class FactoryPurchaseFilter extends ProductFilter {

			private int mIndex;

			public FactoryPurchaseFilter(int index) {
				mIndex = index;
			}

			@Override
			public boolean filter(ProductDescription description) {
				List<String> filterList = ProductManager.getInstance().getInputCodeList();
				if(mIndex != 0) {
					filterList = new ArrayList<String>(filterList);
					String filter = filterList.get(mIndex - 1);
					filterList.clear();
					filterList.add(filter);
				}
				return filterList.contains(description.code);
			}
		}

		@Override
		protected List<Actor<?>> getProductList() {
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture fontTexture = tm.getTexture(R.drawable.font);

			List<Actor<?>> nameList = new ArrayList<Actor<?>>();
			nameList.add(new DLabel("모든 제품", fontTexture));

			List<String> filterList = ProductManager.getInstance().getInputCodeList();
			int n = filterList.size();
			for(int i = 0; i < n; i++) {
				String name = ProductManager.getInstance().getPDescByCode(filterList.get(i)).name;
				nameList.add(new DLabel(name, fontTexture));
			}

			return nameList;
		}

	}

	private static class FactorySales extends Sales {

		/** 내부 판매 여부 */
		private boolean mInternalSales;

		FactorySales(int index, DepartmentManager department_manager) {
			super(index, department_manager);
		}
		
		public void setInternalSales(boolean internalSales) {
			mInternalSales = internalSales;
		}
		
	}

	/*
	 * @Override public BuildingType getBuildingType() { return
	 * BuildingType.FACTORY; }
	 */

}
