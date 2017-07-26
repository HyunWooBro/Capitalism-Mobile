package org.game.news;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.cell.*;
import org.screen.*;

import android.graphics.*;
import android.util.*;

/**
 * 게임 내의 다양한 뉴스를 관리하고 보여주는 클래스
 * @author 김현준
 *
 */
public class NewsManager {
	private ArrayList<NewsItem> m_newsitemlist;		// 뉴스아이템을 관리하는 리스트
	
	private final int MAX_NEWSITEM_SHOWN = 3;	// 화면에 출력하는 최대 뉴스아이템 개수
	
	private final int ICON_POSITION_OFFSET_Y = 0;
	
	//private Bitmap m_iconbox_bitmap = AppManager.getInstance().getBitmap(R.drawable.iconbox_construction, Utility.sOptions);
	//private Bitmap m_iconbox_t_bitmap = AppManager.getInstance().getBitmap(R.drawable.iconbox_construction_t, Utility.sOptions);
	
	private Bitmap m_iconbox_bitmap = AppManager.getInstance().getBitmap(R.drawable.iconbox_red_exclamation, Utility.sOptions);
	private Bitmap m_iconbox_t_bitmap = AppManager.getInstance().getBitmap(R.drawable.iconbox_red_exclamation_t, Utility.sOptions);
	
	public SpriteAnimation mIconboxSprite;
	
	// 싱글턴
	private static NewsManager s_instance;
	private NewsManager() {}
	public static NewsManager GetInstance(){
		if(s_instance == null){
			s_instance = new NewsManager();
		}
		return s_instance;
	}
	
	public void Init()
	{
		m_newsitemlist = new ArrayList<NewsItem>();
		
		mIconboxSprite = new SpriteAnimation(null);
		
		mIconboxSprite.InitSpriteData(41, 40, 6, 2);
	}
	
	/**
	 * 최근 뉴스를 리스트 선두에 삽입
	 * @param newsitem
	 */
	public void Insert(NewsItem newsitem)
	{
		m_newsitemlist.add(0, newsitem);
	}
	
