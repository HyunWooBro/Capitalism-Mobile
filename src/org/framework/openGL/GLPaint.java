package org.framework.openGL;

/**
 * canvas의 paint 클래스를 본따 만든 openGL용 페인트 클래스
 * 
 * @author 김현준
 *
 */
public class GLPaint {
	
	int mAlpha;
	int mColor;

	public GLPaint() {
		// TODO Auto-generated constructor stub
	}
	
	public int getAlpha() {
		return mAlpha;
	}
	
	public void setAlpha(int a)
	{
		if(a > 0xff)
			a = 0xff;
		else if(a < 0)
			a = 0;
		
		mAlpha = a;
	}
	
	public int getColor() {
		return mColor;
	}
	
	public void setColor(int c)
	{
		mColor = c;
	}

}
