package org.framework;

import java.util.*;

import android.content.*;
import android.media.*;

public class SoundManager {

	private SoundPool m_SoundPool;// 사운드 풀
	private HashMap m_SoundPoolMap;// 리소스 아이디 값을 저장할 해시맵 
	private AudioManager m_AudioManager;// 사운드를 관리할 오디오 매니저
	private Context m_Activity;// 애플리케이션의 컨텍스트
	 
	public void  Init(Context _context){
		m_SoundPool=new SoundPool(4, AudioManager.STREAM_MUSIC, 0);// 사운드풀 생성
		m_SoundPoolMap=new HashMap();
		m_AudioManager=(AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);// 핸들 값
		m_Activity=_context;// 핸들 값
	}
	 
	public void addSound(int _index, int _soundID){
		int id=m_SoundPool.load(m_Activity, _soundID, 1);// 사운드 로드
		m_SoundPoolMap.put(_index, id);// 아이디 값을 해시맵의 인덱스로 저장
	}
	 
	public void play(int _index){
		// 사운드 재생
		float streamVolume=m_AudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume=streamVolume/m_AudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		m_SoundPool.play((Integer)m_SoundPoolMap.get(_index), streamVolume, streamVolume, 1, 0, 1f);
	}
	 
	public void playLooped(int _index){
		// 사운드 반복 재생
		float streamVolume=m_AudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume=streamVolume/m_AudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		m_SoundPool.play((Integer)m_SoundPoolMap.get(_index), streamVolume, streamVolume, 1, -1, 1f); 
	}
	 // 싱글턴 패턴 적용
	 private static SoundManager s_instance;
	 
	public static SoundManager getInstance(){
		if(s_instance==null){
			s_instance=new SoundManager();
		}
		return s_instance;
	}
}


