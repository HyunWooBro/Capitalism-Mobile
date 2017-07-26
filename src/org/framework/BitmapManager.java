package org.framework;

import org.game.*;

import android.graphics.*;
import android.util.*;

/**
 * 게임 내에서 사용하는 모든 이미지를 관리하는 클래스. 이미지를 한번만 불러오면 되기 때문에
 * 자원 관리가 효율적이고 깔끔하다. 
 * 
 * @author 김현준
 *
 */
public class BitmapManager {
	
	private SparseArray<Bitmap> mBitmapsByResourceId;

	// 싱글턴
	private static BitmapManager s_instance;
	private BitmapManager() {}
	public static BitmapManager GetInstance(){
		if(s_instance == null){
			s_instance = new BitmapManager();
		}
		return s_instance;
	}
	
	public void buildBitmapArray() {
		
		// 게임 내에서 사용하는 모든 이미지를 등록
		int[] bitmapIDs = new int[] 
				{
				/* 
				 * ***************************************************************** 메뉴 이미지
				 */
				R.drawable.mainmenu_background, 
				R.drawable.singleplaymenu_background,
				R.drawable.multiplaymenu_background,
				R.drawable.optionmenu_background,
				R.drawable.mainmenu_sign, 
				/* ***************************************************  */
				R.drawable.mainmenu_item1_n,
				R.drawable.mainmenu_item1_t, 
				R.drawable.mainmenu_item2_n,
				R.drawable.mainmenu_item2_t, 
				R.drawable.mainmenu_item3_n,
				R.drawable.mainmenu_item3_t,
				R.drawable.mainmenu_item4_n,
				R.drawable.mainmenu_item4_t,
				R.drawable.mainmenu_item5_n,
				R.drawable.mainmenu_item5_t,
				R.drawable.mainmenu_info_n, 
				R.drawable.mainmenu_info_t,
				R.drawable.mainmenu_info_flash,
				/* ***************************************************  */
				R.drawable.multiplaymenu_item1_n,
				R.drawable.multiplaymenu_item1_t,
				R.drawable.multiplaymenu_item2_n,
				R.drawable.multiplaymenu_item2_t,
				/* ***************************************************  */
				R.drawable.optionmenu_whiteboard,
				R.drawable.optionmenu_checkbox_yes,
				R.drawable.optionmenu_checkbox_no,
				/* ***************************************************  */
				R.drawable.menu_item_return_n,
				R.drawable.menu_item_return_t,
				/* ***************************************************  */
				R.drawable.menu_item_flash_n, 
				R.drawable.menu_item_flash_t,
				/* 
				 * ***************************************************************** 셀 이미지
				 */
				R.drawable.cell_road1,
				R.drawable.cell_ground1,
				R.drawable.cell_ground2,
				R.drawable.cell_house1,
				R.drawable.cell_house2,
				R.drawable.cell_2x2_house1,
				R.drawable.cell_2x2_apratment1,
				/* ***************************************************  */
				R.drawable.cell_road1_d,
				R.drawable.cell_ground1_d,
				R.drawable.cell_ground2_d,
				R.drawable.cell_house1_d,
				R.drawable.cell_house2_d,
				R.drawable.cell_2x2_house1_d,
				R.drawable.cell_2x2_apratment1_d,
				/* ***************************************************  */ 		
				R.drawable.cell_southeast_river1,
				R.drawable.cell_southeast_river2,
				R.drawable.cell_southbridge_river1,
				R.drawable.cell_southbridge_river2,
				R.drawable.cell_southwest_river2,
				R.drawable.cell_port,
				/* ***************************************************  */
				R.drawable.cell_southeast_river1_d,
				R.drawable.cell_southeast_river2_d,
				R.drawable.cell_southbridge_river1_d,
				R.drawable.cell_southbridge_river2_d,
				R.drawable.cell_southwest_river2_d,
				R.drawable.cell_port_d,
				/* ***************************************************  */
				R.drawable.cell_retail1,
				R.drawable.cell_factory1,
				R.drawable.cell_farm1,
				R.drawable.cell_laboratory1,
				/* ***************************************************  */
				R.drawable.cell_class_a,
				R.drawable.cell_class_b,
				R.drawable.cell_class_c,
				R.drawable.cell_class_d,
				R.drawable.cell_class_e,
				R.drawable.cell_class_f,
				/* ***************************************************  */
				R.drawable.cell_profit_positive_high,
				R.drawable.cell_profit_positive_medium,
				R.drawable.cell_profit_positive_low,
				R.drawable.cell_profit_negative_high,
				R.drawable.cell_profit_negative_medium,
				R.drawable.cell_profit_negative_low,
				R.drawable.cell_profit_none,
				/* ***************************************************  */
				R.drawable.twobytwo_blue,
				R.drawable.twobytwo_green,
				R.drawable.twobytwo_red,
				R.drawable.twobytwo_yellow,
				R.drawable.threebythree_blue,
				R.drawable.threebythree_green,
				R.drawable.threebythree_red,
				R.drawable.threebythree_yellow,
				/* 
				 * ***************************************************************** 게임 중 UI 이미지
				 */
				R.drawable.ui_bottom,
				R.drawable.ui_toolbar,				
				R.drawable.ui_toolbar_construction1,	
				/* ***************************************************  */
				R.drawable.ui_general_info,
				R.drawable.ui_choice_popup_info,
				R.drawable.ui_choice_popup_no_info,
				R.drawable.ui_choice_popup_yesno_info,
				/* ***************************************************  */
				R.drawable.cityicon_normal_up,
				R.drawable.cityicon_normal_down,
				R.drawable.cityicon_landvalue_up,
				R.drawable.cityicon_landvalue_down,
				R.drawable.cityicon_profit_up,
				R.drawable.cityicon_profit_down,
				R.drawable.cityicon_transaction_up,
				R.drawable.cityicon_transaction_down,
				};
		
		mBitmapsByResourceId = new SparseArray<Bitmap>();
		
		for (int i = 0; i < bitmapIDs.length; i++) 
		{
			Bitmap bitmap = AppManager.getInstance().getBitmap(bitmapIDs[i], Utility.sOptions);
			
			// 각 이미지의 ID를 key로 하여 해쉬로 저장
			mBitmapsByResourceId.put(bitmapIDs[i], bitmap);
		}
	}

	public Bitmap getBitmapByResourceId(int resouceId) {
		return mBitmapsByResourceId.get(resouceId);
	}
	
	public void clear()
	{
		
	}

}
