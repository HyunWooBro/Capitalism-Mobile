package core.scene.stage.actor.drawable;

import core.framework.graphics.Form;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureRegion;

/**
 * {@link #draw(Batch, Form)}은 내부적으로 {@link #draw(Batch, float, float, float, float, boolean, boolean)}를 
 * 단순히 호출한다. 즉, 변환을 직접적으로 표현하지 않으므로 필요하다면 부모의 변환을 간접적으로 이용해야 한다. 
 * 
 * @author 김현우
 */
public class TileDrawable extends TextureRegionDrawable {

	public TileDrawable(TextureRegion textureRegion) {
		super(textureRegion);
	}
	
	@Override
	public void draw(Batch batch, float dstX, float dstY, float dstWidth,
			float dstHeight, boolean flipX, boolean flipY) {
		
		TextureRegion region = getRegion();
		
		float tileWidth = getWidth();
		float tileHeight = getHeight();
		
		int cols = (int) (dstWidth / tileWidth);
		int rows = (int) (dstHeight / tileHeight);
		for(int i=0; i<cols; i++) {
			for(int j=0; j<rows; j++) {
				batch.draw(region, dstX + tileWidth*i, dstY + tileHeight*j, tileWidth, tileHeight, flipX, flipY);
			}
		}
		
		float extraWidth = dstWidth % tileWidth;
		float extraHeight = dstHeight % tileHeight;
		
		boolean hasExtraWidth = extraWidth > 0f;
		boolean hasExtraHeight = extraHeight > 0f;
		if(!hasExtraWidth && !hasExtraHeight) return;
		
		Texture texture = region.getTexture();
		
		int regionX = region.getRegionX1();
		int regionY = region.getRegionY1();
		
		int regionWidth = region.getRegionWidth();
		int regionHeight = region.getRegionHeight();
		
		float scaleX = tileWidth / regionWidth;
		float scaleY = tileHeight / regionHeight;
		
		float scaledExtraWidth = extraWidth / scaleX;
		float scaledExtraHeight = extraHeight / scaleY;
		
		float endX = dstWidth - extraWidth;
		float endY = dstHeight - extraHeight;
		
		if(hasExtraWidth) {
			if(flipX) regionX += region.getRegionWidth()-scaledExtraWidth;
			for(int i=0; i<rows; i++)
				batch.draw(texture, regionX, regionY, scaledExtraWidth, regionHeight, endX, dstY + tileHeight*i, extraWidth, tileHeight, flipX, flipY);
			if(flipX) regionX = region.getRegionX1();
		}
		
		if(hasExtraHeight) {
			if(flipY) regionY += region.getRegionHeight()-scaledExtraHeight;
			for(int i=0; i<cols; i++)
				batch.draw(texture, regionX, regionY, regionWidth, scaledExtraHeight, dstX + tileWidth*i, endY, tileWidth, extraHeight, flipX, flipY);
			if(flipY) regionY = region.getRegionY1();
		}
		
		if(hasExtraWidth && hasExtraHeight) {
			if(flipX) regionX += region.getRegionWidth()-scaledExtraWidth;
			if(flipY) regionY += region.getRegionHeight()-scaledExtraHeight;
			batch.draw(texture, regionX, regionY, scaledExtraWidth, scaledExtraHeight, endX, endY, extraWidth, extraHeight, flipX, flipY);
		}

	}
	
	@Override
	public void draw(Batch batch, Form dstForm) {
		float dstX = dstForm.getX();
		float dstY = dstForm.getY();
		
		float dstWidth = dstForm.getWidth();
		float dstHeight = dstForm.getHeight();
		
		boolean flipX = dstForm.isFlipX();
		boolean flipY = dstForm.isFlipY();
		
		draw(batch, dstX, dstY, dstWidth, dstHeight, flipX, flipY);
	}

}
