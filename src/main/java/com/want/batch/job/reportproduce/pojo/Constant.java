package com.want.batch.job.reportproduce.pojo;

/**
 * 常量配置类.
 *
 *@author Mirabelle
 */
public class Constant {
	public final static double DEFAULT_DOUBLE=-99999999D;
	public final static long DEFAULT_LONG=-99999999L;
	public final static int DEFAULT_INT=-99999999;
	public final static short DEFAULT_SHORT=-99;

	public final static String SELECT_OPTION_ALL_CODE="all";
	public final static String SELECT_OPTION_ALL_DESC="全部";

	public final static String SPECIAL_DISPLAY_TYPE_DINGE_CODE="1";
	public final static String SPECIAL_DISPLAY_TYPE_DINGE_DESC="定额";
	public final static String SPECIAL_DISPLAY_TYPE_DINGDIAN_CODE="2";
	public final static String SPECIAL_DISPLAY_TYPE_DINGDIAN_DESC="定点";
	

	/**
	 * excel文件的每个sheet最多显示的记录行数
	 */
	public final static int MAX_LINE_NUM_EVERY_SHEET=30000;
	

	/////////特陈实际费用报表各字段所在的列start////////////////////////////////////
	public final static byte YEAR_MONTH_COL=0;
	public final static byte POLICY_COL=1;
	public final static byte DIVISION_NAME_COL=2;
	public final static byte COMPANY_NAME_COL=3;
	public final static byte BRANCH_NAME_COL=4;
	public final static byte CUSTOMER_ID_COL=5;
	public final static byte CUSTOMER_NAME_COL=6;
	public final static byte DISPLAY_COST=7;//特陈实际费用
	public final static byte EXC_AMOUNT=8;//市场稽核专员查核终端特陈异常计划金额合计
	/////////特陈实际费用报表各字段所在的列end//////////////////////////////////////

	/////////特陈实际-审核档报表各字段所在的列start////////////////////////////////////
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_DIVISION_COL=0;//事业部
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_COMPANY_COL=1;//分公司
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_BRANCH_COL=2;//营业所
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_CUST_ID_COL=3;//客户编号
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_CUST_NAME_COL=4;//客户名称
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_YEAR_MONTH_COL=5;//特陈年月
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_POLICY_NAME_COL=6;//特陈政策名称
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_SD_NO_COL=7;//单据编号
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_CHECK_STATUS_COL=8;//实际单据状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_YD_COL=9;//业代填写状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_ZR_COL=10;//主任填写状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_FILL_IN_STATUS_KH_COL=11;//客户填写状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_XG_COL=12;//销管审核状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_ZR_COL=13;//主任审核状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_SZ_COL=14;//所长审核状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_AUDIT_AUDIT_CHECK_STATUS_ZJ_COL=15;//总监审核状态
	/////////特陈实际-审核档报表各字段所在的列end//////////////////////////////////////

	/////////特陈实际-基础档报表各字段所在的列start////////////////////////////////////
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_DIVISION_COL=0;//事业部
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_COMPANY_NAME_COL=1;//分公司
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_BRANCH_NAME_COL=2;//营业所
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_THIRD_NAME_COL=3;//三级地
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_ID_COL=4;//客户编号
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_NAME_COL=5;//客户名称
	
  //客户状态 add amy 2015-10-12
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_CUST_STATUS_COL=6;
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_AMOUNT_PER_MONTH_COL=7;//月度标准投入费用
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_YEAR_MONTH_COL=8;//特陈年月
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_POLICY_NAME_COL=9;//特陈政策名称
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_SD_NO_COL=10;//单据编码
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ID_COL=11;//终端店编码
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_MDM_STORE_ID_COL=12;// 新终端编码  2014-02-13 mandy add
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_NAME_COL=13;//终端店名称
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ACREAGE_COL=14;//终端店面积
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_PHONE_COL=15;//终端电话号码
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ADDRESS_COL=16;//终端地址
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_OWNER_COL=17;//终端联系人
//	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_TELEPHONE_COL=15;//固定电话号码
//	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_MOBILE_COL=16;//手机号
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_DISPlAY_ACTUAL_CHECKER_COL=18;//特陈实际检核人
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_DISPLAY_SID_COL=19;//流水码
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_LOCATION_TYPE_NAME_COL=20;//特陈位置
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_DISPLAY_TYPE_NAME_COL=21;//特陈形式
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ADDITIONAL_OPTIONS_COL=22;//附加选项
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_NOT_REACH_COL=23;//不达标
//	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_DISPLAY_ACREAGE_COL=21;//特陈面积
//	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_DISPLAY_SIDE_COUNT_COL=22;//特陈面数
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ASSERT_ID_COL=24;//财产编号
//	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_DECORATION_COL=24;//乐园装饰
//	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_GIFT_EXH_TYPE_COL=25;//礼包展架类型
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_FIRST_VIST_DATE_COL=25;//终端首次排定拜访时间
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_ACTUAL_FILL_STATUS_COL=26;//终端点实际填写状况
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_YD_COL=27;//业代实际首次填写时间
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ACCOUNT_YD_COL=28;//业代实际首次填写人工号
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_USER_NAME_YD_COL=29;//业代实际首次填写人姓名
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_ZR_COL=30;//主任实际首次填写时间
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ACCOUNT_ZR_COL=31;//主任实际首次填写人工号
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_USER_NAME_ZR_COL=32;//主任实际首次填写人姓名
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_COL=33;//实际费用
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_COMPANY_COL=34;//实际费用
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_COST_CUSTOMER_COL=35;//实际费用
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_ACTUAL_SALES_COL=36;//实际销量
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_FILL_DATE_KH_COL=37;//客户填写实际费用与销量时间
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_IS_RECEIVED_AGREEMENT_COL=38;//销管实际是否收到特陈协议
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_ZR_COL=39;//主任实际审核时间
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_ZR_COL=40;//主任审核状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_SZ_COL=41;//所长实际审核时间
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_SZ_COL=42;//所长审核状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_APPROVED_AMOUNT_SZ_COL=43;//所长核定费用
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_DATE_ZJ_COL=44;//总监实际审核时间
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_AUDIT_ACTUAL_STATUS_ZJ_COL=45;//总监核定状态
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_APPROVED_AMOUNT_ZJ_COL=46;//总监核定费用
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_STORE_TYPE_COL=47;//终端类别
	public final static byte SPECIAL_DISPLAY_ACTUAL_BASIC_WITHDRAW_AMOUNT_COL=48;//当月回单金额
	/////////特陈实际-基础档报表各字段所在的列end////////////////////////////////////

