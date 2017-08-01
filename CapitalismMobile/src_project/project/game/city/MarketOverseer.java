package project.game.city;

import java.util.ArrayList;
import java.util.List;

import project.game.Time;
import project.game.building.Building;
import project.game.building.BuildingManager;
import project.game.building.department.stock.Sales;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.product.Product;
import project.game.product.ProductGroup;
import project.game.product.ProductManager;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager.ProductDescription;

import core.framework.Core;
import core.math.MathUtils;

/**
 * 시장을 관리하는 감독관. 도시마다 각 제품에 대해 각 MarketOverseer</p>
 * 
 * 기업과 소비자와의 거래에서 감독관이 중재하는 역할을 맡는다.</p>
 * 
 * @author 김현우
 */
public class MarketOverseer {
	
	private static final List<DisplayProduct> TMP_DISPLAY_PRODUCT_LIST = new ArrayList<DisplayProduct>();
	
	public final ProductDescription desc;
	
	public final City city;

	public int localQuality; 						// 지역 기업의 입장에서의 제품의 품질
	public int localBrand; 						// 지역 기업의 입장에서의 제품의 브랜드
	public double localPrice; 					// 지역 기업의 입장에서의 제품의 브랜드
	
	private int[] mAvgQualityArray = new int[Time.NUM_DAYS];
	private int[] mAvgBrandArray = new int[Time.NUM_DAYS];
	private double[] mAvgPriceArray = new double[Time.NUM_DAYS];
	
	private double mMaxDailySales;
	private double mTotalDailySales;
	private double mTotalEstimatedSales;
	
	private double mTotalDemand;
	
	public double demandScale;
	public double salesScale;
	
	public MarketOverseer(ProductDescription desc, City city) {
		this.desc = desc;
		this.city = city;
		localBrand = MathUtils.random(5, 10);
		localQuality = MathUtils.random(5, 15);
		localPrice = desc.price * MathUtils.randomFloat(1f, 1.2f);
		for(int i=0; i<Time.NUM_DAYS; i++) {
			mAvgBrandArray[i] = localBrand;
			mAvgQualityArray[i] = localQuality;
			mAvgPriceArray[i] = localPrice;
		}
	}
	
	/** 감독관의 중재하에 기업과 소비자와의 거래가 진행된다. */
	public void tradeBetweenProducerAndConsumer() {
		// 관련 진열제품 리스트를 업데이트한다.
		prepare();
		// 당일에 판매될 수 있는 최대 시장크기를 계산한다.
		computeMaxDailySales();
		reset();
		// 각 진열제품에 대한 수요를 예측한다. 여기서 계산된 결과는 
		// 다른 외부환경을 고려하지 않은 값이다.
		estimate();
		// 예측된 수요의 합과 당일 최대 시장크기의 비율을 계산한다.
		computeScale();
		// 각 진열제품의 예측된 수요에, 앞서 계산된 비율을 적용하여 실제 
		// 거래를 진행한다.
		trade();
		computeETC();
	}
	
	private void prepare() {
		TMP_DISPLAY_PRODUCT_LIST.clear();
		
		List<Building> buildingList = BuildingManager.getInstance().getBuildingList();
		int n = buildingList.size();
		for(int i = 0; i < n; i++) {
			Building building = buildingList.get(i);
			// 기업에 대한 판매를 한다면 무시
			if(building.isForSaleToCorporation()) continue;
			// 감독하는 도시가 아니면 무시
			if(city != building.getCity()) continue; 
			
			List<DisplayProduct> displayProductList = building.getDisplayProductList();
			if(displayProductList == null || displayProductList.isEmpty()) continue;
			
			int k = displayProductList.size();
			for(int j=0; j<k; j++) {
				DisplayProduct display = displayProductList.get(j);
				if(display.desc == desc) TMP_DISPLAY_PRODUCT_LIST.add(display);
			}
		}
		
	}
	
	public int getAvgBrand() {
		int avgBrand = 0;
		for(int i=0; i<Time.NUM_DAYS; i++)
			avgBrand += this.mAvgBrandArray[i];
		avgBrand /= Time.NUM_DAYS;
		return avgBrand;
	}
	
	public int getAvgQuality() {
		int avgQuality = 0;
		for(int i=0; i<Time.NUM_DAYS; i++)
			avgQuality += this.mAvgQualityArray[i];
		avgQuality /= Time.NUM_DAYS;
		return avgQuality;
	}
	
	public double getAvgPrice() {
		double avgPrice = 0;
		for(int i=0; i<Time.NUM_DAYS; i++)
			avgPrice += this.mAvgPriceArray[i];
		avgPrice /= Time.NUM_DAYS;
		return avgPrice;
	}
	
