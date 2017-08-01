package test.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.framework.R;

import project.framework.Constant;
import project.framework.Utils;
import project.game.GameScene;

import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.OrthoCamera;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.PlayMode;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.framework.input.TextInput.TextInputListener;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.ColorTo;
import core.scene.stage.actor.action.absolute.FadeIn;
import core.scene.stage.actor.action.absolute.MoveTo;
import core.scene.stage.actor.action.absolute.PivotTo;
import core.scene.stage.actor.action.absolute.ScaleTo;
import core.scene.stage.actor.action.relative.ColorBy;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.action.relative.RotateBy;
import core.scene.stage.actor.action.relative.ScaleBy;
import core.scene.stage.actor.drawable.AnimationDrawable;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.ScrollPane;
import core.scene.stage.actor.widget.SlidingDrawer;
import core.scene.stage.actor.widget.bar.Bar.BarCostume;
import core.scene.stage.actor.widget.bar.ProgressBar;
import core.scene.stage.actor.widget.bar.SlidingBar;
import core.scene.stage.actor.widget.bar.SlidingBar.SlidingBarCostume;
import core.scene.stage.actor.widget.box.DropDownBox;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.Label;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.button.ButtonGroup;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.scene.transition.Transition;
import core.scene.transition.TransitionDelay;
import core.scene.transition.TransitionDissolve;

@SuppressWarnings("rawtypes")
public class ActorTestScene extends Scene{

	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	private PushButton mTestButton;
	
	private Label<?> mTestLabel;
	
	private ProgressBar mTestProgressBar;
	
	private SlidingBar mTestSlider;
	
	private PushButton temp;
	
	private CLabel label6;
	
	private DLabel label8;
	
	private DLabel label9;
	
	private DLabel label10;
	
	private Actor<?> mTestActor;
	
	private int money;
	
	private DropDownBox mDropDownBox;

	@Override
	protected void create() {
		
		CastingDirector cd = CastingDirector.getInstance();
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		
		final Texture fontTexture = tm.getTexture(R.drawable.font);
		
		TextureRegion backgroundRegion = imageTexture.getTextureRegion("mainmenu_background");
		TextureRegion apartmentRegion = imageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(backgroundRegion);
		
		Animation mAnimation = new Animation(500, Arrays.asList(
				imageTexture.getTextureRegion("cell_2x2_house1_d"), 
				imageTexture.getTextureRegion("cell_2x2_house1"), 
				imageTexture.getTextureRegion("cell_2x2_apratment1"), 
				imageTexture.getTextureRegion("cell_2x2_apratment1_d")
				))
			.setPlayMode(PlayMode.PING_PONG);
		
		AnimationDrawable drawable = (AnimationDrawable) Drawable.newDrawable(mAnimation);
		//drawable.forceFixedSize(128f, 186f);
		
		mTestImage = new Image(drawable) {
					@Override
					public float getMinWidth() {
						return 200f;
					}
				}
				.setDebug(true)
				.moveTo(100f, 100f)
				.addAction(new MoveBy(300f, 0f, 4000).setTag("1"))
				.addAction(new RotateBy(90f, 3000).setTag("2"))
				.addEventListener(new ActionListener() {
					
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						if(action.tag.equals("1"))
							Core.APP.debug("!! MoveBy");
							//mTemp.getForm().setPivotToCenter();

						if(action.tag.equals("2"))
							//mTemp.addAction(new FadeOut(3000));
							Core.APP.debug("!! RotateBy");
					}
				});
				/*.addTouchListener(new TouchListenerToRemove() {

					@Override
					public void onTouched(Actor<?> actor) {
						//action.cancel();
						Core.APP.debug("touched");
					}
				});*/
				
		//mTestImage.getForm()
				//.moveBy(100, 100)
				//.setPivot(0, 0);
				//.rotateBy(40);
		
		Texture imageTexture2 = tm.getTexture(R.drawable.temp);

