package com.want.batch.job.business.promotional;

import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component("promotionalClass1Job")
public class Class1Job extends AbstractWantJob {

	@Override
	public void execute() {
		logger.debug(this.getiCustomerJdbcOperations().queryForInt("select 123 from dual"));
	}

	
}
