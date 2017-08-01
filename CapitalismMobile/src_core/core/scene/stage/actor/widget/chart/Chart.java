package core.scene.stage.actor.widget.chart;

import core.scene.stage.actor.widget.Widget;
import core.scene.stage.actor.widget.utils.Align;

/**
 * </p>
 * 
 * @author 김현우
 */
public abstract class Chart<T extends Chart<T>> extends Widget<T> {
	
	// TODO 구현 필요 (core.scene.stage.actor.widget.chart 전체)
	
	// 문제는 이것은 커스터마이징이 필수적인 부분이라는 것이다. 즉, 원형 그대로 게임에서 사용하기 어렵다.
	
	protected String mTitle;
	
	protected Align mTitleAlign;
	
	protected ChartData mChartData = new ChartData();

	public Chart() {
		this(null);
	}
	
	public Chart(Costume costume) {
		super(costume);
	}

	@Override
	protected float getDefaultPrefWidth() {
		return 0;
	}

	@Override
	protected float getDefaultPrefHeight() {
		return 0;
	}

	public ChartData getChartData() {
		return mChartData;
	}
	
	

}
