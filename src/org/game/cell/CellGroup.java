package org.game.cell;

import android.graphics.*;

/**
 * 2X2 이상의 셀을 점유하는 건물을 올바르게 그리기 위해 필요한 정보를 보관하는 클래스
 * @author 김현우
 *
 */
public class CellGroup {
	public Point m_point;				// 건물의 마지막 셀 위치
	public int m_value;				// 최하단 셀로부터의 거리와 건물의 크기를 더한 값
												// 이 값이 큰 것부터 먼저 그려야 한다.
	
	public CellGroup(Point point, int value)
	{
		m_point = point;
		m_value = value;
	}

}
