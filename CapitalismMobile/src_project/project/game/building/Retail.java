package project.game.building;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.game.Time;
import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.department.Department;
import project.game.building.department.Department.DepartmentType;
import project.game.building.department.DepartmentManager;
import project.game.building.department.stock.Purchase;
import project.game.building.department.stock.Sales;
import project.game.building.department.stock.StockDepartment;
import project.game.city.City;
import project.game.city.CityManager;
import project.game.city.MarketOverseer;
import project.game.product.Product;
import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductDescription;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.math.MathUtils;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.label.DLabel;

public class Retail extends Building {
	
	// 진열대
	private List<DisplayProduct> mDisplayProductList = new ArrayList<DisplayProduct>(6);

	/*package*/ Retail(BuildingDescription description, City city) {
		super(description, city);
	}

	public static Building newBuilding(BuildingDescription description, City city) {
		return new Retail(description, city);
	}

	@Override
	protected void setAvailableDepartmentList(List<DepartmentType> availableDepartmentList) {
		availableDepartmentList.add(DepartmentType.PURCHASE);
		availableDepartmentList.add(DepartmentType.SALES);
		// availableDepartmentList.add(DepartmentType.ADVERTISE);
		availableDepartmentList.add(DepartmentType.EMPTY);
	}

	@Override
	public Department newDepartment(int index, DepartmentType type) {
		switch(type) {
			case PURCHASE:
				return new RetailPurchase(index, getDepartmentManager());
			case SALES:
				return new RetailSales(index, getDepartmentManager());
				// case ADVERTISE: return new Advertise(index,
				// getDepartmentManager());
			case EMPTY:
				return null;
			default:
				throw new IllegalArgumentException(type.toString() + " of "
						+ DepartmentType.class.getSimpleName() + " is not allowed.");
		}
	}

	@Override
	public List<DisplayProduct> getDisplayProductList() {
		return mDisplayProductList;
	}
	
	private static class RetailPurchase extends Purchase {

		RetailPurchase(int index, DepartmentManager department_manager) {
			super(index, department_manager);
		}
		
		@Override
		protected double work() {
			return super.work();
		}

		@Override
		protected ProductFilter getProductFilter(int index) {
			return new RetailPurchaseFilter(index, mDepartmentManager);
		}

		@Override
		protected List<Actor<?>> getProductList() {
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture fontTexture = tm.getTexture(R.drawable.font);

			List<Actor<?>> nameList = new ArrayList<Actor<?>>();
			nameList.add(new DLabel("모든 제품", fontTexture));

			String retailCode = mDepartmentManager.getBuilding().getDescription().code;
			List<String> filterList = ProductManager.getInstance().getTypeListByRetailCode(
					retailCode);
			int n = filterList.size();
			for(int i = 0; i < n; i++) {
				String name = ProductManager.getInstance().getNameByType(filterList.get(i));
				nameList.add(new DLabel(name, fontTexture));
			}

			return nameList;
		}

		private static class RetailPurchaseFilter extends ProductFilter {

			private DepartmentManager mManager;

			private int mIndex;

			public RetailPurchaseFilter(int index, DepartmentManager manager) {
				mIndex = index;
				mManager = manager;
			}

			@Override
			public boolean filter(ProductDescription description) {
				String retailCode = mManager.getBuilding().getDescription().code;
				List<String> filterList = ProductManager.getInstance().getTypeListByRetailCode(
						retailCode);
				if(mIndex != 0) {
					filterList = new ArrayList<String>(filterList);
					String filter = filterList.get(mIndex - 1);
					filterList.clear();
					filterList.add(filter);
				}
				return filterList.contains(description.type);
			}
		}

	}

	private static class RetailSales extends Sales {
		
		double mPreMaximumSales;

		RetailSales(int index, DepartmentManager department_manager) {
			super(index, department_manager);
		}
		
		/*@Override
		protected int salesToCitizen() {

			int actual_work = 0;

			m_day++;
			m_day2++;

			double price_diffrence = 0;
			double maximum_sales = 0;
			// double sales_earning = 0;

			int economicIndicator = CityManager.getInstance().getCurrentCity().mEconomicIndicator;

			//if(mDepartmentManager.getBuilding().getDescription().type.equals("Retail")) {

				if(mRepresentative != null)
					mNewPrice = mRepresentative.mNewPrice;

				// price_diffrence = 100 - (mLinkedProduct.m_baseprice +
				// mLinkedProduct.m_baseprice/100 *
				// 0.1)*1.5/(mLinkedProduct.m_baseprice*2)*100;
				price_diffrence = (mLinkedProduct.mDescription.price + mLinkedProduct.mDescription.price * 1.0)
						- mNewPrice;

				double demand = mLinkedProduct.mDescription.demand;

				maximum_sales = 10 - mLinkedProduct.mDescription.necessity
						* -(50 - economicIndicator);
				maximum_sales *= demand;
				if(price_diffrence > 0)
					maximum_sales *= (1.0 + Math.pow(price_diffrence, 1.2)
							/ mLinkedProduct.mDescription.price);
				else
					maximum_sales *= (1.0 / (1.0 + Math.pow(-price_diffrence, 1.2)
							/ mLinkedProduct.mDescription.price));
				Core.APP.info("maximum_sales "
						+ (1.0 / (1.0 + Math.pow(price_diffrence, 1.2)
								/ mLinkedProduct.mDescription.price)));
				// maximum_sales *= Math.sqrt((
				// PlayerCorporation.mQuality[mLinkedProduct.m_index]-29));
				// maximum_sales *= (double)
				// PlayerCorporation.m_retaillist.get(i).m_average_landvalue/LandValue.CLASS_A.GetValue();
				maximum_sales *= MathUtils.random(8, 11);
				maximum_sales *= 100;

				Core.APP.debug("sales : " + maximum_sales);

				if(mMaxWorkPerDay < maximum_sales)
					actual_work = mMaxWorkPerDay;
				else
					actual_work = (int) maximum_sales;

				mDemand[m_day % 30] = (int) maximum_sales;

				if(actual_work > mCurrStock)
					actual_work = mCurrStock;

				mSupply[m_day % 30] = actual_work;

				mCurrStock -= actual_work;
				mSalesEarning = (mNewPrice) * actual_work / mLinkedProduct.mDescription.size;
			//}

			if(mDepartmentManager.getBuilding().getDescription().type.equals("Factory")) {

			}
			
			
			int index = Time.getInstance().getFinancialArrayIndex();
			
			monthlyRevenueArray[index] += (double) actual_work / mLinkedProduct.mDescription.size;
			

			return actual_work;
		}*/
		
		@Override
		protected int getBaseStock() {
			return 100000;
		}
		
		@Override
		protected float getMaxWorkScale() {
			return 0.1f;
		}

	}

	/*
	 * @Override public BuildingType getBuildingType() { return
	 * BuildingType.RETAIL; }
	 */

}
