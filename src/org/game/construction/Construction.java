package org.game.construction;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.GameState.*;
import org.game.commodity.*;
import org.game.commodity.Commodity.CommodityTypes;
import org.game.department.*;
import org.game.department.Department.DepartmentTypes;
import org.screen.*;
import org.screen.layer.component.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.view.*;

public abstract class Construction {
	public enum ConstructionTypes {NOTHING, FARM, FACTORY, RETAIL, RnD, PORT};
	
	public ConstructionTypes m_constructiontype;
	
	public Point m_point = new Point();		// 가장 첫번째 셀의 위치
	public int m_size;									// 건물 크기
	public int m_average_landvalue;				// 총 지가
	
	public int m_select;
	public int m_max_select;
	public int m_select_index_list[];
	public static Construction m_source_construction;
	
	public double distance;
	
	public static Rect m_department_set_rect;
	public static Rect m_return_to_map_rect;
	public static Rect m_summary_tab_rect;
	public static Rect m_department_tab_rect;
	public static Rect m_department_connection_rect;
	
	public static final int DEPARTMENT_SET_LEFT = 329*2/2;
	public static final int DEPARTMENT_SET_TOP = 10*2/2;
	public static final int DEPARTMENT_SET_RIGHT = 637*2/2;
	public static final int DEPARTMENT_SET_BOTTOM = 317*2/2;
	
	public static final int RETURN_TO_MAP_LEFT = 486*2/2;
	public static final int RETURN_TO_MAP_TOP = 330*2/2;
	public static final int RETURN_TO_MAP_RIGHT = 634*2/2;
	public static final int RETURN_TO_MAP_BOTTOM = 353*2/2;
	
	public static final int SUMMARY_TAB_LEFT = 0*2/2;
	public static final int SUMMARY_TAB_TOP = 323*2/2;
	public static final int SUMMARY_TAB_RIGHT = 75*2/2;
	public static final int SUMMARY_TAB_BOTTOM = 345*2/2;
	
	public static final int DEPARTMENT_TAB_LEFT = 77*2/2;
	public static final int DEPARTMENT_TAB_TOP = 323*2/2;
	public static final int DEPARTMENT_TAB_RIGHT = 152*2/2;
	public static final int DEPARTMENT_TAB_BOTTOM = 345*2/2;
	
	public DepartmentManager m_department_manager;
	
	public static Bitmap m_construction_screen_with_summary_tab_bitmap;
	public static Bitmap m_construction_screen_with_department_tab_bitmap;
	
	public static Bitmap m_button_up_bitmap;
	public static Bitmap m_button_down_bitmap;
	
	public long m_annual_netprofit;			// 사업체의 연간순이익
	public long m_monthly_netprofit[];	// 사업체의 최근 12달의 순이익 배열
	public long m_last_netprofit;				// 사업체의 최근 13달 이전의 순이익
	// 위 배열의 인덱스(배열의 끝까지 도달하면 다시 처럼으로)는 Player 클래스의 것을 사용
	
	public long m_max_netprofit;				// 그래프의 현재 최고 수치(1000,0000 단위로 구분)
	
	public enum ConstructionTabTypes {SUMMARY, DEPARTMENT};
	public static ConstructionTabTypes s_tabtype;
	
	private static Button returnToMapButton;
	
	private static Construction mResearchConstruction;

	public Construction(ConstructionTypes construction_type) {
		// TODO Auto-generated constructor stub
		m_department_set_rect = new Rect(DEPARTMENT_SET_LEFT, DEPARTMENT_SET_TOP, 
				DEPARTMENT_SET_RIGHT, DEPARTMENT_SET_BOTTOM);
		m_return_to_map_rect = new Rect(RETURN_TO_MAP_LEFT, RETURN_TO_MAP_TOP,
				RETURN_TO_MAP_RIGHT, RETURN_TO_MAP_BOTTOM);
		m_summary_tab_rect = new Rect(SUMMARY_TAB_LEFT, SUMMARY_TAB_TOP,
				SUMMARY_TAB_RIGHT, SUMMARY_TAB_BOTTOM);
		m_department_tab_rect = new Rect(DEPARTMENT_TAB_LEFT, DEPARTMENT_TAB_TOP,
				DEPARTMENT_TAB_RIGHT, DEPARTMENT_TAB_BOTTOM);
		m_department_connection_rect = new Rect(300/2, 250/2, 300/2+56*2/2, 250/2+24*2/2);
		
		
		m_constructiontype = construction_type;
		
		m_department_manager = new DepartmentManager(this);
		
		m_monthly_netprofit = new long[12];
		
		s_tabtype = ConstructionTabTypes.DEPARTMENT;
		
		// 파일에서 건물 관련 내용 읽기?
	}
	
