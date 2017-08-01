package project.game.building.department.stock;

import project.game.building.department.Department;
import project.game.building.department.DepartmentManager;
import project.game.building.department.Department.DepartmentType;
import project.game.product.*;

import android.graphics.*;
import android.graphics.Paint.Align;
import core.framework.graphics.batch.*;

public class Livestock extends Department {
	public int m_inverntory;
	public int m_maxinverntory;
	public int m_maxwork_perday;
	public int m_supply; // 공급 0 ~ 100%
	public int m_demand; // 수요 0 ~ 100%
	public int mUtilization; // 활용 0 ~ 100%

	public Product m_commodity;

	public Livestock(int index, DepartmentManager department_manager) {
		super(index, department_manager);
		mNumEmployees = 5;
		m_maxinverntory = 10000;
		m_maxwork_perday = 2000;
	}
	
	@Override
	protected double work() {
		return 0;
	}

	@Override
	public DepartmentType getDepartmentType() {
		return DepartmentType.LIVESTOCK;
	}

}
