package core.scene.transition;

import android.view.animation.Interpolator;

import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.scene.Scene;

public class TransitionMorph extends Transition {
	
	public TransitionMorph(long duration) {
		this(0, duration, null, null);
	}
	
	public TransitionMorph(long duration, Interpolator interpolator) {
		this(0, duration, interpolator, null);
	}
	
	public TransitionMorph(long startOffset, long duration) {
		this(startOffset, duration, null, null);
	}
	
	public TransitionMorph(long startOffset, long duration, Interpolator interpolator) {
		this(startOffset, duration, interpolator, null);
	}

	public TransitionMorph(long duration, Scene<?> scene) {
		this(0, duration, null, scene);
	}
	
	public TransitionMorph(long duration, Interpolator interpolator, Scene<?> scene) {
		this(0, duration, interpolator, scene);
	}
	
	public TransitionMorph(long startOffset, long duration, Scene<?> scene) {
		this(startOffset, duration, null, scene);
	}
	
	public TransitionMorph(long startOffset, long duration, Interpolator interpolator, 
			Scene<?> scene) {
		super(scene, interpolator);
		mStartOffset = startOffset;
		setDuration(duration);
	}
	
	@Override
	protected void renderToScreen(Batch batch, Texture currentScene,
			Texture nextScene, float interpolatedTime) {
		
		// TODO 구현 필요
	}
}