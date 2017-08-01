package project.game.building;

import java.util.ArrayList;
import java.util.List;

import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.department.Department;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.Laboratory;
import project.game.city.City;
import project.game.product.Product.DisplayProduct;

public class RND extends Building {
	
	// 진열대
	private List<DisplayProduct> mDisplayProductList = new ArrayList<DisplayProduct>(6);

	/*package*/ RND(BuildingDescription description, City city) {
		super(description, city);
	}

	public static Building newBuilding(BuildingDescription description, City city) {
		return new RND(description, city);
	}

	@Override
	protected void setAvailableDepartmentList(List<DepartmentType> availableDepartmentList) {
		availableDepartmentList.add(DepartmentType.LABORATORY);
		availableDepartmentList.add(DepartmentType.EMPTY);
	}

	@Override
	public Department newDepartment(int index, DepartmentType type) {
		switch(type) {
			case LABORATORY:
				return new Laboratory(index, getDepartmentManager());
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

	/*
	 * @Override public BuildingType getBuildingType() { return
	 * BuildingType.RND; }
	 */

}
