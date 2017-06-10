package com.want.batch.job.spring_example.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

@Component
public class AppleDao {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	private static final String addByBatchSql = "insert into XXX_APPLE_TEST_TBL values(XXX_APPLE_TEST_TBL_SEQ.nextval, ?)";
	
	/**
	 * 批量操作，增加多条apple信息。
	 * 用sequence来生成主键的情况。
	 */
	public int addByBatch(List<Object[]> list) {
		
		// mandy modify 2013-07-01
		return iCustomerJdbcOperations.batchUpdate(addByBatchSql, list).length;
	}
}
