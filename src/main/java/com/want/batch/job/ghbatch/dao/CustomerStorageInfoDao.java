/**
 * 
 */
package com.want.batch.job.ghbatch.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author MandyZhang
 *
 */
@Component
public class CustomerStorageInfoDao {
	
	@Autowired
	public DataSource iCustomerDataSource;
	
	protected final Log logger = LogFactory.getLog(this.getClass());

	// 获取某客户某年月日库存记录的 mapping
	public List<Map<String, Object>> findProdIdsQtyMap(	String customerId,
			String yearMonth,
			String day,
			String divisionSid) throws Exception {
		
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		
		StringBuilder sql = new StringBuilder()
			.append("SELECT  ")
			.append("   CSI.CREATOR, ")
			.append("   CSI.CREATE_DATE, ")
			.append("   SA.DIVSION_SID, ")
			.append("		CSI.PROD_ID,")
			.append("		SUM( ")
			.append("			CSI.S_QTY_1 + ")
			.append("			CSI.S_QTY_2 + ")
			.append("			CSI.S_QTY_3 + ")
			.append("			CSI.S_QTY_4 + ")
			.append("			CSI.S_QTY_5 + ")
			.append("			CSI.S_QTY_6 + ")
			.append("			CSI.S_QTY_7 + ")
			.append("			CSI.S_QTY_8 + ")
			.append("			CSI.S_QTY_9 + ")
			.append("			CSI.S_QTY_10 + ")
			.append("			CSI.S_QTY_11 + ")
			.append("			CSI.S_QTY_12) AS TOTAL_QTY ")
			.append("FROM CUSTOMER_STORAGE_INFO_TBL CSI ")
			.append("  INNER JOIN SALES_AREA_REL SA ON SA.CREDIT_ID = CSI.CREDIT_ID ")
			.append("WHERE CSI.CUSTOMER_ID = ? ")
			.append("  AND CSI.YEARMONTH = ? ")
			.append("  AND CSI.DAY = ? ")

			// 2010-06-23 Deli modify 添加SA.DIVSION_SID为1的查询条件
			// 2010-10-11 Deli modify 添加SA.DIVSION_SID为13(搭配送)的查询条件
			// 2011-5-27 Deli modify 添加30(县城休闲)和31(县城乳饮)
			.append("  AND SA.DIVSION_SID IN (" + divisionSid + ") ")
			.append("GROUP BY ")
			.append("   CSI.CREATOR, ")
			.append("   CSI.CREATE_DATE, ")
			.append("   SA.DIVSION_SID, ")
			.append("   CSI.PROD_ID ");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, customerId);
			pstmt.setString(2, yearMonth);
			pstmt.setString(3, day);
			
			rs = pstmt.executeQuery();
			
			// 2015-08-27 mirabelle update 方法返回list，不返回map，不再分用户和品项分开
			Map<String, Object> creatorInfo = new HashMap<String, Object>();
			
			while(rs.next()) {
				Map<String, Object> prodIdQty = new HashMap<String, Object>();
				String prodId = rs.getString("PROD_ID");

				prodIdQty.put("prodId", prodId);
				prodIdQty.put("totalQty", new BigDecimal(rs.getString("TOTAL_QTY")));
				prodIdQty.put("creator", rs.getString("CREATOR"));
				prodIdQty.put("createDate",rs.getTimestamp("CREATE_DATE"));
				prodIdQty.put("divsionSid", rs.getString("DIVSION_SID"));
				returnList.add(prodIdQty);
//				creatorInfo.put("creator" + prodId, (rs.getString("CREATOR")));
//				creatorInfo.put("createDate" + prodId, rs.getTimestamp("CREATE_DATE"));// 2013-07-16 mirabelle update 修改create_date的取值方式
//				creatorInfo.put("divsionSid" + prodId, (rs.getString("DIVSION_SID")));
				
			}
			
//			if ((!prodIdQty.isEmpty()) && (!creatorInfo.isEmpty())) {
//				
//				returnList.add(prodIdQty);
//				returnList.add(creatorInfo);
//			}
			
		} catch (Exception e) {
			
			logger.error("CustomerStorageInfoDao: findProdIdsQtyMap-----------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			if(null != rs) {
				
				rs.close();
			}
			
			if (pstmt != null) {
				
				pstmt.close();
			}
			
			if (conn != null) {
			
				conn.close();
			}
		}
		
		return returnList;
	}
}
