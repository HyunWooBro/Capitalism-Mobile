package project.game.ui;

import org.framework.R;

import project.framework.Utils;
import project.game.GameScene;
import project.game.Time;
import project.game.building.Building;
import project.game.building.department.DepartmentManager;
import project.game.cell.CellManager;
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
import core.scene.stage.actor.Group;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.SLabel;

public class CellSelectionUI extends Group<CellSelectionUI> {

	public CellSelectionUI() {
		addChild(new GraphUI());
	}

	private static class GraphUI extends Actor<GraphUI> {

		private TextureRegion mNetprofitPopupRegion;

		private SLabel mBusinessNetprofitLabel;
		private SLabel mPast12MonthsLabel;
		private CLabel mNetprofitLabel;

		public GraphUI() {
			TextureManager tm = Core.GRAPHICS.getTextureManager();

			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mNetprofitPopupRegion = imageTexture
					.getTextureRegion("ui_building_netprofit_popup_info");

			mBusinessNetprofitLabel = new SLabel(R.string.label_business_netprofit, fontTexture)
					.south().moveTo(570f, 120f);
			mPast12MonthsLabel = new SLabel(R.string.label_business_past_12months, fontTexture)
					.south().moveTo(570f, 200f);

			mNetprofitLabel = new CLabel("", R.array.label_array_outline_white_10, fontTexture);
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			if(!(Director.getInstance().getCurrentScene() instanceof GameScene))
				return;

			// GameScene screen = (GameScene)
			// Director.getInstance().getCurrentScene();
			CellManager mCellManager = CellManager.getInstance();

			Building building = mCellManager.getSelectedBuilding();
			if(building != null
					&& /* building.mBuildingType != null && */!building.getDescription().type
							.equals("Port")) {

				batch.draw(mNetprofitPopupRegion, 1000 / 2, 200 / 2);

				mBusinessNetprofitLabel.draw(batch, parentAlpha);
				mPast12MonthsLabel.draw(batch, parentAlpha);

				batch.flush();

				ShapeRenderer renderer = DepartmentManager.sShapeRenderer;

				renderer.setProjectionMatrix(batch.getProjectionMatrix());
				renderer.begin(ShapeType.FILLED);

				renderer.setColor(Color4.WHITE);
				// X축을 출력
				renderer.drawRectLine(1030 / 2, 300 / 2, 1250 / 2, 300 / 2, 1);

				renderer.setColor(Color4.RED);

				if(building.mMaxNetprofit != 0) {

					// 가장 오른쪽에 가장 최근의 수치가 출력되고 가장 왼쪽에는 가장 오래된 수치가 출력된다.
					// 현재 인덱스의 다음 인덱스부터 시작한다. 왜냐하면 그것이 가장 오랜된 기록이기 때문이다.
					int index = (Time.getInstance().getMonthlyArrayIndex() + 1) % 12;
					// 초기의 시작 높이는 그래프에는 보이지 않는 바로 이전 높이를 참조하여 계산한다.
					float prevHeight = (float) (25 * building.mLastNetprofit
							/ building.mMaxNetprofit * 2);

					for(int i = 0; i < Time.NUM_MONTHS; i++) {
						float height = (float) (25 * building.mMonthlyNetprofit[index]
								/ building.mMaxNetprofit * 2);

						// 현재 달의 수치를 직사각형 영역으로 출력
						if(i == 11) {
							renderer.drawRect(1050 / 2 + 15 / 2 * i, 300 / 2, 15 / 2, -height / 2);

							// 지난 11개월의 수치를 꺽은 선으로 출력
						} else {
							renderer.drawRectLine(1050 / 2 + 15 / 2 * i, 300 / 2 - prevHeight / 2,
									1050 / 2 + 15 / 2 * (i + 1), 300 / 2 - height / 2, 2);

							// 현재의 마지막 높이를 다음의 시작 높이로 설정한다.
							prevHeight = height;

							index = (index + 1) % 12;
						}
					}
				}

				renderer.end();

				mNetprofitLabel.moveTo(1050 / 2, 270 / 2);
				mNetprofitLabel.setText(Utils.toCash(building.mMaxNetprofit));
				mNetprofitLabel.draw(batch, parentAlpha);

				mNetprofitLabel.moveTo(1050 / 2, 350 / 2);
				mNetprofitLabel.setText(Utils.toCash(-building.mMaxNetprofit));
				mNetprofitLabel.draw(batch, parentAlpha);

			}
		}
	}
}
