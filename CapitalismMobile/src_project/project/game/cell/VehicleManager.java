package project.game.cell;

import java.util.List;
import java.util.ListIterator;

import org.framework.R;

import project.game.GameOptions;
import project.game.Time;
import project.game.cell.Vehicle.VehicleType;

import android.graphics.Canvas;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.MathUtils;
import core.scene.stage.Floor;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Actor.Visible;
import core.utils.SnapshotArrayList;
import core.utils.pool.Pools;

public class VehicleManager {
	
	/*package*/ VehicleManager() {
	}

	private SnapshotArrayList<Vehicle> mVehicleList = new SnapshotArrayList<Vehicle>();

	public int mMaxVehicles;
	public int mNumVehicles;
	
	private TextureRegion mSouthWestVehicle1Region;
	private TextureRegion mSouthWestVehicle1DarkRegion;
	
	private TextureRegion mNorthEastVehicle3Region;
	private TextureRegion mNorthEastVehicle3DarkRegion;

	private int makedelay;
	
	private Cell[][] mCells;

	public void init(Cell[][] cells) {
		
		mCells = cells;
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		
		mSouthWestVehicle1Region = imageTexture.getTextureRegion("cell_southwest_vehicle1");
		mSouthWestVehicle1DarkRegion = imageTexture.getTextureRegion("cell_southwest_vehicle1_d");
		
		mNorthEastVehicle3Region = imageTexture.getTextureRegion("cell_northeast_vehicle3");
		mNorthEastVehicle3DarkRegion = imageTexture.getTextureRegion("cell_northeast_vehicle3_d");

		mMaxVehicles = 10;
		mNumVehicles = 0;
	}

	public void update(long time) {
		if(GameOptions.getInstance().isVehicleHidden()) return;
		if(Time.getInstance().isPaused()) return;
		
		createVehicle();
			
		ListIterator<Vehicle> it = mVehicleList.begin();
		while(it.hasNext()) {
			Vehicle vehicle = it.next();
			// vehicle.mLife--;
			vehicle.mStage++;
			if(vehicle.mStage > Vehicle.MAX_STAGE) {
				vehicle.mStage = 0;
				if(vehicle.mVehicleDirection == Vehicle.VehicleDirection.SOUTH_WEST) {
					vehicle.mSourceCell.x++;
					vehicle.mTargetCell.x++;
					if(CellManager.getInstance().getMapSize() == vehicle.mTargetCell.x) {
						removeVehicle(vehicle);
					}
				} else {
					vehicle.mSourceCell.x--;
					vehicle.mTargetCell.x--;
					if(vehicle.mSourceCell.x == 0) {
						removeVehicle(vehicle);
					}
				}
			}
		}
		mVehicleList.end(it);
	}

	private void createVehicle() {
		if(makedelay > 0) {
			makedelay--;
			return;
		}

		if(mMaxVehicles > mNumVehicles) {
			if(MathUtils.randomBoolean(0.0125f)) {
				makedelay = 40;
				mNumVehicles++;
				Vehicle vehicle = Pools.obtain(Vehicle.class);
				Core.APP.debug("vehicle created");
				vehicle.mCells = mCells;
				vehicle.mStage = 0;
				
				if(MathUtils.randomBoolean()) {
					vehicle.mVehicleDirection = Vehicle.VehicleDirection.SOUTH_WEST;
					vehicle.mSourceCell.x = 0;
					vehicle.mSourceCell.y = 17;
					vehicle.mTargetCell.x = 1;
					vehicle.mTargetCell.y = 17;
					vehicle.mVector.x =
							(mCells[1][0].m_cellpoint.x - mCells[0][0].m_cellpoint.x)/Vehicle.MAX_STAGE;
					vehicle.mVector.y =
							(mCells[1][0].m_cellpoint.y - mCells[0][0].m_cellpoint.y)/Vehicle.MAX_STAGE;
					vehicle.mVehicleType = VehicleType.TRUCK_A;
					vehicle.mRegion = mSouthWestVehicle1Region;
					vehicle.mDarkRegion = mSouthWestVehicle1DarkRegion;
				} else {
					vehicle.mVehicleDirection = Vehicle.VehicleDirection.NORTH_EAST;
					vehicle.mSourceCell.x = 24;
					vehicle.mSourceCell.y = 17;
					vehicle.mTargetCell.x = 23;
					vehicle.mTargetCell.y = 17;
					vehicle.mVector.x =
							(mCells[0][0].m_cellpoint.x - mCells[1][0].m_cellpoint.x)/Vehicle.MAX_STAGE;
					vehicle.mVector.y =
							(mCells[0][0].m_cellpoint.y - mCells[1][0].m_cellpoint.y)/Vehicle.MAX_STAGE;
					vehicle.mVehicleType = VehicleType.CAR_A;
					vehicle.mRegion = mNorthEastVehicle3Region;
					vehicle.mDarkRegion = mNorthEastVehicle3DarkRegion;
				}
				mVehicleList.add(vehicle);
			}
		}
	}

	private void removeVehicle(Vehicle vehicle) {
		mVehicleList.remove(vehicle);
		Pools.recycle(vehicle);
		mNumVehicles--;
	}
	
	public void draw(Batch batch) {
		if(GameOptions.getInstance().isVehicleHidden()) return;
		
		List<Vehicle> vehicleList = mVehicleList;
		int n = vehicleList.size();
		for(int i=0; i<n; i++) {
			Vehicle vehicle = vehicleList.get(i);
			vehicle.draw(batch);
		}
	}

}
