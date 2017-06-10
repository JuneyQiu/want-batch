/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class SubrouteInfoDao {

	@Autowired
	public SimpleJdbcOperations hw09JdbcOperations;
	
	/**
	 * <pre>
	 * 2010-6-4 Deli
	 * 	根据线路sid查询查询projectSid
	 * </pre>
	 * 
	 * @param subrouteSid 线路sid
	 * @return
	 */
	public Integer getProjectSidByRouteSid(Integer subrouteSid) {
		
		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append(" 	ROUTE_INFO_TBL.PROJECT_SID ")
			.append(" FROM ")
			.append(" 	SUBROUTE_INFO_TBL ")
			.append(" 		INNER JOIN ROUTE_INFO_TBL ")
			.append(" 		ON SUBROUTE_INFO_TBL.ROUTE_SID = ROUTE_INFO_TBL.SID ")
			.append(" WHERE ")
			.append(" 	SUBROUTE_INFO_TBL.SID = :subrouteSid ");
		
		return hw09JdbcOperations.queryForInt(sql.toString(), subrouteSid);
	}
}
