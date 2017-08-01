package project.framework;

import org.framework.R;

import core.framework.Core;
import core.framework.graphics.texture.FontTexture;
import core.framework.graphics.texture.ImageTexture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.widget.label.DLabel;

public class ResLoader {

	public static void load() {

		DLabel.sDefaultPaint.set(Utils.sOutlineWhite15);

		// 텍스쳐 관리자
		final TextureManager tm = Core.GRAPHICS.getTextureManager();

		// 텍스쳐를 등록하여 GPU 메모리에 로드한 후 텍스쳐맵 정보를 파싱하여 TextureRegion으로 저장한다.
		tm.addTexture(R.drawable.atlas, new ImageTexture(R.drawable.atlas)).parseTextureMap(
				R.raw.sprite_sheet_map);
		tm.addTexture(R.drawable.atlas_mainmenu, new ImageTexture(R.drawable.atlas_mainmenu))
				.parseTextureMap(R.raw.atlas_mainmenu);
		tm.addTexture(R.drawable.erer, new ImageTexture(R.drawable.erer)).parseTextureMap(
				R.raw.erer);
		tm.addTexture(R.drawable.temp, new ImageTexture(R.drawable.temp)).parseTextureMap(
				R.raw.temp);
		tm.addTexture(R.drawable.atlas_toolbar, new ImageTexture(R.drawable.atlas_toolbar))
				.parseTextureMap(R.raw.atlas_toolbar);
		tm.addTexture(R.drawable.iconbox, new ImageTexture(R.drawable.iconbox)).parseTextureMap(
				R.raw.iconbox);
		tm.addTexture(R.drawable.atlas_selector, new ImageTexture(R.drawable.atlas_selector))
				.parseTextureMap(R.raw.atlas_selector);
		tm.addTexture(R.drawable.atlas_department_bar_graph,
				new ImageTexture(R.drawable.atlas_department_bar_graph)).parseTextureMap(
				R.raw.atlas_department_bar_graph);
		tm.addTexture(R.drawable.atlas_window, new ImageTexture(R.drawable.atlas_window))
				.parseTextureMap(R.raw.atlas_window);
		tm.addTexture(R.drawable.atlas_bar, new ImageTexture(R.drawable.atlas_bar))
				.parseTextureMap(R.raw.atlas_bar);
		tm.addTexture(R.drawable.atlas_checkbutton, new ImageTexture(R.drawable.atlas_checkbutton))
				.parseTextureMap(R.raw.atlas_checkbutton);
		tm.addTexture(R.drawable.atlas_dropdownbox, new ImageTexture(R.drawable.atlas_dropdownbox))
				.parseTextureMap(R.raw.atlas_dropdownbox);
		tm.addTexture(R.drawable.atlas_report_manufacture,
				new ImageTexture(R.drawable.atlas_report_manufacture)).parseTextureMap(
				R.raw.atlas_report_manufacture);
		tm.addTexture(R.drawable.atlas_product, new ImageTexture(R.drawable.atlas_product))
				.parseTextureMap(R.raw.atlas_product);

		tm.addTexture(R.drawable.lock, new ImageTexture(R.drawable.lock));

		tm.addTexture(R.drawable.button_default, new ImageTexture(R.drawable.button_default));

		tm.addTexture(R.drawable.cloud, new ImageTexture(R.drawable.cloud));

		tm.addTexture(R.drawable.font, new FontTexture(R.drawable.font));

		tm.addTexture(R.drawable.font_etc, new FontTexture(R.drawable.font_etc));

		tm.addTexture(R.drawable.font_menu, new FontTexture(R.drawable.font_menu));

		Runnable runnable = null;

		runnable = new Runnable() {

			@Override
			public void run() {
				FontTexture texture = (FontTexture) tm.getTexture(R.drawable.font);

				// ********************************************************************
				// 정적 문자열
				// ************************************************************************************

				texture.addStringRegion(R.string.label_department_purchase, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_sales, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_advertise, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_manufacture, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_laboratory, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_livestock, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_process, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_empty, Utils.sOutlineWhite12);
				
				texture.addStringRegion(R.string.label_product_brand, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_product_quality, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_product_price, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_product_overall, Utils.sOutlineWhite15);
				
				texture.addStringRegion(R.string.label_current_product, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_average, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_department_level, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_num_employees, Utils.sOutlineWhite12);

				texture.addStringRegion(R.string.label_department_utilization, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_stock, Utils.sOutlineWhite12);
				texture.addStringRegion(R.string.label_department_resource, Utils.sOutlineWhite12);

				texture.addStringRegion(R.string.label_research_technology, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_stop_technology, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_clear, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_connect, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_select, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_supplier, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_clients, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_suspend, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_resume, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_internal_sale, Utils.sOutlineWhite15);
				
				texture.addStringRegion(R.string.label_department_tab, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_display_tab, Utils.sOutlineWhite15);
				
				texture.addStringRegion(R.string.label_city_name, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_population, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_economic_indicator, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_spending_level, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_salary_level, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_city_demo_name, Utils.sOutlineWhite15);
				
				texture.addStringRegion(R.string.label_city_ei_boom, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_ei_recovery, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_ei_normal, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_ei_recession, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_ei_depression, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_city_ei_panic, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_building_types, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_building_retail, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_building_factory, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_building_farm, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_building_rnd, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_building_port, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_business_netprofit, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_business_past_12months, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_corporation_netprofit, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_cash, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_netprofit, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_build_cost, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_landvalue, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_house_purchase, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_total_build_cost, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_building_maintenance, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_market_share, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_total_purchase_price, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_sale_price, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_new_sale_price, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_build, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_ok, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_check, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_no, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_cancel, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_return, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_confirm, Utils.sOutlineWhite15);

				texture.addStringRegion(R.string.label_corporation_report, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_employee_report, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_product_report, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_financial_report, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_person_report, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_goal_report, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_guide_report, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_financial_dealings, Utils.sOutlineWhite15);

				// ********************************************************************
				// 정적 문자열배열
				// ************************************************************************************

				texture.addStringArrayRegions(R.array.label_array_outline_white_15,
						Utils.sOutlineWhite15);
				texture.addStringArrayRegions(R.array.label_array_outline_red_15,
						Utils.sOutlineRed15);
				texture.addStringArrayRegions(R.array.label_array_outline_green_15,
						Utils.sOutlineGreen15);
				texture.addStringArrayRegions(R.array.label_array_outline_yellow_12,
						Utils.sOutlineYellow12);
				texture.addStringArrayRegions(R.array.label_array_outline_white_10,
						Utils.sOutlineWhite10);

				texture.addStringArrayRegions(R.array.label_array_outline_white_12,
						Utils.sOutlineWhite12);

				texture.addStringArrayRegions(R.array.label_array_black_15, Utils.sBlack15);
				texture.addStringArrayRegions(R.array.label_array_red_15, Utils.sRed15);
			}
		};

		tm.getTexture(R.drawable.font).addRunnable(runnable);
		runnable.run();

		runnable = new Runnable() {

			@Override
			public void run() {
				FontTexture texture = (FontTexture) tm.getTexture(R.drawable.font_menu);

				// ********************************************************************
				// 정적 문자열
				// ************************************************************************************

				texture.addStringRegion(R.string.label_menu_singleplay, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_menu_multiplay, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_menu_tutorial, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_menu_option, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_menu_exit, Utils.sOutlineWhite15);
				texture.addStringRegion(R.string.label_menu_return, Utils.sOutlineWhite15);
			}
		};

		tm.getTexture(R.drawable.font_menu).addRunnable(runnable);
		runnable.run();
	}

}
