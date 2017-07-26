package org.game;

import java.util.*;

import org.framework.*;
import org.game.GameState.*;
import org.game.cell.*;
import org.game.construction.*;
import org.game.construction.Construction.*;
import org.game.department.*;
import org.game.department.Department.*;
import org.screen.*;

import android.graphics.*;
import android.graphics.Paint.*;
import android.opengl.*;
import android.view.*;
import android.widget.*;

public class Report {
	public static Bitmap m_bitmap;
	public static Bitmap m_selector_bitmap;
	
	public static Bitmap m_financial_dealings_loan_bitmap;
	public static Bitmap m_financial_dealings_repay_bitmap;
	
	public int m_report_type;
	private final int NUM_REPORT_TYPE = 6;
	
	public static Rect m_return_to_map_rect;
	
	public static final int RETURN_TO_MAP_LEFT = 0*2/2;
	public static final int RETURN_TO_MAP_TOP = 261*2/2;
	public static final int RETURN_TO_MAP_RIGHT = 148*2/2;
	public static final int RETURN_TO_MAP_BOTTOM = 284*2/2;
	
	private final int REPORT_LIST_X = 50/2;
	private final int REPORT_LIST_Y = 50/2;
	
	private final int REPORT_MAIN_X = 154*2/2;
	private final int REPORT_MAIN_Y = 50/2;
	
	private long m_lone;
	private long m_repay;
	
	// 싱글턴
	private static Report s_instance;
	private Report() {}
	public static Report GetInstance(){
		if(s_instance == null){
			s_instance = new Report();
		}
		return s_instance;
	}
	
	public static void Destroy()
	{
		s_instance = null;
	}
	
	public static void InitStatic()
	{
		m_bitmap = AppManager.getInstance().getBitmap(R.drawable.report_info_basic, Utility.sOptions);
		m_selector_bitmap = AppManager.getInstance().getBitmap(R.drawable.report_selector, Utility.sOptions);
		
		m_financial_dealings_loan_bitmap = AppManager.getInstance().getBitmap(R.drawable.financial_dealings_loan, Utility.sOptions);
		m_financial_dealings_repay_bitmap = AppManager.getInstance().getBitmap(R.drawable.financial_dealings_repay, Utility.sOptions);
	}
	
	public void Init()
	{
		//sBackgroundBitmap = AppManager.getInstance().getBitmap(R.drawable.report_info_basic);
		//m_selector_bitmap = AppManager.getInstance().getBitmap(R.drawable.report_selector);
		
		m_return_to_map_rect = new Rect(RETURN_TO_MAP_LEFT, RETURN_TO_MAP_TOP,
				RETURN_TO_MAP_RIGHT, RETURN_TO_MAP_BOTTOM);
		
		m_report_type = 0;
		
		m_lone = 0;
		m_repay = 0;
	}
	
