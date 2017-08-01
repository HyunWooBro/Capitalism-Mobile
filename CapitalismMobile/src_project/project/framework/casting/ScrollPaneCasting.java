package project.framework.casting;

import org.framework.R;

import core.framework.Core;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.ScrollPane;
import core.scene.stage.actor.widget.ScrollPane.ScrollPaneCostume;

public class ScrollPaneCasting extends BaseCasting<ScrollPane> {

	@Override
	public ScrollPane cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}

		throw new IllegalArgumentException("No such style found.");
	}

	private ScrollPane style_default(Object[] args) {
		ensureArgs(args.length, 1);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture texture = tm.getTexture(R.drawable.temp);

		TextureRegion hScrollKnobRegion = texture.getTextureRegion("scrollknob_horizontal");
		NinePatch hScrolKnobPatch = new NinePatch(hScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion vScrollKnobRegion = texture.getTextureRegion("scrollknob_vertical");
		NinePatch vScrolKnobPatch = new NinePatch(vScrollKnobRegion, 9, 9, 9, 9);

		ScrollPaneCostume costume = new ScrollPaneCostume();
		costume.hScrollKnob = Drawable.newDrawable(hScrolKnobPatch);
		costume.hScrollKnob.setAlpha(0.8f);
		costume.vScrollKnob = Drawable.newDrawable(vScrolKnobPatch);
		costume.vScrollKnob.setAlpha(0.8f);

		ScrollPane scrollPane = new ScrollPane((Actor<?>) args[0], costume);

		return scrollPane;
	}
}
