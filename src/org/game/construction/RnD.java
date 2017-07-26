package org.game.construction;

import org.game.construction.Construction.*;

import android.graphics.*;

public class RnD extends Construction {
	public static String m_name = "연구소";
	public static long cost = 1200000000;
	public static long maintenance = 40000000;

	public RnD() {
		super(ConstructionTypes.RnD);
		// TODO Auto-generated constructor stub
		m_size = 2;
	}

}