	public void Render(Canvas canvas) 
	{
		int off_x = GameState.off_x;
		int off_y = GameState.off_y;
		
		int num_news_shown = 0;			// 보여질 뉴스 아이템 개수
		int year = 0;
		int month = 0;
    	int day = 0;
		
		Paint p = new Paint();
		p.setTextSize(15);

		num_news_shown = m_newsitemlist.size();
		if(num_news_shown > MAX_NEWSITEM_SHOWN)
			num_news_shown = MAX_NEWSITEM_SHOWN;
		
		
		for(int i=0; i<num_news_shown; i++)
		{
			year = m_newsitemlist.get(num_news_shown-i-1).get_calendar().get(Calendar.YEAR);
			month = m_newsitemlist.get(num_news_shown-i-1).get_calendar().get(Calendar.MONTH);
			day = m_newsitemlist.get(num_news_shown-i-1).get_calendar().get(Calendar.DAY_OF_MONTH);
			
			p.setColor(Color.argb(100, 100, 100, 200));
			canvas.drawRect(450/2, 720/2+5/2-30/2*i, 1300/2, 720/2+5/2-30/2-30/2*i, p);
			p.setColor(Color.WHITE);
			canvas.drawText(String.format("%d년 %2d월 %2d일 ", year, month+1, day)
					+ m_newsitemlist.get(num_news_shown-i-1).get_content(), 500/2, 720/2-30/2*i, p);
		}
		
		for(int i=0; i<m_newsitemlist.size(); i++)
		{

			
			if(m_newsitemlist.get(i).getIconLife() > 0)
			{
				
				Rect screenRect = new Rect(0, 0, Screen.getVirtualScreenWidth() -  m_iconbox_t_bitmap.getWidth()/2, 
					Screen.getVirtualScreenHeight() - m_iconbox_t_bitmap.getHeight() - 38);

				
				if(Screen.getZoom() != 1.0f)
				{
					screenRect.right = (int)(screenRect.right*10.0f/7.0f);
					screenRect.bottom = (int)(screenRect.bottom*10.0f/7.0f);
				}
				
				m_newsitemlist.get(i).ReduceLife();
				
				int offset;
				
				if(Screen.getZoom() != 1.0f)
					offset = 16;
				else
					offset = 0;

				
				if(screenRect.contains(m_newsitemlist.get(i).get_location().x - off_x, 
						m_newsitemlist.get(i).get_location().y - offset - off_y))
				{
					Screen.beginZoomRender(canvas);
					
					if(Screen.getZoom() != 1.0f)
						canvas.scale(10.0f/7.0f, 10.0f/7.0f, m_newsitemlist.get(i).get_location().x - off_x, 
								m_newsitemlist.get(i).get_location().y - off_y);
					
					Rect dest = new Rect(m_newsitemlist.get(i).get_location().x - off_x, 
							m_newsitemlist.get(i).get_location().y - offset - off_y,
							m_newsitemlist.get(i).get_location().x - off_x + m_iconbox_t_bitmap.getWidth()/2,
							m_newsitemlist.get(i).get_location().y - offset - off_y + m_iconbox_t_bitmap.getHeight());
					
					canvas.drawBitmap(m_iconbox_bitmap, mIconboxSprite.mSRectangle, dest, null);
					
					Screen.endZoomRender(canvas);
				}
				else
				{
					Screen.beginZoomRender(canvas);

					if(m_newsitemlist.get(i).get_location().x - off_x > screenRect.right)
					{
						if(m_newsitemlist.get(i).get_location().y - offset - off_y > 
							screenRect.bottom)
						{
							if(Screen.getZoom() != 1.0f)
								canvas.scale(10.0f/7.0f, 10.0f/7.0f);
							
							Rect dest = new Rect(640 - m_iconbox_t_bitmap.getWidth()/2, 
									400 - m_iconbox_t_bitmap.getHeight() -38,
									640,
									400 -38);
							
							canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest, null);
							
							/*canvas.drawBitmap(m_iconbox_t_bitmap, 
									640 - m_iconbox_t_bitmap.getWidth(), 
									400 - m_iconbox_t_bitmap.getHeight() -38,
									null);*/
						}
						else if(m_newsitemlist.get(i).get_location().y - offset - off_y < 
								screenRect.top)
						{
							if(Screen.getZoom() != 1.0f)
								canvas.scale(10.0f/7.0f, 10.0f/7.0f);
							
							Rect dest = new Rect(640 - m_iconbox_t_bitmap.getWidth()/2, 
									0,
									640,
									m_iconbox_t_bitmap.getHeight());
							
							canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest, null);
							
							/*canvas.drawBitmap(m_iconbox_t_bitmap,
									640 - m_iconbox_t_bitmap.getWidth()/2, 
									0,
									null);*/
						}
						else
						{
							if(Screen.getZoom() != 1.0f)
								canvas.scale(10.0f/7.0f, 10.0f/7.0f, 0, m_newsitemlist.get(i).get_location().y - off_y);
							
							Rect dest = new Rect(640 - m_iconbox_t_bitmap.getWidth()/2, 
									m_newsitemlist.get(i).get_location().y - offset - off_y,
									640,
									m_newsitemlist.get(i).get_location().y - offset + m_iconbox_t_bitmap.getHeight() - off_y);
							
							canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest, null);
							
							/*canvas.drawBitmap(m_iconbox_t_bitmap, 
									640 - m_iconbox_t_bitmap.getWidth(), 
									m_newsitemlist.get(i).get_location().y - ICON_POSITION_OFFSET_Y - off_y,
									null);*/
							
						}
					}
					else if(m_newsitemlist.get(i).get_location().x - off_x < screenRect.left)
					{
						if(m_newsitemlist.get(i).get_location().y - ICON_POSITION_OFFSET_Y - off_y > 
							screenRect.bottom)
						{
							if(Screen.getZoom() != 1.0f)
								canvas.scale(10.0f/7.0f, 10.0f/7.0f);
							
							Rect dest = new Rect(0, 
									400 - m_iconbox_t_bitmap.getHeight() - 38,
									m_iconbox_t_bitmap.getWidth()/2,
									400 - 38);
							
							canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest, null);
							
							/*canvas.drawBitmap(m_iconbox_t_bitmap, 
									0, 
									400 - m_iconbox_t_bitmap.getHeight() - 38,
									null);*/
						}
						else if(m_newsitemlist.get(i).get_location().y - ICON_POSITION_OFFSET_Y - off_y < 
								screenRect.top)
						{
							if(Screen.getZoom() != 1.0f)
								canvas.scale(10.0f/7.0f, 10.0f/7.0f);
							
							Rect dest = new Rect(0, 
									0,
									m_iconbox_t_bitmap.getWidth()/2,
									m_iconbox_t_bitmap.getHeight());
							
							canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest, null);
							
							/*canvas.drawBitmap(m_iconbox_t_bitmap, 
									0, 
									0,
									null);*/
						}
						else
						{
							if(Screen.getZoom() != 1.0f)
								canvas.scale(10.0f/7.0f, 10.0f/7.0f, 0, m_newsitemlist.get(i).get_location().y - off_y);
							
							Rect dest = new Rect(0, 
									m_newsitemlist.get(i).get_location().y - offset - off_y,
									m_iconbox_t_bitmap.getWidth()/2,
									m_newsitemlist.get(i).get_location().y - offset + m_iconbox_t_bitmap.getHeight() - off_y);
							
							canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest, null);
							
