package org.game;

import java.util.*;

import org.framework.*;
import org.screen.*;
import org.screen.layer.component.*;
import org.screen.layer.component.Button;
import org.screen.layer.window.Window;

import android.graphics.*;
import android.graphics.Paint.*;
import android.view.*;
import android.widget.*;

/**
 * 게임의 승리조건을 관리하는 클래스
 * @author 김현준
 *
 */
public class VictoryCondition {
	public static int m_bit_condition;
	
	public static long m_annual_netprofit;			// 목표 연간순이익
	public static long m_money;						// 목표 현금
	
	public static boolean m_win;
	public static boolean m_lose;
	public static boolean m_is_message_shown;
	
	public static final int VC_X = 150;
	public static final int VC_Y = 30;
	
	public static final int VC_NETPROFIT = 0x00000001;
	public static final int VC_MONEY = 0x00000002;
	public static final int VC_DEADLINE = 0x00000010;
	
	Thread m_thread;
	
	public Window mPopupOkWindow;

	public VictoryCondition() {
		// TODO Auto-generated constructor stub
		
		m_bit_condition |= VC_NETPROFIT;
		m_bit_condition |= VC_DEADLINE;
		
		m_annual_netprofit = 500000000L;

		m_win = false;
		m_lose = false;
		m_is_message_shown = false;
		
		
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
		
		mPopupOkWindow = new Window(new Point(240/2, 300/2), 
				new Rect(240/2, 300/2, 240/2+400, 300/2+76), 
				AppManager.getInstance().getBitmap(R.drawable.ui_message_popup, Utility.sOptions));
		{
			Text text = new Text(new Rect(10, 10, 390, 40), null, p);
			
			text.setVisiable(true);
			text.setRenderRectEnabled(true);
			
			mPopupOkWindow.Add(text);
			
			Button okButton = new Button(new Point(168, 48), 
					new Rect(168, 48, 231, 70), 
					R.drawable.ui_message_popup_ok_up, true, true);
			
			okButton.setString("확인");
			okButton.setStringNormalPos( new Point(11, 17));
			okButton.setNormalPaint(p);
			okButton.setStringTouchedPos( new Point(11, 18));
			okButton.setTouchedPaint(p);
			okButton.setStringPushedPos( new Point(11, 18));
			okButton.setPushedPaint(p);
			okButton.setStringEnabled(true);
			
			okButton.setVisiable(true);
			okButton.setTouchedBitmap(R.drawable.ui_message_popup_ok_down);
			okButton.setPushedBitmap(R.drawable.ui_message_popup_ok_down);
		
			mPopupOkWindow.Add(okButton);
		}
		
		mPopupOkWindow.setVisiable(true);
		mPopupOkWindow.setMovable(true);
		mPopupOkWindow.setBackGroundColor(Color.argb(100, 0, 0, 0));
		mPopupOkWindow.setBackGroundColorApplied(true);
		mPopupOkWindow.setOutsideTouchable(false);

	}
	
	public void CheckVictoryConditon()
	{
		// 순이익 목표가 설정되었다면
		if((m_bit_condition & VC_NETPROFIT) != 0)
		{
			// 목표 순이익을 달성했다면 승리
			if(Player.m_annual_netprofit >= m_annual_netprofit)
			{
				if(m_lose == false)
					m_win = true;
				// 여기서 토스트를 사용하면 에러가 난다.
				//Toast.makeText(AppManager.getInstance().getActivity(), "목표를 달성하였습니다.", Toast.LENGTH_LONG).show();
			}
		}
		
		// 최종기한이 설정되었다면
    	if((m_bit_condition & VC_DEADLINE) != 0)
		{
    		// 목표를 달성하지 못하고 시간이 지나면 패배
    		if(Time.GetInstance().GetCalendar().after(Time.GetInstance().GetDeadline()))
    		{
    			if(m_win == false)
    				m_lose = true;
    			// 여기서 토스트를 사용하면 에러가 난다.
    			//Toast.makeText(AppManager.getInstance().getActivity(), String.format("abc"), Toast.LENGTH_SHORT).show();
    		}
		}
	}
	
	public void Render(Canvas canvas)
	{
    	if(m_win == true && m_is_message_shown == false)
    	{
    		((Text)mPopupOkWindow.getWindowElementList().get(0)).setString("목표를 달성하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)");
    		mPopupOkWindow.Render(canvas);
    		//Utility.RenderMessagePopup(canvas, "목표를 달성하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)", true);
    	}
    	
    	if(m_lose == true && m_is_message_shown == false)
    	{
    		((Text)mPopupOkWindow.getWindowElementList().get(0)).setString("목표달성에 실패하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)");
    		mPopupOkWindow.Render(canvas);
    		//Utility.RenderMessagePopup(canvas, "목표달성에 실패하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)", true);
    	}
	}
	
	public boolean onTouchEvent(MotionEvent event) 
	{
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		if(VictoryCondition.m_win == true && VictoryCondition.m_is_message_shown == false)
    	{
			if(mPopupOkWindow.onTouchEvent(event))
			{
				if(mPopupOkWindow.getTouchedComponent() == 1)
				{
					mPopupOkWindow.setTouchedComponent(-1);
					VictoryCondition.m_is_message_shown = true;
				}
				
				return true;
			}
    	}
		
		if(VictoryCondition.m_lose == true && VictoryCondition.m_is_message_shown == false)
    	{
			if(mPopupOkWindow.onTouchEvent(event))
			{
				if(mPopupOkWindow.getTouchedComponent() == 1)
				{
					mPopupOkWindow.setTouchedComponent(-1);
					VictoryCondition.m_is_message_shown = true;
				}
				
				return true;
			}
    	}
		
		return false;
	}

}
