package org.game.department;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.commodity.*;
import org.game.construction.*;
import org.game.construction.Construction.ConstructionTabTypes;
import org.game.construction.Construction.ConstructionTypes;
import org.game.department.Department.DepartmentTypes;
import org.screen.*;
import org.screen.layer.component.Button;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.util.*;
import android.view.*;
import android.webkit.WebView.*;
import android.widget.*;

public class DepartmentManager {
	public Department m_departments[] = new Department[DEPARTMENT_COUNT];
	
	public int m_links[] = new int[LINK_COUNT];
	
	public static int m_department_select = -1;	// 선택한 부서
	
	public static final int LINK_COUNT = 16;
	public static final int DEPARTMENT_COUNT = 9;
	
	public static int s_summary_index;;
	
	// 각 부서 위치
	public static Rect m_department_rect[] = new Rect[DEPARTMENT_COUNT];
	
	// 각 링크 위치
	public static Rect m_link_rect[] = new Rect[LINK_COUNT];
	
	public static Bitmap m_department_select_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.department_selector);
	public static Bitmap m_summary_selector_bitmap;// = AppManager.getInstance().getBitmap( R.drawable.summary_selector);
	
	public static Bitmap m_department_purchase_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.department_purchase);
	public static Bitmap m_department_sales_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.department_sales);
	public static Bitmap m_department_advertise_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.department_advertise);
	public static Bitmap m_department_laboratory_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.department_laboratory);
	public static Bitmap m_department_livestock_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.department_livestock);
	public static Bitmap m_department_process_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.department_process);
	public static Bitmap m_department_manufacture_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.department_manufacture);
	
	
	public int m_select;
	public int m_progress;
	
	public Department m_previous_department;
	
	public Construction m_construction;

	private static Button mOnePlusButton;
	private static Button mOneMinusButton;
	private static Button mDoublePlusButton;
	private static Button mDoubleMinusButton;
	private static Button mConfirmButton;
	
	public DepartmentManager(Construction construction) {
		// TODO Auto-generated constructor stub
		
		m_construction = construction;
		
		Resources resources = AppManager.getInstance().getResources();

		for(int i=0; i<m_link_rect.length; i++)
			m_link_rect[i] = new Rect(
					resources.getIntArray(R.array.LINK_0+i)[0]*2/2,
					resources.getIntArray(R.array.LINK_0+i)[1]*2/2,
					resources.getIntArray(R.array.LINK_0+i)[2]*2/2,
					resources.getIntArray(R.array.LINK_0+i)[3]*2/2);

		for(int i=0; i<m_department_rect.length; i++)
			m_department_rect[i] = new Rect(
					resources.getIntArray(R.array.DEPARTMENT_0+i)[0]*2/2, 
					resources.getIntArray(R.array.DEPARTMENT_0+i)[1]*2/2, 
					resources.getIntArray(R.array.DEPARTMENT_0+i)[2]*2/2, 
					resources.getIntArray(R.array.DEPARTMENT_0+i)[3]*2/2);
	}
	
	public static void InitStatic()
	{
		m_department_select_bitmap = AppManager.getInstance().getBitmap( R.drawable.department_selector, Utility.sOptions);
		m_summary_selector_bitmap = AppManager.getInstance().getBitmap( R.drawable.summary_selector, Utility.sOptions);
		
		m_department_purchase_bitmap = AppManager.getInstance().getBitmap(R.drawable.department_purchase, Utility.sOptions);
		m_department_sales_bitmap = AppManager.getInstance().getBitmap(R.drawable.department_sales, Utility.sOptions);
		m_department_advertise_bitmap = AppManager.getInstance().getBitmap(R.drawable.department_advertise, Utility.sOptions);
		m_department_laboratory_bitmap = AppManager.getInstance().getBitmap(R.drawable.department_laboratory, Utility.sOptions);
		m_department_livestock_bitmap = AppManager.getInstance().getBitmap(R.drawable.department_livestock, Utility.sOptions);
		m_department_process_bitmap = AppManager.getInstance().getBitmap(R.drawable.department_process, Utility.sOptions);
		m_department_manufacture_bitmap = AppManager.getInstance().getBitmap(R.drawable.department_manufacture, Utility.sOptions);
		
		mDoublePlusButton = new Button(new Point(150, 275), new Rect(150, 275, 150 + 23, 275 + 23), 
				R.drawable.double_plus_button, true, true);
		mOnePlusButton = new Button(new Point(185, 275), new Rect(185, 275, 185 + 23, 275 + 23), 
				R.drawable.one_plus_button, true, true);
		mOneMinusButton = new Button(new Point(220, 275), new Rect(220, 275, 220 + 23, 275 + 23), 
				R.drawable.one_minus_button, true, true);
		mDoubleMinusButton = new Button(new Point(255, 275), new Rect(255, 275, 255 + 23, 275 + 23), 
				R.drawable.double_minus_button, true, true);
		mConfirmButton = new Button(new Point(290, 275), new Rect(290, 275, 290 + 56, 275 + 24), 
				R.drawable.summary_confirm_button, true, true);
		
		mDoublePlusButton.setVisiable(true);
		mOnePlusButton.setVisiable(true);
		mOneMinusButton.setVisiable(true);
		mDoubleMinusButton.setVisiable(true);
		mConfirmButton.setVisiable(true);

	}
	
	private void ChangeDepartment(Department department)
	{
		Log.i("abc", "ChangeDepartment 진입");
		
		// list에 등록하고 연결 설정하고
		int _index;
		
		if(m_department_select == 0)
		{
			if(department != null)
			{
				if(m_links[0] == 1 && m_departments[1] != null)
				{
					m_departments[0].m_department_list.add(m_departments[1]);
					m_departments[1].m_department_list.add(m_departments[0]);
				}
				
				if(m_links[2] == 1 && m_departments[3] != null)
				{
					m_departments[0].m_department_list.add(m_departments[3]);
					m_departments[3].m_department_list.add(m_departments[0]);
				}
				
				if((m_links[3] == 1 || m_links[3] == 3) && m_departments[4] != null)
				{
					m_departments[0].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[0]);
				}
			}
			else // department == null
			{
				if(m_links[0] == 1 && m_departments[1] != null)
				{
					m_departments[1].m_department_list.remove(m_departments[0]);
				}
				
				if(m_links[2] == 1 && m_departments[3] != null)
				{
					m_departments[3].m_department_list.remove(m_departments[0]);
				}
				
				if((m_links[3] == 1 || m_links[3] == 3) && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[0]);
				}
			}
		}
		
		if(m_department_select == 1)
		{
			if(department != null)
			{
				if(m_links[0] == 1 && m_departments[0] != null)
				{
					m_departments[1].m_department_list.add(m_departments[0]);
					m_departments[0].m_department_list.add(m_departments[1]);
				}
				
				if(m_links[1] == 1 && m_departments[2] != null)
				{
					m_departments[1].m_department_list.add(m_departments[2]);
					m_departments[2].m_department_list.add(m_departments[1]);
				}
				
				if(m_links[4] == 1 && m_departments[4] != null)
				{
					m_departments[1].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[1]);
				}
				
				if((m_links[3] == 2 || m_links[3] == 3) && m_departments[3] != null)
				{
					m_departments[1].m_department_list.add(m_departments[3]);
					m_departments[3].m_department_list.add(m_departments[1]);
				}
				
				if((m_links[5] == 1 || m_links[5] == 3) && m_departments[5] != null)
				{
					m_departments[1].m_department_list.add(m_departments[5]);
					m_departments[5].m_department_list.add(m_departments[1]);
				}
			}
			else // department == null
			{
				if(m_links[0] == 1 && m_departments[0] != null)
				{
					m_departments[0].m_department_list.remove(m_departments[1]);
				}
				
				if(m_links[1] == 1 && m_departments[2] != null)
				{
					m_departments[2].m_department_list.remove(m_departments[1]);
				}
				
				if(m_links[4] == 1 && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[1]);
				}
				
				if((m_links[3] == 2 || m_links[3] == 3) && m_departments[3] != null)
				{
					m_departments[3].m_department_list.remove(m_departments[1]);
				}
				
				if((m_links[5] == 1 || m_links[5] == 3) && m_departments[5] != null)
				{
					m_departments[5].m_department_list.remove(m_departments[1]);
				}
			}
		}
		
		if(m_department_select == 2)
		{
			if(department != null)
			{
				if(m_links[1] == 1 && m_departments[1] != null)
				{
					m_departments[2].m_department_list.add(m_departments[1]);
					m_departments[1].m_department_list.add(m_departments[2]);
				}
				
				if(m_links[6] == 1 && m_departments[5] != null)
				{
					m_departments[2].m_department_list.add(m_departments[5]);
					m_departments[5].m_department_list.add(m_departments[2]);
				}
				
				if((m_links[5] == 2 || m_links[5] == 3) && m_departments[4] != null)
				{
					m_departments[2].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[2]);
				}
			}
			else // department == null
			{
				if(m_links[1] == 1 && m_departments[1] != null)
				{
					m_departments[1].m_department_list.remove(m_departments[2]);
				}
				
				if(m_links[6] == 1 && m_departments[5] != null)
				{
					m_departments[5].m_department_list.remove(m_departments[2]);
				}
				
				if((m_links[5] == 2 || m_links[5] == 3) && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[2]);
				}
			}
		}
		
		if(m_department_select == 3)
		{
			if(department != null)
			{
				if(m_links[2] == 1 && m_departments[0] != null)
				{
					m_departments[3].m_department_list.add(m_departments[0]);
					m_departments[0].m_department_list.add(m_departments[3]);
				}
				
				if(m_links[7] == 1 && m_departments[4] != null)
				{
					m_departments[3].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[3]);
				}
				
				if(m_links[9] == 1 && m_departments[6] != null)
				{
					m_departments[3].m_department_list.add(m_departments[6]);
					m_departments[6].m_department_list.add(m_departments[3]);
				}
				
				if((m_links[3] == 2 || m_links[3] == 3) && m_departments[1] != null)
				{
					m_departments[3].m_department_list.add(m_departments[1]);
					m_departments[1].m_department_list.add(m_departments[3]);
				}
				
				if((m_links[10] == 1 || m_links[10] == 3) && m_departments[7] != null)
				{
					m_departments[3].m_department_list.add(m_departments[7]);
					m_departments[7].m_department_list.add(m_departments[3]);
				}
			}
			else // department == null
			{
				if(m_links[2] == 1 && m_departments[0] != null)
				{
					m_departments[0].m_department_list.remove(m_departments[3]);
				}
				
				if(m_links[7] == 1 && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[3]);
				}
				
				if(m_links[9] == 1 && m_departments[6] != null)
				{
					m_departments[6].m_department_list.remove(m_departments[3]);
				}
				
				if((m_links[3] == 2 || m_links[3] == 3) && m_departments[1] != null)
				{
					m_departments[1].m_department_list.remove(m_departments[3]);
				}
				
				if((m_links[10] == 1 || m_links[10] == 3) && m_departments[7] != null)
				{
					m_departments[7].m_department_list.remove(m_departments[3]);
				}
			}
		}
		
		if(m_department_select == 4)
		{
			if(department != null)
			{
				if(m_links[4] == 1 && m_departments[1] != null)
				{
					m_departments[4].m_department_list.add(m_departments[1]);
					m_departments[1].m_department_list.add(m_departments[4]);
				}
				
				if(m_links[7] == 1 && m_departments[3] != null)
				{
					m_departments[4].m_department_list.add(m_departments[3]);
					m_departments[3].m_department_list.add(m_departments[4]);
				}
				
				if(m_links[8] == 1 && m_departments[5] != null)
				{
					m_departments[4].m_department_list.add(m_departments[5]);
					m_departments[5].m_department_list.add(m_departments[4]);
				}
				
				if(m_links[11] == 1 && m_departments[7] != null)
				{
					m_departments[4].m_department_list.add(m_departments[7]);
					m_departments[7].m_department_list.add(m_departments[4]);
				}
				
				if((m_links[3] == 1 || m_links[3] == 3) && m_departments[0] != null)
				{
					m_departments[4].m_department_list.add(m_departments[0]);
					m_departments[0].m_department_list.add(m_departments[4]);
				}
				
				if((m_links[5] == 2 || m_links[5] == 3) && m_departments[2] != null)
				{
					m_departments[4].m_department_list.add(m_departments[2]);
					m_departments[2].m_department_list.add(m_departments[4]);
				}
				
				if((m_links[10] == 2 || m_links[10] == 3) && m_departments[6] != null)
				{
					m_departments[4].m_department_list.add(m_departments[6]);
					m_departments[6].m_department_list.add(m_departments[4]);
				}
				
				if((m_links[12] == 1 || m_links[12] == 3) && m_departments[7] != null)
				{
					m_departments[4].m_department_list.add(m_departments[8]);
					m_departments[8].m_department_list.add(m_departments[4]);
				}
			}
			else // department == null
			{
				if(m_links[4] == 1 && m_departments[1] != null)
				{
					m_departments[1].m_department_list.remove(m_departments[4]);
				}
				
				if(m_links[7] == 1 && m_departments[3] != null)
				{
					m_departments[3].m_department_list.remove(m_departments[4]);
				}
				
				if(m_links[8] == 1 && m_departments[5] != null)
				{
					m_departments[5].m_department_list.remove(m_departments[4]);
				}
				
				if(m_links[11] == 1 && m_departments[7] != null)
				{
					m_departments[7].m_department_list.remove(m_departments[4]);
				}
				
				if((m_links[3] == 1 || m_links[3] == 3) && m_departments[0] != null)
				{
					m_departments[0].m_department_list.remove(m_departments[4]);
				}
				
				if((m_links[5] == 2 || m_links[5] == 3) && m_departments[2] != null)
				{
					m_departments[2].m_department_list.remove(m_departments[4]);
				}
				
				if((m_links[10] == 2 || m_links[10] == 3) && m_departments[6] != null)
				{
					m_departments[6].m_department_list.remove(m_departments[4]);
				}
				
				if((m_links[12] == 1 || m_links[12] == 3) && m_departments[7] != null)
				{
					m_departments[8].m_department_list.remove(m_departments[4]);
				}
			}
		}
		
		if(m_department_select == 5)
		{
			if(department != null)
			{
				if(m_links[6] == 1 && m_departments[2] != null)
				{
					m_departments[5].m_department_list.add(m_departments[2]);
					m_departments[2].m_department_list.add(m_departments[5]);
				}
				
				if(m_links[8] == 1 && m_departments[4] != null)
				{
					m_departments[5].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[5]);
				}
				
				if(m_links[13] == 1 && m_departments[8] != null)
				{
					m_departments[5].m_department_list.add(m_departments[8]);
					m_departments[8].m_department_list.add(m_departments[5]);
				}
				
				if((m_links[5] == 1 || m_links[5] == 3) && m_departments[1] != null)
				{
					m_departments[5].m_department_list.add(m_departments[1]);
					m_departments[1].m_department_list.add(m_departments[5]);
				}
				
				if((m_links[12] == 2 || m_links[12] == 3) && m_departments[7] != null)
				{
					m_departments[5].m_department_list.add(m_departments[7]);
					m_departments[7].m_department_list.add(m_departments[5]);
				}
			}
			else // department == null
			{
				if(m_links[6] == 1 && m_departments[2] != null)
				{
					m_departments[2].m_department_list.remove(m_departments[5]);
				}
				
				if(m_links[8] == 1 && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[5]);
				}
				
				if(m_links[13] == 1 && m_departments[8] != null)
				{
					m_departments[8].m_department_list.remove(m_departments[5]);
				}
				
				if((m_links[5] == 1 || m_links[5] == 3) && m_departments[1] != null)
				{
					m_departments[1].m_department_list.remove(m_departments[5]);
				}
				
				if((m_links[12] == 2 || m_links[12] == 3) && m_departments[7] != null)
				{
					m_departments[7].m_department_list.remove(m_departments[5]);
				}
			}
		}
		
		if(m_department_select == 6)
		{
			if(department != null)
			{
				if(m_links[9] == 1 && m_departments[3] != null)
				{
					m_departments[6].m_department_list.add(m_departments[3]);
					m_departments[3].m_department_list.add(m_departments[6]);
				}
				
				if(m_links[14] == 1 && m_departments[7] != null)
				{
					m_departments[6].m_department_list.add(m_departments[7]);
					m_departments[7].m_department_list.add(m_departments[6]);
				}
				
				if((m_links[10] == 2 || m_links[10] == 3) && m_departments[4] != null)
				{
					m_departments[6].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[6]);
				}
			}
			else // department == null
			{
				if(m_links[9] == 1 && m_departments[3] != null)
				{
					m_departments[3].m_department_list.remove(m_departments[6]);
				}
				
				if(m_links[14] == 1 && m_departments[7] != null)
				{
					m_departments[7].m_department_list.remove(m_departments[6]);
				}
				
				if((m_links[10] == 2 || m_links[10] == 3) && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[6]);
				}
			}
		}
		
		if(m_department_select == 7)
		{
			if(department != null)
			{
				if(m_links[11] == 1 && m_departments[4] != null)
				{
					m_departments[7].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[7]);
				}
				
				if(m_links[14] == 1 && m_departments[6] != null)
				{
					m_departments[7].m_department_list.add(m_departments[6]);
					m_departments[6].m_department_list.add(m_departments[7]);
				}
				
				if(m_links[15] == 1 && m_departments[8] != null)
				{
					m_departments[7].m_department_list.add(m_departments[8]);
					m_departments[8].m_department_list.add(m_departments[7]);
				}
				
				if((m_links[10] == 1 || m_links[10] == 3) && m_departments[3] != null)
				{
					m_departments[7].m_department_list.add(m_departments[3]);
					m_departments[3].m_department_list.add(m_departments[7]);
				}
				
				if((m_links[12] == 2 || m_links[12] == 3) && m_departments[5] != null)
				{
					m_departments[7].m_department_list.add(m_departments[5]);
					m_departments[5].m_department_list.add(m_departments[7]);
				}
			}
			else // department == null
			{
				if(m_links[11] == 1 && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[7]);
				}
				
				if(m_links[14] == 1 && m_departments[6] != null)
				{
					m_departments[6].m_department_list.remove(m_departments[7]);
				}
				
				if(m_links[15] == 1 && m_departments[8] != null)
				{
					m_departments[8].m_department_list.remove(m_departments[7]);
				}
				
				if((m_links[10] == 1 || m_links[10] == 3) && m_departments[3] != null)
				{
					m_departments[3].m_department_list.remove(m_departments[7]);
				}
				
				if((m_links[12] == 2 || m_links[12] == 3) && m_departments[5] != null)
				{
					m_departments[5].m_department_list.remove(m_departments[7]);
				}
			}
		}
		
		if(m_department_select == 8)
		{
			if(department != null)
			{
				if(m_links[13] == 1 && m_departments[5] != null)
				{
					m_departments[8].m_department_list.add(m_departments[5]);
					m_departments[5].m_department_list.add(m_departments[8]);
				}
				
				if(m_links[15] == 1 && m_departments[7] != null)
				{
					m_departments[8].m_department_list.add(m_departments[7]);
					m_departments[7].m_department_list.add(m_departments[8]);
				}
				
				if((m_links[12] == 1 || m_links[12] == 3) && m_departments[4] != null)
				{
					m_departments[8].m_department_list.add(m_departments[4]);
					m_departments[4].m_department_list.add(m_departments[8]);
				}
			}
			else // department == null
			{
				if(m_links[13] == 1 && m_departments[5] != null)
				{
					m_departments[5].m_department_list.remove(m_departments[8]);
				}
				
				if(m_links[15] == 1 && m_departments[7] != null)
				{
					m_departments[7].m_department_list.remove(m_departments[8]);
				}
				
				if((m_links[12] == 1 || m_links[12] == 3) && m_departments[4] != null)
				{
					m_departments[4].m_department_list.remove(m_departments[8]);
				}
			}
		}
		
		Log.i("abc", "ChangeDepartment 끝");
	}
	
	private void ChangeLink(int index)
	{
		// 연결 설정
		if(index == 0)
			ChangeSingleLink(index, 0, 1);
		
		if(index == 1)
			ChangeSingleLink(index, 1, 2);
		
		if(index == 2)
			ChangeSingleLink(index, 0, 3);
		
		if(index == 3)
			ChangeDoubleLink(index, 0, 4, 1, 3);
		
		if(index == 4)
			ChangeSingleLink(index, 1, 4);
		
		if(index == 5)
			ChangeDoubleLink(index, 1, 5, 2, 4);
		
		if(index == 6)
			ChangeSingleLink(index, 2, 5);
		
		if(index == 7)
			ChangeSingleLink(index, 3, 4);
		
		if(index == 8)
			ChangeSingleLink(index, 4, 5);
		
		if(index == 9)
			ChangeSingleLink(index, 3, 6);
		
		if(index == 10)
			ChangeDoubleLink(index, 3, 7, 4, 6);
		
		if(index == 11)
			ChangeSingleLink(index, 4, 7);
		
		if(index == 12)
			ChangeDoubleLink(index, 4, 8, 5, 7);
		
		if(index == 13)
			ChangeSingleLink(index, 5, 8);
		
		if(index == 14)
			ChangeSingleLink(index, 6, 7);
		
		if(index == 15)
			ChangeSingleLink(index, 7, 8);
		
	}
	
	private void ChangeSingleLink(int link_index, int dept_index1, int dept_index2)
	{
		if(m_links[link_index] == 1)
		{
			if(m_departments[dept_index1] != null && m_departments[dept_index2] != null)
			{
				m_departments[dept_index1].m_department_list.add(m_departments[dept_index2]);
				m_departments[dept_index2].m_department_list.add(m_departments[dept_index1]);
			}
		}
		else // m_links[index] == 0
		{
			if(m_departments[dept_index1] != null && m_departments[dept_index2] != null)
			{
				m_departments[dept_index1].m_department_list.remove(m_departments[dept_index2]);
				m_departments[dept_index2].m_department_list.remove(m_departments[dept_index1]);
			}
		}
	}
	
	private void ChangeDoubleLink(int link_index, int dept_index1, int dept_index2, int dept_index3, int dept_index4)
	{
		if(m_links[link_index] == 1)
		{
			if(m_departments[dept_index1] != null && m_departments[dept_index2] != null)
			{
				m_departments[dept_index1].m_department_list.add(m_departments[dept_index2]);
				m_departments[dept_index2].m_department_list.add(m_departments[dept_index1]);
			}
		}
		else if(m_links[link_index] == 2)
		{
			if(m_departments[dept_index3] != null && m_departments[dept_index4] != null)
			{
				m_departments[dept_index3].m_department_list.add(m_departments[dept_index4]);
				m_departments[dept_index4].m_department_list.add(m_departments[dept_index3]);
			}
			
			if(m_departments[dept_index1] != null && m_departments[dept_index2] != null)
			{
				m_departments[dept_index1].m_department_list.remove(m_departments[dept_index2]);
				m_departments[dept_index2].m_department_list.remove(m_departments[dept_index1]);
			}
		}
		else if(m_links[link_index] == 3)
		{
			if(m_departments[dept_index1] != null && m_departments[dept_index2] != null)
			{
				m_departments[dept_index1].m_department_list.add(m_departments[dept_index2]);
				m_departments[dept_index2].m_department_list.add(m_departments[dept_index1]);
			}
		}
		else // m_links[index] == 0
		{
			if(m_departments[dept_index1] != null && m_departments[dept_index2] != null)
			{
				m_departments[dept_index1].m_department_list.remove(m_departments[dept_index2]);
				m_departments[dept_index2].m_department_list.remove(m_departments[dept_index1]);
			}
			
			if(m_departments[dept_index3] != null && m_departments[dept_index4] != null)
			{
				m_departments[dept_index3].m_department_list.remove(m_departments[dept_index4]);
				m_departments[dept_index4].m_department_list.remove(m_departments[dept_index3]);
			}
		}
	}

	public boolean onTouchEvent(MotionEvent event) 
	{
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		// 요약탭인 경우
		if(Construction.s_tabtype == ConstructionTabTypes.SUMMARY)
		{
			int index = 0;
			
			for(int i=0; i<DepartmentManager.DEPARTMENT_COUNT; i++)
				if(m_departments[i] != null)
					if(m_departments[i].m_department_type == DepartmentTypes.SALES
							&& ((Sales)m_departments[i]).m_commodity != null)
					{

						if(s_summary_index == index)
						{
							Sales sales = (Sales)m_departments[i];
							
							double salesPrice = (sales.m_commodity.m_baseprice + sales.m_commodity.m_baseprice*1.2)/100;
			
							if(mDoublePlusButton.onTouchEvent(event))
							{
								sales.mTempNewPrice += (salesPrice*5);
							}
							if(mOnePlusButton.onTouchEvent(event))
							{
								sales.mTempNewPrice += (salesPrice*1);
							}
							if(mOneMinusButton.onTouchEvent(event))
							{
								sales.mTempNewPrice -= (salesPrice*1);
							}
							if(mDoubleMinusButton.onTouchEvent(event))
							{
								sales.mTempNewPrice -= (salesPrice*5);
							}
							if(mConfirmButton.onTouchEvent(event))
							{
								sales.mNewPrice = sales.mTempNewPrice;
							}
							
							if(sales.mTempNewPrice < 0)
								sales.mTempNewPrice = 0;
						}
						
						index++;
					}
		}
		
		if(event.getSource() != 100)
			return false;
		
		// 부서탭인 경우
		if(Construction.s_tabtype == ConstructionTabTypes.DEPARTMENT)
		{
			for(int i=0; i<m_link_rect.length; i++)
			{
				if(m_construction.m_constructiontype == ConstructionTypes.PORT)
					break;
				
				if(m_link_rect[i].contains(x, y))
				{
					Utility.m_click_sound.start();
					m_links[i]++;
					
					if(i == 3 || i == 5 || i == 10 || i == 12)
					{
						if(m_links[i] > 3)
							m_links[i] = 0;
					}
					else
					{
						if(m_links[i] > 1)
							m_links[i] = 0;
					}
					
					ChangeLink(i);
					
					return true;
				}
			}
			
			for(int i=0; i<m_department_rect.length; i++)
				if(m_department_rect[i].contains(x, y))
				{
					if(m_department_select == i)
					{
						if(m_construction.m_constructiontype == ConstructionTypes.PORT)
							continue;
						
						m_select = 0;
						
						int construction_type = 0;
						
						if(m_construction.m_constructiontype == ConstructionTypes.RETAIL)
							construction_type = R.array.retail;
						else if(m_construction.m_constructiontype == ConstructionTypes.FACTORY)
							construction_type = R.array.factory;
						else if(m_construction.m_constructiontype == ConstructionTypes.FARM)
							construction_type = R.array.farm;
						else if(m_construction.m_constructiontype == ConstructionTypes.RnD)
							construction_type = R.array.RnD;
						
						if(m_departments[m_department_select] != null && 
								m_departments[m_department_select].m_department_type == Department.DepartmentTypes.ADVERTISE)
						{
							final LinearLayout linear = (LinearLayout) View.inflate(AppManager.getInstance().getGameView().getContext(), 
									R.layout.seekbar_text, null);
							
							SeekBar seekbar = (SeekBar) linear.findViewById(R.id.seekBar1);
							TextView textview = (TextView) linear.findViewById(R.id.textView1);			
							
							AlertDialog.Builder bld = new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext());
							bld.setTitle("연결된 각 상품에 대한 광고비를 선택하세요");
							bld.setView(linear);
							bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									((Advertise) m_departments[m_department_select]).m_current_progress = m_progress;
								}
							});
							bld.setNegativeButton("취소", null);
							bld.create();
							bld.show();
							
							seekbar.setMax(((Advertise) m_departments[m_department_select]).m_max_progress);
							seekbar.setProgress(((Advertise) m_departments[m_department_select]).m_current_progress);
							
							textview.setText(((Advertise) m_departments[m_department_select]).m_current_progress*30+"(만원)");
							
							seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
								
								@Override
								public void onStopTrackingTouch(SeekBar seekBar) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void onStartTrackingTouch(SeekBar seekBar) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void onProgressChanged(SeekBar seekBar, int progress,
										boolean fromUser) {
									// TODO Auto-generated method stub
									m_progress = progress;
									
									TextView textview = (TextView) linear.findViewById(R.id.textView1);			
									textview.setText(m_progress*30+"(만원)");
								}
							});
							
							
						}
						/*
						else if(m_departments[m_department_select] != null && m_construction instanceof Retail &&
								m_departments[m_department_select].m_department_type == Department.DepartmentTypes.PURCHASE)
						{
							new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
						    .setTitle("구입할 제품을 선택하세요")
						    .setSingleChoiceItems(R.array.product, 0, 
						    		new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											m_select = which;
										}
									})
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									if(m_select == 0)
										((Purchase) m_departments[m_department_select]).m_commodity = CommodityManager.m_commoditis[3];
									
									if(m_select == 1)
										((Purchase) m_departments[m_department_select]).m_commodity = CommodityManager.m_commoditis[4];
								}
							})
							.setNegativeButton("취소", null)
						    .show();
						}
						else if(m_departments[m_department_select] != null && m_construction instanceof Factory &&
								m_departments[m_department_select].m_department_type == Department.DepartmentTypes.PURCHASE)
						{
							new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
						    .setTitle("구입할 제품을 선택하세요")
						    .setSingleChoiceItems(R.array.material, 0, 
						    		new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											m_select = which;
										}
									})
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									if(m_select == 0)
										((Purchase) m_departments[m_department_select]).m_commodity = CommodityManager.m_commoditis[0];
									
									if(m_select == 1)
										((Purchase) m_departments[m_department_select]).m_commodity = CommodityManager.m_commoditis[1];
								}
							})
							.setNegativeButton("취소", null)
						    .show();
						}
						*/
						else
						{
						
							new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
						    .setTitle("설치할 부서를 선택하세요")
						    .setSingleChoiceItems(construction_type, 0, 
						    		new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											m_select = which;
										}
									})
						    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									DepartmentTypes department_type = null;
									
									if(m_construction.m_constructiontype == ConstructionTypes.RETAIL)
									{
										if(m_select == 0)
											department_type = DepartmentTypes.PURCHASE;
										
										if(m_select == 1)
											department_type = DepartmentTypes.SALES;
										
										if(m_select == 2)
											department_type = DepartmentTypes.ADVERTISE;
										
										if(m_select == 3)
											department_type = null;
									}
									
									if(m_construction.m_constructiontype == ConstructionTypes.FACTORY)
									{
										if(m_select == 0)
											department_type = DepartmentTypes.PURCHASE;
										
										if(m_select == 1)
											department_type = DepartmentTypes.MANUFACTURE;
										
										if(m_select == 2)
											department_type = DepartmentTypes.SALES;
										
										if(m_select == 3)
											department_type = null;
									}
									
									if(m_construction.m_constructiontype == ConstructionTypes.FARM)
									{
										if(m_select == 0)
											department_type = DepartmentTypes.LIVESTOCK;
										
										if(m_select == 1)
											department_type = DepartmentTypes.PROCESS;
										
										if(m_select == 2)
											department_type = DepartmentTypes.SALES;
										
										if(m_select == 3)
											department_type = null;
									}
									
									if(m_construction.m_constructiontype == ConstructionTypes.RnD)
									{
										if(m_select == 0)
											department_type = DepartmentTypes.LABORATORY;
										
										if(m_select == 1)
											department_type = null;
									}
									
									m_previous_department = m_departments[m_department_select];
									
									AddDepartment(department_type);
				
								}
							})
						    .setNegativeButton("취소", null)
						    .show();
						}
					}
					else
					{
						Utility.m_click_sound.start();
						m_department_select = i;
					}
					
					return true;
				}
		}
		
		if(Construction.s_tabtype == ConstructionTabTypes.SUMMARY)
		{
			for(int i=0; i<4; i++)
				if(new Rect(0+161*2/2*i, 15*2/2, 157*2/2+161*2/2*i, 145*2/2).contains(x, y))
				{
					Utility.m_click_sound.start();
					s_summary_index = i;

					return true;
				}
		}
		
		return true;
	}

	public void Render(Canvas canvas)
	{
		// 부서탭인 경우
		if(Construction.s_tabtype == ConstructionTabTypes.DEPARTMENT)
		{
			// 각 부서 상태
			for(int i=0; i<m_departments.length; i++)
			{
				if(m_departments[i] != null)
					m_departments[i].Render(canvas);
			}
			
			// 그리고 연결
			for(int i=0; i<m_links.length; i++)
			{
				if(m_links[i] > 0)
				{
					Resources resources = AppManager.getInstance().getResources();
					
					Bitmap bitmap = AppManager.getInstance().getBitmap( R.drawable.department_horizontal_link, Utility.sOptions);
					
					if(i == 2 || i == 4 || i == 6 || i == 9 || i == 11 || i == 13)
						bitmap = AppManager.getInstance().getBitmap( R.drawable.department_vertical_link, Utility.sOptions);
					if(i == 3 || i == 5 || i == 10 || i == 12)
					{
						if(m_links[i] == 1)
							bitmap = AppManager.getInstance().getBitmap( R.drawable.department_diagonal_link_1, Utility.sOptions);
						else if(m_links[i] == 2)
							bitmap = AppManager.getInstance().getBitmap( R.drawable.department_diagonal_link_2, Utility.sOptions);
						else // m_links[i] == 3
							bitmap = AppManager.getInstance().getBitmap( R.drawable.department_diagonal_link_3, Utility.sOptions);
					}
					
					canvas.drawBitmap(bitmap, 
							resources.getIntArray(R.array.LINK_0+i)[0]*2/2, 
							resources.getIntArray(R.array.LINK_0+i)[1]*2/2, 
							null);
				}
			}
			
			// 부서 선택 표시
			if(m_department_select > -1)
			{
				canvas.drawBitmap(m_department_select_bitmap, 
						m_department_rect[m_department_select].left, 
						m_department_rect[m_department_select].top, 
						null);
			}
		}
		
		// 요약탭인 경우
		if(Construction.s_tabtype == ConstructionTabTypes.SUMMARY)
		{
			int num_summary_item = 0;
			int index = 0;
			
			Paint p = new Paint();
	    	p.setTextSize(15); 
			
			for(int i=0; i<DepartmentManager.DEPARTMENT_COUNT; i++)
				if(m_departments[i] != null)
					if(m_departments[i].m_department_type == DepartmentTypes.SALES
							&& ((Sales)m_departments[i]).m_commodity != null)
					{
						Sales sales = (Sales)m_departments[i];
						Purchase purchase = null;
						//if(((Sales)m_departments[i]).m_department_list.size() != 0)
						//	purchase = (Purchase)((Sales)m_departments[i]).m_department_list.get(0);
						
						canvas.drawBitmap(sales.m_commodity.m_commodity_bitmap, 35/2+(161*2/2*num_summary_item), 20*2/2, null);
						num_summary_item++;

						if(s_summary_index == index)
						{
							p.setColor(Color.BLACK);
					    	canvas.drawBitmap(Department.m_department_content_bar_bitmap, 50/2, 320/2, null);
					    	
					    	p.setTextAlign(Align.CENTER);
					    	canvas.drawText(((Sales)m_departments[i]).m_commodity.m_name, 50/2+163/2, 330/2+25/2, p);
					    	
					    	p.setTextAlign(Align.LEFT);
					    	p.setColor(Color.WHITE);
					    	canvas.drawText("시장 점유율", 100/2, 400/2, p);
					    	
					    	canvas.drawText("총 구입 가격", 300/2, 400/2, p);
					    	
					    	
					    	canvas.drawText("판매 가격", 300/2, 440/2, p);
					    	canvas.drawText("새로운 판매 가격", 300/2, 500/2, p);
					    	
					    	p.setColor(Color.argb(100, 0, 0, 0));
					    	canvas.drawCircle(155/2, 555/2, 80/2, p);
					    	
					    	p.setColor(Color.WHITE);
					    	canvas.drawCircle(150/2, 550/2, (80-1)/2, p);
					    	
					    	Random rand = new Random();
					    	
					    	int angle = 200 + rand.nextInt(100);
					    	
					    	p.setColor(Color.RED);
					    	RectF r = new RectF(150/2-80/2, 550/2-80/2, 150/2+80/2, 550/2+80/2);
					    	canvas.drawArc(r, 90-angle, angle, true, p);
					    	
					    	mDoublePlusButton.Render(canvas);
							mOnePlusButton.Render(canvas);
							mOneMinusButton.Render(canvas);
							mDoubleMinusButton.Render(canvas);
							mConfirmButton.Render(canvas);
					    	
							/*
					    	Utility.RenderSignButton(canvas, 2, +1, 300/2, 550/2);
					    	Utility.RenderSignButton(canvas, 1, +1, 370/2, 550/2);
							Utility.RenderSignButton(canvas, 1, -1, 440/2, 550/2);
							Utility.RenderSignButton(canvas, 2, -1, 510/2, 550/2);
							*/
							
							p.setColor(Color.WHITE);
							
							double totalPurchasePrice = sales.m_commodity.m_baseprice;
							
							/*
							if(((Sales)m_departments[i]).m_department_list.size() != 0)
								totalPurchasePrice = purchase.m_commodity.m_baseprice 
									+ purchase.m_commodity.m_baseprice/50 * purchase.m_distance_from_linked_construction;
							*/
							canvas.drawText(String.format("%.1f",  totalPurchasePrice)+"(원)", 540/2, 400/2, p);
							
							//double salesPrice = sales.m_commodity.m_baseprice + sales.m_commodity.m_baseprice*1.2;
							
							canvas.drawText(sales.mNewPrice+"(원)", 540/2, 440/2, p);
							
							//double newPrice = sales.mNewPrice;
							
							canvas.drawText(sales.mTempNewPrice+"(원)", 540/2, 500/2, p);
					    	
						}
						index++;
					}
			
			canvas.drawBitmap(m_summary_selector_bitmap, 0+(161*2/2*s_summary_index), 15*2/2, null);
			
		}
	}
	
	private void AddDepartment(DepartmentTypes department_type)
	{
		// 비용도 현금에서 제외해야한다.
		
		Log.i("abc", "AddDepartment 진입");
		
		if(department_type == DepartmentTypes.PURCHASE)
			if(m_departments[m_department_select] != null)
			{
				if(m_departments[m_department_select].m_department_type != DepartmentTypes.PURCHASE)
				{
					if(Player.m_money >= 50000000)
						Player.m_money -= 50000000;
					else
					{
						NotEnoughMoney();
						return;
					}
					
					m_departments[m_department_select] = new Purchase(m_department_select, 
							m_department_purchase_bitmap, this);
				}
			}
			else	// m_departments[m_department_select] == null
			{
				if(Player.m_money >= 50000000)
					Player.m_money -= 50000000;
				else
				{
					NotEnoughMoney();
					return;
				}
					
				m_departments[m_department_select] = new Purchase(m_department_select, 
						m_department_purchase_bitmap, this);
			}
		
		if(department_type == DepartmentTypes.SALES)
			if(m_departments[m_department_select] != null)
			{
				if(m_departments[m_department_select].m_department_type != DepartmentTypes.SALES)
				{
					if(Player.m_money >= 50000000)
						Player.m_money -= 50000000;
					else
					{
						NotEnoughMoney();
						return;
					}
					
					m_departments[m_department_select] = new Sales(m_department_select, 
							m_department_sales_bitmap, this);
				}
			}
			else	// m_departments[m_department_select] == null
			{
				if(Player.m_money >= 50000000)
					Player.m_money -= 50000000;
				else
				{
					NotEnoughMoney();
					return;
				}
				
				m_departments[m_department_select] = new Sales(m_department_select, 
						m_department_sales_bitmap, this);
			}
		
		if(department_type == DepartmentTypes.ADVERTISE)
			if(m_departments[m_department_select] != null)
			{
				if(m_departments[m_department_select].m_department_type != DepartmentTypes.ADVERTISE)
				{
					if(Player.m_money >= 5000000)
						Player.m_money -= 5000000;
					else
					{
						NotEnoughMoney();
						return;
					}
					
					m_departments[m_department_select] = new Advertise(m_department_select, 
							m_department_advertise_bitmap, this);
				}
			}
			else	// m_departments[m_department_select] == null
			{
				if(Player.m_money >= 5000000)
					Player.m_money -= 5000000;
				else
				{
					NotEnoughMoney();
					return;
				}
				
				m_departments[m_department_select] = new Advertise(m_department_select, 
						m_department_advertise_bitmap, this);
			}
		
		if(department_type == DepartmentTypes.MANUFACTURE)
			if(m_departments[m_department_select] != null)
			{
				if(m_departments[m_department_select].m_department_type != DepartmentTypes.MANUFACTURE)
				{
					if(Player.m_money >= 100000000)
						Player.m_money -= 100000000;
					else
					{
						NotEnoughMoney();
						return;
					}
					
					m_departments[m_department_select] = new Manufacture(m_department_select, 
							m_department_manufacture_bitmap, this);
				}
			}
			else	// m_departments[m_department_select] == null
			{
				if(Player.m_money >= 100000000)
					Player.m_money -= 100000000;
				else
				{
					NotEnoughMoney();
					return;
				}
				
				m_departments[m_department_select] = new Manufacture(m_department_select, 
						m_department_manufacture_bitmap, this);
			}
		
		if(department_type == DepartmentTypes.LABORATORY)
			if(m_departments[m_department_select] != null)
			{
				if(m_departments[m_department_select].m_department_type != DepartmentTypes.LABORATORY)
				{
					if(Player.m_money >= 150000000)
						Player.m_money -= 150000000;
					else
					{
						NotEnoughMoney();
						return;
					}
					
					m_departments[m_department_select] = new Laboratory(m_department_select, 
							m_department_laboratory_bitmap, this);
				}
			}
			else	// m_departments[m_department_select] == null
			{
				if(Player.m_money >= 150000000)
					Player.m_money -= 150000000;
				else
				{
					NotEnoughMoney();
					return;
				}
				
				m_departments[m_department_select] = new Laboratory(m_department_select, 
						m_department_laboratory_bitmap, this);
			}
		
		if(department_type == DepartmentTypes.LIVESTOCK)
			if(m_departments[m_department_select] != null)
			{
				if(m_departments[m_department_select].m_department_type != DepartmentTypes.LIVESTOCK)
				{
					if(Player.m_money >= 50000000)
						Player.m_money -= 50000000;
					else
					{
						NotEnoughMoney();
						return;
					}
					
					m_departments[m_department_select] = new Livestock(m_department_select, 
							m_department_livestock_bitmap, this);
				}
			}
			else	// m_departments[m_department_select] == null
			{
				if(Player.m_money >= 50000000)
					Player.m_money -= 50000000;
				else
				{
					NotEnoughMoney();
					return;
				}
				
				m_departments[m_department_select] = new Livestock(m_department_select, 
						m_department_livestock_bitmap, this);
			}
		
		if(department_type == DepartmentTypes.PROCESS)
			if(m_departments[m_department_select] != null)
			{
				if(m_departments[m_department_select].m_department_type != DepartmentTypes.PROCESS)
				{
					if(Player.m_money >= 50000000)
						Player.m_money -= 50000000;
					else
					{
						NotEnoughMoney();
						return;
					}
					
					m_departments[m_department_select] = new Process(m_department_select, 
							m_department_process_bitmap, this);
				}
			}
			else
			{
				if(Player.m_money >= 50000000)
					Player.m_money -= 50000000;
				else
				{
					NotEnoughMoney();
					return;
				}
				
				m_departments[m_department_select] = new Process(m_department_select, 
						m_department_process_bitmap, this);
			}
		
		if(department_type == null)
			m_departments[m_department_select] = null;
		
		Log.i("abc", "AddDepartment 마무리");
		
		ChangeDepartment(m_departments[m_department_select]);
	}
	
	public void NotEnoughMoney()
	{
		Utility.MessageDialog("부서 계획을 위한 현금이 부족합니다");
	}
	
	public double Process()
	{
		double result = 0;
		
		int num_employee = 0;
		long player_cost = 0;
		long player_earning = 0;
		long player_net_profit = 0;
		int maximum_day = 0;
		
		for(int i=0; i<m_departments.length; i++)
		{
			if(m_departments[i] != null)
				result += m_departments[i].Process();
		}
		
		
		// 현재 달의 최대 일 수
		maximum_day = Time.GetInstance().GetCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
		
		// 유지비 계산
		if(m_construction.m_constructiontype == ConstructionTypes.RETAIL)
		{
			player_cost += Retail.maintenance/maximum_day;
			Player.m_monthly_maintenance[Player.m_netprofit_index] -= Retail.maintenance/maximum_day;
		}
		if(m_construction.m_constructiontype == ConstructionTypes.FACTORY)
		{
			player_cost += Factory.maintenance/maximum_day;
			Player.m_monthly_maintenance[Player.m_netprofit_index] -= Factory.maintenance/maximum_day;
		}
		if(m_construction.m_constructiontype == ConstructionTypes.FARM)
		{
			player_cost += Farm.maintenance/maximum_day;
			Player.m_monthly_maintenance[Player.m_netprofit_index] -= Farm.maintenance/maximum_day;
		}
		if(m_construction.m_constructiontype == ConstructionTypes.RnD)
		{
			player_cost += RnD.maintenance/maximum_day;
			Player.m_monthly_maintenance[Player.m_netprofit_index] -= RnD.maintenance/maximum_day;
		}
		
		// 임금 계산
		for(int i=0; i<DepartmentManager.DEPARTMENT_COUNT; i++)
		{
			if(m_departments[i] != null)
				num_employee += m_departments[i].m_employee;
		}
		player_cost += (num_employee * Player.m_average_wage);
		Player.m_monthly_wage[Player.m_netprofit_index] -= (num_employee * Player.m_average_wage);
		
		
		// 하루 순이익
		player_net_profit = (long) (player_earning - player_cost + result);
		
		//Player.m_money += player_net_profit;

		
		
		//public long m_annual_netprofit;			// 사업체의 연간순이익
		//public long m_monthly_netprofit[];	// 사업체의 최근 12달의 순이익 배열
		//public long m_last_netprofit;				// 사업체의 최근 13달 이전의 순이익
		
		m_construction.m_monthly_netprofit[Player.m_netprofit_index] += player_net_profit;
		
		
		//Player.m_monthly_netprofit[Player.m_netprofit_index] += player_net_profit;
		
		m_construction.m_annual_netprofit = 0;
		for(int i=0; i<12; i++)
			m_construction.m_annual_netprofit += m_construction.m_monthly_netprofit[i];
		
		
		m_construction.Update();
		
		
		
		
		if(player_net_profit > 0)
		{
			if(CapitalismSystem.m_max_daily_positive_profit < m_construction.m_monthly_netprofit[Player.m_netprofit_index])
				CapitalismSystem.m_max_daily_positive_profit = m_construction.m_monthly_netprofit[Player.m_netprofit_index];
		}
		else
		{
			if(CapitalismSystem.m_max_daily_negative_profit > m_construction.m_monthly_netprofit[Player.m_netprofit_index])
				CapitalismSystem.m_max_daily_negative_profit = m_construction.m_monthly_netprofit[Player.m_netprofit_index];
		}
			
			
			
			
			
		
		return player_net_profit;
	}
	
	/**
	 * 모든 부서의 m_is_done을 false로 리셋한다.
	 */
	public void ResetDepartment()
	{
		for(int i=0; i<m_departments.length; i++)
		{
			if(m_departments[i] != null)
				m_departments[i].m_is_done = false;
		}
	}

}
