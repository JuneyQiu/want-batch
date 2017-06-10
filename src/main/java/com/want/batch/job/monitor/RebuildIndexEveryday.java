package com.want.batch.job.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.job.AbstractWantJob;
import com.want.component.mail.MailService;

public class RebuildIndexEveryday extends AbstractWantJob{
	private static final int ICUSTOMER = 0;
	private static final int HWO9 = 1;
	private static String HW09TITLE = "HW09";
	private static String ICUSTOMERTITLE = "ICUSTOMER";
	private static final String REBUILDINDEX_SQL = "ALTER INDEX %s REBUILD NOLOGGING";
	private static final String ALERTLOG = "ALTER INDEX %s LOGGING";
	
	private static final String TR1 ="<tr>";
	private static final String TR2 ="</tr>";
	private static final String MAIL_TITLE ="每天 重建索引报告：有重建失败的索引,请尽快检查 ";
	private static final String MAIL_TITLE_SUCESSFUL = "每天 重建索引报告: 全部成功";
	private static final String ADDRESSEE = "project.rebuildIndex.mail.hr.to";
	private static final String CC = "project.rebuildIndex.mail.hr.cc";
	private static final String TDSTRFIRST = "<td><font color='%s'>%s</font></td>";
	private static final String PROPERTIESNAME = "project";
	private SimpleJdbcOperations hw09Jdbc; 
	private SimpleJdbcOperations icustomerJdbc;
	@Override
	public void execute() throws Exception {
		List<String> ListHW09 = listIndexs(HWO9);
		List<String> ListICustomer = listIndexs(ICUSTOMER);
		
		List<String> listLoseICustomer = new ArrayList<String>();
		List<String> listLoseHW09 = new ArrayList<String>();
			
		logger.info(String.format("%s 有%s个大于%s的indexName.......",HW09TITLE,ListHW09.size(),100000));
		for (int i = 0; i < ListHW09.size(); i++) {
			try {
				hw09Jdbc.update(String.format(REBUILDINDEX_SQL, ListHW09.get(i)));
				logger.info(String.format("%s-%s rebuild index 成功~~~~~",HW09TITLE,ListHW09.get(i)));
				
				hw09Jdbc.update(String.format(ALERTLOG, ListHW09.get(i)));//
				logger.info(String.format("%s-%s  LOGGING 成功~~~~~",HW09TITLE,ListHW09.get(i)));
				
			} catch (Exception se) {
				listLoseHW09.add(ListHW09.get(i));
					
				logger.info(String.format("%s-%s rebuild index 失败~~~~~",HW09TITLE,ListHW09.get(i)));
				
				logger.info(String.format("%s-%s  LOGGING 失败~~~~~",HW09TITLE,ListHW09.get(i)));//	
			}
		}
		
	
		logger.info(String.format("%s 有%s个大于%s的indexName.......",ICUSTOMERTITLE,ListICustomer.size(),100000));
		for (int i = 0; i < ListICustomer.size(); i++) {
			try {
				icustomerJdbc.update(String.format(REBUILDINDEX_SQL, ListICustomer.get(i)));
				logger.info(String.format("%s-%s rebuild index 成功~~~~~",ICUSTOMERTITLE,ListICustomer.get(i)));
				
				icustomerJdbc.update(String.format(ALERTLOG, ListICustomer.get(i)));//
				logger.info(String.format("%s-%s  LOGGING 成功~~~~~",ICUSTOMERTITLE,ListICustomer.get(i)));
				
				
			} catch (Exception se) {
				listLoseICustomer.add(ListICustomer.get(i));
				
				logger.info(String.format("%s-%s rebuild index 失败~~~~~",ICUSTOMERTITLE,ListICustomer.get(i)));
				
				logger.info(String.format("%s-%s  LOGGING 失败~~~~~",ICUSTOMERTITLE,ListICustomer.get(i)));//	
			}
		}
		
		
		/*
		 * 开始重新Rebuild
		 */
		
		List<String> loserHw09 = new ArrayList<String>();
		
		
		if(listLoseHW09.size()>0){
			for (int i = 0; i < listLoseHW09.size(); i++) {
				try {
					
					hw09Jdbc.update(String.format(REBUILDINDEX_SQL, listLoseHW09.get(i)));
					logger.info(String.format("%s-%s rebuild index 成功~~~~~",HW09TITLE,listLoseHW09.get(i)));
					
					hw09Jdbc.update(String.format(ALERTLOG, ListHW09.get(i)));//
					logger.info(String.format("%s-%s  LOGGING 成功~~~~~",HW09TITLE,listLoseHW09.get(i)));
					
				} catch (Exception se) {
					for(int j=1;j<=60;j++){
						try {
							Thread.sleep(1000*60); //1分钟
						} catch (InterruptedException e) {}
						
						try {
							hw09Jdbc.update(String.format(REBUILDINDEX_SQL, listLoseHW09.get(i)));
							logger.info(String.format("%s-%s 重新rebuild第%s次 成功啦~~~~~",HW09TITLE,listLoseHW09.get(i),j));
							
							hw09Jdbc.update(String.format(ALERTLOG, listLoseHW09.get(i)));
							logger.info(String.format("%s-%s 重新LOGGING第%s次 成功啦~~~~~",HW09TITLE,listLoseHW09.get(i),j));
							break;
						} catch (Exception e) {
							logger.info(String.format("%s-%s 重新rebuild第%s次 失败~~~~~",HW09TITLE,listLoseHW09.get(i),j));
							logger.info(String.format("%s-%s 重新LOGGING第%s次 失败~~~~~",HW09TITLE,listLoseHW09.get(i),j));
							if(j==60) loserHw09.add(listLoseHW09.get(i));
						}
					}
				}
			}
		}
		
		List<String> loserICuseromer = new ArrayList<String>();
		if(listLoseICustomer.size()>0){
			for (int i = 0; i < listLoseICustomer.size(); i++) {
				try {
					icustomerJdbc.update(String.format(REBUILDINDEX_SQL, listLoseICustomer.get(i)));
					logger.info(String.format("%s-%s rebuild index 成功~~~~~",ICUSTOMERTITLE,listLoseICustomer.get(i)));
					
					icustomerJdbc.update(String.format(ALERTLOG, listLoseICustomer.get(i)));//
					logger.info(String.format("%s-%s  LOGGING 成功~~~~~",ICUSTOMERTITLE,listLoseICustomer.get(i)));
					
				} catch (Exception se) {
			
					for(int j=1;j<=60;j++){
						try {
							Thread.sleep(1000*60); //1分钟
						} catch (InterruptedException e) {}
						
						try {
							icustomerJdbc.update(String.format(REBUILDINDEX_SQL, listLoseICustomer.get(i)));
							logger.info(String.format("%s-%s 重新rebuild第%s次 成功啦~~~~~",ICUSTOMERTITLE,listLoseICustomer.get(i),j));
							icustomerJdbc.update(String.format(ALERTLOG, listLoseICustomer.get(i)));
							logger.info(String.format("%s-%s 重新LOGGING第%s次 成功啦~~~~~",ICUSTOMERTITLE,listLoseICustomer.get(i),j));
							break;
						} catch (Exception e) {
							logger.info(String.format("%s-%s 重新rebuild第%s次 失败~~~~~",ICUSTOMERTITLE,listLoseICustomer.get(i),j));
							logger.info(String.format("%s-%s 重新LOGGING第%s次 失败~~~~~",ICUSTOMERTITLE,listLoseICustomer.get(i),j));
							if(j==60) loserICuseromer.add(listLoseICustomer.get(i));
						}
					}	
				}
			}
		}
		
		/*
		 * 发送邮件
		 */
	
		boolean flag = true;
		Map<String, List<String>> mapLose = new HashMap<String, List<String>>();
		if(loserICuseromer.size()>0 || loserHw09.size()>0){	
			
			if(listLoseICustomer.size()>0){
				mapLose.put("10.0.0.181:ICustomer",loserICuseromer);
				flag = false;
			}
			
			if(listLoseHW09.size()>0){
				mapLose.put("10.0.0.181:HW09",loserHw09);
				flag =false;
			}
		} 
		
		sendMailLoseIndex(mapLose,flag);
		
	}
	
