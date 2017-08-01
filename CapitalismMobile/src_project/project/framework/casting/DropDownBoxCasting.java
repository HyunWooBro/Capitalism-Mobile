package project.framework.casting;

import java.util.List;

import org.framework.R;

import core.framework.Core;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.Vector2;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.ScrollPane.ScrollPaneCostume;
import core.scene.stage.actor.widget.box.DropDownBox;
import core.scene.stage.actor.widget.box.DropDownBox.DropDownBoxCostume;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.box.ListBox.ListBoxCostume;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.utils.Align;

public class DropDownBoxCasting extends BaseCasting<DropDownBox> {

	@Override
	public DropDownBox cast(String style, Object[] args) {
		if(style.equals("default")) {
			return style_default(args);
		}
		
		throw new IllegalArgumentException("No such style found.");
	}

	private DropDownBox style_default(Object[] args) {
		ensureArgs(args.length, 0);

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		TextureRegion departmentContentBarRegion = imageTexture
				.getTextureRegion("department_content_bar");

		TextureRegion backgroundRegion = imageTexture
				.getTextureRegion("department_commodity_empty");

		Texture texture = tm.getTexture(R.drawable.temp);

		TextureRegion hScrollKnobRegion = texture.getTextureRegion("scrollknob_horizontal");
		NinePatch hScrolKnobPatch = new NinePatch(hScrollKnobRegion, 9, 9, 9, 9);

		TextureRegion vScrollKnobRegion = texture.getTextureRegion("scrollknob_vertical");
		NinePatch vScrolKnobPatch = new NinePatch(vScrollKnobRegion, 9, 9, 9, 9);

		ScrollPaneCostume scrollPaneCostume = new ScrollPaneCostume();
		scrollPaneCostume.background = Drawable.newDrawable(backgroundRegion);
		scrollPaneCostume.hScrollKnob = Drawable.newDrawable(hScrolKnobPatch);
		scrollPaneCostume.hScrollKnob.setAlpha(0.8f);
		scrollPaneCostume.vScrollKnob = Drawable.newDrawable(vScrolKnobPatch);
		scrollPaneCostume.vScrollKnob.setAlpha(0.8f);

		ListBoxCostume listBoxCostume = new ListBoxCostume();
		// listBoxCostume.background = Drawable.newDrawable(backgroundRegion);
		listBoxCostume.divider = Drawable.newDrawable(departmentContentBarRegion);
		listBoxCostume.selector = Drawable.newDrawable(departmentContentBarRegion);
		listBoxCostume.instantSelector = Drawable.newDrawable(departmentContentBarRegion);
		listBoxCostume.instantSelector.setAlpha(0.5f);
		listBoxCostume.scroll = scrollPaneCostume;

		Texture dropDownBoxTexture = tm.getTexture(R.drawable.atlas_dropdownbox);

		NinePatch backgroundPatch = new NinePatch(
				dropDownBoxTexture.getTextureRegion("dropdownbox_background"), 4, 4, 14, 4);

		NinePatch backgroundPressedPatch = new NinePatch(
				dropDownBoxTexture.getTextureRegion("dropdownbox_background_pressed"), 4, 4, 14, 4);

		DropDownBoxCostume dropDownBoxCostume = new DropDownBoxCostume();
		dropDownBoxCostume.background = Drawable.newDrawable(backgroundPatch);
		dropDownBoxCostume.pressedBackground = Drawable.newDrawable(backgroundPressedPatch);
		dropDownBoxCostume.listBox = listBoxCostume;

		DropDownBox box = new DropDownBox(dropDownBoxCostume);

		return box;
	}
}
