package core.framework.graphics;

import core.math.Matrix3;
import core.math.Matrix4;
import core.math.Vector2;

/**
 * 안드로이드의 Transformation 클래스를 바탕으로한 클래스.</p>
 * 
 * @see android.view.animation.Transformation
 */
public class Form {
	
	private Color4 mColor = new Color4();
	
	private Matrix3 mMatrix = new Matrix3();
	
	private Vector2 mPos = new Vector2();
	private Vector2 mScale = new Vector2();
	private float mRotation;
	
	private Vector2 mPivot = new Vector2();
	
	private float mWidth;
	private float mHeight;
	
	private boolean mFlipX;
	private boolean mFlipY;
    
	private boolean mDirty;
	
	public Form() {
		reset();
    }
    
	public Form(Form form) {
    	set(form);
    }

    public void reset() {
    	mColor.reset();
    	mMatrix.reset();
        mPos.set(0f, 0f);
    	mScale.set(1.0f,  1.0f);
        mRotation = 0f;
        mPivot.set(0.5f, 0.5f);
        mWidth = 0f;
        mHeight = 0f;
        mFlipX = false;
        mFlipY = false;
        mDirty = false;
    }
 
    public void set(Form t) {
    	mColor.set(t.mColor);
    	mMatrix.set(t.getMatrix());
        mPos.set(t.mPos);
        mScale.set(t.mScale);
        mRotation = t.mRotation;
        mPivot.set(t.mPivot);
        mWidth = t.mWidth;
        mHeight = t.mHeight;
        mFlipX = t.mFlipX;
        mFlipY = t.mFlipY;
        mDirty = false;
    }

    /** 행우선 3x3 Matrix를 반환한다. */
    public Matrix3 getMatrix() {
    	calculateMatrix();
        return mMatrix;
    }
    
    /** Form행렬을 열우선 4x4행렬로 얻는다. */
    public void getMatrix(Matrix4 matrix) {
    	calculateMatrix();
    	matrix.set(mMatrix);
    }
    
    private void calculateMatrix() {
    	if(!mDirty) return;
    	mDirty = false;

    	Matrix3 matrix = mMatrix;
		matrix.setScale(mScale.x, mScale.y);
		if(mRotation != 0f) matrix.postRotate(mRotation);
		if(mPos.x != 0f || mPos.y != 0f) matrix.postTranslate(mPos.x, mPos.y);
		
		if(isTransformed()) {
			float pivotWidth = mPivot.x * mWidth;
			float pivotHeight = mPivot.y * mHeight;
			matrix.preTranslate(-pivotWidth, -pivotHeight);
			matrix.postTranslate(pivotWidth, pivotHeight);
		}
    }
    
    public float getAlpha() {
		return mColor.a;
    }
    
    public float getRed() {
		return mColor.r;
    }
    
    public float getGreen() {
		return mColor.g;
    }
    
    public float getBlue() {
		return mColor.b;
    }
	
	public Color4 getColor() {
		return mColor;
	}
	
	public Form setAlpha(float alpha) {
    	mColor.a = alpha;
    	return this;
    }
    
	public Form setRed(float red) {
    	mColor.r = red;
    	return this;
    }
	
	public Form setGreen(float green) {
    	mColor.g = green;
    	return this;
    }
	
	public Form setBlue(float blue) {
    	mColor.b = blue;
    	return this;
    }
	
	public Form setAlpha(int alpha) {
		mColor.setAlpha(alpha);
		return this;
	}

	public Form setRed(int red) {
		mColor.setRed(red);
		return this;
	}

	public Form setGreen(int green) {
		mColor.setGreen(green);
		return this;
	}

	public Form setBlue(int blue) {
		mColor.setBlue(blue);
		return this;
	}
	
	public Form setColor(Color4 color) {
		mColor.set(color);
		return this;
	}
	
	public Form setColor(float alpha, float red, float green, float blue) {
		mColor.set(alpha, red, green, blue);
		return this;
	}
	
	public Form setColor(int alpha, int red, int green, int blue) {
		mColor.set(alpha, red, green, blue);
		return this;
	}
	
	public Form setColor(int color) {
		mColor.set(color);
		return this;
	}
	
	public Form addColor(Color4 color) {
		mColor.add(color);
		return this;
	}
	
	public Form addColor(float alpha, float red, float green, float blue) {
		mColor.add(alpha, red, green, blue);
		return this;
	}
	
	public Form subColor(Color4 color) {
		mColor.sub(color);
		return this;
	}
	
	public Form subColor(float alpha, float red, float green, float blue) {
		mColor.sub(alpha, red, green, blue);
		return this;
	}
	
	public Form mulColor(Color4 color) {
		mColor.mul(color);
		return this;
	}
	
	public Form mulColor(float alpha, float red, float green, float blue) {
		mColor.mul(alpha, red, green, blue);
		return this;
	}
	
	public Form clampColor() {
		mColor.clamp();
		return this;
	}
	
