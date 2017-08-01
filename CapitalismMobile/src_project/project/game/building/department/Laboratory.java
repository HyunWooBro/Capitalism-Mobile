package project.game.building.department;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.framework.R;

import project.framework.Utils;
import project.framework.Utils.MessageDialogData;
import project.framework.Utils.YesOrNoDialogData;
import project.game.Time;
import project.game.building.department.stock.StockDepartment;
import project.game.corporation.Corporation;
import project.game.corporation.CorporationManager;
import project.game.corporation.UserCorporation;
import project.game.product.ProductGroup;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ManufacturingDescription;
import project.game.product.ProductManager.ProductData;
import project.game.product.ProductManager.ProductDescription;
import project.game.ui.UIManager;

import core.framework.Core;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.action.Run;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.label.SLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.table.window.DialogWindow;
import core.scene.stage.actor.widget.utils.Align.HAlign;

public class Laboratory extends Department {
	
	private static int[] sBasicExpectedTechs = { 1, // 3개월
			3, // 6개월
			8, // 12개월
			20, // 24개월
			50 // 48 개월
	};

	private static float[] sTeamPerformances = { 1.00f, 1.70f, // +70%
			2.40f, // +70%
			2.95f, // +55%
			3.50f, // +55%
			3.90f, // +40%
			4.30f, // +40%
			4.55f, // +25%
			4.80f // +25%
	};

	private static PushButton sTechnologyButton;
	private static SLabel sResearchLabel;
	private static SLabel sStopLabel;

