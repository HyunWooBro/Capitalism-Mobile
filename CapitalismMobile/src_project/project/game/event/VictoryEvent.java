package project.game.event;

import project.framework.Utils;
import project.framework.Utils.MessageDialogData;
import project.framework.Utils.YesOrNoDialogData;
import project.game.Time;
import project.game.corporation.CorporationManager;
import project.game.corporation.FinancialData;
import project.game.corporation.PlayerCorporation;
import project.menu.MenuMainScene;

import android.graphics.Paint;

import core.framework.Core;
import core.framework.graphics.Color4;
import core.scene.Director;
import core.scene.stage.Stage;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.transition.TransitionDelay;

/**
 * 게임의 승리조건을 관리하는 클래스
 * 
 * @author 김현우
 * 
 */
public class VictoryEvent extends Event {

	public static final int VC_NETPROFIT = 1 << 0;
	public static final int VC_MONEY = 1 << 1;
	public static final int VC_DEADLINE = 1 << 10;

	public int mCondition;

	public static double mAnnualNetprofit; // 목표 연간순이익
	public static double mMoney; // 목표 현금

	private boolean mCheckAfterDeadline;

	private Stage mStage;

	private boolean mWin;
	private boolean mLose;

	public VictoryEvent(Stage stage) {

		mStage = stage;

		mCondition |= VC_NETPROFIT;
		mCondition |= VC_DEADLINE;

		mAnnualNetprofit = 5000000;

		Paint p = new Paint();
		p.setTextSize(15);
		p.setColor(Color4.WHITE);

		// mPopupOkWindow = new Window(new Point(240/2, 300/2),
		// new Rect(240/2, 300/2, 240/2+400, 300/2+76),
		// Core.GRAPHICS.getBitmap(R.drawable.ui_message_popup)) {};
		{
			// LabelToRemove text = new LabelToRemove(new Rect(10, 10, 390, 40),
			// null, p);

			// text.setVisible(true);
			// text.setRenderRectEnabled(true);

			// mPopupOkWindow.Add(text);

			/*
			 * ButtonToRemove okButton = new ButtonToRemove(new PointF(168, 48),
			 * new RectF(168, 48, 231, 70), R.drawable.ui_message_popup_ok_up,
			 * true, true);
			 * 
			 * okButton.setString("확인"); okButton.setStringNormalPos(new
			 * Point(11, 17)); okButton.setStringTouchedPos(new Point(11, 18));
			 * okButton.setStringPushedPos(new Point(11, 18));
			 * okButton.setStringEnabled(true);
			 * 
			 * //okButton.setVisible(true);
			 * okButton.setTouchedBitmap(R.drawable.ui_message_popup_ok_down);
			 * okButton.setPushedBitmap(R.drawable.ui_message_popup_ok_down);
			 * 
			 * mPopupOkWindow.addChildActor(okButton);
			 */
		}

		// mPopupOkWindow.setVisible(true);
		// mPopupOkWindow.setMovable(true);
		// mPopupOkWindow.setBackGroundColor(Color4.argb(100, 0, 0, 0));
		// mPopupOkWindow.setBackGroundColorApplied(true);
		// mPopupOkWindow.setOutsideTouchable(false);

	}

	@Override
	public boolean checkCondition() {

		if(mCheckAfterDeadline) {
			if(Time.getInstance().getCalendar().compareTo(Time.getInstance().getDeadline()) != 1)
				return false;
		}

		if(mWin || mLose)
			return false;

		// 순이익 목표가 설정되었다면
		if((mCondition & VC_NETPROFIT) != 0) {

			FinancialData financialData = CorporationManager.getInstance().getPlayerCorporation()
					.getFinancialData();

			// 목표 순이익을 달성했다면 승리
			if(financialData.annualNetprofit >= mAnnualNetprofit) {
				// 여기서 토스트를 사용하면 에러가 난다.
				// Toast.makeText(Core.APP.getActivity(), "목표를 달성하였습니다.",
				// Toast.LENGTH_LONG).show();

				mWin = true;
				return true;
			}
		}

		// 최종기한이 설정되었다면
		if((mCondition & VC_DEADLINE) != 0) {

			// 목표를 달성하지 못하고 시간이 지나면 패배
			if(Time.getInstance().getCalendar().after(Time.getInstance().getDeadline())) {
				// 여기서 토스트를 사용하면 에러가 난다.
				// Toast.makeText(Core.APP.getActivity(), String.format("abc"),
				// Toast.LENGTH_SHORT).show();

				mLose = true;
				return true;

			}
		}

		return false;
	}

