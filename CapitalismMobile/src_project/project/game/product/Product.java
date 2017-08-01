package project.game.product;

import java.util.ArrayList;
import java.util.List;

import project.game.Time;
import project.game.building.Building;
import project.game.building.department.stock.Purchase;
import project.game.building.department.stock.Sales;
import project.game.city.City;
import project.game.city.CityManager;
import project.game.city.MarketOverseer;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.product.ProductManager.ProductDescription;

import core.framework.Core;
import core.math.MathUtils;

public class Product {
	
	public static final int MAX_QUALITY = 100;
	
	public final ProductDescription desc;

	public ProductGroup productGroup;
	
	/** 제품의 제조사 */
	public Corporation producer;
	/** 제품의 공급사 */
	public Corporation supplier;
	
	/** 제품의 품질 */
	public int quality; 
	
	/** 비용(구매 등) */
	public double cost;
	
	/** 운송비 */
	public double freight;

	public Product(ProductDescription desc) {
		this.desc = desc;
	}
	
	public Product(Product other) {
		desc = other.desc;
		productGroup = other.productGroup;
		producer = other.producer;
		supplier = other.supplier;
		quality = other.quality;
		cost = other.cost;
		freight = other.freight;
	}
	
	public double getTotalCost() {
		return cost + freight;
	}
	
	/** 진열탭에 등장하는 제품 */
	public static class DisplayProduct extends Product {
		
		public final City city;
		
		/** 판매 가격 */
		public double price;
		
		/** 새로운 판매 가격 */
		public double tempPrice;
		
		/** 이 제품을 구매하는 구매부 리스트 */
		public List<Purchase> purchaseList = new ArrayList<Purchase>();
		
		/** 이 제품을 판매하는 판매부 리스트 */
		public List<Sales> salesList = new ArrayList<Sales>();
		
		/** 최근 한달 동안의 공급배열. 관련 구매부와 판매부가 이것을 공유한다. */
		public int[] supplyArray = new int[Time.NUM_DAYS];
		
		/** 최근 한달 동안의 수요배열. 관련 구매부와 판매부가 이것을 공유한다. */
		public int[] demandArray = new int[Time.NUM_DAYS];
		
		/** 공급과 수요 막대의 길이를 결정한다. */
		public float supplyDemand;
		
		/** 
		 * 최근 한달 동안의 활용배열. 관련 판매부가 이것을 공유한다. 공급과 수요와는 달리 
		 * 구매부는 독자적인 배열을 사용한다.
		 */
		public int[] utilizationArray = new int[Time.NUM_DAYS];
		
		/** 평균 활용값 */
		public float avgUtilization;
		
		/** 
		 * 내부 판매 여부를 지정한다. 건물의 {@link Building#isForSaleToCorporation()}이 
		 * true인 경우에만 의미가 있다. 
		 */
		public boolean internalSales;
		
		public int tempStock;

		public boolean update;
		
		private double mEstimatedSales;
		
		private int mDemand;
		
		public DisplayProduct(Product other, City city) {
			super(other);
			this.city = city;
		}
		
		public void reset() {
			int index = Time.getInstance().getDailyArrayIndex();
			supplyArray[index] = 0;
			demandArray[index] = 0;
			utilizationArray[index] = 0;
		}
		
		public void tradeWithCorporation() {
			
			List<Purchase> purchaseList = this.purchaseList;
			int n = purchaseList.size();
			
			List<Sales> salesList = this.salesList;
			int m = salesList.size();
			
			if(n == 0 || m == 0) return;
			
			int totalSupplyStock = getTotalSupplyStock();
			int totalDemandStock = getTotalDemandStock();
			int amount = totalSupplyStock;
			
			
			// 사실 구매자가 돈이 있는지를 고려해야 한다.
			computeSupplyDemand(totalSupplyStock, totalDemandStock);
			
			
			for(int i=0; i<n; i++) {
				Purchase purchase = purchaseList.get(i);
				
				amount = purchase.trade(this, amount);
			}
			
			amount = totalSupplyStock - amount;
			
			computeUtilization(amount);
			
			if(amount == 0) return;
			
			// 순서대로 돌면서 판매부에서 재고를 줄인다.
			for(int i=0; i<m; i++) {
				Sales sales = salesList.get(i);
				
				amount = sales.trade(amount);
				
				// 할당량이 다 팔린 경우 빠져나간다.
				if(amount == 0) break;
			}
		}
		
		/** 현재 공급 가능한 재고를 얻는다. */
		private int getTotalSupplyStock() {
			int totalSupplyStock = 0;
			
			List<Sales> salesList = this.salesList;
			int n = salesList.size();
			for(int i=0; i<n; i++) {
				Sales sales = salesList.get(i);
				
				int stock = sales.mMaxWorkPerDay;
				if(stock > sales.mCurrStock) 
					stock = sales.mCurrStock;
				
				totalSupplyStock += stock;
			}
			
			return totalSupplyStock;
		}
		
		private int getTotalDemandStock() {
			int totalDemandStock = 0;
			
			// 사실 기업이 이것을 살 수 있는 자금이 있는지도 고려해야 한다.
			
			List<Purchase> purchaseList = this.purchaseList;
			int n = purchaseList.size();
			for(int i=0; i<n; i++) {
				Purchase purchase = purchaseList.get(i);
				
				int stock = purchase.mMaxWorkPerDay;
				int space = purchase.mMaxStock - purchase.mCurrStock;
				if(stock > space) stock = space;
				
				totalDemandStock += stock;
			}
			
			return totalDemandStock;
		}
		
