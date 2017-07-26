package org.game.department;

import org.game.*;
import org.game.commodity.*;
import org.game.department.Department.*;

import android.graphics.*;
import android.graphics.Paint.*;

public class Laboratory extends Department {
	public int m_inverntory;
	public int m_maxinverntory;
	public int m_maxwork_perday;
	
	public static int m_number_generator=0;
	
	public int m_num_project;
	public int m_initial_quality;
	public int m_progress;
	
	public Commodity m_commodity;
	

	public Laboratory(int index, Bitmap bitmap,
			DepartmentManager department_manager) {
		super(index, bitmap, department_manager);
		// TODO Auto-generated constructor stub
		m_department_type = DepartmentTypes.LABORATORY;
		m_employee = 10;
		m_maxinverntory = 10000;
		m_maxwork_perday = 2000;
	}

	@Override
	public double Process() {
		// TODO Auto-generated method stub
		
		m_inverntory += m_maxwork_perday;
		if(m_inverntory >= m_maxinverntory)
		{
			Player.mQuality[m_commodity.m_index]++;
			m_inverntory = 0;
			m_maxinverntory += (Math.E * m_maxinverntory);
		}
		
		return 0;
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint p = new Paint();
    	p.setTextSize(12); 
    	
    	p.setColor(Color.BLUE);
    	canvas.drawRect(DepartmentManager.m_department_rect[m_index].left+10/2, 
    			DepartmentManager.m_department_rect[m_index].top+10/2, 
    			DepartmentManager.m_department_rect[m_index].right-4/2, 
    			DepartmentManager.m_department_rect[m_index].top+33/2, 
    			p);
    	
    	p.setColor(Color.BLACK);
    	canvas.drawText(m_department_type.GetString(), DepartmentManager.m_department_rect[m_index].left+10/2, 
    			DepartmentManager.m_department_rect[m_index].top+30/2, p);
    	if(m_commodity != null)
    	{
    		canvas.drawText(m_commodity.m_name, DepartmentManager.m_department_rect[m_index].left+10/2, 
	    			DepartmentManager.m_department_rect[m_index].top+55/2, p);
	    	canvas.drawText("현재기술 : "+Player.mQuality[m_commodity.m_index], DepartmentManager.m_department_rect[m_index].left+10/2, 
	    			DepartmentManager.m_department_rect[m_index].top+80/2, p);
	    	canvas.drawText("개발현황 : "+String.format("%.0f", (float)m_inverntory/m_maxinverntory*100)+"%", DepartmentManager.m_department_rect[m_index].left+10/2, 
	    			DepartmentManager.m_department_rect[m_index].top+105/2, p);
    	}
    	
    	if(m_index == m_department_manager.m_department_select)
    	{
    		p.setTextSize(15); 
    		p.setColor(Color.BLACK);
    		
	    	if(m_commodity == null)
	    		canvas.drawBitmap(m_commodity_empty_bitmap, 30/2, 50/2, null);
	    	else
	    		canvas.drawBitmap(m_commodity.m_commodity_bitmap, 30/2, 50/2, null);
	    	canvas.drawBitmap(m_department_content_bar_bitmap, 300/2, 50/2, null);
	    	p.setTextAlign(Align.CENTER);
	    	if(m_commodity != null)
	    		canvas.drawText(m_commodity.m_name, 300/2+163/2, 50/2+30/2, p);
	    	
	    	mResearchButton.Render(canvas);
	    	
	    	canvas.drawBitmap(m_division_bar_bitmap, 0, 320/2, null);
	    	
	    	canvas.drawBitmap(m_department_bitmap, 30/2, 350/2, null);
	    	canvas.drawBitmap(m_department_title_bar_bitmap, 350/2, 350/2, null);
	    	p.setTextAlign(Align.CENTER);
	    	canvas.drawText(m_department_type.GetString(), 350/2+143/2, 350/2+30/2, p);
	    	
	    	p.setTextAlign(Align.LEFT);
	    	canvas.drawText("부서 레벨 : " + m_level,	 350/2, 450/2, p);
	    	canvas.drawText("부서 직원수 : " + m_employee, 350/2, 450/2+40/2, p);
	    	
	    	for(int i=0; i<m_employee; i++)
	    		canvas.drawBitmap(m_employee_untrained_bitmap, 340/2+i*30/2, 500/2, null);
    	}
	}

}
