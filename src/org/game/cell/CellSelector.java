package org.game.cell;

import org.framework.*;
import org.game.*;
import org.game.construction.Construction.ConstructionTypes;

import android.graphics.*;

public class CellSelector extends SpriteAnimation{
	public int m_size;
	public enum SelectorColor {RED, YELLOW, BLUE, GREEN};
	
	public int mCurrentBitmapID;
	
	public SelectorColor selector_color; 
	
	public CellSelector(int size)
	{
		super(null);
		
		/*
		twobytwo_blue = AppManager.getInstance().getBitmap(R.drawable.twobytwo_blue);
		twobytwo_yellow = AppManager.getInstance().getBitmap(R.drawable.twobytwo_yellow);
		twobytwo_red = AppManager.getInstance().getBitmap(R.drawable.twobytwo_red);
		twobytwo_green = AppManager.getInstance().getBitmap(R.drawable.twobytwo_green);
		
		threebythree_blue = AppManager.getInstance().getBitmap(R.drawable.threebythree_blue);
		threebythree_yellow = AppManager.getInstance().getBitmap(R.drawable.threebythree_yellow);
		threebythree_red = AppManager.getInstance().getBitmap(R.drawable.threebythree_red);
		threebythree_green = AppManager.getInstance().getBitmap(R.drawable.threebythree_green);
		*/
		
		SetSize(size);
	}
	
	public static void InitStatic()
	{
		/*
		twobytwo_blue = AppManager.getInstance().getBitmap(R.drawable.twobytwo_blue, Utility.sOptions);
		twobytwo_yellow = AppManager.getInstance().getBitmap(R.drawable.twobytwo_yellow, Utility.sOptions);
		twobytwo_red = AppManager.getInstance().getBitmap(R.drawable.twobytwo_red, Utility.sOptions);
		twobytwo_green = AppManager.getInstance().getBitmap(R.drawable.twobytwo_green, Utility.sOptions);	
		
		threebythree_blue = AppManager.getInstance().getBitmap(R.drawable.threebythree_blue, Utility.sOptions);
		threebythree_yellow = AppManager.getInstance().getBitmap(R.drawable.threebythree_yellow, Utility.sOptions);
		threebythree_red = AppManager.getInstance().getBitmap(R.drawable.threebythree_red, Utility.sOptions);
		threebythree_green = AppManager.getInstance().getBitmap(R.drawable.threebythree_green, Utility.sOptions);
		*/
	}

	public void SetSize(int size) 
	{
		m_size = size;
		InitSpriteData(64*size*2/2, 32*size*2/2, 6, 4);
	}
	
	public void SetColor(SelectorColor selectorstate) 
	{
		//if(selector_color = SelectorColor.RED)
			
		//else if()
	}
	
	public SelectorColor ShowSelectorState(Cell cells[][], Point current_point)
	{
		boolean hashouse = false;
		
		if(current_point.x < 0)
			return null;
		
		// selector가 맵을 넘어가는 것을 방지
		if(CellManager.m_size<current_point.x+m_size || CellManager.m_size<current_point.y+m_size)
		{
			mCurrentBitmapID = -1;
			return null;
		}
		
		for(int i=current_point.x; i<current_point.x+m_size; i++)
		{
			for(int j=current_point.y; j<current_point.y+m_size; j++)
			{
					
				if(cells[i][j].m_celltype == Cell.CellTypes.ROAD1 || cells[i][j].m_celltype == Cell.CellTypes.RIVER ||
						cells[i][j].m_maxlink > 0)
				{
					selector_color = SelectorColor.RED;
					if(m_size == 2)
						mCurrentBitmapID = R.drawable.twobytwo_red;
					else if(m_size == 3)
						mCurrentBitmapID = R.drawable.threebythree_red;
					return SelectorColor.RED;
				}
				
				if(cells[i][j].m_celltype == Cell.CellTypes.HOUSE1)
				{
					hashouse = true;
				}
			}
		}
		
		if(hashouse == true)
		{
			selector_color = SelectorColor.YELLOW;
			if(m_size == 2)
				mCurrentBitmapID = R.drawable.twobytwo_yellow;
			else if(m_size == 3)
				mCurrentBitmapID = R.drawable.threebythree_yellow;
			return SelectorColor.YELLOW;
		}
		else
		{
			selector_color = SelectorColor.BLUE;
			if(m_size == 2)
				mCurrentBitmapID = R.drawable.twobytwo_blue;
			else if(m_size == 3)
				mCurrentBitmapID = R.drawable.threebythree_blue;
			return SelectorColor.BLUE;
		}
	}
	
	public SelectorColor ShowSelectorState2(Cell cells[][], Point current_point)
	{
		if(current_point.x < 0)
			return null;
		
		if(cells[current_point.x][current_point.y].m_constructiontype != ConstructionTypes.NOTHING)
		{
			selector_color = SelectorColor.GREEN;
			if(m_size == 2)
				mCurrentBitmapID = R.drawable.twobytwo_green;
			else if(m_size == 3)
				mCurrentBitmapID = R.drawable.threebythree_green;
		}
		
		return null;
		
	}
	
	public void DrawSelectorState()
	{
		
	}
}
