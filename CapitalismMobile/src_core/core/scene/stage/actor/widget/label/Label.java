package core.scene.stage.actor.widget.label;

import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.FontTexture;
import core.math.Vector2;
import core.scene.stage.actor.widget.Widget;
import core.scene.stage.actor.widget.utils.Align;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.scene.stage.actor.widget.utils.Align.PointAlignable;

/**
 * TextureRegion을 흰색으로 할 경우 다양한 색을 적용할 수 있다.</p>
 * 
 * Scale 또는 size를 이용하면 베이스라인을 유지하면서 크기 조절도 가능.
 * 
 * Label은 다른 Actor와는 다르게 TextureRegion을 독특하게 해석한다.
 * 
 * 기본적으로 다른 Actor와는 다르게 좌측 하단을 원점으로 삼고 있다. 베이스라인을 기준으로 레이블을 
 * 표현하기 위해서이다. 정렬을 통해 원점을 수정할 수 있다.
 * 
 * Label 중에서 SLabel이 가장 정적이고 DLable이 가장 동적이다.</p>
 * 
 * <table border="2" width="85%" align="center" frame="hsides" rules="rows">
 *     <colgroup align="center" />
 *     <colgroup align="center" />
 *     <colgroup align="center" />
 *     <colgroup align="center" />
 *
 *     <thead>
 *     <tr><th>항목</th> <th>SLabel</th> <th>CLabel</th> <th>DLabel</th></tr>
 *     </thead>
 *
 *     <tbody>
 *     <tr>
 *         <td align="center">TextureRegion 재사용</td>
 *         <td align="center">Yes</td>
 *         <td align="center">Yes</td>
 *         <td align="center">No</td>
 *     </tr>
 *     <tr>
 *         <td align="center">동적 변경</td>
 *         <td align="center">No</td>
 *         <td align="center">Limited</td>
 *         <td align="center">Yes</td>
 *     </tr>
 *     <tr>
 *         <td align="center">MultiLine 및 Wrap</td>
 *         <td align="center">No</td>
 *         <td align="center">Yes</td>
 *         <td align="center">Yes</td>
 *     </tr>
 *     <tr>
 *         <td align="center">임의의 Drawable 적용</td>
 *         <td align="center">Yes</td>
 *         <td align="center">No</td>
 *         <td align="center">No</td>
 *     </tr>
 *     </tbody>
 * </table>
 * 
 * @see SLabel
 * @see CLabel
 * @see DLabel
 * @author 김현우
 */
@SuppressWarnings("unchecked")
public abstract class Label<T extends Label<T>> extends Widget<T> implements PointAlignable<T> {
	
	protected Align mAlign = new Align(Align.SOUTH_WEST);
	protected HAlign mLineAlign = HAlign.LEFT;
	
	protected int mNumLines;
	
	protected float mLabelX;
	protected float mLabelY;
	
	protected Label<?> mConcatLabel;
	protected float mConcatPadding;
	
	protected float mTopHeight;
	
	protected String mText;
	
	public Label() {
	}
	
	public Label(Label<?> label) {
		mAlign.set(label.mAlign);
		mLineAlign = label.mLineAlign;
		mNumLines = label.mNumLines;
		mLabelX = label.mLabelX;
		mLabelY = label.mLabelY;
		mConcatLabel = label.mConcatLabel;
		mConcatPadding = label.mConcatPadding;
		mTopHeight = label.mTopHeight;
		mText = label.mText;
	}
	
	@Override
	public void layout() {
		if(mConcatLabel == null) {
			alignLayout();
		} else {
			concatLayout();
			invalidate();
		}
	}
	
	protected void alignLayout() {
		float width = getWidth();
		
		switch(mAlign.getHAlign()) {
			case LEFT:		mLabelX = 0f;
				break;
			case CENTER:	mLabelX = -width/2;
				break;
			case RIGHT:		mLabelX = -width;
				break;
		}
		
		// baseline을 기준으로 그리기 위해 top 높일를 이용한다.
		float topHeight = getHeight() * mTopHeight;
		
		switch(mAlign.getVAlign()) {
			case TOP:		mLabelY = 0f;
				break;
			case CENTER:	mLabelY = -topHeight/2;
				break;
			case BOTTOM:	mLabelY = -topHeight;
				break;
		}
	}
	
