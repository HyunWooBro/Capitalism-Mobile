package project.game.corporation;

public class PlayerCorporation extends UserCorporation {

	public void Initialize(double cash) {
		/*
		 * this.cash = cash; //mAnnualNetprofit = 50000000L; annualNetprofit =
		 * 0; monthlyNetprofitArray = new long[Time.NUM_MONTHS];
		 * 
		 * 
		 * 
		 * monthlyOperatingRevenueArray = new long[Time.NUM_MONTHS];
		 * 
		 * monthlySalesCostArray = new long[Time.NUM_MONTHS];
		 * 
		 * monthlySalariesExpensesArray = new long[Time.NUM_MONTHS];
		 * 
		 * monthlyOperatingOverheadArray = new long[Time.NUM_MONTHS];
		 * 
		 * monthlyAdvertisingSpendingArray = new long[Time.NUM_MONTHS];
		 * 
		 * monthlyWelfareExpensesArray = new long[Time.NUM_MONTHS];
		 * 
		 * monthlyLoanInterestArray = new long[Time.NUM_MONTHS];
		 * 
		 * 
		 * //m_num_employee = 0;
		 * 
		 * lastNetprofit = 0;
		 * 
		 * //m_netprofit_index = 0;
		 * 
		 * maxNetprofit = 0;
		 * 
		 * //m_brand = 0;
		 * 
		 * loan = 0; creditLimit = 1000000;
		 */

		mFinancialData = new FinancialData();
		mFinancialData.cash = cash;
	}

	public static void InitStatic() {
		// m_retaillist = new ArrayList<Retail>();
		// m_factorylist = new ArrayList<Factory>();
		// m_farmlist = new ArrayList<Farm>();
		// m_RnDlist = new ArrayList<RND>();
	}

	/*
	 * public void calculateMaxGraphPoint() {
	 * 
	 * long[] monthlyNetprofitArray = this.monthlyNetprofitArray;
	 * 
	 * long max = Math.abs(monthlyNetprofitArray[0]); for(int i=1; i<12; i++) {
	 * long netProfit = Math.abs(monthlyNetprofitArray[i]); if(max < netProfit)
	 * max = netProfit; }
	 * 
	 * if(max < 1000) { maxNetprofit = 1000;
	 * 
	 * } else { max /= 1000; maxNetprofit = (max + 1) * 1000; } }
	 */

}
