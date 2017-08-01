package core.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;

import core.framework.Core;

public class ResUtils {
	
	private ResUtils() {
	}
	
	/** 
	 * 리소스 아이디에 해당하는 rawResource를 String으로 변환하여 얻는다. 내부적으로 
	 * {@link Resources#openRawResource(int)}를 통해 얻은 {@link InputStream}을 
	 * 한 줄씩 읽어들여 String으로 변환한다.
	 */
	public static String openRawResourceAsString(int ResID) {
		InputStream inputStream = Core.APP.getResources().openRawResource(ResID);
		byte[] data = null;
		try {
			data = new byte[inputStream.available()];
			while(inputStream.read(data) != -1);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(data);
	}
}
