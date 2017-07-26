package org.game.department;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.screen.layer.component.*;

import android.content.res.*;
import android.graphics.*;
import android.graphics.Paint.*;

public abstract class Department {
	public enum DepartmentTypes 
	{
		PURCHASE(AppManager.getInstance().getResources().getString(R.string.purchase)), 
		SALES(AppManager.getInstance().getResources().getString(R.string.sales)), 
		ADVERTISE(AppManager.getInstance().getResources().getString(R.string.advertise)),
		MANUFACTURE(AppManager.getInstance().getResources().getString(R.string.manufacture)),
		LABORATORY(AppManager.getInstance().getResources().getString(R.string.laboratory)),
		LIVESTOCK(AppManager.getInstance().getResources().getString(R.string.livestock)),
		PROCESS(AppManager.getInstance().getResources().getString(R.string.process));
		DepartmentTypes(String value) { m_value = value; }
		public String GetString() { return m_value; }
		private String m_value;
	}
	
	public DepartmentTypes m_department_type;						// 부서 타입
	public int m_employee;														// 직원 수
	// 인덱스
	// 0   1   2
	// 3   4   5
	// 6   7   8
	public int m_index;															// 부서 인덱스
	public int m_level;																// 부서 레벨
	public boolean m_is_done;
	
	public Bitmap m_department_bitmap;
	
	// 이 부서와 링크로 연결된 모든 부서를 저장하는 리스트
	public ArrayList<Department> m_department_list = new ArrayList<Department>();
	
	public DepartmentManager m_department_manager;
	
	public static Bitmap m_commodity_empty_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.department_commodity_empty);
	public static Bitmap m_advertise_building_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.department_advertise_building);
	public static Bitmap m_division_bar_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.department_division_bar);
	public static Bitmap m_department_title_bar_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.department_title_bar);
	public static Bitmap m_department_content_bar_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.department_content_bar);
	public static Bitmap m_connection_button_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.department_connection_button);
	
	public static Button mResearchButton;
	
	public static Bitmap m_employee_untrained_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.employee_untrained);

	public Department(int index, Bitmap bitmap, DepartmentManager department_manager) {
		// TODO Auto-generated constructor stub
		m_department_bitmap = bitmap;
		m_department_manager = department_manager;
		m_index = index;
		m_level = 1;
	}
	
	public static void InitStatic()
	{
		m_commodity_empty_bitmap = AppManager.getInstance().getBitmap( R.drawable.department_commodity_empty, Utility.sOptions);
		m_advertise_building_bitmap = AppManager.getInstance().getBitmap( R.drawable.department_advertise_building, Utility.sOptions);
		m_division_bar_bitmap = AppManager.getInstance().getBitmap( R.drawable.department_division_bar, Utility.sOptions);
		m_department_title_bar_bitmap = AppManager.getInstance().getBitmap( R.drawable.department_title_bar, Utility.sOptions);
		m_department_content_bar_bitmap = AppManager.getInstance().getBitmap( R.drawable.department_content_bar, Utility.sOptions);
		m_connection_button_bitmap = AppManager.getInstance().getBitmap( R.drawable.department_connection_button, Utility.sOptions);
		
		m_employee_untrained_bitmap = AppManager.getInstance().getBitmap( R.drawable.employee_untrained, Utility.sOptions);
		
		mResearchButton = new Button(new Point(150, 125), new Rect(150, 125, 150+85, 125+25), 
				R.drawable.button_up_85, true, true);
		
		mResearchButton.setVisiable(true);
		mResearchButton.setTouchedBitmap(R.drawable.button_down_85);
		mResearchButton.setPushedBitmap(R.drawable.button_down_85);
		
		Paint p = new Paint();
    	p.setTextSize(15); 
    	p.setColor(Color.BLACK);
    	p.setAntiAlias(true);
    	p.setTextAlign(Align.CENTER);
		
    	mResearchButton.setString("기술개발");
    	mResearchButton.setStringNormalPos(new Point(44, 16));
    	mResearchButton.setNormalPaint(p);
    	mResearchButton.setStringTouchedPos(new Point(44, 17));
    	mResearchButton.setTouchedPaint(p);
    	mResearchButton.setStringPushedPos(new Point(44, 17));
    	mResearchButton.setPushedPaint(p);
    	mResearchButton.setStringEnabled(true);
	}
	
	public abstract double Process();
	
	public abstract void Render(Canvas canvas);

}