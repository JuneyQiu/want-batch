/**
 * 
 */
package com.want.batch.job.monitor;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.data.pojo.WatchdogServer;
import com.want.service.SmsModel;
import com.want.utils.TimeUtils;

/**
 * @author 00079241
 * 
 */

@Component
public class BpmWatchdogJob extends AbstractWantJob {

	static final int SMS_FUNCTION_SID = 7;

	
	private List<WatchdogServer> servers;
	
	private String rootUrl;
	private int retryTimes = 1;
	private int retryInterval = 5;
//	private Map<Long, String> phoneInfos = new HashMap<Long, String>();
	
	private List<String> jndis = new ArrayList<String>();

	@Autowired(required = false)
	@Qualifier("umpJdbcOperations")
	private SimpleJdbcOperations umpJdbcOperations;

	@Override
	public void execute() {
		for (WatchdogServer server : this.servers) {
			for (Map.Entry<String, String> serverInstance : server
					.getServerInstances().entrySet()) {

				Monitor monitor = new Monitor(serverInstance.getValue()
						+ rootUrl, serverInstance.getKey());

				try {
					monitor.checked(1);
					logger
							.info(String.format("check[%s:%s] ok~",
									serverInstance.getKey(), serverInstance
											.getValue()));
				} catch (Exception e) {
					logger
							.info(String.format("check[%s:%s] error~",
									serverInstance.getKey(), serverInstance
											.getValue()));
					// monitor.sendSms();

					UmpMonitor umpMonitor = new UmpMonitor();

					umpMonitor.setSystemName("bpm");
					umpMonitor.setIp(server.getIp());
					umpMonitor.setPort(server.getNamePorts().get(
							serverInstance.getKey()));

					monitor.pushUmp(umpMonitor);
				}
			}
		}
	}

