
// ~ Package Declaration
// ==================================================

package com.want.batch.job.reportproduce.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;


// ~ Comments
// ==================================================

/**
 * 排程产生报表属性设置表Dao.
 * 
 * <table>
 * 	<tr>
 * 		<th>日期</th>
 * 		<th>变更说明</th>
 * 	</tr>
 * 	<tr>
 * 		<td>2013-7-4</td>
 * 		<td>Mirabelle新建</td>
 * 	</tr>
 * </table>
 *
 *@author Mirabelle
 */
@Component
public class BatchReportAttributeTblDao {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	private static final String getAttributeSql = "SELECT ATTRIBUTE_VAL FROM BATCH_REPORT_ATTRIBUTE_TBL WHERE JOB_NAME = ? AND ATTRIBUTE_NAME = ?"; 
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2013-7-4 Mirabelle
	 * 根据功能编号和属性id取得属性的设定值.
	 * </pre>	
	 * 
	 * @param funcId
	 * @param attributeId
	 * @return
	 */
	public String getAttributeValByFuncIdAndAttrId(String funcId, String attributeId) {

		String attributeVal = "";
		List<Map<String, Object>> attributeList = this.iCustomerJdbcOperations.queryForList(getAttributeSql, funcId, attributeId);
		
		if (attributeList != null && attributeList.size() != 0) {
			
			attributeVal = (String)attributeList.get(0).get("ATTRIBUTE_VAL");
		}
		
		return attributeVal;
	}
}
