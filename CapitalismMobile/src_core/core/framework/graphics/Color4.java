package core.framework.graphics;

import android.graphics.Color;

import core.math.MathUtils;

/**
 * 색의 각 컴퍼넌트(알파, 빨강, 녹색, 파랑)를 개별적인 float로 관리한다.</p>
 * 
 * android.graphics.Color를 확장한다.</p>
 * 
 * @author 김현우
 * @see android.graphics.Color
 */
public class Color4 extends android.graphics.Color {
	
	public static enum ColorType {
		FULL, 
		ALPHA, 
		RED, 
		GREEN, 
		BLUE, 
	}
	
	public static final Color4 BLACK4 = new ImmutableColor4(BLACK);
	public static final Color4 DKGRAY4 = new ImmutableColor4(DKGRAY);
	public static final Color4 GRAY4 = new ImmutableColor4(GRAY);
	public static final Color4 LTGRAY4 = new ImmutableColor4(LTGRAY);
	public static final Color4 WHITE4 = new ImmutableColor4(WHITE);
	
	public static final Color4 DKRED4 = new ImmutableColor4(0xFFC00000);
	public static final Color4 RED4 = new ImmutableColor4(RED);
	public static final Color4 LTRED4 = new ImmutableColor4(0xFFFF7F7F);
	
	public static final Color4 DKGREEN4 = new ImmutableColor4(0xFF00C000);
	public static final Color4 GREEN4 = new ImmutableColor4(GREEN);
	public static final Color4 LTGREEN4 = new ImmutableColor4(0xFF7FFF7F);
	
	public static final Color4 DKBLUE4 = new ImmutableColor4(0xFF0000C0);
	public static final Color4 BLUE4 = new ImmutableColor4(BLUE);
	public static final Color4 LTBLUE4 = new ImmutableColor4(0xFF7F7FFF);
	
	public static final Color4 DKYELLOW4 = new ImmutableColor4(0xFFC0C000);
	public static final Color4 YELLOW4 = new ImmutableColor4(YELLOW);
	public static final Color4 LTYELLOW4 = new ImmutableColor4(0xFFFFFF7F);
	
	public static final Color4 DKCYAN4 = new ImmutableColor4(0xFF00C0C0);
	public static final Color4 CYAN4 = new ImmutableColor4(CYAN);
	public static final Color4 LTCYAN4 = new ImmutableColor4(0xFF7FFFFF);
	
	public static final Color4 DKMAGENTA4 = new ImmutableColor4(0xFFC000C0);
	public static final Color4 MAGENTA4 = new ImmutableColor4(MAGENTA);
	public static final Color4 LTMAGENTA4 = new ImmutableColor4(0xFFFF7FFF);
	
	public static final Color4 TRANSPARENT4 = new ImmutableColor4(TRANSPARENT);
	
	/** alpha 컴퍼넌트 */
	public float a;
	/** red 컴퍼넌트 */
	public float r;
	/** green 컴퍼넌트 */
	public float g;
	/** blue 컴퍼넌트 */
	public float b;

	public Color4() {
		reset();
	}

	public Color4(float alpha, float red, float green, float blue) {
		set(alpha, red, green, blue);
	}
	
	public Color4(int alpha, int red, int green, int blue) {
		set(alpha, red, green, blue);
	}
	
	public Color4(Color4 color) {
		set(color);
	}
	
	public Color4(int argb) {
		set(argb);
	}
	
	/** 각 컴퍼넌트를 1로 세팅한다. */
	public void reset() {
		a = 1.0f;
		r = 1.0f;
		g = 1.0f;
		b = 1.0f;
	}
	
	public void set(float alpha, float red, float green, float blue) {
		a = alpha;
		r = red;
		g = green;
		b = blue;
	}
	
	public void set(int alpha, int red, int green, int blue) {
		a = alpha / 255f;
		r = red / 255f;
		g = green / 255f;
		b = blue / 255f;
	}
	
	public void set(Color4 color) {
		a = color.a;
		r = color.r;
		g = color.g;
		b = color.b;
	}
	
