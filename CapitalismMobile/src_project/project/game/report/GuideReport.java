package project.game.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.framework.R;

import project.framework.Utils;
import project.game.product.ProductManager;
import project.game.product.ProductManager.ManufacturingDescription;
import project.game.product.ProductManager.ProductData;
import project.game.product.ProductManager.ProductDescription;

import android.util.SparseArray;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.framework.graphics.texture.NinePatch;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.event.GestureTouchListener;
import core.scene.stage.actor.event.TouchEvent;
import core.scene.stage.actor.event.TouchListener;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.box.ListBox;
import core.scene.stage.actor.widget.label.DLabel;
import core.scene.stage.actor.widget.table.LayoutTable;
import core.scene.stage.actor.widget.table.Table;
import core.scene.stage.actor.widget.table.TableCell;
import core.scene.stage.actor.widget.table.button.PushButton;
import core.scene.stage.actor.widget.utils.Align.HAlign;
import core.utils.Disposable;

public class GuideReport extends BaseReport {

	private PushButton mButton;

	public GuideReport() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();
		Texture fontTexture = tm.getTexture(R.drawable.font);

		List<Actor<?>> itemList = new ArrayList<Actor<?>>();
		itemList.add(new DLabel("제조업 가이드", fontTexture).setUserObject(new ManufactureGuideReport()));
		// itemList.add(new DLabel("농업 가이드", fontTexture));
		// itemList.add(new DLabel("부서 가이드", fontTexture));

