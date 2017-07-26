package org.game;

import java.util.*;

import org.game.construction.*;

import android.graphics.*;
import android.graphics.Paint.*;

public class Player {
	public static long m_money;						// 현금
	
	public static long m_annual_netprofit;			// 연간순이익
	//public static long m_month_netprofit[];			// 최근 12달의 순이익
	public static long m_monthly_netprofit[];		// 최근 12달의 순이익 배열
	public static long m_last_netprofit;				// 사업체의 최근 13달 이전의 순이익
	
	public static int m_netprofit_index;				// 위 배열의 인덱스(배열의 끝까지 도달하면 다시 처럼으로)
	
	
	
	public static long m_monthly_sales[];					// 최근 12달의 영업 매출 배열
	public static long m_monthly_sales_expense[];	// 최근 12달의 판매 비용 배열
	public static long m_monthly_wage[];					// 최근 12달의 임금 비용 배열
	public static long m_monthly_maintenance[];		// 최근 12달의 유지비 지출 배열
	public static long m_monthly_advertise[];			// 최근 12달의 광고 지출 배열
	public static long m_monthly_welfare[];				// 최근 12달의 사원 복지 지출 배열
	public static long m_monthly_interest[];				// 최근 12달의 대출 이자 배열
	
	
	public static long m_accumulated_netprofit;				// 누적 영업 매출
	public static long m_accumulated_sales;					// 누적 판매 비용
	public static long m_accumulated_sales_expense;	// 누적 판매 비용
	public static long m_accumulated_wage;					// 누적 임금 비용
	public static long m_accumulated_maintenance;		// 누적 유지비 지출
	public static long m_accumulated_advertise;			// 누적 광고 지출
	public static long m_accumulated_welfare;				// 누적 사원 복지 지출
	public static long m_accumulated_interest;				// 누적 대출 이자
	
	
	
	public static long m_max_netprofit;				// 그래프의 현재 최고 수치(1000,0000 단위로 구분)
	
	public static int m_brand;								// 기업 브랜드
	public Color m_color;									// 기업 고유색
	// 등등
	
	public static long m_num_employee;
	
	public static long m_average_wage;					// 일일 평균 임금
	
	
	public static long m_loan;							// 대출금
	public static long m_credit_limit;					// 신용 한도 
	
	public static Construction m_connection_destination_construction; // null 이외의 값이면 연결모드중이고 null이면 종료
	public static int m_connection_destination_department_index;
	public static Construction m_connection_source_construction; // null 이외의 값이면 연결모드중이고 null이면 종료
	public static int m_temp;
	
	
	// 플레이가 소유하는 건물 리스트
	public static ArrayList<Retail> m_retaillist = new ArrayList<Retail>();
	public static ArrayList<Factory> m_factorylist = new ArrayList<Factory>();
	public static ArrayList<Farm> m_farmlist = new ArrayList<Farm>();
	public static ArrayList<RnD> m_RnDlist = new ArrayList<RnD>();
	
	
	public static int[] mQuality = new int[7];
	
	
	public void Initialize(long money)
	{
		m_money = money;
		//m_annual_netprofit = 50000000L;
		m_annual_netprofit = 0;
		m_monthly_netprofit = new long[12];
		
		
		
		m_monthly_sales = new long[12];
		
		m_monthly_sales_expense = new long[12];
		
		m_monthly_wage = new long[12];
		
		m_monthly_maintenance = new long[12];
		
		m_monthly_advertise = new long[12];

		m_monthly_welfare = new long[12];
		
		m_monthly_interest = new long[12];
		
		
		m_num_employee = 0;
		
		m_last_netprofit = 0;
		
		m_netprofit_index = 0;
		
		m_max_netprofit = 0;
		
		m_brand = 0;
		
		m_loan = 0;
		m_credit_limit = 1000000000;
		
		m_average_wage = 50000;
		
		for(int i=0; i<mQuality.length; i++)
			mQuality[i] = 30;
		
	}
	
	public static void InitStatic()
	{
		m_retaillist = new ArrayList<Retail>();
		m_factorylist = new ArrayList<Factory>();
		m_farmlist = new ArrayList<Farm>();
		m_RnDlist = new ArrayList<RnD>();
	}
	
	public void Update()
	{
		long max;
		
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

}
