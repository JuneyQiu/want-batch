package com.want.batch.job;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.ExecuteJob;
import com.want.batch.WantBatchException;
import com.want.component.mail.MailService;
import com.want.data.pojo.BatchStatus;
import com.want.service.SmsSendService;

public abstract class AbstractWantJob implements WantJob {

	private static final String FUNC_STATUS_FAILED = "0";
	private static final String FUNC_STATUS_SUCCESSED = "1";
	private static final String FUNC_STATUS_RUNNING = "2";
	
	private static List<String> EXCLUSIVE_LIST = new ArrayList<String>();

	private static List<String> EXCLUSIVE_BEFORE_LIST = new ArrayList<String>();
	
	static {
		ResourceBundle bundle = ResourceBundle.getBundle("project");
		StringTokenizer jobs = new StringTokenizer(
				bundle.getString("project.batch.monitor.exclude.before"), ",");
		while (jobs.hasMoreElements())
			EXCLUSIVE_BEFORE_LIST.add(jobs.nextToken());
		jobs = new StringTokenizer(
				bundle.getString("project.batch.monitor.exclude"), ",");
		while (jobs.hasMoreElements())
			EXCLUSIVE_LIST.add(jobs.nextToken());
	}

	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired(required = false)
	@Qualifier("iCustomerJdbcOperations")
	private SimpleJdbcOperations iCustomerJdbcOperations;

	@Autowired(required = false)
	@Qualifier("hw09JdbcOperations")
	private SimpleJdbcOperations hw09JdbcOperations;

	@Autowired(required = false)
	@Qualifier("portalJdbcOperations")
	private SimpleJdbcOperations portalJdbcOperations;
	
	@Autowired(required = false)
	@Qualifier("sfa2JdbcOperations")
	private SimpleJdbcOperations sfa2JdbcOperations;

	@Autowired(required = false)
	@Qualifier("wantcompOperations")
	private SimpleJdbcOperations wantcompOperations;
	
	@Autowired(required = false)
	@Qualifier("batchOperations")
	private SimpleJdbcOperations batchOperations;

	@Autowired(required = false)
	@Qualifier("loggerJdbcOperations")
	private SimpleJdbcOperations loggerJdbcOperations;

	@Autowired(required = false)
	@Qualifier("fchJdbcOperations")
	private SimpleJdbcOperations fchJdbcOperations;

	@Autowired(required = false)
	@Qualifier("dataMartJdbcOperations")
	private SimpleJdbcOperations dataMartJdbcOperations;

	@Autowired(required = false)
	@Qualifier("archiveJdbcOperations")
	private SimpleJdbcOperations archiveJdbcOperations;

	@Autowired(required = false)
	@Qualifier("iCustomerDataSource")
	private DataSource iCustomerDataSource;

	@Autowired(required = false)
	@Qualifier("historyDataSource")
	private DataSource historyDataSource;

	@Autowired(required = false)
	@Qualifier("portalDataSource")
	private DataSource portalDataSource;
	
	@Autowired(required = false)
	@Qualifier("sfa2DataSource")
	private DataSource sfa2DataSource;
	
	@Autowired(required = false)
	@Qualifier("wantcompDataSource")
	private DataSource wantcompDataSource;
	
	@Autowired
	private MailService mailService;

	@Autowired
	private SmsSendService smsSendService;

	private String batchStatusFuncId = StringUtils.uncapitalize(this.getClass()
			.getSimpleName());

	protected BatchStatus batchStatus = null;

	private static final String SELECT_SQL = "SELECT SID FROM BATCH.BATCH_STATUS WHERE SID = :sid";

	private static final String UPDATE_SQL = "UPDATE BATCH.BATCH_STATUS SET END_DATE = :endDate, STATUS = :status, REASON = :reason WHERE SID = :sid";
	
	private static final String INSERT_SQL = "INSERT INTO BATCH.BATCH_STATUS(SID, FUNC_ID, START_DATE, END_DATE, STATUS, REASON) "
			+ "VALUES(:sid, :funcId, :startDate, :endDate, :status, :reason)";
	