		init(itemList);
	}

	@Override
	public PushButton getMenuButton() {
		if(mButton == null) {
			mButton = CastingDirector.getInstance().cast(PushButton.class, "static_text",
					R.string.label_guide_report);
			mButton.setColor(Color4.LTYELLOW4);
		}
		return mButton;
	}

	@Override
	public Actor<?> getContent() {
		return this;
	}

	private static class ManufactureGuideReport extends Table<ManufactureGuideReport> implements Report {

		/** 현재 출력되고 있는 {@link ManufacturingDescription} */
		private ManufacturingDescription mDesc;

		private List<Actor<?>> mItemList = new ArrayList<Actor<?>>();

		private int mTypeIndex;

		private Map<String, List<String>> mTypeToOutputCodeMap = new HashMap<String, List<String>>();

		private ListBox mTypeListBox;
		private ListBox mDetailListBox;

		private TableCell mInputConnectorCell;

		private LayoutTable mInputTable1;
		private LayoutTable mInputTable2;
		private LayoutTable mInputTable3;
		private LayoutTable mOutputInfoTable;
		private LayoutTable mOutputTable;

		private Image mInputConnectorImage1;
		private Image mInputConnectorImage2;
		private Image mInputConnectorImage3;

		private int mDetailIndex;

		private SparseArray<List<Actor<?>>> mOutputListMap = new SparseArray<List<Actor<?>>>();

		public ManufactureGuideReport() {

			CastingDirector cd = CastingDirector.getInstance();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);
			Texture manufactureTexture = tm.getTexture(R.drawable.atlas_report_manufacture);

			// 인풋 및 아웃풋 테이블 생성
			createTables();

			// 인풋 연결선 이미지
			mInputConnectorImage1 = new Image(
					manufactureTexture.getTextureRegion("report_manufacture_input_connector1"));
			mInputConnectorImage2 = new Image(
					manufactureTexture.getTextureRegion("report_manufacture_input_connector2"));
			mInputConnectorImage3 = new Image(
					manufactureTexture.getTextureRegion("report_manufacture_input_connector3"));

			// 아웃풋 연결선 이미지
			Image outputConnector = new Image(
					manufactureTexture.getTextureRegion("report_manufacture_output_connector"));

			List<Actor<?>> itemList = mItemList;

			// 제품 종류를 얻어온다.
			List<String> typeList = ProductManager.getInstance().getProductTypeList();
			int n = typeList.size();
			for(int i = 0; i < n; i++) {
				String type = typeList.get(i);
				List<ManufacturingDescription> descList = ProductManager.getInstance()
						.getMDescListByType(type);
				if(descList == null) continue;

				String name = ProductManager.getInstance().getNameByType(type);

				DLabel label = new DLabel(name, fontTexture).setTag(name).setUserObject(type);

				itemList.add(label);
			}

			// 태그를 기준으로 정렬
			Utils.sort(itemList);

			mTypeListBox = cd.cast(ListBox.class, "default", itemList, HAlign.LEFT, 0f)
					.setItemPadding(2f, 5f, 0f, 1f).select(0)
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							if(event.isTargetActor()) {
								reset();
								ListBox box = (ListBox) listener;
								mTypeIndex = box.getSelectedIndex();
								showOutputList(mTypeIndex);
							}
						}

					});

			mDetailListBox = cd.cast(ListBox.class, "default", new ArrayList<Actor<?>>(), HAlign.LEFT, 0f)
					.setItemPadding(2f, 5f, 0f, 1f).addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							if(event.isTargetActor()) {
								ListBox box = (ListBox) listener;

								// 중간에 set할 때 clear되기 때문에 ChangeEvent가 발생한다.
								// 비어있는 경우
								// 에러가 발생하기 때문에 무시한다.
								if(box.getItemList().isEmpty()) return;

								mDetailIndex = box.getSelectedIndex();

								String type = (String) mTypeListBox.getSelectedItem()
										.getUserObject();

								List<ManufacturingDescription> descList = ProductManager
										.getInstance().getMDescListByType(type);

								mDesc = descList.get(mDetailIndex);
								rebuildGuide(mDesc);
							}
						}

					}).addEventListener(new TouchListener() {
						@Override
						public void onTouch(TouchEvent event, float x, float y, Actor<?> listener) {
							Core.APP.debug(listener.toString());
						}
					});

			LayoutTable table = new LayoutTable();

			table.col(0).padRight(5f);

			table.addCell(new DLabel("제품 종류", fontTexture));
			table.addCell(new DLabel("출력 제품", fontTexture));
			table.row();
			table.addCell(mTypeListBox).actorSize(100f, 110f);
			table.addCell(mDetailListBox).actorSize(100f, 110f);

			col(0).actorSize(155f, 100f);
			col(4).actorSize(155f, 100f);

			addCell(mInputTable1);
			mInputConnectorCell = addCell(mInputConnectorImage3).actorSize(20f, 200f).rowSpan(3)
					.padLeft(-7f);
			addCell(mOutputInfoTable).actorSize(130f, 100f).top().rowSpan(2).padTop(41f);
			addCell(outputConnector).actorSize(20f, 8f).top().rowSpan(2).padLeft(-7f).padTop(88f);
			addCell(mOutputTable).top().rowSpan(2).padTop(41f);
			row().padTop(5f);
			addCell(mInputTable2);
			row().padTop(5f);
			addCell(mInputTable3);
			addCell(table).colSpan(3).padTop(-45f);

			// debugAll();
		}

		private void reset() {
			mDetailIndex = 0;
		}

		private void createTables() {

			CastingDirector cd = CastingDirector.getInstance();

			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture imageTexture = tm.getTexture(R.drawable.atlas);
			Texture fontTexture = tm.getTexture(R.drawable.font);
			Texture manufactureTexture = tm.getTexture(R.drawable.atlas_report_manufacture);

			NinePatch manufacturePatch = new NinePatch(
					manufactureTexture.getTextureRegion("report_manufacture_guide"), 9, 9, 13, 12);

			PushButton button1 = cd.cast(PushButton.class, "dynamic_text", "더 보기")
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							if(event.isTargetActor()) {
								showMore((ProductDescription) mInputTable1.getUserObject());
							}
						}
					});
			((DLabel) button1.getCell(0).getActor()).setDisposable(false);

			mInputTable1 = new LayoutTable().setDrawable(Drawable.newDrawable(manufacturePatch));

			mInputTable1.col(1).expandX();

			mInputTable1.addCell().size(70f, 70f).rowSpan(4);
			mInputTable1.addCell();
			mInputTable1.row();
			mInputTable1.addCell();
			mInputTable1.row();
			mInputTable1.addCell();
			mInputTable1.row();
			mInputTable1.addCell(button1).width(50f);

			PushButton button2 = cd.cast(PushButton.class, "dynamic_text", "더 보기")
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							if(event.isTargetActor()) {
								showMore((ProductDescription) mInputTable2.getUserObject());
							}
						}
					});
			((DLabel) button2.getCell(0).getActor()).setDisposable(false);

			mInputTable2 = new LayoutTable().setDrawable(Drawable.newDrawable(manufacturePatch));

			mInputTable2.col(1).expandX();

			mInputTable2.addCell().size(70f, 70f).rowSpan(4);
			mInputTable2.addCell();
			mInputTable2.row();
			mInputTable2.addCell();
			mInputTable2.row();
			mInputTable2.addCell();
			mInputTable2.row();
			mInputTable2.addCell(button2).width(50f);

			PushButton button3 = cd.cast(PushButton.class, "dynamic_text", "더 보기")
					.addEventListener(new ChangeListener() {

						@Override
						public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
							if(event.isTargetActor()) {
								showMore((ProductDescription) mInputTable3.getUserObject());
							}
						}
					});
			((DLabel) button3.getCell(0).getActor()).setDisposable(false);

			mInputTable3 = new LayoutTable().setDrawable(Drawable.newDrawable(manufacturePatch));

			mInputTable3.col(1).expandX();

			mInputTable3.addCell().size(70f, 70f).rowSpan(4);
			mInputTable3.addCell();
			mInputTable3.row();
			mInputTable3.addCell();
			mInputTable3.row();
			mInputTable3.addCell();
			mInputTable3.row();
			mInputTable3.addCell(button3).width(50f);

			mOutputInfoTable = new LayoutTable()
					.setDrawable(Drawable.newDrawable(manufacturePatch));

			mOutputInfoTable.all().left();

			mOutputInfoTable.addCell();
			mOutputInfoTable.row();
			mOutputInfoTable.addCell();
			mOutputInfoTable.row();
			mOutputInfoTable.addCell().padTop(5f);
			mOutputInfoTable.row();
			mOutputInfoTable.addCell();

			mOutputTable = new LayoutTable().setDrawable(Drawable.newDrawable(manufacturePatch));

			mOutputTable.col(1).expandX();

			mOutputTable.addCell().size(70f, 70f).rowSpan(3);
			mOutputTable.addCell();
			mOutputTable.row();
			mOutputTable.addCell();
			mOutputTable.row();
			mOutputTable.addCell().padTop(20f);

		}

		private void rebuildGuide(ManufacturingDescription description) {
			TextureManager tm = Core.GRAPHICS.getTextureManager();

			Texture fontTexture = tm.getTexture(R.drawable.font);

			ProductData data;

			int numInputs = description.getNumInputs();

			mInputTable2.setVisible(false);
			mInputTable3.setVisible(false);

			int total = description.inputQuality1 + description.inputQuality2
					+ description.inputQuality3;

			Image inputImage;

			mInputTable1.disposeAll();
			data = ProductManager.getInstance().getProductDataByCode(description.inputCode1);
			inputImage = new Image(data.getImageRegion());
			mInputTable1.getCellList().get(0).setActor(inputImage);
			mInputTable1
					.getCellList()
					.get(1)
					.setActor(new DLabel(data.desc.name, fontTexture, Utils.sOutlineWhite12)
									.setColor(Color4.GREEN4));
			createOutputLink(inputImage, data.desc);
			mInputTable1
					.getCellList()
					.get(2)
					.setActor(new DLabel("" + description.inputQuantity1, fontTexture,
									Utils.sOutlineWhite12));
			mInputTable1
					.getCellList()
					.get(3)
					.setActor(new DLabel("품질 : " + description.inputQuality1 + "/" + total,
									fontTexture, Utils.sOutlineWhite12));
			decideMoreState(mInputTable1.getCellList().get(4).getActor(), data.desc);
			mInputTable1.setUserObject(data.desc);

			if(numInputs == 1) {
				setInputConnectorImage(mInputConnectorImage1);
			}
			if(numInputs > 1) {
				rebuildInputTable2(description, total);
			}
			if(numInputs > 2) {
				rebuildInputTable3(description, total);
			}

			mOutputTable.disposeAll();
			data = ProductManager.getInstance().getProductDataByCode(description.outputCode);
			inputImage = new Image(data.getImageRegion());
			mOutputTable.getCellList().get(0).setActor(inputImage);
			mOutputTable
					.getCellList()
					.get(1)
					.setActor(
							new DLabel(data.desc.name, fontTexture, Utils.sOutlineWhite12)
									.setColor(Color4.GREEN4));
			String typeName = ProductManager.getInstance().getNameByType(data.desc.type);
			mOutputTable
					.getCellList()
					.get(2)
					.setActor(new DLabel("" + description.outputQuantity, fontTexture,
									Utils.sOutlineWhite12));
			mOutputTable
					.getCellList()
					.get(3)
					.setActor(new DLabel(typeName, fontTexture, Utils.sOutlineWhite12)
									.setColor(Color4.YELLOW4));
			createInputLink(inputImage, data.desc);

			mOutputInfoTable.disposeAll();
			mOutputInfoTable
					.getCellList()
					.get(0)
					.setActor(new DLabel("원료 품질 : " + total + "/100", fontTexture,
									Utils.sOutlineWhite12));
			mOutputInfoTable
					.getCellList()
					.get(1)
					.setActor(new DLabel("생산 기술 : " + (100 - total) + "/100", fontTexture,
									Utils.sOutlineWhite12));
			mOutputInfoTable.getCellList().get(2)
					.setActor(new DLabel("기술 발견 : 예", fontTexture, Utils.sOutlineWhite12));
			mOutputInfoTable.getCellList().get(3)
					.setActor(new DLabel("기술 개발 : 예", fontTexture, Utils.sOutlineWhite12));
		}

		private void rebuildInputTable2(ManufacturingDescription description, int total) {
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mInputTable2.disposeAll();
			ProductData data = ProductManager.getInstance().getProductDataByCode(description.inputCode2);
			Image inputImage = new Image(data.getImageRegion());
			mInputTable2.getCellList().get(0).setActor(inputImage);
			mInputTable2
					.getCellList()
					.get(1)
					.setActor(new DLabel(data.desc.name, fontTexture, Utils.sOutlineWhite12)
									.setColor(Color4.GREEN4));
			createOutputLink(inputImage, data.desc);
			mInputTable2
					.getCellList()
					.get(2)
					.setActor(new DLabel("" + description.inputQuantity2, fontTexture,
									Utils.sOutlineWhite12));
			mInputTable2
					.getCellList()
					.get(3)
					.setActor(new DLabel("품질 : " + description.inputQuality2 + "/" + total,
									fontTexture, Utils.sOutlineWhite12));
			decideMoreState(mInputTable2.getCellList().get(4).getActor(), data.desc);
			mInputTable2.setUserObject(data.desc);

			mInputTable2.setVisible(true);
			setInputConnectorImage(mInputConnectorImage2);
		}

		private void rebuildInputTable3(ManufacturingDescription description, int total) {
			TextureManager tm = Core.GRAPHICS.getTextureManager();
			Texture fontTexture = tm.getTexture(R.drawable.font);

			mInputTable3.disposeAll();
			ProductData data = ProductManager.getInstance().getProductDataByCode(description.inputCode3);
			Image inputImage = new Image(data.getImageRegion());
			mInputTable3.getCellList().get(0).setActor(inputImage);
			mInputTable3
					.getCellList()
					.get(1)
					.setActor(
							new DLabel(data.desc.name, fontTexture, Utils.sOutlineWhite12)
									.setColor(Color4.GREEN4));
			createOutputLink(inputImage, data.desc);
			mInputTable3
					.getCellList()
					.get(2)
					.setActor(new DLabel("" + description.inputQuantity3, fontTexture,
									Utils.sOutlineWhite12));
			mInputTable3
					.getCellList()
					.get(3)
					.setActor(new DLabel("품질 : " + description.inputQuality3 + "/" + total,
									fontTexture, Utils.sOutlineWhite12));
			decideMoreState(mInputTable3.getCellList().get(4).getActor(), data.desc);
			mInputTable3.setUserObject(data.desc);

			mInputTable3.setVisible(true);
			setInputConnectorImage(mInputConnectorImage3);
		}

		private void setInputConnectorImage(Image image) {
			mInputConnectorCell.setActor(image);
			// 연결커넥터 이미지가 가장 앞에 출력되도록 한다.
			image.toFront();
		}

		/** 인풋 제품이 아웃풋 제품이 될 수 있다면 링크를 만든다 */
		private void createOutputLink(Actor<?> content, final ProductDescription description) {
			final ManufacturingDescription desc = ProductManager.getInstance()
					.getMDescByOutputCode(description.code);
			if(desc == null) return;

			createLink(content, desc.outputCode, description);
		}

		/** 아웃풋 제품이 인풋 제품이 될 수 있다면 링크를 만든다 */
		private void createInputLink(Actor<?> content, final ProductDescription description) {
			final List<ManufacturingDescription> descList = ProductManager.getInstance()
					.getMDescListByInputCode(description.code);
			if(descList == null) return;

			createLink(content, descList.get(0).outputCode, description);
		}

		private void createLink(Actor<?> content, final String outputCode,
				final ProductDescription description) {
			content.addEventListener(new GestureTouchListener() {
				@Override
				public void onSingleTapUp(TouchEvent event, float x, float y, Actor<?> listener) {

					String type = ProductManager.getInstance().getPDescByCode(outputCode).type;

					List<Actor<?>> itemList = mTypeListBox.getItemList();
					for(int i = 0; i < itemList.size(); i++) {
						if(itemList.get(i).getUserObject().equals(type)) {
							mTypeListBox.select(i);
							break;
						}
					}

					List<ManufacturingDescription> dList = ProductManager.getInstance()
							.getMDescListByType(type);

					for(int j = 0; j < dList.size(); j++) {
						ManufacturingDescription d = dList.get(j);
						ProductDescription output = ProductManager.getInstance()
								.getPDescByCode(d.outputCode);
						if(description == output) {
							mDetailListBox.select(j);
							break;
						}
					}
				}
			});
		}

		/** '더 보기' 버튼의 활성화 여부를 결정한다 */
		private void decideMoreState(Actor<?> content, ProductDescription description) {

			PushButton button = (PushButton) content;

			List<ManufacturingDescription> descList = ProductManager.getInstance()
					.getMDescListByInputCode(description.code);
			if(descList.size() == 1) {
				button.setDisabled(true);
				button.getChildList().get(0).setColor(Color4.GRAY4);
				button.setColor(Color4.GRAY4);
			} else {
				button.setDisabled(false);
				button.getChildList().get(0).setColor(Color4.WHITE4);
				button.setColor(Color4.WHITE4);
			}
		}

		/** 해당 제품을 인풋으로 하는 또 다른 제품을 출력한다 */
		private void showMore(ProductDescription description) {

			List<ManufacturingDescription> descList = ProductManager.getInstance()
					.getMDescListByInputCode(description.code);

			int index = descList.indexOf(mDesc) + 1;
			if(index == descList.size()) index = 0;

			String outputCode = descList.get(index).outputCode;
			ManufacturingDescription desc = ProductManager.getInstance()
					.getMDescByOutputCode(outputCode);
			String type = desc.type;

			List<Actor<?>> itemList = mTypeListBox.getItemList();
			for(int i = 0; i < itemList.size(); i++) {
				if(itemList.get(i).getUserObject().equals(type)) {
					mTypeListBox.select(i);
					break;
				}
			}

			ProductDescription pdesc = ProductManager.getInstance().getPDescByCode(
					desc.outputCode);

			List<ManufacturingDescription> dList = ProductManager.getInstance()
					.getMDescListByType(type);

			for(int j = 0; j < dList.size(); j++) {
				ManufacturingDescription d = dList.get(j);
				ProductDescription output = ProductManager.getInstance().getPDescByCode(
						d.outputCode);
				if(pdesc == output) {
					mDetailListBox.select(j);
					break;
				}
			}

		}

		/** 현재 선택된 제품 종류에 속하는 출력 제품의 리스트를 출력한다 */
		private void showOutputList(int index) {

			List<Actor<?>> itemList = mOutputListMap.get(index);
			if(itemList == null) {
				TextureManager tm = Core.GRAPHICS.getTextureManager();
				Texture fontTexture = tm.getTexture(R.drawable.font);

				itemList = new ArrayList<Actor<?>>();

				String type = (String) mItemList.get(mTypeIndex).getUserObject();

				List<ManufacturingDescription> descList = ProductManager.getInstance()
						.getMDescListByType(type);

				for(int j = 0; j < descList.size(); j++) {
					ManufacturingDescription desc = descList.get(j);
					ProductDescription output = ProductManager.getInstance().getPDescByCode(
							desc.outputCode);
					itemList.add(new DLabel(output.name, fontTexture).setTag(output.name));
				}

				Utils.sort(itemList);

				mOutputListMap.append(index, itemList);
			}

			mDetailListBox.set(itemList).select(mDetailIndex);
		}

		@Override
		public void disposeAll() {
			for(int i = 0; i < mOutputListMap.size(); i++) {
				List<Actor<?>> itemList = mOutputListMap.valueAt(i);
				int m = itemList.size();
				for(int j = 0; j < m; j++) {
					Actor<?> item = itemList.get(j);
					((Disposable) item).dispose();
				}
			}

			((DLabel) ((PushButton) mInputTable1.getCell(4).getActor()).getCell(0).getActor())
					.setDisposable(true);
			((DLabel) ((PushButton) mInputTable2.getCell(4).getActor()).getCell(0).getActor())
					.setDisposable(true);
			((DLabel) ((PushButton) mInputTable3.getCell(4).getActor()).getCell(0).getActor())
					.setDisposable(true);

			super.disposeAll();
		}

		@Override
		public void onShow() {
			showOutputList(mTypeIndex);
		}

		@Override
		public void onHide() {
		}

		@Override
		public PushButton getMenuButton() {
			return null;
		}

		@Override
		public Actor<?> getContent() {
			return null;
		}

	}

}
