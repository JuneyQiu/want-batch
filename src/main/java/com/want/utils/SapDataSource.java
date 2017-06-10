package com.want.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sap.mw.jco.JCO;

public class SapDataSource {
	
	private static final Log logger = LogFactory.getLog(SapDataSource.class);
	
	private static final JCO.PoolManager POOL_MANAGER = JCO
			.getClientPoolManager();
	private String poolName;
	private int maxPoolCount;
	private String clientIdentification;
	private String clientUser;
	private String clientPassword;
	private String clientHost;
	private String clientLang;
	private String clientSystemNumber;

	public synchronized JCO.Client createConnection() throws SapComponentException {
		JCO.Pool pool = POOL_MANAGER.getPool(this.poolName);

		if (pool == null)
			JCO.addClientPool(this.poolName, this.maxPoolCount,
					this.clientIdentification, this.clientUser,
					this.clientPassword, this.clientLang, this.clientHost,
					this.clientSystemNumber);
		
		try {
			return JCO.getClient(this.poolName);
		} catch (Exception e) {
			throw new SapComponentException(
					"throw exception when create sap client.", e);
		}
	}

	public void close(JCO.Client conn) {
		if (conn != null)
			try {
				JCO.releaseClient(conn);
			} catch (Exception e) {
				logger.error("exception when close sap client: " + e.getMessage());
			}
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public void setMaxPoolCount(int maxPoolCount) {
		this.maxPoolCount = maxPoolCount;
	}

	public void setClientIdentification(String clientIdentification) {
		this.clientIdentification = clientIdentification;
	}

	public void setClientUser(String clientUser) {
		this.clientUser = clientUser;
	}

	public void setClientPassword(String clientPassword) {
		this.clientPassword = clientPassword;
	}

	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public void setClientLang(String clientLang) {
		this.clientLang = clientLang;
	}

	public void setClientSystemNumber(String clientSystemNumber) {
		this.clientSystemNumber = clientSystemNumber;
	}
}