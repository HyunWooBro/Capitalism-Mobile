package core.framework.graphics;

import java.util.Stack;

import android.opengl.GLES20;

import core.framework.Core;
import core.framework.graphics.utils.ShaderProgram;
import core.framework.graphics.utils.VertexAttribute;
import core.framework.graphics.utils.mesh.Mesh;
import core.math.MathUtils;
import core.math.Matrix3;
import core.math.Matrix4;
import core.math.Vector2;
import core.utils.Disposable;

public class ShapeRenderer implements Disposable {
	
	public enum ShapeType {
		POINT(GLES20.GL_POINTS), 
		LINE(GLES20.GL_LINES), 
		FILLED(GLES20.GL_TRIANGLES);
		
		private int mType;
		ShapeType(int type) {mType = type;}
		public int getGLConstant() {return mType;}
	}
	
	public static final int DEFAULT_MAX_VERTICES = 2000;
	
	private static final int[] TMP_PARAMS = new int[2];
	
	private static final String VERTEX_SHADER_CODE =    
			"uniform mat4 " + ShaderProgram.UNIFORM_PROJECTION_MATRIX + ";" +
			"attribute vec4 " + ShaderProgram.ATTRIBUTE_POSITION +";" +
			"attribute vec4 " + ShaderProgram.ATTRIBUTE_COLOR + ";" +
			"uniform float " + ShaderProgram.UNIFORM_POINT_SIZE + ";" +
			"varying vec4 v_color;" + 
		    "void main() {" +
		    "  gl_Position = " + ShaderProgram.UNIFORM_PROJECTION_MATRIX + " * " + ShaderProgram.ATTRIBUTE_POSITION + ";" +
		    "  v_color = " + ShaderProgram.ATTRIBUTE_COLOR + ";" +
		    "  gl_PointSize = " + ShaderProgram.UNIFORM_POINT_SIZE + ";" +
		    "}";
	
	private static final String FRAGMENT_SHADER_CODE =    
			"precision mediump float;" +
			"varying vec4 v_color;" + 
		    "void main() {" +
		    "  gl_FragColor = v_color;" +
		    "}";
	
	private static final Matrix3 MATRIX = new Matrix3();
	private static final Vector2 VECTOR = new Vector2();
	private static final float[] POINTS = new float[2];
	
	private ShapeType mShapeType;
	
	/** 배치 렌더링할 정점의 배열 */
	private float[] mVertices;
	/** 배치 렌더링할 정점의 최대 인덱스 */
	private int mVertexIndex;
	
	private Mesh mMesh;
	
	private float mPointSize = 1f;
	
	private ShaderProgram mProgram;
	
	private Matrix3[] mTransformMatrices = new Matrix3[] {new Matrix3(), new Matrix3(), new Matrix3()};
	private Stack<Matrix3> mTransformMatrixStack = new Stack<Matrix3>();
	private Matrix4 mProjectionMatrix = new Matrix4();
	
	private Color4 mColor = new Color4();
	
	/** {@link #begin()}과 {@link #end()}의 쌍을 맞추기 위해 사용 */
	private boolean mDrawing;
	
	private int mMaxVertices;
	/** 한 프레임에서 렌더링할 vertex 개수 */
	private int mVertexCount;
	
	private boolean mBlendEnabled;
	private int mBlendSrcFactor = GLES20.GL_SRC_ALPHA;
	private int mBlendDstFactor = GLES20.GL_ONE_MINUS_SRC_ALPHA;
	
	private boolean mAuto;
	
	public ShapeRenderer() {
		this(DEFAULT_MAX_VERTICES, false);
	}
	
	public ShapeRenderer(int maxVertices) {
		this(maxVertices, false);
	}
	
	/** 
	 * auto가 true일 경우 버퍼가 가득 찰 경우 자동으로 1.5배 늘어나도록 하여 flush()를 
	 * 피하도록 한다
	 */
	public ShapeRenderer(int maxVertices, boolean auto) {
		build(maxVertices);
		mProgram = new ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
		mAuto = auto;
	}
	
