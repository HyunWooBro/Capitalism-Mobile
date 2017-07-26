package org.game.construction;

import org.game.construction.Construction.*;

import android.graphics.*;

public class Farm extends Construction {
	public static String m_name = "농장";
	public static long cost = 750000000;
	public static long maintenance = 37500000;

	public Farm() {
		super(ConstructionTypes.FARM);
		// TODO Auto-generated constructor stub
		m_size = 3;
	}

}
