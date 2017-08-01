package test.scene;

import org.framework.R;

import project.framework.Utils;

import android.view.KeyEvent;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.math.Rectangle;
import core.scene.Scene;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.DataTable;
import core.scene.stage.actor.widget.table.DataTable.ExtraBackground;
import core.scene.stage.actor.widget.table.DataTable.ExtraGradationBackground;
import core.scene.stage.actor.widget.table.DataTable.ExtraLine;
import core.scene.stage.actor.widget.table.DataTable.TableStyle;

@SuppressWarnings("rawtypes")
public class DataTableTestScene extends Scene {
	
	private Image mBackGroundImage;
	
	private Image mTestImage;

	@Override
	protected void create() {
		
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		
		Texture mImageTexture = tm.getTexture(R.drawable.atlas);
		Texture fontTexture = tm.getTexture(R.drawable.font);
		
		TextureRegion mBackgroundRegion = mImageTexture.getTextureRegion("mainmenu_background");
		TextureRegion mApartmentRegion = mImageTexture.getTextureRegion("cell_2x2_apratment1");
		
		mBackGroundImage = new Image(mBackgroundRegion);

		mTestImage = new Image(mApartmentRegion)
				.moveTo(0, 100);
		
		DataTable data = new DataTable();
		
		TableStyle style = new TableStyle();
		style.lineWidth = 10f;
		style.showBackground = true;
		//style.bgColor = null;
		//style.extraBackgroundColor = Color4.LTGREEN4;
		//style.extraBackgroundStart = 0;
		//style.extraBackgroundSpan = 2;
		//style.extraBackgroundHop = 999;
		//style.extraBackgroundHorizontal = false;
		ExtraBackground extra1 = new ExtraBackground();
		extra1.color = Color4.LTGREEN4;
		extra1.offset = 0;
		extra1.hop = 999;
		style.addExtraBackground(extra1);
		ExtraBackground extra2 = new ExtraBackground();
		extra2.color = Color4.LTGRAY4;
		extra2.offset = 1;
		extra2.hop = 1;
		extra2.range = new Rectangle(1f, 1f, 1f, 3f);
		style.addExtraBackground(extra2);
		ExtraBackground extra3 = new ExtraBackground();
		extra3.horizontal = false;
		extra3.color = Color4.LTRED4;
		extra3.offset = 0;
		extra3.hop = 1;
		extra3.range = new Rectangle(0f, 4f, 0f, 0f);
		style.addExtraBackground(extra3);
		data.setTableStyle(style);
		
		data.pad(10f);
		
		data.all().pad(5f);
		data.col(0).left();
		
		data.addCell(new DLabel("이름", fontTexture));
		data.addCell(new DLabel("나이", fontTexture));
		data.addCell(new DLabel("주민등록번호", fontTexture));
		data.row();
		data.addCell(new DLabel("김현우", fontTexture));
		data.addCell(new DLabel("29", fontTexture));
		data.addCell(new DLabel("2912131", fontTexture));
		data.row();
		data.addCell(new DLabel("김현준", fontTexture));
		data.addCell(new DLabel("24", fontTexture));
		data.addCell(new DLabel("1234554", fontTexture));
		data.row();
		data.addCell(new DLabel("이경희", fontTexture));
		data.addCell(new DLabel("56", fontTexture));
		data.addCell(new DLabel("2123145", fontTexture));
		data.row();
		data.addCell(new DLabel("김인식", fontTexture));
		data.addCell(new DLabel("58", fontTexture));
		data.addCell(new DLabel("2911111", fontTexture));
		data.row();
		data.addCell(new DLabel("john", fontTexture));
		data.addCell(new DLabel("56", fontTexture));
		data.addCell(new DLabel("2786655", fontTexture));
		data.row();
		data.addCell(new DLabel("richard", fontTexture));
		data.addCell(new DLabel("58", fontTexture));
		data.addCell(new DLabel("2222222", fontTexture));
		
		data.pack();
		
		
		
		
		DataTable data2 = new DataTable();
		
		TableStyle style2 = new TableStyle();
		style2.lineColor = null;
		style2.lineWidth = 3f;
		ExtraLine extra__ = new ExtraLine();
		//extra__.range = new Rectangle(1f, 0f, 0f, 0f);
		extra__.width = 10f;
		extra__.color = Color4.BLUE4;
		//extra__.autoComplete = false;
		extra__.style = ExtraLine.EL_TOP | ExtraLine.EL_LEFT | ExtraLine.EL_BOTTOM | ExtraLine.EL_RIGHT;
		//style2.addExtraLine(extra__);
		ExtraLine extra__2 = new ExtraLine();
		extra__2.range = new Rectangle(0f, 1f, -1f, -1f);
		extra__2.style = ExtraLine.EL_TOP | ExtraLine.EL_HORIZONTAL;
		style2.addExtraLine(extra__2);
		style2.showBackground = true;
		//style.bgColor = null;
		//style.extraBackgroundColor = Color4.LTGREEN4;
		//style.extraBackgroundStart = 0;
		//style.extraBackgroundSpan = 2;
		//style.extraBackgroundHop = 999;
		//style.extraBackgroundHorizontal = false;
		ExtraBackground extra_ = new ExtraBackground();
		extra_.color = Color4.LTGREEN4;
		extra_.offset = 0;
		extra_.span = 2;
		//extra_.horizontal = false;
		extra_.range = new Rectangle(0f, 3f, 0f, 0f);
		//style2.addExtraBackground(extra_);
		ExtraGradationBackground extra_2 = new ExtraGradationBackground();
		//extra_2.color = Color4.LTRED4;
		extra_2.topLeftColor = Color4.LTRED4;
		extra_2.topRightColor = Color4.LTRED4;
		extra_2.bottomLeftColor = Color4.DKRED4;
		extra_2.bottomRightColor = Color4.DKRED4;
		extra_2.offset = 0;
		extra_2.hop = 0;
		extra_2.horizontal = true;
		extra_2.range = new Rectangle(1f, 1f, -1f, -1f);
		//style2.addExtraBackground(extra_2);
		data2.setTableStyle(style2);
		
		data2.all().pad(5f);
		//data2.col(0).left();
		
		data2.addCell(new DLabel("A", fontTexture)).colSpan(2);
		data2.addCell(new Image(mApartmentRegion)).actorSize(40f, 15f);
		data2.row();
		data2.addCell(new DLabel("B", fontTexture)).rowSpan(2);
		data2.addCell(new DLabel("B1", fontTexture));
		data2.addCell(new DLabel("B1111", fontTexture));
		data2.row();
		data2.addCell(new DLabel("B2", fontTexture));
		data2.addCell(new DLabel("B2222", fontTexture));
		data2.row();
		data2.addCell(new DLabel("C", fontTexture)).rowSpan(2);
		data2.addCell(new DLabel("C1", fontTexture));
		data2.addCell(new DLabel("C1111", fontTexture));
		data2.row();
		data2.addCell(new DLabel("C2", fontTexture));
		data2.addCell(new DLabel("C2222", fontTexture));
		
		data2.pack();
		
		data2.moveTo(200, 50);
		
		
		
		getStage().addFloor()
			.addChild(mBackGroundImage)
			.addChild(mTestImage)
			.addChild(data)
			.addChild(data2);
	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		//if(keyCode == KeyEvent.KEYCODE_MENU)
		//	Director.getInstance().changeScene(new WindowTestScene());
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
