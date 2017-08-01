package project.menu;

import org.framework.R;

import project.framework.Utils;
import project.game.GameOptions;

import android.view.KeyEvent;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import core.framework.Core;
import core.framework.app.Preference;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.bar.SlidingBar;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.button.CheckButton;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.transition.TransitionDelay;

public class MenuOptionScene extends Scene<MenuAct> {

	private CheckButton mOptionCheckBox1;
	private CheckButton mOptionCheckBox2;
	private CheckButton mOptionCheckBox3;

	private SlidingBar mScrollSensitiveSlidingBar;

	public MenuOptionScene() {
	}

	@Override
	protected void create() {

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		Image mBackGroundImage = new Image(imageTexture.getTextureRegion("optionmenu_background"));

		Image mWhiteBoardImage = new Image(imageTexture.getTextureRegion("optionmenu_whiteboard"))
				.moveTo(55f, 15f);

		PushButton returnButton = cd
				.cast(PushButton.class, "menu_item", R.string.label_menu_return)
				.moveTo(660f, 335f)
				.addAction(
						Utils.createMoveInAction(-230f, 0f, 300, 300, new OvershootInterpolator(),
								new LinearInterpolator())).addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						fireChangeScene();
					}
				});

		final Preference pref = Core.APP.getPreference();

		mOptionCheckBox1 = cd.cast(CheckButton.class, "default")
				.setChecked(pref.getBoolean(Utils.PREF_SOUND_SWITCH, true))
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						pref.putBoolean(Utils.PREF_SOUND_SWITCH, mOptionCheckBox1.isChecked());
						pref.commit();
					}
				});

		mOptionCheckBox2 = cd.cast(CheckButton.class, "default")
				.setChecked(pref.getBoolean(Utils.PREF_MUSIC_SWITCH, true))
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						pref.putBoolean(Utils.PREF_MUSIC_SWITCH, mOptionCheckBox2.isChecked());
						pref.commit();
					}
				});

		mOptionCheckBox3 = cd.cast(CheckButton.class, "default")
				.setChecked(pref.getBoolean(Utils.PREF_VEHICLE_SWITCH, false))
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						GameOptions.getInstance().setVehicleHidden(mOptionCheckBox3.isChecked());
						pref.putBoolean(Utils.PREF_VEHICLE_SWITCH, mOptionCheckBox3.isChecked());
						pref.commit();
					}
				});

		mScrollSensitiveSlidingBar = cd.cast(SlidingBar.class, "default")
				.setValue(pref.getFloat(Utils.PREF_SCROLL_SENSITIVE, 50f))
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						GameOptions.getInstance().setScrollSensitivity(mScrollSensitiveSlidingBar.getValue());
						pref.putFloat(Utils.PREF_SCROLL_SENSITIVE, mScrollSensitiveSlidingBar.getValue());
						pref.commit();
					}
				});

		LayoutTable table = new LayoutTable().moveTo(300f, 70f);

		table.addCell(mOptionCheckBox1).size(30f, 30f);
		table.row();
		table.addCell(mOptionCheckBox2).size(30f, 30f).padTop(10f);
		table.row();
		table.addCell(mOptionCheckBox3).size(30f, 30f).padTop(10f);
		table.row();
		table.addCell(mScrollSensitiveSlidingBar).size(110f, 30f).padTop(10f);

		getStage().addFloor().addChild(mBackGroundImage).addChild(mWhiteBoardImage)
				.addChild(returnButton).addChild(table);
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
			fireChangeScene();
	}

	private void fireChangeScene() {
		Director.getInstance().changeScene(new TransitionDelay(250, new MenuMainScene()));
	}

}
