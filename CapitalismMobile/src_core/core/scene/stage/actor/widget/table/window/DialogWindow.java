package core.scene.stage.actor.widget.table.window;

import core.scene.stage.actor.Actor;
import core.scene.stage.actor.widget.label.Label;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.TableCell;

public class DialogWindow extends Window<DialogWindow> {
	
	private LayoutTable mContentTable;
	
	private TableCell mContentCell;
	private TableCell mButtonCell;

	public DialogWindow(WindowCostume costume) {
		super(costume);
		mContentTable = new LayoutTable();
		mButtonTable = new LayoutTable();
		
		mContentCell = addCell(mContentTable);
		row();
		mButtonCell = addCell(mButtonTable);
		
		mButtonTable.all().padLeft(5f).padRight(5f);
	}
	
	public DialogWindow addText(Label<?> label) {
		mContentTable.addCell(label);
		return this;
	}
	
	public DialogWindow addContent(Actor<?> actor) {
		mContentTable.row();
		mContentTable.addCell(actor);
		return this;
	}

	public LayoutTable getContentTable() {
		return mContentTable;
	}
	
	public TableCell getContentCell() {
		return mContentCell;
	}

	public TableCell getButtonCell() {
		return mButtonCell;
	}

	@Override
	protected void computeSize() {
		mButtonCell.prefWidth(mContentTable.getPrefWidth());
		super.computeSize();
	}

}
