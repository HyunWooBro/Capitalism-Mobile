package project.game.corporation;

import project.game.Time;

public class FinancialData {

	public double cash; // 현금

	public double annualNetprofit; // 연간순이익

	// ********************************************************************
	// 최근 12달의 수치
	// ************************************************************************************

	public double[] monthlyOperatingRevenueArray; // 최근 12달의 영업매출 배열
	public double[] monthlySalesCostArray; // 최근 12달의 원가비용 배열
	public double[] monthlySalariesExpensesArray; // 최근 12달의 임금지출 배열
	public double[] monthlyWelfareExpensesArray; // 최근 12달의 복지지출 배열
	public double[] monthlyOperatingOverheadArray; // 최근 12달의 유지비지출 배열
	public double[] monthlyAdvertisingSpendingArray; // 최근 12달의 광고지출 배열
	public double[] monthlyTrainingAndNewEquipmentArray; // 최근 12달의 훈련 및 신규장비 배열
	public double[] monthlyWirteOffsArray; // 최근 12달의 삭감 배열
	public double[] monthlyOperatingExpensesArray; // 최근 12달의 영업지출 배열
	public double[] monthlyOperatingProfitArray; // 최근 12달의 영업순이익 배열

	public double[] monthlyAssetValue; // 최근 12달의 자산가치 배열
	public double[] monthlyLoanInterestArray; // 최근 12달의 대출이자 배열
	public double[] monthlyOtherProfit; // 최근 12달의 기타순이익 배열

	public double[] monthlyNetprofitArray; // 최근 12달의 순이익 배열

	// ********************************************************************
	// 누적 수치
	// ************************************************************************************

	public double accumulatedOperatingRevenue; // 누적 영업매출
	public double accumulatedSalesCost; // 누적 원가비용
	public double accumulatedSalariesExpenses; // 누적 임금지출
	public double accumulatedWelfareExpenses; // 누적 복지지출
	public double accumulatedOperatingOverhead; // 누적 유지비지출
	public double accumulatedAdvertisingSpending; // 누적 광고지출
	public double accumulatedTrainingAndNewEquipment; // 누적 훈련 및 신규장비
	public double accumulatedWirteOffs; // 누적 삭감
	public double accumulatedOperatingExpenses; // 누적 영업지출
	public double accumulatedOperatingProfit; // 누적 영업순이익

	public double accumulatedAssetValue; // 누적 자산가치
	public double accumulatedLoanInerest; // 누적 대출이자
	public double accumulatedOtherProfit; // 누적 기타순이익

	public double accumulatedNetprofit; // 누적 순이익

	public int maxNetprofit; // 순이익 그래프의 현재 최고 수치($1,000 단위로 구분)

	public double lastNetprofit; // 사업체의 최근 12달 이전의 순이익

	// public static int m_brand; // 기업 브랜드
	// public Color m_color; // 기업 고유색
	// 등등

	// public double m_num_employee;

	public double loan; // 대출금
	public double creditLimit; // 신용 한도

	public FinancialData() {
		// this.cash = cash;
		// mAnnualNetprofit = 50000000L;
		// annualNetprofit = 0;

		monthlyOperatingRevenueArray = new double[Time.NUM_MONTHS];
		monthlySalesCostArray = new double[Time.NUM_MONTHS];
		monthlySalariesExpensesArray = new double[Time.NUM_MONTHS];
		monthlyWelfareExpensesArray = new double[Time.NUM_MONTHS];
		monthlyOperatingOverheadArray = new double[Time.NUM_MONTHS];
		monthlyAdvertisingSpendingArray = new double[Time.NUM_MONTHS];
		monthlyTrainingAndNewEquipmentArray = new double[Time.NUM_MONTHS];
		monthlyWirteOffsArray = new double[Time.NUM_MONTHS];
		monthlyOperatingExpensesArray = new double[Time.NUM_MONTHS];
		monthlyOperatingProfitArray = new double[Time.NUM_MONTHS];

		monthlyAssetValue = new double[Time.NUM_MONTHS];
		monthlyLoanInterestArray = new double[Time.NUM_MONTHS];
		monthlyOtherProfit = new double[Time.NUM_MONTHS];

		monthlyNetprofitArray = new double[Time.NUM_MONTHS];

		// m_num_employee = 0;

		// lastNetprofit = 0;

		// m_netprofit_index = 0;

		// maxNetprofit = 0;

		// m_brand = 0;

		// loan = 0;
		creditLimit = 1000000;
	}

	public void reset() {

		int index = Time.getInstance().getMonthlyArrayIndex();

		lastNetprofit = monthlyNetprofitArray[index];

		monthlyOperatingRevenueArray[index] = 0;
		monthlySalesCostArray[index] = 0;
		monthlySalariesExpensesArray[index] = 0;
		monthlyWelfareExpensesArray[index] = 0;
		monthlyOperatingOverheadArray[index] = 0;
		monthlyAdvertisingSpendingArray[index] = 0;
		monthlyTrainingAndNewEquipmentArray[index] = 0;
		monthlyWirteOffsArray[index] = 0;
		monthlyOperatingExpensesArray[index] = 0;
		monthlyOperatingProfitArray[index] = 0;

		monthlyAssetValue[index] = 0;
		monthlyLoanInterestArray[index] = 0;
		monthlyOtherProfit[index] = 0;

		monthlyNetprofitArray[index] = 0;

	}

	public void calculateMaxGraphPoint() {

		double[] monthlyNetprofitArray = this.monthlyNetprofitArray;

		double max = lastNetprofit;
		for(int i = 0; i < Time.NUM_MONTHS; i++) {
			double netProfit = Math.abs(monthlyNetprofitArray[i]);
			if(max < netProfit)
				max = netProfit;
		}

		if(max < 1000) {
			maxNetprofit = 1000;

		} else {
			maxNetprofit = (int) (max / 1000);
			maxNetprofit = (maxNetprofit + 1) * 1000;
		}
	}
}