	private void build(int maxVertices) {
		mMaxVertices = maxVertices;
		
		int numPostionComponents = 2;
		int numColorComponents = 4;
		int numSingleVertexComponents = numPostionComponents + numColorComponents;

		mMesh = new Mesh(
				true, 
				false, 
				maxVertices * numSingleVertexComponents, 
				0, 
				new VertexAttribute(ShaderProgram.ATTRIBUTE_POSITION, numPostionComponents),
				new VertexAttribute(ShaderProgram.ATTRIBUTE_COLOR, numColorComponents));
		
		mVertices = new float[numSingleVertexComponents * maxVertices];
	}
	
	public void begin(ShapeType type) {
		if(mDrawing)
			throw new IllegalStateException("begin() must be ended with end() before another begin()");
		
		mDrawing = true;
		mShapeType = type;
		mTransformMatrixStack.clear();
	}
	
	public void end() {
		if(!mDrawing) 
			throw new IllegalStateException("begin() must be called before end()");
		
		flush();
		mDrawing = false;
	}
	
	public void flush() {
		if(mVertexCount == 0) return;
		
		beginBlend();
		mProgram.begin();
		
		setUniform();
		mMesh.begin(mProgram);
		mMesh.setVertices(mVertices, 0, mVertexIndex);
		mMesh.render(mShapeType.getGLConstant());
		mMesh.end(mProgram);
		
		mProgram.end();
		endBlend();
		
		Core.GRAPHICS.incrementDrawCount();
		
		reset();
	}

	private void setUniform() {
		int location;
		
		location = mProgram.getLocationByName(ShaderProgram.UNIFORM_PROJECTION_MATRIX);
		if(location > -1) {
			GLES20.glUniformMatrix4fv(
					location, 
	        		1, 
	        		false, 
	        		mProjectionMatrix.value, 
	        		0);
		}
		
		// ShapeType이 POINT일 경우에만 사이즈를 지정한다.
		if(mShapeType == ShapeType.POINT) {
			location = mProgram.getLocationByName(ShaderProgram.UNIFORM_POINT_SIZE);
			if(location > -1) GLES20.glUniform1f(location, mPointSize);
		}
	}
	
	private void beginBlend() {
		if(mBlendEnabled)	{
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(mBlendSrcFactor, mBlendDstFactor);
		} else
			GLES20.glDisable(GLES20.GL_BLEND);
	}
	
	private void endBlend() {
		if(mBlendEnabled) GLES20.glDisable(GLES20.GL_BLEND);
	}
	
	private void reset() {
		mVertexIndex = 0;
		mVertexCount = 0;
	}
	
	public Color4 getColor() {
		return mColor;
	}
	
	/** draw... 메서드에서 사용될 디폴트 색을 지정한다. */
	public void setColor(Color4 color) {
		mColor.set(color);
	}

	public void setColor(float a, float r, float g, float b) {
		mColor.set(a, r, g, b);
	}
	
	public void setColor(int a, int r, int g, int b) {
		mColor.set(a, r, g, b);
	}
	
	public void setColor(int color) {
		mColor.set(color);
	}
	
	/** 한 정점의 위치를 직접 지정한다. */
	private void vertex(float x, float y) {
		final float[] vertices = mVertices;
		int oldVertexIndex = mVertexIndex;
		int vertexIndex = oldVertexIndex;
		vertices[vertexIndex++] = x;
		vertices[vertexIndex++] = y;
		mVertexIndex = vertexIndex;
		
		if(!mTransformMatrixStack.isEmpty())
			mTransformMatrixStack.peek().mapPoints(vertices, oldVertexIndex, vertices, oldVertexIndex, 1/*pair(s)*/);
	}
	
	private void color(Color4 color) {
		color(color.a, color.r, color.g, color.b);
	}
	
	/** 한 정점의 색을 직접 지정한다. */
	private void color(float a, float r, float g, float b) {
		final float[] vertices = mVertices;
		int vertexIndex = mVertexIndex;
		vertices[vertexIndex++] = r;
		vertices[vertexIndex++] = g;
		vertices[vertexIndex++] = b;
		vertices[vertexIndex++] = a;
		mVertexIndex = vertexIndex;
	}
	
