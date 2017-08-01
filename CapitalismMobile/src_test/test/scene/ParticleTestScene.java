package test.scene;

import project.framework.*;
import android.view.*;
import core.scene.*;
import core.scene.stage.actor.widget.*;

@SuppressWarnings("rawtypes")
public class ParticleTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		//mBackGroundImage = new Image(new PointF(0, 0), R.drawable.mainmenu_background);
		
		//mTestImage = new Image(new PointF(0, 100), R.drawable.cell_2x2_apratment1);
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage);
		
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
