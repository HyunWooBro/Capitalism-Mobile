package project.framework.casting;

import java.util.Arrays;

import org.framework.R;

import project.framework.Utils;

import core.framework.Core;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.PlayMode;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.button.Button.ButtonCostume;
import core.scene.stage.actor.widget.table.button.PushButton;

public class PushButtonCasting extends BaseCasting<PushButton> {

	@Override
	public PushButton cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}
		
		if(style.equals("dynamic_text")) {
			return style_dynamic_text(args);
		}
		
		if(style.equals("static_text")) {
			return style_static_text(args);
		}
		
		if(style.equals("menu_item")) {
			return style_menu_item(args);
		}
		
		if(style.equals("menu_info")) {
			return style_menu_info(args);
		}
		
		if(style.equals("up")) {
			return style_up(args);
		}
		
		if(style.equals("up_down")) {
			return style_up_down(args);
		}
		
		if(style.equals("up_down_checked")) {
			return style_up_down_checked(args);
		}

		throw new IllegalArgumentException("No such style found.");
	}

	private PushButton style_default(Object[] args) {
		ensureArgs(args.length, 0);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture buttonTexture = tm.getTexture(R.drawable.button_default);

		TextureRegion region = buttonTexture.getAsTextureRegion();
		NinePatch patch = new NinePatch(region, 10, 10, 11, 10);

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(patch);

		PushButton button = new PushButton(costume).setStartTouchAction(
				Utils.createButtonStartTouchAction()).setFinalTouchAction(
				Utils.createButtonFinalTouchAction());

		return button;
	}

	private PushButton style_dynamic_text(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture fontTexture = tm.getTexture(R.drawable.font);
		Texture buttonTexture = tm.getTexture(R.drawable.button_default);

		final DLabel buttonLabel = new DLabel((String) args[0], fontTexture, Utils.sOutlineWhite15);

		TextureRegion region = buttonTexture.getAsTextureRegion();
		NinePatch patch = new NinePatch(region, 10, 10, 11, 10);

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(patch);

		PushButton button = new PushButton(costume).setStartTouchAction(
				Utils.createButtonStartTouchAction()).setFinalTouchAction(
				Utils.createButtonFinalTouchAction());

		button.addCell(buttonLabel);
		return button;
	}

	private PushButton style_static_text(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture fontTexture = tm.getTexture(R.drawable.font);
		Texture buttonTexture = tm.getTexture(R.drawable.button_default);

		SLabel buttonLabel = new SLabel((Integer) args[0], fontTexture);

		TextureRegion region = buttonTexture.getAsTextureRegion();
		NinePatch patch = new NinePatch(region, 10, 10, 11, 10);

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(patch);

		PushButton button = new PushButton(costume).setStartTouchAction(
				Utils.createButtonStartTouchAction()).setFinalTouchAction(
				Utils.createButtonFinalTouchAction());

		button.addCell(buttonLabel);
		return button;
	}

	private PushButton style_menu_item(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas_mainmenu);
		Texture fontTexture = tm.getTexture(R.drawable.font_menu);
		Texture buttonTexture = tm.getTexture(R.drawable.button_default);

		Animation itemAnimation = new Animation(30, Arrays.asList(null,
				imageTexture.getTextureRegion("menu_item_flash_01"),
				imageTexture.getTextureRegion("menu_item_flash_02"),
				imageTexture.getTextureRegion("menu_item_flash_03"),
				imageTexture.getTextureRegion("menu_item_flash_04"),
				imageTexture.getTextureRegion("menu_item_flash_05"),
				imageTexture.getTextureRegion("menu_item_flash_06"),
				imageTexture.getTextureRegion("menu_item_flash_07"),
				imageTexture.getTextureRegion("menu_item_flash_08"), null))
				.setPlayMode(PlayMode.REPEAT).setFrameDuration(0, 2000).setFrameDuration(9, 3000)
				.setPatternIndex(1);

		SLabel buttonLabel = new SLabel((Integer) args[0], fontTexture).scaleTo(1.5f);

		TextureRegion region = buttonTexture.getAsTextureRegion();
		NinePatch patch = new NinePatch(region, 10, 10, 11, 10);

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(patch);

		PushButton button = new PushButton(costume).sizeTo(160f, 60f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addChild(new Image(itemAnimation).setFillParent(true));

		button.addCell(buttonLabel);
		return button;
	}

	private PushButton style_menu_info(Object[] args) {
		ensureArgs(args.length, 0);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas_mainmenu);

		Animation infoAnimation = new Animation(50, Arrays.asList(null,
				imageTexture.getTextureRegion("mainmenu_info_flash_01"),
				imageTexture.getTextureRegion("mainmenu_info_flash_02"),
				imageTexture.getTextureRegion("mainmenu_info_flash_03"),
				imageTexture.getTextureRegion("mainmenu_info_flash_04"),
				imageTexture.getTextureRegion("mainmenu_info_flash_05"),
				imageTexture.getTextureRegion("mainmenu_info_flash_06"),
				imageTexture.getTextureRegion("mainmenu_info_flash_07"), null))
				.setPlayMode(PlayMode.REPEAT).setFrameDuration(0, 2500).setFrameDuration(8, 3500)
				.setPatternIndex(1);

		TextureRegion infoRegion = imageTexture.getTextureRegion("mainmenu_info");

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(infoRegion);

		PushButton button = new PushButton(costume)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addChild(new Image(infoAnimation).setFillParent(true)).pack();

		return button;
	}

	private PushButton style_up(Object[] args) {
		ensureArgs(args.length, 1);

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(args[0]);

		PushButton button = new PushButton(costume);

		return button;
	}

	private PushButton style_up_down(Object[] args) {
		ensureArgs(args.length, 2);

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(args[0]);
		costume.down = Drawable.newDrawable(args[1]);

		PushButton button = new PushButton(costume);

		return button;
	}

	private PushButton style_up_down_checked(Object[] args) {
		ensureArgs(args.length, 3);

		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(args[0]);
		costume.down = Drawable.newDrawable(args[1]);
		costume.checked = Drawable.newDrawable(args[2]);

		PushButton button = new PushButton(costume);

		return button;
	}

}
