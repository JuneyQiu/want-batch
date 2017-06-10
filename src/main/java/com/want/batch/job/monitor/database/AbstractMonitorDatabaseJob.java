package com.want.batch.job.monitor.database;

import java.util.List;
import java.util.Map;

import com.want.batch.job.AbstractWantJob;

public abstract class AbstractMonitorDatabaseJob extends AbstractWantJob {

	private ProdDatabaseInfos prodDatabaseInfos;

	/**
	 * 当没有设定schemas时，抓取非系统schema的所有schema
	 */
	@Override
	public void init() {
		List<String> schemas = this.getProdDatabaseInfos().getSchemas();
		if (schemas.isEmpty()) {
			List<Map<String, Object>> schemaInfos = 
				this.getProdDatabaseInfos().getJdbcOperations().queryForList(
					new StringBuilder()
						.append("SELECT DISTINCT OWNER ")
						.append("FROM DBA_TABLES ")
						.toString());
			
			for (Map<String, Object> schemaInfoMap : schemaInfos) {
				schemas.add(schemaInfoMap.get("OWNER").toString());
			}
		}
		schemas.removeAll(this.getProdDatabaseInfos().getSysSchemas());
	}
	
	protected ProdDatabaseInfos getProdDatabaseInfos() {
		return prodDatabaseInfos;
	}

	public void setProdDatabaseInfos(ProdDatabaseInfos prodDatabaseInfos) {
		this.prodDatabaseInfos = prodDatabaseInfos;
	}

}
