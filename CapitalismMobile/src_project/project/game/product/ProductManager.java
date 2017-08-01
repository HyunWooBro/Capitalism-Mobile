package project.game.product;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.framework.R;

import project.framework.Utils;
import project.game.city.City;
import project.game.city.CityManager;
import project.game.city.MarketOverseer;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.product.Product.DisplayProduct;

import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.JDBFException;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.widget.label.DLabel;
import core.utils.Disposable;

public class ProductManager implements Disposable {
	
	public static final int INIT_TECH = 30;
	
	public static final int INIT_MAX_TECH = 100;
	
	private Map<String, ProductDescription> mCodeToPDescMap = new HashMap<String, ProductDescription>();

	// 쓰임새 없음
	private Map<String, List<ProductDescription>> mTypeToPDescListMap = new HashMap<String, List<ProductDescription>>();

	private List<ProductDescription> mPDescList = new ArrayList<ProductDescription>();

	private Map<String, ProductData> mCodeToProductDataMap = new HashMap<String, ProductData>();

	private Map<String, String> mTypeToNameMap = new HashMap<String, String>();

	private List<String> mProductTypeList = new ArrayList<String>();

	private Map<String, List<String>> mRetailCodeToProductTypeListMap = new HashMap<String, List<String>>();

	private Map<String, List<ManufacturingDescription>> mTypeToMDescListMap = new HashMap<String, List<ManufacturingDescription>>();

	private List<ManufacturingDescription> mMDescList = new ArrayList<ManufacturingDescription>();

	// 쓰임새 현재 없지만 사용 가능 - Laboratory
	private List<ProductDescription> mInputPDescList = new ArrayList<ProductDescription>();
	
	// 추가하자
	private List<ProductDescription> mOutputPDescList = new ArrayList<ProductDescription>();
	
	private List<String> mInputCodeList = new ArrayList<String>();
	
	private Map<String, ManufacturingDescription> mOutputCodeToMDescMap = new HashMap<String, ManufacturingDescription>();
	
	private Map<String, List<ManufacturingDescription>> mInputCodeToMDescListMap = new HashMap<String, List<ManufacturingDescription>>();
	
	// 쓰임새 현재 없지만 사용 가능 - Manufacturing
	private Map<String, List<ProductDescription>> mOutputCodeToPDescListMap = new HashMap<String, List<ProductDescription>>();
	
	// 쓰임새 없음
	private Map<String, List<Product>> mTypeToProductListMap = new HashMap<String, List<Product>>();
	
	// 쓰임새 없음
	private List<Product> mProductList = new ArrayList<Product>();
	
	private Map<String, Integer> mCodeToMaxTechMap = new HashMap<String, Integer>();
	
	/** 싱글턴 인스턴스 */
	private volatile static ProductManager sInstance;

	private ProductManager() {
	}

	public static ProductManager getInstance() {
		if(sInstance == null) {
			synchronized(ProductManager.class) {
				if(sInstance == null) {
					sInstance = new ProductManager();
				}
			}
		}
		return sInstance;
	}

	public void init() {
		loadProductType();
		loadProduct();
		loadRetailProduct();
		loadManufacturing();
		initCorporationProductData();
		initCityMarketOverseer();
	}

	private void loadProductType() {
		try {
			InputStream inputStream = Core.APP.getResources().openRawResource(R.raw.product_type);
			DBFReader reader = new DBFReader(inputStream);
			for(int i = 0; reader.hasNextRecord(); i++) {
				Object objects[] = reader.nextRecord(Charset.forName("UTF-8"));
				String type = objects[0].toString();
				if(type.isEmpty()) break;

				mTypeToNameMap.put(type, objects[1].toString());
				mProductTypeList.add(type);
			}
		} catch (JDBFException e) {
			e.printStackTrace();
		}
	}

	private void loadProduct() {
		try {
			InputStream inputStream = Core.APP.getResources().openRawResource(R.raw.product);
			DBFReader reader = new DBFReader(inputStream);
			for(int i = 0; reader.hasNextRecord(); i++) {
				Object objects[] = reader.nextRecord(Charset.forName("UTF-8"));
				if(objects[0].toString().isEmpty()) break;
				
				ProductDescription desc = new ProductDescription(objects[0].toString(),
						objects[1].toString(), objects[2].toString(), objects[3].toString(),
						Boolean.parseBoolean(objects[4].toString()), (Double) objects[5],
						(Long) objects[6], (Double) objects[7],	(Long) objects[8], 
						(Long) objects[9], (Long) objects[10],	(Long) objects[11], 
						(Long) objects[12]);

				mCodeToPDescMap.put(desc.code, desc);
				mPDescList.add(desc);
				mCodeToProductDataMap.put(desc.code, new ProductData(desc, i));
				mCodeToMaxTechMap.put(desc.code, INIT_MAX_TECH);

				List<ProductDescription> descList = mTypeToPDescListMap.get(desc.type);
				if(descList == null) {
					descList = new ArrayList<ProductDescription>();
					mTypeToPDescListMap.put(desc.type, descList);
				}
				descList.add(desc);
			}
		} catch (JDBFException e) {
			e.printStackTrace();
		}
	}

