package core.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import android.opengl.GLES20;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import core.framework.Core;
import core.framework.app.AppListener;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.batch.ParallelBatch;
import core.framework.input.InputManager;
import core.math.MathUtils;
import core.scene.stage.Stage;
import core.scene.transition.Transition;
import core.utils.SnapshotArrayList;

/**
 * Director은 Act와 Scene을 관리한다. 극의 막과 장을 책임지는 일종의 감독인 셈이다.</p>
 * 	<ul>
 * 		<li>Scene 관리(렌더링 및 입력 처리)
 * 		<li>Scene스택 및 Scene오버랩 관리
 * 		<li>Scene관련 작업스케쥴 관리
 * 		<li>Scene에서 사용될 Batch 관리
 * 		<li>Transition 관리 및 리스너 제공
 * 	</ul>
 * 
 * Scene은 스택을 통해 관리된다. pushScene(..)을 하면 새로운 Scene을 최상위 위치에 
 * 추가하고, popScene(...)은 최상위 Scene을 제거하며, 마지막으로 changeScene(...)은 
 * 최상위 Scene을 교체한다. 최종적으로 화면에 출력되고 입력이벤트를 처리하는 것은 
 * 최상위 Scene이다.</p>
 * 
 * Director는 Act와 Scene을 설정하는 것 이외에 또 다른 Scene을 오버랩하는 기능도 담당한다. 
 * 오버랩은 기존 Scene 위나 아래에 새로운 Scene을 추가적으로 노출하는 기능이다. 오버랩된 
 * Scene은 추가로 첨가된 것으로써 changeScene(...)이나 pushScene(...) 등과는 관련없이 
 * 표현되는 특별한 Scene이다. 즉, changeScene(...) 등으로 기본 Scene이 바뀌더라도 이미 
 * 오버랩된 Scene은 계속 유지된다. 제거하기 위해서는 removeOverlapScene...(...)을 호출하면 
 * 된다.</p>
 * 
 * Scene에 대한 대부분의 작업은 입력된 순서대로 스케줄로 입력되어 처리된다.
 * <pre>
 * Director.getInstance().changeScene(
 *     new TransitionWipeOutRight(500, 1500, 
 *         new TestScene()));
 *         
 * Director.getInstance().overlapSceneOnTop(new AnotherTestScene());
 * </pre>
 * 위의 코드와 같이, Transition을 적용한 새로운 Scene을 changeScene(...)으로 교체하고 바로 
 * {@link #overlapSceneOnTop(Scene)}으로 또 다른 Scene을 기존 Scene 위에 오버랩 할 때, 우선 
 * Transition이 적용된 후 changeScene(...)의 popScene()과 pushScene(...)이 순서대로 적용되고 
 * 마지막으로 {@link #overlapSceneOnTop(Scene)}이 적용되는 식이다.</p>
 * 
 * Transition을 적용하기 위해서는 pushScene(...) 등을 통해 Scene을 추가할 때, 위의 예에서처럼 
 * 데코레이션 패턴을 사용하여 적용하고 싶은 Transition을 추가하면 된다. popScene(...)도 
 * Transition을 추가할 수 있는데, 오버로딩 된 메서드를 이용하면 된다. 단, nextScene을 설정하지 
 * 않은 Transiton만 허용된다.
 * 
 * 디렉터의 Scene관련 메서드는 기본적으로 렌더링스레드에서만 호출해야 한다. 단, 처음 Scene을 
 * 시작하는 {@link #startScene(Scene)}만 예외적으로 UI스레드에서 호출할 수 있다.</p>
 * 
 * @author 김현우
 */
public class Director implements AppListener {
	
	private static final String TAG = Director.class.getSimpleName();
	
	private enum Task {
		TRANSITION, 
		PUSH, 
		POP, 
		OVERLAP_ON_TOP, 
		OVERLAP_ON_BOTTOM, 
		REMOVE_OVERLAPS, 
		REMOVE_OVERLAP, 
	}
	
	/** Scene을 보관하는 스택 */
	private Stack<Scene<?>> mSceneStack = new Stack<Scene<?>>();
	
	/** 스택의 최상위 Scene. 최상위 Scene에 대한 접근을 빠르게 하기 위해 독립적으로 제공된다. */  
	private Scene<?> mCurrentScene;
	
