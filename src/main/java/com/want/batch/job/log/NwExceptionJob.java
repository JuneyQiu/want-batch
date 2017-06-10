package com.want.batch.job.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.monitor.HRModel;
import com.want.batch.job.utils.DBUtils;
import com.want.batch.job.utils.ProjectConfig;
import com.want.component.mail.MailService;

@Component
public class NwExceptionJob extends AbstractWantJob {

	private static final Log logger = LogFactory.getLog(NwExceptionJob.class);

	private final String logPath = ProjectConfig.getInstance().getString(
			"nw.exception.base");
	private final String nwTracePath = ProjectConfig.getInstance().getString(
			"nw.log.path");

	private Timestamp MAX_DATE;

	//@Autowired
	//private DataSource historyRptlogDataSource;

	@Autowired
	private SimpleJdbcOperations historyRptlogJdbcOperations;

	@Override
	public void execute() throws Exception {
		
		MAX_DATE = getMaxDate();

		String[] cePaths = nwTracePath.split(",");
		for (int i = 0; i < cePaths.length; i++) {
			String localPath = logPath + "/" + cePaths[i];
			String apServer = cePaths[i];
			File dir = new File(localPath);
			String[] children = dir.list();
			if (children != null) {
				for (int j = 0; j < children.length; j++) {
					String filename = children[j];
					if (filename.indexOf(".trc") != -1) {
						String fullPath = localPath + "/" + filename;
						logger.info("parse " + fullPath);
						try {
							parse(fullPath, apServer);
						} catch (Exception e) {
							logger.error("process file " + fullPath
									+ " for server " + apServer + ": "
									+ e.getMessage());
						}
					}
				}
			}
		}
		
		DBUtils.rebuildTable(historyRptlogJdbcOperations, "PROD_ERROR_INFO", "PROD_ERROR_INFO_TMP", "create_date", 3, 3);
		
		/*
		 * 开始发送邮件
		 */
		
		logger.info("开始发送邮件操作.....: "+new Date());
		NWExceptionSendMail ns = new NWExceptionSendMail();
		ns.sendMail();
	}