	public float getX() {
		return mPos.x;
	}
	
	public float getY() {
		return mPos.y;
	}
	
	public Form setX(float x) {
		if(mPos.x != x) {
			mPos.x = x;
			mDirty = true;
		}
		return this;
	}
	
	public Form setY(float y) {
		if(mPos.y != y) {
			mPos.y = y;
			mDirty = true;
		}
		return this;
	}
	
	public Form moveTo(float x, float y) {
		if(mPos.x != x || mPos.y != y) {
			mPos.x = x;
			mPos.y = y;
			mDirty = true;
		}
		return this;
	}
	
	public Form moveBy(float x, float y) {
		if(x != 0f || y != 0f) {
			mPos.x += x;
			mPos.y += y;
			mDirty = true;
		}
		return this;
	}
	
	public float getScaleX() {
		return mScale.x;
	}

	public float getScaleY() {
		return mScale.y;
	}
	
	public Form setScaleX(float scaleX) {
		if(mScale.x != scaleX) {
			mScale.x = scaleX;
			mDirty = true;
		}
		return this;
	}

	public Form setScaleY(float scaleY) {
		if(mScale.y != scaleY) {
			mScale.y = scaleY;
			mDirty = true;
		}
		return this;
	}
	
	public Form scaleTo(float scale) {
		if(mScale.x != scale || mScale.y != scale) {
			mScale.x = scale;
			mScale.y = scale;
			mDirty = true;
		}
		return this;
	}
	
	public Form scaleTo(float scaleX, float scaleY) {
		if(mScale.x != scaleX || mScale.y != scaleY) {
			mScale.x = scaleX;
			mScale.y = scaleY;
			mDirty = true;
		}
		return this;
	}
	
	public Form scaleBy(float scaleX, float scaleY) {
		if(scaleX != 0f || scaleY != 0f) {
			mScale.x += scaleX;
			mScale.y += scaleY;
			mDirty = true;
		}
		return this;
	}
	
	public Form scaleBy(float scale) {
		if(scale != 0f) {
			mScale.x += scale;
			mScale.y += scale;
			mDirty = true;
		}
		return this;
	}
	
	public float getRotation() {
		return mRotation;
	}

	public Form rotateTo(float rotation) {
		if(mRotation != rotation) {
			mRotation = rotation;
			mDirty = true;
		}
		return this;
	}
	
	public Form rotateBy(float rotation) {
		if(rotation != 0f) {
			mRotation += rotation;
			mDirty = true;
		}
		return this;
	}
	
	public float getPivotX() {
		return mPivot.x;
	}

	public float getPivotY() {
		return mPivot.y;
	}
	
	public Form setPivotX(float pivotX) {
		if(mPivot.x != pivotX) {
			mPivot.x = pivotX;
			mDirty = true;
		}
		return this;
	}
	
	public Form setPivotY(float pivotY) {
		if(mPivot.y != pivotY) {
			mPivot.y = pivotY;
			mDirty = true;
		}
		return this;
	}
	
	public Form pivotToCenter() {
		if(mPivot.x != 0.5f || mPivot.y != 0.5f) {
			mPivot.set(0.5f, 0.5f);
			mDirty = true;
		}
		return this;
	}
	
	public Form pivotTo(float pivotX, float pivotY) {
		if(mPivot.x !=  pivotX || mPivot.y != pivotY) {
			mPivot.set(pivotX, pivotY);
			mDirty = true;
		}
		return this;
	}
	
	public boolean isTransformed() {
		return mRotation != 0f || mScale.x != 1f || mScale.y != 1f;
	}
	
	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	public Form setWidth(float width) {
		mWidth = width;
		return this;
	}

	public Form setHeight(float height) {
		mHeight = height;
		return this;
	}
	
	public Form sizeTo(float width, float height) {
		mWidth = width;
		mHeight = height;
		return this;
	}
	
	public Form sizeBy(float width, float height) {
		mWidth += width;
		mHeight += height;
		return this;
	}
	
	public Form flipSize() {
		float tmp = mWidth;
		mWidth = mHeight;
		mHeight = tmp;
		return this;
	}
	
	public Form setBounds(float x, float y, float width, float height) {
		moveTo(x, y);
		mWidth = width;
		mHeight = height;
		return this;
	}
	
	public Form moveCenterTo(float x, float y) {
		moveTo(x - mWidth/2, y - mHeight/2);
		return this;
	}
	
	public float getCenterX() {
		return mPos.x + mWidth/2;
	}
	
	public float getCenterY() {
		return mPos.y + mHeight/2;
	}
	
	public boolean isFlipX() {
		return mFlipX;
	}

	public boolean isFlipY() {
		return mFlipY;
	}

	public Form setFlipX(boolean flipX) {
		mFlipX = flipX;
		return this;
	}

	public Form setFlipY(boolean flipY) {
		mFlipY = flipY;
		return this;
	}

}
