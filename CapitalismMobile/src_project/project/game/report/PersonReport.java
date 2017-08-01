package project.game.report;

import org.framework.R;

import core.framework.graphics.Color4;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.widget.table.button.PushButton;

public class PersonReport extends Group<PersonReport> implements Report {

	private PushButton mButton;

	public PersonReport() {
	}

	@Override
	public PushButton getMenuButton() {
		if(mButton == null)
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_person_report);
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
