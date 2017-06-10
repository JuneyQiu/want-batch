/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class SpecialSubstandardDao {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * <pre>
	 * 2010-11-8 Deli
	 * 	根据特陈形式和特陈形式属性编号，查询特陈形式属性的名称
	 * </pre>
	 * 
	 * @param displayType
	 * @param paramId
	 * @return
	 */
	public String getDisplayPramName(String displayType, String paramId) {

		String sql = " SELECT PARAMETER_NAME name FROM PARAMETERS_TBL WHERE DISPLAY_TYPE_SID = ? AND PARAMETER_ID = ? ";

		logger.debug("********** query PARAMETER_NAME sql>>" + sql);
		logger.debug("********** displayType>>" + displayType);
		logger.debug("********** paramId>>" + paramId);
	
		List<Map<String, Object>> paramNames = iCustomerJdbcOperations.queryForList(sql, displayType, paramId);
		
		return ((paramNames != null) && (paramNames.size() > 0)) ? paramNames.get(0).get("name").toString() : null;
	}
	
	/**
	 * <pre>
	 * 2010-11-8 Deli
	 * 	根据特陈Sid查询不达标名称
	 * </pre>
	 * 
	 * @param specialId
	 * @return
	 */
	public List<Map<String, Object>> getSustandardsBySpecialId(Integer specialId) {

		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append("   SPECIAL_SUBSTANDARD.SID sid, ")
			.append("   SUBSTANDARD_TBL.SID id, ")
			.append("   SUBSTANDARD_TBL.SUBSTANDARD_NAME name ")
			.append(" FROM ")
			.append("   SPECIAL_SUBSTANDARD ")
			.append("     INNER JOIN SUBSTANDARD_TBL ") 
			.append("     ON SPECIAL_SUBSTANDARD.SUBSTANDARD_ID = SUBSTANDARD_TBL.SID ") 
			.append(" WHERE ")
			.append("   SPECIAL_SUBSTANDARD.STORE_SPECIAL_SID = ? ");
		
		logger.debug("********** query SUBSTANDARD_TBL.SID,SUBSTANDARD_TBL.SUBSTANDARD_NAME sql>>" + sql);
		logger.debug("********** specialId>>" + specialId);
			
		return iCustomerJdbcOperations.queryForList(sql.toString(), specialId);
	}
}
