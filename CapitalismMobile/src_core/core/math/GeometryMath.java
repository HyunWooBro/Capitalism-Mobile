package core.math;

/** 
 * 기하학(geometry) 관련 수학메서드를 제공한다.
 * 
 * @author 김현우
 */
public class GeometryMath {
	
	private GeometryMath() {
	}
	
	/** 
	 * 점 a(x1, y1)과 점 b(x2, y2)를 지나는 직선의 기울기를 구한다. 만약 x1과 x2의 값이 같다면 
	 * {@link Float#POSITIVE_INFINITY}를 리턴한다. 
	 */
	public static float getLineSlope(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		if(dx == 0f) return Float.POSITIVE_INFINITY;
		return (y2 - y1) / dx;
	}
	
	/** 
	 * 점 a(x1, y1)과 점 b(x2, y2)를 지나는 직선과 수직인 직선의 기울기를 구한다. 만약 y1과 y2의 값이 같다면 
	 * {@link Float#POSITIVE_INFINITY}를 리턴한다. 
	 */
	public static float getInvLineSlope(float x1, float y1, float x2, float y2) {
		float dy = y2 - y1;
		if(dy == 0f) return Float.POSITIVE_INFINITY;
		return - (x2 - x1) / dy;
	}
	
	/** 
	 * 기울기가 a이며 점 p(x1, y1)을 지나는 선이 있을 때, 이 선의 y2에 대응되는 x2를 구한다. 만약 
	 * 기울기 a가 0이라면 {@link Float#NaN}을 리턴한다.
	 */
	public static float getXOnLine(float a, float x1, float y1, float y2) {
		if(a == 0f) return Float.NaN;
		return (y2 - y1)/a + x1;
	}
	
	/** 기울기가 a이며 점 p(x1, y1)을 지나는 선이 있을 때, 이 선의 x2에 대응되는 y2를 구한다. */
	public static float getYOnLine(float a, float x1, float y1, float x2) {
		return a*(x2 - x1) + y1;
	}
	
	/**
	 * 점 c가 점 a와 점 b로 이루어진 선을 기준으로 어느 위체에 있는지를 체크한다. a에서 b를 향하는 
	 * 선을 기준으로 -1인 경우 왼쪽, 0인 경우 선의 위, 1인 경우 오른쪽에 위치한다.
	 * 
	 * @see http://stackoverflow.com/questions/1560492/how-to-tell-whether-a-point-is-to-the-right-or-left-side-of-a-line
	 */
	public static int determineWhichSidePointOn(Vector2 a, Vector2 b, Vector2 c){
	     return (int) Math.signum((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x));
	}
	
	public static int determineWhichSidePointOn(float x1, float y1, float x2, float y2, float x3, float y3){
	     return (int) Math.signum((x2 - x1)*(y3 - y1) - (y2 - y1)*(x3 - x1));
	}
	
	/**
	 * 점 a(x1, y1), 점 b(x2, y2)로 이루어진 선 A와 점 c(x3, y3), 점 d(x4, y4)로 이루어진 
	 * 선 B가 만난다면 true을 리턴한다. 추가적으로, intersection을 통해 두 선이 만나는 지점을 
	 * 구할 수 있다.
	 * 
	 * @see http://paulbourke.net/geometry/pointlineplane/
	 */
	public static boolean intersectTwoLines(float x1, float y1, float x2, float y2, 
			float x3, float y3, float x4, float y4, Vector2 intersection) {
		
		float denominator = (y4-y3)*(x2-x1) - (x4-x3)*(y2-y1);
		if(denominator == 0) return false;
		
		if(intersection != null) {
			float u1 = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / denominator;
			intersection.set(x1 + u1*(x2-x1), y1 + u1*(y2-y1));
		}
		return true;
	}
}
