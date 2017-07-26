package org.framework;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import org.framework.openGL.*;
import org.game.*;
import org.game.cell.*;
import org.network.*;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.opengl.*;
import android.os.*;
import android.os.PowerManager.WakeLock;
import android.util.*;
import android.view.*;
import android.widget.*;

public class GameActivity extends Activity {
	private WakeLock mWakeLock;
	
	/** The OpenGL view */
	private GLSurfaceView glSurfaceView;
	
	
	CellManager abc;
	
	String strServerIp = "";						// 서버 IP
	String strServerSendText = "";					// 서버가보낸문자
	String strClientSendText = "";					// 클라이언트가 받은 문자
	String strReceiveText = "";						// 받은문자
	String strTotReceiveText= "";					// 받은 총문자
	int strServerPort= 10876;						// 임의의 서버포트
	
	Handler mHandler = null;						// 이벤트 핸들러
	
	ServerSocket serverSocket = null;				// 서버소켓
	Socket socket = null;							// 서보로부터 받은 연결 소켓
	Socket clientSocket = null;						// 클라이언트 소켓
	
	public static boolean isReadySever=false;						// 서버가 응답받을 준비.
	public boolean isReadyClient =false;					// 클라이언트가 응답받을 준비
	
    Thread receiveThread = null;
    Thread receiveClientThread = null;
	
	final static int RESULT_CONNECT_CODE = 1000;	// 연결
	final static int RESULT_CANCEL = 1001;				// 취소
	
