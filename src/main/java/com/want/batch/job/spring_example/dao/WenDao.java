package com.want.batch.job.spring_example.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.spring_example.bo.WenSend;
import com.want.batch.job.spring_example.util.Tool;
import com.want.utils.SapDAO;

@Component
public class WenDao {
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SimpleJdbcOperations iCustomerJdbcOperations;
	
	@Autowired
	public SapDAO sapDAO;
	
	private static final String insertWenSendSql = "insert into WEN_SEND_TBL "
		+ " (SID, WEN_SID, CUSTOMER_SID, SEND_QTY, REFUSE_QTY, SEND_DATE, RU_KU_DATE, "
		+ " TEMP_MEINS, TEMP_VBELN, TEMP_SPART, TEMP_VTWEG, TEMP_VKORG) "
		+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String getWenInfoSql = "select SID, WEN_NO from  WEN_INFO_TBL order by SID";
	
	private static final String getAllWenOrderSql = "select WEN_SID, CUSTOMER_SID, SEND_QTY, SEND_DATE, RU_KU_DATE from WEN_SEND_TBL";
	
	private static final String getMaxSql = "select max(sid) as maxId from WEN_SEND_TBL";
	
	private static final String getCustomerSql = "SELECT SID FROM CUSTOMER_INFO_TBL WHERE ID = ?";
	
	/**
	 * 批量add  文宣品 send  ===== for: 文宣品
	 */
	public void addWenSend(List<Object[]> objList) {
		
		// mandy modify 2013-07-01
		this.iCustomerJdbcOperations.batchUpdate(insertWenSendSql, objList);
	}
	
	/**
	 * 取得文宣品MAP (key:3099080156M0, value:sid)  ===== for: 文宣品
	 * @throws SQLException 
	 */
	public Map<String, String> getWenInfoMap() {
		
		List<Map<String, Object>> wenInfoList = this.iCustomerJdbcOperations.queryForList(getWenInfoSql);
		
		Map<String, String> map = new HashMap<String, String>();
		
		if(null != wenInfoList && wenInfoList.size() > 0) {
			
			for(Map<String, Object>  wenMap: wenInfoList){
				
				map.put((String)wenMap.get("WEN_NO"), String.valueOf(wenMap.get("SID")));
			}
		}
		
		return map;
	}
	
	/**
	 * get All Wen Order
	 * @throws SQLException 
	 */
	public List<String> getAllWenOrder() {
		
		List<Map<String, Object>> wenOrderList = this.iCustomerJdbcOperations.queryForList(getAllWenOrderSql);
		
		List<String> strList = new ArrayList<String>();
		
		for(Map<String, Object> wenOrderMap : wenOrderList) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(wenOrderMap.get("WEN_SID"));
			sb.append("_");
			sb.append(wenOrderMap.get("CUSTOMER_SID"));
			sb.append("_");
			sb.append(Tool.objectToString(Tool.math45(String.valueOf(wenOrderMap.get("SEND_QTY")))));
			sb.append("_");
			sb.append(wenOrderMap.get("SEND_DATE"));
			sb.append("_");
			sb.append(wenOrderMap.get("RU_KU_DATE"));
			
			strList.add(sb.toString());
		}
		
		return strList;
	}
	
	/**
	 * 从SAP抓取: 文宣品订单  ===== for: 文宣品
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public List<WenSend> getWenOrderFromSAP(String date8, Map<String, String> wenInfoMap) {
		HashMap queryMap = new HashMap();
		queryMap.put("ZVKORG", "C101");
		queryMap.put("ZVKORG1", "C551");
		queryMap.put("ZERDST", date8);
		queryMap.put("ZERDAT", date8);
		queryMap.put("ZKUNNR",  "0011000000");
		queryMap.put("ZKUNNR1", "0011999999");
		
		List<Map<String, String>> list = sapDAO.getSAPData("ZRFC15", "ZST015", queryMap);
    	
    	int length = (list == null || list.isEmpty()) ? 0 : list.size();
		
		if(length == 0) {
			return null;
		}
		
		List<WenSend> list2 = new ArrayList<WenSend>();
        
        for (int i = 0; i < length; i++) {
            Map<String, String> tempMap = (Map<String, String>) list.get(i);
            
            String wenNo = Tool.objectToString(tempMap.get("MATNR")); //物料号
            
            if(wenNo.startsWith("0000003099")) {
            	wenNo = wenNo.substring(6, wenNo.length()); //3099080156M0
            }
            
            String wenSid = wenInfoMap.get(wenNo);
            
            if(wenNo.indexOf("99040177E") >= 0) {
            	wenSid = "34"; //99040177A, 99040177E
            }
            
            if(wenSid != null) { //只记录县城的27个文宣品的订单信息
            	String customerNo = Tool.objectToString(tempMap.get("KUNNR")); //客户编号
//            	int customerSid = Tool.getCustomerSidById(customerNo);
            	int customerSid = getCustomerSid(Tool.changeCustomerIdTo10(customerNo));
            	
            	WenSend bo = new WenSend();
            	
            	bo.setWenSid(Tool.myParseInt(wenSid));
            	bo.setCustomerSid(customerSid);
            	bo.setSendQty(Tool.StringToBigDecimal(Tool.objectToString(tempMap.get("LFIMG"))));
            	bo.setRefuseQty(Tool.StringToBigDecimal(Tool.objectToString(tempMap.get("LFIMG1"))));
            	bo.setSendDate(Tool.objectToString(tempMap.get("WADAT_IST")));
            	bo.setRuKuDate(Tool.objectToString(tempMap.get("BUDAT")));
            	
            	bo.setTempMEINS(Tool.objectToString(tempMap.get("MEINS")));
            	bo.setTempVBELN(Tool.objectToString(tempMap.get("VBELN")));
            	bo.setTempSPART(Tool.objectToString(tempMap.get("SPART")));
            	bo.setTempVTWEG(Tool.objectToString(tempMap.get("VTWEG")));
            	bo.setTempVKORG(Tool.objectToString(tempMap.get("VKORG")));
            	
                list2.add(bo);
            }
        }
        
        return list2;
    }
	
	/**
	 * 取得主键的最大值。  ===== for: 文宣品
	 * @throws SQLException 
	 */
	public int getMaxID() {
		
		return this.iCustomerJdbcOperations.queryForInt(getMaxSql);
	}
	
	public Integer getCustomerSid(String customerId) {
		
		return this.iCustomerJdbcOperations.queryForInt(getCustomerSql, customerId);
	}
}