	public void drawPoint(float x, float y) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.POINT)
			throw new IllegalStateException("ShapeType allowed for drawPoint : POINT");
		
		checkVertexCount(1);
		
		vertex(x, y);
		color(mColor);
	}

	public void drawLine(float x1, float y1, float x2, float y2) {
		drawLine(x1, y1, x2, y2, mColor, mColor);
	}
	
	public void drawLine(float x1, float y1, float x2, float y2, Color4 color1, Color4 color2) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE)
			throw new IllegalStateException("ShapeType allowed for drawLine : LINE");
		
		checkVertexCount(2);
		
		vertex(x1, y1);
		color(color1);
		vertex(x2, y2);
		color(color2);
	}
	
	/**
	 * 
	 * 
	 * @see http://www.antigrain.com/research/bezier_interpolation/index.html#PAGE_BEZIER_INTERPOLATION
	 */
	public void drawCurve(float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2, int segments) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE)
			throw new IllegalStateException("ShapeType allowed for drawCurve : LINE");
		
		checkVertexCount(segments * 2 + 2);
		
		float subdiv_step = 1f / segments;
		float subdiv_step2 = subdiv_step * subdiv_step;
		float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;

		float pre1 = 3 * subdiv_step;
		float pre2 = 3 * subdiv_step2;
		float pre4 = 6 * subdiv_step2;
		float pre5 = 6 * subdiv_step3;

		float tmp1x = x1 - cx1 * 2 + cx2;
		float tmp1y = y1 - cy1 * 2 + cy2;

		float tmp2x = (cx1 - cx2) * 3 - x1 + x2;
		float tmp2y = (cy1 - cy2) * 3 - y1 + y2;

		float fx = x1;
		float fy = y1;

		float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
		float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;

		float ddfx = tmp1x * pre4 + tmp2x * pre5;
		float ddfy = tmp1y * pre4 + tmp2y * pre5;

		float dddfx = tmp2x * pre5;
		float dddfy = tmp2y * pre5;
		
		Color4 color = mColor;

		while (segments-- > 0) {
			color(color);
			vertex(fx, fy);
			fx += dfx;
			fy += dfy;
			dfx += ddfx;
			dfy += ddfy;
			ddfx += dddfx;
			ddfy += dddfy;
			color(color);
			vertex(fx, fy);
		}
		color(color);
		vertex(fx, fy);
		color(color);
		vertex(x2, y2);
	}
	
	public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		drawTriangle(x1, y1, x2, y2, x3, y3, mColor, mColor, mColor);
	}
	
	@SuppressWarnings("incomplete-switch")
	public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, 
			Color4 color1, Color4 color2, Color4 color3) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawTriangle : LINE, FILLED");
		
		switch(mShapeType) {
			case LINE:
				checkVertexCount(6);
				
				vertex(x1, y1);
				color(color1);
				vertex(x2, y2);
				color(color2);
				
				vertex(x2, y2);
				color(color2);
				vertex(x3, y3);
				color(color3);
				
				vertex(x3, y3);
				color(color3);
				vertex(x1, y1);
				color(color1);
				break;
			case FILLED:
				checkVertexCount(3);
				
				vertex(x1, y1);
				color(color1);
				vertex(x2, y2);
				color(color2);
				vertex(x3, y3);
				color(color3);
				break;
		}
	}
	
	public void drawRect(float x, float y, float width, float height) {
		drawRect(x, y, width, height, mColor, mColor, mColor, mColor);
	}
	
	@SuppressWarnings("incomplete-switch")
	public void drawRect(float x, float y, float width, float height, 
			Color4 color1, Color4 color2, Color4 color3, Color4 color4) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawRect : LINE, FILLED");
		
		float x1 = x;
		float y1 = y;
		
		float x2 = x;
		float y2 = y + height;
		
		float x3 = x + width;
		float y3 = y + height;
		
		float x4 = x + width;
		float y4 = y;
		
		switch(mShapeType) {
			case LINE:
				checkVertexCount(8);
				
				vertex(x1, y1);
				color(color1);
				vertex(x2, y2);
				color(color2);
				
				vertex(x2, y2);
				color(color2);
				vertex(x3, y3);
				color(color3);
				
				vertex(x3, y3);
				color(color3);
				vertex(x4, y4);
				color(color4);
				
				vertex(x4, y4);
				color(color4);
				vertex(x1, y1);
				color(color1);
				break;
			case FILLED:
				checkVertexCount(6);
				
				vertex(x1, y1);
				color(color1);
				vertex(x2, y2);
				color(color2);
				vertex(x3, y3);
				color(color3);
				
				vertex(x1, y1);
				color(color1);
				vertex(x3, y3);
				color(color3);
				vertex(x4, y4);
				color(color4);
				break;
		}
	}
	
	public void drawRoundRect(float x, float y, float width, float height, float rx, float ry) {
		drawRoundRect(x, y, width, height, rx, ry, (int) (width + height)/2);
	}
	
	@SuppressWarnings("incomplete-switch")
	public void drawRoundRect(float x, float y, float width, float height, float rx, float ry, int segments) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawRoundRect : LINE, FILLED");
		
		int cornerSegments = segments / 4;
		
		float diameterX = rx * 2;
		float diameterY = ry * 2;
		
		float centerWidth = width - diameterX;
		float centerHeight = height - diameterY;
		
		float centerX = x + rx;
		float centerY = y + ry;
		
		float rightX = centerX + centerWidth;
		float bottomY = centerY + centerHeight;
		
		switch(mShapeType) {
			case LINE:
				
				// left
				drawLine(x, centerY, x, bottomY);
				
				// top
				drawLine(centerX, y, rightX, y);
				
				// right
				drawLine(x + width, centerY, x + width, bottomY);
				
				// bottom
				drawLine(centerX, y + height, rightX, y + height);
				
				// top-left
				drawArc(x, y, diameterX, diameterY, 180f, 90f, false, cornerSegments);
				
				// top-right
				drawArc(rightX - rx, y, diameterX, diameterY, 270f, 90f, false, cornerSegments);
				
				// bottom-left
				drawArc(x, y + centerHeight, diameterX, diameterY, 90f, 90f, false, cornerSegments);
				
				// bottom-right
				drawArc(rightX - rx, y + centerHeight, diameterX, diameterY, 0f, 90f, false, cornerSegments);
				
				break;
			case FILLED:
				
				// left
				drawRect(x, centerY, rx, centerHeight);
				
				// center
				drawRect(centerX, y, centerWidth, height);
				
				// right
				drawRect(rightX, centerY, rx, centerHeight);
				
				// top-left
				drawArc(x, y, diameterX, diameterY, 180f, 90f, true, cornerSegments);
				
				// top-right
				drawArc(rightX - rx, y, diameterX, diameterY, 270f, 90f, true, cornerSegments);
				
				// bottom-left
				drawArc(x, y + centerHeight, diameterX, diameterY, 90f, 90f, true, cornerSegments);
				
				// bottom-right
				drawArc(rightX - rx, y + centerHeight, diameterX, diameterY, 0f, 90f, true, cornerSegments);
				
				break;
		}
	}
	
	/** 
	 * {@link #drawLine()}의 경우 하드웨어에 따라 가능한 라인의 너비가 다른데, {@link #drawRectLine()}을 
	 * 사용하면 하드웨어와 상관없이 원하는 너비로 라인을 렌더링할 수 있다.
	 */
	@SuppressWarnings("incomplete-switch")
	public void drawRectLine(float x1, float y1, float x2, float y2, float width) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawRectLine : LINE, FILLED");
		
		Color4 color = mColor;
		
		// n(x, y)이 두 점 p1(x1, y1), p2(x2, y2)를 방향으로 하는 단위벡터이고 v(x', y')는 
		// 점 p1을 기준으로 n과 90도를 이루며 라인의 너비만큼의 길이를 가진 벡터라고 할 때,
		// 1. n내적 v는 0이다. 
		// 2. v의 길이는 w/2이다.
		// 의 두 방정식을 풀면 x'와 y'를 알아낼 수 있고 이것을 두 점에 적용하면 라인을 이루는 
		// 네 점을 알아낼 수 있다.
		Vector2 vector = VECTOR.set(x2 - x1, y2 - y1).nor();
		width /= 2f;
		float dx = vector.y * width;
		float dy = vector.x * width;
		
		switch(mShapeType) {
			case LINE:
				checkVertexCount(8);
				
				vertex(x1 - dx, y1 + dy);
				color(color);
				vertex(x1 + dx, y1 - dy);
				color(color);
				
				vertex(x1 + dx, y1 - dy);
				color(color);
				vertex(x2 + dx, y2 - dy);
				color(color);
				
				vertex(x2 + dx, y2 - dy);
				color(color);
				vertex(x2 - dx, y2 + dy);
				color(color);
				
				vertex(x2 - dx, y2 + dy);
				color(color);
				vertex(x1 - dx, y1 + dy);
				color(color);
				break;
			case FILLED:
				checkVertexCount(6);
				
				vertex(x1 - dx, y1 + dy);
				color(color);
				vertex(x1 + dx, y1 - dy);
				color(color);
				vertex(x2 + dx, y2 - dy);
				color(color);
				
				vertex(x1 - dx, y1 + dy);
				color(color);
				vertex(x2 + dx, y2 - dy);
				color(color);
				vertex(x2 - dx, y2 + dy);
				color(color);
				break;
		}
		
	}
	
	public void drawArc(float x, float y, float radius, float startAngle, float sweepAngle) {
		drawArc(x, y, radius, startAngle, sweepAngle, true, (int) Math.max((radius * Math.abs(sweepAngle)/360.0f), 1f));
	}
	
	/** 
	 * start 각도에서 angle이 양이면 시계방향, 음이면 반시계방향으로 호를 그린다. ShapeType이 FILLED인 경우
	 * useCenter은 무시된다. 
	 */
	public void drawArc(float x, float y, float radius, float startAngle, float sweepAngle, boolean useCenter) {
		drawArc(x, y, radius, startAngle, sweepAngle, useCenter, (int) Math.max((radius * Math.abs(sweepAngle)/360.0f), 1f));
	}
	
	@SuppressWarnings("incomplete-switch")
	public void drawArc(float x, float y, float radius, float startAngle, float sweepAngle, boolean useCenter, int segments) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawArc : LINE, FILLED");
		if(segments < 1) 
			throw new IllegalArgumentException("segments can't be less than 1");
		
		Color4 color = mColor;
		
		float theta = sweepAngle / segments;
		
		Matrix3 matrix = MATRIX;
		matrix.setRotate(startAngle);
		
		final float[] points = POINTS;
		points[0] = radius;
		points[1] = 0;
		
		matrix.mapPoints(points, 0, points, 0, 1/*pair(s)*/);
		
		matrix.setRotate(theta);
		
		switch(mShapeType) {
			case LINE:
				if(useCenter) {
					checkVertexCount(segments * 2 + 4);
					
					vertex(x, y);
					color(color);
					vertex(x + points[0], y + points[1]);
					color(color);
					
					for(int i=0; i<segments; i++) {
						vertex(x + points[0], y + points[1]);
						color(color);
						
						matrix.mapPoints(points, 0, points, 0, 1/*pair(s)*/);
						vertex(x + points[0], y + points[1]);
						color(color);
					}
					
					vertex(x + points[0], y + points[1]);
					color(color);
					vertex(x, y);
					color(color);
					
				} else {
					checkVertexCount(segments * 2);
					
					for(int i=0; i<segments; i++) {
						vertex(x + points[0], y + points[1]);
						color(color);
						
						matrix.mapPoints(points, 0, points, 0, 1/*pair(s)*/);
						vertex(x + points[0], y + points[1]);
						color(color);
					}
				}
				break;
			case FILLED:
				checkVertexCount(segments * 3);
	
				for(int i=0; i<segments; i++) {
					vertex(x, y);
					color(color);
					
					vertex(x + points[0], y + points[1]);
					color(color);
					
					matrix.mapPoints(points, 0, points, 0, 1/*pair(s)*/);
					vertex(x + points[0], y + points[1]);
					color(color);
				}
				break;
		}
	}
	
	public void drawArc(float x, float y, float width, float height, float startAngle, float sweepAngle) {
		drawArc(x, y, width, height, startAngle, sweepAngle, true, (int) Math.max(((width+height)/2 * Math.abs(sweepAngle)/360.0f), 1f));
	}
	
	public void drawArc(float x, float y, float width, float height, float startAngle, float sweepAngle, 
			boolean useCenter) {
		drawArc(x, y, width, height, startAngle, sweepAngle, useCenter, (int) Math.max(((width+height)/2 * Math.abs(sweepAngle)/360.0f), 1f));
	}
	
	@SuppressWarnings("incomplete-switch")
	public void drawArc(float x, float y, float width, float height, float startAngle, float sweepAngle, 
			boolean useCenter, int segments) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawArc : LINE, FILLED");
		if(segments < 1) 
			throw new IllegalArgumentException("segments can't be less than 1");
		
		Color4 color = mColor;
		
		float theta = (float) (Math.toRadians(sweepAngle) / segments);
		float angle = (float) Math.toRadians(startAngle);
		
		float a = width / 2;
		float b = height / 2;
		
		float cx = x + width/2;
		float cy = y + height/2;
		
		float dx = a * (float) Math.cos(angle);
		float dy = b * (float) Math.sin(angle);
		
		switch(mShapeType) {
			case LINE:
				if(useCenter) {
					checkVertexCount(segments * 2 + 4);
					
					vertex(cx, cy);
					color(color);
					vertex(cx + dx, cy + dy);
					color(color);
					
					for(int i=0; i<segments; i++) {
						vertex(cx + dx, cy + dy);
						color(color);
						
						angle += theta;
						dx = a * (float) Math.cos(angle);
						dy = b * (float) Math.sin(angle);
						
						vertex(cx + dx, cy + dy);
						color(color);
					}
					
					vertex(cx + dx, cy + dy);
					color(color);
					vertex(cx, cy);
					color(color);
					
				} else {
					checkVertexCount(segments * 2);
					
					for(int i=0; i<segments; i++) {
						vertex(cx + dx, cy + dy);
						color(color);
						
						angle += theta;
						dx = a * (float) Math.cos(angle);
						dy = b * (float) Math.sin(angle);
						
						vertex(cx + dx, cy + dy);
						color(color);
					}
				}
				break;
			case FILLED:
				checkVertexCount(segments * 3);
				
				for(int i=0; i<segments; i++) {
					vertex(cx, cy);
					color(color);
					
					vertex(cx + dx, cy + dy);
					color(color);
					
					angle += theta;
					dx = a * (float) Math.cos(angle);
					dy = b * (float) Math.sin(angle);
	
					vertex(cx + dx, cy + dy);
					color(color);
				}
				break;
		}
	}
	
	/** 원을 그린다. radius에 비례해서 segments를 자동으로 설정한다. */
	public void drawCircle(float x, float y, float radius) {
		drawCircle(x, y, radius, (int) Math.max(radius, 1f));
	}
	
	/** 원을 그린다. segments가 적으면 원보다는 다각형에 가깝게 그려진다. */
	@SuppressWarnings("incomplete-switch")
	public void drawCircle(float x, float y, float radius, int segments) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawCircle : LINE, FILLED");
		if(segments < 1) 
			throw new IllegalArgumentException("segments can't be less than 1");
		
		Color4 color = mColor;
		
		float theta = 360.0f / segments;
		
		Matrix3 matrix = MATRIX;
		matrix.setRotate(theta);
		
		final float[] points = POINTS;
		points[0] = radius;
		points[1] = 0;
		
		switch(mShapeType) {
			case LINE:
				checkVertexCount(segments * 2);
	
				for(int i=0; i<segments; i++) {
					vertex(x + points[0], y + points[1]);
					color(color);
					
					matrix.mapPoints(points, 0, points, 0, 1/*pair(s)*/);
					vertex(x + points[0], y + points[1]);
					color(color);
				}
				break;
			case FILLED:
				checkVertexCount(segments * 3);
	
				for(int i=0; i<segments; i++) {
					vertex(x, y);
					color(color);
					
					vertex(x + points[0], y + points[1]);
					color(color);
					
					matrix.mapPoints(points, 0, points, 0, 1/*pair(s)*/);
					vertex(x + points[0], y + points[1]);
					color(color);
				}
				break;
		}
	}
	
	public void drawEllipse(float x, float y, float width, float height) {
		drawEllipse(x, y, width, height, (int) Math.max((width + height)/2, 1f));
	}
	
	@SuppressWarnings("incomplete-switch")
	public void drawEllipse(float x, float y, float width, float height, int segments) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE && mShapeType != ShapeType.FILLED)
			throw new IllegalStateException("ShapeType allowed for drawEllipse : LINE, FILLED");
		if(segments < 1) 
			throw new IllegalArgumentException("segments can't be less than 1");
		
		Color4 color = mColor;
		
		float theta = (float) (Math.toRadians(360f) / segments);
		float angle = 0;
		
		float a = width / 2;
		float b = height / 2;
		
		float cx = x + width/2;
		float cy = y + height/2;
		
		float dx = a;
		float dy = 0f;
		
		switch(mShapeType) {
			case LINE:
				checkVertexCount(segments * 2);

				for(int i=0; i<segments; i++) {
					vertex(cx + dx, cy + dy);
					color(color);
					
					angle += theta;
					dx = a * (float) Math.cos(angle);
					dy = b * (float) Math.sin(angle);
					
					vertex(cx + dx, cy + dy);
					color(color);
				}
				break;
			case FILLED:
				checkVertexCount(segments * 3);
	
				for(int i=0; i<segments; i++) {
					vertex(cx, cy);
					color(color);
					
					vertex(cx + dx, cy + dy);
					color(color);
					
					angle += theta;
					dx = a * (float) Math.cos(angle);
					dy = b * (float) Math.sin(angle);

					vertex(cx + dx, cy + dy);
					color(color);
				}
				break;
		}
	}
	
	public void drawPolyline(float[] vertices) {
		drawPolyline(vertices, 0, vertices.length);
	}
	
	public void drawPolyline(float[] vertices, int offset, int count) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before draw...");
		if(mShapeType != ShapeType.LINE)
			throw new IllegalStateException("ShapeType allowed for drawPolyline : LINE");
		if(count%2 != 0)
			throw new IllegalArgumentException("count must be an even number");
		if(count < 4)
			throw new IllegalArgumentException("count can't be less than 4");
		
		Color4 color = mColor;
		
		checkVertexCount(count-2);
		
		int n = offset + (count-2);
		for(int i=offset; i<n; i+=2) {
			vertex(vertices[i], vertices[i+1]);
			color(color);
			vertex(vertices[i+2], vertices[i+3]);
			color(color);
		}
	}
	
	private void checkVertexCount(int count) {
		mVertexCount += count;
		if(mMaxVertices < mVertexCount) {
			
			// 자동으로 버퍼를 늘린다.
			if(mAuto) {
				float[] verices = mVertices;
				// 새로운 버퍼의 크기는 1.5배로 한다.
				build((int) (mMaxVertices * 1.5f));
				System.arraycopy(verices, 0, mVertices, 0, mVertexIndex);
				
			// 지금까지의 버퍼의 내용을 출력한다.
			} else {
				flush();
				mVertexCount = count;
			}
		}
	}
	
	/** 기기에서 사용 가능한 최대 point 사이즈를 얻는다. */
	public int getSupportedMaxPointSize() {
		final int[] params = TMP_PARAMS;
		GLES20.glGetIntegerv(GLES20.GL_ALIASED_POINT_SIZE_RANGE, params, 0);
		return params[1];
	}
	
	public float getPointSize() {
		return mPointSize;
	}
	
	public void setPointSize(float size) {
		mPointSize = size;
	}
	
	public float getSupportedMaxLineWidth() {
		final int[] params = TMP_PARAMS;
		GLES20.glGetIntegerv(GLES20.GL_ALIASED_LINE_WIDTH_RANGE, params, 0);
		return params[1];
	}
	
	public int getLineWidth() {
		final int[] params = TMP_PARAMS;
		GLES20.glGetIntegerv(GLES20.GL_LINE_WIDTH , params, 0);
		return params[0];
	}
	
	public void setLineWidth(float width) {
		float w = MathUtils.clamp(width, 1f, getSupportedMaxLineWidth());
		GLES20.glLineWidth(w);
	}
	
	private boolean prepareStack() {
		if(mTransformMatrixStack.isEmpty()) {
			mTransformMatrices[0].reset();
			mTransformMatrixStack.push(mTransformMatrices[0]);
			return true;
		}
		return false;
	}	
	
	public void translate(float dx, float dy) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		prepareStack();
		mTransformMatrixStack.peek().preTranslate(dx, dy);
	}
	
	public void rotate(float angle) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		prepareStack();
		mTransformMatrixStack.peek().preRotate(angle);
	}
	
	public void rotate(float angle, float px, float py) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		prepareStack();
		mTransformMatrixStack.peek().preRotate(angle, px, py);
	}

	public void scale(float sx, float sy) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		prepareStack();
		mTransformMatrixStack.peek().preScale(sx, sy);
	}
	
	public void scale(float sx, float sy, float px, float py) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		prepareStack();
		mTransformMatrixStack.peek().preScale(sx, sy, px, py);
	}
	
	public void concatMatrix(Matrix3 matrix) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		prepareStack();
		mTransformMatrixStack.peek().preConcat(matrix);
	}
	
	public void setTransformMatrix(Matrix3 matrix) {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		prepareStack();
		mTransformMatrixStack.peek().set(matrix);
	}
	
	public void pushTransformMatrix() {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		if(prepareStack()) return;
		
		if(mTransformMatrixStack.size() > 2) {
			mTransformMatrixStack.push(new Matrix3(mTransformMatrixStack.peek()));
		} else {
			int index = mTransformMatrixStack.size();
			mTransformMatrices[index].set(mTransformMatrixStack.peek());
			mTransformMatrixStack.push(mTransformMatrices[index]);
		}
	}
	
	public Matrix3 peekTransformMatrix() {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		if(mTransformMatrixStack.isEmpty()) return null;
		return mTransformMatrixStack.peek();
	}

	public Matrix3 popTransformMatrix() {
		if(!mDrawing) throw new IllegalStateException("begin() must be called before transformMatrix operations");
		return mTransformMatrixStack.pop();
	}
	
	public Matrix4 getProjectionMatrix() {
		return mProjectionMatrix;
	}

	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		if(mDrawing) flush();
		mProjectionMatrix.set(projectionMatrix);
	}

	public ShapeType getShapeType() {
		return mShapeType;
	}
	
	public boolean isBlendEnabled() {
		return mBlendEnabled;
	}

	public void setBlendEnabled(boolean blendEnabled) {
		if(mBlendEnabled == blendEnabled) return;
		if(mDrawing) flush();
		mBlendEnabled = blendEnabled;
	}
	
	public int getBlendSrcFactor() {
		return mBlendSrcFactor;
	}

	public int getBlendDstFactor() {
		return mBlendDstFactor;
	}

	public void setBlendFunc(int srcFactor, int dstFactor) {
		if(mBlendSrcFactor == srcFactor && mBlendDstFactor == dstFactor) return;
		if(mDrawing) flush();
		mBlendSrcFactor = srcFactor;
		mBlendDstFactor = dstFactor;
	}
	
	public int getMaxVertices() {
		return mMaxVertices;
	}

	public int getVertexCount() {
		return mVertexCount;
	}

	@Override
	public void dispose() {
		mMesh.dispose();
		mProgram.dispose();
	}

}
