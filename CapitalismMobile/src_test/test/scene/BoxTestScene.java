package test.scene;

import java.util.List;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.action.relative.ScaleBy;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.ScrollPane;
import core.scene.stage.actor.widget.SlidingDrawer;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.stage.actor.widget.table.window.FloatingWindow;
import core.scene.stage.actor.widget.table.window.Window.WindowCostume;

@SuppressWarnings("rawtypes")
public class BoxTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	Image image;
	
	FloatingWindow mWindow;

	@Override
	protected void create() {
		
		CastingDirector cd = CastingDirector.getInstance();
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		Texture mFontTexture = tm.getTexture(R.drawable.font);
		Texture mWindowTexture = tm.getTexture(R.drawable.atlas_window);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);
		
		SlidingDrawer drawer = new SlidingDrawer(new Image(mImageTexture.getTextureRegion("commodity_perfume")), 
				new Image(mImageTexture.getTextureRegion("employee_trained")));
		drawer.setRelativeContentPos(1f);
		
		image = new Image(mImageTexture.getTextureRegion("commodity_perfume"))
				.moveTo(200, 200)
				.addEventListener(new TouchListener() {

					@Override
					public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
						return false;
					}

					@Override
					public void onMove(TouchEvent event, float x, float y, Actor<?> listener) {
						//image.moveTo(x, y);
					}

					@Override
					public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
					}

					@Override
					public void onEnter(TouchEvent event, float x, float y, Actor<?> fromActor, Actor<?> listener) {
						Core.APP.debug("x : " + x + " y : " + y);
					}

					@Override
					public void onExit(TouchEvent event, float x, float y, Actor<?> toActor, Actor<?> listener) {
						Core.APP.debug("enter!!");
					}
				});
		
		Image image1 = new Image(mApartmentRegion)
				.sizeTo(50, 100);
		Image image2 = new Image(mApartmentRegion)
				.sizeTo(50, 100);
		Image image3 = new Image(mApartmentRegion)
				.sizeTo(50, 100);
		
		DLabel.sDefaultPaint.setColor(Color4.WHITE);
		DLabel.sDefaultPaint.setTextSize(15);
		
		DLabel label1 = new DLabel("김현우", mFontTexture);
		DLabel label2 = new DLabel("이경희", mFontTexture);
		DLabel label3 = new DLabel("소리꾼", mFontTexture);
		
		TextureRegion untrained = mImageTexture.getTextureRegion("employee_untrained");
		TextureRegion trained = mImageTexture.getTextureRegion("employee_trained");
		
		PushButton button1 = cd.cast(PushButton.class, "up_down_checked", untrained, trained, trained)
				.sizeTo(50, 50)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Core.INPUT.bringUpTextInput(null, -1, null);
					}
				});
		PushButton button2 = cd.cast(PushButton.class, "up_down_checked", untrained, trained, trained)
				.sizeTo(50, 50)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						Director.getInstance().changeScene(new LayoutTestScene());
					}
				});
		PushButton button3 = cd.cast(PushButton.class, "up_down_checked", untrained, trained, trained)
				.sizeTo(50, 50)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						target.addAction(new FadeOut(1000));
					}
				});
		
		
		
		
		
		
		LayoutTable table = new LayoutTable();
		
		LayoutTable table1 = new LayoutTable();
		table1.addCell(image1);
		table1.addCell(label1).top();
		table1.addCell(button1);
		table1.validate();
		//table1.debug();
		//table1.debugTable();
		
		LayoutTable table2 = new LayoutTable();
		table2.all().left().top();
		table2.col(0).bottom().cellPrefHeight(20f);	
		table2.col(1).bottom();	
		table2.col(2).bottom();
		table2.row();
		table2.addCell(image2).rowSpan(2);	
		table2.addCell(label2);	
		table2.addCell(button2).fillY(0.2f);
		table2.row();
		table2.addCell().cellPrefHeight(20f);
		table2.validate();
		//table2.debug();
		//table2.debugTable();
		
		LayoutTable table3 = new LayoutTable().setTag("abcd");
		table3.addCell(image3);
		table3.addCell(label3).top();
		table3.addCell(button3).rowSpan(2);
		//table3.row();
		//table3.addCell(new Image(mBackgroundRegion));
		table3.validate();
		//table3.debug();
		//table3.debugTable();
		
		//table.addCellActor(table1);
		table.row();
		//table.addCellActor(table2);
		table.row();
		table.addCell(table3);
		table.validate();
		table.debug();
		table.debugTable();
		
		ScrollPane scroll = new ScrollPane(table)
				.moveTo(50, 50).sizeTo(200, 150);
		
		//scroll.setFriction(0.01f);
		
		TextureRegion departmentContentBarRegion = mImageTexture.getTextureRegion("department_content_bar");

		Actor<?>[] items = {table1, table2, table3};
		
		/*Actor<?>[] items = {
				new DLabel("웃기셔", mFontTexture), 
				new DLabel("물마셔", mFontTexture), 
				new DLabel("어머나", mFontTexture), 
				new DLabel("이러지 마세요", mFontTexture), 
				new DLabel("갈대입니다", mFontTexture), 
				new DLabel("이건 꼭 사야해", mFontTexture)
		};*/

		ListBox list = new ListBox(null)
				.moveTo(100, 100)
				.set(items)
				.setItemHeight(30f)
				.setDividerHeight(1f)
				//.setDividerDrawable(Drawable.newDrawable(departmentContentBarRegion));
				//.setSelectorDrawable(Drawable.newDrawable(departmentContentBarRegion))
				//.create(200f, 150f)
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						/*ListBox box = (ListBox) actor;
						int index = box.getSelectedPos();
						DLabel label = (DLabel) box.getItem(index);
						Core.APP.showToast(label.getText(), Toast.LENGTH_SHORT);*/
					}
				});
		
		List<TableCell> cellList = list.getTable().getCellList();
		int n = cellList.size();
		for(int i=0; i<n; i++) {
			//cellList.get(i).left();
		}
		
		Texture imageTexture2 = tm.getTexture(R.drawable.temp);
		
		TextureRegion scrollBarRegion = imageTexture2.getTextureRegion("scrollbar");
		NinePatch scrollBarPatch = new NinePatch(scrollBarRegion, 2, 2, 2, 2);
		
		TextureRegion scrollKnobRegion = imageTexture2.getTextureRegion("scrollknob");
		NinePatch scrolKnobPatch = new NinePatch(scrollKnobRegion, 2, 2, 2, 2);
		
