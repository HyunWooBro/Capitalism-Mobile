package project.game.event;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import project.game.Time.DayListener;

import core.utils.Disposable;
import core.utils.SnapshotArrayList;

public class EventManager implements DayListener, Disposable {

	private SnapshotArrayList<Event> mEventList = new SnapshotArrayList<Event>();

	// private List<Event> mActiveEventList = new ArrayList<Event>();

	private VictoryEvent mVictoryEvent;

	/** 싱글턴 인스턴스 */
	private volatile static EventManager sInstance;

	private EventManager() {
	}

	public static EventManager getInstance() {
		if(sInstance == null) {
			synchronized(EventManager.class) {
				if(sInstance == null)
					sInstance = new EventManager();
			}
		}
		return sInstance;
	}

	public void addEvent(Event event) {
		if(event instanceof VictoryEvent) {
			mEventList.remove(event);
			mVictoryEvent = (VictoryEvent) event;
		}
		mEventList.add(event);
	}

	public VictoryEvent getVictoryEvent() {
		return mVictoryEvent;
	}

	public SnapshotArrayList<Event> getEventList() {
		return mEventList;
	}

	public void handleEvent() {
		ListIterator<Event> it = mEventList.begin();
		while(it.hasNext()) {
			Event event = it.next();
			if(event.checkCondition())
				event.fire();
		}
		mEventList.end(it);
	}

	@Override
	public void dispose() {
		sInstance = null;
	}

	@Override
	public void onDayChanged(GregorianCalendar calendar, int year, int month, int day) {
		handleEvent();
	}

}
