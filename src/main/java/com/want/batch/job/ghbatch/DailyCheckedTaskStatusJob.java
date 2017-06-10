// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.ghbatch.adapter.WantMisServiceAdapter;
import com.want.batch.job.ghbatch.dao.AuditCustomerStoreage02Dao;
import com.want.batch.job.ghbatch.dao.CustomerStorageInfoDao;
import com.want.batch.job.ghbatch.dao.ProdCustomerDao;
import com.want.batch.job.ghbatch.dao.ProdInfoDao;
import com.want.batch.job.ghbatch.dao.SendpoHeadTblDao;
import com.want.batch.job.ghbatch.dao.TaskCustomerDao;
import com.want.batch.job.ghbatch.dao.TaskCustomerProductDao;
import com.want.batch.job.ghbatch.dao.TaskListDao;
import com.want.batch.job.ghbatch.pojo.ProdInfo;
import com.want.batch.job.ghbatch.pojo.TaskCustomer;
import com.want.batch.job.ghbatch.pojo.TaskCustomerProduct;
import com.want.batch.job.ghbatch.util.DateFormatEnum;
import com.want.batch.job.ghbatch.util.DomainDateUtils;
import com.want.batch.job.utils.ProjectConfig;

// ~ Comments
// ==================================================

/**
 * 
 * 每日24:00，即次日 00:00，检核稽核任务状态.
 * 
 * <pre>
 * 历史纪录：
 * 2010-3-24 Timothy
 * 	新建文件
 * 2010-3-28 Timothy 添加业务逻辑说明：
 * 
 * <!-- 以下被注释的部分，描述逻辑暂停使用 -->
 * <!--
 * 1. 针对“未上传”的终端稽核任务行程：
 *   1.1 验证所有行程日期24小时之内的稽核终端清单是否已提交：
 *     Y : 若已全部提交，调整该笔行程任务状态为“准时”。
 *   1.2 验证所有行程日期 24 ~ 48 小时之内的 稽核终端清单是否已提交：
 *     Y : 若已全部提交，调整该笔行程任务状态为“延迟”。
 * 
 * 2. 计算稽核客户的稽核结果，根据上期入库&在途
 *   2.1 查询所有行程日期24小时之内的稽核客户资料所对应客户的上期库存关系，上期库存时间去稽核客户资料的前一天
 *   2.2 将上期库存关系逐笔更新到稽核客户-产品清单上期相关栏位
 *   2.3 根据 稽核客户-产品清单 中的上期数据，自动计算所有客户清单中，[稽核专员盘点库存数量]已经录入，但尚未有[稽核结果]的数据
 * -->
 * 
 * 3. 针对稽核任务：
 *   3.1 查询所有结束日期在当前系统日期之前24小时之内的任务，当对应所有行程为“准时”时，修改状态为“已稽核”
 *   3.2 查询所有结束日期在当前系统日期之前 24 ~ 48 小时之内的任务，修改状态为“已稽核”
 * 
 * 2010-3-28 Timothy 添加业务逻辑说明：
 * 
 * 4. 写入所有当天稽核终端行程所对应的产品清单一下栏位：
 *   上期盘点年月(1)、上期盘点日(2)、上期业务盘点数量(3)、近期平均销量(4)、系统开单量(5)
 *   4.1 (1)&(2)&(3) 取得方式：查询所有行程日期为系统日期的的稽核客户资料所对应客户的上期库存关系，上期库存时间去稽核客户资料的前一天
 *   4.2 (4)&(5)：
 *     4.2.1 通过 {@link WantMisServiceAdapter#getAllSkusQty(String, String, String, String, String)} 方法获取 SAP 相关sku 与其 数量关系
 *     4.2.2 再通过 sku 取得对应 品项重新计算 总数关系后，依次存入相关栏位...
 *     
 * 2012-08-01 mirabelle 添加业务逻辑说明：
 * 5.写入“客户到货量”
 * 			抓取的是最近一次业代盘库日期00:00到稽核日期24:00的品项第五层的客户确认的到货数量汇总
 * 6.往234db写入数据
 * 			在稽核客户排程执行完后，再执行往234ICUSTOMER.AUDIT_CUSTOMER_STOREAGE02表中写入数据
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	Timothy
 * PG
 * 
 * UT
 * 
 * MA
 * </pre>
 * @version $Rev$
 * 
 *          <p/>
 *          $Id$
 * 
 */
@Component
public class DailyCheckedTaskStatusJob extends AbstractWantJob {

	// ~ Static Fields
	// ==================================================

	//	private static final int FLAG_PRODUCT_QTY_LAST = 1;

	private static final int FLAG_RECENTLY_QTY_AVG = 2;
	private static final int FLAG_SYSTEM_QTY_TOTAL = 3;

	// ~ Fields
	// ==================================================

	@Autowired
	private WantMisServiceAdapter wantMisServiceAdapter;
	
	@Autowired
	private TaskListDao taskListDao;
	
	@Autowired
	private TaskCustomerDao taskCustomerDao;
	
	@Autowired
	private TaskCustomerProductDao taskCustomerProductDao;
	
	@Autowired
	private CustomerStorageInfoDao customerStorageInfoDao;

	@Autowired
	private ProdInfoDao prodInfoDao;
	
	@Autowired
	private ProdCustomerDao prodCustomerDao;
	
	@Autowired
	private SendpoHeadTblDao sendpoHeadTblDao;
	
	@Autowired
	private AuditCustomerStoreage02Dao auditCustomerStoreage02Dao;
	
//	private ProductQtyEndInfos productQtyEndInfos;
	
