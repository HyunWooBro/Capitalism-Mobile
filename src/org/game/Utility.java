package org.game;

import org.framework.*;
import org.game.construction.*;
import org.screen.*;
import org.screen.layer.component.Button;

import android.app.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class Utility {
	public static final String TAG = "Capitalism Mobile";
	
	public static MediaPlayer m_click_sound = MediaPlayer.create(AppManager.getInstance().getActivity(), R.raw.click);
	
	//public static MediaPlayer m_horn_002_sound = MediaPlayer.create(AppManager.getInstance().getActivity(), R.raw.horn_002);
	//public static MediaPlayer m_horn_003_sound = MediaPlayer.create(AppManager.getInstance().getActivity(), R.raw.horn_003);
	//public static MediaPlayer m_horn_004_sound = MediaPlayer.create(AppManager.getInstance().getActivity(), R.raw.horn_004);
	//public static MediaPlayer m_horn_005_sound = MediaPlayer.create(AppManager.getInstance().getActivity(), R.raw.horn_005);
	//public static MediaPlayer m_horn_006_sound = MediaPlayer.create(AppManager.getInstance().getActivity(), R.raw.horn_006);
	
	// 토스트를 static으로 하여 UserInterface에 넣었는데 MenuState에서 GameState로 바꾸면서 에러가 나서 이곳으로
	// 옮겼다. 원인은 아직 미상
	static Toast m_toast;
	
	static Bitmap m_message_popup_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.ui_message_popup);
	public static boolean is_message_popup_showing = false;
	
	static Rect m_ok_rect = new Rect(240/2+168*2/2, 300/2+48*2/2, 240/2+231*2/2, 300/2+69*2/2);
	
	private static Bitmap m_one_plus_button_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.one_plus_button);
	private static Bitmap m_one_minus_button_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.one_minus_button);
	private static Bitmap m_double_plus_button_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.double_plus_button);
	private static Bitmap m_double_minus_button_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.double_minus_button);
	
	public static BitmapFactory.Options sOptions;
	
	private static Paint pWhiteLeft15;
	

	public Utility() {
		// TODO Auto-generated constructor stub
		
		//is_message_popup_showing = false;
		//m_ok_rect = new Rect(240+168*2, 300+48*2, 240+231*2, 300+69*2);
	}
	
	public static void InitStatic()
	{
		sOptions = new BitmapFactory.Options();
		sOptions.inDensity=160;
		sOptions.inTargetDensity = 160;
		
		//m_message_popup_bitmap = AppManager.getInstance().getBitmap(R.drawable.ui_message_popup, Utility.sOptions);
		
		//m_one_plus_button_bitmap = AppManager.getInstance().getBitmap(R.drawable.one_plus_button, Utility.sOptions);
		//m_one_minus_button_bitmap = AppManager.getInstance().getBitmap(R.drawable.one_minus_button, Utility.sOptions);
		//m_double_plus_button_bitmap = AppManager.getInstance().getBitmap(R.drawable.double_plus_button, Utility.sOptions);
		//m_double_minus_button_bitmap = AppManager.getInstance().getBitmap(R.drawable.double_minus_button, Utility.sOptions);

	}

	
	public static void RenderMessagePopup(Canvas canvas, String content, boolean darker)
	{
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.WHITE);
    	
    	if(darker == true)
    		canvas.drawARGB(100, 0, 0, 0);
		
		canvas.drawBitmap(m_message_popup_bitmap, 240/2, 300/2, null);

		if(content.length() < 26)
			canvas.drawText(content, 300/2, 340/2, p);
		else
		{
			canvas.drawText(content.substring(0, 25), 300/2, 340/2, p);
			canvas.drawText(content.substring(26, content.length()), 300/2, 380/2, p);
		}
		
		canvas.drawText("확인", 590/2, 430/2, p);
	}
	
	public static boolean onTouchEvent(MotionEvent event) 
	{
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		if(VictoryCondition.m_win == true && VictoryCondition.m_is_message_shown == false)
    	{
			if(event.getSource() != 100)
				return true;
			
			if(m_ok_rect.contains(x, y))
			{
				Utility.m_click_sound.start();
				VictoryCondition.m_is_message_shown = true;
				
			}
			
			return true;
    	}
		
		if(VictoryCondition.m_lose == true && VictoryCondition.m_is_message_shown == false)
    	{
			if(event.getSource() != 100)
				return true;
			
			if(m_ok_rect.contains(x, y))
			{
				Utility.m_click_sound.start();
				VictoryCondition.m_is_message_shown = true;
				
			}
			
			return true;
    	}
		
		return false;
	}
	
	public static void MessageDialog(String content)
	{
		new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
		.setTitle(content)
	    .setPositiveButton("확인", null)
	    .show();
	}
	
	public static void RenderSignButton(Canvas canvas, int num_op, int sign, int x, int y)
	{
		if(sign > 0)
		{
			if(num_op == 1)
				canvas.drawBitmap(m_one_plus_button_bitmap, x,  y, null);
			else	// num_op == 2
				canvas.drawBitmap(m_double_plus_button_bitmap, x,  y, null);
		}
		else
		{
			if(num_op == 1)
				canvas.drawBitmap(m_one_minus_button_bitmap, x,  y, null);
			else	// num_op == 2
				canvas.drawBitmap(m_double_minus_button_bitmap, x,  y, null);
		}
	}
	
	public static int getDistance(MotionEvent event)
	{
		int dx = (int)(event.getX(0) - event.getX(1));
		int dy = (int)(event.getY(0) - event.getY(1));
		return (int)(Math.sqrt(dx*dx + dy*dy));
	}

	public static void ShowToast(String text, int duration)
	{
		if(m_toast == null)
			m_toast = Toast.makeText(AppManager.getInstance().getActivity(), "", Toast.LENGTH_SHORT);
		m_toast.setDuration(duration);
		m_toast.setText(text);
		m_toast.show();
	}

}
