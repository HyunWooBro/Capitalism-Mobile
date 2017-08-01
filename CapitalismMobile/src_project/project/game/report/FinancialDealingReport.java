package project.game.report;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.framework.Utils.MessageDialogData;
import project.game.city.CityManager;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.DataTable;
import core.scene.stage.actor.widget.table.button.Button.ButtonCostume;
import core.scene.stage.actor.widget.table.button.PushButton;

public class FinancialDealingReport extends BaseReport {

	private PushButton mButton;

	public FinancialDealingReport() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		itemList.add(new DLabel("대출/상환", fontTexture).setUserObject(new LoneReport()));
		// itemList.add(new DLabel("주식 거래", fontTexture).setUserObject(new
		// StockTradingReport()));

		init(itemList);
	}

	@Override
	public PushButton getMenuButton() {
		if(mButton == null) {
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_financial_dealings);
			mButton.setColor(Color4.LTYELLOW4);
		}
		return mButton;
	}

	@Override
	public Actor<?> getContent() {
		return this;
	}

	private static class LoneReport extends DataTable implements Report {

		private PushButton mOnePlusButton1;
		private PushButton mOneMinusButton1;
		private PushButton mDoublePlusButton1;
		private PushButton mDoubleMinusButton1;
		private PushButton mLoanButton;

		private PushButton mOnePlusButton2;
		private PushButton mOneMinusButton2;
		private PushButton mDoublePlusButton2;
		private PushButton mDoubleMinusButton2;
		private PushButton mRepayButton;

		private double mTempLone;
		private double mTempRepay;

		private CLabel mTotalLone;
		private CLabel mMonthlyInterest;
		private CLabel mCreditLimit;
		private CLabel mCityInterest;
		private CLabel mTempLoneLabel;
		private CLabel mTempRepayLabel;

		public LoneReport() {

			CastingDirector cd = CastingDirector.getInstance();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			ButtonCostume costume = new ButtonCostume();

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_plus_button"));
			mDoublePlusButton1 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewLone(5);
						}
					});

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("one_plus_button"));
			mOnePlusButton1 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewLone(1);
						}
					});

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("one_minus_button"));
			mOneMinusButton1 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewLone(-1);
						}
					});

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_minus_button"));
			mDoubleMinusButton1 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewLone(-5);
						}
					});

			mLoanButton = cd.cast(PushButton.class, "dynamic_text", "대출").addEventListener(
					new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							FinancialData financialData = CorporationManager.getInstance()
									.getPlayerCorporation().getFinancialData();
							financialData.loan += mTempLone;
							financialData.cash += mTempLone;
							mTempLone = 0;
							updateTempLone();
						}
					});

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_plus_button"));
			mDoublePlusButton2 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewRepay(5);
						}
					});

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("one_plus_button"));
			mOnePlusButton2 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewRepay(1);
						}
					});

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("one_minus_button"));
			mOneMinusButton2 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewRepay(-1);
						}
					});

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_minus_button"));
			mDoubleMinusButton2 = new PushButton(costume)
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewRepay(-5);
						}
					});

			mRepayButton = cd.cast(PushButton.class, "dynamic_text", "상환").addEventListener(
					new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							FinancialData financialData = CorporationManager.getInstance()
									.getPlayerCorporation().getFinancialData();
							if(financialData.cash < mTempRepay) {
								MessageDialogData data = new MessageDialogData();
								data.title = "알림";
								data.content = "상환을 위한 현금이 부족합니다.";
								Utils.showMessageDialog(getFloor().getStage(), "repay", data);

								mTempRepay = 0;
								updateTempRepay();
								return;
							}

							financialData.loan -= mTempRepay;
							financialData.cash -= mTempRepay;
							mTempRepay = 0;
							updateTempRepay();
						}
					});

			TableStyle style = new TableStyle();
			style.lineColor = null;
			ExtraLine line = new ExtraLine();
			line.style = ExtraLine.EL_BOTTOM;
			line.range = new Rectangle(0f, 3f, 0f, 1f);
			style.addExtraLine(line);
			style.backgroundColor = new Color4(255, 189, 207, 247);
			ExtraBackground background = new ExtraBackground();
			background.color = new Color4(255, 222, 227, 247);
			background.range = new Rectangle(0f, 0f, 0f, 4f);
			background.offset = 1;
			style.addExtraBackground(background);
			setTableStyle(style);

			all().padTop(2f).padBottom(1f).padLeft(5f);

			col(0).cellWidth(80f).left();
			col(1).cellWidth(100f).right();
			col(2).padLeft(5f);
			col(3).padLeft(5f);
			col(4).padLeft(5f);
			col(5).padLeft(5f);
			col(6).size(70f, 40f).padLeft(5f).padRight(5f);

			addCell(new DLabel("신용 한도", fontTexture));
			addCell(mCreditLimit = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture));
			row();
			addCell(new DLabel("연이자율", fontTexture));
			addCell(mCityInterest = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture));
			row();// .padTop(10f);
			addCell(new DLabel("총 대출금", fontTexture).setColor(Color4.LTRED4));
			addCell(mTotalLone = new CLabel(null, R.array.label_array_outline_white_15, fontTexture));
			row();
			addCell(new DLabel("월간 이자", fontTexture).setColor(Color4.DKYELLOW4));
			addCell(mMonthlyInterest = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture));
			row()/* .padTop(10f) */;
			addCell(new DLabel("대출", fontTexture));
			addCell(mTempLoneLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture));
			addCell(mDoublePlusButton1);
			addCell(mOnePlusButton1);
			addCell(mOneMinusButton1);
			addCell(mDoubleMinusButton1);
			addCell(mLoanButton);
			row();
			addCell(new DLabel("상환", fontTexture));
			addCell(mTempRepayLabel = new CLabel(null, R.array.label_array_outline_white_15,
					fontTexture));
			addCell(mDoublePlusButton2);
			addCell(mOnePlusButton2);
			addCell(mOneMinusButton2);
			addCell(mDoubleMinusButton2);
			addCell(mRepayButton);

			// debugTableContainer();

			// debugAll();
		}

		@Override
		public void update(long time) {
			super.update(time);
		}

		private void applyNewLone(double lone) {
			FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
					.getFinancialData();

			double temp1 = (financialData.creditLimit - financialData.loan) / 10;

			mTempLone += temp1 * lone;
			if(mTempLone < 0) mTempLone = 0;

			if(mTempLone > financialData.creditLimit - financialData.loan) {
				mTempLone = financialData.creditLimit - financialData.loan;
			}

			updateTempLone();
			updateAll();
		}

		private void updateTempLone() {
			mTempLoneLabel.setText(Utils.toCash(mTempLone));
			updateAll();
		}

		private void applyNewRepay(double repay) {
			FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
					.getFinancialData();

			double temp2 = financialData.loan / 10;

			mTempRepay += temp2 * repay;
			if(mTempRepay < 0) mTempRepay = 0;

			if(mTempRepay > financialData.loan) {
				mTempRepay = financialData.loan;
			}

			updateTempRepay();
			updateAll();
		}

		private void updateTempRepay() {
			mTempRepayLabel.setText(Utils.toCash(mTempRepay));
			updateAll();
		}

		private void updateAll() {
			FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
					.getFinancialData();
			mTotalLone.setText(Utils.toCash(financialData.loan));
			float interest = CityManager.getInstance().getCurrentCity().mInterestRate;
			mMonthlyInterest.setText(Utils.toCash(financialData.loan * interest / 12));
			mCreditLimit.setText(Utils.toCash(financialData.creditLimit));
			mCityInterest.setText(Utils.toPercent2(interest));
		}

		@Override
		public void onShow() {
			updateTempLone();
			updateTempRepay();
			updateAll();
		}

		@Override
		public void onHide() {
			mTempLone = 0;
			mTempRepay = 0;
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

	private static class StockTradingReport extends DataTable implements Report {

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
