package core.framework.network;

import org.framework.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

// 접속 버튼 클릭하면 나타나는 액티비티
public class ConnectActivity extends Activity {

	final static int RESULT_CONNECT_CODE = 1000;	// 연결
	final static int RESULT_CANCEL = 1001;			// 취소
	
	Context mContext;
	String strServerIp = "";						// 서버 IP

	// 컨트롤
	EditText EdtServerIp;	
	
	boolean isReadySever=false;						// 서버가 응답받을 준비.
	boolean isReadyClient =false;					// 클라이언트가 응답받을 준비
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);
        mContext = this;
        
        // 연결 버튼
		Button BtnConnect = (Button)findViewById(R.id.btnConnect);
		// 취소 버튼
		Button BtnCncl   = (Button)findViewById(R.id.btnCncl);
		
		// 에디트
		EdtServerIp = (EditText)findViewById(R.id.edtServerIp);
		EdtServerIp.setText("192.168.200.107");	// 임시
		// 클라이언트소켓생성및 연결
		// 연결 버튼 클릭시
		BtnConnect.setOnClickListener(new View.OnClickListener(){
	        @Override
			public void onClick(View v){	    		
	    		// 서버연결
	        	Intent returnIntent = new Intent();	        	
	        	returnIntent.putExtra("strServerIp", EdtServerIp.getText().toString());
	        	setResult( RESULT_CONNECT_CODE, returnIntent);
	        	finish();
	        }
	    }); 
		// 클라이언트소켓생성및 연결
		// 취소 버튼 클릭시
		BtnCncl.setOnClickListener(new View.OnClickListener(){
	        @Override
			public void onClick(View v){	    		
	    		// 서버연결
	        	Intent returnIntent = new Intent();	        	
	        	setResult( RESULT_CANCEL, returnIntent);
	        	finish();
	        }
	    }); 		
    }
}