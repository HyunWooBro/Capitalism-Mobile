package core.scene.transition;

import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.scene.Scene;

public class TransitionDelay extends Transition {
	
	public TransitionDelay(long duration) {
		this(duration, null);
	}

	public TransitionDelay(long duration, Scene<?> scene) {
		super(scene, null);
		mStartOffset = duration;
	}
	
	@Override
	protected void renderToScreen(Batch batch, Texture currentScene,
			Texture nextScene, float interpolatedTime) {

		batch.setColor(1f, 1f, 1f, 1f);
		batch.draw(currentScene.getAsTextureRegion(), 
				0f, 0f, 1f, 1f, 
				false, true);
	}
}