package project.game.ui;

import org.framework.R;

import project.framework.Utils;
import project.game.GameScene;
import project.game.GameScene.GameScreenType;
import project.game.city.City;
import project.game.city.CityManager;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;

public class CityInfo extends Table<CityInfo> {

	private CLabel mPopulationLabel;
	private CLabel mSpendingLevelLabel;
	private CLabel mSalaryLevelLabel;

	private TableCell mCell;

	private SLabel mPanicLabel;
	private SLabel mDepressionLabel;
	private SLabel mRecessionLabel;
	private SLabel mNormalLabel;
	private SLabel mRecoveryLabel;
	private SLabel mBoomLabel;

	public CityInfo() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		// Image cityInfoUIImage = new
		// Image(imageTexture.getTextureRegion("ui_general_info"));

		// 도시명
		SLabel cityNameLabel = new SLabel(R.string.label_city_name, fontTexture);

		// 인구
		SLabel cityPopLabel = new SLabel(R.string.label_city_population, fontTexture);
		mPopulationLabel = new CLabel("", R.array.label_array_outline_white_15, fontTexture);

		// 경제지표
		SLabel economicIndicatorLabel = new SLabel(R.string.label_city_economic_indicator,
				fontTexture);

		// 소비레벨
		SLabel spendingLevelIndicatorLabel = new SLabel(R.string.label_city_spending_level,
				fontTexture);
		mSpendingLevelLabel = new CLabel("", R.array.label_array_outline_white_15, fontTexture);

		// 봉급레벨
		SLabel salaryLevelIndicatorLabel = new SLabel(R.string.label_city_salary_level, fontTexture);
		mSalaryLevelLabel = new CLabel("", R.array.label_array_outline_white_15, fontTexture);

		setDrawable(Drawable.newDrawable(imageTexture.getTextureRegion("ui_general_info")));

		// addChild(cityInfoUIImage);
		// sizeTo(cityInfoUIImage.getWidth()+5f,
		// cityInfoUIImage.getHeight()+5f);

		moveTo(500f, 0f);

		left().top().pad(1f).padLeft(5f).padRight(5f);

		col(0).left();
		col(1).right().expandX();

		addCell(cityNameLabel);
		addCell(new SLabel(R.string.label_city_demo_name, fontTexture));
		row();
		addCell(cityPopLabel);
		addCell(mPopulationLabel);
		row();
		addCell(economicIndicatorLabel);
		mCell = addCell();
		row();
		addCell(spendingLevelIndicatorLabel);
		addCell(mSpendingLevelLabel);
		row();
		addCell(salaryLevelIndicatorLabel);
		addCell(mSalaryLevelLabel);

		pack();

		mPanicLabel = new SLabel(R.string.label_city_ei_panic, fontTexture);
		mDepressionLabel = new SLabel(R.string.label_city_ei_depression, fontTexture);
		mRecessionLabel = new SLabel(R.string.label_city_ei_recession, fontTexture);
		mNormalLabel = new SLabel(R.string.label_city_ei_normal, fontTexture);
		mRecoveryLabel = new SLabel(R.string.label_city_ei_recovery, fontTexture);
		mBoomLabel = new SLabel(R.string.label_city_ei_boom, fontTexture);

		// debugAll();

	}

	@Override
	public Actor<?> contact(float x, float y) {
		return null;
	}

	/*package*/ void updateDailyInfo() {

		if(GameScene.mGameScreenType != GameScreenType.MAP)
			return;

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		int economicIndicator = CityManager.getInstance().getCurrentCity().mEconomicIndicator;

		if(economicIndicator < City.EI_DEPRESSION) {
			mCell.setActor(mPanicLabel);
		} else if(economicIndicator < City.EI_RECESSION) {
			mCell.setActor(mDepressionLabel);
		} else if(economicIndicator < City.EI_NORMAL) {
			mCell.setActor(mRecessionLabel);
		} else if(economicIndicator < City.EI_RECOVERY) {
			mCell.setActor(mNormalLabel);
		} else if(economicIndicator < City.EI_BOOM) {
			mCell.setActor(mRecoveryLabel);
		} else
			mCell.setActor(mBoomLabel);

		mPopulationLabel.setText(Utils
				.toString(CityManager.getInstance().getCurrentCity().mPopulation));
		mSpendingLevelLabel.setText("" + CityManager.getInstance().getCurrentCity().mSpendingLevel);
		mSalaryLevelLabel.setText("" + CityManager.getInstance().getCurrentCity().mSalaryLevel);
	}

	/*package*/ void updateMonthlyInfo() {

	}
}
