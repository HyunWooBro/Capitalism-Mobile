package project.game.event;

import java.util.Calendar;

public abstract class Event {
	// 이벤트 종류 중에 victory condition도 넣자.

	private Calendar mStartDate;
	private Calendar mEndDate;

	private boolean mCanceled;

	public abstract boolean checkCondition();

	public abstract void fire();

}
