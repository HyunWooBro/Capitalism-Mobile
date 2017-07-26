package org.framework;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameViewThread extends Thread {
	private SurfaceHolder _surfaceHolder;
    private GameView _gameview;
    private boolean _run = false;
 
    public GameViewThread(SurfaceHolder surfaceHolder, GameView gameview) {
        _surfaceHolder = surfaceHolder;
        _gameview = gameview;
    }
 
    public void setRunning(boolean run) {
        _run = run;
    }
 
    @Override
    public void run() {
        Canvas c;
        while (_run) {
        	
        	_gameview.Update();
        	
            c = null;
            try {
                c = _surfaceHolder.lockCanvas(null);
                synchronized (_surfaceHolder) {
                	//_gameview.onDraw(c);
                }
            } finally {
                if (c != null) {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

}
