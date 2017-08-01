package core.scene.stage;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.view.MotionEvent;

import core.framework.graphics.batch.Batch;
import core.framework.input.InputManager;
import core.math.Vector2;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchEvent.TouchType;
import core.utils.SnapshotArrayList;

/**
 * Scene의 렌더링 및 터치이벤트를 처리하는 과정은 기본적으로 Stage를 통해서 이루어진다. 
 * 특히, 터치이벤트의 경우 안드로이드의 raw 이벤트인 {@link MotionEvent}를 받아 {@link TouchEvent}로 
 * 가공하여 Actor 및 Group에 전달하여 처리하게 된다.</p>
 * 
 * Stage는 Actor을 직접적으로 처리하는 Floor을 관리한다. 렌더링은 가장 낮은 단계의 Floor부터 
 * 시작되며, 이벤트는 가장 높은 단계의 Floor부터 처리할 기회가 주어진다.</p>
 * 
 * 한편, 터치이벤트 중 터치오버이벤트(Enter, Exit)의 경우는 update(...)에서 처리된다.
 * 
 * @author 김현우
 */
public class Stage {
	
	private static final Vector2 VECTOR = new Vector2();
	
	private SnapshotArrayList<Floor> mFloorList = new SnapshotArrayList<Floor>();
	
	private StageGroup mStageGroup = new StageGroup(this);
	
	private float[] mOverScreenX = new float[InputManager.MAX_TOUCHES];
	private float[] mOverScreenY = new float[InputManager.MAX_TOUCHES];
	private Actor<?>[] mOverActors = new Actor<?>[InputManager.MAX_TOUCHES];
	private boolean[] mOverTouched = new boolean[InputManager.MAX_TOUCHES];
	
	private float mAlpha = 1f;
	
	public Stage() {
	}
	
	public Floor addFloor() {
		Floor floor = new Floor(this);
		mFloorList.add(floor);
		mStageGroup.addChild(floor.mFloorGroup);
		return floor;
	}
	
	public Floor getFloor(int index) {
		return mFloorList.get(index);
	}
	
	public Floor getFirstFloor() {
		return (mFloorList.isEmpty())? null : mFloorList.get(0);
	}
	
	public Floor getLastFloor() {
		return (mFloorList.isEmpty())? null : mFloorList.get(mFloorList.size()-1);
	}
	
	public SnapshotArrayList<Floor> getFloorList() {
		return mFloorList;
	}
	
	public Floor removeFloor(int index) {
		Floor floor = mFloorList.remove(index);
		mStageGroup.removeChild(floor.mFloorGroup);
		return floor;
	}
	
	public boolean removeFloor(Floor floor) {
		mStageGroup.removeChild(floor.mFloorGroup);
		return mFloorList.remove(floor);
	}

	public Actor<?> getActorByTag(String tag) {
		return getActorByTag(tag, true);
	}
	
	public Actor<?> getActorByTag(String tag, boolean recursive) {
		return mStageGroup.getChildByTag(tag, recursive);
	}
	
	public void update(long time) {
		mStageGroup.update(time);
		handleTouchOverEvent();
	}
	
	private void handleTouchOverEvent() {
		if(!mStageGroup.isVisible() || !mStageGroup.isTouchable()) return;

		int n = InputManager.MAX_TOUCHES;
		for(int i=0; i<n; i++) {
			Actor<?> overActor = mOverActors[i];
			if(!mOverTouched[i]) {
				if(overActor != null) {
					if(overActor.hasFloor()) {
						TouchEvent e = new TouchEvent(TouchType.TOUCH_EXIT, mOverScreenX[i], mOverScreenY[i], i, null);
						overActor.fire(e);
					}
					mOverActors[i] = null;
				}
			} else {
				Actor<?> contact = mStageGroup.contact(mOverScreenX[i], mOverScreenY[i]);
				if(contact != null || overActor != null) {
					if(contact == overActor) continue;
					
					if(overActor != null && overActor.hasFloor()) {
						TouchEvent e = new TouchEvent(TouchType.TOUCH_EXIT, mOverScreenX[i], mOverScreenY[i], i, null);
						e.setOverActor(contact);
						overActor.fire(e);
					}
					
					if(contact != null) {
						TouchEvent e = new TouchEvent(TouchType.TOUCH_ENTER, mOverScreenX[i], mOverScreenY[i], i, null);
						e.setOverActor(overActor);
						contact.fire(e);
					}
					
					mOverActors[i] = contact;
				}
			}
			
		}
	}
	
