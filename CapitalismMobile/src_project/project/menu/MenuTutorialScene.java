package project.menu;

import project.framework.Utils;

import android.view.KeyEvent;

import core.scene.Scene;

/**
 * 이 클래스 사용 안하고 바로 튜토리얼 할 수 있도록 하자.
 * 
 * @author 김현우
 */
public class MenuTutorialScene extends Scene<MenuAct> {

	/*
	 * private static ButtonToRemove sInfoButton;
	 * 
	 * private ButtonToRemove mItem1Button; private ButtonToRemove mItem2Button;
	 * private ButtonToRemove mItem3Button; private ButtonToRemove mItem4Button;
	 * private ButtonToRemove mItem5Button;
	 */

	public MenuTutorialScene() {
	}

	@Override
	protected void create() {

		/*
		 * sInfoButton = new ButtonToRemove(new PointF(25, 15),
		 * R.drawable.mainmenu_info_n, true, true)
		 * .setTouchedBitmap(R.drawable.mainmenu_info_t) .setFlashEnabled(true)
		 * .setNormalFlashAnimation(R.drawable.mainmenu_info_flash, 15, 7,
		 * false, true)
		 * .setTouchedFlashAnimation(R.drawable.mainmenu_info_flash, 15, 7,
		 * false, true);
		 * 
		 * mItem1Button = new ButtonToRemove(new PointF(430, 40),
		 * R.drawable.mainmenu_item1_n, true, true)
		 * .setTouchedBitmap(R.drawable.mainmenu_item1_t) .setFlashEnabled(true)
		 * .setNormalFlashAnimation(R.drawable.menu_item_flash_n, 30, 8, false,
		 * true) .setTouchedFlashAnimation(R.drawable.menu_item_flash_t, 30, 8,
		 * false, true);
		 * 
		 * mItem2Button = new ButtonToRemove(new PointF(410, 110),
		 * R.drawable.mainmenu_item2_n, true, true)
		 * .setTouchedBitmap(R.drawable.mainmenu_item2_t) .setFlashEnabled(true)
		 * .setNormalFlashAnimation(R.drawable.menu_item_flash_n, 30, 8, false,
		 * true) .setTouchedFlashAnimation(R.drawable.menu_item_flash_t, 30, 8,
		 * false, true);
		 * 
		 * mItem3Button = new ButtonToRemove(new PointF(390, 180),
		 * R.drawable.mainmenu_item3_n, true, true)
		 * .setTouchedBitmap(R.drawable.mainmenu_item3_t) .setFlashEnabled(true)
		 * .setNormalFlashAnimation(R.drawable.menu_item_flash_n, 30, 8, false,
		 * true) .setTouchedFlashAnimation(R.drawable.menu_item_flash_t, 30, 8,
		 * false, true);
		 * 
		 * mItem4Button = new ButtonToRemove(new PointF(370, 250),
		 * R.drawable.mainmenu_item4_n, true, true)
		 * .setTouchedBitmap(R.drawable.mainmenu_item4_t) .setFlashEnabled(true)
		 * .setNormalFlashAnimation(R.drawable.menu_item_flash_n, 30, 8, false,
		 * true) .setTouchedFlashAnimation(R.drawable.menu_item_flash_t, 30, 8,
		 * false, true);
		 * 
		 * mItem5Button = new ButtonToRemove(new PointF(350, 320),
		 * R.drawable.mainmenu_item5_n, true, true)
		 * .setTouchedBitmap(R.drawable.mainmenu_item5_t) .setFlashEnabled(true)
		 * .setNormalFlashAnimation(R.drawable.menu_item_flash_n, 30, 8, false,
		 * true) .setTouchedFlashAnimation(R.drawable.menu_item_flash_t, 30, 8,
		 * false, true);
		 * 
		 * sInfoButton.SetReplyTime(System.currentTimeMillis()-1500, 3500);
		 * mItem1Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
		 * mItem2Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
		 * mItem3Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
		 * mItem4Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
		 * mItem5Button.SetReplyTime(System.currentTimeMillis()-3000, 4500);
		 * 
		 * getStage().addFloor() .addActor(new Extra() {
		 * 
		 * @Override public void draw(Batch batch, float parentAlpha) {
		 * 
		 * 
		 * }
		 * 
		 * });
		 */

	}

	@Override
	public void update(long time) {

		/*
		 * sInfoButton.update(time);
		 * 
		 * mItem1Button.update(time); mItem2Button.update(time);
		 * mItem3Button.update(time); mItem4Button.update(time);
		 * mItem5Button.update(time);
		 */

	}

	@Override
	public void handleKeyEvent(KeyEvent event, int keyCode) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
			Utils.exit(getStage());
	}

}
