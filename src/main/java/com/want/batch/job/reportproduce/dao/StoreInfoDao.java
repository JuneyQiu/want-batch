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
public class StoreInfoDao {

	@Autowired
	public SimpleJdbcOperations hw09JdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-18 Deli
	 * 	查询拜访顺序,终端负责人,电话和地址
	 * </pre>
	 * 
	 * @param storeId
	 *          终端编号
	 * @param subroutSid
	 *          业代线路
	 * @return
	 */
	public Map<String, Object> getStoreInfoByStoreId(String storeId, String subroutSid) {

		StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
			.append("   FORWARDER_INFO_TBL.FORWARDER_ID, ")
			.append(" 	SUBROUTE_STORE_TBL.VISIT_ORDER, ")
			.append(" 	d.STORE_OWNER, ")
			.append(" 	d.STORE_ZIP1 || '-' || d.STORE_PHONE1 AS PHONE1, ")
			.append(" 	d.STORE_ZIP2 || '-' || d.STORE_PHONE2 AS PHONE2, ")
			.append(" 	d.STORE_MOBILE1, ")
			.append(" 	d.STORE_MOBILE2, ")
			.append(" 	DECODE (THIRD_LV_INFO_TBL.THIRD_LV_NAME,'null',' ',NVL(THIRD_LV_INFO_TBL.THIRD_LV_NAME,' ')) || ")
//			.append(" 	CASE WHEN d.add1 IS NULL THEN ' ' ELSE DECODE(d.ADD1_TYPE_SID, '1', '县 ','2', '市 ', '3', '旗 ', '') END || ")
			.append(" 	DECODE (THIRD_LV_INFO_TBL.THIRD_LV_NAME,'null',' ',NVL(FORTH_LV_INFO_TBL.FORTH_LV_NAME,' ') ) || ")
//			.append(" 	CASE WHEN d.add2 IS NULL THEN ' ' ELSE DECODE (d.ADD2_TYPE_SID, '4', '乡 ', '5', '镇 ', '12','区 ','') END || ")
			.append(" 	DECODE (THIRD_LV_INFO_TBL.THIRD_LV_NAME, 'null',' ',NVL(d.add3,' ')) || ")
			.append(" 	CASE WHEN d.add3 IS NULL THEN ' ' ELSE DECODE (d.ADD3_TYPE_SID, '6', '村 ', '') END || ")
			.append("	DECODE (THIRD_LV_INFO_TBL.THIRD_LV_NAME, 'null',' ',NVL(d.add4,' ')) || ")
			.append(" 	CASE WHEN d.add4 IS NULL THEN ' ' ELSE DECODE (d.ADD4_TYPE_SID, '7', '路 ', '8', '街 ', '') END || ")
			.append(" 	DECODE (THIRD_LV_INFO_TBL.THIRD_LV_NAME,'null',' ',NVL(d.add5,' ')) || ")
			.append(" 	CASE WHEN d.add5 IS NULL THEN ' ' ELSE DECODE (d.ADD5_TYPE_SID, '9', '号 ', '10', '弄 ','11', '巷','') END || ")
			.append(" 	DECODE (THIRD_LV_INFO_TBL.THIRD_LV_NAME, 'null' ,' ',NVL(d.add6,' ')) AS ADDRESS, ")
			.append(" 	FORTH_LV_INFO_TBL.FORTH_LV_NAME, ")
			.append(" 	THIRD_LV_INFO_TBL.THIRD_LV_NAME, ") // Deli 2010-07-29 add
			.append("   STORE_TYPE_TBL.STORE_TYPE_ID || STORE_TYPE_TBL.STORE_SUBTYPE_ID AS STORE_TYPE ") // 2011-5-18 Deli add 
			.append(" FROM ")
			.append(" 	STORE_INFO_TBL d ")
			.append("  		INNER JOIN SUBROUTE_STORE_TBL ")
			.append("  		ON SUBROUTE_STORE_TBL.STORE_SID = d.SID ")
			.append(" 		 INNER JOIN SUBROUTE_INFO_TBL ")
			.append(" 		 ON SUBROUTE_INFO_TBL.SID = SUBROUTE_STORE_TBL.SUBROUTE_SID ")
			.append("      	INNER JOIN ROUTE_INFO_TBL ON ROUTE_INFO_TBL.SID = SUBROUTE_INFO_TBL.ROUTE_SID ")
			.append("       	INNER JOIN FORWARDER_INFO_TBL ON FORWARDER_INFO_TBL.SID = ROUTE_INFO_TBL.FORWARDER_SID")
			.append("      			INNER JOIN FORTH_LV_INFO_TBL ON d.FORTH_SID = FORTH_LV_INFO_TBL.SID ")
			
			// Deli 2010-07-29 add
			.append(" 						INNER JOIN THIRD_LV_INFO_TBL ")
			.append(" 						ON THIRD_LV_INFO_TBL.SID = FORTH_LV_INFO_TBL.THIRD_LV_SID ")
			// add end
			
			// 2011-5-18 Deli add
			.append(" 						INNER JOIN STORE_TYPE_TBL ")
			.append(" 						ON d.STORE_TYPE_SID = STORE_TYPE_TBL.SID ")
			// add end
			
			.append(" WHERE ")
			.append(" 	d.STORE_ID = ? AND ")
			.append(" 	SUBROUTE_INFO_TBL.SID = ? ");

		List<Map<String, Object>> storeInfos = hw09JdbcOperations.queryForList(sql.toString(),
			new Object[] { storeId, subroutSid });

		return storeInfos.isEmpty() ? null : storeInfos.get(0);
	}
}