	public static void InitStatic()
	{
		m_construction_screen_with_summary_tab_bitmap = AppManager.getInstance().getBitmap( R.drawable.construction_screen_with_summary_tab,
				Utility.sOptions);
		m_construction_screen_with_department_tab_bitmap = AppManager.getInstance().getBitmap( R.drawable.construction_screen_with_department_tab,
				Utility.sOptions);
		
		m_button_up_bitmap = AppManager.getInstance().getBitmap( R.drawable.button_up_149, Utility.sOptions);
		m_button_down_bitmap = AppManager.getInstance().getBitmap( R.drawable.button_down_149, Utility.sOptions);
		
		returnToMapButton = new Button(new Point(486, 330), 
				new Rect(486, 330, 486 + 149, 330 + 25), 
				R.drawable.button_up_149, true, true);
		
		returnToMapButton.setVisiable(true);
		returnToMapButton.setTouchedBitmap(R.drawable.button_down_149);
		returnToMapButton.setPushedBitmap(R.drawable.button_down_149);
		
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.BLACK);
    	p.setAntiAlias(true);
    	p.setTextAlign(Align.CENTER);
		
		returnToMapButton.setString("돌아가기");
		returnToMapButton.setStringNormalPos(new Point(75, 16));
		returnToMapButton.setNormalPaint(p);
		returnToMapButton.setStringTouchedPos(new Point(75, 17));
		returnToMapButton.setTouchedPaint(p);
		returnToMapButton.setStringPushedPos(new Point(75, 17));
		returnToMapButton.setPushedPaint(p);
		returnToMapButton.setStringEnabled(true);
	}
	
	public boolean onTouchEvent(MotionEvent event) 
	{
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		//if(m_return_to_map_rect.contains(x, y))
		if(returnToMapButton.onTouchEvent(event))
		{
			returnToMapButton.setPushed(false);
			GameState.mGameScreenType = GameScreenTypes.NORMAL;
			GameState.selection2 = 0;
			DepartmentManager.m_department_select = -1;
			//s_tabtype = ConstructionTabTypes.DEPARTMENT;
			DepartmentManager.s_summary_index = 0;
		}
		
		if(Department.mResearchButton.onTouchEvent(event))
		{
			if(m_department_manager.m_departments[DepartmentManager.m_department_select].m_department_type
				== DepartmentTypes.LABORATORY)
			{
				Department.mResearchButton.setPushed(false);
				
				String[] result = new String[4];
				
				result[0] = "금반지";
				result[1] = "빵";
				result[2] = "향수";
				result[3] = "텔레비젼";
				
				m_select = 0;
				
				mResearchConstruction = this;
				
				new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
			    .setTitle("기술연구할 제품을 선택하세요")
			    .setSingleChoiceItems(result, 0,
			    		new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								m_select = which;
							}
						})
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						if(m_select == 0)
							((Laboratory)mResearchConstruction.m_department_manager.m_departments[DepartmentManager.m_department_select])
									.m_commodity = CommodityManager.m_commoditis[4];
						
						if(m_select == 1)
							((Laboratory)mResearchConstruction.m_department_manager.m_departments[DepartmentManager.m_department_select])
									.m_commodity = CommodityManager.m_commoditis[3];
						
						if(m_select == 2)
							((Laboratory)mResearchConstruction.m_department_manager.m_departments[DepartmentManager.m_department_select])
									.m_commodity = CommodityManager.m_commoditis[5];
						
						if(m_select == 3)
							((Laboratory)mResearchConstruction.m_department_manager.m_departments[DepartmentManager.m_department_select])
									.m_commodity = CommodityManager.m_commoditis[6];
						
					}
				})
				.setNegativeButton("취소", null)
			    .show();
			}
		}
		
		m_department_manager.onTouchEvent(event);
		
		if(event.getSource() != 100)
			return false;
		
		if(m_department_connection_rect.contains(x, y))
		{
			if(DepartmentManager.m_department_select > -1)
				if(m_department_manager.m_departments[DepartmentManager.m_department_select] != null)
					if(m_department_manager.m_departments[DepartmentManager.m_department_select].m_department_type
							== DepartmentTypes.PURCHASE)
					{
						Utility.m_click_sound.start();
						Player.m_connection_destination_construction = this;
						UserInterface.m_is_toolbar_hidden = true;
						Player.m_connection_destination_department_index = DepartmentManager.m_department_select;
						Player.m_temp = 0;
						
						GameState.mGameScreenType = GameScreenTypes.NORMAL;
						GameState.selection2 = 0;
						DepartmentManager.m_department_select = -1;
					}
		}
		
		if(m_summary_tab_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			s_tabtype = ConstructionTabTypes.SUMMARY;
		}
		
		if(m_department_tab_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			s_tabtype = ConstructionTabTypes.DEPARTMENT;
		}

		return true;
	}
	
	public void Render(Canvas canvas)
	{
		if(s_tabtype == ConstructionTabTypes.SUMMARY)
			canvas.drawBitmap(m_construction_screen_with_summary_tab_bitmap, 0, 0, null);
		if(s_tabtype == ConstructionTabTypes.DEPARTMENT)
			canvas.drawBitmap(m_construction_screen_with_department_tab_bitmap, 0, 0, null);
		
		returnToMapButton.Render(canvas);
    	
    	m_department_manager.Render(canvas);
    	
    	
    	
    	/*if(m_constructiontype == ConstructionTypes.RnD || m_constructiontype == ConstructionTypes.FARM)
    	{
    		Paint p = new Paint();
        	p.setTextSize(35); 
        	p.setColor(Color.BLACK);
        	
    		canvas.drawText("준비중입니다...", 50, 150, p);
    	}*/
    	
    	/*
    	if(m_department_manager.m_department_select > -1 )
    		if(m_department_manager.m_departments[m_department_manager.m_department_select] != null)
    		{
	    		canvas.drawBitmap(m_department_manager.m_departments[m_department_manager.m_department_select].m_department_bitmap
	    				, 40, 350, null);
	    		canvas.drawBitmap(m_department_manager.m_departments[m_department_manager.m_department_select].m
	    				, 40, 150, null);
    		}*/
	}
	
	public void Update()
	{
		long max;
		
		/*
		Random rand = new Random();
		
		if(rand.nextInt(2) == 0)
			m_monthly_netprofit[Player.m_netprofit_index] += (rand.nextInt(10) * 1000000);
		else
			m_monthly_netprofit[Player.m_netprofit_index] -= (rand.nextInt(10) * 1000000);
		
		m_annual_netprofit = 0;
		for(int i=0; i<12; i++)
		{
			m_annual_netprofit += m_monthly_netprofit[i];
		}
		*/
		
		max = Math.abs(m_monthly_netprofit[0]);
		for(int i=1; i<12; i++)
		{
			if(max < Math.abs(m_monthly_netprofit[i]))
					max = Math.abs(m_monthly_netprofit[i]);
		}
		
		m_max_netprofit = max;
		
		max /= 10000000;
		
		if(max == 0)
		{
			m_max_netprofit = 10000000;
		}
		else
		{
			if((m_max_netprofit%10000000) != 0)
				m_max_netprofit = (max+1)*10000000;
		}
		
	}
	
	public void Reset()
	{
		m_last_netprofit = m_monthly_netprofit[Player.m_netprofit_index];
		m_monthly_netprofit[Player.m_netprofit_index] = 0;
	}
	
	public void ShowGraphs(Canvas canvas)
	{
		long height;
		long previous_height;
		long temp_height;
		int index;
		
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	//p.setStyle(style)
    	//Style ff;
    	//ff.
    	
    	canvas.drawBitmap(UserInterface.m_netprofit_popup_info_bitmap, 1000/2, 200/2, null);
    	
    	p.setTextAlign(Align.CENTER);
		canvas.drawText("사업체 순이익", 1140/2, 240/2, p);
		canvas.drawText("지난 12개월간", 1140/2, 400/2, p);
		
		p.setStrokeWidth(3);
		canvas.drawLine(1030/2, 300/2, 1250/2, 300/2, p);
		
		p.setColor(Color.RED);
		index = (Player.m_netprofit_index+1)%12;
		previous_height = 0;
		if(m_max_netprofit != 0)
		{
			for(int i=0; i<12; i++)
			{
				height = 25*m_monthly_netprofit[index]/m_max_netprofit*2;
				
				index = (index+1)%12;
				
				if(i == 0)
				{
					temp_height = 25*m_last_netprofit/m_max_netprofit*2;
					canvas.drawLine(1050/2+15/2*i, 300/2-temp_height/2, 1050/2+15/2*(i+1), 300/2-height/2, p);
				}
				else if(i == 11)
				{
					canvas.drawRect(1050/2+15/2*i, 300/2-height/2, 1050/2+15/2*(i+1), 300/2, p);
					//drawLine(1050+15*i, 300+previous_height, 1050+15*(i+1), 300+height, p);
					break;
				}
				else
				{
					canvas.drawLine(1050/2+15/2*i, 300/2-previous_height/2, 1050/2+15/2*(i+1), 300/2-height/2, p);
				}
				
				previous_height = height;
			}
		}
		
		p.setColor(Color.WHITE);
		p.setTextAlign(Align.LEFT);
		p.setTextSize(10);
		canvas.drawText(m_max_netprofit/10000+"(만)", 1050/2, 270/2, p);
		canvas.drawText("("+m_max_netprofit/10000+"(만))", 1050/2, 350/2, p);

	}
	
	public void ResetDepartment()
	{
		m_department_manager.ResetDepartment();
	}
	
	public void ShowAvailableConnection(ConstructionTypes source_construction_type)
	{
		m_max_select = 0;
		
		ArrayList<String> temp = new ArrayList<String>();
		String result[];
		
		m_select_index_list = new int[DepartmentManager.DEPARTMENT_COUNT];
		
		m_source_construction = this;
		
		// 거리 계산
		distance = Math.sqrt(Math.pow(m_source_construction.m_point.x-Player.m_connection_destination_construction.m_point.x, 2) + 
				Math.pow(m_source_construction.m_point.y-Player.m_connection_destination_construction.m_point.y, 2));
		
		if(source_construction_type == ConstructionTypes.RETAIL)
		{
			if(m_constructiontype == ConstructionTypes.FACTORY)
			{
				for(int i=0; i<m_department_manager.m_departments.length; i++)
				{
					if(m_department_manager.m_departments[i] != null)
						if(m_department_manager.m_departments[i].m_department_type == DepartmentTypes.SALES)
							if(((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.PRODUCT)
							{
								temp.add(((Sales)m_department_manager.m_departments[i]).m_commodity.m_name + " (가격:"+((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice
									+",운송비:"+String.format("%.1f",((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice/50*distance)+")");
								m_select_index_list[temp.size()-1] = i;
							}
				}
			}
			
			if(m_constructiontype == ConstructionTypes.FARM)
			{
				for(int i=0; i<m_department_manager.m_departments.length; i++)
				{
					if(m_department_manager.m_departments[i] != null)
						if(m_department_manager.m_departments[i].m_department_type == DepartmentTypes.SALES)
							if(((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.PRODUCT)
							{
								temp.add(((Sales)m_department_manager.m_departments[i]).m_commodity.m_name + " (가격:"+((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice
									+",운송비:"+String.format("%.1f",((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice/50*distance)+")");
								m_select_index_list[temp.size()-1] = i;
							}
				}
			}
			
			if(m_constructiontype == ConstructionTypes.PORT)
			{
				for(int i=0; i<m_department_manager.m_departments.length; i++)
				{
					if(m_department_manager.m_departments[i] != null)
						if(m_department_manager.m_departments[i].m_department_type == DepartmentTypes.SALES)
							if(((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.PRODUCT)
							{
								temp.add(((Sales)m_department_manager.m_departments[i]).m_commodity.m_name + " (가격:"+((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice
									+",운송비:"+String.format("%.1f",((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice/50*distance)+")");
								m_select_index_list[temp.size()-1] = i;
							}
				}
			}
		}
		
		if(source_construction_type == ConstructionTypes.FACTORY)
		{
			if(m_constructiontype == ConstructionTypes.FARM)
			{
				for(int i=0; i<m_department_manager.m_departments.length; i++)
				{
					if(m_department_manager.m_departments[i] != null)
						if(m_department_manager.m_departments[i].m_department_type == DepartmentTypes.SALES)
							if(((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.INTERMEDIATE_MATERIAL
								|| ((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.RAW_MATERIAL)
							{
								temp.add(((Sales)m_department_manager.m_departments[i]).m_commodity.m_name + " (가격:"+((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice
									+",운송비:"+String.format("%.1f",((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice/50*distance)+")");
								m_select_index_list[temp.size()-1] = i;
							}
				}
			}
			
			if(m_constructiontype == ConstructionTypes.FACTORY)
			{
				for(int i=0; i<m_department_manager.m_departments.length; i++)
				{
					if(m_department_manager.m_departments[i] != null)
						if(m_department_manager.m_departments[i].m_department_type == DepartmentTypes.SALES)
							if(((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.INTERMEDIATE_MATERIAL
								|| ((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.RAW_MATERIAL)
							{
								temp.add(((Sales)m_department_manager.m_departments[i]).m_commodity.m_name + " (가격:"+((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice
									+",운송비:"+String.format("%.1f",((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice/50*distance)+")");
								m_select_index_list[temp.size()-1] = i;
							}
				}
			}
			
			if(m_constructiontype == ConstructionTypes.PORT)
			{
				for(int i=0; i<m_department_manager.m_departments.length; i++)
				{
					if(m_department_manager.m_departments[i] != null)
						if(m_department_manager.m_departments[i].m_department_type == DepartmentTypes.SALES)
							if(((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.INTERMEDIATE_MATERIAL
								|| ((Sales)m_department_manager.m_departments[i]).m_commodity.m_commodity_type == CommodityTypes.RAW_MATERIAL)
							{
								temp.add(((Sales)m_department_manager.m_departments[i]).m_commodity.m_name + " (가격:"+((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice
									+",운송비:"+String.format("%.1f",((Sales)m_department_manager.m_departments[i]).m_commodity.m_baseprice/50*distance)+")");
								m_select_index_list[temp.size()-1] = i;
							}
				}
			}
		}
		
		m_max_select = temp.size();
		result = new String[m_max_select];
		
		for(int i=0; i<temp.size(); i++)
		{
			result[i] = temp.get(i);
		}
		
		if(temp.size() == 0)
		{
			Utility.MessageDialog("거래할 수 있는 제품이 없습니다");
			
			return;
		}
		
		m_select = 0;
		
		new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
	    .setTitle(String.format("거래할 제품을 선택하세요 (%.1fKm)", distance))
	    .setSingleChoiceItems(result, 0,
	    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						m_select = which;
					}
				})
		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				
				{
																													//
					

					
					//DepartmentManager.m_department_select = Player.m_connection_destination_department_index;
					//GameState.mGameScreenType = GameScreenTypes.CONSTRUCTION;
					//	GameState.selection = 0;
					//	GameState.construction_type_selection = ConstructionTypes.RETAIL;
					
					
				}
				
				for(int i=0; i<m_max_select; i++)
				{
					if(m_select == i)
					{
						
						
						((Purchase)Player.m_connection_destination_construction.m_department_manager.m_departments[Player.m_connection_destination_department_index])
							.m_linked_construction = m_source_construction;
						((Purchase)Player.m_connection_destination_construction.m_department_manager.m_departments[Player.m_connection_destination_department_index])
							.m_linked_department_index = m_select_index_list[i];
						((Purchase)Player.m_connection_destination_construction.m_department_manager.m_departments[Player.m_connection_destination_department_index])
							.m_commodity = ((Sales)m_source_construction.m_department_manager.m_departments[m_select_index_list[i]]).m_commodity;
						((Purchase)Player.m_connection_destination_construction.m_department_manager.m_departments[Player.m_connection_destination_department_index])
							.m_distance_from_linked_construction = distance;
						
						((Sales)m_source_construction.m_department_manager.m_departments[m_select_index_list[i]])
							.m_linked_constructionlist.add(Player.m_connection_destination_construction);
						((Sales)m_source_construction.m_department_manager.m_departments[m_select_index_list[i]])
							.m_linked_department_indexlist.add(Player.m_connection_destination_department_index);
						
						Player.m_connection_destination_construction = null;
						Player.m_connection_destination_department_index = -1;
						Player.m_connection_source_construction = null;
						UserInterface.m_is_toolbar_hidden = false;
						
						break;
					}
					
				}
			}
		})
		.setNegativeButton("취소", null)
	    .show();
		

	}

}
