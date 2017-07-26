package org.framework.openGL;

import android.content.Context;
import android.graphics.*;
import android.util.*;

public class BasicTexture extends Texture {
	// Basic texture for drawing sprites

	public BasicTexture(int bitmapId) {
		this.bitmapId = bitmapId;
	}

	@Override
	protected Bitmap getBitmap(Context context) {
		
		////////////////////////////////////////////////////////////// <추가
		BitmapFactory.Options sOptions;

		sOptions = new BitmapFactory.Options();
		sOptions.inDensity=160;
		sOptions.inTargetDensity = 160;
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapId, sOptions);
		//////////////////////////////////////////////////////////////추가>
		
		return bitmap;
	}

}
