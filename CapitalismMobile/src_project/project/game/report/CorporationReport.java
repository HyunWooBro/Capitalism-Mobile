package project.game.report;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.game.GameScene;
import project.game.Time;
import project.game.Time.DayListener;
import project.game.building.Building;
import project.game.building.Factory;
import project.game.building.Retail;
import project.game.building.department.DepartmentManager;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.corporation.PlayerCorporation;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.ShapeRenderer.ShapeType;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.DataTable;
import core.scene.stage.actor.widget.table.DataTable.ExtraBackground;
import core.scene.stage.actor.widget.table.DataTable.TableStyle;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.button.PushButton;

public class CorporationReport extends BaseReport implements DayListener {

	private PushButton mButton;

	public CorporationReport() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		itemList.add(new DLabel("기업 요약", fontTexture).setUserObject(new CorporationSummaryReport()));
		// itemList.add(new DLabel("기업 상세", fontTexture).setUserObject(new
		// CorporationDetailReport()));

		init(itemList);
	}

	@Override
	public PushButton getMenuButton() {
		if(mButton == null) {
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_corporation_report);
			// mButton.setColor(Color4.LTRED4);
		}
		return mButton;
	}

	@Override
	public Actor<?> getContent() {
		return this;
	}

	private static class CorporationSummaryReport extends Table<CorporationSummaryReport> implements
			Report {

		private CLabel mCashLabel;
		private CLabel mAnnualSalesLabel;
		private CLabel mAnnualNetprofitLabel;

		private CLabel mTotalEmployeesLabel;
		private CLabel mTotalBuildingsLabel;
		private CLabel mTotalRetailsLabel;
		private CLabel mTotalFactoriesLabel;
		private CLabel mTotalRNDsLabel;

		public CorporationSummaryReport() {

			CastingDirector cd = CastingDirector.getInstance();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mCashLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mAnnualSalesLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mAnnualNetprofitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			mTotalEmployeesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mTotalBuildingsLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mTotalRetailsLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);
			mTotalFactoriesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mTotalRNDsLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);

			DataTable data = new DataTable();

			TableStyle style = new TableStyle();
			style.showLine = false;
			style.backgroundColor = new Color4(255, 189, 207, 247);
			ExtraBackground background = new ExtraBackground();
			background.color = new Color4(255, 222, 227, 247);
			background.offset = 1;
			style.addExtraBackground(background);
			data.setTableStyle(style);

			data.all().padTop(2f).padBottom(1f).padLeft(5f).padRight(5f).right();

			data.col(0).cellWidth(100f);
			data.col(1).cellWidth(130f);

			data.addCell(new DLabel("현금", fontTexture));
			data.addCell(mCashLabel);
			data.row();
			data.addCell(new DLabel("연간 매출", fontTexture));
			data.addCell(mAnnualSalesLabel);
			data.row();
			data.addCell(new DLabel("연간 순이익", fontTexture));
			data.addCell(mAnnualNetprofitLabel);
			data.row();
			data.addCell(new DLabel("총 사원 수", fontTexture));
			data.addCell(mTotalEmployeesLabel);
			data.row();
			data.addCell(new DLabel("총 사업체 개수", fontTexture));
			data.addCell(mTotalBuildingsLabel);
			data.row();
			data.addCell(new DLabel("총 소매점 개수", fontTexture));
			data.addCell(mTotalRetailsLabel);
			data.row();
			data.addCell(new DLabel("총 공장 개수", fontTexture));
			data.addCell(mTotalFactoriesLabel);
			data.row();
			data.addCell(new DLabel("총 연구소 개수", fontTexture));
			data.addCell(mTotalRNDsLabel);
			data.row();

			addCell(data);
			addCell(new GraphUI()).padLeft(15f);
		}

		@Override
		public void update(long time) {
			super.update(time);

			PlayerCorporation player = (PlayerCorporation) CorporationManager.getInstance()
					.getPlayerCorporation();
			FinancialData financialData = player.getFinancialData();

			mCashLabel.setText(Utils.toCash(financialData.cash));

			long sum = 0;
			for(int i = 0; i < Time.NUM_MONTHS; i++)
				sum += financialData.monthlyOperatingRevenueArray[i];

			mAnnualSalesLabel.setText(Utils.toCash(sum));

			mAnnualNetprofitLabel.setText(Utils.toCash(financialData.annualNetprofit));

			int totalEmployees = 0;
			int totalRetails = 0;
			int totalFactories = 0;
			int totalRNDs = 0;

			// 사실 이 계산은 미리 할 수 있다. 부서 추가와 건물 건설로 나누어서
			List<Building> buildingList = player.getBuildingList();
			int totalBuildings = buildingList.size();
			for(int i = 0; i < totalBuildings; i++) {
				Building building = buildingList.get(i);
				totalEmployees += building.getTotalEmployees();
				if(building instanceof Retail)
					totalRetails++;
				else if(building instanceof Factory)
					totalFactories++;
				else
					totalRNDs++;
			}

			mTotalEmployeesLabel.setText(Utils.toString(totalEmployees));
			mTotalBuildingsLabel.setText("" + totalBuildings);
			mTotalRetailsLabel.setText("" + totalRetails);
			mTotalFactoriesLabel.setText("" + totalFactories);
			mTotalRNDsLabel.setText("" + totalRNDs);
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

	private static class CorporationDetailReport extends Table<CorporationDetailReport> implements
			Report {

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

	private static class GraphUI extends Table<GraphUI> {

		private SLabel mBusinessNetprofitLabel;
		private SLabel mPast12MonthsLabel;
		private CLabel mNetprofitLabel;

		public GraphUI() {
			TextureManager tm = Core.GRAPHICS.getTextureManager();

			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			TextureRegion netprofitPopupRegion = imageTexture
					.getTextureRegion("ui_building_netprofit_popup_info");
			setDrawable(Drawable.newDrawable(netprofitPopupRegion));

			mBusinessNetprofitLabel = new SLabel(R.string.label_corporation_netprofit, fontTexture)
					.moveTo(20f, 20f);
			mPast12MonthsLabel = new SLabel(R.string.label_business_past_12months, fontTexture)
					.moveTo(20f, 100f);

			mNetprofitLabel = new CLabel("", R.array.label_array_outline_white_10, fontTexture);
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			if(!(Director.getInstance().getCurrentScene() instanceof GameScene))
				return;
			super.draw(batch, parentAlpha);
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			super.drawChildren(batch, parentAlpha);

			batch.flush();

			mBusinessNetprofitLabel.draw(batch, parentAlpha);
			mPast12MonthsLabel.draw(batch, parentAlpha);

			ShapeRenderer renderer = DepartmentManager.sShapeRenderer;

			renderer.setProjectionMatrix(batch.getProjectionMatrix());
			renderer.begin(ShapeType.FILLED);
			renderer.setTransformMatrix(batch.peekTransformMatrix());

			renderer.setColor(Color4.WHITE);
			// X축을 출력
			renderer.drawRectLine(15f, 50f, 125f, 50f, 1);

			renderer.setColor(Color4.RED);

			FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation().getFinancialData();
			int maxNetprofit = financialData.maxNetprofit;

			if(maxNetprofit != 0) {
				// 가장 오른쪽에 가장 최근의 수치가 출력되고 가장 왼쪽에는 가장 오래된 수치가 출력된다.
				// 현재 인덱스의 다음 인덱스부터 시작한다. 왜냐하면 그것이 가장 오랜된 기록이기 때문이다.
				int index = (Time.getInstance().getMonthlyArrayIndex() + 1) % 12;
				// 초기의 시작 높이는 그래프에는 보이지 않는 바로 이전 높이를 참조하여 계산한다.
				float prevHeight = (float) (25 * financialData.lastNetprofit / maxNetprofit * 2);

				for(int i = 0; i < Time.NUM_MONTHS; i++) {
					float height = (float) (25 * financialData.monthlyNetprofitArray[index]
							/ maxNetprofit * 2);

					// 현재 달의 수치를 직사각형 영역으로 출력
					if(i == 11) {
						renderer.drawRect(25 + 15 / 2 * i, 50, 15 / 2, -height / 2);

						// 지난 11개월의 수치를 꺽은 선으로 출력
					} else {
						renderer.drawRectLine(25 + 15 / 2 * i, 50 - prevHeight / 2,
								25 + 15 / 2 * (i + 1), 50 - height / 2, 2);

						// 현재의 마지막 높이를 다음의 시작 높이로 설정한다.
						prevHeight = height;

						index = (index + 1) % 12;
					}
				}
			}

			renderer.end();

			mNetprofitLabel.moveTo(20f, 35f);
			mNetprofitLabel.setText(Utils.toCash(financialData.maxNetprofit));
			mNetprofitLabel.draw(batch, parentAlpha);

			mNetprofitLabel.moveTo(20f, 75f);
			mNetprofitLabel.setText(Utils.toCash(-financialData.maxNetprofit));
			mNetprofitLabel.draw(batch, parentAlpha);
		}
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
	}

}
