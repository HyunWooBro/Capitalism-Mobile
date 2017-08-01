package project.framework;

import org.framework.*;

import project.game.*;
import project.game.GameScene.GameScreenType;
import android.util.*;
import core.framework.*;
import core.framework.graphics.*;
import core.framework.graphics.batch.*;
import core.framework.graphics.texture.*;
import core.scene.*;
import core.scene.stage.actor.*;
import core.scene.stage.actor.widget.label.*;
import core.scene.stage.actor.widget.table.*;

public class Debug extends Group<Debug> {

	private LayoutTable mtable;

	public Debug(Scene<?> scene) {

		mtable = new LayoutTable().moveTo(100f, 50f);
		addChild(mtable);

		GameScene s = (GameScene) scene;

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture fontTexture = tm.getTexture(R.drawable.font);

		final DLabel touchLabel = new DLabel(null, fontTexture) {
			@Override
			public void update(long time) {
				super.update(time);

				float x = Core.INPUT.getScreenX() * Core.GRAPHICS.getVirtualWidth()
						/ Core.GRAPHICS.getWidth();
				float y = Core.INPUT.getScreenY() * Core.GRAPHICS.getVirtualHeight()
						/ Core.GRAPHICS.getHeight();

				setText("touchX : " + (int) x + " touchY : " + (int) y);
			}
		};

		final DLabel fpsLabel = new DLabel(null, fontTexture) {
			@Override
			public void update(long time) {
				super.update(time);

				setText("FPS : " + Core.GRAPHICS.getFPS());
			}
		};

		final DLabel gameScreenTypelabel = new DLabel(null, fontTexture) {
			@Override
			public void update(long time) {
				super.update(time);

				if(GameScene.mGameScreenType == GameScreenType.MAP)
					setText("GameScreenType : map");
				if(GameScene.mGameScreenType == GameScreenType.BUILDING)
					setText("GameScreenType : building");
				if(GameScene.mGameScreenType == GameScreenType.REPORT)
					setText("GameScreenType : report");
			}
		};

		addDebugActor(new DLabel("debug info", fontTexture).setColor(Color4.RED));
		addDebugActor(touchLabel);
		addDebugActor(fpsLabel);
		addDebugActor(gameScreenTypelabel);

		/*
		 * canvas.translate(0, 10); canvas.drawText("해상도 x:"
		 * +mActorWidth+" y:"+mActorHeight, DEBUG_X, DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10);
		 * canvas.drawText("dpi :"+Core.APP.getResources
		 * ().getDisplayMetrics().densityDpi, DEBUG_X, DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10);
		 * canvas.drawText("off x:"+gamestate.off_x+" y:"+gamestate.off_y,
		 * DEBUG_X, DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10); if(gamestate.m_UI.constructiontype ==
		 * UIManager.ToolBarConstructionType.RETAIL)
		 * canvas.drawText("constructiontype : RETAIL", DEBUG_X, DEBUG_Y, p);
		 * if(gamestate.m_UI.constructiontype ==
		 * UIManager.ToolBarConstructionType.FACTORY)
		 * canvas.drawText("constructiontype : FACTORY", DEBUG_X, DEBUG_Y, p);
		 * if(gamestate.m_UI.constructiontype ==
		 * UIManager.ToolBarConstructionType.NONE)
		 * canvas.drawText("constructiontype : NONE", DEBUG_X, DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10);
		 * canvas.drawText("selection :"+CellManager.selection, DEBUG_X,
		 * DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10);
		 * canvas.drawText("selection2 :"+CellManager.selection2, DEBUG_X,
		 * DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10);
		 * canvas.drawText("negative_profit :"+CapitalProcessingUnit
		 * .m_max_daily_negative_profit, DEBUG_X, DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10); canvas.drawText("FPS :"+mFPS, DEBUG_X,
		 * DEBUG_Y, p);
		 * 
		 * canvas.translate(0, 10);
		 * canvas.drawText("isReadySever :"+AppMain.isReadySever, DEBUG_X,
		 * DEBUG_Y, p);
		 */

	}

	private void addDebugActor(Actor<?> actor) {
		mtable.addCell(actor).left();
		mtable.row();
	}

	public static void log(String message) {
		// if(DEBUG_ENABLED)
		{
			int stackTraceIndex = 3;

			String fullClassName = Thread.currentThread().getStackTrace()[stackTraceIndex]
					.getClassName();
			String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
			String methodName = Thread.currentThread().getStackTrace()[stackTraceIndex]
					.getMethodName();
			int lineNumber = Thread.currentThread().getStackTrace()[stackTraceIndex]
					.getLineNumber();

			Core.APP.debug(className + "." + methodName + "():" + lineNumber, message);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		// 화면 중앙 표시 점
		// batch.prepareToDrawRect(319, 199, 320, 200, Utility.sRed15);

		// 화면 이동 표시 점
		// batch.prepareToDrawRect(off_x2-1, off_y2-1, off_x2, off_y2,
		// Utility.sOutlineGreen15);
	}
}
