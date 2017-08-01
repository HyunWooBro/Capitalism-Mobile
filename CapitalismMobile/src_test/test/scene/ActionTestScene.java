package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.ActionSet;
import core.scene.stage.actor.action.absolute.AlphaTo;
import core.scene.stage.actor.action.absolute.FadeOut;
import core.scene.stage.actor.action.absolute.MoveTo;
import core.scene.stage.actor.action.relative.MoveBy;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.widget.Image;

/**
 * Action 사용법을 테스트한다.
 * 
 * @author 김현우
 */
@SuppressWarnings("rawtypes")
public class ActionTestScene extends Scene{
	
	private Image mBackGroundImage;
	
	private Image mTestImage;
	private Image mTestImage2;
	private Image mTestImage3;
	private Image mTestImage4;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);
		
		///////////////////////////////////////////////////////////////////////////////////////////
		/* 단일 Action 사용 예 	*/
		///////////////////////////////////////////////////////////////////////////////////////////
		
		// 0.9초동안 페이드아웃을 하고 다시 반대 동작을 무한대(infinite)로 하는 액션
		// 중간에 다른 액션이 이 액션을 취소한다. setFillAfter(...)에 의해 취소한 순간의 
		// 액션이 계속 적용된다.
		final Action fadeOut = new FadeOut(900)
				.setRepeatCount(Action.INFINITE)
				.setRepeatMode(Action.REVERSE);
		
		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100)
				.addAction(fadeOut)
				.addEventListener(new ActionListener() {
					@Override
					public void onCancel(ActionEvent event, Action action, Actor<?> listener) {
						Core.APP.debug("action canceled");
					}
				});
		
		
		//-----------------------------------------------------------------------//
				
		// 액션이 현재보다 늦게 시작하는 경우(아래는 offSet 1초) fillBefore을 false로 하면
		// 액션이 실제로 시작해야 적용되기 시작한다. fillBefore은 디폴트로 true이다.
		Action moveTo2 = new MoveTo(150, 200, 1500)
				.setStartOffset(1000)
				.setFillBefore(false)
				.setInterpolator(new OvershootInterpolator());
		
		mTestImage2 = new Image(mApartmentRegion)
				.moveTo(150, 100)
				.addAction(moveTo2);
		
		//-----------------------------------------------------------------------//
		
		// 다음과 같이 Action을 임시 객체로 넣을 수도 있다. 여러 Action이 
		// 한꺼번에 적용되는 경우 tag로 구분할 수 있다.
		mTestImage3 = new Image(mApartmentRegion)
				.moveTo(300, 100)
				.addAction(new MoveTo(500, 0, 1500)
						.setInterpolator(new OvershootInterpolator()).setTag("1"))
				.addEventListener(new ActionListener() {
					@Override
					public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
						// 액션이 종료되면 mTestImage의 액션을 취소한다.
						// 여기서는 Action이 한개이므로 반드시 이렇게 할 필요는 없다.
						if(action.tag.equals("1"))
							mTestImage.cancelActions();
					}
				});
		
		
		///////////////////////////////////////////////////////////////////////////////////////////
		/* ActionSet 사용 예 */
		///////////////////////////////////////////////////////////////////////////////////////////
		
		Action fadeTo4 = new AlphaTo(0.5f, 500)
				.setStartOffset(1000);
		
		Action moveBy4 = new MoveBy(100f, 50f, 1000);
				//.scaleCurrentDuration(2.0f);
		
		ActionSet set4 = new ActionSet(true, true)
				.setInterpolator(new AnticipateInterpolator())
				.addAction(fadeTo4)
				.addAction(moveBy4);
		
		mTestImage4 = new Image(mApartmentRegion)
				.moveTo(450, 100)
				.addAction(set4)
				.addAction(new MoveBy(-200, -100, 2000).setStartAfter(true));
		
		//-----------------------------------------------------------------------//
		
		getStage().addFloor()
			//.addElement(mBackGroundImage)
			.addChild(mTestImage)
			.addChild(mTestImage2)
			.addChild(mTestImage3)
			.addChild(mTestImage4);
	}
	

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
			//Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

	@Override
	public void destroy(boolean lifeCycle) {
	}

}
