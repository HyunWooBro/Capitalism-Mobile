package project.game.city;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import project.game.Time.DayListener;
import project.game.Time.MonthListener;
import project.game.cell.CellManager;
import project.game.news.NewsManager;

import core.utils.Disposable;

public class CityManager implements DayListener, MonthListener, Disposable {

	private List<CityListener> mCityListenerList = new ArrayList<CityListener>();

	private List<City> mCityList = new ArrayList<City>();

	private int mCurrentCityIndex;
	
	private int mMaxIndex;

	/** 싱글턴 인스턴스 */
	private volatile static CityManager sInstance;

	private CityManager() {
	}

	public static CityManager getInstance() {
		if(sInstance == null) {
			synchronized(CityManager.class) {
				if(sInstance == null) {
					sInstance = new CityManager();
				}
			}
		}
		return sInstance;
	}

	public void init() {
		mCityListenerList.add(CellManager.getInstance());
		mCityListenerList.add(NewsManager.getInstance());
	}

	public int getCurrentCityIndex() {
		return mCurrentCityIndex;
	}

	public City getCurrentCity() {
		return mCityList.get(mCurrentCityIndex);
	}
	
	public City getCity(int index) {
		return mCityList.get(index);
	}
	
	public List<City> getCityList() {
		return mCityList;
	}

	/** 
	 * 현재 도시의 인덱스를 설정한다. 다른 도시가 선택되는 경우
	 * 모든 CityListener에게 이 사실을 통보한다. 
	 */
	public void setCurrentCityIndex(int currentCityIndex) {
		if(mCurrentCityIndex == currentCityIndex) return;

		mCurrentCityIndex = currentCityIndex;

		List<CityListener> listenerList = mCityListenerList;
		int n = listenerList.size();
		for(int i = 0; i < n; i++) {
			CityListener listener = listenerList.get(i);
			listener.onCityChanged(currentCityIndex);
		}
	}

	public void addCity(City city) {
		if(city == null) {
			throw new IllegalArgumentException("city can't be null.");
		}
		if(mCityList.contains(city)) {
			throw new IllegalArgumentException("city is already added.");
		}

		mCityList.add(city);
		city.index = mMaxIndex++;
	}
	
	@Override
	public void dispose() {
		sInstance = null;
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
		List<City> cityList = mCityList;
		int n = cityList.size();
		for(int i = 0; i < n; i++) {
			City city = cityList.get(i);
			city.onDayChanged(calendar, year, month, day);
		}
	}

	@Override
	public void onMonthChanged(GregorianCalendar calendar, int year, int month, int day) {
		List<City> cityList = mCityList;
		int n = cityList.size();
		for(int i = 0; i < n; i++) {
			City city = cityList.get(i);
			city.onMonthChanged(calendar, year, month, day);
		}
	}

}
