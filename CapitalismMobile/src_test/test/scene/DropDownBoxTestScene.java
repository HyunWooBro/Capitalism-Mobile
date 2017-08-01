package test.scene;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.game.city.City;
import project.game.city.CityManager;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.corporation.PlayerCorporation;
import project.game.report.ReportManager;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.box.DropDownBox;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.button.Button.ButtonCostume;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.utils.Align.HAlign;

@SuppressWarnings("rawtypes")
public class DropDownBoxTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	private LoneTable mLoneTable;

	@Override
	protected void create() {
		
		
		PlayerCorporation mPlayer = new PlayerCorporation();
		City city = new City("서울", 5000, 40, 50, 50);
		CityManager.getInstance().addCity(city);
		mPlayer.Initialize(60000000);
		
		//ReportManager.getInstance().init();
		//ReportManager.getInstance().showReport(8);
		//ReportManager.getInstance().setVisible(true);
		
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);
		
		
		
		CastingDirector cd = CastingDirector.getInstance();
		
		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		
		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		itemList.add(new DLabel("대출/상환", fontTexture));
		itemList.add(new DLabel("주식 거래", fontTexture));
		
		final DropDownBox dropDownBox1 = cd.cast(DropDownBox.class, "default")
				.moveTo(100, 100)
				.setDividerHeight(2f)
				.set(itemList)
				.select(0)
				.setItemHAlign(HAlign.LEFT)
				.setItemPadding(0f, 5f, 0f, 0f)
				.pack()
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						if(event.isTargetActor()) {
							/*DropDownBox box = (DropDownBox) listener;
							sIndex1 = box.getSelectedIndex();
							buildProductPurchaseList();
							rebuildListBoxItems();*/
						}
					}
				});
		
		LayoutTable table2 = new LayoutTable();
		
		
		table2.pad(10f);
		table2.north();
		
		LayoutTable table = new LayoutTable();
		//table.addCell(mLoneTable = new LoneTable());
		table.pad(10f);
		table.debugTableContainer();
		
		table2.addCell(dropDownBox1);
		table2.row();
		table2.addCell(table);
		
		table2.pack();
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage)
			.addChild(dropDownBox1);
		
		//mLoneTable.onShow();
	}
	
	@Override
	public void update(long time) {
		super.update(time);
		
		//Core.APP.debug(getStage().toString());
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
		
		if(keyCode == KeyEvent.KEYCODE_MENU)
			ReportManager.getInstance().setVisible(true);
	}
	
private static class LoneTable extends Table<LoneTable> {
		
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
		
		public LoneTable() {
			
			
			
			CastingDirector cd = CastingDirector.getInstance();
			
			
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);
			
			
			List<Actor<?>> itemList = new ArrayList<Actor<?>>();
			itemList.add(new DLabel("대출/상환", fontTexture));
			itemList.add(new DLabel("주식 거래", fontTexture));
			
