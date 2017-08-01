package core.math;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 사각영역을 표현한다.</p>
 * 
 * @author 김현우
 */
public class Rectangle {
	
	/** 안드로이드의 Rect에 대한 호환을 위해 제공 */
	public static final Rect TMP_RECT = new Rect();
	/** 안드로이드의 RectF에 대한 호환을 위해 제공 */
	public static final RectF TMP_RECT_F = new RectF();
	
	public static final float[] TMP_ARRAY = new float[4];

	public float x;
	public float y;
	public float width;
	public float height;
	
	public Rectangle() {
	}
	
	public Rectangle(Rectangle rect) {
		set(rect);
	}
	
	public Rectangle(float x, float y, float width, float height) {
		set(x, y, width, height);
	}
	
	public Rectangle set(Rectangle rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;
		return this;
	}
	
	public Rectangle set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public Rectangle set(Rect rect) {
		this.x = rect.left;
		this.y = rect.top;
		this.width = rect.right - rect.left;
		this.height = rect.bottom - rect.top;
		return this;
	}
	
	public Rectangle set(RectF rect) {
		this.x = rect.left;
		this.y = rect.top;
		this.width = rect.right - rect.left;
		this.height = rect.bottom - rect.top;
		return this;
	}
	
	public float left() {
		return x;
	}
	
	public float right() {
		return x + width;
	}
	
	public float top() {
		return y;
	}
	
	public float bottom() {
		return y + height;
	}
	
	public Rectangle left(float left) {
		float right = right();
		x = left;
		right(right);
		return this;
	}
	
	public Rectangle right(float right) {
		width = right - x;
		return this;
	}
	
	public Rectangle top(float top) {
		float bottom = bottom();
		y = top;
		bottom(bottom);
		return this;
	}
	
	public Rectangle bottom(float bottom) {
		height = bottom - y;
		return this;
	}
	
	public float centerX() {
		return x + width/2;
		
	}
	
	public float centerY() {
		return y + height/2;
	}
	
	public Rectangle center(float cx, float cy) {
		x = cx - width/2;
		y = cy - height/2;
		return this;
	}
	
	public boolean contains(float x, float y) {
		return (left() <= x && x <= right()) && (top() <= y && y <= bottom());
	}
	
	public boolean contains(Rectangle rectangle) {
		return (left() <= rectangle.left() && rectangle.right() <= right()) && (top() <= rectangle.top() && rectangle.bottom() <= bottom());
	}
	
	public boolean intersects(float x, float y, float width, float height) {
		float left = x;
		float top = y;
		float right = x + width;
		float bottom = y + height;
        return left() < right && right() > left && top() < bottom && bottom() > top;
    }
	
    public boolean intersects(Rectangle rectangle) {
        return left() < rectangle.right() && right() > rectangle.left() && top() < rectangle.bottom() && bottom() > rectangle.top();
    }
	
	public Rectangle offset(float dx, float dy) {
        x += dx;
        y += dy;
        return this;
    }
	
	public void inset(float dx, float dy) {
		x += dx;
		y += dy;
		width -= dx;
		height -= dy;
	}
	
	public Rectangle setPostion(float x, float y) {
		this.x = x;
		this.y = y;
        return this;
    }
	
	public Rectangle setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }
	
	public Rect getAsRect() {
		TMP_RECT.set((int) left(), (int) top(), (int) right(), (int) bottom());
		return TMP_RECT;
	}
	
	public RectF getAsRectF() {
		TMP_RECT_F.set(left(), top(), right(), bottom());
		return TMP_RECT_F;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Rectangle other = (Rectangle) obj;
		if(Float.floatToIntBits(height) != Float.floatToIntBits(other.height)) return false;
		if(Float.floatToIntBits(width) != Float.floatToIntBits(other.width)) return false;
		if(Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
		if(Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rectangle [x=").append(x).append(", y=").append(y)
				.append(", width=").append(width).append(", height=")
				.append(height).append("]");
		return builder.toString();
	}

}
