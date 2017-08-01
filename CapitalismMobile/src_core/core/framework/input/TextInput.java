package core.framework.input;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import core.framework.Core;

/**
 * 내장 키보드를 사용하여 실시간 입력을 처리한다. 내부적으로 안드로이드의 EditText를 갖는 
 * Dialog를 사용한다. {@link #show(String, String, boolean, int)}를 호출하면 Dialog의 EditText와 
 * 더불어 내장 키보드를 자동으로 띄워준다. 내장 키보드가 띄워진 상황에서 유저가 back버튼이나 
 * 내장 키보드의 완료버튼을 터치하면 내장 키보드 및 Dialog가 종료된다. (실제로는 Dialog는 숨겨질 
 * 뿐이다.)</p>
 * 
 * 완료버튼 또는 back버튼이 터치되면 {@link #setTextInputListener(TextInputListener)}으로 
 * 등록한 리스너가 불려진다.</p>
 *  
 * @author 김현우
 */
public class TextInput {
	
	private ExEditText mExEditText;

	private Dialog mDialog;
	
	private InputMethodManager mIMM;
	
	private boolean mShowing;
	
	private TextInputListener mListener;

	/*package*/ TextInput() {
		
		mExEditText = new ExEditText(this);
		
		// builder의 마지막 함수에서 리턴하는 AlertDialog를 받아야 정상적으로 해당 함수를
		// 사용할 수 있다.
		AlertDialog.Builder builder = new AlertDialog.Builder(Core.APP.getActivity());
		builder.setView(mExEditText);
		mDialog = builder.create();
		
		mIMM = (InputMethodManager) Core.APP.
				getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	public void setTextInputListener(TextInputListener listener) {
		mListener = listener;
	}
	
	public void show(String title, String text, boolean textAsHint, int maxLength) {
		// 이미 다른 Dialog가 보여지고 있다면 리턴
		// Dialog의 isShowing()메서드는 오류가 있어서 사용하지 않는다.
		if(mShowing) return;
		
		mShowing = true;
		
		ExEditText ex = mExEditText;
		
		// 다이얼로그 타이틀 설정
		mDialog.setTitle(title);
		
		// 기본메시지와 힌트을 초기화한다.
		ex.setHint((textAsHint)? text : null);
		ex.setText((textAsHint)? null : text);
		
		// 최대 글자 수 설정
		InputFilter[] fArray = new InputFilter[1];
		if(maxLength >= 0) 	fArray[0] = new InputFilter.LengthFilter(maxLength);
		else 							fArray[0] = new InputFilter.LengthFilter(Integer.MAX_VALUE);
		ex.setFilters(fArray);
		
		// 다이얼로그 띄우기
		mDialog.show();
		
		// 다이얼로그의 가로 길이는 스크린의 반을, 세로는 내용에 따라 적절히 조절되도록 한다.
		mDialog.getWindow().setLayout(Core.GRAPHICS.getWidth()/2, LayoutParams.WRAP_CONTENT);
		
		// 내장 키보드도 나타나도록 강제
		mIMM.showSoftInput(Core.APP.getView(), InputMethodManager.SHOW_FORCED);
	}
	
	private static class ExEditText extends EditText {
		
		private TextInput mTextInput;
		
		public ExEditText(TextInput input) {
			super(Core.APP.getActivity());
			
			mTextInput = input;
			
			// 한줄 입력
			setSingleLine();
			// 입력받는 중에 확장된 UI를 금지
			setImeOptions(getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		}
		
		@Override
		public boolean onKeyPreIme(int keyCode, KeyEvent event) {
			
			// 내장 키보드가 있는 상태에서 back버튼을 터치했을 때
			if(keyCode == KeyEvent.KEYCODE_BACK) {
				final TextInput input = mTextInput;
				
				input.mDialog.hide();
				input.mShowing = false;
				
				// 내장 키보드를 숨긴다.
				input.mIMM.hideSoftInputFromWindow(getWindowToken(), 0);
				
				// 등록한 리스너가 있다면 렌더링 스레드에서 실행한다.
				Core.APP.runOnGLThread(new Runnable() {
					
					@Override
					public void run() {
						if(input.mListener != null) input.mListener.onCancel();
					}
				});
				
			}
			
			return super.onKeyPreIme(keyCode, event);
		}

		@Override
		public void onEditorAction(int actionCode) {

			// 내장 키보드에서 '입력'을 터치했을 때
			if(actionCode == EditorInfo.IME_ACTION_DONE) {
				final TextInput input = mTextInput;
				
				input.mDialog.hide();
				input.mShowing = false;
				
				// 등록한 리스너가 있다면 렌더링 스레드에서 실행한다.
				Core.APP.runOnGLThread(new Runnable() {
					
					@Override
					public void run() {
						if(input.mListener != null) input.mListener.onInput(getText().toString());
					}
				});
			}
			
			super.onEditorAction(actionCode);
		}
	}
	
	/** TextInput 리스너 */
	public static interface TextInputListener {
		
		/** 유저가 내장 키보드의 완료를 터치한 경우 */
		public void onInput(String text);
		
		/** 유저가 back버튼을 터치한 경우 */
		public void onCancel();
	}
}
