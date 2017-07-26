package org.game;

import java.util.*;

import org.framework.*;
import org.game.construction.*;
import org.game.department.*;

/**
 * 게임의 핵심인 경제활동을 제어하는 클래스 
 * @author 김현준
 *
 */
public class CapitalismSystem {
	public static long m_max_daily_positive_profit;
	public static long m_max_daily_negative_profit;
	
	// 싱글턴
	private static CapitalismSystem s_instance;
	private CapitalismSystem() {}
	public static CapitalismSystem GetInstance(){
		if(s_instance == null){
			s_instance = new CapitalismSystem();
		}
		return s_instance;
	}
	
	public static void Destroy()
	{
		s_instance = null;
	}
	
	public static void InitStatic()
	{
		m_max_daily_positive_profit = 0;
		m_max_daily_negative_profit = 0;
	}
	
	public void Process(GameState gamestate)
	{
		long player_cost = 0;
		long player_earning = 0;
		long player_net_profit = 0;
		int maximum_day = 0;
		
		long player_economic_result = 0;
		
		m_max_daily_positive_profit = 0;
		m_max_daily_negative_profit = 0;
		
		for(int i=0; i<Player.m_retaillist.size(); i++)
			player_economic_result += Player.m_retaillist.get(i).m_department_manager.Process();
		
		for(int i=0; i<Player.m_factorylist.size(); i++)
			player_economic_result += Player.m_factorylist.get(i).m_department_manager.Process();
		
		for(int i=0; i<Player.m_farmlist.size(); i++)
			player_economic_result +=  Player.m_farmlist.get(i).m_department_manager.Process();
		
		for(int i=0; i<Player.m_RnDlist.size(); i++)
			player_economic_result +=  Player.m_RnDlist.get(i).m_department_manager.Process();
		
		
		Player.m_num_employee = 0;
		
		for(int i=0; i<Player.m_retaillist.size(); i++)
    		for(int j=0; j<DepartmentManager.DEPARTMENT_COUNT; j++)
    			if(Player.m_retaillist.get(i).m_department_manager.m_departments[j] != null)
    				Player.m_num_employee += Player.m_retaillist.get(i).m_department_manager.m_departments[j].m_employee;
    	
    	for(int i=0; i<Player.m_factorylist.size(); i++)
    		for(int j=0; j<DepartmentManager.DEPARTMENT_COUNT; j++)
    			if(Player.m_factorylist.get(i).m_department_manager.m_departments[j] != null)
    				Player.m_num_employee += Player.m_factorylist.get(i).m_department_manager.m_departments[j].m_employee;
    	
    	for(int i=0; i<Player.m_farmlist.size(); i++)
    		for(int j=0; j<DepartmentManager.DEPARTMENT_COUNT; j++)
    			if(Player.m_farmlist.get(i).m_department_manager.m_departments[j] != null)
    				Player.m_num_employee += Player.m_farmlist.get(i).m_department_manager.m_departments[j].m_employee;
    	
    	for(int i=0; i<Player.m_RnDlist.size(); i++)
    		for(int j=0; j<DepartmentManager.DEPARTMENT_COUNT; j++)
    			if(Player.m_RnDlist.get(i).m_department_manager.m_departments[j] != null)
    				Player.m_num_employee += Player.m_RnDlist.get(i).m_department_manager.m_departments[j].m_employee;
		
		/*
		if(gamestate.m_player.m_factorylist.isEmpty() == false)
		{
			for(int i=0; i<gamestate.m_player.m_retaillist.size(); i++)
			{
				Purchase purchase = (Purchase) gamestate.m_player.m_retaillist.get(i).m_department_manager.m_departments.get(0);
				Sales sales = (Sales) gamestate.m_player.m_retaillist.get(i).m_department_manager.m_departmentlist.get(1);
				
				//int work_perday = purchase.m_maxwork_perday / purchase.m_commodity.m_size;
				
				int actual_work;
				int transfer_inventory;
				
				if(sales.m_inverntory == 0)
				{
					if(purchase.m_inverntory == 0)
					{
						if(purchase.m_maxinverntory-purchase.m_inverntory < purchase.m_maxwork_perday)
							actual_work = purchase.m_maxinverntory-purchase.m_inverntory;
						else
							actual_work = purchase.m_maxwork_perday;
						
						purchase.m_inverntory += actual_work;
						int purchase_cost = (//purchase.m_commodity.m_baseprice +
								purchase.m_commodity.m_baseprice/100 * purchase.m_distance_from_linked_construction) * actual_work / purchase.m_commodity.m_size;
						if(purchase.m_inverntory > purchase.m_maxinverntory)
							purchase.m_inverntory = purchase.m_maxinverntory;
						
						player_cost += purchase_cost;
					}
					else
					{
						if(purchase.m_inverntory < purchase.m_maxinverntory)
							transfer_inventory = purchase.m_inverntory;
						else
							transfer_inventory = purchase.m_maxinverntory;
						
						purchase.m_inverntory -= transfer_inventory;
						sales.m_inverntory += transfer_inventory;
					}
				}
				else
				{
					if(purchase.m_maxinverntory-purchase.m_inverntory < purchase.m_maxwork_perday)
						actual_work = purchase.m_maxinverntory-purchase.m_inverntory;
					else
						actual_work = purchase.m_maxwork_perday;
					
					purchase.m_inverntory += actual_work;
					int purchase_cost = (//purchase.m_commodity.m_baseprice +
							purchase.m_commodity.m_baseprice/100 * purchase.m_distance_from_linked_construction) * actual_work / purchase.m_commodity.m_size;
					if(purchase.m_inverntory > purchase.m_maxinverntory)
						purchase.m_inverntory = purchase.m_maxinverntory;
					
					player_cost += purchase_cost;
					
					
					
					Random rand = new Random();
					
					double price_diffrence = 100 - (purchase.m_commodity.m_baseprice + 
							purchase.m_commodity.m_baseprice/100 * purchase.m_distance_from_linked_construction)*1.5/(sales.m_commodity.m_baseprice*2)*100;
					double maximum_sales = sales.m_commodity.m_basesales + 
							price_diffrence*price_diffrence +
							100-sales.m_commodity.m_daily_necessity * -(50-gamestate.m_city.m_economic_indicator);
					maximum_sales *=  (double) gamestate.m_player.m_retaillist.get(i).m_average_landvalue/160000000;
					maximum_sales *= (rand.nextInt(5) + 8);
					maximum_sales /= 10;

					if(sales.m_maxwork_perday < maximum_sales)
						actual_work = sales.m_maxwork_perday;
					else
						actual_work = (int) maximum_sales;
					
					if(actual_work > sales.m_inverntory)
						actual_work = sales.m_inverntory;
					
					sales.m_inverntory -= actual_work;
					int sales_earning = (purchase.m_commodity.m_baseprice + 
							purchase.m_commodity.m_baseprice/100 * purchase.m_distance_from_linked_construction)*2/3 * actual_work / purchase.m_commodity.m_size;
					
					player_earning += sales_earning;
					
				}
				
				
				
				
				//sales.
				
			}
		}*/
		
		// 현재 달의 최대 일 수
		maximum_day = Time.GetInstance().GetCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
		
		// 유지비 계산
		//player_cost += gamestate.m_player.m_retaillist.size() * Retail.maintenance/maximum_day;
		//player_cost += gamestate.m_player.m_factorylist.size() * Factory.maintenance/maximum_day;
		//player_cost += gamestate.m_player.m_farmlist.size() * Farm.maintenance/maximum_day;
		//player_cost += gamestate.m_player.m_RnDlist.size() * RnD.maintenance/maximum_day;
		
		// 대출 이자
		player_cost += Player.m_loan/10/12/maximum_day;
		Player.m_monthly_interest[Player.m_netprofit_index] -= Player.m_loan/10/12/maximum_day;
		
		// 하루 순이익
		player_net_profit = player_earning - player_cost + player_economic_result;
		
		gamestate.m_player.m_money += player_net_profit;
		
		//gamestate.m_player.m_annual_netprofit += player_net_profit;
		
		gamestate.m_player.m_monthly_netprofit[gamestate.m_player.m_netprofit_index] += player_net_profit;
		
		gamestate.m_player.m_annual_netprofit = 0;
		for(int i=0; i<12; i++)
			gamestate.m_player.m_annual_netprofit += gamestate.m_player.m_monthly_netprofit[i];
		
		gamestate.m_player.Update();
		
	}

}
