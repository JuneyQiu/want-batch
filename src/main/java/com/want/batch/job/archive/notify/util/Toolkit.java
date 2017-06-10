package com.want.batch.job.archive.notify.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Toolkit {
	public static String dateToString(Date date,String patten){
		if(date==null) return "";
		SimpleDateFormat sdf=new SimpleDateFormat(patten);
		return sdf.format(date);
	}

	public static Date stringToDate(String dateStr,String patten){
		if(dateStr==null||dateStr.trim().equals("")) return null;
		SimpleDateFormat sdf=new SimpleDateFormat(patten);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}
	public static int getIndexFromArr(int[] arr,int num){
		for(int i=0;i<arr.length;i++){
			if(num==arr[i]) return i;
		}
		return -1;
	}
	/**
	  * 
	  * @param str 小数点前的字符串
	  * @return 返回小数点前的逗号分割
	  */
	 public static  String splitFirstNumber(String str){
		  StringBuffer strBuffer = new StringBuffer();
		  strBuffer.append(str).reverse();
		  str = strBuffer.toString();
		  StringBuffer sb = new StringBuffer();
		  
		  for(int i = 0; i < str.length(); i=i+3){
			  if(i+3 < str.length()){
				  String str1 = str.substring(i,i+3);
				  sb.append(str1+",");
			  }else{
				  String str2 = str.substring(i,str.length());
				  sb.append(str2);
			  }
		  }
		  return sb.reverse().toString();
	 }
	//校验excel2003文件(后缀为xls)
	public static boolean validateExcel03(String path){
		if(path==null) return false;
		String str=".xls";
		if(path.lastIndexOf(str)+str.length()==path.length()) return true;
		else return false;
	}
	//校验excel2007文件(后缀为xlsx)
	public static boolean validateExcel07(String path){
		if(path==null) return false;
		String str=".xlsx";
		if(path.lastIndexOf(str)+str.length()==path.length()) return true;
		else return false;
	}
	 /**
	  * 
	  * @param str 小数点后字符串
	  * @return 逗号分割后的字符串
	  */
	 public static String splitLastNumber(String str){
		  StringBuffer sb = new StringBuffer();
		  for(int i = 0; i < str.length(); i=i+3){
			  if(i+3 < str.length()){
				  String str1 = str.substring(i,i+3);
				  sb.append(str1+",");
			  }else{
				  String str2 = str.substring(i,str.length());
				  sb.append(str2);
			  }
		  }
		  return sb.toString();
	}
	//逗号分割数据输出
	public static String splitNumber(int num){
		return splitFirstNumber(String.valueOf(num));
	}
	public static String splitNumber(long num){
		return splitFirstNumber(String.valueOf(num));
	}
	public static String splitNumber(double num){
//		if((int)num-num==0)  return splitNumber((int)num);
		String numStr=String.valueOf(num);
		int pointIndex=numStr.indexOf(".");
		if(pointIndex<0){
			return splitFirstNumber(numStr);
		}else{
			return splitFirstNumber(numStr.substring(0,pointIndex))+"."+splitLastNumber(numStr.substring(pointIndex+1,numStr.length()));
		}
	}
	//格式化输出数字，默认保留两位小数
	public static String formatData(double num){
		return formatData(num,"#0.00");
	}
	public static String formatData(double num,String pattern){
		return new DecimalFormat(pattern).format(num);
	}
	public static String formatData(String str){
		return formatData(str,"#0.00");
	}
	public static String formatData(String str,String pattern){
		return new DecimalFormat(pattern).format(Double.parseDouble(str));
	}
	//输出百分数(保留两位小数)
	public static String printPercentNum(double num){
//		return (double)(Math.round(num*100)/100.0)+"%";
		return num==0?"0%":new DecimalFormat("#0.00").format(num*100)+"%";
	}
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
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}
	public static double mul(double v1,double v2){
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}
	public static double div(double v1, double v2, int scale) {
		if(scale<0) scale=3;
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	public static double round(double v, int scale) {
		if(scale<0) scale=3;
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
