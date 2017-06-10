package com.want.batch.job.business.promotional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.utils.SapDAO;

/**
 * @author 00078460 从sap同步促销品开单信息数据
 *
 */
@Component("promotionalFromSapToDBJob")
public class PromotionalFromSapToDBJob extends AbstractWantJob {

	@Autowired
	public SapDAO sapDAO;

	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	public void execute() {
		try {
			/** 第一次跑4个月的，以后只跑当天 **/
			HashMap<String,String> querydata = new HashMap<String,String>();
			String function_name = "ZRFC15";
			String result = "ZST015";
			querydata.put("ZVKORG", "C101");
		    querydata.put("ZVKORG1", "C999");
		    querydata.put("ZKUNNR", "0011000001");
		    querydata.put("ZKUNNR1", "0011999999");
			
			DateTime currentDate = new DateTime();
			int syncDays = this.syncDays();
			
			for (int i = 0; i < syncDays; i++) {
				querydata.put("ZERDST", currentDate.minusDays(i).toString("yyyyMMdd"));
				querydata.put("ZERDAT", currentDate.minusDays(i).toString("yyyyMMdd"));
				syncData(querydata, function_name, result);
			}
			/*
			  	促销品管理同步SAP部分的资料，经销商网站需要的资料为当前月的资料+当前月-3个月的1日起的资料。
       			例如：
              	2011年12月20日 ，DB中需要的资料为：2011年09月01日到2011年12月20日的资料。
              	2012年01月01日 ，DB中需要的资料为：2011年10月01日到2012年01月01日的资料。
				DB只需要维护4个自然月的资料，在同步当前资料的同时，都需要删除4个自然月以外的资料。
			 */
			String year = String.valueOf(currentDate.minusMonths(3).getYear());
			String month = String.valueOf(currentDate.minusMonths(3).getMonthOfYear());
			month = month.length() == 1 ? "0"+month : month;
			deletePro(year+month+"01");
		} catch (Exception e) {
			logger.error("数据库异常！", e);
			throw new WantBatchException(e);
		}
	}

	private void syncData(HashMap<String, String> querydata,
			String function_name, String result) throws ParseException {
		ArrayList<HashMap<String,String>> promotionalList = sapDAO.getSAPData2(function_name, result, querydata);
		logger.info("ZRFC15 datas >>> " + promotionalList.size());
		int i = 0;
		for (HashMap<String,String> datahm : promotionalList) {
			savePro(datahm);
			
			if (i % 10 == 0) {
				logger.info("Insert ITEM_RECORD >>> " + i);
			}
			++i;
		}
	}
	/**
	 * 判断是否第一次
	 */
	public int syncDays(){
		if (this.getiCustomerJdbcOperations().queryForInt("select count(1) from ITEM_RECORD") > 0) {
			return 1;
		}
		else {
			return 4 * 31;//第一次同步4个月
		}
	}
	/**
	 * 删除4个月以外的信息
	 */
	public void deletePro(String bill_date)throws DataAccessException, ParseException{
		String delete = " delete from ITEM_RECORD where BILL_DATE < TO_DATE('"+bill_date+"','yyyymmdd') ";
		this.getiCustomerJdbcOperations().update(delete, new Object[]{});
	}
	
	/**
	 * 保存促销品开单信息 
	 * 20130606 Nash add 如果查询到的事业部为空不进行新增
	 */
	public void savePro(HashMap<String,String> data) throws DataAccessException, ParseException{
		String insert = "insert into ITEM_RECORD(SID,PROMOTIONAL_ID,CUSTOMER_ID,DIVISION_SID,INSTORE_DATE,OUTSTORE_DATE,OUTSTORE_QUANTITY,BILL_DATE) values" +
        		"(ITEM_RECORD_SEQ.nextval,?,?,?,?,?,?,?)";
		
		Integer division = getDivsionSid(data.get("VTWEG").toString(), data.get("SPART").toString());
		
		if(null != division){
			
			this.getiCustomerJdbcOperations().update(insert, new Object[]{
				data.get("MATNR").toString(),
				data.get("KUNNR").toString(),
				division,
				new Timestamp(format.parse(data.get("BUDAT").toString()).getTime()),
				new Timestamp(format.parse(data.get("WADAT_IST").toString()).getTime()),
				new BigDecimal(data.get("LFIMG").toString()),
				new Timestamp(format.parse(data.get("ERDAT").toString()).getTime())
			});
		}
	}

	/**
	 * 根据channel_id与prod_group_id查出divsion
	 * 20130606 Nash modify 防止查询出的事业部为null,
	 * 原来的查询方法getiCustomerJdbcOperations().queryForInt()
	 * 如果结果是null时回报异常
	 */
	public Integer getDivsionSid(String channel_id,String prod_group_id){
		
		String sql = "select DIVSION_SID from SALES_AREA_REL where CHANNEL_ID = ? and PROD_GROUP_ID = ?";
		
		Integer returnValue;
		
		List<Map<String,Object>> listMap = getiCustomerJdbcOperations().queryForList(sql,channel_id,prod_group_id);
		
		// 判断查出的集合是否为空
		if(null != listMap && listMap.size() > 0){
			
			//如果事业部为空返回null 不为空事业部toString()
			returnValue = listMap.get(0).get("DIVSION_SID") == null ? null :Integer.valueOf(listMap.get(0).get("DIVSION_SID").toString());
		}else{
			
			returnValue = null;
		}
		
		return returnValue;
	}
}
