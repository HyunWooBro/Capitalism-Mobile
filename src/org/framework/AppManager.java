package org.framework;

import org.game.*;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.*;

public class AppManager {
	
		// Menu_MainState GameView
		private GameView m_gameview;
		// Menu_MainState GameView 의 Resources
		private Resources m_resources;
		// Application 의 Activity
		private Activity m_activity;
		

		// GameState
		public IState m_state;
		
		private int m_width;
		private int m_height;
		
		public void setGameView(GameView _gameview){
			m_gameview = _gameview;
		}
		public void setResources(Resources _resources ){
			m_resources = _resources;
		}
		public void setActivity(Activity _activity){
			m_activity = _activity;
		}
		public GameView getGameView(){
			return m_gameview;
		}
		public Resources getResources(){
			return m_resources;
		}
		public Activity getActivity(){
			return m_activity;
		}
		
		public void setSize(int x,int y){
			m_width = x;
			m_height = y;
		}
		public int getWidth(){
			return m_width;
		}
		
		public int getHeight(){
			return m_height;
		}
		
		public Bitmap getBitmap(int r){
			return BitmapFactory.decodeResource(m_resources,r);
		}
		
		public Bitmap getBitmap(int r, Options options){
			return BitmapFactory.decodeResource(m_resources, r, options);
		}
		
		private static AppManager s_instance;
		
		public static AppManager getInstance(){
			if(s_instance == null){
				s_instance = new AppManager();
			}
			return s_instance;
		}
		
	
	}
