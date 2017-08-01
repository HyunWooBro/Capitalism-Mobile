package project.game.corporation;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import project.game.Time.DayListener;
import project.game.Time.MonthListener;

import core.framework.graphics.Color4;
import core.utils.Disposable;

public class CorporationManager implements Disposable, DayListener, MonthListener {

	/** Public - User - AI 순서로 List에 넣는다. */
	private List<Corporation> mCorporationList = new ArrayList<Corporation>();

	private PublicCorporation mPublicCorporation;

	private UserCorporation mPlayerCorporation;

	/** 싱글턴 인스턴스 */
	private volatile static CorporationManager sInstance;

	private CorporationManager() {
	}

	public static CorporationManager getInstance() {
		if(sInstance == null) {
			synchronized(CorporationManager.class) {
				if(sInstance == null)
					sInstance = new CorporationManager();
			}
		}
		return sInstance;
	}

	public void init() {

		// 공공 기업
		mPublicCorporation = new PublicCorporation();
		mPublicCorporation.mName = "항구";
		mPublicCorporation.mColor = Color4.GRAY4;
		mCorporationList.add(mPublicCorporation);

		mPlayerCorporation = new PlayerCorporation();
		mPlayerCorporation.mName = "Player1";
		mPlayerCorporation.getFinancialData().cash = 60000000;
		mPlayerCorporation.mColor = Color4.RED4;
		mCorporationList.add(mPlayerCorporation);
	}

	public List<Corporation> getCorporationList() {
		return mCorporationList;
	}

	public PublicCorporation getPublicCorporation() {
		return mPublicCorporation;
	}

	public UserCorporation getPlayerCorporation() {
		return mPlayerCorporation;
	}

	@Override
	public void dispose() {
		sInstance = null;
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
		List<Corporation> corporationList = mCorporationList;
		int n = corporationList.size();
		for(int i = 0; i < n; i++) {
			Corporation corporation = corporationList.get(i);
			corporation.updateBrand();
		}
	}

	@Override
	public void onMonthChanged(GregorianCalendar calendar, int year, int month, int day) {
		List<Corporation> corporationList = mCorporationList;
		int n = corporationList.size();
		for(int i = 0; i < n; i++) {
			Corporation corporation = corporationList.get(i);
			if(corporation.mFinancialData != null)
				corporation.mFinancialData.reset();
		}
	}

}
