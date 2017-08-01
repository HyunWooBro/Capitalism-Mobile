package project.game.building.department;

import java.util.ArrayList;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.game.CPU;
import project.game.GameScene;
import project.game.GameScene.GameScreenType;
import project.game.building.Building;
import project.game.building.BuildingManager;
import project.game.building.BuildingManager.BuildingDescription;
import project.game.building.BuildingManager.DepartmentDescription;
import project.game.building.department.stock.Livestock;
import project.game.building.department.stock.Manufacture;
import project.game.building.department.stock.Process;
import project.game.building.department.stock.Purchase;
import project.game.building.department.stock.Sales;
import project.game.building.Factory;
import project.game.cell.CellManager;
import project.game.corporation.PlayerCorporation;
import project.game.product.ProductManager;

import android.app.AlertDialog;
import android.content.DialogInterface;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.Director;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.label.CLabel;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.Label;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.button.PushButton;

public abstract class Department extends Group<Department> {

	public enum DepartmentType {
		PURCHASE(Purchase.class.getSimpleName()), 
		SALES(Sales.class.getSimpleName()), 
		ADVERTISE(Advertise.class.getSimpleName()), 
		MANUFACTURE(Manufacture.class.getSimpleName()), 
		LABORATORY(Laboratory.class.getSimpleName()), 
		LIVESTOCK(Livestock.class.getSimpleName()), 
		PROCESS(Process.class.getSimpleName()), 
		EMPTY("Empty");

		DepartmentType(String className) {
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);

			DepartmentDescription desc = BuildingManager.getInstance().getDDescByType(
					className);
			mDesc = desc;

			mName = desc.name;

			int id = Core.APP.getResources().getIdentifier(desc.name, "string",
					Core.APP.getActivity().getPackageName());
			mLabel = new SLabel(id, fontTexture);

			if(className.equals("Empty")) return;

			mRegion = imageTexture.getTextureRegion(desc.image);

			mCost = desc.setupCost;

			mColor.set(Color4.parseColor(desc.color));
		}

		public String getName() {
			return mName;
		}

		public TextureRegion getImageRegion() {
			return mRegion;
		}

		public double getCost() {
			return mCost;
		}

		public Color4 getColor() {
			return mColor;
		}

		public Label<?> getLabel() {
			return mLabel;
		}

		public DepartmentDescription getDescription() {
			return mDesc;
		}

