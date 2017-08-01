package project.framework.casting;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector.Casting;

public abstract class BaseCasting<T extends Actor<?>> implements Casting<T> {

	protected void ensureArgs(int argLegth, int count) {
		if(argLegth != count)
			throw new IllegalArgumentException("the length of args must be " + count);
	}
}
