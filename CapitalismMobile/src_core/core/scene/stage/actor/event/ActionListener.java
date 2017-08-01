package core.scene.stage.actor.event;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.action.Action;

public abstract class ActionListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		if(!(event instanceof ActionEvent)) return false;
		ActionEvent e = (ActionEvent) event;
		
		Action action = e.getAction();
		switch(e.getActionType()) {
			case ACTION_START:	onStart(e, action, e.getListenerActor());
				break;
			case ACTION_END:		onEnd(e, action, e.getListenerActor());
				break;
			case ACTION_REPEAT:	onRepeat(e, action, e.getListenerActor());
				break;
			case ACTION_CANCEL:	onCancel(e, action, e.getListenerActor());
				break;
		}

		return true;
	}
	
	public void onStart(ActionEvent event, Action action, Actor<?> listener) {
	}

	public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
    }

	public void onRepeat(ActionEvent event, Action action, Actor<?> listener) {
	}
	
	public void onCancel(ActionEvent event, Action action, Actor<?> listener) {
	}

}