		private int getTotalMaxWorkPerDay() {
			int totalMaxWorkPerDay = 0;
			
			List<Sales> salesList = this.salesList;
			int n = salesList.size();
			for(int i=0; i<n; i++) {
				Sales sales = salesList.get(i);
				totalMaxWorkPerDay += sales.mMaxWorkPerDay;
			}
			
			return totalMaxWorkPerDay;
		}

		private void computeSupplyDemand(int supply, int demand) {
			
			// 공급과 수요 계산
			int index = Time.getInstance().getDailyArrayIndex();
			supplyArray[index] = supply;
			demandArray[index] = demand;
			
			int totalSupply = 0;
			int totalDemand = 0;
			for(int i=0; i<Time.NUM_DAYS; i++) {
				totalSupply += supplyArray[i];
				totalDemand += demandArray[i];
			}
			
			if(totalSupply >= totalDemand) {
				supplyDemand = (float) totalDemand / totalSupply;
			} else
				supplyDemand = (float) -totalSupply / totalDemand;
		}
		
		private void computeUtilization(int work) {
			int index = Time.getInstance().getDailyArrayIndex();
			utilizationArray[index] = work;
			
			float totalUtilization = 0;
			for(int i=0; i<Time.NUM_DAYS; i++)
				totalUtilization += utilizationArray[i];
			avgUtilization = totalUtilization / (getTotalMaxWorkPerDay()*Time.NUM_DAYS);
		}
		
		public void estimate(MarketOverseer overseer) {
			
			int totalSupplyStock = getTotalSupplyStock();
			
			mEstimatedSales = 0;
			
			double maximum_sales = 0;
			
			// 총점
			int overall = ProductManager.getInstance().getOverallScore(this);
			int avgOverall = ProductManager.getInstance().getOverallScore(desc, overseer.getAvgBrand(), overseer.getAvgQuality(), overseer.getAvgPrice());
			double diff = overall - avgOverall;
			if(diff < -50) 			diff = 1.01;
			else if(diff < -30) 	diff = 1.05;
			else if(diff < -10) 	diff = 1.1;
			else if(diff < 2) 	diff = 2;
			maximum_sales = Math.log10(diff);
			
			// 경제지표 & 생필품지수
			int necessity = desc.necessity;
			int economicIndicator = CityManager.getInstance().getCurrentCity().mEconomicIndicator;
			double adjustment = (10 - necessity)/3.0 * -(50 - economicIndicator);
			if(adjustment < -130) 			adjustment = -99;
			else if(adjustment < -110) 	adjustment = -95;
			else if(adjustment < -90) 		adjustment = -90;
			maximum_sales *= (1 + adjustment/100);
			
			// 지가
			// maximum_sales *= (double)
			// PlayerCorporation.m_retaillist.get(i).m_average_landvalue/LandValue.CLASS_A.GetValue();
			
			// 보정
			maximum_sales *= MathUtils.randomFloat(0.8f, 1.2f);
			
			// 공급량
			maximum_sales *= totalSupplyStock;
			
			//Core.APP.debug("%%0 maxDailySales : " + overseer.maxDailySales);
			
			//Core.APP.debug("%%1 maximum_sales : " + maximum_sales);
			
			// 정리
			int result = getTrimmedValue(maximum_sales);
			
			mDemand = result;
			//computeSupplyDemand(totalSupplyStock, result);
			
			
			overseer.addDemand(result);
			
			if(result > totalSupplyStock)
				result = totalSupplyStock;
			mEstimatedSales = result;
			
			overseer.addEstimatedSales(result);
			
			
			Core.APP.debug("%%2 mEstimatedSales : " + mEstimatedSales);
			
		}
		
		public void tradeWithConsumer(MarketOverseer overseer) {
			
			int totalSupplyStock = getTotalSupplyStock();
			
			// 정리
			int demandStock = getTrimmedValue(mEstimatedSales * overseer.salesScale);
			
			Core.APP.debug("%%3 totalDemandStock : " + demandStock);
			
			// 정리
			int demand = getTrimmedValue(mDemand * overseer.demandScale);
			
			computeSupplyDemand(totalSupplyStock, demand);
			
			
			int amount = demandStock;
			if(amount > totalSupplyStock) {
				amount = totalSupplyStock;
			}
			
			overseer.addScaledSales(amount, quality, getBrand().getTotalBrand(), 0, supplier);
			
			// 순서대로 돌면서 판매부에서 재고를 줄인다.
			List<Sales> salesList = this.salesList;
			int n = salesList.size();
			for(int i=0; i<n; i++) {
				Sales sales = salesList.get(i);
				
				amount = sales.trade(amount);
				
				// 할당량이 다 팔린 경우 빠져나간다.
				// 그런데 이렇게 하면 나머지 부서의 공급, 수요 그래프를 계산할 수 없다.
				if(amount == 0) break;
			}
			
			computeUtilization(totalSupplyStock - amount);
			
		}
		
		private int getTrimmedValue(double value) {
			int result = (int) value;
			double decimal  = value - result;
			result += MathUtils.randomBoolean((float) decimal)? 1 : 0;
			return result;
		}
		
		public Brand getBrand() {
			Brand brand = null;
			switch(producer.getBrandType()) {
				case CORPORATE:	return producer.getCorporateBrandList().get(city.index);
				case RANGE:			return productGroup.rangeBrand;
				case UNIQUE:			return brand = productGroup.uniqueBrandList.get(city.index);
			}
			throw new IllegalStateException();
		}
		
	}

}
