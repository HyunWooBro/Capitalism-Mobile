package project.game.report;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.table.button.PushButton;

public interface Report {

	public void onShow();

	public void onHide();

	public PushButton getMenuButton();

	public Actor<?> getContent();
}