	/////////特陈实际-品项档报表各字段所在的列start////////////////////////////////////
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_DIVISION_COL=0;//事业部
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_COMPANY_COL=1;//分公司
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_BRANCH_COL=2;//营业所
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_CUST_ID_COL=3;//客户编号
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_CUST_NAME_COL=4;//客户名称
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_AMOUNT_PER_MONTH_COL=5;//月度标准投入费用
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_YEAR_MONTH_COL=6;//特陈年月
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_POLICY_NAME_COL=7;//特陈政策名称
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_SD_NO_COL=8;//单据编码
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_STORE_ID_COL=9;//终端店编码
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_STORE_NAME_COL=10;//终端店名称
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_STORE_ACREAGE_COL=11;//终端店面积
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_DISPlAY_ACTUAL_CHECKER_COL=12;//特陈实际检核人
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_STORE_DISPLAY_SID_COL=13;//终端特陈编码
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_LOCATION_TYPE_NAME_COL=14;//特陈位置
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_DISPLAY_TYPE_NAME_COL=15;//特陈形式
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_ADDITIONAL_OPTIONS_COL=16;//附加选项
//	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_DISPLAY_ACREAGE_COL=16;//特陈面积
//	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_DISPLAY_SIDE_COUNT_COL=17;//特陈面数
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_ASSERT_ID_COL=17;//财产编号
//	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_DECORATION_COL=19;//乐园装饰
//	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_GIFT_EXH_TYPE_COL=20;//礼包展架类型
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_PROD_GROUP_NAME_COL=18;//线别
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_PROD_ID_COL=19;//品项ID
	public final static byte SPECIAL_DISPLAY_ACTUAL_PROD_PROD_NAME_COL=20;//品项名称
	/////////特陈实际-品项档报表各字段所在的列end////////////////////////////////////
	
	// 报表状态
	public static final String DIRECTIVE_STATUS_NEWJOB = "NEWJOB"; // 报表状态：新任务
	public static final String DIRECTIVE_STATUS_WAIT = "WAIT";    // 报表状态 ：报表未生成
	public static final String DIRECTIVE_STATUS_RUNNING= "RUNNING";    // 报表状态 ：报表生成开始
	public static final String DIRECTIVE_STATUS_FINISH = "FINISH";   // 报表状态 ：报表生成结束
	public static final String DIRECTIVE_STATUS_DOWNLOAD= "DOWNLOAD";    // 报表状态 ：报表下载结束
	public static final String DIRECTIVE_STATUS_EXCEPTION = "EXCEPTION";    // 报表状态 ：报表生成异常
	
	// 报表类型
	public static final String REPORT_SPECDISPLAYAUDITRPT = "1";   // 特陈实际-审核档报表
	public static final String REPORT_SPECDISPLAYBASICRPT = "2";   // 特陈实际-基础档报表
	public static final String REPORT_SPECDISPLAYPRODRPT = "3";    // 特陈实际-品项档报表
	
	// 删除类型
	public static final String DIRECTIVEHISTORYLOG_ENTER_TYPE_WEB = "WEB";
	public static final String DIRECTIVEHISTORYLOG_ENTER_TYPE_SCHEDULER = "SCHEDULER";
	
	/**
	 * <pre>
	 * 2013-7-4 Mirabelle
	 * 输出异常信息.
	 * </pre>	
	 * 
	 * @param e
	 * @return
	 */
	public static String generateExceptionMessage(Throwable e) {

		StringBuilder result = new StringBuilder(e.toString());

        for (StackTraceElement trace : e.getStackTrace()) {
        	String traceString = trace.toString();
        	
        	result.append("\n\tat ").append(traceString);
        }

        if (e.getCause() != null) {
        	result.append("\nCause : ").append(Constant.generateExceptionMessage(e.getCause()));
        }

		return result.toString();
	}
}