	public void set(int argb) {
		a = alpha(argb) / 255f;
		r = red(argb) / 255f;
		g = green(argb) / 255f;
		b = blue(argb) / 255f;
	}
	
	public void setAlpha(int alpha) {
		a = alpha / 255f;
	}

	public void setRed(int red) {
		r = red / 255f;
	}

	public void setGreen(int green) {
		g = green / 255f;
	}

	public void setBlue(int blue) {
		b = blue / 255f;
	}
	
	/** 각 컴퍼넌트를 0으로 세팅한다. */
	public void clear() {
		a = 0.0f;
		r = 0.0f;
		g = 0.0f;
		b = 0.0f;
	}
	
	public Color4 add(Color4 color) {
		a += color.a;
		r += color.r;
		g += color.g;
		b += color.b;
		return this;
	}
	
	public Color4 add(float alpha, float red, float green, float blue) {
		a += alpha;
		r += red;
		g += green;
		b += blue;
		return this;
	}
	
	public Color4 sub(Color4 color) {
		a -= color.a;
		r -= color.r;
		g -= color.g;
		b -= color.b;
		return this;
	}
	
	public Color4 sub(float alpha, float red, float green, float blue) {
		a -= alpha;
		r -= red;
		g -= green;
		b -= blue;
		return this;
	}
	
	public Color4 mul(Color4 color) {
		a *= color.a;
		r *= color.r;
		g *= color.g;
		b *= color.b;
		return this;
	}
	
	public Color4 mul(float alpha, float red, float green, float blue) {
		a *= alpha;
		r *= red;
		g *= green;
		b *= blue;
		return this;
	}
	
	/** 각 컴퍼넌트를 0과 1 사이로 clamp한다. */
	public Color4 clamp() {
		MathUtils.clamp(a, 0f, 1f);
		MathUtils.clamp(r, 0f, 1f);
		MathUtils.clamp(g, 0f, 1f);
		MathUtils.clamp(b, 0f, 1f);
		return this;
	}
	
	public static Color4 blue() {
		return new Color4(Color.BLUE);
	}
	
	public static Color4 red() {
		return new Color4(Color.RED);
	}
	
	public static Color4 yellow() {
		return new Color4(Color.YELLOW);
	}
	
	public static Color4 green() {
		return new Color4(Color.GREEN);
	}
	
	public static Color4 black() {
		return new Color4(Color.BLACK);
	}

	public static Color4 magenta() {
		return new Color4(Color.MAGENTA);
	}
	
	public static Color4 gray() {
		return new Color4(Color.GRAY);
	}
	
	public static Color4 cyan() {
		return new Color4(Color.CYAN);
	}
	
	public static Color4 transparent() {
		return new Color4(Color.TRANSPARENT);
	}
	
	public static Color4 white() {
		return new Color4(Color.WHITE);
	}
	
	public static Color4 darkGray() {
		return new Color4(Color.DKGRAY);
	}
	
	public static Color4 lightGray() {
		return new Color4(Color.LTGRAY);
	}
	
	public static class ImmutableColor4 extends Color4 {
		
		private boolean mCreated;
		
		public ImmutableColor4(int argb) {
			super(argb);
		}

		@Override
		public void reset() {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void set(float alpha, float red, float green, float blue) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void set(int alpha, int red, int green, int blue) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void set(Color4 color) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void set(int argb) {
			if(mCreated) throw new UnsupportedOperationException("This instance is immutable");
			mCreated = true;
			super.set(argb);
		}

		@Override
		public void setAlpha(int alpha) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void setRed(int red) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void setGreen(int green) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void setBlue(int blue) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public Color4 add(Color4 color) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public Color4 add(float alpha, float red, float green, float blue) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public Color4 sub(Color4 color) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public Color4 sub(float alpha, float red, float green, float blue) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public Color4 mul(Color4 color) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public Color4 mul(float alpha, float red, float green, float blue) {
			throw new UnsupportedOperationException("This instance is immutable");
		}

		@Override
		public Color4 clamp() {
			throw new UnsupportedOperationException("This instance is immutable");
		}

	}
}
