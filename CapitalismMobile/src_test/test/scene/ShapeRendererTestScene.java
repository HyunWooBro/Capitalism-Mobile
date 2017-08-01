package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.OrthoCamera;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.ShapeRenderer.ShapeType;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.widget.Image;

@SuppressWarnings("rawtypes")
public class ShapeRendererTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);

		final ShapeRenderer renderer = new ShapeRenderer();
		//renderer.setBlendEnabled(true);
		
		final float[] array = new float[] {50, 50, 120, 100, 200, 50};
		
		// 디버그 플로워
		getStage().addFloor(/*3*/)
			.addChild(mBackGroundImage)
			.addChild(mTestImage)
			.addChild(new Extra() {
				@Override
				public void update(long time) {

				}
				
				@Override
				public void draw(Batch batch, float parentAlpha) {
					batch.flush();
					OrthoCamera camera = getFloor().getCamera();
					renderer.setProjectionMatrix(batch.getProjectionMatrix());
					renderer.setLineWidth(5);
					renderer.setPointSize(10);
					renderer.setColor(1f, 1, 0, 0);
					
					renderer.begin(ShapeType.LINE);
					//renderer.drawRect(50, 50, 200, 250);
					
					renderer.drawRectLine(200, 200, 400, 300, 5);
					renderer.drawRectLine(200, 200, 20, 150, 5);
					renderer.drawRectLine(200, 200, 400, 200, 5);
					renderer.drawRectLine(200, 200, 200, 300, 5);
					renderer.drawRectLine(200, 200, 300, 50, 5);
					
					renderer.drawCircle(300, 300, 50);
					renderer.drawArc(100, 100, 50, 100, -50, true);
					renderer.drawPolyline(array);
					renderer.drawLine(120, 120, 200, 70);
					renderer.drawEllipse(450, 50, 150, 50);
					renderer.setColor(1f, 0, 1, 0);
					renderer.drawArc(450, 50, 150, 150, 0, 100);
					
					renderer.drawRoundRect(50, 50, 200, 100, 20, 20);
					
					renderer.end();
					
					
					renderer.begin(ShapeType.FILLED);
					renderer.drawRoundRect(50, 250, 200, 100, 20, 20);
					renderer.drawEllipse(450, 250, 150, 50);
					renderer.drawArc(450, 250, 150, 50, 100, 100);
					renderer.end();
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
