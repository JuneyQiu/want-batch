package com.want.batch.main;

import org.junit.Test;

import com.want.batch.job.utils.DBUtils;

public class RebuildIndexTest extends AbstractWantMainTest {

	@Test
	public void test() {
		DBUtils.rebuildIndexes(this.iCustomerJdbcOperations, "COMPANY_INFO_TBL_IDX02", "CHANNEL_INFO_TBL_PK");
	}
}