	private void parse(String filePath, String server) throws IOException,
			SQLException {
		File file = new File(filePath);

		FileReader filereader = new FileReader(file);
		BufferedReader bufferedreader = new BufferedReader(filereader);
		String line = null;
		int index = -1;
		NwException excp = null;

		while ((line = bufferedreader.readLine()) != null) {

			try {
				if (line.indexOf("#2.0#") == 0) {
					if (excp != null) {
						save(excp, false);
						logger.debug(excp);
					}

					index = 0;
					String[] tokens = line.split("#+");

					if (tokens[4] != null
							&& (tokens[4].equals("Error") || tokens[4]
									.equals("Fatal"))) {
						excp = new NwException();
						excp.setDate(tokens[2]);

						if (MAX_DATE != null && excp.isAfter(MAX_DATE)) {
							excp = null;
						} else if (MAX_DATE == null || !excp.isAfter(MAX_DATE)) {
							excp.setServer(server);
							excp.setType(tokens[4]);
						}
					} else {
						excp = null;
					}

				} else {
					if (excp != null) {
						if (index >= 0)
							index++;

						if (index == 1) {
							String[] tokens = line.split("#+");

							for (int i = 0; i < tokens.length; i++) {
								if (tokens[i].matches("[0-9]{8}")) {
									excp.setId(tokens[i]);
									excp.setPath(tokens[i - 1]);
									break;
								} else if (tokens[i].equals("Guest")) {
									excp.setId("Guest");
									if (tokens[i - 2].matches("[0-9]+")) {
										excp.setPath(tokens[i - 1]);
									} else
										excp.setPath(tokens[i - 2]);
									break;
								} else if (tokens[i].equals("ZBOTEST1")) {
									excp.setId("ZBOTEST1");
									excp.setPath(tokens[i - 1]);
									break;
								} else if (tokens[i].equals("System.err")) {
									excp.setPath(tokens[i - 1]);
									excp.setId(tokens[i + 1]);
									if (tokens[i + 1].equals("Guest")
											&& tokens[i - 1]
													.matches("[A-Z0-9]+"))
										excp.setPath(tokens[1]);
									break;
								}
							}

							if (excp.getId() == null && excp.getPath() == null) {
								index = -1;
								excp = null;
								logger.error("cannot parse: " + line);
							}

						} else if (index == 2) {
							if (line.length() > 1000) {
								logger.error("length greater than 1000, truncate to 900: "
										+ line);
								line = line.substring(0, 900);
							}
							excp.setMessage(line);
						} else if (index > 2) {
							if (excp != null) {
								excp.appendTrace(line);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				logger.info(excp.getMessage()==null);
				logger.info(excp);
				excp = null;
			}
		}

		bufferedreader.close();
		filereader.close();

		if (excp != null){
			save(excp, true);
		}
		//
		save();
	}

	private static final String ISNERT_SQL = "INSERT INTO RPTLOG.PROD_ERROR_INFO "
			+ " (SID, URI, CREATE_DATE, LOGIN_USER, CODE, MESSAGE, TRACE, CLIENT_IP, SERVER_IP, SERVER_PORT, ENV) "
			+ " VALUES (RPTLOG.PROD_ERROR_INFO_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, 0, 'NW')";

	private static final String sql = "delete from RPTLOG.PROD_ERROR_INFO where env='NW' and create_date<sysdate-10";

	ArrayList<Object[]> params = new ArrayList<Object[]>();
	
	private void save(NwException excp, boolean force) throws SQLException {
		if (excp.getId() != null && excp.getPath() != null && excp.getMessage() != null) {
			if (excp.getMessage().trim().length() > 0) {
				Object[] row = new Object[] { 
						excp.getPath(), 
						excp.getDate(),
						excp.getId(),
						excp.getType(),
						excp.getMessage(),
						excp.getTrace(),
						excp.getServer(),
						excp.getServer()
				};
				params.add(row);
				
				/*
				 * 将批量插入的量缩小为1000
				 */
				if (params.size() > 1000 || force == true) {
					historyRptlogJdbcOperations.batchUpdate(ISNERT_SQL, params);
					logger.info("inserts " + params.size());
					params = new ArrayList<Object[]>();
				}
			}
		} else {
			logger.error("ID, URI or message is null: \n" + excp);
		}
	}
	
	private void save() throws SQLException {
		if (params.size() > 0) {
			historyRptlogJdbcOperations.batchUpdate(ISNERT_SQL, params);
			logger.info("inserts " + params.size());
			params = new ArrayList<Object[]>();
		}
	}
	
	private Timestamp getMaxDate() {
		String sql = "select max(create_date) from RPTLOG.PROD_ERROR_INFO where env='NW'";
		return historyRptlogJdbcOperations.queryForObject(sql, Timestamp.class);
	}
	
	
	/*
	 * 内部类 发送NW异常邮件
	 */
	
	public class NWExceptionSendMail{
		private static final String ADDRESS = "project";
		private static final String EMAILADDRESS = "project.nw.monitor.mail.to";
		private static final String EMAILCC = "project.nw.monitor.mail.cc";
		private static final String MAIL_TITLE = "NW 异常报告:";
		

		
			
		private static final String SQL = "SELECT MESSAGE, LENGTH(TRACE), MIN(SID) AS SID,URI, COUNT(1) num FROM RPTLOG.PROD_ERROR_INFO WHERE ENV='NW' and "
				+" CREATE_DATE between ? and ?"
				+" GROUP BY MESSAGE, LENGTH(TRACE), URI"
				+" ORDER BY COUNT(1) DESC";
			
		private final String viewErrorUrl = "http://10.0.0.205:8680/QueryException_NW/query?sId=%s";
		
			  
			 
		public void sendMail(){
			
			String errorNameUrl = "errorMessage"+new SimpleDateFormat("yyyyMMdd").format(new Date())+".html";
			
			DateTime endDate = new DateTime().withHourOfDay(0).withMinuteOfHour(0)
			.withSecondOfMinute(0).withMillisOfSecond(0);

			DateTime startDate = (endDate.getDayOfWeek() == DateTimeConstants.WEDNESDAY) ? endDate
				.minusDays(1) : endDate.minusDays(1);
						
			logger.info("startTime:"+startDate.toDate());
			logger.info("endTimee:"+endDate.toDate());	
			
			List<Map<String,Object>> list = historyRptlogJdbcOperations.queryForList (SQL,startDate.toDate(), endDate.toDate());
			
			
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
						"<head> <meta http-equiv='Content-Type' content='text/html; charset=gb2312'></head>" +
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
	
	}

}