	public void onDraw() {
		// if(m_win && !m_is_message_shown)
		{
			// ((LabelToRemove)mPopupOkWindow.getWindowElementList().get(0)).setString("목표를 달성하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)");
			// mPopupOkWindow.Render(canvas);
			// Utility.RenderMessagePopup(canvas,
			// "목표를 달성하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)", true);
		}

		// if(m_lose && !m_is_message_shown)
		{
			// ((LabelToRemove)mPopupOkWindow.getWindowElementList().get(0)).setString("목표달성에 실패하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)");
			// mPopupOkWindow.Render(canvas);
			// Utility.RenderMessagePopup(canvas,
			// "목표달성에 실패하였습니다. 게임은 계속 진행할 수 있습니다. (목표 보고서 참조)", true);
		}
	}

	@Override
	public void fire() {

		if(mWin) {
			final YesOrNoDialogData data = new YesOrNoDialogData();
			data.title = "결과";
			data.content = "축하합니다!! 목표달성에 성공하였습니다. 게임을 계속 진행하겠습니까?";
			data.titleColor = Color4.LTRED4;
			data.noListener = new ChangeListener() {

				@Override
				public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
					Director.getInstance().changeScene(
							new TransitionDelay(250, new MenuMainScene()));
				}
			};
			// update에서 showMessageDiagog를 호출해서인지는 알 수 없지만
			// 출력이 제대로 되지 않아 다음과 같이 1 프레임 뒤에 출력하도록 했다.
			Core.APP.runOnGLThread(1, new Runnable() {

				@Override
				public void run() {
					Utils.showYesOrNoDialog(mStage, "victory", data);
				}
			});
		}

		if(mLose) {
			final MessageDialogData data = new MessageDialogData();
			data.title = "결과";
			data.content = "최종기한이 지났지만 목표달성에 실패하였습니다.";
			data.titleColor = Color4.LTRED4;
			data.okListener = new ChangeListener() {

				@Override
				public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
					Core.APP.runOnGLThread(30, new Runnable() {

						@Override
						public void run() {
							Director.getInstance().changeScene(
									new TransitionDelay(250, new MenuMainScene()));
						}
					});
				}
			};
			// update에서 showMessageDiagog를 호출해서인지는 알 수 없지만
			// 출력이 제대로 되지 않아 다음과 같이 1 프레임 뒤에 출력하도록 했다.
			Core.APP.runOnGLThread(1, new Runnable() {

				@Override
				public void run() {
					Utils.showMessageDialog(mStage, "victory", data);
				}
			});

		}

	}

	public boolean willCheckAfterDeadline() {
		return mCheckAfterDeadline;
	}

	public void setCheckAfterDeadline(boolean checkAfterDeadline) {
		mCheckAfterDeadline = checkAfterDeadline;
	}

	public int getCondition() {
		return mCondition;
	}

	public void addCondition(int condition) {
		mCondition |= condition;
	}

	public void removeCondition(int condition) {
		mCondition &= ~condition;
	}

	public void setCondition(int condition) {
		mCondition = condition;
	}

	/*
	 * public boolean onTouchEvent(MotionEvent event) { int x =
	 * 0;//ScreenToRemove.touchX((int)event.getX()); int y =
	 * 0;//ScreenToRemove.touchY((int)event.getY());
	 * 
	 * if(VictoryCondition.m_win && !VictoryCondition.m_is_message_shown) {
	 * if(mPopupOkWindow.handleTouchEvent(event, 1f, 1f)) {
	 * //if(mPopupOkWindow.getTouchedComponent() == 1) {
	 * //mPopupOkWindow.setTouchedComponent(-1);
	 * VictoryCondition.m_is_message_shown = true; }
	 * 
	 * return true; } }
	 * 
	 * if(VictoryCondition.m_lose && !VictoryCondition.m_is_message_shown) {
	 * if(mPopupOkWindow.handleTouchEvent(event, 1f, 1f)) {
	 * //if(mPopupOkWindow.getTouchedComponent() == 1) {
	 * //mPopupOkWindow.setTouchedComponent(-1);
	 * VictoryCondition.m_is_message_shown = true; }
	 * 
	 * return true; } }
	 * 
	 * return false; }
	 */

}
