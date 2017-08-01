package core.scene.stage.actor.widget.table;

import java.util.List;

import core.scene.stage.actor.Actor;

/**
 * 세로로만 TableCell을 배치할 수 있는 특수 Table이다. 기존 Table의 경우 중간에 TableCell을 추가하거나 
 * 제거하기 위해서는 직접 Table을 정리한 후 다시 만들거나 아예 새로 Table을 생성해야 하지만 
 * VerticalTable은 어디든 추가 및 삭제를 지원하고 자동으로 Table을 재생성한다.</p>
 * 
 * 삽입되는 TableCell의 가로 또는 세로 span은 1만 허용되며 수정하더라도 무시된다.</p>
 * 
 * @author 김현우
 */
public class VerticalTable extends Table<VerticalTable> {
	
	private boolean mRebuilding;
	
	public TableCell addCell(int index) {
		return add(index, new TableCell(this, null));
	}
	
	public TableCell addCell(int index, Actor<?> actor) {
		return add(index, new TableCell(this, actor));
	}
	
	public TableCell addCell(int index, TableCell cell) {
		return add(index, cell);
	}
	
	@Override
	protected TableCell add(TableCell cell) {
		return add(getCellList().size(), cell);
	}
	
	protected TableCell add(int index, TableCell cell) {
		List<TableCell> cellList = getCellList();
		// 중간에 삽입하는 경우
		if(cellList.size() != index) {
			cellList.add(index, cell);
			applyDefaults(cell);
			mRebuilding = true;
			rebuild();
			mRebuilding = false;
		} else { // 마지막에 삽입하는 경우
			super.add(cell);
			super.row();
		}
		return cell;
	}
	
	@Override
	public boolean removeChild(Actor<?> child) {
		List<TableCell> cellList = getCellList();
		TableCell cell = getCellByActor(child);
		if(!super.removeChild(child)) return false;
		cellList.remove(cell);
		mRebuilding = true;
		rebuild();
		mRebuilding = false;
		return true;
	}
	
	@Override
	protected void applyDefaults(TableCell cell) {
		if(mRebuilding) return;
		super.applyDefaults(cell);
	}
	
	@Override
	protected int getCellRowSpan(TableCell cell) {
		return 1;
	}
	
	@Override
	protected int getCellColSpan(TableCell cell) {
		return 1;
	}
	
	@Deprecated
	@Override
	public TableCell row() {
		throw new UnsupportedOperationException("Invoking this method is restricted.");
	}
	
}
