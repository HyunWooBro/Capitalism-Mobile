package org.game;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.microedition.khronos.opengles.*;

import org.etc.*;
import org.framework.*;
import org.framework.openGL.*;
import org.game.UserInterface.ToolbarButtons;
import org.game.cell.*;
import org.game.commodity.*;
import org.game.construction.*;
import org.game.construction.Construction.ConstructionTypes;
import org.game.department.*;
import org.game.news.*;
import org.menu.*;
import org.screen.*;
import org.screen.layer.*;
import org.tutorial.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.widget.*;

public class GameState implements IState {
	
	LayerManager mLayerManager;
	
	public CellManager m_cellmanager;
	public UserInterface m_UI;
	public Player m_player;
	public City m_city;
	public VictoryCondition m_victory_condition;
	
	static int m_currentday;
	static int m_currentmonth;
	
	
	public static int m_real_gamespeed;
	public static int m_virtual_gamespeed;
	
	//Random m_randEnem = new Random();
	//Random m_randItem = new Random();
	
	int imageX, imageY, oldX, oldY, moveX, moveY, touchX, touchY;
	public static int off_x, off_y;
	Rect rect_toolbar = new Rect(10, 10, 10+77, 10+397);
	
	final int CELL_X = 64;
	final int CELL_Y = 31;
	
	int select;
	
	Bitmap bit, bit2, bit3, bit4, bit5;
	static Bitmap bit6 = AppManager.getInstance().getBitmap( R.drawable.construction_info);
	
	public static int selection;
	public static int selection2;
	public static ConstructionTypes construction_type_selection;
	
	public enum GameScreenTypes {NORMAL, CONSTRUCTION, REPORT};
	
	public static GameScreenTypes mGameScreenType = GameScreenTypes.NORMAL;
	
	public static boolean m_is_restart;
	
	public static int m_horn;
	
	public static int mBaseDist;
	public static boolean mIsDouble; 
	
	public GameState()
	{
		// 전역변수처럼 등록
		AppManager.getInstance().m_state = this;
		
		mIsDouble = false;
	}
	

	@Override
	public void Init() 
	{
		Log.i("abc", "GameState 진입");
		
		//mGameScreenType = GameScreenTypes.NORMAL;
		
		mGameScreenType = GameScreenTypes.NORMAL;
		
		off_x = 0;
		off_y = 0;
		
		select = 0;
		
		m_horn = 0;

		CapitalismSystem.InitStatic();
		City.InitStatic();

		Log.i("abc", "GameState 0");

		//Utility.InitStatic();
		Player.InitStatic();
		Report.InitStatic();
		Time.InitStatic();
		UserInterface.InitStatic();
		CellManager.InitStatic();
		VehicleManager.InitStatic();
		CommodityManager.InitStatic();
		Construction.InitStatic();
		CellSelector.InitStatic();
		DepartmentManager.InitStatic();
		Department.InitStatic();
		

		//bit5 = AppManager.getInstance().getBitmap( R.drawable.twobytwo);
		//bit6 = AppManager.getInstance().getBitmap( R.drawable.construction_info);
		
		Log.i("abc", "GameState 1");


		m_player = new Player();
		m_city = new City("서울", 5000, 40, 50, 50);
		m_cellmanager = new CellManager();
		m_UI = new UserInterface();
		m_victory_condition = new VictoryCondition();
		
			((GameActivity)AppManager.getInstance().getActivity()).GetCellManager(m_cellmanager);
		
		
		CommodityManager.getInstance().Init();
		
		Log.i("abc", "GameState 2");
		
		m_cellmanager.PortInit();
		
		m_player.Initialize(6000000000L);
		
		Log.i("abc", "GameState 22");
		
		VehicleManager.GetInstance().Init();
		
		Report.GetInstance().Init();
		
		Log.i("abc", "GameState 222");
		
		NewsManager.GetInstance().Init();
		Tutorial.GetInstance().Init();
		
		Log.i("abc", "GameState 2222");
		
		Time.GetInstance().Init();
		m_currentday = Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH);
		m_currentmonth = Time.GetInstance().GetCalendar().get(Calendar.MONTH);
		m_real_gamespeed = 20;
		m_virtual_gamespeed = 2;
		
