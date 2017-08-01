package core.scene.stage;

import java.util.List;
import java.util.ListIterator;

import core.framework.Core;
import core.framework.graphics.OrthoCamera;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.ShapeRenderer.ShapeType;
import core.framework.graphics.batch.Batch;
import core.math.Vector2;
import core.scene.stage.Stage.StageGroup;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.event.EventListener;
import core.scene.stage.actor.event.TouchEvent;
import core.utils.SnapshotArrayList;

/**
 * 각 Scene의 렌더링 및 터치이벤트 처리는 Stage의 Floor이 직접적으로 담당한다. Floor는 
 * Stage에서 일종의 레이어와 같은 역할을 하며, Scene 그래프에서 사실상의 root역할을 한다.</p>
 * 
 * 기본적으로 Floor에는 디폴트 카메라가 사용된다. 하지만 원하는 경우에는 Floor에 고유의 
 * 카메라를 지정할 수 있다.</p>
 * 
 * Floor는 down이벤트를 제외한 터치이벤트를 {@link TouchFocus}를 통해 처리한다.</p>
 * 
 * Floor의 개수가 많아질수록 openGL의 draw...(...)의 개수가 많아져 성능에 영향을
 * 끼칠 수 있다.</p>
 * 
 * @author 김현우
 */
public class Floor {
	
	private static final Vector2 VECTOR = new Vector2();
	
	private static ShapeRenderer sDebugRenderer;
	
	private Stage mStage;
	
	private OrthoCamera mCamera = Core.GRAPHICS.getDefaultCamera();
	
	/*package*/ FloorGroup mFloorGroup = new FloorGroup(this);
	
	private SnapshotArrayList<TouchFocus> mTouchFocusList = new SnapshotArrayList<TouchFocus>();
	
	/*package*/ Floor(Stage stage) {
		mStage = stage;
	}
	
	public Floor addChild(Actor<?> child) {
		mFloorGroup.addChild(child);
		return this;
	}
	
	public Floor addChild(int index, Actor<?> child) {		
		mFloorGroup.addChild(index, child);
		return this;
	}

	public Floor addChildBefore(Actor<?> childBefore, Actor<?> newChild) {	
		mFloorGroup.addChildBefore(childBefore, newChild);
		return this;
	}
	
	public Floor addChildAfter(Actor<?> childAfter, Actor<?> newChild) {		
		mFloorGroup.addChildAfter(childAfter, newChild);
		return this;
	}
	
	public boolean hasChild(Actor<?> child) {
		return mFloorGroup.hasChild(child);
	}
	
	public boolean hasChildren() {
		return mFloorGroup.hasChildren();
	}
	
	public Actor<?> getChildByTag(String tag) {
		return getChildByTag(tag, true);
	}
	
	public Actor<?> getChildByTag(String tag, boolean recursive) {
		return mFloorGroup.getChildByTag(tag, recursive);
	}

	public List<Actor<?>> getChildList() {
		return mFloorGroup.getChildList();
	}
	
	public boolean removeChild(Actor<?> child) {
		return mFloorGroup.removeChild(child);
	}
	
	public Floor clearChildren() {
		mFloorGroup.clearChildren();
		return this;
	}
	
	public Floor addAction(Action action) {
		mFloorGroup.addAction(action);
		return this;
	}
	
	public Floor addEventCaptureListener(EventListener listener) {
		mFloorGroup.addEventCaptureListener(listener);
		return this;
	}
	
	public Floor addEventListener(EventListener listener) {
		mFloorGroup.addEventListener(listener);
		return this;
	}
	
	/*package*/ void handleTouchFocusEvent(TouchEvent event) {
		SnapshotArrayList<TouchFocus> touchFocusList = mTouchFocusList;
		ListIterator<TouchFocus> it = touchFocusList.begin();
		while(it.hasNext()) {
			TouchFocus focus = it.next();
			if(focus.mPointerID != event.getPointerID()) continue;
			
			Actor<?> targetActor = focus.mTargetActor;
			Actor<?> listenerActor = focus.mListenerActor;
			
			if(!listenerActor.isVisible() 
					|| !listenerActor.isTouchable()
					|| !targetActor.hasFloor()
					|| listenerActor.getFloor() != this) {
				touchFocusList.remove(focus);
				continue;
			}
			
			event.setTargetActor(targetActor);
			event.setListenerActor(listenerActor);
			focus.mEventListener.handle(event);
		}
		touchFocusList.end(it);
	}
	
	public Stage getStage() {
		return mStage;
	}
	
	public OrthoCamera getCamera() {
		return mCamera;
	}
	
	public Floor setCamera(OrthoCamera camera) {
		mCamera = (camera == null)? Core.GRAPHICS.getDefaultCamera() : camera;
		return this;
	}
	
	public boolean isVisible() {
		return mFloorGroup.isVisible();
	}
	
	public boolean isTouchable() {
		return mFloorGroup.isTouchable();
	}

	public Floor setVisible(boolean visible) {
		mFloorGroup.setVisible(visible);
		return this;
	}

	public Floor setTouchable(boolean touchable) {
		mFloorGroup.setTouchable(touchable);
		return this;
	}
	
	public Group<?> getFloorGroup() {
		return mFloorGroup;
	}

	public void addTouchFocus(TouchFocus focus) {
		mTouchFocusList.add(focus);
	}
	
