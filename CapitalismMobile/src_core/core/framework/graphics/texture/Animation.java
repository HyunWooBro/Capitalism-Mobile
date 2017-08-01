package core.framework.graphics.texture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.framework.graphics.texture.TextureRegion.ImmutableTextureRegion;

/**
 * Animation은 TextureRegion을 지정한 시간동안 연속해서 보여주어 말 그대로 
 * 애니메이션 효과를 만들 수 있다. 각 프레임마다 서로 다른 지속 시간을 부여할 수 
 * 있다.</p>
 * 
 * Animation은 {@link Arrays#asList(Object...)}를 이용하여 다음과 같이 초기화 할 수 있다.</p>
 * 
 * <pre>
 * 	Animation itemAnimation = new Animation(30, Arrays.asList(
 *		null, 
 *		texture.getTextureRegion("menu_item_flash_n_01"), 
 *		texture.getTextureRegion("menu_item_flash_n_02"), 
 *		texture.getTextureRegion("menu_item_flash_n_03"), 
 *		texture.getTextureRegion("menu_item_flash_n_04"), 
 *		texture.getTextureRegion("menu_item_flash_n_05"), 
 *		texture.getTextureRegion("menu_item_flash_n_06"), 
 *		texture.getTextureRegion("menu_item_flash_n_07"), 
 *		texture.getTextureRegion("menu_item_flash_n_08"), 
 *		null
 *		))
 *	.setPlayMode(PlayMode.REPEAT)
 *	.setFrameDuration(0, 2000)
 *	.setFrameDuration(9, 3000)
 *	.setPatternIndex(1);</pre>
 *
 * 보다시피 리스트에는 null을 넣을 수 있고 duration도 지정할 수 있다. 키프레임이 null인 경우 
 * {@link #getKeyFrame()}에서는 null을 리턴하며 지정된 시간만큼 대기하기 위해 사용한다.</p>
 * 
 * @author 김현우
 */
public class Animation {
	
	/** Animation의 플레이 방식을 지정 */ 
	public enum PlayMode{
		/** 순서대로 한번 플레이 */
		ONCE, 
		/** 순서대로 무한 플레이 */
		REPEAT, 
		/** 순서대로 시작하여 무한 핑퐁 플레이 */
		PING_PONG, 
		/** 반대로 한번 플레이 */
		REVERSE_ONCE, 
		/** 반대로 무한 플레이 */
		REVERSE_REPEAT, 
		/** 반대로 시작하여 무한 핑퐁 플레이 */
		REVERSE_PING_PONG, 
	}
	
	/** 패턴 인덱스를 제거한다. */
	public static final int CLEAR_PATTERN_INDEX = -1;
	
	private static final int START_ON_FIRST_FRAME = -1;
	
	List<FrameData> mFrameDataList = new ArrayList<FrameData>();
	private int mListSize;
	
	private int mCurrentFrameIndex;
	private FrameData mCurrentFrame;
	
	private boolean mRightDirection;
	
	private long mStartTime = START_ON_FIRST_FRAME;
	
	private boolean mFinished;
	
	private int mPatternIndex = CLEAR_PATTERN_INDEX;
	
	private boolean mSizeFixed;
	
	/** Animation 플레이 방식. 디폴트는 순서대로 무한 플레이. */
	private PlayMode mPlayMode = PlayMode.REPEAT;

	public Animation(List<FrameData> frameDataList) {
		setFrameData(frameDataList);
	}
	
	public Animation(long frameDuration, List<TextureRegion> keyFrameList) {
		setFrameData(frameDuration, keyFrameList);
	}
	
	public Animation(Animation animation) {
		setFrameData(animation.mFrameDataList);
		mPatternIndex = animation.mPatternIndex;
		mPlayMode = animation.mPlayMode;
		reset();
	}
	
	public void setFrameData(List<FrameData> frameDataList) {
		mFrameDataList.clear();
		mFrameDataList.addAll(frameDataList);
		mListSize = mFrameDataList.size();
		checkSize();
		reset();
	}

