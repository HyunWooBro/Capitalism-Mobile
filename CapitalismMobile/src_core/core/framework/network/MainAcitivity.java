package core.framework.network;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Enumeration;

import project.game.cell.CellManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import core.framework.Core;

public class MainAcitivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		createConnection();
	}
	
	@Override
	protected void onDestroy() {
		
		closeSocket();
		
		super.onDestroy();
	}
	
	
	
	
	
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
	
	
	public void createConnection() {
		// 이것을 추가 안하면 작동 안함
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		isReadySever=false;
        
        mHandler = new Handler() {
			@Override
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
			@Override
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
					        Core.APP.debug("server message received");
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
			@Override
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

	
	public void Connect()
	{
		Core.APP.getActivity().startActivityForResult(
				new Intent(Core.APP.getActivity(), 
				ConnectActivity.class), 
				RESULT_CONNECT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_CONNECT_CODE) {
    		strServerIp = data.getStringExtra("strServerIp");
    		connectSocket(strServerIp);
    	}else if(resultCode == RESULT_CANCEL) {
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
	
	public void connectSocket(String input) {
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
		Toast.makeText(Core.APP.getActivity(),"소캣을 생성합니다.",1000).show();
		Thread acceptThread = new Thread(){
			@Override
			public void run(){
		    	try{
			    	serverSocket = new ServerSocket( strServerPort );
			    	Core.APP.debug("waiting");
		        	socket = serverSocket.accept();
		        	//Toast.makeText(Core.APP.getActivity() ,"연결되었습니다.", 1000).show();
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
	        //this.mainView.receiveData(INPUT);
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
	        //this.mainView.receiveData(INPUT);
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
    
    
    CellManager abc;
	
	public void GetCellManager(CellManager abc) {
    	this.abc = abc; 
    }
   
    
    public void receiveFromClient(String strReceiveText)
    {
    	Core.APP.showToast(strReceiveText.substring(3), Toast.LENGTH_SHORT);
    	
    	Core.APP.debug("0");
    	
    	abc.BuildFromSocket(strReceiveText);
    	
    	
    }

}
