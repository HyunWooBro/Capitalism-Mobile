package project.game;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import project.game.Time.DayListener;
import project.game.building.Building;
import project.game.building.BuildingManager;
import project.game.city.City;
import project.game.city.CityManager;
import project.game.city.MarketOverseer;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager;

import core.framework.Core;
import core.scene.Director;
import core.utils.Disposable;

/**
 * 게임의 핵심인 경제활동을 제어한다. Capital Processing Unit의 약자로써 컴퓨터의 CPU 같은 역할을 한다.
 * 
 * @author 김현우
 */
public class CPU implements DayListener, Disposable {
	public static double m_max_daily_positive_profit;
	public static double m_max_daily_negative_profit;

	/** 사원 일일 평균 일당 */
	public double mDailySalariesExpenses = 60;

	// 싱글턴
	private volatile static CPU sInstance;

	private CPU() {
	}

	public static CPU getInstance() {
		if(sInstance == null) {
			synchronized(CPU.class) {
				if(sInstance == null)
					sInstance = new CPU();
			}
		}
		return sInstance;
	}

	public static void InitStatic() {
		m_max_daily_positive_profit = 0;
		m_max_daily_negative_profit = 0;
	}

	public void process(GameScene scene) {
		double player_cost = 0;
		double player_earning = 0;
		double player_net_profit = 0;
		int maximum_day = 0;

		double player_economic_result = 0;

		double value = 0;

		List<Building> buildingList = BuildingManager.getInstance().getBuildingList();
		int n = buildingList.size();
		
		
		
		// 소비자에게 판매할 수 있는 최대량 계산
		/*for(int i=0; i<m; i++) {
			ProductOverseer data = productDataList.get(i);
			data.computeMaxDailySales(0);
			data.reset();
		}*/
		
		// 각 부서의 사전 처리
		/*for(int i = 0; i < n; i++) {
			Building building = buildingList.get(i);
			building.getDepartmentManager().preprocess();
		}*/
		
		// 브랜드 계산
		// 각 기업에 대해
		// 브랜드 종류에 따라 처리 (switch)
		
		
		
		// 기업간 거래
		for(int i = 0; i<n; i++) {
			Building building = buildingList.get(i);
			// 기업에 대한 판매를 하지 않으면 무시
			if(!building.isForSaleToCorporation()) continue;
			
			List<DisplayProduct> displayProductList = building.getDisplayProductList();
			if(displayProductList == null || displayProductList.isEmpty()) continue;
			
			int k = displayProductList.size();
			for(int j=0; j<k; j++) {
				DisplayProduct display = displayProductList.get(j);
				display.reset();
				display.tradeWithCorporation();
			}
		}
		
		// 소비자에게 판매
		List<City> cityList = CityManager.getInstance().getCityList();
		int m = cityList.size();
		for(int i=0; i<m; i++) {
			City city = cityList.get(i);
			
			List<MarketOverseer> overseerList = city.getMarketOverseerList();
			int l = overseerList.size();
			for(int j=0; j<l; j++) {
				MarketOverseer overseer = overseerList.get(j);
				// 소비할 수 없는 제품은 무시한다.
				if(!overseer.desc.isConsumable()) continue;
				overseer.tradeBetweenProducerAndConsumer();
			}
		}
		
		
		//ProductManager.getInstance().get
		
		// 소비자에게 실제 판매할 수 있는 비율 계산
		/*for(int i=0; i<m; i++) {
			ProductOverseer data = productDataList.get(i);
			data.computeScale();
			data.resetTotalDailySales();
		}*/
		
		
		// 각 부서처리
		for(int i = 0; i < n; i++) {
			Building building = buildingList.get(i);
			if(building.getCorporation() == CorporationManager.getInstance().getPlayerCorporation()) {
				player_economic_result += building.getDepartmentManager().process();
			} else
				building.getDepartmentManager().process();
		}
		
		// 시장점유율, 도시 평균 총점 등 계산
		/*for(int i=0; i<m; i++) {
			ProductOverseer data = productDataList.get(i);
			data.computeETC();
		}*/

		/*
		 * List<Building> buildingList =
		 * BuildingManager.getInstance().getBuildingListByType("Retail"); int n
		 * = buildingList.size(); for(int i=0; i<n; i++) { Building building =
		 * buildingList.get(i); player_economic_result +=
		 * building.getDepartmentManager().process(); }
		 * 
		 * buildingList =
		 * BuildingManager.getInstance().getBuildingListByType("Factory"); n =
		 * buildingList.size(); for(int i=0; i<n; i++) { Building building =
		 * buildingList.get(i); player_economic_result +=
		 * building.getDepartmentManager().process(); }
		 */

		m_max_daily_positive_profit = 0;
		m_max_daily_negative_profit = 0;

		int index = Time.getInstance().getMonthlyArrayIndex();

		// 현재 달의 최대 일 수
		maximum_day = Time.getInstance().getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);

		FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
				.getFinancialData();

		// 대출 이자
		value = financialData.loan / 10 / 12 / maximum_day;
		player_cost += value;
		financialData.monthlyLoanInterestArray[index] += value;
		financialData.accumulatedLoanInerest += value;

		// 기타순이익 계산
		financialData.monthlyOtherProfit[index] = financialData.monthlyAssetValue[index]
				- financialData.monthlyLoanInterestArray[index];

		financialData.accumulatedOtherProfit = financialData.accumulatedAssetValue
				- financialData.accumulatedLoanInerest;

		// 하루 순이익
		player_net_profit = player_earning - player_cost + player_economic_result;

		Core.APP.debug("net : " + player_net_profit);

		financialData.cash += player_net_profit;

		// gamestate.m_player.m_annual_netprofit += player_net_profit;

		// 영업지출 계산
		financialData.monthlyOperatingExpensesArray[index] = financialData.monthlySalesCostArray[index]
				+ financialData.monthlySalariesExpensesArray[index]
				+ financialData.monthlyOperatingOverheadArray[index];

		financialData.accumulatedOperatingExpenses = financialData.accumulatedSalesCost
				+ financialData.accumulatedSalariesExpenses
				+ financialData.accumulatedOperatingOverhead;

		// 영업순이익 계산
		financialData.monthlyOperatingProfitArray[index] = financialData.monthlyOperatingRevenueArray[index]
				- financialData.monthlyOperatingExpensesArray[index];

		financialData.accumulatedOperatingProfit = financialData.accumulatedOperatingRevenue
				- financialData.accumulatedOperatingExpenses;

		// 순이익 계산
		financialData.monthlyNetprofitArray[index] += player_net_profit;
		financialData.accumulatedNetprofit += player_net_profit;

		financialData.annualNetprofit = 0;
		for(int i = 0; i < Time.NUM_MONTHS; i++)
			financialData.annualNetprofit += financialData.monthlyNetprofitArray[i];

		financialData.calculateMaxGraphPoint();

	}

	@Override
	public void dispose() {
		sInstance = null;
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
		// 경제관련 계산을 수행

		process((GameScene) Director.getInstance().getCurrentScene());
	}

}