			final DropDownBox dropDownBox1 = cd.cast(DropDownBox.class, "default")
					.setDividerHeight(2f)
					.set(itemList)
					.select(0)
					.setItemHAlign(HAlign.LEFT)
					.setItemPadding(0f, 5f, 0f, 0f)
					.addEventListener(new ChangeListener() {
						
						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							if(event.isTargetActor()) {
								/*DropDownBox box = (DropDownBox) listener;
								sIndex1 = box.getSelectedIndex();
								buildProductPurchaseList();
								rebuildListBoxItems();*/
							}
						}
					});
			
			
			
			
			
			
			
			ButtonCostume costume = new ButtonCostume();

			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_plus_button"));
			mDoublePlusButton1 = new PushButton(costume)
					.pack()
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
					.pack()
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
					.pack()
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
					.pack()
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {
						
						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewLone(-5);
						}
					});
			
			mLoanButton = cd.cast(PushButton.class, "dynamic_text", "대출")
					.addEventListener(new ChangeListener() {
						
						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							FinancialData data = CorporationManager.getInstance().getPlayerCorporation().getFinancialData();
							data.loan += mTempLone;
							data.cash += mTempLone;
							mTempLone = 0;
							updateTempLone();
						}
					});
			
			costume.up = Drawable.newDrawable(imageTexture.getTextureRegion("double_plus_button"));
			mDoublePlusButton2 = new PushButton(costume)
					.pack()
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
					.pack()
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
					.pack()
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
					.pack()
					.setStartTouchAction(Utils.createButtonStartTouchAction())
					.setFinalTouchAction(Utils.createButtonFinalTouchAction())
					.addEventListener(new ChangeListener() {
						
						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							applyNewRepay(-5);
						}
					});
			
			mRepayButton = cd.cast(PushButton.class, "dynamic_text", "상환")
					.addEventListener(new ChangeListener() {
						
						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							FinancialData data = CorporationManager.getInstance().getPlayerCorporation().getFinancialData();
							if(data.cash < mTempRepay) {
								//Utils.showMessageDialog("알림", "상환을 위한 현금이 부족합니다");
								mTempRepay = 0;
								updateTempRepay();
								return;
							}

							data.loan -= mTempRepay;
							data.cash -= mTempRepay;
							mTempRepay = 0;
							updateTempRepay();
						}
					});
			
			
			pad(10f);
			northWest();
			
			col(0).cellWidth(80f).left();
			col(1).cellWidth(100f).right();
			col(2).padLeft(5f);
			col(3).padLeft(5f);
			col(4).padLeft(5f);
			col(5).padLeft(5f);
			col(6).size(70f, 40f).padLeft(5f);
			
			addCell(new DLabel("신용 한도", fontTexture));
			addCell(mCreditLimit = new CLabel(null, R.array.label_array_outline_white_15, fontTexture));
			row();
			addCell(new DLabel("연이자율", fontTexture));
			addCell(mCityInterest = new CLabel(null, R.array.label_array_outline_white_15, fontTexture));
			row().padTop(10f);
			addCell(new DLabel("총 대출금", fontTexture).setColor(Color4.LTRED4));
			addCell(mTotalLone = new CLabel(null, R.array.label_array_outline_white_15, fontTexture));
			row();
			addCell(new DLabel("월간 이자", fontTexture).setColor(Color4.DKYELLOW4));
			addCell(mMonthlyInterest = new CLabel(null, R.array.label_array_outline_white_15, fontTexture));
			row().padTop(10f);
			addCell(new DLabel("대출", fontTexture));
			addCell(mTempLoneLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture));
			addCell(mDoublePlusButton1);
			addCell(mOnePlusButton1);
			addCell(mOneMinusButton1);
			addCell(mDoubleMinusButton1);
			addCell(mLoanButton);
			row();
			addCell(new DLabel("상환", fontTexture));
			addCell(mTempRepayLabel = new CLabel(null, R.array.label_array_outline_white_15, fontTexture));
			addCell(mDoublePlusButton2);
			addCell(mOnePlusButton2);
			addCell(mOneMinusButton2);
			addCell(mDoubleMinusButton2);
			addCell(mRepayButton);
			
			// debugTableContainer();
		}
		
		@Override
		public void update(long time) {
			super.update(time);
			
			
		}
		
		private void applyNewLone(double lone) {
			FinancialData data = CorporationManager.getInstance().getPlayerCorporation().getFinancialData();
			
			double temp1 = (data.creditLimit - data.loan)/10;
			
			mTempLone += temp1*lone;
			if(mTempLone < 0) mTempLone = 0;
			
			if(mTempLone > data.creditLimit - data.loan)
				mTempLone = data.creditLimit - data.loan;
			
			updateTempLone();
			updateAll();
		}
		
		private void updateTempLone() {
			mTempLoneLabel.setText(Utils.toCash(mTempLone));
			updateAll();
		}
		
		private void applyNewRepay(double repay) {
			FinancialData data = CorporationManager.getInstance().getPlayerCorporation().getFinancialData();
			
			double temp2 = data.loan/10;
			
			mTempRepay += temp2*repay;
			if(mTempRepay < 0) mTempRepay = 0;
			
			if(mTempRepay > data.loan)
				mTempRepay = data.loan;
			
			updateTempRepay();
			updateAll();
		}
		
		private void updateTempRepay() {
			mTempRepayLabel.setText(Utils.toCash(mTempRepay));
			updateAll();
		}
		
		private void updateAll() {
			FinancialData data = CorporationManager.getInstance().getPlayerCorporation().getFinancialData();
			mTotalLone.setText(Utils.toCash(data.loan));
			float interest = 0.1f;
			mMonthlyInterest.setText(Utils.toCash(data.loan*interest/12));
			mCreditLimit.setText(Utils.toCash(data.creditLimit));
			mCityInterest.setText(interest*100 + "%");
		}
		
		public void onShow() {
			updateTempLone();
			updateTempRepay();
			updateAll();
		}
		
		public void onHide() {
			mTempLone = 0;
			mTempRepay = 0;
		}
	}

}
