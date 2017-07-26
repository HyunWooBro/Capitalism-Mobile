package org.game.department;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.cell.Cell.LandValues;
import org.game.commodity.*;
import org.game.construction.*;
import org.game.construction.Construction.*;
import org.game.department.Department.*;

import android.graphics.*;
import android.graphics.Paint.*;
import android.util.*;

public class Sales extends Department {
	public int m_inverntory;
	public int m_maxinverntory;
	public int m_maxwork_perday;
	public int m_supply;						// 공급 0 ~ 100%
	public int m_demand;					// 수요 0 ~ 100%
	public int m_practical_use; 			// 활용도 0 ~ 100%
	public Commodity m_commodity;
	public ArrayList<Construction> m_linked_constructionlist;
	public ArrayList<Integer> m_linked_department_indexlist;
	
	public long away_profit;
	
	public double mNewPrice; 
	public double mTempNewPrice;

	public Sales(int index, Bitmap bitmap, 
			DepartmentManager department_manager) {
		super(index, bitmap, department_manager);
		// TODO Auto-generated constructor stub
		m_department_type = DepartmentTypes.SALES;
		m_employee = 3;
		m_maxinverntory = 10000;
		m_maxwork_perday = 2000;
		
		m_linked_constructionlist = new ArrayList<Construction>();
		m_linked_department_indexlist = new ArrayList<Integer>();

	}

	@Override
	public double Process() {
		// TODO Auto-generated method stub
		
		if(m_is_done == true && away_profit == 0)
			return 0;
		
		if(m_commodity != null)
		{
			long player_cost = 0;
			long player_earning = 0;
			long player_net_profit = 0;
			int maximum_day = 0;
			
			int actual_work;
			int transfer_inventory;
			
			Purchase purchase = null;
			Manufacture manufacture = null;
			
			for(int i=0; i<m_department_list.size(); i++)
			{
				if(m_department_list.get(i).m_department_type == DepartmentTypes.PURCHASE)
					purchase = (Purchase) m_department_list.get(i);
				
				if(m_department_list.get(i).m_department_type == DepartmentTypes.MANUFACTURE)
					manufacture = (Manufacture) m_department_list.get(i);
			}
			
			if(m_inverntory == 0)
			{
				if(purchase != null)
				{
					if(purchase.m_commodity == m_commodity)
					{
						if(purchase.m_inverntory != 0 && purchase.m_is_done == false)
						{
							m_inverntory += purchase.m_inverntory;
							purchase.m_inverntory = 0;
							m_is_done = true;
							purchase.m_is_done = true;
							
							if(away_profit != 0)
							{
								Player.m_monthly_sales[Player.m_netprofit_index] += away_profit;
								away_profit = 0;
								return away_profit;
							}
							
							return 0;
						}
					}
				}
				
				if(manufacture != null)
				{
					if(manufacture.m_commodity == m_commodity)
					{
						if(manufacture.m_inverntory != 0 && manufacture.m_is_done == false)
						{
							m_inverntory += manufacture.m_inverntory;
							manufacture.m_inverntory = 0;
							m_is_done = true;
							manufacture.m_is_done = true;
							
							if(away_profit != 0)
							{
								Player.m_monthly_sales[Player.m_netprofit_index] += away_profit;
								away_profit = 0;
								return away_profit;
							}
							
							return 0;
						}
					}
				}
				
				if(away_profit != 0)
				{
					Player.m_monthly_sales[Player.m_netprofit_index] += away_profit;
					away_profit = 0;
					return away_profit;
				}
			}
			else
			{
				double price_diffrence = 0;
				double maximum_sales = 0;
				double sales_earning = 0;
				
				if(m_department_manager.m_construction.m_constructiontype == ConstructionTypes.RETAIL)
				{
					Random rand = new Random();
					
					/*price_diffrence = 100 - (m_commodity.m_baseprice + 
							m_commodity.m_baseprice/100 * 0.1)*1.5/(m_commodity.m_baseprice*2)*100;*/
					price_diffrence = (m_commodity.m_baseprice + m_commodity.m_baseprice*1.0) - mNewPrice;
					
					maximum_sales = m_commodity.m_basesales
							+ 100-m_commodity.m_daily_necessity * -(50-City.m_economic_indicator);
					if(price_diffrence >0)
						maximum_sales *= (1.0+Math.pow(price_diffrence, 1.2)/m_commodity.m_baseprice);
					else
						maximum_sales *= (1.0/(1.0+Math.pow(-price_diffrence, 1.2)/m_commodity.m_baseprice));	
					Log.i(Utility.TAG, "maximum_sales "+(1.0/(1.0+Math.pow(price_diffrence, 1.2)/m_commodity.m_baseprice)));
					maximum_sales *= Math.sqrt(( Player.mQuality[m_commodity.m_index]-29));
					//maximum_sales *=  (double) Player.m_retaillist.get(i).m_average_landvalue/LandValues.CLASS_A.GetValue();
					maximum_sales *= (rand.nextInt(5) + 8);
					maximum_sales /= 10;
	
					if(m_maxwork_perday < maximum_sales)
						actual_work = m_maxwork_perday;
					else
						actual_work = (int) maximum_sales;
					
					if(actual_work > m_inverntory)
						actual_work = m_inverntory;
					
					m_inverntory -= actual_work;
					sales_earning = (mNewPrice) * actual_work / m_commodity.m_size;
				}
				
				if(m_department_manager.m_construction.m_constructiontype == ConstructionTypes.FACTORY)
				{
					
				}
				
				sales_earning += away_profit;
				away_profit = 0;
				
				Player.m_monthly_sales[Player.m_netprofit_index] += sales_earning;
				
				player_earning += sales_earning;
				m_is_done = true;
				return player_earning;
			}
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
						m_commodity = purchase.m_commodity;
						mNewPrice = m_commodity.m_baseprice + m_commodity.m_baseprice*1.2;
						mTempNewPrice = mNewPrice;
						m_is_done = true;
						return 0;
					}
				}
				
				if(m_department_list.get(i).m_department_type == DepartmentTypes.MANUFACTURE)
				{
					Manufacture manufacture = (Manufacture) m_department_list.get(i);
					if(manufacture.m_commodity != null)
					{
						m_commodity = manufacture.m_commodity;
						//mNewPrice = m_commodity.m_baseprice + m_commodity.m_baseprice*1.2;
						//mTempNewPrice = mNewPrice;
						m_is_done = true;
						return 0;
					}
				}
			}
		}
		m_is_done = true;
		return 0;
		
	}
	
	public void AwayProcess(int actual_work)
	{
		double sales_earning = 0;
		
		m_inverntory -= actual_work;
		sales_earning = (mNewPrice) * actual_work / m_commodity.m_size;
		
		
		
		away_profit += sales_earning;
	}

	@Override
	public void Render(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint p = new Paint();
    	p.setTextSize(12); 
    	
    	p.setColor(Color.YELLOW);
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
