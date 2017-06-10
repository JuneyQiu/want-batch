package com.want.utils;

public class TimeUtils {

	public static void waitingSeconds(int seconds) {
		long endTime = System.currentTimeMillis() + seconds * 1000;
		while(System.currentTimeMillis() < endTime);
	}

}
