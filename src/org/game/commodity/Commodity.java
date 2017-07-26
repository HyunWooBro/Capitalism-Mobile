package org.game.commodity;

import org.framework.*;

import android.graphics.*;

public class Commodity{
	public enum CommodityTypes {RAW_MATERIAL, INTERMEDIATE_MATERIAL, PRODUCT};
	public enum ProductTypes {GROCERIES, COSMETICS, JEWELRY, ELECTRONICS};
	
	public String m_name;										// 물품 이름
	public int m_daily_necessity;								// 생필품 지수 0 ~ 100
	public int m_size;												// 물품의 크기
	public int m_basesales;										// 하루에 고정적으로 판매되는 수량
	public int m_baseprice;
	public CommodityTypes m_commodity_type;		// 물품 타입(원자재, 반제품, 제품)
	public ProductTypes m_product_type;				// 물품 종류(식료품, 화장품 등등)
	public Bitmap m_commodity_bitmap;					// 물품 이미지
	
	public int quality[];											// 각 기업의 입장에서의 상품의 품질
	public int brand[];											// 각 기업의 입장에서의 상품의 브랜드
	
	public int m_index;
	
	//public CommodityManufacture m_commodity_manufacture;	// 물품에 대한 제품 정보

	public Commodity() {
		// TODO Auto-generated constructor stub
		quality = new int[1];
		brand = new int[1];
	}

}