	public boolean removeTouchFocus(EventListener listener, Actor<?> listenerActor, Actor<?> targetActor, int pointerID) {
		List<TouchFocus> touchFocusList = mTouchFocusList;
		int n = touchFocusList.size();
		for(int i=0; i<n; i++) {
			TouchFocus focus = touchFocusList.get(i);
			if(focus.mEventListener == listener 
					&& focus.mListenerActor == listenerActor
					&& focus.mTargetActor == targetActor
					&& focus.mPointerID == pointerID) {
				touchFocusList.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * 스크린 좌표계에서 Floor좌표계로 변환한다. 전자는 안드로이드의 좌표계(y축이 아래를 향하는)에서 
	 * 정의되고 후자는 OpenGL의 좌표계(y축이 위를 향하는)에서 정의된다. 
	 */
	public Vector2 screenToFloorCoordinates(Vector2 pos) {
		// 스크린 좌표계와 OpenGL 좌표계의 y축의 방향이 반대이므로 다음의 계산이 필요하다.
		pos.y = Core.GRAPHICS.getHeight() - pos.y;
		return mCamera.unproject(pos);
	}
	
	/** 
	 * Floor좌표계에서 스크린 좌표계로 변환한다. 전자는 OpenGL의 좌표계(y축이 위를 향하는)에서 
	 * 정의되고 후자는 안드로이드의 좌표계(y축이 아래를 향하는)에서 정의된다. 
	 */
	public Vector2 floorToScreenCoordinates(Vector2 pos) {
		mCamera.project(pos);
		// 스크린 좌표계와 OpenGL 좌표계의 y축의 방향이 반대이므로 다음의 계산이 필요하다.
		pos.y = Core.GRAPHICS.getHeight() - pos.y;
		return pos;
	}
	
	public Floor disposeAll() {
		mFloorGroup.disposeAll();
		return this;
	}
	
	public boolean willDebug() {
		return mFloorGroup.willDebug();
	}

	public Floor setDebug(boolean debug) {
		mFloorGroup.setDebug(debug);
		return this;
	}
	
	public Floor setDebug(boolean debug, boolean recursive) {
		mFloorGroup.setDebug(debug, recursive);
		return this;
	}
	
	public Floor setDebug(boolean debug, int recursionDepth) {
		mFloorGroup.setDebug(debug, recursionDepth);
		return this;
	}
	
	public Floor debugAll() {
		mFloorGroup.debugAll();
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int index = mStage.getFloorList().indexOf(this);
		builder.append("----- Floor [" + index + "] begin -----");
		List<Actor<?>> childList = mFloorGroup.getChildList();
		int n = childList.size();
		for(int i=0; i<n; i++) {
			Actor<?> child = childList.get(i);
			builder.append("\n" + child.toString());
		}
		builder.append("\n----- Floor [" + index + "] end -----");
		return builder.toString();
	}

	public static ShapeRenderer getDebugRenderer() {
		return sDebugRenderer;
	}

	public static class TouchFocus {
		private EventListener mEventListener;
		private Actor<?> mListenerActor;
		private Actor<?> mTargetActor;
		private int mPointerID;
		
		public TouchFocus(EventListener eventListener, Actor<?> listenerActor, Actor<?> targetActor, int pointerID) {
			mEventListener = eventListener;
			mListenerActor = listenerActor;
			mTargetActor = targetActor;
			mPointerID = pointerID;
		}
	}

	/*package*/ static class FloorGroup extends Group<FloorGroup> {
		
		public FloorGroup(Floor floor) {
			setFloor(floor);
			mTransformMatrixApplied = false;
		}
		
		@Override
		public void update(long time) {
			super.update(time);
			getFloor().mCamera.update();
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
		
			boolean debug = willDebug();
			
			// 디버깅 시작
			if(debug) {
				if(sDebugRenderer == null) sDebugRenderer = new ShapeRenderer(1000, true);
				sDebugRenderer.setProjectionMatrix(getFloor().mCamera.getCombinedMatrix());
				sDebugRenderer.setLineWidth(3f);
				sDebugRenderer.begin(ShapeType.LINE);
				if(isVisible()) drawDebug(batch, sDebugRenderer);
			}
		
			// 카메라의 최종매트릭스를 batch의 투영매트릭스로 설정
			batch.setProjectionMatrix(getFloor().mCamera.getCombinedMatrix());
			
			// Floor에 배치된 Actor의 출력을 시작한다.
			if(mVisible != Visible.DISABLED) {
				pushTransformation(batch);
				drawChildren(batch, parentAlpha);
				popTransformation(batch);
			}
			
			// 마지막으로 현재 Floor에서 축적된 출력할 내용을 flush한다.
			batch.flush();
			
			// 디버깅 출력 종료. 마지막으로 출력하여 가장 위에 나타나도록 한다.
			if(debug) sDebugRenderer.end();
		}
		
		@Override
		protected void setFloor(Floor floor) {
			if(hasFloor()) return;
			super.setFloor(floor);
		}

		@Override
		protected void setParent(Group<?> parent) {
			if(parent == null) return;
			if(!(parent instanceof StageGroup))
				throw new UnsupportedOperationException("Group of Floor can't have a parent.");
		}

		@Override
		public FloorGroup setDebug(boolean debug) {
			mDebug = debug;
			return this;
		}
	}
	
}
