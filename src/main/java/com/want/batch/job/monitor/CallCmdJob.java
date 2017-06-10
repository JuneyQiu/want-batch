package com.want.batch.job.monitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.utils.ProjectConfig;

@Component
public class CallCmdJob extends AbstractWantJob{
	private final Log logger = LogFactory.getLog(CallCmdJob.class);
	
	@Autowired
	private SimpleJdbcOperations historyRptlogJdbcOperations;
	
	private static final String PING_IP = "ping_ip";
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static final String SQL = "INSERT INTO RPTLOG.SERVER_PING_TIME (SERVER_IP,PING_INFO,CREATE_DATE) VALUES (?,?,?)";
	
	public Object [] getPingResult(String serverIp) {
		
		Date currentDate = new Date();
		
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec("ping "+serverIp+" -n 1");
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			
			
			while ((line = br.readLine()) != null) {
				
				if(line.indexOf("Reply from")!=-1 || line.indexOf("来自")!=-1){
					
					String lineArr [] =line.split(" ");
					
					for (int i = 0; i < lineArr.length; i++) {
						
						if(lineArr[i].indexOf("ms")!=-1){
							
							if(lineArr[i].indexOf("<")!=-1){
								
								String newLine = lineArr[i].substring(lineArr[i].indexOf("<")+1,lineArr[i].lastIndexOf("ms"));
								
								sb.append(newLine);
							}
							
							
							if(lineArr[i].indexOf("=")!=-1){
								
								String newLine = lineArr[i].substring(lineArr[i].indexOf("=")+1,lineArr[i].lastIndexOf("ms"));
								
								sb.append(newLine);
							}
							
						}
					}
					
				}
				
				if(line.indexOf("out")!=-1 || line.indexOf("超时")!=-1){
					
					logger.info(serverIp+" is time out..");
					
					sb.append(-1);
				}
					
			}
		} catch (Exception e) {
			logger.error(e.toString());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
		}
		
		
		return new String[]{serverIp,sb.toString(),sdf.format(currentDate),};
	}

	@Override
	public void execute() throws Exception {
			 
		String ipArr[] = ProjectConfig.getInstance().getString(PING_IP)
				.split(",");
		
		List<Object[]> listPars = new ArrayList<Object[]>();
		for (int i = 0; i < ipArr.length; i++) {
			String serverIp  = ipArr[i];
			
			Object[] pingRs = getPingResult(serverIp);
			
			listPars.add(pingRs);
		}
		
		this.historyRptlogJdbcOperations.batchUpdate(SQL,listPars);
		logger.info("批量插入"+listPars.size()+"笔数据");
		
		
/*		//排程执行23小时
		String overTime = getOverDate(20);
		Date OverDate = sdf.parse(overTime);
		logger.info("PING给定的结束时间:　"+sdf.format(OverDate));
		
		
		List<Object[]> listPars = new ArrayList<Object[]>();
		
		while(2>1){
			Date now = new Date();
			if(now.getTime()>=OverDate.getTime()){
				logger.info(sdf.format(now)+", PING结束");
				
				logger.info("给定的结束PING时间: "+sdf.format(OverDate));
				logger.info("给定的结束PING时间， listPars size: "+listPars.size());
				break;
			}		
			
			for (int i = 0; i < ipArr.length; i++) {
				String serverIp  = ipArr[i];
				
				Object[] pingRs = getPingResult(serverIp);
				
				listPars.add(pingRs);
			}
			
			if(listPars.size()==5000){
				try {
					this.historyRptlogJdbcOperations.batchUpdate(SQL,listPars);
					logger.info("批量插入"+listPars.size()+"笔数据");
					listPars.clear();
					logger.info("批量插入 清空listPars后 size: "+listPars.size());
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
				
			
			
		}//while
		
		
		logger.info(new Date()+"while循环结束，listPars size: "+listPars.size());
		
		try {
			this.historyRptlogJdbcOperations.batchUpdate(SQL,listPars);
			logger.info("插入"+listPars.size()+"笔数据");
		} catch (Exception e) {
			logger.error(e.toString());
		}
		*/
	}
	
	
	public static String getOverDate(int n){
		Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, n);
        String  overDate = sdf.format(calendar.getTime());
        return overDate;
		
	}
	
	
	
	public SimpleJdbcOperations getHistoryRptlogJdbcOperations() {
		return historyRptlogJdbcOperations;
	}

	public void setHistoryRptlogJdbcOperations(
			SimpleJdbcOperations historyRptlogJdbcOperations) {
		this.historyRptlogJdbcOperations = historyRptlogJdbcOperations;
	}

	public static void main(String[] args) {
		System.out.println(getOverDate(23));
	}
}
