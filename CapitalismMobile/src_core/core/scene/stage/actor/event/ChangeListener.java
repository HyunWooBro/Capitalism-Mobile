package core.scene.stage.actor.event;

import core.scene.stage.actor.Actor;

public abstract class ChangeListener implements EventListener {
	
	@Override
	public boolean handle(Event event) {
		if(!(event instanceof ChangeEvent)) return false;
		ChangeEvent e = (ChangeEvent) event;
		onChanged(e, e.getTargetActor(), e.getListenerActor());
		return true;
	}
	
	public abstract void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener);
}