	private void loadRetailProduct() {
		try {
			InputStream inputStream = Core.APP.getResources().openRawResource(R.raw.retail_product);
			DBFReader reader = new DBFReader(inputStream);
			for(int i = 0; reader.hasNextRecord(); i++) {
				Object objects[] = reader.nextRecord(Charset.forName("UTF-8"));
				if(objects[0].toString().isEmpty()) break;

				List<String> typeList = mRetailCodeToProductTypeListMap.get(objects[0]);
				if(typeList == null) {
					typeList = new ArrayList<String>();
					mRetailCodeToProductTypeListMap.put(objects[0].toString(), typeList);
				}
				typeList.add(objects[1].toString());
			}
		} catch (JDBFException e) {
			e.printStackTrace();
		}
	}

	private void loadManufacturing() {
		try {
			InputStream inputStream = Core.APP.getResources().openRawResource(R.raw.manufacturing);
			DBFReader reader = new DBFReader(inputStream);
			for(int i = 0; reader.hasNextRecord(); i++) {
				Object objects[] = reader.nextRecord(Charset.forName("UTF-8"));
				if(objects[0].toString().isEmpty()) break;
				
				ManufacturingDescription desc = new ManufacturingDescription(objects[0].toString(),
						objects[1].toString(), (Long) objects[2], objects[3].toString(),
						(Long) objects[4], (Long) objects[5], objects[6].toString(),
						(Long) objects[7], (Long) objects[8], objects[9].toString(),
						(Long) objects[10], (Long) objects[11]);

				mMDescList.add(desc);
				mOutputCodeToMDescMap.put(desc.outputCode, desc);

				List<ManufacturingDescription> descList1;

				descList1 = mTypeToMDescListMap.get(desc.type);
				if(descList1 == null) {
					descList1 = new ArrayList<ManufacturingDescription>();
					mTypeToMDescListMap.put(desc.type, descList1);
				}
				descList1.add(desc);

				if(!desc.inputCode1.isEmpty()) {
					descList1 = mInputCodeToMDescListMap.get(desc.inputCode1);
					if(descList1 == null) {
						descList1 = new ArrayList<ManufacturingDescription>();
						mInputCodeToMDescListMap
								.put(desc.inputCode1, descList1);
					}
					descList1.add(desc);
				}
				if(!desc.inputCode2.isEmpty()) {
					descList1 = mInputCodeToMDescListMap.get(desc.inputCode2);
					if(descList1 == null) {
						descList1 = new ArrayList<ManufacturingDescription>();
						mInputCodeToMDescListMap
								.put(desc.inputCode2, descList1);
					}
					descList1.add(desc);
				}
				if(!desc.inputCode3.isEmpty()) {
					descList1 = mInputCodeToMDescListMap.get(desc.inputCode3);
					if(descList1 == null) {
						descList1 = new ArrayList<ManufacturingDescription>();
						mInputCodeToMDescListMap
								.put(desc.inputCode3, descList1);
					}
					descList1.add(desc);
				}

				List<ProductDescription> descList2 = mOutputCodeToPDescListMap
						.get(desc.outputCode);
				if(descList2 == null) {
					descList2 = new ArrayList<ProductDescription>();
					mOutputCodeToPDescListMap.put(desc.outputCode, descList2);
				}

				ProductDescription productDesc;
				productDesc = mCodeToPDescMap.get(desc.inputCode1);
				if(productDesc != null && !mInputPDescList.contains(productDesc)) {
					mInputPDescList.add(productDesc);
					mInputCodeList.add(productDesc.code);
					descList2.add(productDesc);
				}
				productDesc = mCodeToPDescMap.get(desc.inputCode2);
				if(productDesc != null && !mInputPDescList.contains(productDesc)) {
					mInputPDescList.add(productDesc);
					mInputCodeList.add(productDesc.code);
					descList2.add(productDesc);
				}
				productDesc = mCodeToPDescMap.get(desc.inputCode3);
				if(productDesc != null && !mInputPDescList.contains(productDesc)) {
					mInputPDescList.add(productDesc);
					mInputCodeList.add(productDesc.code);
					descList2.add(productDesc);
				}
			}
		} catch (JDBFException e) {
			e.printStackTrace();
		}
	}

