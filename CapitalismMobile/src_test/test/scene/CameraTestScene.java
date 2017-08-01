package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;
import android.view.animation.BounceInterpolator;

import core.framework.Core;
import core.framework.graphics.OrthoCamera;
import core.framework.graphics.OrthoCamera.TargetMode;
import core.framework.graphics.OrthoCamera3D;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.table.button.Button.ButtonCostume;
import core.scene.stage.actor.widget.table.button.PushButton;

@SuppressWarnings("rawtypes")
public class CameraTestScene extends Scene {

	private Image mBackGroundImage;
	
	private Image mTestImage;
	
	private PushButton mTransparentButton;
	
	float a;
	
	OrthoCamera3D camera3D = new OrthoCamera3D();

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion);
				//.moveTo(0, 100);
		
		ButtonCostume costume = new ButtonCostume();
		costume.up = Drawable.newDrawable(mImageTexture.getTextureRegion("cell_2x2_apratment1"));

		// 카메라의 이동을 가이드할 투명한 Button을 만들어 Action을 설정한다. 이 역할은 
		// ButtonToRemove 뿐만 아니라 어떠한 Actor라도 수행할 수 있다.
		mTransparentButton = new PushButton(costume)
			//.moveTo(500, 500);
			//.setVisible(false)
			.addAction(new MoveBy(600f, 400f, 3500)
					.setInterpolator(new BounceInterpolator()));
		
		// 임의의 카메라를 만들어 타겟으로 위의 투명한 버튼을 설정한다.
		final OrthoCamera camera = new OrthoCamera()
			.setTarget(mTransparentButton)
			.setTargetMode(TargetMode.CHASE)
			.setChaseSpeed(2f)
			.setRotation(40);
		
		// 이 코드를 실행하면 위의 카메라를 디폴트 카메라로 설정하여 오버랩된 Scene에도
		// 이 카메라가 적용된다.
		//Director.getInstance().setDefaultCamera(camera3D);
		
		
		camera3D.setPivot(0, 0);
		
		//camera3D.setAxisYDown(false);
		camera3D.translate(0, 0);

		//camera3D.setDistance(-2);
		
		//camera3D.dotWithNormal(5, 0, 0);
		
		//camera3D.setZoom(0.7f);
		
		camera3D.rotateZ(30);

		
		//camera3D.setLocation(1/72f, 0, -8);
		
		//camera3D.setPivot(0.5f, 0.5f);
		
		/*
		OrthoCamera cam = new OrthoCamera();
		cam.rotateY(360 * interpolatedTime);
		Matrix matrix = t.getMatrix();
		cam.getMatrix(matrix);
		matrix.preTranslate(-cx, -cy);
		matrix.postTranslate(cx, cy);*/
		
		
		// 원하는 Floor에 위의 카메라를 설정한다. 
		getStage().addFloor()
			.setCamera(camera3D)
			.addChild(new Extra() {

				@Override
				public void update(long time) {
					camera3D.rotate(0, 0.5f, 0);
					
					//camera3D.ro
					
					//a += 0.01f;
					
					//camera3D.translate(0, 0, a);
					
					//camera3D.setZoom(a);
					
					//camera3D.translate(a, 0);
				}
				
				@Override
				public void draw(Batch batch, float parentAlpha) {
					batch.setProjectionMatrix(camera3D.getCombinedMatrix());
				}
			})
			.addChild(mBackGroundImage.moveTo(50, 50).sizeTo(540, 300).setFlipY(true));
			//.addActor(mTestImage)
			//.addActor(mTransparentButton);
		
		getStage().addFloor()
			.setCamera(camera)
			.addChild(mTestImage)
			.addChild(mTransparentButton)
			.addChild(new Extra() {
				
				@Override
				protected void prepare() {
					super.prepare();
					addEventListener(new TouchListener() {
						@Override
						public void onTouch(TouchEvent event, float x, float y, Actor<?> listener) {
							camera.rotate(20);
						}
					});
				}
				
				@Override
				public void update(long time) {
					
				}
			});
		
		// 오버랩된 Scene은 어떠한 카메라 설정이 없으므로 디폴트 카메라를 사용한다.
		//Director.GetInstance().overlapSceneOnTop(new ActionTestScene());
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			Director.getInstance().removeOverlapScenes();
			Director.getInstance().changeScene(new CameraTestScene());
		}
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
