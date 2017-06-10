package com.want.batch.job.monitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.component.mail.MailService;

@Component
public class BatchMonitorJob extends AbstractWantJob {
	
	private static final String SID = "SID";
	private static final String FUNC_ID = "FUNC_ID";
	private static final String NAME = "NAME";
	private static final String START_DATE = "START_DATE";
	private static final String END_DATE = "END_DATE";
	private static final String PERIOD = "PERIOD";
	private static final String STATUS = "STATUS";
	private static final String SERVER = "SERVER";
	private static final String REASON = "REASON";
	private static final String FOWNER = "FOWNER";
	private static final String SOWNER = "SOWNER";
	
	private static final String TDSTR = "<td nowrap>%s</td>";
	private static final String TRSTRCENTER = "<td style='text-align:center' nowrap>%s</td>";
	private static final String TDCOLOR = "<td nowrap><font color='%s'>%s</font></td>";
	private static final String TDCOLORCENTER = "<td style='text-align:center' nowrap><font color='%s'>%s</font></td>";
	
	private static final String FUNC_STATUS_FAILED = "0";
	private static final String FUNC_STATUS_SUCCESSED = "1";
	private static final String FUNC_STATUS_RUNNING = "2";
	
	private static final String NULLNAME = "未定义";
	
	private static final String PROPERTIESNAME = "project";
	private static final String ADDRESSEE = "project.mail.to";
	private static final String CC = "project.mail.cc";
	
	private static final String SMSTO = "project.batch.monitor.sms";
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String JOBNAME = "batchMonitorJob";
	
	/*时间段成功的排程执行信息*/
	private static final String RESULTSUL_SQL = "select B.SID,B.FUNC_ID,A.NAME,B.START_DATE,B.END_DATE,round((B.end_date - B.start_date)*1440) PERIOD,A.SERVER,B.STATUS,B.REASON,A.FOWNER,A.SOWNER"
			+" from BATCH.batch_status B left join BATCH.batch_func A on B.func_id=A.id"
			+" where B.STATUS='1' and B.START_DATE BETWEEN ? and ?"
			+" order by B.START_DATE desc";
	
	/*时间段内的失败的排程信息*/
	private static final String RESULTFAILED_SQL = "select B.SID,B.FUNC_ID,A.NAME,B.START_DATE,B.END_DATE,round((B.end_date - B.start_date)*1440) PERIOD,A.SERVER,B.STATUS,B.REASON,A.FOWNER,A.SOWNER"
		+" from BATCH.batch_status B left join BATCH.batch_func A on B.func_id=A.id"
		+" where B.STATUS='0' and B.START_DATE BETWEEN ? and ?"
		+" order by B.START_DATE desc";
	
	/*时间段内的进行中的排程信息*/
	private static final String RESULTING_SQL = "select B.SID,B.FUNC_ID,A.NAME,B.START_DATE,B.END_DATE,round((B.end_date - B.start_date)*1440) PERIOD,A.SERVER,B.STATUS,B.REASON,A.FOWNER,A.SOWNER"
		+" from BATCH.batch_status B left join BATCH.batch_func A on B.func_id=A.id"
		+" where B.STATUS='2' and B.START_DATE BETWEEN ? and ?"
		+" order by B.START_DATE desc";
	
	
	/*获取是一个时间段的batchMonitor*/
	private static final String BATCHMONITOR_DATE = "SELECT MAX(START_DATE) PRE_EXECUTE_DATE FROM BATCH.BATCH_STATUS WHERE FUNC_ID = ?";
	
	/*上一个阶段时间点*/
	private static final String THELASTDATE_SQL = "select * from (select * from  (select * from BATCH.batch_status where FUNC_ID=? order by START_DATE desc) where ROWNUM<=2 )"
		+" where SID not in (select SID from  (select * from BATCH.batch_status where FUNC_ID=? order by START_DATE desc) where ROWNUM<=1)";
	 
	
	/*存储未完成的*/
	private static final String INSERT_RUNNING_SQL = " INSERT INTO BATCH.BATCH_THELAST_RUNNING( JOB_ID, FUNC_ID, START_DATE) VALUES(?,?,?)";
	
	
	private static final String THELASTMESSAGE_SQL = "SELECT  Z.SID,Z.FUNC_ID,A.NAME,Z.START_DATE,Z.END_DATE,Z.PERIOD,A.SERVER,Z.STATUS,Z.REASON,A.FOWNER,A.SOWNER"
		 +" from (SELECT B.SID,B.FUNC_ID,B.START_DATE,B.END_DATE,round((B.end_date - B.start_date)*1440) PERIOD,B.STATUS,B.REASON"
		 +" from BATCH.batch_status B,BATCH.BATCH_THELAST_RUNNING C  WHERE B.SID=C.JOB_ID AND B.START_DATE BETWEEN ? and ?) Z"
		 +" left join BATCH.batch_func A ON Z.FUNC_ID=A.ID"; 
	
