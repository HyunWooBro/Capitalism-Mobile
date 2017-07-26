package org.game.cell;

import org.framework.*;

import android.graphics.*;

public class Vehicle{
	
	public enum VehicleTypes {TRUCK_A, CAR_A};
	public enum VehicleDirections {SOUTH_WEST, NORTH_EAST};
	
	public static final int MAX_STAGE = 10;
	
	public int m_life;
	public Point m_target_cell = new Point();
	public Point m_source_cell = new Point();
	public Point m_vector = new Point();
	public int m_stage;
	public VehicleTypes m_vehicle_type;
	public VehicleDirections m_vehicle_direction;
	public Bitmap m_bitmap;
	
	public Bitmap m_dark_version_bitmap;

	public Vehicle(/*int life, VehicleTypes vehicle_type*/) {
		// TODO Auto-generated constructor stub
	}

}
