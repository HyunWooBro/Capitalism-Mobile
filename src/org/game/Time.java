package org.game;

import java.util.*;


public class Time {
	
	private GregorianCalendar m_calendar; // 현재 날짜
	private GregorianCalendar m_deadline; // 최종 기한
	
	// 싱글턴
	private static Time s_instance;
	private Time() {}
	public static Time GetInstance(){
		if(s_instance == null){
			s_instance = new Time();
		}
		return s_instance;
	}
	
	public static void Destroy() 
	{
		s_instance = null;
	}
	
	public void Init() 
	{
		int startyear;
		int startmonth;
		int startday;
		
		int endyear;
		int endmonth;
		int endday;
		
		m_calendar = new GregorianCalendar();
		startyear = 2000;
		startmonth = 1;
		startday = 1;
		m_calendar.set(startyear, startmonth-1, startday, 0, 0);
		
		m_deadline = new GregorianCalendar();
		endyear = 2000;
		endmonth = 1;
		endday = 5;
		m_deadline.set(endyear, endmonth-1, endday, 0, 0);
	}
	
	public static void InitStatic()
	{
		
	}
	
	public GregorianCalendar GetCalendar() {
		return m_calendar;
	}
	public void SetCalendar(GregorianCalendar m_calendar) {
		this.m_calendar = m_calendar;
	}
	public GregorianCalendar GetDeadline() {
		return m_deadline;
	}
	public void SetDeadline(GregorianCalendar m_deadline) {
		this.m_deadline = m_deadline;
	}

}