//list.getScroll().setScrollDrawable(Drawable.newDrawable(scrollBarPatch), Drawable.newDrawable(scrolKnobPatch));
		
		//list.getTable().debugAll();
		//list.getTable().setDebug(true, 2);
		
		DLabel title = new DLabel("박스 테스트 윈도우", mFontTexture);
		
		TextureRegion minimizeRegion = mWindowTexture.getTextureRegion("window_minimize");
		TextureRegion maximizeRegion = mWindowTexture.getTextureRegion("window_maximize");
		TextureRegion closeRegion = mWindowTexture.getTextureRegion("window_close");
		
		PushButton minimizeButton = cd.cast(PushButton.class, "up", minimizeRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						
					}
				});
		
		PushButton maximizeButton = cd.cast(PushButton.class, "up", maximizeRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						
					}
				});
		
		PushButton closeButton = cd.cast(PushButton.class, "up", closeRegion)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {
					
					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						mWindow.close();
					}
				});
		
		final TextureRegion windowRegion = mWindowTexture.getTextureRegion("window_basic");
		NinePatch windowPatch = new NinePatch(windowRegion, 7, 32, 97, 7);
		
		WindowCostume costume = new WindowCostume();
		costume.background = Drawable.newDrawable(windowPatch);
		
		mWindow = new FloatingWindow(costume)
				.moveTo(120f, 150f).pivotTo(0f, 0f)
				.sizeTo(212f, 188f)
				.setMovable(true)
				.setTitle(title)
				//.startAction(new ScaleBy(new PointF(2f, 2f), 1000))
				//.startAction(new MoveBy(new PointF(100, 0), 1000))
				//.setDrawable(Drawable.newDrawable(windowPatch), false)
				//.setBorderSize(6f)
				.setTitleHeight(25f);
				//.addAction(set);

		mWindow.addCell(list);
		
		LayoutTable buttonTable = mWindow.getButtonTable();
		buttonTable.addCell(minimizeButton).padRight(5f);
		buttonTable.addCell(maximizeButton).padRight(5f);
		buttonTable.addCell(closeButton).padRight(5f);
		
		//mWindow.debug();
		
		
		Image image = new Image(mImageTexture.getTextureRegion("employee_trained"))
				.addEventListener(new TouchListener() {
					@Override
					public boolean onDown(TouchEvent event, float x, float y, Actor<?> listener) {
						event.getTargetActor().setColor(1f, 1f, 0f, 0f);
						event.getTargetActor().addAction(new ScaleBy(1.5f, 1.5f, 1000));
						return true;
					}
					
					@Override
					public void onUp(TouchEvent event, float x, float y, Actor<?> listener) {
						event.getTargetActor().setColor(1f, 1f, 1f, 1f);
					}
				});
		
		
		/*SlidingDrawer drawer2 = new SlidingDrawer(list, 
				image)
				.setRelativeContentPos(1f)
				.setRelativeHandlePos(0.5f);*/

		WindowCostume costume2 = new WindowCostume();
		costume.background = Drawable.newDrawable(windowPatch);
		
		DialogWindow dialog = new DialogWindow(costume2)
				.moveTo(120f, 150f)
				.sizeTo(212f, 188f)
				.setMovable(true)
				.setTitle(title)
				//.setDrawable(Drawable.newDrawable(windowPatch), false)
				//.setBorderSize(6f)
				.setTitleHeight(25f)
				.addButton(button1)
				.addButton(button3)
				.addText(new DLabel("고용하고자 하는 인간을 선택하십시오.", mFontTexture).setColor(Color4.BLACK))
				.pack();
		
		//dialog.debug();  
		
		getStage().addFloor(/*0*/)
			.addChild(new Image() {})
			.addChild(this.image)
			.addChild(dialog)
			.addChild(new Image(untrained).moveTo(140f, 170f))
			//.addActor(scroll)
			.addChild(new Extra() {
				
				@Override
				public void update(long time) {
					//Core.APP.debug("count : " + Core.INPUT.getTouchCount());
				}
				
				@Override
				public void draw(Batch batch, float parentAlpha) {
				}
			});
		
		Core.APP.debug(getStage().toString());
		
		Core.APP.debug("mTag test" + getStage().getActorByTag("abcd"));
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU)
			Director.getInstance().changeScene(new BoxTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}
	
	/*private static class ImageBoxItem extends BoxItem<LayoutTable> {
		
		public ImageBoxItem(LayoutTable image) {
			setItem(image);
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			getItem().setForm(getForm());
			getItem().draw(batch, parentAlpha);
		}
		
	}*/

}
