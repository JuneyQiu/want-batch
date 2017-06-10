/**
 * 
 */
package com.want.batch.job.storeprodseries.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class SchedulerExcutionflagDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * 根据Function Job获取该排程是否执行状态
	 * 
	 * @param func_id
	 * @return String
	 */
	public Map<String, Object> getFlagByFunctionId(String func_id) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		String sql = "SELECT EXCUTE_FLAG,PARAM FROM SCHEDULER_EXCUTIONFLAG_TBL WHERE FUNC_ID = ?";
		
		List<Map<String, Object>> lstResult = this.iCustomerJdbcOperations.queryForList(sql, func_id);
		
		if(null != lstResult) {
			
			map = lstResult.get(0);
		}
		
		return map;
	}
	
	/**
	 * 根据Function Job修改排程状态为"N"
	 * 
	 * @param func_id
	 */
	public void updateByFunctionId(String func_id) {
		
		String sql = "UPDATE SCHEDULER_EXCUTIONFLAG_TBL SET EXCUTE_FLAG = 'N' WHERE FUNC_ID = ?";
		
		this.iCustomerJdbcOperations.update(sql, func_id);
	}
}