	public void sendMailLoseIndex(Map<String,List<String>> mapLose,boolean flag){
		/*邮件整体*/		
		String content = getContent(flag);
		
		/*邮件结果集*/
		StringBuilder resultContent = new StringBuilder("");
		
		/*邮件title*/
		String subject = "";
		
		
		if(flag ==false){
			resultContent = assignment(mapLose);
			subject = MAIL_TITLE;;
		}
		if(flag==true) subject = MAIL_TITLE_SUCESSFUL;
			
		
		
		/*获取properties文件,该文件里封装了邮件接受者*/
		ResourceBundle bundle = ResourceBundle.getBundle(PROPERTIESNAME);
		
		
		
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
				.content(String.format(content,resultContent))
				.send();
	}
	
	/*
	 * 创建邮件内容的主体架构
	 */
	public String getContent(boolean flag){
		String content = new StringBuilder()
		.append("<html><body> <span style='color:#228B22'> Dear 王维:</span> <br> <hr>" +
				"<table border='1' bgcolor='#F2F2F2' style=text-align:center>").append("<tr bgcolor='#FF6347'>")
		.append("<td colspan='2'><b>重建失败的索引</b></td>")
		.append("</tr>")
		.append("<tr>")
		.append("<td><b>Scheam</b></td>")
		.append("<td><b>Index</b></td>")
		.append("</tr>%s")
		.append("</table></body></html>").toString();
		
		String contentNull = new StringBuilder()
		.append("<html><body> <span style='color:#228B22'> Dear All:</span> <br> <hr>" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='color:#228B22'><span style='color:red'>每天</span>重建索引已完成，全部成功，请知悉!</span>" +
				"<br><span style='color:#228B22'>重建范围：181(ICUSTOMER,HW09)，每天上午11:00开始重建的索引由王维提供。</span>"+
				"</body>%s</html>")
		.toString();
		
		
		if(flag==true) return contentNull;
		else return content;
	}
	