	private String oldDateFrom;
	private String oldDateTo;
	
	// 2011-12-19 mirabelle add
	@SuppressWarnings("unused")
	private String importandProdFrom;
	
	@SuppressWarnings("unused")
	private String importandProdTo;
	
	@SuppressWarnings("unused")
	private String divisionSid;
	
	@SuppressWarnings("unused")
	private String status;
	
	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/*
	 * 2010-3-28 Timothy
	 * 
	 * @see com.want.mis.domain.batch.AbstractMisJob#execute()
	 */
	public void execute() throws Exception {

		DateTime currentDate = new DateTime();

		this.executeTasks(currentDate);

		// 只有当这两个属性同时被设定时，才会更新制定时间段范围内的 业务逻辑说明 4 的部分
		if (StringUtils.isNotBlank(this.getOldDateFrom()) && StringUtils.isNotBlank(this.getOldDateTo())) {

			DateTime oldDate = new DateTime(DateFormatEnum.DATE_NO_PARTITION.parse(this.getOldDateFrom()));
			String oldDateTo = new DateTime(DateFormatEnum.DATE_NO_PARTITION.parse(this.getOldDateTo()))
				.plusDays(1)
					.toString(DateFormatEnum.DATE_NO_PARTITION.getPattern());

			while (!oldDate.toString(DateFormatEnum.DATE_NO_PARTITION.getPattern()).equals(oldDateTo)) {

				logger.debug("***************** 排程日期 ======>" + oldDate.toString(DateFormatEnum.DATE_NO_PARTITION.getPattern()));

				this.executeStoreTaskDetailsSystemData(oldDate);
				oldDate = oldDate.plusDays(1);
			}
		}

		this.executeStoreTaskDetailsSystemData(currentDate);
	}

	/**
	 * <pre>
	 * 2010-3-28 Timothy
	 * 参照类注释中，业务逻辑说明 3 的部分
	 * </pre>
	 * @throws SQLException 
	 * 
	 */
	private void executeTasks(DateTime currentDate) throws SQLException {

		// 参照类注释中，业务逻辑说明 3.1
		this.getTaskListDao().updateStateCheckedByAllDetailsHaveSameState(currentDate.minusDays(1).toDate(), true);

		// 参照类注释中，业务逻辑说明 3.2
		this.getTaskListDao().updateStateCheckedByAllDetailsHaveSameState(currentDate.minusDays(2).toDate(), false);
	}

