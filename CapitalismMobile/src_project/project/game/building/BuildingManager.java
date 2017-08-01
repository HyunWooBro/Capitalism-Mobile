package project.game.building;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.framework.R;

import project.game.Time.DayListener;
import project.game.Time.MonthListener;
import project.game.city.City;
import project.game.corporation.Corporation;

import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.JDBFException;

import core.framework.Core;
import core.utils.Disposable;

public class BuildingManager implements DayListener, MonthListener, Disposable {
	
	private List<String> mBuildingTypeList = new ArrayList<String>();
	
	private Map<String, String> mTypeToNameMap = new HashMap<String, String>();
	
	
	private List<BuildingDescription> mBDescList = new ArrayList<BuildingDescription>();

	private Map<String, BuildingDescription> mCodeToBDescMap = new HashMap<String, BuildingDescription>();

	private Map<String, List<BuildingDescription>> mTypeToBDescListMap = new HashMap<String, List<BuildingDescription>>();
	
	
	private List<DepartmentDescription> mDDescList = new ArrayList<DepartmentDescription>();

	private Map<String, DepartmentDescription> mTypeToDDescMap = new HashMap<String, DepartmentDescription>();
	
	
	// 이와 같은 데이터는 어떻게 해야 할지 고민중
	private Map<String, List<Building>> mTypeToBuildingListMap = new HashMap<String, List<Building>>();

	private List<Building> mBuildingList = new ArrayList<Building>();

	/** 싱글턴 인스턴스 */
	private volatile static BuildingManager sInstance;

	private BuildingManager() {
	}

	public static BuildingManager getInstance() {
		if(sInstance == null) {
			synchronized(BuildingManager.class) {
				if(sInstance == null) {
					sInstance = new BuildingManager();
				}
			}
		}
		return sInstance;
	}

	public void init() {
		loadBuildingType();
		loadBuilding();
		loadDepartment();
	}

	private void loadBuildingType() {
		try {
			InputStream inputStream = Core.APP.getResources().openRawResource(R.raw.building_type);
			DBFReader reader = new DBFReader(inputStream);
			for(int i = 0; reader.hasNextRecord(); i++) {
				Object objects[] = reader.nextRecord(Charset.forName("UTF-8"));
				String type = objects[0].toString();
				if(type.isEmpty()) break;
				
				mBuildingTypeList.add(type);
				mTypeToNameMap.put(type, objects[1].toString());
			}
		} catch (JDBFException e) {
			e.printStackTrace();
		}
	}

	private void loadBuilding() {
		/*
		 * List<String> typeList = mBuildingTypeList; int n = typeList.size();
		 * for(int i=0; i<n; i++) { String type = typeList.get(i);
		 * mTypeToBuildingDescListMap.put(type, new
		 * ArrayList<BuildingDescription>()); }
		 */

		try {
			InputStream inputStream = Core.APP.getResources().openRawResource(R.raw.building);
			DBFReader reader = new DBFReader(inputStream);
			for(int i = 0; reader.hasNextRecord(); i++) {
				Object objects[] = reader.nextRecord(Charset.forName("UTF-8"));
				if(objects[0].toString().isEmpty()) break;
				
				BuildingDescription desc = new BuildingDescription(objects[0].toString(),
						objects[1].toString(), objects[2].toString(), objects[3].toString(),
						(Long) objects[4], (Long) objects[5], (Long) objects[6], (Long) objects[7],
						Boolean.parseBoolean(objects[8].toString()));
				
				mBDescList.add(desc);
				mCodeToBDescMap.put(desc.code, desc);
				
				List<BuildingDescription> descList = mTypeToBDescListMap.get(desc.type);
				if(descList == null) {
					descList = new ArrayList<BuildingDescription>();
					mTypeToBDescListMap.put(desc.type, descList);
				}
				descList.add(desc);
				
			}
		} catch (JDBFException e) {
			e.printStackTrace();
		}
	}

	private void loadDepartment() {
		try {
			InputStream inputStream = Core.APP.getResources().openRawResource(R.raw.department);
			DBFReader reader = new DBFReader(inputStream);
			for(int i = 0; reader.hasNextRecord(); i++) {
				Object objects[] = reader.nextRecord(Charset.forName("UTF-8"));
				if(objects[0].toString().isEmpty()) break;
				
				DepartmentDescription desc = new DepartmentDescription(objects[0].toString(),
						objects[1].toString(), objects[2].toString(), objects[3].toString(),
						(Long) objects[4], (Long) objects[5]);
				
				mDDescList.add(desc);
				mTypeToDDescMap.put(desc.type, desc);
			}
		} catch (JDBFException e) {
			e.printStackTrace();
		}
	}

