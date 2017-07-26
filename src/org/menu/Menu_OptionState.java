package org.menu;

import javax.microedition.khronos.opengles.*;

import org.framework.*;
import org.framework.openGL.*;
import org.game.*;
import org.menu.MenuState.*;
import org.screen.*;
import org.screen.layer.component.*;
import org.screen.layer.component.Button.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.view.*;

public class Menu_OptionState implements IState{
	
	private Button sItemReturnButton;

	private CheckBox sOptionCheckbox1;
	private CheckBox sOptionCheckbox2;
	private CheckBox sOptionCheckbox3;

	Paint p = new Paint();
	
	int i;
	int i1;
	
	int temp;

	public Menu_OptionState() {
		// TODO Auto-generated constructor stub
		
		i = 0;
		i1 = 0;
	}

	@Override
	public void Init() {
		// TODO Auto-generated method stub
		
		sItemReturnButton = new Button(new Point(430, 335), R.drawable.menu_item_return_n, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.menu_item_return_t)
				.setFlashEnabled(true)
				.InitNormalSpriteData(R.drawable.menu_item_flash_n, 30, 8, false, true)
				.InitTouchedSpriteData(R.drawable.menu_item_flash_t, 30, 8, false, true);
		
		temp = 15;
		
		
		sOptionCheckbox1 = new CheckBox(new Point(55 + 170, temp + 67), 
				R.drawable.optionmenu_checkbox_yes, R.drawable.optionmenu_checkbox_no, false)
				.setVisiable(true)
				//sOptionCheckbox1.setTouchedBitmap(m_bitmap_return_t);
				.setTouchedEnabled(true);
		
		sOptionCheckbox2 = new CheckBox(new Point(55 + 170, temp + 107), 
				R.drawable.optionmenu_checkbox_yes, R.drawable.optionmenu_checkbox_no, false)
				.setVisiable(true)
				//sOptionCheckbox1.setTouchedBitmap(m_bitmap_return_t);
				.setTouchedEnabled(true);

		
		sOptionCheckbox3 = new CheckBox(new Point(55 + 170, temp + 147), 
				R.drawable.optionmenu_checkbox_yes, R.drawable.optionmenu_checkbox_no, false)
				.setVisiable(true)
				//sOptionCheckbox1.setTouchedBitmap(m_bitmap_return_t);
				.setTouchedEnabled(true);
		
	}

	@Override
	public void Destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Update() {
		// TODO Auto-generated method stub
		
		switch(MenuState.getAnimationType())
		{
		case OPENING:
			break;
		case NONE:
		{
			sItemReturnButton.Update();
			
			break;
		}
		case CLOSING:
		{
			if(i1 == 0)
			{
				// 돌아가기 선택
				MenuState.ChangeState(new Menu_MainState());
				MenuState.setAnimationType(AnimationTypes.OPENING);
			}
						
			break;
		}
		default:
			break;
		
		}
		
	}
	
	@Override
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
		switch(MenuState.getAnimationType())
		{
		case OPENING:
		{
			spriteBatcher.drawBitmap2(R.drawable.optionmenu_background, 0, 0, null);
			
			//if(i1 < 255)
			
			i1= 255;
			
			{
				i1 = i1 + 30;
				if(i1 > 255)
					i1 = 255;
				
				p.setAlpha(i1);
				spriteBatcher.drawBitmap2(R.drawable.optionmenu_whiteboard, 55, temp-255/5+i1/5, p);
			}
			
			if(i < 255 && i1 == 255)
			{
				i = i + 30;
				if(i > 255)
					i = 255;
				
				p.setAlpha(i);
				spriteBatcher.drawBitmap2(R.drawable.menu_item_return_n, 430+255/2-i/2, 335, p);
			}
			
			if(i == 255)
			{
				sItemReturnButton.SetReplyTime(System.currentTimeMillis()-3000, 4500);
				MenuState.setAnimationType(AnimationTypes.NONE);
			}
			
			sOptionCheckbox1.onDrawFrame(gl, spriteBatcher);
			sOptionCheckbox2.onDrawFrame(gl, spriteBatcher);
			sOptionCheckbox3.onDrawFrame(gl, spriteBatcher);
			
			break;
			
		}
		case NONE:
		{
			spriteBatcher.drawBitmap2(R.drawable.optionmenu_background, 0, 0, null);
			
			spriteBatcher.drawBitmap2(R.drawable.optionmenu_whiteboard, 55, temp, null);

			sItemReturnButton.onDrawFrame(gl, spriteBatcher);
			
			sOptionCheckbox1.onDrawFrame(gl, spriteBatcher);
			sOptionCheckbox2.onDrawFrame(gl, spriteBatcher);
			sOptionCheckbox3.onDrawFrame(gl, spriteBatcher);
		
			break;
		}
		case CLOSING:
		{
			spriteBatcher.drawBitmap2(R.drawable.optionmenu_background, 0, 0, null);
			
			if(i == 0)
			{
				i1 = 0;
				
				i1 = i1 - 30;
				if(i1 < 0)
					i1 = 0;
				
				//p.setAlpha(i1);
				//canvas.drawBitmap(m_bitmap_white_board, 55, temp-255/5+i1/5, p);
			}
			//else
				spriteBatcher.drawBitmap2(R.drawable.optionmenu_whiteboard, 55, temp, null);
			
			if(i >= 0)
			{
				i = i - 30;
				if(i < 0)
					i = 0;
				
				p.setAlpha(i);
				spriteBatcher.drawBitmap2(R.drawable.menu_item_return_n, 430+255/2-i/2, 335, p);
			}
			
			sOptionCheckbox1.onDrawFrame(gl, spriteBatcher);
			sOptionCheckbox2.onDrawFrame(gl, spriteBatcher);
			sOptionCheckbox3.onDrawFrame(gl, spriteBatcher);
			
			break;
		}
		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			MenuState.setAnimationType(AnimationTypes.CLOSING);
		
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		switch(MenuState.getAnimationType())
		{
		case OPENING:
			break;
		case NONE:
		{
			if(sItemReturnButton.onTouchEvent(event))
				MenuState.setAnimationType(AnimationTypes.CLOSING);
			
			sOptionCheckbox1.onTouchEvent(event);
			sOptionCheckbox2.onTouchEvent(event);
			sOptionCheckbox3.onTouchEvent(event);
			
			break;
		}
		case CLOSING:
			break;
		default:
			break;
		}
		
		return false;
	}

}
