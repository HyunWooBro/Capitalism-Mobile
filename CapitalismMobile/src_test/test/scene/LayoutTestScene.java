package test.scene;

import java.util.Arrays;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;
import android.view.animation.BounceInterpolator;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.OrthoCamera;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.PlayMode;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.Vector2;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.MoveTo;
import core.scene.stage.actor.action.absolute.SizeTo;
import core.scene.stage.actor.action.relative.RotateBy;
import core.scene.stage.actor.action.relative.ScaleBy;
import core.scene.stage.actor.drawable.AnimationDrawable;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.ScrollPane;
import core.scene.stage.actor.widget.bar.SlidingBar;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.button.Button.ButtonCostume;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.transition.TransitionWipeInDown;

@SuppressWarnings("rawtypes")
public class LayoutTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	Image mPopupImage;
	
	Vector2 vector = new Vector2();

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		final TextureRegion mPopupRegion = mImageTexture.getTextureRegion("ui_choice_popup_info");
		
		mBackGroundImage = new Image(mBackgroundRegion) 
				.moveTo(100, 100);

		mTestImage = new Image(mApartmentRegion)/* {
					public Float getMaxWidth() {
						return 150f;
					}
				}*/
				.moveTo(0, 100)
				.addEventListener(new GestureTouchListener() {
					@Override
					public void onSingleTapUp(TouchEvent event, float x,
							float y, Actor<?> listener) {
						Core.INPUT.bringUpTextInput(null, -1, null);
					}
				});
		
		mPopupImage = new Image(mPopupRegion)
				.moveTo(100, 100);//.rotateBy(45).setPivot(0, 0)
				/*.addTouchListener(new TouchListenerToRemove() {

					@Override
					public void onTouched(Actor<?> actor) {
						Core.APP.info("touch");
					}
				});*/
				//.addAction(new RotateBy(300, 30000));
		
		LayoutTable table = new LayoutTable().setTag("table");

		TextureRegion buttonRegion = mImageTexture.getTextureRegion("mainmenu_item1_n");
		
		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(buttonRegion);
		
		PushButton abc = new PushButton(costume)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new GestureTouchListener() {
					@Override
					public void onScroll(TouchEvent event, float distanceX,
							float distanceY, float x, float y, Actor<?> listener) {
						Core.INPUT.bringUpTextInput(null, -1, null);
					}
				})
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Director.getInstance().pushScene(
								new TransitionWipeInDown(300, 700, new BounceInterpolator(), 
										new ActorTestScene()));
					}
				});
		
		
		//layout.addAction(new RotateBy(200, 7000).setInterpolator(new LinearInterpolator()));
		//layout.addAction(new FadeOut(3000));
		//layout.setPivot(0, 0);
		
		//table.setFillParent(true);
		//table.setWidth(520);
		//table.setHeight(800);
		
		//table.west();
		
		CastingDirector cd = CastingDirector.getInstance();
		final SlidingBar bar = cd.cast(SlidingBar.class, "default")
				.setRange(0f, 4f)
				.setValue(2f)
				.addEventListener(new TouchListener() {
					@Override
					public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
						//Core.INPUT.setTouchEventStopped(true);
					}
				})
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						int speed = (int) ((SlidingBar) target).getValue();
						
						/*switch(speed) {
							case 4: mSpeedLabel.setText("가장 빠름");
								break;
							case 3: mSpeedLabel.setText("빠름");
								break;
							case 2: mSpeedLabel.setText("보통");
								break;
							case 1: mSpeedLabel.setText("느림");
								break;
							case 0: mSpeedLabel.setText("정지");
								break;
						}
						
						mDialog.getContentTable().invalidateHierarchy();*/
						
					}
				});
		
		table.setDrawable(Drawable.newDrawable(mBackgroundRegion), false);
		
		table.col(1).cellMinWidth(10f);
		
		table.addCell(abc).right().bottom();
		table.addCell();
		table.addCell(mTestImage.setTag("image")).actorMinSize(100f, 50f).actorMaxWidth(150f).cellPrefWidth(150f).fillX().left();
		table.row();
		
		table.addCell(mPopupImage).right().top().expand();
		table.addCell();
		table.addCell(mBackGroundImage).actorPrefSize(100f, 100f).rowSpan(2).top().pad(20).expandX();
		table.row();
		table.addCell(bar.addAction(new RotateBy(360f, 20000))).right().colSpan(2);
		table.addCell(new Image(mApartmentRegion)).cellPrefWidth(200f).right();
		
		table.debugTable();
		//table.validate();
		table.pack();
		
		//table.sizeTo(200f, 200f);
		//table.addAction(new SizeTo(20f, 20f, 7000));

		
		ScrollPane scroller = cd.cast(ScrollPane.class, "default", table)
				.moveTo(200, 0).sizeTo(440, 400);
		
		DLabel.sDefaultPaint.setTextSize(20);
		//DLabel.sDefaultPaint.setTypeface(typeface);
		DLabel.sDefaultPaint.setAntiAlias(true);
		DLabel.sDefaultPaint.setShadowLayer(2, 0, 0, Color4.BLACK);
		DLabel.sDefaultPaint.setColor(Color4.WHITE);
		
		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		DLabel label5 = new DLabel("$(22,012)", fontTexture)
				.moveTo(50, 200).setColor(Color4.MAGENTA).setTextSize(10)
				.addAction(new SizeTo(300, 200, 2000));
		
		CLabel label6 = new CLabel("123", R.array.label_array_outline_yellow_12, fontTexture)
				.moveTo(150, 200).setColor(1f, 1f, 1f, 0f).setPivotY(0.8f)
				.addAction(new ScaleBy(4.5f, 4.5f, 2000));
				/*.addTouchListener(new TouchListenerToRemove() {

					@Override
					public void onTouched(Actor<?> actor) {
						Core.APP.info("scale touch ok");
					}
				});*/
		
		final CLabel label7 = new CLabel("123", R.array.label_array_red_15, fontTexture)
				.moveTo(60, 100).setColor(1f, 1f, 1f, 0f).pivotTo(0f, 1f)
				.addEventListener(new TouchListener() {
					@Override
					public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
						Core.APP.info("downdown");
						return false;
					}
				})
				.addAction(new MoveTo(100f, 50f, 2000).setStartOffset(2000));
		
		
		Animation mAnimation = new Animation(100, Arrays.asList(
				fontTexture.getTextureRegion(R.string.label_building_factory), 
				fontTexture.getTextureRegion(R.string.label_building_port), 
				fontTexture.getTextureRegion(R.string.label_building_retail)
				))
			.setPlayMode(PlayMode.PING_PONG);
		
		AnimationDrawable drawable = (AnimationDrawable) Drawable.newDrawable(mAnimation);
		
		SLabel label8 = new SLabel(drawable)
				.moveTo(20, 200);
		
		
		Image image = new Image(mApartmentRegion)
				.moveTo(50, 50).scaleTo(2f).pivotTo(0.8f, 0.7f)
				.addEventListener(new TouchListener() {
					@Override
					public void onEnter(TouchEvent event, float x, float y, Actor<?> fromActor, Actor<?> listener) {
						Core.APP.debug("onEnter");
					}
					
					@Override
					public void onExit(TouchEvent event, float x, float y, Actor<?> toActor, Actor<?> listener) {
						Core.APP.debug("onExit");
					}
				});
				/*.addTouchListener(new TouchListenerToRemove() {

					@Override
					public void onTouched(Actor<?> actor) {
						//Core.APP.info("scale touch ok");
					}
				});*/
		
		Core.APP.debug("abcffff");
		
		OrthoCamera camera = new OrthoCamera();
		
		//getStage().addPreTouchListener();
		
		getStage().addFloor()
			.setCamera(camera)
			.addChild(scroller)
			.addChild(label5)
			.addChild(label6)
			.addChild(label7)
			.addChild(label8)
			.addChild(image)
			.addEventListener(new TouchListener() {
				@Override
				public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
					Core.APP.debug(event.getTargetActor().getClass().getSimpleName());
					
					if(!bar.getActionList().isEmpty()) {
						Action action = bar.getActionList().get(0);
						if(action != null) action.start();
					}
					return false;
				}
				
				@Override
				public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
					//Core.APP.debug("1111");
				}
			})
			.addEventCaptureListener(new TouchListener() {
				@Override
				public void onEnter(TouchEvent event, float x, float y, Actor<?> fromActor, Actor<?> listener) {
					Core.APP.debug("pre onEnter" + event.getTargetActor().getClass().getSimpleName());
				}
			});
			
		
		Director.getInstance().overlapSceneOnTop(new ActionTestScene());
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU)
			Director.getInstance().changeScene(new LayoutTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
