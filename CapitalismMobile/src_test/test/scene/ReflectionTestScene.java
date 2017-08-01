package test.scene;

import java.lang.reflect.*;

import org.framework.*;

import project.framework.*;
import android.view.*;
import core.framework.*;
import core.framework.graphics.texture.*;
import core.scene.*;
import core.scene.stage.actor.widget.*;

@SuppressWarnings("rawtypes")
public class ReflectionTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage);
		
		
		Class<?> c = getClass();
		//c.is
		
		//Core.APP.debug(dump(new Vector2(), 0));
		
		main();
		
	}
	
	// http://onecellboy.tistory.com/66
	public static void main() {         
        Class c = Integer.class; 
        Class[] iface = c.getInterfaces(); 
        Constructor [] ctor = c.getConstructors(); 
        Method [] m = c.getMethods(); 
        Field[] f = c.getFields(); 
        Class temp = c; 
        
        Core.APP.debug("==========start getSuperclass() ============" ); 
        while( (temp=temp.getSuperclass()) != null ){ 
            Core.APP.debug(temp.toString()); 
        }
         Core.APP.debug("==========end getSuperclass() ============" ); 
        
        Core.APP.debug("");
         Core.APP.debug("==========start getInterfaces() ============" ); 
        for(int i=0; i<iface.length; i++){ 
            Core.APP.debug("interface[" + i + "]:" + iface[i].toString()); 
        }
         Core.APP.debug("==========end getInterfaces() ============" );  
        Core.APP.debug(""); 
        Core.APP.debug("==========start getConstructors() ============" ); 
        for(int i=0; i<ctor.length; i++){ 
            Core.APP.debug("Constructor[" + i + "]:" + ctor[i].toString()); 
        } 
        Core.APP.debug("==========end getConstructors() ============" );
         Core.APP.debug(""); 
        Core.APP.debug("==========start getMethods() ============" );
         for(int i=0; i<m.length; i++){ 
            Core.APP.debug("Method[" + i + "]:" + m[i].toString()); 
        } 
        Core.APP.debug("==========end getMethods() ============" );
         Core.APP.debug(""); 
        Core.APP.debug("==========start getFields() ============" );
         for(int i=0; i<f.length; i++){ 
            Core.APP.debug("Field[" + i + "]:" + f[i].toString()); 
        }
         Core.APP.debug("==========end getFields() ============");
         
	} //end of main 

	
	// http://stackoverflow.com/questions/37628/what-is-reflection-and-why-is-it-useful
	public static String dump(Object o, int callCount) {
	    callCount++;
	    StringBuffer tabs = new StringBuffer();
	    for (int k = 0; k < callCount; k++) {
	        tabs.append("\t");
	    }
	    StringBuffer buffer = new StringBuffer();
	    Class oClass = o.getClass();
	    if (oClass.isArray()) {
	        buffer.append("\n");
	        buffer.append(tabs.toString());
	        buffer.append("[");
	        for (int i = 0; i < Array.getLength(o); i++) {
	            if (i < 0)
	                buffer.append(",");
	            Object value = Array.get(o, i);
	            if (value.getClass().isPrimitive() ||
	                    value.getClass() == java.lang.Long.class ||
	                    value.getClass() == java.lang.String.class ||
	                    value.getClass() == java.lang.Integer.class ||
	                    value.getClass() == java.lang.Boolean.class
	                    ) {
	                buffer.append(value);
	            } else {
	                buffer.append(dump(value, callCount));
	            }
	        }
	        buffer.append(tabs.toString());
	        buffer.append("]\n");
	    } else {
	        buffer.append("\n");
	        buffer.append(tabs.toString());
	        buffer.append("{\n");
	        while (oClass != null) {
	            Field[] fields = oClass.getDeclaredFields();
	            for (int i = 0; i < fields.length; i++) {
	                buffer.append(tabs.toString());
	                fields[i].setAccessible(true);
	                buffer.append(fields[i].getName());
	                buffer.append("=");
	                try {
	                    Object value = fields[i].get(o);
	                    if (value != null) {
	                        if (value.getClass().isPrimitive() ||
	                                value.getClass() == java.lang.Long.class ||
	                                value.getClass() == java.lang.String.class ||
	                                value.getClass() == java.lang.Integer.class ||
	                                value.getClass() == java.lang.Boolean.class
	                                ) {
	                            buffer.append(value);
	                        } else {
	                            buffer.append(dump(value, callCount));
	                        }
	                    }
	                } catch (IllegalAccessException e) {
	                    buffer.append(e.getMessage());
	                }
	                buffer.append("\n");
	            }
	            oClass = oClass.getSuperclass();
	        }
	        buffer.append(tabs.toString());
	        buffer.append("}\n");
	    }
	    return buffer.toString();
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}
}