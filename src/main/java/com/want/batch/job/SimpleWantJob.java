/**
 * 
 */
package com.want.batch.job;

import org.springframework.stereotype.Component;

@Component
public class SimpleWantJob extends AbstractWantJob {

	/* (non-Javadoc)
	 * @see com.want.batch.job.WantJob#execute()
	 */
	@Override
	public void execute() {
		logger.debug(this.getiCustomerJdbcOperations().queryForLong(
			String.format("SELECT %s FROM DUAL", System.currentTimeMillis())));
	}

}
