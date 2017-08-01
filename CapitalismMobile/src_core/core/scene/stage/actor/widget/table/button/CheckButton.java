package core.scene.stage.actor.widget.table.button;

import core.scene.stage.actor.action.Action;
import core.scene.stage.actor.drawable.Drawable;
import core.scene.stage.actor.widget.Image;
import core.scene.stage.actor.widget.label.Label;
import core.scene.stage.actor.widget.table.TableCell;

public class CheckButton extends Button<CheckButton> {
	
	private Label<?> mLabel;
	private TableCell mLabelCell;
	
	private Image mImage;
	private TableCell mImageCell;
	
	private Action mCheckOnAction;
	private Action mCheckOffAction;
	
	public CheckButton(CheckButtonCostume costume) {
		this(costume, null);
	}
	
	/** label은 null이 가능하다. */
	public CheckButton(CheckButtonCostume costume, Label<?> label) {
		super(costume);
		init(costume, label);
	}
	
	protected void init(CheckButtonCostume costume, Label<?> label) {
		mImage = new Image(costume.checkedOff);
		mImageCell = addCell(mImage);
		setLabel(label);
		pack();
	}
	
	@Override
	protected Costume createCostume(Costume costume) {
		return (costume != null)? new CheckButtonCostume(costume) : new CheckButtonCostume();
	}
	
	@Override
	public CheckButtonCostume getCostume() {
		return (CheckButtonCostume) mCostume;
	}
	
	@Override
	protected void setDrawable() {
		super.setDrawable();
		CheckButtonCostume costume = getCostume();
		Drawable checkDrawable = null;
		if(mDisabled && mChecked)	checkDrawable = costume.disabledCheckedOn;
		else if(mDisabled)					checkDrawable = costume.disabledCheckedOff;
		else if(mChecked) 				checkDrawable = costume.checkedOn;
		else 										checkDrawable = costume.checkedOff;
		mImage.setDrawable(checkDrawable);
	}
	
	@Override
	protected boolean onCheckChanged() {
		if(!super.onCheckChanged()) return false;
		if(mChecked) {
			if(mCheckOnAction != null) addAction(mCheckOnAction);
		} else {
			if(mCheckOffAction != null) addAction(mCheckOffAction);
		}
		return true;
	}
	
	public Image getImage() {
		return mImage;
	}

	public Label<?> getLabel() {
		return mLabel;
	}

	public TableCell getLabelCell() {
		return mLabelCell;
	}

	public TableCell getImageCell() {
		return mImageCell;
	}

	public CheckButton setLabel(Label<?> label) {
		if(mLabelCell == null) mLabelCell = addCell().right().expandX();
		mLabel = label;
		mLabelCell.setActor(label);
		return this;
	}

	public Action getCheckOnAction() {
		return mCheckOnAction;
	}

	public Action getCheckOffAction() {
		return mCheckOffAction;
	}

	public CheckButton setCheckOnAction(Action checkOnAction) {
		mCheckOnAction = checkOnAction;
		return this;
	}

	public CheckButton setCheckOffAction(Action checkOffAction) {
		mCheckOffAction = checkOffAction;
		return this;
	}
	
	public static class CheckButtonCostume extends ButtonCostume {
	
		public Drawable checkedOn;
		public Drawable checkedOff;
		public Drawable disabledCheckedOn;
		public Drawable disabledCheckedOff;
		
		public CheckButtonCostume() {
		}
		
		public CheckButtonCostume(Costume costume) {
			super(costume);
		}
		
		@Override
		public void set(Costume costume) {
			if(!(costume instanceof CheckButtonCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			super.set(costume);
			CheckButtonCostume c = (CheckButtonCostume) costume;
			checkedOn = c.checkedOn;
			checkedOff = c.checkedOff;
			disabledCheckedOn = (c.disabledCheckedOn == null)? c.checkedOn : c.disabledCheckedOn;
			disabledCheckedOff = (c.disabledCheckedOff == null)? c.checkedOff : c.disabledCheckedOff;
		}
		
		@Override
		public void merge(Costume costume) {
			if(!(costume instanceof CheckButtonCostume))
				throw new IllegalArgumentException("costume must be an instance of " + getClass().getSimpleName());
			super.merge(costume);
			CheckButtonCostume c = (CheckButtonCostume) costume;
			if(c.checkedOn != null) checkedOn = c.checkedOn;
			if(c.checkedOff != null) checkedOff = c.checkedOff;
			if(c.disabledCheckedOn != null) disabledCheckedOn = c.disabledCheckedOn;
			if(c.disabledCheckedOff != null) disabledCheckedOff = c.disabledCheckedOff;
		}
	}

}
