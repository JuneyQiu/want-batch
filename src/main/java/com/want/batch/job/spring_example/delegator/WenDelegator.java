package com.want.batch.job.spring_example.delegator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.spring_example.bo.WenSend;
import com.want.batch.job.spring_example.dao.WenDao;
import com.want.batch.job.spring_example.util.Tool;

@Component
public class WenDelegator {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private WenDao wenDao;
	
	/**
	 *  insert into 文宣品send表  ===== for: 文宣品
	 * @throws SQLException 
	 */
	public void insertWenOrder(String startDate, String endDate) {
		
		logger.debug("=================================");
		logger.debug(startDate + "  /  " + endDate);
        
		List<WenSend> insertWenSendList = new ArrayList<WenSend>(); //要增加的文宣品send list（可能已经存在于DB）
		List<WenSend> realWenSendList = new ArrayList<WenSend>(); //要增加的文宣品send list（在DB中还不存在）
		Map<String, String> wenInfoMap = wenDao.getWenInfoMap();
		List<String> existList = wenDao.getAllWenOrder(); //已经存在于DB中的文宣品send list
		
		logger.debug("existList size = " + existList.size());
		
		do {
			List<WenSend> list = wenDao.getWenOrderFromSAP(startDate, wenInfoMap);
			if(list != null && list.size() > 0) {
				insertWenSendList.addAll(list);
			}
			startDate = Tool.moveDate(startDate, 1); //日期加一，例如：20091020 to 20091021
		}
		while (Tool.myParseInt(startDate) <= Tool.myParseInt(endDate));
		
		logger.debug("all  insertWenSendList size = " + insertWenSendList.size());
		
		for(WenSend bo: insertWenSendList) {
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(String.valueOf(bo.getWenSid()));
			sb.append("_");
			sb.append(String.valueOf(bo.getCustomerSid()));
			sb.append("_");
			sb.append(Tool.objectToString(Tool.math45(bo.getSendQty())));
			sb.append("_");
			sb.append(bo.getSendDate());
			sb.append("_");
			sb.append(bo.getRuKuDate());
			
			if(! existList.contains(sb.toString())) { //在DB中不存在的话，insert it
				realWenSendList.add(bo);
			}
		}
		
		int maxId = getMaxID();
		
		List<Object[]> objList = new ArrayList<Object[]>();
		
		// mandy add 213-07-01 转换成数组list
		for(WenSend bo: realWenSendList) {
			
			maxId++;
			
			Object[] obj = new Object[12];
			obj[0] = maxId;
			obj[1] = bo.getWenSid();
			obj[2] = bo.getCustomerSid();
			obj[3] = bo.getSendQty();
			obj[4] = bo.getRefuseQty();
			obj[5] = bo.getSendDate();
			obj[6] = bo.getRuKuDate();
			obj[7] = bo.getTempMEINS();
			obj[8] = bo.getTempVBELN();
			obj[9] = bo.getTempSPART();
			obj[10] = bo.getTempVTWEG();
			obj[11] = bo.getTempVKORG();
			
			objList.add(obj);
		}
		
		logger.debug("real insertWenSendList size = " + realWenSendList.size());
		wenDao.addWenSend(objList);
		
		logger.debug("=================================");
	}
	
	/**
	 * 取得主键的最大值。  ===== for: 文宣品
	 * @throws SQLException 
	 */
	public synchronized int getMaxID() {
		return wenDao.getMaxID();
	}
}
