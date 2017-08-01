package project.game.product;

import java.util.ArrayList;
import java.util.List;

import core.math.MathUtils;

import project.game.city.City;
import project.game.corporation.Corporation;

public class Brand {
	
	public int brandAwareness;
	public int brandLoyalty;
	
	//private City mCity;
	
	//private Corporation mCorporation;
	
	public Brand() {
	}

	public Brand(int brandAwareness, int brandLoyalty) {
		this.brandAwareness = brandAwareness;
		this.brandLoyalty = brandLoyalty;
	}
	
	public int getTotalBrand() {
		return brandAwareness + brandLoyalty;
	}
	
	// 광고나 시장점유율에 따른 변동을 계산하는 메서드를 추가하자.
	
	public void update(double marketShare) {
		// 인지도가 오를수록 증가폭은 반비례한다.
		float divider = Math.max(1f, brandAwareness);
		if(MathUtils.randomBoolean((float) (marketShare / divider))) {
			brandAwareness++;
			if(brandAwareness > 100) brandAwareness = 100;
		}
		
		// 시장점유율이 인지도에 비해 적으면 감소하는 것도 있어야 할 것 같다.
	}
	
	public static class RangeBrand {
		
		public List<Brand> brandList = new ArrayList<Brand>();
		
		public RangeBrand(int numTypes) {
			for(int i=0; i<numTypes; i++) 
				brandList.add(new Brand());
		}
	}

}
