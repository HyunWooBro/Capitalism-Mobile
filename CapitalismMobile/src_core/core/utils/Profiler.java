package core.utils;

import core.framework.Core;

public class Profiler {
	
	private static long elapsed;
	
	private Profiler() { 
	}
	
	/**
	 *  작업의 경과시간을 측정 시작
	 */
	public static void beginElapsedTime() {
		elapsed = System.nanoTime();
	}
	
	/**
	 *  작업의 경과시간을 측정 종료 및 로깅
	 * @param mTag
	 */
	public static void EndElapsedTime(String tag) {
		elapsed = System.nanoTime() - elapsed;
		Core.APP.debug(tag, "<Elapsed> : " + String.format("%.2f", elapsed/1000000f) + " millisecs");
	}
	
	/**
	 * 작업의 경과시간을 측정 종료 및 로깅
	 */
	public static void EndElapsedTime() {
		elapsed = System.nanoTime() - elapsed;
		Core.APP.debug("<Elapsed> : " + String.format("%.2f", elapsed/1000000f) + " millisecs");
	}

}
