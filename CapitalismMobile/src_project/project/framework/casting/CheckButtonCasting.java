package project.framework.casting;

import org.framework.R;

import project.framework.Utils;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.action.FloatAction;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.table.button.CheckButton;
import core.scene.stage.actor.widget.table.button.CheckButton.CheckButtonCostume;

public class CheckButtonCasting extends BaseCasting<CheckButton> {

	@Override
	public CheckButton cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}

		throw new IllegalArgumentException("No such style found.");
	}

	private CheckButton style_default(Object[] args) {
		ensureArgs(args.length, 0);

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture buttonTexture = tm.getTexture(R.drawable.button_default);

		Texture checkButtonTexture = tm.getTexture(R.drawable.atlas_checkbutton);

		// TextureRegion region = buttonTexture.getAsTextureRegion();
		// NinePatch patch = new NinePatch(region, 10, 10, 11, 10);

		CheckButtonCostume costume = new CheckButtonCostume();
		// costume.up = Drawable.newDrawable(patch);
		costume.checkedOn = Drawable.newDrawable(checkButtonTexture
				.getTextureRegion("checkbutton_off"));
		costume.checkedOff = Drawable.newDrawable(checkButtonTexture
				.getTextureRegion("checkbutton_off"));

		CheckButton button = new FadeCheckButton(costume).setStartTouchAction(
				Utils.createButtonStartTouchAction()).setFinalTouchAction(
				Utils.createButtonFinalTouchAction());

		return button;
	}

	private static class FadeCheckButton extends CheckButton {

		private float mCheckAlpha = 0f;

		private Image mExtraImage;

		public FadeCheckButton(CheckButtonCostume costume) {
			super(costume);
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture checkButtonTexture = tm.getTexture(R.drawable.atlas_checkbutton);
			mExtraImage = new Image(checkButtonTexture.getTextureRegion("checkbutton_check"));
			setCheckOnAction(new FadeAction(true));
			setCheckOffAction(new FadeAction(false));
			addChild(mExtraImage);
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			Image checkImage = getImage();
			mExtraImage.setBounds(checkImage.getX(), checkImage.getY(), checkImage.getWidth(),
					checkImage.getHeight());
			mExtraImage.setAlpha(mCheckAlpha);
			super.drawChildren(batch, parentAlpha);
		}

		private static class FadeAction extends FloatAction {

			public FadeAction(boolean on) {
				mTo = (on) ? 1f : 0f;
				setDuration(300);
			}

			@Override
			protected void initialize() {
				FadeCheckButton button = (FadeCheckButton) mActor;
				mFrom = button.mCheckAlpha;
			}

			@Override
			protected void apply(float interpolatedTime) {
				super.apply(interpolatedTime);
				FadeCheckButton window = (FadeCheckButton) mActor;
				window.mCheckAlpha = mValue;
			}
		}

	}

}
