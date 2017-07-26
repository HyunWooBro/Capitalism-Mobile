/* Sprite Batcher V1.1
   Copyright (c) 2013 Tim Wicksteed <tim@twicecircled.com>
   http:/www.twicecircled.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.framework.openGL;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.framework.*;
import org.game.*;

import android.R.raw;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.*;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;
import android.util.SparseArray;

public class SpriteBatcher implements Renderer {

	// drawer.onDrawFrame(..) gets called by SpriteBatcher each frame
	private Drawer drawer;

	private int apkExpansionVersionMain = 1;
	private int apkExpansionVersionPatch = 1;

	private int width;
	private int height;

	private int maxFPS;
	private long lastTime;

	private int count;

	private Context context;
	
	
	private Bitmap textBitmap;
	private Canvas canvas;

	// A Texture object holds all the information to send a batch of sprites to
	SparseArray<Texture> texturesByResourceId;
	// ArrayList for consistent draw order
	ArrayList<Texture> drawOrder;

	// BitmapManager types
	private static final String DRAWABLE = "drawable";
	private static final String STRING = "string";

	// For debugging
	protected static final String TAG = "SpriteBatcher";

	/**
	 * Constructor.
	 * 
	 * @param resources
	 * @param resourceIds
	 *            valid resource ids are R.drawable.xxx for normal sprites or
	 *            R.String.xxx where the string resource contains a path to a
	 *            font.
	 * @param drawer
	 *            object implementing Drawer interface
	 */

	public SpriteBatcher(Context context, int[] resourceIds, Drawer drawer) {
		setUp(context, resourceIds, drawer);
	}

	/**
	 * Constructor if using expansion apks. Need to provide expansion apk
	 * version.
	 * 
	 * @param resources
	 * @param resourceIds
	 *            valid resource ids are R.drawable.xxx for normal sprites or
	 *            R.String.xxx where the string resource contains a path to a
	 *            font or to a drawable in an expansion file
	 * @param drawer
	 *            object implementing Drawer interface
	 */
	public SpriteBatcher(Context context, int[] resourceIds, Drawer drawer,
			int apkExpansionVersionMain, int apkExpansionVersionPatch) {
		this.apkExpansionVersionMain = apkExpansionVersionMain;
		this.apkExpansionVersionPatch = apkExpansionVersionPatch;
		setUp(context, resourceIds, drawer);
	}

	private void setUp(Context context, int[] resourceIds, Drawer drawer) {
		// Need a reference to resources to load textures later
		this.context = context;

		// Create texture objects for each resource id
		setUpTextureObjects(context, resourceIds);

		this.drawer = drawer;
		
		
		BitmapFactory.Options sOptions;

		sOptions = new BitmapFactory.Options();
		sOptions.inDensity=160;
		sOptions.inTargetDensity = 160;
		sOptions.inMutable = true;
		
		this.textBitmap = AppManager.getInstance().getBitmap(R.drawable.canvas, sOptions);
		
		canvas = new Canvas(textBitmap);
	}

	private void setUpTextureObjects(Context context, int[] resourceIds) {
		texturesByResourceId = new SparseArray<Texture>();
		drawOrder = new ArrayList<Texture>();
		// Loop through resource ids and generate texture objects
		String filePath;
		Texture texture;
		for (int i = 0; i < resourceIds.length; i++) {
			try {
				if (context.getResources().getResourceTypeName(resourceIds[i])
						.equals(DRAWABLE)) {
					texture = new BasicTexture(resourceIds[i]);
					texturesByResourceId.put(resourceIds[i], texture);
					drawOrder.add(texture);
				} else if (context.getResources()
						.getResourceTypeName(resourceIds[i]).equals(STRING)) {
					// Could be a font or file texture
					filePath = context.getResources().getString(resourceIds[i]);
					if (filePath.substring(0, 5).equals("fonts")) {
						// Try to get font file from path in string
						Typeface tf;
						try {
							tf = Typeface.createFromAsset(context
									.getResources().getAssets(), filePath);
							if (tf == null) {
								throw new Exception();
							}
						} catch (Exception e) {
							Log.e(TAG,
									"Error, could not create font from asset filePath: "
											+ filePath, e);
							// Skip on to next resource id
							continue;
						}
						// Store font texture
						texture = new FontTexture(tf);
						texturesByResourceId.put(resourceIds[i], texture);
						drawOrder.add(texture);
					} else {
						// It must be the path to an expansion file
						texture = new FileTexture(filePath,
								apkExpansionVersionMain,
								apkExpansionVersionPatch);
						texturesByResourceId.put(resourceIds[i], texture);
						drawOrder.add(texture);
					}
				} else {
					Log.w(TAG,
							"Warning: resourceIds["
									+ i
									+ "] resource type not recognised. Must be a drawable or string.");
				}
			} catch (Resources.NotFoundException e) {
				Log.e(TAG, "Error: resourceIds[" + i
						+ "] not found. Not a resource id.", e);
			}
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (maxFPS != 0) {
			long elapsed = System.currentTimeMillis() - lastTime;
			int minElapsed = 1000 / maxFPS;
			if (elapsed < minElapsed) {
				try {
					Thread.sleep(minElapsed - elapsed);
				} catch (InterruptedException e) {
					Log.e("SpriteBatcher",
							"Error sleeping thread, to cap FPS.", e);
				}
			}
			lastTime = System.currentTimeMillis();
		}

		AppManager.getInstance().getGameView().Update();

		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Rotate world by 180 around x axis so positive y is down (like canvas)
		gl.glRotatef(-180, 1, 0, 0);

		// START DRAWING
		count = 0;
		drawer.onDrawFrame(gl, this);
		
		// batchDraw를 하기 전에 text bitmap을 텍스쳐로 만들어 삽입
		addTextTexture(gl);

		// Finally, send off all the draw commands in batches
		batchDraw(gl);
		// Log.i(TAG, "Batch count = " + count);
		
		// text bitmap을 지우기
		emptyTextBackground();
	}

	public void setMaxFPS(int maxFPS) {
		this.maxFPS = maxFPS;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Stores width and height
		this.width = width;
		this.height = height;
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		// Orthographic mode for 2d
		//gl.glOrthof(0, width, -height, 0, -1, 8);
		gl.glOrthof(0, 640, -400, 0, -1, 8);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// SETTINGS
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// DRAWING SETUP
		// NOTES: As we are always drawing with textures and viewing our
		// elements from the same side all the time we can leave all these
		// settings on the whole time
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Telling OpenGL to enable textures.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// Tell OpenGL to enable the use of UV coordinates.
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		// Blending on
		gl.glEnable(GL10.GL_BLEND);
		// GL10.GL_SRC_ALPHA 에서 GL10.GL_ONE으로 변경
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// Get unique texture ids
		int[] textureIds = new int[texturesByResourceId.size()];
		gl.glGenTextures(textureIds.length, textureIds, 0);

		// Iterate over textures
		Texture currentTexture;
		for (int i = 0; i < texturesByResourceId.size(); i++) {
			currentTexture = texturesByResourceId.valueAt(i);
			// Assign texture id
			currentTexture.setTextureId(textureIds[i]);
			// Load bitmap into openGL
			if(texturesByResourceId.get(R.drawable.canvas) != currentTexture)
				addTexture(gl, context, currentTexture, textureIds[i]);
		}
	}

	/**
	 * Prematurely batch off all 'draws' made so far. This can be useful for
	 * layering your sprites as all draws made so far will be below any
	 * subsequent draws. NB Prematurely batching off your sprites is less
	 * efficient and should be avoided where possible, by default sprites are
	 * drawn in the order that you pass in the drawable resource ids in
	 * SpriteBatcher's constructor.
	 * 
	 * @param gl
	 */
	public void batchDraw(GL10 gl) {
		// All the draw commands are already batched together for each seperate
		// texture tile. Now we loop through each tile and make the draw calls
		// to OpenGL.
		// NOTE: You can call this method early to send a batch. This gives you
		// more control over layer order of the sprites.
		Texture currentTexture;
		for (Iterator<Texture> i = drawOrder.iterator(); i.hasNext();) {
			// GRAB TEXTURE
			currentTexture = i.next();
			SparseArray<SpriteData> array = currentTexture.getSpriteData();

			// NB Each texture can have multiple spritedatas to support
			// different gl.glColor4f(r, g, b, a) parameters. This allows alpha
			// and colour effects
			for (int j = 0; j < array.size(); j++) {
				// GRAB SPRITEDATA
				SpriteData currentSpriteData = array.valueAt(j);

				// CONVERT INTO ARRAY
				float[] vertices = currentSpriteData.getVertices();
				short[] indices = currentSpriteData.getIndices();
				float[] textureCoords = currentSpriteData.getTextureCoords();

				// ONLY DRAW IF ALL NOT NULL
				if (vertices != null && indices != null
						&& textureCoords != null) {

					// CREATE BUFFERS - these are just containers for sending
					// the
					// draw information we have already collected to OpenGL

					// Vertex buffer (position information of every draw
					// command)
					ByteBuffer vbb = ByteBuffer
							.allocateDirect(vertices.length * 4);
					vbb.order(ByteOrder.nativeOrder());
					FloatBuffer vertexBuffer = vbb.asFloatBuffer();
					vertexBuffer.put(vertices);
					vertexBuffer.position(0);

					// Index buffer (which vertices go together to make the
					// elements)
					ByteBuffer ibb = ByteBuffer
							.allocateDirect(indices.length * 2);
					ibb.order(ByteOrder.nativeOrder());
					ShortBuffer indexBuffer = ibb.asShortBuffer();
					indexBuffer.put(indices);
					indexBuffer.position(0);

					// How to paste the texture over each element so that the
					// right
					// image is shown
					ByteBuffer tbb = ByteBuffer
							.allocateDirect(textureCoords.length * 4);
					tbb.order(ByteOrder.nativeOrder());
					FloatBuffer textureBuffer = tbb.asFloatBuffer();
					textureBuffer.put(textureCoords);
					textureBuffer.position(0);

					// CONVERT RGBA TO SEPERATE VALUES
					int color = currentSpriteData.getARGB();
					float r = (float) Color.red(color) / 255;
					float g = (float) Color.green(color) / 255;
					float b = (float) Color.blue(color) / 255;
					float a = (float) Color.alpha(color) / 255;

					// DRAW COMMAND
					gl.glColor4f(r, g, b, a);
					// Tell OpenGL where our texture is located.
					gl.glBindTexture(GL10.GL_TEXTURE_2D,
							currentTexture.getTextureId());
					// Telling OpenGL where our textureCoords are.
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
					// Specifies the location and data format of the array of
					// vertex
					// coordinates to use when rendering.
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
					// Draw elements command using indices so it knows which
					// vertices go together to form each element
					gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
							GL10.GL_UNSIGNED_SHORT, indexBuffer);
					count++;

					// Clear spriteData
					currentSpriteData.clear();
				}
			}
		}
	}

	private void addTexture(GL10 gl, Context context, Texture texture,
			int textureId) {
		// Get bitmap
		Bitmap bitmap = texture.getBitmap(context);
		
		// Working with textureId
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// SETTINGS
		// Scale up if the texture is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		// Scale down if the mesh is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		// Clamp to edge behaviour at edge of texture (repeats last pixel)
		//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
		//		GL10.GL_REPEAT);
		//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
		//		GL10.GL_REPEAT);
		
		// 이미지 크기를 넘어가면 반복이 아니라 마지막 픽셀로 채우는 것으로 수정
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// Attach bitmap to current texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// Add dimensional info to spritedata
		texture.setDimensions(bitmap.getWidth(), bitmap.getHeight());
		
		bitmap.recycle();
	}
	
	// 추가
	private void addTextTexture(GL10 gl) {
		
		Texture currentTexture;
		currentTexture = texturesByResourceId.get(R.drawable.canvas);
		
		// Working with textureId
		gl.glBindTexture(GL10.GL_TEXTURE_2D, currentTexture.getTextureId());

		// SETTINGS
		// Scale up if the texture is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		// Scale down if the mesh is smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		// Clamp to edge behaviour at edge of texture (repeats last pixel)
		//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
		//		GL10.GL_REPEAT);
		//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
		//		GL10.GL_REPEAT);
		
		// 이미지 크기를 넘어가면 반복이 아니라 마지막 픽셀로 채우는 것으로 수정
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// Attach bitmap to current texture
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textBitmap, 0);

		// Add dimensional info to spritedata
		currentTexture.setDimensions(textBitmap.getWidth(), textBitmap.getHeight());
		
		// 항상 텍스트 텍스쳐를 그리도록 한다.
		drawBitmap(R.drawable.canvas, 0, 0, null);
		
		//bitmap2.recycle();
	}
	
	// 추가
	private void emptyTextBackground()
	{
		
		BitmapFactory.Options sOptions;

		sOptions = new BitmapFactory.Options();
		sOptions.inDensity=160;
		sOptions.inTargetDensity = 160;
		sOptions.inMutable = true;
		
		if(textBitmap != null)
			textBitmap.recycle();
		
		textBitmap = AppManager.getInstance().getBitmap(R.drawable.canvas, sOptions);
		
		canvas.setBitmap(textBitmap);
	}

	/**
	 * Returns the width of the drawing canvas in DIPS. Use this to scale your
	 * sprites if you wish them to take up a proportion of the GLSurfaceView.
	 * 
	 * @return
	 */
	public int getViewWidth() {
		return width;
	}

	/**
	 * Returns the height of the drawing canvas in DIPS. Use this to scale your
	 * sprites if you wish them to take up a proportion of the GLSurfaceView.
	 * 
	 * @return
	 */
	public int getViewHeight() {
		return height;
	}

	/**
	 * Modify the default font params. Note this method should be called prior
	 * to calling setRenderer(SpriteBatcher) on you GLSurfaceView otherwise the
	 * new params may not be realised.
	 * 
	 * @param params
	 */
	public void setFontParams(int resourceId, FontParams params) {
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null && texture.getClass() == FontTexture.class) {
			FontTexture fontTexture = (FontTexture) texture;
			fontTexture.setParams(params);
		} else {
			Log.w(TAG,
					"Warning: Could not recognise resource id in FontParams. Has it been passed into the the constructor?");
		}
	}

	// ----------- DRAW METHODS --------------------

	// SIMPLE
	public void draw(int resourceId, Rect src, Rect dst) {
		// Simple src->dst draws
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.addSprite(src, dst);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}
	
	// 추가
	// canvas.drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint)과 유사하게 만듬
	public void drawBitmap(int resourceId, Rect src, Rect dst, GLPaint _GLPaint) {
		Texture texture = texturesByResourceId.get(resourceId);
		
		int argb = 0xffffffff;
		
		if(_GLPaint != null)
		{
			argb = (_GLPaint.getAlpha() << 24) + (_GLPaint.getAlpha() << 16) + (_GLPaint.getAlpha() << 8) 
					+ (_GLPaint.getAlpha());
		}
		
		if (texture != null) {
			texture.addSprite(src, dst, 0, argb);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}
	
	// 추가
	// canvas.drawBitmap(Bitmap bitmap, float left, float top, Paint paint)과 유사하게 만듬
	public void drawBitmap(int resourceId, int drawX, int drawY, GLPaint _GLPaint) {
		Texture texture = texturesByResourceId.get(resourceId);
		
		int argb = 0xffffffff;
		
		if(_GLPaint != null)
		{
			argb = (_GLPaint.getAlpha() << 24) + (_GLPaint.getAlpha() << 16) + (_GLPaint.getAlpha() << 8) 
					+ (_GLPaint.getAlpha());
		}
		
		if (texture != null) {
			texture.addSprite(
					new Rect(0, 0, texture.getWidth(), texture.getHeight()), 
					new Rect(drawX, drawY, drawX + texture.getWidth(), drawY + texture.getHeight()), 
					0, argb);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}
	
	// 추가
	// canvas.drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint)의 랩퍼 메서드
	public void drawBitmap2(int resourceId, Rect src, Rect dst, Paint p) {
		//Canvas canvas = new Canvas(textBitmap);

		canvas.drawBitmap(BitmapManager.GetInstance().getBitmapByResourceId(resourceId), src, dst, p);
	}
	
	// 추가
	// canvas.drawBitmap(Bitmap bitmap, float left, float top, Paint paint)의 랩퍼 메서드
	public void drawBitmap2(int resourceId, int drawX, int drawY, Paint p) {
		//Canvas canvas = new Canvas(textBitmap);
		
		//canvas.clipRect(0, 0, 640, 300);

		canvas.drawBitmap(BitmapManager.GetInstance().getBitmapByResourceId(resourceId), drawX, drawY, p);
	}

	public void draw(int resourceId, Rect src, Rect dst, int angle) {
		// src->dst draws with rotation about src centre
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.addSprite(src, dst, angle);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}

	public void draw(int resourceId, Rect src, Rect dst, int angle, int argb) {
		// src->dst draws with rotation about src centre
		// With colour alteration
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.addSprite(src, dst, angle, argb);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}

	// COMPLICATED
	public void draw(int resourceId, Rect src, int drawX, int drawY,
			Rect hotRect, int angle, float scale) {
		// Just redirects to below with equal scale in x and y
		draw(resourceId, src, drawX, drawY, hotRect, angle, scale, scale);
	}

	public void draw(int resourceId, Rect src, int drawX, int drawY,
			Rect hotRect, int angle, float sizeX, float sizeY) {
		// This method allows rotations but needs additional input
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.addSprite(src, drawX, drawY, hotRect, angle, sizeX, sizeY);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}

	public void draw(int resourceId, Rect src, int drawX, int drawY,
			Rect hotRect, int angle, float sizeX, float sizeY, int argb) {
		// This method allows rotations but needs additional input
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.addSprite(src, drawX, drawY, hotRect, angle, sizeX, sizeY,
					argb);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}

	/**
	 * Draw opaque white text.
	 * 
	 * @param gl
	 * @param resourceId
	 *            Id of the string resource that contains the path of your font.
	 *            Should be the same as passed into SpriteBatcher's constructor.
	 * @param text
	 *            text to draw
	 * @param x
	 *            position of text, x (centre)
	 * @param y
	 *            position of text, y (centre)
	 * @param scale
	 *            change size of text using post scaling. For the best quality
	 *            leave as 1 and change the native size when creating each
	 *            FontData object.
	 */
	public void drawText(int resourceId, String text, int x, int y, float scale) {
		// Pass on with default argb value
		drawText(resourceId, text, x, y, scale, Texture.DEFAULT_ARGB);
	}

	/**
	 * Draw text with a non-default ARGB values.
	 * 
	 * @param gl
	 * @param resourceId
	 *            Id of the string resource that contains the path of your font.
	 *            Should be the same as passed into SpriteBatcher's constructor.
	 * @param text
	 *            text to draw
	 * @param x
	 *            position of text, x (centre)
	 * @param y
	 *            position of text, y (centre)
	 * @param scale
	 *            change size of text using post scaling. For the best quality
	 *            leave as 1 and change the native size when creating each
	 *            FontData object.
	 * @param argb
	 *            Hex representation of argb number. e.g. 0xFF0000FF for opaque
	 *            blue text. You can use Android's Color to generate these on
	 *            your behalf.
	 */
	public void drawText(int resourceId, String text, int x, int y,
			float scale, int argb) {
		// Draw text. x and y are top left corner of text line
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			// Try casting to a FontTexture
			FontTexture fontTexture = null;
			try {
				fontTexture = (FontTexture) texture;
			} catch (ClassCastException e) {
				Log.e(TAG,
						"Error: Tried to drawText() with non-font resourceId!",
						e);
				return;
			}
			// Draw text
			fontTexture.drawText(text, x, y, scale, argb);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}
	
	// 추가
	// canvas.drawRect(float left, float top, float right, float bottom, Paint paint)의 랩퍼 메서드
	public void drawText(String text, int x, int y, Paint p) {
		
		//Canvas canvas = new Canvas(textBitmap);
		
		/*Paint p = new Paint();
		p.setTextSize(15); */
    	p.setColor(Color.RED);
    	p.setAntiAlias(true);

		canvas.drawText(text, x, y, p);
	}

	/**
	 * ALPHA - Use at your own risk
	 * 
	 * @param resourceId
	 * @param src
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param width
	 */
	public void drawLine(int resourceId, Rect src, int x1, int y1, int x2,
			int y2, int width) {
		// Draw a line from x to y. The resource texture will be repeated
		// longitudinally along the line.
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.drawLine(src, x1, y1, x2, y2, width);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}
	
	// 추가
	// canvas.drawLine(float startX, float startY, float stopX, float stopY, Paint paint)의 랩퍼 메서드
	public void drawLine( int x1, int y1, int x2,	int y2, Paint p) {
		
		canvas.drawLine(x1, y1, x2, y2, p);
	}
	
	// 추가
	// canvas.drawRect(float left, float top, float right, float bottom, Paint paint)의 랩퍼 메서드
	public void drawRect( int left, int top, int right,	int bottom, Paint p) {

		canvas.drawRect(left, top, right, bottom, p);
	}
	
	// 추가
	public void drawArc(int resourceId, Rect src, int x1, int y1, int x2,
			int y2, int width) {
		// Draw a line from x to y. The resource texture will be repeated
		// longitudinally along the line.
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.drawLine(src, x1, y1, x2, y2, width);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}

	/**
	 * ALPHA - Use at your own risk
	 * 
	 * @param resourceId
	 * @param src
	 * @param offsetX
	 * @param offsetY
	 */
	public void drawTile(int resourceId, Rect dst, int offsetX, int offsetY,
			float scale) {
		// Tile a texture across an entire
		Texture texture = texturesByResourceId.get(resourceId);
		if (texture != null) {
			texture.drawTile(dst, offsetX, offsetY, scale);
		} else
			Log.w("SpriteBatcher", "Warning: resourceId not found");
	}

	public Canvas getCanvas() {
		return canvas;
	}
}
