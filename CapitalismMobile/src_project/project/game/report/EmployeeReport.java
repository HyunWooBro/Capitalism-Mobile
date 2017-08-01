package project.game.report;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.game.building.Building;
import project.game.corporation.CorporationManager;
import project.game.corporation.PlayerCorporation;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.bar.SlidingBar;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.DataTable;
import core.scene.stage.actor.widget.table.DataTable.ExtraBackground;
import core.scene.stage.actor.widget.table.DataTable.TableStyle;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.table.button.CheckButton;
import core.scene.stage.actor.widget.table.button.PushButton;

public class EmployeeReport extends BaseReport {

	private PushButton mButton;

	public EmployeeReport() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		itemList.add(new DLabel("사원 요약", fontTexture).setUserObject(new EmployeeSummaryReport()));
		// itemList.add(new DLabel("사원 상세", fontTexture).setUserObject(new
		// EmployeeDetailReport()));

		init(itemList);
	}

	@Override
	public PushButton getMenuButton() {
		if(mButton == null)
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_employee_report);
		return mButton;
	}

	@Override
	public Actor<?> getContent() {
		return this;
	}

	private static class EmployeeSummaryReport extends Table<EmployeeSummaryReport> implements
			Report {

		private CheckButton mOvertimeButton;

		private SlidingBar mSalariesExpensesBar;

		private SlidingBar mWelfareExpensesBar;

		private TableCell mOverTimeCell;

		private TableCell mWelfareExpensesCell;

		private TableCell mSalariesExpensesCell;

		private CLabel mTotalEmployeesLabel;

		public EmployeeSummaryReport() {
			CastingDirector cd = CastingDirector.getInstance();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			final Texture fontTexture = tm.getTexture(R.drawable.font);

			mTotalEmployeesLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			mOvertimeButton = cd.cast(CheckButton.class, "default").setChecked(false)
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							if(mOvertimeButton.isChecked()) {
								mOverTimeCell.setActor(new DLabel("예", fontTexture));
							} else
								mOverTimeCell.setActor(new DLabel("아니오", fontTexture));

						}
					});

			mSalariesExpensesBar = cd.cast(SlidingBar.class, "default").setRange(0f, 6f)
					.setValue(3f).addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							switch((int) mSalariesExpensesBar.getValue()) {
								case 0:
									mSalariesExpensesCell.setActor(new DLabel("최하", fontTexture));
									break;
								case 1:
									mSalariesExpensesCell.setActor(new DLabel("하위", fontTexture));
									break;
								case 2:
									mSalariesExpensesCell.setActor(new DLabel("평균이하", fontTexture));
									break;
								case 3:
									mSalariesExpensesCell.setActor(new DLabel("평균", fontTexture));
									break;
								case 4:
									mSalariesExpensesCell.setActor(new DLabel("평균이상", fontTexture));
									break;
								case 5:
									mSalariesExpensesCell.setActor(new DLabel("상위", fontTexture));
									break;
								case 6:
									mSalariesExpensesCell.setActor(new DLabel("최상", fontTexture));
									break;
							}
						}
					});

			mWelfareExpensesBar = cd.cast(SlidingBar.class, "default").setRange(0f, 6f)
					.setValue(3f).addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							switch((int) mWelfareExpensesBar.getValue()) {
								case 0:
									mWelfareExpensesCell.setActor(new DLabel("최하", fontTexture));
									break;
								case 1:
									mWelfareExpensesCell.setActor(new DLabel("하위", fontTexture));
									break;
								case 2:
									mWelfareExpensesCell.setActor(new DLabel("평균이하", fontTexture));
									break;
								case 3:
									mWelfareExpensesCell.setActor(new DLabel("평균", fontTexture));
									break;
								case 4:
									mWelfareExpensesCell.setActor(new DLabel("평균이상", fontTexture));
									break;
								case 5:
									mWelfareExpensesCell.setActor(new DLabel("상위", fontTexture));
									break;
								case 6:
									mWelfareExpensesCell.setActor(new DLabel("최상", fontTexture));
									break;
							}
						}
					});

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

			data.addCell(new DLabel("총 사원수", fontTexture));
			data.addCell(mTotalEmployeesLabel);
			data.row();
			data.addCell(new DLabel("전반적 만족도", fontTexture));
			data.addCell(new DLabel("평균", fontTexture));
			data.row();
			data.addCell(new DLabel("업계 평균대비 월급수준", fontTexture));
			mSalariesExpensesCell = data.addCell(new DLabel("평균", fontTexture));
			data.row();
			data.addCell(mSalariesExpensesBar).colSpan(2);
			data.row();
			data.addCell(new DLabel("업계 평균대비 복지수준", fontTexture));
			mWelfareExpensesCell = data.addCell(new DLabel("평균", fontTexture));
			data.row();
			data.addCell(mWelfareExpensesBar).colSpan(2);
			data.row();
			data.addCell(new DLabel("야근 여부", fontTexture));
			mOverTimeCell = data.addCell(new DLabel("아니오", fontTexture));
			data.addCell(mOvertimeButton).size(30f, 30f);

			addCell(data);

		}

		@Override
		public void update(long time) {
			super.update(time);

			PlayerCorporation player = (PlayerCorporation) CorporationManager.getInstance()
					.getPlayerCorporation();

			int totalEmployees = 0;

			// 사실 이 계산은 미리 할 수 있다. 부서 추가와 건물 건설로 나누어서
			List<Building> buildingList = player.getBuildingList();
			int totalBuildings = buildingList.size();
			for(int i = 0; i < totalBuildings; i++) {
				Building building = buildingList.get(i);
				totalEmployees += building.getTotalEmployees();
			}

			mTotalEmployeesLabel.setText(Utils.toString(totalEmployees));
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

	private static class EmployeeDetailReport extends Table<EmployeeDetailReport> implements Report {

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
