package core.scene.stage.actor.action;

public class FloatAction extends Action {

	protected float mFrom;
	protected float mTo;
	
	protected float mValue;
	
	public FloatAction() {
	}
	
	public FloatAction(float to, float from) {
		this(to, from, 0);
	}
	
	public FloatAction(float to, float from, long duration) {
		mTo = to;
		mFrom = from;
		setDuration(duration);
	}
	
	@Override
	protected void apply(float interpolatedTime) {
		float from = mFrom;
		float to = mTo;
		if(from != to) mValue = from + ((to - from) * interpolatedTime);
	}

	public float getFrom() {
		return mFrom;
	}

	public float getTo() {
		return mTo;
	}

	public float getValue() {
		return mValue;
	}

	public void setFrom(float from) {
		mFrom = from;
	}

	public void setTo(float to) {
		mTo = to;
	}

	public void setValue(float value) {
		mValue = value;
	}
}