	/*短息内容*/
	private static final String SMS_SQL = "INSERT INTO SMS_TBL(SID,FUNC_SID, CREATE_TIME, SEND_TIME, PHONE_NUMBER, CONTENT, STATUS)"
		+" VALUES(SMS_SID_SEQ.NEXTVAL, 7, ?, ?, ?, ?, 1) ";
	
	
	@Override
	public void execute() {
	
		logger.info(StringUtils.uncapitalize(this.getClass().getSimpleName()));

		DateTime preExecuteDate = new DateTime().withHourOfDay(0)
				.withMinuteOfHour(0).withSecondOfMinute(0)
				.withMillisOfSecond(0);

		List<Map<String, Object>> maxBatchStatusDate = this
				.getBatchOperations()
				.queryForList(BATCHMONITOR_DATE,JOBNAME);

		if (maxBatchStatusDate.size() == 1) {
			preExecuteDate = new DateTime((Date) maxBatchStatusDate.get(0).get(
					"PRE_EXECUTE_DATE"));
		}

		DateTime currentExcuteDate = new DateTime();


		/*邮件主题*/
		String content = getContent();
		
		
		/* 时间段内失败的排程信息 */
		List<Map<String,Object>> listResultF = this.getBatchOperations()
				.queryForList(RESULTFAILED_SQL,
				preExecuteDate.toDate(),
				currentExcuteDate.toDate());
		
		/* 时间段内进行中的排程信息 */
		List<Map<String,Object>> listResultING = this.getBatchOperations()
				.queryForList(RESULTING_SQL,
				preExecuteDate.toDate(),
				currentExcuteDate.toDate());
		
		/* 时间段内成功的排程信息 */
		List<Map<String,Object>> listResultSUL = this.getBatchOperations()
				.queryForList(RESULTSUL_SQL,
				preExecuteDate.toDate(),
				currentExcuteDate.toDate());
		
		
		/*邮件body_当前时间段*/
		StringBuilder resultContent = new StringBuilder();
		
		/*
		 * 存储时间段内的所有排程信息*
		 */
		
		getResultConent(resultContent,listResultF);
		getResultConent(resultContent,listResultING);
		getResultConent(resultContent,listResultSUL);
		
		
		String Statistics_status = "<b>统计: 成功    :<b style='color:green'>"+listResultSUL.size()+";</b>  失败：  <b style='color:red'>"+listResultF.size()+";</b>  进行中:  <b style='color:orange'>"+listResultING.size()+";</b></b>";
		
		/*存储进行中的排成*/
		if(listResultING.size()>0){
			for (int i = 0; i < listResultING.size(); i++) {
				Map<String,Object> map = listResultING.get(i);
				this.getBatchOperations().update(INSERT_RUNNING_SQL,
					Integer.parseInt(map.get(SID).toString()),
					map.get(FUNC_ID).toString(),
					(Date)map.get(START_DATE));
			}
		}
	
		/*---------------------------------------------------------------------------------------------------------------------------*/
		
		/*
		 * 追踪上一个时间段
		 */
			
		DateTime theLastDate = new DateTime().withHourOfDay(0)
		.withMinuteOfHour(0).withSecondOfMinute(0)
		.withMillisOfSecond(0);
	
		List<Map<String, Object>> theLasetDateList = this.getBatchOperations()
		.queryForList(THELASTDATE_SQL,JOBNAME,JOBNAME);
		
		
		if (theLasetDateList.size() == 1)  
			theLastDate = new DateTime((Date)theLasetDateList.get(0).get(START_DATE));
				
		String currentTime =" 排程状态报告:"+preExecuteDate.toString("yyyy-MM-dd HH:mm")+"  --  "+currentExcuteDate.toString("yyyy-MM-dd HH:mm")+"  时间内启动的排程状况";
		/* 上一个时间段内进行中的排程信息 */
		List<Map<String,Object>> theLastlistResultING = this.getBatchOperations()
				.queryForList(THELASTMESSAGE_SQL,
				theLastDate.toDate(),
				preExecuteDate.toDate());
		
		String theLastContent = "";
		if(theLastlistResultING.size()>0){
			String theLastTime = "追踪: "+theLastDate.toString("yyyy-MM-dd HH:mm")+"  --  "+preExecuteDate.toString("yyyy-MM-dd HH:mm")+"  时间段内启动未完成的排程";
			StringBuilder theLastRunningResultContent = new StringBuilder(); 
			/*赋值*/
			getResultConent(theLastRunningResultContent, theLastlistResultING);
			
			theLastContent = String.format(getTheLastContent(),theLastTime,theLastRunningResultContent);
		}
		
		/*------------------------------------开始发送短信和邮件----------------------------------------------------------------------------*/
		
		/*获取properties文件,该文件里封装了邮件接受者*/
		ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIESNAME);
		
