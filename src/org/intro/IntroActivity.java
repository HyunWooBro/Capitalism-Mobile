package org.intro;

import java.util.*;

import org.framework.*;
import org.game.*;
import org.network.*;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

/**
 * 인트로를 canvas에 그리지 않고 따로 activity로 만든 이유는 처음 로딩을 최대한 
 * 간단하게 하여 인트로 화면을 보여주는 시간을 일정하게 유지하기 위함이다. canvas에 
 * 그리면 처음 로딩 시간이 일정하지 않아 인트로 화면이 보이는 시간이 매번 달랐다.
 * 
 * @author 김현준
 *
 */
public class IntroActivity extends Activity{
	
	private int mResourceID;

	public IntroActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 최상단 탭과 타이틀바를 제거하여 풀스크린으로 만들어준다.
        // 출처 : 이 소스의 책
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // 화면 방향을 가로로 고정한다.
        // 출처 : http://bitwizx.tistory.com/84
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        
        AppManager.getInstance().setActivity(this);
        AppManager.getInstance().setResources(getResources());
        
        mResourceID = 0;
        
        // 인트로 이미지를 랜덤하게 선택
        switch(new Random().nextInt(7))
		{
		case 0:
			mResourceID = R.drawable.intro_background1;
			break;
		case 1:
			mResourceID = R.drawable.intro_background2;
			break;
		case 2:
			mResourceID = R.drawable.intro_background3;
			break;
		case 3:
			mResourceID = R.drawable.intro_background4;
			break;
		case 4:
			mResourceID = R.drawable.intro_background5;
			break;
		case 5:
			mResourceID = R.drawable.intro_background6;
			break;
		case 6:
			mResourceID = R.drawable.intro_background7;
			break;
		} 
        
        setContentView(R.layout.intro);
        
        final ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        final ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        
        imageView1.setBackgroundColor(Color.BLACK);

        //////////////////////////////////////////////////////////////////////////////////// <인트로 핸들러
        
        // 인트로 fadein
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				AlphaAnimation ani = new AlphaAnimation(0f, 1f);
				ani.setDuration(300);

		        imageView2.setImageResource(mResourceID);
		        
		        imageView2.startAnimation(ani);
			}
		}, 500);
        
        // 게임 준비 작업
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				Utility.InitStatic();
				
				BitmapManager.GetInstance().buildBitmapArray();
			}
		}, 1000);
		
        // 인트로 fadeout
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				AlphaAnimation ani = new AlphaAnimation(1f, 0f);
				
				ani.setFillAfter(true);	// 애니메이션 종료 후에도 상태 유지
				ani.setDuration(300);

				imageView2.startAnimation(ani);
			}
		}, 2700);
		
        // GameAcitivity 시작
        new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				// 인텐트 생성(현 액티비티, 새로 실행할 액티비티)
				Intent i = new Intent(IntroActivity.this, GameActivity.class);
				
				// 새로운 액티비티 실행후 지금의 인트로액티비티 종료 
				startActivity(i);
				finish();
				
				//이것이 왜 적용안되는지 의문
				//overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}, 3000);
		
		//////////////////////////////////////////////////////////////////////////////////// 인트로 핸들러>

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		// 인트로 중에 뒤로가기를 누를 경우 핸들러를 끊어버려 아무일 없게 만드는 부분.
		// 미 설정시 인트로 중 뒤로가기를 누르면 인트로 후에 홈화면이 나옴.
		// 출처 : http://zedd.tistory.com/1
		// 출처에서는 onBackPressed에서 핸들러의 removeCallbacks를 사용했지만
		// 이렇게 하는 것이 더 편하다.
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			finish();
			System.exit(0);
			ActivityManager activityManager = (ActivityManager) AppManager.getInstance().getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
			activityManager.restartPackage(AppManager.getInstance().getActivity().getPackageName());
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
