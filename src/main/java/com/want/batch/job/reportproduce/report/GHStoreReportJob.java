/**
 * 
 */
package com.want.batch.job.reportproduce.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import com.want.batch.job.reportproduce.dao.DirectiveTblDao;
import com.want.batch.job.reportproduce.pojo.Constant;
import com.want.batch.job.reportproduce.service.GhCommonService;
import com.want.batch.job.reportproduce.service.StoreExcelView;
import com.want.batch.job.reportproduce.util.CommonUtils;

/**
 * @author MandyZhang
 *
 */
@Component
public class GHStoreReportJob {

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public GhCommonService ghCommonService;
	
	@Autowired
	public StoreExcelView storeExcelView;
	
	@Autowired
	public DirectiveTblDao directiveTblDao;
	
	@Autowired
	public CommonUtils commonUtils;
	
	@Async
	public Future<Boolean> run(Map<String, Object> directiveTblMap)  {
		
		boolean result = true;
		
		try {
			
			String[] taskId = (null == directiveTblMap.get("SELECT_PARAM_VALUE") ? null : directiveTblMap
					.get("SELECT_PARAM_VALUE")
						.toString()
						.split(";"));
			
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_RUNNING, Integer.parseInt(directiveTblMap.get("SID").toString()), null);
			logger.info("稽核专员终端报表，更新db状态为running");
			
			logger.info("稽核专员终端报表开始执行查询。。。。" + new Date());
			
			Map<String, Object> storeSubrote = ghCommonService.getStoreSubrote(taskId[0], "1");
			
			// 查询终端
			// 2010-06-04 Deli add "projectSid"参数栏位
			List<Map<String, Object>> lstMap = ghCommonService.getStoreInfos(taskId[0],
				(null != storeSubrote.get("projectSid")) ? storeSubrote.get("projectSid").toString() : null);
			
			// 查询终端明细页面资料
			List<Map<String, Object>> lstDetailMap = new ArrayList<Map<String, Object>>();
			
			logger.info("稽核专员终端报表：循环终端，查询终端明细start" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			for (int i = 0; i < lstMap.size(); i++) {
			
				Map<String, Object> mapDetailReport = new HashMap<String, Object>();
				Map<String, Object> mapTemp = lstMap.get(i);

				// 获得当前终端的SID
				String strStoreSID = "";
				if (mapTemp.get("SID") != null) {
					strStoreSID = mapTemp.get("SID").toString();
				}

				// 加入一笔终端明细-终端资料
				mapDetailReport.put("storeInfo", mapTemp);

				// 查询稽核特陈真实性
				mapDetailReport.put("speciaExhibit", ghCommonService.getSpecialExhibit(strStoreSID));

				// 查询终端价格零售价格
				mapDetailReport.put("taskStore", ghCommonService.getTaskStoreList(strStoreSID));

				lstDetailMap.add(mapDetailReport);
			}
			logger.info("稽核专员终端报表：循环终端，查询终端明细end" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));

			List<Map<String, Object>> taskList = new ArrayList<Map<String, Object>>();
			
			// 获得任务物件
			if (StringUtils.isNotEmpty(taskId[1])) {
				
				taskList = ghCommonService.findTaskListById(Integer.parseInt(taskId[1]));
			}
			
			logger.info("稽核专员终端报表结束查询。。。。" + new Date());
			
			logger.info("稽核专员终端报表" + directiveTblMap.get("SID") + "开始时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			
			storeExcelView.buildExcelDocument(storeSubrote, lstMap, lstDetailMap, taskList, directiveTblMap);
			
			logger.info("稽核专员终端报表 " + directiveTblMap.get("SID") + "完成结束时间：" + new SimpleDateFormat("yyyyMMdd hh:mm:ss").format(new Date()));
			
			commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_FINISH, Integer.parseInt(directiveTblMap.get("SID").toString()), null);
			logger.info("稽核专员终端报表，更新db状态为finish");
			
			try {
				
				commonUtils.sendMail(directiveTblDao.getDirectiveBySid(directiveTblMap.get("SID").toString()));
			}
			catch (Exception e) {

				logger.error(Constant.generateExceptionMessage(e));
			}
		} 
		catch (Exception e) {
			
			result = false;
			
			logger.error(Constant.generateExceptionMessage(e));
			
			try {
				
				commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_EXCEPTION, Integer.parseInt(directiveTblMap.get("SID").toString()), Constant.generateExceptionMessage(e));
				logger.info("产生异常，更新db状态为exception");
			}
			catch (Exception e1) {
				
				logger.error(Constant.generateExceptionMessage(e1));
			}
		}
		finally {
			
			// 内存溢出属于error，不属于exception，所以捕获不到，所以只能放在finally里面处理
			// 如果执行完，指令状态还是running，则认定为发生内存溢出，则修改状态为exception，原因为内存溢出
			Map<String, Object> map = this.directiveTblDao.findDirectiveById(directiveTblMap.get("SID").toString());
			
			if(map.get("STATUS").equals(Constant.DIRECTIVE_STATUS_RUNNING)) {
				
				result = false;
				
				commonUtils.updataReportStatus(Constant.DIRECTIVE_STATUS_EXCEPTION, Integer.parseInt(directiveTblMap.get("SID").toString()), "内存溢出");
			}
		}
		
		return new AsyncResult<Boolean>(Boolean.valueOf(result));
	}
}
