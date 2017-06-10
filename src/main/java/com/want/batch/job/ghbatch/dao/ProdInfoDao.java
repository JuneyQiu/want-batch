/**
 * 
 */
package com.want.batch.job.ghbatch.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.ghbatch.pojo.ProdInfo;

/**
 * @author MandyZhang
 *
 */
@Component
public class ProdInfoDao {

	@Autowired
	public DataSource iCustomerDataSource;
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	// 根据 prod_id 清单，查询所有的产品
	public List<ProdInfo> findByProdIds(Set<String> prodIds) throws SQLException {

		List<ProdInfo> lstProdInfo = new ArrayList<ProdInfo>();
		
		String sql = String.format(
				"SELECT * FROM PROD_INFO_TBL WHERE PROD_ID IN ('%s')",
				StringUtils.join(prodIds, "','"));

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			conn = iCustomerDataSource.getConnection();
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				ProdInfo prodInfo = new ProdInfo();
				prodInfo.setProdId(rs.getString("PROD_ID"));
				prodInfo.setName1(rs.getString("NAME1"));
				prodInfo.setName2(rs.getString("NAME2"));
				prodInfo.setSpecTaste(rs.getString("SPEC_TASTE"));
				prodInfo.setMateriatType(rs.getString("MATERIAL_TYPE"));
				prodInfo.setBaseUnit(rs.getString("BASE_UNIT"));
				prodInfo.setGroupTypeId(rs.getString("GROUP_TYPE_ID"));
				prodInfo.setLv5Id(rs.getString("LV_5_ID"));
				
				lstProdInfo.add(prodInfo);
			}
		} catch (SQLException e) {
			
			logger.error("ProdInfoDao: findByProdIds---------------------" + e.getLocalizedMessage());
			throw e;
		}
		finally {
			
			if (rs != null) {
				
				rs.close();
			}
			
			if (pstmt != null) {
				
				pstmt.close();
			}
			
			if (conn != null) {
				
				conn.close();
			}
		}
		
		return lstProdInfo;
	}
}
