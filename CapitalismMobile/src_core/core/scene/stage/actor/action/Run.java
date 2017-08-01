package core.scene.stage.actor.action;

public class Run extends Action {

	private Runnable mRunnable;

    public Run(Runnable runnable) {
    	mRunnable = runnable;
    }
    
    /** @deprecated Run은 지속시작을 가질 수 없다. */
    @Deprecated
    @Override
    public Action setDuration(long durationMillis) {
    	throw new UnsupportedOperationException("Run can't have duration");
    }

    @Override
    protected void apply(float interpolatedTime) {
    	if(interpolatedTime > 0f) mRunnable.run();
    }

}