		/*
		 * 短信发送
		 */
		 
		try {
			/*短信内容*/
			String smsContent = String.format("排程状态报告 : %s ~ %s, %s ",preExecuteDate.toString("yyyy-MM-dd HH:mm"),
					currentExcuteDate.toString("yyyy-MM-dd HH:mm"), 
					"成功:["+ listResultSUL.size() + "]个;失败:["+ listResultF.size() + "]个,进行中:[" + listResultING.size() + "]个");
			
			StringTokenizer sms = new StringTokenizer(
					bundle.getString(SMSTO), ",");
			List<Object[]> msgList = new ArrayList<Object[]>();
			
			Date currentDate = new Date();
			
			while (sms.hasMoreElements()) {
				String phone = sms.nextToken();
				Object[] msgArray = new Object[4];
				msgArray[0] = currentDate;
				msgArray[1] = currentDate;
				msgArray[2] = phone;
				msgArray[3] = smsContent;
				msgList.add(msgArray);
			}
			
			this.getPortalJdbcOperations().batchUpdate(SMS_SQL, msgList);
		} catch (Exception e) {
			
		}
		
		
		/*邮件title*/
		String status = listResultF.size()>0?"有失败状况，请尽快确认":"全部成功";
		String subject =" 排程状态报告:"+preExecuteDate.toString("yyyy-MM-dd HH:mm")+"  ~  "+currentExcuteDate.toString("yyyy-MM-dd HH:mm")+"  "+status;
		/*获取邮件发送factroy*/
		MailService mailService = getMailService();
		/*解析邮件接受者*/
		StringTokenizer tos = new StringTokenizer(
				bundle.getString(ADDRESSEE), ",");
		while (tos.hasMoreElements()) {
			String to = tos.nextToken();
			mailService.to(to);
		}
		/*解析邮件抄送对象*/
		StringTokenizer ccs = new StringTokenizer(
				bundle.getString(CC), ",");
		while (ccs.hasMoreElements()) {
			String cc = ccs.nextToken();
			mailService.cc(cc);
		}
		/*发送邮件*/
		mailService.subject(subject)
				.content(String.format(content,theLastContent,Statistics_status,currentTime,resultContent))
				.send();

	}//execute
	
	/*
	 * 创建邮件内容的主体架构
	 */
	public String getContent(){
		String content = new StringBuilder()//style=text-align:center>
		.append("<html><body>%s<br><br>%s")
		.append("<table border='1' bgcolor='#F2F2F2'>")
		.append("<tr><th colspan='10'><font color=''>%s</font></th></tr>")
		.append("<tr bgcolor='#308DBB'>")
		.append("<th nowrap>排程编号</th>")
		.append("<th nowrap>排程描述</th>")
		.append("<th nowrap>排程开始时间</th>")
		.append("<th nowrap>排程结束时间</th>")
		.append("<th nowrap>排程运行时间(min)</th>")
		
		.append("<th nowrap>排程运行状态</th>")
		.append("<th nowrap>排程失败原因</th>")
		.append("<th nowrap>排程第一处理人</th>")
		.append("<th nowrap>排程第二处理人</th>")
		.append("<th nowrap>服务器</th>")
		.append("</tr>%s")
		.append("</table></body></html>").toString();
			
		return content;
	}
	
	/*
	 *添加上一个阶段进行中的排程信息 
	 */
	public String getTheLastContent(){
		String content = new StringBuilder()//style=text-align:center>
		.append("<table bgcolor='#F2F2F2' border='1'>")
		
		.append("<tr><th colspan='10'><font color='orange'>%s</font></th></tr>")
		.append("<tr bgcolor='#FFDD55'>")
		.append("<th nowrap>排程编号</th>")
		.append("<th nowrap>排程描述</th>")
		.append("<th nowrap>排程开始时间</th>")
		.append("<th nowrap>排程结束时间</th>")
		.append("<th nowrap>排程运行时间(min)</th>")
		
		.append("<th nowrap>排程运行状态</th>")
		.append("<th nowrap>排程失败原因</th>")
		.append("<th nowrap>排程第一处理人</th>")
		.append("<th nowrap>排程第二处理人</th>")
		.append("<th nowrap>服务器</th>")
		.append("</tr>%s")
		.append("</table>").toString();
		
		return content;
	}
	
	
	/*时间段内排程信息 to　mail*/
	public void getResultConent(StringBuilder sb,List<Map<String,Object>> listResultS){
		if(listResultS.size()>0){
			
			for (int i = 0; i < listResultS.size(); i++) {
				Map<String,Object> mapS = listResultS.get(i);
				
				sb.append("<tr>");
				
				sb.append(String.format(TDSTR,mapS.get(FUNC_ID)));
				
				/*排程描述*/
				String name = "";
				if(mapS.get(NAME)!=null)
					name= String.format(TDCOLOR,"black",mapS.get(NAME));
				else name= String.format(TDCOLOR,"red",NULLNAME);
				sb.append(name);
	
				sb.append(String.format(TDSTR,mapS.get(START_DATE)==null ? "" 
							: dateFormat.format(mapS.get(START_DATE))));
				sb.append(String.format(TDSTR,mapS.get(END_DATE)==null ? "" 
							: dateFormat.format(mapS.get(END_DATE))));
				
				/*排程运行时间*/
				String period ="";
				if(mapS.get(PERIOD)!=null){
					int date = Integer.parseInt(mapS.get(PERIOD).toString());
					if(date==0) period="<  1";
					if(date>0) period= mapS.get(PERIOD).toString();
				}
				sb.append(String.format(TRSTRCENTER,period));
				
				
				/*状态判断*/
				String statusPar = mapS.get(STATUS)==null ? "" : mapS.get(STATUS).toString();
				String statusStr="";
				String color="";
				if(statusPar.equals(FUNC_STATUS_FAILED)){
					statusStr="<b>失败</b>";
					color="red";
				}
					
				if(statusPar.equals(FUNC_STATUS_SUCCESSED)){
					statusStr="<b>成功</b>";
					color="#228B22";
				}
					
				if(statusPar.equals(FUNC_STATUS_RUNNING)){
					statusStr="<b>正在进行中</b>";
					color="orange";
				}
				
				sb.append(String.format(TDCOLORCENTER,color,statusStr));
				
				/*异常信息*/
				String reason = "";
				if(mapS.get(REASON)!=null){
					String urlError = "<a href='http://10.0.0.205:8680/QueryException/query?sId=%s' style='color:red'>点击查询错误信息</a>";
					reason=String.format(TDCOLOR,"red",String.format(urlError,mapS.get(SID).toString()));
				}
						
				else reason=String.format(TDCOLOR,"red","");
				sb.append(reason);
				
				String MailtoTo =  "<a href='mailto:%s'>%s</a>";
				
				sb.append(String.format(TRSTRCENTER,mapS.get(FOWNER)==null ? "" 
							: String.format(MailtoTo,mapS.get(FOWNER) ,mapS.get(FOWNER))));  
				
				
				sb.append(String.format(TRSTRCENTER,mapS.get(SOWNER)==null ? "" 
							: String.format(MailtoTo,mapS.get(SOWNER) ,mapS.get(SOWNER))));
						
				/*所属服务器*/
				sb.append(String.format(TRSTRCENTER,mapS.get(SERVER)==null ? "" 
							: mapS.get(SERVER)));
				sb.append("</tr>");
			}
			
		}
	} 
}