	public void setFrameData(long frameDuration, List<TextureRegion> keyFrameList) {
		mFrameDataList.clear();
		int n = keyFrameList.size();
		for(int i=0; i<n; i++) {
			TextureRegion region = keyFrameList.get(i);
			mFrameDataList.add(new FrameData(region, frameDuration));
		}
		mListSize = mFrameDataList.size();
		setFrameDuration(frameDuration);
		checkSize();
		reset();
	}
	
	private void checkSize() {
		int k = 0;
		TextureRegion first = null;
		int width = 0;
		int height = 0;
		
		while(true) {
			first = mFrameDataList.get(k++).getKeyFrame();
			if(first == null) continue;
			width = first.getRegionWidth();
			height = first.getRegionHeight();
			break;
		}
		
		mSizeFixed = true;
		for(int i=k; i<mListSize; i++) {
			TextureRegion region = mFrameDataList.get(i).getKeyFrame();
			if(region != null && (region.getRegionWidth() != width || region.getRegionHeight() != height)) {
				mSizeFixed = false;
				return;
			}
		}
	}
	
	/** 애니메이션을 재시작한다. */
	public void reset() {
		mFinished = false;
		mStartTime = START_ON_FIRST_FRAME;
	}
	
	public void init() {
		switch(mPlayMode) {
			case ONCE:
			case REPEAT:
			case PING_PONG:
				mCurrentFrameIndex = 0;
				mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				mRightDirection = true;
				break;
			case REVERSE_ONCE:
			case REVERSE_REPEAT:
			case REVERSE_PING_PONG:
				mCurrentFrameIndex = mListSize - 1;
				mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				mRightDirection = false;
				break;
		}
	}
	
	public void update(long time) {
		if(mFinished) return;
		
		if(mStartTime == START_ON_FIRST_FRAME) {
			mStartTime = time;
			init();
		}
		
		long frameDuration = mCurrentFrame.mFrameDuration;
	    if(time > mStartTime + frameDuration ) {
	    	mStartTime = time;
	    	
	    	switch(mPlayMode) {
			case ONCE:
				if(mCurrentFrameIndex < mListSize - 1) {
					mCurrentFrameIndex++;
					mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				}
				else
					mFinished = true;
				break;
			case REPEAT:
				if(mCurrentFrameIndex < mListSize - 1)
					mCurrentFrameIndex++;
				else {
					if(mPatternIndex != CLEAR_PATTERN_INDEX)
						mCurrentFrameIndex = mPatternIndex;
					else
						mCurrentFrameIndex = 0;
				}
				
				mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				break;
			case PING_PONG:
				if(mRightDirection) {
					if(mCurrentFrameIndex < mListSize - 1)
						mCurrentFrameIndex++;
					else {
						mRightDirection = !mRightDirection;
						if(mPatternIndex != mListSize - 1)
							mCurrentFrameIndex--;
					}
				}
				else {
					if(mCurrentFrameIndex > Math.max(0, mPatternIndex))
						mCurrentFrameIndex--;
					else {
						mRightDirection = !mRightDirection;
						if(mPatternIndex != mListSize - 1)
							mCurrentFrameIndex++;
					}
				}

				mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				break;
			case REVERSE_ONCE:
				if(mCurrentFrameIndex > 0) {
					mCurrentFrameIndex--;
					mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				}
				else
					mFinished = true;
				break;
			case REVERSE_REPEAT:
				if(mCurrentFrameIndex > 0)
					mCurrentFrameIndex--;
				else {
					if(mPatternIndex != CLEAR_PATTERN_INDEX)
						mCurrentFrameIndex = mPatternIndex;
					else
						mCurrentFrameIndex = mListSize - 1;
				}
				
				mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				break;
			case REVERSE_PING_PONG:
				if(!mRightDirection) {
					if(mCurrentFrameIndex > 0)
						mCurrentFrameIndex--;
					else {
						mRightDirection = !mRightDirection;
						if(mPatternIndex != 0)
							mCurrentFrameIndex++;
					}
				}
				else {
					if(mCurrentFrameIndex < Math.min(mListSize - 1, mPatternIndex))
						mCurrentFrameIndex++;
					else {
						mRightDirection = !mRightDirection;
						if(mPatternIndex != 0)
							mCurrentFrameIndex--;
					}
				}

				mCurrentFrame = mFrameDataList.get(mCurrentFrameIndex);
				break;
	    	}
	    }
	}
	
