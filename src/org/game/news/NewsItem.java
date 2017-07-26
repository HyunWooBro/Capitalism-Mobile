package org.game.news;

import java.util.*;

import org.framework.*;

import android.graphics.*;

/**
 * 뉴스 아이템 클래스
 * @author 김현준
 *
 */
public class NewsItem {
	private GregorianCalendar m_calendar;			// 뉴스 발생 날짜
	private String m_content;								// 뉴스 내용
	private Point m_location;								// 뉴스 발생 위치
	private int mIconLife;									// 뉴스 아이콘알림 남은 시간
	private SpriteAnimation mIconInsideBitmap;		// 스크린 내부에 위치한 뉴스 아이콘 스프라이트
	private SpriteAnimation mIconOutsideBitmap;	// 스크린 외부에 위치한 뉴스 아이콘 스프라이트

	// private Type mIconType							// 뉴스 종류
	// private Player mPlayer;							// 뉴스의 주체(중립인 경우 null)

	public NewsItem(GregorianCalendar calendar, String content, Point location) {
		// TODO Auto-generated constructor stub
		
		m_calendar = calendar;
		m_content = content;
		m_location = location;
		
		mIconLife = 20000;
	}

	public GregorianCalendar get_calendar()
	{
		return m_calendar;
	}

	public String get_content()
	{
		return m_content;
	}

	public Point get_location()
	{
		return m_location;
	}

	public int getIconLife() 
	{
		return mIconLife;
	}

	public void ReduceLife() 
	{
		mIconLife--;
	}

}
