package project.game.ui;

import org.framework.R;

import project.framework.Utils;
import project.game.cell.CellManager;

import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import core.framework.Core;
import core.framework.graphics.texture.Texture;
import core.framework.graphics.texture.TextureManager;
import core.framework.graphics.texture.TextureRegion;
import core.scene.stage.actor.Actor;
import core.scene.stage.actor.CastingDirector;
import core.scene.stage.actor.event.ChangeEvent;
import core.scene.stage.actor.event.ChangeListener;
import core.scene.stage.actor.widget.table.button.ButtonGroup;
import core.scene.stage.actor.widget.table.button.PushButton;

public class CityIconButtons extends ButtonGroup {

	public enum CityIconType {
		NORMAL, 
		LAND_VALUE, 
		PROFIT, 
		TRANSACTION, 
	}

	private CityIconType mIconType = CityIconType.NORMAL;

	public CityIconButtons() {
		super(false, 10f);

		CastingDirector cd = CastingDirector.getInstance();

		TextureManager tm = Core.GRAPHICS.getTextureManager();

		Texture imageTexture = tm.getTexture(R.drawable.atlas);

		TextureRegion cityiconNormalUp = imageTexture.getTextureRegion("cityicon_normal_up");
		TextureRegion cityiconNormalDown = imageTexture.getTextureRegion("cityicon_normal_down");

		TextureRegion cityiconLandvalueUp = imageTexture.getTextureRegion("cityicon_landvalue_up");
		TextureRegion cityiconLandvalueDown = imageTexture
				.getTextureRegion("cityicon_landvalue_down");

		TextureRegion cityiconProfitUp = imageTexture.getTextureRegion("cityicon_profit_up");
		TextureRegion cityiconProfitDown = imageTexture.getTextureRegion("cityicon_profit_down");

		TextureRegion cityiconTransactionUp = imageTexture
				.getTextureRegion("cityicon_transaction_up");
		TextureRegion cityiconTransactionDown = imageTexture
				.getTextureRegion("cityicon_transaction_down");

		PushButton cityiconNormalButton = cd
				.cast(PushButton.class, "up_down_checked", cityiconNormalUp, null,
						cityiconNormalDown)
				.setAlpha(0f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addAction(
						Utils.createMoveInAction(0f, -40f, 1500, 400, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton cityiconLandvalueButton = cd
				.cast(PushButton.class, "up_down_checked", cityiconLandvalueUp, null,
						cityiconLandvalueDown)
				.setAlpha(0f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addAction(
						Utils.createMoveInAction(0f, -40f, 1600, 400, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton cityiconProfitButton = cd
				.cast(PushButton.class, "up_down_checked", cityiconProfitUp, null,
						cityiconProfitDown)
				.setAlpha(0f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addAction(
						Utils.createMoveInAction(0f, -40f, 1700, 400, new OvershootInterpolator(),
								new LinearInterpolator()));

		PushButton cityiconTransactionButton = cd
				.cast(PushButton.class, "up_down_checked", cityiconTransactionUp, null,
						cityiconTransactionDown)
				.setAlpha(0f)
				.setStartTouchAction(Utils.createButtonStartTouchAction())
				.setFinalTouchAction(Utils.createButtonFinalTouchAction())
				.addAction(
						Utils.createMoveInAction(0f, -40f, 1800, 400, new OvershootInterpolator(),
								new LinearInterpolator()));

		moveTo(255f, 45f);
		addButton(cityiconNormalButton);
		addButton(cityiconLandvalueButton);
		addButton(cityiconProfitButton);
		addButton(cityiconTransactionButton);
		pack();
		check(0);
		addEventListener(new ChangeListener() {

			@Override
			public void onChanged(ChangeEvent event, Actor<?> target, Actor<?> listener) {
				if(!event.isTargetActor())
					return;

				switch(getCheckedIndex()) {
					case 0:
						mIconType = CityIconType.NORMAL;
						Core.APP.showToast("도시 모드", Toast.LENGTH_SHORT);
						break;
					case 1:
						mIconType = CityIconType.LAND_VALUE;
						Core.APP.showToast("지가 모드", Toast.LENGTH_SHORT);
						break;
					case 2:
						mIconType = CityIconType.PROFIT;
						Core.APP.showToast("수익 모드", Toast.LENGTH_SHORT);
						break;
					case 3:
						mIconType = CityIconType.TRANSACTION;
						Core.APP.showToast("거래 모드", Toast.LENGTH_SHORT);
						break;
				}
			}
		});
	}

	public CityIconType getIconType() {
		return mIconType;
	}

	public void setIconType(CityIconType iconType) {
		mIconType = iconType;
	}

	@Override
	public Actor<?> contactSelf(float x, float y) {
		return null;
	}
}
