package project.game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import project.framework.Debug;
import project.framework.Utils;
import project.game.building.BuildingDelegate;
import project.game.building.BuildingManager;
import project.game.cell.CellManager;
import project.game.cell.VehicleManager;
import project.game.city.City;
import project.game.city.CityManager;
import project.game.corporation.CorporationManager;
import project.game.event.EventManager;
import project.game.event.VictoryEvent;
import project.game.news.NewsManager;
import project.game.product.ProductManager;
import project.game.report.ReportManager;
import project.game.ui.UIManager;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.scene.Scene;
import core.scene.stage.Floor;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Extra;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.widget.label.DLabel;

public class GameScene extends Scene<GameAct> {

	public enum GameScreenType {
		MAP, 
		BUILDING, 
		REPORT, 
	}

	public static GameScreenType mGameScreenType;

	private static List<GameScreenListener> sGameScreenListernerList = new ArrayList<GameScreenListener>();

	private Debug mDebug;

	@Override
	protected void create() {
		
		mGameScreenType = GameScreenType.MAP;
		
		GameOptions.getInstance().init();
		
		BuildingManager.getInstance().init();
		
		CorporationManager.getInstance().init();
		
		BuildingDelegate.getInstance().init();
		
		CityManager.getInstance().init();
		
		City city = new City("서울", 1000000, 60, 50, 50);
		CityManager.getInstance().addCity(city);
		
		ProductManager.getInstance().init();
		
		NewsManager.getInstance().init();
		
		UIManager.getInstance().init();
		
		CellManager.getInstance().init();
		
		VictoryEvent event = new VictoryEvent(getStage());
		EventManager.getInstance().addEvent(event);
		
		ReportManager.getInstance().init();
		
		sGameScreenListernerList.clear();
		sGameScreenListernerList.add(UIManager.getInstance());
		sGameScreenListernerList.add(BuildingDelegate.getInstance());
		sGameScreenListernerList.add(ReportManager.getInstance());
		sGameScreenListernerList.add(CellManager.getInstance());

		CorporationManager.getInstance().getPublicCorporation().InitPort();

		Time.getInstance().init();
		Time.getInstance().setCalendar(new GregorianCalendar(2000, Calendar.JANUARY, 1));
		Time.getInstance().setDeadline(new GregorianCalendar(2002, Calendar.JANUARY, 1));

		// Tutorial.GetInstance().Init();

		// SoundManager.getInstance().addSound(0, R.raw.click);

		// 맵 플로워
		getStage().addFloor(/* 0 */).addChild(CellManager.getInstance())
				.addChild(NewsManager.getInstance());

		// 건물 및 보고서 플로워
		getStage().addFloor(/* 1 */).addChild(BuildingDelegate.getInstance())
				.addChild(ReportManager.getInstance());
		// .debugAll();

		// UIManager 플로워
		getStage().addFloor(/* 2 */).addChild(UIManager.getInstance());

		mDebug = new Debug(this);

		final Texture texture = Core.GRAPHICS.getTextureManager().getTexture(
				org.framework.R.drawable.font);

		// 디버그 플로워
		// getStage().addFloor(/*3*/)
		/*
		 * .addChild(new Extra() {
		 * 
		 * @Override public void draw(Batch batch, float parentAlpha) {
		 * texture.drawDebug(batch, Floor.getDebugRenderer()); } })
		 * .setDebug(true) .addChild(mDebug);
		 */

		// getStage().debugAll();
	}

	@Override
	public void update(long time) {
		super.update(time);
		Time.getInstance().update(time);
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		// if(keyCode == KeyEvent.KEYCODE_MENU) {
		// Director.getInstance().changeScene(
		// new TransitionDelay(250, new MenuMainScene()));
		//
		// //int index = CityManager.getInstance().getCurrentCityIndex();
		// //CityManager.getInstance().setCurrentCityIndex((index == 0)? 1 : 0);
		// }

		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Utils.onBackKey(getStage());
		}
	}

	@Override
	public void destroy(boolean lifeCycle) {
		CPU.getInstance().dispose();

		Time.getInstance().dispose();

		ProductManager.getInstance().dispose();

		CityManager.getInstance().dispose();

		EventManager.getInstance().dispose();

		CPU.InitStatic();

		BuildingManager.getInstance().dispose();

		CorporationManager.getInstance().dispose();

		getStage().disposeAll();

		Utils.clear();

		List<Actor<?>> children = ((Group<?>) mDebug.getChildList().get(0)).getChildList();
		for(int i = 0; i < children.size(); i++) {
			Actor<?> actor = children.get(i);
			if(actor instanceof DLabel) {
				((DLabel) actor).dispose();
			}
		}
	}

	public static GameScreenType getGameScreenType() {
		return mGameScreenType;
	}

	public static void chnageGameScreenType(GameScreenType gameScreenType) {
		if(mGameScreenType == gameScreenType) return;

		mGameScreenType = gameScreenType;

		List<GameScreenListener> listenerList = sGameScreenListernerList;
		int n = listenerList.size();
		for(int i = 0; i < n; i++) {
			GameScreenListener listener = listenerList.get(i);
			listener.onGameScreenChanged(gameScreenType);
		}
	}

	public interface GameScreenListener {
		public void onGameScreenChanged(GameScreenType screenType);
	}

}
