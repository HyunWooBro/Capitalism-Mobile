package project.game.news;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.framework.R;

import project.game.building.department.DepartmentManager;
import project.game.cell.CellManager;
import project.game.city.CityListener;

import core.framework.Core;
import core.framework.graphics.ShapeRenderer;
import core.framework.graphics.ShapeRenderer.ShapeType;
import core.framework.graphics.batch.Batch;
import core.framework.graphics.texture.Animation;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.math.Rectangle;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.Group;
import core.scene.stage.actor.widget.Image;
import core.utils.Disposable;

/**
 * 게임 내의 다양한 뉴스를 관리하고 보여주는 클래스
 * 
 * @author 김현우
 */
public class NewsManager extends Group<NewsManager> implements CityListener, Disposable {

	public enum NewsIconType {
		CONSTRUCTION, 
		RED_EXCLMATION, 
	}

	// 뉴스아이템을 관리하는 리스트
	private List<NewsItem> mNewsItemList = new ArrayList<NewsItem>();

	private Map<NewsIconType, Image[]> mNewsIconTypeToImagesMap = new HashMap<NewsIconType, Image[]>();

	private static final int MAX_NEWSITEM_SHOWN = 3; // 화면에 출력하는 최대 뉴스아이템 개수

	private Rectangle mScreenRect = new Rectangle();

	private float mWidth;
	private float mHeight;

	private float mBottomUIHeight;

	private int mCurrentCityIndex;

	/** 싱글턴 인스턴스 */
	private volatile static NewsManager sInstance;

	private NewsManager() {
	}

	public static NewsManager getInstance() {
		if(sInstance == null) {
			synchronized(NewsManager.class) {
				if(sInstance == null)
					sInstance = new NewsManager();
			}
		}
		return sInstance;
	}

	public void init() {
		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);
		Texture iconboxTexture = tm.getTexture(R.drawable.iconbox);

		mBottomUIHeight = imageTexture.getTextureRegion("ui_bottom").getRegionHeight();

		Image[] images = null;

		Animation iconboxConstructionAnimation = new Animation(200, Arrays.asList(
				iconboxTexture.getTextureRegion("iconbox_construction_01"),
				iconboxTexture.getTextureRegion("iconbox_construction_02")));

		Animation iconboxConstructionTAnimation = new Animation(200, Arrays.asList(
				iconboxTexture.getTextureRegion("iconbox_construction_t_01"),
				iconboxTexture.getTextureRegion("iconbox_construction_t_02")));

		images = new Image[2];
		images[0] = new Image(iconboxConstructionAnimation).pivotTo(0f, 0f);
		images[1] = new Image(iconboxConstructionTAnimation).pivotTo(0f, 0f);
		addChild(images[0]);
		addChild(images[1]);

		mNewsIconTypeToImagesMap.put(NewsIconType.CONSTRUCTION, images);

		Animation iconboxRedExclamationAnimation = new Animation(200, Arrays.asList(
				iconboxTexture.getTextureRegion("iconbox_red_exclamation_01"),
				iconboxTexture.getTextureRegion("iconbox_red_exclamation_01")));

		Animation iconboxRedExclamationTAnimation = new Animation(200, Arrays.asList(
				iconboxTexture.getTextureRegion("iconbox_red_exclamation_t_01"),
				iconboxTexture.getTextureRegion("iconbox_red_exclamation_t_02")));

		images = new Image[2];
		images[0] = new Image(iconboxRedExclamationAnimation).pivotTo(0f, 0f);
		images[1] = new Image(iconboxRedExclamationTAnimation).pivotTo(0f, 0f);
		addChild(images[0]);
		addChild(images[1]);

		mNewsIconTypeToImagesMap.put(NewsIconType.RED_EXCLMATION, images);