		private String mName;
		private TextureRegion mRegion;
		private double mCost;
		private Color4 mColor = new Color4();
		private Label<?> mLabel;
		private DepartmentDescription mDesc;
	}

	protected static TextureRegion sDepartmentEmptyRegion;
	protected static TextureRegion sDepartmentContentBarRegion;

	private static TextureRegion sDepartmentDivisionBarRegion;
	private static TextureRegion sDepartmentTitleBarRegion;

	private static TextureRegion sEmployeeUntrainedRegion;
	private static TextureRegion sEmployeeTrainedRegion;

	private static SLabel sDepartmentLevelLabel;
	private static SLabel sDepartmentNumEmployeesLabel;

	protected static CLabel sCountLabel;

	static {
		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture barTexture = tm.getTexture(R.drawable.atlas_department_bar_graph);
		Texture fontTexture = tm.getTexture(R.drawable.font);

		sDepartmentEmptyRegion = imageTexture.getTextureRegion("department_commodity_empty");
		sDepartmentContentBarRegion = imageTexture.getTextureRegion("department_content_bar");

		sDepartmentDivisionBarRegion = imageTexture.getTextureRegion("department_division_bar");
		sDepartmentTitleBarRegion = imageTexture.getTextureRegion("department_title_bar");

		sEmployeeUntrainedRegion = imageTexture.getTextureRegion("employee_untrained");
		sEmployeeTrainedRegion = imageTexture.getTextureRegion("employee_trained");

		sDepartmentLevelLabel = new SLabel(R.string.label_department_level, fontTexture);
		sDepartmentNumEmployeesLabel = new SLabel(R.string.label_department_num_employees,
				fontTexture);

		sCountLabel = new CLabel("", R.array.label_array_outline_white_12, fontTexture);
	}

	/** 이 부서와 링크로 연결된 모든 부서를 저장하는 리스트 */
	protected List<Department> mConnectedDepartmentList = new ArrayList<Department>(8);
	
	/** 부서관리자 */
	protected DepartmentManager mDepartmentManager;
	
	/** 사원수 */
	protected int mNumEmployees;
	/** 훈련된 사원수 */
	protected int mNumTrainedEmployees;
	
	/**
	 * 부서 인덱스</p>
	 * 
	 * 인덱스 순서는 다음과 같다<br>
	 * 0 1 2<br>
	 * 3 4 5<br>
	 * 6 7 8<br>
	 */
	protected int mIndex;
	
	/** 부서 레벨 */
	protected int mLevel;
	
	public Department(int index, DepartmentManager departmentManager) {
		mDepartmentManager = departmentManager;
		mIndex = index;
		mLevel = 1;
	}
	
	/** 부서의 작업을 진행한다. */
	protected abstract double work();

	@Override
	public void draw(Batch batch, float parentAlpha) {
		drawPanel(batch, parentAlpha);
		// 선택된 경우에만 내용을 출력한다.
		if(isSelected()) {
			drawContent(batch, parentAlpha);
			super.draw(batch, parentAlpha);
		}
	}

	/** 패널은 부서탭에서 우측에 있는 부서가 위치할 수 있는 9개의 공간을 의미한다. */
	protected void drawPanel(Batch batch, float parentAlpha) {
		// 부서 고유색
		ShapeRenderer renderer = DepartmentManager.sShapeRenderer;
		renderer.setColor(getDepartmentType().getColor());
		renderer.drawRect(DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
				DepartmentManager.sDepartmentRectangles[mIndex].top() + 10 / 2,
				DepartmentManager.sDepartmentRectangles[mIndex].right() - 4 / 2
						- DepartmentManager.sDepartmentRectangles[mIndex].left() - 10 / 2,
				DepartmentManager.sDepartmentRectangles[mIndex].top() + 33 / 2
						- DepartmentManager.sDepartmentRectangles[mIndex].top() - 10 / 2);

		// 부서명 레이블
		Label<?> titleLabel = getDepartmentType().getLabel();
		titleLabel.southWest();
		titleLabel.moveTo(DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
				DepartmentManager.sDepartmentRectangles[mIndex].top() + 33 / 2);
		titleLabel.draw(batch, parentAlpha);

		sCountLabel.setConcatLabel(null);
		sCountLabel.moveTo(DepartmentManager.sDepartmentRectangles[mIndex].right() - 10 / 2,
				DepartmentManager.sDepartmentRectangles[mIndex].top() + 33 / 2);
		sCountLabel.setText(Integer.toString(mLevel));
		sCountLabel.southEast();
		sCountLabel.draw(batch, parentAlpha);
	}

	/** 부서를 선택하면 좌측 공간에 자세한 세부내용이 출력된다. */
	protected void drawContent(Batch batch, float parentAlpha) {
		// 부서 분활 막대
		batch.draw(sDepartmentDivisionBarRegion, 0, 320 / 2);

		// 부서 이미지
		batch.draw(getDepartmentType().getImageRegion(), 30 / 2, 350 / 2);

		// 부서명 바
		batch.draw(sDepartmentTitleBarRegion, 350 / 2, 350 / 2);

		// 부서명 레이블
		Label<?> titleLabel = getDepartmentType().getLabel();
		titleLabel.south();
		titleLabel.moveTo(350 / 2 + 143 / 2, 350 / 2 + 30 / 2);
		titleLabel.draw(batch, parentAlpha);

		// 부서 레벨 레이블
		sDepartmentLevelLabel.moveTo(350 / 2, 420 / 2);
		sDepartmentLevelLabel.draw(batch, parentAlpha);
		sCountLabel.setConcatLabel(sDepartmentLevelLabel);
		sCountLabel.setText(" : " + mLevel);
		sCountLabel.draw(batch, parentAlpha);

		// 부서 사원수 레이블
		sDepartmentNumEmployeesLabel.moveTo(350 / 2, 510 / 2);
		sDepartmentNumEmployeesLabel.draw(batch, parentAlpha);
		sCountLabel.setConcatLabel(sDepartmentNumEmployeesLabel);
		sCountLabel.setText(" : " + mNumEmployees);
		sCountLabel.draw(batch, parentAlpha);

		// 사원 이미지
		int untrained = mNumTrainedEmployees;
		for(int i = 0; i < mNumEmployees; i++) {
			if(untrained > 0) {
				batch.draw(sEmployeeTrainedRegion, 340 / 2 + i * 30 / 2, 520 / 2);
				untrained--;
			} else {
				batch.draw(sEmployeeUntrainedRegion, 340 / 2 + i * 30 / 2, 520 / 2);
			}
		}
	}

	@Override
	public Actor<?> contact(float x, float y) {
		if(!isSelected()) return null;
		return super.contact(x, y);
	}

	public DepartmentManager getDepartmentManager() {
		return mDepartmentManager;
	}

	/** 이 부서와 연결된 부서 리스트를 반환한다. */
	public List<Department> getConnectedDepartmentList() {
		return mConnectedDepartmentList;
	}

	public abstract DepartmentType getDepartmentType();

	// 작동하는가??? 인가...
	// public abstract boolean isMeaningfull();

	/** 사원의 수를 얻는다. */
	public int getNumEmployees() {
		return mNumEmployees;
	}

	/** 이 부서가 선택된 경우 true를 반환한다. */
	public boolean isSelected() {
		if(!mDepartmentManager.hasParent()) return false;
		return DepartmentManager.sSelectedDepartmentIndex == mIndex;
	}

	/**
	 * 부서와 인접한 링크의 변화가 발생했을 때 호출된다. 단 링크와 연결되는 반대편 부서가 존재하는 경우에만 적용된다.</p>
	 * 
	 * 기본적으로 {@link #onChanged()}를 호출한다.</p>
	 */
	protected void onLinkChanged() {
		onChanged();
	}

	/**
	 * 현재 부서와 링크로 연결된 부서에 변화(삽입, 변경, 삭제)가 발생하거나 연결된 부서에 의해
	 * {@link #notifyDepartmentChange()}가 발생하는 경우 호출된다.</p>
	 * 
	 * 기본적으로 {@link #onChanged()}를 호출한다.</p>
	 */
	protected void onLinkedDepartmentChanged() {
		onChanged();
	}

	/** 이 부서와 연결된 모든 부서의 {@link #onLinkedDepartmentChanged()}를 호출한다. */
	public void notifyDepartmentChange() {
		List<Department> departmentList = mConnectedDepartmentList;
		int n = departmentList.size();
		for(int i = 0; i < n; i++) {
			Department department = departmentList.get(i);
			department.onLinkedDepartmentChanged();
		}
	}

	/**
	 * {@link #onLinkChanged()}나 {@link #onLinkedDepartmentChanged()}에서 기본적으로
	 * 호출한다.
	 */
	protected void onChanged() {
	}

	/** 부서를 변경할 수 있는가? false를 리턴하면 부서를 변경할 수도 제거할 수도 없다. */
	protected boolean isModifiable() {
		return true;
	}

	/** Department를 터치하거나 새로 추가되면 호출된다. */
	protected void onSelected() {
	}

	/** 
	 * 매일 리셋할 내용이 있으면 이 메서드를 재정의해서 추가하면 된다. 
	 * {@link CPU#process(GameScene)}가 처리되기 전에 호출된다.
	 */
	protected void resetDaily() {
	}
	
	/** 
	 * 매달 리셋할 내용이 있으면 이 메서드를 재정의해서 추가하면 된다. 
	 * {@link CPU#process(GameScene)}가 처리되기 전에 호출된다.
	 */
	protected void resetMonthly() {
	}
	
	public void setLevel(int level) {
		if(mLevel == level) return;
		mLevel = level;
		onLevelChanged();
	}

	/** 부서의 레벨이 변경될 경우 호출된다. */
	protected void onLevelChanged() {
	}

}