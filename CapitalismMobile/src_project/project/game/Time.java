package project.game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import project.game.building.BuildingManager;
import project.game.city.CityManager;
import project.game.corporation.CorporationManager;
import project.game.event.EventManager;
import project.game.report.ReportManager;
import project.game.ui.UIManager;

import core.utils.Disposable;

public class Time implements Disposable {
	
	/** 1년의 달 수 */
	public static final int NUM_MONTHS = 12;
	/** 1년의 일 수 */
	public static final int NUM_DAYS_OF_YEAR = 365;
	/** 1달의 임의의 일 수 */
	public static final int NUM_DAYS = 30;
	
	private GregorianCalendar mCalendar; 	// 현재 날짜
	private GregorianCalendar mDeadline; 	// 최종 기한
	
	private List<DayListener> mDayListenerList = new ArrayList<DayListener>();
	private List<MonthListener> mMonthListenerList = new ArrayList<MonthListener>();
	private List<YearListener> mYearListenerList = new ArrayList<YearListener>();
	
	private int mInitialYear;
	private int mCurrentYear;
	private int mCurrentMonth;
	private int mCurrentDay;
	private int mMaxDayOfMonth;
	
	private int mRealSpeed;
	private int mVirtualSpeed;
	
	private int mMonthlyArrayIndex; 	// 배열의 인덱스(배열의 끝까지 도달하면 다시 처럼으로)
	
	private int mDailyArrayIndex; 			// 배열의 인덱스(배열의 끝까지 도달하면 다시 처럼으로)
	
	/** 싱글턴 인스턴스 */
	private volatile static Time sInstance;
	
	private Time() {
	}

	public static Time getInstance() {
		if(sInstance == null) {
			synchronized(Time.class) {
				if(sInstance == null)
					sInstance = new Time();
			}
		}
		return sInstance;
	}

	public void init() {
		// 업데이트는 종속성을 고려해서 앞에 추가하고 출력은 뒤에 추가한다.
		
		mDayListenerList.clear();
		mDayListenerList.add(CityManager.getInstance());
		mDayListenerList.add(ReportManager.getInstance());
		mDayListenerList.add(BuildingManager.getInstance());
		mDayListenerList.add(CorporationManager.getInstance());
		mDayListenerList.add(CPU.getInstance());
		mDayListenerList.add(EventManager.getInstance());
		mDayListenerList.add(UIManager.getInstance());
		
		mMonthListenerList.clear();
		mMonthListenerList.add(CityManager.getInstance());
		mMonthListenerList.add(ReportManager.getInstance());
		mMonthListenerList.add(BuildingManager.getInstance());
		mMonthListenerList.add(CorporationManager.getInstance());
		mMonthListenerList.add(UIManager.getInstance());
		
		setSpeed(2);
	}

	public void update(long time) {

		GregorianCalendar calendar = mCalendar;

		// 시간 흐름
		calendar.add(Calendar.MINUTE, mRealSpeed);

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		// 날짜(일)이 바뀌지 않으면 리턴
		if(mCurrentDay == day) return;

		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		// 날짜(년)이 바뀌면
		if(mCurrentYear != year) {
			// 새로운 날짜(년)을 등록하고
			mCurrentYear = year;

			List<YearListener> yearListenerList = mYearListenerList;
			int n = yearListenerList.size();
			for(int i = 0; i < n; i++) {
				YearListener listener = yearListenerList.get(i);
				listener.onYearChanged(calendar, year, month, day);
			}
		}

		// 날짜(달)이 바뀌면
		if(mCurrentMonth != month) {
			// 새로운 날짜(달)을 등록하고
			mCurrentMonth = month;

			mMonthlyArrayIndex++;
			if(mMonthlyArrayIndex == NUM_MONTHS)
				mMonthlyArrayIndex = 0;
			
			mMaxDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

			List<MonthListener> monthListenerList = mMonthListenerList;
			int n = monthListenerList.size();
			for(int i = 0; i < n; i++) {
				MonthListener listener = monthListenerList.get(i);
				listener.onMonthChanged(calendar, year, month, day);
			}
		}

		// 새로운 날짜(일)을 등록하고
		mCurrentDay = day;
		
		mDailyArrayIndex++;
		if(mDailyArrayIndex == NUM_DAYS)
			mDailyArrayIndex = 0;

		List<DayListener> dayListenerList = mDayListenerList;
		int n = dayListenerList.size();
		for(int i = 0; i < n; i++) {
			DayListener listener = dayListenerList.get(i);
			listener.onDayChanged(calendar, year, month, day);
		}
	}

	public GregorianCalendar getCalendar() {
		return mCalendar;
	}

	public void setCalendar(GregorianCalendar calendar) {
		mCalendar = calendar;
		mInitialYear = calendar.get(Calendar.YEAR);
		mCurrentYear = mInitialYear;
		mCurrentMonth = calendar.get(Calendar.MONTH);
		mCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
		mMaxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public GregorianCalendar getDeadline() {
		return mDeadline;
	}

	public void setDeadline(GregorianCalendar deadline) {
		this.mDeadline = deadline;
	}

	public boolean hasDeadline() {
		return mDeadline != null;
	}

	public int getMonthlyArrayIndex() {
		return mMonthlyArrayIndex;
	}

	public int getDailyArrayIndex() {
		return mDailyArrayIndex;
	}

	public int getCurrentYear() {
		return mCurrentYear;
	}

	public int getCurrentMonth() {
		return mCurrentMonth;
	}

	public int getCurrentDay() {
		return mCurrentDay;
	}
	
	public int getMaxDayOfMonth() {
		return mMaxDayOfMonth;
	}

	public int getElapsedYear() {
		return mCurrentYear - mInitialYear;
	}
	
	public void setSpeed(int speed) {
		mVirtualSpeed = speed;
		mRealSpeed = speed * speed * 5;
	}
	
	public int getSpeed() {
		return mVirtualSpeed;
	}

	public boolean isPaused() {
		return mVirtualSpeed == 0;
	}

	@Override
	public void dispose() {
		sInstance = null;
	}

	public interface DayListener {
		public void onDayChanged(GregorianCalendar calendar, int year, int month, int day);
	}

	public interface MonthListener {
		public void onMonthChanged(GregorianCalendar calendar, int year, int month, int day);
	}

	public interface YearListener {
		public void onYearChanged(GregorianCalendar calendar, int year, int month, int day);
	}

}
