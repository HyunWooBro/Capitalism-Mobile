package test.scene;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.Scene;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.ColorTo;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.table.button.Button.ButtonCostume;
import core.scene.stage.actor.widget.table.button.PushButton;

@SuppressWarnings("rawtypes")
public class EditorTestScene extends Scene {

	private Image mBackGroundImage;
	
	private PushButton mItem1Button;
	private PushButton mItem2Button;
	
	private List<Integer> mChatStringIDList;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion backgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion apartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		TextureRegion item1 = mImageTexture.getTextureRegion("mainmenu_item1_n");
		TextureRegion item2 = mImageTexture.getTextureRegion("cell_laboratory1");
		
		mChatStringIDList = new ArrayList<Integer>();
		
		mBackGroundImage = new Image(backgroundRegion);
		
		ButtonCostume costume = new ButtonCostume();
		
		costume.up = Drawable.newDrawable(item1);
		mItem1Button = new PushButton(costume)
				.moveTo(200, 100)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction());
				/*.addTouchListener(new TouchListenerToRemove() {
					
					@Override
					public void onTouched(Actor<?> actor) {
						Core.INPUT.bringUpTextInput("기업명을 입력하세요.", "웃기시네", true, 
								5, new TextInputListener() {
							
							@Override
							public void input(String text) {
								Core.APP.debug("기업명 : " + text);
							}

							@Override
							public void canceled() {
							}
						});
					}
					
				});*/
		
		costume.up = Drawable.newDrawable(item2);
		mItem2Button = new PushButton(costume)
				.moveTo(400, 100)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				/*.addTouchListener(new TouchListenerToRemove() {
					
					@Override
					public void onTouched(Actor<?> actor) {
						Core.INPUT.bringUpTextInput("CEO의 이름을 입력하세요.", 10, 
								new TextInputListener() {
							
							@Override
							public void input(String text) {
								Core.APP.debug("CEO : " + text);
								
								if(text.isEmpty())
									return;
								
								int id = Core.APP.genCustomID();
								
								mChatStringIDList.add(id);

								//((FrameRenderer) ((BaseView) Core.APP.getView()).getRenderer()).
								//		makeCustomLabel(id, mEditor.getText(), Utility.sOutlineWhite15, true);
							}

							@Override
							public void canceled() {
							}
						});
					}
					
				})*/
				.addAction(new ColorTo(new Color4(1f, 1f, 0f, 0f), 1000)
						.setRepeatCount(Action.INFINITE)
						.setRepeatMode(Action.REVERSE));
		
		getStage().addFloor()
				.addChild(mBackGroundImage)
				.addChild(mItem1Button)
				.addChild(mItem2Button)
				.addChild(new Extra() {
					@Override
					public void draw(Batch batch, float parentAlpha) {
						for(int i=0; i<mChatStringIDList.size(); i++)
						{
							//Utility.log("Enter " + mChatStringIDList.get(i));
							
							//LabelToRemove.prepareToDrawText(mChatStringIDList.get(i), 100, 100+i*20, batch);
						}
					}
				});
		
	}
	
	@Override
	public void destroy(boolean lifeCycle) {
		//TextInput.dispose();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			Director.getInstance().changeScene(new EditorTestScene());
			/*
			Director.getInstance().changeScene(
					new TransitionWipeOutLeft(1000, 
							new WindowTestScene()));
			
			Director.getInstance().changeScene(
					new TransitionWipeOutRight(1000, 
							new EditorTestScene()));*/
		}
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
