package project.game.building;

import java.util.List;

import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.department.Department;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.stock.Livestock;
import project.game.building.department.stock.Process;
import project.game.building.department.stock.Sales;
import project.game.city.City;

public class Farm extends Building {

	/*package*/ Farm(BuildingDescription description, City city) {
		super(description, city);
	}

	public static Building newBuilding(BuildingDescription description, City city) {
		return new Farm(description, city);
	}

	@Override
	protected void setAvailableDepartmentList(List<DepartmentType> availableDepartmentList) {
		availableDepartmentList.add(DepartmentType.LIVESTOCK);
		availableDepartmentList.add(DepartmentType.PROCESS);
		availableDepartmentList.add(DepartmentType.SALES);
		availableDepartmentList.add(DepartmentType.EMPTY);
	}

	@Override
	public Department newDepartment(int index, DepartmentType type) {
		switch(type) {
			case LIVESTOCK:
				return new Livestock(index, getDepartmentManager());
			case PROCESS:
				return new Process(index, getDepartmentManager());
			case SALES:
				return new Sales(index, getDepartmentManager());
			case EMPTY:
				return null;
			default:
				throw new IllegalArgumentException(type.toString() + " of "
						+ DepartmentType.class.getSimpleName() + " is not allowed.");
		}
	}
	
	@Override
	public boolean isForSaleToCorporation() {
		return true;
	}

	/*
	 * @Override public BuildingType getBuildingType() { return
	 * BuildingType.FARM; }
	 */

}