	private void initCorporationProductData() {
		List<Corporation> corporationList = CorporationManager.getInstance().getCorporationList();
		int n = corporationList.size();
		for(int i = 0; i < n; i++) {
			Corporation corp = corporationList.get(i);
			corp.initProductData();
		}
	}
	
	private void initCityMarketOverseer() {
		List<City> cityList = CityManager.getInstance().getCityList();
		int n = cityList.size();
		for(int i=0; i<n; i++) {
			City city = cityList.get(i);
			
			List<ProductDescription> descList = mPDescList;
			int m = descList.size();
			for(int j=0; j<m; j++) {
				MarketOverseer overseer = new MarketOverseer(descList.get(j), city);
				city.getMarketOverseerList().add(overseer);
			}
		}
	}

	public Product newProduct(ProductDescription desc) {
		Product product = new Product(desc);
		//product.desc = desc;

		// product.m_name = description.name;

		// TextureManager tm = Core.GRAPHICS.getTextureManager();
		// Texture imageTexture = tm.getTexture(R.drawable.atlas);
		// Texture fontTexture = tm.getTexture(R.drawable.font);

		//product.mNameLabel = mCodeToProductGroupMap.get(description.code).mNameLabel;

		//product.mImageRegion = mCodeToProductGroupMap.get(description.code).mImageRegion;

		// product.m_size = description.size;
		// product.m_daily_necessity = description.necessity;
		// product.m_basesales = 10; // 실제로는 size로 나눠야 한다.
		// product.m_baseprice = (int) description.price;

		return product;
	}
	
	public List<ProductDescription> getPDescList() {
		return mPDescList;
	}

	public List<String> getTypeListByRetailCode(String code) {
		return mRetailCodeToProductTypeListMap.get(code);
	}

	public List<ManufacturingDescription> getMDescListByType(String type) {
		return mTypeToMDescListMap.get(type);
	}

	public ManufacturingDescription getMDescByOutputCode(String code) {
		return mOutputCodeToMDescMap.get(code);
	}

	public List<ManufacturingDescription> getMDescListByInputCode(String code) {
		return mInputCodeToMDescListMap.get(code);
	}

	public List<ProductDescription> getInputPDescListByOutputCode(String code) {
		return mOutputCodeToPDescListMap.get(code);
	}

	public List<ProductDescription> getOutputPDescListByInputCodes(List<String> codeList) {

		List<ProductDescription> pdescList = new ArrayList<ProductDescription>();

		int n = codeList.size();
		for(int i = 0; i < n; i++) {
			String input = codeList.get(i);
			List<ManufacturingDescription> mdescList = mInputCodeToMDescListMap.get(input);
			if(mdescList == null) continue;
			
			int m = mdescList.size();
			for(int j = 0; j < m; j++) {
				ManufacturingDescription desc = mdescList.get(j);
				ProductDescription output = mCodeToPDescMap.get(desc.outputCode);
				if(pdescList.contains(output)) continue;
				
				if(containsNecessaryInputs(desc, codeList)) {
					pdescList.add(output);
				}
			}
		}

		return pdescList;
	}

	private boolean containsNecessaryInputs(ManufacturingDescription desc, List<String> inputList) {

		String input1 = desc.inputCode1;
		String input2 = desc.inputCode2;
		String input3 = desc.inputCode3;

		boolean contains = true;

		if(!input1.isEmpty() && !inputList.contains(input1)) contains = false;
		if(!input2.isEmpty() && !inputList.contains(input2)) contains = false;
		if(!input3.isEmpty() && !inputList.contains(input3)) contains = false;
		
		return contains;
	}

	public ProductDescription getPDescByCode(String code) {
		return mCodeToPDescMap.get(code);
	}
	
	public ProductData getProductDataByCode(String code) {
		return mCodeToProductDataMap.get(code);
	}

	public List<String> getInputCodeList() {
		return mInputCodeList;
	}

	public String getNameByType(String type) {
		return mTypeToNameMap.get(type);
	}

	public List<Product> getProductListByType(String type) {
		List<Product> productList = mTypeToProductListMap.get(type);
		if(productList == null) {
			if(!mProductTypeList.contains(type)) return null;
			
			productList = new ArrayList<Product>();
			mTypeToProductListMap.put(type, productList);
		}
		return productList;
	}