	public void executeBatch() {
		
		try {
			batchStatus = getBatchStatus(batchStatusFuncId);
		} catch (Exception e) {
			logger.error("dataBase error>>>>",e);
		}

		try {
			if (!isExclusiveBefore(batchStatusFuncId) && !isExclusive(batchStatusFuncId))
				try {		
					batchOperations.update(INSERT_SQL, new BeanPropertySqlParameterSource(batchStatus));
				} catch (Exception e) {
					logger.error("dataBase error>>>>",e);
				}
				
			
			gc();
			logger.info("*** Job started: " + batchStatus.getFuncId() + ", " + getMemoryInfo());
			this.execute();
			batchStatus.setStatus(FUNC_STATUS_SUCCESSED);
			logger.info("*** Job success: " + batchStatus);
		} catch (Exception e) {
			logger.error("batch execute error >>> ", e);
			batchStatus.setStatus(FUNC_STATUS_FAILED);
			batchStatus.setReason(e);
			logger.info("*** Job failed: " + batchStatus);
		} finally {
			batchStatus.setEndDate(new Date());
			if (!isExclusive(batchStatusFuncId) && !isExclusive(batchStatusFuncId)) {
				List rs = batchOperations.queryForList(SELECT_SQL, new BeanPropertySqlParameterSource(batchStatus));
				if (rs != null && rs.size() > 0)
					batchOperations.update(UPDATE_SQL, new BeanPropertySqlParameterSource(batchStatus));
				else
					batchOperations.update(INSERT_SQL, new BeanPropertySqlParameterSource(batchStatus));
			}
			gc();
			logger.info(getConnectionUsageInfo() + "\n\n");
		}
	}

	private BatchStatus getBatchStatus(String funcId) {
		BatchStatus bs = new BatchStatus();
		bs.setFuncId(funcId);
		bs.setStartDate(new Date());
		bs.setEndDate(null);
		bs.setStatus(FUNC_STATUS_RUNNING);
		bs.setReason("");
		Long nl = batchOperations.queryForObject("select BATCH.BATCH_STATUS_SEQ.NEXTVAL from dual", Long.class);
		bs.setSid(nl);
		return bs;
	}

	private boolean isExclusive(String f) {
		boolean result = false;
		for (String i: EXCLUSIVE_LIST) {
			if (i.equals(f)) {
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean isExclusiveBefore(String f) {
		boolean result = false;
		for (String i: EXCLUSIVE_BEFORE_LIST) {
			if (i.equals(f)) {
				result = true;
				break;
			}
		}
		return result;
	}


	/**
	 * 需要在bean初始化后预先做的事情，可重写实现这个方法
	 * <p>
	 * 需要通过xml配置bean
	 */
	public void init() {

	}

	/**
	 * 需要在bean完成任务后需要做的事情，可重写实现这个方法
	 * <p>
	 * 需要通过xml配置bean
	 */
	public void destory() {

	}

	/**
	 * @Title getHistoryConnection
	 * @Description 获取连接 history 的 connection
	 * @return history Connection
	 * @throws WantBatchException
	 *             当获取失败时抛出此异常
	 */
	protected final Connection getHistoryConnection() {
		try {
			return this.historyDataSource.getConnection();
		} catch (Exception e) {
			throw new WantBatchException(
					"throw exception when get history connection.", e);
		}
	}

	/**
	 * @Title getICustomerConnection
	 * @Description 获取连接 icustomer 的 connection
	 * @return iCustomer Connection
	 * @throws WantBatchException
	 *             当获取失败时抛出此异常
	 */
	protected final Connection getICustomerConnection() {
		try {
			return this.iCustomerDataSource.getConnection();
		} catch (Exception e) {
			throw new WantBatchException(
					"throw exception when get icustomer connection.", e);
		}
	}

	/**
	 * @Title getICustomerConnection
	 * @Description 获取连接 portal 的 connection
	 * @return iCustomer Connection
	 * @throws WantBatchException
	 *             当获取失败时抛出此异常
	 */
	protected final Connection getPortalConnection() {
		try {
			return this.portalDataSource.getConnection();
		} catch (Exception e) {
			throw new WantBatchException(
					"throw exception when get icustomer connection.", e);
		}
	}

	/**
	 * @Title: close
	 * @Description: close {@link Connection}
	 * @param 要关闭的
	 *            {@link Connection} 实例
	 */
	protected final void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("throw exception when close Connection.", e);
			}
		}
	}

