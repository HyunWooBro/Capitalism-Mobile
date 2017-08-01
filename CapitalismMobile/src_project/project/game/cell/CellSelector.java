package project.game.cell;

import java.util.Arrays;
import java.util.List;

import org.framework.R;

import project.game.GameScene;
import project.game.GameScene.GameScreenType;
import project.game.building.Building;
import project.game.building.BuildingManager;
import project.game.ui.BuildingBar;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Animation.PlayMode;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.math.MathUtils;
import core.math.Vector2;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.action.absolute.ColorTo;
import core.scene.stage.actor.action.absolute.ScaleTo;
import core.scene.stage.actor.drawable.AnimationDrawable;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ActionEvent;
import core.scene.stage.actor.event.ActionListener;
import core.scene.stage.actor.widget.Image;

public class CellSelector extends Image {

	public static final int MIN_SELECTOR_SIZE = 2;
	public static final int MAX_SELECTOR_SIZE = 3;

	public enum SelectorColor {
		/** 건설 불가 */
		RED(Color4.rgb(240, 60, 80)),
		/** 건설 가능(건물 구입 비용 추가) */
		YELLOW(Color4.rgb(240, 240, 60)),
		/** 건설 가능(건물 구입 비용 없음) */
		GREEN(Color4.rgb(100, 240, 80)),
		/** 건물 선택 */
		BLUE(Color4.rgb(60, 120, 240));

		SelectorColor(int color) {
			mColor.set(color);
		}

		public Color4 getColor() {
			return mColor;
		}

		private Color4 mColor = new Color4();
	}

	private AnimationDrawable mTwoByTwoAnimationDrawable;
	private AnimationDrawable mThreeByThreeAnimationDrawable;

	private SelectorColor mSelectorColor;

	private int mSelectorSize;

	private Cell mSelectedCell;

	private Building mSelectedBuilding;

	public CellSelector() {
		super();
		initialize();
	}

	private void initialize() {

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture selectorTexture = tm.getTexture(R.drawable.atlas_selector);

		Animation twoByTwo = new Animation(200, Arrays.asList(
				selectorTexture.getTextureRegion("twobytwo_white_01"),
				selectorTexture.getTextureRegion("twobytwo_white_02"),
				selectorTexture.getTextureRegion("twobytwo_white_03")))
				.setPlayMode(PlayMode.PING_PONG);

		mTwoByTwoAnimationDrawable = (AnimationDrawable) Drawable.newDrawable(twoByTwo);

		Animation threeByThree = new Animation(200, Arrays.asList(
				selectorTexture.getTextureRegion("threebythree_white_01"),
				selectorTexture.getTextureRegion("threebythree_white_02"),
				selectorTexture.getTextureRegion("threebythree_white_03")))
				.setPlayMode(PlayMode.PING_PONG);

		mThreeByThreeAnimationDrawable = (AnimationDrawable) Drawable.newDrawable(threeByThree);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(!isVisible())
			return;
		drawSelector(batch);
	}

	private void drawSelector(Batch batch) {

		if(mSelectedCell == null)
			return;

		validate();

		Cell[][] cells = CellManager.getInstance().getCells();
		int row = mSelectedCell.mRow;
		int col = mSelectedCell.mColumn;

		Cell cell = cells[row][col];

		// 건설중이 아니라면
		if(BuildingBar.sSelectedBuildingDescription == null) {
			// 이미 건물이 존재하는 것을 확인했으므로
			int x = cell.m_first_link_point.x;
			int y = cell.m_first_link_point.y;

			moveTo(cells[x][y].m_cellpoint.x - Cell.CELL_WIDTH / 2 - (getSelectorSize() - 1)
					* Cell.CELL_WIDTH / 2, cells[x][y].m_cellpoint.y - Cell.CELL_HEIGHT / 2);
			drawSelf(batch, 1f);

			// 건설중이라면
		} else {
			moveTo(cell.m_cellpoint.x - Cell.CELL_WIDTH / 2 - (getSelectorSize() - 1)
					* Cell.CELL_WIDTH / 2, cell.m_cellpoint.y - Cell.CELL_HEIGHT / 2);
			drawSelf(batch, 1f);
		}

	}

