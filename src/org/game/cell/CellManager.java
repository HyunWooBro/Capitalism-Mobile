package org.game.cell;


import java.io.*;
import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.game.*;
import org.game.UserInterface.*;
import org.game.cell.Cell.*;
import org.game.cell.CellSelector.*;
import org.game.commodity.*;
import org.game.construction.*;
import org.game.construction.Construction.ConstructionTypes;
import org.game.department.*;
import org.game.department.Department.DepartmentTypes;
import org.game.news.*;
import org.screen.*;
import org.screen.layer.window.*;
import org.framework.*;
import org.framework.R;
import org.framework.openGL.*;

import android.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.util.*;
import android.view.*;
import android.widget.*;

/**
 * 셀과 관련된 모든 기능을 관리하고 조정하는 클래스
 * @author 김현우
 *
 */
public class CellManager {
	public Cell m_cells[][];
	ArrayList<CellGroup> m_cellgroup_list = new ArrayList<CellGroup>();
	public CellSelector m_cellselector = new CellSelector(1);
	public CellSelector m_connection_cellselector = new CellSelector(1);
	
	public int m_total_landvalue;
	public int m_total_purchase;
	
	public static Point m_current_point = new Point(-1, -1);
	
	public static Bitmap m_cell_house1_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_house1);
	public static Bitmap m_cell_house2_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_house2);
	public static Bitmap m_cell_gound1_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_ground1);
	public static Bitmap m_cell_gound2_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_ground2);
	public static Bitmap m_cell_road1_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_road1);
	public static Bitmap m_cell_2x2_house1_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_house1);
	public static Bitmap m_cell_2x2_apartment1_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_apratment1);
	
	public static Bitmap m_cell_house1_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_house1_d);
	public static Bitmap m_cell_house2_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_house2_d);
	public static Bitmap m_cell_gound1_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_ground1_d);
	public static Bitmap m_cell_gound2_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_ground2_d);
	public static Bitmap m_cell_road1_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_road1_d);
	public static Bitmap m_cell_2x2_house1_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_house1_d);
	public static Bitmap m_cell_2x2_apartment1_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_apratment1_d);
	
	public static Bitmap  m_cell_southeast_river1_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river1);
	public static Bitmap m_cell_southeast_river2_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river2);
	public static Bitmap m_cell_southbridge_river1_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river1);
	public static Bitmap m_cell_southbridge_river2_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river2);
	public static Bitmap m_cell_southwest_river2_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_river2);
	public static Bitmap m_cell_3X3_port_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_port);
	
	public static Bitmap m_cell_southeast_river1_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river1_d);
	public static Bitmap m_cell_southeast_river2_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river2_d);
	public static Bitmap m_cell_southbridge_river1_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river1_d);
	public static Bitmap m_cell_southbridge_river2_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river2_d);
	public static Bitmap m_cell_southwest_river2_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_river2_d);
	public static Bitmap m_cell_3X3_port_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_port_d);
	
	public static Bitmap m_cell_retail1_bitmap;
	public static Bitmap m_cell_factory1_bitmap;
	public static Bitmap m_cell_farm1_bitmap;
	public static Bitmap m_cell_RnD1_bitmap;
	
	public static Bitmap m_cell_class_a_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_class_a);
	public static Bitmap m_cell_class_b_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_class_b);
	public static Bitmap m_cell_class_c_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_class_c);
	public static Bitmap m_cell_class_d_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_class_d);
	public static Bitmap m_cell_class_e_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_class_e);
	public static Bitmap m_cell_class_f_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_class_f);
	
	public static Bitmap m_cell_profit_positive_high_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_high);
	public static Bitmap m_cell_profit_positive_medium_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_medium);
	public static Bitmap m_cell_profit_positive_low_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_low);
	public static Bitmap m_cell_profit_negative_high_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_high);
	public static Bitmap m_cell_profit_negative_medium_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_medium);
	public static Bitmap m_cell_profit_negative_low_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_low);
	public static Bitmap m_cell_profit_none_bitmap;// = AppManager.getInstance().getBitmap(R.drawable.cell_profit_none);
	
	public static int m_size;
	
	public int m_select;
	
	//static Bitmap construction_info = AppManager.getInstance().getBitmap( R.drawable.construction_info_basic);
	
	public static int m_citiyicon_selection = 0;
	
	public CellManager()
	{
		Integer a;
		
		m_total_landvalue = -1;
		m_total_purchase = -1;
		
		/*
		m_cell_house1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house1);
		m_cell_house2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house2);
		m_cell_gound1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground1);
		m_cell_gound2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground2);
		m_cell_road1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_road1);
		m_cell_2x2_house1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_house1);
		m_cell_2x2_apartment1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_apratment1);
		
		m_cell_house1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house1_d);
		m_cell_house2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house2_d);
		m_cell_gound1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground1_d);
		m_cell_gound2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground2_d);
		m_cell_road1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_road1_d);
		m_cell_2x2_house1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_house1_d);
		m_cell_2x2_apartment1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_apratment1_d);
		
		m_cell_southeast_river1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river1);
		m_cell_southeast_river2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river2);
		m_cell_southbridge_river1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river1);
		m_cell_southbridge_river2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river2);
		m_cell_southwest_river2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_river2);
		m_cell_3X3_port_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_port);
		
		m_cell_southeast_river1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river1_d);
		m_cell_southeast_river2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river2_d);
		m_cell_southbridge_river1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river1_d);
		m_cell_southbridge_river2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river2_d);
		m_cell_southwest_river2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_river2_d);
		m_cell_3X3_port_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_port_d);
		
		m_cell_retail1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_retail1);
		m_cell_factory1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_factory1);
		m_cell_farm1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_farm1);
		m_cell_RnD1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_laboratory1);
		
		m_cell_class_a_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_a);
		m_cell_class_b_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_b);
		m_cell_class_c_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_c);
		m_cell_class_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_d);
		m_cell_class_e_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_e);
		m_cell_class_f_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_f);
		
		m_cell_profit_positive_high_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_high);
		m_cell_profit_positive_medium_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_medium);
		m_cell_profit_positive_low_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_low);
		m_cell_profit_negative_high_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_high);
		m_cell_profit_negative_medium_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_medium);
		m_cell_profit_negative_low_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_low);
		m_cell_profit_none_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_none);
		*/

		String input;
		try {  
			InputStream input_stream = AppManager.getInstance().getResources().openRawResource(R.raw.map);
			byte data[] = new byte[input_stream.available()];
			while(input_stream.read(data) != -1) {;}
			input_stream.close();
			input = new String(data);
			
			// input을 토큰으로 구분  
			StringTokenizer s = new StringTokenizer(input, " |\r\n\t");
			// 앞으로 버젼과 캐피탈리즘 맵이 맞는지 확인작업까지
			//if(s.hasMoreTokens(" \r"))
				m_size = Integer.parseInt(s.nextToken());
			
			m_cells = new Cell[m_size][m_size];
			
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					m_cells[i][j] = new Cell();
				}
			}
			
			Random rand = new Random();
			int temp = 1;
			
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					//if(s.hasMoreTokens())
					int type = Integer.parseInt(s.nextToken());
					
					if(type == 1)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.HOUSE1;
						
						if(rand.nextInt(3) == 0)
						{
							m_cells[i][j].m_bitmap = m_cell_house2_bitmap;
							m_cells[i][j].m_dark_version_bitmap = m_cell_house2_d_bitmap;
							m_cells[i][j].mBitmapID = R.drawable.cell_house2;
							m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_house2_d;
						}
						else
						{
							m_cells[i][j].m_bitmap = m_cell_house1_bitmap;
							m_cells[i][j].m_dark_version_bitmap = m_cell_house1_d_bitmap;
							m_cells[i][j].mBitmapID = R.drawable.cell_house1;
							m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_house1_d;
						}
					}
					if(type == 2)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.GROUND1;
						m_cells[i][j].m_bitmap = m_cell_gound1_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_gound1_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_ground1;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_ground1_d;
					}
					if(type == 3)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.GROUND2;
						m_cells[i][j].m_bitmap = m_cell_gound2_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_gound2_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_ground2;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_ground2_d;
					}
					
					if(type == 5)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.ROAD1;
						m_cells[i][j].m_bitmap = m_cell_road1_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_road1_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_road1;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_road1_d;
					}
					
					
					if(type == 9)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.HOUSE1_2X2;
						m_cells[i][j].m_bitmap = m_cell_2x2_house1_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_2x2_house1_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_2x2_house1;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_2x2_house1_d;
						m_cells[i][j].m_maxlink = 4;
					}
					
					if(type == 10)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.APAPTMENT1_2X2;
						m_cells[i][j].m_bitmap = m_cell_2x2_apartment1_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_2x2_apartment1_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_2x2_apratment1;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_2x2_apratment1_d;
						m_cells[i][j].m_maxlink = 4;
					}
					
					if(type == 80)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.RIVER;
						m_cells[i][j].m_bitmap = m_cell_southeast_river1_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_southeast_river1_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_southeast_river1;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_southeast_river1_d;
					}
					
					if(type == 81)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.RIVER;
						m_cells[i][j].m_bitmap = m_cell_southeast_river2_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_southeast_river2_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_southeast_river2;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_southeast_river2_d;
					}
					
					if(type == 82)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.RIVER;
						m_cells[i][j].m_bitmap = m_cell_southbridge_river1_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_southbridge_river1_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_southbridge_river1;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_southbridge_river1_d;
					}
					
					if(type == 83)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.RIVER;
						m_cells[i][j].m_bitmap = m_cell_southbridge_river2_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_southbridge_river2_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_southbridge_river2;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_southbridge_river2_d;
					}
					
					if(type == 84)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.RIVER;
						m_cells[i][j].m_bitmap = m_cell_southwest_river2_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_southwest_river2_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_southwest_river2;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_southwest_river2_d;
					}
					
					if(type == 99)
					{
						m_cells[i][j].m_celltype = Cell.CellTypes.PORT;
						m_cells[i][j].m_bitmap = m_cell_3X3_port_bitmap;
						m_cells[i][j].m_dark_version_bitmap = m_cell_3X3_port_d_bitmap;
						m_cells[i][j].mBitmapID = R.drawable.cell_port;
						m_cells[i][j].m_dark_version_bitmapID = R.drawable.cell_port_d;
						m_cells[i][j].m_maxlink = 9;
					}
					
					String land_value = s.nextToken();
					
					if(land_value.equalsIgnoreCase("a"))
						m_cells[i][j].m_landvalue = LandValues.CLASS_A;
					if(land_value.equalsIgnoreCase("b"))
						m_cells[i][j].m_landvalue = LandValues.CLASS_B;
					if(land_value.equalsIgnoreCase("c"))
						m_cells[i][j].m_landvalue = LandValues.CLASS_C;
					if(land_value.equalsIgnoreCase("d"))
						m_cells[i][j].m_landvalue = LandValues.CLASS_D;
					if(land_value.equalsIgnoreCase("e"))
						m_cells[i][j].m_landvalue = LandValues.CLASS_E;
					if(land_value.equalsIgnoreCase("f"))
						m_cells[i][j].m_landvalue = LandValues.CLASS_F;
					
					int cur_link = Integer.parseInt(s.nextToken());
					m_cells[i][j].m_currentlink = cur_link;
				}
			}

		} catch (IOException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  
		
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				m_cells[i][j].m_cellpoint.x = (m_size-1)*Cell.CELL_WIDTH/2 + j*Cell.CELL_WIDTH/2 - i*Cell.CELL_WIDTH/2 + Cell.CELL_WIDTH/2;
				m_cells[i][j].m_cellpoint.y = j*Cell.CELL_HEIGHT/2 + i*Cell.CELL_HEIGHT/2 + Cell.CELL_HEIGHT/2;
				
				m_cells[i][j].m_cellrect.left = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH/2;
				m_cells[i][j].m_cellrect.top = m_cells[i][j].m_cellpoint.y - Cell.CELL_HEIGHT/2;
				m_cells[i][j].m_cellrect.right = m_cells[i][j].m_cellpoint.x + Cell.CELL_WIDTH/2;
				m_cells[i][j].m_cellrect.bottom = m_cells[i][j].m_cellpoint.y + Cell.CELL_HEIGHT/2;
				
				if(m_cells[i][j].m_celltype == Cell.CellTypes.HOUSE1)
				{
					m_cells[i][j].m_imagepoint.x = (m_size-1)*Cell.CELL_WIDTH/2 + j*Cell.CELL_WIDTH/2 - i*Cell.CELL_WIDTH/2;
					m_cells[i][j].m_imagepoint.y = j*Cell.CELL_HEIGHT/2 + i*Cell.CELL_HEIGHT/2 - (Cell.CELL_HEIGHT-1);
				}
				else if(m_cells[i][j].m_celltype == Cell.CellTypes.GROUND2)
				{
					m_cells[i][j].m_imagepoint.x = (m_size-1)*Cell.CELL_WIDTH/2 + j*Cell.CELL_WIDTH/2 - i*Cell.CELL_WIDTH/2;
					m_cells[i][j].m_imagepoint.y = j*Cell.CELL_HEIGHT/2 + i*Cell.CELL_HEIGHT/2 - (Cell.CELL_HEIGHT-1);
				}
				else if(m_cells[i][j].m_celltype == Cell.CellTypes.HOUSE1_2X2)
				{
					if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
					{
						m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH/2*2;
						m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
						AddCellGroup(i-1, j-1, 2);
					}
				}
				else if(m_cells[i][j].m_celltype == Cell.CellTypes.APAPTMENT1_2X2)
				{
					if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
					{
						m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH/2*2;
						m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*5 - (Cell.CELL_HEIGHT-1)/2;
						AddCellGroup(i-1, j-1, 2);
					}
				}
				else if(m_cells[i][j].m_celltype == Cell.CellTypes.PORT)
				{
					if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
					{
						Log.i("abc", ""+(- (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT)/2));
						m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH/2*3;
						m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
						AddCellGroup(i-2, j-2, 3);
					}
				}
				else
				{
					m_cells[i][j].m_imagepoint.x = (m_size-1)*Cell.CELL_WIDTH/2 + j*Cell.CELL_WIDTH/2 - i*Cell.CELL_WIDTH/2;
					m_cells[i][j].m_imagepoint.y =  j*Cell.CELL_HEIGHT/2 + i*Cell.CELL_HEIGHT/2;
				}
				
				m_cells[i][j].m_cell_imagepoint.x = (m_size-1)*Cell.CELL_WIDTH/2 + j*Cell.CELL_WIDTH/2 - i*Cell.CELL_WIDTH/2;
				m_cells[i][j].m_cell_imagepoint.y =  j*Cell.CELL_HEIGHT/2 + i*Cell.CELL_HEIGHT/2;
			}
		}
		
		// 항구인 경우 m_first_link_point를 설정
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				if(m_cells[i][j].m_celltype == Cell.CellTypes.PORT && m_cells[i][j].m_currentlink == 1)
				{
					City.m_portlist.add(new Port());
    				
					City.m_portlist.get(City.m_portlist.size()-1).m_point.x = i;
					City.m_portlist.get(City.m_portlist.size()-1).m_point.y = j;		
					
					for(int i2=i; i2<i+3; i2++)
    				{
    					for(int j2=j; j2<j+3; j2++)
    					{
    						m_cells[i2][j2].m_first_link_point.x = i;
    						m_cells[i2][j2].m_first_link_point.y = j;

    						m_cells[i2][j2].m_constructiontype = ConstructionTypes.PORT;
    						m_cells[i2][j2].m_construction = City.m_portlist.get(City.m_portlist.size()-1);
    						
    					}
    				}
				}
			}
		}
		
		// 맵 정보를 모두 읽은 후 CellGroup을 정리
	}
	
	public static void InitStatic()
	{
		m_current_point = new Point(-1, -1);

		m_size = 0;
		
		m_citiyicon_selection = 0;
		
		
		m_cell_house1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house1, Utility.sOptions);
		m_cell_house2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house2, Utility.sOptions);
		m_cell_gound1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground1, Utility.sOptions);
		m_cell_gound2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground2, Utility.sOptions);
		m_cell_road1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_road1, Utility.sOptions);
		m_cell_2x2_house1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_house1, Utility.sOptions);
		m_cell_2x2_apartment1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_apratment1, Utility.sOptions);
		
		m_cell_house1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house1_d, Utility.sOptions);
		m_cell_house2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_house2_d, Utility.sOptions);
		m_cell_gound1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground1_d, Utility.sOptions);
		m_cell_gound2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_ground2_d, Utility.sOptions);
		m_cell_road1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_road1_d, Utility.sOptions);
		m_cell_2x2_house1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_house1_d, Utility.sOptions);
		m_cell_2x2_apartment1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_2x2_apratment1_d, Utility.sOptions);
		
		m_cell_southeast_river1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river1, Utility.sOptions);
		m_cell_southeast_river2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river2, Utility.sOptions);
		m_cell_southbridge_river1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river1, Utility.sOptions);
		m_cell_southbridge_river2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river2, Utility.sOptions);
		m_cell_southwest_river2_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_river2, Utility.sOptions);
		m_cell_3X3_port_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_port, Utility.sOptions);
		
		m_cell_southeast_river1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river1_d, Utility.sOptions);
		m_cell_southeast_river2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southeast_river2_d, Utility.sOptions);
		m_cell_southbridge_river1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river1_d, Utility.sOptions);
		m_cell_southbridge_river2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southbridge_river2_d, Utility.sOptions);
		m_cell_southwest_river2_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_river2_d, Utility.sOptions);
		m_cell_3X3_port_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_port_d, Utility.sOptions);
		
		m_cell_retail1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_retail1, Utility.sOptions);
		m_cell_factory1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_factory1, Utility.sOptions);
		m_cell_farm1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_farm1, Utility.sOptions);
		m_cell_RnD1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_laboratory1, Utility.sOptions);
		
		m_cell_class_a_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_a, Utility.sOptions);
		m_cell_class_b_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_b, Utility.sOptions);
		m_cell_class_c_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_c, Utility.sOptions);
		m_cell_class_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_d, Utility.sOptions);
		m_cell_class_e_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_e, Utility.sOptions);
		m_cell_class_f_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_class_f, Utility.sOptions);
		
		m_cell_profit_positive_high_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_high, Utility.sOptions);
		m_cell_profit_positive_medium_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_medium, Utility.sOptions);
		m_cell_profit_positive_low_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_positive_low, Utility.sOptions);
		m_cell_profit_negative_high_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_high, Utility.sOptions);
		m_cell_profit_negative_medium_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_medium, Utility.sOptions);
		m_cell_profit_negative_low_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_negative_low, Utility.sOptions);
		m_cell_profit_none_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_profit_none, Utility.sOptions);
		
	}
	
	public void PortInit()
	{
		for(int i=0; i<City.m_portlist.size(); i++)
		{
			City.m_portlist.get(i).m_department_manager.m_departments[0] = new Sales(0, 
					DepartmentManager.m_department_sales_bitmap,
					City.m_portlist.get(i).m_department_manager);
			
			Log.i("abc", "PortInit 1");
			
			if(CommodityManager.m_commoditis[0] != null)
				Log.i("abc", "PortInit 11");
			
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[0]).m_commodity = CommodityManager.m_commoditis[0];
			Log.i("abc", "PortInit 2");
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[0]).m_inverntory =
					((Sales)City.m_portlist.get(i).m_department_manager.m_departments[0]).m_maxinverntory;
			
			Log.i("abc", "PortInit 3");
			
			City.m_portlist.get(i).m_department_manager.m_departments[1] = new Sales(1, 
					DepartmentManager.m_department_sales_bitmap,
					City.m_portlist.get(i).m_department_manager);
			
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[1]).m_commodity = CommodityManager.m_commoditis[1];
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[1]).m_inverntory =
					((Sales)City.m_portlist.get(i).m_department_manager.m_departments[1]).m_maxinverntory;	
			

			City.m_portlist.get(i).m_department_manager.m_departments[3] = new Sales(3, 
					DepartmentManager.m_department_sales_bitmap,
					City.m_portlist.get(i).m_department_manager);
			
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[3]).m_commodity = CommodityManager.m_commoditis[5];
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[3]).m_inverntory =
					((Sales)City.m_portlist.get(i).m_department_manager.m_departments[3]).m_maxinverntory;	
			
			City.m_portlist.get(i).m_department_manager.m_departments[4] = new Sales(4, 
					DepartmentManager.m_department_sales_bitmap,
					City.m_portlist.get(i).m_department_manager);
			
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[4]).m_commodity = CommodityManager.m_commoditis[6];
			((Sales)City.m_portlist.get(i).m_department_manager.m_departments[4]).m_inverntory =
					((Sales)City.m_portlist.get(i).m_department_manager.m_departments[4]).m_maxinverntory;	

		}
	}
	
	/**
	 * 셀, 셀실렉터 등의 맵관련 이미지를 그린다.
	 * @param canvas
	 * @param off_x
	 * @param off_y
	 * @param constructiontype
	 */
	public void Render(Canvas canvas)
	{
		/*
		int off_x = GameState.off_x;
		int off_y = GameState.off_y;
		
		UserInterface.ToolbarConstructionTypes constructiontype = 
				((GameState) AppManager.getInstance().m_state).m_UI.constructiontype;
		
		// 중복되어 그리는 것을 방지하기 위해 맵의 크기가 같은 boolean형 배열을 이용
		boolean isdrawn[][] = new boolean[m_size][m_size];
		
		Screen.beginZoomRender(canvas);
		
		// 가장 먼저, 도로와 차량부터 그린다. 도로부터 그리는 이유는 차량때문이다.
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				if(isdrawn[i][j] == false && m_cells[i][j].m_celltype == Cell.CellTypes.ROAD1)
				{
					isdrawn[i][j] = true;
					
					if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
						canvas.drawBitmap(m_cells[i][j].m_dark_version_bitmap, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
					else
						canvas.drawBitmap(m_cells[i][j].m_bitmap, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
					
				}
			}
		}
		
		// 도로를 그린 후, 이제 차량을 그린다.
		VehicleManager.GetInstance().Render(canvas, off_x, off_y, this);
		
		// 그 다음, 셀그룹(2X2 이상의 셀의 그룹)을 기준으로 화면을 그린다.
		for(int k = 0; k<m_cellgroup_list.size(); k++)
		{
			for(int i = 0; i<=m_cellgroup_list.get(k).m_point.x; i++)
			{
				for(int j = 0; j<=m_cellgroup_list.get(k).m_point.y; j++)
				{
					if(isdrawn[i][j] == false)
					{
						isdrawn[i][j] = true;
						// 셀이 다른 셀과 독립적이면
						if(m_cells[i][j].m_maxlink == 0)
						{
							if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
								canvas.drawBitmap(m_cells[i][j].m_dark_version_bitmap, 
										m_cells[i][j].m_imagepoint.x - off_x, 
										m_cells[i][j].m_imagepoint.y - off_y, 
										null);
							else
								canvas.drawBitmap(m_cells[i][j].m_bitmap, 
										m_cells[i][j].m_imagepoint.x - off_x, 
										m_cells[i][j].m_imagepoint.y - off_y, 
										null);
						}
						else	// 셀이 다른 셀과 연결되어 있다면
						{
							// 셀의 m_maxlink과 m_currentlink가 같은 셀에 한하여
							// 즉, 건물의 최하단 셀에서
							if(m_cells[i][j].m_maxlink == m_cells[i][j].m_currentlink)
							{
								// 셀에 건물이 지어져 있다면
								if(m_cells[i][j].m_constructiontype != null)
								{
									// +1은 지금 현재 셀 배치에 약간의 공간이 남기 때문에 추가했다. 
									// 셀의 높이계산에서 문제가 있는 것 같다. 이것을 통해 2X2셀 이상을 점유하는
									// 건물을 그리더라도 공백이 크게 느껴지지 않는다.
									canvas.drawBitmap(m_cells[i][j].m_bitmap, 
											m_cells[i][j].m_constructionpoint.x - off_x, 
											m_cells[i][j].m_constructionpoint.y - off_y - 1,
											null);
								}
								else
								{
									if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
									{
										canvas.drawBitmap(m_cells[i][j].m_dark_version_bitmap, 
												m_cells[i][j].m_constructionpoint.x - off_x, 
												m_cells[i][j].m_constructionpoint.y - off_y - 1,
												null);
									}
									else
										canvas.drawBitmap(m_cells[i][j].m_bitmap, 
												m_cells[i][j].m_constructionpoint.x - off_x, 
												m_cells[i][j].m_constructionpoint.y - off_y - 1,
												null);
								}
							}
						}
					}
				}
			}
		}
		
		// 이제 남은 셀을 채운다. 남은 셀은 모두 1X1셀 뿐이다.
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				if(isdrawn[i][j] == false)
				{
					isdrawn[i][j] = true;
					if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
						canvas.drawBitmap(m_cells[i][j].m_dark_version_bitmap, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
					else
						canvas.drawBitmap(m_cells[i][j].m_bitmap, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
				}
			}
		}
		
		if(m_citiyicon_selection == 1)
		{
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_A)
					{
						canvas.drawBitmap(m_cell_class_a_bitmap,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_B)
					{
						canvas.drawBitmap(m_cell_class_b_bitmap,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_C)
					{
						canvas.drawBitmap(m_cell_class_c_bitmap,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_D)
					{
						canvas.drawBitmap(m_cell_class_d_bitmap,
								m_cells[i][j].m_cell_imagepoint.x - off_x, 
								m_cells[i][j].m_cell_imagepoint.y - off_y, 
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_E)
					{
						canvas.drawBitmap(m_cell_class_e_bitmap,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_F)
					{
						canvas.drawBitmap(m_cell_class_f_bitmap,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
				}
			}
			
		}
		
		// 사실 m_citiyicon_selection == 2, 3은 이렇게 온 맵을 순환하는 것이 아니고 리스트로 해당 지역만
		// 확인하도록 만들어야 한다.
		if(m_citiyicon_selection == 2)
		{
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					if(m_cells[i][j].m_constructiontype != null
							&& m_cells[i][j].m_constructiontype != ConstructionTypes.PORT)
					{
						double temp = m_cells[i][j].m_construction.m_monthly_netprofit[Player.m_netprofit_index];
						
						if(temp > 0)
						{
							temp /= CapitalismSystem.m_max_daily_positive_profit;
							if(temp > 0.666)
								canvas.drawBitmap(m_cell_profit_positive_high_bitmap, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else if(temp > 0.333)
								canvas.drawBitmap(m_cell_profit_positive_medium_bitmap, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else
								canvas.drawBitmap(m_cell_profit_positive_low_bitmap, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
						}
						else
						{
							temp /= CapitalismSystem.m_max_daily_negative_profit;
							if(temp > 0.666)
								canvas.drawBitmap(m_cell_profit_negative_high_bitmap, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else if(temp > 0.333)
								canvas.drawBitmap(m_cell_profit_negative_medium_bitmap, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else
								canvas.drawBitmap(m_cell_profit_negative_low_bitmap, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
						}
					}
					
					//else
					//{
					//	canvas.drawBitmap(m_cell_profit_none_bitmap, 
					//			m_cells[i][j].m_cell_imagepoint.x - off_x, 
					//			m_cells[i][j].m_cell_imagepoint.y - off_y, 
					//			null);
					//}
				}
			}
		}
		
		//Screen.endZoomRender(canvas);

		
		if(m_citiyicon_selection == 3)
		{
			
			//for(int i=0; i<m_size; i++)
			//{
			//	for(int j=0; j<m_size; j++)
			//	{
			//		if(m_cells[i][j].m_constructiontype == null)
			//			canvas.drawBitmap(m_cell_profit_none_bitmap, 
			//				m_cells[i][j].m_cell_imagepoint.x - off_x, 
			//				m_cells[i][j].m_cell_imagepoint.y - off_y, 
			//				null);
			//	}
			//}
			
			// 연결선
			if(m_current_point.x > -1 && m_cells[m_current_point.x][m_current_point.y].m_construction != null)	
			{
				int i = m_cells[m_current_point.x][m_current_point.y].m_construction.m_point.x;
				int j = m_cells[m_current_point.x][m_current_point.y].m_construction.m_point.y;
				for(int k=0; k<DepartmentManager.DEPARTMENT_COUNT; k++)
				{
					if(m_cells[i][j].m_construction.m_department_manager.m_departments[k] != null)
					{
						if(m_cells[i][j].m_construction.m_department_manager.m_departments[k].m_department_type 
								== DepartmentTypes.PURCHASE)
						{
							if(((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction != null)
							{
								Point source = new Point();
								Point destination = new Point(m_cells[i][j].m_cellpoint);
								
								int source_size;
								int destination_size;
								
								Point temp = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_point;
								
								source.x = m_cells[temp.x][temp.y].m_cellpoint.x;
								source.y = m_cells[temp.x][temp.y].m_cellpoint.y;
								source_size = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_size;
								
								destination_size = m_cells[i][j].m_construction.m_size;
								
								source.y = source.y + Cell.CELL_HEIGHT/2*(source_size-1);
								
								destination.y = destination.y + Cell.CELL_HEIGHT/2*(destination_size-1);
								
								Paint p = new Paint();
								p.setStrokeWidth(3);
								p.setTextSize(12);
								
								
								//double distance = Math.sqrt(Math.pow(temp.x-i, 2) + Math.pow(temp.y-j, 2));
								
								//Path path = new Path();
								//path.moveTo((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y);
								
								double dif_x = 0;;
								double dif_y = 0;;
								
								if(source.x - destination.x > 0)
								{
									if(source.y - destination.y > 0)
									{
										dif_x = source.x - destination.x;
										dif_y = source.y - destination.y;
									}
									else
									{
										dif_x = destination.x - source.x;
										dif_y = destination.y - source.y;
									}
								}
								else
								{
									if(source.y - destination.y > 0)
									{
										dif_x = destination.x - source.x;
										dif_y = destination.y - source.y;
									}
									else
									{
										dif_x = source.x - destination.x;
										dif_y = source.y - destination.y;
									}
								}
								
								//double slope = -dif_x/dif_y;
								
								double def_x2 = dif_x;
								double def_y2 = dif_y;
								
								dif_x = def_y2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
								dif_y = def_x2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
						
								dif_x /= 2;
								dif_y /= 2;
								
								if(source.x > destination.x)
								{
									if(source.y > destination.y)
									{
										dif_y = -dif_y;
										dif_x *= 10;
										dif_y *= 10;
									}
									else
									{
										dif_y = -dif_y;
										if((double)(source.y-destination.y)/(source.x-destination.x) > 3)
										{
											dif_x *= 19;
											dif_y *= 19;
										}
										else if((double)(source.y-destination.y)/(source.x-destination.x) > 1)
										{
											dif_x *= 22;
											dif_y *= 22;
										}
										else if((double)(source.y-destination.y)/(source.x-destination.x) > 0.5)
										{
											dif_x *= 25;
											dif_y *= 25;
										}
										else
										{
											dif_x *= 28;
											dif_y *= 28;
										}
									}
								}
								else
								{
									if(source.y > destination.y)
									{
										dif_x = -dif_x;
										if(-(double)(destination.y-source.y)/(destination.x-source.x) > 3)
										{
											dif_x *= 19;
											dif_y *= 19;
										}
										else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 1)
										{
											dif_x *= 22;
											dif_y *= 22;
										}
										else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 0.5)
										{
											dif_x *= 25;
											dif_y *= 25;
										}
										else
										{
											dif_x *= 28;
											dif_y *= 28;
										}
									}
									else
									{
										dif_x = -dif_x;
										dif_x *= 10;
										dif_y *= 10;
									}
								}

								if(source.x == destination.x)
								{
									dif_x = 15/2;
									dif_y = 0;
								}
								
								if(source.y == destination.y)
								{
									dif_x = 0;
									dif_y = -15/2;
								}
								
								//canvas.drawLine((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y, (source.x+ destination.x)/2-off_x+(int)dif_x,  (source.y+ destination.y)/2-off_y+(int)dif_y, p);

								//path.rLineTo(dif_x, dif_y);
								
								//Screen.beginZoomRender(canvas);
								
								p.setAntiAlias(true);
								p.setColor(Color.WHITE);
								canvas.drawLine(source.x  - off_x, source.y  - off_y, destination.x  - off_x, destination.y - off_y, p);
								
								canvas.save();
								if(Screen.getZoom() != 1.0f)
									canvas.scale(10.0f/7.0f, 10.0f/7.0f, (source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y);
								
								p.setColor(Color.YELLOW);
								canvas.drawText(String.format("%.1fKm", ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_distance_from_linked_construction), 
										(source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y, p);
								//canvas.drawTextOnPath(String.format("%.1fKm", distance), path, 0, 0, p);
								
								canvas.restore();
								//Screen.endZoomRender(canvas);
							}
						}
						
						if(m_cells[i][j].m_construction.m_department_manager.m_departments[k].m_department_type 
								== DepartmentTypes.SALES)
						{
							for(int t=0; t<((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.size(); t++)
							{
								Point source =new Point(m_cells[i][j].m_cellpoint);
								Point destination = new Point();
								
								int source_size;
								int destination_size;
								
								Point temp = ((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.get(t).m_point;
								
								destination.x = m_cells[temp.x][temp.y].m_cellpoint.x;
								destination.y = m_cells[temp.x][temp.y].m_cellpoint.y;
								destination_size = ((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.get(t).m_size;
								
								source_size = m_cells[i][j].m_construction.m_size;
								
								source.y = source.y + Cell.CELL_HEIGHT/2*(source_size-1);
								
								destination.y = destination.y + Cell.CELL_HEIGHT/2*(destination_size-1);
								
								Paint p = new Paint();
								p.setStrokeWidth(3);
								p.setTextSize(12);
								
								
								//double distance = Math.sqrt(Math.pow(temp.x-i, 2) + Math.pow(temp.y-j, 2));
								
								//Path path = new Path();
								//path.moveTo((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y);
								
								double dif_x = 0;;
								double dif_y = 0;;
								
								if(source.x - destination.x > 0)
								{
									if(source.y - destination.y > 0)
									{
										dif_x = source.x - destination.x;
										dif_y = source.y - destination.y;
									}
									else
									{
										dif_x = destination.x - source.x;
										dif_y = destination.y - source.y;
									}
								}
								else
								{
									if(source.y - destination.y > 0)
									{
										dif_x = destination.x - source.x;
										dif_y = destination.y - source.y;
									}
									else
									{
										dif_x = source.x - destination.x;
										dif_y = source.y - destination.y;
									}
								}
								
								//double slope = -dif_x/dif_y;
								
								double def_x2 = dif_x;
								double def_y2 = dif_y;
								
								dif_x = def_y2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
								dif_y = def_x2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
						
								dif_x /= 2;
								dif_y /= 2;
								
								if(source.x > destination.x)
								{
									if(source.y > destination.y)
									{
										dif_y = -dif_y;
										dif_x *= 10;
										dif_y *= 10;
									}
									else
									{
										dif_y = -dif_y;
										if((double)(source.y-destination.y)/(source.x-destination.x) > 3)
										{
											dif_x *= 19;
											dif_y *= 19;
										}
										else if((double)(source.y-destination.y)/(source.x-destination.x) > 1)
										{
											dif_x *= 22;
											dif_y *= 22;
										}
										else if((double)(source.y-destination.y)/(source.x-destination.x) > 0.5)
										{
											dif_x *= 25;
											dif_y *= 25;
										}
										else
										{
											dif_x *= 28;
											dif_y *= 28;
										}
									}
								}
								else
								{
									if(source.y > destination.y)
									{
										dif_x = -dif_x;
										if(-(double)(destination.y-source.y)/(destination.x-source.x) > 3)
										{
											dif_x *= 19;
											dif_y *= 19;
										}
										else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 1)
										{
											dif_x *= 22;
											dif_y *= 22;
										}
										else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 0.5)
										{
											dif_x *= 25;
											dif_y *= 25;
										}
										else
										{
											dif_x *= 28;
											dif_y *= 28;
										}
									}
									else
									{
										dif_x = -dif_x;
										dif_x *= 10;
										dif_y *= 10;
									}
								}

								if(source.x == destination.x)
								{
									dif_x = 15/2;
									dif_y = 0;
								}
								
								if(source.y == destination.y)
								{
									dif_x = 0;
									dif_y = -15/2;
								}
								
								//canvas.drawLine((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y, (source.x+ destination.x)/2-off_x+(int)dif_x,  (source.y+ destination.y)/2-off_y+(int)dif_y, p);

								//path.rLineTo(dif_x, dif_y);
								
								//Screen.beginZoomRender(canvas);
								
								p.setAntiAlias(true);
								p.setColor(Color.WHITE);
								canvas.drawLine(source.x  - off_x, source.y  - off_y, destination.x  - off_x, destination.y - off_y, p);
								
								canvas.save();
								if(Screen.getZoom() != 1.0f)
									canvas.scale(10.0f/7.0f, 10.0f/7.0f, (source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y);
								
								p.setColor(Color.YELLOW);
								canvas.drawText(String.format("%.1fKm", ((Purchase)((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.get(t)
										.m_department_manager.m_departments[((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_department_indexlist.get(t)]).m_distance_from_linked_construction), 
										(source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y, p);
								//canvas.drawTextOnPath(String.format("%.1fKm", distance), path, 0, 0, p);
								
								canvas.restore();
								//Screen.endZoomRender(canvas);
							}
						}
								
							
							
					}
				}
			}
			else
			{
				for(int i=0; i<m_size; i++)
				{
					for(int j=0; j<m_size; j++)
					{
						if(m_cells[i][j].m_constructiontype != null
								&& m_cells[i][j].m_constructiontype != ConstructionTypes.NOTHING
								&& m_cells[i][j].m_currentlink == 1)
						{
							for(int k=0; k<DepartmentManager.DEPARTMENT_COUNT; k++)
							{
								if(m_cells[i][j].m_construction.m_department_manager.m_departments[k] != null)
								{
									if(m_cells[i][j].m_construction.m_department_manager.m_departments[k].m_department_type == DepartmentTypes.PURCHASE)
									{
										if(((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction != null)
										{
											Point source = new Point();
											Point destination = new Point(m_cells[i][j].m_cellpoint);
											
											int source_size;
											int destination_size;
											
											Point temp = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_point;
											
											source.x = m_cells[temp.x][temp.y].m_cellpoint.x;
											source.y = m_cells[temp.x][temp.y].m_cellpoint.y;
											source_size = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_size;
											
											destination_size = m_cells[i][j].m_construction.m_size;
											
											source.y = source.y + Cell.CELL_HEIGHT/2*(source_size-1);
											
											destination.y = destination.y + Cell.CELL_HEIGHT/2*(destination_size-1);
											
											Paint p = new Paint();
											p.setStrokeWidth(3);
											p.setTextSize(12);
											
											
											//double distance = Math.sqrt(Math.pow(temp.x-i, 2) + Math.pow(temp.y-j, 2));
											
											//Path path = new Path();
											//path.moveTo((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y);
											
											double dif_x = 0;;
											double dif_y = 0;;
											
											if(source.x - destination.x > 0)
											{
												if(source.y - destination.y > 0)
												{
													dif_x = source.x - destination.x;
													dif_y = source.y - destination.y;
												}
												else
												{
													dif_x = destination.x - source.x;
													dif_y = destination.y - source.y;
												}
											}
											else
											{
												if(source.y - destination.y > 0)
												{
													dif_x = destination.x - source.x;
													dif_y = destination.y - source.y;
												}
												else
												{
													dif_x = source.x - destination.x;
													dif_y = source.y - destination.y;
												}
											}
											
											//double slope = -dif_x/dif_y;
											
											double def_x2 = dif_x;
											double def_y2 = dif_y;
											
											dif_x = def_y2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
											dif_y = def_x2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
									
											dif_x /= 2;
											dif_y /= 2;
											
											if(source.x > destination.x)
											{
												if(source.y > destination.y)
												{
													dif_y = -dif_y;
													dif_x *= 10;
													dif_y *= 10;
												}
												else
												{
													dif_y = -dif_y;
													if((double)(source.y-destination.y)/(source.x-destination.x) > 3)
													{
														dif_x *= 19;
														dif_y *= 19;
													}
													else if((double)(source.y-destination.y)/(source.x-destination.x) > 1)
													{
														dif_x *= 22;
														dif_y *= 22;
													}
													else if((double)(source.y-destination.y)/(source.x-destination.x) > 0.5)
													{
														dif_x *= 25;
														dif_y *= 25;
													}
													else
													{
														dif_x *= 28;
														dif_y *= 28;
													}
												}
											}
											else
											{
												if(source.y > destination.y)
												{
													dif_x = -dif_x;
													if(-(double)(destination.y-source.y)/(destination.x-source.x) > 3)
													{
														dif_x *= 19;
														dif_y *= 19;
													}
													else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 1)
													{
														dif_x *= 22;
														dif_y *= 22;
													}
													else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 0.5)
													{
														dif_x *= 25;
														dif_y *= 25;
													}
													else
													{
														dif_x *= 28;
														dif_y *= 28;
													}
												}
												else
												{
													dif_x = -dif_x;
													dif_x *= 10;
													dif_y *= 10;
												}
											}
	
											if(source.x == destination.x)
											{
												dif_x = 15/2;
												dif_y = 0;
											}
											
											if(source.y == destination.y)
											{
												dif_x = 0;
												dif_y = -15/2;
											}
											
											//canvas.drawLine((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y, (source.x+ destination.x)/2-off_x+(int)dif_x,  (source.y+ destination.y)/2-off_y+(int)dif_y, p);
	
											//path.rLineTo(dif_x, dif_y);
											
											//Screen.beginZoomRender(canvas);
											
											p.setAntiAlias(true);
											p.setColor(Color.WHITE);
											canvas.drawLine(source.x  - off_x, source.y  - off_y, destination.x  - off_x, destination.y - off_y, p);
											
											canvas.save();
											if(Screen.getZoom() != 1.0f)
												canvas.scale(10.0f/7.0f, 10.0f/7.0f, (source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y);
											
											p.setColor(Color.YELLOW);
											canvas.drawText(String.format("%.1fKm", ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_distance_from_linked_construction), 
													(source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y, p);
											//canvas.drawTextOnPath(String.format("%.1fKm", distance), path, 0, 0, p);
											
											canvas.restore();
											//Screen.endZoomRender(canvas);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		// 이것도 사실 맵 전체를 순환하면 안된다.
				for(int i=0; i<m_size; i++)
				{
					for(int j=0; j<m_size; j++)
					{
						Paint p = new Paint();
				    	p.setTextSize(15); 
				    	p.setTextAlign(Align.CENTER);
				    	
				    	p.setStyle(Style.FILL);
				    	
						if(m_cells[i][j].m_constructiontype != null
								&& m_cells[i][j].m_currentlink == 1)
						{
							
					    	
							if(m_cells[i][j].m_constructiontype == ConstructionTypes.FACTORY)
							{
								Point origin = new Point(m_cells[i][j].m_cellpoint);
								origin.y = origin.y + Cell.CELL_HEIGHT/2*(2-1);
								
								//Screen.beginZoomRender(canvas);
								
								canvas.save();
								if(Screen.getZoom() != 1.0f)
									canvas.scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
								
								p.setColor(Color.RED); 
								canvas.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setColor(Color.WHITE);
								p.setStyle(Style.STROKE);
								canvas.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setStyle(Style.FILL);
						    	canvas.drawText(Factory.m_name, origin.x - off_x, origin.y - off_y, p);
						    	
						    	canvas.restore();
						    	
						    	//Screen.endZoomRender(canvas);
							}
							
							if(m_cells[i][j].m_constructiontype == ConstructionTypes.RETAIL)
							{
								Point origin = new Point(m_cells[i][j].m_cellpoint);
								origin.y = origin.y + Cell.CELL_HEIGHT/2*(2-1);
								
								//Screen.beginZoomRender(canvas);
								
								canvas.save();
								if(Screen.getZoom() != 1.0f)
									canvas.scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
								
								p.setColor(Color.RED); 
								canvas.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setColor(Color.WHITE);
								p.setStyle(Style.STROKE);
								canvas.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setStyle(Style.FILL);
						    	canvas.drawText(Retail.m_name, origin.x - off_x, origin.y - off_y, p);
						    	
						    	canvas.restore();
						    	
						    	//Screen.endZoomRender(canvas);
							}
							
							if(m_cells[i][j].m_constructiontype == ConstructionTypes.FARM)
							{
								Point origin = new Point(m_cells[i][j].m_cellpoint);
								origin.y = origin.y + Cell.CELL_HEIGHT/2*(3-1);
								
								//Screen.beginZoomRender(canvas);
								
								canvas.save();
								if(Screen.getZoom() != 1.0f)
									canvas.scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
								
								p.setColor(Color.RED); 
								canvas.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setColor(Color.WHITE);
								p.setStyle(Style.STROKE);
								canvas.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setStyle(Style.FILL);
						    	canvas.drawText(Farm.m_name, origin.x - off_x, origin.y - off_y, p);
						    	
						    	canvas.restore();
						    	
						    	//Screen.endZoomRender(canvas);
							}
							
							if(m_cells[i][j].m_constructiontype == ConstructionTypes.RnD)
							{
								Point origin = new Point(m_cells[i][j].m_cellpoint);
								origin.y = origin.y + Cell.CELL_HEIGHT/2*(2-1);
								
								//Screen.beginZoomRender(canvas);
								
								canvas.save();
								if(Screen.getZoom() != 1.0f)
									canvas.scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
								
								p.setColor(Color.RED); 
								canvas.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setColor(Color.WHITE);
								p.setStyle(Style.STROKE);
								canvas.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
								p.setStyle(Style.FILL);
						    	canvas.drawText(RnD.m_name, origin.x - off_x, origin.y - off_y, p);
						    	
						    	canvas.restore();
						    	
						    	//Screen.endZoomRender(canvas);
							}
								
						}
						
						// 항구일 경우
						if(m_cells[i][j].m_bitmap == m_cell_3X3_port_bitmap && m_cells[i][j].m_currentlink == 1)
						{
							Point origin = new Point(m_cells[i][j].m_cellpoint);
							origin.y = origin.y + Cell.CELL_HEIGHT/2*(3-1);
							
							//Screen.beginZoomRender(canvas);
							
							canvas.save();
							if(Screen.getZoom() != 1.0f)
								canvas.scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
							
							p.setColor(Color.GRAY);
							canvas.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
							p.setColor(Color.WHITE);
							p.setStyle(Style.STROKE);
							canvas.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
							p.setStyle(Style.FILL);
					    	canvas.drawText("항구", origin.x - off_x, origin.y - off_y, p);
					    	
					    	canvas.restore();
					    	
					    	//if(Screen.getZoom() != 1.0f)
							//	canvas.scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
					    	
					    	//Screen.endZoomRender(canvas);
						}
					}
				}
				
		Screen.endZoomRender(canvas);

				
		if(Player.m_connection_destination_construction != null)
		{
			int x, y;


			//if(m_cellselector.selector_color == SelectorColor.GREEN)
			{
				
				//if(m_cells[m_current_point.x][m_current_point.y].m_first_link_point.x > -1)
				{
					x = m_cells[Player.m_connection_destination_construction.m_point.x][Player.m_connection_destination_construction.m_point.y].m_first_link_point.x;
					y = m_cells[Player.m_connection_destination_construction.m_point.x][Player.m_connection_destination_construction.m_point.y].m_first_link_point.y;
					
					if(Player.m_temp == 0)
					{
						Player.m_temp++;
						if(Player.m_connection_destination_construction.m_size == 2)
						{
							m_connection_cellselector.mCurrentBitmapID = CellSelector.twobytwo_red;
							m_connection_cellselector.SetSize(2);
						}
						if(Player.m_connection_destination_construction.m_size == 3)
						{
							m_connection_cellselector.mCurrentBitmapID = CellSelector.threebythree_red;
							m_connection_cellselector.SetSize(3);
						}
					}
						
					Rect dest = new Rect(m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_connection_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x, 
							m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y,
							m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_connection_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x + m_connection_cellselector.mSpriteWidth,
							m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y + m_connection_cellselector.mSpriteHeight);
				 
					Screen.beginZoomRender(canvas);
					
				    canvas.drawBitmap(m_connection_cellselector.mCurrentBitmapID, m_connection_cellselector.mSRectangle, dest, null);
				    
				    Screen.endZoomRender(canvas);
				    
				}
			}
		}
		
		// 이 내용은 Selector에 넣어야
		if(constructiontype != UserInterface.ToolbarConstructionTypes.NOTHING)
		{
			if(m_cellselector.mCurrentBitmapID != null && m_current_point.x>-1)
			{
				//canvas.drawBitmap(m_cellselector.current_bitmap, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.size-1)*Cell.CELL_WIDTH/2 - off_x, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y, 
				//		null);
				
				int x, y;

				
				x = m_current_point.x;
				y = m_current_point.y;
				
				
				Rect dest = new Rect(m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x, 
						m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y,
						m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x + m_cellselector.mSpriteWidth,
						m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y + m_cellselector.mSpriteHeight);
				
				Screen.beginZoomRender(canvas);
				
				canvas.drawBitmap(m_cellselector.mCurrentBitmapID, m_cellselector.mSRectangle, dest, null);
			    
			    Screen.endZoomRender(canvas);

				Paint p = new Paint();
		    	p.setTextSize(30/2); 
		    	
		    	// 소매점 공장 등 나중에 클래스에서 다루기
		    	p.setColor(Color.WHITE);
		    	if(m_cellselector.selector_color != CellSelector.SelectorColor.RED)
		    	{
		    		canvas.drawBitmap(UserInterface.m_choice_popup_yesno_info_bitmap, 0, 430/2, null);
		    		
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
			    	{
			    		canvas.drawText(Retail.m_name, 30/2, 470/2, p);
			    		canvas.drawText("건설비 : "+Retail.cost/10000+"(만)", 30/2, 500/2, p);
			    		canvas.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		canvas.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		canvas.drawText("유지비  : "+Retail.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		canvas.drawText("건설", 30/2, 650/2, p);
			    		canvas.drawText("취소", 200/2, 650/2, p);
			    	}
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
			    	{
			    		canvas.drawText(Factory.m_name, 30/2, 470/2, p);
			    		canvas.drawText("건설비 : "+Factory.cost/10000+"(만)", 30/2, 500/2, p);
			    		canvas.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		canvas.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		canvas.drawText("유지비  : "+Factory.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		canvas.drawText("건설", 30/2, 650/2, p);
			    		canvas.drawText("취소", 200/2, 650/2, p);
			    	}
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
			    	{
			    		canvas.drawText(Farm.m_name, 30/2, 470/2, p);
			    		canvas.drawText("건설비 : "+Farm.cost/10000+"(만)", 30/2, 500/2, p);
			    		canvas.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		canvas.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		canvas.drawText("유지비  : "+Farm.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		canvas.drawText("건설", 30/2, 650/2, p);
			    		canvas.drawText("취소", 200/2, 650/2, p);
			    	}
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RnD)
			    	{
			    		canvas.drawText(RnD.m_name, 30/2, 470/2, p);
			    		canvas.drawText("건설비 : "+RnD.cost/10000+"(만)", 30/2, 500/2, p);
			    		canvas.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		canvas.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		canvas.drawText("유지비  : "+RnD.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		canvas.drawText("건설", 30/2, 650/2, p);
			    		canvas.drawText("취소", 200/2, 650/2, p);
			    	}
		    	}
		    	else
		    	{
		    		canvas.drawBitmap(UserInterface.m_choice_popup_no_info_bitmap, 0, 430/2, null);
		    		
		    		if(constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
			    		canvas.drawText(Retail.m_name, 30/2, 470/2, p);
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
			    		canvas.drawText(Factory.m_name, 30/2, 470/2, p);
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
			    		canvas.drawText(Farm.m_name, 30/2, 470/2, p);
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RnD)
			    		canvas.drawText(RnD.m_name, 30/2, 470/2, p);
			    	canvas.drawText("건설할 수 없는", 30/2, 500/2, p);
			    	canvas.drawText("지역입니다.", 30/2, 500/2+30/2, p);
			    	canvas.drawText("취소", 200/2, 650/2, p);
		    	}
			}
			else
			{
				Paint p = new Paint();
		    	p.setTextSize(30/2); 
		    	p.setColor(Color.WHITE);
		    	
				canvas.drawBitmap(UserInterface.m_choice_popup_no_info_bitmap, 0, 430/2, null);
	    		
				if(constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
		    		canvas.drawText(Retail.m_name, 30/2, 470/2, p);
		    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
		    		canvas.drawText(Factory.m_name, 30/2, 470/2, p);
		    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
		    		canvas.drawText(Farm.m_name, 30/2, 470/2, p);
		    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RnD)
		    		canvas.drawText(RnD.m_name, 30/2, 470/2, p);
		    	
		    	canvas.drawText("취소", 200/2, 650/2, p);
			}
		}
		else
		{
			if(m_cellselector.mCurrentBitmapID != null && m_current_point.x>-1)
			{
				//canvas.drawBitmap(m_cellselector.current_bitmap, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.size-1)*Cell.CELL_WIDTH/2 - off_x, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y, 
				//		null);
				
				int x, y;
				
				if(m_cellselector.selector_color == SelectorColor.GREEN)
				{
					if(m_cells[m_current_point.x][m_current_point.y].m_first_link_point.x > -1)
					{
						x = m_cells[m_current_point.x][m_current_point.y].m_first_link_point.x;
						y = m_cells[m_current_point.x][m_current_point.y].m_first_link_point.y;
						
						Rect dest = new Rect(m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x, 
								m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y,
								m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x + m_cellselector.mSpriteWidth,
								m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y + m_cellselector.mSpriteHeight);
						
						Screen.beginZoomRender(canvas);
						
						canvas.drawBitmap(m_cellselector.mCurrentBitmapID, m_cellselector.mSRectangle, dest, null);
					    
					    Screen.endZoomRender(canvas);
					    
					}
				}
			}
		}*/

	}
	
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		int off_x = GameState.off_x;
		int off_y = GameState.off_y;
		
		UserInterface.ToolbarConstructionTypes constructiontype = 
				((GameState) AppManager.getInstance().m_state).m_UI.constructiontype;
		
		// 중복되어 그리는 것을 방지하기 위해 맵의 크기가 같은 boolean형 배열을 이용
		boolean isdrawn[][] = new boolean[m_size][m_size];
		
		Screen.beginZoomRender(spriteBatcher.getCanvas());
		
		// 가장 먼저, 도로와 차량부터 그린다. 도로부터 그리는 이유는 차량때문이다.
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				if(isdrawn[i][j] == false && m_cells[i][j].m_celltype == Cell.CellTypes.ROAD1)
				{
					isdrawn[i][j] = true;
					
					if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
						spriteBatcher.drawBitmap2(m_cells[i][j].m_dark_version_bitmapID, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
					else
						spriteBatcher.drawBitmap2(m_cells[i][j].mBitmapID, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
					
				}
			}
		}
		
		// 도로를 그린 후, 이제 차량을 그린다.
		//VehicleManager.GetInstance().Render(canvas, off_x, off_y, this);
		
		// 그 다음, 셀그룹(2X2 이상의 셀의 그룹)을 기준으로 화면을 그린다.
		for(int k = 0; k<m_cellgroup_list.size(); k++)
		{
			for(int i = 0; i<=m_cellgroup_list.get(k).m_point.x; i++)
			{
				for(int j = 0; j<=m_cellgroup_list.get(k).m_point.y; j++)
				{
					if(isdrawn[i][j] == false)
					{
						isdrawn[i][j] = true;
						// 셀이 다른 셀과 독립적이면
						if(m_cells[i][j].m_maxlink == 0)
						{
							if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
								spriteBatcher.drawBitmap2(m_cells[i][j].m_dark_version_bitmapID, 
										m_cells[i][j].m_imagepoint.x - off_x, 
										m_cells[i][j].m_imagepoint.y - off_y, 
										null);
							else
								spriteBatcher.drawBitmap2(m_cells[i][j].mBitmapID, 
										m_cells[i][j].m_imagepoint.x - off_x, 
										m_cells[i][j].m_imagepoint.y - off_y, 
										null);
						}
						else	// 셀이 다른 셀과 연결되어 있다면
						{
							// 셀의 m_maxlink과 m_currentlink가 같은 셀에 한하여
							// 즉, 건물의 최하단 셀에서
							if(m_cells[i][j].m_maxlink == m_cells[i][j].m_currentlink)
							{
								// 셀에 건물이 지어져 있다면
								if(m_cells[i][j].m_constructiontype != null)
								{
									// +1은 지금 현재 셀 배치에 약간의 공간이 남기 때문에 추가했다. 
									// 셀의 높이계산에서 문제가 있는 것 같다. 이것을 통해 2X2셀 이상을 점유하는
									// 건물을 그리더라도 공백이 크게 느껴지지 않는다.
									spriteBatcher.drawBitmap2(m_cells[i][j].mBitmapID, 
											m_cells[i][j].m_constructionpoint.x - off_x, 
											m_cells[i][j].m_constructionpoint.y - off_y - 1,
											null);
								}
								else
								{
									if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
									{
										spriteBatcher.drawBitmap2(m_cells[i][j].m_dark_version_bitmapID, 
												m_cells[i][j].m_constructionpoint.x - off_x, 
												m_cells[i][j].m_constructionpoint.y - off_y - 1,
												null);
									}
									else
										spriteBatcher.drawBitmap2(m_cells[i][j].mBitmapID, 
												m_cells[i][j].m_constructionpoint.x - off_x, 
												m_cells[i][j].m_constructionpoint.y - off_y - 1,
												null);
								}
							}
						}
					}
				}
			}
		}
		
		
		// 이제 남은 셀을 채운다. 남은 셀은 모두 1X1셀 뿐이다.
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				if(isdrawn[i][j] == false)
				{
					isdrawn[i][j] = true;
					if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
						spriteBatcher.drawBitmap2(m_cells[i][j].m_dark_version_bitmapID, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
					else
						spriteBatcher.drawBitmap2(m_cells[i][j].mBitmapID, 
								m_cells[i][j].m_imagepoint.x - off_x, 
								m_cells[i][j].m_imagepoint.y - off_y, 
								null);
				}
			}
		}
		
		if(m_citiyicon_selection == 1)
		{
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_A)
					{
						spriteBatcher.drawBitmap2(R.drawable.cell_class_a,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_B)
					{
						spriteBatcher.drawBitmap2(R.drawable.cell_class_b,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_C)
					{
						spriteBatcher.drawBitmap2(R.drawable.cell_class_c,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_D)
					{
						spriteBatcher.drawBitmap2(R.drawable.cell_class_d,
								m_cells[i][j].m_cell_imagepoint.x - off_x, 
								m_cells[i][j].m_cell_imagepoint.y - off_y, 
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_E)
					{
						spriteBatcher.drawBitmap2(R.drawable.cell_class_e,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
					if(m_cells[i][j].m_landvalue == LandValues.CLASS_F)
					{
						spriteBatcher.drawBitmap2(R.drawable.cell_class_f,
								m_cells[i][j].m_cell_imagepoint.x - off_x,
								m_cells[i][j].m_cell_imagepoint.y - off_y,
								null);
					}
				}
			}
			
		}
		
		// 사실 m_citiyicon_selection == 2, 3은 이렇게 온 맵을 순환하는 것이 아니고 리스트로 해당 지역만
		// 확인하도록 만들어야 한다.
		if(m_citiyicon_selection == 2)
		{
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					if(m_cells[i][j].m_constructiontype != null
							&& m_cells[i][j].m_constructiontype != ConstructionTypes.PORT)
					{
						double temp = m_cells[i][j].m_construction.m_monthly_netprofit[Player.m_netprofit_index];
						
						if(temp > 0)
						{
							temp /= CapitalismSystem.m_max_daily_positive_profit;
							if(temp > 0.666)
								spriteBatcher.drawBitmap2(R.drawable.cell_profit_positive_high, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else if(temp > 0.333)
								spriteBatcher.drawBitmap2(R.drawable.cell_profit_positive_medium, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else
								spriteBatcher.drawBitmap2(R.drawable.cell_profit_positive_low, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
						}
						else
						{
							temp /= CapitalismSystem.m_max_daily_negative_profit;
							if(temp > 0.666)
								spriteBatcher.drawBitmap2(R.drawable.cell_profit_negative_high, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else if(temp > 0.333)
								spriteBatcher.drawBitmap2(R.drawable.cell_profit_negative_medium, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
							else
								spriteBatcher.drawBitmap2(R.drawable.cell_profit_negative_low, 
										m_cells[i][j].m_cell_imagepoint.x - off_x, 
										m_cells[i][j].m_cell_imagepoint.y - off_y, 
										null);
						}
					}
					/*
					else
					{
						canvas.drawBitmap(m_cell_profit_none_bitmap, 
								m_cells[i][j].m_cell_imagepoint.x - off_x, 
								m_cells[i][j].m_cell_imagepoint.y - off_y, 
								null);
					}*/
				}
			}
		}
		
		//Screen.endZoomRender(spriteBatcher.canvas);

		
		if(m_citiyicon_selection == 3)
		{
			/*
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					if(m_cells[i][j].m_constructiontype == null)
						canvas.drawBitmap(m_cell_profit_none_bitmap, 
							m_cells[i][j].m_cell_imagepoint.x - off_x, 
							m_cells[i][j].m_cell_imagepoint.y - off_y, 
							null);
				}
			}*/
			
			
		}
		
		if(true)
			return;
		
		// 연결선
		if(m_current_point.x > -1 && m_cells[m_current_point.x][m_current_point.y].m_construction != null)	
		{
			int i = m_cells[m_current_point.x][m_current_point.y].m_construction.m_point.x;
			int j = m_cells[m_current_point.x][m_current_point.y].m_construction.m_point.y;
			for(int k=0; k<DepartmentManager.DEPARTMENT_COUNT; k++)
			{
				if(m_cells[i][j].m_construction.m_department_manager.m_departments[k] != null)
				{
					if(m_cells[i][j].m_construction.m_department_manager.m_departments[k].m_department_type 
							== DepartmentTypes.PURCHASE)
					{
						if(((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction != null)
						{
							Point source = new Point();
							Point destination = new Point(m_cells[i][j].m_cellpoint);
							
							int source_size;
							int destination_size;
							
							Point temp = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_point;
							
							source.x = m_cells[temp.x][temp.y].m_cellpoint.x;
							source.y = m_cells[temp.x][temp.y].m_cellpoint.y;
							source_size = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_size;
							
							destination_size = m_cells[i][j].m_construction.m_size;
							
							source.y = source.y + Cell.CELL_HEIGHT/2*(source_size-1);
							
							destination.y = destination.y + Cell.CELL_HEIGHT/2*(destination_size-1);
							
							Paint p = new Paint();
							p.setStrokeWidth(3);
							p.setTextSize(12);
							
							
							//double distance = Math.sqrt(Math.pow(temp.x-i, 2) + Math.pow(temp.y-j, 2));
							
							//Path path = new Path();
							//path.moveTo((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y);
							
							double dif_x = 0;;
							double dif_y = 0;;
							
							if(source.x - destination.x > 0)
							{
								if(source.y - destination.y > 0)
								{
									dif_x = source.x - destination.x;
									dif_y = source.y - destination.y;
								}
								else
								{
									dif_x = destination.x - source.x;
									dif_y = destination.y - source.y;
								}
							}
							else
							{
								if(source.y - destination.y > 0)
								{
									dif_x = destination.x - source.x;
									dif_y = destination.y - source.y;
								}
								else
								{
									dif_x = source.x - destination.x;
									dif_y = source.y - destination.y;
								}
							}
							
							//double slope = -dif_x/dif_y;
							
							double def_x2 = dif_x;
							double def_y2 = dif_y;
							
							dif_x = def_y2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
							dif_y = def_x2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
					
							dif_x /= 2;
							dif_y /= 2;
							
							if(source.x > destination.x)
							{
								if(source.y > destination.y)
								{
									dif_y = -dif_y;
									dif_x *= 10;
									dif_y *= 10;
								}
								else
								{
									dif_y = -dif_y;
									if((double)(source.y-destination.y)/(source.x-destination.x) > 3)
									{
										dif_x *= 19;
										dif_y *= 19;
									}
									else if((double)(source.y-destination.y)/(source.x-destination.x) > 1)
									{
										dif_x *= 22;
										dif_y *= 22;
									}
									else if((double)(source.y-destination.y)/(source.x-destination.x) > 0.5)
									{
										dif_x *= 25;
										dif_y *= 25;
									}
									else
									{
										dif_x *= 28;
										dif_y *= 28;
									}
								}
							}
							else
							{
								if(source.y > destination.y)
								{
									dif_x = -dif_x;
									if(-(double)(destination.y-source.y)/(destination.x-source.x) > 3)
									{
										dif_x *= 19;
										dif_y *= 19;
									}
									else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 1)
									{
										dif_x *= 22;
										dif_y *= 22;
									}
									else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 0.5)
									{
										dif_x *= 25;
										dif_y *= 25;
									}
									else
									{
										dif_x *= 28;
										dif_y *= 28;
									}
								}
								else
								{
									dif_x = -dif_x;
									dif_x *= 10;
									dif_y *= 10;
								}
							}

							if(source.x == destination.x)
							{
								dif_x = 15/2;
								dif_y = 0;
							}
							
							if(source.y == destination.y)
							{
								dif_x = 0;
								dif_y = -15/2;
							}
							
							//canvas.drawLine((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y, (source.x+ destination.x)/2-off_x+(int)dif_x,  (source.y+ destination.y)/2-off_y+(int)dif_y, p);

							//path.rLineTo(dif_x, dif_y);
							
							//Screen.beginZoomRender(canvas);
							
							p.setAntiAlias(true);
							p.setColor(Color.WHITE);
							spriteBatcher.drawLine(source.x  - off_x, source.y  - off_y, destination.x  - off_x, destination.y - off_y, p);
							
							spriteBatcher.getCanvas().save();
							if(Screen.getZoom() != 1.0f)
								spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, (source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y);
							
							p.setColor(Color.YELLOW);
							spriteBatcher.drawText(String.format("%.1fKm", ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_distance_from_linked_construction), 
									(source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y, p);
							//canvas.drawTextOnPath(String.format("%.1fKm", distance), path, 0, 0, p);
							
							spriteBatcher.getCanvas().restore();
							//Screen.endZoomRender(canvas);
						}
					}
					
					if(m_cells[i][j].m_construction.m_department_manager.m_departments[k].m_department_type 
							== DepartmentTypes.SALES)
					{
						for(int t=0; t<((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.size(); t++)
						{
							Point source =new Point(m_cells[i][j].m_cellpoint);
							Point destination = new Point();
							
							int source_size;
							int destination_size;
							
							Point temp = ((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.get(t).m_point;
							
							destination.x = m_cells[temp.x][temp.y].m_cellpoint.x;
							destination.y = m_cells[temp.x][temp.y].m_cellpoint.y;
							destination_size = ((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.get(t).m_size;
							
							source_size = m_cells[i][j].m_construction.m_size;
							
							source.y = source.y + Cell.CELL_HEIGHT/2*(source_size-1);
							
							destination.y = destination.y + Cell.CELL_HEIGHT/2*(destination_size-1);
							
							Paint p = new Paint();
							p.setStrokeWidth(3);
							p.setTextSize(12);
							
							
							//double distance = Math.sqrt(Math.pow(temp.x-i, 2) + Math.pow(temp.y-j, 2));
							
							//Path path = new Path();
							//path.moveTo((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y);
							
							double dif_x = 0;;
							double dif_y = 0;;
							
							if(source.x - destination.x > 0)
							{
								if(source.y - destination.y > 0)
								{
									dif_x = source.x - destination.x;
									dif_y = source.y - destination.y;
								}
								else
								{
									dif_x = destination.x - source.x;
									dif_y = destination.y - source.y;
								}
							}
							else
							{
								if(source.y - destination.y > 0)
								{
									dif_x = destination.x - source.x;
									dif_y = destination.y - source.y;
								}
								else
								{
									dif_x = source.x - destination.x;
									dif_y = source.y - destination.y;
								}
							}
							
							//double slope = -dif_x/dif_y;
							
							double def_x2 = dif_x;
							double def_y2 = dif_y;
							
							dif_x = def_y2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
							dif_y = def_x2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
					
							dif_x /= 2;
							dif_y /= 2;
							
							if(source.x > destination.x)
							{
								if(source.y > destination.y)
								{
									dif_y = -dif_y;
									dif_x *= 10;
									dif_y *= 10;
								}
								else
								{
									dif_y = -dif_y;
									if((double)(source.y-destination.y)/(source.x-destination.x) > 3)
									{
										dif_x *= 19;
										dif_y *= 19;
									}
									else if((double)(source.y-destination.y)/(source.x-destination.x) > 1)
									{
										dif_x *= 22;
										dif_y *= 22;
									}
									else if((double)(source.y-destination.y)/(source.x-destination.x) > 0.5)
									{
										dif_x *= 25;
										dif_y *= 25;
									}
									else
									{
										dif_x *= 28;
										dif_y *= 28;
									}
								}
							}
							else
							{
								if(source.y > destination.y)
								{
									dif_x = -dif_x;
									if(-(double)(destination.y-source.y)/(destination.x-source.x) > 3)
									{
										dif_x *= 19;
										dif_y *= 19;
									}
									else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 1)
									{
										dif_x *= 22;
										dif_y *= 22;
									}
									else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 0.5)
									{
										dif_x *= 25;
										dif_y *= 25;
									}
									else
									{
										dif_x *= 28;
										dif_y *= 28;
									}
								}
								else
								{
									dif_x = -dif_x;
									dif_x *= 10;
									dif_y *= 10;
								}
							}

							if(source.x == destination.x)
							{
								dif_x = 15/2;
								dif_y = 0;
							}
							
							if(source.y == destination.y)
							{
								dif_x = 0;
								dif_y = -15/2;
							}
							
							//canvas.drawLine((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y, (source.x+ destination.x)/2-off_x+(int)dif_x,  (source.y+ destination.y)/2-off_y+(int)dif_y, p);

							//path.rLineTo(dif_x, dif_y);
							
							//Screen.beginZoomRender(canvas);
							
							p.setAntiAlias(true);
							p.setColor(Color.WHITE);
							spriteBatcher.drawLine(source.x  - off_x, source.y  - off_y, destination.x  - off_x, destination.y - off_y, p);
							
							spriteBatcher.getCanvas().save();
							if(Screen.getZoom() != 1.0f)
								spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, (source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y);
							
							p.setColor(Color.YELLOW);
							spriteBatcher.drawText(String.format("%.1fKm", ((Purchase)((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_constructionlist.get(t)
									.m_department_manager.m_departments[((Sales)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_department_indexlist.get(t)]).m_distance_from_linked_construction), 
									(source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y, p);
							//canvas.drawTextOnPath(String.format("%.1fKm", distance), path, 0, 0, p);
							
							spriteBatcher.getCanvas().restore();
							//Screen.endZoomRender(canvas);
						}
					}
							
						
						
				}
			}
		}
		else
		{
			for(int i=0; i<m_size; i++)
			{
				for(int j=0; j<m_size; j++)
				{
					if(m_cells[i][j].m_constructiontype != null
							&& m_cells[i][j].m_constructiontype != ConstructionTypes.NOTHING
							&& m_cells[i][j].m_currentlink == 1)
					{
						for(int k=0; k<DepartmentManager.DEPARTMENT_COUNT; k++)
						{
							if(m_cells[i][j].m_construction.m_department_manager.m_departments[k] != null)
							{
								if(m_cells[i][j].m_construction.m_department_manager.m_departments[k].m_department_type == DepartmentTypes.PURCHASE)
								{
									if(((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction != null)
									{
										Point source = new Point();
										Point destination = new Point(m_cells[i][j].m_cellpoint);
										
										int source_size;
										int destination_size;
										
										Point temp = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_point;
										
										source.x = m_cells[temp.x][temp.y].m_cellpoint.x;
										source.y = m_cells[temp.x][temp.y].m_cellpoint.y;
										source_size = ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_linked_construction.m_size;
										
										destination_size = m_cells[i][j].m_construction.m_size;
										
										source.y = source.y + Cell.CELL_HEIGHT/2*(source_size-1);
										
										destination.y = destination.y + Cell.CELL_HEIGHT/2*(destination_size-1);
										
										Paint p = new Paint();
										p.setStrokeWidth(3);
										p.setTextSize(12);
										
										
										//double distance = Math.sqrt(Math.pow(temp.x-i, 2) + Math.pow(temp.y-j, 2));
										
										//Path path = new Path();
										//path.moveTo((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y);
										
										double dif_x = 0;;
										double dif_y = 0;;
										
										if(source.x - destination.x > 0)
										{
											if(source.y - destination.y > 0)
											{
												dif_x = source.x - destination.x;
												dif_y = source.y - destination.y;
											}
											else
											{
												dif_x = destination.x - source.x;
												dif_y = destination.y - source.y;
											}
										}
										else
										{
											if(source.y - destination.y > 0)
											{
												dif_x = destination.x - source.x;
												dif_y = destination.y - source.y;
											}
											else
											{
												dif_x = source.x - destination.x;
												dif_y = source.y - destination.y;
											}
										}
										
										//double slope = -dif_x/dif_y;
										
										double def_x2 = dif_x;
										double def_y2 = dif_y;
										
										dif_x = def_y2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
										dif_y = def_x2/Math.sqrt(def_x2*def_x2 + def_y2*def_y2);
								
										dif_x /= 2;
										dif_y /= 2;
										
										if(source.x > destination.x)
										{
											if(source.y > destination.y)
											{
												dif_y = -dif_y;
												dif_x *= 10;
												dif_y *= 10;
											}
											else
											{
												dif_y = -dif_y;
												if((double)(source.y-destination.y)/(source.x-destination.x) > 3)
												{
													dif_x *= 19;
													dif_y *= 19;
												}
												else if((double)(source.y-destination.y)/(source.x-destination.x) > 1)
												{
													dif_x *= 22;
													dif_y *= 22;
												}
												else if((double)(source.y-destination.y)/(source.x-destination.x) > 0.5)
												{
													dif_x *= 25;
													dif_y *= 25;
												}
												else
												{
													dif_x *= 28;
													dif_y *= 28;
												}
											}
										}
										else
										{
											if(source.y > destination.y)
											{
												dif_x = -dif_x;
												if(-(double)(destination.y-source.y)/(destination.x-source.x) > 3)
												{
													dif_x *= 19;
													dif_y *= 19;
												}
												else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 1)
												{
													dif_x *= 22;
													dif_y *= 22;
												}
												else if(-(double)(destination.y-source.y)/(destination.x-source.x) > 0.5)
												{
													dif_x *= 25;
													dif_y *= 25;
												}
												else
												{
													dif_x *= 28;
													dif_y *= 28;
												}
											}
											else
											{
												dif_x = -dif_x;
												dif_x *= 10;
												dif_y *= 10;
											}
										}

										if(source.x == destination.x)
										{
											dif_x = 15/2;
											dif_y = 0;
										}
										
										if(source.y == destination.y)
										{
											dif_x = 0;
											dif_y = -15/2;
										}
										
										//canvas.drawLine((source.x+ destination.x)/2-off_x, (source.y+ destination.y)/2-off_y, (source.x+ destination.x)/2-off_x+(int)dif_x,  (source.y+ destination.y)/2-off_y+(int)dif_y, p);

										//path.rLineTo(dif_x, dif_y);
										
										//Screen.beginZoomRender(canvas);
										
										p.setAntiAlias(true);
										p.setColor(Color.WHITE);
										spriteBatcher.drawLine(source.x  - off_x, source.y  - off_y, destination.x  - off_x, destination.y - off_y, p);
										
										spriteBatcher.getCanvas().save();
										if(Screen.getZoom() != 1.0f)
											spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, (source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y);
										
										p.setColor(Color.YELLOW);
										spriteBatcher.drawText(String.format("%.1fKm", ((Purchase)m_cells[i][j].m_construction.m_department_manager.m_departments[k]).m_distance_from_linked_construction), 
												(source.x+ destination.x)/2-off_x+(int)dif_x, (source.y+ destination.y)/2-off_y+(int)dif_y, p);
										//canvas.drawTextOnPath(String.format("%.1fKm", distance), path, 0, 0, p);
										
										spriteBatcher.getCanvas().restore();
										//Screen.endZoomRender(canvas);
									}
								}
							}
						}
					}
				}
			}
		}
	
		// 이것도 사실 맵 전체를 순환하면 안된다.
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				Paint p = new Paint();
		    	p.setTextSize(15); 
		    	p.setTextAlign(Align.CENTER);
		    	
		    	p.setStyle(Style.FILL);
		    	
				if(m_cells[i][j].m_constructiontype != null
						&& m_cells[i][j].m_currentlink == 1)
				{
					
			    	
					if(m_cells[i][j].m_constructiontype == ConstructionTypes.FACTORY)
					{
						Point origin = new Point(m_cells[i][j].m_cellpoint);
						origin.y = origin.y + Cell.CELL_HEIGHT/2*(2-1);
						
						//Screen.beginZoomRender(canvas);
						
						spriteBatcher.getCanvas().save();
						if(Screen.getZoom() != 1.0f)
							spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
						
						p.setColor(Color.RED); 
						spriteBatcher.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setColor(Color.WHITE);
						p.setStyle(Style.STROKE);
						spriteBatcher.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setStyle(Style.FILL);
				    	spriteBatcher.drawText(Factory.m_name, origin.x - off_x, origin.y - off_y, p);
				    	
				    	spriteBatcher.getCanvas().restore();
				    	
				    	//Screen.endZoomRender(canvas);
					}
					
					if(m_cells[i][j].m_constructiontype == ConstructionTypes.RETAIL)
					{
						Point origin = new Point(m_cells[i][j].m_cellpoint);
						origin.y = origin.y + Cell.CELL_HEIGHT/2*(2-1);
						
						//Screen.beginZoomRender(canvas);
						
						spriteBatcher.getCanvas().save();
						if(Screen.getZoom() != 1.0f)
							spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
						
						p.setColor(Color.RED); 
						spriteBatcher.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setColor(Color.WHITE);
						p.setStyle(Style.STROKE);
						spriteBatcher.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setStyle(Style.FILL);
						spriteBatcher.drawText(Retail.m_name, origin.x - off_x, origin.y - off_y, p);
				    	
				    	spriteBatcher.getCanvas().restore();
				    	
				    	//Screen.endZoomRender(canvas);
					}
					
					if(m_cells[i][j].m_constructiontype == ConstructionTypes.FARM)
					{
						Point origin = new Point(m_cells[i][j].m_cellpoint);
						origin.y = origin.y + Cell.CELL_HEIGHT/2*(3-1);
						
						//Screen.beginZoomRender(canvas);
						
						spriteBatcher.getCanvas().save();
						if(Screen.getZoom() != 1.0f)
							spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
						
						p.setColor(Color.RED); 
						spriteBatcher.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setColor(Color.WHITE);
						p.setStyle(Style.STROKE);
						spriteBatcher.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setStyle(Style.FILL);
						spriteBatcher.drawText(Farm.m_name, origin.x - off_x, origin.y - off_y, p);
				    	
						spriteBatcher.getCanvas().restore();
				    	
				    	//Screen.endZoomRender(canvas);
					}
					
					if(m_cells[i][j].m_constructiontype == ConstructionTypes.RnD)
					{
						Point origin = new Point(m_cells[i][j].m_cellpoint);
						origin.y = origin.y + Cell.CELL_HEIGHT/2*(2-1);
						
						//Screen.beginZoomRender(canvas);
						
						spriteBatcher.getCanvas().save();
						if(Screen.getZoom() != 1.0f)
							spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
						
						p.setColor(Color.RED); 
						spriteBatcher.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setColor(Color.WHITE);
						p.setStyle(Style.STROKE);
						spriteBatcher.drawRect(origin.x - 48/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 48/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
						p.setStyle(Style.FILL);
						spriteBatcher.drawText(RnD.m_name, origin.x - off_x, origin.y - off_y, p);
				    	
				    	spriteBatcher.getCanvas().restore();
				    	
				    	//Screen.endZoomRender(canvas);
					}
						
				}
				
				// 항구일 경우
				if(m_cells[i][j].m_bitmap == m_cell_3X3_port_bitmap && m_cells[i][j].m_currentlink == 1)
				{
					Point origin = new Point(m_cells[i][j].m_cellpoint);
					origin.y = origin.y + Cell.CELL_HEIGHT/2*(3-1);
					
					//Screen.beginZoomRender(canvas);
					
					spriteBatcher.getCanvas().save();
					if(Screen.getZoom() != 1.0f)
						spriteBatcher.getCanvas().scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
					
					p.setColor(Color.GRAY);
					spriteBatcher.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
					p.setColor(Color.WHITE);
					p.setStyle(Style.STROKE);
					spriteBatcher.drawRect(origin.x - 32/2 - off_x, origin.y - 12/2 - 15/2 - off_y, origin.x + 32/2 - off_x, origin.y + 20/2 - 15/2 - off_y, p);
					p.setStyle(Style.FILL);
					spriteBatcher.drawText("항구", origin.x - off_x, origin.y - off_y, p);
			    	
			    	spriteBatcher.getCanvas().restore();
			    	
			    	//if(Screen.getZoom() != 1.0f)
					//	canvas.scale(10.0f/7.0f, 10.0f/7.0f, origin.x - off_x, origin.y - off_y);
			    	
			    	//Screen.endZoomRender(canvas);
				}
			}
		}
		
		Screen.endZoomRender(spriteBatcher.getCanvas());
	
				
		if(Player.m_connection_destination_construction != null)
		{
			int x, y;
	
	
			//if(m_cellselector.selector_color == SelectorColor.GREEN)
			{
				
				//if(m_cells[m_current_point.x][m_current_point.y].m_first_link_point.x > -1)
				{
					x = m_cells[Player.m_connection_destination_construction.m_point.x][Player.m_connection_destination_construction.m_point.y].m_first_link_point.x;
					y = m_cells[Player.m_connection_destination_construction.m_point.x][Player.m_connection_destination_construction.m_point.y].m_first_link_point.y;
					
					if(Player.m_temp == 0)
					{
						Player.m_temp++;
						if(Player.m_connection_destination_construction.m_size == 2)
						{
							m_connection_cellselector.mCurrentBitmapID = R.drawable.twobytwo_red;
							m_connection_cellselector.SetSize(2);
						}
						if(Player.m_connection_destination_construction.m_size == 3)
						{
							m_connection_cellselector.mCurrentBitmapID = R.drawable.threebythree_red;
							m_connection_cellselector.SetSize(3);
						}
					}
						
					Rect dest = new Rect(m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_connection_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x, 
							m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y,
							m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_connection_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x + m_connection_cellselector.mSpriteWidth,
							m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y + m_connection_cellselector.mSpriteHeight);
				 
					Screen.beginZoomRender(spriteBatcher.getCanvas());
					
					spriteBatcher.drawBitmap2(m_connection_cellselector.mCurrentBitmapID, m_connection_cellselector.mSRectangle, dest, null);
				    
				    Screen.endZoomRender(spriteBatcher.getCanvas());
				    
				}
			}
		}
		
		// 이 내용은 Selector에 넣어야
		if(constructiontype != UserInterface.ToolbarConstructionTypes.NOTHING)
		{
			if(m_cellselector.mCurrentBitmapID != -1 && m_current_point.x>-1)
			{
				//canvas.drawBitmap(m_cellselector.current_bitmap, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.size-1)*Cell.CELL_WIDTH/2 - off_x, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y, 
				//		null);
				
				int x, y;
	
				
				x = m_current_point.x;
				y = m_current_point.y;
				
				
				Rect dest = new Rect(m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x, 
						m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y,
						m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x + m_cellselector.mSpriteWidth,
						m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y + m_cellselector.mSpriteHeight);
				
				Screen.beginZoomRender(spriteBatcher.getCanvas());
				
				spriteBatcher.drawBitmap2(m_cellselector.mCurrentBitmapID, m_cellselector.mSRectangle, dest, null);
			    
			    Screen.endZoomRender(spriteBatcher.getCanvas());
	
				Paint p = new Paint();
		    	p.setTextSize(30/2); 
		    	
		    	// 소매점 공장 등 나중에 클래스에서 다루기
		    	p.setColor(Color.WHITE);
		    	if(m_cellselector.selector_color != CellSelector.SelectorColor.RED)
		    	{
		    		spriteBatcher.drawBitmap2(R.drawable.ui_choice_popup_yesno_info, 0, 430/2, null);
		    		
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
			    	{
			    		spriteBatcher.drawText(Retail.m_name, 30/2, 470/2, p);
			    		spriteBatcher.drawText("건설비 : "+Retail.cost/10000+"(만)", 30/2, 500/2, p);
			    		spriteBatcher.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		spriteBatcher.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		spriteBatcher.drawText("유지비  : "+Retail.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		spriteBatcher.drawText("건설", 30/2, 650/2, p);
			    		spriteBatcher.drawText("취소", 200/2, 650/2, p);
			    	}
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
			    	{
			    		spriteBatcher.drawText(Factory.m_name, 30/2, 470/2, p);
			    		spriteBatcher.drawText("건설비 : "+Factory.cost/10000+"(만)", 30/2, 500/2, p);
			    		spriteBatcher.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		spriteBatcher.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		spriteBatcher.drawText("유지비  : "+Factory.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		spriteBatcher.drawText("건설", 30/2, 650/2, p);
			    		spriteBatcher.drawText("취소", 200/2, 650/2, p);
			    	}
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
			    	{
			    		spriteBatcher.drawText(Farm.m_name, 30/2, 470/2, p);
			    		spriteBatcher.drawText("건설비 : "+Farm.cost/10000+"(만)", 30/2, 500/2, p);
			    		spriteBatcher.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		spriteBatcher.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		spriteBatcher.drawText("유지비  : "+Farm.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		spriteBatcher.drawText("건설", 30/2, 650/2, p);
			    		spriteBatcher.drawText("취소", 200/2, 650/2, p);
			    	}
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RnD)
			    	{
			    		spriteBatcher.drawText(RnD.m_name, 30/2, 470/2, p);
			    		spriteBatcher.drawText("건설비 : "+RnD.cost/10000+"(만)", 30/2, 500/2, p);
			    		spriteBatcher.drawText("지가 : "+m_total_landvalue/10000+"(만)", 30/2, 530/2, p);
			    		spriteBatcher.drawText("건물구입 : "+m_total_purchase/10000+"(만)", 30/2, 560/2, p);
			    		spriteBatcher.drawText("유지비  : "+RnD.maintenance/10000+"(만)", 30/2, 590/2, p);
			    		spriteBatcher.drawText("건설", 30/2, 650/2, p);
			    		spriteBatcher.drawText("취소", 200/2, 650/2, p);
			    	}
		    	}
		    	else
		    	{
		    		spriteBatcher.drawBitmap2(R.drawable.ui_choice_popup_no_info, 0, 430/2, null);
		    		
		    		if(constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
		    			spriteBatcher.drawText(Retail.m_name, 30/2, 470/2, p);
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
			    		spriteBatcher.drawText(Factory.m_name, 30/2, 470/2, p);
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
			    		spriteBatcher.drawText(Farm.m_name, 30/2, 470/2, p);
			    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RnD)
			    		spriteBatcher.drawText(RnD.m_name, 30/2, 470/2, p);
			    	spriteBatcher.drawText("건설할 수 없는", 30/2, 500/2, p);
			    	spriteBatcher.drawText("지역입니다.", 30/2, 500/2+30/2, p);
			    	spriteBatcher.drawText("취소", 200/2, 650/2, p);
		    	}
			}
			else
			{
				Paint p = new Paint();
		    	p.setTextSize(30/2); 
		    	p.setColor(Color.WHITE);
		    	
		    	spriteBatcher.drawBitmap2(R.drawable.ui_choice_popup_no_info, 0, 430/2, null);
	    		
				if(constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
					spriteBatcher.drawText(Retail.m_name, 30/2, 470/2, p);
		    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
		    		spriteBatcher.drawText(Factory.m_name, 30/2, 470/2, p);
		    	if(constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
		    		spriteBatcher.drawText(Farm.m_name, 30/2, 470/2, p);
		    	if(constructiontype == UserInterface.ToolbarConstructionTypes.RnD)
		    		spriteBatcher.drawText(RnD.m_name, 30/2, 470/2, p);
		    	
		    	spriteBatcher.drawText("취소", 200/2, 650/2, p);
			}
		}
		else
		{
			if(m_cellselector.mCurrentBitmapID != -1 && m_current_point.x>-1)
			{
				//canvas.drawBitmap(m_cellselector.current_bitmap, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.size-1)*Cell.CELL_WIDTH/2 - off_x, 
				//		cells[m_current_point.x][m_current_point.y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y, 
				//		null);
				
				int x, y;
				
				if(m_cellselector.selector_color == SelectorColor.GREEN)
				{
					if(m_cells[m_current_point.x][m_current_point.y].m_first_link_point.x > -1)
					{
						x = m_cells[m_current_point.x][m_current_point.y].m_first_link_point.x;
						y = m_cells[m_current_point.x][m_current_point.y].m_first_link_point.y;
						
						Rect dest = new Rect(m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x, 
								m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y,
								m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 - (m_cellselector.m_size-1)*Cell.CELL_WIDTH/2 - off_x + m_cellselector.mSpriteWidth,
								m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 - off_y + m_cellselector.mSpriteHeight);
						
						Screen.beginZoomRender(spriteBatcher.getCanvas());
						
						spriteBatcher.drawBitmap2(m_cellselector.mCurrentBitmapID, m_cellselector.mSRectangle, dest, null);
					    
					    Screen.endZoomRender(spriteBatcher.getCanvas());
					    
					}
				}
			}
		}
}

	
	// 이것 selector에 넣자.
	public Point GetCurrentCell(MotionEvent event, int off_x, int off_y)
	{
		if(event.getSource() != 100)
			return null;
		
		//int count = 0;
		double distance;
		//Point nearpoint[] = new Point[2];
		
		int off_pt_x = off_x + Screen.zoomX((int)event.getX());
		int off_pt_y = off_y + Screen.zoomY((int)event.getY());
		
		//Rect check1 = new Rect(0, 0, 128, 62);
		//Rect check2 = new Rect(64 -64, 31-31, 64+64, 31+31);
		//Rect check3 = new Rect(-64 -64, 31-31, -64+64, 31+31);
		//Rect check4 = new Rect(0 -64, 31*2-31, 64, 31*2+31);
		
		// for 루프 모든 셀
		
		for(int i=0; i<m_size; i++)
		{
			for(int j=0; j<m_size; j++)
			{
				if(m_cells[i][j].m_cellrect.contains(off_pt_x, off_pt_y))
				{
					double temp, temp2 ;
					
					//if(temp > distance)
					//	distance = temp;
					
					if(off_pt_x >= m_cells[i][j].m_cellpoint.x)
					{
						if(m_size > j+1)
						{
							temp = Math.sqrt((off_pt_x-m_cells[i][j].m_cellpoint.x)*(off_pt_x-m_cells[i][j].m_cellpoint.x) +
									(off_pt_y-m_cells[i][j].m_cellpoint.y)*(off_pt_y-m_cells[i][j].m_cellpoint.y));
							temp2 = Math.sqrt((off_pt_x-m_cells[i][j+1].m_cellpoint.x)*(off_pt_x-m_cells[i][j+1].m_cellpoint.x) +
									(off_pt_y-m_cells[i][j+1].m_cellpoint.y)*(off_pt_y-m_cells[i][j+1].m_cellpoint.y));
							
							if(temp >= temp2)
							{
								//Utility.ShowToast(String.format("(%d,  %d)선택", i, j), Toast.LENGTH_SHORT);
								m_current_point.set(i, j+1);
								return new Point(i, j+1);
							}
							else
							{
								//Utility.ShowToast(String.format("(%d,  %d)선택", i, j), Toast.LENGTH_SHORT);
								m_current_point.set(i, j);
								return new Point(i, j);
							}
						}
						else
						{
							//Utility.ShowToast(String.format("(%d,  %d)선택", i, j), Toast.LENGTH_SHORT);
							m_current_point.set(i, j);
							return new Point(i, j);
						}
					
					}
					else
					{
						if(m_size > i+1)
						{
							temp = Math.sqrt((off_pt_x-m_cells[i][j].m_cellpoint.x)*(off_pt_x-m_cells[i][j].m_cellpoint.x) +
									(off_pt_y-m_cells[i][j].m_cellpoint.y)*(off_pt_y-m_cells[i][j].m_cellpoint.y));
							temp2 = Math.sqrt((off_pt_x-m_cells[i+1][j].m_cellpoint.x)*(off_pt_x-m_cells[i+1][j].m_cellpoint.x) +
									(off_pt_y-m_cells[i+1][j].m_cellpoint.y)*(off_pt_y-m_cells[i+1][j].m_cellpoint.y));
							
							if(temp >= temp2)
							{
								//Utility.ShowToast(String.format("(%d,  %d)선택", i, j), Toast.LENGTH_SHORT);
								m_current_point.set(i+1, j);
								return new Point(i+1, j);
							}
							else
							{
								//Utility.ShowToast(String.format("(%d,  %d)선택", i, j), Toast.LENGTH_SHORT);
								m_current_point.set(i, j);
								return new Point(i, j);
							}
						}
						else
						{
							//Utility.ShowToast(String.format("(%d,  %d)선택", i, j), Toast.LENGTH_SHORT);
							m_current_point.set(i, j);
							return new Point(i, j);
						}
					}
					
					
					
					
					//nearpoint[0] = i;

					//count++;
					//if(count >= 2)
					//	break;
				}
				
			}
			//if(count >= 2)
			//	break;
		}
		
		
		
		/*
		if(check1.contains(off_ms_x, off_ms_y))
		{
			
			if(select >= 1)
				select = 2;
			else
				select = 1;
			
			//postinvalidate();
		}
		else
		{
			select = 0;
			//invalidate();
		}*/
		m_current_point.set(-1, -1);
		return null;
	}
	
	public void ShowSelectorState(UserInterface.ToolbarConstructionTypes constructiontype)
	{
		if(constructiontype != UserInterface.ToolbarConstructionTypes.NOTHING)
			m_cellselector.ShowSelectorState(m_cells, m_current_point);
		else
			m_cellselector.ShowSelectorState2(m_cells, m_current_point);
	}
	
	/**
	 * 2X2이상을 점유하는 건물의 최하단 셀의 정보를 CellGroup에 추가한다.
	 * value가 높은 순서대로 앞에 배치시킨다.
	 */
	public void AddCellGroup(int x, int y, int size)
	{
		int index;
		int distance = Math.abs((m_size-1) - (x+size-1)) + Math.abs((m_size-1) - (y+size-1));
		int value = distance + size;
		for(index=0; index<m_cellgroup_list.size(); index++)
		{
			if(value >= m_cellgroup_list.get(index).m_value)
				break;
		}
		m_cellgroup_list.add(index, new CellGroup(new Point(x+(size-1), y+(size-1)), value));
	}
	
	public void CalculateTotalConstructionPrice(UserInterface.ToolbarConstructionTypes constructiontype)
	{
		int total_landvalue = 0, total_purchase = 0;
		
		if(constructiontype != UserInterface.ToolbarConstructionTypes.NOTHING)	
		{
			if(m_cellselector.mCurrentBitmapID != -1)
			{
				if(m_current_point.x > -1)
				{
					for(int i=m_current_point.x; i<m_current_point.x+m_cellselector.m_size; i++)
					{
						for(int j=m_current_point.y; j<m_current_point.y+m_cellselector.m_size; j++)
						{
								
							total_landvalue += m_cells[i][j].m_landvalue.GetValue();
							
							if(m_cells[i][j].m_celltype == CellTypes.HOUSE1)
								total_purchase += m_cells[i][j].m_landvalue.GetValue() * 0.5;
							
						}
					}
					m_total_landvalue = total_landvalue;
					m_total_purchase = total_purchase;
				}
			}
		}
	}
	
	public void ConstructionSelect(MotionEvent event, UserInterface UI, Player player)
	{
		if(event.getSource() != 100)
			return;
		
		int x = Screen.touchX((int)event.getX());
		int y = Screen.touchY((int)event.getY());
		
		long sum_cost = 0;
		
		if(UI.constructiontype != UserInterface.ToolbarConstructionTypes.NOTHING)
		{
			//if(m_cellselector.current_bitmap != null && m_current_point.x>-1)
			{
	    		if(UserInterface.construction_yes_rect.contains(x, y)
	    				&& m_cellselector.selector_color != CellSelector.SelectorColor.RED
	    				&& m_cellselector.mCurrentBitmapID != -1
	    				&& m_current_point.x>-1) 
	    		{
	    			Utility.m_click_sound.start();
	    			
	    			Log.i("abc", "ConstructionSelect 진입");
	    			
	    			if(UI.constructiontype == UserInterface.ToolbarConstructionTypes.RETAIL)
	    			{
	    				int count = 1;
	    				
	    				sum_cost = Retail.cost + m_total_landvalue + m_total_purchase;
	    				
	    				if(Player.m_money >= sum_cost)
							Player.m_money -= sum_cost;
						else
						{
							NotEnoughMoney();
							return;
						}
	    				
	    				player.m_retaillist.add(new Retail());
	    				
	    				Log.i("abc", "ConstructionSelect 1");
	    				
	    				player.m_retaillist.get(player.m_retaillist.size()-1).m_point.x = m_current_point.x;
	    				player.m_retaillist.get(player.m_retaillist.size()-1).m_point.y = m_current_point.y;
	    				
	    				Log.i("abc", "ConstructionSelect 2");
	    				
	    				for(int i=m_current_point.x; i<m_current_point.x+m_cellselector.m_size; i++)
	    				{
	    					for(int j=m_current_point.y; j<m_current_point.y+m_cellselector.m_size; j++)
	    					{
	    						m_cells[i][j].m_maxlink = m_cellselector.m_size*m_cellselector.m_size;
	    						m_cells[i][j].m_first_link_point.x = m_current_point.x;
	    						m_cells[i][j].m_first_link_point.y = m_current_point.y;
	    						m_cells[i][j].m_currentlink = count++;
	    						m_cells[i][j].m_constructiontype = ConstructionTypes.RETAIL;
	    						m_cells[i][j].m_construction = player.m_retaillist.get(player.m_retaillist.size()-1);
	    						
	    						Log.i("abc", "ConstructionSelect 3");
	    						player.m_retaillist.get(player.m_retaillist.size()-1).m_average_landvalue += m_cells[i][j].m_landvalue.GetValue();
	    						if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
	    						{
	    							m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH;
	    							m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
	    							m_cells[i][j].m_bitmap = m_cell_retail1_bitmap;
	    						}
	    					}
	    				}
	    				
	    				AddCellGroup(m_current_point.x, m_current_point.y, m_cellselector.m_size);
	    				
	    				Log.i("abc", "ConstructionSelect 4");
	    				
	    				if(((GameActivity)AppManager.getInstance().getActivity()).isReadyClient == true)
	    				{
	    					((GameActivity)AppManager.getInstance().getActivity()).sendFromClient(0+";"+m_current_point.x+";"+m_current_point.y+";");
	    				}
	    				
	    				player.m_retaillist.get(player.m_retaillist.size()-1).m_average_landvalue /= (m_cellselector.m_size * m_cellselector.m_size);
	    			}
	    			if(UI.constructiontype == UserInterface.ToolbarConstructionTypes.FACTORY)
	    			{
	    				int count = 1;
	    				
	    				sum_cost = Factory.cost + m_total_landvalue + m_total_purchase;
	    				
	    				if(Player.m_money >= sum_cost)
							Player.m_money -= sum_cost;
						else
						{
							NotEnoughMoney();
							return;
						}
	    				
	    				player.m_factorylist.add(new Factory());
	    				
	    				Log.i("abc", "ConstructionSelect 11");
	    				
	    				player.m_factorylist.get(player.m_factorylist.size()-1).m_point.x = m_current_point.x;
	    				player.m_factorylist.get(player.m_factorylist.size()-1).m_point.y = m_current_point.y;
	    				
	    				Log.i("abc", "ConstructionSelect 22");
	    				
	    				for(int i=m_current_point.x; i<m_current_point.x+m_cellselector.m_size; i++)
	    				{
	    					for(int j=m_current_point.y; j<m_current_point.y+m_cellselector.m_size; j++)
	    					{
	    						m_cells[i][j].m_maxlink = m_cellselector.m_size*m_cellselector.m_size;
	    						m_cells[i][j].m_first_link_point.x = m_current_point.x;
	    						m_cells[i][j].m_first_link_point.y = m_current_point.y;
	    						m_cells[i][j].m_currentlink = count++;
	    						m_cells[i][j].m_constructiontype = ConstructionTypes.FACTORY;
	    						m_cells[i][j].m_construction = player.m_factorylist.get(player.m_factorylist.size()-1);
	    						
	    						Log.i("abc", "ConstructionSelect 33");
	    						player.m_factorylist.get(player.m_factorylist.size()-1).m_average_landvalue += m_cells[i][j].m_landvalue.GetValue();
	    						if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
	    						{
	    							m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH;
	    							m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
	    							m_cells[i][j].m_bitmap = m_cell_factory1_bitmap;
	    						}
	    					}
	    				}
	    				
	    				AddCellGroup(m_current_point.x, m_current_point.y, m_cellselector.m_size);
	    				
	    				Log.i("abc", "ConstructionSelect 44");
	    				
	    				if(((GameActivity)AppManager.getInstance().getActivity()).isReadyClient == true)
	    				{
	    					((GameActivity)AppManager.getInstance().getActivity()).sendFromClient(1+";"+m_current_point.x+";"+m_current_point.y+";");
	    				}
	    				
	    				player.m_factorylist.get(player.m_factorylist.size()-1).m_average_landvalue /= (m_cellselector.m_size * m_cellselector.m_size);
	    			}
	    			if(UI.constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
	    			{
	    				int count = 1;
	    				
	    				sum_cost = Farm.cost + m_total_landvalue + m_total_purchase;
	    				
	    				if(Player.m_money >= sum_cost)
							Player.m_money -= sum_cost;
						else
						{
							NotEnoughMoney();
							return;
						}
	    				
	    				player.m_farmlist.add(new Farm());
	    				
	    				player.m_farmlist.get(player.m_farmlist.size()-1).m_point.x = m_current_point.x;
	    				player.m_farmlist.get(player.m_farmlist.size()-1).m_point.y = m_current_point.y;
	    				
	    				for(int i=m_current_point.x; i<m_current_point.x+m_cellselector.m_size; i++)
	    				{
	    					for(int j=m_current_point.y; j<m_current_point.y+m_cellselector.m_size; j++)
	    					{
	    						m_cells[i][j].m_maxlink = m_cellselector.m_size*m_cellselector.m_size;
	    						m_cells[i][j].m_first_link_point.x = m_current_point.x;
	    						m_cells[i][j].m_first_link_point.y = m_current_point.y;
	    						m_cells[i][j].m_currentlink = count++;
	    						m_cells[i][j].m_constructiontype = ConstructionTypes.FARM;
	    						m_cells[i][j].m_construction = player.m_farmlist.get(player.m_farmlist.size()-1);
	    						player.m_farmlist.get(player.m_farmlist.size()-1).m_average_landvalue += m_cells[i][j].m_landvalue.GetValue();
	    						if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
	    						{
	    							m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH/2*3;
	    							m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
	    							m_cells[i][j].m_bitmap = m_cell_farm1_bitmap;
	    						}
	    					}
	    				}
	    				
	    				AddCellGroup(m_current_point.x, m_current_point.y, m_cellselector.m_size);
	    				
	    				if(((GameActivity)AppManager.getInstance().getActivity()).isReadyClient == true)
	    				{
	    					((GameActivity)AppManager.getInstance().getActivity()).sendFromClient(2+";"+m_current_point.x+";"+m_current_point.y+";");
	    				}
	    				
	    				player.m_farmlist.get(player.m_farmlist.size()-1).m_average_landvalue /= (m_cellselector.m_size * m_cellselector.m_size);
	    			}
	    			if(UI.constructiontype == UserInterface.ToolbarConstructionTypes.RnD)
	    			{
	    				int count = 1;
	    				
	    				sum_cost = RnD.cost + m_total_landvalue + m_total_purchase;
	    				
	    				if(Player.m_money >= sum_cost)
							Player.m_money -= sum_cost;
						else
						{
							NotEnoughMoney();
							return;
						}
	    				
	    				player.m_RnDlist.add(new RnD());
	    				
	    				player.m_RnDlist.get(player.m_RnDlist.size()-1).m_point.x = m_current_point.x;
	    				player.m_RnDlist.get(player.m_RnDlist.size()-1).m_point.y = m_current_point.y;
	    				
	    				for(int i=m_current_point.x; i<m_current_point.x+m_cellselector.m_size; i++)
	    				{
	    					for(int j=m_current_point.y; j<m_current_point.y+m_cellselector.m_size; j++)
	    					{
	    						m_cells[i][j].m_maxlink = m_cellselector.m_size*m_cellselector.m_size;
	    						m_cells[i][j].m_first_link_point.x = m_current_point.x;
	    						m_cells[i][j].m_first_link_point.y = m_current_point.y;
	    						m_cells[i][j].m_currentlink = count++;
	    						m_cells[i][j].m_constructiontype = ConstructionTypes.RnD;
	    						m_cells[i][j].m_construction = player.m_RnDlist.get(player.m_RnDlist.size()-1);
	    						player.m_RnDlist.get(player.m_RnDlist.size()-1).m_average_landvalue += m_cells[i][j].m_landvalue.GetValue();
	    						if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
	    						{
	    							m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH;
	    							m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
	    							m_cells[i][j].m_bitmap = m_cell_RnD1_bitmap;
	    						}
	    					}
	    				}
	    				
	    				AddCellGroup(m_current_point.x, m_current_point.y, m_cellselector.m_size);
	    				
	    				if(((GameActivity)AppManager.getInstance().getActivity()).isReadyClient == true)
	    				{
	    					((GameActivity)AppManager.getInstance().getActivity()).sendFromClient(3+";"+m_current_point.x+";"+m_current_point.y+";");
	    				}
	    				
	    				player.m_RnDlist.get(player.m_RnDlist.size()-1).m_average_landvalue /= (m_cellselector.m_size * m_cellselector.m_size);
	    			}
	    			
	    			if(m_cellselector.m_size == 2)
		    			NewsManager.GetInstance().Insert(new NewsItem(new GregorianCalendar(
		    					Time.GetInstance().GetCalendar().get(Calendar.YEAR), 
		    					Time.GetInstance().GetCalendar().get(Calendar.MONTH),
		    					Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH)),
		    					"Player1이 건물을 건설(서울)", new Point(m_cells[m_current_point.x][m_current_point.y].m_cellpoint.x, 
		    							m_cells[m_current_point.x][m_current_point.y].m_cellpoint.y-35)));
	    			else
	    				NewsManager.GetInstance().Insert(new NewsItem(new GregorianCalendar(
		    					Time.GetInstance().GetCalendar().get(Calendar.YEAR), 
		    					Time.GetInstance().GetCalendar().get(Calendar.MONTH),
		    					Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH)),
		    					"Player1이 건물을 건설(서울)", new Point(m_cells[m_current_point.x][m_current_point.y].m_cellpoint.x, 
		    							m_cells[m_current_point.x][m_current_point.y].m_cellpoint.y-19)));
	    			
	    			
	    			UI.constructiontype = UserInterface.ToolbarConstructionTypes.NOTHING;
	    			UI.toolbartype = UserInterface.ToolbarTypes.TOOLBAR;
	    			m_cellselector.mCurrentBitmapID = -1;
	    			
	    		}
	    		if(UserInterface.construction_no_rect.contains(x, y))
	    		{
	    			Utility.m_click_sound.start();
	    			
	    			UI.constructiontype = UserInterface.ToolbarConstructionTypes.NOTHING;
	    			UI.toolbartype = UserInterface.ToolbarTypes.TOOLBAR;
	    			m_cellselector.mCurrentBitmapID = -1;
	    		}
		    }
		}
	}
	
	public void NotEnoughMoney()
	{
		Utility.MessageDialog("건물 건설을 위한 현금이 부족합니다");
	}
	
	public void Building(String strReceiveText)
	{
		Log.e(Utility.TAG, "1");
		
		Log.e(Utility.TAG, strReceiveText);
		
		String a = strReceiveText.substring(3, 4);
		
		Log.e(Utility.TAG, a);
		
		int type = Integer.parseInt(a);
		
		//Log.e(Utility.TAG, strReceiveText.substring(3, 3));
		
		String b = strReceiveText.substring(5, 6);
    	
    	int x = Integer.parseInt(b);
    	
    	//Log.e(Utility.TAG, strReceiveText.substring(5, 5));
    	
    	String c = strReceiveText.substring(7, 8);
    	
    	int y = Integer.parseInt(c);
    	
    	//Log.e(Utility.TAG, strReceiveText.substring(7, 7));
    	
    	Log.e(Utility.TAG, "11");
    	
    	int size = 2;
    	
    	if(type == 2)
    		size = 3;
    	
    	int count = 1;
    	
    	if(type == 0)
    	{
	    	for(int i=x; i<x+size; i++)
			{
				for(int j=y; j<y+size; j++)
				{
					Log.e(Utility.TAG, "2");
					m_cells[i][j].m_maxlink = size*size;
					m_cells[i][j].m_first_link_point.x = x;
					m_cells[i][j].m_first_link_point.y = y;
					m_cells[i][j].m_currentlink = count++;
					m_cells[i][j].m_constructiontype = ConstructionTypes.RETAIL;
					//m_cells[i][j].m_construction = player.m_retaillist.get(player.m_retaillist.size()-1);
					
					Log.i("abc", "ConstructionSelect 3");
					//player.m_retaillist.get(player.m_retaillist.size()-1).m_average_landvalue += m_cells[i][j].m_landvalue.GetValue();
					if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
					{
						m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH;
						m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
						m_cells[i][j].m_bitmap = m_cell_retail1_bitmap;
					}
				}
			}
		
	    	AddCellGroup(x, y, size);
    	}
    	
    	if(type == 1)
    	{
	    	for(int i=x; i<x+size; i++)
			{
				for(int j=y; j<y+size; j++)
				{
					m_cells[i][j].m_maxlink = size*size;
					m_cells[i][j].m_first_link_point.x = x;
					m_cells[i][j].m_first_link_point.y = y;
					m_cells[i][j].m_currentlink = count++;
					m_cells[i][j].m_constructiontype = ConstructionTypes.FACTORY;
					//m_cells[i][j].m_construction = player.m_retaillist.get(player.m_retaillist.size()-1);
					
					Log.i("abc", "ConstructionSelect 3");
					//player.m_retaillist.get(player.m_retaillist.size()-1).m_average_landvalue += m_cells[i][j].m_landvalue.GetValue();
					if(m_cells[i][j].m_currentlink == m_cells[i][j].m_maxlink)
					{
						m_cells[i][j].m_constructionpoint.x = m_cells[i][j].m_cellpoint.x - Cell.CELL_WIDTH;
						m_cells[i][j].m_constructionpoint.y = m_cells[i][j].m_cellpoint.y - (Cell.CELL_HEIGHT-1)*3 - (Cell.CELL_HEIGHT-1)/2;
						m_cells[i][j].m_bitmap = m_cell_factory1_bitmap;
					}
				}
			}
		
	    	AddCellGroup(x, y, size);
    	}
    	
    	
	}
}
