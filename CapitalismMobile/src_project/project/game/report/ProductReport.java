package project.game.report;

import org.framework.R;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.widget.table.button.PushButton;

public class ProductReport extends Group<ProductReport> implements Report {

	private PushButton mButton;

	@Override
	public PushButton getMenuButton() {
		if(mButton == null)
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_product_report);
		return mButton;
	}

	@Override
	public void onShow() {
	}

	@Override
	public void onHide() {
	}

	@Override
	public Actor<?> getContent() {
		return this;
	}

}
