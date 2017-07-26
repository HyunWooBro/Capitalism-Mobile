package org.game;

import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.framework.*;
import org.framework.openGL.*;
import org.game.GameState.*;
import org.game.cell.*;
import org.game.construction.*;
import org.game.department.*;
import org.network.*;
import org.screen.*;
import org.screen.layer.component.*;
import org.screen.layer.component.Button;


import android.app.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class UserInterface {
	public enum ToolbarTypes {HIDDEN, TOOLBAR, TOOLBAR_CONSTRUCTION};
	public enum ToolbarButtons {NOTHING, CONSTRUCTION, REPORT, RESTART, TIME};
	public enum ToolbarConstructionTypes {NOTHING, RETAIL, FACTORY, FARM, RnD};
	
	private Radio mCityiconRadio;
	
	public static boolean m_is_toolbar_hidden = false;
	
	public ToolbarTypes toolbartype; 
	public static Bitmap bottom;
	public static Bitmap toolbar;
	public static Bitmap toolbar_construction;
	
	public static Bitmap m_general_info_bitmap;
	
	public static Bitmap m_netprofit_popup_info_bitmap;
	public static Bitmap m_choice_popup_info_bitmap;
	public static Bitmap m_choice_popup_yesno_info_bitmap;
	public static Bitmap m_choice_popup_no_info_bitmap;
	
	public static Bitmap m_citiyicon_normal_up_bitmap;
	public static Bitmap m_citiyicon_landvalue_up_bitmap;
	public static Bitmap m_citiyicon_profit_up_bitmap;
	public static Bitmap m_citiyicon_transaction_up_bitmap;
	
	public static Bitmap m_citiyicon_normal_down_bitmap;
	public static Bitmap m_citiyicon_landvalue_down_bitmap;
	public static Bitmap m_citiyicon_profit_down_bitmap;
	public static Bitmap m_citiyicon_transaction_down_bitmap;
	
	public Rect m_cityicon_normal_rect;
	public Rect m_cityicon_landvalue_rect;
	public Rect m_cityicon_profit_rect;
	public Rect m_cityicon_transaction_rect;
	
	public Rect toolbar_construction_rect;
	public Rect toolbar_report_rect;
	public Rect toolbar_restart_rect;
	public Rect toolbar_time_rect;
	
	public ToolbarConstructionTypes constructiontype;
	public Rect construction_retail_rect;
	public Rect construction_factory_rect;
	public Rect construction_farm_rect;
	public Rect construction_RnD_rect;
	public Rect construction_return_rect;
	
	public static Rect construction_yes_rect;
	public static Rect construction_no_rect;
	
	public static final int MONEY_X = 350/2;
	public static final int MONEY_Y = 781/2;
	
	public static final int NETPROFIT_X = 650/2;
	public static final int NETPROFIT_Y = 781/2;
	
	public static final int CALENDAR_X = 1115/2;
	public static final int CALENDAR_Y = 781/2;
	
	public static final int CONSTRUCTION_X = 10*2/2;
	public static final int CONSTRUCTION_Y = 12*2/2;
	public static final int CONSTRUCTION_WIDTH = 59*2/2;
	public static final int CONSTRUCTION_HEIGHT = 40*2/2;
	
	public static final int CONSTRUCTION_LIST_X = 22*2/2;
	public static final int CONSTRUCTION_LIST_Y = 26*2/2;
	
	public static final int CONSTRUCTION_RETAIL_X = 13*2/2;
	public static final int CONSTRUCTION_RETAIL_Y = 39*2/2;
	public static final int CONSTRUCTION_RETAIL_WIDTH = 93*2/2;
	public static final int CONSTRUCTION_RETAIL_HEIGHT = 60*2/2;
	
	public static final int CONSTRUCTION_FACTORY_X = 13*2/2;
	public static final int CONSTRUCTION_FACTORY_Y = 62*2/2;
	public static final int CONSTRUCTION_FACTORY_WIDTH = 93*2/2;
	public static final int CONSTRUCTION_FACTORY_HEIGHT = 83*2/2;
	
	public static final int CONSTRUCTION_FARM_X = 13*2/2;
	public static final int CONSTRUCTION_FARM_Y = 85*2/2;
	public static final int CONSTRUCTION_FARM_WIDTH = 93*2/2;
	public static final int CONSTRUCTION_FARM_HEIGHT = 106*2/2;
	
	public static final int CONSTRUCTION_LABORATORY_X = 13*2/2;
	public static final int CONSTRUCTION_LABORATORY_Y = 108*2/2;
	public static final int CONSTRUCTION_LABORATORY_WIDTH = 93*2/2;
	public static final int CONSTRUCTION_LABORATORY_HEIGHT = 129*2/2;
	
	public static final int CONSTRUCTION_RETURN_X = 13*2/2;
	public static final int CONSTRUCTION_RETURN_Y = 152*2/2;
	public static final int CONSTRUCTION_RETURN_WIDTH = 93*2/2;
	public static final int CONSTRUCTION_RETURN_HEIGHT = 174*2/2;
	
	public static final int CONSTRUCTION_YES_LEFT = 0 + 5*2/2;
	public static final int CONSTRUCTION_YES_TOP = 430/2 + 91*2/2;
	public static final int CONSTRUCTION_YES_RIGHT = 0 + 70*2/2;
	public static final int CONSTRUCTION_YES_BOTTOM = 430/2 + 115*2/2;
	
	public static final int CONSTRUCTION_NO_X = 0 + 90*2/2;
	public static final int CONSTRUCTION_NO_Y = 430/2 + 91*2/2;
	public static final int CONSTRUCTION_NO_WIDTH = 0 + 155*2/2;
	public static final int CONSTRUCTION_NO_HEIGHT = 430/2 + 115*2/2;
	
	public static final int REPORT_X = 10*2/2;
	public static final int REPORT_Y = 100*2/2;
	public static final int REPORT_WIDTH = 59*2/2;
	public static final int REPORT_HEIGHT = 180*2/2;
	
	public static final int RESTART_X = 10*2/2;
	public static final int RESTART_Y = 182*2/2;
	public static final int RESTART_WIDTH = 59*2/2;
	public static final int RESTART_HEIGHT = 214*2/2;
	
	public static final int TIME_X = 10*2/2;
	public static final int TIME_Y = 240*2/2;
	public static final int TIME_WIDTH = 59*2/2;
	public static final int TIME_HEIGHT = 284*2/2;

	public UserInterface()
	{
		//bottom = new GraphicObject(AppManager.getInstance().getBitmap(R.drawable.ui_bottom));

		//bottom.m_y = AppManager.getInstance().getResources().getDisplayMetrics().heightPixels - bottom.m_bitmap.getHeight();
		
		//toolbar = new GraphicObject(AppManager.getInstance().getBitmap(R.drawable.ui_toolbar));
		
		//toolbar_construction = new GraphicObject(AppManager.getInstance().getBitmap(R.drawable.ui_toolbar_construction1));
		
		//m_general_info_bitmap = AppManager.getInstance().getBitmap(R.drawable.ui_general_info);
		
		toolbartype = ToolbarTypes.TOOLBAR;
		
		construction_yes_rect = new Rect(CONSTRUCTION_YES_LEFT, CONSTRUCTION_YES_TOP,
				CONSTRUCTION_YES_RIGHT, CONSTRUCTION_YES_BOTTOM);
		construction_no_rect = new Rect(CONSTRUCTION_NO_X, CONSTRUCTION_NO_Y,
				CONSTRUCTION_NO_WIDTH, CONSTRUCTION_NO_HEIGHT);
		
		toolbar_construction_rect = new Rect(CONSTRUCTION_X, CONSTRUCTION_Y, CONSTRUCTION_WIDTH, CONSTRUCTION_HEIGHT);
		toolbar_report_rect = new Rect(REPORT_X, REPORT_Y, REPORT_WIDTH, REPORT_HEIGHT);
		toolbar_restart_rect = new Rect(RESTART_X, RESTART_Y, RESTART_WIDTH, RESTART_HEIGHT);
		toolbar_time_rect = new Rect(TIME_X, TIME_Y, TIME_WIDTH, TIME_HEIGHT);
		
		construction_retail_rect = new Rect(CONSTRUCTION_RETAIL_X, CONSTRUCTION_RETAIL_Y,
				CONSTRUCTION_RETAIL_WIDTH, CONSTRUCTION_RETAIL_HEIGHT);
		construction_factory_rect = new Rect(CONSTRUCTION_FACTORY_X, CONSTRUCTION_FACTORY_Y,
				CONSTRUCTION_FACTORY_WIDTH, CONSTRUCTION_FACTORY_HEIGHT);
		construction_farm_rect = new Rect(CONSTRUCTION_FARM_X, CONSTRUCTION_FARM_Y, 
				CONSTRUCTION_FARM_WIDTH, CONSTRUCTION_FARM_HEIGHT);
		construction_RnD_rect = new Rect(CONSTRUCTION_LABORATORY_X, CONSTRUCTION_LABORATORY_Y, 
				CONSTRUCTION_LABORATORY_WIDTH, CONSTRUCTION_LABORATORY_HEIGHT);
		construction_return_rect = new Rect(CONSTRUCTION_RETURN_X, CONSTRUCTION_RETURN_Y,
				CONSTRUCTION_RETURN_WIDTH, CONSTRUCTION_RETURN_HEIGHT);
		
		bottom = AppManager.getInstance().getBitmap(R.drawable.ui_bottom, Utility.sOptions);
		toolbar = AppManager.getInstance().getBitmap(R.drawable.ui_toolbar, Utility.sOptions);
		toolbar_construction = AppManager.getInstance().getBitmap(R.drawable.ui_toolbar_construction1, Utility.sOptions);
		
		m_general_info_bitmap = AppManager.getInstance().getBitmap(R.drawable.ui_general_info, Utility.sOptions);
		
		m_netprofit_popup_info_bitmap = AppManager.getInstance().getBitmap(R.drawable.ui_construction_netprofit_popup_info, Utility.sOptions);
		m_choice_popup_info_bitmap = AppManager.getInstance().getBitmap(R.drawable.ui_choice_popup_info, Utility.sOptions);
		m_choice_popup_yesno_info_bitmap = AppManager.getInstance().getBitmap(R.drawable.ui_choice_popup_yesno_info, Utility.sOptions);
		m_choice_popup_no_info_bitmap = AppManager.getInstance().getBitmap(R.drawable.ui_choice_popup_no_info, Utility.sOptions);
		
		m_citiyicon_normal_up_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_normal_up, Utility.sOptions);
		m_citiyicon_landvalue_up_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_landvalue_up, Utility.sOptions);
		m_citiyicon_profit_up_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_profit_up, Utility.sOptions);
		m_citiyicon_transaction_up_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_transaction_up, Utility.sOptions);
		
		m_citiyicon_normal_down_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_normal_down, Utility.sOptions);
		m_citiyicon_landvalue_down_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_landvalue_down, Utility.sOptions);
		m_citiyicon_profit_down_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_profit_down, Utility.sOptions);
		m_citiyicon_transaction_down_bitmap = AppManager.getInstance().getBitmap(R.drawable.cityicon_transaction_down, Utility.sOptions);
		
		Button normal = new Button(new Point(510/2, 10/2), R.drawable.cityicon_normal_up, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.cityicon_normal_down)
				.setPushedBitmap(R.drawable.cityicon_normal_down);
		
		Button landvalue = new Button(new Point(600/2, 10/2), R.drawable.cityicon_landvalue_up, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.cityicon_landvalue_down)
				.setPushedBitmap(R.drawable.cityicon_landvalue_down);
		
		Button profit = new Button(new Point(690/2, 10/2), R.drawable.cityicon_profit_up, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.cityicon_profit_down)
				.setPushedBitmap(R.drawable.cityicon_profit_down);
		
		Button transaction = new Button(new Point(780/2, 10/2), R.drawable.cityicon_transaction_up, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.cityicon_transaction_down)
				.setPushedBitmap(R.drawable.cityicon_transaction_down);
		
		mCityiconRadio = new Radio()
				.Add(normal)
				.Add(landvalue)
				.Add(profit)
				.Add(transaction)
				.setSelectIndex(0);
		
		constructiontype = ToolbarConstructionTypes.NOTHING;
	}
	
	public static void InitStatic()
	{
		
	}
	
	public void Render(Canvas canvas)
	{
		GameState gamestate = (GameState) AppManager.getInstance().m_state;
		
		canvas.drawBitmap(bottom, 0, 362, null);
		
		Paint p = new Paint();
    	p.setTextSize(15);
    	
    	p.setColor(Color.WHITE); 
    	canvas.drawText("현금 : ", MONEY_X, MONEY_Y, p);
    	if(gamestate.m_player.m_money >= 0)
    	{
    		p.setColor(Color.GREEN);
    		canvas.drawText(gamestate.m_player.m_money/10000+"(만)", MONEY_X + 40, MONEY_Y, p);
    	}
    	else
    	{
    		p.setColor(Color.RED);
    		canvas.drawText("("+-gamestate.m_player.m_money/10000+"(만))", MONEY_X + 40, MONEY_Y, p);
    	}
    	
    	p.setColor(Color.WHITE); 
    	canvas.drawText("순이익 : ", NETPROFIT_X, NETPROFIT_Y, p);
    	if(gamestate.m_player.m_annual_netprofit >= 0)
    	{
    		p.setColor(Color.GREEN);
    		canvas.drawText(gamestate.m_player.m_annual_netprofit/10000+"(만)", NETPROFIT_X + 55, NETPROFIT_Y, p);
    	}
    	else
    	{
    		p.setColor(Color.RED);
    		canvas.drawText("("+-gamestate.m_player.m_annual_netprofit/10000+"(만))", NETPROFIT_X + 55, NETPROFIT_Y, p);
    	}
    	
    	// 날짜 표시
    	p.setColor(Color.BLACK);
    	int month = Time.GetInstance().GetCalendar().get(Calendar.MONTH);
    	int day = Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH);
    	canvas.drawText(String.format("%2d/ %2d", month+1, day), CALENDAR_X, CALENDAR_Y, p);
    	
    	p.setColor(Color.RED);
    	int year = Time.GetInstance().GetCalendar().get(Calendar.YEAR);
    	canvas.drawText(year+"", CALENDAR_X + 45, CALENDAR_Y, p);
    	
    	if(gamestate.mGameScreenType == GameScreenTypes.NORMAL)
    	{
    		if(m_is_toolbar_hidden == false)
    		{
		    	if(toolbartype == ToolbarTypes.TOOLBAR)
		    	{
		    		if(GameState.selection == 0 || GameState.selection2 == 0)
				    	canvas.drawBitmap(toolbar, 0, 0, null);
		    	}
		    	else
		    	{
		    		canvas.drawBitmap(toolbar_construction, 0, 0, null);
		    		
		    		p.setColor(Color.BLACK); 
		        	canvas.drawText("건물 유형", CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y, p);
		        	canvas.drawText(Retail.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+30, p);
		        	canvas.drawText(Factory.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+53, p);
		        	canvas.drawText(Farm.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+76, p);
		        	canvas.drawText(RnD.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+99, p);
		        	
		        	canvas.drawText("돌아가기", CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+145, p);
		    	}
    		}
	    	
    		// 모드 아이콘 표시
    		mCityiconRadio.Render(canvas);
    		
    		/*
    		if(CellManager.m_citiyicon_selection == 0)
    			canvas.drawBitmap(m_citiyicon_normal_down_bitmap, 255, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_normal_up_bitmap, 255, 5, null);
    		
    		if(CellManager.m_citiyicon_selection == 1)
    			canvas.drawBitmap(m_citiyicon_landvalue_down_bitmap,300, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_landvalue_up_bitmap, 300, 5, null);
    		
    		if(CellManager.m_citiyicon_selection == 2)
    			canvas.drawBitmap(m_citiyicon_profit_down_bitmap, 345, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_profit_up_bitmap, 345, 5, null);
    		
    		if(CellManager.m_citiyicon_selection == 3)
    			canvas.drawBitmap(m_citiyicon_transaction_down_bitmap, 390, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_transaction_up_bitmap, 390, 5, null);*/

	    
	    	if(Player.m_connection_destination_construction != null)
	    	{
	    		if(Player.m_connection_source_construction != null)
	    			canvas.drawBitmap(UserInterface.m_choice_popup_yesno_info_bitmap, 0, 430/2, null);
	    		else
	    			canvas.drawBitmap(UserInterface.m_choice_popup_no_info_bitmap, 0, 430/2, null);
	    		
		    	p.setTextSize(15); 
		    	p.setColor(Color.WHITE);
		    	
	    		canvas.drawText("거래 연결설정", 30/2, 470/2, p);

	    		if(Player.m_connection_source_construction != null)
	    			canvas.drawText("연결", 30/2, 650/2, p);
	    		canvas.drawText("취소", 200/2, 650/2, p);
	    	}
	    	
    	}

    	
	}
	
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher)
	{
		GameState gamestate = (GameState) AppManager.getInstance().m_state;
		
		spriteBatcher.drawBitmap2(R.drawable.ui_bottom, 0, 362, null);
		
		Paint p = new Paint();
    	p.setTextSize(15);
    	
    	p.setColor(Color.WHITE); 
    	spriteBatcher.drawText("현금 : ", MONEY_X, MONEY_Y, p);
    	if(gamestate.m_player.m_money >= 0)
    	{
    		p.setColor(Color.GREEN);
    		spriteBatcher.drawText(gamestate.m_player.m_money/10000+"(만)", MONEY_X + 40, MONEY_Y, p);
    	}
    	else
    	{
    		p.setColor(Color.RED);
    		spriteBatcher.drawText("("+-gamestate.m_player.m_money/10000+"(만))", MONEY_X + 40, MONEY_Y, p);
    	}
    	
    	p.setColor(Color.WHITE); 
    	spriteBatcher.drawText("순이익 : ", NETPROFIT_X, NETPROFIT_Y, p);
    	if(gamestate.m_player.m_annual_netprofit >= 0)
    	{
    		p.setColor(Color.GREEN);
    		spriteBatcher.drawText(gamestate.m_player.m_annual_netprofit/10000+"(만)", NETPROFIT_X + 55, NETPROFIT_Y, p);
    	}
    	else
    	{
    		p.setColor(Color.RED);
    		spriteBatcher.drawText("("+-gamestate.m_player.m_annual_netprofit/10000+"(만))", NETPROFIT_X + 55, NETPROFIT_Y, p);
    	}
    	
    	// 날짜 표시
    	p.setColor(Color.BLACK);
    	int month = Time.GetInstance().GetCalendar().get(Calendar.MONTH);
    	int day = Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH);
    	spriteBatcher.drawText(String.format("%2d/ %2d", month+1, day), CALENDAR_X, CALENDAR_Y, p);
    	
    	p.setColor(Color.RED);
    	int year = Time.GetInstance().GetCalendar().get(Calendar.YEAR);
    	spriteBatcher.drawText(year+"", CALENDAR_X + 45, CALENDAR_Y, p);
    	
    	if(gamestate.mGameScreenType == GameScreenTypes.NORMAL)
    	{
    		if(m_is_toolbar_hidden == false)
    		{
		    	if(toolbartype == ToolbarTypes.TOOLBAR)
		    	{
		    		if(GameState.selection == 0 || GameState.selection2 == 0)
		    			spriteBatcher.drawBitmap2(R.drawable.ui_toolbar, 0, 0, null);
		    	}
		    	else
		    	{
		    		spriteBatcher.drawBitmap2(R.drawable.ui_toolbar_construction1, 0, 0, null);
		    		
		    		p.setColor(Color.BLACK); 
		    		spriteBatcher.drawText("건물 유형", CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y, p);
		    		spriteBatcher.drawText(Retail.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+30, p);
		    		spriteBatcher.drawText(Factory.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+53, p);
		    		spriteBatcher.drawText(Farm.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+76, p);
		    		spriteBatcher.drawText(RnD.m_name, CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+99, p);
		        	
		    		spriteBatcher.drawText("돌아가기", CONSTRUCTION_LIST_X, CONSTRUCTION_LIST_Y+145, p);
		    	}
    		}
	    	
    		// 모드 아이콘 표시
    		mCityiconRadio.onDrawFrame(gl, spriteBatcher);
    		
    		/*
    		if(CellManager.m_citiyicon_selection == 0)
    			canvas.drawBitmap(m_citiyicon_normal_down_bitmap, 255, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_normal_up_bitmap, 255, 5, null);
    		
    		if(CellManager.m_citiyicon_selection == 1)
    			canvas.drawBitmap(m_citiyicon_landvalue_down_bitmap,300, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_landvalue_up_bitmap, 300, 5, null);
    		
    		if(CellManager.m_citiyicon_selection == 2)
    			canvas.drawBitmap(m_citiyicon_profit_down_bitmap, 345, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_profit_up_bitmap, 345, 5, null);
    		
    		if(CellManager.m_citiyicon_selection == 3)
    			canvas.drawBitmap(m_citiyicon_transaction_down_bitmap, 390, 5, null);
    		else
    			canvas.drawBitmap(m_citiyicon_transaction_up_bitmap, 390, 5, null);*/

	    
	    	if(Player.m_connection_destination_construction != null)
	    	{
	    		if(Player.m_connection_source_construction != null)
	    			spriteBatcher.drawBitmap(R.drawable.ui_choice_popup_yesno_info, 0, 430/2, null);
	    		else
	    			spriteBatcher.drawBitmap(R.drawable.ui_choice_popup_no_info, 0, 430/2, null);
	    		
		    	p.setTextSize(15); 
		    	p.setColor(Color.WHITE);
		    	
		    	spriteBatcher.drawText("거래 연결설정", 30/2, 470/2, p);

	    		if(Player.m_connection_source_construction != null)
	    			spriteBatcher.drawText("연결", 30/2, 650/2, p);
	    		spriteBatcher.drawText("취소", 200/2, 650/2, p);
	    	}
	    	
    	}
	}
	
	public boolean onTouchEvent(MotionEvent event) 
	{
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		/*
		if(m_cityicon_normal_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			CellManager.m_citiyicon_selection = 0;
			Utility.ShowToast("도시 모드", Toast.LENGTH_SHORT);
		}
		if(m_cityicon_landvalue_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			CellManager.m_citiyicon_selection = 1;
			Utility.ShowToast("지가 모드", Toast.LENGTH_SHORT);
		}
		if(m_cityicon_profit_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			CellManager.m_citiyicon_selection = 2;
			Utility.ShowToast("수익 모드", Toast.LENGTH_SHORT);
		}
		if(m_cityicon_transaction_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			CellManager.m_citiyicon_selection = 3;
			Utility.ShowToast("거래 모드", Toast.LENGTH_SHORT);
		}*/
		
		if(mCityiconRadio.onTouchEvent(event))
		{
			if(mCityiconRadio.getSelectIndex() == 0) 
			{
				CellManager.m_citiyicon_selection = 0;
				Utility.ShowToast("도시 모드", Toast.LENGTH_SHORT);
			}
			
			if(mCityiconRadio.getSelectIndex() == 1) 
			{
				CellManager.m_citiyicon_selection = 1;
				Utility.ShowToast("지가 모드", Toast.LENGTH_SHORT);
			}
			
			if(mCityiconRadio.getSelectIndex() == 2) 
			{
				CellManager.m_citiyicon_selection = 2;
				Utility.ShowToast("수익 모드", Toast.LENGTH_SHORT);
			}
			
			if(mCityiconRadio.getSelectIndex() == 3) 
			{
				CellManager.m_citiyicon_selection = 3;
				Utility.ShowToast("거래 모드", Toast.LENGTH_SHORT);
			}
			
			return true;
		}
		
		if(event.getSource() != 100)
			return false;
		
		if(Player.m_connection_destination_construction != null)
		{
			if(construction_no_rect.contains(x, y))
			{
				Utility.m_click_sound.start();
				Player.m_connection_destination_construction = null;
				Player.m_connection_destination_department_index = -1;
				Player.m_connection_source_construction = null;
				UserInterface.m_is_toolbar_hidden = false;
				
				return true;
			}
			
			if(Player.m_connection_source_construction != null)
				if(construction_yes_rect.contains(x, y))
				{
					Utility.m_click_sound.start();
					Player.m_connection_source_construction.ShowAvailableConnection(Player.m_connection_destination_construction.m_constructiontype);
					
					return true;
				}
		}

		return false;
	}
	
	public void AddCityIcon(int resource_id)
	{
		
	}
	
	public ToolbarButtons ButtonClick(MotionEvent event)
	{
		if(event.getSource() != 100)
			return null;
		
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		if(m_is_toolbar_hidden == false)
			if(toolbartype == ToolbarTypes.TOOLBAR)
			{
				if(toolbar_construction_rect.contains(x, y))
				{
					Utility.m_click_sound.start();
					toolbartype = ToolbarTypes.TOOLBAR_CONSTRUCTION;
					return ToolbarButtons.CONSTRUCTION;
				}
				
				if(toolbar_report_rect.contains(x, y))
				{
					Utility.m_click_sound.start();
					GameState.mGameScreenType = GameScreenTypes.REPORT;
					return ToolbarButtons.REPORT;
				}
				
				if(toolbar_restart_rect.contains(x, y))
				{
					Utility.m_click_sound.start();
					new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
				    .setTitle("메뉴로 돌아가겠습니까?")
				    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							GameState.m_is_restart = true;
						}
					})
				    .setNegativeButton("취소", null)
				    .show();
					
					//AppManager.getInstance().getGameView().ChangeState(new MenuState());
					return ToolbarButtons.RESTART;
				}
				
				// 서버에 연결하기
				if(new Rect(5, 46, 61, 98).contains(x, y))
				{
					((GameActivity)AppManager.getInstance().getActivity()).Connect();
				}
				
				// 서버 생성하기
				if(new Rect(5, 215, 61, 235).contains(x, y))
				{
					((GameActivity)AppManager.getInstance().getActivity()).createSocket();
					((GameActivity)AppManager.getInstance().getActivity()).getLocalIpAddr();
				}
	
				if(toolbar_time_rect.contains(x, y))
				{
					Utility.m_click_sound.start();
					return ToolbarButtons.TIME;
				}
			}
		
		
		return ToolbarButtons.NOTHING;
		//if(InRect(p, rect_toolbar) == true)
		//	Toast.makeText(MainActivity.this, "툴바 선택", Toast.LENGTH_SHORT).show();
	}
	
	public ToolbarConstructionTypes ConstructionClick(MotionEvent event)
	{
		if(event.getSource() != 100)
			return null;
		
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		if(construction_return_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			constructiontype = ToolbarConstructionTypes.NOTHING;
			toolbartype = ToolbarTypes.TOOLBAR;
			//CellSelector.mCurrentBitmapID = null;
			
			return ToolbarConstructionTypes.NOTHING;
		}
		
		//if(constructiontype != ToolbarConstructionTypes.NOTHING)
		//	return null;
		
		if(toolbartype == ToolbarTypes.TOOLBAR_CONSTRUCTION)
		{
			if(construction_retail_rect.contains(x, y))
			{
				Utility.m_click_sound.start();
				constructiontype = ToolbarConstructionTypes.RETAIL;
				return ToolbarConstructionTypes.RETAIL;
			}
			
			if(construction_factory_rect.contains(x, y))
			{
				Utility.m_click_sound.start();
				constructiontype = ToolbarConstructionTypes.FACTORY;
				return ToolbarConstructionTypes.FACTORY;
			}
			
			if(construction_farm_rect.contains(x, y))
			{
				Utility.m_click_sound.start();
				constructiontype = ToolbarConstructionTypes.FARM;
				return ToolbarConstructionTypes.FARM;
			}
			
			if(construction_RnD_rect.contains(x, y))
			{
				Utility.m_click_sound.start();
				constructiontype = ToolbarConstructionTypes.RnD;
				return ToolbarConstructionTypes.RnD;
			}
			
		}
		
		//constructiontype = ToolbarConstructionTypes.NOTHING;
		//return ToolbarConstructionTypes.NOTHING;
		return null;
		//if(InRect(p, rect_toolbar) == true)
		//	Toast.makeText(MainActivity.this, "툴바 선택", Toast.LENGTH_SHORT).show();
	}
	
}
