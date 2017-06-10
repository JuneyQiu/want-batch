package com.want.batch.job.smsapp;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.jasson.im.api.APIClient;
import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.monitor.DataArchiveJob;

/**
 * 
 * @author 王翊丞
 *
 */
@Component
public class MasAppjob extends AbstractWantJob{
	
	/*
	 *	List<Map<String,Object>> 不会为null 如果没有返回结果 则list.size=0 
	 */
	@Autowired(required = false)
	@Qualifier("smsJdbcOperations")
	private SimpleJdbcOperations smsJdbcOperations;
	/*日志*/
	private static final Log logger = LogFactory.getLog(DataArchiveJob.class);
	
	/*获取旧平台需发送的短信信息*/
	//PORTAL.SMS_TBL
	//"select * from %s where send_time < sysdate and status = '1' and ROWNUM<=500";
	private static final String OLDSMS_SQL = "select * from sms_tbl";
	//private static final String OLDSMS_SQL = "select * from PORTAL.SMS_TBL where SID in (select max(SID) from PORTAL.SMS_TBL group by PHONE_NUMBER) and CONTENT='各位如果您在2013-08-01 01:37分左右收到了若干份来自旺旺集团短信平台发送的短信，是由于我们的短信系统平台在做测试，您收到的短信内容均为系统随机编辑，所以给您带来不便敬请谅解！好消息是： 在这个时间段收到测试短信的同仁，会在2014年的2月31收到一份神秘的礼物，敬请期待，祝大家晚安！' and STATUS =1";
	
	/*发送后修改状态*/
	//PORTAL.SMS_TBL
	private static final String OLD_SEND_UPDATE_STATUS_SQL = "update %s set UPDATE_TIME = ?, status = ?, log = ? where sid = ? ";
	
	/*同步*/
	//sms_tbl_his,sms_tbl
	private static final String INSERT_HIS_SQL = "insert into %s select * from %s where to_char(send_time,'YYYY/MM/DD hh24:mi') <= to_char(sysdate-(1/24),'YYYY/MM/DD hh24:mi')";
	//sms_tbl,
	private static final String DEL_SMS_SQL = "delete from %s where to_char(send_time,'YYYY/MM/DD hh24:mi') <= to_char(sysdate-(1/24),'YYYY/MM/DD hh24:mi')";
	
	
	private static final String PROPERTIESNAME = "project";
	
	private long srcId = 0;
	
	private static final String SID = "SID";
	private static final String FUNC_SID = "FUNC_SID";
	private static final String CREATE_TIME = "CREATE_TIME";
	private static final String UPDATE_TIME = "UPDATE_TIME";
	private static final String SEND_TIME = "SEND_TIME";
	private static final String PHONE_NUMBER = "PHONE_NUMBER";
	private static final String CONTENT = "CONTENT";
	private static final String STATUS = "STATUS";
	private static final String LOG = "LOG";
	
	private static final String MAS_HOST = "mas_host";
	private static final String MAS_BACKUPHOST = "mas_backupHost";
	private static final String MAS_DBNAME = "mas_dbName";	
	private static final String MAS_APILD = "mas_apiId";
	private static final String MAS_NAME = "mas_name";
	private static final String MAS_PWD = "mas_pwd";
	//private static final String MAS_DM_MOBILES = "mas_adm_mobiles";
	
	private static final String TYPE_OLD = "old";
	private static final String TYPE_NEW = "new";
	
	private boolean dualConnected = false;
	
	
	private APIClient handler = new APIClient();
	
	private long smId = 0;
	
	/*已将短信发送*/
	public static final String SENT = "2";  
	/*电话或者短信内容为空*/
	public static final String FAIL = "5";
	
	/*失败信息*/
	public static final String SENDERROR= "phone or smsContent is null";
	
	@Override
	public void execute() throws Exception {
		/*初始化连接*/
		initSms();
		
		logger.info("开始发送旧平台短息");
		sendNote(TYPE_OLD);
		
//		logger.info("开始发送新平台短息");
//		sendNote(TYPE_NEW);

		
	}
	
	
	/*初始化连接*/
	public void initSms(){
		/*加载配置文件*/
		ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIESNAME);
		
		String host = bundle.getString(MAS_HOST);
		String backupHost = bundle.getString(MAS_BACKUPHOST);
		String dbName = bundle.getString(MAS_DBNAME);
		String apiId = bundle.getString(MAS_APILD);
		String name = bundle.getString(MAS_NAME);
		String pwd = bundle.getString(MAS_PWD);
		//String admMobiles = bundle.getString(MAS_DM_MOBILES);
		
		int connectRe = handler.init(host, name, pwd, apiId, dbName);

		if (connectRe == APIClient.IMAPI_SUCC)
			logger.info("初始化成功");
		else if (connectRe == APIClient.IMAPI_CONN_ERR)
			logger.info("连接失败");
		else if (connectRe == APIClient.IMAPI_API_ERR)
			logger.info("apiID不存在");