	/**
	 * <pre>
	 * 2010-4-27 Timothy
	 * 参照类注释中，业务逻辑说明 4 的部分
	 * </pre>
	 * 
	 * @param currentDate
	 * @throws SQLException 
	 */
	private void executeStoreTaskDetailsSystemData(DateTime currentDate) throws SQLException {
		
		try {
			
			// 2011-04-08 Deli modify 将行程日期改为currentDate的前一天
			DateTime taskDetailDate = currentDate.minusDays(1);

			DateTime checkDate = null;
			
			// 是否需要执行重点品项
			boolean isImportant = false;

			// 2010-12-13 Deli 因为从SAP中取出的是一个客户对应的所有订单信息，所以宣告一个Cache，防止每次根据sku循环时会重复获取同一个 客户的信息
			Map<String, Map<String, Object>> productQtyLastImportantMapCache = new HashMap<String, Map<String, Object>>();
			Map<String, Map<String, Integer>> recentlyQtyAvgImportantMapCache = new HashMap<String, Map<String, Integer>>();
			Map<String, Map<String, Integer>> systemQtyTotalImportantMapCache = new HashMap<String, Map<String, Integer>>();

			// 因为从SAP中取出的是一个客户对应的所有订单信息，所以宣告一个Cache，防止每次根据sku循环时会重复获取同一个 客户的信息
			Map<String, Map<String, Object>> productQtyLastCommonMapCache = new HashMap<String, Map<String, Object>>();
			Map<String, Map<String, Integer>> recentlyQtyAvgCommonMapCache = new HashMap<String, Map<String, Integer>>();
			Map<String, Map<String, Integer>> systemQtyTotalCommonMapCache = new HashMap<String, Map<String, Integer>>();

			Map<String, Map<String, Object>> productQtyLastMapCache = null;
			Map<String, Map<String, Integer>> recentlyQtyAvgMapCache = null;
			Map<String, Map<String, Integer>> systemQtyTotalMapCache = null;
			
			// 2011-12-19 mirabelle add 将日期转换成int型数值
			int intTaskDetailDate = Integer.parseInt(DateFormatEnum.DATE.format(taskDetailDate.toDate()).replaceAll("/", ""));
			
			// 2012-11-14 mirabelle add 当状态为all时，执行181db的数据更新
			if("all".equals(this.getStatus())) {
				
				// 2010-11-17 Deli 将当前系统日期改为当前系统日期前一天作为查询条件
				for (TaskCustomer taskCustomer : this.getTaskCustomerDao().findByTaskDetailDate(taskDetailDate)) {
	
					String customerId = taskCustomer.getCustomerId();
	
					for (TaskCustomerProduct taskCustomerProduct : this.getTaskCustomerProductDao().findByTaskCustomer(taskCustomer)) {

						// 2010-12-13 Deli add 若为一月份的重点品项，则按重点品项的规则处理
						// 2011-12-19 mirabelle update 若为gh-job.properties中设定区间内的重点品相，则按重点品项的规则处理，验证日期长度是否为8位，是否为空
						if (StringUtils.isNotEmpty(this.getImportandProdFrom()) && StringUtils.isNotEmpty(this.getImportandProdTo())
								&& (this.getImportandProdFrom().length() == 8 && this.getImportandProdTo().length() == 8)
								&&  intTaskDetailDate >= Integer.parseInt(this.getImportandProdFrom()) 
								&&	intTaskDetailDate <= Integer.parseInt(this.getImportandProdTo())
								&& "1".equals(taskCustomerProduct.getIsImportant())) {
	
							checkDate = this.getDateByRule(taskDetailDate) != null ? new DateTime(this.getDateByRule(taskDetailDate)) : null;
							productQtyLastMapCache = productQtyLastImportantMapCache;
							recentlyQtyAvgMapCache = recentlyQtyAvgImportantMapCache;
							systemQtyTotalMapCache = systemQtyTotalImportantMapCache;
							
							// 是否重点品项为 true
							isImportant = true;
						}
						else {
			
							checkDate = new DateTime(DomainDateUtils.getDateByRule01(taskDetailDate));
							
							productQtyLastMapCache = productQtyLastCommonMapCache;
							recentlyQtyAvgMapCache = recentlyQtyAvgCommonMapCache;
							systemQtyTotalMapCache = systemQtyTotalCommonMapCache;
						}
	
						// 2013-11-15 mirabelle add 添加checkDate非空验证，因为201402返回为null
						taskCustomerProduct.setCheckYearMonth(checkDate != null ? checkDate.toString("yyyyMM") : "");
						taskCustomerProduct.setCheckDay(checkDate != null ? checkDate.toString("dd") : "");
	
						Map<String, Object> produtQtyLastInfo = this.getProductQtyLastInfo(productQtyLastMapCache,
							customerId,
							taskCustomerProduct,
							taskDetailDate,isImportant);
							
						taskCustomerProduct.setProductQtyLast((Integer) produtQtyLastInfo.get(taskCustomerProduct.getLv5Id()));
	
						// 2010-12-14 Deli add
						// 2011-02-16 Deli modify 若ProductQtyLast不为null，则给赋值
						if (taskCustomerProduct.getProductQtyLast() != null) {
							
							taskCustomerProduct.setCheckUser((String) produtQtyLastInfo.get("creator" + taskCustomerProduct.getLv5Id()));
							taskCustomerProduct.setCheckDate((Date) produtQtyLastInfo.get("createDate" + taskCustomerProduct.getLv5Id()));
						}
						// 2014-12-25 mirabelle add 当重跑排程时，如果之前check_user和check_date被更新过，此次排程执行时没有查询到值，需要情况资料
						else {
							
							taskCustomerProduct.setCheckUser(null);
							taskCustomerProduct.setCheckDate(null);
						}
						
						taskCustomerProduct.setTaskDate(new Date());
	
						// 2011-01-12 Deli add 客户类型(事业部)
						taskCustomerProduct.setCustomerDivision((produtQtyLastInfo.get("divsionSid" + taskCustomerProduct.getLv5Id()) != null) 
							? new Integer(produtQtyLastInfo.get("divsionSid" + taskCustomerProduct.getLv5Id()) 
								.toString()) : null);
	
						// 参照类注释中，业务逻辑说明 4.2
						taskCustomerProduct.setRecentlyQtyAvg(this.getQty(recentlyQtyAvgMapCache,
							customerId,
							taskCustomerProduct,
							taskDetailDate,
							DailyCheckedTaskStatusJob.FLAG_RECENTLY_QTY_AVG,
							null)
								/ TaskCustomerProduct.RECENTLY_QTY_AVG_MONTHS);
	
						taskCustomerProduct.setSystemQtyTotal(this.getQty(systemQtyTotalMapCache,
							customerId,
							taskCustomerProduct,
							taskDetailDate,
							DailyCheckedTaskStatusJob.FLAG_SYSTEM_QTY_TOTAL,
							null));
	
						// 日志记录...
						taskCustomerProduct.setUpdateUser("sys");
						taskCustomerProduct.setUpdateDate(new Date());
						
						// 2012-08-01 mirabelle add 取得客户到货量
						taskCustomerProduct.setProductQtyEnd(this.getProductQtyEnd(taskCustomerProduct.getLv5Id(), checkDate, taskDetailDate, customerId));
						
						this.getTaskCustomerProductDao().update(taskCustomerProduct);
					}
				}
			}
			
			// 2012-11-14 mirabelle add 当状态为all或者to234时，才执行此部分
			if ("all".equals(this.getStatus()) || "to234".equals(this.getStatus())) {
			
				// 2012-11-12 mirabelle add 从181移转数据到234icustomer.AUDIT_CUSTOMER_STOREAGE02
				this.dataTranferToAuditCustomerStoreage02(taskDetailDate);
			}
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>
	 * 2012-11-12 Mirabelle
	 * 	从181移转数据到234icustomer.AUDIT_CUSTOMER_STOREAGE02
	 * 作为客户库存报表的数据
	 * </pre>
	 * @throws SQLException 
	 * 
	 */
	private void dataTranferToAuditCustomerStoreage02(DateTime taskDetailDate) throws SQLException {

		// 删除当前行程日期下的资料
		this.getAuditCustomerStoreage02Dao().deleteAuditCustomerStoreage02(taskDetailDate);
		
		// 取得181取得客户库存报表的资料
		List<Map<String, Object>> taskCustomerProducts = this.getTaskCustomerDao().getTaskCustomerProductInfos(taskDetailDate);
		
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		
		// 循环拼接成需要插入的资料
		for (int i = 0; i < taskCustomerProducts.size(); i++) {
			
			Map<String, Object> map = taskCustomerProducts.get(i);
			Object[] obj = new Object[18];
			obj[0] = map.get("SID");
			obj[1] = map.get("CUSTOMER_THIRD_ID");
			obj[2] = map.get("TASK_DATE");
			obj[3] = map.get("CUSTOMER_DIVISION");
			obj[4] = map.get("CUSTOMER_ID");
			obj[5] = map.get("CUSTOMER_NAME");
			obj[6] = map.get("LV_5_ID");
			
			// modify 2014-11-07 添加重点品项标示
			if(map.get("IS_IMPORTANT").equals("1")) {
				
				obj[7] = "◆" + map.get("PROD_NAME");
			}
			else {
				
				obj[7] = map.get("PROD_NAME");
			}
			
			obj[8] = map.get("TOTAL_QTY");
			obj[9] = map.get("PRODUCT_QTY_LAST");
			obj[10] = map.get("PRODUCT_QTY_END");
			obj[11] = map.get("RECENTLY_QTY_AVG");
			obj[12] = map.get("CHECK_DATE");
			obj[13] = map.get("CREATE_DATE");
			obj[14] = map.get("CHECK_USER");
			obj[15] = map.get("JH_ID");
			obj[16] = map.get("JH_NAME");
			obj[17] = map.get("ALL_SUM_RESULT");
			
			batchArgs.add(obj);
		}
		
		// 往234db写数据
		this.getAuditCustomerStoreage02Dao().createAuditCustomerStoreage02(batchArgs, this.getDataMartJdbcOperations());
	}
	
	/**
	 * <pre>
	 * 2012-8-2 Mirabelle
	 * 	取得客户到货量
	 * 2012-09-28 mirabelle update qtyEnd由Integer修改为BigDecimal
	 * </pre>
	 * 
	 * @param lv5Id
	 * @return
	 * @throws SQLException 
	 */
	private BigDecimal getProductQtyEnd(String lv5Id, DateTime checkDate, DateTime taskDetailDate, String customerId) throws SQLException {

		List<Map<String, Object>> productQtyEndInfos = this.getProductQtyEndInfos(checkDate, taskDetailDate, customerId);
		
		BigDecimal qtyEnd = BigDecimal.ZERO;

//		logger.debug(productQtyEndInfos.size());
		
		if (productQtyEndInfos != null) {
			
			for (int i = 0; i < productQtyEndInfos.size(); i++) {
				
				Map<String, Object> productQtyEnd = productQtyEndInfos.get(i);
				
				if (lv5Id.equals(productQtyEnd.get("LV_5_ID"))) {
					
					qtyEnd = new BigDecimal(String.valueOf((productQtyEnd.get("CONFIRM_QTY"))));
				}
			}
		}
		
		return qtyEnd;
	}
	
	/**
	 * <pre>
	 * 2012-7-30 Mirabelle
	 * 	根据客户编号，业代最后一次盘库日期，稽核日期查询客户到货量
	 * </pre>
	 * 
	 * @param checkDate
	 * @param taskDetailDate
	 * @param customerId
	 * @throws SQLException 
	 */
	private List<Map<String, Object>> getProductQtyEndInfos(DateTime checkDate, DateTime taskDetailDate, String customerId) throws SQLException {

		List<Map<String, Object>> productQtyEndInfos = null;
		
		// 2013-11-15 mirabelle add 当盘点日期为null时，不进行查询，也就是说明201402不进行重点盘点品项
		if (checkDate != null) {
			
			Map<String, String> param = new HashMap<String, String>();
			param.put("customerId", customerId);
			param.put("beginDate", new SimpleDateFormat("yyyyMMdd").format(checkDate.toDate()));
			param.put("endDate", new SimpleDateFormat("yyyyMMdd").format(taskDetailDate.toDate()));
			
			productQtyEndInfos = this.getSendpoHeadTblDao().getProductQtyEnd(param);
		}
		
		return productQtyEndInfos;
	}
	
	/**
	 * 根据传入的日期，按照某种规则，获取新日期.
	 * <p>
	 * 注：由于规则难以以命名显示，故使用编号处理。
	 * </p>
	 * 
	 * <pre>
	 * 2010-12-13 Deli
	 * 	获取新日期规则：
	 *   传入日期为：5-9号，return "05"
	 *   传入日期为：10-14号，return "10"
	 *   传入日期为：15-19号，return "15"
	 *   传入日期为：20-24号，return "20"
	 *   传入日期为：25-29号，return "25"
	 *   传入日期为：30-下月4号，return "30"
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	private Date getDateByRule(DateTime date) {

		if (date == null) {

			throw new NullPointerException("输入日期不可为空...");
		}
		
		int dayOfMonth = date.getDayOfMonth();

		// 1-4号，返回上个月的30号
		if (dayOfMonth <= 4) {

			return date.minusMonths(1).withDayOfMonth(30).toDate();
		}

		// 5-9号，返回5号
		else if ((dayOfMonth >= 5) && (dayOfMonth <= 9)) {

			return date.withDayOfMonth(5).toDate();
		}

		// 10-14号，返回10号
		else if ((dayOfMonth >= 10) && (dayOfMonth <= 14)) {

			return date.withDayOfMonth(10).toDate();
		}

		// 15-19号，返回15号
		else if ((dayOfMonth >= 15) && (dayOfMonth <= 19)) {

			return date.withDayOfMonth(15).toDate();
		}

		// 20-24号，返回20号
		else if ((dayOfMonth >= 20) && (dayOfMonth <= 24)) {

			return date.withDayOfMonth(20).toDate();
		}

		// 25-29号，返回25号
		else if ((dayOfMonth >= 25) && (dayOfMonth <= 29)) {

			return date.withDayOfMonth(25).toDate();
		}

		// 30-31号，返回30号
		return date.withDayOfMonth(30).toDate();
	}

	/**
	 * <pre>
	 * 2010-4-27 Timothy
	 * 
	 * 2011-01-21 Deli 去掉查询ProductQtyLast方法
	 * </pre>
	 * 
	 * @param customerQtyMapCache
	 * @param taskCustomerProduct
	 * @param currentDate
	 * @param qtyFlag
	 * @param logInfo
	 * @return
	 * @throws Exception 
	 */
	private Integer getQty(	Map<String, Map<String, Integer>> customerQtyMapCache,
													String customerId,
													TaskCustomerProduct taskCustomerProduct,
													DateTime currentDate,
													int qtyFlag,
													Map<String, Object> logInfo) throws Exception {

		String lv5Id = taskCustomerProduct.getLv5Id();
		Map<String, Integer> lv5sQtyMap = customerQtyMapCache.get(customerId);

		logger.debug("lv5sQtyMap is >>>" + lv5sQtyMap);

		Map<String, BigDecimal> skusQtyMap = null;

		// 在一次运行中，当尚未取得此客户的相关数据时，才获取资料，否则无需重复获取
		if (lv5sQtyMap == null) {

			// 客户到货量、系统开单量 从 SAP 获取
			String companyId = this.getCompanyId(customerId, lv5Id) + 1;
			String orderDateFrom = this.getOrderDataFrom(currentDate, qtyFlag);
			String orderDateTo = this.getOrderDataTo(currentDate, qtyFlag);
			String poType = WantMisServiceAdapter.SAP_PO_TYPE_ALL;

			skusQtyMap = this.getWantMisServiceAdapter().getAllSkusQty(customerId, companyId, orderDateFrom, orderDateTo, poType);

			lv5sQtyMap = this.getLv5sQtyMap(skusQtyMap);
			
			customerQtyMapCache.put(customerId, lv5sQtyMap);
		}


		return NumberUtils.toInt(String.valueOf(lv5sQtyMap.get(lv5Id)));
	}

	/**
	 * <pre>
	 * 2010-4-27 Timothy
	 * </pre>
	 * 
	 * @param skusQtyMap
	 * @return
	 * @throws SQLException 
	 */
	private Map<String, Integer> getLv5sQtyMap(Map<String, BigDecimal> skusQtyMap) throws SQLException {

		Map<String, Integer> lv5sQtyMap = new HashMap<String, Integer>();

		if (skusQtyMap != null) {
			
			for (Map.Entry<String, String> entry : this.getSkuIdMappingLv5Id(skusQtyMap.keySet()).entrySet()) {

				String skuId = entry.getKey();
				String lv5Id = entry.getValue();

				Integer lv5Qty = skusQtyMap.get(skuId).intValue();

				// 当已经存在此笔 lv5Id 时，则进行累加...
				if (lv5sQtyMap.containsKey(lv5Id)) {
					lv5Qty = lv5Qty + lv5sQtyMap.get(lv5Id);
				}

				lv5sQtyMap.put(lv5Id, lv5Qty);
			}
		}		

		return lv5sQtyMap;
	}

	/**
	 * <pre>
	 * 2011-1-15 Deli
	 * </pre>
	 * 
	 * @param skusQtyMap
	 * @return
	 * @throws SQLException 
	 * @throws NumberFormatException 
	 */
	private Map<String, Object> getLv5sQtyMap(List<Map<String, Object>> skusQtyMap) throws NumberFormatException, SQLException {

		Map<String, Object> lv5sQtyMap = new HashMap<String, Object>();

		if (skusQtyMap != null) {			
		
			for (Map.Entry<String, String> entry : this.getSkuIdListLv5Id(skusQtyMap).entrySet()) {
	
				String skuId = entry.getKey();
				String lv5Id = entry.getValue();
				
				Integer lv5Qty = (skusQtyMap != null) ? this.getLv5TotalCount(skuId, skusQtyMap) : 0;//new Integer(skusQtyMap.get(skuId).toString()) : 0;
	
				// 取得该sku的创建时间
				Map<String, Object> creatorInfoMap = this.getCreatorInfo(skuId, lv5Id, skusQtyMap);
				
				// 当已经存在此笔 lv5Id 时，则进行累加...;2012-10-08 mirabelle update 当各个sku的录入时间不一致时，取最后一次录入的时间
				if (lv5sQtyMap.containsKey(lv5Id)) {
					
					lv5Qty = lv5Qty + (Integer) lv5sQtyMap.get(lv5Id);
					
					
					// 在sku中汇总到lv5取得最后录入的日期，取得lv5sQtyMap中的creator信息，将最大的日期放入lv5sQtyMap中
					if (lv5sQtyMap.get("createDate" + lv5Id) != null && creatorInfoMap.get("createDate" + skuId) != null) {
						
						DateTime preDateTime = new DateTime(lv5sQtyMap.get("createDate" + lv5Id));
						DateTime nowDateTime = new DateTime(creatorInfoMap.get("createDate" + lv5Id));
						
						if (preDateTime.compareTo(nowDateTime) == -1) {
							
							lv5sQtyMap.put("creator" + lv5Id, creatorInfoMap.get("creator" + lv5Id));
							lv5sQtyMap.put("createDate" + lv5Id, creatorInfoMap.get("createDate" + lv5Id));
							lv5sQtyMap.put("divsionSid" + lv5Id, creatorInfoMap.get("divsionSid" + lv5Id));
						}
					}
					// 2012-09-09 mirabelle add 当一个lv5sQtyMap为空，另一个creatorInfoMap不为空时，将不为空的一个放入
					else {
						
						if (lv5sQtyMap.get("createDate" + lv5Id) == null && creatorInfoMap.get("createDate" + skuId) != null) {
							
							lv5sQtyMap.put("creator" + lv5Id, creatorInfoMap.get("creator" + lv5Id));
							lv5sQtyMap.put("createDate" + lv5Id, creatorInfoMap.get("createDate" + lv5Id));
							lv5sQtyMap.put("divsionSid" + lv5Id, creatorInfoMap.get("divsionSid" + lv5Id));
						}
					}
				}
				else {
					
					// 获取品相对应的盘点人、盘点日期和客户所对应的事业部
					lv5sQtyMap.put("creator" + lv5Id, creatorInfoMap.get("creator" + lv5Id));
					lv5sQtyMap.put("createDate" + lv5Id, creatorInfoMap.get("createDate" + lv5Id));
					lv5sQtyMap.put("divsionSid" + lv5Id, creatorInfoMap.get("divsionSid" + lv5Id));
				}
	
				lv5sQtyMap.put(lv5Id, lv5Qty);
			}
			
		}

		return lv5sQtyMap;
	}

	/**
	 * <pre>
	 * 2010-4-27 Timothy
	 * 根据 产品 id，获取其每个产品id与其品项id的对应关系
	 * </pre>
	 * 
	 * @param skuIds
	 * @return
	 * @throws SQLException 
	 */
	private Map<String, String> getSkuIdMappingLv5Id(Set<String> skuIds) throws SQLException {

		Map<String, String> results = new HashMap<String, String>();
		
		for (ProdInfo prodInfo : this.getProdInfoDao().findByProdIds(skuIds)) {
			results.put(prodInfo.getProdId(), prodInfo.getLv5Id());
		}

		return results;
	}

	/**
	 * <pre>
	 * 2015-8-27 D11-mirabelle
	 * 根据传过来的sku取得品项对应的lv5.
	 * </pre>	
	 * 
	 *	<ol>
	 * 		<li>客户库存skuIds</li>
	 *	</ol>
	 * @param skuIds
	 * @return
	 * @throws SQLException
	 */
	private Map<String, String> getSkuIdListLv5Id(List<Map<String, Object>> skuIds) throws SQLException {

		Map<String, String> results = new HashMap<String, String>();
		Set<String> s = new HashSet<String>();
		
		for(int i = 0; i < skuIds.size(); i++) {
			
			String prodId = (String)skuIds.get(i).get("prodId");
			s.add(prodId);
		}
		
		for (ProdInfo prodInfo : this.getProdInfoDao().findByProdIds(s)) {
			results.put(prodInfo.getProdId(), prodInfo.getLv5Id());
		}
		
		return results;
	}
	
	/**
	 * <pre>
	 * 2015-8-27 D11-mirabelle
	 * 取得指定品项的业代盘点之和.
	 * </pre>	
	 * 
	 *	<ol>
	 * 		<li>指定的sku</li>
	 *  	<li>业代盘点客户库存</li>
	 *	</ol>
	 * @param skuId
	 * @param skusQtyList
	 * @return
	 */
	private Integer getLv5TotalCount(String skuId, List<Map<String, Object>> skusQtyList){
		
		Integer qty = 0;
		
		// 遍历skusQtyMap，找到指定的skuId的对应和加总
		for (int i = 0; i < skusQtyList.size(); i++) {
			
			Map<String, Object> map = skusQtyList.get(i);
			
			if (skuId.equals(map.get("prodId"))) {
				
				qty += new Integer(map.get("totalQty").toString());
			}
		}
		
		return qty;
	}
	
	/**
	 * <pre>
	 * 2015-8-27 D11-mirabelle
	 * 取得指定的客户的创建日期取最大的日期.
	 * </pre>	
	 * 
	 *	<ol>
	 * 		<li>sku品项id</li>
	 *  	<li>lv5Id业代盘点客户库存</li>
	 *  	<li></li>
	 *	</ol>
	 * @param skuId
	 * @param skusQtyList
	 * @return
	 */
	private Map<String, Object> getCreatorInfo(String skuId, String lv5Id, List<Map<String, Object>> skusQtyList){
		
		Map<String, Object> creatorMap = new HashMap<String,Object>();
		
		// 遍历skusQtyMap，找到指定的skuId的对应和加总
		for (int i = 0; i < skusQtyList.size(); i++) {
			
			Map<String, Object> map = skusQtyList.get(i);
			
			if (skuId.equals(map.get("prodId"))) {
				
				if (creatorMap.get("creator" + lv5Id ) != null) {
					
					// 取最晚录入的那个时间
					DateTime nowDateTime = new DateTime(map.get("createDate"));
					DateTime preDateTime = new DateTime(creatorMap.get("createDate" + lv5Id));
					
					if (preDateTime.compareTo(nowDateTime) == -1) {
						
						creatorMap.put("creator" + lv5Id, map.get("creator"));
						creatorMap.put("createDate" + lv5Id, map.get("createDate"));
						creatorMap.put("divsionSid" + lv5Id, map.get("divsionSid"));
					}
				}
				else {
					
					creatorMap.put("creator" + lv5Id, map.get("creator"));
					creatorMap.put("createDate" + lv5Id, map.get("createDate"));
					creatorMap.put("divsionSid" + lv5Id, map.get("divsionSid"));
				}
			}
		}
		
		return creatorMap;
	}
	
	/**
	 * <pre>
	 * 2010-4-27 Timothy
	 * </pre>
	 * 
	 * @param currentDate
	 * @param qtyFlag
	 * @return
	 */
	private String getOrderDataTo(DateTime currentDate, int qtyFlag) {

		// 近期销量结束日期：上月最后一天
		if (qtyFlag == DailyCheckedTaskStatusJob.FLAG_RECENTLY_QTY_AVG) {
			return DateFormatEnum.DATE.format(currentDate.withDayOfMonth(1).minusDays(1).toDate());
		}

		// 业务开单量结束日期：稽核前一日
		if (qtyFlag == DailyCheckedTaskStatusJob.FLAG_SYSTEM_QTY_TOTAL) {
			return DateFormatEnum.DATE.format(currentDate.minusDays(1).toDate());
		}

		throw new IllegalArgumentException();
	}

	/**
	 * <pre>
	 * 2010-4-27 Timothy
	 * </pre>
	 * 
	 * @param currentDate
	 * @param qtyFlag
	 * @return
	 */
	private String getOrderDataFrom(DateTime currentDate, int qtyFlag) {

		// 近期销量开始日期：3月前第一天
		if (qtyFlag == DailyCheckedTaskStatusJob.FLAG_RECENTLY_QTY_AVG) {
			return DateFormatEnum.DATE.format(currentDate
				.minusMonths(TaskCustomerProduct.RECENTLY_QTY_AVG_MONTHS)
					.withDayOfMonth(1)
					.toDate());
		}

		// 业务开单量开始日期：业代盘库日 - 3日
		if (qtyFlag == DailyCheckedTaskStatusJob.FLAG_SYSTEM_QTY_TOTAL) {
			return DateFormatEnum.DATE.format(new DateTime(DomainDateUtils.getDateByRule01(currentDate))
				.minusDays(3)
					.toDate());
		}

		throw new IllegalArgumentException();
	}

	/**
	 * <pre>
	 * 2010-4-27 Timothy
	 * 
	 * 2010-11-01 Deli 添加lv5Id参数，并且修改查询companyId的sql
	 * </pre>
	 * 
	 * @param customerId
	 * @param lv5Id
	 * @return
	 * @throws SQLException 
	 */
	private String getCompanyId(String customerId, String lv5Id) throws SQLException {

		// return this.getCompanyInfoDao().findCompanyIdByCustomerId(customerId);

		return this.getProdCustomerDao().getCompanyIdByCustomerIdAndLv5Id(customerId, lv5Id);
	}

	/**
	 * <pre>
	 * 2011-1-15 Deli
	 * 	获取上期库存盘点人、盘点日期和客户对应的事业部
	 * </pre>
	 * 
	 * @return
	 * @throws SQLException 
	 */
	private Map<String, Object> getProductQtyLastInfo(Map<String, Map<String, Object>> customerQtyMapCache,
																										String customerId,
																										TaskCustomerProduct taskCustomerProduct,
																										DateTime currentDate,
																										boolean isImportant) throws Exception {

		String lv5Id = taskCustomerProduct.getLv5Id();
		Map<String, Object> lv5sQtyMap = customerQtyMapCache.get(customerId);

		logger.debug("lv5sQtyMap is >>>" + lv5sQtyMap);

		// 在一次运行中，当尚未取得此客户的相关数据时，才获取资料，否则无需重复获取
		if (lv5sQtyMap == null) {

//			Map<String, Object> skusQtyMap = null;
//			Map<String, Object> creatorInfo = null;

			// 若为普通品相取得 业代6,16,26日 中午1200 的 datetime 物件，若为1月份的重点品相，则取得 业代6,11,16,21,26,31日 中午1200 的 datetime 物件
			// 2011-12-19 mirabelle update 若为gh-job.properties中设定区间内的重点品相，则取得 业代6,11,16,21,26,31日 中午1200 的 datetime 物件，验证日期长度是否为8位，是否为空
			// 2012-02-13 mirabelle delete 取消日期的时间限制，无论业代什么时候录入，都可以 更新PRODUCT_QTY_LAST 栏位
//			DateTime enteringDate = isImportant
//				? new DateTime(this.getDateByRule(currentDate)).plusDays(1).withHourOfDay(12).withMinuteOfHour(0)
//					: new DateTime(DomainDateUtils.getDateByRule01(currentDate)).plusDays(1).withHourOfDay(12).withMinuteOfHour(0);
				
			// 上期业务盘点库存 从 DB 获取
			List<Map<String, Object>> prodMapping = this.getCustomerStorageInfoDao().findProdIdsQtyMap(customerId,
				taskCustomerProduct.getCheckYearMonth(),
				taskCustomerProduct.getCheckDay(), this.getDivisionSid());
// 2015-08-27 mirabelle update 取消list拆分为map
//			if (prodMapping.size() == 2) {
//
//				skusQtyMap = prodMapping.get(0);
//				creatorInfo = prodMapping.get(1);
//			}

//			logger.debug("when lv5sQtyMap is'nt null >>>" + skusQtyMap);

			lv5sQtyMap = this.getLv5sQtyMap(prodMapping);
			
			customerQtyMapCache.put(customerId, lv5sQtyMap);
		}

		Map<String, Object> prodInfoMap = new HashMap<String, Object>();

		prodInfoMap.put(lv5Id, lv5sQtyMap.get(lv5Id));
		prodInfoMap.put("creator" + lv5Id, lv5sQtyMap.get("creator" + lv5Id));
		prodInfoMap.put("createDate" + lv5Id, lv5sQtyMap.get("createDate" + lv5Id));
		prodInfoMap.put("divsionSid" + lv5Id, lv5sQtyMap.get("divsionSid" + lv5Id));

		return prodInfoMap;
	}

	/**
	 * @return the taskListDao
	 */
	public TaskListDao getTaskListDao() {
		return taskListDao;
	}

	/**
	 * @param taskListDao the taskListDao to set
	 */
	public void setTaskListDao(TaskListDao taskListDao) {
		this.taskListDao = taskListDao;
	}

	/**
	 * @return the oldDateFrom
	 */
	public String getOldDateFrom() {
		return ProjectConfig.getInstance().getString("old.date.form");
	}

	/**
	 * @param oldDateFrom the oldDateFrom to set
	 */
	public void setOldDateFrom(String oldDateFrom) {
		this.oldDateFrom = oldDateFrom;
	}

	/**
	 * @return the oldDateTo
	 */
	public String getOldDateTo() {
		return ProjectConfig.getInstance().getString("old.date.to");
	}

	/**
	 * @param oldDateTo the oldDateTo to set
	 */
	public void setOldDateTo(String oldDateTo) {
		this.oldDateTo = oldDateTo;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return ProjectConfig.getInstance().getString("gh0101.daily.function.status");
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the taskCustomerDao
	 */
	public TaskCustomerDao getTaskCustomerDao() {
		return taskCustomerDao;
	}

	/**
	 * @param taskCustomerDao the taskCustomerDao to set
	 */
	public void setTaskCustomerDao(TaskCustomerDao taskCustomerDao) {
		this.taskCustomerDao = taskCustomerDao;
	}

	/**
	 * @return the taskCustomerProductDao
	 */
	public TaskCustomerProductDao getTaskCustomerProductDao() {
		return taskCustomerProductDao;
	}

	/**
	 * @param taskCustomerProductDao the taskCustomerProductDao to set
	 */
	public void setTaskCustomerProductDao(
			TaskCustomerProductDao taskCustomerProductDao) {
		this.taskCustomerProductDao = taskCustomerProductDao;
	}

	/**
	 * @return the importandProdFrom
	 */
	public String getImportandProdFrom() {
		return ProjectConfig.getInstance().getString("important.prod.from");
	}

	/**
	 * @param importandProdFrom the importandProdFrom to set
	 */
	public void setImportandProdFrom(String importandProdFrom) {
		this.importandProdFrom = importandProdFrom;
	}

	/**
	 * @return the importandProdTo
	 */
	public String getImportandProdTo() {
		return ProjectConfig.getInstance().getString("important.prod.to");
	}

	/**
	 * @param importandProdTo the importandProdTo to set
	 */
	public void setImportandProdTo(String importandProdTo) {
		this.importandProdTo = importandProdTo;
	}

	/**
	 * @return the customerStorageInfoDao
	 */
	public CustomerStorageInfoDao getCustomerStorageInfoDao() {
		return customerStorageInfoDao;
	}

	/**
	 * @param customerStorageInfoDao the customerStorageInfoDao to set
	 */
	public void setCustomerStorageInfoDao(
			CustomerStorageInfoDao customerStorageInfoDao) {
		this.customerStorageInfoDao = customerStorageInfoDao;
	}

	/**
	 * @return the prodInfoDao
	 */
	public ProdInfoDao getProdInfoDao() {
		return prodInfoDao;
	}

	/**
	 * @param prodInfoDao the prodInfoDao to set
	 */
	public void setProdInfoDao(ProdInfoDao prodInfoDao) {
		this.prodInfoDao = prodInfoDao;
	}

	/**
	 * @return the divisionSid
	 */
	public String getDivisionSid() {
		return ProjectConfig.getInstance().getString("gh0101.daily.customer.division");
	}

	/**
	 * @param divisionSid the divisionSid to set
	 */
	public void setDivisionSid(String divisionSid) {
		this.divisionSid = divisionSid;
	}

	/**
	 * @return the prodCustomerDao
	 */
	public ProdCustomerDao getProdCustomerDao() {
		return prodCustomerDao;
	}

	/**
	 * @param prodCustomerDao the prodCustomerDao to set
	 */
	public void setProdCustomerDao(ProdCustomerDao prodCustomerDao) {
		this.prodCustomerDao = prodCustomerDao;
	}

	/**
	 * @return the sendpoHeadTblDao
	 */
	public SendpoHeadTblDao getSendpoHeadTblDao() {
		return sendpoHeadTblDao;
	}

	/**
	 * @param sendpoHeadTblDao the sendpoHeadTblDao to set
	 */
	public void setSendpoHeadTblDao(SendpoHeadTblDao sendpoHeadTblDao) {
		this.sendpoHeadTblDao = sendpoHeadTblDao;
	}

	/**
	 * @return the auditCustomerStoreage02Dao
	 */
	public AuditCustomerStoreage02Dao getAuditCustomerStoreage02Dao() {
		return auditCustomerStoreage02Dao;
	}

	/**
	 * @param auditCustomerStoreage02Dao the auditCustomerStoreage02Dao to set
	 */
	public void setAuditCustomerStoreage02Dao(
			AuditCustomerStoreage02Dao auditCustomerStoreage02Dao) {
		this.auditCustomerStoreage02Dao = auditCustomerStoreage02Dao;
	}

	/**
	 * @return the wantMisServiceAdapter
	 */
	public WantMisServiceAdapter getWantMisServiceAdapter() {
		return wantMisServiceAdapter;
	}

	/**
	 * @param wantMisServiceAdapter the wantMisServiceAdapter to set
	 */
	public void setWantMisServiceAdapter(WantMisServiceAdapter wantMisServiceAdapter) {
		this.wantMisServiceAdapter = wantMisServiceAdapter;
	}
}
