package com.want.batch.job.spring_example.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精确的浮点数运算，包括加减乘除和四舍五入。
 * 
 * 这个类还提供其它一些方法。这个类很杂乱，很杂乱
 */
public class Tool {
	
	/**
	 * 分隔符
	 */
	public static String FEN_GE = "=ncaa=";
	
	/**
	 * CD_I
	 */
	public static String CD_I = "CDI";
	
	/**
	 * 批量操作数据库时，每次最多提交的数目: 20000
	 */
	public static int BATCH_UPDATE_DB_NUM = 20000;
	
	/**
	 * OK
	 */
	public static String OK = "ok";
	
	/**
	 * ERROR
	 */
	public static String ERROR = "error";
	
	
	/**
	 * 18位品项编号(例如：000000306101020900)  开头
	 */
	public static final String START = "0000003061";
	
	/**
	 * 18位品项编号(例如：000000306101020900)  结尾
	 */
	public static final String END = "00";
	
	/**
	 * 12位品项编号(例如：3061040121B0)  开头
	 */
	public static final String START2 = "3061";
	
	/**
	 * 12位品项编号(例如：3061040121B0)  结尾
	 */
	public static final String END2 = "0";
	
	/**
	 * 往后推延50天
	 */
	public static final int MOVE_DAY_NUM = 50;
	
	/**
	 * 显示文本：未发货
	 */
	public static final String NO_SEND = "未发货";
	
	/**
	 * 显示文本：-
	 */
	public static final String REPLACE_NULL_STR = "-";
	
	
	//for: 文宣品
	public static final String INCLUDE = "INCLUDE";
	public static final String NOT_INCLUDE = "NOT-INCLUDE";
	public static final String ALL = "ALL";
	
	public static final BigDecimal zero = new BigDecimal("0");
	public static final BigDecimal one = new BigDecimal("1");
	public static final BigDecimal number85 = new BigDecimal("85");
	public static final BigDecimal number30 = new BigDecimal("30");
	public static final BigDecimal number90000 = new BigDecimal("90000");
	
	private static final int DEF_DIV_SCALE = 4; // 默认除法运算精度
	
	/**
	 * 常用的非汉字 字符   的    一个子集
	 */
	private static final String NOT_CN = "abcdefghijklmnopqrstuvwxyz _=+-/&%#$!@*.,;?()[]{}`~^0123456789";
	
	/**
	 * 英文字母集合
	 */
	private static final String EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

//	/**
//	 * customer Map, key:sid, value:id
//	 */
//	private static Map<Integer, String> customerMap_sid_id = new HashMap<Integer, String>();
//	
//	/**
//	 * customer Map, key:sid, value:id
//	 */
//	public static String getCustomerId(int sid) {
//		
//		String id = customerMap_sid_id.get(sid);
//		
//		if(StringUtils.isEmpty(id)) {
//			WenDelegator wenDelegator = (WenDelegator) MySpringContainer.getBean("wenDelegator");
//			id = wenDelegator.getCustomerId(sid);
//			customerMap_sid_id.put(sid, id);
//			customerMap_id_sid.put(id, sid);
//		}
//		
//		return id;
//	}
//	
//	/**
//	 * customer Map, key:id, value:sid
//	 */
//	private static Map<String, Integer> customerMap_id_sid = new HashMap<String, Integer>();
	
//	/**
//	 * customer Map, key:id, value:sid
//	 */
//	public static Integer getCustomerSid(String id) {
//		
//		Integer sid = customerMap_id_sid.get(id);
//		
//		if(sid == null) {
//			WenDelegator wenDelegator = (WenDelegator) MySpringContainer.getBean("wenDelegator");
//			sid = wenDelegator.getCustomerSid(id);
//			customerMap_id_sid.put(id, sid);
//			customerMap_sid_id.put(sid, id);
//		}
//		
//		return sid;
//	}
	
//	/**
//	 * 根据客户的id，得到sid
//	 */
//	public static int getCustomerSidById(String id) {
//		return getCustomerSid(changeCustomerIdTo10(id));
//	}
	
//	/**
//	 * 根据客户的sid，得到id
//	 */
//	public static String getCustomerIdBySid(int sid) {
//		return getCustomerId(sid);
//	}
	
	/**
	 * 把字符串转换为整数
	 */
	public static int myParseInt(String str) {
		if (str == null || "".equals(str.trim())) {
			return -1;
		}

		int num = 0;

		try {
			num = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			num = -1;
		}

		return num;
	}
	
	/**
	 * 把字符串转换为 long 类型的整数
	 */
	public static long myParseLong(String str) {
		if (str == null || "".equals(str.trim())) {
			return -1L;
		}

		long num = 0;

		try {
			num = Long.parseLong(str);
		} catch (NumberFormatException e) {
			num = -1L;
		}

		return num;
	}
	

	/**
	 * 把字符串转换为浮点数
	 */
	public static double myParseDouble(String str) {
		if (str == null || "".equals(str.trim())) {
			return -999999d;
		}

		double num = 0d;

		try {
			num = Double.parseDouble(str);
		} catch (NumberFormatException e) {
			num = -999999d;
		}

		return num;
	}
	
	
	
