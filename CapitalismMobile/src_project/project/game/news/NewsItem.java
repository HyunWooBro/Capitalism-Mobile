package project.game.news;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.framework.R;

import project.game.corporation.PlayerCorporation;
import project.game.news.NewsManager.NewsIconType;

import android.graphics.Point;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.math.Vector2;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.DLabel;

/**
 * 뉴스 아이템 클래스
 * 
 * @author 김현우
 */
public class NewsItem {

	public enum NewsType {
		CONSTRUCTION, 
		ALERT, 
	}

	/*package*/ GregorianCalendar mCalendar; // 뉴스 발생 날짜
	/*package*/ String mContent; // 뉴스 내용
	/*package*/ Vector2 mLocation; // 뉴스 발생 위치

	/*package*/ Image[] mIconboxImages;
	/*package*/ NewsIconType mIconType;
	/*package*/ NewsType mNewsType; // 뉴스 종류
	/*package*/ PlayerCorporation mPlayer; // 뉴스의 주체(중립인 경우 null)
	/*package*/ long mLife; // 뉴스 아이콘알림 남은 시간

	/*package*/ DLabel mLabel;

	/*package*/ int mCityIndex;

	public NewsItem(NewsType type, Point location) {

	}

	public NewsItem(GregorianCalendar calendar, String content, Vector2 location) {

		mCalendar = calendar;
		mContent = content;
		mLocation = location;

		mIconType = NewsIconType.CONSTRUCTION;

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture fontTexture = tm.getTexture(R.drawable.font_etc);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		mLabel = new DLabel(String.format("%d년 %2d월 %2d일 " + content, year, month + 1, day),
				fontTexture);

		mLife = 10000;
	}

	public GregorianCalendar get_calendar() {
		return mCalendar;
	}

	public String get_content() {
		return mContent;
	}

	public Vector2 get_location() {
		return mLocation;
	}

	public long getLife() {
		return mLife;
	}

}
