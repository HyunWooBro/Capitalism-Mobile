package test.scene;

import java.io.IOException;
import java.util.ArrayList;

import project.framework.Utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.scene.Scene;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;

@SuppressWarnings("rawtypes")
public class TextTestScreen extends Scene {
	
	int size;
	int index;
	
	public static ArrayList<Fonts> list;
	
	public static final Paint pRedFont = new Paint();

	public TextTestScreen() {
		
		String[] f = null;
		try {
			f = Core.APP.getActivity().getAssets().list("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String f1 : f){
			Core.APP.debug("names",f1);
		}

		//Core.APP.getResources().
		
		list = new ArrayList<Fonts>();
		
		for(String f1 : f)
		{
			if(f1.equals("images"))
				break;
			list.add(new Fonts(f1));
		}
	}

	@Override
	protected void create() {
		
		
		pRedFont.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		pRedFont.setColor(Color.WHITE);
		pRedFont.setTextSize(15);
		//pRedFont.setAntiAlias(true);
		//pRedFont.setXfermode(new Avoi(Color4.BLACK));
		//pRedFont.setDither(true);
		//pRedFont.setTextScaleX(1.3f);
		//pRedFont.setRasterizer(new Rasterizer());
		//pRedFont.setStrokeWidth(4);
		//pRedFont.setTextLocale(Locale.KOREA);
		//pRedFont.setShadowLayer(2, 0, 0, Color4.BLACK);
		//pRedFont.setAntiAlias(true);
		//pRedFont.setFakeBoldText(true);

		getStage().addFloor()
			.addChild(new Extra() {
				
				@Override
				protected void prepare() {
					super.prepare();
					addEventListener(new TouchListener() {
						@Override
						public void onTouch(TouchEvent event, float x, float y, Actor<?> listener) {
							// 폰트 테스트용
							
							if(event.getNativeMotionEvent().getAction() == MotionEvent.ACTION_DOWN)
							{
								size++;
								index++;
								if(list.size() < index+1)
									index = list.size()-1;
							}
						}
					});
				}

				@Override
				public void update(long time) {
					
				}

				@Override
				public void draw(Batch batch, float parentAlpha) {
					
							//batch.prepareToDrawBitmap(R.drawable.mainmenu_background, 0, 0, null);
					
					/*
					LabelToRemove.prepareToDrawText(R.string.label_city_population2, 300, 200, glRenderer);
					
					LabelToRemove.prepareToDrawText(R.string.label_city_population, 300, 250, glRenderer);
					LabelToRemove.prepareToDrawText(R.array.label_array_outline_white_15, LabelToRemove.LABEL_INDEX_SPACE, 
							LabelToRemove.getConcatPosX(true), LabelToRemove.getConcatPosY(), glRenderer);
					LabelToRemove.prepareToDrawText(R.string.label_city_population, 
							LabelToRemove.getConcatPosX(false), LabelToRemove.getConcatPosY(), glRenderer);*/
					
		
					Paint p = new Paint();
					p.setTextSize(15);
					p.setColor(Color.RED);
					p.setAntiAlias(true);
					//p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		
					//Utility.sOutlineRed15.setShadowLayer(2f, 0, 0, Color4.BLACK);
					Utils.sOutlineRed15.setTextSize(15);
					Utils.sOutlineRed15.setAntiAlias(true);
					
					//Utility.sOutlineRed15.setStyle(Paint.Style.STROKE);
					//Utility.sOutlineRed15.setStrokeWidth(1);
					//Utility.sOutlineRed15.setStrokeJoin(Paint.Join.BEVEL);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 1234567890", 100, 200, p);
					//glRenderer.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 250, 200, Utility.sOutlineRed15);
					//glRenderer.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 250, 200, Utility.sOutlineRed15);
					p.setTextSize(30);
					//p.setShadowLayer(5.5f, 0f, 0f, Color4.WHITE);
		
					p.setColor(Color.RED);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 100, 250, p);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 100, 250, p);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 100, 250, p);
					
					/*
					Typeface typeface = Typeface.createFromAsset(Core.APP.getActivity().getAssets(),
									"H2GPRM.TTF");
					p.setTypeface(typeface);
					p.setColor(Color4.RED);
			
					glRenderer.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 250, 300, p);
					glRenderer.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 250, 300, p);
					glRenderer.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 250, 300, p);*/
		
				
					
					//Paint pp = new Paint();
					//pp.setTextSize(15);
					//pp.setColor(Color4.RED);
					
					//glRenderer.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 250, 300, pp);
					
					
					//if(Utility.list.size() <= index)
					//	index = index - 1;
					
					pRedFont.setShadowLayer(0, 0, 0, 0);
					pRedFont.setTypeface(list.get(index).tf);
					/*
					if(index%2 == 0)
						pRedFont.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
					else
						pRedFont.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
						*/
					pRedFont.setColor(Color.RED);
					pRedFont.setTextSize(15);
					pRedFont.setAntiAlias(true);
					pRedFont.setShadowLayer(2, 0, 0, Color.BLACK);
							//batch.drawText(list.get(index).name, 250, 100, pRedFont);
					
							//batch.drawText("경제 지표 : 후퇴 1234567890", 250, 130, pRedFont);
					//glRenderer.drawText("경제 지표 : 후퇴", 250, 130, pRedFont);
					//glRenderer.drawText("경제 지표 : 후퇴", 250, 130, pRedFont);
					
					pRedFont.setTextSize(14);
							//batch.drawText("경제 지표 : 후퇴 14", 250, 160, pRedFont);
					
					pRedFont.setTextSize(16);
							//batch.drawText("경제 지표 : 후퇴 16", 250, 190, pRedFont);
					
					pRedFont.setShadowLayer(2, 0, 0, Color.BLACK);
					pRedFont.setTextSize(15);
							//batch.drawText("경제 지표 : 후퇴", 250, 220, pRedFont);
					
					pRedFont.setShadowLayer(0, 0, 0, 0);
					pRedFont.setAntiAlias(false);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 요리해서 먹어라", 250, 250, pRedFont);
					
					pRedFont.setShadowLayer(2, 0, 0, Color.BLACK);
					pRedFont.setColor(Color.WHITE);
					pRedFont.setAntiAlias(false);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
					
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라", 250, 280, pRedFont);
					
					pRedFont.setShadowLayer(2, 0, 0, Color.BLACK);
					pRedFont.setColor(Color.WHITE);
					pRedFont.setAntiAlias(true);
					//pRedFont.setTextSize(20);
							//batch.drawText("경제 지표 : 후퇴 만나서 반갑습니다. 012444 요리해서 먹어라", 250, 310, pRedFont);
					
					float width = pRedFont.measureText("경제 지표 : 후퇴 만나서 반갑습니다. 012 요리해서 먹어라");
					//batch.getCanvas().drawLine(250, 320, 250 + width, 320, pRedFont);
					
					Core.APP.debug("width in main : "+width);
					
					Paint p2 = new Paint();
					p2.setTextSize(10);
					p2.setColor(Color.WHITE);
					p2.setShadowLayer(2, 0, 0, Color.BLACK);
							//batch.drawText("경제 지표 abcdefg ABCDEFG", 50, 50, p2);
					
					p2.setTextSize(15);
							//batch.drawText("경제 지표 abcdefg ABCDEFG", 100, 50, p2);
					
					p2.setTextSize(20);
							//batch.drawText("경제 지표 abcdefg ABCDEFG", 150, 50, p2);
					
					p2.setColor(Color.GREEN);
					//batch.getCanvas().drawLine(30*2, 50*2, 500*2, 50*2, p2);
					
					//Paint p = new Paint();
					
					//Typeface tf = p.getTypeface();
					
					//if(pRedFont.getTypeface().isBold())
					//	Log.e(Utility.TAG, "bold  : ");
					
					//Log.e(Utility.TAG, "size : " + p.getTextSize());
				
					
					
					
					//glRenderer.batchDraw();
					
				}
				
				
				
			});
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			index--;
			if(index < 0)
				index = 0;
		}
	}
	
	public static class Fonts {
		
		public Typeface tf;
		public String name;

		public Fonts(String name) {
			this.name = name; 
			this.tf = Typeface.createFromAsset(Core.APP.getActivity().getAssets(), 
					name);
		}
	}

}
