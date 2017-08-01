package project.game.building.department.stock;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.CircularRedirectException;
import org.framework.R;

import project.game.building.department.Department;
import project.game.building.department.DepartmentManager;
import project.game.corporation.Corporation;
import project.game.product.Product;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductDescription;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.widget.label.SLabel;

/** {@link StockDepartment}는 재고를 다루는 부서다. */
public abstract class StockDepartment extends Department {
	
	/** 기본재고량이 레벨 당 {@value}씩 증가한다. */
	public static final float LEVEL_UP_EXPANTION_SCALE = 0.2f;
	
	public static final int INIT_WAITING_DAYS = 16;
	
	/** 임시로 사용할 재고를 다루는 부서 리스트 */
	protected static final List<StockDepartment> TMP_STOCK_DEPARTMENT_LIST = new ArrayList<StockDepartment>();

	protected static SLabel sDepartmentUtilizationLabel;
	protected static SLabel sDepartmentStockLabel;
	protected static SLabel sDepartmentResourceLabel;

	public static TextureRegion sDepartmentBrandBarRegion;
	public static TextureRegion sDepartmentSupplyBarRegion;
	public static TextureRegion sDepartmentDemandBarRegion;
	public static TextureRegion sDepartmentQualityBarRegion;
	public static TextureRegion sDepartmentPriceBarRegion;
	public static TextureRegion sDepartmentUtilizationBarRegion;

	protected static TextureRegion sDepartmentStockRegion;

	static {
		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture barTexture = tm.getTexture(R.drawable.atlas_department_bar_graph);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		sDepartmentUtilizationLabel = new SLabel(R.string.label_department_utilization, fontTexture);
		sDepartmentStockLabel = new SLabel(R.string.label_department_stock, fontTexture);
		sDepartmentResourceLabel = new SLabel(R.string.label_department_resource, fontTexture);

		sDepartmentBrandBarRegion = barTexture.getTextureRegion("department_brand_bar");
		sDepartmentSupplyBarRegion = barTexture.getTextureRegion("department_supply_bar");
		sDepartmentDemandBarRegion = barTexture.getTextureRegion("department_demand_bar");
		sDepartmentUtilizationBarRegion = barTexture.getTextureRegion("department_utilization_bar");
		sDepartmentQualityBarRegion = barTexture.getTextureRegion("department_quality_bar");
		sDepartmentPriceBarRegion = barTexture.getTextureRegion("department_price_bar");

		sDepartmentStockRegion = barTexture.getTextureRegion("department_stock");
	}

	/*package*/ //List<Department> mInDepartmentList = new ArrayList<Department>();

	/*package*/ //List<Department> mOutDepartmentList = new ArrayList<Department>();
	
	protected int mWaitingDays = INIT_WAITING_DAYS / 2;

	public int mCurrStock;
	public int mMaxStock;
	public int mMaxWorkPerDay;

	protected Product mProduct;

	StockDepartment(int index, DepartmentManager departmentManager) {
		super(index, departmentManager);
	}
	
	public Product getProduct() {
		return mProduct;
	}
	
	/** 
	 * 일반적으로 제품을 제공하는 부서는 한번에 하나이지만 제조부와 같은 
	 * 특수한 경우에는 한번에 세 개의 부서가 공급할 수도 있기 때문에 리스트로 
	 * 처리한다. 외부에 공급처가 있는 구매부나 부서 자체내에서 생산하는 
	 * 사육부, 채굴부 등과는 관련 없다.
	 */
	protected List<StockDepartment> getSupplyDepartmentList() {
		List<StockDepartment> stockDepartmentList = TMP_STOCK_DEPARTMENT_LIST;
		stockDepartmentList.clear();
		
		int maxStock = 0;
		StockDepartment supplier = null;

		List<Department> departmentList = getConnectedDepartmentList();
		int n = departmentList.size();
		for(int i = 0; i < n; i++) {
			Department department = departmentList.get(i);
			if(department instanceof Sales) continue;
			
			if(department instanceof StockDepartment) {
				StockDepartment stock = (StockDepartment) department;
				Product product = stock.getProduct();
				if(product == null) continue;
				
				if(ProductManager.getInstance().compareProducts(product, mProduct)) {
					if(stock.mCurrStock > maxStock) {
						maxStock = stock.mCurrStock;
						supplier = stock;
					}
				}
			}
		}
		
		if(supplier != null) {
			stockDepartmentList.add(supplier);
			return stockDepartmentList;
		}
		
		// 공급 부서의 재고가 없고 현재 부서의 재고가 없을 경우 다른 대안 제품을 찾는다.
		if(mCurrStock == 0) {
			for(int i = 0; i < n; i++) {
				Department department = departmentList.get(i);
				if(department instanceof Sales) continue;
				
				if(department instanceof StockDepartment) {
					StockDepartment stock = (StockDepartment) department;
					Product product = stock.getProduct();
					if(product == null) continue;
					
					if(stock.mCurrStock != 0) {
						stockDepartmentList.add(stock);
						return stockDepartmentList;
					}
				}
			}
		}
		
		return stockDepartmentList;
	}
	
