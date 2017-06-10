package com.want.batch.job.monitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.component.mail.MailService;


@Component("nwMonitorJob")
public class NWExceptionMonitorJob extends AbstractWantJob{
	private static final String ADDRESS = "project";
	private static final String EMAILADDRESS = "project.nw.monitor.mail.to";
	private static final String EMAILCC = "project.nw.monitor.mail.cc";
	private static final String MAIL_TITLE = "NW 异常报告:";
	

	
		
	private static final String SQL = "SELECT MESSAGE, LENGTH(TRACE), MIN(SID) AS SID,URI, COUNT(1) num FROM RPTLOG.PROD_ERROR_INFO WHERE ENV='NW' and "
			+" CREATE_DATE between ? and ?"
			+" GROUP BY MESSAGE, LENGTH(TRACE), URI"
			+" ORDER BY COUNT(1) DESC";
		
	private final String viewErrorUrl = "http://10.0.0.205:8680/QueryException_NW/query?sId=%s";
	
	@Autowired(required = false)
	@Qualifier("historyRptlogJdbcOperations")
	private SimpleJdbcOperations historyRptlogJdbcOperations;
		  
		 
	
	@Override
		public void execute() throws Exception {
		
		String errorNameUrl = "errorMessage"+new SimpleDateFormat("yyyyMMdd").format(new Date())+".html";
		
		DateTime endDate = new DateTime().withHourOfDay(0).withMinuteOfHour(0)
		.withSecondOfMinute(0).withMillisOfSecond(0);

		DateTime startDate = (endDate.getDayOfWeek() == DateTimeConstants.WEDNESDAY) ? endDate
			.minusDays(1) : endDate.minusDays(1);
					
		logger.info("startTime:"+startDate.toDate());
		logger.info("endTimee:"+endDate.toDate());	
		
		List<Map<String,Object>> list = this.historyRptlogJdbcOperations.queryForList (SQL,startDate.toDate(), endDate.toDate());
		
		
		String ResultContent = getResultContent(list);
		logger.info("开始上传205");
		
		writeFile(ResultContent,errorNameUrl);
		
		logger.info("结束 上传。。。");
		
		
		/*
		 * 开始发送邮件
		 */

		/*邮件title*/
		String subject = String.format(MAIL_TITLE+HRModel.getYestedayDate(-1)+"--"+
				new SimpleDateFormat("yyyyMMdd").format(new Date()));
		
		
		ResourceBundle bundle = ResourceBundle.getBundle(ADDRESS);
		//获取邮件工程
		MailService mailservice = getMailService();
		mailservice.setEncoding("UTF-8");
		
		//解析接收者
		StringTokenizer sto = new StringTokenizer(
				bundle.getString(EMAILADDRESS), ",");
		while (sto.hasMoreElements()) {
			String to = sto.nextToken();
			mailservice.to(to);
			
		}
		//抄送
		StringTokenizer stk = new StringTokenizer(
				bundle.getString(EMAILCC),",");
		while(stk.hasMoreElements()){
			String cc = stk.nextToken();
			mailservice.cc(cc);
		}
        
		String content = "Dear All:<br>" +
				"<a style='color:red' href = 'http://10.0.0.205:8680/QueryException/%s'>点击查看NW异常详情</a>";
		
		String content_url = String.format(content, errorNameUrl);
		
		getMailService().subject(subject)
		.content(content_url)
		.send();
		logger.info("已发送邮件");
		
	}	
	
		public String getResultContent(List<Map<String,Object>> list){
			
			
			System.out.println("List size:"+list.size());
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < list.size(); i++) {
				
				
				Map<String,Object> map = list.get(i);
				
				int sId = Integer.parseInt(map.get("SID").toString());
				
				String number = map.get("num")==null?"0":map.get("num").toString();
				
				String parUrlValue = String.format(viewErrorUrl,sId);
				
				
				String href = "<a href='%s'>"+number+"</a>";
				
				String tdValue = String.format(href, parUrlValue);
			
				sb.append("<tr>")
				.append("<td align='center'>")
				.append((i + 1))
				.append("</td>")
				.append("<td align='center'>")
				.append(tdValue)
				.append("</td>")
				.append("<td align='center'>NW")
				.append("</td>")
				.append("<td>")
				.append(map.get("URI") == null ? "" : map.get("URI").toString()).append("</td>")
				.append("<td align='left'><xmp>")
				.append(map.get("MESSAGE") == null ? "" : map.get("MESSAGE").toString().trim())
				.append("</xmp></td>")
			    .append("</tr>");

			}
		
			return String.format(getContent(),sb.toString());
		}
	
		public String getContent(){
			
			StringBuilder content= new StringBuilder();
			
			content.append("<html>" +
					"<head> <meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head>" +
					"<body><table width='1311' border='1' bgcolor='#CCCCCC' style='table-layout:fixed;word-break:break-all;word-wrap:break-word;'>");
			content.append("<tr bgcolor='#FF6633' height='25'><th width='41'>序号</th><th width='83'>发生次数</th><th width='83'>所属应用</th><th width='510'>功能URI</th><th width='550'>EXCEPTION</th></tr>");
			content.append("%s");
			content.append("</table></body></html>");
			return content.toString();
		}
		
		
		
		
		public void writeFile(String content,String errorNameUrl){
			try { 
				
				String url = "//10.0.0.205/monitorService/webapps/QueryException/"+errorNameUrl;
	            FileOutputStream out = new FileOutputStream(url);
	            
	            out.write(content.getBytes()); 
	            out.close(); 
	        } catch (FileNotFoundException e) { 
	            e.printStackTrace(); 
	        } catch (IOException e) { 
	            e.printStackTrace(); 
	        } 

		}

		public void setHistoryRptlogJdbcOperations(
				SimpleJdbcOperations historyRptlogJdbcOperations) {
			this.historyRptlogJdbcOperations = historyRptlogJdbcOperations;
		}
		
		
		
}

	