	protected void concatLayout() {
		float h = getHeight();
		
		float topHeight = h * mTopHeight;

		Label<?> concatLabel = mConcatLabel;

		// 기준점
		moveTo(concatLabel.getX(), concatLabel.getY());
		
		mLabelX = concatLabel.mLabelX + concatLabel.getWidth() + mConcatPadding;
		
		Align align = concatLabel.mAlign;
		switch(align.getVAlign()) {
			case TOP:		mLabelY = 0f;
				break;
			case CENTER:	mLabelY = -topHeight/2;
				break;
			case BOTTOM:	mLabelY = -topHeight;
				break;
		}
		
		// 정렬은 다음 연결할 Label을 위해 저장한다.
		mAlign.set(align);
	}
	
	@Override
	public float getMinHeight() {
		return getPrefHeight()/2;
	}

	@Override
	public float getMinWidth() {
		return getPrefWidth()/2;
	}
	
	@Override
	protected void drawSelf(Batch batch, float parentAlpha) {
		float x = getX();
		float y = getY();
		moveTo(x+mLabelX, y+mLabelY);
		super.drawSelf(batch, parentAlpha);
		moveTo(x, y);
	}

	@Override
	public T center() {
		mAlign.center();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T top() {
		mAlign.top();
		invalidate();
		return (T) this;
	}

	@Override
	public T left() {
		mAlign.left();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T right() {
		mAlign.right();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T bottom() {
		mAlign.bottom();
		invalidate();
		return (T) this;
	}

	@Override
	public T north() {
		mAlign.north();
		invalidate();
		return (T) this;
	}

	@Override
	public T west() {
		mAlign.west();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T east() {
		mAlign.east();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T south() {
		mAlign.south();
		invalidate();
		return (T) this;
	}

	@Override
	public T northWest() {
		mAlign.northWest();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T northEast() {
		mAlign.northEast();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T southWest() {
		mAlign.southWest();
		invalidate();
		return (T) this;
	}
	
	@Override
	public T southEast() {
		mAlign.southEast();
		invalidate();
		return (T) this;
	}
	
	@Override
	public Align getAlign() {
		return mAlign;
	}

	@Override
	public T setAlign(Align align) {
		mAlign.set(align);
		invalidate();
		return (T) this;
	}
	
	public Label<?> getConcatLabel() {
		return mConcatLabel;
	}

	public T setConcatLabel(Label<?> concatLabel) {
		return setConcatLabel(concatLabel, 0f);
	}
	
	public T setConcatLabel(Label<?> concatLabel, float padding) {
		mConcatLabel = concatLabel;
		mConcatPadding = padding;
		return (T) this;
	}
	
	protected void onTextChanged() {
		float height = getPrefHeight();
		float bottomHeight = FontTexture.sFontDestHeight - FontTexture.sFontDestBaseline;
		mTopHeight = (height- bottomHeight) / height;
		invalidateHierarchy();
	}
	
	public String getText() {
		return mText;
	}
	
	public float getLabelX() {
		return mLabelX;
	}

	public float getLabelY() {
		return mLabelY;
	}

	@Override
	protected void drawDebugBounds(Batch batch, ShapeRenderer renderer) {
		float x = getX();
		float y = getY();
		moveTo(x+mLabelX, y+mLabelY);
		super.drawDebugBounds(batch, renderer);
		moveTo(x, y);
	}

	public HAlign getLineAlign() {
		return mLineAlign;
	}

	/** Label의 각 라인에 대한 정렬을 지정한다. 싱글라인의 경우는 의미가 없다. */
	public T setLineAlign(HAlign lineAlign) {
		mLineAlign = lineAlign;
		return (T) this;
	}

	public int getNumLines() {
		return mNumLines;
	}

	@Override
	public Vector2 parentToLocalCoordinates(Vector2 pos) {
		float x = getX();
		float y = getY();
		moveTo(x+mLabelX, y+mLabelY);
		super.parentToLocalCoordinates(pos);
		moveTo(x, y);
		return pos;
	}

	@Override
	public Vector2 localToParentCoordinates(Vector2 pos) {
		return super.localToParentCoordinates(pos).add(mLabelX, mLabelY);
	}

}
