package project.framework.casting;

import org.framework.R;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.action.FloatAction;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.bar.SlidingBar;
import core.scene.stage.actor.widget.bar.SlidingBar.SlidingBarCostume;

public class SlidingBarCasting extends BaseCasting<SlidingBar> {

	@Override
	public SlidingBar cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}

		throw new IllegalArgumentException("No such style found.");
	}

	private SlidingBar style_default(Object[] args) {
		ensureArgs(args.length, 0);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture texture = tm.getTexture(R.drawable.atlas_bar);

		TextureRegion hScrollKnobRegion = texture.getTextureRegion("bar_background");
		NinePatch hScrolKnobPatch = new NinePatch(hScrollKnobRegion, 4, 10, 4, 10);

		SlidingBarCostume costume = new SlidingBarCostume();
		costume.background = Drawable.newDrawable(hScrolKnobPatch);
		costume.knob = Drawable.newDrawable(texture.getTextureRegion("bar_knob"));
		costume.progress = Drawable.newDrawable(texture.getTextureRegion("bar_progress"));

		SlidingBar sliderBar = new ScaleSlidingBar(costume).sizeTo(150f, 40f);

		return sliderBar;
	}

	private static class ScaleSlidingBar extends SlidingBar {

		private float mKnobScale = 1f;

		public ScaleSlidingBar(SlidingBarCostume costume) {
			super(costume);
			setStartTouchAction(new ScaleAction(true));
			setFinalTouchAction(new ScaleAction(false));
		}

		@Override
		protected void drawSelf(Batch batch, float parentAlpha) {
			if(mDrawable == getCostume().knob) {
				drawKnob(batch, parentAlpha);
			} else
				super.drawSelf(batch, parentAlpha);
		}

		private void drawKnob(Batch batch, float parentAlpha) {
			float scaleX = getScaleX();
			float scaleY = getScaleY();
			scaleTo(mKnobScale);
			pivotToCenter();
			super.drawSelf(batch, parentAlpha);
			scaleTo(scaleX, scaleY);
		}

		private static class ScaleAction extends FloatAction {

			public ScaleAction(boolean expand) {
				mTo = (expand) ? 1.2f : 1f;
				setDuration(75);
			}

			@Override
			protected void initialize() {
				ScaleSlidingBar bar = (ScaleSlidingBar) mActor;
				mFrom = bar.mKnobScale;
			}

			@Override
			protected void apply(float interpolatedTime) {
				super.apply(interpolatedTime);
				ScaleSlidingBar bar = (ScaleSlidingBar) mActor;
				bar.mKnobScale = mValue;
			}
		}

	}

}
