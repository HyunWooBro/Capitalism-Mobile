package org.game.commodity;

import java.io.*;
import java.util.*;

import org.framework.*;
import org.game.*;
import org.game.cell.*;
import org.game.cell.Cell.*;
import org.game.commodity.Commodity.*;

import android.graphics.*;
import android.util.*;

public class CommodityManager {
	public static Commodity m_commoditis[];
	public static int m_commodity_manufactures[][];
	
	public static int m_count;
	
	private static CommodityManager s_instance;
	private CommodityManager() {}
	public static CommodityManager getInstance(){
		if(s_instance == null){
			s_instance = new CommodityManager();
			Log.i("abc", "CommodityManager null");
		}
		return s_instance;
	}
	
	public static void Destroy()
	{
		s_instance = null;
	}
	
	public void Init() 
	{

		/*
		n = 1;
		m_commoditis = new Commodity[n];
		
		for(int i=0; i<n; i++)
		{
			m_commoditis[i] = new Commodity();
		}
		
		m_commoditis[0].m_name = "빵";
		m_commoditis[0].m_size = 5;
		m_commoditis[0].m_daily_necessity = 70;
		m_commoditis[0].m_basesales = 500;			// 실제로는 size로 나눠야 한다.
		m_commoditis[0].m_baseprice = 1000;
		m_commoditis[0].m_commodity_type = CommodityTypes.PRODUCT;
		m_commoditis[0].m_product_type = ProductTypes.GROCERIES;
		*/
		
		// 파일에서 잘못된 내용 잡아내기(e.g.  4개 이상을 등록하거나)
		
		Log.i("abc", "CommodityManager 1");
		
		String input;
		try {  
			InputStream input_stream = AppManager.getInstance().getResources().openRawResource(R.raw.commodity);
			byte data[] = new byte[input_stream.available()];
			while(input_stream.read(data) != -1) {;}
			input_stream.close();
			input = new String(data);
			
			// input을 토큰으로 구분  
			StringTokenizer s = new StringTokenizer(input, " \r\n\t");
			// 앞으로 버젼과 캐피탈리즘 맵이 맞는지 확인작업까지
			//if(s.hasMoreTokens(" \r"))
			m_count = Integer.parseInt(s.nextToken());
			
			// 이름, 크기 등등 넘기기
			for(int j=0; j<8; j++)
				s.nextToken();
			
			
			
			m_commoditis = new Commodity[m_count];
			
			for(int i=0; i<m_count; i++)
			{
				//if(s.hasMoreTokens())
				int type;
				
				Log.i("abc", "CommodityManager 2");
				
				m_commoditis[i] = new Commodity();
				
				// index 넘기기
				s.nextToken();

				m_commoditis[i].m_name = s.nextToken();
				
				String name = s.nextToken();
				int id = AppManager.getInstance().getResources().getIdentifier(name, "drawable", "org.framework");
				m_commoditis[i].m_commodity_bitmap = AppManager.getInstance().getBitmap(id, Utility.sOptions);
				
				m_commoditis[i].m_size = Integer.parseInt(s.nextToken());
				m_commoditis[i].m_daily_necessity = Integer.parseInt(s.nextToken());
				m_commoditis[i].m_basesales = Integer.parseInt(s.nextToken());			// 실제로는 size로 나눠야 한다.
				m_commoditis[i].m_baseprice = Integer.parseInt(s.nextToken());
				
				m_commoditis[i].m_index = i;
				
				type = Integer.parseInt(s.nextToken());
				
				if(type == 0)
					m_commoditis[i].m_commodity_type = CommodityTypes.RAW_MATERIAL;
				if(type == 1)
					m_commoditis[i].m_commodity_type = CommodityTypes.INTERMEDIATE_MATERIAL;
				if(type == 2)
					m_commoditis[i].m_commodity_type = CommodityTypes.PRODUCT;
				
				type = Integer.parseInt(s.nextToken());
				
				if(type == 0)
					m_commoditis[i].m_product_type = ProductTypes.GROCERIES;
				if(type == 1)
					m_commoditis[i].m_product_type = ProductTypes.COSMETICS;
				
				/*
				if(m_commoditis[i].m_commodity_type != CommodityTypes.RAW_MATERIAL)
				{
					int count = Integer.parseInt(s.nextToken());
					if(count > 0)
					{
						m_commoditis[i].m_commodity_manufacture = new CommodityManufacture();
						m_commoditis[i].m_commodity_manufacture.materials = new Commodity[count];
						m_commoditis[i].m_commodity_manufacture.inputs_count = new int[count];
						m_commoditis[i].m_commodity_manufacture.max_count = count;
						for(int j=0; j<count; j++)
						{
							m_commoditis[i].m_commodity_manufacture.materials[j] = m_commoditis[Integer.parseInt(s.nextToken())];
							m_commoditis[i].m_commodity_manufacture.inputs_count[j] = Integer.parseInt(s.nextToken());
						}
						m_commoditis[i].m_commodity_manufacture.output_count = Integer.parseInt(s.nextToken());
					}
				}*/

			}

		} catch (IOException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  
		
		try {  
			InputStream input_stream = AppManager.getInstance().getResources().openRawResource(R.raw.commodity_manufacture);
			byte data[] = new byte[input_stream.available()];
			while(input_stream.read(data) != -1) {;}
			input_stream.close();
			input = new String(data);
			
			// input을 토큰으로 구분  
			StringTokenizer s = new StringTokenizer(input, " \r\n\t");
			// 앞으로 버젼과 캐피탈리즘 맵이 맞는지 확인작업까지
			//if(s.hasMoreTokens(" \r"))
			m_count = Integer.parseInt(s.nextToken());
			
			// index 넘기기
			for(int i=0; i<m_count; i++)
				s.nextToken();
			
			m_commodity_manufactures = new int[m_count + 1][m_count];

			
			for(int i=0; i<m_count; i++)
			{
				// index 넘기기
				s.nextToken();
				
				for(int j=0; j<m_count; j++)
					m_commodity_manufactures[i][j] = Integer.parseInt(s.nextToken());

			}
			
			for(int i=0; i<m_count; i++)
				m_commodity_manufactures[m_count-1][i] = Integer.parseInt(s.nextToken());

		} catch (IOException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  


	}
	
	public static void InitStatic()
	{
		m_commoditis = null;
		m_commodity_manufactures = null;
		
		m_count = 0;
	}
	
	public Commodity[] GetInputCommodity(Commodity output)
	{
		Commodity input[] = new Commodity[3];
		int count = 0;
		
		for(int i=0; i<m_count; i++)
		{
			if(m_commodity_manufactures[i][output.m_index] != 0)
				input[count++] = m_commoditis[i];
		}
		
		return input;
	}
	
	public Commodity GetOutputCommodity(Commodity input)
	{
		Commodity output = null;
		
		for(int i=0; i<m_count; i++)
		{
			if(m_commodity_manufactures[input.m_index][i] != 0)
				output = m_commoditis[i];
		}
		
		return output;
	}

}
