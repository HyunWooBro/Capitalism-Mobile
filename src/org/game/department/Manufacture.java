package org.game.department;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.commodity.*;
import org.game.construction.*;
import org.game.department.Department.*;

import android.graphics.*;
import android.graphics.Paint.*;

public class Manufacture extends Department {
	public int m_inverntory;
	public int m_maxinverntory;
	public int m_supply_inverntory;
	public int m_supply_maxinverntory;
	public int m_maxwork_perday;
	public int m_supply;						// 공급 0 ~ 100%
	public int m_demand;					// 수요 0 ~ 100%
	public int m_practical_use; 			// 활용도 0 ~ 100%
	public Commodity m_commodity;
	public Commodity m_commodity_input;
	//public Commodity m_commodity_output;

	public Manufacture(int index, Bitmap bitmap, 
			DepartmentManager department_manager) {
		super(index, bitmap, department_manager);
		// TODO Auto-generated constructor stub
		m_department_type = DepartmentTypes.MANUFACTURE;
		m_employee = 5;
		m_maxinverntory = 10000;
		m_supply_maxinverntory = 10000;
		m_maxwork_perday = 2000;
	}

	@Override
	public double Process() {
		// TODO Auto-generated method stub
		
		if(m_is_done == true)
			return 0;
		
		if(m_commodity != null)
		{
			long player_cost = 0;
			long player_earning = 0;
			long player_net_profit = 0;
			int maximum_day = 0;
			
			int actual_work;
			int transfer_inventory;
			
			int temp;
			
			Purchase purchase = null;
			Manufacture input_manufacture = null;
			Manufacture output_manufacture = null;
			Sales sales = null;
			
			for(int i=0; i<m_department_list.size(); i++)
			{
				if(m_department_list.get(i).m_department_type == DepartmentTypes.PURCHASE)
				{
					purchase = (Purchase) m_department_list.get(i);
					if(purchase.m_commodity != m_commodity_input)
					//	break;
					//else
						purchase = null;
				}
				
				if(m_department_list.get(i).m_department_type == DepartmentTypes.MANUFACTURE)
				{
					input_manufacture = (Manufacture) m_department_list.get(i);
					if(input_manufacture.m_commodity != m_commodity_input)
					//	break;
					//else
						input_manufacture = null;
				}
				
				if(m_department_list.get(i).m_department_type == DepartmentTypes.MANUFACTURE)
				{
					output_manufacture = (Manufacture) m_department_list.get(i);
					if(output_manufacture.m_commodity_input != m_commodity)
					//	break;
					//else
						output_manufacture = null;
				}

				if(m_department_list.get(i).m_department_type == DepartmentTypes.SALES)
				{
					sales = (Sales) m_department_list.get(i);
					if(sales.m_commodity != m_commodity)
					//	break;
					//else
						sales = null;
				}
			}
			
			if(m_inverntory > 0)
			{
				if(sales != null)
				{
					if(sales.m_inverntory == 0 && sales.m_is_done == false)
					{
						sales.m_inverntory += m_inverntory;
						m_inverntory = 0;
						sales.m_is_done = true;
						m_is_done = true;
						return 0;
					}
				}
				
				if(output_manufacture != null)
				{
					if(output_manufacture.m_supply_inverntory == 0 && output_manufacture.m_is_done == false)
					{
						output_manufacture.m_supply_inverntory += m_inverntory;
						m_inverntory = 0;
						output_manufacture.m_is_done = true;
						m_is_done = true;
						return 0;
					}
				}
			}
			
			if(m_supply_inverntory == 0)
			{
				if(purchase != null)
				{
					if(purchase.m_commodity == m_commodity_input)
					{
						if(purchase.m_inverntory != 0 && purchase.m_is_done == false)
						{
							m_supply_inverntory += purchase.m_inverntory;
							purchase.m_inverntory = 0;
							m_is_done = true;
							purchase.m_is_done = true;
							return 0;
						}
					}
				}
				
				if(input_manufacture != null)
				{
					if(input_manufacture.m_commodity == m_commodity_input)
					{
						if(input_manufacture.m_inverntory != 0 && input_manufacture.m_is_done == false)
						{
							m_supply_inverntory += input_manufacture.m_inverntory;
							input_manufacture.m_inverntory = 0;
							m_is_done = true;
							input_manufacture.m_is_done = true;
							return 0;
						}
					}
				}
			}
			//else
			
			if(m_maxinverntory-m_inverntory < m_maxwork_perday)
				actual_work = m_maxinverntory-m_inverntory;
			else
				actual_work = m_maxwork_perday;
			
			if(actual_work > m_supply_inverntory)
				actual_work = m_supply_inverntory;
			
			m_inverntory += actual_work;
			m_supply_inverntory -= actual_work;
			
			/*if(m_inverntory > m_maxinverntory)
				m_inverntory = m_maxinverntory;*/
			
			m_is_done = true;
			//purchase.m_is_done = true;
			return 0;

			/*
			if(purchase != null)
			{
				if(purchase.m_commodity == m_commodity_input)
				{
					if(purchase.m_inverntory != 0) // && purchase.m_is_done == false)
					{
						
						//m_inverntory += purchase.m_inverntory;
						//purchase.m_inverntory = 0;
						
						//int input_count = CommodityManager.getInstance().
						//		m_commodity_manufactures[m_commodity_input.m_index][m_commodity.m_index];
						//int input_work = input_count * m_commodity_input.m_size;
						
						//int output_count = purchase.m_inverntory / input_work;
						
						
						if(m_maxinverntory-m_inverntory < m_maxwork_perday)
							actual_work = m_maxinverntory-m_inverntory;
						else
							actual_work = m_maxwork_perday;
						
						if(actual_work > purchase.m_inverntory)
							actual_work = purchase.m_inverntory;
						
						m_inverntory += actual_work;
						purchase.m_inverntory -= actual_work;
						
						if(m_inverntory > m_maxinverntory)
							m_inverntory = m_maxinverntory;
						
						m_is_done = true;
						//purchase.m_is_done = true;
						return 0;
					}
				}
			}
			
			if(input_manufacture != null)
			{
				if(input_manufacture.m_commodity == m_commodity_input)
				{
					if(input_manufacture.m_inverntory != 0) // && purchase.m_is_done == false)
					{
						
						if(m_maxinverntory-m_inverntory < m_maxwork_perday)
							actual_work = m_maxinverntory-m_inverntory;
						else
							actual_work = m_maxwork_perday;
						
						if(actual_work < input_manufacture.m_inverntory)
							actual_work = input_manufacture.m_inverntory;
						
						m_inverntory += actual_work;
						input_manufacture.m_inverntory -= actual_work;
						
						if(m_inverntory > m_maxinverntory)
							m_inverntory = m_maxinverntory;
						
						m_is_done = true;
						//purchase.m_is_done = true;
						return 0;
					}
				}
			}*/
		}
		else
		{
			for(int i=0; i<m_department_list.size(); i++)
			{
				if(m_department_list.get(i).m_department_type == DepartmentTypes.PURCHASE)
				{
					Purchase purchase = (Purchase) m_department_list.get(i);
					if(purchase.m_commodity != null)
					{
						Commodity output = CommodityManager.getInstance().GetOutputCommodity(purchase.m_commodity);
						m_commodity_input = purchase.m_commodity;

						m_commodity = CommodityManager.getInstance().m_commoditis[output.m_index];
						m_is_done = true;
						return 0;
					}
				}
				
				if(m_department_list.get(i).m_department_type == DepartmentTypes.MANUFACTURE)
				{
					Manufacture manufacture = (Manufacture) m_department_list.get(i);
					if(manufacture.m_commodity != null)
					{
						Commodity output = CommodityManager.getInstance().GetOutputCommodity(manufacture.m_commodity);
						m_commodity_input =  manufacture.m_commodity;

						m_commodity = CommodityManager.getInstance().m_commoditis[output.m_index];
						m_is_done = true;
						return 0;
					}
				}
			}
		}
		
		m_is_done = true;
		return 0;
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint p = new Paint();
    	p.setTextSize(12); 
    	
    	p.setColor(Color.MAGENTA);
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
	    	canvas.drawText("원료 : "+m_supply_inverntory/m_commodity.m_size, DepartmentManager.m_department_rect[m_index].left+10/2, 
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
