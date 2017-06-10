/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class TaskStoreListDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-17 Deli
	 * 	查询终端数量
	 * </pre>
	 * 
	 * @param sid 终端线路SID
	 *  @param flag "1"为查询未找到的,否则查询总的
	 * @return
	 */
	public int getStoreCount(String sid, String flag) { 
		StringBuilder sql = new StringBuilder()
			.append(" SELECT COUNT(*) FROM TASK_STORE_LIST ")
				.append(" WHERE TASK_STORE_SUBROUTE_SID = :sid ");

		// 查询未找到的
		if ("1".equals(flag)) {

			// Lucien 2010-06-04 Add: 新增判断条件,加入对[终端资料异常]栏位的判断[STORE_ABNORMAL_STATE = '1']
			sql.append(" AND IS_FIND = '0' AND STORE_ABNORMAL_STATE = '1' ");
		}

		// 查询已提交的
		else if ("2".equals(flag)) {

			sql.append(" AND IS_APPLY = '1' ");
		}

		return iCustomerJdbcOperations.queryForInt(sql.toString(), sid);
	}
	
	/**
	 * <pre>
	 * 2010-3-18 Deli
	 * 	查询终端编号,名称和找到状态
	 * </pre>
	 * 
	 * @param taskDetailId 行程编号
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getStoreInfos(String taskDetailId) {

		StringBuilder sql = new StringBuilder()
		.append("	SELECT ")
			.append(" 	store.SID, ")
			.append(" 	store.STORE_ID, ")
			.append("		store.STORE_NAME, ")
			.append("		store.IS_FIND, ")

			// 2010-08-26 Deli add
			.append("		store.IS_APPLY, ")
			.append("		store.IS_TCH_APPLY, ")
			.append("		store.IS_CANCEL, ")
			.append("		store.CANCEL_REASON, ")

			.append("		store.STORE_ABNORMAL_STATE, ")
			.append("   subroute.SUBROUTE_SID ")
			.append(" FROM")
			.append("		TASK_STORE_LIST store ")
			.append("   	INNER JOIN TASK_STORE_SUBROUTE subroute ")
			.append("   	ON store.TASK_STORE_SUBROUTE_SID = subroute.SID ")
			.append(" WHERE subroute.TASK_DETAIL_ID = :taskDetailId ");

		return iCustomerJdbcOperations.queryForList(sql.toString(), taskDetailId);
	}
	
	/**
	 * <pre>
	 * 2010-3-19 Deli
	 * 	查询特陈数量
	 * 2010-6-4 Deli modify 添加flag栏位
	 * </pre>
	 * 
	 * @param storeId 终端编号
	 * @param taskStoreListSid 行程类别-终端清单表SID
	 * @param flag 标示栏位,若为4则不根据事业部分组查询
	 * @return
	 */
	public List<Map<String, Object>> getSpecialCount(String storeId, String taskStoreListSid, String flag) {

		StringBuilder sql = new StringBuilder().append(" SELECT ");

		if (!StringUtils.equals("4", flag)) {

			sql.append(" 	TASK_STORE_SPECIAL_EXHIBIT.DIVSION_SID , ");
		}

		sql
			.append(" 	COUNT(TASK_STORE_SPECIAL_EXHIBIT.DIVSION_SID) AS COUNT ")
				.append(" FROM ")
				.append(" 	TASK_STORE_LIST  ")
				.append(" 		INNER JOIN TASK_STORE_SPECIAL_EXHIBIT ")
				.append(" 		ON TASK_STORE_LIST.SID = TASK_STORE_SPECIAL_EXHIBIT.TASK_STORE_LIST_SID ")
				.append(" WHERE ")
				.append("		TASK_STORE_LIST.STORE_ID = :storeId ")
				.append("		AND TASK_STORE_LIST.SID = :taskStoreListSid");

		if (!StringUtils.equals("4", flag)) {

			sql.append(" GROUP BY  ").append("		TASK_STORE_SPECIAL_EXHIBIT.DIVSION_SID ");
		}

		return iCustomerJdbcOperations.queryForList(sql.toString(), storeId, taskStoreListSid);
	}
}