	public void reset() {
		mSelectedCell = null;
		mSelectedBuilding = null;
	}

	public void select(float x, float y) {

		Cell[][] cells = CellManager.getInstance().getCells();
		int mapSize = CellManager.getInstance().getSize();

		mSelectedCell = null;

		out: for(int i = 0; i < mapSize; i++) {
			for(int j = 0; j < mapSize; j++) {
				Cell cell = cells[i][j];

				// 셀의 사격 영역에 포함된다면
				if(cell.mCellRectangle.contains(x, y)) {
					float dst1, dst2;

					// 셀의 중심에서 오른쪽에 위치해 있으면
					if(x >= cell.m_cellpoint.x) {
						if(mapSize > j + 1) {
							// 현재 셀(i, j)의 중심과 터치된 좌표와의 거리
							dst1 = Vector2.dst2(x, y, cell.m_cellpoint.x, cell.m_cellpoint.y);
							// 다음 셀(i, j+1)의 중심과 터치된 좌표와의 거리
							dst2 = Vector2.dst2(x, y, cells[i][j + 1].m_cellpoint.x,
									cells[i][j + 1].m_cellpoint.y);

							// 현재 셀과 더 가깝다면
							if(dst1 >= dst2)
								mSelectedCell = cells[i][j + 1];
							// 다음 셀과 더 가깝다면
							else
								mSelectedCell = cells[i][j];
							break out;
						} else {
							mSelectedCell = cells[i][j];
							break out;
						}
					} else { // 셀의 중심에서 왼쪽에 위치해 있으면
						if(mapSize > i + 1) {
							dst1 = Vector2.dst2(x, y, cell.m_cellpoint.x, cell.m_cellpoint.y);
							dst2 = Vector2.dst2(x, y, cells[i + 1][j].m_cellpoint.x,
									cells[i + 1][j].m_cellpoint.y);

							if(dst1 >= dst2)
								mSelectedCell = cells[i + 1][j];
							else
								mSelectedCell = cells[i][j];
							break out;
						} else {
							mSelectedCell = cells[i][j];
							break out;
						}
					}
				}
			}
		}

		updateSelectedCell();
	}

	/** row와 col으로 셀을 선택한다 */
	public void select(int row, int col) {
		// CellMap cellMap = mCellMapList.get(mCurrentCellMapIndex);
		Cell[][] cells = CellManager.getInstance().getCells();
		int mapSize = CellManager.getInstance().getSize();

		if((row < 0 || col < 0) && (row > mapSize || col > mapSize)) {
			mSelectedCell = null;
		} else
			mSelectedCell = cells[row][col];

		updateSelectedCell();
	}

	private void updateSelectedCell() {
		// Selector의 상태를 업데이트한다.
		updateSelectorState();

		// 건설중이면 건물을 선택할 수 없다.
		if(BuildingBar.sSelectedBuildingDescription == null)
			selectBuilding(mSelectedCell);
	}

	/** CellSelector의 상태(색, 출력 여부)를 결정한다. */
	private void updateSelectorState() {
		if(mSelectedCell == null)
			return;

		// 건설중이 아니라면
		if(BuildingBar.sSelectedBuildingDescription == null) {
			// 선택한 셀에 건물이 존재하면
			if(mSelectedCell.mBuilding != null) {
				setVisible(true);
				setSelectorColor(SelectorColor.BLUE);

				// 선택한 셀에 건물이 존재하지 않으면
			} else
				setVisible(false);

			// 건설중이라면
		} else {
			boolean hasHouse = false;

			int row = mSelectedCell.mRow;
			int col = mSelectedCell.mColumn;

			// selector가 맵을 넘어가는 것을 방지
			int mapSize = CellManager.getInstance().getMapSize();
			if(mapSize < row + mSelectorSize || mapSize < col + mSelectorSize) {
				setVisible(false);
				return;
			}

			Cell[][] cells = CellManager.getInstance().getCells();

			setVisible(true);

			for(int i = row; i < row + mSelectorSize; i++) {
				for(int j = col; j < col + mSelectorSize; j++) {

					if(cells[i][j].m_celltype == Cell.CellType.ROAD
							|| cells[i][j].m_celltype == Cell.CellType.RIVER
							|| cells[i][j].m_maxlink > 0) {
						setSelectorColor(SelectorColor.RED);
						return;
					}

					if(cells[i][j].m_celltype == Cell.CellType.HOUSE_1X1)
						hasHouse = true;
				}
			}

			if(hasHouse) {
				setSelectorColor(SelectorColor.YELLOW);
			} else
				setSelectorColor(SelectorColor.GREEN);
		}
	}

