package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;
import android.view.animation.LinearInterpolator;

import core.framework.Core;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.ActionSet;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.action.absolute.MoveTo;
import core.scene.stage.actor.action.relative.ScaleBy;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.FloatingWindow;
import core.scene.stage.actor.widget.table.window.Window.WindowCostume;

@SuppressWarnings("rawtypes")
public class WindowTestScene extends Scene{
	
	private FloatingWindow mWindow;
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

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
				.moveTo(0, 100)
				.sizeTo(50, 70);
		
		Action sizeBy = new ScaleBy(0.1f, 0.2f, 400)
				.setRepeatCount(Action.INFINITE)
				.setRepeatMode(Action.REVERSE);
		
		Action moveTo = new MoveTo(470f, 300f, 400)
				.setRepeatCount(Action.INFINITE)
				.setRepeatMode(Action.REVERSE);
		
		Action fadeOut = new FadeOut(400)
				.setInterpolator(new LinearInterpolator())
				.setRepeatCount(Action.INFINITE)
				.setRepeatMode(Action.REVERSE);
		
		ActionSet set = new ActionSet(false)
				.addAction(sizeBy)
				.addAction(moveTo)
				.addAction(fadeOut);
		
		DLabel title = new DLabel("윈도우", mFontTexture);
		
		final TextureRegion popupRegion = mImageTexture.getTextureRegion("ui_choice_popup_info");
		NinePatch patch = new NinePatch(popupRegion, 10, 10, 10, 10);
		
		
		final TextureRegion windowRegion = mWindowTexture.getTextureRegion("window_basic");
		NinePatch windowPatch = new NinePatch(windowRegion, 5, 31, 5, 5);
		
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
						Core.APP.vibrate(1000);
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
		
		WindowCostume costume = new WindowCostume();
		costume.background = Drawable.newDrawable(windowPatch);
		
		mWindow = new FloatingWindow(costume)
			.moveTo(120f, 150f)
			//.sizeTo(400, 76)
			.setMovable(true)
			.setTitle(title)
			//.startAction(new ScaleBy(new PointF(2f, 2f), 1000))
			//.startAction(new MoveBy(new PointF(100, 0), 1000))
			//.setDrawable(Drawable.newDrawable(windowPatch))
			//.setBorderSize(7f)
			.setTitleHeight(25f)
			.sizeTo(200f, 150f);
			//.addAction(set);
		
		LayoutTable buttonTable = mWindow.getButtonTable();
		buttonTable.addCell(minimizeButton).padRight(5f);
		buttonTable.addCell(maximizeButton).padRight(5f);
		buttonTable.addCell(closeButton).padRight(5f);

		mWindow.addCell(mTestImage);
		
		//mWindow.debug();
		
		//mWindow.debugAll();
			
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mWindow);
		
		Director.getInstance().removeOverlapScenes();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU)
			Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

	@Override
	public void destroy(boolean lifeCycle) {
	}

}
