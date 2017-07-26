package org.game.department;

import org.framework.*;
import org.game.department.Department.*;

import android.graphics.*;

public class Advertise extends Department {
	public int m_current_progress;
	public int m_max_progress;
	public int m_advertise_type;

	public Advertise(int index, Bitmap bitmap, 
			DepartmentManager department_manager) {
		super(index, bitmap, department_manager);
		// TODO Auto-generated constructor stub
		m_department_type = DepartmentTypes.ADVERTISE;
		m_employee = 1;
		m_current_progress = 0;
		m_max_progress = 20;
	}

	@Override
	public double Process() {
		return 0;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint p = new Paint();
    	p.setTextSize(25); 
    	p.setColor(Color.BLACK);
    	canvas.drawText(m_department_type.GetString(), DepartmentManager.m_department_rect[m_index].left+10, 
    			DepartmentManager.m_department_rect[m_index].top+30, p);
    	//if(m_advertise_type != null)
    	{
	    	canvas.drawText("상품별 광고비", DepartmentManager.m_department_rect[m_index].left+10, 
	    			DepartmentManager.m_department_rect[m_index].top+55, p);
	    	canvas.drawText(" : "+m_current_progress*30+"(만원)", DepartmentManager.m_department_rect[m_index].left+10, 
	    			DepartmentManager.m_department_rect[m_index].top+80, p);
    	}
    	
    	if(m_index == m_department_manager.m_department_select)
    	{
    		canvas.drawBitmap(m_advertise_building_bitmap, 40, 50, null);
    		
    		canvas.drawBitmap(m_division_bar_bitmap, 0, 320, null);
    		
	    	canvas.drawBitmap(m_department_bitmap, 40, 350, null);
    	}

	}

}
