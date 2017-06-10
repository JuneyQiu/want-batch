package com.want.batch.job.archive.notify.util;

/**
 * @author 00078588
 *
 */
public class Constant {
	public final static double DEFAULT_DOUBLE=-99999999D;
	public final static long DEFAULT_LONG=-99999999L;
	public final static int DEFAULT_INT=-99999999;
	public final static short DEFAULT_SHORT=-99;

	public final static int USER_TYPE_SID_ZR=5;
	public final static int USER_TYPE_SID_SZ=6;
	public final static int USER_TYPE_SID_ZY=7;
	public final static int USER_TYPE_SID_ZJ=12;
	public final static int USER_TYPE_SID_ZJL=14;

	/**
	 * 异常编号：业代日报未录入
	 */
	public final static int EXC_TYPE_CODE_DR_NOT_INPUT=31;
	/**
	 * 异常编号：业代日报表录入不完整
	 */
	public final static int EXC_TYPE_CODE_DR_NOT_COMPLETELY=32;
	/**
	 * 异常编号：业代当日销售金额为0
	 */
	public final static int EXC_TYPE_CODE_DR_SALES_IS_0=33;
	/**
	 * 异常编号：业代库存未录入
	 */
	public final static int EXC_TYPE_CODE_STORAGE_UNFILLED=1;
	/**
	 * 异常编号：客户库存未确认
	 */
	public final static int EXC_TYPE_CODE_STORAGE_UNCONFIRM=2;
	/**
	 * 异常编号：客户货需未录入
	 */
	public final static int EXC_TYPE_CODE_REQUIRE_UNFILLED=3;
	/**
	 * 异常编号：营业所客户货需未审核
	 */
	public final static int EXC_TYPE_CODE_REQUIRE_UNAUDIT_BY_BRANCH=4;
	/**
	 * 异常编号：分公司客户货需未确认
	 */
	public final static int EXC_TYPE_CODE_REQUIRE_UNAUDIT_BY_COMPANY=5;
	/**
	 * 异常编号：客户库存总金额异常(客户库存总金额>=客户次月签约目标额)
	 */
	public final static int EXC_TYPE_CODE_CUST_STORAGE_TOTAL_AMOUNT=8;
	/**
	 * 异常编号：促销品库存未录入
	 */
	public final static int EXC_TYPE_CODE_WEN_STORAGE_UNINPUT=10;
	/**
	 * 异常编号：促销品库存未确认
	 */
	public final static int EXC_TYPE_CODE_WEN_STORAGE_UNCONFIRMED=11;
	/**
	 * 异常编号：客户未提交特陈计划表
	 */
	public final static int EXC_TYPE_CODE_CUSTOMER_SD_UNSUBMIT=14;
	/**
	 * 异常编号：主任未审核特陈计划表
	 */
	public final static int EXC_TYPE_CODE_ZR_SD_PLAN_UNAUDIT=15;
	/**
	 * 异常编号：业代未提交特陈（实际）检核信息
	 */
	public final static int EXC_TYPE_CODE_SALES_SD_ACTUAL_UNSUBMIT=18;
	/**
	 * 异常编号：主任未提交特陈(实际)检核信息
	 */
	public final static int EXC_TYPE_CODE_ZR_SD_ACTUAL_UNSUBMIT=19;
	/**
	 * 异常编号：主任未提交特陈(实际)稽核信息
	 */
	public final static int EXC_TYPE_CODE_ZR_SD_ACTUAL_NOT_FILL=20;
}
