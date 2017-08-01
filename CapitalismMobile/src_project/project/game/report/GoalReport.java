package project.game.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.game.Time;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.corporation.PlayerCorporation;
import project.game.event.EventManager;
import project.game.event.VictoryEvent;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.box.DropDownBox;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.DataTable;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.utils.Align.HAlign;

public class GoalReport extends BaseReport {

	private PushButton mButton;

	public GoalReport() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		itemList.add(new DLabel("목표 달성", fontTexture).setUserObject(new GoalAchievementReport()));
		// itemList.add(new DLabel("취득 점수", fontTexture));

		init(itemList);
	}

	@Override
	public PushButton getMenuButton() {
		if(mButton == null) {
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_goal_report);
			mButton.setColor(Color4.LTYELLOW4);
		}
		return mButton;
	}

	@Override
	public Actor<?> getContent() {
		return this;
	}

	private static class GoalAchievementReport extends DataTable implements Report {

		private CLabel mTotalLone;
		private CLabel mMonthlyInterest;
		private CLabel mCreditLimit;
		private CLabel mCityInterest;
		private CLabel mTempLoneLabel;
		private CLabel mTempRepayLabel;

		private CLabel mFinalNetprofitLabel;
		private CLabel mCurrNetprofitLabel;

		private CLabel mFinalCalendarLabel;
		private CLabel mCurrCalendarLabel;

		public GoalAchievementReport() {

			CastingDirector cd = CastingDirector.getInstance();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mFinalCalendarLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mCurrCalendarLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture);

			mFinalNetprofitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);
			mCurrNetprofitLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture);

			TableStyle style = new TableStyle();
			style.lineColor = null;
			ExtraLine line = new ExtraLine();
			line.style = ExtraLine.EL_BOTTOM;
			line.range = new Rectangle(0f, 0f, 0f, 1f);
			style.addExtraLine(line);
			style.backgroundColor = new Color4(255, 189, 207, 247);
			ExtraBackground background1 = new ExtraBackground();
			background1.color = new Color4(255, 222, 227, 247);
			background1.offset = 2;
			style.addExtraBackground(background1);
			ExtraGradationBackground background2 = new ExtraGradationBackground();
			background2.bottomLeftColor = new Color4(255, 100, 111, 247);
			background2.bottomRightColor = new Color4(255, 100, 111, 247);
			background2.topLeftColor = new Color4(255, 222, 227, 247);
			background2.topRightColor = new Color4(255, 222, 227, 247);
			background2.range = new Rectangle(0f, 0f, 0f, 1f);
			style.addExtraBackground(background2);
			setTableStyle(style);

			all().padTop(2f).padBottom(1f);

			col(0).cellWidth(90f);
			col(1).cellWidth(130f);
			col(2).cellWidth(130f);
			col(3).cellWidth(100f);

			// VictoryEvent 에서 한 아이템씩 얻어와서 출력하자. 다음처럼 하드코딩하면 안된다.

			addCell(new DLabel("항목", fontTexture));
			addCell(new DLabel("목표", fontTexture));
			addCell(new DLabel("현재", fontTexture));
			addCell(new DLabel("달성여부", fontTexture));
			row();
			addCell(new DLabel("최종날짜", fontTexture));
			addCell(mFinalCalendarLabel);
			addCell(mCurrCalendarLabel);
			addCell();
			row();
			if((EventManager.getInstance().getVictoryEvent().getCondition() & VictoryEvent.VC_NETPROFIT) != 0) {
				addCell(new DLabel("연간순이익", fontTexture));
				addCell(mFinalNetprofitLabel);
				addCell(mCurrNetprofitLabel);
				addCell(new SLabel(R.string.label_no, fontTexture));
			}

			// debugAll();
		}

		@Override
		public void update(long time) {
			super.update(time);

			FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
					.getFinancialData();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mFinalCalendarLabel.setText(String.format("%d년 %d월 %d일", Time.getInstance()
					.getDeadline().get(Calendar.YEAR),
					Time.getInstance().getDeadline().get(Calendar.MONTH) + 1, Time.getInstance()
							.getDeadline().get(Calendar.DAY_OF_MONTH)));

			mCurrCalendarLabel.setText(String.format("%d년 %d월 %d일", Time.getInstance()
					.getCalendar().get(Calendar.YEAR),
					Time.getInstance().getCalendar().get(Calendar.MONTH) + 1, Time.getInstance()
							.getCalendar().get(Calendar.DAY_OF_MONTH)));

			mFinalNetprofitLabel.setText(Utils.toCash(VictoryEvent.mAnnualNetprofit));
			PlayerCorporation player = (PlayerCorporation) CorporationManager.getInstance()
					.getPlayerCorporation();

			mCurrNetprofitLabel.setText(Utils.toCash(financialData.annualNetprofit));

			if(VictoryEvent.mAnnualNetprofit <= financialData.annualNetprofit) {
				getCellList().get(11).setActor(new SLabel(R.string.label_ok, fontTexture));
			} else {
				getCellList().get(11).setActor(new SLabel(R.string.label_no, fontTexture));
			}
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

}
