package org.menu;

import javax.microedition.khronos.opengles.*;

import org.framework.*;
import org.framework.openGL.*;
import org.game.*;
import org.menu.MenuState.AnimationTypes;
import org.screen.*;
import org.screen.layer.component.*;
import org.screen.layer.component.Button.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.os.*;
import android.util.*;
import android.view.*;

public class Menu_MainState implements IState {
	
	private static Button sInfoButton;
	
	private Button mItem1Button;
	private Button mItem2Button;
	private Button mItem3Button;
	private Button mItem4Button;
	private Button mItem5Button;

	Paint p = new Paint();
	
	int i;
	int i1;
	int i2;
	int i3;
	int i4;
	int i5;
	
	private static Handler h;
	
	boolean mStartAllowed;
	
	public static long mNextTime;

	public Menu_MainState() {
		// TODO Auto-generated constructor stub
		
		i = 0;
		i1 = 0;
		i2 = 0;
		i3 = 0;
		i4 = 0;
		i5 = 0;
	}

	@Override
	public void Init() {
		// TODO Auto-generated method stub
		
		sInfoButton = new Button(new Point(25, 15), R.drawable.mainmenu_info_n, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.mainmenu_info_t)
				.setFlashEnabled(true)
				.InitNormalSpriteData(R.drawable.mainmenu_info_flash, 15, 7, false, true)
				.InitTouchedSpriteData(R.drawable.mainmenu_info_flash, 15, 7, false, true);
		
		mItem1Button = new Button(new Point(430, 40), R.drawable.mainmenu_item1_n, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.mainmenu_item1_t)
				.setFlashEnabled(true)
				.InitNormalSpriteData(R.drawable.menu_item_flash_n, 30, 8, false, true)
				.InitTouchedSpriteData(R.drawable.menu_item_flash_t, 30, 8, false, true);
		
		mItem2Button = new Button(new Point(410, 110), R.drawable.mainmenu_item2_n, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.mainmenu_item2_t)
				.setFlashEnabled(true)
				.InitNormalSpriteData(R.drawable.menu_item_flash_n, 30, 8, false, true)
				.InitTouchedSpriteData(R.drawable.menu_item_flash_t, 30, 8, false, true);
		
		mItem3Button = new Button(new Point(390, 180), R.drawable.mainmenu_item3_n, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.mainmenu_item3_t)
				.setFlashEnabled(true)
				.InitNormalSpriteData(R.drawable.menu_item_flash_n, 30, 8, false, true)
				.InitTouchedSpriteData(R.drawable.menu_item_flash_t, 30, 8, false, true);
		
		mItem4Button = new Button(new Point(370, 250), R.drawable.mainmenu_item4_n, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.mainmenu_item4_t)
				.setFlashEnabled(true)
				.InitNormalSpriteData(R.drawable.menu_item_flash_n, 30, 8, false, true)
				.InitTouchedSpriteData(R.drawable.menu_item_flash_t, 30, 8, false, true);
		
		mItem5Button = new Button(new Point(350, 320), R.drawable.mainmenu_item5_n, true, true)
				.setVisiable(true)
				.setTouchedBitmap(R.drawable.mainmenu_item5_t)
				.setFlashEnabled(true)
				.InitNormalSpriteData(R.drawable.menu_item_flash_n, 30, 8, false, true)
				.InitTouchedSpriteData(R.drawable.menu_item_flash_t, 30, 8, false, true);
		
		if(h == null)
		{
			mStartAllowed = false;
			h = new Handler();
			h.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mStartAllowed = true;
				}
			}, 400);
		}
		else
			mStartAllowed = true;
		
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
			sInfoButton.Update();

			mItem1Button.Update();
			mItem2Button.Update();
			mItem3Button.Update();
			mItem4Button.Update();
			mItem5Button.Update();
			
			break;
		}
		case CLOSING:
		{
			if(i == 0)
			{
				// 싱글플레이 선택
				if(mItem1Button.isPushed())
				{
					MenuState.ChangeState(new Menu_SinglePlayState());
					MenuState.setAnimationType(AnimationTypes.OPENING);
				}
				
				// 멀티플레이 선택
				if(mItem2Button.isPushed())
				{
					MenuState.ChangeState(new Menu_MultiPlayState());
					MenuState.setAnimationType(AnimationTypes.OPENING);
				}
				
				// 튜토리얼 선택
				//if(mItem3Button.isPushed())
				
				// 옵션 선택
				if(mItem4Button.isPushed())
				{
					MenuState.ChangeState(new Menu_OptionState());
					MenuState.setAnimationType(AnimationTypes.OPENING);
				}
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
			
			spriteBatcher.drawBitmap2(R.drawable.mainmenu_background, 0, 0, null);
			
			if(mStartAllowed == false)
				return;
			
			i = i + 30;
			if(i > 255)
				i = 255;
			
			p.setAlpha(i);
			spriteBatcher.drawBitmap2(R.drawable.mainmenu_sign, 110+i/5-255/5, 0, p);
			
			if(i5 < 255 && i == 255)
			{
				//if(i > 140)
					
				{
					i1 = i1 + 30;
					if(i1 > 255)
						i1 = 255;
					
					p.setAlpha(i1);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item1_n, 430+255/2-i1/2, 40, p);
				}
				
				if(i1 > 50)//i > 170)
				{
					i2 = i2 + 30;
					if(i2 > 255)
						i2 = 255;
					
					p.setAlpha(i2);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item2_n, 410+255/2-i2/2, 110, p);
				}
				
				if(i1 > 100)//i > 200)
				{
					i3 = i3 + 30;
					if(i3 > 255)
						i3 = 255;
					
					p.setAlpha(i3);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item3_n, 390+255/2-i3/2, 180, p);
				}
				
				if(i1 > 150)//i > 230)
				{
					i4 = i4 + 30;
					if(i4 > 255)
						i4 = 255;
					
					p.setAlpha(i4);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item4_n, 370+255/2-i4/2, 250, p);
				}
				
				if(i1 > 200)//i > 250)
				{
					i5 = i5 + 30;
					if(i5 > 255)
						i5 = 255;
					
					p.setAlpha(i5);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item5_n, 350+255/2-i5/2, 320, p);
				}
			}
			
			if(i5 == 255)
			{
				sInfoButton.SetReplyTime(System.currentTimeMillis()-1500, 3500);
				mItem1Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
				mItem2Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
				mItem3Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
				mItem4Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
				mItem5Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
				MenuState.setAnimationType(AnimationTypes.NONE);
			}
			
			break;
		}
		case NONE:
		{
			spriteBatcher.drawBitmap2(R.drawable.mainmenu_background, 0, 0, null);
			spriteBatcher.drawBitmap2(R.drawable.mainmenu_sign, 110, 0, null);
			
			//spriteBatcher.drawLine(R.drawable.mainmenu_background, new Rect(0, 0, 640, 400), 50, 50, 200, 300, 3);

			sInfoButton.onDrawFrame(gl, spriteBatcher);
			
			mItem1Button.onDrawFrame(gl, spriteBatcher);
			mItem2Button.onDrawFrame(gl, spriteBatcher);
			mItem3Button.onDrawFrame(gl, spriteBatcher);
			mItem4Button.onDrawFrame(gl, spriteBatcher);
			mItem5Button.onDrawFrame(gl, spriteBatcher);
		
			break;
		}
		case CLOSING:
		{
			spriteBatcher.drawBitmap2(R.drawable.mainmenu_background, 0, 0, null);
			
			if(i5 >= 0 && i == 255)
			{
				//if(i > 140)
					
				{
					i1 = i1 - 30;
					if(i1 < 0)
						i1 = 0;
					
					p.setAlpha(i1);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item5_n, 350+255/2-i1/2, 320, p);
				}
				
				if(i1 < 200)//i > 170)
				{
					i2 = i2 - 30;
					if(i2 < 0)
						i2 = 0;
					
					p.setAlpha(i2);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item4_n, 370+255/2-i2/2, 250, p);
				}
				else
					spriteBatcher.drawBitmap(R.drawable.mainmenu_item4_n, 370, 250, null);
				
				if(i1 < 150)//i > 200)
				{
					i3 = i3 - 30;
					if(i3 < 0)
						i3 = 0;
					
					p.setAlpha(i3);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item3_n, 390+255/2-i3/2, 180, p);
				}
				else
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item3_n, 390, 180, null);
				
				if(i1 < 100)//i > 230)
				{
					i4 = i4 - 30;
					if(i4 < 0)
						i4 = 0;
					
					p.setAlpha(i4);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item2_n, 410+255/2-i4/2, 110, p);
				}
				else
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item2_n, 410, 110, null);
				
				if(i1 < 50)//i > 250)
				{
					i5 = i5 - 30;
					if(i5 < 0)
						i5 = 0;
					
					p.setAlpha(i5);
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item1_n, 430+255/2-i5/2, 40, p);
				}
				else
					spriteBatcher.drawBitmap2(R.drawable.mainmenu_item1_n, 430, 40, null);
			}
			
			if(i5 == 0)
			{
				i = i - 30;
				if(i < 0)
					i = 0;
				
				p.setAlpha(i);
				spriteBatcher.drawBitmap2(R.drawable.mainmenu_sign, 110+i/5-255/5, 0, p);
			}
			else
				spriteBatcher.drawBitmap2(R.drawable.mainmenu_sign, 110, 0, null);
			
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
		{
			new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
		    .setTitle("게임을 정말 종료하겠습니까?")
		    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					AppManager.getInstance().getActivity().moveTaskToBack(true);
					AppManager.getInstance().getActivity().finish();
					System.exit(0);
					ActivityManager activityManager = (ActivityManager) AppManager.getInstance().getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
					activityManager.restartPackage(AppManager.getInstance().getActivity().getPackageName());
				}

			})
		    .setNegativeButton("취소", null)
		    .show();
		}
		
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
			if(sInfoButton.onTouchEvent(event))
			{
				sInfoButton.setPushed(false);
				new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
			    .setTitle("Capitalism Mobile")
			    .setIcon(R.drawable.ic_launcher)
			    .setMessage("기획 : 김현우\n" + 
						    		"프로그래밍 : 김현우\n" + 
						    		"그래픽 : 김현우\n" + 
						    		"메일 : kis7385@naver.com\n\n" +
						    		"원작 : 캐피탈리즘2(PC)")
			    .setPositiveButton("확인", null)
			    .show();
			}
		
			if(mItem1Button.onTouchEvent(event))
				MenuState.setAnimationType(AnimationTypes.CLOSING);
			
			if(mItem2Button.onTouchEvent(event))
				MenuState.setAnimationType(AnimationTypes.CLOSING);
			/*{
				mItem2Button.setPushed(false);
				new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
			    .setTitle("준비중입니다")
			    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}

				})
			    .show();
			}*/
			
			if(mItem3Button.onTouchEvent(event))
			{
				mItem3Button.setPushed(false);
				new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
			    .setTitle("준비중입니다")
			    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}

				})
			    .show();
			}
			
			if(mItem4Button.onTouchEvent(event))
				MenuState.setAnimationType(AnimationTypes.CLOSING);

			if(mItem5Button.onTouchEvent(event))
			{
				mItem5Button.setPushed(false);
				new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
			    .setTitle("게임을 정말 종료하겠습니까?")
			    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						AppManager.getInstance().getActivity().moveTaskToBack(true);
						AppManager.getInstance().getActivity().finish();
						System.exit(0);
						ActivityManager activityManager = (ActivityManager) AppManager.getInstance().getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
						activityManager.restartPackage(AppManager.getInstance().getActivity().getPackageName());
					}

				})
			    .setNegativeButton("취소", null)
			    .show();
			}
			
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
