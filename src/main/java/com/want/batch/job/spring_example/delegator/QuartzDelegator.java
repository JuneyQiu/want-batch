package com.want.batch.job.spring_example.delegator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.spring_example.bo.KnvvBO;
import com.want.batch.job.spring_example.dao.QuartzDao;
import com.want.batch.job.spring_example.util.Tool;

@Component
public class QuartzDelegator extends AbstractWantJob {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	private static final int UPDATE_WEN_ORDER_ID = 7;
	private static final int UPDATE_SEND_PLACE_ID = 8;
	
	@Autowired
	private QuartzDao quartzDao;
	
	@Autowired
	private WenDelegator wenDelegator;
	
	@Autowired
	private AppleDelegator appleDelegator;

	@Override
	public void execute() throws Exception {
			
		updateSendPlace();
		updateWenOrder();
		updateQuartzStatusByBatch();
		logger.info("同步成功");
	}

	/**
	 * 从SAP同步县城文宣品发货信息。
	 * 运行时间：凌晨 03:00:07
	 * @throws SQLException 
	 */
	public void updateWenOrder() {

		// 判断ID是7的状态，如果状态是‘n’，执行操作，如果是‘y’则return
		if (isRunning(UPDATE_WEN_ORDER_ID)) {
			return;
		}

		// 修改状态为‘y’
		setRunning(UPDATE_WEN_ORDER_ID);

		//========= start ============
		String today = String.valueOf(Tool.getTodayToNumber());
		String startDay = today;
		
		for(int i = 0; i < 15; i++) {
			startDay = Tool.moveDate(startDay, -1);
		}
		
		//同步从前15天到今天的数据（如果数据在DB中已经存在，不会覆盖，而是直接忽略这种重复的数据）
		this.wenDelegator.insertWenOrder(startDay, today);
		//========== end =============
		
		this.addLogToApple("updateWenOrder");
	}
	
	/**
	 * updateQuartzStatusByBatch。
	 * 运行时间： 10:00:07
	 * @throws SQLException 
	 */
	public void updateQuartzStatusByBatch() throws SQLException {
		
		// 把所有状态是‘y’的修改成‘n’
		quartzDao.updateQuartzStatusByBatch();
		
		this.addLogToApple("updateQuartzStatusByBatch");
	}
	
	/**
	 * 批量更新 CUSTOMER_BRANCH_TBL 的数据。
	 * @throws SQLException 
	 */
	public void updateSendPlace() {
		//========= start ============
		
		// 如果状态是‘Y’则return，如果是‘N’则执行
		if (isRunning(UPDATE_SEND_PLACE_ID)) {
			return;
		}

		setRunning(UPDATE_SEND_PLACE_ID);

		quartzDao.removeTable();
		
		List<KnvvBO> list = quartzDao.getSendPlaceFromSAP();
		int length = (list == null || list.isEmpty()) ? 0 : list.size();
		logger.debug("length = " + length);
		
		if(length == 0) {
			return;
		}
		
		int sid = 0;
		
		for(KnvvBO bo: list) {
			sid++;
			bo.setSid(sid);
		}
		
		int pageNum = Tool.BATCH_UPDATE_DB_NUM;
		
		int totalPage = (length + pageNum - 1) / pageNum; //分几次提交数据库（类似于分页）
		
		for(int i = 1; i <= totalPage; i++) {
			int start = (i - 1) * pageNum + 1; //开始位置
			int end = i * pageNum; //结束位置
			end = (end > length) ? length : end;
			
			logger.debug(start + "   " + end);
			
			// mandy modify 2013-07-02
			List<Object[]> objList = new ArrayList<Object[]>();
			
			for(KnvvBO bo: list) {
				
				int idYouBiao = bo.getSid(); //类似于游标
				
				if(idYouBiao >= start && idYouBiao <= end) {
					
					Object[] object = new Object[9];
					object[0] = bo.getSid();
					object[1] = bo.getCustomerId();
					object[2] = bo.getCompanyId();
					object[3] = bo.getSalesChannel();
					object[4] = bo.getProduct();
					object[5] = bo.getIdFriend();
					object[6] = bo.getAddress();
					object[7] = bo.getSTR_SUPPL2();
					object[8] = bo.getLOCATION();
					
					objList.add(object);
				}
			}
			
			quartzDao.addSendPlaceByBatch(objList);
		}
		
		//========== end =============
		
		this.addLogToApple("updateSendPlace");
	}
	
	/**
	 * 用于验证当前的排成是否已经开始执行. 
	 * 
	 * @param id
	 * @return boolean
	 * @throws SQLException 
	 */
	private boolean isRunning(int id) {

		return QuartzDao.SYSTEM_PARAMETER_STATUS_RUNNING.equalsIgnoreCase(quartzDao.getQuartzStatus(id));
	}
	
	/**
	 * 
	 * 设定当前排程已经开始被执行.
	 * 
	 * @param id
	 * @throws SQLException 
	 */
	private void setRunning(int id) {
		quartzDao.updateQuartzStatus(id, QuartzDao.SYSTEM_PARAMETER_STATUS_RUNNING);
	}
	
	/**
	 * 记录定时器运行的日志：往apple表写1条数据
	 * @throws SQLException 
	 */
	public void addLogToApple(String quartzType) {
		
		String content = quartzType + "__" + Tool.getShiJian();
		
		this.appleDelegator.addByBatch(content, 1);
	}
}
