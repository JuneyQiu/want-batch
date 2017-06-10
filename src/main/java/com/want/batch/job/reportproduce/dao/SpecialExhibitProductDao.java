/**
 * 
 */
package com.want.batch.job.reportproduce.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.pojo.SpecialExhibitProduct;

/**
 * @author MandyZhang
 *
 */
@Component
public class SpecialExhibitProductDao {

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	/**
	 * <pre>
	 * 2010-3-17 Lucien
	 * 	根据特陈编号SID查询特陈与品项清单关联表
	 * </pre>
	 * 
	 * @param specialExhibitSid 特陈编号SID
	 * @return
	 */
	public List<SpecialExhibitProduct> findSpecialExhibitProduct(int specialExhibitSid) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT ");
		builder.append(" SID,TASK_STORE_SPECIAL_EXHIBIT_SID,PRODUCT_ID,PRODUCT_NAME,IS_HAVE_SPECIAL_EXHIBIT,DATE_ROUTE");
		builder.append(" FROM ");
		builder.append(" SPECIALEXHIBIT_PRODUCT ");
		builder.append(" WHERE ");
		builder.append(" TASK_STORE_SPECIAL_EXHIBIT_SID="+ specialExhibitSid + "");
		
		List<Map<String, Object>> results = iCustomerJdbcOperations.queryForList(builder.toString());
		
		List<SpecialExhibitProduct> taskDetailList = new ArrayList<SpecialExhibitProduct>();
		
		for (Map<String, Object> rs : results) {
			
			SpecialExhibitProduct specialExhibitProduct = new SpecialExhibitProduct();
			specialExhibitProduct.setId(null != rs.get("SID") ? Integer.parseInt(rs.get("SID").toString()) : null);
			specialExhibitProduct.setTaskStoreSpecialExhibitSid(null != rs.get("TASK_STORE_SPECIAL_EXHIBIT_SID") ? Integer.parseInt(rs.get("TASK_STORE_SPECIAL_EXHIBIT_SID").toString()) : null);
			specialExhibitProduct.setProductId(rs.get("PRODUCT_ID") == "" ? "" : rs.get("PRODUCT_ID").toString());
			specialExhibitProduct.setProductName(rs.get("PRODUCT_NAME") == "" ? "" : rs.get("PRODUCT_NAME").toString());
			specialExhibitProduct.setIsHaveSpecialExhibit(rs.get("IS_HAVE_SPECIAL_EXHIBIT") == null ? "" : rs.get("IS_HAVE_SPECIAL_EXHIBIT").toString());
			specialExhibitProduct.setDateRoute(rs.get("DATE_ROUTE") == null ? "" : rs.get("DATE_ROUTE").toString());
			
			taskDetailList.add(specialExhibitProduct);
		}
		
		return taskDetailList;
	}
}
