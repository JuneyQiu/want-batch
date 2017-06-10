package com.want.batch.job.sfa2.pic.jobs;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa2.pic.dao.FileUploadDao;
import com.want.batch.job.sfa2.pic.pojo.CompFileUpload;
import com.want.batch.job.utils.ProjectConfig;
@Component
public class RemoveBPM2File extends AbstractWantJob{
	@Autowired
	public FileUploadDao fileUploadDao;
	public static final String KEEP_TIME = ProjectConfig.getInstance().getString("i.bpm.file.time");
	public static final String NEW_PATH = ProjectConfig.getInstance().getString("i.bpm.new.path");
	
	public static final String DEL_STATUS="9";//删除状态
	public static final String BAK_STATUS="7";//备份状态
	
	@Override
	public void execute() throws Exception {
		//1、获取超过保留日期的照片
		List<CompFileUpload> fileList = getAfterKeepTimeFileList(KEEP_TIME);
		
		//测试数据
//		bpmTestFile(fileList);
		//2、备份bpm2超过半年的附件资料，并删除原有资料
		backAfterDateFile(fileList);
	}
	
	/**
	 * 获取超过保留日期的照片
	 * @param keepTime  保留照片的时间（单位：月）
	 * @param photoType 要删除的照片类型
	 * @return
	 */
	public List<CompFileUpload> getAfterKeepTimeFileList(String keepTime) {
		List<CompFileUpload> fileList = new ArrayList<CompFileUpload>();
		StringBuffer sql = new StringBuffer();
		sql.append("select FILE_SID,FILE_NAME,FILE_SIZE,FILE_PATH,FILE_STATUS  ");
		sql.append("from  BPMUSER.MASTER_FILE_REL a inner join BPMUSER.FORM_MASTER_INFO b on a.MASTER_ID = b.PROCESSID ");
		sql.append("inner join wantcomp.FILE_UPLOAD_TBL c on a.FILE_ID= c.FILE_SID ");
		sql.append("where b.FORMRESULT  in ('A','R','C')   and a.CREATE_DATE< trunc(add_months(sysdate,-?)) and c.FILE_STATUS ='1' ");
		
		logger.info("BPM2 getAfterKeepTimeFileList sql: "+ sql.toString());
		List<Map<String, Object>> list = this.getWantcompOperations()
				.queryForList(sql.toString(),
						new Object[] { keepTime });
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
		
		logger.info("getAfterKeepTimeFileList BPM2 file size : "+list.size());
		return fileList;
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
				fileUploadDao.deleteFile(filePath, fileName);
				//备份并且删除成功
				bakList.add(file);
			} catch (IOException e) {
				logger.error("file: "+file.getFilePath()+file.getFileName()+" failed, exception is "+e);
				continue;
			}
		}
		
		logger.info("backAfterDateFile list size : "+ bakList.size());
		int statusCounts = fileUploadDao.updateFileStatus(bakList,BAK_STATUS );
		logger.info("updateFileStatus record counts" +statusCounts);

	}
	
	public void bpmTestFile(List<CompFileUpload>  list){
		for(CompFileUpload file : list){
			String filePath = file.getFilePath();
			String fileName = file.getFileName();
			try {
				fileUploadDao.backUpFile(filePath, fileName,NEW_PATH);
				
			} catch (IOException e) {
				logger.error("file: "+file.getFilePath()+file.getFileName()+" failed, exception is "+e);

			}

		}
	}
	
}
