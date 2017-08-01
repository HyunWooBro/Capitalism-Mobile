package core.scene;

import android.view.KeyEvent;
import android.view.MotionEvent;

import core.framework.graphics.batch.Batch;
import core.scene.stage.Floor;
import core.scene.stage.Stage;

/**
 * Scene은 앱의 한 장면을 표현한다. 모든 Scene은 이 클래스를 상속하여 {@link #create()}를 비롯하여 
 * 필요한 메서드를 구현해야 한다.</p>
 * 
 * Scene은 전역적으로 접근할 수 있는 {@link Director}을 통해 내부적으로 스택을 통해 관리된다. 
 * 즉, 스크린에 출력되는 Scene은 스택에서 최상위에 있는 Scene이다.</p>
 * 
 * 렌더링과 터치이벤트의 처리에 대해서는 내부적으로 {@link Floor}을 통하여 구현하게 된다. 
 * 렌더링은 낮은 Floor에서 높은 Floor로, 터치이벤트는 높은 Floor에서 낮은 Floor로 단계적으로 
 * 진행된다. Floor는 init()에서 {@link Stage}를 통해서 추가할 수 있으며, 추가된 Floor는 이미 만들어진 
 * Floor 위에 쌓이는 구조로 이루어져 있다. 초기에는 Floor가 없으므로, Init()에서 {@link Stage#addFloor()}을 
 * 통해 Floor를 추가해야 한다.</p>
 * 
 * {@link #handleTouchEvent(MotionEvent)}와 {@link #handleKeyEvent(KeyEvent, int)}를 포함하여 모든 
 * 메서드는 렌더링스레드에서 실행된다.
 * 
 * @author 김현우
 */
public abstract class Scene <T extends Act> {
	
	/** 업데이트, 렌더링 및 터치이벤트를 담당하는 스테이지 */
	/*package*/ Stage mStage;
	
	private T mAct;
	
	private boolean mCreated;
	
	public Scene() {
	}

	/** 
	 * Scene이 스택에 처음 생성될 때 호출되거나 {@link Director#createScene(Scene)}을 통해 
	 * 직접 초기화할 수 있다.
	 */
	protected abstract void create();
	
	/** 
	 * 스택에 보관되던 이 Scene이 다시 최상위 Scene이 될 때(false) 또는 AppMain의 
	 * onResume()이 앱시작 직후는 제외하고 실행될 때(true) 호출  
	 */
	protected void resume(boolean lifeCycle) {
	}
	
	/** 
	 * Scene 위에 새로운 Scene을 스택에 넣을 때(false) 또는 AppMain의 
	 * onPasue()가 실행될 때(true) 호출  
	 */
	protected void pause(boolean lifeCycle) {
	}
	
	/** 
	 * Scene이 스택에서 제거될 때(false) 또는 AppMain의 onPasue()에서 
	 * isFinishing()이 true일 때(true) 호출	
	 */
	protected void destroy(boolean lifeCycle) {
	}
	
	/** 
	 * Scene 업데이트 처리. 내부적으로 {@link Stage}의 update(...)를 호출한다.</p>
	 *  
	 * 앱의 전반적인 중요한 데이터의 관리를 위해서는 이 메서드를 재정의하여 처리할 수 있다. 
	 * 그래픽과 터치이벤트의 처리와는 달리, 앱 전반에 영향을 주는 업데이트의 경우에는 Floor 단위에 
	 * 머물지 않는 경우가 있기 때문이다.</p>
	 */
	public void update(long time) {
		if(mStage != null) mStage.update(time);
	}
	
	/** Scene 그래픽 처리. 내부적으로 {@link Stage}의 draw(...)를 호출한다. */
	public void draw(Batch batch) {
		if(mStage != null) mStage.draw(batch);
	}

	/** 
	 * Scene 터치이벤트 처리. 터치이벤트가 발생한 경우에만 호출되며, 내부적으로 
	 * {@link Stage}의 handleTouchEvent(...)를 호출한다. 
	 */ 
	public void handleTouchEvent(MotionEvent event, int action, float screenX, float screenY, int pointerID) {
		if(mStage != null) mStage.handleTouchEvent(event, action, screenX, screenY, pointerID);
	}
	
	/** Scene 키이벤트 처리. 키이벤트가 발생한 경우에만 호출된다. */
	public void handleKeyEvent(KeyEvent event, int keyCode) {
	}

	/** 업데이트, 렌더링 및 터치이벤트를 담당하는 스테이지를 얻는다. */
	protected Stage getStage() {
		return mStage;
	}

	public T getAct() {
		if(mAct == null) throw new IllegalStateException("Retriving anonymous Act not possible");
		return mAct;
	}

	/*package*/ @SuppressWarnings("unchecked")
	void setAct(Act act) {
		mAct = (T) act;
	}

	public boolean isCreated() {
		return mCreated;
	}

	/*package*/ void setCreated(boolean created) {
		mCreated = created;
	}
}