	public List<Product> getProductList() {
		return mProductList;
	}

	public void addProduct(Product product) {
		addProduct(product, null);
	}

	public void addProduct(Product prodcut, Corporation corporation) {
		getProductListByType(prodcut.desc.type).add(prodcut);
		mProductList.add(prodcut);
		// Owner에게도 여기서 처리한다.
		if(corporation != null) {
			corporation.addProduct(prodcut);
		}
	}

	public void removeProduct(Product product) {
		getProductListByType(product.desc.type).remove(product);
		mProductList.remove(product);
		// Owner에게도 여기서 처리한다.
		Corporation corp = product.producer;
		if(corp != null)
			corp.removeProduct(product);
	}

	public List<String> getProductTypeList() {
		return mProductTypeList;
	}

	public List<ManufacturingDescription> getMDescList() {
		return mMDescList;
	}
	
	public List<ProductDescription> getImportablePDescList() {
		List<ProductDescription> descList = new ArrayList<ProductDescription>();

		List<ProductDescription> list = mPDescList;

		for(int i = 0; i < list.size(); i++) {
			ProductDescription desc = list.get(i);
			if(desc.importable) {
				descList.add(desc);
			}
		}

		return descList;
	}
	
	public int getBrandScore(DisplayProduct display) {
		int BC = display.desc.brandConcern;
		int BR = display.getBrand().getTotalBrand();
		return BR * BC / 60;
	}
	
	public int getQualityScore(DisplayProduct display) {
		int QC = display.desc.qualityConcern;
		int QR = display.quality;
		return QR * QC / 60;
	}
	
	public int getPriceScore(DisplayProduct display) {
		int PC = display.desc.priceConcern;
		double PD = display.desc.price - display.price;
		return (int) (PD * PC / display.desc.price);
	}
	
	// Product의 총점을 구한다.
	public int getOverallScore(DisplayProduct display) {
		return getBrandScore(display) + getQualityScore(display) + getPriceScore(display);
	}
	
	public int getBrandScore(ProductDescription desc, int brand) {
		int BC = desc.brandConcern;
		int BR = brand;
		return BR * BC / 60;
	}
	
	public int getQualityScore(ProductDescription desc, int quality) {
		int QC = desc.qualityConcern;
		int QR = quality;
		return QR * QC / 60;
	}
	
	public int getPriceScore(ProductDescription desc, double price) {
		int PC = desc.priceConcern;
		double PD = desc.price - price;
		return (int) (PD * PC / desc.price);
	}
	
	public int getOverallScore(ProductDescription desc, int brand, int quality, double price) {
		return getBrandScore(desc, brand) + getQualityScore(desc, quality) + getPriceScore(desc, price);
	}
	
	public boolean compareProducts(Product product1, Product product2) {
		if(product1 == null || product2 == null) return false;
		return compareProducts(product1.desc, product1.producer, product2.desc, product2.producer);
	}
	
	public boolean compareProducts(ProductDescription desc1, Corporation producer1, ProductDescription desc2, Corporation producer2) {
		if(desc1 != desc2) return false;
		if(producer1 != producer2) return false;
		return true;
	}
	
	/** 운송비를 계산한다. 운송비는 고정비 + 거리에 비례하는 추가비용으로 구성된다. */
	public double calculateFreight(float distance, ProductDescription desc) {
		return desc.price/50 + desc.price*distance/500;
	}
	
	public int getMaxTechByCode(String code) {
		return mCodeToMaxTechMap.get(code);
	}
	
	/** 
	 * 기술이 발전된 경우 이 메서드를 호출하여 현재의 최고 기술수준보다 높으면 
	 * 업데이트 한다.
	 */
	public void updateMaxTech(String code, int newTech) {
		int oldTech = mCodeToMaxTechMap.get(code);
		if(newTech > oldTech) {
			mCodeToMaxTechMap.remove(code);
			mCodeToMaxTechMap.put(code, newTech);
		}
	}

	@Override
	public void dispose() {
		Map<String, ProductData> codeToDataMap = mCodeToProductDataMap;
		Object[] keys = codeToDataMap.keySet().toArray();
		int n = keys.length;
		for(int i = 0; i < n; i++) {
			String code = (String) keys[i];
			DLabel label = codeToDataMap.get(code).mNameLabel;
			label.setDisposable(true);
			label.dispose();
		}

		sInstance = null;
	}

