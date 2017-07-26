package org.game.construction;

import android.graphics.*;

public class Retail extends Construction {
	public static String m_name = "소매점";
	public static long cost = 500000000;
	public static long maintenance = 25000000;

	public Retail() {
		super(ConstructionTypes.RETAIL);
		// TODO Auto-generated constructor stub
		m_size = 2;
	}

}
