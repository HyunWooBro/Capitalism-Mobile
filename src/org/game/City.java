package org.game;

import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.framework.*;
import org.framework.openGL.*;
import org.game.cell.*;
import org.game.commodity.*;
import org.game.construction.*;
import org.screen.*;

import android.graphics.*;

public class City {
		
	public int m_index;
	public String m_name;
	public int m_population;
	
	public final int CITY_X = 1020/2; 
	public final int CITY_Y = 35/2;
	
	// 경제 지표
	public static final int EI_PANIC = 0;					// 공황
	public static final int EI_DEPRESSION = 20;		// 침체
	public static final int EI_RECESSION = 40;			// 후퇴
	public static final int EI_NORMAL = 50;				// 표준
	public static final int EI_RECOVERY = 60;			// 회복
	public static final int EI_BOOM = 80;					// 호황
	
	/*
	public enum ECONOMIC_INDICATOR 
	{
		// 	공황				침체					후퇴					표준					회복				호황
		EI_PANIC(0), EI_DEPRESSION(20), EI_RECESSION(40), EI_NORMAL(50), EI_RECOVERY(60), EI_BOOM(80);
		ECONOMIC_INDICATOR(int value) { m_value = value; }
		public int GetValue() { return m_value; }
		private int m_value;	// 0 ~ 100
	}*/
	
	public static int m_economic_indicator;	// 경제 지표 0 ~ 100
	public static int m_spending_level;			// 소비 레벨 0 ~ 100
	public static int m_salary_level;				// 봉급 레벨 0 ~ 100
	// 등등
	
	// 각 상품에 대한 지역 상인들의 브랜드, 가격, 품질 등에 관한 평균 정보 추가
	
	
	// 항구, AI, 유저가 등록한 모든 품목을 등록한다.
	ArrayList<Commodity> m_commodity_list = new ArrayList<Commodity>();
	
	// 항구
	public static ArrayList<Port> m_portlist = new ArrayList<Port>();
	
	public static double m_rate_interest;		// 이자율 
	
	
	public City(String name, int population, int economic_indicator, int spending_level, int salary_level)
	{
		m_name = name;
		m_population = population;
		m_economic_indicator = economic_indicator;
		m_spending_level = spending_level;
		m_salary_level = salary_level;
		
		m_rate_interest = 10.0;

	}
	
	public static void InitStatic()
	{
		m_portlist = new ArrayList<Port>();
	}

	public void Render(Canvas canvas)
	{
		String economic_indicator;
		
		canvas.drawBitmap(UserInterface.m_general_info_bitmap, 500, 0, null);
		
		Paint p = new Paint();
    	p.setTextSize(15); p.setColor(Color.WHITE);
    	
    	canvas.drawText("도시명 : " +m_name, CITY_X, CITY_Y, p);
    	canvas.drawText("인구 : " + m_population, CITY_X, CITY_Y + 15, p);
    	if(m_economic_indicator  >= EI_BOOM)
    		economic_indicator = "호황";
    	else if(m_economic_indicator  >= EI_RECOVERY)
    		economic_indicator = "회복";
    	else if(m_economic_indicator  >= EI_NORMAL)
    		economic_indicator = "표준";
    	else if(m_economic_indicator  >= EI_RECESSION)
    		economic_indicator = "후퇴";
    	else if(m_economic_indicator  >= EI_DEPRESSION)
    		economic_indicator = "침체";
    	else
    		economic_indicator = "공황";
    	canvas.drawText("경제 지표 : " + economic_indicator, CITY_X, CITY_Y + 30, p);
    	canvas.drawText("소비 레벨 : " + m_spending_level, CITY_X, CITY_Y + 45, p);
    	canvas.drawText("봉급 레벨 : " + m_salary_level, CITY_X, CITY_Y + 60, p);
	}
	
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		String economic_indicator;
		
		spriteBatcher.drawBitmap2(R.drawable.ui_general_info, 500, 0, null);
		
		Paint p = new Paint();
    	p.setTextSize(15); p.setColor(Color.WHITE);
    	
    	spriteBatcher.drawText("도시명 : " +m_name, CITY_X, CITY_Y, p);
    	spriteBatcher.drawText("인구 : " + m_population, CITY_X, CITY_Y + 15, p);
    	if(m_economic_indicator  >= EI_BOOM)
    		economic_indicator = "호황";
    	else if(m_economic_indicator  >= EI_RECOVERY)
    		economic_indicator = "회복";
    	else if(m_economic_indicator  >= EI_NORMAL)
    		economic_indicator = "표준";
    	else if(m_economic_indicator  >= EI_RECESSION)
    		economic_indicator = "후퇴";
    	else if(m_economic_indicator  >= EI_DEPRESSION)
    		economic_indicator = "침체";
    	else
    		economic_indicator = "공황";
    	spriteBatcher.drawText("경제 지표 : " + economic_indicator, CITY_X, CITY_Y + 30, p);
    	spriteBatcher.drawText("소비 레벨 : " + m_spending_level, CITY_X, CITY_Y + 45, p);
    	spriteBatcher.drawText("봉급 레벨 : " + m_salary_level, CITY_X, CITY_Y + 60, p);
	}
}