	public void Render(Canvas canvas)
	{
		int item_gap = 35;
		int rect_width = 10;
		int rect_height = 10;
		int rect_gap = 5;
		
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setStyle(Style.FILL);
    	
		canvas.drawBitmap(m_bitmap, 0, 0, null);
		
		canvas.save();
		canvas.translate(0, 0);

    	p.setColor(Color.BLUE);
		canvas.drawRect(REPORT_LIST_X-rect_gap, REPORT_LIST_Y, REPORT_LIST_X-rect_gap-rect_width, REPORT_LIST_Y-rect_height, p);
		p.setColor(Color.WHITE);
    	canvas.drawText("기업 보고서", REPORT_LIST_X, REPORT_LIST_Y, p);
    	
    	canvas.translate(0, item_gap);
    	
    	p.setColor(Color.BLUE);
		canvas.drawRect(REPORT_LIST_X-rect_gap, REPORT_LIST_Y, REPORT_LIST_X-rect_gap-rect_width, REPORT_LIST_Y-rect_height, p);
		p.setColor(Color.WHITE);
    	canvas.drawText("재무 보고서", REPORT_LIST_X, REPORT_LIST_Y, p);
    	
    	canvas.translate(0, item_gap);
    	
    	p.setColor(Color.BLUE);
		canvas.drawRect(REPORT_LIST_X-rect_gap, REPORT_LIST_Y, REPORT_LIST_X-rect_gap-rect_width, REPORT_LIST_Y-rect_height, p);
		p.setColor(Color.WHITE);
    	canvas.drawText("사원 보고서", REPORT_LIST_X, REPORT_LIST_Y, p);
    	
    	canvas.translate(0, item_gap);
    	
    	p.setColor(Color.MAGENTA);
		canvas.drawRect(REPORT_LIST_X-rect_gap, REPORT_LIST_Y, REPORT_LIST_X-rect_gap-rect_width, REPORT_LIST_Y-rect_height, p);
		p.setColor(Color.WHITE);
    	canvas.drawText("목표 보고서", REPORT_LIST_X, REPORT_LIST_Y, p);
    	
    	canvas.translate(0, item_gap);
    	
    	p.setColor(Color.GREEN);
		canvas.drawRect(REPORT_LIST_X-rect_gap, REPORT_LIST_Y, REPORT_LIST_X-rect_gap-rect_width, REPORT_LIST_Y-rect_height, p);
		p.setColor(Color.WHITE);
    	canvas.drawText("제조업 가이드", REPORT_LIST_X, REPORT_LIST_Y, p);
    	
    	canvas.translate(0, item_gap);
    	
    	p.setColor(Color.RED);
		canvas.drawRect(REPORT_LIST_X-rect_gap, REPORT_LIST_Y, REPORT_LIST_X-rect_gap-rect_width, REPORT_LIST_Y-rect_height, p);
		p.setColor(Color.WHITE);
    	canvas.drawText("금융 거래", REPORT_LIST_X, REPORT_LIST_Y, p);
    	
    	canvas.restore();

    	
    	canvas.drawBitmap(m_selector_bitmap, REPORT_LIST_X-45/2, REPORT_LIST_Y-35/2+item_gap*m_report_type, null);
    	
    	
    	if(m_report_type == 0)
    		RenderCorporationReports(canvas);	// 기업 보고서
    	else if(m_report_type == 1)	
    		RenderFinancialReports(canvas);		// 재무 보고서		
    	else if(m_report_type == 2)
    		RenderEmployeeReports(canvas);		// 사원 보고서
    	else if(m_report_type == 3)
    		RenderGoalReports(canvas);				// 목표 보고서
    	else if(m_report_type == 4)
    		RenderManufacturingGuide(canvas);	// 제조업 가이드
    	else if(m_report_type == 5)
    		RenderFinancialDealings(canvas);		// 금융 거래
	}
	