		Animation flashAnimation = new Animation(30, Arrays.asList(
				new TextureRegion(imageTexture2), 
				imageTexture2.getTextureRegion("menu_item_flash_n_01"), 
				imageTexture2.getTextureRegion("menu_item_flash_n_02"), 
				imageTexture2.getTextureRegion("menu_item_flash_n_03"), 
				imageTexture2.getTextureRegion("menu_item_flash_n_04"),
				imageTexture2.getTextureRegion("menu_item_flash_n_05"), 
				imageTexture2.getTextureRegion("menu_item_flash_n_06"), 
				imageTexture2.getTextureRegion("menu_item_flash_n_07"), 
				imageTexture2.getTextureRegion("menu_item_flash_n_08"), 
				new TextureRegion(imageTexture2)
				))
			.setPlayMode(PlayMode.REPEAT)
			.setFrameDuration(0, 1000)
			.setFrameDuration(9, 2000)
			.setPatternIndex(1);

		final TextureRegion buttonRegion = imageTexture.getTextureRegion("mainmenu_item1_n");
		
		temp = cd.cast(PushButton.class, "up", buttonRegion)
				.moveTo(300, 100).rotateBy(75)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addChild(new Image(flashAnimation).setFillParent(true))
				.addAction(new ColorTo(new Color4(1f, 1f, 0f, 0f), 7000).setFillAfter(false))
				.addAction(new MoveBy(100f, 100f, 7000).setFillEnabled(false).setRepeatMode(Action.REVERSE).setRepeatCount(1))
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, final Actor<?> target, Actor<?> listener) {
						
						//actor.addAction(new ColorTo(ColorType.GREEN, 1f, 1000).setFillAfter(true));
						
						//actor.addAction(new ColorBy(ColorType.RED, -0.5f, 1000));
						
						/*actor.addAction(new Run(new Runnable() {
							
							@Override
							public void run() {
								actor.setVisible(!actor.isVisible());
							}
						}).setStartOffset(2000).setRepeatCount(Action.INFINITE));*/
						
						//actor.addAction(new ColorTo(new Color4(1f, 2f, 2f, 2f), 70).setFillEnabled(false).setRepeatMode(Action.REVERSE).setRepeatCount(Action.INFINITE));
						
						//actor.finishActions();
						
						/*int n = actor.getActionList().size();
						for(int i=0; i<n; i++) {
							if(actor.getActionList().get(i).getTag() == 1)
								actor.getActionList().get(i).finish();
						}*/
						
						//actor.addAction(new SizeTo(10f, 10f, 180f, 80f, 2000).setFillAfter(true));
						
						//actor.addAction(new SizeBy(10f, -10f, 2000).setFillAfter(true));
						
						//actor.addAction(new FadeOut(2000));
						
						//actor.addAction(new FadeIn(2000));
						
						//actor.addAction(new AlphaBy(-0.4f, 2000).setFillAfter(true));
						
						Core.INPUT.bringUpTextInput(null, -1, new TextInputListener() {
							
							@Override
							public void onInput(String text) {
								int size = Integer.parseInt(text);
								
								label8.setTextSize(size);
								
								
								//label8.setString(text);
							}
							
							@Override
							public void onCancel() {
							}
						});
						
						
						//actor.addAction(new ColorBy(ColorType.BLUE, -1f,  2000));
						
						/*Core.INPUT.bringUpTextInput(null, -1, new TextInputListener() {
							
							@Override
							public void INPUT(String text) {
							}
							
							@Override
							public void canceled() {
							}
						});*/
					}
				});
		
		final TextureRegion popupRegion = imageTexture.getTextureRegion("ui_choice_popup_info");
		NinePatch patch = new NinePatch(popupRegion, 10, 10, 10, 10);
		
		Image nineImage = new Image(patch)
				.moveTo(300, 200)
				.addAction(new ScaleTo(2.0f, 2.0f, 1000))
				.addAction(new ColorTo(new Color4(1f, 1f, 0f, 1f), 1000))
				.addAction(new MoveBy(-100f,-50f, 2000));
		
		TextureRegion rectRegion = imageTexture.getTextureRegion("department_selector");
		Image rectImage = new Image(rectRegion)
				.moveTo(50, 200).sizeTo(200, 150);
		
		
		final ScrollPane scrollPane = cd.cast(ScrollPane.class, "default", 
					new Image(backgroundRegion).setPrefWidth(200f).setPrefHeight(1200f)
						.addAction(new ColorBy(new Color4(0f, -0.4f, -0.4f, 0f), 15000)))
				.moveTo(50f, 200f).setPrefSize(200f, 150f).setOverScrollDistance(20f)
				//.addAction(new MoveTo(100f, 150f, 2000).setStartOffset(1500).setFillAfter(true))
				;
		
		DLabel buttonLabel = new DLabel("싱글플레이", fontTexture)
				.setColor(1f, 1f, 1f, 0f).scaleTo(1.5f);
		
		PushButton button1 = cd.cast(PushButton.class, "up", buttonRegion)
				.setDebug(true)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Director.getInstance().popScene(new TransitionDissolve(400, 500));
						/*Director.getInstance().changeScene(
								new TransitionWipeOutUp(300, 300, 
										new LayoutTestScene()));*/
					}
				})
				.addAction(new RotateBy(-360,4000).setStartOffset(1000).setRepeatCount(Action.INFINITE));
		
		button1.addCell(buttonLabel).padTop(45);
		
		
		
		/*List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		for(int j=0; j<5; j++) {
			DLabel label = new DLabel("abc", fontTexture);
			DLabel label2 = new DLabel("def", fontTexture);
			itemList.add(label);
			itemList.add(label2);
		}*/
		
		
		//String[] departments = Core.APP.getResources().getStringArray(R.array.retail);
		
		
		
		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		/*for(int j=0; j<departments.length; j++) {
			LayoutTable table = cd.cast(LayoutTable.class, "department_list_item", departments[j]);
			itemList.add(table);
		}*/
		
		itemList.add(new DLabel("웃기지 않니?", fontTexture, Utils.sOutlineWhite15));
		itemList.add(new DLabel("페드로도 거의 던딜이라고 함", fontTexture, Utils.sOutlineWhite15));
		itemList.add(new DLabel("이적료는 320억~350억 사이", fontTexture, Utils.sOutlineWhite15));
		itemList.add(new DLabel("5년계약", fontTexture, Utils.sOutlineWhite15));
		itemList.add(new DLabel("페드로 영입과 동시에", fontTexture, Utils.sOutlineWhite15));
		itemList.add(new DLabel("디마리아는 팔 거라고 하던데", fontTexture, Utils.sOutlineWhite15));
		itemList.add(new DLabel("웃기지 않니?", fontTexture, Utils.sOutlineWhite15));
		
		
		
		
		
		
		
		
		final ListBox listBox = cd.cast(ListBox.class, "default", itemList, HAlign.LEFT, 0f)
				.moveTo(100f, 250f)
				//.sizeTo(200f, 150f);
				.pack();
		
		PushButton itemButton0 = cd.cast(PushButton.class, "dynamic_text", "모두 선택")
				.moveTo(100f, 0f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.getSelector().addAll(listBox.getItemList());
					}
				})
				.pack();
		
		/*PushButton itemButton1 = cd.cast(PushButton.class, "dynamic_text", "3개 선택")
				.moveTo(100f, 50f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.getSelector().setMinSelections(0);
						listBox.getSelector().setMaxSelections(3);
						listBox.getSelector().setRemoveLastSelection(false);
					}
				})
				.pack();*/
		
		/*PushButton itemButton1 = cd.cast(PushButton.class, "dynamic_text", "처음 항목과 마지막 항목 바꾸기")
				.moveTo(100f, 50f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.swapItems(0, listBox.getItemList().size()-1);
					}
				})
				.pack();*/
		
		/*PushButton itemButton1 = cd.cast(PushButton.class, "dynamic_text", "인덱스2부터 4까지 지우기")
				.moveTo(100f, 50f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.removeAll(2, 4);
					}
				})
				.pack();*/
		
		PushButton itemButton1 = cd.cast(PushButton.class, "dynamic_text", "선택 반전")
				.moveTo(100f, 50f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.reverseAll(listBox.getItemList());
					}
				})
				.pack();
		
		PushButton itemButton2 = cd.cast(PushButton.class, "dynamic_text", "선택 모두 취소")
				.moveTo(100f, 100f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.select(null);
					}
				})
				.pack();
		
		PushButton itemButton3 = cd.cast(PushButton.class, "dynamic_text", "선택한 것 1개 제거")
				.moveTo(100f, 150f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.removeItem(listBox.getSelectedItem());
					}
				})
				.pack();
		
		PushButton itemButton4 = cd.cast(PushButton.class, "dynamic_text", "선택한 것 모두 제거")
				.moveTo(100f, 200f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						listBox.removeAll(listBox.getSelector().getSelectionList());
					}
				})
				.pack();
		
		mDropDownBox = cd.cast(DropDownBox.class, "default")
				//.moveTo(200, 100)//.setPivot(0.5f, 2.5f);//
				//.sizeTo(300, 100)
				//.setItemList(itemList)
				.addAction(new MoveBy(0f, 150f, 10000))
				//.addAction(new RotateBy(360f, 3000).setStartOffset(1000).setRepeatCount(Action.INFINITE));
				//.setItemHeight(40f)
				//.create(200f, 150f)
				.setDividerHeight(2f)
				.setItemHAlign(HAlign.LEFT)
				.setItemPadding(0f, 5f, 0f, 0f)
				.pack()
				.setWidth(200f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						DropDownBox box = (DropDownBox) listener;
						Core.APP.debug("index : " + box.getSelectedIndex());
					}
				});
				//.pack();
				//.setMaxVisibleItems(2);
				//.setWidth(300);
				/*.addChild(mTestImage)
				.addChild(temp)
				.addChild(button1)
				//.addChildActor(nineImage)
				.addChild(new Extra() {

					@Override
					public void draw(Batch batch, float parentAlpha) {
						batch.setColor(1f, 1f, 1f, 1f);
						batch.draw(buttonRegion, 30, 30);
					}
				});*/
				//.addAction(new RotateBy(360, 4000).setStartOffset(1000).setRepeatCount(Action.INFINITE));
		
		PushButton button2 = cd.cast(PushButton.class, "up_down_checked", 
					buttonRegion, imageTexture.getTextureRegion("cell_2x2_house1_d"), popupRegion)
				.moveTo(200, 300)
				.pack()
				.addEventListener(new ChangeListener() {
					
					boolean first;
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						
						if(first) event.cancel();
						first = true;
						
						/*TextureManager tm = Core.GRAPHICS.getTextureManager();
						
						Texture imageTexture = tm.getTexture(R.drawable.atlas);
						
						final Texture fontTexture = tm.getTexture(R.drawable.font);
						
						Core.INPUT.bringUpTextInput(null, -1, new TextInputListener() {
							
							@Override
							public void onInput(String text) {
								mDropDownBox.addItem(0, new DLabel(text, fontTexture, Utility.sOutlineWhite15));
							}
							
							@Override
							public void onCancel() {
							}
						});*/
					}
				});
		
		PushButton button3 = cd.cast(PushButton.class, "up_down_checked", 
					buttonRegion, imageTexture.getTextureRegion("cell_2x2_house1_d"), popupRegion)
				.moveTo(350, 300)
				.pack()
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						
						//event.cancel();
						/*TextureManager tm = Core.GRAPHICS.getTextureManager();
						
						Texture imageTexture = tm.getTexture(R.drawable.atlas);
						
						final Texture fontTexture = tm.getTexture(R.drawable.font);
						
						Core.INPUT.bringUpTextInput(null, -1, new TextInputListener() {
							
							@Override
							public void onInput(String text) {
								mDropDownBox.removeItem(Integer.parseInt(text));
							}
							
							@Override
							public void onCancel() {
							}
						});*/
					}
				});
		
		ButtonGroup buttonGroup = new ButtonGroup()
				.addButton(button2)
				.addButton(button3)
				//.setChecked(button2)
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						ButtonGroup group = (ButtonGroup) listener;
						Core.APP.debug("index is : " + group.getCheckedIndex());
						
						//if(event.isTargetActor()) 
						//	event.cancel();
						
						//group.getSelector().addAll(group.getButtonList());
					}
				});
		
		TextureRegion tranied = imageTexture.getTextureRegion("employee_trained");
		TextureRegion untrained = imageTexture.getTextureRegion("employee_untrained");
		
		Image image = new Image(tranied)
				.addEventListener(new TouchListener() {
					@Override
					public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
						listener.setColor(1f, 1f, 0f, 0f);
						listener.addAction(new ScaleBy(1.5f, 1.5f, 1000).setFillAfter(false));
						return true;
					}
					
					@Override
					public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
						listener.setColor(1f, 1f, 1f, 1f);
					}
				});
		
		SlidingDrawer drawer = new SlidingDrawer(scrollPane, image)
				.setRelativeContentPos(1f)
				.setRelativeHandlePos(0.5f);
				
		
		
		BarCostume costume1 = new BarCostume();
		costume1.background = Drawable.newDrawable(imageTexture.getTextureRegion("button_up_63"));
		costume1.progress = Drawable.newDrawable(imageTexture.getTextureRegion("button_down_63"));
		
		mTestProgressBar = new ProgressBar(costume1)
			.moveTo(50f, 50f)
			.setValue(40f)
			.setInterpolator(new AccelerateInterpolator())
			.addEventListener(new TouchListener() {
				@Override
				public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
					mTestProgressBar.setValue(80f, 1500);
				}
			});
		
		SlidingBarCostume costume2 = new SlidingBarCostume();
		costume2.background = Drawable.newDrawable(imageTexture.getTextureRegion("button_up_63"));
		costume2.knob = Drawable.newDrawable(imageTexture.getTextureRegion("cityicon_profit_down"));
		
		SlidingBar sliderBar = new SlidingBar(costume2)
			.sizeTo(150f, 40f)
			.moveTo(350f, 50f)
			.setMax(60f)
			.setValue(30f);
		
		//Group<?> group = getStage().getStageGroup();
		//group.setMatrixToChildren(true);
		//group.pivotTo(0f, 0f).rotateBy(50f);
		
		//group.addChild(new Image());
		
		getStage().addFloor(/*0*/)
			//.addChild(mBackGroundImage)
			.addChild(mTestImage)
			//.addChild(temp)
			//.addChild(nineImage)
			.addChild(rectImage)
			//.addChild(scrollPane)
			.addChild(mDropDownBox)
			.addChild(itemButton0)
			.addChild(itemButton1)
			.addChild(itemButton2)
			.addChild(itemButton3)
			.addChild(itemButton4)
			.addChild(listBox)
			.addChild(buttonGroup)
			.addChild(drawer)
			.addChild(mTestProgressBar)
			.addChild(sliderBar)
			.addChild(new Extra() {
				
				@Override
				public void update(long time) {
					Core.APP.debug("isFlinging : " + scrollPane.isFlinging());
					//Core.APP.debug("isScrolling : " + scrollPane.isScrolling());
				}
				
				@Override
				public void draw(Batch batch, float parentAlpha) {
				}
			})
			.debugAll();
		
		Group<?> group = getStage().getFloor(0).getFloorGroup();
		//group.setDrawable(Drawable.newDrawable(backgroundRegion));
		//group.moveTo(100, 0);
		group.addAction(new RotateBy(50f));//.setTransformMatrixApplied(true);
		
		OrthoCamera camera = new OrthoCamera(/*50f, 50f*/);
		
		//camera.rotate(10f);
		//camera3D.translate(2f, 0f);
		
		
		
		final TextureRegion fontRegion1 = fontTexture.getTextureRegion(R.string.label_city_economic_indicator);
		final TextureRegion fontRegion2 = fontTexture.getTextureRegion(R.array.label_array_outline_red_15, Constant.LABEL_INDEX_COMMA);

		SLabel label1 = new SLabel(Drawable.newDrawable(fontRegion1))
				.moveTo(100, 400)
				.setColor(1f, 1f, 0f, 0f)
				.addAction(new FadeIn(2000).setRepeatCount(Action.INFINITE));
		
		SLabel label2 = new SLabel(R.array.label_array_outline_red_15, Constant.LABEL_INDEX_COMMA, fontTexture)
				.moveTo(200, 400);
		
		SLabel label3 = new SLabel(R.string.label_city_economic_indicator, fontTexture)
				.moveTo(100, 300).south()
				.addAction(new RotateBy(360f, 6000));

		SLabel label4 = new SLabel(R.string.label_city_economic_indicator, fontTexture)
				.moveTo(100, 200).right();
		
		CLabel label5 = new CLabel("$(22,012)", R.array.label_array_outline_red_15, fontTexture)
				.moveTo(300, 400);
		
		label6 = new CLabel("$(22,012)", R.array.label_array_outline_red_15, fontTexture)
				.moveTo(300, 300).south()
				.addAction(new MoveTo(100, 10, 2000))
				.addAction(new PivotTo(0.5f, 0.8f).setStartOffset(2000))
				.addAction(new ScaleBy(2f, 2f, 2000).setStartOffset(2000));
		
		final CLabel label7 = new CLabel("$(22,012)", R.array.label_array_outline_white_15, fontTexture)
				.moveTo(300, 200).pivotTo(0f, 1f).right()
				.addAction(new RotateBy(360f, 3000));
		
		Typeface typeface = Typeface.createFromAsset(Core.APP.getActivity().getAssets(),
				"HYHWPEQ.TTF");
		
		DLabel.sDefaultPaint.setTextSize(20);
		DLabel.sDefaultPaint.setTypeface(typeface);
		DLabel.sDefaultPaint.setAntiAlias(true);
		DLabel.sDefaultPaint.setShadowLayer(2, 0, 0, Color4.BLACK);
		DLabel.sDefaultPaint.setColor(Color4.WHITE);
		
		label8 = new DLabel(R.string.label_build, fontTexture)
				.moveTo(500, 200).setTextSize(10).setColor(Color4.YELLOW).pivotTo(0f, 1f)
				.addAction(new RotateBy(360f, 2000).setRepeatCount(Action.INFINITE));
		//label8.setText("바보");
		
		
		label9 = new DLabel("웃기시네", fontTexture)
				.moveTo(550, 200).scaleBy(0.5f, 0.5f).setColor(Color4.YELLOW);
		
		label10 = new DLabel("김인식", fontTexture);
		
		money = 3452;
		
		mTestActor = new Extra()
				.setDrawable(Drawable.newDrawable(imageTexture.getTextureRegion("mainmenu_background")))
				.sizeTo(200f, 200f).moveTo(100f, 250f);
		
		getStage().addFloor(/*1*/)
			//.setCamera(camera3D)
			.addChild(new Extra() {
				
				@Override
				public void update(long time) {
					//getStage().getFloor(1).setCamera(null);
					
					Transition tran = Director.getInstance().getTransition();
					if(tran != null && tran.hasStarted()) {
						
					}
					
					String string = "$(" + money +")..km";
					money++;
					if(money > 4000)
						label7.setColor(1f, 1f, 0f, 0f);
					label7.setText(string);
				}
				
				@Override
				public void draw(Batch batch, float parentAlpha) {
					batch.draw(popupRegion, 10, 10, 10, 10);
					//batch.draw(fontTexture.getAsTextureRegion(), 0, 0, 640, 400, false, false, true);
					//batch.draw(fontRegion, 0, 100);
				}
			})
			.addChild(label1)
			.addChild(label2)
			.addChild(label3)
			.addChild(label4)
			.addChild(label5)
			.addChild(label6)
			.addChild(label7)
			.addChild(label8)
			//.addChild(mTestActor)
			.debugAll();
		
		getStage().addFloor(/*2*/)
				.setCamera(camera)
				.addChild(new Extra() {
					@Override
					public void draw(Batch batch, float parentAlpha) {
						batch.draw(fontTexture.getAsTextureRegion(), 0, 0, 640, 400, false, false, true);
					}
				});
			
		
		Director.getInstance().removeOverlapScenes();
		
		Core.APP.debug(getStage().toString());
	}
	
	@Override
	public void destroy(boolean lifeCycle) {
		label8.dispose();
		label9.dispose();
		label10.dispose();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			//Director.getInstance().changeScene(new ActorTestScene());
			//Core.APP.showToast("안녕", Toast.LENGTH_SHORT);
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			
			final Texture fontTexture = tm.getTexture(R.drawable.font);
			
			
			if(mDropDownBox.getItemList().size() < 3) {
				Core.INPUT.bringUpTextInput(null, -1, new TextInputListener() {
					
					@Override
					public void onInput(String text) {
						mDropDownBox.addItem(0, new DLabel(text, fontTexture, Utils.sOutlineWhite15));
					}
					
					@Override
					public void onCancel() {
					}
				});
			} else {
				mDropDownBox.removeItem(0);
			}
			
			//mDropDownBox.removeItem(0);
			
			
		}
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