	/**
	 * 只保留字符串中的汉字，去掉其它字符（字母，数字，等等）
	 * (用正则表达式来做应该更好)
	 */
	public static String onlyCn(String str) {
		if(str == null || str.trim().equals("")) {
			return "";
		}
		
		str = str.trim().toLowerCase();
		int len = str.length();
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < len; i++) {
			String s = "" + str.charAt(i);
			if(NOT_CN.indexOf(s) < 0) {
				sb.append(s);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 得到上一月份
	 */
	public static String getLastMonth(String year, String month) {
		int y = Integer.parseInt(year);
		int m = Integer.parseInt(month);
		
		if(m == 1) {
			return "" + (y - 1) + "12";
		}
		
		m--;
		
		String add = (m < 10) ? "0" : "";
		
		return "" + y + add + m;
	}
	
	
	
	/**
	 * 加法
	 */
	public static double add(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.add(b2).doubleValue();
	}
	
	/**
	 * 加法 to String
	 */
	public static String addToString(String v1, String v2) {
		if(v1 == null || "".equals(v1.trim())) {
			v1 = "0";
		}
		if(v2 == null || "".equals(v2.trim())) {
			v2 = "0";
		}
		
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.add(b2).toString();
	}

	/**
	 * 减法
	 */
	public static double jian(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.subtract(b2).doubleValue();
	}
	
	/**
	 * change 同期成长率
	 */
	public static String changeTqczl(String str) {
		return Tool.isTqczlOK(str) ? str : "<font color='red'>" + str + "<font>";
	}
	
	/**
	 * 同期成长率是否 大于等于 0
	 */
	public static boolean isTqczlOK(String str) {
		if(str == null || ! str.endsWith("%") || str.length() == 1) {
			return false;
		}
		
		String temp = str.substring(0, str.length() - 1);
		double num = Double.parseDouble(temp);
		
		return num >= 0;
	}
	
	/**
	 * change 开单进度
	 */
	public static String changeKdjd(String str) {
		return Tool.isKdjdOK(str) ? str : "<font color='red'>" + str + "<font>";
	}
	
	public static String changeKdjhl(String str,String month) {
		if(getMonthToString().equals(month)){
			if(getDate()>=21){
				str= Tool.isKdjdOK(str) ? str : "<font color='red'>" + str + "<font>";
			}
		}else{
		str= Tool.isKdjdOK(str) ? str : "<font color='red'>" + str + "<font>";
		}
		return str;
	}
	/**
	 * 开单进度是否小于100
	 */
	public static boolean isKdjdOK(String str) {
		if(str == null || ! str.endsWith("%") || str.length() == 1) {
			return false;
		}else if(str.equals("0%")){
			return true;
		}
		
		String temp = str.substring(0, str.length() - 1);
		double num = Double.parseDouble(temp);
		
		return num >= 100;
	}
	
	/**
	 * change 目标达成率
	 */
	public static String changeMbdcl(String str) {
		return Tool.isMbdclOK(str) ? str : "<font color='red'>" + str + "<font>";
	}
	
	/**
	 * 目标达成率是否 大于等于 80%
	 */
	public static boolean isMbdclOK(String str) {
		//System.out.println(str);
		if(str == null || ! str.endsWith("%") || str.length() == 1) {
			return false;
		}else if(str.equals("0%")){
			return true;
		}
		
		String temp = str.substring(0, str.length() - 1);
		double num = Double.parseDouble(temp);
		
		return num >= 100;
	}
	
	
	/**
	 * 同期成长率，保留0位小数
	 */
	public static String tqczl(String b1, String b2) {
		return tqczl(new BigDecimal(b1), new BigDecimal(b2));
	}
	
	/**
	 * 同期成长率，保留0位小数
	 */
	public static String tqczl(BigDecimal b1, BigDecimal b2) {
		return tqczl(b1, b2, 0);
	}
	public static String tqczlforMB(String year,String month,String b1, String b2) {
		String cu ="1";
		if(getYearToString().equals(year)&&getMonthToString().equals(month)){
		Calendar   cDate   =   new   GregorianCalendar();     
		cDate.set(Integer.parseInt(year),Integer.parseInt(month),01);         //设置年月日，日随便设一个就可以   
		int lastday = cDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		cu = String.valueOf(chu(String.valueOf(getDate()),String.valueOf(lastday))); 
		}
		return tqczl(new BigDecimal(b1), new BigDecimal(b2).multiply(new BigDecimal(cu)).divide(new BigDecimal("1"),0, BigDecimal.ROUND_HALF_UP));
	}
	
	public static String mbdclforMB(String b1, String b2, String b3,String b4,String month) {
		
	if(getMonth()==Integer.parseInt(month)&&getDate()>=1&&getDate()<10){
		b4 = new BigDecimal(b2).toString();
		}else if(getMonth()==Integer.parseInt(month)&&getDate()>=10&&getDate()<20){
		b4 = new BigDecimal(b2).add(new BigDecimal(b3)).toString();
		}	
		return mbdcl(new BigDecimal(b1),new BigDecimal(b4), 0);
	}
	/**
	 * 同期成长率
	 */
	public static String tqczl(BigDecimal b1, BigDecimal b2, int scale) {
		//System.out.println(b2);
		if(b2 == null || b2.equals(new BigDecimal("0"))||b2.equals(new BigDecimal("0.0"))) {
			//return (b1 == null || b1.equals(new BigDecimal("0"))) ? "0%" : "100%";
			return "0%";
		}
		
		BigDecimal temp = b1.subtract(b2);
		temp = temp.divide(b2, 5, BigDecimal.ROUND_HALF_UP);
		
		BigDecimal n100 = new BigDecimal("100");
		temp = temp.multiply(n100);
		
		BigDecimal one = new BigDecimal("1");
		temp = temp.divide(one, scale, BigDecimal.ROUND_HALF_UP);
		String str = temp.toString();
		
		if(str.endsWith(".0")) {
			str = str.substring(0, str.length() - 2);
		}
		else if(str.endsWith(".00")) {
			str = str.substring(0, str.length() - 3);
		}
		else if(str.endsWith(".000")) {
			str = str.substring(0, str.length() - 4);
		}
		
		return str + "%";
	}
	
	/**
	 * 目标达成率，保留0位小数
	 */
	public static String mbdcl(BigDecimal b1, BigDecimal b2) {
		return mbdcl(b1, b2, 0);
	}
	public static String mbdcl(String b1, String b2) {
		return mbdcl(new BigDecimal(b1),new BigDecimal(b2), 0);
	}
	
	
	//
	/**
	 * 目标达成率
	 */
	public static String mbdcl(BigDecimal b1, BigDecimal b2, int scale) {
		if(b2 == null || b2.equals(new BigDecimal("0"))||b2.equals(new BigDecimal("0.0"))||b2.equals(new BigDecimal("0.00"))) {
			//return (b1 == null || b1.equals(new BigDecimal("0"))) ? "0%" : "100%";
			return "0%";
		}
		
		BigDecimal temp = b1;
		temp = temp.divide(b2, 5, BigDecimal.ROUND_HALF_UP);
		
		BigDecimal n100 = new BigDecimal("100");
		temp = temp.multiply(n100);
		
		BigDecimal one = new BigDecimal("1");
		temp = temp.divide(one, scale, BigDecimal.ROUND_HALF_UP);
		String str = temp.toString();
		
		if(str.endsWith(".0")) {
			str = str.substring(0, str.length() - 2);
		}
		else if(str.endsWith(".00")) {
			str = str.substring(0, str.length() - 3);
		}
		else if(str.endsWith(".000")) {
			str = str.substring(0, str.length() - 4);
		}
		
		return str + "%";
	}
	
	
	/**
	 * 乘法 for : BigDecimal 。避免了控指针异常
	 */
	public static BigDecimal chengForBigDecimal(BigDecimal v1, BigDecimal v2) {
		return (v1 == null || v2 == null) ? zero : v1.multiply(v2);
	}
	
	/**
	 * 乘法
	 */
	public static double cheng(String v1, String v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 除法运算。当发生除不尽的情况时，精确到小数点以后4位，以后的数字四舍五入
	 */
	public static double chu(String v1, String v2) {
		return chu(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入
	 * 
	 * @param scale 表示需要精确到小数点以后几位
	 */
	public static double chu(String v1, String v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);

		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 四舍五入。小数点后保留2位
	 */
	public static double math45(double d) {
		return math45(String.valueOf(d));
	}

	/**
	 * 四舍五入。小数点后保留2位
	 */
	public static double math45(String v) {
		return math45(v, 2);
	}
	
	/**
	 * 四舍五入
	 * 
	 * @param scale 小数点后保留几位
	 */
	public static double math45(String v, int scale) {
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");

		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static String math45toString(String v) {
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, 0, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	
	/**
	 * 对字符串进行四舍五入，小数点后保留0位。避免了空指针异常 和 NumberFormatException
	 */
	public static String string45(String v) {
		if(v == null || "0".equals(v)) {
			return "0";
		}
		
		double value = myParseDouble(v);
		
		if(value < -99990d) {
			return "0";
		}
		
		v = String.valueOf(value);
		
		BigDecimal b = new BigDecimal(v);
		BigDecimal one = new BigDecimal("1");

		return b.divide(one, 0, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	
	/**
	 * 四舍五入，小数点后保留0位
	 */
	public static BigDecimal math45(BigDecimal v) {
		if(v == null) {
			return (new BigDecimal("0"));
		}
		return math45(v, 0);
	}
	
	/**
	 * 四舍五入
	 * 
	 * @param scale 小数点后保留几位
	 */
	public static BigDecimal math45(BigDecimal v, int scale) {
		BigDecimal one = new BigDecimal("1");
		return v.divide(one, scale, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 此方法可比较两个数值是否相等。相等，则返回true
	 */
	public static boolean isEquals(BigDecimal v1, BigDecimal v2) {
		return v1.compareTo(v2) == 0;
	}
	
	
	
	/**
	 * 此方法可获得当天的年份，例如：今天是2004年10月18日，则返回：2004
	 */
	public static int getYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * add by wangyue 年节库存录入时间
	 * 业代录入库存时，显示的8位的日期，如：20091205
	 */
	public static String getStoreDate1() {
		return getYearToString() + getMonthToString() + getDateToString1();
	}
	/**
	 * 库存，默认显示的年份
	 */
	public static String getYearToString() {
		int year = getYear();
		if(getMonth() == 1 && getDate() < 5) {
			year--;
		}
		
		return String.valueOf(year);
	}

	/**
	 * 此方法可获得当天的月份，例如：今天是2004年10月18日，则返回：10
	 */
	public static int getMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * 库存，默认显示的月份
	 */
	public static String getMonthToString() {
		int m = getMonth();
		if(getDate() < 5) {
			m = (m == 1) ? 12 : (m - 1);
		}
		String month = String.valueOf(m);
		return month.length() == 1 ? ("0" + month) : month;
	}
	public static String getMonthToString1() {
		int m = getMonth();
		String month = String.valueOf(m);
		return month.length() == 1 ? ("0" + month) : month;
	}
	/**
	 * 此方法可获得当天的日子，例如：今天是2004年10月18日，则返回：18
	 */
	public static int getDate() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DATE);
	}
	
	public static int getDateEnd() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public static String getDateToString1() {
		int day = getDate();
		String str = "30";
		if(day >= 5 && day < 10) {
			str = "05";
		}else if(day >= 10 && day < 15) {
			str = "10";
		}else if(day >= 15 && day < 20) {
			str = "15";
		}else if(day >= 20 && day < 25) {
			str = "20";
		}else if(day >= 25 && day < 30) {
			str = "25";
		}
		
		return str;
	}
	
	
	

	/**
	 * 不管控品项
	 */
	private static Map<String, String> not_controlnm = null;
	
	/**
	 * 不管控品项
	 */
	public static Map<String, String> getNot_controlnm() {
		if(not_controlnm == null) {
			not_controlnm = new HashMap<String, String>();
			
			not_controlnm.put("000000306108101500", "");
	        not_controlnm.put("000000306108018300", "");
	        not_controlnm.put("000000306107076200", "");
	        not_controlnm.put("000000306107076100", "");
	        not_controlnm.put("000000306107076100", "");
	        not_controlnm.put("000000306108043600", "");
	        not_controlnm.put("000000306108043700", "");
	        not_controlnm.put("000000306108043800", "");
	        not_controlnm.put("000000306108101300", "");
	        not_controlnm.put("000000306106126100", "");
	        not_controlnm.put("000000306106126200", "");
	        not_controlnm.put("000000306106127000", "");
	        not_controlnm.put("000000306106144700", "");
	        not_controlnm.put("000000306106145100", "");
	        not_controlnm.put("000000306104012100", "");
	        not_controlnm.put("000000306104012500", "");
	        not_controlnm.put("000000306104013400", "");
	        not_controlnm.put("000000306104013700", "");
	        not_controlnm.put("000000306104013800", "");
	        not_controlnm.put("000000306104013900", "");
	        not_controlnm.put("000000306104014200", "");
	        not_controlnm.put("000000306104014800", "");
	        not_controlnm.put("000000306104015000", "");
	        not_controlnm.put("000000306105050900", "");
	        not_controlnm.put("0000003061040105A0", "");
	        not_controlnm.put("0000003061040115A0", "");
	        not_controlnm.put("0000003061040121B0", "");
	        not_controlnm.put("0000003061040124B0", "");
	        not_controlnm.put("0000003061040125B0", "");
	        not_controlnm.put("0000003061040134B0", "");
	        
		}
		
		return not_controlnm;
	}

	
	
	
	
	
	
	
	
	
	/**
	 * 增加1个文宣品编号
	 */
	private static void addValueForWen(List<String> list, String str) {
		list.add(str); //可能需要加前缀和后缀
	}

	/**
	 * 县城的文宣品 list
	 */
	private static List<String> wenList = null;
	
	/**
	 * 县城的文宣品 list
	 */
	public static List<String> getWenList() {
		if(wenList == null) {
			wenList = new ArrayList<String>();
			
			addValueForWen(wenList, "99000257F");
			addValueForWen(wenList, "99000258F");
			addValueForWen(wenList, "99000259F");
			addValueForWen(wenList, "99000260F");
			addValueForWen(wenList, "99000261F");
			addValueForWen(wenList, "99000262F");
			addValueForWen(wenList, "99000263F");
			addValueForWen(wenList, "99000268F");
			addValueForWen(wenList, "99000269F");
			addValueForWen(wenList, "99000270F");
			addValueForWen(wenList, "99010017A");
			addValueForWen(wenList, "99070128K");
			addValueForWen(wenList, "99070129K");
			addValueForWen(wenList, "99080159Y");
			addValueForWen(wenList, "99080160Y");
			addValueForWen(wenList, "99080161Y");
			addValueForWen(wenList, "99090101T");
			addValueForWen(wenList, "99090102T");
			addValueForWen(wenList, "99090104T");
			addValueForWen(wenList, "99090105T");
			addValueForWen(wenList, "99090106T");
			addValueForWen(wenList, "99090107T");
			addValueForWen(wenList, "99100157A");
			addValueForWen(wenList, "99100158A");
			addValueForWen(wenList, "99100159A");
			addValueForWen(wenList, "99100160A");
			addValueForWen(wenList, "99100161A");
		}
		
		return wenList;
	}
	
	
	/**
	 * 增加2或者4个品项编号（可能有 6位, 7位, 12位, 18位, 19位 的）
	 * 081015
	 * 040105B
	 */
	private static void add2Value(List<String> list, String str) {
		list.add(str); //040121   or   040121B
		list.add(START + str + END); //000000306104012100    or    0000003061040121B00
		
		boolean haveEn = haveEn(str); //是否含有英文字母
		
		if(haveEn) {
			if(str.length() != 7) {
				System.out.println(str + "  ======  len != 7");
			}
			list.add(START2 + str + END2); //3061040121B0
			list.add(START + str + END2);  //0000003061040121B0
		}
	}
	
	/**
	 * 囤货品项 list
	 */
	private static List<String> tunhuoList = null;
	
	/**
	 * 囤货品项 list
	 */
	public static List<String> getTunhuoList() {
		if(tunhuoList == null) {
			tunhuoList = new ArrayList<String>();
			
			add2Value(tunhuoList, "070144");
			add2Value(tunhuoList, "070101");
			add2Value(tunhuoList, "070201");
			add2Value(tunhuoList, "070202");
			add2Value(tunhuoList, "070104");
			add2Value(tunhuoList, "070146");
			add2Value(tunhuoList, "080436");
			add2Value(tunhuoList, "080437");
			add2Value(tunhuoList, "080438");
			add2Value(tunhuoList, "081015");
			add2Value(tunhuoList, "081016");
			add2Value(tunhuoList, "081030");
			add2Value(tunhuoList, "081031");
			add2Value(tunhuoList, "081032");
			add2Value(tunhuoList, "081033");
			add2Value(tunhuoList, "080183");
			add2Value(tunhuoList, "081029");
			add2Value(tunhuoList, "070761");
			add2Value(tunhuoList, "070762");
			add2Value(tunhuoList, "081013");
			add2Value(tunhuoList, "061269");
			add2Value(tunhuoList, "061451");
			add2Value(tunhuoList, "061447");
			add2Value(tunhuoList, "010209");
			add2Value(tunhuoList, "011736");
			add2Value(tunhuoList, "011248");
		}
		
		return tunhuoList;
	}
	
	/**
	 * 乳品 list
	 */
	private static List<String> list01 = null;
	
	/**
	 * 乳品 list-------新的
	 */
	public static List<String> getList01() {
		if(list01 == null) {
			list01 = new ArrayList<String>();
			
			add2Value(list01, "070101");
			add2Value(list01, "070104");
			add2Value(list01, "070105");
			add2Value(list01, "070106");
			add2Value(list01, "070109");
			add2Value(list01, "070115");
			add2Value(list01, "070122");
			add2Value(list01, "070129");
			add2Value(list01, "070140");
			add2Value(list01, "070144");
			add2Value(list01, "070146");
			add2Value(list01, "070147");
			add2Value(list01, "070148");
			add2Value(list01, "070149");
			add2Value(list01, "070151");
			add2Value(list01, "070152");
			add2Value(list01, "070201");
			add2Value(list01, "070202");
			add2Value(list01, "070441");
			add2Value(list01, "070451");
			add2Value(list01, "070452");
			add2Value(list01, "070553");
			add2Value(list01, "070554");
			add2Value(list01, "070555");
			add2Value(list01, "070557");
			add2Value(list01, "070558");

		}
		
		return list01;
	}
	
	
	/**
	 * 饮料 list
	 */
	private static List<String> list02 = null;
	
	/**
	 * 饮料 list
	 */
	public static List<String> getList02() {
		if(list02 == null) {
			list02 = new ArrayList<String>();
			
			add2Value(list02, "070753");
			add2Value(list02, "070754");
			add2Value(list02, "070755");
			add2Value(list02, "070758");
			add2Value(list02, "070759");
			add2Value(list02, "070760");
			add2Value(list02, "070761");
			add2Value(list02, "070762");
			add2Value(list02, "080112");
			add2Value(list02, "080113");
			add2Value(list02, "080114");
			add2Value(list02, "080115");
			add2Value(list02, "080149");
			add2Value(list02, "080161");
			add2Value(list02, "080164");
			add2Value(list02, "080165");
			add2Value(list02, "080168");
			add2Value(list02, "080169");
			add2Value(list02, "080170");
			add2Value(list02, "080171");
			add2Value(list02, "080172");
			add2Value(list02, "080173");
			add2Value(list02, "080174");
			add2Value(list02, "080175");
			add2Value(list02, "080176");
			add2Value(list02, "080177");
			add2Value(list02, "080178");
			add2Value(list02, "080179");
			add2Value(list02, "080180");
			add2Value(list02, "080181");
			add2Value(list02, "080182");
			add2Value(list02, "080183");
			add2Value(list02, "080184");
			add2Value(list02, "080185");
			add2Value(list02, "080186");
			add2Value(list02, "080187");
			add2Value(list02, "080189");
			add2Value(list02, "080190");
			add2Value(list02, "080191");
			add2Value(list02, "080192");
			add2Value(list02, "080194");
			add2Value(list02, "080319");
			add2Value(list02, "080320");
			add2Value(list02, "080321");
			add2Value(list02, "080427");
			add2Value(list02, "080428");
			add2Value(list02, "080429");
			add2Value(list02, "080436");
			add2Value(list02, "080437");
			add2Value(list02, "080438");
			add2Value(list02, "080442");
			add2Value(list02, "080443");
			add2Value(list02, "080444");
			add2Value(list02, "080448");
			add2Value(list02, "080449");
			add2Value(list02, "080450");
			add2Value(list02, "080451");
			add2Value(list02, "080452");
			add2Value(list02, "080453");
			add2Value(list02, "080454");
			add2Value(list02, "080455");
			add2Value(list02, "080456");
			add2Value(list02, "080457");
			add2Value(list02, "081005");
			add2Value(list02, "081006");
			add2Value(list02, "081007");
			add2Value(list02, "081009");
			add2Value(list02, "081010");
			add2Value(list02, "081011");
			add2Value(list02, "081012");
			add2Value(list02, "081013");
			add2Value(list02, "081014");
			add2Value(list02, "081015");
			add2Value(list02, "081016");
			add2Value(list02, "081017");
			add2Value(list02, "081018");
			add2Value(list02, "081019");
			add2Value(list02, "081020");
			add2Value(list02, "081021");
			add2Value(list02, "081022");
			add2Value(list02, "081023");
			add2Value(list02, "081024");
			add2Value(list02, "081025");
			add2Value(list02, "081026");
			add2Value(list02, "081027");
			add2Value(list02, "081028");
			add2Value(list02, "081029");
			add2Value(list02, "081030");
			add2Value(list02, "081031");
			add2Value(list02, "081032");
			add2Value(list02, "081033");
			add2Value(list02, "081034");
			add2Value(list02, "081035");
			add2Value(list02, "081036");
			add2Value(list02, "081037");
			add2Value(list02, "081039");
			add2Value(list02, "081040");
			add2Value(list02, "081041");
			add2Value(list02, "081042");


		}
		
		return list02;
	}
	
	/**
	 * 口袋包 list----取消变成方便
	 */
	private static List<String> list03 = null;
	
	/**
	 * 口袋包 list取消变成方便
	 */
	public static List<String> getList03() {
		if(list03 == null) {
			list03 = new ArrayList<String>();
			
			add2Value(list03, "090112");
			add2Value(list03, "090118");
			add2Value(list03, "090116");
			add2Value(list03, "090110");
			add2Value(list03, "090119");
			add2Value(list03, "090117");
			add2Value(list03, "090125");
			add2Value(list03, "090123");
			add2Value(list03, "090124");
		}
		
		return list03;
	}
	
	
	/**
	 * 米果 list
	 */
	private static List<String> list04 = null;
	
	/**
	 * 米果 list
	 */
	public static List<String> getList04() {
		if(list04 == null) {
			list04 = new ArrayList<String>();
			
			add2Value(list04, "010102");
			add2Value(list04, "010111");
			add2Value(list04, "010114");
			add2Value(list04, "010137");
			add2Value(list04, "010151");
			add2Value(list04, "010154");
			add2Value(list04, "010160");
			add2Value(list04, "010167");
			add2Value(list04, "010209");
			add2Value(list04, "010240");
			add2Value(list04, "010280");
			add2Value(list04, "010318");
			add2Value(list04, "010337");
			add2Value(list04, "010350");
			add2Value(list04, "010351");
			add2Value(list04, "010353");
			add2Value(list04, "010354");
			add2Value(list04, "010357");
			add2Value(list04, "010359");
			add2Value(list04, "010389");
			add2Value(list04, "010393");
			add2Value(list04, "010394");
			add2Value(list04, "010395");
			add2Value(list04, "010519");
			add2Value(list04, "010804");
			add2Value(list04, "010805");
			add2Value(list04, "010830");
			add2Value(list04, "010832");
			add2Value(list04, "011032");
			add2Value(list04, "011058");
			add2Value(list04, "011060");
			add2Value(list04, "011093");
			add2Value(list04, "011095");
			add2Value(list04, "011212");
			add2Value(list04, "011224");
			add2Value(list04, "011233");
			add2Value(list04, "011248");
			add2Value(list04, "011251");
			add2Value(list04, "011259");
			add2Value(list04, "011301");
			add2Value(list04, "011302");
			add2Value(list04, "011303");
			add2Value(list04, "011321");
			add2Value(list04, "011327");
			add2Value(list04, "011328");
			add2Value(list04, "011329");
			add2Value(list04, "011330");
			add2Value(list04, "011331");
			add2Value(list04, "011332");
			add2Value(list04, "011343");
			add2Value(list04, "011344");
			add2Value(list04, "011345");
			add2Value(list04, "011349");
			add2Value(list04, "011350");
			add2Value(list04, "011351");
			add2Value(list04, "011710");
			add2Value(list04, "011718");
			add2Value(list04, "011736");
			add2Value(list04, "011739");
			add2Value(list04, "011745");
//ok
			
//			add2Value(list04, "010102");
//			add2Value(list04, "010114");
//			add2Value(list04, "011233");
//			add2Value(list04, "011248");
//			add2Value(list04, "011251");
//			add2Value(list04, "011259");
//			add2Value(list04, "010151");
//			add2Value(list04, "011212");
//			add2Value(list04, "010804");
//			add2Value(list04, "010160");
//			add2Value(list04, "011060");
//			add2Value(list04, "011058");
//			add2Value(list04, "010280");
//			add2Value(list04, "010805");
//			add2Value(list04, "011718");
//			add2Value(list04, "011736");
//			add2Value(list04, "011739");
//			add2Value(list04, "011745");
//			add2Value(list04, "010209");
//			add2Value(list04, "011093");
//			add2Value(list04, "011327");
//			add2Value(list04, "011331");
//			add2Value(list04, "011332");
//			add2Value(list04, "010389");
//			add2Value(list04, "011343");
//			add2Value(list04, "011344");
//			add2Value(list04, "011345");
//			add2Value(list04, "011349");
//			add2Value(list04, "011350");
//			add2Value(list04, "011351");
//			add2Value(list04, "010350");
//			add2Value(list04, "010351");
//			add2Value(list04, "010359");
//			add2Value(list04, "010353");
//			add2Value(list04, "010354");
//			add2Value(list04, "010357");

		}
		
		return list04;
	}
	
	
	
	/**
	 * 糖果 list
	 */
	private static List<String> list05 = null;
	
	/**
	 * 糖果 list
	 */
	public static List<String> getList05() {
		if(list05 == null) {
			list05 = new ArrayList<String>();
			
			add2Value(list05, "061456");
			add2Value(list05, "061369");
			add2Value(list05, "061241");
			add2Value(list05, "061269");
			add2Value(list05, "061447");
			add2Value(list05, "061451");
			add2Value(list05, "061242");
			add2Value(list05, "061229");
			add2Value(list05, "060270");
			add2Value(list05, "060271");
			add2Value(list05, "060709");
			add2Value(list05, "060710");
			add2Value(list05, "060764");
			add2Value(list05, "060728");
			add2Value(list05, "060729");
			add2Value(list05, "060733");
			add2Value(list05, "060734");
			add2Value(list05, "060775");
			add2Value(list05, "060776");
			add2Value(list05, "061226");
			add2Value(list05, "061227");
			add2Value(list05, "060704");
			add2Value(list05, "060705");
			add2Value(list05, "060706");
			add2Value(list05, "060707");
			add2Value(list05, "060708");
			add2Value(list05, "060711");
			add2Value(list05, "060712");
			add2Value(list05, "060731");
			add2Value(list05, "061281");
			add2Value(list05, "061431");
			add2Value(list05, "060206");
			add2Value(list05, "060207");
			add2Value(list05, "060208");
			add2Value(list05, "060209");
			add2Value(list05, "060299");
			add2Value(list05, "060713");
			add2Value(list05, "060714");
			add2Value(list05, "060732");
			add2Value(list05, "061225");

//ok
		}
		
		return list05;
	}
	
	
	
	
	/**
	 * 果冻 list
	 */
	private static List<String> list06 = null;
	
	/**
	 * 果冻 list
	 */
	public static List<String> getList06() {
		if(list06 == null) {
			list06 = new ArrayList<String>();
			
			add2Value(list06, "061041");
			add2Value(list06, "061042");
			add2Value(list06, "061533");
			add2Value(list06, "061534");
			add2Value(list06, "061535");
			add2Value(list06, "061583");
			add2Value(list06, "061584");
			add2Value(list06, "061585");
			add2Value(list06, "061575");
			add2Value(list06, "061576");
			add2Value(list06, "061577");
			add2Value(list06, "061578");
			add2Value(list06, "060153");
			add2Value(list06, "060161");
			add2Value(list06, "060162");
			add2Value(list06, "060163");
			add2Value(list06, "060427");
			add2Value(list06, "060428");
			add2Value(list06, "061548");
			add2Value(list06, "061549");
			add2Value(list06, "061550");
			add2Value(list06, "061551");
			add2Value(list06, "061552");
			add2Value(list06, "061553");
			add2Value(list06, "061637");
			add2Value(list06, "061638");
			add2Value(list06, "061639");
			add2Value(list06, "061043");
			add2Value(list06, "061044");
			add2Value(list06, "061022");
			add2Value(list06, "061023");
			add2Value(list06, "061024");
			add2Value(list06, "061025");
			add2Value(list06, "061011");
			add2Value(list06, "061014");
//ok
		}
		
		return list06;
	}
	
	
	
	
	
	
	/**
	 * 休闲 list --- 变成休一
	 */
	private static List<String> list07 = null;
	
	/**
	 * 休闲 list--- 变成休一
	 */
	public static List<String> getList07() {
		if(list07 == null) {
			list07 = new ArrayList<String>();
			
			add2Value(list07, "020503");
			add2Value(list07, "020538");
			add2Value(list07, "020559");
			add2Value(list07, "020506");
			add2Value(list07, "020560");
			add2Value(list07, "020510");
			add2Value(list07, "050206");
			add2Value(list07, "050272");
			add2Value(list07, "050273");
			add2Value(list07, "050274");
			add2Value(list07, "050726");
			add2Value(list07, "050727");
			add2Value(list07, "050509");

			
		}
		
		return list07;
	}
	
	
	/**
	 * 休闲 list --- 变成休二
	 */
	private static List<String> list08 = null;
	
	/**
	 * 休闲 list--- 变成休二
	 */
	public static List<String> getList08() {
		if(list08 == null) {
			list08 = new ArrayList<String>();
			
			add2Value(list08, "040150");
			add2Value(list08, "040138");
			add2Value(list08, "030222");
			add2Value(list08, "030223");
			add2Value(list08, "030225");
			add2Value(list08, "030226");
			add2Value(list08, "030221");
			add2Value(list08, "030224");
			add2Value(list08, "030105");
			add2Value(list08, "030126");
			add2Value(list08, "030143");
			add2Value(list08, "030144");
			add2Value(list08, "030101");
			add2Value(list08, "030127");
			add2Value(list08, "030145");
			add2Value(list08, "030146");
			add2Value(list08, "030103");
			add2Value(list08, "030128");
			add2Value(list08, "030147");
			add2Value(list08, "030148");
			add2Value(list08, "030611");
			add2Value(list08, "030612");
			add2Value(list08, "030613");
			add2Value(list08, "030601");
			add2Value(list08, "030606");
			add2Value(list08, "030608");
			add2Value(list08, "030609");
			add2Value(list08, "030610");
			add2Value(list08, "030621");
			add2Value(list08, "030635");
			add2Value(list08, "030636");
			add2Value(list08, "030429");
			add2Value(list08, "030430");
			add2Value(list08, "030431");
			add2Value(list08, "030432");
			add2Value(list08, "030413");
			add2Value(list08, "030414");
			add2Value(list08, "030419");
			add2Value(list08, "030420");
			add2Value(list08, "030401");
			add2Value(list08, "030402");
			add2Value(list08, "030520");
			add2Value(list08, "030521");
			add2Value(list08, "030522");
			add2Value(list08, "030501");
			add2Value(list08, "180166");
			add2Value(list08, "180128");
			add2Value(list08, "180169");
			add2Value(list08, "180172");
			add2Value(list08, "180173");
			add2Value(list08, "020306");
			add2Value(list08, "020303");
			add2Value(list08, "020202");
			add2Value(list08, "020257");
			add2Value(list08, "020242");
			add2Value(list08, "020238");
			add2Value(list08, "020265");
			add2Value(list08, "020240");
			add2Value(list08, "020101");
			add2Value(list08, "020102");
			add2Value(list08, "020108");
			add2Value(list08, "020109");
			add2Value(list08, "020110");

			
		}
		
		return list08;
	}
	////////////////=================================================================================
	
	
	/**
	 * 不管控品项 list
	 */
	private static List<String> buGuanList = null;
	
	/**
	 * 不管控品项 list
	 */
	public static List<String> getBuGuanList() {
		if(buGuanList == null) {
			buGuanList = new ArrayList<String>();
			
			add2Value(buGuanList, "040134");
			add2Value(buGuanList, "040134B");
			add2Value(buGuanList, "040121");
			add2Value(buGuanList, "040121B");
			add2Value(buGuanList, "040139");
			add2Value(buGuanList, "040149");
			add2Value(buGuanList, "040149B");
			add2Value(buGuanList, "040105A");
			add2Value(buGuanList, "040105B");
			add2Value(buGuanList, "040125");
			add2Value(buGuanList, "040125B");
			add2Value(buGuanList, "040124");
			add2Value(buGuanList, "040124B");
			
			add2Value(buGuanList, "080436");
			add2Value(buGuanList, "080437");
			add2Value(buGuanList, "080438");
			add2Value(buGuanList, "081015");
			add2Value(buGuanList, "081030");
			add2Value(buGuanList, "081016");
			add2Value(buGuanList, "081031");
			add2Value(buGuanList, "081032");
			add2Value(buGuanList, "081033");
			add2Value(buGuanList, "080183");
			add2Value(buGuanList, "081029");
			add2Value(buGuanList, "070761");
			add2Value(buGuanList, "070762");
			add2Value(buGuanList, "081013");
			add2Value(buGuanList, "061269");
			add2Value(buGuanList, "061451");
			add2Value(buGuanList, "061447");
		}
		
		return buGuanList;
	}
	
	private static List<String> Xiuxianbuguan = null;
	public static List<String> getXiuxianbuguan() {
		if(Xiuxianbuguan==null){
			Xiuxianbuguan = new ArrayList<String>();
			Xiuxianbuguan.add("000000306106170300");
			Xiuxianbuguan.add("000000306106145600");
			Xiuxianbuguan.add("000000306106149000");
			Xiuxianbuguan.add("000000306106170600");
			Xiuxianbuguan.add("000000306106170700");
			Xiuxianbuguan.add("000000306106111000");
			Xiuxianbuguan.add("000000306106111600");
			Xiuxianbuguan.add("000000306106149200");
			Xiuxianbuguan.add("000000306106149300");
			Xiuxianbuguan.add("000000306102050300");
			Xiuxianbuguan.add("000000306102050400");
			Xiuxianbuguan.add("000000306102050500");
			Xiuxianbuguan.add("000000306102050600");
			Xiuxianbuguan.add("000000306102051000");
			Xiuxianbuguan.add("000000306102051100");
			Xiuxianbuguan.add("000000306102051200");
			Xiuxianbuguan.add("000000306102051500");
			Xiuxianbuguan.add("000000306102051600");
			Xiuxianbuguan.add("000000306102051700");
			Xiuxianbuguan.add("000000306102051800");
			Xiuxianbuguan.add("000000306102051900");
			Xiuxianbuguan.add("000000306102052300");
			Xiuxianbuguan.add("000000306102052400");
			Xiuxianbuguan.add("000000306102052500");
			Xiuxianbuguan.add("000000306102052600");
			Xiuxianbuguan.add("000000306102052700");
			Xiuxianbuguan.add("000000306102052800");
			Xiuxianbuguan.add("000000306102053200");
			Xiuxianbuguan.add("000000306102053300");
			Xiuxianbuguan.add("000000306102053400");
			Xiuxianbuguan.add("000000306102053500");
			Xiuxianbuguan.add("000000306102053800");
			Xiuxianbuguan.add("000000306102055100");
			Xiuxianbuguan.add("000000306102055200");
			Xiuxianbuguan.add("000000306102055400");
			Xiuxianbuguan.add("000000306102055500");
			Xiuxianbuguan.add("000000306102055600");
			Xiuxianbuguan.add("000000306102055700");
			Xiuxianbuguan.add("000000306102055800");
			Xiuxianbuguan.add("000000306102055900");
			Xiuxianbuguan.add("000000306102056000");
			Xiuxianbuguan.add("000000306102056100");
			Xiuxianbuguan.add("000000306102056300");
			Xiuxianbuguan.add("000000306102056400");
			Xiuxianbuguan.add("000000306102117900");
			Xiuxianbuguan.add("3061020518H0");
			Xiuxianbuguan.add("000000306102054600");
			Xiuxianbuguan.add("000000306102054700");
			Xiuxianbuguan.add("000000306102054800");
			Xiuxianbuguan.add("000000306102056200");
			Xiuxianbuguan.add("000000306102116800");
			Xiuxianbuguan.add("000000306105020500");
			Xiuxianbuguan.add("000000306105020600");
			Xiuxianbuguan.add("000000306105020700");
			Xiuxianbuguan.add("000000306105020800");
			Xiuxianbuguan.add("000000306105021900");
			Xiuxianbuguan.add("000000306105022000");
			Xiuxianbuguan.add("000000306105022100");
			Xiuxianbuguan.add("000000306105022200");
			Xiuxianbuguan.add("000000306105022300");
			Xiuxianbuguan.add("000000306105022400");
			Xiuxianbuguan.add("000000306105022500");
			Xiuxianbuguan.add("000000306105022600");
			Xiuxianbuguan.add("000000306105022700");
			Xiuxianbuguan.add("000000306105022800");
			Xiuxianbuguan.add("000000306105022900");
			Xiuxianbuguan.add("000000306105023000");
			Xiuxianbuguan.add("000000306105023100");
			Xiuxianbuguan.add("000000306105023200");
			Xiuxianbuguan.add("000000306105023300");
			Xiuxianbuguan.add("000000306105023400");
			Xiuxianbuguan.add("000000306105023500");
			Xiuxianbuguan.add("000000306105023900");
			Xiuxianbuguan.add("000000306105024000");
			Xiuxianbuguan.add("000000306105024200");
			Xiuxianbuguan.add("000000306105024400");
			Xiuxianbuguan.add("000000306105024500");
			Xiuxianbuguan.add("000000306105024600");
			Xiuxianbuguan.add("000000306105024800");
			Xiuxianbuguan.add("000000306105024900");
			Xiuxianbuguan.add("000000306105025000");
			Xiuxianbuguan.add("000000306105025100");
			Xiuxianbuguan.add("000000306105025200");
			Xiuxianbuguan.add("000000306105025300");
			Xiuxianbuguan.add("000000306105025400");
			Xiuxianbuguan.add("000000306105025500");
			Xiuxianbuguan.add("000000306105025600");
			Xiuxianbuguan.add("000000306105025700");
			Xiuxianbuguan.add("000000306105025800");
			Xiuxianbuguan.add("000000306105025900");
			Xiuxianbuguan.add("000000306105026000");
			Xiuxianbuguan.add("000000306105026100");
			Xiuxianbuguan.add("000000306105026200");
			Xiuxianbuguan.add("000000306105026300");
			Xiuxianbuguan.add("000000306105026400");
			Xiuxianbuguan.add("000000306105026900");
			Xiuxianbuguan.add("000000306105027000");
			Xiuxianbuguan.add("000000306105027100");
			Xiuxianbuguan.add("000000306105027200");
			Xiuxianbuguan.add("000000306105027300");
			Xiuxianbuguan.add("000000306105027400");
			Xiuxianbuguan.add("000000306105027500");
			Xiuxianbuguan.add("000000306105027600");
			Xiuxianbuguan.add("000000306105027800");
			Xiuxianbuguan.add("000000306105027900");
			Xiuxianbuguan.add("000000306105028100");
			Xiuxianbuguan.add("000000306105028200");
			Xiuxianbuguan.add("000000306105028300");
			Xiuxianbuguan.add("000000306105030100");
			Xiuxianbuguan.add("000000306105030500");
			Xiuxianbuguan.add("000000306105030600");
			Xiuxianbuguan.add("000000306105031400");
			Xiuxianbuguan.add("000000306105032000");
			Xiuxianbuguan.add("000000306105040500");
			Xiuxianbuguan.add("000000306105041000");
			Xiuxianbuguan.add("000000306105041100");
			Xiuxianbuguan.add("000000306105041200");
			Xiuxianbuguan.add("000000306105041300");
			Xiuxianbuguan.add("000000306105041400");
			Xiuxianbuguan.add("000000306105050200");
			Xiuxianbuguan.add("000000306105050900");
			Xiuxianbuguan.add("000000306105070600");
			Xiuxianbuguan.add("000000306105070700");
			Xiuxianbuguan.add("000000306105070800");
			Xiuxianbuguan.add("000000306105070900");
			Xiuxianbuguan.add("000000306105071000");
			Xiuxianbuguan.add("000000306105071200");
			Xiuxianbuguan.add("000000306105071300");
			Xiuxianbuguan.add("000000306105071400");
			Xiuxianbuguan.add("000000306105071500");
			Xiuxianbuguan.add("000000306105071600");
			Xiuxianbuguan.add("000000306105071700");
			Xiuxianbuguan.add("000000306105071800");
			Xiuxianbuguan.add("000000306105071900");
			Xiuxianbuguan.add("000000306105072000");
			Xiuxianbuguan.add("000000306105072500");
			Xiuxianbuguan.add("000000306105072600");
			Xiuxianbuguan.add("000000306105072700");
			Xiuxianbuguan.add("000000306105072800");
			Xiuxianbuguan.add("000000306105072900");
			Xiuxianbuguan.add("000000306105073400");
			Xiuxianbuguan.add("000000306105080100");
			Xiuxianbuguan.add("000000306105080200");
			Xiuxianbuguan.add("000000306105080300");
			Xiuxianbuguan.add("000000306105080400");
			Xiuxianbuguan.add("000000306105080500");
			Xiuxianbuguan.add("000000306105080600");
			Xiuxianbuguan.add("000000306105080700");
			Xiuxianbuguan.add("000000306105081400");
			Xiuxianbuguan.add("000000306106148400");
			Xiuxianbuguan.add("000000306106148500");
			Xiuxianbuguan.add("000000306106148600");

		}
		return Xiuxianbuguan;
	}
	
	
	private static List<String> MilkForMarch = null;
	public static List<String> getMilkForMarch() {
		if(MilkForMarch==null){
			MilkForMarch = new ArrayList<String>();
			MilkForMarch.add("000000306107020200");
			MilkForMarch.add("000000306107014400");
			MilkForMarch.add("000000306107010500");
			MilkForMarch.add("000000306107015100");
			MilkForMarch.add("000000306107010100");
			MilkForMarch.add("000000306107020100");
			MilkForMarch.add("000000306107010400");
			MilkForMarch.add("000000306107014600");
			MilkForMarch.add("000000306107015200");
			MilkForMarch.add("000000306107011500");
			MilkForMarch.add("000000306107012900");
		}
		return MilkForMarch;
	}
	
	private static List<String> wuguList = null;
	public static List<String> getWuGuList() {
		if(wuguList == null) {
			wuguList = new ArrayList<String>();
			wuguList.add("000000306107014600");
	        wuguList.add("000000306107055300");
	        wuguList.add("000000306107055400");
	        wuguList.add("000000306107055500");
	        wuguList.add("000000306107055800");
	        wuguList.add("000000306107055700");
		}
		return wuguList;
	}
	
	private static List<String> miguoList = null;
	public static List<String> getMiguoList() {
		if(wuguList == null) {
			miguoList = new ArrayList<String>();
			//miguoList.add("000000306101020900");
			//miguoList.add("000000306101109300");
			miguoList.add("000000306101124800");
			//miguoList.add("000000306101125100");
			miguoList.add("000000306101173600");
			//miguoList.add("000000306101173900");
		}
		return miguoList;
	}
	private static List<String> CDIxiuxianbuguan = null;
	public static List<String> getCDIxiuxianbuguan() {
		if(CDIxiuxianbuguan == null) {
			CDIxiuxianbuguan = new ArrayList<String>();
			
			CDIxiuxianbuguan.add("000000306107055300");
			CDIxiuxianbuguan.add("000000306107055400");
			CDIxiuxianbuguan.add("000000306107055700");
			CDIxiuxianbuguan.add("000000306107055500");
			CDIxiuxianbuguan.add("000000306107055600");
			CDIxiuxianbuguan.add("000000306107055800");
			CDIxiuxianbuguan.add("000000306107040100");
			CDIxiuxianbuguan.add("000000306107041000");
			CDIxiuxianbuguan.add("000000306107041100");
			CDIxiuxianbuguan.add("000000306107041200");
			CDIxiuxianbuguan.add("000000306107041300");
			CDIxiuxianbuguan.add("000000306107041400");
			CDIxiuxianbuguan.add("000000306107041500");
			CDIxiuxianbuguan.add("000000306107041600");
			CDIxiuxianbuguan.add("000000306107041700");
			CDIxiuxianbuguan.add("000000306107041800");
			CDIxiuxianbuguan.add("000000306107041900");
			CDIxiuxianbuguan.add("000000306107042000");
			CDIxiuxianbuguan.add("000000306107042100");
			CDIxiuxianbuguan.add("000000306107042700");
			CDIxiuxianbuguan.add("000000306107042800");
			CDIxiuxianbuguan.add("000000306107042900");
			CDIxiuxianbuguan.add("000000306107043000");
			CDIxiuxianbuguan.add("000000306107043100");
			CDIxiuxianbuguan.add("000000306107043200");
			CDIxiuxianbuguan.add("000000306107043300");
			CDIxiuxianbuguan.add("000000306107043400");
			CDIxiuxianbuguan.add("000000306107044100");
			CDIxiuxianbuguan.add("000000306107044300");
			CDIxiuxianbuguan.add("000000306107044400");
			CDIxiuxianbuguan.add("000000306107044500");
			CDIxiuxianbuguan.add("000000306107044600");
			CDIxiuxianbuguan.add("000000306107044700");
			CDIxiuxianbuguan.add("000000306107044800");
			CDIxiuxianbuguan.add("000000306107044900");
			CDIxiuxianbuguan.add("000000306107045000");
			CDIxiuxianbuguan.add("000000306107045100");
			CDIxiuxianbuguan.add("000000306107045200");
			CDIxiuxianbuguan.add("000000306107045300");
			CDIxiuxianbuguan.add("000000306107045600");
			CDIxiuxianbuguan.add("000000306107112100");
			CDIxiuxianbuguan.add("000000306107112300");
			CDIxiuxianbuguan.add("000000306107112700");
			CDIxiuxianbuguan.add("000000306106170300");
			CDIxiuxianbuguan.add("000000306106145600");
			CDIxiuxianbuguan.add("000000306106149000");
			CDIxiuxianbuguan.add("000000306106170600");
			CDIxiuxianbuguan.add("000000306106170700");
			CDIxiuxianbuguan.add("000000306106111000");
			CDIxiuxianbuguan.add("000000306106111600");
			CDIxiuxianbuguan.add("000000306106149200");
			CDIxiuxianbuguan.add("000000306106149300");
			CDIxiuxianbuguan.add("000000306109011000");
			CDIxiuxianbuguan.add("000000306109011200");
			CDIxiuxianbuguan.add("000000306109011600");
			CDIxiuxianbuguan.add("000000306109011700");
			CDIxiuxianbuguan.add("000000306109011800");
			CDIxiuxianbuguan.add("000000306109011900");
			CDIxiuxianbuguan.add("000000306109012000");
			CDIxiuxianbuguan.add("000000306109012100");
			CDIxiuxianbuguan.add("000000306109012200");
			CDIxiuxianbuguan.add("000000306109012300");
			CDIxiuxianbuguan.add("000000306109012400");
			CDIxiuxianbuguan.add("000000306109012500");
			CDIxiuxianbuguan.add("000000306109012600");
			CDIxiuxianbuguan.add("000000306109012700");
			CDIxiuxianbuguan.add("000000306109012800");
			CDIxiuxianbuguan.add("000000306109012900");
			CDIxiuxianbuguan.add("000000306109013000");
			CDIxiuxianbuguan.add("000000306109013100");
			CDIxiuxianbuguan.add("000000306109013200");
			CDIxiuxianbuguan.add("000000306109013300");
			CDIxiuxianbuguan.add("000000306109013400");
			CDIxiuxianbuguan.add("000000306109013500");
			CDIxiuxianbuguan.add("000000306109013600");
			CDIxiuxianbuguan.add("000000306109013700");
			CDIxiuxianbuguan.add("000000306109110100");
			CDIxiuxianbuguan.add("000000306109110200");
			CDIxiuxianbuguan.add("000000306109110300");
			//qita
			CDIxiuxianbuguan.add("000000306105027200");
			CDIxiuxianbuguan.add("000000306105027300");
			CDIxiuxianbuguan.add("000000306105027400");
			CDIxiuxianbuguan.add("000000306105072600");
			CDIxiuxianbuguan.add("000000306107078600");
			CDIxiuxianbuguan.add("000000306107078700");
			CDIxiuxianbuguan.add("000000306108042700");
			CDIxiuxianbuguan.add("000000306108042800");
			CDIxiuxianbuguan.add("000000306108042900");
			CDIxiuxianbuguan.add("000000306108043600");
			CDIxiuxianbuguan.add("000000306108043700");
			CDIxiuxianbuguan.add("000000306108043800");
			CDIxiuxianbuguan.add("000000306108101900");
			CDIxiuxianbuguan.add("000000306108102000");
			CDIxiuxianbuguan.add("000000306108103400");
			CDIxiuxianbuguan.add("000000306108103500");
		}
		return CDIxiuxianbuguan;
	}
	
	private static List<String> xiaoxiongList = null;
	public static List<String> getXiaoXiongList() {
		if(xiaoxiongList == null) {
			xiaoxiongList = new ArrayList<String>();
			xiaoxiongList.add("000000306118016600");
			xiaoxiongList.add("000000306118017200");
	        
		}
		return xiaoxiongList;
	}
	private static List<String> rupinList = null;
	public static List<String> getRuPinList() {
		if(rupinList == null) {
			rupinList = new ArrayList<String>();
			rupinList.add("000000306107011500");
			rupinList.add("000000306107012900");
			rupinList.add("000000306107013000");
			rupinList.add("000000306107014700");
			rupinList.add("000000306107014600");
			rupinList.add("000000306107015000");
			rupinList.add("000000306107015200");
			rupinList.add("000000306107012200");
			rupinList.add("000000306107013400");
			rupinList.add("000000306107014000");
			rupinList.add("000000306107014100");
			rupinList.add("000000306107014400");
			rupinList.add("000000306107021400");
			rupinList.add("000000306107010100");
			rupinList.add("000000306107010400");
			rupinList.add("000000306107010500");
			rupinList.add("000000306107010600");
			rupinList.add("000000306107010700");
			rupinList.add("000000306107010800");
			rupinList.add("000000306107010900");
			rupinList.add("000000306107013500");
			rupinList.add("000000306107014800");
			rupinList.add("000000306107014900");
			rupinList.add("000000306107015100");
			rupinList.add("000000306107020100");
			rupinList.add("000000306107020200");

			
		}
		return rupinList;
	}
	
	private static List<String> rupinguankongList = null;
	public static List<String> getRuPinGuanKongList() {
		if(rupinguankongList == null) {
			rupinguankongList = new ArrayList<String>();
			rupinguankongList.add("000000306107011500");
			rupinguankongList.add("000000306107012900");
			rupinguankongList.add("000000306107014400");
			rupinguankongList.add("000000306107010100");
			rupinguankongList.add("000000306107010400");
			rupinguankongList.add("000000306107010500");
		}
		return rupinguankongList;
	}
	private static List<String> xiuxianList = null;
	public static List<String> getXiuXianList() {
		if(xiuxianList == null) {
			xiuxianList = new ArrayList<String>();
			xiuxianList.add("000000306101010200");
			xiuxianList.add("000000306101123300");
			xiuxianList.add("000000306101016700");
			xiuxianList.add("000000306101122400");
			xiuxianList.add("000000306101124800");
			xiuxianList.add("000000306101125100");
			xiuxianList.add("000000306101125900");
			xiuxianList.add("000000306101011100");
			xiuxianList.add("000000306101015100");
			xiuxianList.add("000000306101083200");
			xiuxianList.add("000000306101121200");
			xiuxianList.add("000000306101011400");
			xiuxianList.add("000000306101015400");
			xiuxianList.add("000000306101013700");
			xiuxianList.add("000000306101080400");
			xiuxianList.add("000000306101106000");
			xiuxianList.add("000000306101105800");
			xiuxianList.add("000000306101028000");
			xiuxianList.add("000000306101024000");
			xiuxianList.add("000000306101109500");
			xiuxianList.add("000000306101016000");
			xiuxianList.add("000000306101171800");
			xiuxianList.add("000000306101103200");
			xiuxianList.add("000000306101171000");
			xiuxianList.add("000000306101173600");
			xiuxianList.add("000000306101173900");
			xiuxianList.add("000000306101174500");
			xiuxianList.add("000000306101020900");
			xiuxianList.add("000000306101083000");
			xiuxianList.add("000000306101109300");
			xiuxianList.add("000000306101080500");
			xiuxianList.add("000000306101031800");
			xiuxianList.add("000000306101051900");
			xiuxianList.add("000000306101132700");
			xiuxianList.add("000000306101033700");
			xiuxianList.add("000000306101132100");
			xiuxianList.add("000000306101133100");
			xiuxianList.add("000000306101133200");
			xiuxianList.add("000000306101038900");
			xiuxianList.add("000000306101132800");
			xiuxianList.add("000000306101132900");
			xiuxianList.add("000000306101133000");
			xiuxianList.add("000000306101134300");
			xiuxianList.add("000000306101134400");
			xiuxianList.add("000000306101134500");
			xiuxianList.add("000000306101134900");
			xiuxianList.add("000000306101135000");
			xiuxianList.add("000000306101135100");
			xiuxianList.add("000000306101035000");
			xiuxianList.add("000000306101035100");
			xiuxianList.add("000000306101035900");
			xiuxianList.add("000000306101039300");
			xiuxianList.add("000000306101039400");
			xiuxianList.add("000000306101039500");
			xiuxianList.add("000000306101035300");
			xiuxianList.add("000000306101035400");
			xiuxianList.add("000000306101035700");
			xiuxianList.add("000000306101130100");
			xiuxianList.add("000000306101130200");
			xiuxianList.add("000000306101130300");
			
			xiuxianList.add("000000306106104100");
			xiuxianList.add("000000306106104200");
			xiuxianList.add("000000306106153300");
			xiuxianList.add("000000306106153400");
			xiuxianList.add("000000306106153500");
			xiuxianList.add("000000306106158300");
			xiuxianList.add("000000306106158400");
			xiuxianList.add("000000306106158500");
			xiuxianList.add("000000306106157500");
			xiuxianList.add("000000306106157600");
			xiuxianList.add("000000306106157700");
			xiuxianList.add("000000306106157800");
			xiuxianList.add("000000306106015300");
			xiuxianList.add("000000306106016100");
			xiuxianList.add("000000306106016200");
			xiuxianList.add("000000306106016300");
			xiuxianList.add("000000306106042700");
			xiuxianList.add("000000306106042800");
			xiuxianList.add("000000306106154800");
			xiuxianList.add("000000306106154900");
			xiuxianList.add("000000306106155000");
			xiuxianList.add("000000306106155100");
			xiuxianList.add("000000306106155200");
			xiuxianList.add("000000306106155300");
			xiuxianList.add("000000306106163700");
			xiuxianList.add("000000306106163800");
			xiuxianList.add("000000306106163900");
			xiuxianList.add("000000306106104300");
			xiuxianList.add("000000306106104400");
			//xiuxianList.add("000000306106136900");
			//xiuxianList.add("000000306106124100");
			//xiuxianList.add("000000306106126900");
			//xiuxianList.add("000000306106144700");
			//xiuxianList.add("000000306106145100");
			//xiuxianList.add("000000306106124200");
			xiuxianList.add("000000306106122900");
			xiuxianList.add("000000306106027000");
			xiuxianList.add("000000306106027100");
			xiuxianList.add("000000306106070900");
			xiuxianList.add("000000306106071000");
			xiuxianList.add("000000306106076400");
			xiuxianList.add("000000306106072800");
			xiuxianList.add("000000306106072900");
			xiuxianList.add("000000306106073300");
			xiuxianList.add("000000306106073400");
			xiuxianList.add("000000306106077500");
			xiuxianList.add("000000306106077600");
			xiuxianList.add("000000306106122600");
			xiuxianList.add("000000306106122700");
			xiuxianList.add("000000306106070400");
			xiuxianList.add("000000306106070500");
			xiuxianList.add("000000306106070600");
			xiuxianList.add("000000306106070700");
			xiuxianList.add("000000306106070800");
			xiuxianList.add("000000306106071100");
			xiuxianList.add("000000306106071200");
			xiuxianList.add("000000306106073100");
			xiuxianList.add("000000306106128100");
			xiuxianList.add("000000306106143100");
			xiuxianList.add("000000306106020600");
			xiuxianList.add("000000306106020700");
			xiuxianList.add("000000306106020800");
			xiuxianList.add("000000306106020900");
			xiuxianList.add("000000306106029900");
			xiuxianList.add("000000306106071300");
			xiuxianList.add("000000306106071400");
			xiuxianList.add("000000306106073200");
			xiuxianList.add("000000306106122500");
			xiuxianList.add("000000306106102200");
			xiuxianList.add("000000306106102300");
			xiuxianList.add("000000306106102400");
			xiuxianList.add("000000306106102500");
			xiuxianList.add("000000306106101100");
			xiuxianList.add("000000306106101400");

		}
	return xiuxianList;
	}
	private static List<String> yinpinList = null;
	/**
	 * 饮品品项 list
	 */
	public static List<String> getYinPinList() {
		if(yinpinList == null) {
			yinpinList = new ArrayList<String>();
			yinpinList.add("000000306108017900");
			yinpinList.add("000000306108018000");
			yinpinList.add("000000306108018600");
			yinpinList.add("000000306108018700");
			yinpinList.add("000000306108101900");
			yinpinList.add("000000306108102000");
			yinpinList.add("000000306108019100");
			yinpinList.add("000000306108019200");
			yinpinList.add("000000306108102700");
			yinpinList.add("000000306108102800");
			yinpinList.add("000000306108016800");
			yinpinList.add("000000306108016900");
			yinpinList.add("000000306108017700");
			yinpinList.add("000000306108017800");
			yinpinList.add("000000306108018100");
			yinpinList.add("000000306108018200");
			yinpinList.add("000000306108018400");
			yinpinList.add("000000306108018500");
			yinpinList.add("000000306108018900");
			yinpinList.add("000000306108019000");
			yinpinList.add("000000306108102300");
			yinpinList.add("000000306108102400");
			yinpinList.add("000000306108100900");
			yinpinList.add("000000306108101000");
			yinpinList.add("000000306108101100");
			yinpinList.add("000000306108101200");
			yinpinList.add("000000306108102100");
			yinpinList.add("000000306108102200");
			yinpinList.add("000000306108101500");
			yinpinList.add("000000306108101600");
			yinpinList.add("000000306108103000");
			yinpinList.add("000000306108103100");
			yinpinList.add("000000306108103600");
			yinpinList.add("000000306108103700");
			yinpinList.add("000000306108014900");
			yinpinList.add("000000306108016100");
			yinpinList.add("000000306108017600");
			yinpinList.add("000000306108019400");
			yinpinList.add("000000306108102500");
			yinpinList.add("000000306108102600");
			yinpinList.add("000000306108101700");
			yinpinList.add("000000306108101800");
			yinpinList.add("000000306108103200");
			yinpinList.add("000000306108103300");
			yinpinList.add("000000306108103400");
			yinpinList.add("000000306108103500");
			yinpinList.add("000000306108018300");
			yinpinList.add("000000306108102900");
			yinpinList.add("000000306108042700");
			yinpinList.add("000000306108042800");
			yinpinList.add("000000306108042900");
			yinpinList.add("000000306108043600");
			yinpinList.add("000000306108043700");
			yinpinList.add("000000306108043800");
			yinpinList.add("000000306108104200");
			yinpinList.add("000000306108045400");
			yinpinList.add("000000306108045500");
			yinpinList.add("000000306108045600");
			yinpinList.add("000000306108045700");
			yinpinList.add("000000306108044800");
			yinpinList.add("000000306108044900");
			yinpinList.add("000000306108045000");
			yinpinList.add("000000306108044200");
			yinpinList.add("000000306108044300");
			yinpinList.add("000000306108044400");
			yinpinList.add("000000306108045100");
			yinpinList.add("000000306108045200");
			yinpinList.add("000000306108045300");
			yinpinList.add("000000306107075300");
			yinpinList.add("000000306107075400");
			yinpinList.add("000000306107075500");
			yinpinList.add("000000306107075800");
			yinpinList.add("000000306107075900");
			yinpinList.add("000000306107076000");
			yinpinList.add("000000306107076100");
			yinpinList.add("000000306107076200");
			yinpinList.add("000000306108103900");
			yinpinList.add("000000306108104000");
			yinpinList.add("000000306108104100");
			yinpinList.add("000000306108011200");
			yinpinList.add("000000306108011300");
			yinpinList.add("000000306108011400");
			yinpinList.add("000000306108011500");
			yinpinList.add("000000306108016400");
			yinpinList.add("000000306108016500");
			yinpinList.add("000000306108017000");
			yinpinList.add("000000306108017100");
			yinpinList.add("000000306108017200");
			yinpinList.add("000000306108017300");
			yinpinList.add("000000306108017400");
			yinpinList.add("000000306108100500");
			yinpinList.add("000000306108100600");
			yinpinList.add("000000306108100700");
			yinpinList.add("000000306108017500");
			yinpinList.add("000000306108101300");
			yinpinList.add("000000306108101400");
			yinpinList.add("000000306108031900");
			yinpinList.add("000000306108032000");
			yinpinList.add("000000306108032100");
			
			yinpinList.add("000000306109011200");//方便
			yinpinList.add("000000306109011800");
			yinpinList.add("000000306109011600");
			yinpinList.add("000000306109011000");
			yinpinList.add("000000306109011900");
			yinpinList.add("000000306109011700");

		}
		return yinpinList;
	}
	/**
	 * 按最高供货量管控品项 list，但不能保证数据库里面都有其对应的最高供货量
	 */
	private static List<String> useMaxList = null;
	
	/**
	 * 按最高供货量管控品项 list，但不能保证数据库里面都有其对应的最高供货量
	 */
	public static List<String> getUseMaxList() {
		if(useMaxList == null) {
			useMaxList = new ArrayList<String>();
			
			add2Value(useMaxList, "070144");
			add2Value(useMaxList, "070105");
			add2Value(useMaxList, "070151");
			add2Value(useMaxList, "070101");
			add2Value(useMaxList, "070201");
			add2Value(useMaxList, "070202");
			add2Value(useMaxList, "070104");
			add2Value(useMaxList, "070146");
			add2Value(useMaxList, "010209");
			add2Value(useMaxList, "011093");
			add2Value(useMaxList, "011736");
			add2Value(useMaxList, "011739");
			add2Value(useMaxList, "011248");
			add2Value(useMaxList, "011251");
			
			useMaxList.addAll(getTunhuoList());
		}
		
		return useMaxList;
	}
	
	/**
	 * 按最高供货量管控品项 list 的一个子集，包含6个品项，关联三级地市
	 */
	private static List<String> useMax6List = null;
	
	/**
	 * 按最高供货量管控品项 list 的一个子集，包含6个品项，关联三级地市
	 */
	public static List<String> getUseMax6List() {
		if(useMax6List == null) {
			useMax6List = new ArrayList<String>();
			
			add2Value(useMax6List, "010209");
			add2Value(useMax6List, "011093");
			add2Value(useMax6List, "011736");
			add2Value(useMax6List, "011739");
			add2Value(useMax6List, "011248");
			add2Value(useMax6List, "011251");
		}
		
		return useMax6List;
	}
	
	/**
	 * 取得同意或者驳回的 head sid list
	 * "6199_2_6221_1_6224_1_4210_2_4243_1_4192_1_4242_1_6235_1_4161_1_"
	 */
	public static List<String> getHeadSidList(String str, String yes) {
		if(str == null) {
			return null;
		}
		
		str = str.trim();
		
		if(str.length() < 2 || ! str.endsWith("_")) {
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		String str12 = "yes".equals(yes) ? "1" : "2";
		
		str = str.substring(0, str.length() - 1);
		String[] array = str.split("_");
		
		for(int i = 0; i < array.length; i++) {
			//6199  2  6221  1  6224  1
			if(i % 2 == 1 && str12.equals(array[i])) {
				list.add(array[i - 1]);
			}
		}
		
		return list;
	}
	
	
	public static void main(String[] args) {
		/*
		String str = "6199_2_6221_1_6224_1_4210_2_4243_1_4192_1_4242_1_6235_1_4161_1_";
		List<String> al = getHeadSidList(str, "no");
		
		if(al != null && al.size() > 0) {
			for(String s: al) {
				System.out.println("=" + s + "=");
			}
		}
		else {
			System.out.println("null list");
		}
		*/
		
		//String str = "QQ04叙2= =`~^===永dfdf[]{}()-_,.;d永=县 ==县";
		//System.out.println("=" + onlyCn(str) + "=");
		
		/*
		String aLine = "1|2|3|4|53";
		String[] lineArray = aLine.split("\\|");
		
		for(String s: lineArray) {
			System.out.println("=" + s + "=");
		}
		
		int aaa = getDate();
		System.out.println("=" + aaa + "=");
		*/
		
		
		
		//System.out.println(moveDate(2009, 10, 28, -365*30));
		
//		System.out.println(changeCustomerIdTo8("0011025316"));
//		System.out.println(changeCustomerIdTo8("11025316"));
//		
//		System.out.println(changeCustomerIdTo10("0011025316"));
//		System.out.println(changeCustomerIdTo10("11025316"));
		
//		System.out.println(changeTo6or7(null));
//		System.out.println(changeTo6or7("081015"));
//		System.out.println(changeTo6or7("040105B"));
//		System.out.println(changeTo6or7("3061040121B0"));
//		System.out.println(changeTo6or7("3061040149B0"));
//		System.out.println(changeTo6or7("000000306101020900"));
//		System.out.println(changeTo6or7("000000306101020900"));
		
		
		//System.out.println(getShiJian());
		
//		Date d = new Date();
//		int year = d.getYear() + 1900;
//		int month = d.getMonth() + 1;
//		int date = d.getDate(); //1-31
//		int day = d.getDay(); //0 = Sunday, 1 = Monday
//		
//		System.out.println("year = " + year);
//		System.out.println("month = " + month);
//		System.out.println("date = " + date);
//		System.out.println("day = " + day);
		
		
		//System.out.println(moveDate(2009, 10, 20, 50));
		//System.out.println(moveDate(2009, 10, 21, 50));
		
		/*
		String[] array = {"aa", "bb", "cc"};
		
		boolean b = ArrayUtils.contains(array, "ccc");
		System.out.println(b);
		
		b = ArrayUtils.contains(array, "cc");
		System.out.println(b);
		
		b = ArrayUtils.contains(array, "");
		System.out.println(b);
		
		b = ArrayUtils.contains(array, null);
		System.out.println(b);
		*/
		
		
		
		/*
		List<String> al = getYpList();
		int length = al.size();
		System.out.println("length = " + length);
		//length = 4; ///////////////////////////////////////
		
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		
		for(int i = 0; i < length; i++) {
			//  'aaa', 'bbb', 'ccc'
			
			String str = al.get(i);
			sb.append("'" + str + "'");
			
			if(i < length - 1) {
				sb.append(", ");
			}
			
		}
		
		sb.append(")");
		
		System.out.println("==" + sb.toString() + "==");
		*/
		
		
		
		
		
		//List<ProductInfoBO> al = getYpKxpxList();
		//System.out.println(getYpKxpxMap());
		
		
		//table(9);
		
		/*
		String str = "not-include:c11,c33,c44";
		
		List<String> allNoList = new ArrayList<String>();
		allNoList.add("C11");
		allNoList.add("C22");
		allNoList.add("C33");
		allNoList.add("C44");
		allNoList.add("C55");
		allNoList.add("C66");

		String companyList = getCompanyNoList(str, allNoList);
		System.out.println("==" + companyList + "==");
		*/
		
		
		/*
		String company = "C22HS";
		String[] array = company.split("HS");
		int arraySize = array.length;
		System.out.println("arraySize = " + arraySize);
		
		for(String s: array) {
			System.out.println("==" + s + "==");
		}
		*/
		
		//System.out.println(moveDate("20071101", 365));
		/*
		int num = 0;
		
		do {
			num++;
			System.out.println(num);
		}
		while (num <= 5);
		 */
		
		
		//System.out.println(get8DateNum("2009-02-05"));
		
		//System.out.println(moveDateForWen("2009-12-01", 22));
		
		//System.out.println(get8Date("2009.02.14"));
		
		//String str = "4545454545";
		//boolean b2 = StringUtils.isNumeric(str); //判断是否只包含0-9
		//System.out.println(b2);
		
		//boolean b2 = StringUtils.isAlphanumeric(str); //判断是否只包含字母和数字的组合
		
		
//		boolean b2 = StringUtils.isBlank(str); //判断是否为空格，空字符串或null
//		System.out.println(b2);
//		
//		boolean b3 = StringUtils.isEmpty(str); //判断是否为空字符串或null
//		System.out.println(b3);
//		
//		String aa = StringUtils.abbreviate("nba篮球1234g", 9);
//		System.out.println(aa);
		
		
//		String str = "篮球nba篮球";
//		String[] array = str.split("/");
//		System.out.println("len = " + array.length);
//		
//		for(String s: array) {
//			System.out.println("==" + s + "==");
//		}
		
		//System.out.println("==" + missFu("8195.230") + "==");
		
//		System.out.println("==" + string45("12.49999999") + "==");
//		System.out.println("==" + string45("12.50") + "==");
//		System.out.println("==" + string45("12.49") + "==");
//		System.out.println("==" + string45("12.5") + "==");
		
		
		
//		int length = 37;
//		int pageNum = 10;
//		
//		int totalPage = (length + pageNum - 1) / pageNum;
//		
//		for(int i = 1; i <= totalPage; i++) {
//			int start = (i - 1) * pageNum + 1;
//			int end = i * pageNum;
//			end = (end > length) ? length : end;
//			
//			System.out.println(start + "   " + end);
//		}
		
		
		int today = getTodayToNumber();
		System.out.println(getToday());
		System.out.println(today);
		
		System.out.println(moveDate(String.valueOf(today), -1));
		
	}
	
	
	/**
	 * 判断字符串里面是否包含至少3个数字
	 */
	public static boolean have3num(String str) {
		if(StringUtils.isBlank(str)) {
			return false;
		}
		
		str = str.trim();
		int num = 0;
		
		for(int i = 0; i < str.length(); i++) {
			if(StringUtils.isNumeric("" + str.charAt(i))) {
				num++;
			}
		}
		
		return  num >= 3;
	}
	
	
	
	
	/**
	 * 移动日期
	 * @param date8  8位的日期，如：20091201
	 * @param num 要移动的天数
	 */
	public static String moveDate(String date8, int num) {
		int date = myParseInt(date8);
		int year = date / 10000;
		int month = (date % 10000) / 100;
		int day = date - year * 10000 - month * 100;
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day + num);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		df.setLenient(false);

		return df.format(cal.getTime());
	}
	
	
	
	/**
	 * 移动日期
	 */
	public static String moveDate(int year, int month, int day, int num) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day + num);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setLenient(false);

		int week = cal.get(Calendar.DAY_OF_WEEK);

		return df.format(cal.getTime()) + " " + getCnByWeek(week);
	}
	
	
	/**
	 * 移动日期,  for:文宣品
	 */
	public static String moveDateForWen(String date10, int num) {
		if(date10 == null || "".equals(date10.trim())) {
			return " ";
		}
		
		date10 = date10.trim();
		
		//2009-12-31
		int year = myParseInt(date10.substring(0, 4));
		int month = myParseInt(date10.substring(5, 7));
		int day = myParseInt(date10.substring(8, 10));
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day + num);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setLenient(false);

		return df.format(cal.getTime());
	}
	
	
	
	
	/**
	 * 得到星期几
	 */
	public static String getCnByWeek(int week) {
		String cn = null;

		if (week == 1) {
			cn = "星期天";
		} else if (week == 2) {
			cn = "星期一";
		} else if (week == 3) {
			cn = "星期二";
		} else if (week == 4) {
			cn = "星期三";
		} else if (week == 5) {
			cn = "星期四";
		} else if (week == 6) {
			cn = "星期五";
		} else if (week == 7) {
			cn = "星期六";
		} else {
			cn = "";
		}

		return cn;
	}
	
	/**
	 * 得到当前的具体时间，如：2004-12-11 星期六 14:52:01
	 */
	public static String getShiJian() {
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		int week = cal.get(Calendar.DAY_OF_WEEK);

		StringBuffer sb = new StringBuffer(30);
		sb.append(getDateFromYYYYMMDD(year, month, day));
		sb.append(" ");
		sb.append(getCnByWeek(week));
		sb.append(" ");
		sb.append(getTimeFromHHMMSS(hour, minute, second));

		return sb.toString();
	}
	
	/**
	 * 返回格式：2006-01-15
	 */
	public static String getDateFromYYYYMMDD(int year, int month, int day) {
		StringBuffer sb = new StringBuffer(12);

		sb.append(year);
		sb.append('-');

		if (month < 10) {
			sb.append('0');
		}

		sb.append(month);
		sb.append('-');

		if (day < 10) {
			sb.append('0');
		}

		sb.append(day);

		return sb.toString();
	}
	
	/**
	 * 返回格式：08:59:03 （呵呵，快迟到了，赶快打卡上班）
	 */
	public static String getTimeFromHHMMSS(int hour, int minute, int second) {
		StringBuffer sb = new StringBuffer(12);

		if (hour < 10) {
			sb.append('0');
		}
		sb.append(hour);
		sb.append(':');
		if (minute < 10) {
			sb.append('0');
		}
		sb.append(minute);
		sb.append(':');
		if (second < 10) {
			sb.append('0');
		}
		sb.append(second);

		return sb.toString();
	}
	
	
	/**
	 * 判断字符串里面是否含有英文字母
	 */
	public static boolean haveEn(String str) {
		if(str == null) {
			return false;
		}
		
		boolean haveEn = false; //是否包含有英文字母     040105B
		
		str = str.trim().toUpperCase();
		int len = str.length();
		
		for(int i = 0; i < len; i++) {
			String s = "" + str.charAt(i);
			if(EN.indexOf(s) >= 0) {
				haveEn = true;
				break;
			}
		}
		
		return haveEn;
	}
	
	
	/**
	 * 把字符串转化为大写，避免字符串为null
	 */
	public static String toUpperCase(String str) {
		if(str == null || "".equals(str.trim())) {
			return "";
		}
		
		return str.trim().toUpperCase();
	}
	
	
	/**
	 * 把品项编号转换成  6 或者 7  位，如：  040121  或者  040121B
	 */
	public static String changeTo6or7(String prodId) {
		if (prodId == null) {
			return "";
		}

		prodId = prodId.trim();
		int length = prodId.length();

		if(length <= 7) {
			return prodId;
		}
		
		boolean haveEn = haveEn(prodId); //是否含有英文字母
		int endIndex = haveEn ? (length - 1) : (length - 2);
		
		//000000306118017200  or  0000003061180172B0  or  306118017200  or  3061180172B0
		if(length == 18 || length == 12) {
			return prodId.substring(length - 8, endIndex);
		}

		return prodId;
	}
	
	/**
	 * 把客户ID转成8位长度
	 */
	public static String changeCustomerIdTo8(String customerId) {
		if (customerId == null) {
			return "";
		}

		customerId = customerId.trim();
		int length = customerId.length();
		
		return (length == 10) ? customerId.substring(2, length) : customerId;
	}
	
	/**
	 * 把客户ID转成10位长度
	 */
	public static String changeCustomerIdTo10(String customerId) {
		if (customerId == null) {
			return "";
		}

		customerId = customerId.trim();
		
		return (customerId.length() == 8) ? ("00" + customerId) : customerId;
	}
	
	/**
	 * 返回系统当前日期字符，格式为"YYYY-MM-DD"
	 */
	public static String getToday() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setLenient(false);

		return df.format(new Date());
	}
	
	/**
	 * 返回系统当前日期的8位数字，如：20091231
	 */
	public static int getTodayToNumber() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		df.setLenient(false);

		return myParseInt(df.format(new Date()));
	}
	
	/**
	 * 判断给定的日期是否比当前日期小（转化为数字, 再比较大小）。
	 * 例如：给定的日期：2009-12-03, 当前日期:2009-12-10,  则返回true
	 */
	public static boolean isSmallThanToday(String date) {
		if(date == null ) {
			return false;
		}
		
		date = date.trim();
		int length = date.length();
		
		if(length < 6) {
			return false;
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < length; i++) {
			char c = date.charAt(i);
			if(c != '-') {
				sb.append(c);
			}
		}
		
		int today = getTodayToNumber();
		int theDate = myParseInt(sb.toString());
		
		return (theDate < today);
	}
	
	
	/**
	 * format日期，格式为"YYYY-MM-DD"
	 */
	public static String formatDate(Date date) {
		if(date == null) {
			return " ";
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setLenient(false);

		return df.format(date);
	}
	
	/**
	 * object To String,  避免了空指针异常
	 */
	public static String objectToString(Object obj) {
    	return (null != obj) ? obj.toString().trim() : "";
    }
	
	
	/**
	 * 避免负数
	 */
	public static String missFu(Object obj) {
		String str = objectToString(obj);
    	return str.startsWith("-") ? str.substring(1, str.length()) : str;
    }
	
	
	/**
	 * String To BigDecimal
	 */
	public static BigDecimal StringToBigDecimal(String v1) {
		v1 = (v1 == null || "".equals(v1.trim())) ? "0" : v1.trim();
		return (new BigDecimal(v1));
	}
	
	
	/**
	 * 避免 BigDecimal 为 null
	 */
	public static BigDecimal passNullForBigDecimal(BigDecimal num) {
		return (num == null) ? zero : num;
	}
	
	/**
	 * 用递归和 System.out.print() , 写九九乘法表
	 */
	public static void table(int num) {
		if(num > 1) {
			//System.out.println("goto = " + (num - 1));
			table(num - 1);
		}
		
		//System.out.println("print, num = " + num);
		for(int i = 1; i <= 9; i++) {
			System.out.print(i * num);
			String temp = (i * num < 10) ? "  " : " ";
			String str = (i == 9) ? "\n" : temp;
			System.out.print(str); //一个空格，或者二个空格，或者回车符
		}
	}
	
	/**
	 * 得到字符串的前N个字符
	 */
	public static String getBeforeString(String str, int num) {
		if(str == null) {
			return "";
		}
		
		str = str.trim();
		
		return (str.length() <= num) ? str : str.substring(0, num);
	}
	
	
	/**
	 * 得到8位的日期，例如： 20091231
	 */
	public static String get8Date(String str) {
		if(str == null || "".equals(str.trim())) {
			return "";
		}
		
		str = str.trim();
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if(c != '-' && c != '/' && c != '.') {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
	
	/**
	 * 根据事业部ID，取得 信控范围, 如 CDJ, CDS 等
	 */
	public static String getCD_XByDivisionSid1(String divisionSid) {
		String cd_x = null;
		
		if(divisionSid == null || divisionSid.equals("1")) {
			cd_x = CD_I; //县城送旺 信控范围Z02
		}
		else if(divisionSid.equals("3")) {
			cd_x = "CDS"; //饮品事业部 信控范围Z02
		}
		else if(divisionSid.equals("6")) {
			cd_x = "CDJ"; //乳品事业部 信控范围Z02
		}
		else if(divisionSid.equals("4")) {
			cd_x = "CDX"; //米果事业部 信控范围
		}
		else if(divisionSid.equals("5")) {
			cd_x = "CDW"; //糖果事业部 信控范围
		}
		else if(divisionSid.equals("11")) {
			cd_x = "CDV"; //休一事业部 信控范围
		}
		else if(divisionSid.equals("12")) {
			cd_x = "CDT"; //休二事业部 信控范围Z02
		}
		else if(divisionSid.equals("10")) {
			cd_x = "CDU"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("13")) {
			cd_x = "CD7"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("15")) {
			cd_x = "CC3"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("16")) {
			cd_x = "CC1"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("17")) {
			cd_x = "CC2"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("18")) {
			cd_x = "CD2"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("19")) {
			cd_x = "CDR"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("20")) {
			cd_x = "CDK"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("21")) {
			cd_x = "CD3"; //方便事业部 信控范围Z02
		}else if(divisionSid.equals("22")) {
			cd_x = "CD1"; //方便事业部 信控范围Z02
		}
		else {
			cd_x = CD_I;
		}
		/*
		 * CDI	1
CDU	10
CDV	11
CDT	12
CD7	13
CC3	15
CC1	16
CC2	17
CD2	18
CDR	19
CDK	20
CD3	21
CD1	22
CDS	3
CDX	4
CDW	5
CDJ	6*/
		return cd_x;
	}
	
	/**
	 * 得到8位的日期，例如: 20091231
	 * @param date 日期，例如: 2009-12-31
	 */
	public static int get8DateNum(String date) {
		if(date == null) {
			return 0;
		}
		
		date = date.trim();
		
		if(date.length() < 10) {
			return 0;
		}
		
		String str = date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10);
		return myParseInt(str);
	}
	
	
	/**
	 * 得到 company No List
	 */
	public static String getCompanyNoList(String str, List<String> allNoList) {
		StringBuilder sb = new StringBuilder();
		str = str.trim().toUpperCase();
		
		if(str.startsWith(ALL)) {
			for(String s: allNoList) {
				sb.append(s);
				sb.append(",");
			}
		}
		else if(str.startsWith(INCLUDE)) {
			//include:c14,c13,c46
			int index = str.indexOf(":");
			sb.append(str.substring(index + 1, str.length())); //c14,c13,c46
			sb.append(",");
		}
		else if(str.startsWith(NOT_INCLUDE)) {
			//not-include:c44,c38,c39
			int index = str.indexOf(":");
			str = str.substring(index + 1, str.length()); //c44,c38,c39
			String[] array = str.split(",");
			List<String> otherList = new ArrayList<String>(); //排除的公司编号list
			for(String s: array) {
				otherList.add(s);
			}
			for(String s: allNoList) {
				if(! otherList.contains(s)) {
					sb.append(s);
					sb.append(",");
				}
			}
		}
		
		String temp = sb.toString();
		return temp.substring(0, temp.length() - 1);
	}
	
	/**
	 * 得到 客户登录的位置
	 */
	public static String getLoginPlace(String ip) {
		if(ip == null || ip.equals("")) {
			return "";
		}
		if(ip.equals("127.0.0.1") || ip.startsWith("192.168.")) {
			return "旺旺集团内部";
		}
		
		return (ip.trim().startsWith("10.")) ? "旺旺集团内部" : "外部";
	}
	
	/**
	 * 得到 Syb Name
	 */
	public static String getSybName(String sybId) {
		String value = "";
		
		if(sybId == null) {
			value = "";
		}
		else if("1".equals(sybId)) {
			value = "县城";
		}
		else if("3".equals(sybId)) {
			value = "城区饮品";
		}
		else if("6".equals(sybId)) {
			value = "城区乳品";
		}
		else if("4".equals(sybId)) {
			value = "城区米果";
		}
		else if("5".equals(sybId)) {
			value = "城区糖果";
		}
		else if("11".equals(sybId)) {
			value = "休一";
		}
		else if("12".equals(sybId)) {
			value = "休二";
		}
		else if("10".equals(sybId)) {
			value = "方便";
		}
		
		return value;
	}
	
	
	public static String getOrderNo(String msg) {
		String numList = "0123456789";
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < msg.length(); i++) {
			String str = "" + msg.charAt(i);
			if(numList.indexOf(str) >= 0) {
				sb.append(str);
			}
		}
		
		return sb.toString();
	}
	
	
}