	public void draw(Batch batch) {
		mStageGroup.draw(batch, 1f);
	}

	public boolean handleTouchEvent(MotionEvent event, int action, float screenX, float screenY, int pointerID) {
		if(!mStageGroup.isVisible() || !mStageGroup.isTouchable()) return false;
		
		mOverScreenX[pointerID] = screenX;
		mOverScreenY[pointerID] = screenY;
		
		TouchEvent e = null;
		
		// down 이벤트의 경우
		if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			mOverTouched[pointerID] = true;
			Actor<?> contact = mStageGroup.contact(screenX, screenY);
			if(contact != null) {
				e = new TouchEvent(TouchType.TOUCH_DOWN, screenX, screenY, pointerID, event);
				contact.fire(e);
				return e.isHandled();
			}
		} else { // 그 밖의 이벤트의 경우
			switch(action) {
				case MotionEvent.ACTION_MOVE:
					e = new TouchEvent(TouchType.TOUCH_MOVE, screenX, screenY, pointerID, event);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE:
					mOverTouched[pointerID] = false;
					e = new TouchEvent(TouchType.TOUCH_UP, screenX, screenY, pointerID, event);
					break;
			}
			
			// Floor에서 따로 TouchFocus를 통해 처리한다.
			SnapshotArrayList<Floor> floorList = mFloorList;
			ListIterator<Floor> it = floorList.begin();
			while(it.hasNext()) {
				Floor floor = it.next();
				floor.handleTouchFocusEvent(e);
			}
			floorList.end(it);
			return e.isHandled();
		}
		
		return false;
	}
	
	public float getAlpha() {
		return mAlpha;
	}
	
	public Stage setAlpha(float alpha) {
		mAlpha = alpha;
		return this;
	}

	public boolean isVisible() {
		return mStageGroup.isVisible();
	}

	public boolean isTouchable() {
		return mStageGroup.isTouchable();
	}

	public void setVisible(boolean visible) {
		mStageGroup.setVisible(visible);
	}

	public void setTouchable(boolean touchable) {
		mStageGroup.setTouchable(touchable);
	}

	public Stage disposeAll() {
		mStageGroup.disposeAll();
		return this;
	}
	
	public Stage debugAll() {
		mStageGroup.debugAll();
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("########--- Stage ---########");
		List<Floor> floorList = mFloorList;
		int n = floorList.size();
		for(int i=0; i<n; i++) {
			Floor floor = floorList.get(i);
			builder.append("\n" + floor.toString());
		}
		builder.append("\n##############################");
		return builder.toString();
	}
	
	/*package*/ static class StageGroup extends Group<StageGroup> {
		
		private Stage mStage;
		
		public StageGroup(Stage stage) {
			mStage = stage;
		}
		
		@Override
		protected void prepare() {
			// Action, EventListener, Form 등은 사용되지 않으므로 초기화하지 않는다.
			mChildList = new SnapshotArrayList<Actor<?>>();
		}
		
		@Override
		public void update(long time) {
			ListIterator<Actor<?>> it = mChildList.begin();
			while(it.hasNext()) {
				Actor<?> child = it.next();
				child.update(time);
			}
			mChildList.end(it);
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if(mVisible == Visible.DISABLED) return;
			drawChildren(batch, parentAlpha);
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			float alpha = parentAlpha * mStage.mAlpha;
			ListIterator<Actor<?>> it = mChildList.begin();
			while(it.hasNext()) {
				Actor<?> child = it.next();
				if(child.getVisible() == Visible.DISABLED) continue;
				child.draw(batch, alpha);
			}
			mChildList.end(it);
		}

		@Override
		public Actor<?> contact(float x, float y) {
			final Vector2 v = VECTOR;
			List<Actor<?>> childList = mChildList;
			int n = childList.size();
			for(int i=n-1; i>-1; i--) {
				Actor<?> child = childList.get(i);
				if(child.getVisible() == Visible.DISABLED) continue;
				child.screenToLocalCoordinates(v.set(x, y)); 
				Actor<?> contact = child.contact(v.x, v.y);
				if(contact != null) return contact;
			}
			return null;
		}

	}

}
