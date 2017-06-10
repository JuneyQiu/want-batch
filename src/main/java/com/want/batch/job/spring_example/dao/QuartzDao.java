package com.want.batch.job.spring_example.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.spring_example.bo.KnvvBO;
import com.want.batch.job.spring_example.util.Tool;
import com.want.utils.SapDAO;

@Component
public class QuartzDao {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public static final String SYSTEM_PARAMETER_STATUS_RUNNING = "y";
	public static final String SYSTEM_PARAMETER_STATUS_STOPPING = "n";
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	@Autowired
	public SapDAO sapDAO;
	
	private static final String updateQuartzStatusSql = "update SYSTEM_PARAMETERS_TBL set STATUS=? where sid=?";
	
	private static final String getQuartzStatusSql = "select STATUS from SYSTEM_PARAMETERS_TBL where sid=?";
	
	private static final String updateStatusSql = "update SYSTEM_PARAMETERS_TBL set STATUS=? where STATUS=?";
	
	private static final String removeTableSql = "delete from CUSTOMER_SEND_PLACE_TBL";
	
	private static final String insertSql = "insert into CUSTOMER_SEND_PLACE_TBL "
		+" (SID, CUSTOMER_ID, COMPANY_ID, SALES_CHANNEL, PRODUCT, ID_FRIEND, ADDRESS,STR_SUPPL2,LOCATION) "
		+" values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	/**
	 * 根据主键，修改某个参数的状态
	 * @throws SQLException 
	 */
	public void updateQuartzStatus(int id, String newStatus) {
		
		this.iCustomerJdbcOperations.update(updateQuartzStatusSql, newStatus, id);
	}
	
	/**
	 * 根据主键，取某个参数当前的状态
	 * @throws SQLException 
	 */
	public String getQuartzStatus(int id) {
		
		String status = "";
		
		List<Map<String, Object>> statusList = this.iCustomerJdbcOperations.queryForList(getQuartzStatusSql, id);
		
		if(null != statusList && statusList.size() > 0) {
			
			status = (String) statusList.get(0).get("STATUS");
		}
		
		return status;
	}
	
	/**
	 * 修改状态
	 * @throws SQLException 
	 */
	public void updateQuartzStatusByBatch() {
		
		this.iCustomerJdbcOperations.update(updateStatusSql, 
				QuartzDao.SYSTEM_PARAMETER_STATUS_STOPPING, QuartzDao.SYSTEM_PARAMETER_STATUS_RUNNING);
	}
	
	/**
	 * 删除一张表的全部记录
	 * @throws SQLException 
	 */
	public void removeTable() {
		
		this.iCustomerJdbcOperations.update(removeTableSql);
	}
	
	/**
	 * 从SAP抓取 送达方 信息
	 */
	@SuppressWarnings("unchecked")
	public List<KnvvBO> getSendPlaceFromSAP() {
		
		List list = sapDAO.getSAPData3("ZRFC16", "ZST016");
		
    	int length = (list == null || list.isEmpty()) ? 0 : list.size();
    	
        logger.debug("=================================");
        logger.debug("sap ... list size = " + length);
    	
		if(length == 0) {
			return null;
		}
		
		List<KnvvBO> list2 = new ArrayList<KnvvBO>();
        
        for (int i = 0; i < length; i++) {
            Map tempMap = (Map) list.get(i);
            KnvvBO cbo = new KnvvBO();
            
            //cbo.setId(***);
            cbo.setCustomerId(Tool.objectToString(tempMap.get("KUNNR"))); //客户编号1
            cbo.setCompanyId(Tool.objectToString(tempMap.get("VKORG"))); //销售组织
            cbo.setSalesChannel(Tool.objectToString(tempMap.get("VTWEG"))); //分销渠道
            cbo.setProduct(Tool.objectToString(tempMap.get("SPART"))); //产品组
            cbo.setIdFriend(Tool.objectToString(tempMap.get("KUNN2"))); //业务伙伴的客户号
            cbo.setAddress(Tool.objectToString(tempMap.get("STRAS"))); //住宅号及街道
            //莎莎需求，CRQ000000019248新增经销商门市与办公地址信息，2014/12/15
            cbo.setSTR_SUPPL2(Tool.objectToString(tempMap.get("STR_SUPPL2"))); //办公地址
            cbo.setLOCATION(Tool.objectToString(tempMap.get("LOCATION"))); //门市地址
            
            list2.add(cbo);
        }
        
        logger.debug("sap ... list2 size = " + list2.size());
        logger.debug("=================================");
        
        return list2;
    }
	
	/**
	 * 批量insert  送达方 信息
	 */
	public int addSendPlaceByBatch(List<Object[]> objList) {
		
        return this.iCustomerJdbcOperations.batchUpdate(insertSql, objList).length;
	}
}
