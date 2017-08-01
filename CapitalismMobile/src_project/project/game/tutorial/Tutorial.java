package project.game.tutorial;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Tutorial {

	// public SpriteAnimation mTutorialPointsSprite;

	private Bitmap m_tutorial_points_bitmap;// =
											// Core.GRAPHICS.getBitmap(R.drawable.tutorial_points,
											// Utility.sOptions);

	// 싱글턴
	private static Tutorial s_instance;

	private Tutorial() {
	}

	public static Tutorial GetInstance() {
		if(s_instance == null) {
			s_instance = new Tutorial();
		}
		return s_instance;
	}

	public void Init() {
		// mTutorialPointsSprite = new SpriteAnimation(null);

		// mTutorialPointsSprite.InitSpriteData(75, 65, 6, 5);
	}

	public void Render(Canvas canvas/* , int off_x, int off_y */) {
		/*
		 * RectF dest = new RectF(0, 0, 0 +
		 * m_tutorial_points_bitmap.getWidth()/5, 0 +
		 * m_tutorial_points_bitmap.getHeight());
		 * 
		 * canvas.drawBitmap(m_tutorial_points_bitmap,
		 * mTutorialPointsSprite.mSRectangle, dest, null);
		 */
	}

}