		if (connectRe != APIClient.IMAPI_SUCC && dualConnected == false) {
			dualConnected = true;
			host = backupHost; // 使用备援服务器
			logger.info("主要服务器连线失败，连线备援服务器");
			initSms();
		}

	}
	
	
	/*
	 * 短信发送结果
	 */
	
	public String getResultDesc(int result) {
		if (result == APIClient.IMAPI_SUCC)
			return "发送成功";
		else if (result == APIClient.IMAPI_INIT_ERR)
			return "未初始化";
		else if (result == APIClient.IMAPI_CONN_ERR)
			return "数据库连接失败";
		else if (result == APIClient.IMAPI_DATA_ERR)
			return "参数错误";
		else if (result == APIClient.IMAPI_DATA_TOOLONG)
			return "消息内容太长";
		else if (result == APIClient.IMAPI_INS_ERR)
			return "数据库插入错误";
		else
			return "出现其他错误";
	}
	
	/*
	 * 发送短信
	 */

	public void sendNote(String type){
		//sms_tbl_his,sms_tbl
		String SMS_TABLE = "";
		String HIS_TABLE = "";
		
		if(type.equals(TYPE_OLD)){
			SMS_TABLE = "sms_tbl";
			HIS_TABLE = "sms_tbl_his";
		}
		if(type.equals(TYPE_NEW))
		{
			SMS_TABLE = "sms_mas_tbl";
			HIS_TABLE = "sms_mas_tbl_his";
		}
		
		/*获取旧平台短信信息*/
		List<Map<String, Object>> listOldSms = this.smsJdbcOperations.queryForList(String.format(OLDSMS_SQL, SMS_TABLE));
		
		logger.info("符合传送旧平台预约短信的笔数：" + listOldSms.size());
		
		if(listOldSms.size()>0){
			for (int i = 0; i < listOldSms.size(); i++) {
				Map<String,Object> mapSms = listOldSms.get(i);
				int sId = Integer.parseInt(mapSms.get(SID).toString());
				smId = Long.parseLong("2" + sId);
				/*在电话和短信内容不为空的情况下*/
				if(mapSms.get(PHONE_NUMBER)!=null && mapSms.get(CONTENT)!=null){
					int result = 0;
					String phoneArr[] = mapSms.get(PHONE_NUMBER).toString().split(",");
					
					boolean flag = true;
					
					/*开始发送短息*/
					if (srcId == 0)
						try {
							
							result = handler.sendSM(phoneArr, mapSms.get(CONTENT).toString(), smId);
							
							logger.info("已成功发送出去 ~~"+" , 接收者:"+mapSms.get(PHONE_NUMBER).toString()+" ,内容:"+mapSms.get(CONTENT).toString());
							
						} catch (Exception e) {
							flag = false;
							logger.error("发送失败~~~"+" , 接收者:"+mapSms.get(PHONE_NUMBER).toString()+" ,内容:"+mapSms.get(CONTENT).toString());
						}
						
					else 
						try {
							
							result = handler.sendSM(phoneArr, mapSms.get(CONTENT).toString(), smId,srcId);
							
							logger.info("已成功发送出去 ~~"+" , 接收者:"+mapSms.get(PHONE_NUMBER).toString()+" ,内容:"+mapSms.get(CONTENT).toString());
							
						} catch (Exception e) {
							flag = false;
							logger.error("发送失败~~~"+" , 接收者:"+mapSms.get(PHONE_NUMBER).toString()+" ,内容:"+mapSms.get(CONTENT).toString());
						}
						
					
					/*发送后修改状态 和发送结果*/
					if(flag==true){
						/*返回发送结果*/
						String resultDesc = getResultDesc(result);
						
						try {
							Date currentDate = new Date();
							this.smsJdbcOperations.update(String.format(OLD_SEND_UPDATE_STATUS_SQL,SMS_TABLE),currentDate,SENT,resultDesc,sId);
							
							logger.info("成功发送后，修改了发送状态和返回结果内容!!");
							
						} catch (Exception e) {
							logger.error("发送短信后，修改记录失败： "+" , 接收者:"+mapSms.get(PHONE_NUMBER).toString()+" ,内容:"+mapSms.get(CONTENT).toString());
						}
						
					}//flag==true
					
					
				}//if phone and content != null
				
				/*hone and content == null*/
				else{
					
					try {
						this.smsJdbcOperations.update(String.format(OLD_SEND_UPDATE_STATUS_SQL,SMS_TABLE),FAIL,SENDERROR,sId);
						logger.info("编号为:"+SID+" 的记录没有要发送的手机号或者内容");
					} catch (Exception e) {
						logger.error("数据库update失败...");
					}
				}//else
				
			}//for

		}
				
		
		/*---------------同步-----------------------------
		logger.info("****************开始同步***************");
		int re = this.getPortalJdbcOperations().update(String.format(INSERT_HIS_SQL,HIS_TABLE,SMS_TABLE));
		logger.info("新增笔数: "+re);
		
		re = this.getPortalJdbcOperations().update(String.format(DEL_SMS_SQL, SMS_TABLE));
		logger.info("删除笔数: "+re);
		*/
	}
	
}
