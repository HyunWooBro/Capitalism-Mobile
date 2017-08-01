package project.game;

import project.framework.Utils;

import core.framework.Core;
import core.framework.app.Preference;

public class GameOptions {

	private boolean mVehicleHidden;

	private float mScrollSensitivity;
	
	/** 싱글턴 인스턴스 */
	private volatile static GameOptions sInstance;

	private GameOptions() {
	}

	public static GameOptions getInstance() {
		if(sInstance == null) {
			synchronized(GameOptions.class) {
				if(sInstance == null) {
					sInstance = new GameOptions();
				}
			}
		}
		return sInstance;
	}
	
	public void init() {
		final Preference pref = Core.APP.getPreference();
		mVehicleHidden = pref.getBoolean(Utils.PREF_VEHICLE_SWITCH, false);
		mScrollSensitivity = pref.getFloat(Utils.PREF_SCROLL_SENSITIVE, 50f);
	}

	public boolean isVehicleHidden() {
		return mVehicleHidden;
	}

	public void setVehicleHidden(boolean vehicleHidden) {
		mVehicleHidden = vehicleHidden;
	}

	public float getScrollSensitivity() {
		return mScrollSensitivity;
	}

	public void setScrollSensitivity(float scrollSensitivity) {
		mScrollSensitivity = scrollSensitivity;
	}

}
