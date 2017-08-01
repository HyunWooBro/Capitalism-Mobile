package core.scene.stage.actor.event;

import core.scene.stage.actor.action.Action;

public class ActionEvent extends Event {
	
	public enum ActionType {
		ACTION_START, 
		ACTION_END, 
		ACTION_REPEAT, 
		ACTION_CANCEL, 
	}
	
	private ActionType mActionType;

	private Action mAction;
	
	public ActionEvent(ActionType type, Action action) {
		mActionType = type;
		mAction = action;
	}

	public Action getAction() {
		return mAction;
	}

	public void setAction(Action action) {
		mAction = action;
	}

	public ActionType getActionType() {
		return mActionType;
	}

	public void setActionType(ActionType actionType) {
		mActionType = actionType;
	}
}