	public void setServers(List<WatchdogServer> servers) {
		this.servers = servers;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

//	public void setPhoneInfos(Map<Long, String> phoneInfos) {
//		this.phoneInfos = phoneInfos;
//	}

	public void setJndis(List<String> jndis) {
		this.jndis = jndis;
	}

	private class Monitor extends Thread {

		private final Log logger = LogFactory.getLog(this.getClass());

		private boolean production;
		private String productionMatchString = "OldWantArch.ajax.post";
		private String name;
		private String url;
		private HttpURLConnection connection;
		private String errorMessage = "";
		private boolean running = true;
		private SmsModel smsModel;

		private Monitor(String url, String name) {
			this.url = url;
			this.name = name;

			this.smsModel = new SmsModel();
			this.smsModel.setFuncSid(SMS_FUNCTION_SID);
			this.smsModel.setStatus(SmsModel.SMS_STATUS);
		}

		@Override
		public void run() {
			try {
				this.checked(1);
				logger.info(String.format("%s[%s] normal ... ", this.name,
						this.url));
			} catch (Exception e) {
				logger.error(this.errorMessage, e);
			} finally {
				this.close();
			}
		}

		private void checked(int retryIndex) {
			try {
				this.openConnection(retryIndex);
				this.connect(retryIndex);
				this.checkedConectionResult(retryIndex);
			} catch (Exception e) {
				if (retryIndex < retryTimes) {
					TimeUtils.waitingSeconds(retryInterval);
					checked(++retryIndex);
				} else {
					throw new MonitorException(e);
				}
			}
		}

		private void openConnection(int retryIndex) {
			try {
				logger.debug(String.format("%sopen http connection : [%s]",
						(retryIndex > 1) ? "re-" : "", this.url));
				this.connection = (HttpURLConnection) new URL(this.url + "?"
						+ StringUtils.join(jndis, "&")).openConnection();
				logger.debug(String.format(
						"%sopen http connection[%s] successed.",
						(retryIndex > 1) ? "re-" : "", this.url));
			} catch (Exception e) {
				if (retryIndex == retryTimes) {
					this.errorMessage = this
							.getConnectionErrorMessage("open http connection error... ");
				} else {
					logger
							.debug(String
									.format(
											"%sopen http connection[%s] error, retry[%s] ... ",
											(retryIndex > 1) ? "re-" : "",
											this.url, retryIndex));
				}
				throw new MonitorException(e);
			}
		}

		private void connect(int retryIndex) {
			try {
				logger.debug(String.format("%sconnect : [%s]",
						(retryIndex > 1) ? "re-" : "", this.url));
				this.connection.connect();
				logger.debug(String.format("%sconnect[%s] successed.",
						(retryIndex > 1) ? "re-" : "", this.url));
			} catch (Exception e) {
				if (retryIndex == retryTimes) {
					this.errorMessage = this
							.getConnectionErrorMessage("connect error...");
				} else {
					logger.debug(String
							.format("%sconnect[%s] error, retry[%s] ... ",
									(retryIndex > 1) ? "re-" : "", this.url,
									retryIndex));
				}
				throw new MonitorException(e);
			}
		}

		private void checkedConectionResult(int retryIndex) {
			String message = getInputContent(retryIndex);

			if (this.production) {
				checkProductionPage(retryIndex, message);
			} else {
				checkServerInstanceInfo(retryIndex, message);
			}
		}

		private void checkServerInstanceInfo(int retryIndex, String message) {

			logger.debug(String.format("%scheck server instance[%s] info ... ",
					(retryIndex > 1) ? "re-" : "", this.name));

			try {
				
				if(message.indexOf("pass")!=-1){
					logger.debug(this.url+": "+"pass");
				
				}
				
//				JSONObject jo = new JSONObject(StringUtils.replace(message,
//						"\"", "'"));
//
//				for (String jndi : jndis) {
//					if ("pass".equals(jo.get(jndi))) {
//						logger.info(String.format(
//								"%scall %s[%s]'s jndi[%s] ok!",
//								(retryIndex > 1) ? "re-" : "", this.name,
//								this.url, jndi));
//					} else {
//						if (retryIndex == retryTimes) {
//							this.errorMessage = this.getJndiErrorMessage(jndi,
//									jo.get(jndi));
//						} else {
//							logger
//									.debug(String
//											.format(
//													"%scall %s[%s]'s jndi[%s] error! retry[%s] ... ",
//													(retryIndex > 1) ? "re-"
//															: "", this.name,
//													this.url, jndi, retryIndex));
//						}
//						throw new MonitorException(String.format(
//								"server instance jndi[%s] error...", jndi));
//					}
//				}
			} catch (Exception e) {
				if (retryIndex == retryTimes) {
					this.errorMessage = this
							.getConnectionErrorMessage("server instance error...");
				} else {
					logger
							.debug(String
									.format(
											"%scheck server instance[%s] info error! retry[%s]... ",
											(retryIndex > 1) ? "re-" : "",
											this.name, retryIndex));
				}
				throw new MonitorException(e);
			}
		}

		private void checkProductionPage(int retryIndex, String message) {
			logger.debug(String.format("%scheck production[%s] page ... ",
					(retryIndex > 1) ? "re-" : "", this.url));
			if (!StringUtils.contains(message, this.productionMatchString)) {
				if (retryIndex == retryTimes) {
					this.errorMessage = this
							.getConnectionErrorMessage("production page error...");
				} else {
					logger
							.debug(String
									.format(
											"%scheck production[%s] page error, retry[%s] ... ",
											(retryIndex > 1) ? "re-" : "",
											this.url, retryIndex));
				}
				throw new MonitorException("production page error...");
			} else {
				logger.debug(String.format("check production[%s] page ok!",
						this.url));
			}
		}

		private String getInputContent(int retryIndex) {
			try {
				logger.debug(String.format("get conn[%s] input stream ... ",
						this.url));
				return IOUtils.toString(this.connection.getInputStream());
			} catch (Exception e) {
				if (retryIndex == retryTimes) {
					this.errorMessage = this
							.getConnectionErrorMessage("get input stream error...");
				}
				throw new MonitorException(e);
			}
		}

		void close() {

			logger.debug("close monitor ... ");

			try {
				this.connection.disconnect();
				logger.debug("connection closed!");
			} catch (Exception e) {
				logger.debug(String
						.format("close monitor error ... ", this.url), e);
			}

			// 最终一定要验证是否error，决定是否需要发送短信
			finally {
				this.running = false;
//				if (this.isError() && isWorktime()) {
//					this.sendSms();
//				}
			}

			logger.debug("monitor closed!");
		}

		private boolean isWorktime() {

			int hour = Integer.parseInt(new DateTime().toString("HH"));

			return hour < 24 && hour > 7;
		}

		@Override
		public void interrupt() {
			logger.debug(String.format("interrupt thread[%s], timeout ... "));
			this.errorMessage = this
					.getConnectionErrorMessage("connected timeout... ");
			this.close();
			super.interrupt();
		}

//		private void sendSms() {
//
//			Date currentDate = new Date();
//			this.smsModel.setCreateDate(currentDate);
//			this.smsModel.setSendDate(currentDate);
//			this.smsModel.setContent(this.errorMessage);
//
//			for (Map.Entry<Long, String> phoneInfo : phoneInfos.entrySet()) {
//				this.smsModel.setPersonalName(phoneInfo.getValue());
//				this.smsModel.setPhoneNumber(phoneInfo.getKey());
//				getSmsSendService().send(this.smsModel);
//				logger.error(this.errorMessage);
//			}
//		}

		private void pushUmp(UmpMonitor umpMonitor) {

			umpMonitor.setMonitorLevel(UmpMonitorLevel.L5);
			umpMonitor.setMessage(this.errorMessage);
			umpMonitor.setCreateDate(new Date());

			umpJdbcOperations
					.update(
							new StringBuilder()
									.append(
											"INSERT INTO TIVOLI_MONITOR(ID, SYSTEM_NAME, IP, PORT, MESSAGE, MONITOR_LEVEL, CREATE_DATE, HOSTNAME) ")
									.append(
											"VALUES(TIVOLI_MONITOR_SEQ.NEXTVAL, :systemName, :ip, :port, :message, :monitorLevel, :createDate, ")
									.append(
											"  (SELECT HOSTNAME FROM IP_HOSTNAME_MAPPING WHERE IP = :ip))")
									.toString(),
							new BeanPropertySqlParameterSource(umpMonitor));
		}

		boolean isError() {
			return StringUtils.isNotBlank(this.errorMessage);
		}

		@SuppressWarnings("unused")
		boolean isRunning() {
			return this.running;
		}

		private String getConnectionErrorMessage(String cause) {
			return String.format("%s error! cause : %s.", this.name, cause);
		}

		private String getJndiErrorMessage(String jndiName, Object cause) {
			return String.format("%s's jndi[%s] maybe error! cause : %s.",
					this.name, jndiName, cause);
		}
	}

}