		mWidth = 41f;
		mHeight = 40f;

	}

	/** 최근 뉴스를 리스트 선두에 삽입 */
	public void Insert(NewsItem item) {
		item.mIconboxImages = mNewsIconTypeToImagesMap.get(item.mIconType);
		item.mCityIndex = mCurrentCityIndex;
		mNewsItemList.add(0, item);
	}

	@Override
	public void update(long time) {
		super.update(time);

		List<NewsItem> newsItemList = mNewsItemList;
		int n = newsItemList.size();
		for(int i = 0; i < n; i++) {
			NewsItem item = newsItemList.get(i);
			if(item.mLife > 0)
				item.mLife -= Core.GRAPHICS.getDeltaTime();
		}
	}

	@Override
	public Actor<?> contact(float x, float y) {
		return null;
	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		drawNewsHeadline(batch, parentAlpha);
		drawNewsIcon(batch, parentAlpha);
	}

	private void drawNewsHeadline(Batch batch, float parentAlpha) {
		// 보여질 뉴스 아이템 개수
		int num_news_shown = mNewsItemList.size();
		if(num_news_shown == 0)
			return;
		if(num_news_shown > MAX_NEWSITEM_SHOWN)
			num_news_shown = MAX_NEWSITEM_SHOWN;

		batch.setProjectionMatrix(Core.GRAPHICS.getDefaultCamera().getCombinedMatrix());

		ShapeRenderer renderer = DepartmentManager.sShapeRenderer;

		int year = 0;
		int month = 0;
		int day = 0;

		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.begin(ShapeType.FILLED);
		renderer.setColor(100, 100, 100, 200);

		for(int i = 0; i < num_news_shown; i++) {
			NewsItem item = mNewsItemList.get(num_news_shown - i - 1);

			year = item.get_calendar().get(Calendar.YEAR);
			month = item.get_calendar().get(Calendar.MONTH);
			day = item.get_calendar().get(Calendar.DAY_OF_MONTH);

			renderer.drawRect(450 / 2, 720 / 2 + 5 / 2 - 30 / 2 - 30 / 2 * i, 850 / 2, 30 / 2);

			item.mLabel.moveTo(500 / 2, 720 / 2 - 30 / 2 * i);
			item.mLabel.draw(batch, parentAlpha);

			// drawer.drawText(String.format("%d년 %2d월 %2d일 ", year, month+1,
			// day)
			// + mNewsItemList.get(num_news_shown-i-1).get_content(), 500/2,
			// 720/2-30/2*i, p);
		}

		renderer.end();

		batch.setProjectionMatrix(getFloor().getCamera().getCombinedMatrix());
	}

	private void drawNewsIcon(Batch batch, float parentAlpha) {
		float zoom = CellManager.getInstance().getZoom();

		Rectangle screenRect = mScreenRect;
		screenRect.set(getFloor().getCamera().getVisibleRectangle());
		screenRect.top(screenRect.y + mHeight * zoom);
		screenRect.width -= mWidth * zoom;
		screenRect.height -= mBottomUIHeight * zoom;

		// 각 뉴스 아이템에 대해
		List<NewsItem> newsItemList = mNewsItemList;
		int n = newsItemList.size();
		for(int i = 0; i < n; i++) {
			NewsItem item = newsItemList.get(i);
			if(item.mCityIndex != mCurrentCityIndex)
				continue;
			if(item.mIconType == null)
				continue;
			if(item.mLife <= 0)
				continue;

			float x = item.mLocation.x;
			float y = item.mLocation.y;

			Image[] images = item.mIconboxImages;

			// 뉴스 아이콘이 화면 내부에 그려진다면
			if(screenRect.contains(x, y)) {
				images[0].scaleTo(zoom);
				images[0].moveTo(x, y - mHeight * zoom);
				images[0].draw(batch, parentAlpha);
			} else { // 뉴스 아이콘이 화면 외부에 그려진다면
				if(screenRect.left() > x)
					x = screenRect.left();
				if(screenRect.right() < x)
					x = screenRect.right();
				if(screenRect.top() > y)
					y = screenRect.top();
				if(screenRect.bottom() < y)
					y = screenRect.bottom();

				images[1].scaleTo(zoom);
				images[1].moveTo(x, y - mHeight * zoom);
				images[1].draw(batch, parentAlpha);
			}
		}
	}

	@Override
	public void onCityChanged(int index) {
		mCurrentCityIndex = index;
	}

	@Override
	public void dispose() {
		sInstance = null;
	}

}
