package org.game.construction;

import android.graphics.*;

public class Factory extends Construction {
	public static String m_name = "공장";
	public static long cost = 1000000000;
	public static long maintenance = 50000000;

	public Factory() {
		super(ConstructionTypes.FACTORY);
		// TODO Auto-generated constructor stub
		m_size = 2;
	}

}
