package core.scene.transition;

import android.view.animation.Interpolator;

import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.scene.Scene;

public class TransitionWipeOutRight extends Transition{
	
	public TransitionWipeOutRight(long duration) {
		this(0, duration, null, null);
	}
	
	public TransitionWipeOutRight(long duration, Interpolator interpolator) {
		this(0, duration, interpolator, null);
	}
	
	public TransitionWipeOutRight(long startOffset, long duration) {
		this(startOffset, duration, null, null);
	}
	
	public TransitionWipeOutRight(long startOffset, long duration, Interpolator interpolator) {
		this(startOffset, duration, interpolator, null);
	}

	public TransitionWipeOutRight(long duration, Scene<?> scene) {
		this(0, duration, null, scene);
	}
	
	public TransitionWipeOutRight(long duration, Interpolator interpolator, Scene<?> scene) {
		this(0, duration, interpolator, scene);
	}
	
	public TransitionWipeOutRight(long startOffset, long duration, Scene<?> scene) {
		this(startOffset, duration, null, scene);
	}
	
	public TransitionWipeOutRight(long startOffset, long duration, Interpolator interpolator, 
			Scene<?> scene) {
		super(scene, interpolator);
		mStartOffset = startOffset;
		setDuration(duration);
	}
	
	@Override
	protected void renderToScreen(Batch batch, Texture currentScene,
			Texture nextScene, float interpolatedTime) {
		
		float x = interpolatedTime;
		
		batch.setColor(1f, 1f, 1f, 1f);
		batch.draw(nextScene.getAsTextureRegion(), 
				0f, 0, 1f, 1f, 
				false, true);
		batch.setColor(1f, 1f, 1f, 1f);
		batch.draw(currentScene.getAsTextureRegion(), 
				x, 0f, 1f, 1f, 
				false, true);
	}
}