	final static int RECEIVE_FROM_SERVER = 1000;	// 서버로부터응답이 왔을 경우 이벤트
	final static int RECEIVE_FROM_CLIENT = 1001;	// 클라이언트로부터 응답이 왔을 경우 이벤트
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i("abc", "onCreate 진입");
       
        
        // 최상단 탭과 타이틀바를 제거하여 풀스크린으로 만들어준다.
        // 출처 : 이 소스의 책
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // 화면 방향을 가로로 고정한다.
        // 출처 : http://bitwizx.tistory.com/84
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        
        PowerManager pm = (PowerManager)this.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, Utility.TAG);
        
    	AppManager.getInstance().setActivity(this);
    	AppManager.getInstance().setResources(getResources());
    	
    	SoundManager.getInstance().Init(this);
        
        
        /*
        final LinearLayout linear = (LinearLayout) View.inflate(AppManager.getInstance().getGameView().getContext(), 
				R.layout.seekbar_text, null);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, LayoutParams.WRAP_CONTENT);
        addContentView(linear, params);
        */
    	
        
    	// Initiate the Open GL view and
        // create an instance with this activity
        glSurfaceView = new GameView(this);
		
        setContentView(glSurfaceView);

        
        
        Log.w("abc", "create");
        
		
		// 이것을 추가 안하면 작동 안함
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		isReadySever=false;
        
        mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RECEIVE_FROM_SERVER:
					//receiveFromServer();
					receiveFromClient(strReceiveText);
					break;
				case RECEIVE_FROM_CLIENT:
					receiveFromClient(strReceiveText);
					break;

				}
			}
		};	


		// 서버에서 응답을 기다리는 이벤트 관리 쓰레드
		receiveThread = new Thread(){ 
			public void run(){
				InputStream in = null;
		        try{
		        	in= socket.getInputStream();
		        }
		        catch(Exception exSocket){
		        	Log.i("", exSocket.toString());
		        }				
				while(true){
					try{
						try{
							Thread.sleep(2000);
						}
						catch(Exception ex){}
						if( isReadySever == true)
						{
					        byte arrlen[] = new byte[3];
					        in.read(arrlen);					        
					        String strLen = new String(arrlen);
					        int len = Integer.parseInt(strLen);
					        byte arrcont[] = new byte[len];
					        in.read(arrcont);		
					        String strArrCont = new String(arrcont);
					        strReceiveText = strLen + strArrCont;
					        //Utility.ShowToast(strReceiveText, Toast.LENGTH_LONG);
					        Log.i(Utility.TAG, "server message received");
					        mHandler.sendMessage(Message.obtain(mHandler, RECEIVE_FROM_SERVER));
						}
					}
					catch(Exception exTemp){
						Log.e("", exTemp.toString());
					}
				}
			}
		};
		receiveClientThread = new Thread(){ 
			public void run(){
		        InputStream in = null;
		        try{
		        	in= clientSocket.getInputStream();
		        }
		        catch(Exception exSocket){
		        	Log.i("", exSocket.toString());
		        }
				while(true){
					try{

						try{
							Thread.sleep(2000);
						}
						catch(Exception ex){}

						if( isReadyClient == true){
					        byte arrlen[] = new byte[3];
					        in.read(arrlen);
					        String strLen = new String(arrlen);
					        int len = Integer.parseInt(strLen);
					        byte arrcont[] = new byte[len];
					        in.read(arrcont);		
				        
					        String strArrCont = new String(arrcont);
					        strReceiveText = strLen + strArrCont;
					        mHandler.sendMessage(Message.obtain(mHandler, RECEIVE_FROM_CLIENT));
						}
					}
					catch(Exception exTemp){
						Log.e("", exTemp.toString());
					}
				}
			}
		};

    }
	

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		//newConfig.orientation
		
		Log.e("abc", "configure");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		glSurfaceView.onPause();
		
		if(mWakeLock.isHeld())
			mWakeLock.release();
		
		//AppManager.getInstance().getGameView().get_thread().setRunning(false);
		
		Log.e("abc", "pause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		Log.e("abc", "stop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		closeSocket();
		
		Log.e("abc", "destory");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		Log.e("abc", "onRestart");
		
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		glSurfaceView.onResume();
		
		mWakeLock.acquire();
		
		Log.e("abc", "onResume");
		
		//AppManager.getInstance().getGameView().get_thread().setRunning(true);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		Log.e("abc", "start");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		Log.e("abc", "option");
		
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		Log.e("abc", "back");
		
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
		Log.e("abc", "onRestoreInstanceState");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		Log.e("abc", "onSaveInstanceState");
		
		//outState.putSerializable("abc", mSingleInstance);
	}

	
	public void Connect()
	{
		AppManager.getInstance().getActivity().startActivityForResult(
				new Intent(AppManager.getInstance().getActivity(), 
				ConnectActivity.class), 
				RESULT_CONNECT_CODE);    	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_CONNECT_CODE){
    		strServerIp = data.getStringExtra("strServerIp");
    		connectSocket(strServerIp);
    	}else if(resultCode == RESULT_CANCEL){
    		// 취소
    	}
		/*
    	else if(resultCode == RESULT_OPENSTONE_YES){
    		// 흑
    		if(this.mainView.myStone == 1){
    			this.sendFromClient("410000");
    		}
    		// 백
    		else if(this.mainView.myStone == 2){
    			this.sendFromServer("420000");
    		}    		
    	}else if(resultCode == RESULT_OPENSTONE_CANCEL){
    		// 취소
    	}    */	
	}
	
	public void connectSocket(String input){
	    try{
	    	clientSocket = new Socket (input, strServerPort);
	    	isReadyClient = true;
	    	//mainView.myStone = 1;	// 흙
	    	//mainView.setStone(1);
	    	//mainView.myTurn = true;
	    	Toast toast = Toast.makeText(this,"연결되었습니다.", 1000);
	    	toast.show();	 
	    	receiveClientThread.start();
	    }
	    catch(Exception exSocket){
	    	 Toast toast = Toast.makeText(this,"연결오류:" + exSocket.toString(),1000);
	    	 toast.show();
	    }
    }
	
	public void createSocket(){
		Toast.makeText(AppManager.getInstance().getActivity(),"소캣을 생성합니다.",1000).show();
		Thread acceptThread = new Thread(){
			public void run(){
		    	try{
			    	serverSocket = new ServerSocket( strServerPort );
			    	Log.i(Utility.TAG, "waiting");
		        	socket = serverSocket.accept();
		        	//Toast.makeText(AppManager.getInstance().getActivity() ,"연결되었습니다.", 1000).show();
		        	//mainView.myStone = 2;	// 백
		        	//mainView.setStone(2);
		        	//mainView.myTurn = false;
		        	isReadySever= true;
		        	receiveThread.start();
		    	}
		    	catch(Exception exSocket){
		    		Log.e("socketCreate error", exSocket.toString());
		    	}					
			}
		};    
		acceptThread.start();
    }
	
	public void getLocalIpAddr() {
        try {
            Enumeration<NetworkInterface> en =
            NetworkInterface.getNetworkInterfaces(); 
            while(en.hasMoreElements()) {
                NetworkInterface interf = en.nextElement();
                Enumeration<InetAddress> ips = interf.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress inetAddress = ips.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                		Toast toast = Toast.makeText(this, 
        					"소캣생성" +  inetAddress.getHostAddress(), 100000);
                		toast.show();
                    }
                }
            }
        } catch (Exception ex) {
        	Toast toast = Toast.makeText(this, 
					"소캣생성 " + ex.toString(), 10000);
				toast.show();
        }        
    }      
	
	
	public void sendFromServer(String input){
    	try{
    		byte[] temp = input.getBytes();
        	int len = temp.length;
        	DecimalFormat df = new DecimalFormat("000");
        	String strLen = df.format(len);
	        BufferedWriter out= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	        out.write(strLen + input);
	        out.flush();
	        Toast toast = Toast.makeText(this,"서버에서 문자를 보냄",1000);
	        toast.show();
	        //this.mainView.receiveData(input);
    	}
    	catch(Exception exTemp){
    		Log.e("", "error" + exTemp.toString());
    	}
    }
    public void sendFromClient(String input){
    	try{
    		byte[] temp = input.getBytes();
        	int len = temp.length;
        	DecimalFormat df = new DecimalFormat("000");
        	String strLen = df.format(len);
	        BufferedWriter out= new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	        out.write(strLen + input);
	        out.flush();
	        Toast toast = Toast.makeText(this,"클라이언트에서 문자를 보냄",1000);
	        toast.show();
	        //this.mainView.receiveData(input);
    	}
    	catch(Exception exTemp){
    		Log.e("", "error" + exTemp.toString());
    	} 
    }
    
    public void closeSocket(){
	   	Toast toast = Toast.makeText(this,"연결종료",1000);
	   	toast.show();
		try{
			clientSocket.close();
			socket.close();
			serverSocket.close();
		}
		catch(Exception ex){
			Log.i("", "abc");
			Log.e("", ex.toString());
		}
    }
    
    public void GetCellManager(CellManager abc)
    {
    	this.abc = abc; 
    }
    
    public void receiveFromClient(String strReceiveText)
    {
    	Utility.ShowToast(strReceiveText.substring(3), Toast.LENGTH_SHORT);
    	
    	Log.e(Utility.TAG, "0");
    	
    	abc.Building(strReceiveText);
    	
    	
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		AppManager.getInstance().getGameView().onKeyDown(keyCode, event);

		return false;//super.onKeyDown(keyCode, event);
	}

}
