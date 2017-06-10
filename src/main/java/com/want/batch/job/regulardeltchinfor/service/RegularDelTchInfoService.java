
// ~ Package Declaration
// ==================================================

package com.want.batch.job.regulardeltchinfor.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.regulardeltchinfor.dao.RegularDelTchInfoDao;
import com.want.batch.job.reportproduce.pojo.Constant;


// ~ Comments
// ==================================================

@Component
public class RegularDelTchInfoService {

	// ~ Static Fields
	// ==================================================
	
  //数据分批次插入的笔数
	public final static int DATA_NUMBER=60000;

	// ~ Fields
	// ==================================================
	
	protected final Log logger = LogFactory.getLog(RegularDelTchInfoService.class);
	
	@Autowired
	public RegularDelTchInfoDao regularDelTchInfoDao;
	
	@Autowired
	public DataSource iCustomerDataSource;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================
	
	/**
	 * <pre>
	 * 2015-10-28 Amy
	 * 	 定期删除批发特陈实际信息不完整数据，并备份
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void deleteAndInsertImperfectTchInfo(String runDate)throws Exception {
		
		Connection conn = null ;
		ResultSet rsQuery = null;
		ResultSet rsFind = null;
		
		
		try {
			
			logger.debug("排程执行开始>>>>>>>>>>>>>>>>>>>>>");
			conn = iCustomerDataSource.getConnection();
			
	 	  logger.debug("排程执行开始>>>>>>>>>>>>>>>>>>>>>");
		
			logger.debug("开始执行查询方法>>>>>>>>>>startTime:"+ runDate);
			rsQuery= this.regularDelTchInfoDao.queryImperfectInfo(conn, runDate);
			logger.debug("查询方法执行结束>>>>>>>>>>endTime");
			
			// 存放实际填写主表的用于判断在SD_ACTUAL_DISPLAY表中该sid是否还有资料
			
			List<Integer> sdActualDisplaySidIntList = new ArrayList<Integer>();
			List<Object[]> sdActualDisplaySidList = new ArrayList<Object[]>();
			
			// 去list中重复的元素
			Set<Integer> sdActualDisplaySidSet= new HashSet<Integer>();
			
			// 存放ACTUAL_SID，用于删除SPECIAL_DISPLAY_ACTUAL的异常信息
			List<Integer> specialDisplayActualIntList = new ArrayList<Integer>();
			List<Object[]> specialDisplayActualList = new ArrayList<Object[]>();
			Set<Integer> specialDisplayActualSet = new HashSet<Integer>();
			
			while(rsQuery.next()) {
				
			 // 判断SDACTUALDISPLAYSID是否为空，不为空则加入sdActualDisplaySidIntList，便于分批次去除重复数据
				if((Integer)rsQuery.getInt("SDACTUALDISPLAYSID") != null) {
					
					logger.debug("存放SID>>>>>>>>>>>>sdActualDisplaySidIntList>>");
						
						sdActualDisplaySidIntList.add((Integer)rsQuery.getInt("SDACTUALDISPLAYSID"));
						
						 // 去除重复的sid
						sdActualDisplaySidSet.addAll(sdActualDisplaySidIntList);
						
						//清空sdActualDisplaySidList，不然下次把set元素加入此list的时候是在原来的基础上追加元素的   
						sdActualDisplaySidIntList.clear();
						
						sdActualDisplaySidIntList.addAll(sdActualDisplaySidSet);
				}
				
				// 存放带有异常信息的actualSid，以删除SD_ACTUAL_DISPLAY表的异常信息
				if((Integer)rsQuery.getInt("ACTUAL_SID") != null) {
					
					logger.debug("存放ACTUAL_SID>>>>>>>>>>>>specialDisplayActualIntList>>");
						
						specialDisplayActualIntList.add((Integer)rsQuery.getInt("ACTUAL_SID"));
						
						specialDisplayActualSet.addAll(specialDisplayActualIntList);
						
						specialDisplayActualIntList.clear();
						
						specialDisplayActualIntList.addAll(specialDisplayActualSet);
				}
			}
			
			// 将泛型为Integer的sdActualDisplaySidIntList改为泛型为Object[]便于分批次执行新增和删除操作
			if(sdActualDisplaySidIntList.size() > 0) {
				
				for(int i=0;i < sdActualDisplaySidIntList.size(); i++) {
					
					 Object[] obj = new Object[1];
					 obj[0] = sdActualDisplaySidIntList.get(i);
						
					sdActualDisplaySidList.add(obj);
				}
				
			  // 每60000笔，批量新增/删除一次
				if (sdActualDisplaySidList.size() == DATA_NUMBER) {
					
					logger.debug("sdActualDisplaySidList的大小1>>>>>>>>>>"+sdActualDisplaySidList.size());
					
					logger.debug("开始插入SD_ACTUAL_PROD_BACKUP>>>>>>>>>>startTime1");
				  // 将SD_ACTUAL_PROD表中异常信息添加到SD_ACTUAL_PROD_BACKUP备份表中
					this.regularDelTchInfoDao.addImperfectInfoSdActualProd(sdActualDisplaySidList);
					logger.debug("插入SD_ACTUAL_PROD_BACKUP>>>>>>>>>>endTime1");
					
					logger.debug("开始删除SD_ACTUAL_PROD>>>>>>>>>>startTime1");
				 // 删除SD_ACTUAL_PROD表中的异常信息
					this.regularDelTchInfoDao.delTchInfoSdActualProd(sdActualDisplaySidList);
					logger.debug("删除SD_ACTUAL_PROD>>>>>>>>>>endTime1");
					
					logger.debug("开始插入SD_ACTUAL_PICTURE_BACKUP>>>>>>>>>>startTime1");
				  // 将 SD_ACTUAL_PICTURE表中的异常信息添加到SD_ACTUAL_PICTURE_BACKUP备份表中
					this.regularDelTchInfoDao.addImperfectInfoSdActualPicture(sdActualDisplaySidList);
					logger.debug("插入SD_ACTUAL_PICTURE_BACKUP>>>>>>>>>>endTime1");
					
					logger.debug("删除SD_ACTUAL_PICTURE>>>>>>>>>>startTime1");
					// 删除SD_ACTUAL_PICTURE表中的异常信息
					this.regularDelTchInfoDao.delTchInfoSdActualPicture(sdActualDisplaySidList);
					logger.debug("删除SD_ACTUAL_PICTURE>>>>>>>>>>endTime1");
					
					logger.debug("开始插入SD_ACTUAL_DISPLAY_BACKUP>>>>>>>>>>startTime1");
					// 将SD_ACTUAL_DISPLAY表中异常信息添加到SD_ACTUAL_DISPLAY_BACKUP备份表中
					this.regularDelTchInfoDao.addImperfectInfoSdActualDisplay(sdActualDisplaySidList);
					logger.debug("插入SD_ACTUAL_DISPLAY_BACKUP>>>>>>>>>>endTime1");
					
					logger.debug("开始删除SD_ACTUAL_DISPLAY>>>>>>>>>>startTime1");
				  // 删除SD_ACTUAL_DISPLAY表中的异常信息
					this.regularDelTchInfoDao.delTchInfoSdActualDisplay(sdActualDisplaySidList);
					logger.debug("删除SD_ACTUAL_DISPLAY>>>>>>>>>>endTime1");
					
					sdActualDisplaySidList.clear();
				}
			}
			
			logger.info(">>>>>>>>>>>>sdActualDisplaySidIntList>>" + sdActualDisplaySidIntList.size());
			logger.info(">>>>>>>>>>>>sdActualDisplaySidSet>>" + sdActualDisplaySidSet.size());
			logger.info(">>>>>>>>>>>>sdActualDisplaySidList>>" + sdActualDisplaySidList.size());
			
		  // 如果还有未新增的数据（不足60000笔的情况），则处理这些数据
			if(sdActualDisplaySidList.size() > 0 ) {
				
				logger.debug("开始插入SD_ACTUAL_PROD_BACKUP>>>>>>>>>>startTime2");
			  // 将SD_ACTUAL_PROD表中异常信息添加到SD_ACTUAL_PROD_BACKUP备份表中
				this.regularDelTchInfoDao.addImperfectInfoSdActualProd(sdActualDisplaySidList);
				logger.debug("插入SD_ACTUAL_PROD_BACKUP>>>>>>>>>>endTime2");
				
				logger.debug("开始删除SD_ACTUAL_PROD>>>>>>>>>>startTime2");
			 // 删除SD_ACTUAL_PROD表中的异常信息
				this.regularDelTchInfoDao.delTchInfoSdActualProd(sdActualDisplaySidList);
				logger.debug("删除SD_ACTUAL_PROD>>>>>>>>>>endTime2");
				
				logger.debug("开始插入SD_ACTUAL_PICTURE_BACKUP>>>>>>>>>>startTime2");
			  // 将 SD_ACTUAL_PICTURE表中的异常信息添加到SD_ACTUAL_PICTURE_BACKUP备份表中
				this.regularDelTchInfoDao.addImperfectInfoSdActualPicture(sdActualDisplaySidList);
				logger.debug("插入SD_ACTUAL_PICTURE_BACKUP>>>>>>>>>>endTime2");
				
				logger.debug("删除SD_ACTUAL_PICTURE>>>>>>>>>>startTime2");
				// 删除SD_ACTUAL_PICTURE表中的异常信息
				this.regularDelTchInfoDao.delTchInfoSdActualPicture(sdActualDisplaySidList);
				logger.debug("删除SD_ACTUAL_PICTURE>>>>>>>>>>endTime2");
				
				logger.info("开始插入SD_ACTUAL_DISPLAY_BACKUP>>>>>>>>>>startTime2");
				// 将SD_ACTUAL_DISPLAY表中异常信息添加到SD_ACTUAL_DISPLAY_BACKUP备份表中
				this.regularDelTchInfoDao.addImperfectInfoSdActualDisplay(sdActualDisplaySidList);
				logger.debug("插入SD_ACTUAL_DISPLAY_BACKUP>>>>>>>>>>endTime2");
				
				logger.debug("开始删除SD_ACTUAL_DISPLAY>>>>>>>>>>startTime2");
			  // 删除SD_ACTUAL_DISPLAY表中的异常信息
				this.regularDelTchInfoDao.delTchInfoSdActualDisplay(sdActualDisplaySidList);
				logger.debug("删除SD_ACTUAL_DISPLAY>>>>>>>>>>endTime2");
			}
			
			logger.debug(">>>>>>>>>>>>specialDisplayActualIntList>>" + specialDisplayActualIntList.size());
			logger.debug(">>>>>>>>>>>>specialDisplayActualSet>>" + specialDisplayActualSet.size());
			
		  // 记录当执行完SD_ACTUAL_DISPLAY的删除操作时在SD_ACTUAL_DISPLAY还存有资料的actual_sid
			List<Integer>  actualList = new ArrayList<Integer>();

			// 记录actual_sid的个数，分批次执行
			int count =0;
			
			// 1000笔拼接一次actual_sid
			String strActualSid = ""; 
			
			for(int i=0;i< specialDisplayActualIntList.size();i++) {
				
				count++;
				
				strActualSid+=specialDisplayActualIntList.get(i) + "," ;
				
				logger.debug("strActualSid>>>>>"+strActualSid.substring(0, strActualSid.length() - 1));
				
				if(count == 1000) {
					
					// 查询在SD_ACTUAL_DISPLAY表中删除异常信息后，异常信息对应的ACTUAL_SID在SD_ACTUAL_DISPLAY没有对应数据ACTUAL_SID
					actualList = this.regularDelTchInfoDao.querySdActualDisplayInfoByspecialActualSid(conn, strActualSid.substring(0, strActualSid.length() - 1));
					
					if(actualList.size() > 0) {
						
						for(int j=0; j < actualList.size(); j++) {
							
							Object[] obj = new Object[1];
						  obj[0] = specialDisplayActualIntList.get(j);
						  
						  specialDisplayActualList.add(obj);
						}
						
						logger.info("specialDisplayActualList的大小1>>>>>>>>>>"+specialDisplayActualList.size());
						
						logger.debug("开始插入SPECIAL_DISPLAY_ACTUAL_BACKUP>>>>>>>>>>startTime1");
						// 将SPECIAL_DISPLAY_ACTUAL表中异常信息添加到SPECIAL_DISPLAY_ACTUAL_BACKUP备份表中
						this.regularDelTchInfoDao.addImperfectInfoSpecialDisplayActual(specialDisplayActualList);
						logger.debug("插入SPECIAL_DISPLAY_ACTUAL_BACKUP>>>>>>>>>>endTime1");
						
						logger.debug("删除SPECIAL_DISPLAY_ACTUAL>>>>>>>>>>startTime1");
						// 将SPECIAL_DISPLAY_ACTUAL表中的异常信息删除
						this.regularDelTchInfoDao.delTchInfoSpecialDisplayActual(specialDisplayActualList);
						logger.debug("删除SPECIAL_DISPLAY_ACTUAL>>>>>>>>>>endTime1");
					}
					
					specialDisplayActualList.clear();
					actualList.clear();
					strActualSid= "";
					count = 0;
				}
			}
			
			// 不足一千笔
			if(count >0) {
				
				actualList = this.regularDelTchInfoDao.querySdActualDisplayInfoByspecialActualSid(conn, strActualSid.substring(0, strActualSid.length() - 1));
				
				if(actualList.size() > 0) {
					
					for(int j=0; j < actualList.size(); j++) {
						
						Object[] obj = new Object[1];
					  obj[0] = specialDisplayActualIntList.get(j);
					  
					  specialDisplayActualList.add(obj);
					}
					
					logger.info("specialDisplayActualList的大小2>>>>>>>>>>"+specialDisplayActualList.size());
					
					logger.info("开始插入SPECIAL_DISPLAY_ACTUAL_BACKUP>>>>>>>>>>startTime2");
					// 将SPECIAL_DISPLAY_ACTUAL表中异常信息添加到SPECIAL_DISPLAY_ACTUAL_BACKUP备份表中
					this.regularDelTchInfoDao.addImperfectInfoSpecialDisplayActual(specialDisplayActualList);
					logger.info("插入SPECIAL_DISPLAY_ACTUAL_BACKUP>>>>>>>>>>endTime2");
					
					logger.info("删除SPECIAL_DISPLAY_ACTUAL>>>>>>>>>>startTime2");
					// 将SPECIAL_DISPLAY_ACTUAL表中的异常信息删除
					this.regularDelTchInfoDao.delTchInfoSpecialDisplayActual(specialDisplayActualList);
					logger.info("删除SPECIAL_DISPLAY_ACTUAL>>>>>>>>>>endTime2");
				}
			}
			
			logger.info("排程执行结束>>>>>>>>>>>>>>>>>>>>>");

		} 
		catch (Exception e) {
			
			logger.error(Constant.generateExceptionMessage(e));
			throw e;
		}
		finally {
			
			if(rsQuery != null) {
				
				rsQuery.close();
			}
			
			if(rsFind != null) {
				
				rsFind.close();
			}
			
			if(conn != null) {
				
				conn.close();
			}
		}
	}
}
