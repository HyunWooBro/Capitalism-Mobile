package project.game.product;

import java.util.ArrayList;
import java.util.List;

import project.game.Time;
import project.game.product.ProductManager.ProductDescription;

public class ProductGroup {
	
	public final ProductDescription desc;

	public List<Product> productList;

	/** 제품의 현재 기술수준 */
	public int tech;
	
	/** 연구중인가? */
	public boolean researching;
	
	/** 제품을 판매하기 시작한 후 경과 일자 */
	public int elaspedDays;

	/** 제품별 브랜드의 경우에만 적용된다. */
	public List<Brand> uniqueBrandList = new ArrayList<Brand>();
	
	/** 현재의 제품과 관련된 종류별 브랜드 */
	public Brand rangeBrand;
	
	public List<MarketShare> marketShareList = new ArrayList<MarketShare>();
	
	public ProductGroup(ProductDescription desc) {
		// 각 기업에 대해서는 리스트를 초기화하고 초기 기술은 30으로 설정한다.
		this.desc = desc;
		productList = new ArrayList<Product>();
		tech = ProductManager.INIT_TECH;
	}
	
	/** 
	 * 최근 1달을 기간으로 시장점유율을 관리한다. 각 요소는 최대값 1(100%)을 
	 * 갖을 수 있다.
	 */
	public static class MarketShare {
		
		private int mArrayIndex = -1;
		
		private double mAvg;
		
		public double[] marketShareArray = new double[Time.NUM_DAYS];
		
		public double getAvg() {
			int index = Time.getInstance().getDailyArrayIndex();
			if(index == mArrayIndex) return mAvg;
			mArrayIndex = index;
			
			double avg = 0;
			for(int k=0; k<Time.NUM_DAYS; k++) {
				avg += marketShareArray[k];
			}
			avg /= Time.NUM_DAYS;
			mAvg = avg;
			return avg;
		}
	}
	
}