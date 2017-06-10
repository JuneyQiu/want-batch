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
public class DisplayBadDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * <pre>
	 * 2013-4-1 MandyZhang
	 * 	根据特陈SID查询陈列差
	 * </pre>
	 * 
	 * @param specialId
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getDisplayBadBySpecialId(Integer specialId) {

		StringBuilder strSql = new StringBuilder()
			.append(" SELECT DISTINCT ")
			.append(" DISPLAY_BAD_TBL.SID, ")
			.append(" DISPLAY_BAD_TBL.DISPLAY_BAD_NAME, ")
			.append(" SPECIAL_DISPLAYBAD_REL.STORE_SPECIAL_SID, ")
			.append(" SPECIAL_DISPLAYBAD_REL.SID ID ")
			.append(" FROM ")
			.append(" SPECIAL_DISPLAYBAD_REL ")
			.append(" INNER JOIN DISPLAY_BAD_TBL ")
			.append(" ON SPECIAL_DISPLAYBAD_REL.DISPLAY_BAD_SID=DISPLAY_BAD_TBL.SID ")
			.append(" WHERE ")
			.append(" SPECIAL_DISPLAYBAD_REL.STORE_SPECIAL_SID =:specialId");
		
		return iCustomerJdbcOperations.queryForList(strSql.toString(), specialId);
	}
}
