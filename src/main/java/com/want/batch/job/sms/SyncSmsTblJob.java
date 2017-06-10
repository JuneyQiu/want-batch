/**
 * 
 */
package com.want.batch.job.sms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.service.SmsModel;

/**
 * @author yu_chao.myc
 * @date 2011-12-01 向客户发送短信
 * 
 */
@Component
public class SyncSmsTblJob extends AbstractWantJob {
	/*
	 * // 创建sql语句，使每次执行累计到sms_tbl 表中 private StringBuilder sqlbuilder = new
	 * StringBuilder() // 正式 .append(" INSERT INTO   PORTAL.SMS_TBL ") // 220测试表
	 * // .append(" INSERT INTO  ICUSTOMER.TEMP_SMS_TBL ") .append(
	 * " (SID, FUNC_SID, FUNC_CASE_ID, CREATE_TIME,UPDATE_TIME, SEND_TIME, PHONE_NUMBER,CONTENT,STATUS, LOG ) "
	 * ) // 获取发送短信表的seq的下一个值
	 * .append(" SELECT portal.sms_sid_seq.nextval as SID, ")
	 * .append(" 9 as FUNC_SID,  ") // FUNC_CASE_ID
	 * 字段是以‘sms-’+‘客户编号’+‘-系统时间所在分钟的秒数-’+‘一天从午夜开始的累积秒数’
	 * .append(" 'SMS-'||H.TKNUM||to_char(sysdate,'-SS-SSSSS') as FUNC_CASE_ID, "
	 * ) .append(" sysdate as CREATE_TIME, ") .append(" null as UPDATE_TIME, ")
	 * // 短信发送时间定为系统时间的后10分钟 .append(
	 * " to_date(to_char(sysdate + 10 / (24 * 60), 'yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss') as send_time, "
	 * ) .append(" H.TELF2 as PHONE_NUMBER, ") // 组装的短信内容
	 * .append(" ('您旺旺的'||G.COUNTS||'笔订单，预计将于今日送达，车牌号码：' ") .append(
	 * " || H.PLATE||'，联系电话：'||H.TELEPHONE||'。请您收货后登陆经销商网站进行确认，谢谢！') as content ,"
	 * ) // 发送短信的默认状态是1 当系统发完之后status会被改为2 .append(" 1 as STATUS , ")
	 * .append(" null as LOG ")
	 * .append(" FROM (SELECT T.KUNNR, COUNT(T.KUNNR) AS COUNTS ") // 这张表是同步排成
	 * SAP_SENDSMS_DATA .append(" FROM SAP_SENDSMS_DATA T ")
	 * .append(" WHERE T.TELF2 IS NOT NULL ") .append("  AND T.VBELN NOT IN ")
	 * .append(" (SELECT DISTINCT A.PO_NO FROM SENDPO_LINE_TBL A) ")
	 * .append(" GROUP BY T.KUNNR) G, ")
	 * .append(" (SELECT DISTINCT T.TKNUM, T.KUNNR, T.TELF2, T.PLATE, T.TELEPHONE "
	 * ) // 这张表是同步排成 SAP_SENDSMS_DATA .append(" FROM SAP_SENDSMS_DATA T ")
	 * .append(" WHERE T.TELF2 IS NOT NULL ") .append(" AND T.VBELN NOT IN ")
	 * .append(" (SELECT DISTINCT A.PO_NO FROM SENDPO_LINE_TBL A)) H ")
	 * .append(" WHERE G.KUNNR = H.KUNNR ");
	 */

	private StringBuilder selectQuery = new StringBuilder()
			.append(" SELECT  ('SMS-' || H.TKNUM || to_char(sysdate, '-SS-SSSSS')) as FUNC_CASE_ID ,")
			.append(" H.TELF2 as PHONE_NUMBER, G.COUNTS ,H.PLATE ,H.TELEPHONE ")
			.append(" FROM (SELECT T.KUNNR, COUNT(T.KUNNR) AS COUNTS ")
			.append(" FROM SAP_SENDSMS_DATA T ")
			.append(" WHERE T.TELF2 IS NOT NULL ")
			.append(" AND T.VBELN NOT IN ")
			.append(" (SELECT DISTINCT A.PO_NO FROM SENDPO_LINE_TBL A)")
			.append(" GROUP BY T.KUNNR) G,  ")
			.append(" (SELECT DISTINCT T.TKNUM, T.KUNNR, T.TELF2, T.PLATE, T.TELEPHONE ")
			.append(" FROM SAP_SENDSMS_DATA T ")
			.append(" WHERE T.TELF2 IS NOT NULL ")
			.append(" AND T.VBELN NOT IN ")
			.append(" (SELECT DISTINCT A.PO_NO FROM SENDPO_LINE_TBL A)) H ")
			.append(" WHERE G.KUNNR = H.KUNNR  and H.PLATE IS NOT NULL ")
			.append("AND H.TELEPHONE IS NOT NULL");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.want.batch.job.WantJob#execute()
	 */
	@Override
	public void execute() {
		synctoPortmessage();
	}

	private void synctoPortmessage() {
		Date sendDate=null;
		// 执行sql
		ArrayList<Map<String, Object>> smsContent = (ArrayList<Map<String, Object>>) this
				.getiCustomerJdbcOperations().queryForList(
						selectQuery.toString());
		for (Map<String, Object> map : smsContent) {
			SmsModel smsModel = new SmsModel();
			smsModel.setFuncSid(9);
			smsModel.setFuncCaseId((String) map.get("FUNC_CASE_ID"));
			smsModel.setCreateDate(new Date());
			smsModel.setContent("您旺旺的" + map.get("COUNTS")
					+ "笔订单，预计将于今日送达，车牌号码：" + map.get("PLATE") + "，联系电话："
					+ map.get("TELEPHONE") + "。请您收货后登陆经销商网站进行确认，谢谢！");
			String phoneNumber = (String) map.get("PHONE_NUMBER");
			// 手机号为空则不发送
			if (NumberUtils.isNumber(phoneNumber)) {
				// NumberUtils.isNumber(str) Null and empty String will return
				// false
				// 是否是手机号码 是否达到11位
				Pattern pattern = Pattern.compile("^1\\d{10}");
				Matcher matcher = pattern.matcher(phoneNumber);
				if (matcher.matches()) {
					logger.info("---------" + phoneNumber);
					smsModel.setStatus(SmsModel.SMS_STATUS);
					smsModel.setPhoneNumber(Long.valueOf(phoneNumber));
					// 分钟加10
					DateTime dateTime = new DateTime().plusMinutes(10);
					String str = dateTime.getYear() + "-"
							+ dateTime.getMonthOfYear() + "-"
							+ dateTime.getDayOfMonth() + " "
							+ dateTime.getHourOfDay() + ":"
							+ dateTime.getMinuteOfHour() + ":"
							+ dateTime.getSecondOfMinute();
					SimpleDateFormat   df   =new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
					try {
						sendDate=df.parse(str);
					} catch (ParseException e) {
						logger.info("-------------dateTime plus error !!!");
						smsModel = null;
						continue;
						
					}
					smsModel.setSendDate(sendDate);
					// 发送短信
					this.getSmsSendService().send(smsModel);
				}
			} else {
				smsModel = null;
				continue;
			}
		}
	}
}