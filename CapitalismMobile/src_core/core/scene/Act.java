package core.scene;

/**
 * 
 * 연극에서 1막 2장(act 1 scene 2)과 같이, Act는 관련된 여러 Scene을 하나의 그룹으로 엮어  
 * 그룹에서 공유하는 자원을 관리한다. 각 프레임마다 {@link Scene#update(long)}에 앞서 
 * {@link Act#update(long)}가 먼저 처리된다.</p>
 * 
 * Act의 기능이 필요없다면 무시하고 Scene으로만 극을 완성할 수도 있다.</p>
 * 
 * @author 김현우
 */
public abstract class Act {
	
	private boolean mCreated;
	
	public Act() {
	}
	
	protected void create() {
	}

	public void update(long time) {
	}
	
	protected void destroy() {
	}
	
	public boolean isCreated() {
		return mCreated;
	}

	/*package*/ void setCreated(boolean created) {
		mCreated = created;
	}
}
