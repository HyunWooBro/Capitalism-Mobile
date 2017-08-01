package core.scene.stage.actor.widget.table.window;

import core.framework.graphics.batch.Batch;
import core.scene.stage.actor.widget.table.LayoutTable;

public class FloatingWindow extends Window<FloatingWindow> {

	public FloatingWindow(WindowCostume costume) {
		super(costume);
		mButtonTable = new LayoutTable();
		addChild(mButtonTable);
	}
	
	@Override
	public void layout() {
		super.layout();
		mButtonTable.validate();
	}
	
	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		float width = getWidth();
		float padRight = getPadRight();
		float buttonWidth = mButtonTable.getWidth();
		mButtonTable.moveTo(width - (buttonWidth+padRight), 0f);
		super.drawChildren(batch, parentAlpha);
	}

}
