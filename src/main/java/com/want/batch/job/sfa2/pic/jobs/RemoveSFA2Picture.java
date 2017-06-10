package com.want.batch.job.sfa2.pic.jobs;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa2.pic.dao.FileUploadDao;
import com.want.batch.job.sfa2.pic.pojo.CompFileUpload;
import com.want.batch.job.utils.ProjectConfig;
@Component
public class RemoveSFA2Picture extends AbstractWantJob {
	
	@Autowired
	public FileUploadDao fileUploadDao;
	
	public static final String KEEP_TIME = ProjectConfig.getInstance().getString("i.sfa.file.time");
	public static final String NEW_PATH = ProjectConfig.getInstance().getString("i.sfa.new.path");

	public static final String DEL_STATUS="9";//删除状态
	public static final String BAK_STATUS="7";//备份状态
	
	@Override
	public void execute() throws Exception {
//		//获取超过保留日期的照片
//		//装车照片
//		List<CompFileUpload> carFileList = getAfterKeepTimeFileList(KEEP_TIME, 1);
//		backAfterDateFile(carFileList);
//		
//		//基础陈列照片
//		List<CompFileUpload> dailyReportFileList = getAfterKeepTimeFileList(KEEP_TIME,3);
//		backAfterDateFile(dailyReportFileList);
//		
//		//特殊陈列照片
//		List<CompFileUpload> sdAcutalFileList = getAfterKeepTimeFileList(KEEP_TIME, 4);
//		backAfterDateFile(sdAcutalFileList);
//		
//		//异常拜访照片
//		List<CompFileUpload> unusualFileList = getAfterKeepTimeFileList(KEEP_TIME, 5);
//		backAfterDateFile(unusualFileList);
//		
//		//mdm 中不用的照片
//		List<CompFileUpload> mdmFileList = getMDMUnCheckedFileList(KEEP_TIME);
//		backAfterDateFile(mdmFileList);
		
		List<CompFileUpload> fileList = getAfterKeepTimeFileList(KEEP_TIME, null);
		backAfterDateFile(fileList);

	}

	/**
	 * 获取一个月前审核未通过的照片，或者在mdm中不使用的
	 * @return
	 */
	public List<CompFileUpload> getMDMUnCheckedFileList(String keepTime) {
		List<CompFileUpload> fileList = new ArrayList<CompFileUpload>();
		StringBuffer sql = new StringBuffer();
		sql.append("select FILE_SID,FILE_NAME,FILE_SIZE,FILE_PATH,FILE_STATUS from wantcomp.file_upload_tbl where file_sid in (  ");
		sql.append("select a.file_sid from ( ");
		sql.append("   select b.file_sid from sfa2.VISIT_PHOTO a inner join wantcomp.FILE_UPLOAD_TBL b  ");
		sql.append("   on a.PHOTO_SID = b.FILE_SID WHERE a.PHOTO_TYPE = 2  and b.FILE_STATUS='1' ");
		sql.append(")a left join (  ");
		sql.append("   select c.file_sid  from mdm.store_info a inner join mdm.store_photo_rel b on a.sid = b.store_sid  ");
		sql.append("   inner join wantcomp.file_upload_tbl c on b.file_sid = c.file_sid  ");
		sql.append(") b on a.file_sid= b.file_sid where b.file_sid is null ) ");
		sql.append("and CREATE_DATE< trunc(add_months(sysdate,-?))  ");
		List<Map<String, Object>> list = this.getSfa2JdbcOperations().queryForList(sql.toString(),new Object[] { keepTime });
		for(Map<String, Object> file : list){
			BigDecimal fileSid = new BigDecimal(file.get("FILE_SID").toString());
			String fileName  = file.get("FILE_NAME").toString();
			BigDecimal fileSize = new BigDecimal(file.get("FILE_SIZE").toString());
			String filePath = file.get("FILE_PATH").toString();
			String fileStatus = file.get("FILE_STATUS").toString();
			CompFileUpload compUpload = new CompFileUpload();
			compUpload.setFileSid(fileSid);
			compUpload.setFileName(fileName);
			compUpload.setFileSize(fileSize);
			compUpload.setFilePath(filePath);
			compUpload.setFileStatus(fileStatus);
			fileList.add(compUpload);
		}
		
		logger.info("getMDMUnCheckedFileList list size："+fileList.size());
		return fileList;
	}
	
