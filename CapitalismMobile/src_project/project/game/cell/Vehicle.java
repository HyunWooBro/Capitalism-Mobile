package project.game.cell;

import android.graphics.Point;

import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.TextureRegion;
import core.math.Vector2;
import core.utils.pool.Poolable;

public class Vehicle implements Poolable {

	public enum VehicleType {
		TRUCK_A, 
		CAR_A, 
	}

	public enum VehicleDirection {
		SOUTH_WEST, 
		NORTH_EAST, 
	}

	public static final int MAX_STAGE = 20;

	/*package*/ int mLife;
	/*package*/ Point mTargetCell = new Point();
	/*package*/ Point mSourceCell = new Point();
	/*package*/ Vector2 mVector = new Vector2();
	/*package*/ int mStage;
	/*package*/ VehicleType mVehicleType;
	/*package*/ VehicleDirection mVehicleDirection;
	
	/*package*/ TextureRegion mRegion;
	/*package*/ TextureRegion mDarkRegion;
	
	/*package*/ Cell[][] mCells;

	public Vehicle(/* int life, VehicleType vehicle_type */) {
	}
	
	public void draw(Batch batch) {
		boolean isBackgroundDark = CellManager.getInstance().isBackgroundCellDark();
		
		int x = mSourceCell.x;
		int y = mSourceCell.y;
		
		if(isBackgroundDark) {
			batch.draw(mDarkRegion, 
					mCells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 
						+ mVector.x*mStage,
						mCells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 
						+ mVector.y*mStage);
		} else {
			batch.draw(mRegion, 
					mCells[x][y].m_cellpoint.x - Cell.CELL_WIDTH/2 
						+ mVector.x*mStage,
					mCells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT/2 
						+ mVector.y*mStage);
		}
	}

	@Override
	public void recycle() {
	}

}
