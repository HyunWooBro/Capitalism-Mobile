package project.game.corporation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.game.Time;
import project.game.building.Building;
import project.game.building.BuildingManager;
import project.game.city.City;
import project.game.city.CityManager;
import project.game.person.Person;
import project.game.product.Brand;
import project.game.product.Brand.RangeBrand;
import project.game.product.Product;
import project.game.product.ProductGroup;
import project.game.product.ProductGroup.MarketShare;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductDescription;

import core.framework.graphics.Color4;

/**
 * 기업은 크게 3가지로 구분된다. 공기업, 유저기업, AI기업
 * 
 * @author 김현우
 */
public abstract class Corporation {

	public enum BrandType {
		/** 기업 브랜드 */
		CORPORATE, 
		/** 종류별 브랜드 */
		RANGE, 
		/** 제품별 브랜드 */
		UNIQUE, 
	}
	
	/*pcakage*/ int index;

	/*package*/ BrandType mBrandType = BrandType.UNIQUE;

	// 재무적인 자세한 데이터는 이곳에서
	protected FinancialData mFinancialData;

	/** 기업 브랜드의 경우에만 적용된다. */
	/*package*/ List<Brand> mCorporateBrandList = new ArrayList<Brand>();
	
	/** 종류별 브랜드의 경우에만 적용된다. */
	/*package*/ List<RangeBrand> mRangeBrandList = new ArrayList<RangeBrand>();

	/*package*/ Person mOwner;

	/*package*/ String mName;

	/*package*/ Color4 mColor;

	/*package*/ int logo;

	/*package*/ int mTotalEmployees;
	
	/*package*/ Map<String, List<Building>> mTypeToBuildingListMap = new HashMap<String, List<Building>>();

	/*package*/ List<Building> mBuildingList = new ArrayList<Building>();
	
	/*package*/ List<ProductGroup> mProductGroupList = new ArrayList<ProductGroup>();
	
	/*package*/ Map<String, ProductGroup> mCodeToProductGroupMap = new HashMap<String, ProductGroup>();

	/*package*/ Map<String, List<Product>> mTypeToProductListMap = new HashMap<String, List<Product>>();

	/*package*/ List<Product> mProductList = new ArrayList<Product>();
	
	public void initProductData() {
		List<City> cityList = CityManager.getInstance().getCityList();
		int m = cityList.size();
		
		List<ProductGroup> productGroupList =  mProductGroupList;
		Map<String, ProductGroup> codeToProductListMap = mCodeToProductGroupMap;
		
		List<ProductDescription> descList = ProductManager.getInstance().getPDescList();
		int n = descList.size();
		
		List<String> typeList = ProductManager.getInstance().getProductTypeList();
		int l = typeList.size();
		
		List<Brand> brandList = mCorporateBrandList;
		List<RangeBrand> rangeBrandList = mRangeBrandList;
		for(int i=0; i<m; i++) {
			brandList.add(new Brand());
			rangeBrandList.add(new RangeBrand(l));
		}
		
		for(int i = 0; i<n; i++) {
			ProductDescription desc = descList.get(i);
			ProductGroup group = new ProductGroup(desc);
			productGroupList.add(group);
			codeToProductListMap.put(desc.code, group);
			
			for(int j=0; j<m; j++) {
				group.uniqueBrandList.add(new Brand());
				group.marketShareList.add(new MarketShare());
				
				RangeBrand brand = rangeBrandList.get(j);
				int index = ProductManager.getInstance().getProductTypeList().indexOf(group.desc.type);
				group.rangeBrand = brand.brandList.get(index);
			}
		}
		
	}

	public List<Building> getBuildingListByType(String type) {
		List<Building> buildingList = mTypeToBuildingListMap.get(type);
		if(buildingList == null) {
			if(!BuildingManager.getInstance().getBuildingTypeList().contains(type))
				return null;
			buildingList = new ArrayList<Building>();
			mTypeToBuildingListMap.put(type, buildingList);
		}
		return buildingList;
	}

	public List<Building> getBuildingList() {
		return mBuildingList;
	}

	public void addBuilding(Building building) {
		Corporation corp = building.getCorporation();
		if(corp != null)
			corp.removeBuilding(building);
		getBuildingListByType(building.getDescription().type).add(building);
		mBuildingList.add(building);
		building.setCorporation(this);
	}

	public void removeBuilding(Building building) {
		getBuildingListByType(building.getDescription().type).remove(building);
		mBuildingList.remove(building);
		building.setCorporation(null);
	}

	public Map<String, ProductGroup> getCodeToProductGroupMap() {
		return mCodeToProductGroupMap;
	}

	public ProductGroup getProductGroupByCode(String code) {
		return mCodeToProductGroupMap.get(code);
	}

	public List<Product> getProductListByType(String type) {
		List<Product> productList = mTypeToProductListMap.get(type);
		if(productList == null) {
			if(!ProductManager.getInstance().getProductTypeList().contains(type))
				return null;
			productList = new ArrayList<Product>();
			mTypeToProductListMap.put(type, productList);
		}
		return productList;
	}

	public List<Product> getProductList() {
		return mProductList;
	}

	public void addProduct(Product product) {
		Corporation corp = product.producer;
		if(corp != null)
			corp.removeProduct(product);
		getProductListByType(product.desc.type).add(product);
		mProductList.add(product);
		mCodeToProductGroupMap.get(product.desc.code).productList.add(product);
		product.producer = this;
		product.productGroup = mCodeToProductGroupMap.get(product.desc.code);
	}

	public void removeProduct(Product product) {
		getProductListByType(product.desc.type).remove(product);
		mProductList.remove(product);
		mCodeToProductGroupMap.get(product.desc.code).productList.remove(product);
		product.producer = null;
		product.productGroup = null;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public BrandType getBrandType() {
		return mBrandType;
	}

	public void setBrandType(BrandType brandType) {
		if(mBrandType == brandType) return;
		mBrandType = brandType;
		// 모든 brand를 리셋해야...
	}
	
	public FinancialData getFinancialData() {
		return mFinancialData;
	}

	public Color4 getColor() {
		return mColor;
	}
	
	/** 
	 * 매일 광고부와는 별개로 현재 시장상황에 따라 브랜드를 업데이트 한다. 
	 * 브랜드는 다음과 같은 요소에 의해 변동된다.
	 * <li>fefe</li>
	 * <li>fefe</li>
	 */
	public void updateBrand() {
		switch(mBrandType) {
			case CORPORATE:
				break;
			case RANGE:
				break;
			case UNIQUE:
				
				List<ProductGroup> productGroupList =  mProductGroupList;
				int n = productGroupList.size();
				for(int i = 0; i<n; i++) {
					ProductGroup group = productGroupList.get(i);
					// 소비할 수 없는 제품은 무시한다.
					if(!group.desc.isConsumable()) continue;
					
					List<Brand> brandList = group.uniqueBrandList;
					int m = brandList.size();
					for(int j=0; j<m; j++) {
						Brand brand = brandList.get(j);
						double avg = group.marketShareList.get(j).getAvg();
						brand.update(avg);
					}
					
				}
				
				break;
		}
	}

	public List<Brand> getCorporateBrandList() {
		return mCorporateBrandList;
	}

	public List<RangeBrand> getRangeBrandList() {
		return mRangeBrandList;
	}

}