	/** 현재 렌더링되고 있는 스택 취상위 Scene을 비롯하여 오버랩된 Scene 전체의 리스트 */
	private SnapshotArrayList<Scene<?>> mWorkingSceneList = new SnapshotArrayList<Scene<?>>();
	
	/** 대기중인 Schedule 리스트 */
	private List<Schedule> mScheduleList = new ArrayList<Schedule>(6);

	/** 현재 설정된 Act */
	private Act mCurrentAct;	
	
	/** 현재 Scene 전환 애니메이션 */
	private Transition mCurrentTransition;
	
	private TransitionListener mTransitionListener;
	
	private Batch mBatch;
	
	private int mMaxTouches = InputManager.MAX_TOUCHES;

	/** 싱글턴 인스턴스 */
	private volatile static Director sInstance;
	private Director() {}
	public static Director getInstance() {
		if(sInstance == null) {
			synchronized(Director.class) {
				if(sInstance == null) sInstance = new Director();
			}
		}
		return sInstance;
	}
	
	@Override
	public void onCreate() {
		// 처음 capacity를 기본값 10에서 0으로 만든다.
		mSceneStack.trimToSize();
	}
	
	@Override
	public void onResume() {
		ListIterator<Scene<?>> it = mWorkingSceneList.begin();
		while(it.hasNext()) {
			Scene<?> scene = it.next();
			scene.resume(true);
		}
		mWorkingSceneList.end(it);
	}

	@Override
	public void onPause() {
		ListIterator<Scene<?>> it = mWorkingSceneList.begin();
		while(it.hasPrevious()) {
			Scene<?> scene = it.previous();
			scene.pause(true);
		}
		mWorkingSceneList.end(it);
	}

	@Override
	public void onDestroy() {
		ListIterator<Scene<?>> it = mWorkingSceneList.begin();
		while(it.hasPrevious()) {
			Scene<?> scene = it.previous();
			destroyScene(scene, true);
		}
		mWorkingSceneList.end(it);
	}
	
	@Override
	public void onRender() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		long time = Core.GRAPHICS.getCurrentTime();
		
		Act act = mCurrentAct;
		if(act != null) act.update(time);
		
		Batch batch = mBatch;
		if(batch == null) return;
		batch.begin();
		
		Transition transition = mCurrentTransition;
		if(transition != null && transition.isReady(time)) {
			if(!transition.hasStarted()) {
				TransitionListener listener = mTransitionListener;
				if(listener != null) listener.onStart(transition);
			}
			
			ListIterator<Scene<?>> it = mWorkingSceneList.begin();
			while(it.hasNext()) {
				Scene<?> scene = it.next();
				if(scene == mCurrentScene) {
					transition.render(batch, time);
	    		} else {
		    		scene.update(time);
		    		scene.draw(batch);
	    		}
	    	}
			mWorkingSceneList.end(it);
	    	
	    	if(transition.hasEnded()) {
	    		TransitionListener listener = mTransitionListener;
	    		if(listener != null) listener.onEnd(transition);
	    		
	        	Core.INPUT.setInputEnabled(true);
	        	mCurrentTransition = null;
	        	checkSchedule();
	    	}
		} else {
			ListIterator<Scene<?>> it = mWorkingSceneList.begin();
			while(it.hasNext()) {
				Scene<?> scene = it.next();
				scene.update(time);
	    		scene.draw(batch);
			}
			mWorkingSceneList.end(it);
		}
		
