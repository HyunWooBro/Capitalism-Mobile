package core.framework.graphics.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.opengl.GLES20;

import core.framework.Core;
import core.utils.Disposable;

/**
 * 
 * 
 * @see http://developer.android.com/training/graphics/opengl/draw.html
 * @author 김현우
 */
public class ShaderProgram implements GLReloadable, Disposable {
	
	public static final String ATTRIBUTE_POSITION = "a_position";
	public static final String ATTRIBUTE_TEXCOORD = "a_texCoord";
	public static final String ATTRIBUTE_COLOR = "a_color";
	
	public static final String UNIFORM_PROJECTION_MATRIX = "u_projectionMatrix";
	public static final String UNIFORM_TEXTURE = "u_texture";
	public static final String UNIFORM_POINT_SIZE = "u_pointSize";
	
	private static final int[] TMP_PARAMS = new int[1];
	
	// Program variables
	private int mProgramID;
	
	private String mVertexSource;
	private String mFragmentSource;
	
	private List<String> mUniformNameList = new ArrayList<String>(6);
	private List<String> mAttributeNameList = new ArrayList<String>(6);
	private Map<String, Integer> mNameToLocationMap = new HashMap<String, Integer>();
	
	public ShaderProgram(String vertexSource, String fragmentSource) {
		mVertexSource = vertexSource;
		mFragmentSource = fragmentSource;
		createShaderProgram();
	    fetchUniforms();
	    fetchAttributes();
	    Core.GRAPHICS.addGLReloadable(this);
	}
	
	private void createShaderProgram() {
		int vertexShaderID = loadShader(GLES20.GL_VERTEX_SHADER, mVertexSource);
		int fragmentShaderID = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentSource);

		// create empty OpenGL ES Program
	    mProgramID = GLES20.glCreateProgram();
	    // add the vertex shader to program
	    GLES20.glAttachShader(mProgramID, vertexShaderID);
	    // add the fragment shader to program
	    GLES20.glAttachShader(mProgramID, fragmentShaderID);
	    // creates OpenGL ES program executables
	    GLES20.glLinkProgram(mProgramID);
	    
	    GLES20.glDeleteShader(vertexShaderID);
	    GLES20.glDeleteShader(fragmentShaderID);
	}

	private int loadShader(int type, String shaderSource){
	    // create a vertex shader clazz (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader clazz (GLES20.GL_FRAGMENT_SHADER)
	    int shaderID = GLES20.glCreateShader(type);

	    // add the source code to the shader and compile it
	    GLES20.glShaderSource(shaderID, shaderSource);
	    GLES20.glCompileShader(shaderID);

	    // return the shader
	    return shaderID;
	}
	
	private void fetchUniforms() {
		final int[] params = TMP_PARAMS;
		GLES20.glGetProgramiv(mProgramID, GLES20.GL_ACTIVE_UNIFORMS, params, 0);
		int uniformSize = params[0];
		
		for(int i=0; i<uniformSize; i++) {
			// 얻어오는 uniform은 실제로 사용되는 것만 해당된다.
			// 얻어오는 순서는 알파벳 순이다. 즉, 셰이더에 정의된 순서와는 상관없다.
			// 얻어오는 size는 정점에 들어가는 size와는 다르다.
			// 얻어오는 type은 glDraw... 메서드에 지정할 type과는 다르다.
			// 따라서, 이 값은 사용하지 않느다.
			String name = GLES20.glGetActiveUniform(mProgramID, i, params, 0, params, 0);
			int location = GLES20.glGetUniformLocation(mProgramID, name);
			mNameToLocationMap.put(name, location);
			mUniformNameList.add(name);
		}
	}
	
	private void fetchAttributes() {
		final int[] params = TMP_PARAMS;
		GLES20.glGetProgramiv(mProgramID, GLES20.GL_ACTIVE_ATTRIBUTES, params, 0);
		int attributeSize = params[0];
		
		for(int i=0; i<attributeSize; i++) {
			String name = GLES20.glGetActiveAttrib(mProgramID, i, params, 0, params, 0);
			int location = GLES20.glGetAttribLocation(mProgramID, name);
			mNameToLocationMap.put(name, location);
			mAttributeNameList.add(name);
		}
	}
	
	public void begin() {
		GLES20.glUseProgram(mProgramID);
	}
	
	public void end() {
		GLES20.glUseProgram(0);
	}

	public int getProgramID() {
		return mProgramID;
	}
	
	public List<String> getUniformNameList() {
		return mUniformNameList;
	}
	
	public List<String> getAttributeNameList() {
		return mAttributeNameList;
	}
	
	/** attribute나 uniform의 이름에 맵핑된 위치를 얻는다. 해당 이름이 존재하지 않으면 -1을 리턴한다.*/
	public int getLocationByName(String name) {
		Integer location = mNameToLocationMap.get(name);
		if(location == null) return -1;
		return location;
	}

	@Override
	public void reload() {
		if(GLES20.glIsProgram(mProgramID)) return;
		createShaderProgram();
	}

	@Override
	public void dispose() {
		end();
		GLES20.glDeleteProgram(mProgramID);
		Core.GRAPHICS.removeGLReloadable(this);
	}

}
