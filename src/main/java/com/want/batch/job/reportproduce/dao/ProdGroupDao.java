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
public class ProdGroupDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * 根据ID 获取Name
	 * 
	 * @return String
	 */
	public String fingProdGroupById(String prodId) {
		
		String sql = "SELECT PROD_NAME FROM PROD_GROUP_TBL WHERE PROD_ID = ?";
		
		List<Map<String, Object>> result = iCustomerJdbcOperations.queryForList(sql, prodId);
		
		String prodName = "";
		
		if(result!=null && result.size() > 0) {
			
			prodName = (String)result.get(0).get("PROD_NAME");
		}
		
		return prodName;
	}
}
