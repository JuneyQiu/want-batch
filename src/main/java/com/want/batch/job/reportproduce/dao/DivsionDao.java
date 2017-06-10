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
public class DivsionDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	public String findById(Integer id) {
		
		String sql = "SELECT NAME FROM DIVSION WHERE SID = " + id;
		
		List<Map<String, Object>> result = iCustomerJdbcOperations.getJdbcOperations().queryForList(sql);
		
		return null != result && result.size() > 0 ? (String) result.get(0).get("NAME") : "";
	}
}
