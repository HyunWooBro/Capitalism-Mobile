package core.scene.stage.actor.action;

public class IntAction extends Action {

	protected int mFrom;
	protected int mTo;
	
	protected int mValue;
	
	public IntAction() {
	}
	
	public IntAction(int to, int from) {
		this(to, from, 0);
	}
	
	public IntAction(int to, int from, long duration) {
		mTo = to;
		mFrom = from;
		setDuration(duration);
	}
	
	@Override
	protected void apply(float interpolatedTime) {
		int from = mFrom;
		int to = mTo;
		if(from != to) mValue = (int) (from + ((to - from) * interpolatedTime));
	}

	public int getFrom() {
		return mFrom;
	}

	public int getTo() {
		return mTo;
	}

	public int getValue() {
		return mValue;
	}

	public void setFrom(int from) {
		mFrom = from;
	}

	public void setTo(int to) {
		mTo = to;
	}

	public void setValue(int value) {
		mValue = value;
	}
}