		Log.i("abc", "GameState 3");
		
		selection = 0;
		selection2 = 0;
		construction_type_selection = null;
		
		Log.i("abc", "GameState 4");
		
		m_is_restart = false;
		
		SoundManager.getInstance().addSound(0, R.raw.click);
		
		
		Log.i("abc", "GameState 끝");
		
		//Debug.GetInstance().Init(this);
		
		//AppManager.getInstance().getGameView().Go();
		
		mLayerManager = new LayerManager(4);

		// 게임 컨텐츠 레이어
		mLayerManager.AddElement(0, new ILayerElement() {
			
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				// TODO Auto-generated method stub
				
				if(mGameScreenType == GameScreenTypes.NORMAL)
				{


					
					//Rect abc = new Rect(off_x, off_y, off_x+mWidth, off_y+mHeight);
					
					m_cellmanager.ConstructionSelect(event, m_UI, m_player);
					
					//if(selection != 0)
					//	selection = 0;
					//selection2 = 0;
					
					m_cellmanager.GetCurrentCell(event, off_x, off_y);
					
					int temp = 0;
					//Player.m_connection_source_construction = null;
					
					if(event.getSource() == 100)
					{
						if(m_cellmanager.m_current_point.x > -1 && m_cellmanager.m_cells[m_cellmanager.m_current_point.x][m_cellmanager.m_current_point.y].m_construction != null)
							if(m_UI.constructiontype == UserInterface.ToolbarConstructionTypes.NOTHING)
							{
								//if(m_cellmanager.m_cells[m_cellmanager.m_current_point.x][m_cellmanager.m_current_point.y].m_construction instanceof Retail)
									for(int i=0; i<m_player.m_retaillist.size(); i++)
										if(m_cellmanager.m_cells[m_cellmanager.m_current_point.x][m_cellmanager.m_current_point.y].m_construction.equals(m_player.m_retaillist.get(i)))
										{
											m_cellmanager.m_cellselector.SetSize(2);
											temp++;
											if(selection == 0)
											{
												selection = i+1;
												construction_type_selection = ConstructionTypes.RETAIL;
												
												if(Player.m_connection_destination_construction != null)
													if(m_player.m_retaillist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_retaillist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											else
											{
												if(Player.m_connection_destination_construction == null)
												{
													if(selection == i+1)
													{
														if(construction_type_selection == ConstructionTypes.RETAIL)
														{
															if(selection2 == 0)
															{
																selection2 = selection;
																mGameScreenType = GameScreenTypes.CONSTRUCTION;
															}
															else
																selection2 = 0;
														}
														else
															construction_type_selection = ConstructionTypes.RETAIL;
													}
													else
													{
														selection =  i+1;
														construction_type_selection = ConstructionTypes.RETAIL;
													}
												}
												else
													if(m_player.m_retaillist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_retaillist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											
										}
									
									for(int i=0; i<m_player.m_factorylist.size(); i++)
										if(m_cellmanager.m_cells[m_cellmanager.m_current_point.x][m_cellmanager.m_current_point.y].m_construction.equals(m_player.m_factorylist.get(i)))
										{
											m_cellmanager.m_cellselector.SetSize(2);
											temp++;
											if(selection == 0)
											{
												selection = i+1;
												construction_type_selection = ConstructionTypes.FACTORY;
												
												if(Player.m_connection_destination_construction != null)
													if(m_player.m_factorylist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_factorylist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											else
											{
												if(Player.m_connection_destination_construction == null)
												{
													if(selection == i+1)
													{
														if(construction_type_selection == ConstructionTypes.FACTORY)
														{
															if(selection2 == 0)
															{
																selection2 = selection;
																mGameScreenType = GameScreenTypes.CONSTRUCTION;
															}
															else
																selection2 = 0;
														}
														else
														{
															construction_type_selection = ConstructionTypes.FACTORY;
															Player.m_connection_source_construction = null;
														}
													}
													else
													{
														selection =  i+1;
														construction_type_selection = ConstructionTypes.FACTORY;
													}
												}
												else
													if(m_player.m_factorylist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_factorylist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											
										}
									
									for(int i=0; i<m_player.m_farmlist.size(); i++)
										if(m_cellmanager.m_cells[m_cellmanager.m_current_point.x][m_cellmanager.m_current_point.y].m_construction.equals(m_player.m_farmlist.get(i)))
										{
											m_cellmanager.m_cellselector.SetSize(3);
											temp++;
											if(selection == 0)
											{
												selection = i+1;
												construction_type_selection = ConstructionTypes.FARM;
												
												if(Player.m_connection_destination_construction != null)
													if(m_player.m_farmlist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_farmlist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											else
											{
												if(Player.m_connection_destination_construction == null)
												{
													if(selection == i+1)
													{
														if(construction_type_selection == ConstructionTypes.FARM)
														{
															if(selection2 == 0)
															{
																
																if(Player.m_connection_destination_construction == null)
																{
																	selection2 = selection;
																	mGameScreenType = GameScreenTypes.CONSTRUCTION;
																}
															}
															else
																selection2 = 0;
														}
														else
															construction_type_selection = ConstructionTypes.FARM;
													}
													else
													{
														selection =  i+1;
														construction_type_selection = ConstructionTypes.FARM;
													}
												}
												else
													if(m_player.m_farmlist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_farmlist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											
										}
									for(int i=0; i<m_player.m_RnDlist.size(); i++)
										if(m_cellmanager.m_cells[m_cellmanager.m_current_point.x][m_cellmanager.m_current_point.y].m_construction.equals(m_player.m_RnDlist.get(i)))
										{
											m_cellmanager.m_cellselector.SetSize(2);
											temp++;
											if(selection == 0)
											{
												selection = i+1;
												construction_type_selection = ConstructionTypes.RnD;
												
												if(Player.m_connection_destination_construction != null)
													if(m_player.m_RnDlist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_RnDlist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											else
											{
												if(Player.m_connection_destination_construction == null)
												{
													if(selection == i+1)
													{
														if(construction_type_selection == ConstructionTypes.RnD)
														{
															if(selection2 == 0)
															{
																
																if(Player.m_connection_destination_construction == null)
																{
																	selection2 = selection;
																	mGameScreenType = GameScreenTypes.CONSTRUCTION;
																}
															}
															else
																selection2 = 0;
														}
														else
															construction_type_selection = ConstructionTypes.RnD;
													}
													else
													{
														selection =  i+1;
														construction_type_selection = ConstructionTypes.RnD;
													}
												}
												else
													if(m_player.m_RnDlist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = m_player.m_RnDlist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											
										}
									for(int i=0; i<City.m_portlist.size(); i++)
										if(m_cellmanager.m_cells[m_cellmanager.m_current_point.x][m_cellmanager.m_current_point.y].m_construction.equals(City.m_portlist.get(i)))
										{
											m_cellmanager.m_cellselector.SetSize(3);
											temp++;
											if(selection == 0)
											{
												selection = i+1;
												construction_type_selection = ConstructionTypes.PORT;
												
												if(Player.m_connection_destination_construction != null)
													if(City.m_portlist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = City.m_portlist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											else
											{
												if(Player.m_connection_destination_construction == null)
												{
													if(selection == i+1)
													{
														if(construction_type_selection == ConstructionTypes.PORT)
														{
															if(selection2 == 0)
															{
																
																if(Player.m_connection_destination_construction == null)
																{
																	selection2 = selection;
																	mGameScreenType = GameScreenTypes.CONSTRUCTION;
																}
															}
															else
																selection2 = 0;
														}
														else
															construction_type_selection = ConstructionTypes.PORT;
													}
													else
													{
														selection =  i+1;
														construction_type_selection = ConstructionTypes.PORT;
													}
												}
												else
													if(City.m_portlist.get(i) != Player.m_connection_destination_construction)
														Player.m_connection_source_construction = City.m_portlist.get(i);
													else
														Player.m_connection_source_construction = null;
											}
											
										}
							
						}
					
					
						if(temp == 0)
						{
							selection = 0;
							selection2 = 0;
							construction_type_selection = null;
							Player.m_connection_source_construction = null;
						}
			
						
						m_cellmanager.ShowSelectorState(m_UI.constructiontype);
						m_cellmanager.CalculateTotalConstructionPrice(m_UI.constructiontype);
					
					
					}
					
					
					return true;
				}
				
				if(mGameScreenType == GameScreenTypes.CONSTRUCTION)
				{
					int x = (int)event.getX();
					int y = (int)event.getY();
					
					if(construction_type_selection == ConstructionTypes.RETAIL)
					{
						if(m_player.m_retaillist.isEmpty() == false)
						{
							m_player.m_retaillist.get(selection-1).onTouchEvent(event);
						}
					}
					
					if(construction_type_selection == ConstructionTypes.FACTORY)
					{
						if(m_player.m_factorylist.isEmpty() == false)
						{
							m_player.m_factorylist.get(selection-1).onTouchEvent(event);
						}
					}
					
					if(construction_type_selection == ConstructionTypes.FARM)
					{
						if(m_player.m_farmlist.isEmpty() == false)
						{
							m_player.m_farmlist.get(selection-1).onTouchEvent(event);
						}
					}
					
					if(construction_type_selection == ConstructionTypes.RnD)
					{
						if(m_player.m_RnDlist.isEmpty() == false)
						{
							m_player.m_RnDlist.get(selection-1).onTouchEvent(event);
						}
					}
					
					if(construction_type_selection == ConstructionTypes.PORT)
					{
						if(City.m_portlist.isEmpty() == false)
						{
							City.m_portlist.get(selection-1).onTouchEvent(event);
						}
					}
					
					return true;
				}
				
				if(mGameScreenType == GameScreenTypes.REPORT)
				{
					Report.GetInstance().onTouchEvent(event);
					
					return true;
				}
				
				return false;
			}
			
			@Override
			public void Render(Canvas canvas) {
				// TODO Auto-generated method stub
				
				/*
				if(mGameScreenType == GameScreenTypes.NORMAL)
				{
					canvas.drawColor(Color.LTGRAY);
					
					m_cellmanager.Render(canvas);

			    	//m_city.Render(canvas);
			    	
			    	if(selection > 0)
			    	{
			    		if(construction_type_selection == ConstructionTypes.RETAIL)
						{
							Player.m_retaillist.get(selection-1).ShowGraphs(canvas);
						}
						if(construction_type_selection == ConstructionTypes.FACTORY)
						{
							Player.m_factorylist.get(selection-1).ShowGraphs(canvas);
						}
						if(construction_type_selection == ConstructionTypes.FARM)
						{
							Player.m_farmlist.get(selection-1).ShowGraphs(canvas);
						}
						if(construction_type_selection == ConstructionTypes.RnD)
						{
							Player.m_RnDlist.get(selection-1).ShowGraphs(canvas);
						}
			    	}
			    	
			    	NewsManager.GetInstance().Render(canvas);
			    	
			    	
			    	
			    	m_city.Render(canvas);
				}
				else if(mGameScreenType == GameScreenTypes.CONSTRUCTION)
				{
					if(construction_type_selection == ConstructionTypes.RETAIL)
						m_player.m_retaillist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.FACTORY)
						m_player.m_factorylist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.FARM)
						m_player.m_farmlist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.RnD)
						m_player.m_RnDlist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.PORT)
						City.m_portlist.get(selection-1).Render(canvas);
				}
				else if(mGameScreenType == GameScreenTypes.REPORT)
				{
					Report.GetInstance().Render(canvas);
				}*/
				
			}

			@Override
			public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
				// TODO Auto-generated method stub
				
				m_cellmanager.onDrawFrame(gl, spriteBatcher);
				
				m_city.onDrawFrame(gl, spriteBatcher);
				
				/*
				if(mGameScreenType == GameScreenTypes.NORMAL)
				{
					//canvas.drawColor(Color.LTGRAY);
					
					//m_cellmanager.onDrawFrame(gl, spriteBatcher);

			    	//m_city.Render(canvas);
			    	
			    	if(selection > 0)
			    	{
			    		if(construction_type_selection == ConstructionTypes.RETAIL)
						{
							Player.m_retaillist.get(selection-1).ShowGraphs(canvas);
						}
						if(construction_type_selection == ConstructionTypes.FACTORY)
						{
							Player.m_factorylist.get(selection-1).ShowGraphs(canvas);
						}
						if(construction_type_selection == ConstructionTypes.FARM)
						{
							Player.m_farmlist.get(selection-1).ShowGraphs(canvas);
						}
						if(construction_type_selection == ConstructionTypes.RnD)
						{
							Player.m_RnDlist.get(selection-1).ShowGraphs(canvas);
						}
			    	}
			    	
			    	NewsManager.GetInstance().Render(canvas);
			    	
			    	
			    	
			    	m_city.Render(canvas);
				}
				else if(mGameScreenType == GameScreenTypes.CONSTRUCTION)
				{
					if(construction_type_selection == ConstructionTypes.RETAIL)
						m_player.m_retaillist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.FACTORY)
						m_player.m_factorylist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.FARM)
						m_player.m_farmlist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.RnD)
						m_player.m_RnDlist.get(selection-1).Render(canvas);
					if(construction_type_selection == ConstructionTypes.PORT)
						City.m_portlist.get(selection-1).Render(canvas);
				}
				else if(mGameScreenType == GameScreenTypes.REPORT)
				{
					Report.GetInstance().Render(canvas);
				}*/
				
			}
		});
		
		// UI 레이어
		mLayerManager.AddElement(1, new ILayerElement() {
			
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				// TODO Auto-generated method stub
				
				if(m_UI.onTouchEvent(event))
					return true;
				
				if(m_UI.ConstructionClick(event) != null)
				{
					if(m_UI.constructiontype == UserInterface.ToolbarConstructionTypes.FARM)
						m_cellmanager.m_cellselector.SetSize(3);
					else
						m_cellmanager.m_cellselector.SetSize(2);
				}
				
				
				ToolbarButtons button = m_UI.ButtonClick(event);
				//if(Button == ToolbarButtons.RESTART)
					//m_is_restart = true;
					//return true;
				
				
				if(button == ToolbarButtons.TIME)
				{
					//////////////////////////
							
					//m_virtual_gamespeed = (m_real_gamespeed-1)/10;
					
					final LinearLayout linear = (LinearLayout) View.inflate(AppManager.getInstance().getGameView().getContext(), 
							R.layout.seekbar_text, null);
					
					SeekBar seekbar = (SeekBar) linear.findViewById(R.id.seekBar1);
					TextView textview = (TextView) linear.findViewById(R.id.textView1);
					
					AlertDialog.Builder bld = new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext());
					bld.setTitle("게임속도를 조절하세요.");
					bld.setView(linear);
					bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							m_real_gamespeed = m_virtual_gamespeed*m_virtual_gamespeed*5;
						}
					});
					bld.setNegativeButton("취소", null);
					bld.create();
					bld.show();
					
					seekbar.setMax(4);
					seekbar.setProgress(m_virtual_gamespeed);
					
					if(m_virtual_gamespeed == 4)
						textview.setText("가장 빠름");
					else if(m_virtual_gamespeed == 3)
						textview.setText("빠름");
					else if(m_virtual_gamespeed == 2)
						textview.setText("보통");
					else if(m_virtual_gamespeed == 1)
						textview.setText("느림");
					else
						textview.setText("정지");
					
					seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
						
						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress,
								boolean fromUser) {
							// TODO Auto-generated method stub
							m_virtual_gamespeed = progress;
							
							TextView textview = (TextView) linear.findViewById(R.id.textView1);
							if(m_virtual_gamespeed == 4)
								textview.setText("가장 빠름");
							else if(m_virtual_gamespeed == 3)
								textview.setText("빠름");
							else if(m_virtual_gamespeed == 2)
								textview.setText("보통");
							else if(m_virtual_gamespeed == 1)
								textview.setText("느림");
							else
								textview.setText("정지");
						}
					});
					/////////////////////////
				}
				
				return false;
			}
			
			@Override
			public void Render(Canvas canvas) {
				// TODO Auto-generated method stub
				
				m_UI.Render(canvas);
				
				Tutorial.GetInstance().Render(canvas);
				
			}

			@Override
			public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
				// TODO Auto-generated method stub
				
				m_UI.onDrawFrame(gl, spriteBatcher);
				
			}
		});
		
		// 메시지창과 디버그내용 출력 레이어
		mLayerManager.AddElement(3, new ILayerElement() {
			
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				// TODO Auto-generated method stub

				if(m_victory_condition.onTouchEvent(event))
					return true;
				
				return false;
			}
			
			@Override
			public void Render(Canvas canvas) {
				// TODO Auto-generated method stub
				
				m_victory_condition.Render(canvas);
		    	
		    	// 디버그내용 출력
		    	Debug.GetInstance().Render(canvas);
				
			}

			@Override
			public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// 전반적인 터치 이벤트 처리 레이어(렌더링 없음)
		mLayerManager.AddElement(2, new ILayerElement() {
			
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				// TODO Auto-generated method stub
				
				if(mGameScreenType == GameScreenTypes.NORMAL)
				{
					if(event.getPointerCount() == 2)
					{
						mIsDouble = true;
						int action = event.getAction();
						int pureaction = action & MotionEvent.ACTION_MASK;
						if(pureaction == MotionEvent.ACTION_POINTER_DOWN)
						{
							Log.i("abc", "ACTION_POINTER_DOWNn");
							mBaseDist = Utility.getDistance(event);
						}
						else
						{
							int delta = (int)((Utility.getDistance(event) - mBaseDist) / 100.0f);
							Log.i("abc", ""+ delta);
							if(delta > 0)
							{
								if(Screen.getZoom() == 1.0f)
								{
									//mIsZoomed = true;
									Screen.setZoom(10.0f/7.0f);
									off_x = off_x - 10*CellManager.m_size;
									off_y = off_y - 5*CellManager.m_size;
									mBaseDist = Utility.getDistance(event);
								}
							}
							if(delta < 0)
							{
								if(Screen.getZoom() == 10.0f/7.0f)
								{
									//mIsZoomed = true;
									Screen.setZoom(1.0f);
									off_x = off_x + 10*CellManager.m_size;
									off_y = off_y + 5*CellManager.m_size;
									mBaseDist = Utility.getDistance(event);
								}
							}
						}
					}
				}
				
				if(mIsDouble == true)
				{
					oldX = moveX = Screen.zoomX((int)event.getX());
					oldY = moveY = Screen.zoomY((int)event.getY());
				}

				if(event.getPointerCount() == 2)
					return true;
				

				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(mGameScreenType == GameScreenTypes.NORMAL)
					{
						oldX =  Screen.zoomX((int)event.getX());
						oldY =  Screen.zoomY((int)event.getY());
					}
					
					touchX = Screen.zoomX((int)event.getX());
					touchY = Screen.zoomY((int)event.getY());
					
					Log.i("abc", "ACTION_DOWN");
					
					
					
					//return true;
				}
				
				if(event.getAction() == MotionEvent.ACTION_MOVE)
				{
					Log.i("abc", "ACTION_MOVE");
					
					if(mGameScreenType == GameScreenTypes.NORMAL)
					{
						mIsDouble = false;
						
						moveX = Screen.zoomX((int)event.getX());
						moveY = Screen.zoomY((int)event.getY());
						 
						off_x = off_x+(oldX-moveX);
						off_y = off_y+(oldY-moveY);
						 
						oldX =  moveX;
						oldY =  moveY;

						//return true;
					}

					//return true;
				}
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					int x = Screen.zoomX((int)event.getX());
					int y = Screen.zoomY((int)event.getY());
					
					Log.i("abc", "ACTION_UP");

					int touch_range = 10;
					
					if(new Rect(touchX-touch_range, touchY-touch_range, 
							touchX+touch_range, touchY+touch_range).contains(x, y))
					{
						event.setSource(100);
						
						//return false;
					}

					//return true;
				}
				
				return false;
			}
			
			@Override
			public void Render(Canvas canvas) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
				// TODO Auto-generated method stub
				
			}
		});
 		
		mLayerManager.getLayerVector().get(0).setVisiable(true);
		mLayerManager.getLayerVector().get(1).setVisiable(true);
		mLayerManager.getLayerVector().get(3).setVisiable(true);
	}
	
	@Override
	public void onDrawFrame(GL10 gl, SpriteBatcher spriteBatcher) {
		// TODO Auto-generated method stub
		
		mLayerManager.onDrawFrame(gl, spriteBatcher);
		
		//Log.e(Utility.TAG, "onDrawFrame reached");
		spriteBatcher.drawText("fps : " + Debug.LogFPS(), 50, 50, new Paint());
	}
	
	
	public void Render(Canvas canvas) 
	{		
		canvas.scale(Screen.getRatioX(), Screen.getRatioY());

		
		mLayerManager.Render(canvas);
		
		//Canvas canvas2 = new Canvas();
		
		if(canvas.isHardwareAccelerated())
			Log.i("abc", "canvas accelerated");
		else
			Log.i("abc", "canvas not accelerated");

	}


	@Override
	public void Update()
	{
		//Debug.LogFPS();
		
		//if(true)
		//	return;
		
		long GameTime = System.currentTimeMillis();
		m_cellmanager.m_cellselector.Update(GameTime);
		m_cellmanager.m_connection_cellselector.Update(GameTime);
		NewsManager.GetInstance().mIconboxSprite.Update(GameTime);
		Tutorial.GetInstance().mTutorialPointsSprite.Update(GameTime);
		
		
		VehicleManager.GetInstance().MakeVehicle(m_cellmanager);
		VehicleManager.GetInstance().Update(GameTime, m_cellmanager);
		

		
		// 시간 흐름
		Time.GetInstance().GetCalendar().add(Calendar.MINUTE, m_real_gamespeed);		
		
		// 날짜(달)이 바뀌면
		if(m_currentmonth != Time.GetInstance().GetCalendar().get(Calendar.MONTH))
		{
			// 새로운 날짜(달)을 등록하고
			m_currentmonth = Time.GetInstance().GetCalendar().get(Calendar.MONTH);
						
			Player.m_netprofit_index++;
			if(Player.m_netprofit_index > 11)
				Player.m_netprofit_index = 0;
			
			Player.m_last_netprofit = Player.m_monthly_netprofit[Player.m_netprofit_index];
			
			Player.m_monthly_netprofit[Player.m_netprofit_index] = 0;
			Player.m_monthly_sales[Player.m_netprofit_index] = 0;
			Player.m_monthly_sales_expense[Player.m_netprofit_index] = 0;
			Player.m_monthly_wage[Player.m_netprofit_index] = 0;
			Player.m_monthly_maintenance[Player.m_netprofit_index] = 0;
			Player.m_monthly_advertise[Player.m_netprofit_index] = 0;
			Player.m_monthly_welfare[Player.m_netprofit_index] = 0;
			Player.m_monthly_interest[Player.m_netprofit_index] = 0;

			// 각 사업체의 이번달 순이익 배열 초기화
			for(int i=0; i<Player.m_retaillist.size(); i++)
				Player.m_retaillist.get(i).Reset();
			for(int i=0; i<Player.m_factorylist.size(); i++)
				Player.m_factorylist.get(i).Reset();
			for(int i=0; i<Player.m_farmlist.size(); i++)
				Player.m_farmlist.get(i).Reset();
			for(int i=0; i<Player.m_RnDlist.size(); i++)
				Player.m_RnDlist.get(i).Reset();
		}

		// 날짜(일)이 바뀌면
		if(m_currentday != Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH))
		{
			// 새로운 날짜(일)을 등록하고
			m_currentday = Time.GetInstance().GetCalendar().get(Calendar.DAY_OF_MONTH);
			
			// 항구 리셋
			for(int i=0; i<City.m_portlist.size(); i++)
				City.m_portlist.get(i).Reset();
			
			// 부서 리셋
			for(int i=0; i<Player.m_retaillist.size(); i++)
				Player.m_retaillist.get(i).ResetDepartment();
			for(int i=0; i<Player.m_factorylist.size(); i++)
				Player.m_factorylist.get(i).ResetDepartment();
			for(int i=0; i<Player.m_farmlist.size(); i++)
				Player.m_farmlist.get(i).ResetDepartment();
			for(int i=0; i<Player.m_RnDlist.size(); i++)
				Player.m_RnDlist.get(i).ResetDepartment();
			
			// 경제관련 계산을 수행
			CapitalismSystem.GetInstance().Process(this);
			
			/*
			for(int i=0; i<Player.m_retaillist.size(); i++)
				Player.m_retaillist.get(i).Update();
			for(int i=0; i<Player.m_factorylist.size(); i++)
				Player.m_factorylist.get(i).Update();
			for(int i=0; i<Player.m_farmlist.size(); i++)
				Player.m_farmlist.get(i).Update();
			for(int i=0; i<Player.m_RnDlist.size(); i++)
				Player.m_RnDlist.get(i).Update();
			*/

			
			// 승리 조건을 검사
			//m_victory_condition.CheckVictoryConditon();
		}
		
		if(m_is_restart == true)
			AppManager.getInstance().getGameView().ChangeState(new MenuState());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK) 
		{
			new AlertDialog.Builder(AppManager.getInstance().getGameView().getContext())
		    .setTitle("게임을 정말 종료하겠습니까?")
		    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					AppManager.getInstance().getActivity().moveTaskToBack(true);
					AppManager.getInstance().getActivity().finish();
					System.exit(0);
					ActivityManager activityManager = (ActivityManager) AppManager.getInstance().getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
					activityManager.restartPackage(AppManager.getInstance().getActivity().getPackageName());
				}

			})
		    .setNegativeButton("취소", null)
		    .show();
		}
		
		return true;
	}
	
	int m_select=0;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		return mLayerManager.onTouchEvent(event);
		
		// Move 와 Up 의 이벤트를 받기위해 true 를 리턴해준다.
		//return true;
	}

	@Override
	public void Destroy() 
	{
		// TODO Auto-generated method stub
		
		Log.i("abc", "GameState Destroy Called");
		
		CapitalismSystem.Destroy();
		Time.Destroy();
		VehicleManager.Destroy();
		CommodityManager.Destroy();
		Report.Destroy();
		
		CapitalismSystem.InitStatic();
		City.InitStatic();
		Player.InitStatic();
		Report.InitStatic();
		Time.InitStatic();
		UserInterface.InitStatic();
		CellManager.InitStatic();
		VehicleManager.InitStatic();
		CommodityManager.InitStatic();
		
	}

}