	/**
	 * 기업 보고서
	 * @param canvas
	 */
	public void RenderCorporationReports(Canvas canvas)
	{
		int num_retail = 0;
		int num_factory = 0;
		int num_farm = 0;
		int num_RnD = 0;
		int num_total_construction = 0;
    	int employee_happiness = 0;
    	int offset_row = 25;
    	long sum = 0;
    	
    	int offset_column1 = 400/2;
    	
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	
    	num_retail = Player.m_retaillist.size();
    	num_factory = Player.m_factorylist.size();
    	num_farm = Player.m_farmlist.size();
    	num_RnD = Player.m_RnDlist.size();
    	num_total_construction = num_retail + num_factory + num_farm + num_RnD;
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_sales[i];
   
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("기업 브랜드", REPORT_MAIN_X, REPORT_MAIN_Y, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(""+Player.m_brand, REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y, p);
    	
    	p.setTextAlign(Align.LEFT);
		canvas.drawText("현금", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*2, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(Player.m_money/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*2, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("연간 매출", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*3, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*3, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("연간 순이익", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*4, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(Player.m_annual_netprofit/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*4, p);
		
		p.setTextAlign(Align.LEFT);
		canvas.drawText("총 사원 수", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*6, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(Player.m_num_employee+"명", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*6, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("총 사업체 개수", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*7, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(num_total_construction+"개", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*7, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("  총 소매점 개수", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*8, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(num_retail+"개", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*8, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("  총 공장 개수", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*9, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(num_factory+"개", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*9, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("  총 농장 개수", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*10, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(num_farm+"개", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*10, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("  총 연구소 개수", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*11, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(num_RnD+"개", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*11, p);
		
		
		
		
		long height;
		long previous_height;
		long temp_height;
		int index;
		
    	p.setTextSize(30); 
    	p.setColor(Color.WHITE);

    	
    	canvas.drawBitmap(UserInterface.m_netprofit_popup_info_bitmap, 1000, 200, null);
    	
    	p.setTextAlign(Align.CENTER);
		canvas.drawText("기업 순이익", 1140, 240, p);
		canvas.drawText("지난 12개월간", 1140, 400, p);
		
		p.setStrokeWidth(5);
		canvas.drawLine(1030, 300, 1250, 300, p);
		
		p.setColor(Color.RED);
		index = (Player.m_netprofit_index+1)%12;
		previous_height = 0;
		if(Player.m_max_netprofit != 0)
		{
			for(int i=0; i<12; i++)
			{
				height = 25*Player.m_monthly_netprofit[index]/Player.m_max_netprofit*2;
				
				index = (index+1)%12;
				
				if(i == 0)
				{
					temp_height = 25*Player.m_last_netprofit/Player.m_max_netprofit*2;
					canvas.drawLine(1050+15*i, 300-temp_height, 1050+15*(i+1), 300-height, p);
				}
				else if(i == 11)
				{
					canvas.drawRect(1050+15*i, 300-height, 1050+15*(i+1), 300, p);
					//drawLine(1050+15*i, 300+previous_height, 1050+15*(i+1), 300+height, p);
					break;
				}
				else
				{
					canvas.drawLine(1050+15*i, 300-previous_height, 1050+15*(i+1), 300-height, p);
				}
				
				previous_height = height;
			}
		}
		
		p.setColor(Color.WHITE);
		p.setTextAlign(Align.LEFT);
		p.setTextSize(20);
		canvas.drawText(Player.m_max_netprofit/10000+"(만)", 1050, 270, p);
		canvas.drawText("("+Player.m_max_netprofit/10000+"(만))", 1050, 350, p);
	}
	
	/**
	 * 재무 보고서
	 * @param canvas
	 */
	public void RenderFinancialReports(Canvas canvas)
	{
		int offset_row = 25;
    	
    	int offset_column1 = 350/2;
    	int offset_column2 = 550/2;
    	int offset_column3 = 750/2;
    	int offset_column4 = 950/2;
    	
    	int current_index = Player.m_netprofit_index;
    	int previous_index;
    	
    	long sum = 0;
    	
    	previous_index = current_index - 1;
    	if(previous_index < 0)
    		previous_index = 11;
    	
    	Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	p.setStyle(Style.FILL);
    	
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText("이번달", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y, p);
    	canvas.drawText("지난달", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y, p);
    	canvas.drawText("연간", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y, p);
    	canvas.drawText("창업이래", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y, p);
    	
    	p.setTextAlign(Align.LEFT);
    	for(int i=0; i<12; i=i+2)
    	{
	    	p.setColor(Color.argb(255, 189, 207, 247));
	    	canvas.drawRect(REPORT_MAIN_X-5/2, REPORT_MAIN_Y+15/2+offset_row*i, REPORT_MAIN_X+1200/2, REPORT_MAIN_Y+15/2+offset_row*(i+1), p);
	    	p.setColor(Color.argb(255, 222, 227, 247));
	    	canvas.drawRect(REPORT_MAIN_X-5/2, REPORT_MAIN_Y+15/2+offset_row*(i+1), REPORT_MAIN_X+1200/2, REPORT_MAIN_Y+15/2+offset_row*(i+2), p);
    	}
    	p.setColor(Color.BLACK);
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_sales[i];
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("영업 매출", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*1, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_sales[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*1, p);
    	canvas.drawText(Player.m_monthly_sales[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*1, p);
    	canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*1, p);
    	canvas.drawText(Player.m_accumulated_sales/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*1, p);
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_sales_expense[i];
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("원가 비용", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*2, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_sales_expense[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*2, p);
    	canvas.drawText(Player.m_monthly_sales_expense[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*2, p);
    	canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*2, p);
    	canvas.drawText(Player.m_accumulated_sales_expense/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*2, p);
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_wage[i];
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("임금 비용", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*3, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_wage[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*3, p);
    	canvas.drawText(Player.m_monthly_wage[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*3, p);
    	canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*3, p);
    	canvas.drawText(Player.m_accumulated_wage/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*3, p);
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_maintenance[i];
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("유지비 지출", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*4, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_maintenance[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*4, p);
    	canvas.drawText(Player.m_monthly_maintenance[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*4, p);
    	canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*4, p);
    	canvas.drawText(Player.m_accumulated_maintenance/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*4, p);
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_advertise[i];
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("광고 지출", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*5, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_advertise[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*5, p);
    	canvas.drawText(Player.m_monthly_advertise[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*5, p);
    	canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*5, p);
    	canvas.drawText(Player.m_accumulated_advertise/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*5, p);
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_welfare[i];
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("사원 복지 지출", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*6, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_welfare[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*6, p);
    	canvas.drawText(Player.m_monthly_welfare[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*6, p);
    	canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*6, p);
    	canvas.drawText(Player.m_accumulated_welfare/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*6, p);
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("영업 순이익", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*7, p);
    	
    	sum = 0;
    	for(int i=0; i<12; i++)
    		sum +=  Player.m_monthly_interest[i];
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("대출 이자", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*9, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_interest[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*9, p);
    	canvas.drawText(Player.m_monthly_interest[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*9, p);
    	canvas.drawText(sum/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*9, p);
    	canvas.drawText(Player.m_accumulated_interest/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*9, p);
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("기타 순이익", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*10, p);
    	
    	p.setTextAlign(Align.LEFT);
    	canvas.drawText("총 순이익", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*12, p);
    	p.setTextAlign(Align.RIGHT);
    	canvas.drawText(Player.m_monthly_netprofit[current_index]/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*12, p);
    	canvas.drawText(Player.m_monthly_netprofit[previous_index]/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*12, p);
    	canvas.drawText(Player.m_annual_netprofit/10000+"(만)", REPORT_MAIN_X+offset_column3, REPORT_MAIN_Y+offset_row*12, p);
    	canvas.drawText(Player.m_accumulated_netprofit/10000+"(만)", REPORT_MAIN_X+offset_column4, REPORT_MAIN_Y+offset_row*12, p);
    	
/*
    	p.setTextAlign(Align.LEFT);
		canvas.drawText("총 대출금", REPORT_MAIN_X, REPORT_MAIN_Y, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(Player.m_loan/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("월간 이자", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*1, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(Player.m_loan/10000/10/12+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*1, p);
*/

	}
	
	/**
	 * 사원 보고서
	 * @param canvas
	 */
	public void RenderEmployeeReports(Canvas canvas)
	{
    	int employee_happiness = 0;
    	int offset_row = 25;
    	
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	
		canvas.drawText("총 사원 수 : "+Player.m_num_employee+"명", REPORT_MAIN_X, REPORT_MAIN_Y, p);
		canvas.drawText("전반적인 사원 만족도 : "+employee_happiness+" (1 ~ 100)", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*1, p);
		canvas.drawText("야근 유무 : "+employee_happiness, REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*2, p);
		
		canvas.drawText("준비중입니다...", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*4, p);
	}
	
	/**
	 * 목표 보고서
	 * @param canvas
	 */
	public void RenderGoalReports(Canvas canvas)
	{
    	int offset_row = 25;
    	
    	int offset_column1 = 200/2;
    	int offset_column2 = 500/2;
    	
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	
    	canvas.drawText("목표", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y, p);
    	canvas.drawText("현재", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y, p);
    	
    	// 최종기한이 설정되었다면
    	if((VictoryCondition.m_bit_condition & VictoryCondition.VC_DEADLINE) != 0)
    	{
    		canvas.drawText("날짜", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*1, p);
    		
    		p.setColor(Color.WHITE);
	    	canvas.drawText(String.format("%d년 %d월 %d일", 
	    			Time.GetInstance().GetDeadline().get(Calendar.YEAR),
	    			Time.GetInstance().GetDeadline().get(Calendar.MONTH)+1,
	    			Time.GetInstance().GetDeadline().get(Calendar.DAY_OF_MONTH)),
	    			REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*1, p);
	    	p.setColor(Color.GREEN);
	    	canvas.drawText(String.format("%d년 %d월 %d일", 
	    			Time.GetInstance().GetCalendar().get(Calendar.YEAR),
	    			Time.GetInstance().GetCalendar().get(Calendar.MONTH)+1,
	    			Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH)),
	    			REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*1, p);
    	}
    	
    	// 순이익 목표가 설정되었다면
    	if((VictoryCondition.m_bit_condition & VictoryCondition.VC_NETPROFIT) != 0)
    	{
    		p.setColor(Color.WHITE);
    		canvas.drawText("연간순이익", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*2, p);
    		
    		canvas.drawText(VictoryCondition.m_annual_netprofit/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*2, p);
    		p.setColor(Color.GREEN);
    		canvas.drawText(Player.m_annual_netprofit/10000+"(만)", REPORT_MAIN_X+offset_column2, REPORT_MAIN_Y+offset_row*2, p);
    	}
    	
    	p.setColor(Color.WHITE);
    	if(VictoryCondition.m_win == true)
    		canvas.drawText("목표달성여부 : 달성!!", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*5, p);
    	else if(VictoryCondition.m_lose == true)
    		canvas.drawText("목표달성여부 : 실패..", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*5, p);
    	else
    		canvas.drawText("목표달성여부 : 진행중", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*5, p);
	}
	
	/**
	 * 제조업 가이드
	 * @param canvas
	 */
	public void RenderManufacturingGuide(Canvas canvas)
	{
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	
    	canvas.drawText("준비중입니다...", REPORT_MAIN_X, REPORT_MAIN_Y, p);
	}
	
	/** 
	 * 금융 거래
	 * @param canvas
	 */
	public void RenderFinancialDealings(Canvas canvas)
	{
		int num_employee = 0;
    	int employee_happiness = 0;
    	int offset_row = 25;
    	
    	int offset_column1 = 350/2;
    	
    	int offset_button = 70/2;
    	
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	
    	p.setTextAlign(Align.LEFT);
		canvas.drawText("총 대출금", REPORT_MAIN_X, REPORT_MAIN_Y, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(Player.m_loan/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("월간 이자", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*1, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(Player.m_loan/10000/10/12+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*1, p);
		
		p.setTextAlign(Align.LEFT);
		canvas.drawText("신용 한도", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*3, p);
		p.setTextAlign(Align.RIGHT);
		if(Player.m_credit_limit - Player.m_loan > 0)
			canvas.drawText((Player.m_credit_limit - Player.m_loan)/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*3, p);
		else
			canvas.drawText("0(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*3, p);
		p.setTextAlign(Align.LEFT);
		canvas.drawText("연이자율", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*4, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(City.m_rate_interest+"%", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*4, p);
		
		p.setTextAlign(Align.LEFT);
		canvas.drawText("대출", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*6, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(m_lone/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*6, p);
		
		Utility.RenderSignButton(canvas, 2, +1, REPORT_MAIN_X+offset_column1+offset_button*1, REPORT_MAIN_Y-30/2+offset_row*6);
		Utility.RenderSignButton(canvas, 1, +1, REPORT_MAIN_X+offset_column1+offset_button*2, REPORT_MAIN_Y-30/2+offset_row*6);
		Utility.RenderSignButton(canvas, 1, -1, REPORT_MAIN_X+offset_column1+offset_button*3, REPORT_MAIN_Y-30/2+offset_row*6);
		Utility.RenderSignButton(canvas, 2, -1, REPORT_MAIN_X+offset_column1+offset_button*4, REPORT_MAIN_Y-30/2+offset_row*6);
		canvas.drawBitmap(m_financial_dealings_loan_bitmap, REPORT_MAIN_X+offset_column1+offset_button*5, REPORT_MAIN_Y-30/2+offset_row*6, null);
		
		p.setTextAlign(Align.LEFT);
		canvas.drawText("상환", REPORT_MAIN_X, REPORT_MAIN_Y+offset_row*8, p);
		p.setTextAlign(Align.RIGHT);
		canvas.drawText(m_repay/10000+"(만)", REPORT_MAIN_X+offset_column1, REPORT_MAIN_Y+offset_row*8, p);
		
		Utility.RenderSignButton(canvas, 2, +1, REPORT_MAIN_X+offset_column1+offset_button*1, REPORT_MAIN_Y-30/2+offset_row*8);
		Utility.RenderSignButton(canvas, 1, +1, REPORT_MAIN_X+offset_column1+offset_button*2, REPORT_MAIN_Y-30/2+offset_row*8);
		Utility.RenderSignButton(canvas, 1, -1, REPORT_MAIN_X+offset_column1+offset_button*3, REPORT_MAIN_Y-30/2+offset_row*8);
		Utility.RenderSignButton(canvas, 2, -1, REPORT_MAIN_X+offset_column1+offset_button*4, REPORT_MAIN_Y-30/2+offset_row*8);
		canvas.drawBitmap(m_financial_dealings_repay_bitmap, REPORT_MAIN_X+offset_column1+offset_button*5, REPORT_MAIN_Y-30/2+offset_row*8, null);

	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getSource() != 100)
			return false;
		
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		if(m_return_to_map_rect.contains(x, y))
		{
			Utility.m_click_sound.start();
			GameState.mGameScreenType = GameScreenTypes.NORMAL;
			GameState.selection2 = 0;
			DepartmentManager.m_department_select = -1;
		}
		
		for(int i=0; i<NUM_REPORT_TYPE; i++)
			if(new Rect(5/2, 15/2+70/2*i, 5/2+142*2/2, 15+24*2/2+70/2*i).contains(x, y))
			{
				Utility.m_click_sound.start();
				m_report_type = i;
				m_lone = 0;
				m_repay = 0;
				break;
			}
		
		// 금융 거래
		if(m_report_type == 5)
		{
			int offset_row = 25;
	    	int offset_column1 = 350/2;
	    	int offset_button = 70/2;
	    	
			for(int i=0; i<4; i++)
				if(new Rect(REPORT_MAIN_X+offset_column1+offset_button*(i+1), REPORT_MAIN_Y-30/2+offset_row*6, REPORT_MAIN_X+offset_column1+offset_button*(i+1) + 22*2/2, REPORT_MAIN_Y-30/2+offset_row*6 + 22*2/2).contains(x, y))
				{
					Utility.m_click_sound.start();
					
					long temp = (Player.m_credit_limit - Player.m_loan)/10;
					
					if(i == 0)
						m_lone += (temp*5);
					if(i == 1)
						m_lone += temp;
					if(i == 2)
						m_lone -= temp;
					if(i == 3)
						m_lone -= (temp*5);

					if(m_lone < 0)
						m_lone = 0;
					
					if(m_lone > Player.m_credit_limit - Player.m_loan)
						m_lone = Player.m_credit_limit - Player.m_loan;
					
					break;
				}
			
			if(new Rect(REPORT_MAIN_X+offset_column1+offset_button*5, REPORT_MAIN_Y-30/2+offset_row*6, REPORT_MAIN_X+offset_column1+offset_button*5 + 55*2/2, REPORT_MAIN_Y-30/2+offset_row*6 + 23*2/2).contains(x, y))
			{
				Utility.m_click_sound.start();

				Player.m_loan += m_lone;
				Player.m_money += m_lone;
				m_lone = 0;
			}
			
			for(int i=0; i<4; i++)
				if(new Rect(REPORT_MAIN_X+offset_column1+offset_button*(i+1), REPORT_MAIN_Y-30/2+offset_row*8, REPORT_MAIN_X+offset_column1+offset_button*(i+1) + 22*2/2, REPORT_MAIN_Y-30/2+offset_row*8 + 22*2/2).contains(x, y))
				{
					Utility.m_click_sound.start();
					
					long temp = Player.m_loan/10;

					if(i == 0)
						m_repay += (temp*5);
					if(i == 1)
						m_repay += temp;
					if(i == 2)
						m_repay -= temp;
					if(i == 3)
						m_repay -= (temp*5);

					if(m_repay < 0)
						m_repay = 0;
					
					if(m_repay > Player.m_loan)
						m_repay = Player.m_loan;
					
					break;
				}
			
			if(new Rect(REPORT_MAIN_X+offset_column1+offset_button*5, REPORT_MAIN_Y-30/2+offset_row*8, REPORT_MAIN_X+offset_column1+offset_button*5 + 55*2/2, REPORT_MAIN_Y-30/2+offset_row*8 + 23*2/2).contains(x, y))
			{
				Utility.m_click_sound.start();
				
				if(Player.m_money < m_repay)
				{
					Utility.MessageDialog("상환을 위한 현금이 부족합니다");
					m_repay = 0;
					return true;
				}

				Player.m_loan -= m_repay;
				Player.m_money -= m_repay;
				m_repay = 0;
			}
		}

		return true;
	}

}


