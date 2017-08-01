package project.game.report;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.game.Time;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.ui.UIManager;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.absolute.ScaleTo;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.widget.WidgetGroup;
import core.scene.stage.actor.widget.box.DropDownBox;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.DataTable;
import core.scene.stage.actor.widget.table.button.PushButton;

public class FinancialReport extends BaseReport {

	private PushButton mButton;

	public FinancialReport() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		itemList.add(new DLabel("손익계산서", fontTexture)
				.setUserObject(new IncomeStatementsReport(this)));
		// itemList.add(new DLabel("대차대조표", fontTexture).setUserObject(new
		// BalanceSheetReport()));

		init(itemList);
		setupListener(itemList);
	}

	private void setupListener(List<Actor<?>> itemList) {

		float dropDownPadBottom = getDropDownPadBottom();

		float virtualWidth = Core.APP.getAppConfig().virtualWidth;
		float virtualHeight = Core.APP.getAppConfig().virtualHeight;

		float menuWidth = ReportManager.MENU_WIDTH;

		float contentWidth = ReportManager.CONTENT_WIDTH;
		float contentHeight = virtualHeight - UIManager.BOTTOM_UI_HEIGHT;

		float dropDownHeight = ((WidgetGroup<?>) getCellActor(0)).getPrefHeight();

		float padWidth = getPadWidth();
		float padHeight = getPadHeight();

		float pureContentWidth = contentWidth - padWidth;
		float pureContentHeight = contentHeight - (dropDownHeight + dropDownPadBottom + padHeight);

		float preX = menuWidth + getPadLeft();
		float preY = dropDownHeight + dropDownPadBottom + getPadTop();

		float pivotX = ((preX * pureContentWidth) / (virtualWidth - pureContentWidth))
				/ pureContentWidth;
		float pivotY = ((preY * pureContentHeight) / (contentHeight - pureContentHeight))
				/ pureContentHeight;

		final float scaleX = virtualWidth / pureContentWidth;
		final float scaleY = contentHeight / pureContentHeight;

		// 터치 했을 때 확대 및 축소를 위한 이벤트 리스너를 등록한다.
		int n = itemList.size();
		for(int i = 0; i < n; i++) {
			Actor<?> report = (Actor<?>) itemList.get(i).getUserObject();
			report.pivotTo(pivotX, pivotY);
			report.addEventListener(new GestureTouchListener() {

				private boolean magnified;

				@Override
				public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {
					if(!magnified) {
						listener.addAction(new ScaleTo(scaleX, scaleY, 250));
						magnified = true;
					} else {
						listener.addAction(new ScaleTo(1f, 1f, 250));
						magnified = false;
					}
				}
			});
		}
	}

	@Override
	public PushButton getMenuButton() {
		if(mButton == null) {
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_financial_report);
		}
		return mButton;
	}

	@Override
	public Actor<?> getContent() {
		return this;
	}

	/**
	 * 손익계산서
	 * 
	 * @author 김현우
	 */
	private static class IncomeStatementsReport extends DataTable implements Report {

		// 영업매출
		private CLabel mCurrentOperatingRevenueLabel;
		private CLabel mLastOperatingRevenueLabel;
		private CLabel mYTDOperatingRevenueLabel;
		private CLabel mLifetimeOperatingRevenueLabel;

		// 원가비용
		private CLabel mCurrentSalesCostLabel;
		private CLabel mLastSalesCostLabel;
		private CLabel mYTDSalesCostLabel;
		private CLabel mLifetimeSalesCostLabel;

		// 임금지출
		private CLabel mCurrentSalariesExpensesLabel;
		private CLabel mLastSalariesExpensesLabel;
		private CLabel mYTDSalariesExpensesLabel;
		private CLabel mLifetimeSalariesExpensesLabel;

		// 복지지출
		private CLabel mCurrentWelfareExpensesLabel;
		private CLabel mLastWelfareExpensesLabel;
		private CLabel mYTDWelfareExpensesLabel;
		private CLabel mLifetimeWelfareExpensesLabel;

		// 유지비지출
		private CLabel mCurrentOperatingOverheadLabel;
		private CLabel mLastOperatingOverheadLabel;
		private CLabel mYTDOperatingOverheadLabel;
		private CLabel mLifetimeOperatingOverheadLabel;

		// 광고지출
		private CLabel mCurrentAdvertisingSpendingLabel;
		private CLabel mLastAdvertisingSpendingLabel;
		private CLabel mYTDAdvertisingSpendingLabel;
		private CLabel mLifetimeAdvertisingSpendingLabel;

		// 훈련 및 신규장비
		private CLabel mCurrentTrainingAndNewEquipmentLabel;
		private CLabel mLastTrainingAndNewEquipmentLabel;
		private CLabel mYTDTrainingAndNewEquipmentLabel;
		private CLabel mLifetimeTrainingAndNewEquipmentLabel;

		// 삭감
		private CLabel mCurrentWriteOffsLabel;
		private CLabel mLastWriteOffsLabel;
		private CLabel mYTDWriteOffsLabel;
		private CLabel mLifetimeWriteOffsLabel;

		// 영업지출
		private CLabel mCurrentOperatingExpensesLabel;
		private CLabel mLastOperatingExpensesLabel;
		private CLabel mYTDOperatingExpensesLabel;
		private CLabel mLifetimeOperatingExpensesLabel;

		// 영업순이익
		private CLabel mCurrentOperatingProfitLabel;
		private CLabel mLastOperatingProfitLabel;
		private CLabel mYTDOperatingProfitLabel;
		private CLabel mLifetimeOperatingProfitLabel;

		// 자산가치
		private CLabel mCurrenAssetValueLabel;
		private CLabel mLastAssetValueLabel;
		private CLabel mYTDAssetValueLabel;
		private CLabel mLifetimeAssetValueLabel;

		// 대출이자
		private CLabel mCurrentLoanInterestLabel;
		private CLabel mLastLoanInterestLabel;
		private CLabel mYTDLoanInterestLabel;
		private CLabel mLifetimeLoanInterestLabel;

		// 기타순이익
		private CLabel mCurrentOtherProfitLabel;
		private CLabel mLastOtherProfitLabel;
		private CLabel mYTDOtherProfitLabel;
		private CLabel mLifetimeOtherProfitLabel;

		// 총순이익
		private CLabel mCurrentNetprofitLabel;
		private CLabel mLastNetprofitLabel;
		private CLabel mYTDNetprofitLabel;
		private CLabel mLifetimeNetprofitLabel;

		private DLabel mLifetimeLabel;

		public IncomeStatementsReport(FinancialReport financialReport) {

			CastingDirector cd = CastingDirector.getInstance();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mCurrentOperatingRevenueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastOperatingRevenueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDOperatingRevenueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeOperatingRevenueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 원가비용
			mCurrentSalesCostLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastSalesCostLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDSalesCostLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mLifetimeSalesCostLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 임금지출
			mCurrentSalariesExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastSalariesExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDSalariesExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeSalariesExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 복지지출
			mCurrentWelfareExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastWelfareExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDWelfareExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeWelfareExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 유지비지출
			mCurrentOperatingOverheadLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastOperatingOverheadLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDOperatingOverheadLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeOperatingOverheadLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);

			// 광고지출
			mCurrentAdvertisingSpendingLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);
			mLastAdvertisingSpendingLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDAdvertisingSpendingLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeAdvertisingSpendingLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);

			// 훈련 및 신규장비
			mCurrentTrainingAndNewEquipmentLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);
			mLastTrainingAndNewEquipmentLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);
			mYTDTrainingAndNewEquipmentLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);
			mLifetimeTrainingAndNewEquipmentLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);

			// 삭감
			mCurrentWriteOffsLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastWriteOffsLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDWriteOffsLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mLifetimeWriteOffsLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 영업지출
			mCurrentOperatingExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastOperatingExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDOperatingExpensesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeOperatingExpensesLabel = new CLabel(null,
					R.array.label_array_outline_white_15, fontTexture);

			// 영업순이익
			mCurrentOperatingProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastOperatingProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDOperatingProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeOperatingProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 자산가치
			mCurrenAssetValueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastAssetValueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDAssetValueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeAssetValueLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 대출이자
			mCurrentLoanInterestLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastLoanInterestLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDLoanInterestLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeLoanInterestLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 기타순이익
			mCurrentOtherProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastOtherProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDOtherProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLifetimeOtherProfitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			// 총순이익
			mCurrentNetprofitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mLastNetprofitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mYTDNetprofitLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mLifetimeNetprofitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			TableStyle style = new TableStyle();
			style.lineColor = null;
			ExtraLine line1 = new ExtraLine();
			line1.style = ExtraLine.EL_BOTTOM | ExtraLine.EL_HORIZONTAL;
			line1.range = new Rectangle(0f, 0f, 0f, 2f);
			style.addExtraLine(line1);
			ExtraLine line2 = new ExtraLine();
			line2.style = ExtraLine.EL_BOTTOM | ExtraLine.EL_HORIZONTAL;
			line2.range = new Rectangle(0f, 8f, 0f, 3f);
			style.addExtraLine(line2);
			ExtraLine line3 = new ExtraLine();
			line3.style = ExtraLine.EL_BOTTOM | ExtraLine.EL_HORIZONTAL;
			line3.range = new Rectangle(0f, 13f, 0f, 2f);
			style.addExtraLine(line3);
			style.backgroundColor = new Color4(255, 189, 207, 247);
			ExtraGradationBackground background1 = new ExtraGradationBackground();
			background1.bottomLeftColor = new Color4(255, 100, 111, 247);
			background1.bottomRightColor = new Color4(255, 100, 111, 247);
			background1.topLeftColor = new Color4(255, 222, 227, 247);
			background1.topRightColor = new Color4(255, 222, 227, 247);
			background1.range = new Rectangle(0f, 0f, 0f, 1f);
			style.addExtraBackground(background1);
			ExtraBackground background2 = new ExtraBackground();
			background2.color = new Color4(255, 222, 227, 247);
			background2.range = new Rectangle(0f, 2f, 0f, 7f);
			background2.offset = 1;
			style.addExtraBackground(background2);
			ExtraBackground background3 = new ExtraBackground();
			background3.color = new Color4(255, 222, 227, 247);
			background3.range = new Rectangle(0f, 11f, 0f, 3f);
			background3.offset = 1;
			style.addExtraBackground(background3);
			ExtraBackground background4 = new ExtraBackground();
			background4.color = new Color4(255, 247, 217, 217);
			background4.range = new Rectangle(0f, 10f, 0f, 1f);
			style.addExtraBackground(background4);
			ExtraBackground background5 = new ExtraBackground();
			background5.color = new Color4(255, 247, 217, 217);
			background5.range = new Rectangle(0f, 14f, 0f, 2f);
			background5.span = 2;
			style.addExtraBackground(background5);
			setTableStyle(style);

			all().padTop(2f).padBottom(1f).padRight(5f).right();

			col(0).cellWidth(85f).padLeft(5f);
			col(1).cellWidth(85f);
			col(2).cellWidth(85f);
			col(3).cellWidth(90f);
			col(4).cellWidth(95f);

			addCell(new DLabel("항목", fontTexture));
			addCell(new DLabel("이번달", fontTexture));
			addCell(new DLabel("지난달", fontTexture));
			addCell(new DLabel("연간", fontTexture));
			addCell(mLifetimeLabel = new DLabel("창업이래", fontTexture));
			row();
			addCell(new DLabel("영업매출", fontTexture));
			addCell(mCurrentOperatingRevenueLabel);
			addCell(mLastOperatingRevenueLabel);
			addCell(mYTDOperatingRevenueLabel);
			addCell(mLifetimeOperatingRevenueLabel);
			row();
			addCell(new DLabel("원가비용", fontTexture));
			addCell(mCurrentSalesCostLabel);
			addCell(mLastSalesCostLabel);
			addCell(mYTDSalesCostLabel);
			addCell(mLifetimeSalesCostLabel);
			row();
			addCell(new DLabel("임금지출", fontTexture));
			addCell(mCurrentSalariesExpensesLabel);
			addCell(mLastSalariesExpensesLabel);
			addCell(mYTDSalariesExpensesLabel);
			addCell(mLifetimeSalariesExpensesLabel);
			row();
			addCell(new DLabel("복지지출", fontTexture));
			addCell(mCurrentWelfareExpensesLabel);
			addCell(mLastWelfareExpensesLabel);
			addCell(mYTDWelfareExpensesLabel);
			addCell(mLifetimeWelfareExpensesLabel);
			row();
			addCell(new DLabel("유지비지출", fontTexture));
			addCell(mCurrentOperatingOverheadLabel);
			addCell(mLastOperatingOverheadLabel);
			addCell(mYTDOperatingOverheadLabel);
			addCell(mLifetimeOperatingOverheadLabel);
			row();
			addCell(new DLabel("광고지출", fontTexture));
			addCell(mCurrentAdvertisingSpendingLabel);
			addCell(mLastAdvertisingSpendingLabel);
			addCell(mYTDAdvertisingSpendingLabel);
			addCell(mLifetimeAdvertisingSpendingLabel);
			row();
			addCell(new DLabel("훈련 및 신규장비", fontTexture));
			addCell(mCurrentTrainingAndNewEquipmentLabel);
			addCell(mLastTrainingAndNewEquipmentLabel);
			addCell(mYTDTrainingAndNewEquipmentLabel);
			addCell(mLifetimeTrainingAndNewEquipmentLabel);
			row();
			addCell(new DLabel("삭감", fontTexture));
			addCell(mCurrentWriteOffsLabel);
			addCell(mLastWriteOffsLabel);
			addCell(mYTDWriteOffsLabel);
			addCell(mLifetimeWriteOffsLabel);
			row();
			addCell(new DLabel("영업지출", fontTexture));
			addCell(mCurrentOperatingExpensesLabel);
			addCell(mLastOperatingExpensesLabel);
			addCell(mYTDOperatingExpensesLabel);
			addCell(mLifetimeOperatingExpensesLabel);
			row();
			addCell(new DLabel("영업순이익", fontTexture));
			addCell(mCurrentOperatingProfitLabel);
			addCell(mLastOperatingProfitLabel);
			addCell(mYTDOperatingProfitLabel);
			addCell(mLifetimeOperatingProfitLabel);
			row();
			addCell(new DLabel("주식가치", fontTexture));
			addCell(new CLabel("$" + 0, R.array.label_array_outline_white_15, fontTexture));
			addCell(new CLabel("$" + 0, R.array.label_array_outline_white_15, fontTexture));
			addCell(new CLabel("$" + 0, R.array.label_array_outline_white_15, fontTexture));
			addCell(new CLabel("$" + 0, R.array.label_array_outline_white_15, fontTexture));
			row();
			addCell(new DLabel("자산가치", fontTexture));
			addCell(mCurrenAssetValueLabel);
			addCell(mLastAssetValueLabel);
			addCell(mYTDAssetValueLabel);
			addCell(mLifetimeAssetValueLabel);
			row();
			addCell(new DLabel("대출이자", fontTexture));
			addCell(mCurrentLoanInterestLabel);
			addCell(mLastLoanInterestLabel);
			addCell(mYTDLoanInterestLabel);
			addCell(mLifetimeLoanInterestLabel);
			row();
			addCell(new DLabel("기타순이익", fontTexture));
			addCell(mCurrentOtherProfitLabel);
			addCell(mLastOtherProfitLabel);
			addCell(mYTDOtherProfitLabel);
			addCell(mLifetimeOtherProfitLabel);
			row();
			addCell(new DLabel("총순이익", fontTexture));
			addCell(mCurrentNetprofitLabel);
			addCell(mLastNetprofitLabel);
			addCell(mYTDNetprofitLabel);
			addCell(mLifetimeNetprofitLabel);

			// debugAll();
		}

		@Override
		public void update(long time) {
			super.update(time);

			FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
					.getFinancialData();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mLifetimeLabel.setText("창업이래(" + Time.getInstance().getElapsedYear() + "년)");

			int index = Time.getInstance().getMonthlyArrayIndex();
			int prevIndex = index - 1;
			if(prevIndex < 0) prevIndex = 11;

			double sum;

			double TotalOperatingRevenue;
			double TotalOperatingExpenses;

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlyOperatingRevenueArray[i];
			}

			TotalOperatingRevenue = sum;

			// 영업매출
			mCurrentOperatingRevenueLabel.setText(Utils
					.toCash(financialData.monthlyOperatingRevenueArray[index]));
			mLastOperatingRevenueLabel.setText(Utils
					.toCash(financialData.monthlyOperatingRevenueArray[prevIndex]));
			mYTDOperatingRevenueLabel.setText(Utils.toCash(sum));
			mLifetimeOperatingRevenueLabel.setText(Utils
					.toCash(financialData.accumulatedOperatingRevenue));

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlySalesCostArray[i];
			}

			// 원가비용
			mCurrentSalesCostLabel.setText(Utils
					.toCash(financialData.monthlySalesCostArray[index]));
			mLastSalesCostLabel.setText(Utils
					.toCash(financialData.monthlySalesCostArray[prevIndex]));
			mYTDSalesCostLabel.setText(Utils.toCash(sum));
			mLifetimeSalesCostLabel.setText(Utils.toCash(financialData.accumulatedSalesCost));

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlySalariesExpensesArray[i];
			}

			// 임금지출
			mCurrentSalariesExpensesLabel.setText(Utils
					.toCash(financialData.monthlySalariesExpensesArray[index]));
			mLastSalariesExpensesLabel.setText(Utils
					.toCash(financialData.monthlySalariesExpensesArray[prevIndex]));
			mYTDSalariesExpensesLabel.setText(Utils.toCash(sum));
			mLifetimeSalariesExpensesLabel.setText(Utils
					.toCash(financialData.accumulatedSalariesExpenses));

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlyWelfareExpensesArray[i];
			}

			// 복지지출
			mCurrentWelfareExpensesLabel.setText(Utils
					.toCash(financialData.monthlyWelfareExpensesArray[index]));
			mLastWelfareExpensesLabel.setText(Utils
					.toCash(financialData.monthlyWelfareExpensesArray[prevIndex]));
			mYTDWelfareExpensesLabel.setText(Utils.toCash(sum));
			mLifetimeWelfareExpensesLabel.setText(Utils
					.toCash(financialData.accumulatedTrainingAndNewEquipment));

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlyOperatingOverheadArray[i];
			}

			// 유지비지출
			mCurrentOperatingOverheadLabel.setText(Utils
					.toCash(financialData.monthlyOperatingOverheadArray[index]));
			mLastOperatingOverheadLabel.setText(Utils
					.toCash(financialData.monthlyOperatingOverheadArray[prevIndex]));
			mYTDOperatingOverheadLabel.setText(Utils.toCash(sum));
			mLifetimeOperatingOverheadLabel.setText(Utils
					.toCash(financialData.accumulatedOperatingOverhead));

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlyAdvertisingSpendingArray[i];
			}

			// 광고지출
			mCurrentAdvertisingSpendingLabel.setText(Utils
					.toCash(financialData.monthlyAdvertisingSpendingArray[index]));
			mLastAdvertisingSpendingLabel.setText(Utils
					.toCash(financialData.monthlyAdvertisingSpendingArray[prevIndex]));
			mYTDAdvertisingSpendingLabel.setText(Utils.toCash(sum));
			mLifetimeAdvertisingSpendingLabel.setText(Utils
					.toCash(financialData.accumulatedAdvertisingSpending));

			// 훈련 및 신규장비
			mCurrentTrainingAndNewEquipmentLabel.setText("$" + 0);
			mLastTrainingAndNewEquipmentLabel.setText("$" + 0);
			mYTDTrainingAndNewEquipmentLabel.setText("$" + 0);
			mLifetimeTrainingAndNewEquipmentLabel.setText("$" + 0);

			// 삭감
			mCurrentWriteOffsLabel.setText("$" + 0);
			mLastWriteOffsLabel.setText("$" + 0);
			mYTDWriteOffsLabel.setText("$" + 0);
			mLifetimeWriteOffsLabel.setText("$" + 0);

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlyOperatingExpensesArray[i];
			}

			TotalOperatingExpenses = sum;

			// 영업지출
			mCurrentOperatingExpensesLabel.setText(Utils
					.toCash(financialData.monthlyOperatingExpensesArray[index]));
			mLastOperatingExpensesLabel.setText(Utils
					.toCash(financialData.monthlyOperatingExpensesArray[prevIndex]));
			mYTDOperatingExpensesLabel.setText(Utils.toCash(sum));
			mLifetimeOperatingExpensesLabel.setText(Utils
					.toCash(financialData.accumulatedOperatingExpenses));

			// 영업순이익
			mCurrentOperatingProfitLabel.setText(Utils
					.toCash(financialData.monthlyOperatingProfitArray[index]));
			mLastOperatingProfitLabel.setText(Utils
					.toCash(financialData.monthlyOperatingProfitArray[prevIndex]));
			mYTDOperatingProfitLabel.setText(Utils.toCash(TotalOperatingRevenue
					- TotalOperatingExpenses));
			mLifetimeOperatingProfitLabel.setText(Utils
					.toCash(financialData.accumulatedOperatingProfit));

			// 자산가치
			mCurrenAssetValueLabel.setText("$" + 0);
			mLastAssetValueLabel.setText("$" + 0);
			mYTDAssetValueLabel.setText("$" + 0);
			mLifetimeAssetValueLabel.setText("$" + 0);

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlyLoanInterestArray[i];
			}

			// 대출이자
			mCurrentLoanInterestLabel.setText(Utils
					.toCash(financialData.monthlyLoanInterestArray[index]));
			mLastLoanInterestLabel.setText(Utils
					.toCash(financialData.monthlyLoanInterestArray[prevIndex]));
			mYTDLoanInterestLabel.setText(Utils.toCash(sum));
			mLifetimeLoanInterestLabel.setText(Utils
					.toCash(financialData.accumulatedLoanInerest));

			sum = 0;
			for(int i = 0; i < 12; i++) {
				sum += financialData.monthlyOtherProfit[i];
			}

			// 기타순이익
			mCurrentOtherProfitLabel.setText(Utils
					.toCash(financialData.monthlyOtherProfit[index]));
			mLastOtherProfitLabel.setText(Utils
					.toCash(financialData.monthlyOtherProfit[prevIndex]));
			mYTDOtherProfitLabel.setText(Utils.toCash(sum));
			mLifetimeOtherProfitLabel.setText(Utils
					.toCash(financialData.accumulatedOtherProfit));

			// 총순이익
			mCurrentNetprofitLabel.setText(Utils
					.toCash(financialData.monthlyNetprofitArray[index]));
			mLastNetprofitLabel.setText(Utils
					.toCash(financialData.monthlyNetprofitArray[prevIndex]));
			mYTDNetprofitLabel.setText(Utils.toCash(financialData.annualNetprofit));
			mLifetimeNetprofitLabel.setText(Utils.toCash(financialData.accumulatedNetprofit));
		}

		@Override
		public void onShow() {
		}

		@Override
		public void onHide() {
		}

		@Override
		public PushButton getMenuButton() {
			return null;
		}

		@Override
		public Actor<?> getContent() {
			return null;
		}
	}

	/**
	 * 대차대조표
	 * 
	 * @author 김현우
	 */
	private static class BalanceSheetReport extends DataTable implements Report {

		@Override
		public void onShow() {
		}

		@Override
		public void onHide() {
		}

		@Override
		public PushButton getMenuButton() {
			return null;
		}

		@Override
		public Actor<?> getContent() {
			return null;
		}
	}

}
