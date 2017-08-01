package project.game.building;

import java.util.ArrayList;
import java.util.List;

import project.game.Time;
import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.department.Department;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.DepartmentManager;
import project.game.city.City;
import project.game.corporation.Corporation;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager.ProductDescription;

import core.math.Vector2;

/**
 * Building의 확장 클래스를 구현할 때마다 building_class.dbf와 building.dbf에 추가해야 한다. 또한, </p>
 * 
 * <code> public static Building newBuilding(BuildingDescription) </code></p>
 * 
 * 를 추가해야 Building 객체를 생성할 수 있다.</p>
 * 
 * @author 김현우
 */
public abstract class Building {

	/**
	 * 선언된 순서는 실제 게임에서 출력되는 순서와 같다.
	 * 
	 * @author 김현우
	 */
	/*
	 * public enum BuildingType { RETAIL(Retail.class.getSimpleName()),
	 * FARM(Farm.class.getSimpleName()), FACTORY(Factory.class.getSimpleName()),
	 * RND(RND.class.getSimpleName()), PORT(Port.class.getSimpleName()),
	 * HQ(Port.class.getSimpleName());
	 * 
	 * BuildingType(String className) { mClassName = className; } public String
	 * getClassName() { return mClassName; } private String mClassName; }
	 */

	private List<DepartmentType> mAvailableDepartmentList = new ArrayList<DepartmentType>();
	
	/** 건물 Description */
	protected BuildingDescription mDesc;

	protected boolean mCanEnter;

	/** 소속 기업 */
	private Corporation mCorporation;

	/** 건설된 도시 */
	private City mCity;

	public Vector2 mFirstCellPos = new Vector2(); // 가장 첫번째 셀의 위치
	public int mAverageLandvalue; // 총 지가

	private DepartmentManager mDepartmentManager = new DepartmentManager(this);

	public double mAnnualNetprofit; // 사업체의 연간순이익
	public double[] mMonthlyNetprofit; // 사업체의 최근 12달의 순이익 배열
	public double mLastNetprofit; // 사업체의 최근 13달 이전의 순이익
	// 위 배열의 인덱스(배열의 끝까지 도달하면 다시 처럼으로)는 PlayerCorporation 클래스의 것을 사용

	public int mMaxNetprofit; // 그래프의 현재 최고 수치($1,000 단위로 구분)

	/*package*/ Building(BuildingDescription desc, City city/*
														 * BuildingType
														 * buildingType
														 */) {
		mDesc = desc;
		setAvailableDepartmentList(mAvailableDepartmentList);
		mMonthlyNetprofit = new double[Time.NUM_MONTHS];
		mCity = city;
	}

	protected void setAvailableDepartmentList(List<DepartmentType> availableDepartmentList) {
	}

	public List<DepartmentType> getAvailableDepartmentList() {
		return mAvailableDepartmentList;
	}

	public Department newDepartment(int index, DepartmentType type) {
		return null;
	}

	public void calculateMaxGraphPoint() {

		double[] monthlyNetprofit = mMonthlyNetprofit;

		double max = Math.abs(monthlyNetprofit[0]);
		for(int i = 1; i < 12; i++) {
			double netProfit = Math.abs(monthlyNetprofit[i]);
			if(max < netProfit)
				max = netProfit;
		}

		if(max < 1000) {
			mMaxNetprofit = 1000;

		} else {
			mMaxNetprofit = (int) (max / 1000);
			mMaxNetprofit = (mMaxNetprofit + 1) * 1000;
		}

		/*
		 * mAnnualNetprofit = 0; for(int i=0; i<12; i++) { mAnnualNetprofit +=
		 * mMonthlyNetprofit[i]; }
		 */

	}

	public void reset() {
		int index = Time.getInstance().getMonthlyArrayIndex();
		mLastNetprofit = mMonthlyNetprofit[index];
		mMonthlyNetprofit[index] = 0;
	}

	public void resetDeptDaily() {
		mDepartmentManager.resetDaily();
	}
	
	public void resetDeptMonthly() {
		mDepartmentManager.resetMonthly();
	}

	public BuildingDescription getDescription() {
		return mDesc;
	}

	public DepartmentManager getDepartmentManager() {
		return mDepartmentManager;
	}

	public Corporation getCorporation() {
		return mCorporation;
	}

	/** Building의 소유권을 corporation에게 수여한다. 만약 이미 Corporation에 속해 있다면 제거된다. */
	public void setCorporation(Corporation corporation) {
		// if(mCorporation == corporation) return;
		// mCorporation.removeBuilding(this);
		mCorporation = corporation;
		// if(corporation != null) corporation.addBuilding(this);
	}

	public int getTotalEmployees() {
		return mDepartmentManager.getTotalEmployees();
	}

	public City getCity() {
		return mCity;
	}
	
	public List<DisplayProduct> getDisplayProductList() {
		return null;
	}
	
	/** {@link ProductDescription}과 {@link Corporation}이 일치하는 진열제품을 얻는다. */
	public DisplayProduct getDisplayProduct(ProductDescription description, Corporation producer) {
		List<DisplayProduct> displayProductList = getDisplayProductList();
		if(displayProductList == null) return null;
		
		int n = displayProductList.size();
		for(int i=0; i<n; i++) {
			DisplayProduct displayProduct = displayProductList.get(i);
			if(displayProduct.desc == description && displayProduct.producer == producer) {
				return displayProduct;
			}
		}
		
		return null;
	}
	
	public boolean canEnter() {
		return canViewDepartmentTab() || canViewDisplayTab();
	}
	
	public boolean canViewDepartmentTab() {
		return true;
	}
	
	public boolean canViewDisplayTab() {
		return true;
	}
	
	public boolean isForSaleToCorporation() {
		return false;
	}

	// abstract BuildingType getBuildingType();

}
