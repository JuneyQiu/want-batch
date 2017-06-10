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
public class EmpInfoDao {

	@Autowired
	public SimpleJdbcOperations hw09JdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-18 Deli
	 * 	业代线别查询
	 * </pre>
	 * 
	 * @param empId 业代工号
	 * @return
	 */
	public Map<String, Object> getSaleTpyeName(String empId) {

		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	sale.SALE_TYPE_NAME ")
			.append(" FROM ")
			.append(" 	EMP_INFO_TBL info ")
			.append(" 		INNER JOIN EMP_SALE_TYPE sale ")
			.append(" 		ON info.EMP_TYPE_SID = sale.SID ")
			.append(" WHERE ")
			.append(" 	info.STATUS = '1' AND ")
			//.append(" 	sale.STATUS = '1' AND ") Ryan 2010/05/26 经与客户确认后，去掉此限制条件。
			.append(" 	info.EMP_ID = :empId ");
		List<Map<String, Object>> saleNames = hw09JdbcOperations.queryForList(sql.toString(), empId);
		
		return saleNames.isEmpty() ? null : saleNames.get(0);
	}
}
