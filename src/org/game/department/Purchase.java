package org.game.department;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.cell.*;
import org.game.commodity.*;
import org.game.construction.*;
import org.game.construction.Construction.*;
import org.game.department.Department.*;

import android.R.color;
import android.graphics.*;
import android.graphics.Paint.*;
import android.util.*;

public class Purchase extends Department {
	public int m_inverntory;
	public int m_maxinverntory;
	public int m_maxwork_perday;
	public int m_supply;						// 공급 0 ~ 100%
	public int m_demand;					// 수요 0 ~ 100%
	public int m_practical_use[]; 		// 활용도 0 ~ 100%
	public Commodity m_commodity;
	public Construction m_linked_construction;
	public int m_linked_department_index;
	public double m_distance_from_linked_construction;		// 단위는 km
	
	private int m_day;

	public Purchase(int index, Bitmap bitmap, 
			DepartmentManager department_manager) {
		super(index, bitmap, department_manager);
		// TODO Auto-generated constructor stub
		m_department_type = DepartmentTypes.PURCHASE;
		m_employee = 5;
		m_maxinverntory = 10000;
		m_maxwork_perday = 2000;

		m_practical_use = new int[30];
		m_day = -1;
	}

	@Override
	public double Process() {
		// TODO Auto-generated method stub
		
		m_day++;
		
		if(m_is_done == true)
			return 0;
		
		long player_cost = 0;
		long player_earning = 0;
		long player_net_profit = 0;
		int maximum_day = 0;
		
		int actual_work;
		int transfer_inventory;
		
		int temp;
		
		Sales away_sales = null;						// 연결된 다른 곳의 판매부서(물품을 구입하는 부서)
		Sales here_sales = null;						// 연결된 이 곳의 판매부서
		Manufacture manufacture = null;			// 연결된 제조부서
		
		for(int i=0; i<m_department_list.size(); i++)
		{
			if(m_department_list.get(i).m_department_type == DepartmentTypes.SALES)
				here_sales = (Sales) m_department_list.get(i);
		}
		
		//if(m_department_manager.m_construction.m_constructiontype == ConstructionTypes.RETAIL)
		{
			if(m_linked_construction != null)
				away_sales = (Sales)m_linked_construction.m_department_manager.m_departments[m_linked_department_index];
		}
		
		for(int i=0; i<m_department_list.size(); i++)
		{
			if(m_department_list.get(i).m_department_type == DepartmentTypes.MANUFACTURE)
				manufacture = (Manufacture) m_department_list.get(i);
		}
		
		if(m_commodity != null)
		{
			if(m_inverntory > 0)
			{
				if(here_sales != null)
				{
					if(here_sales.m_inverntory == 0 && here_sales.m_is_done == false)
					{
						here_sales.m_inverntory += m_inverntory;
						m_inverntory = 0;
						here_sales.m_is_done = true;
						m_is_done = true;
						return 0;
					}
					
				}
				
				if(manufacture != null)
				{
					if(manufacture.m_supply_inverntory == 0 && manufacture.m_is_done == false)
					{
						manufacture.m_supply_inverntory += m_inverntory;
						m_inverntory = 0;
						manufacture.m_is_done = true;
						m_is_done = true;
						return 0;
					}
				}
			}
			else
			{
				/*
				if(away_sales != null)
				{
					if(m_is_done == false)
					{
						m_inverntory += away_sales.m_inverntory;
						away_sales.m_inverntory = 0;
						away_sales.m_is_done = true;
						m_is_done = true;
						return 0;
					}
				}*/
			}
		
			double purchase_cost = 0;
			
			//if(m_department_manager.m_construction.m_constructiontype == ConstructionTypes.RETAIL)
			{
				
			}
			
			//if(m_department_manager.m_construction.m_constructiontype == ConstructionTypes.FACTORY)
			{
				if(m_maxinverntory-m_inverntory < m_maxwork_perday)
					actual_work = m_maxinverntory-m_inverntory;
				else
					actual_work = m_maxwork_perday;
				
				if(away_sales.m_inverntory < actual_work)
					actual_work = away_sales.m_inverntory;
				
				m_inverntory += actual_work;
				away_sales.AwayProcess(actual_work);
				purchase_cost = (m_commodity.m_baseprice +
						m_commodity.m_baseprice/50 * m_distance_from_linked_construction) * actual_work / m_commodity.m_size;
				if(m_inverntory > m_maxinverntory)
					m_inverntory = m_maxinverntory;
			}
			
			Player.m_monthly_sales_expense[Player.m_netprofit_index] -= purchase_cost;
			
			m_practical_use[m_day%30] = actual_work;
			player_cost += purchase_cost;
			m_is_done = true;
			return -player_cost;
		}
		m_is_done = true;
		return 0;
		
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		// departmentmanager가 해야할 일들
		Paint p = new Paint();
    	p.setTextSize(25/2);
    	
    	p.setColor(Color.GREEN);
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
	    	canvas.drawText("재고 : "+m_inverntory/m_commodity.m_size, DepartmentManager.m_department_rect[m_index].left+10/2, 
	    			DepartmentManager.m_department_rect[m_index].top+80/2, p);
	    	
	    	double avg = 0;
	    	if(m_day < 30)
	    	{
	    		for(int i=0; i<m_day+1; i++)
	    			avg += m_practical_use[i];
	    		avg /= ((m_day+1)*m_maxwork_perday);
	    	}
	    	else
	    	{
	    		for(int i=0; i<30; i++)
	    			avg += m_practical_use[i];
	    		avg /= ((m_day+1)*m_maxwork_perday);
	    	}
	    	
	    	avg *= 100;
	    	
	    	Log.e("abc", " "+avg);
	    	
	    	int length = 79/2*(int)avg/100*2/2;
	    	
	    	p.setColor(Color.rgb(156, 125, 57));
	    	canvas.drawRect(DepartmentManager.m_department_rect[m_index].left+5/2, DepartmentManager.m_department_rect[m_index].top+120/2, 
	    			DepartmentManager.m_department_rect[m_index].left+5/2+length, DepartmentManager.m_department_rect[m_index].top+120/2+3/2, p);
	    	p.setColor(Color.rgb(206, 190, 156));
	    	canvas.drawRect(DepartmentManager.m_department_rect[m_index].left+5/2, DepartmentManager.m_department_rect[m_index].top+120/2+3/2, 
	    			DepartmentManager.m_department_rect[m_index].left+5/2+length, DepartmentManager.m_department_rect[m_index].top+120/2+11/2, p);
	    	p.setColor(Color.rgb(181, 158, 107));
	    	canvas.drawRect(DepartmentManager.m_department_rect[m_index].left+5/2, DepartmentManager.m_department_rect[m_index].top+120/2+11/2, 
	    			DepartmentManager.m_department_rect[m_index].left+5/2+length, DepartmentManager.m_department_rect[m_index].top+120/2+15/2, p);
	    	p.setColor(Color.rgb(156, 125, 57));
	    	canvas.drawRect(DepartmentManager.m_department_rect[m_index].left+5/2, DepartmentManager.m_department_rect[m_index].top+120/2+15/2, 
	    			DepartmentManager.m_department_rect[m_index].left+5/2+length, DepartmentManager.m_department_rect[m_index].top+120/2+19/2, p);
	    	p.setColor(Color.rgb(140, 109, 49));
	    	canvas.drawRect(DepartmentManager.m_department_rect[m_index].left+5/2, DepartmentManager.m_department_rect[m_index].top+120/2+19/2, 
	    			DepartmentManager.m_department_rect[m_index].left+5/2+length, DepartmentManager.m_department_rect[m_index].top+120/2+23/2, p);
	    	
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
	    	
	    	canvas.drawBitmap(m_connection_button_bitmap, 300/2, 250/2, null);
	    	
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
