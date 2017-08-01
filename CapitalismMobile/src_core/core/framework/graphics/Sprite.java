package core.framework.graphics;

import core.framework.graphics.batch.Batch;
import core.framework.graphics.batch.SpriteBatch;
import core.framework.graphics.texture.TextureRegion;

/**
 * Sprite는 TextureRegion에 렌더링에 필요한 고유의 기하정보(이동, 회전, 스케일 등)와 
 * 색을 결합한, 2D그래픽을 구성하는 기본요소 중 하나이다. 텍스쳐를 화면에 렌더링하기 위해 
 * 요구되는 2가지 필수요소인 텍스쳐 좌표계와 스크린 좌표계를 캡슐화한 클래스라고 할 수 있다.</p>
 * 
 * 참고로 {@link SpriteBatch}의 경우 렌더링하기 위해 요구되는 패러미터는 크게 2가지로 
 * 구분할 수 있는데, 바로 텍스쳐 좌표계(e.g. TextureRegion)와 스크린 좌표계(e.g. Form)이다. 
 * 이들을 묶으면 Sprite라는 개념이 완성되므로, 내부적으로 {@link Sprite} 자체를 그대로 사용하지는 
 * 않지만 SpriteBatch라는 이름이 붙은 것은 바로 이 때문이다.</p>
 * 
 * @author 김현우
 */
public class Sprite extends TextureRegion {
	
	/** Sprite를 이루는 인덱스의 개수 */
	public static final int NUM_INDICES = 6;
	
	/** Sprite를 이루는 버텍스의 개수 */
	public static final int NUM_VERTICES = 4;
	
	private Form mForm = new Form();

	public Sprite(TextureRegion textureRegion) {
		super(textureRegion);
		mForm.reset();
		mForm.sizeTo(getRegionWidth(), getRegionHeight());
	}
	
	public Sprite(Sprite sprite) {
		super(sprite);
		mForm.set(sprite.getForm());
	}

	public Form getForm() {
		return mForm;
	}

	public void setForm(Form form) {
		mForm.set(form);
	}
	
	public void draw(Batch batch) {
		batch.draw(this, mForm);
	}
	
	public float getWidth() {
		return mForm.getWidth();
	}
	
	public float getHeight() {
		return mForm.getHeight();
	}
	
}
