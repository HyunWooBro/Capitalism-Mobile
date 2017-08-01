package project.game.building.department;

import core.framework.graphics.batch.Batch;

public class Advertise extends Department {
	public int m_current_progress;
	public int m_max_progress;
	public int m_advertise_type;

	public Advertise(int index, DepartmentManager department_manager) {
		super(index, department_manager);
		mNumEmployees = 1;
		m_current_progress = 0;
		m_max_progress = 20;
	}
	
	@Override
	protected double work() {
		return 0;
	}

	public void onDrawFrame(Batch drawer) {

		// if(m_advertise_type != null)
		{
			// drawer.drawText("제품별 광고비",
			// DepartmentManager.sDepartmentRectangles[mIndex].left+10,
			// DepartmentManager.sDepartmentRectangles[mIndex].top+55, p);
			// drawer.drawText(" : "+m_current_progress*30+"(만원)",
			// DepartmentManager.sDepartmentRectangles[mIndex].left+10,
			// DepartmentManager.sDepartmentRectangles[mIndex].top+80, p);
		}

		if(isSelected()) {
			// drawer.prepareToDrawBitmap(m_advertise_building_bitmap, 40, 50,
			// null);

			// drawer.prepareToDrawBitmap(m_division_bar_bitmap, 0, 320, null);

			// drawer.prepareToDrawBitmap(m_department_bitmap, 40, 350, null);
		}

	}

	private int m_progress;

	@Override
	public DepartmentType getDepartmentType() {
		return DepartmentType.ADVERTISE;
	}

	/*
	 * if(mDepartments[sDepartmentSelectedIndex] != null &&
	 * mDepartments[sDepartmentSelectedIndex].mDepartmentType ==
	 * Department.DepartmentType.ADVERTISE) { final LinearLayout linear =
	 * (LinearLayout) View.inflate(Core.APP.getActivity(),
	 * R.layout.seekbar_text, null);
	 * 
	 * SeekBar seekbar = (SeekBar) linear.findViewById(R.id.seekBar1); TextView
	 * textview = (TextView) linear.findViewById(R.id.textView1);
	 * 
	 * Core.APP.showAlertDialog(new AlertDialog.Builder(Core.APP.getActivity())
	 * .setTitle("연결된 각 제품에 대한 광고비를 선택하세요") .setView(linear)
	 * .setPositiveButton("확인", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * ((Advertise) mDepartments[sDepartmentSelectedIndex]).m_current_progress =
	 * m_progress; } }) .setNegativeButton("취소", null));
	 * 
	 * seekbar.setMax(((Advertise)
	 * mDepartments[sDepartmentSelectedIndex]).m_max_progress);
	 * seekbar.setProgress(((Advertise)
	 * mDepartments[sDepartmentSelectedIndex]).m_current_progress);
	 * 
	 * textview.setText(((Advertise)
	 * mDepartments[sDepartmentSelectedIndex]).m_current_progress*30+"(만원)");
	 * 
	 * seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
	 * {
	 * 
	 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
	 * 
	 * }
	 * 
	 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
	 * 
	 * }
	 * 
	 * @Override public void onProgressChanged(SeekBar seekBar, int progress,
	 * boolean fromUser) { m_progress = progress;
	 * 
	 * TextView textview = (TextView) linear.findViewById(R.id.textView1);
	 * textview.setText(m_progress*30+"(만원)"); } });
	 * 
	 * 
	 * }
	 */

}