	/** 
	 * 기본적으로 다음의 조건에 해당하면 연결된 공급 부서에서 제품을 받는다.<br>
	 * 1. 대기일이 모두 지난 경우<br>
	 * 2. 공급 부서의 재고가 가득 차고 현재 부서의 재고가 바닥이 났을 경우</p> 
	 * 
	 * 대기일은 어떤 조건이든 상관 없이 제품이 이동한 후 초기화된다.</p>
	 * 
	 * 한편, 외부에 공급처가 있는 구매부나 부서 자체내에서 생산하는 
	 * 사육부, 채굴부 등과는 관련 없다.</p>
	 */
	protected void transferStock() {
		mWaitingDays--;
		if(mCurrStock != 0 && mWaitingDays > 0) return;
		
		List<StockDepartment> supplyDepartmentList = getSupplyDepartmentList();
		if(supplyDepartmentList.isEmpty()) return;
		
		StockDepartment supplier = supplyDepartmentList.get(0);
		if((supplier.mCurrStock == supplier.mMaxStock && mCurrStock == 0) || mWaitingDays <= 0) {
			
			if(ProductManager.getInstance().compareProducts(supplier.mProduct, mProduct)) {
				int transfer = Math.min(mMaxStock - mCurrStock, supplier.mCurrStock);
				if(transfer == 0) return;
				
				if(mProduct.quality != supplier.mProduct.quality) {
					mProduct.quality = mProduct.quality*mCurrStock + supplier.mProduct.quality*transfer; 
					mProduct.quality /= (mCurrStock + transfer);
				}
				
				if(mProduct.cost != supplier.mProduct.cost) {
					mProduct.cost = mProduct.cost*mCurrStock + supplier.mProduct.cost*transfer; 
					mProduct.cost /= (mCurrStock + transfer);
				}
				
				if(mProduct.freight != supplier.mProduct.freight) {
					mProduct.freight = mProduct.freight*mCurrStock + supplier.mProduct.freight*transfer; 
					mProduct.freight /= (mCurrStock + transfer);
				}
						
				mCurrStock += transfer;
				supplier.mCurrStock -= transfer;
			
			} else {
				confirmProduct(supplier.mProduct, mDepartmentManager.getBuilding().getCorporation());
				int transfer = Math.min(mMaxStock - mCurrStock, supplier.mCurrStock);
				if(transfer == 0) return;
				
				mCurrStock += transfer;
				supplier.mCurrStock -= transfer;
			}
			
			mWaitingDays = INIT_WAITING_DAYS;
		}
	}
	
	public void resetProduct() {
		mProduct = null;
		mCurrStock = 0;
	}
	
	/** 부서에서 다루어질 제품을 확정한다. */
	public void confirmProduct(Product product) {
		confirm(product.desc, product.producer, product.quality, product.cost, product.freight);
	}
	
	/** 부서에서 다루어질 제품을 확정한다. product와 다른 제조사를 지정할 수 있다. */
	public void confirmProduct(Product product, Corporation producer) {
		confirm(product.desc, producer, product.quality, product.cost, product.freight);
	}
	
	/** 부서에서 다루어질 제품을 확정한다. */
	public void confirmProduct(ProductDescription desc, Corporation producer, int quality, double cost, double freight) {
		confirm(desc, producer, quality, cost, freight);
	}
	
	protected void confirm(ProductDescription desc, Corporation producer, int quality, double cost, double freight) {
		mProduct = new Product(desc);
		mProduct.producer = producer;
		mProduct.productGroup = producer.getProductGroupByCode(desc.code);
		mProduct.quality = quality;
		mProduct.cost = cost;
		mProduct.freight = freight;
		mProduct.supplier = mDepartmentManager.getBuilding().getCorporation(); 
		mCurrStock = 0;
		computeStockCapacity();
		notifyDepartmentChange();
	}
	
	/** 
	 * 부서에서 다룰 제품이 아직 결정되지 않은 경우 연결된 부서 중에서 공급 부서가 
	 * 될 수 있는 부서를 찾는다. 외부에 공급처가 있는 구매부나 부서 자체내에서 생산하는 
	 * 사육부, 채굴부 등과는 관련 없다.
	 */
	protected void searchSupplier() {
		List<Department> departmentList = getConnectedDepartmentList();
		int n = departmentList.size();
		for(int i = 0; i < n; i++) {
			Department department = departmentList.get(i);
			if(department instanceof Sales) continue;
			
			if(department instanceof StockDepartment) {
				StockDepartment stock = (StockDepartment) department;
				if(stock.mCurrStock == 0) continue;
				Product product = stock.getProduct();
				if(product == null) continue;
				confirmProduct(product);
				return;
			}
		}
	}
	
	@Override
	protected void onLevelChanged() {
		computeStockCapacity();
	}
	
	protected void computeStockCapacity() {
		if(mProduct == null) return;
		int base = getBaseStock();
		base += ((mLevel-1)*base*LEVEL_UP_EXPANTION_SCALE);
		base /= mProduct.desc.price;
		// 깔끔하게 1의 자리는 버린다.
		//base = base/10*10;
		//base *= mProduct.desc.price;
		mMaxStock = base;
		mMaxWorkPerDay = (int) (mMaxStock * getMaxWorkScale());
	}
	
	protected int getBaseStock() {
		return 100000;
	}
	
	protected float getMaxWorkScale() {
		return 0.1f;
	}

}
