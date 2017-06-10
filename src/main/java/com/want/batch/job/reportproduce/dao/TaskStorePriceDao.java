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
public class TaskStorePriceDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-18 Deli
	 * 	稽核终端零售价清单
	 * </pre>
	 * 
	 * @param taskStoreListId
	 * @return
	 */
	public List<Map<String, Object>> getTaskStoreList(String taskStoreListId) {

		StringBuilder sql = new StringBuilder()
				.append("	SELECT ")
				.append("		TASK_STORE_LIST.STORE_ID,")
				.append("		TASK_STORE_PRICE.SID, ")
				.append("		TASK_STORE_PRICE.LV_5_ID PROD_ID, ")
				.append("		PROD_GROUP_TBL.PROD_NAME AS PROD_NAME, ")
				.append("		TASK_STORE_PRICE.UNIT_PRICE, ")
				.append("		TASK_STORE_PRICE.PRICE_NAME, ")
				.append("		TASK_STORE_PRICE.PRICE, ")
				.append("		TASK_STORE_PRICE.JH_DISPLAY_NAME, ")
				
				// 2011-03-02 Deli add
				.append("		TASK_STORE_PRICE.PRICE_CONVERT_RATE, ")
				
				// Lucien 2010-06-02 Modify:新增查询栏位
				.append("		TASK_STORE_PRICE.NO_PRICETAG, ")
				
				.append("		TASK_STORE_LIST.STIME, ")
				.append("		TASK_STORE_LIST.ETIME, ")
				.append("		TASK_STORE_LIST.IS_FIND, ")
				.append("		TASK_STORE_LIST.REASON_LINE, ")
				
				// Lucien 2010-06-02 Modify:新增查询栏位
				.append("		TASK_STORE_LIST.STORE_ABNORMAL_STATE, ")
				.append("		TASK_STORE_LIST.SPEC_ARR, ")
				.append("		TASK_STORE_LIST.SPEC_ARR_REA, ")
				.append("		TASK_STORE_LIST.IS_PASTDUE, ")
				.append("		TASK_STORE_LIST.IS_PASTDUE_REA, ")
				
				// 2010-08-10 Deli add 查询栏位
				.append("   TASK_STORE_PRICE.IS_ENTERSTORE, ")
				.append("   TASK_STORE_PRICE.DISPLAY_ACTUAL ")

				.append("	FROM ")
				.append("		TASK_STORE_LIST ")
				.append("	INNER JOIN TASK_STORE_PRICE ")
				.append("		ON TASK_STORE_PRICE.TASK_STORE_LIST_SID=TASK_STORE_LIST.SID ")
				.append("	INNER JOIN PROD_GROUP_TBL  ")
				.append("		ON PROD_GROUP_TBL.PROD_ID=TASK_STORE_PRICE.LV_5_ID ")
				.append("		AND PROD_GROUP_TBL.PROD_LEVEL = 5")
				.append("	WHERE ")
				.append("		TASK_STORE_LIST.SID = :taskStoreListId ")
				.append("	ORDER BY ")
				.append("		TASK_STORE_PRICE.LV_5_ID ");
		
		return iCustomerJdbcOperations.queryForList(sql.toString(), taskStoreListId);
	}
}
