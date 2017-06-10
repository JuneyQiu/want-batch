/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.pojo.TaskStoreSpecialExhibit;

/**
 * @author MandyZhang
 *
 */
@Component
public class TaskStoreSpecialExhibitDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-17 Lucien
	 * 	根据终端清单表SID查询行程类别-终端特陈
	 * </pre>
	 * 
	 * @param taskStoreListSid 行程类别-终端清单表SID
	 * @return
	 */
	public List<TaskStoreSpecialExhibit> findTaskStoreSpecialExhibit(int taskStoreListSid) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT ");
		builder.append(" task.SID AS sid,");
		builder.append(" task.TASK_STORE_LIST_SID as taskStoreListSid,");
		builder.append(" task.DISPLAY_TYPE_NAME as displayTypeName,");
		builder.append(" task.CUSTOMER_ID as customerId,");
		builder.append(" task.CUSTOMER_NAME as customerName,");
		builder.append(" task.SPECIAL_IS_OK as specialIsOk, ");
		builder.append(" task.LOCATION_TYPE_NAME as locationTypeName,");
		builder.append(" displaya.DISPLAY_TYPE_NAME as displayTypeTblNamea, ");
		builder.append(" displayb.DISPLAY_TYPE_NAME as displayTypeTblNameb,");
		builder.append(" task.DISPLAY_ACREAGE as displayAcreage,");
		builder.append(" task.DISPLAY_SIDECOUNT as displaySideCount,");
		builder.append(" task.CREATE_DATE as createDate, ");
		builder.append(" task.CREATE_USER as createUser,");
		builder.append(" task.DISPLAY_ACREAGE_RESULT as displayAcreageResult,");
		builder.append(" task.DISPLAY_TYPE_NAME_RESULT as displayTypeNameResult,");
		builder.append(" task.LOCATION_TYPE_NAME_RESULT as locationTypeNameResult, ");
		builder.append(" task.DISPLAY_SIDECOUNT_RESULT as displaySideCountResult, ");
		builder.append(" task.UPDATE_USER as updateUser, ");
		builder.append(" task.UPDATE_DATE as updateDate,  ");
		builder.append(" task.DIVSION_SID as divsionSid, ");
		
		// 2010-11-16 Deli add
		builder.append(" task.DISPLAY_PARAM_ID as diplayParamId, ");
		builder.append(" task.DISPLAY_PARAM_ID_RESULT as diplayParamIdResult, ");
		
		// 2010-11-30 Deli ad
		builder.append(" task.STORE_DISPLAY_SID as storeDisplaySid, ");
		
		// 2012-12-07 John Add
		builder.append(" SPECIAL_DISPLAY_APPLICATION.SD_NO as sdNo ");
		
		builder.append(" FROM ");
		builder.append(" TASK_STORE_SPECIAL_EXHIBIT task");
		builder.append(" LEFT JOIN DISPLAY_TYPE_TBL displaya ON task.DISPLAY_TYPE_NAME = displaya.SID ");
		builder.append(" LEFT JOIN DISPLAY_TYPE_TBL displayb ON task.DISPLAY_TYPE_NAME_RESULT = displayb.SID ");
		
		// 2012-12-07 John Add
		builder.append(" INNER JOIN APPLICATION_STORE_DISPLAY ON APPLICATION_STORE_DISPLAY.SID = TASK.STORE_DISPLAY_SID ");
		builder.append(" INNER JOIN APPLICATION_STORE ON APPLICATION_STORE.SID = APPLICATION_STORE_DISPLAY.APPLICATION_STORE_SID ");
		builder.append(" INNER JOIN SPECIAL_DISPLAY_APPLICATION ON SPECIAL_DISPLAY_APPLICATION.SID = APPLICATION_STORE.APPLICATION_SID ");
		
		builder.append(" WHERE ");
		builder.append(" TASK_STORE_LIST_SID='"+ taskStoreListSid + "'");
		
		List<Map<String, Object>> results = iCustomerJdbcOperations.queryForList(builder.toString());

		List<TaskStoreSpecialExhibit> taskDetailList = new ArrayList<TaskStoreSpecialExhibit>();
		
		for (Map<String, Object> rs : results) {
			
			TaskStoreSpecialExhibit taskStoreSpecialExhibit = new TaskStoreSpecialExhibit();

			if (rs.get("sid") != null) {
				
				taskStoreSpecialExhibit.setId(Integer.parseInt(rs.get("sid").toString()));
			}
				
			if (rs.get("taskStoreListSid") != null) {
				
				taskStoreSpecialExhibit.setTaskStoreListSid(Integer.parseInt(rs.get("taskStoreListSid").toString()));
			}
			
			taskStoreSpecialExhibit.setDisplayTypeName(rs.get("displayTypeName") == null ? "" : rs.get("displayTypeName").toString());
			taskStoreSpecialExhibit.setCustomerId(rs.get("customerId") == null ? "" : rs.get("customerId").toString());
			taskStoreSpecialExhibit.setCustomerName(rs.get("customerName") == null ? "" : rs.get("customerName").toString());
			taskStoreSpecialExhibit.setSpecialIsOk(rs.get("specialIsOk") == null ? "" : rs.get("specialIsOk").toString());
			taskStoreSpecialExhibit.setLocationTypeName(rs.get("locationTypeName") == null ? "" : rs.get("locationTypeName").toString());
			taskStoreSpecialExhibit.setDisplayTypeTblNamea(rs.get("displayTypeTblNamea") == null ? "" : rs.get("displayTypeTblNamea").toString());
			taskStoreSpecialExhibit.setDisplayTypeTblNameb(rs.get("displayTypeTblNameb") == null ? "" : rs.get("displayTypeTblNameb").toString());
			
			if (rs.get("displayAcreage") != null) {
				
				taskStoreSpecialExhibit.setDisplayAcreage(Integer.parseInt(rs.get("displayAcreage").toString()));
			}
			
			taskStoreSpecialExhibit.setDisplaySideCount(rs.get("displaySideCount") == null ? "" : rs.get("displaySideCount").toString());
			
			if (rs.get("createUser") != null) {
				
				taskStoreSpecialExhibit.setCreateUser(rs.get("createUser").toString());
			}
			
			if (rs.get("createDate") != null) {
				
				taskStoreSpecialExhibit.setCreateDate((Date)rs.get("createDate"));
			}
			
			if (rs.get("displayAcreageResult") != null) {
				
				taskStoreSpecialExhibit.setDisplayAcreageResult(new BigDecimal(rs.get("displayAcreageResult").toString()));
			}
			
			if (rs.get("displayTypeNameResult") != null) {
				
				taskStoreSpecialExhibit.setDisplayTypeNameResult(rs.get("displayTypeNameResult").toString());
			}
			
			if (rs.get("locationTypeNameResult") != null) {
				
				taskStoreSpecialExhibit.setLocationTypeNameResult(rs.get("locationTypeNameResult").toString());
			}
			
			if (rs.get("updateDate") != null) {
				
				taskStoreSpecialExhibit.setUpdateDate((Date)rs.get("updateDate"));
			}
			
			taskStoreSpecialExhibit.setDisplaySideCountResult(rs.get("displaySideCountResult") == null ? "" : rs.get("displaySideCountResult").toString());
			
			if (rs.get("updateUser") != null) {
				
				taskStoreSpecialExhibit.setUpdateUser(rs.get("updateUser").toString());
			}
			
			if (rs.get("divsionSid") != null) {
				
				taskStoreSpecialExhibit.setDivsionSid(Integer.parseInt(rs.get("divsionSid").toString()));
			}
			
			// 2010-11-16 Deli add
			if (rs.get("diplayParamId") != null) {
				
				taskStoreSpecialExhibit.setDisplayParamId(rs.get("diplayParamId").toString());
			}
			
			if (rs.get("diplayParamIdResult") != null) {
				
				taskStoreSpecialExhibit.setDisplayParamIdResult(rs.get("diplayParamIdResult").toString());
			}
			
			// 2010-11-30 Deli add
			if (rs.get("storeDisplaySid") != null) {
				
				taskStoreSpecialExhibit.setStoreDisplaySid(Integer.parseInt(rs.get("storeDisplaySid").toString()));
			}
			
			// 2012-12-07 John add
			if (rs.get("sdNo") != null) {
				
				taskStoreSpecialExhibit.setSdNo(rs.get("sdNo").toString());
			}
			
			taskDetailList.add(taskStoreSpecialExhibit);
		}

		return taskDetailList;
	}
}
