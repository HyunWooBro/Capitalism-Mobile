package core.math;

/** 
 * 행우선(행렬의 원소를 가로로 연결하여 메모리에 저장하는 형식) 3x3 열벡터행렬이며 안드로이드에서 
 * 기본적으로 사용되는 형식이다. 2D프로젝트에서 매트릭스를 처리할 필요가 있다면 Matrix4보다는 Matrix3을 
 * 사용하면 충분하다.</p>
 * 
 * android.graphics.Matrix를 확장한다.</p> 
 * 
 * @author 김현우
 */
public class Matrix3 extends android.graphics.Matrix {
	
	public static final int M00 = 0;
    public static final int M01 = 1;
    public static final int M02 = 2;
    
    public static final int M10 = 3;
    public static final int M11 = 4;
    public static final int M12 = 5;
    
    public static final int M20 = 6;
    public static final int M21 = 7;
    public static final int M22 = 8;
	
	public static final float[] TMP_VALUE = new float[9];
	
	public Matrix3() {
		super();
    }

    public Matrix3(Matrix3 src) {
        super(src);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append("Matrix3 {");
        sb.append(toShortString());
        sb.append("}");
        return sb.toString();
                
    }
}
