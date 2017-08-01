package project.framework;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.framework.R;

import project.game.product.Product.DisplayProduct;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ProductDescription;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.Floor;
import core.scene.stage.Stage;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.ActionSet;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.action.absolute.FadeIn;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.action.absolute.ScaleTo;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.Widget;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.utils.pool.Pools;

/**
 * 유용한 기능을 제공한다.
 * 
 * @author 김현우
 */
public class Utils {

	/** 로깅용 태그 */
	public static final String TAG = Core.APP.getResources().getString(R.string.app_name);

	public static MediaPlayer m_click_sound = MediaPlayer.create(Core.APP.getActivity(),
			R.raw.click);

	// public static MediaPlayer m_horn_002_sound =
	// MediaPlayer.create(Core.APP.getActivity(), R.raw.horn_002);
	// public static MediaPlayer m_horn_003_sound =
	// MediaPlayer.create(Core.APP.getActivity(), R.raw.horn_003);
	// public static MediaPlayer m_horn_004_sound =
	// MediaPlayer.create(Core.APP.getActivity(), R.raw.horn_004);
	// public static MediaPlayer m_horn_005_sound =
	// MediaPlayer.create(Core.APP.getActivity(), R.raw.horn_005);
	// public static MediaPlayer m_horn_006_sound =
	// MediaPlayer.create(Core.APP.getActivity(), R.raw.horn_006);

	public static final String PREF_SOUND_SWITCH = "sound_switch";
	public static final String PREF_MUSIC_SWITCH = "music_switch";
	public static final String PREF_SCROLL_SENSITIVE = "scroll_sensitive";
	public static final String PREF_VEHICLE_SWITCH = "vehicle_switch";

	// paint의 이름 - p + color + textalignment + textsize
	// 언급되지 않는 내용은 default

	public static Paint sPushButton15 = new Paint();

	public static Paint sOutlineWhite15 = new Paint();
	public static Paint sOutlineGreen15 = new Paint();
	public static Paint sOutlineRed15 = new Paint();

	public static Paint sBlack15 = new Paint();
	public static Paint sRed15 = new Paint();

	public static Paint sOutlineYellow12 = new Paint();
	public static Paint sOutlineWhite12 = new Paint();

	public static Paint sOutlineWhite10 = new Paint();
	
	private static DecimalFormat sIntegerFormat = new DecimalFormat("#,##0;(#,##0)");

	private static DecimalFormat sDoubleFormat = new DecimalFormat("#,##0.00;(#,##0.00)");

	private static DecimalFormat sCashFormat = new DecimalFormat("$#,##0;$(#,##0)");
	
	private static DecimalFormat sPriceFormat = new DecimalFormat("$#,##0.00");

	private static DecimalFormat sPercentFormat1 = new DecimalFormat("0%;(0%)");
	
	private static DecimalFormat sPercentFormat2 = new DecimalFormat("0.0%;(0.0%)");

	private static Map<String, DialogWindow> sTagToDialogMap = new HashMap<String, DialogWindow>();
	private static List<String> sTagList = new ArrayList<String>();

	private static TagComparator sTagComparator;

	static {
		Typeface typeface = Typeface.createFromAsset(Core.APP.getActivity().getAssets(),
				"HYHWPEQ.TTF");

		sPushButton15.setColor(0xFF119933);
		sPushButton15.setTextSize(15);
		sPushButton15.setTypeface(typeface);
		sPushButton15.setAntiAlias(true);
		// sPushButton15.setStyle(Paint.Style.FILL_AND_STROKE);
		// sPushButton15.setStrokeWidth(0.5f);
		sPushButton15.setShadowLayer(2, 0, 0, Color.WHITE);

		sOutlineWhite15.setColor(Color.WHITE);
		sOutlineWhite15.setTextSize(15);
		sOutlineWhite15.setTypeface(typeface);
		sOutlineWhite15.setAntiAlias(true);
		// sOutlineWhite15.setStyle(Paint.Style.FILL_AND_STROKE);
		// sOutlineWhite15.setStrokeWidth(0.5f);
		sOutlineWhite15.setShadowLayer(2, 0, 0, Color.BLACK);

		sOutlineRed15.setColor(Color.RED);
		sOutlineRed15.setTextSize(15);
		sOutlineRed15.setTypeface(typeface);
		sOutlineRed15.setAntiAlias(true);
		sOutlineRed15.setShadowLayer(2, 0, 0, Color.BLACK);

		sOutlineGreen15.setColor(Color.GREEN);
		sOutlineGreen15.setTextSize(15);
		sOutlineGreen15.setTypeface(typeface);
		sOutlineGreen15.setAntiAlias(true);
		sOutlineGreen15.setShadowLayer(2, 0, 0, Color.BLACK);

		sBlack15.setTextSize(15);
		sBlack15.setColor(Color.BLACK);
		sBlack15.setTypeface(typeface);
		sBlack15.setAntiAlias(true);

		sRed15.setTextSize(15);
		sRed15.setColor(Color.RED);
		sRed15.setTypeface(typeface);
		sRed15.setAntiAlias(true);

		sOutlineYellow12.setTextSize(12);
		sOutlineYellow12.setColor(Color.YELLOW);
		sOutlineYellow12.setTypeface(typeface);
		sOutlineYellow12.setAntiAlias(true);
		sOutlineYellow12.setShadowLayer(2, 0, 0, Color.BLACK);

		sOutlineWhite12.setTextSize(12);
		sOutlineWhite12.setColor(Color.WHITE);
		sOutlineWhite12.setTypeface(typeface);
		sOutlineWhite12.setAntiAlias(true);
		sOutlineWhite12.setShadowLayer(2, 0, 0, Color.BLACK);

		sOutlineWhite10.setTextSize(10);
		sOutlineWhite10.setColor(Color.WHITE);
		sOutlineWhite10.setTypeface(typeface);
		sOutlineWhite10.setAntiAlias(true);
		sOutlineWhite10.setShadowLayer(2, 0, 0, Color.BLACK);
	}

