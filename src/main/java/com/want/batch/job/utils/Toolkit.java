package com.want.batch.job.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Toolkit {
	//毫秒换算成   ##小时##分钟##秒##毫秒
	public static String timeTransfer(long t){
		long hour=t/(1000*60*60);
		t=t-hour*60*60*1000;
		long minute=t/(1000*60);
		t=t-minute*60*1000;
		long sec=t/1000;
		t=t-sec*1000;
		return hour+"小时"+minute+"分"+sec+"秒"+t+"毫秒";
	}
	public static String dateToString(Date date,String patten){
		if(date==null) return "";
		SimpleDateFormat sdf=new SimpleDateFormat(patten);
		return sdf.format(date);
	}
}