	public StringBuilder assignment(Map<String,List<String>> mapLose){
		/*邮件结果集*/
		StringBuilder resultContent = new StringBuilder();
	
		Iterator<Entry<String, List<String>>> it = mapLose.entrySet().iterator();
		
		while(it.hasNext()){
			Entry<String,List<String>> entry = it.next();
			
			String key = entry.getKey();
			List<String> listIndexs = entry.getValue();
			
			String strs = "";
			for (int i = 0; i < listIndexs.size(); i++) {
				strs+=(listIndexs.get(i)+",");
			}
			
			resultContent.append(TR1);
			resultContent.append(String.format(TDSTRFIRST,"black",key));
			resultContent.append(String.format(TDSTRFIRST,"red",strs));
			resultContent.append(TR2);
		}
		return resultContent;
	}
	
	
	public List<String> listIndexs(int flag){
		if(flag==ICUSTOMER){
			List<String> list = new ArrayList<String>();
			list.add("ORDER_RECEIPT_HEAD_IDX01");
			list.add("DIVSION_PROJECT_REL_IDX01");
			list.add("PO_TYPE_TBL_IDX01") ;
			list.add("CUSTOMER_INFO_TBL_IDX01"); 
			list.add("CUSTOMER_INFO_TBL_IDX02") ;
			list.add("CUSTOMER_INFO_TBL_IDX03");
		    list.add("FMLT_IDX01");
		    list.add("FMLT_IDX02");
		    list.add("SDACTUAL_IDX01");
			list.add("SD_ACTUAL_APPSIDX01"); 
			list.add("SPECIAL_DISPLAY_ACTUAL_IDX02");
			
			list.add("DAILY_REPORT_IDX01_NEW");
			list.add("DAILY_REPORT_IDX02_NEW");
			list.add("DAILY_REPORT_IDX03_NEW");
			list.add("DAILY_REPORT_IDX04_NEW");
			list.add("DAILY_REPORT_IDX06_NEW");
			list.add("DAILY_REPORT_IDX07_NEW");
			list.add("DAILY_REPORT_IDX08_NEW");
			
			
			list.add("ICUSTOMER.IDX_TSS_TDI");
			list.add("ICUSTOMER.SYS_C0036874");
			list.add("ICUSTOMER.TASK_STORE_SUBROUTE_IDX01");
			list.add("ICUSTOMER.TSSE_IDX01");
			list.add("ICUSTOMER.SYS_C0036872");
			list.add("ICUSTOMER.DIVSION_SID_INX02");
			list.add("ICUSTOMER.TASK_SPECIAL_EXHIBIT_IDX02");
			list.add("ICUSTOMER.TASK_SPECIAL_EXHIBIT_IDX04");
			list.add("ICUSTOMER.TASK_STORE_SP_EXHIBIT_IDX03");
			list.add("ICUSTOMER.INDEX_TASK_STORE_DISPLAY_SID");
			list.add("ICUSTOMER.SYS_C0036871");
			list.add("ICUSTOMER.TASK_STORE_PRICE_IDX01");
			list.add("ICUSTOMER.TASK_STORE_PRICE_IDX02");
			list.add("ICUSTOMER.TASK_STORE_PRICE_IDX03");
			list.add("ICUSTOMER.TSL_IDX01");
			list.add("ICUSTOMER.TSL_IDX02");
			list.add("ICUSTOMER.SYS_C0036869");
			list.add("ICUSTOMER.SYS_C0036866");
			list.add("ICUSTOMER.TASK_LIST_IDX01");
			list.add("ICUSTOMER.TASK_DATE");
			list.add("ICUSTOMER.SYS_C006206");
			list.add("ICUSTOMER.IS_DELETE_IDX");
			list.add("ICUSTOMER.TASK_DETAIL_IDX02");
			list.add("ICUSTOMER.SYS_C0036864");
			list.add("ICUSTOMER.TASK_CUSTOMER_IDX01");
			list.add("ICUSTOMER.IDX_SAD_SDS");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY_IDX01");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY_IDX02");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY_IDX03");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY_IDX04");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY_IDX05");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY_IDX06");
			list.add("ICUSTOMER.SD_ACTUAL_DISPLAY_IDX07");
			list.add("ICUSTOMER.IDX_ASDP_PI");
			list.add("ICUSTOMER.SYS_C0049625");
			list.add("ICUSTOMER.APPLICATION_SD_PROD_IDX1");
			
			return list;
		}
		if(flag==HWO9){
			List<String> list = new ArrayList<String>();
			
			list.add("SUBROUTE_INFO_TBL_IDX02");
			list.add("SUBROUTE_INFO_TBL_IDX04");
			list.add("SUBROUTE_INFO_TBL_IDX05");
			list.add("SUBROUTE_INFO_TBL_IDX07");
			list.add("SUBROUTE_INFO_TBL_IDX08");
			list.add("SUBROUTE_INFO_TBL_IDX09");
			list.add("SUBROUTE_INFO_TBL_IDX10");
			list.add("SUBROUTE_INFO_TBL_IDX11");
			list.add("SUBROUTE_INFO_TBL_PK");
			list.add("ROUTE_INFO_TBL_IDX01");
			list.add("ROUTE_INFO_TBL_IDX03");
			list.add("ROUTE_INFO_TBL_IDX05");
			list.add("ROUTE_INFO_TBL_IDX06");
			list.add("ROUTE_INFO_TBL_IDX07");
			list.add("ROUTE_INFO_TBL_IDX08");
			list.add("ROUTE_INFO_TBL_PK");
			
			list.add("STORE_INFO_TBL_IDX01_N");
			list.add("STORE_INFO_TBL_IDX02_N");
			list.add("STORE_INFO_TBL_IDX03_N");
			list.add("STORE_INFO_TBL_IDX06_N");
			list.add("STORE_INFO_TBL_IDX09_N");
			list.add("STORE_INFO_TBL_IDX10_N");
			list.add("STORE_INFO_TBL_IDX11_N");
			list.add("STORE_INFO_TBL_IDX12_N");
			list.add("STORE_INFO_TBL_IDX13_N");
			list.add("STORE_INFO_TBL_IDX14_N");                                                                       
			
			list.add("BRANCH_INFO_TBL_IDX01");
			list.add("BRANCH_INFO_TBL_IDX02");              
			list.add("BRANCH_INFO_TBL_IDX03");                     
			list.add("BRANCH_INFO_TBL_IDX04");
			list.add("BRANCH_INFO_TBL_IDX09"); 
			list.add("EMP_INFO_TBL_IDX01");  
			list.add("EMP_INFO_TBL_IDX02");  
			list.add("EMP_INFO_TBL_IDX03");  
			list.add("EMP_INFO_TBL_IDX04");  
			list.add("EMP_INFO_TBL_IDX05");  
			
			return list;
		}
		else return null;
	} 
	
	public void setHw09Jdbc(SimpleJdbcOperations hw09Jdbc) {
		this.hw09Jdbc = hw09Jdbc;
	}
	public void setIcustomerJdbc(SimpleJdbcOperations icustomerJdbc) {
		this.icustomerJdbc = icustomerJdbc;
	}	
}