	public static class ManufacturingDescription {
	
		public final String type;
	
		public final String outputCode;
	
		public final int outputQuantity;
	
		public final String inputCode1;
	
		public final int inputQuantity1;
	
		public final int inputQuality1;
	
		public final String inputCode2;
	
		public final int inputQuantity2;
	
		public final int inputQuality2;
	
		public final String inputCode3;
	
		public final int inputQuantity3;
	
		public final int inputQuality3;
		
		private ManufacturingDescription(String type, String outputCode, long outputQuantity,
				String inputCode1, long inputQuantity1, long inputQuality1, String inputCode2,
				long inputQuantity2, long inputQuality2, String inputCode3, long inputQuantity3,
				long inputQuality3) {
			this.type = type;
			this.outputCode = outputCode;
			this.outputQuantity = (int) outputQuantity;
			this.inputCode1 = inputCode1;
			this.inputQuantity1 = (int) inputQuantity1;
			this.inputQuality1 = (int) inputQuality1;
			this.inputCode2 = inputCode2;
			this.inputQuantity2 = (int) inputQuantity2;
			this.inputQuality2 = (int) inputQuality2;
			this.inputCode3 = inputCode3;
			this.inputQuantity3 = (int) inputQuantity3;
			this.inputQuality3 = (int) inputQuality3;
		}
		
		/** 인풋의 개수를 얻는다. */
		public int getNumInputs() {
			if(inputCode2.isEmpty()) return 1;
			if(inputCode3.isEmpty()) return 2;
			return 3;
		}
		
		public String getInputCode(int index) {
			switch(index) {
				case 0:		return inputCode1;
				case 1:		return inputCode2;
				case 2:		return inputCode3;
				default:
					throw new IllegalArgumentException("index range should be from 0 to 2");
			}
		}
		
		public int getInputQuality(int index) {
			switch(index) {
				case 0:		return inputQuality1;
				case 1:		return inputQuality2;
				case 2:		return inputQuality3;
				default:
					throw new IllegalArgumentException("index range should be from 0 to 2");
			}
		}
		
		public int getInputQuantity(int index) {
			switch(index) {
				case 0:		return inputQuantity1;
				case 1:		return inputQuantity2;
				case 2:		return inputQuantity3;
				default:
					throw new IllegalArgumentException("index range should be from 0 to 2");
			}
		}
		
	}

	public static class ProductDescription {

		public final String type;
	
		public final String code;
	
		public final String name;
	
		public final String image;
	
		public final boolean importable;
	
		public final double price;
	
		/** 생필품지수(1~9) */
		public final int necessity;
	
		public final double demand;
	
		public final int priceConcern;
	
		public final int qualityConcern;
	
		public final int brandConcern;
	
		public final int inventSince;
	
		public final int inventPeriod;
		
		private ProductDescription(String type, String code, String name, String image,
				boolean importable, double price, long necessity, double demand,
				long priceConcern, long qualityConcern, long brandConcern, long inventSince,
				long inventPeriod) {
			this.type = type;
			this.code = code;
			this.name = name;
			this.image = image;
			this.importable = importable;
			this.price = price;
			this.necessity = (int) necessity;
			this.demand = demand;
			this.priceConcern = (int) priceConcern;
			this.qualityConcern = (int) qualityConcern;
			this.brandConcern = (int) brandConcern;
			this.inventSince = (int) inventSince;
			this.inventPeriod = (int) inventPeriod;
		}
		
		/** 제품이 소비될 수 있는지, 즉 완제품인지를 조사한다. */
		public boolean isConsumable() {
			// 완제품은 생필품지수 또는 요구량이 0이 아니다. 여기서는 요구량을 조사한다.
			return demand != 0;
		}
		
	}
	
	public static class ProductData {
		
		public ProductDescription desc;
		
		public int index;
		
		private DLabel mNameLabel;
		
		private TextureRegion mImageRegion;
		
		public ProductData(ProductDescription desc, int index) {
			this.desc = desc;
			this.index= index;  
			
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture productTexture = tm.getTexture(R.drawable.atlas_product);
			Texture fontTexture = tm.getTexture(R.drawable.font);
			
			mNameLabel = new DLabel(desc.name, fontTexture, Utils.sOutlineWhite12).setDisposable(false);
			mImageRegion = productTexture.getTextureRegion(desc.image);
		}
		
		public DLabel getNameLabel() {
			return mNameLabel;
		}
		
		public TextureRegion getImageRegion() {
			return mImageRegion;
		}
	}
	
}