	/**
	 * @Title: close
	 * @Description: close {@link ResultSet}
	 * @param 要关闭的
	 *            {@link ResultSet} 实例
	 */
	protected final void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				logger.error("throw exception when close ResultSet.", e);
			}
		}
	}

	/**
	 * @Title: close
	 * @Description: close {@link Statement}
	 * @param 要关闭的
	 *            {@link Connection} 实例
	 */
	protected final void close(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (Exception e) {
				logger.error("throw exception when close Statement.", e);
			}
		}
	}

	/**
	 * @Title: close
	 * @Description: close {@link ResultSet}, {@link Statement},
	 *               {@link Connection}
	 * @param 要关闭的
	 *            {@link ResultSet} 实例
	 * @param 要关闭的
	 *            {@link Statement} 实例
	 * @param 要关闭的
	 *            {@link Connection} 实例
	 */
	protected final void close(ResultSet rs, Statement statement,
			Connection conn) {
		this.close(rs);
		this.close(statement);
		this.close(conn);
	}

	/**
	 * @Title close
	 * @Description close {@link Statement}, {@link Connection}
	 * @param 要关闭的
	 *            {@link Statement} 实例
	 * @param 要关闭的
	 *            {@link Connection} 实例
	 */
	protected final void close(Statement statement, Connection conn) {
		this.close(statement);
		this.close(conn);
	}

	protected final SimpleJdbcOperations getiCustomerJdbcOperations() {
		return iCustomerJdbcOperations;
	}

	protected final SimpleJdbcOperations getHw09JdbcOperations() {
		return hw09JdbcOperations;
	}

	protected final SimpleJdbcOperations getPortalJdbcOperations() {
		return portalJdbcOperations;
	}

	protected final SimpleJdbcOperations getLoggerJdbcOperations() {
		return loggerJdbcOperations;
	}

	protected final SimpleJdbcOperations getFchJdbcOperations() {
		return fchJdbcOperations;
	}

	protected SimpleJdbcOperations getDataMartJdbcOperations() {
		return dataMartJdbcOperations;
	}

	public SimpleJdbcOperations getArchiveJdbcOperations() {
		return archiveJdbcOperations;
	}

	protected MailService getMailService() {
		return mailService;
	}

	protected SmsSendService getSmsSendService() {
		return smsSendService;
	}

	protected String getBatchStatusFuncId() {
		return batchStatusFuncId;
	}

	public void setBatchStatusFuncId(String batchStatusFuncId) {
		this.batchStatusFuncId = batchStatusFuncId;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public void setDataMartJdbcOperations(
			SimpleJdbcOperations dataMartJdbcOperations) {
		this.dataMartJdbcOperations = dataMartJdbcOperations;
	}

	public void setiCustomerJdbcOperations(
			SimpleJdbcOperations iCustomerJdbcOperations) {
		this.iCustomerJdbcOperations = iCustomerJdbcOperations;
	}

	public DataSource getSfa2DataSource() {
		return sfa2DataSource;
	}

	public void setSfa2DataSource(DataSource sfa2DataSource) {
		this.sfa2DataSource = sfa2DataSource;
	}

	public DataSource getWantcompDataSource() {
		return wantcompDataSource;
	}

	public void setWantcompDataSource(DataSource wantcompDataSource) {
		this.wantcompDataSource = wantcompDataSource;
	}

	public SimpleJdbcOperations getSfa2JdbcOperations() {
		return sfa2JdbcOperations;
	}

	public void setSfa2JdbcOperations(SimpleJdbcOperations sfa2JdbcOperations) {
		this.sfa2JdbcOperations = sfa2JdbcOperations;
	}

	public SimpleJdbcOperations getWantcompOperations() {
		return wantcompOperations;
	}

	public void setWantcompOperations(SimpleJdbcOperations wantcompOperations) {
		this.wantcompOperations = wantcompOperations;
	}
	
	public SimpleJdbcOperations getBatchOperations() {
		return batchOperations;
	}

	public void setBatchOperations(SimpleJdbcOperations batchOperations) {
		this.batchOperations = batchOperations;
	}

	public String getMemoryInfo() {
		return "max memory " + Runtime.getRuntime().maxMemory()
			+ ", free memory " + Runtime.getRuntime().freeMemory();
	}
	
	public void gc() {
		Runtime.getRuntime().gc();
	}
	
	private String getConnectionUsageInfo() {
		StringBuffer out = new StringBuffer();
		
		try {
			if (ExecuteJob.applicationContext != null) {
				String[] beans = ExecuteJob.applicationContext.getBeanNamesForType(BasicDataSource.class);
				for (String b : beans) {
					BasicDataSource ds = (BasicDataSource) ExecuteJob.applicationContext.getBean(b);
					out.append(b + ": actives " + ds.getNumActive() + ",  idles "
								+ ds.getNumIdle() + ", max actives "
								+ ds.getMaxActive() + "\n");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return out.toString();
	}

}