	private Utils() {
	}

	private static boolean canOpenDialogWith(String tag) {
		if(tag == null) {
			throw new IllegalArgumentException("tag cann't be null");
		}
		if(sTagList.contains(tag)) return false;
		return true;
	}

	public static void showMessageDialog(final Stage stage, final String tag, MessageDialogData data) {

		if(!canOpenDialogWith(tag))
			return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		if(data.ok == null) data.ok = "확인";
		final PushButton button = cd.cast(PushButton.class, "dynamic_text", data.ok)
				.setTag("firedOnBackButton").addEventListener(data.okListener)
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog(tag);
					}
				});

		if(data.titleColor == null) {
			data.titleColor = Color4.WHITE4;
		}
		if(data.contentColor == null) {
			data.contentColor = Color4.WHITE4;
		}

		if(data.titlePaint == null) {
			data.titlePaint = DLabel.sDefaultPaint;
		}
		if(data.contentPaint == null) {
			data.contentPaint = DLabel.sDefaultPaint;
		}

		DialogWindow dialog = cd
				.cast(DialogWindow.class, "default")
				.setTitle(new DLabel(data.title, fontTexture, data.titlePaint)
								.setColor(data.titleColor))
				.setModal(true)
				.addContent(new DLabel(null, fontTexture, data.contentPaint)
								.setColor(data.contentColor).enableWrap(data.contentWrap)
								.setText(data.content)).addButton(button);

		dialog.getContentCell().pad(5f);
		dialog.pack().moveCenterTo(320f, 200f);

		stage.addFloor().addChild(dialog);

		dialog.open();

		sTagList.add(tag);
		sTagToDialogMap.put(tag, dialog);
	}

	public static void showYesOrNoDialog(final Stage stage, final String tag, YesOrNoDialogData data) {

		if(!canOpenDialogWith(tag))
			return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		if(data.yes == null) data.yes = "예";
		PushButton yesButton = cd.cast(PushButton.class, "dynamic_text", data.yes)
				.addEventListener(data.yesListener).addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog(tag);
					}
				});

		// 기본적으로 back 버튼을 터치하면 no가 선택된다.
		if(data.no == null)
			data.no = "아니오";
		PushButton noButton = cd.cast(PushButton.class, "dynamic_text", "아니오")
				.setTag("firedOnBackButton").addEventListener(data.noListener)
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog(tag);
					}
				});

		if(data.titleColor == null) {
			data.titleColor = Color4.WHITE4;
		}
		if(data.contentColor == null) {
			data.contentColor = Color4.WHITE4;
		}

		if(data.titlePaint == null) {
			data.titlePaint = DLabel.sDefaultPaint;
		}
		if(data.contentPaint == null) {
			data.contentPaint = DLabel.sDefaultPaint;
		}

		DialogWindow dialog = cd
				.cast(DialogWindow.class, "default")
				.setTitle(new DLabel(data.title, fontTexture, data.titlePaint))
				.setModal(true)
				.addContent(new DLabel(null, fontTexture, data.contentPaint)
						.enableWrap(	data.contentWrap).setText(data.content)).addButton(yesButton)
				.addButton(noButton);

		dialog.getContentCell().pad(5f);
		dialog.pack().moveCenterTo(320f, 200f);

		stage.addFloor().addChild(dialog);

		dialog.open();

		sTagList.add(tag);
		sTagToDialogMap.put(tag, dialog);
	}

	private static void closeDialog(final String tag) {

		final DialogWindow dialog = sTagToDialogMap.get(tag);
		if(dialog == null) return;

		dialog.close();
		dialog.addAction(new Run(new Runnable() {

			@Override
			public void run() {
				Floor floor = dialog.getFloor();
				floor.getStage().removeFloor(floor);
				dialog.disposeAll();

				sTagToDialogMap.remove(tag);
				sTagList.remove(tag);
			}
		}).setStartOffset(dialog.getAnimationDuration()));
	}

	/** 다이얼로그 관련하여 정적 변수를 초기화한다. */
	public static void clear() {
		sTagToDialogMap.clear();
		sTagList.clear();
	}

	public static void exit(final Stage stage) {
		YesOrNoDialogData data = new YesOrNoDialogData();
		data.title = "선택";
		data.content = "게임을 정말 종료하겠습니까?";
		data.yesListener = new ChangeListener() {

			@Override
			public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
				Core.APP.exit();
			}
		};
		showYesOrNoDialog(stage, "exit", data);
	}
	
	/** 
	 * 뒤로가기 버튼을 터치할 경우 호출된다. 열린 다이얼로그를 닫거나 게임을 종료하기 
	 * 위한 다이얼로그를 띄운다.
	 */
	public static void onBackKey(Stage stage) {
		// Tag가 비어있다면 즉, 열린 다이얼로그가 없다면 exit(...)을 호출한다.
		if(sTagList.isEmpty()) {
			exit(stage);
			// 열린 다이얼로그가 있다면 가장 최근의 것부터 닫는다.
		} else {
			String tag = sTagList.get(sTagList.size() - 1);
			DialogWindow dialog = sTagToDialogMap.get(tag);

			// 다이얼로그가 열리는 중이나 닫히는 중이라면 무시한다.
			if(dialog.isOpening() || dialog.isClosing()) return;

			PushButton button = (PushButton) dialog.getChildByTag("firedOnBackButton");
			button.fire(Pools.obtain(ChangeEvent.class));
		}
	}
	
	/** 
	 * 정수를 Striing으로 반환한다. 3자리마다 쉼표가 추가된다. 음수일 경우 
	 * 괄호로 감싼다.
	 */
	public static String toString(int num) {
		return sIntegerFormat.format(num);
	}
	
	/** 
	 * 실수를 String으로 반환한다. 3자리마다 쉼표가 추가되고 소수점 1자리까지 
	 * 표시된다. 음수일 경우 괄호로 감싼다.
	 */
	public static String toString(double num) {
		return sDoubleFormat.format(num);
	}
	
	/** 
	 * 실수를 달러기호를 포함한 String으로 반환한다. 3자리마다 쉼표가 추가된다. 
	 * 음수일 경우 괄호로 감싼다.
	 */
	public static String toCash(double num) {
		return sCashFormat.format(num);
	}
	
	/** 
	 * 실수를 달러기호를 포함한 String으로 반환한다. 3자리마다 쉼표가 추가된다. 
	 * 소수점 2자리를 포함한다.
	 */
	public static String toPrice(double num) {
		return sPriceFormat.format(num);
	}
	
	/** 
	 * 실수를 %기호를 포함한 String으로 반환한다. 예를 들어, 실수 1은 100%로 
	 * 반환된다. 
	 */ 
	public static String toPercent1(double num) {
		return sPercentFormat1.format(num);
	}

	/** 실수를 %기호를 포함한 String으로 반환한다. 소수점 1자리를 포함한다. */
	public static String toPercent2(double num) {
		return sPercentFormat2.format(num);
	}

	public static Action createMoveInAction(float dx, float dy, long startOffset, long duration,
			Interpolator moveInterpolator, Interpolator fadeInterpolator) {

		Action moveBy = new MoveBy(dx, dy, duration).setInterpolator(moveInterpolator);

		Action fadeIn = new FadeIn(duration).setInterpolator(fadeInterpolator);

		ActionSet set = new ActionSet(false).setStartOffset(startOffset).addAction(moveBy)
				.addAction(fadeIn);

		return set;
	}

	public static Action createMoveOutAction(float dx, float dy, long startOffset, long duration,
			Interpolator moveInterpolator, Interpolator fadeInterpolator) {

		Action moveBy = new MoveBy(dx, dy, duration).setInterpolator(moveInterpolator);

		Action fadeOut = new FadeOut(duration).setInterpolator(fadeInterpolator);

		ActionSet set = new ActionSet(false).setStartOffset(startOffset).addAction(moveBy)
				.addAction(fadeOut);

		return set;
	}

	public static Action createButtonStartTouchAction() {
		Action sizeTo = new ScaleTo(1.2f, 1.2f, 75)
				.setInterpolator(new DecelerateInterpolator(1.3f));
		return sizeTo;
	}

	public static Action createButtonFinalTouchAction() {
		Action sizeTo = new ScaleTo(1.0f, 1.0f, 75)
				.setInterpolator(new AccelerateInterpolator(1.3f));
		return sizeTo;
	}

	/** List의 각 Actor을 태그를 기준으로 정렬한다 */
	public static void sort(List<Actor<?>> actorList) {
		if(sTagComparator == null) {
			sTagComparator = new TagComparator();
		}

		Collections.sort(actorList, sTagComparator);
	}

	public static class MessageDialogData {
		public String title;
		public String content;
		public float contentWrap = 250f;
		public String ok;
		public Color4 titleColor;
		public Color4 contentColor;
		public Color4 okColor;
		public Paint titlePaint;
		public Paint contentPaint;
		// public Paint okPaint;
		public ChangeListener okListener;
		// public Drawable icon;
	}

	public static class YesOrNoDialogData {
		public String title;
		public String content;
		public float contentWrap = 250f;
		public String yes;
		public String no;
		public Color4 titleColor;
		public Color4 contentColor;
		public Color4 yesColor;
		public Color4 noColor;
		public Paint titlePaint;
		public Paint contentPaint;
		// public Paint yesPaint;
		// public Paint noPaint;
		public ChangeListener yesListener;
		public ChangeListener noListener;
		// public Drawable icon;
	}
	
	public static class OverallScoreBar extends Widget<OverallScoreBar> {
		
		private static final int MAX_SCORE = 100;
		
		private float mMaxHeight;
		
		private int mBrand;
		private int mQuality;
		private int mPrice;
		
		private TextureRegion mBrandRegion;
		private TextureRegion mQualityRegion;
		private TextureRegion mPriceRegion;
		
		public OverallScoreBar() {
			this(null, 0f);
		}
		
		public OverallScoreBar(DisplayProduct display, float maxHeight) {
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture barTexture = tm.getTexture(R.drawable.atlas_department_bar_graph);

			mBrandRegion = barTexture.getTextureRegion("department_brand_bar");
			mQualityRegion = barTexture.getTextureRegion("department_quality_bar");
			mPriceRegion = barTexture.getTextureRegion("department_price_bar");
			
			mMaxHeight = maxHeight;
			
			set(display);
		}
		
		public OverallScoreBar set(DisplayProduct display) {
			if(display == null) return this;
			mBrand = ProductManager.getInstance().getBrandScore(display);
			mQuality = ProductManager.getInstance().getQualityScore(display);
			mPrice = ProductManager.getInstance().getPriceScore(display);
			return this;
		}
		
		public OverallScoreBar set(ProductDescription desc, int brand, int quality, double price) {
			mBrand = ProductManager.getInstance().getBrandScore(desc, brand);
			mQuality = ProductManager.getInstance().getQualityScore(desc, quality);
			mPrice = ProductManager.getInstance().getPriceScore(desc, price);
			return this;
		}

		@Override
		public void layout() {
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			
			batch.setColor(Color4.WHITE4);
			
			int overallScore = mBrand + mQuality + mPrice;
			// 총점이 음수일 경우 무시한다.
			if(overallScore <= 0) return;
			// 최대 총점과 최대 높이를 기준으로 적절하게 스케일한다.
			float sacle = (overallScore > MAX_SCORE)? mMaxHeight/overallScore : mMaxHeight/MAX_SCORE;
			
			int priceScore;
			int qualityScore; 
			// 가격이 음수일 경우에 보정한다.
			if(mPrice >= 0) {
				priceScore = mPrice;
				qualityScore = mQuality;
			} else {
				priceScore = 0;
				qualityScore = (int) (mQuality * ((float) overallScore / (mBrand + mQuality)));
			}
			
			float width = mPriceRegion.getRegionWidth();
			
			float x = getX();
			float y = getY();
			
			// 가격
			y += (mMaxHeight - overallScore*sacle);
			batch.draw(mPriceRegion, x, y, width, overallScore*sacle);
			
			// 품질
			y += priceScore*sacle;
			batch.draw(mQualityRegion, x, y, width, (overallScore - priceScore)*sacle);
			
			// 브랜드
			y += qualityScore*sacle;
			batch.draw(mBrandRegion, x, y, width, (overallScore - priceScore - qualityScore)*sacle);
			
		}

		@Override
		protected float getDefaultPrefWidth() {
			return mBrandRegion.getRegionWidth();
		}

		@Override
		protected float getDefaultPrefHeight() {
			return mMaxHeight;
		}
		
		@Override
		public Float getMaxHeight() {
			return mMaxHeight;
		}

		public OverallScoreBar setMaxHeight(float maxHeight) {
			mMaxHeight = maxHeight;
			return this;
		}
	}

	private static class TagComparator implements Comparator<Actor<?>> {

		@Override
		public int compare(Actor<?> lhs, Actor<?> rhs) {
			return lhs.getTag().compareTo(rhs.getTag());
		}
	}

}
