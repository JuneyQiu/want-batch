package com.want.batch.job.monitor;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import com.want.batch.job.AbstractWantJob;
import com.want.data.pojo.WatchdogServer;
import com.want.utils.TimeUtils;

/**
 * 
 * @author 00159184 yicheng
 * 
 * This is a public class,Any system can use the class!!!
 */
public class WatchdogJob extends AbstractWantJob{
	
	/*
	 * from models.xml by IOC
	 * ip、namePort
	 * 
	 */
	private List<WatchdogServer> servers;
	
	/*Set the number of new connections   1*/
	private int retryTimes;
	
	/* If the connection fails then the next reconnect interval   5*/
	private int retryInterval;	

	/* rootURL */
	private String rootUrl = "";
	
	private String systemName="";
	/*
	 * jdbcUrl:jdbc:oracle:thin:@10.0.0.220:1521:orcl
	 * user:rpt
	 * pwd:rpt
	 */
	private SimpleJdbcOperations jdbcOperations;

	
	@Override
	public void execute() {
		for (WatchdogServer server : this.servers) {
			for (Map.Entry<String, String> serverInstance : server
					.getServerInstances().entrySet()) {

				Monitor monitor = new Monitor(serverInstance.getValue(), serverInstance.getKey());

				try {
					monitor.checked(1);
					logger.info(String.format("check[%s:%s] ok~",serverInstance.getKey(), serverInstance.getValue()));
				} catch (Exception e) {
					logger.info(String.format("check[%s:%s] error~",serverInstance.getKey(), serverInstance.getValue()));
					
					/*
					 * 连接失败，向database插入数据
					 */
					UmpMonitor umpMonitor = new UmpMonitor();
					umpMonitor.setSystemName(systemName);
					umpMonitor.setIp(server.getIp());
					umpMonitor.setPort(server.getNamePorts().get(serverInstance.getKey()));
					monitor.pushUmp(umpMonitor);
				}
			}
		}
	}

	/*
	 * Set Mehod
	 */
	
	public void setServers(List<WatchdogServer> servers) {
		this.servers = servers;
	}
	public List<WatchdogServer> getServers() {
		return servers;
	}
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}
	
	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}





	/**
	 * 
	 * @author 00159184
	 *	If you want to add short message function, The inner class can be used as the thread class
	 */
	private class Monitor{
		
		private final Log logger = LogFactory.getLog(this.getClass());
		private String name;
		private String url;
		private HttpURLConnection connection;
		private String errorMessage = "";

		private Monitor(String url, String name) {
			this.url = url;
			this.name = name;
		}

		/*
		 * 开始检查连接
		 */
		private void checked(int retryIndex) {
			try {
				this.openConnection(retryIndex);
				this.connect(retryIndex);
			} catch (Exception e) {
				if (retryIndex < retryTimes) {
					TimeUtils.waitingSeconds(retryInterval);
					checked(++retryIndex); /*重新连接 retry+1*/
				} else {
					throw new MonitorException(e);
				}
			}
		}

		private void openConnection(int retryIndex) {
			try {
				logger.debug(String.format("%sopen http connection : [%s]",(retryIndex > 1) ? "re-" : "", this.url));
				/* 
				 *创建httpUrl
				 */
				this.connection = (HttpURLConnection) new URL(this.url).openConnection();
				logger.debug(String.format("%sopen http connection[%s] successed.",(retryIndex > 1) ? "re-" : "", this.url));
			} catch (Exception e) {
				if (retryIndex == retryTimes) {
					this.errorMessage = this.getConnectionErrorMessage("open http connection error... ");
				} else {
					logger.debug(String.format("%sopen http connection[%s] error, retry[%s] ... ",(retryIndex > 1) ? "re-" : "",this.url, retryIndex));
				}
				throw new MonitorException(e);
			}
		}

		private void connect(int retryIndex) {
			try {
				logger.debug(String.format("%sconnect : [%s]",
						(retryIndex > 1) ? "re-" : "", this.url));
				/*
				 * 连接
				 */
				this.connection.connect();
				logger.debug(String.format("%sconnect[%s] successed.",(retryIndex > 1) ? "re-" : "", this.url));
			} catch (Exception e) {
				if (retryIndex == retryTimes) {
					this.errorMessage = this.getConnectionErrorMessage("connect error...");
				} else {
					logger.debug(String.format("%sconnect[%s] error, retry[%s] ... ",(retryIndex > 1) ? "re-" : "", this.url,retryIndex));
				}
				throw new MonitorException(e);
			}
		}

		/*
		 * If the connection fails then the wrong information into the database
		 */
		private void pushUmp(UmpMonitor umpMonitor) {

			umpMonitor.setMonitorLevel(UmpMonitorLevel.L5);
			umpMonitor.setMessage(this.errorMessage);
			umpMonitor.setCreateDate(new Date());

			jdbcOperations
					.update(
							new StringBuilder()
									.append(
											"INSERT INTO TIVOLI_MONITOR(ID, SYSTEM_NAME, IP, PORT, MESSAGE, MONITOR_LEVEL, CREATE_DATE, HOSTNAME) ")
									.append(
											"VALUES(IDAUTO.NEXTVAL, :systemName, :ip, :port, :message, :monitorLevel, :createDate, ")
									.append(
											"  (SELECT HOSTNAME FROM IP_HOSTNAME_MAPPING_ICUSTOMER WHERE IP = :ip))")
									.toString(),
							new BeanPropertySqlParameterSource(umpMonitor));
		}
		private String getConnectionErrorMessage(String cause) {
			return String.format("%s error! cause : %s.", this.name, cause);
		}

	}



	public SimpleJdbcOperations getJdbcOperations() {
		return jdbcOperations;
	}

	public void setJdbcOperations(SimpleJdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
	}
	
	
}
