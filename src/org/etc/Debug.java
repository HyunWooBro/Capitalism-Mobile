package org.etc;

import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.framework.*;
import org.framework.openGL.*;
import org.game.*;
import org.game.commodity.*;

import android.R.string;
import android.graphics.*;
import android.util.*;

public class Debug {
	
	// 싱글턴
	private static Debug s_instance;
	private Debug() {}
	public static Debug GetInstance(){
		if(s_instance == null){
			s_instance = new Debug();
		}
		return s_instance;  
	}
	
	//private ArrayList<String> m_array = new ArrayList<String>();
		
	private final int DEBUG_X = 100;
	private final int DEBUG_Y = 20;
	
	
	public static int mFPS;
	public static int mFrameCount;
	public static long mStartFPSTime;
	public static long mNextFPSTime;
	
	//private static int offset = 0;
	
	/*public void Init(GameState gamestate)
	{
		 ;
		m_array.add("off x:"+gamestate.off_x+" y:"+gamestate.off_y);
	}
	
	private void AddItem(string str)
	{
		
	}*/
	
	public void Render(Canvas canvas)
	{
		GameState gamestate = (GameState) AppManager.getInstance().m_state;
		
		int mWidth = AppManager.getInstance().getResources().getDisplayMetrics().widthPixels;
		int mHeight = AppManager.getInstance().getResources().getDisplayMetrics().heightPixels;
		
		long GameTime = System.currentTimeMillis();
		
		mFrameCount++;
		if(GameTime > mNextFPSTime)
		{
			mNextFPSTime = GameTime + 1000;
			mFPS = mFrameCount;
			mFrameCount = 0;
		}
		
		Paint p = new Paint();
    	p.setTextSize(10); p.setColor(Color.MAGENTA);
    	
    	canvas.save();
    	{
    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("해상도 x:" +mWidth+" y:"+mHeight, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("dpi :"+AppManager.getInstance().getResources().getDisplayMetrics().densityDpi, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("off x:"+gamestate.off_x+" y:"+gamestate.off_y, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	if(gamestate.m_UI.constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
	    		canvas.drawText("constructiontype : RETAIL", DEBUG_X, DEBUG_Y, p);
	    	if(gamestate.m_UI.constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
	    		canvas.drawText("constructiontype : FACTORY", DEBUG_X, DEBUG_Y, p);
	    	if(gamestate.m_UI.constructiontype == UserInterface.ToolbarConstructionTypes.NOTHING)
	    		canvas.drawText("constructiontype : NOTHING", DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("selection :"+gamestate.selection, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("selection2 :"+gamestate.selection2, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("negative_profit :"+CapitalismSystem.m_max_daily_negative_profit, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("FPS :"+mFPS, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("isReadySever :"+GameActivity.isReadySever, DEBUG_X, DEBUG_Y, p);
    	
    	}
    	canvas.restore();
    	
    	
    	//canvas.drawText("maxium day :"+Time.GetInstance().GetCalendar().getActualMaximum(Calendar.DAY_OF_MONTH), DEBUG_X, DEBUG_Y+120, p);
	}
	
	public void Render2(Canvas canvas)
	{
		int mWidth = AppManager.getInstance().getResources().getDisplayMetrics().widthPixels;
		int mHeight = AppManager.getInstance().getResources().getDisplayMetrics().heightPixels;
		
		long GameTime = System.currentTimeMillis();
		
		mFrameCount++;
		if(GameTime > mNextFPSTime)
		{
			mNextFPSTime = GameTime + 1000;
			mFPS = mFrameCount;
			mFrameCount = 0;
		}
		
		Paint p = new Paint();
    	p.setTextSize(10); p.setColor(Color.MAGENTA);
    	
    	canvas.save();
    	{
    		canvas.translate(0, 10);
	    	canvas.drawText("해상도 x:" +mWidth+" y:"+mHeight, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("dpi :"+AppManager.getInstance().getResources().getDisplayMetrics().densityDpi, DEBUG_X, DEBUG_Y, p);
	    	
	    	canvas.translate(0, 10);
	    	canvas.drawText("FPS :"+mFPS, DEBUG_X, DEBUG_Y, p);

    	}
    	canvas.restore();
    	
    	
    	//canvas.drawText("maxium day :"+Time.GetInstance().GetCalendar().getActualMaximum(Calendar.DAY_OF_MONTH), DEBUG_X, DEBUG_Y+120, p);
	}
	
	public static int LogFPS() {
		// TODO Auto-generated method stub
		
		long GameTime = System.currentTimeMillis();
		
		mFrameCount++;
		if(GameTime > mNextFPSTime)
		{
			mNextFPSTime = GameTime + 1000;
			mFPS = mFrameCount;
			mFrameCount = 0;
		}
		
		Log.e(Utility.TAG, "FPS : " + mFPS);
		
		return mFPS;
	}
}