	private void setSelectorColor(SelectorColor color) {
		mSelectorColor = color;
		setColor(color.getColor());
	}

	private void selectBuilding(Cell cell) {
		if(cell != null && cell.mBuilding != null) {
			List<Building> buildingList = BuildingManager.getInstance().getBuildingList();
			int n = buildingList.size();
			for(int i = 0; i < n; i++) {
				Building building = buildingList.get(i);
				if(cell.mBuilding != building) continue;

				if(!hasSelectedBuilding()) {
					selectBuilding(building, cell);
				} else {
					// 이미 선택된 건물을 또 다시 선택하면 스크린을 바꾼다.
					if(getSelectedBuilding() == building) {
						// 단, 내부에 진입할 수 있는 경우에만
						if(building.canEnter())
							GameScene.chnageGameScreenType(GameScreenType.BUILDING);
					} else
						selectBuilding(building, cell);
				}
				break;
			}
		} else
			reset();
	}

	private void selectBuilding(Building building, Cell cell) {
		mSelectedBuilding = building;
		setSelectorSize(building.getDescription().size);
		animateSelectedBuilding(cell);
	}

	private void animateSelectedBuilding(Cell cell) {

		int sizeMinusOne = cell.mBuilding.getDescription().size - 1;
		int row = cell.m_first_link_point.x + sizeMinusOne;
		int col = cell.m_first_link_point.y + sizeMinusOne;
		final Cell buildingCell = CellManager.getInstance().getCell(row, col);

		buildingCell.cancelActions();
		buildingCell.addAction(
				new ColorTo(new Color4(1f, 2f, 2f, 2f), 300).setRepeatCount(1).setRepeatMode(
						Action.REVERSE)).addAction(
				new ScaleTo(1.2f, 1.2f, 300).setRepeatCount(1).setRepeatMode(Action.REVERSE)
						.setActionListener(new ActionListener() {

							@Override
							public void onStart(ActionEvent event, Action action, Actor<?> listener) {
								buildingCell.mDrawLast = true;
								CellManager.getInstance().addDrawLast(action.getActor());
							}

							@Override
							public void onEnd(ActionEvent event, Action action, Actor<?> listener) {
								remove(action);
							}

							@Override
							public void onCancel(ActionEvent event, Action action, Actor<?> listener) {
								remove(action);
							}

							private void remove(Action action) {
								if(!buildingCell.mDrawLast)
									return;
								buildingCell.mDrawLast = false;
								buildingCell.scaleTo(1f);
								buildingCell.setColor(Color4.WHITE4);
								CellManager.getInstance().removeDrawLast(action.getActor());
							}
						}));
	}

	public int getSelectorSize() {
		return mSelectorSize;
	}

	/** 출력될 CellSelector의 사이즈를 지정한다 (size by size) */
	public void setSelectorSize(int size) {
		mSelectorSize = MathUtils.clamp(size, MIN_SELECTOR_SIZE, MAX_SELECTOR_SIZE);

		switch(mSelectorSize) {
			case 2:
				setDrawable(mTwoByTwoAnimationDrawable);
				break;
			case 3:
				setDrawable(mThreeByThreeAnimationDrawable);
				break;
		}
	}

	public boolean canBuild() {
		return mSelectorColor == SelectorColor.GREEN || mSelectorColor == SelectorColor.YELLOW;
	}

	public Building getSelectedBuilding() {
		return mSelectedBuilding;
	}

	public Cell getSelectedCell() {
		return mSelectedCell;
	}

	public boolean hasSelectedBuilding() {
		return mSelectedBuilding != null;
	}

	public boolean hasSelectedCell() {
		return mSelectedCell != null;
	}

}
