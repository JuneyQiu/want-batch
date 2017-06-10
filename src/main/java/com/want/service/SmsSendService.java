package com.want.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Service;

@Service
public class SmsSendService {

	private Log logger = LogFactory.getLog(this.getClass());

	@Autowired(required = false)
	@Qualifier("portalJdbcOperations")
	private SimpleJdbcOperations portalJdbcOperations;
	
	@Value("${sms.context}")
	private String smsContext;

	private String sql = new StringBuilder()
			.append("INSERT INTO SMS_TBL(SID, FUNC_SID, FUNC_CASE_ID, CREATE_TIME, SEND_TIME, PHONE_NUMBER, CONTENT, STATUS) ")
			.append("VALUES(SMS_SID_SEQ.NEXTVAL, :funcSid, :funcCaseId, :createDate, :sendDate, :phoneNumber, :content, :status) ")
			.toString();

	public void send(SmsModel smsModel) {

		try {
			this.portalJdbcOperations.update(sql,
					new BeanPropertySqlParameterSource(smsModel));
			logger.info(String.format("send sms to %s[%s] successful.",
					smsModel.getPersonalName(), smsModel.getPhoneNumber()));
		} catch (Exception e) {
			// TODO : 短信发送失败，只记录异常，不做处理
			logger.error(
					String.format("send sms to %s[%s] failed.",
							smsModel.getPersonalName(),
							smsModel.getPhoneNumber()), e);
		}
	}

	/**
	 * 批量发短信
	 * 
	 * add by Chris Yu 2013-12-20
	 * 
	 * @param smsModelList
	 */
	public void sendBatchSms(final List<SmsModel> smsModelList) {
		
		int[] batchSize = portalJdbcOperations.getJdbcOperations().batchUpdate(
				sql, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						SmsModel model = smsModelList.get(i);
						
						ps.setInt(1, model.getFuncSid());
						ps.setString(2, model.getFuncCaseId());
						
						ps.setTimestamp(3, new Timestamp(model.getCreateDate().getTime()));	
						//发送时间延迟半小时
						ps.setTimestamp(4, new Timestamp(model.getSendDate().getTime()));
						
						ps.setLong(5, model.getPhoneNumber());
						//判断是否有默认短信内容
						if (StringUtils.isNotBlank(smsContext)) {
							ps.setString(6, smsContext);
						}else{
							ps.setString(6, model.getContent());							
						}
						ps.setInt(7, 1);
						// log 记录批量写入sms_tbl内容
						logger.info(String.format(
								"send sms to %s[%s] successful.",
								model.getPhoneNumber(), model.getContent()));
					}

					@Override
					public int getBatchSize() {
						return smsModelList.size();
					}
				});
		// 记录批量写入短信总条数
		logger.info(String.format("send sms count %s number successful.",
				batchSize.length));

	}
}