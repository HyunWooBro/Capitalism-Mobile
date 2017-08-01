package project.game.city;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import project.game.Time.DayListener;
import project.game.Time.MonthListener;
import project.game.product.ProductManager;

import core.framework.Core;
import core.math.MathUtils;

public class City implements DayListener, MonthListener {
	
	// 경제 지표
	public static final int EI_PANIC = 0; // 공황
	public static final int EI_DEPRESSION = 20; // 침체
	public static final int EI_RECESSION = 40; // 후퇴
	public static final int EI_NORMAL = 50; // 표준
	public static final int EI_RECOVERY = 60; // 회복
	public static final int EI_BOOM = 80; // 호황
	
	private static final int MAX_RANDOM_POP = 3000;
	
	private static final int MAX_ECONOMIC_POP = 5000;
	
	/*package*/ //Map<String, ProductGroup> mCodeToProductUnitMap = new HashMap<String, ProductGroup>();
	
	public int index;
	public String mName;
	public int mPopulation;
	
	public int mGDP;
	
	
	
	
	private List<MarketOverseer> mMarketOverseerList = new ArrayList<MarketOverseer>();
	
	
	
	
	// 도시마다 특색이 있으면 좋겠다. 관세 면제 등 도시 정책에 따라 농장 유지비 반값 등
	// int policy;
	
	/*
	 * public enum ECONOMIC_INDICATOR { // 공황 침체 후퇴 표준 회복 호황 EI_PANIC(0),
	 * EI_DEPRESSION(20), EI_RECESSION(40), EI_NORMAL(50), EI_RECOVERY(60),
	 * EI_BOOM(80); ECONOMIC_INDICATOR(int TMP_ARRAY) { m_value = TMP_ARRAY; }
	 * public int GetValue() { return m_value; } private int m_value; // 0 ~ 100
	 * }
	 */
	
	public int mEconomicIndicator; 	// 경제 지표 0 ~ 100
	public int mSpendingLevel; 		// 소비 레벨 0 ~ 100
	public int mSalaryLevel; 			// 봉급 레벨 0 ~ 100
	// 등등
	
	// 각 제품에 대한 지역 상인들의 브랜드, 가격, 품질 등에 관한 평균 정보 추가
	
	// 항구, AI, 유저가 등록한 모든 품목을 등록한다.
	// List<Product> m_commodity_list = new ArrayList<Product>();
	
	// 항구
	// public static List<Port> m_portlist = new ArrayList<Port>();
	
	public float mInterestRate; // 이자율
	
	public City(String name, int population, int economic_indicator, int spending_level,
			int salary_level) {
		mName = name;
		mPopulation = population;
		mEconomicIndicator = economic_indicator;
		mSpendingLevel = spending_level;
		mSalaryLevel = salary_level;

		mInterestRate = 0.1f;
	}

	private void updatePopulation() {
		int pop = 0;
		float randomFactor;

		// 랜덤 이동
		randomFactor = MathUtils.randomFloat(0f, 0.01f);
		pop = (int) (mPopulation * randomFactor);
		if(pop > MAX_RANDOM_POP)
			pop = MAX_RANDOM_POP + MathUtils.random(999);
		if(MathUtils.randomBoolean())
			pop = -pop;
		mPopulation += pop;
		Core.APP.debug("pop1 " + pop);

		// 경제 지표에 따른 이동
		randomFactor = MathUtils.randomFloat(0f, 0.01f);
		pop = (int) (mPopulation * randomFactor);
		if(pop > MAX_ECONOMIC_POP)
			pop = MAX_ECONOMIC_POP + MathUtils.random(999);
		pop = (int) ((mEconomicIndicator - 50) / 50f * pop * MathUtils.randomFloat(0.5f, 1f));
		mPopulation += pop;
		Core.APP.debug("pop2 " + pop);
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
	}

	@Override
	public void onMonthChanged(GregorianCalendar calendar, int year, int month, int day) {
		// 한달마다 인구의 변동을 반영한다.
		updatePopulation();
	}
	
	public MarketOverseer getMarketOverseerByCode(String code) {
		int index = ProductManager.getInstance().getProductDataByCode(code).index;
		return mMarketOverseerList.get(index);
	}

	public List<MarketOverseer> getMarketOverseerList() {
		return mMarketOverseerList;
	}

}
