package test.scene;

import java.util.Arrays;

import org.framework.R;

import project.framework.Utils;

import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;

import core.framework.Core;
import core.framework.graphics.Form;
import core.framework.graphics.Sprite;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.PlayMode;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;

@SuppressWarnings("rawtypes")
public class TextureTestScene extends Scene {
	
	private Texture mImageTexture;
	
	private Texture mImageTexture2;
	
	private Texture mFontTexture;
	
	private Animation mAnimation;
	
	private TextureRegion mBackgroundRegion;
	private TextureRegion mApartmentRegion;
	
	private TextureRegion mFontRegion;
	
	//private DyncRegion mAlloc;
	
	//private DyncRegion[] mAllocs;
	
	private int allocIndex;
	
	private Sprite mSprite;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		mImageTexture = tm.getTexture(R.drawable.atlas);
		mImageTexture2 = tm.getTexture(R.drawable.erer);
		mFontTexture = tm.getTexture(R.drawable.font);
		
		final TextureRegion departmentProcessRegion = mImageTexture.getTextureRegion("department_process");
		NinePatch patch = new NinePatch(departmentProcessRegion, 10, 10, 10, 10);
		
		final Image nineImage = new Image(patch)
				.moveTo(300, 200)
				.setFlipY(true).setFlipX(true).sizeTo(100, 80);
		
		// 문자열 말고 정수도 선택적으로 할 수 있게?
		mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		// "cell_2x2_apratment1"의 텍스쳐 영역의 아이디를 얻어 이 아이디에 또 다른 
		// String을 맵핑시킨다. 앞으로 "cell_2x2_apratment1"뿐만 아니라 "abc"로도
		// 같은 영역을 가리킬 수 있다.
		int id = mImageTexture.getIDByString("cell_2x2_apratment1");
		mImageTexture.mapStringToID("abc", id);
		
		mFontRegion = mFontTexture.getTextureRegion(R.array.label_array_outline_yellow_12, 5);
		
		// 텍스쳐 영역 4개를 넣고 핑퐁 플레이 모드로 플레이 한다.
		// 패턴 인덱스를 1로 정하여 0, 1, 2, 3, 2, 1, 2, 3, 2, 1, ... 순으로 
		// 진행되도록 한다.
		// 마지막으로 첫번째 영역만 2초 동안 렌더링한다. 
		mAnimation = new Animation(500, Arrays.asList(
				mImageTexture.getTextureRegion("cell_2x2_house1_d"), 
				mImageTexture.getTextureRegion("cell_2x2_house1"), 
				mImageTexture.getTextureRegion("cell_2x2_apratment1"), 
				mImageTexture.getTextureRegion("cell_2x2_apratment1_d")
				))
			.setPlayMode(PlayMode.PING_PONG)
			.setPatternIndex(1)
			.setFrameDuration(0, 2000);
		
		final RectF dest = new RectF(0, 0, 640, 400);
		
		final Form form = new Form()
				.moveBy(10, 200)
				.moveBy(100, 0)
				.scaleBy(1.5f, 1.5f)
				.rotateBy(-40)
				.moveBy(100, 0)
				.rotateBy(-50);
		
		mSprite = new Sprite(mApartmentRegion);
		mSprite.getForm()
				.moveTo(100, 100)
				.scaleTo(1.5f, 1.5f)
				.pivotTo(0.2f, 0.2f);
		
		
		//Sprite ff = new Sprite(mApartmentRegion);
		//ff.getForm().setPivot(1f, 0.5f);
		
		Core.APP.debug(form.getMatrix().toShortString() + " angle : " + form.getRotation());
		
		//form.setColor(new Color4(0xFF00FF00));
		//form.setPivot(0.5f, 0.5f);
		
		/*int regionKey = mFontTexture.allocDyncRegion(100);
		mAlloc = mFontTexture.getDyncRegion(regionKey);
		Bitmap bitmap = Core.GRAPHICS.createStringBitmap("안뇽", FontTexture.sFontDrawCount, Utility.sOutlineRed15);
		mAlloc.update(bitmap);
		//mFontTexture.mapStringToID("custom", mAlloc.getTextureRegionID());
		bitmap.recycle();
		
		int regionKey2 = mFontTexture.allocDyncRegion(100);
		DyncRegion alloc = mFontTexture.getDyncRegion(regionKey2);
		Bitmap bitmap2 = Core.GRAPHICS.createStringBitmap("찍찍", FontTexture.sFontDrawCount, Utility.sOutlineRed15);
		alloc.update(bitmap2);
		bitmap2.recycle();
		
		Paint p = new Paint();
		p.setStyle(Style.STROKE);
		p.setColor(0xFFFF00FF);
		
		int regionKey3 = mImageTexture2.allocDyncRegion(200);
		mImageTexture2.mapStringToID("arc", regionKey3);
		DyncRegion alloc2 = mImageTexture2.getDyncRegion("arc");
		Bitmap bitmap3 = Bitmap.createBitmap(200, 100, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawRect(new Rect(10, 10, 140, 140), Utility.sOutlineGreen15);
		canvas.drawRect(new Rect(0, 0, 199, 99), p);
		alloc2.update(bitmap3);
		
		int regionKey4 = mImageTexture2.allocDyncRegion(438);
		final DyncRegion alloc3 = mImageTexture2.getDyncRegion(regionKey4);
		Bitmap bitmap4 = Bitmap.createBitmap(438, 100, Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap4);
		canvas2.drawRect(new Rect(10, 10, 140, 140), Utility.sOutlineGreen15);
		canvas2.drawRect(new Rect(0, 0, 437, 99), p);
		canvas2.drawArc(new RectF(50, 50, 100, 100), 0, 120, true, Utility.sOutlineRed15);
		alloc3.update(bitmap4);
		
		int regionKey5 = mImageTexture2.allocDyncRegion(200);
		final DyncRegion alloc4 = mImageTexture2.getDyncRegion(regionKey5);
		Bitmap bitmap5 = Bitmap.createBitmap(200, 100, Config.ARGB_8888);
		Canvas canvas3 = new Canvas(bitmap5);
		canvas3.drawRect(new Rect(0, 0, 199, 99), Utility.sOutlineGreen15);
		alloc4.update(bitmap5);*/
		
