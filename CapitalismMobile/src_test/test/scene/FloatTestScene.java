package test.scene;

import project.framework.*;
import android.view.*;
import core.scene.*;
import core.scene.stage.actor.widget.*;

@SuppressWarnings("rawtypes")
public class FloatTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		float normalizedTime;
		long currentTime = 375;
		long mStartTime = currentTime;
		long startOffset = 0;
		long duration = 7000;
		if (duration != 0) {
		    normalizedTime = ((float) (currentTime - (mStartTime + startOffset))) /
		            (float) duration;
		} else {
		    // time is a step-change with a zero duration
			// 기존 안드로이드 Animation 클래스에서는 startOffset이 빠졌는데 
			// 버그로 보인다.
		    normalizedTime = (currentTime < mStartTime + startOffset)? 0.0f : 1.0f;
		}

		final boolean expired = normalizedTime >= 1.0f;
		boolean mMore = !expired;

		//if (!mFillEnabled) normalizedTime = Math.max(Math.min(normalizedTime, 1.0f), 0.0f);


		if ((normalizedTime >= 0.0f) && (normalizedTime <= 1.0f)) {
			duration = 500;
		}

		
		
		
		
		
		getStage().addFloor();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
