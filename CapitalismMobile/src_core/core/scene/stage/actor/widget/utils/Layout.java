package core.scene.stage.actor.widget.utils;

import core.framework.graphics.batch.Batch;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.Widget;
import core.scene.stage.actor.widget.WidgetGroup;
import core.scene.stage.actor.widget.table.Table;

/**
 * Widget과 WidgetGroup의 레이아웃과 관련된 메서드의 원형을 정의한다.</p>
 * 
 * 최소, 선호, 최대 사이즈는 {@link Table}과 관련하여 사용된다.</p>
 * 
 * 선호 사이즈에 한해 원하는 값을 직접 입력할 수 있다.</p>
 * 
 * @see Widget
 * @see WidgetGroup
 * @author 김현우
 */
public interface Layout<T extends Layout<T>> {
	
	/** 
	 * 기본적으로 {@link #clampSize()}를 호출한다. 이 메서드를 재정의하여 추가적인 
	 * 레이아웃 관련 작업을 정의할 수 있다.</p>
	 * 
	 * 이 메서드를 직접 호출하기 보다는 {@link #validate()}를 통해 간접적으로 호출해야 
	 * 한다.</p>
	 */
	public void layout();
	
	/**
	 * 현재의 사이즈를 최소 사이즈와 최대 사이즈 사이로 clamp한다.
	 */
	public void clampSize();
	
	/** 
	 * {@link #setFillParent(boolean)}를 true로 지정했다면 자신의 사이즈를 부모 또는 
	 * Floor의 사이즈와 동일하게 수정하여 부모를 채우며, 레이아웃이 무효화된 상태라면 
	 * {@link #layout()}을 호출한다. 이들은 레이아웃이 활성화되어 있을 경우에만 적용된다.</p>
	 * 
	 * 레이아웃을 구현하는 Actor는 이 메서드를 {@link Actor#draw(Batch, float)}에서 
	 * 호출해야 한다.</p>
	 */
	public void validate();
	
	/** 레이아웃을 무효화하여 {@link #layout()}이 호출될 수 있도록 한다. */
	public void invalidate();
	
	/** 
	 * Layout을 구현하는 부모를 재귀적으로 invalidate한다. 자신의 상태가 변경되어 
	 * 부모의 레이아웃에도 영향을 끼칠 가능성이 있다면 invalidate보다는 이 메서드를 
	 * 호출해야 한다.</p>
	 * 
	 * 부모의 레이아웃에 영향을 끼칠 수 있는 상태에는 위치, 사이즈, 스케일, 회전 등이 
	 * 있다. 그런데 이들이 부모의 레이아웃과 독립적으로 이용되는 경우도 있기 때문에 
	 * 기본적으로 부모까지 invalidate하지 않는다. 물론, 그 때문에 부모에 속한 자식이 
	 * 상태를 임의로 바꾸는 경우 전체 레이아웃이 깨질 위험은 있다. 이것을 방지하기 
	 * 위해서는 직접 이 메서드를 호출해야 한다.</p>
	 */
	public void invalidateHierarchy();
	
	/** 부모를 채울 것인지 지정한다. 부모가 없으면 Floor를 채우도록 한다. */
	public T setFillParent(boolean fillParent);
	
	/** 레이아웃의 활성화 상태를 지정한다. */
	public T setLayoutEnabled(boolean enabled);

	/** 레이아웃이 무효화된 상태라면 true을 리턴한다. */
	public boolean isInvalidated();

	/** 부모를 채우는 경우 true을 리턴한다. */
	public boolean getFillParent();

	/** 레이아웃이 활성화된 경우 true을 리턴한다.*/
	public boolean isLayoutEnabled();
	
	/** 최소 너비를 얻는다. */
	public float getMinWidth();

	/** 최소 높이를 얻는다. */
	public float getMinHeight();

	/** 선호 너비를 얻는다. */
	public float getPrefWidth();

	/** 선호 높이를 얻는다. */
	public float getPrefHeight();

	/** 최대 너비를 얻는다. 최대 너비를 제한하지 않는 경우 null을 리턴한다. */
	public Float getMaxWidth();

	/** 최대 높이를 얻는다. 최대 높이를 제한하지 않는 경우 null을 리턴한다. */
	public Float getMaxHeight();
	
	/** 
	 * 선호 너비를 직접 입력한다. 디폴트 선호 너비를 대신한다. null을 입력하면
	 * 디폴트 선호 너비가 사용된다. 
	 */
	public T setPrefWidth(Float width);
	
	/** 
	 * 선호 높이를 직접 입력한다. 디폴트 선호 높이를 대신한다. null을 입력하면
	 * 디폴트 선호 높이가 사용된다. 
	 */
	public T setPrefHeight(Float height);
	
	/** 
	 * 선호 사이즈를 직접 입력한다. 디폴트 선호 사이즈를 대신한다. null을 입력하면
	 * 디폴트 선호 사이즈가 사용된다. 
	 */
	public T setPrefSize(Float width, Float height);
	
	/** 선호 너비를 직접 입력한 경우 true를 리턴한다. */
	public boolean isPrefWidthSet();
	
	/** 선호 높이를 직접 입력한 경우 true를 리턴한다. */
	public boolean isPrefHeightSet();

	/** 
	 * Actor의 사이즈를 {@link #getPrefWidth()}와 {@link #getPrefHeight()}가 반환하는 
	 * 값으로 변경한다. 
	 */
	public T pack();
}