	private void computeMaxDailySales() {
		
		int population = city.mPopulation;
		int economicIndicator = city.mEconomicIndicator;
		double demand = desc.demand;
		int necessity = desc.necessity;
		double overall = ProductManager.getInstance().getOverallScore(desc, getAvgBrand(), getAvgQuality(), getAvgPrice());
		if(overall < -50) 			overall = 1.05;
		else if(overall < -30) 	overall = 1.1;
		else if(overall < -10) 	overall = 1.5;
		else if(overall < 2) 		overall = 2;
		
		mMaxDailySales = population * (demand / Time.NUM_DAYS_OF_YEAR);
		// 상하선을 기본 시장크기의 2배로 정한다.
		double limitSales = mMaxDailySales * 2;
		
		double adjustment = (10 - necessity)/3.0 * -(50 - economicIndicator);
		if(adjustment < -130) 			adjustment = -99;
		else if(adjustment < -110) 	adjustment = -95;
		else if(adjustment < -90) 		adjustment = -90;
		mMaxDailySales *= (1 + adjustment/100);
		mMaxDailySales *= Math.log10(overall);
		
		int result = (int) mMaxDailySales;
		double decimal  = mMaxDailySales - result;
		result += MathUtils.randomBoolean((float) decimal)? 1 : 0;
		if(result > limitSales)
			result = (int) limitSales;
		
		mMaxDailySales = result;
		Core.APP.debug("==maxDailySales : " + mMaxDailySales);
		Core.APP.debug("==overall : " + overall);
		Core.APP.debug("==getAvgBrand() : " + getAvgBrand());
		Core.APP.debug("==getAvgQuality() : " + getAvgQuality());
		Core.APP.debug("==getAvgPrice() : " + getAvgPrice());
		
	}
	
	private void reset() {
		
		int index = Time.getInstance().getDailyArrayIndex();
		
		mAvgQualityArray[index] = 0;
		mAvgBrandArray[index] = 0;
		mAvgPriceArray[index] = 0;
		
		mTotalDailySales = 0;
		mTotalEstimatedSales = 0;
		
		mTotalDemand = 0;
		
		List<Corporation> corporationList = CorporationManager.getInstance().getCorporationList();
		int n = corporationList.size();
		for(int i=0; i<n; i++) {
			Corporation corporation = corporationList.get(i);
			ProductGroup group = corporation.getProductGroupByCode(desc.code);
			group.marketShareList.get(city.index).marketShareArray[index] = 0;
		}
	}

	private void estimate() {
		List<DisplayProduct> displayProductList = TMP_DISPLAY_PRODUCT_LIST;
		int n = displayProductList.size();
		for(int i=0; i<n; i++) {
			DisplayProduct display = displayProductList.get(i);
			display.estimate(this);
		}
	}

	public void addEstimatedSales(double sales) {
		mTotalEstimatedSales += sales;
	}
	
	public void addDemand(double amount) {
		mTotalDemand += amount;
	}
	
	private void computeScale() {
		salesScale = (mTotalEstimatedSales > mMaxDailySales)? mMaxDailySales/mTotalEstimatedSales : 1f;
		demandScale = (mTotalDemand > mMaxDailySales)? mMaxDailySales/mTotalDemand : 1f;
	}
	
	private void trade() {
		List<DisplayProduct> displayProductList = TMP_DISPLAY_PRODUCT_LIST;
		int n = displayProductList.size();
		for(int i=0; i<n; i++) {
			DisplayProduct display = displayProductList.get(i);
			display.tradeWithConsumer(this);
		}
	}

	public void addScaledSales(double sales, int quality, int brand, double price, Corporation corporation) {
		
		int index = Time.getInstance().getDailyArrayIndex();
		
		mAvgQualityArray[index] += (sales * quality);
		mAvgBrandArray[index] += (sales * brand);
		mAvgPriceArray[index] += (sales * price);
		mTotalDailySales += sales;
		
		ProductGroup group = corporation.getProductGroupByCode(desc.code);
		group.marketShareList.get(city.index).marketShareArray[index] += sales;
		
		Core.APP.debug("==sales : " + sales);
	}
	
	private void computeETC() {
		
		int index = Time.getInstance().getDailyArrayIndex();
		
		double tempTotal = mTotalDailySales;
		
		// 나머지는 지역 제품이 팔린 것으로 처리한다.
		double localSales = mMaxDailySales - tempTotal;
		if(localSales > 0) {
			mAvgQualityArray[index] += (localSales * localQuality);
			mAvgBrandArray[index] += (localSales * localBrand);
			mAvgPriceArray[index] += (localSales * localPrice);
			tempTotal += localSales;
		}
		
		mAvgQualityArray[index] /= tempTotal;
		mAvgBrandArray[index] /= tempTotal;
		mAvgPriceArray[index] /= tempTotal;
		
		
		
		List<Corporation> corporationList = CorporationManager.getInstance().getCorporationList();
		int n = corporationList.size();
		for(int i=0; i<n; i++) {
			Corporation corporation = corporationList.get(i);
			ProductGroup group = corporation.getProductGroupByCode(desc.code);
			group.marketShareList.get(city.index).marketShareArray[index] /= tempTotal;
			
			if(desc.code.equals("GOLD_RING")) {
				Core.APP.debug("==group.marketSharesList.get(city.index)[index] : " + group.marketShareList.get(city.index).marketShareArray[index]);
				Core.APP.debug("==tempTotal : " + tempTotal);
			}
		}
		
		Core.APP.debug("~~sales : " + mTotalDailySales);
		Core.APP.debug("~~percent : " + mTotalDailySales/mMaxDailySales*100 + "%");
	}
	
}
