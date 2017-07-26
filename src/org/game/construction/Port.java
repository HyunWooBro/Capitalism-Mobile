package org.game.construction;

import org.game.construction.Construction.*;
import org.game.department.*;

import android.graphics.*;

public class Port extends Construction {

	public Port() {
		super(ConstructionTypes.PORT);
		// TODO Auto-generated constructor stub
		m_size = 3;
	}
	
	public void Reset()
	{
		for(int i=0; i<DepartmentManager.DEPARTMENT_COUNT; i++)
			if(m_department_manager.m_departments[i] != null)
				((Sales)m_department_manager.m_departments[i]).m_inverntory =
				((Sales)m_department_manager.m_departments[i]).m_maxinverntory;
	}

}
