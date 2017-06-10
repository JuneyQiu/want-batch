package com.want.batch.job.sfa2.pic.dao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.stereotype.Component;

import com.want.batch.job.sfa2.pic.pojo.CompFileUpload;
import com.want.batch.job.utils.HttpUtil;
import com.want.batch.job.utils.ProjectConfig;
@Component
public class FileUploadDao {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	public static final String MAP_PATH = ProjectConfig.getInstance().getString("i.map.path.old");
	public static final String REAL_PATH = ProjectConfig.getInstance().getString("i.real.path.old");
	
	public static final String DEL_STATUS="9";//删除状态
	public static final String BAK_STATUS="7";//备份状态
	
	@Autowired
	public SimpleJdbcOperations wantcompOperations;
	/**
	 * 更新要删除或者备份的照片的数据状态
	 * @param list
	 * @param status
	 * @return
	 */
	public int updateFileStatus(List<CompFileUpload> list,String status){
		String sql ="update wantcomp.FILE_UPLOAD_TBL b set FILE_STATUS= ? where b.file_sid = ? ";
		List<Object[]> updateList = new ArrayList<Object[]>(); // 要修改状态的照片列表
		for(CompFileUpload file: list){
			Object[] updateValue = new Object[2];
			updateValue[0] = status;
			updateValue[1] = file.getFileSid();
			updateList.add(updateValue);
		}
		
		int[] updates = wantcompOperations.batchUpdate(sql, updateList);
		return updates.length;
	}
	
	/**
	 * 删除文件
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	public  void deleteFile(String filePath, String fileName) throws IOException{
		filePath = filePath.replace(REAL_PATH, MAP_PATH);
		File file = new File(filePath +fileName);
		if(file.exists()){
			FileUtils.forceDelete(file);
		}
		if(file.exists()){
			logger.info(filePath +fileName+", delete failed!");
		}
	}
	
	/**
	 * 备份文件
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	public  void backUpFile(String filePath, String fileName,String newFilePath) throws IOException{
		String fileMapPath = filePath.replace(REAL_PATH, MAP_PATH);
		String fileNewPath = filePath.replace(REAL_PATH, newFilePath);
		byte[] download = null;
		try {
			download = HttpUtil.download(fileMapPath+fileName, 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getFile(download, fileNewPath, fileName);
		
		logger.info(fileMapPath+fileName+" back up success!");
	}

	/** 
     * 根据byte数组，生成文件 
     */  
    public void getFile(byte[] bfile, String filePath,String fileName) {  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
        	logger.info("filePath: "+filePath);
            File dir = new File(filePath);  
            if(!dir.exists()){//判断文件目录是否存在
                dir.mkdirs();  
            }  
            file = new File(filePath+fileName);  
            logger.info("file :"+filePath+fileName);
            fos = new FileOutputStream(file);  
            fos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
	
	public int updateFilePath(List<CompFileUpload> delList, String newfilepath) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
