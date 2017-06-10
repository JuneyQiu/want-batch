/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

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
public class SubRouteStoreDao {

	@Autowired
	public SimpleJdbcOperations hw09JdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-26 Deli
	 * 	根据编号查询线路编号和拜访日期
	 * </pre>
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getSubRouteById(Integer id) {
		
		// 2010-05-27 Deli add SUBROUTE_ATT_SID(线路类型)栏位
		String sql = " SELECT SUBROUTE_NAME, VISIT_DATE, SUBROUTE_ATT_SID FROM SUBROUTE_INFO_TBL WHERE SID = :id ";
		
		List<Map<String, Object>> subroutes = hw09JdbcOperations.queryForList(sql, id);
				
		return subroutes.isEmpty() ? null : subroutes.get(0);
	}
}