	static {
		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);
	}

	private GregorianCalendar mFinalDate;
	private long mFinalDateInMillis;
	private long mDurationInMillis;
	private int mExpectedTech;

	private int mNeededMonths;

	private float mProgress;

	private Laboratory mLeaderLab;

	private ProductDescription mDesc;

	private DialogWindow mDialog;

	public Laboratory(int index, DepartmentManager department_manager) {
		super(index, department_manager);
		mNumEmployees = 10;

		initResearchButton();
	}

	private void initResearchButton() {
		if(sTechnologyButton != null) return;

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		sTechnologyButton = cd.cast(PushButton.class, "default")
				.moveTo(140f, 110f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addEventListener(new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						final Laboratory leaderLab = (Laboratory) listener.getParent();

						if(leaderLab.mLeaderLab == null) {
							leaderLab.bringUpResearchDialog();
							return;
						}

						YesOrNoDialogData data = new YesOrNoDialogData();
						data.title = "선택";
						data.content = "연구를 정말로 중단하겠습니까? 중단하면 진행중인 연구성과는 폐기됩니다.";
						data.yesListener = new ChangeListener() {

							@Override
							public void onChanged(ChangeEvent event, Actor<?> target,
									Actor<?> listener) {
								List<Department> departmentList = leaderLab.mConnectedDepartmentList;
								int n = departmentList.size();
								for(int i = 0; i < n; i++) {
									Laboratory lab = (Laboratory) departmentList.get(i);
									if(lab.mDesc == leaderLab.mDesc) {
										lab.mLeaderLab = null;
										lab.mDesc = null;

										int linkIndex = leaderLab.mDepartmentManager.getLinkIndex(
												leaderLab.mIndex, lab.mIndex);
										leaderLab.mDepartmentManager.unlockLink(linkIndex);
									}
								}

								ProductGroup group = CorporationManager.getInstance()
										.getPlayerCorporation()
										.getProductGroupByCode(leaderLab.mDesc.code);
								group.researching = false;

								leaderLab.mLeaderLab = null;
								leaderLab.mDesc = null;

								leaderLab.mExpectedTech = 0;
								leaderLab.mNeededMonths = 0;

								// update를 위해
								leaderLab.mDepartmentManager.selectDepartment(leaderLab.mIndex);
							}
						};
						Utils.showYesOrNoDialog(getFloor().getStage(), "stopResearch", data);

					}
				});

		sResearchLabel = new SLabel(R.string.label_research_technology, fontTexture) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				Laboratory lab = (Laboratory) getParent().getParent();
				if(lab.mLeaderLab == null) {
					super.draw(batch, parentAlpha);
				}
			}
		};

		sStopLabel = new SLabel(R.string.label_stop_technology, fontTexture) {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				Laboratory lab = (Laboratory) getParent().getParent();
				if(lab.mLeaderLab == lab) {
					super.draw(batch, parentAlpha);
				}
			}
		};

		sTechnologyButton.addCell(sResearchLabel);
		sTechnologyButton.pack().setWidth(80f);
	}
	
	@Override
	protected double work() {
		if(mLeaderLab != this) return 0;

		GregorianCalendar calendar = Time.getInstance().getCalendar();

		long elapsedInMillis = mFinalDateInMillis - calendar.getTimeInMillis();

		// 연구 진행중
		if(elapsedInMillis >= 0) {
			mProgress = 1f - 1f * elapsedInMillis / mDurationInMillis;
			
		// 연구 완료
		} else {
			Corporation corp = mDepartmentManager.getBuilding().getCorporation();
			corp.getProductGroupByCode(mDesc.code).tech += mExpectedTech;
			updateDate();
			ProductManager.getInstance().updateMaxTech(mDesc.code, corp.getProductGroupByCode(mDesc.code).tech);
		}

		return 0;
	}

	@Override
	protected void onSelected() {
		clearChildren();

		if(mLeaderLab == null) {
			addChild(sTechnologyButton);
			sTechnologyButton.getCellList().get(0).setActor(sResearchLabel);

		} else if(mLeaderLab == this) {
			addChild(sTechnologyButton);
			sTechnologyButton.getCellList().get(0).setActor(sStopLabel);
		}
	}

	@Override
	protected void drawPanel(Batch batch, float parentAlpha) {
		super.drawPanel(batch, parentAlpha);

		if(mDesc != null) {
			ProductData data = ProductManager.getInstance().getProductDataByCode(mDesc.code);
			DLabel label = data.getNameLabel();
			
			label.southWest();
			label.moveTo(
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 60 / 2);
			label.draw(batch, parentAlpha);
			
			// 리더 연구부만 아래의 내용을 출력한다.
			if(mLeaderLab != this) return;
			
			Corporation corp = mDepartmentManager.getBuilding().getCorporation();
			
			sCountLabel.southWest();
			sCountLabel.moveTo(
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 100 / 2);
			int currTech = corp.getProductGroupByCode(mDesc.code).tech;
			int targetTech = currTech + mExpectedTech;
			sCountLabel.setText(currTech + "  >>  " + targetTech);
			sCountLabel.draw(batch, parentAlpha);
			
			sCountLabel.southWest();
			sCountLabel.moveTo(
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 135 / 2);
			sCountLabel.setText(mNeededMonths + "개월");
			sCountLabel.draw(batch, parentAlpha);
			
			float length = 80 * mProgress;
			batch.draw(StockDepartment.sDepartmentBrandBarRegion,
					DepartmentManager.sDepartmentRectangles[mIndex].left() + 10 / 2,
					DepartmentManager.sDepartmentRectangles[mIndex].top() + 150 / 2,
					length, StockDepartment.sDepartmentBrandBarRegion.getRegionWidth(), false,
					false, true);
		}
	}

	@Override
	protected void drawContent(Batch batch, float parentAlpha) {
		
		ProductData data = null;
		if(mDesc != null) {
			data = ProductManager.getInstance().getProductDataByCode(mDesc.code);
		}

		if(data == null) {
			batch.draw(sDepartmentEmptyRegion, 30 / 2, 50 / 2);
		} else {
			batch.draw(data.getImageRegion(), 30 / 2, 50 / 2);
		}

		batch.draw(sDepartmentContentBarRegion, 300 / 2, 50 / 2);

		if(data != null) {
			DLabel label = data.getNameLabel();
			label.south();
			label.moveTo(300 / 2 + 163 / 2, 50 / 2 + 30 / 2);
			label.draw(batch, parentAlpha);
		}

		// 하단 출력
		super.drawContent(batch, parentAlpha);
	}

	@Override
	public DepartmentType getDepartmentType() {
		return DepartmentType.LABORATORY;
	}

	private void bringUpResearchDialog() {
		if(mDialog != null) {
			// 이미 열린 상태라면 리턴
			if(mDialog.isVisible()) return;
		}

		List<ManufacturingDescription> descList = ProductManager.getInstance()
				.getMDescList();

		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		CastingDirector cd = CastingDirector.getInstance();

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		for(int i = 0; i < descList.size(); i++) {
			LayoutTable table = cd.cast(LayoutTable.class, "rnd_list_item", descList.get(i));
			table.setUserObject(descList.get(i));
			itemList.add(table);
		}

		Utils.sort(itemList);

		final ListBox list1 = cd.cast(ListBox.class, "default", itemList, HAlign.LEFT, 1f)
				.select(0);

		// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
		list1.getScroll().startScrollFade();

		itemList.clear();

		final int numTeams = mConnectedDepartmentList.size();

		int months = 3;
		for(int i = 0; i < sBasicExpectedTechs.length; i++) {
			LayoutTable table = new LayoutTable();
			table.left();
			
			table.col(0).cellPrefWidth(80f);
			table.col(1).expandX();
			
			table.addCell(new DLabel("" + months, fontTexture));
			
			months *= 2;
			
			table.addCell(new DLabel(""
					+ (int) (sBasicExpectedTechs[i] * sTeamPerformances[numTeams]), fontTexture));
			
			itemList.add(table);
		}

		final ListBox list2 = cd.cast(ListBox.class, "default", itemList, HAlign.LEFT, 1f)
				.setItemPadding(2f, 0f, 0f, 1f).select(0);

		// 스크롤바를 표시하여 현재의 위치에 대한 힌트를 준다.
		list2.getScroll().startScrollFade();

		PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "확인").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						
						Actor<?> item = list1.getSelectedItem();
						int index = list2.getSelectedIndex();
						
						ManufacturingDescription desc = (ManufacturingDescription) item
								.getUserObject();

						ProductGroup group = CorporationManager.getInstance()
								.getPlayerCorporation().getProductGroupByCode(desc.outputCode);
						if(group.researching) {
							MessageDialogData data = new MessageDialogData();
							data.title = "알림";
							data.content = "해당 제품은 이미 연구를 진행중입니다.";
							Utils.showMessageDialog(getFloor().getStage(),
									"bringUpResearchDialog", data);
							return;
						}

						mDesc = ProductManager.getInstance().getPDescByCode(
								desc.outputCode);

						List<Department> departmentList = mConnectedDepartmentList;
						int n = departmentList.size();

						// 검사
						for(int i = 0; i < n; i++) {
							Laboratory lab = (Laboratory) departmentList.get(i);
							if(lab.mDesc != null) {
								MessageDialogData data = new MessageDialogData();
								data.title = "알림";
								data.content = "연결된 부서 중에서 다른 연구를 이미 진행중인 부서가 있습니다.";
								Utils.showMessageDialog(getFloor().getStage(),
										"bringUpResearchDialog", data);
								return;
							}
						}

						for(int i = 0; i < n; i++) {
							Laboratory lab = (Laboratory) departmentList.get(i);
							lab.mLeaderLab = Laboratory.this;
							lab.mDesc = mDesc;

							int linkIndex = mDepartmentManager.getLinkIndex(Laboratory.this.mIndex,
									lab.mIndex);
							mDepartmentManager.lockLink(linkIndex);
						}

						mExpectedTech = (int) (sBasicExpectedTechs[index] * sTeamPerformances[numTeams]);

						int months = 3;
						for(int i = 0; i < index; i++) {
							months *= 2;
						}
						mNeededMonths = months;

						updateDate();

						mLeaderLab = Laboratory.this;

						// update하기 위해
						mDepartmentManager.selectDepartment(Laboratory.this.mIndex);

						group.researching = true;

						closeDialog();
					}
				});

		PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "취소").addEventListener(
				new ChangeListener() {

					@Override
					public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
						closeDialog();
					}
				});

		LayoutTable headTable1 = new LayoutTable();

		headTable1.col(0).cellPrefWidth(100f);
		headTable1.col(1).cellPrefWidth(70f);
		headTable1.col(2).cellPrefWidth(70f);

		headTable1.addCell(new DLabel("제품", fontTexture));
		headTable1.addCell(new DLabel("현재 기술", fontTexture));
		headTable1.addCell(new DLabel("최고 기술", fontTexture));

		LayoutTable headTable2 = new LayoutTable();

		headTable2.col(0).cellPrefWidth(80f);
		headTable2.col(1).cellPrefWidth(80f);

		headTable2.addCell(new DLabel("기간(월)", fontTexture));
		headTable2.addCell(new DLabel("예상 기술", fontTexture));

		mDialog = cd.cast(DialogWindow.class, "default")
				.setTitle(new DLabel("어떤 제품을 연구하시겠습니까?", fontTexture)).setModal(true)
				.addButton(button1).addButton(button2);

		LayoutTable table = mDialog.getContentTable();

		table.col(0).padRight(5f);

		table.addCell(headTable1);
		table.addCell(headTable2);
		table.row();
		table.addCell(list1).actorSize(240f, 150f);
		table.addCell(list2).actorSize(160f, 150f);

		mDialog.pack().moveCenterTo(320f, 200f);

		UIManager.getInstance().addChild(mDialog);
		mDialog.open();

		// mDialog.debugAll();

		// Core.APP.debug(mDialog.toString());
	}

	private void closeDialog() {
		if(mDialog == null) return;
		
		mDialog.close();
		mDialog.addAction(new Run(new Runnable() {

			@Override
			public void run() {
				UIManager.getInstance().removeChild(mDialog);
				mDialog.disposeAll();
				mDialog = null;
			}
		}).setStartOffset(mDialog.getAnimationDuration()));
	}

	private void updateDate() {
		GregorianCalendar calendar = Time.getInstance().getCalendar();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		mFinalDate = new GregorianCalendar(year, month, day);
		mFinalDate.add(Calendar.MONTH, mNeededMonths);
		mFinalDateInMillis = mFinalDate.getTimeInMillis();

		mDurationInMillis = mFinalDateInMillis - calendar.getTimeInMillis();
	}

	@Override
	protected boolean isModifiable() {
		if(mDesc != null) {
			MessageDialogData data = new MessageDialogData();
			data.title = "알림";
			data.content = "부서를 변경할 수 없습니다. 먼저 진행중인 연구를 중단하십시오.";
			Utils.showMessageDialog(getFloor().getStage(), "canChange", data);
			return false;
		}
		return true;
	}

}
