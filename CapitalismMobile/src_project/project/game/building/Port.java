package project.game.building;

import java.util.ArrayList;
import java.util.List;

import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.department.Department;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.DepartmentManager;
import project.game.building.department.stock.Sales;
import project.game.city.City;
import project.game.product.Product.DisplayProduct;

public class Port extends Building {
	
	// 진열대
	private List<DisplayProduct> mDisplayProductList = new ArrayList<DisplayProduct>(6);

	/*package*/ Port(BuildingDescription description, City city) {
		super(description, city);
	}

	public static Building newBuilding(BuildingDescription description, City city) {
		return new Port(description, city);
	}

	@Override
	public Department newDepartment(int index, DepartmentType type) {
		switch(type) {
			case SALES:
				return new PortSales(index, getDepartmentManager());
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
	public boolean canViewDepartmentTab() {
		return false;
	}
	
	@Override
	public boolean isForSaleToCorporation() {
		return true;
	}

	/*
	 * @Override public BuildingType getBuildingType() { return
	 * BuildingType.PORT; }
	 */

	private static class PortSales extends Sales {

		PortSales(int index, DepartmentManager department_manager) {
			super(index, department_manager);
		}

		@Override
		public double work() {

			mCurrStock += mMaxWorkPerDay * 3;
			if(mCurrStock > mMaxStock)
				mCurrStock = mMaxStock;

			return 0;
		}
		
		@Override
		protected int getBaseStock() {
			return 500000;
		}

	}

}
