package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;

@SuppressWarnings("rawtypes")
public class LabelTestScene extends Scene {

	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		mBackGroundImage = new Image(mBackgroundRegion);
		
		DLabel d1 = new DLabel("efefe", fontTexture).moveTo(100f, 100f);
		
		DLabel d2 = new DLabel(d1).right().moveTo(100f, 200f);
		
		//d1.setText("bbbccc");
		
		// 복사본은 Texture 자체를 수정하는 메서드를 호출할 수 없다.
		//DLabel d3 = new DLabel(d1).setStrikeThruText(true);
		
		SLabel s1 = new SLabel(R.string.label_build, fontTexture).moveTo(200f, 100f);
		
		final SLabel s2 = new SLabel(s1).moveTo(200f, 200f);
		
		final SLabel s3 = new SLabel(s1.getDrawable(), "bbc").moveTo(200f, 300f);
		
		CLabel c1 = new CLabel("101010", R.array.label_array_outline_green_15, fontTexture).moveTo(300f, 100f);
		
		CLabel c2 = new CLabel(c1).center().moveTo(300f, 200f);
		
		CLabel c3 = new CLabel(c1, "010101").moveTo(300f, 300f);
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(d1)
			.addChild(d2)
			.addChild(s1)
			.addChild(s2)
			.addChild(s3)
			.addChild(c1)
			.addChild(c2)
			.addChild(c3)
			.addChild(new Extra() {
				@Override
				public void update(long time) {
					Core.APP.debug("s2 " + s2.getText());
					Core.APP.debug("s3 " + s3.getText());
				}
			});
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}
}