		//mImageTexture.getDyncRegionRowHeight();
		
		/*DyncRegion alloc2 = mImageTexture.allocDyncRegion(200);
		Bitmap bitmap3 = Bitmap.createBitmap(200, 200, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawRect(r, paint)
		alloc2.setBitmap(bitmap3);
		alloc2.update();*/
		
		/*mAllocs = new DyncRegion[3];
		for(int i=0; i<3; i++) {
			int key = mFontTexture.allocDyncRegion(300);
			mAllocs[i] = mFontTexture.getDyncRegion(key);
		}*/
		
		
		getStage().addFloor()
			.addChild(new Extra() {
				
				@Override
				protected void prepare() {
					super.prepare();
					addEventListener(new TouchListener() {
						@Override
						public void onTouch(TouchEvent event, float x, float y, Actor<?> listener) {
							Core.APP.vibrate(1000);
						}
					});
				}
				
				@Override
				public Actor<?> contact(float x, float y) {
					return this;
				}
				
				@Override
				public void update(long time) {
					mAnimation.update(time);
					form.rotateBy(-0.1f);
					
					mSprite.getForm().rotateBy(-0.5f);
				}
				
				@Override
				public void draw(Batch renderer, float parentAlpha) {
					// 텍스쳐 자체로 렌더링할 수도 있다.
					//renderer.draw(mImageTexture, null, dest);
					//renderer.draw(mImageTexture.getTextureRegion("mainmenu_background"), 0, 0, 640, 400);
					
					//renderer.draw(mImageTexture2.getAsTextureRegion(), 0, 0, 640, 400);
					
					//renderer.draw(mApartmentRegion, 150, 100);
					
					renderer.draw(mImageTexture.getTextureRegion("cell_retail1"), form);
					
					//renderer.draw(mAnimation.getKeyFrame(), 250, 100);
					
					//renderer.setColor(0.5f, 1f, 1f, 1f);
					//renderer.draw(mImageTexture.getTextureRegion("abc"), 400, 100);
					//renderer.setColor(1f, 1f, 1f, 1f);
					
					renderer.draw(mFontRegion, 200, 300);

					
					mSprite.draw(renderer);
					
					nineImage.draw(renderer, parentAlpha);
					
					
					//renderer.draw(mFontTexture.getTextureRegion("custom"), 500, 350);
					
					//renderer.draw(mImageTexture2.getTextureRegion(alloc3.getTextureRegionID()), 100, 100);
					
					renderer.draw(mFontTexture.getAsTextureRegion(), 0, 0, 640, 400);
					
					/*for(int i=0; i<mAllocs.length; i++) {
						if(mAllocs[i] != null)
							renderer.draw(mAllocs[i].getTextureRegion(), 400, 300 + i*50);
					}*/
				}
			});

	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		
		if(keyCode == KeyEvent.KEYCODE_MENU)
			//Director.getInstance().changeScene(new TextureTestScene());
			mAnimation.reset();
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());

		/*
		Core.INPUT.showTextInput("메시지를 입력하세요.", 10, new TextInputListener() {
			
			@Override
			public void INPUT(final String text) {
				//Utility.log("기업명 : " + mEditor.getText());
				Core.APP.runOnGLThread(new Runnable() {
					
					@Override
					public void run() {
						Bitmap bitmap = Core.GRAPHICS.makeFontBitmap(text, Utility.sOutlineRed15);
						mAllocs[allocIndex].setBitmap(bitmap);
						mAllocs[allocIndex].update();
						bitmap.recycle();
						
						allocIndex++;
						allocIndex %= 3;
					}
				});
			}

			@Override
			public void canceled() {
			}
		});*/
		
		/*Core.GRAPHICS.queueEvent(new Runnable() {
			
			@Override
			public void run() {
				Bitmap bitmap = Core.GRAPHICS.makeFontBitmap("안뇽함?", Utility.sOutlineRed15);
				mAlloc.setBitmap(bitmap);
				mAlloc.update();
			}
		});*/

	}

}
