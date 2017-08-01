package project.framework;

import project.intro.IntroScene;

import android.os.Bundle;

import core.framework.app.AppConfig;
import core.framework.app.AppConfig.ScreenOrientation;
import core.framework.app.AppMain;
import core.scene.Director;
import core.scene.stage.actor.CastingDirector;

public class CapitalismMobile extends AppMain {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppConfig config = new AppConfig();
		config.virtualWidth = 640;
		config.virtualHeight = 400;
		config.screenOrientation = ScreenOrientation.LANDSCAPE;
		init(config);

		// 캐스팅 위치 지정
		CastingDirector.getInstance().setLocation("project.framework.casting");

		// 프로젝트에서 허용할 최대 터치의 개수를 1로 지정
		Director.getInstance().setMaxTouches(1);

		// 인트로 시작
		Director.getInstance().startScene(new IntroScene());
	}
}