	/**
	 * 获取超过保留日期的照片
	 * @param keepTime  保留照片的时间（单位：月）
	 * @param photoType 要删除的照片类型
	 * @return
	 */
	public List<CompFileUpload> getAfterKeepTimeFileList(String keepTime,Integer photoType) {
		List<CompFileUpload> fileList = new ArrayList<CompFileUpload>();
		StringBuffer sql = new StringBuffer();
		sql.append("select FILE_SID,FILE_NAME,FILE_SIZE,FILE_PATH,FILE_STATUS  ");
		sql.append("from sfa2.VISIT_PHOTO a inner join wantcomp.FILE_UPLOAD_TBL b on a.PHOTO_SID = b.FILE_SID  ");
		sql.append("WHERE   a.VISIT_DATE< to_date('20140701','yyyymmdd') and a.VISIT_DATE>= to_date('20140601','yyyymmdd')  and a.EMP_ID='00000754'");
		if( null != photoType && !"".equals(photoType) ){
			sql.append(" and a.PHOTO_TYPE =  "+photoType);
		}
		
		List<Map<String, Object>> list = this.getSfa2JdbcOperations()
				.queryForList(sql.toString(),
						new Object[] { });
		for(Map<String, Object> file : list){
			BigDecimal fileSid = new BigDecimal(file.get("FILE_SID").toString());
			String fileName  = file.get("FILE_NAME").toString();
			BigDecimal fileSize = new BigDecimal(file.get("FILE_SIZE").toString());
			String filePath = file.get("FILE_PATH").toString();
			String fileStatus = file.get("FILE_STATUS").toString();
			CompFileUpload compUpload = new CompFileUpload();
			compUpload.setFileSid(fileSid);
			compUpload.setFileName(fileName);
			compUpload.setFileSize(fileSize);
			compUpload.setFilePath(filePath);
			compUpload.setFileStatus(fileStatus);
			fileList.add(compUpload);
		}
		
		logger.info("getAfterKeepTimeFileList photoType: "+photoType+" ,size : "+list.size());
		return fileList;
	}

	/**
	 * 执行删除不需要的附件，并更新file_uplod_tbl该笔的记录
	 * @param list
	 */
	public void removeUnUsedFile(List<CompFileUpload>  list){
		List<CompFileUpload> delList = new ArrayList<CompFileUpload>();
		for(CompFileUpload file : list){
			String filePath = file.getFilePath();
			String fileName = file.getFileName();
			try {
				fileUploadDao.deleteFile(filePath, fileName);
				//删除成功
				delList.add(file);
			} catch (IOException e) {
				logger.error("file: "+file.getFilePath()+file.getFileName()+" delete failed, exception is "+e);
				continue;
			}
		}
		logger.info("removeUnUsedFile list size : "+ delList.size());
		int statusCounts = fileUploadDao.updateFileStatus(delList, DEL_STATUS);
		logger.info("updateFileStatus record counts" +statusCounts);
		
	}
	
	/**
	 * 执行备份的附件，并更新file_uplod_tbl该笔的记录
	 * @param list
	 */
	public void backAfterDateFile(List<CompFileUpload>  list){
		List<CompFileUpload> bakList = new ArrayList<CompFileUpload>();
		for(CompFileUpload file : list){
			String filePath = file.getFilePath();
			String fileName = file.getFileName();
			try {
				fileUploadDao.backUpFile(filePath, fileName,NEW_PATH);
//				fileUploadDao.deleteFile(filePath, fileName);
				//备份并且删除成功
				bakList.add(file);
			} catch (IOException e) {
				logger.error("file: "+file.getFilePath()+file.getFileName()+" failed, exception is "+e);
				continue;
			}
		}
		
		logger.info("backAfterDateFile list size : "+ bakList.size());
		int statusCounts = fileUploadDao.updateFileStatus(bakList, DEL_STATUS);
		logger.info("updateFileStatus record counts" +statusCounts);

	}
	
	
	
	public static void main(String[] args){

	}
	
	
}