	public Animation setFrameDuration(long duration) {
		if(duration < 0) throw new IllegalArgumentException("Duration can't be negative");
		
		for(int i=0; i<mListSize; i++)
			mFrameDataList.get(i).mFrameDuration = duration;
		return this;
	}
	
	public Animation setFrameDuration(int index, long duration) {
		if(duration < 0) throw new IllegalArgumentException("Duration can't be negative");
		
		mFrameDataList.get(index).mFrameDuration = duration;
		return this;
	}
	
	public long getFrameDuration(int index) {
		return mFrameDataList.get(index).mFrameDuration;
	}
	
	/** 현재의 키프레임 TextureRegion을 얻는다. 키프레임이 null이라면 null을 리턴한다. */
	public TextureRegion getKeyFrame() {
		return (mCurrentFrame != null)? mCurrentFrame.getKeyFrame() : null;
	}
	
	public PlayMode getPlayMode() {
		return mPlayMode;
	}

	public Animation setPlayMode(PlayMode playMode) {
		mPlayMode = playMode;
		return this;
	}
	
	public boolean isFinished() {
		return mFinished;
	}

	public void clear() {
		reset();
		mFinished = true;
		mFrameDataList.clear();
		mCurrentFrame = null;
	}
	
	public int getPatternIndex() {
		return mPatternIndex;
	}

	/**
	 * Animation의 플레이 모드가 {@code PlayMode.REPEAT}, {@code PlayMode.REVERSE_REPEAT}
	 * {@code PlayMode.PING_PONG}, 그리고 {@code PlayMode.REVERSE_PING_PONG}일 
	 * 경우에 반복할 패턴을 지정한다. 예를 들어, 첫번째의 경우 index가 0, 1, 2, 3라고 할 때, 
	 * patternIndex를 2로 지정할 경우 0부터 3까지 진행한 뒤에 2, 3, 2, 3, ... 과 같이 플레이 된다. 
	 * 두번째의 경우라면 3부터 0까지 진행한 뒤에 2, 1, 0, 2, 1, 0, ... 과 같이 플레이 된다.</p>
	 * 
	 * 패턴을 제거하기 위해서는 {@link #CLEAR_PATTERN_INDEX}을 지정하면 된다.</p>
	 */
	public Animation setPatternIndex(int patternIndex) {
		mPatternIndex = patternIndex;
		return this;
	}
	
	public boolean isSizeFixed() {
		return mSizeFixed;
	}
	
	public List<FrameData> getFrameDataList() {
		return mFrameDataList;
	}
	
	public static class FrameData {
		private TextureRegion mKeyFrame;
		private long mFrameDuration;
		
		public FrameData(TextureRegion keyFrame, long frameDuration) {
			mKeyFrame = (keyFrame != null)? new ImmutableTextureRegion(keyFrame) : null;
			mFrameDuration = frameDuration;
		}

		public TextureRegion getKeyFrame() {
			return (mKeyFrame != null)? mKeyFrame : null;
		}

		public long getFrameDuration() {
			return mFrameDuration;
		}

		public void setKeyFrame(TextureRegion keyFrame) {
			mKeyFrame = (keyFrame != null)? new ImmutableTextureRegion(keyFrame) : null;
		}

		public void setFrameDuration(long frameDuration) {
			mFrameDuration = frameDuration;
		}
	}

}
