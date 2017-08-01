package core.framework.graphics.texture;

public class ImageTexture extends Texture{
	
	public ImageTexture(int resID) {
		super(resID);
	}
	
	public ImageTexture(int width, int height) {
		super(width, height);
	}
	
	@Override
	protected void removeTextureRegion(TextureRegion region) {
		int x = region.getRegionX1();
		int y = region.getRegionY1();
		int width = region.getRegionWidth();
		int height = region.getRegionHeight();
		removeRegionImage(x, y, width, height);
	}
}
