package org.game.cell;

import org.framework.*;
import org.game.construction.*;
import org.game.construction.Construction.ConstructionTypes;
import org.screen.*;

import android.graphics.*;

public class Cell extends SpriteAnimation {
	// 지가
	public enum LandValues 
	{
		CLASS_A(160000000), CLASS_B(140000000), CLASS_C(120000000), CLASS_D(100000000), CLASS_E(85000000), 
		CLASS_F(70000000), CLASS_G(60000000), CLASS_H(50000000), CLASS_Z(0);
		LandValues(long value) { m_value = value; }
		public long GetValue() { return m_value; }
		private long m_value;
	}
	public enum CellTypes {ROAD1, RIVER, HOUSE1, GROUND1, GROUND2, HOUSE1_2X2, APAPTMENT1_2X2, PORT};
	
	public static int CELL_WIDTH = 64;
	public static int CELL_HEIGHT = 32;
	
	//public static final int  CELL_WIDTH = 64 *2;
	//public static final int  CELL_HEIGHT = 31 *2;
	
	public LandValues m_landvalue;
	public CellTypes m_celltype;
	public int m_maxlink;
	public int m_currentlink;
	public Point m_first_link_point = new Point(-1, -1);
	public Point m_cellpoint = new Point();
	public Rect m_cellrect = new Rect();
	public Point m_imagepoint = new Point();
	public Point m_cell_imagepoint = new Point();
	public Rect m_imagerect = new Rect();
	public Point m_constructionpoint = new Point();
	public Rect m_constructionrect = new Rect();
	public ConstructionTypes m_constructiontype;
	public Construction m_construction;
	
	public Bitmap m_dark_version_bitmap;
	public int m_dark_version_bitmapID;
	
	public Cell() 
	{
		super(null);
		// TODO Auto-generated constructor stub
	}

}
