package org.game.cell;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.cell.*;
import org.game.cell.Vehicle.VehicleTypes;
import org.screen.*;

import android.graphics.*;
import android.util.*;

public class VehicleManager {
	
	// 싱글턴
	private static VehicleManager s_instance;
	private VehicleManager() {}
	public static VehicleManager GetInstance(){
		if(s_instance == null){
			s_instance = new VehicleManager();
			Log.i("abc", "VehicleManager null");
		}
		return s_instance;
	}
	
	public static void Destroy()
	{
		s_instance = null;
	}
	
	public static void InitStatic()
	{
		makedelay = 0;
	}
		
	ArrayList<Vehicle> m_array = new ArrayList<Vehicle>();
	
	public int m_FPS;	
	public long m_frame_timer;
	
	public int m_max_car;
	public int m_num_car;

	public Bitmap m_cell_southwest_vehicle1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_vehicle1, Utility.sOptions);
	public Bitmap m_cell_northeast_vehicle3_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_northeast_vehicle3, Utility.sOptions);
	
	public Bitmap m_cell_southwest_vehicle1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_vehicle1_d, Utility.sOptions);
	public Bitmap m_cell_northeast_vehicle3_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_northeast_vehicle3_d, Utility.sOptions);
	
	static int makedelay;

	public void Init() {
		// TODO Auto-generated constructor stub
		
		/*
		m_array.add(new Vehicle());
		m_array.get(0).m_stage = 0;
		m_array.get(0).vehicle_direction = Vehicle.VehicleDirections.SOUTH_WEST;
		m_array.get(0).m_source_cell.x = 0;
		m_array.get(0).m_source_cell.y = 7;
		m_array.get(0).m_target_cell.x = 1;
		m_array.get(0).m_target_cell.y = 7;
		*/
		
		m_FPS = 1000 /8;
		m_frame_timer = 0;
		
		m_max_car = 10;
		m_num_car = 0;
		
		//m_cell_southwest_vehicle1_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_vehicle1);
		//m_cell_northeast_vehicle3_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_northeast_vehicle3);
		
		//m_cell_southwest_vehicle1_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_southwest_vehicle1_d);
		//m_cell_northeast_vehicle3_d_bitmap = AppManager.getInstance().getBitmap(R.drawable.cell_northeast_vehicle3_d);
	}
	
	public void Update(long GameTime, CellManager cell_manager)
	{
		if(GameState.m_virtual_gamespeed == 4)
			m_FPS = 1000 /32;
		if(GameState.m_virtual_gamespeed == 3)
			m_FPS = 1000 /18;
		if(GameState.m_virtual_gamespeed == 2)
			m_FPS = 1000 /8;
		if(GameState.m_virtual_gamespeed == 1)
			m_FPS = 1000 /2;
		
		if(GameTime > m_frame_timer + m_FPS && GameState.m_virtual_gamespeed > 0) 
		{
			m_frame_timer = GameTime;
			  
			for(int i=0; i<m_array.size(); i++)
			{
				//m_array.get(i).m_life--;
				m_array.get(i).m_stage++;
				if(m_array.get(i).m_stage > Vehicle.MAX_STAGE)
				{
					m_array.get(i).m_stage = 0;
					if(m_array.get(i).m_vehicle_direction == Vehicle.VehicleDirections.SOUTH_WEST)
					{
						m_array.get(i).m_source_cell.x++;
						m_array.get(i).m_target_cell.x++;
						if(cell_manager.m_size == m_array.get(i).m_target_cell.x)
						{
							DeleteVehicle(m_array.get(i));
							//m_array.get(i).m_source_cell.x = 0;
							//m_array.get(i).m_target_cell.x = 1;
						}
					}
					else
					{
						m_array.get(i).m_source_cell.x--;
						m_array.get(i).m_target_cell.x--;
						if(0 == m_array.get(i).m_source_cell.x)
						{
							DeleteVehicle(m_array.get(i));
							//m_array.get(i).m_source_cell.x = 0;
							//m_array.get(i).m_target_cell.x = 1;
						}
					}
				}
			}
		}
	}
	
	public void MakeVehicle(CellManager cell_manager)
	{
		if(makedelay > 0)
		{
			makedelay--;
			return;
		}
		
		if(m_max_car > m_num_car)
		{
			Random rand = new Random();
			if(rand.nextInt(80) == 0)
			{
				makedelay = 40;
				m_num_car++;
				m_array.add(new Vehicle());
				m_array.get(m_array.size()-1).m_stage = 0;
				if(rand.nextInt(2) == 0)
				{
					m_array.get(m_array.size()-1).m_vehicle_direction = Vehicle.VehicleDirections.SOUTH_WEST;
					m_array.get(m_array.size()-1).m_source_cell.x = 0;
					m_array.get(m_array.size()-1).m_source_cell.y = 7;
					m_array.get(m_array.size()-1).m_target_cell.x = 1;
					m_array.get(m_array.size()-1).m_target_cell.y = 7;
					m_array.get(m_array.size()-1).m_vector.x = (cell_manager.m_cells[1][0].m_cellpoint.x - cell_manager.m_cells[0][0].m_cellpoint.x)/Vehicle.MAX_STAGE;
					m_array.get(m_array.size()-1).m_vector.y =  (cell_manager.m_cells[1][0].m_cellpoint.y - cell_manager.m_cells[0][0].m_cellpoint.y)/Vehicle.MAX_STAGE;
					m_array.get(m_array.size()-1).m_vehicle_type = VehicleTypes.TRUCK_A;
					m_array.get(m_array.size()-1).m_bitmap = m_cell_southwest_vehicle1_bitmap;
					m_array.get(m_array.size()-1).m_dark_version_bitmap = m_cell_southwest_vehicle1_d_bitmap;
				}
				else
				{
					m_array.get(m_array.size()-1).m_vehicle_direction = Vehicle.VehicleDirections.NORTH_EAST;
					m_array.get(m_array.size()-1).m_source_cell.x = 14;
					m_array.get(m_array.size()-1).m_source_cell.y = 7;
					m_array.get(m_array.size()-1).m_target_cell.x = 13;
					m_array.get(m_array.size()-1).m_target_cell.y = 7;
					m_array.get(m_array.size()-1).m_vector.x = (cell_manager.m_cells[0][0].m_cellpoint.x - cell_manager.m_cells[1][0].m_cellpoint.x)/Vehicle.MAX_STAGE;
					m_array.get(m_array.size()-1).m_vector.y =  (cell_manager.m_cells[0][0].m_cellpoint.y - cell_manager.m_cells[1][0].m_cellpoint.y)/Vehicle.MAX_STAGE;
					m_array.get(m_array.size()-1).m_vehicle_type = VehicleTypes.CAR_A;
					m_array.get(m_array.size()-1).m_bitmap = m_cell_northeast_vehicle3_bitmap;
					m_array.get(m_array.size()-1).m_dark_version_bitmap = m_cell_northeast_vehicle3_d_bitmap;
				}
			}
		}
	}
	
	private void DeleteVehicle(Vehicle vehicle)
	{
		m_array.remove(vehicle);
		m_num_car--;
	}
	
	public void Render(Canvas canvas, int off_x, int off_y, CellManager cell_manager)
	{
		int x, y;
		
		Screen.beginZoomRender(canvas);
		
		for(int i=0; i<m_array.size(); i++)
		{
			x = m_array.get(i).m_source_cell.x;
			y = m_array.get(i).m_source_cell.y;
			
			if(CellManager.m_citiyicon_selection == 2 || CellManager.m_citiyicon_selection == 3)
				canvas.drawBitmap(m_array.get(i).m_dark_version_bitmap, 
						cell_manager.m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 + m_array.get(i).m_vector.x*m_array.get(i).m_stage  - off_x, 
						cell_manager.m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 + m_array.get(i).m_vector.y*m_array.get(i).m_stage - off_y, 
						null);
			else
				canvas.drawBitmap(m_array.get(i).m_bitmap, 
						cell_manager.m_cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 + m_array.get(i).m_vector.x*m_array.get(i).m_stage  - off_x, 
						cell_manager.m_cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 + m_array.get(i).m_vector.y*m_array.get(i).m_stage - off_y, 
						null);
		}
		
		Screen.endZoomRender(canvas);
	}

}