	public List<String> getBuildingTypeList() {
		return mBuildingTypeList;
	}

	public String getNameByType(String type) {
		return mTypeToNameMap.get(type);
	}

	public List<BuildingDescription> getBDescList() {
		return mBDescList;
	}

	public BuildingDescription getBDescByCode(String code) {
		return mCodeToBDescMap.get(code);
	}

	public List<BuildingDescription> getBDescListByType(String type) {
		return mTypeToBDescListMap.get(type);
	}

	public List<DepartmentDescription> getDDescList() {
		return mDDescList;
	}

	public DepartmentDescription getDDescByType(String type) {
		return mTypeToDDescMap.get(type);
	}

	public Building newBuilding(BuildingDescription desc, City city) {
		try {
			Class<?> c = Class.forName("project.game.building." + desc.type);
			Method m = c.getMethod("newBuilding", BuildingDescription.class, City.class);
			return (Building) m.invoke(null, desc, city);
		} catch (Exception e) {
			e.printStackTrace();
		}

		throw new RuntimeException("Exception has occured during cast(...).");
	}

	public List<Building> getBuildingListByType(String type) {
		List<Building> buildingList = mTypeToBuildingListMap.get(type);
		if(buildingList == null) {
			if(!mBuildingTypeList.contains(type)) return null;
			
			buildingList = new ArrayList<Building>();
			mTypeToBuildingListMap.put(type, buildingList);
		}
		return buildingList;
	}

	public List<Building> getBuildingList() {
		return mBuildingList;
	}

	public void addBuilding(Building building) {
		addBuilding(building, null);
	}

	public void addBuilding(Building building, Corporation corporation) {
		getBuildingListByType(building.mDesc.type).add(building);
		mBuildingList.add(building);
		// Owner에게도 여기서 처리한다.
		if(corporation != null)
			corporation.addBuilding(building);
	}

	public void removeBuilding(Building building) {
		getBuildingListByType(building.mDesc.type).remove(building);
		mBuildingList.remove(building);
		// Owner에게도 여기서 처리한다.
		Corporation corp = building.getCorporation();
		if(corp != null) {
			corp.removeBuilding(building);
		}
	}

	@Override
	public void dispose() {
		List<Building> buildingList = mBuildingList;
		int n = buildingList.size();
		for(int i = 0; i < n; i++) {
			Building building = buildingList.get(i);
			building.getDepartmentManager().disposeAll();
		}

		sInstance = null;
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
		// 부서 리셋
		List<Building> buildingList = mBuildingList;
		int n = buildingList.size();
		for(int i = 0; i < n; i++) {
			Building building = buildingList.get(i);
			building.resetDeptDaily();
		}
	}

	@Override
	public void onMonthChanged(GregorianCalendar calendar, int year, int month, int day) {
		// 각 사업체의 이번달 순이익 배열 초기화
		List<Building> buildingList = mBuildingList;
		int n = buildingList.size();
		for(int i = 0; i < n; i++) {
			Building building = buildingList.get(i);
			building.resetDeptMonthly();
			building.reset();
		}
	}

	public static class BuildingDescription {
	
		public final String type;
	
		public final String code;
	
		public final String name;
	
		public final String image;
	
		public final long frame;
	
		public final int size;
	
		public final double setupCost;
	
		public final double maintenance;
	
		public final boolean buildable;
		
		private BuildingDescription(String type, String code, String name, String image,
				long frame, long size, double setupCost, double maintenance, boolean buildable) {
			this.type = type;
			this.code = code;
			this.name = name;
			this.image = image;
			this.frame = frame;
			this.size = (int) size;
			this.setupCost = setupCost;
			this.maintenance = maintenance;
			this.buildable = buildable;
		}
	}

	public static class DepartmentDescription {
	
		public final String type;
	
		public final String name;
	
		public final String image;
	
		public final String color;
	
		public final long employees;
	
		public final double setupCost;
		
		private DepartmentDescription(String type, String name, String image, String color,
				long employees, double setupCost) {
			this.type = type;
			this.name = name;
			this.image = image;
			this.color = color;
			this.employees = employees;
			this.setupCost = setupCost;
		}
	}

}