		batch.end();
	}
	
	@Override
	public void onInputEvent(InputEvent event) {
		// 입력이벤트는 스택 최상위 Scene에서만 처리한다.
		if(event instanceof MotionEvent) {
			MotionEvent e = (MotionEvent) event;
			int action = e.getActionMasked();
			switch(action) {
				case MotionEvent.ACTION_MOVE:
					int count = e.getPointerCount();
					for(int i=0; i<count; i++) {
						int id = e.getPointerId(i);
						if(id >= mMaxTouches) continue;
						mCurrentScene.handleTouchEvent(e, action, e.getX(i), e.getY(i), id);
					}
					break;
				default: // 다른 액션일 경우
					int index = e.getActionIndex();
					int id = e.getPointerId(index);
					if(id >= mMaxTouches) return;
					mCurrentScene.handleTouchEvent(e, action, e.getX(index), e.getY(index), id);
					break;
			}
		} else {
			KeyEvent e = (KeyEvent) event;
			mCurrentScene.handleKeyEvent(e, e.getKeyCode());
		}
	}
	
	public int getMaxTouches() {
		return mMaxTouches;
	}
	
	public void setMaxTouches(int maxTouches) {
		mMaxTouches = MathUtils.clamp(maxTouches, 0, InputManager.MAX_TOUCHES);
	}
	
	/** 디렉터가 현재 진행중인 스케줄을 종료하고 다음 스케줄이 있다면 시작한다. */ 
	private void checkSchedule() {
		// 현재 스케쥴을 제거한다.
		mScheduleList.remove(0);
		// 다음 스케쥴이 있다면 실행
		checkNextSchedule();
	}

	private void checkNextSchedule() {
		if(!mScheduleList.isEmpty()) {
			Schedule s = mScheduleList.get(0);
			switch(s.task) {
				case TRANSITION:
					startTransition((Transition) s.scene);
					break;
				case PUSH:
					_pushScene();
					break;
				case POP:
					_popScene();
					break;
				case OVERLAP_ON_TOP:
					_overlapSceneOnTop();
					break;
				case OVERLAP_ON_BOTTOM:
					_overlapSceneOnBottom();
					break;
				case REMOVE_OVERLAPS:
					_removeOverlapScenes();
					break;
				case REMOVE_OVERLAP:
					_removeOverlapScene();
					break;
			}
		}
	}
	
	/**
	 * 도입 Scene(일반적으로 intro 또는 splash 화면)을 시작한다. 이 메서드만 
	 * UI스레드에서 호출할 수 있다.
	 * 
	 * @param scene
	 */
	public void startScene(final Scene<?> scene) {
		Core.APP.info("startScene called");

		// 이 메서드는 일반적으로 UI스레드에서 호출되므로 내부적으로 
		// 렌더링스레드에서 처리한다. 앱이 시작하면서 발생하는 배경의 
		// 잔상을 빠르게 제거하기 위해 첫번째 프레임은 화면을 정리하는 
		// 역할만을 한다.
		// 여기서 기본 runOnGLThread(Runnable)을 사용할 수 없다. 이벤트가 
		// onSurfaceCreated(...)가 호출되기 전에 처리되기 때문이다.
		Core.APP.runOnGLThread(1, new Runnable() {
			
			@Override
			public void run() {
				if(mSceneStack.capacity() != 0)
					throw new IllegalStateException("startScene(...) must be called before any pushScene(...) or changeScene(...)");
				mSceneStack.ensureCapacity(1);
				
				pushScene(scene);
				
				// 유저가 명시적으로 Batch를 지정하지 않았으면 다음과 같이 초기화한다.
				if(mBatch == null) mBatch = new ParallelBatch();
			}
		});
	}
	
	/** popScene과 pushScene을 연속으로 불러주는 과정을 랩핑한 메서드. */
	public void changeScene(Scene<?> scene) {
		if(mSceneStack.capacity() == 0)
			throw new IllegalStateException("First scene must start with startScene(...)");
		if(scene == null)
			throw new IllegalArgumentException("null scene not allowed");
		
		Scene<?> nextScene = scene;
		
		if(scene instanceof Transition) {
			Transition transition = (Transition) scene;
			
			mScheduleList.add(new Schedule(Task.TRANSITION, scene));
			if(mScheduleList.size() == 1) startTransition(transition);
			
			nextScene = transition.getNextScene();
			if(nextScene == null)
				throw new IllegalArgumentException("changeScene transition should have nextScene");
		}
		
		popScene();
		pushScene(nextScene);
	}
	
	public void pushScene(Scene<?> scene) {
		Core.APP.info("pushScene called");
		
		if(mSceneStack.capacity() == 0)
			throw new IllegalStateException("First scene must start with startScene(...)");
		if(scene == null)
			throw new IllegalArgumentException("null scene not allowed");
		
		Scene<?> nextScene = scene;
		
		if(scene instanceof Transition) {
			Transition transition = (Transition) scene;
			mScheduleList.add(new Schedule(Task.TRANSITION, scene));
			if(mScheduleList.size() == 1) startTransition(transition);
			
			nextScene = transition.getNextScene();
			if(nextScene == null)
				throw new IllegalArgumentException("pushScene transition should have nextScene");
		}
		
		mScheduleList.add(new Schedule(Task.PUSH, nextScene));
		if(mScheduleList.size() == 1) _pushScene();
	}
	
	private void _pushScene() {
		Scene<?> scene = mScheduleList.get(0).scene;

		if(mWorkingSceneList.contains(scene))
			throw new IllegalStateException("Same scene already exists in workingSceneArray");
			
		if(mCurrentScene != null) mCurrentScene.pause(false);
		
		mSceneStack.push(scene);
		
		int index = mWorkingSceneList.indexOf(mCurrentScene);
		
		mCurrentScene = scene;
		createScene(scene);
		
		if(index != -1)	mWorkingSceneList.set(index, scene);
		else					mWorkingSceneList.add(scene);
		
		checkSchedule();
	}
	
	public void popScene() {
		popScene(null);
	}
	
	/** 
	 * Transition 애니메이션을 첨가하여 popScene을 한다. transition의 
	 * nextScene은 null이어야 한다. 
	 */ 
	public void popScene(Transition transition) {
		Core.APP.info(TAG, "popScene called");
		
		if(transition != null) {
			if(transition.getNextScene() != null)
				throw new IllegalArgumentException("popScene transition shouldn't have nextScene");
			
			mScheduleList.add(new Schedule(Task.TRANSITION, transition));
			if(mScheduleList.size() == 1) startTransition(transition);
		}
		
		mScheduleList.add(new Schedule(Task.POP));
		if(mScheduleList.size() == 1) _popScene();
	}
	
	private void _popScene() {
		// 최상위 Scene을 pop한다. 스택이 비어있으면 예외발생
		Scene<?> scene = mSceneStack.pop();
		
		destroyScene(mCurrentScene, false);
		
		int index = mWorkingSceneList.indexOf(mCurrentScene);
		
		if(mSceneStack.isEmpty()) {
			mCurrentScene = null;
		} else {
			mCurrentScene = mSceneStack.peek();
			mCurrentScene.resume(false);
		}
		
		mWorkingSceneList.set(index, mCurrentScene);
		
		checkSchedule();
	}
	
	public void overlapSceneOnTop(Scene<?> scene) {
		Core.APP.info(TAG, "overlapSceneToTop called");
		mScheduleList.add(new Schedule(Task.OVERLAP_ON_TOP, scene));
		if(mScheduleList.size() == 1) _overlapSceneOnTop();
	}
	
	private void _overlapSceneOnTop() {
		Scene<?> scene = mScheduleList.get(0).scene;

		if(scene instanceof Transition)
			throw new IllegalArgumentException("Transition is not allowed for overlapSceneOnTop(Scene)");
		if(mWorkingSceneList.contains(scene))
			throw new IllegalStateException("Same scene already exists in workingSceneArray");
		
		createScene(scene);
		mWorkingSceneList.add(scene);
		
		checkSchedule();
	}
	
	public void overlapSceneOnBottom(Scene<?> scene) {
		Core.APP.info(TAG, "overlapSceneToBottom called");
		mScheduleList.add(new Schedule(Task.OVERLAP_ON_BOTTOM, scene));
		if(mScheduleList.size() == 1) _overlapSceneOnBottom();
	}
	
	private void _overlapSceneOnBottom() {
		Scene<?> scene = mScheduleList.get(0).scene;

		if(scene instanceof Transition)
			throw new IllegalArgumentException("Transition is not allowed for overlapSceneOnBottom(Scene)");
		if(mWorkingSceneList.contains(scene))
			throw new IllegalStateException("Same scene already exists in workingSceneArray");
		
		createScene(scene);
		mWorkingSceneList.add(0, scene);
		
		checkSchedule();
	}
	
	public void removeOverlapScenes() {
		Core.APP.info(TAG, "removeOverlapScenes called");
		mScheduleList.add(new Schedule(Task.REMOVE_OVERLAPS));
		if(mScheduleList.size() == 1) _removeOverlapScenes();
	}
	
	private void _removeOverlapScenes() {
		List<Scene<?>> sceneList = mWorkingSceneList;
    	int n = sceneList.size();
    	for(int i=n-1; i>-1; i--) {
    		Scene<?> scene = sceneList.get(i);
    		if(scene == mCurrentScene) continue;
    		destroyScene(scene, false);
    		mWorkingSceneList.remove(i);
    	}

		checkSchedule();
	}
	
	public void removeOverlapScene(int index) {
		Core.APP.info(TAG, "removeOverlapSceneWithIndex called");
		mScheduleList.add(new Schedule(Task.REMOVE_OVERLAP, index));
		if(mScheduleList.size() == 1) _removeOverlapScene();
	}
	
	private void _removeOverlapScene() {
		int index = mScheduleList.get(0).index;
		Scene<?> scene = mWorkingSceneList.get(index);
		if(scene == mCurrentScene)
			throw new IllegalStateException("Removing current Scene not possible");
		
		mWorkingSceneList.remove(index);
		destroyScene(scene, false);
		
		checkSchedule();
	}
	
	/**
	 * 스택의 최상위 Scene을 얻는다. 성능을 위해 최상위 Scene을 미리 보관하는 멤버
	 * 변수를 사용한다.</p>
	 * 
	 * Transition 중이라면 이전 Scene이 리턴된다. Transition이 완전히 끝나야 새로운 
	 * Scene이 리턴된다.</p>
	 */
	public Scene<?> getCurrentScene() {
		return mCurrentScene;
	}
	
	/** 
	 * 스택의 최상위 Scene 바로 아래의 Scene을 얻는다. 스택에 Scene이 하나만 존재 한다면 
	 * null을 리턴한다.
	 */
	public Scene<?> getPreviousScene() {
		int n = mSceneStack.size();
		if(n < 2) return null;
		return mSceneStack.get(n-2);
	}
	
	/** 스택 취상위 Scene을 비롯하여 오버랩된 Scene들의 리스트를 리턴한다. */
	public SnapshotArrayList<Scene<?>> getWorkingSceneList() {
		return mWorkingSceneList;
	}
	
	/** 
	 * 매개변수로 전달된 Scene을 초기화한다. {@link Scene#create()}을 호출하고 
	 * 현재 Director에 지정된 Act를 Scene의 Act로 설정한다. Scene의 초기화가 
	 * 성공적으로 끝나면 true을 리턴하고, 이미 초기화 되어 있으면 false를 리턴한다. 
	 */
	public boolean createScene(Scene<?> scene) {
		if(scene.isCreated()) return false;
		scene.setCreated(true);
		scene.setAct(mCurrentAct);
		scene.mStage = new Stage();
		scene.create();
		return true;
	}
	
	public boolean destroyScene(Scene<?> scene, boolean lifeCycle) {
		if(!scene.isCreated()) return false;
		scene.destroy(lifeCycle);
		scene.setCreated(false);
		scene.setAct(null);
		scene.mStage = null;
		return true;
	}
	
	public Act getCurrentAct() {
		return mCurrentAct;
	}
	
	/** 
	 * 현재 Act를 지정한다. 이전에 지정된 Act가 있다면 {@link Act#destroy()}가 호출되고  
	 * 매개변수로 전달된 Act가 초기화되지 않았다면 {@link Act#create()}가 호출된다. 
	 * */
	public void setCurrentAct(Act act) {
		if(mCurrentAct != null) {
			mCurrentAct.setCreated(false);
			mCurrentAct.destroy();
		}
		if(act != null && !act.isCreated()) {
			act.setCreated(true);
			act.create();
		}
		mCurrentAct = act;
	}
	
	public Batch getBatch() {
		return mBatch;
	}

	/** 
	 * Scene을 렌더링할 {@link Batch}를 지정한다. 유저가 지정하지 않은 경우 디폴트로 
	 * {@link ParallelBatch}가 선택된다. 
	 */
	public void setBatch(Batch batch) {
		mBatch = batch;
	}
	
	private void startTransition(Transition transition) {
		mCurrentTransition = transition;
		Scene<?> nextScene = transition.getNextScene();
		if(nextScene != null) createScene(nextScene);
		Core.INPUT.setInputEnabled(false);
	}

	public Transition getTransition() {
		return mCurrentTransition;
	}
	
	public TransitionListener getTransitionListener() {
		return mTransitionListener;
	}

	public void setTransitionListener(TransitionListener listener) {
		mTransitionListener = listener;
	}

	public static abstract class TransitionListener {
		
		public void onStart(Transition transition) {
		}

        public void onEnd(Transition transition) {
        }
    }
	
	private static class Schedule {
		public Task task;
		public Scene<?> scene;
		public int index;
		
		public Schedule(Task task) {
			this.task = task;
		}
		
		public Schedule(Task task, Scene<?> scene) {
			this.task = task;
			this.scene = scene;
		}
		
		public Schedule(Task task, int index) {
			this.task = task;
			this.index = index;
		}
	}
	
}
