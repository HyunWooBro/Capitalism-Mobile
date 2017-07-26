package org.game.department;

import org.game.commodity.*;
import org.game.department.Department.*;

import android.graphics.*;
import android.graphics.Paint.*;

public class Livestock extends Department {
	public int m_inverntory;
	public int m_maxinverntory;
	public int m_maxwork_perday;
	public int m_supply;						// 공급 0 ~ 100%
	public int m_demand;					// 수요 0 ~ 100%
	public int m_practical_use; 			// 활용도 0 ~ 100%
	
	public Commodity m_commodity;

	public Livestock(int index, Bitmap bitmap, 
			DepartmentManager department_manager) {
		super(index, bitmap, department_manager);
		// TODO Auto-generated constructor stub
		m_department_type = DepartmentTypes.LIVESTOCK;
		m_employee = 5;
		m_maxinverntory = 10000;
		m_maxwork_perday = 2000;
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
    	if(m_commodity != null)
    	{
	    	canvas.drawText(m_commodity.m_name, DepartmentManager.m_department_rect[m_index].left+10, 
	    			DepartmentManager.m_department_rect[m_index].top+55, p);
	    	canvas.drawText("재고 : "+m_inverntory/m_commodity.m_size, DepartmentManager.m_department_rect[m_index].left+10, 
	    			DepartmentManager.m_department_rect[m_index].top+80, p);
    	}
    	
    	if(m_index == m_department_manager.m_department_select)
    	{
    		p.setTextSize(30); 
    		
	    	if(m_commodity == null)
	    		canvas.drawBitmap(m_commodity_empty_bitmap, 30, 50, null);
	    	else
	    		canvas.drawBitmap(m_commodity.m_commodity_bitmap, 30, 50, null);
	    	canvas.drawBitmap(m_department_content_bar_bitmap, 300, 50, null);
	    	p.setTextAlign(Align.CENTER);
	    	if(m_commodity != null)
	    		canvas.drawText(m_commodity.m_name, 300+163, 50+30, p);
	    	
	    	canvas.drawBitmap(m_division_bar_bitmap, 0, 320, null);
	    	
	    	canvas.drawBitmap(m_department_bitmap, 30, 350, null);
	    	canvas.drawBitmap(m_department_title_bar_bitmap, 350, 350, null);
	    	p.setTextAlign(Align.CENTER);
	    	canvas.drawText(m_department_type.GetString(), 350+143, 350+30, p);
	    	
	    	p.setTextAlign(Align.LEFT);
	    	canvas.drawText("부서 레벨 : " + m_level,	 350, 450, p);
	    	canvas.drawText("부서 직원수 : " + m_employee, 350, 450+40, p);
    	}
	}

}