							/*canvas.drawBitmap(m_iconbox_t_bitmap, 
									0, 
									m_newsitemlist.get(i).get_location().y - ICON_POSITION_OFFSET_Y - off_y,
									null);*/
						}
					}
					else if(m_newsitemlist.get(i).get_location().y - ICON_POSITION_OFFSET_Y - off_y > screenRect.bottom)
					{
						if(Screen.getZoom() != 1.0f)
							canvas.scale(10.0f/7.0f, 10.0f/7.0f, m_newsitemlist.get(i).get_location().x - off_x, 0);
						
						Rect dest = new Rect(m_newsitemlist.get(i).get_location().x - off_x, 
								400 - m_iconbox_t_bitmap.getHeight() -38,
								m_newsitemlist.get(i).get_location().x + m_iconbox_t_bitmap.getWidth()/2 - off_x,
								400 - 38);
						
						canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest, null);
						
						/*canvas.drawBitmap(m_iconbox_t_bitmap, 
								m_newsitemlist.get(i).get_location().x - off_x, 
								400 - m_iconbox_t_bitmap.getHeight() -38,
								null);*/
					}
					else if(m_newsitemlist.get(i).get_location().y - ICON_POSITION_OFFSET_Y - off_y < screenRect.top)
					{
						if(Screen.getZoom() != 1.0f)
							canvas.scale(10.0f/7.0f, 10.0f/7.0f, m_newsitemlist.get(i).get_location().x - off_x, 0);
						
						Rect dest = new Rect(m_newsitemlist.get(i).get_location().x - off_x, 
								0,
								m_newsitemlist.get(i).get_location().x + m_iconbox_t_bitmap.getWidth()/2 - off_x,
								m_iconbox_t_bitmap.getHeight());
						
						canvas.drawBitmap(m_iconbox_t_bitmap, mIconboxSprite.mSRectangle, dest,	null);
						
						/*canvas.drawBitmap(m_iconbox_t_bitmap, 
								m_newsitemlist.get(i).get_location().x - off_x, 
								0,
								null);*/
					}
					
					Screen.endZoomRender(canvas);
					
					
				}
				
				
			}
		}
	}

}
