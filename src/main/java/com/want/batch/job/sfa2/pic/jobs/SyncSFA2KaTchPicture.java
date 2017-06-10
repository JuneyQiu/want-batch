package com.want.batch.job.sfa2.pic.jobs;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa2.pic.pojo.SdActualPicture;
import com.want.batch.job.sfa2.pic.pojo.TaskPicture;
import com.want.batch.job.sfa2.pic.pojo.VisitFileUpload;
import com.want.batch.job.utils.ProjectConfig;

@Component
public class SyncSFA2KaTchPicture extends AbstractWantJob {
	// private static final Properties PROJECT_PROPERTIES = ReadResourceUtils
	// .getProjectProperties();
	private String stringSequencePrefix = "W";
	private int primaryKeyLength = 19;
	private char sequenceLeftPadString = '0';
	private static final String MAP_PATH = ProjectConfig.getInstance().getString("i.map.path");
	private static final String REAL_PATH = ProjectConfig.getInstance().getString("i.real.path");

	private static final String OLD_MAP_PATH = ProjectConfig.getInstance().getString("i.map.path.old");
	private static final String OLD_REAL_PATH = ProjectConfig.getInstance().getString("i.real.path.old");
	@Override
	public void execute() throws Exception {
		// 得到需要同步照片的特陈资料
		List<Map<String, Object>> actualTch = querySFA2ActualTch();

		// 得到这些特陈对应的sfa的拜访照片
		List<VisitFileUpload> files = queryTchVisitPhoto(actualTch);

		// 将照片从服务器读取后保存在数据库中TASK_PICTURE
		downLoadTaskPicture(files);

	}

	/**
	 * 获取SFA2同步回业务网站且无照片的特陈资料
	 * 
	 * @return
	 */
	public List<Map<String, Object>> querySFA2ActualTch() {
		StringBuffer sql = new StringBuffer();
		sql.append("select a.SID,b.DISPLAY_ID,a.STORE_ID,a.DATA_SOURCE from icustomer.KA_SD_ACTUAL_DETAIL a  ");
		sql.append("inner join icustomer.KA_SD_PLAN_DETAIL b on a.PLAN_DETAIL_SID = b.SID  ");
		sql.append("inner join icustomer.KA_SD_PLAN c on b.SD_NO = c.SD_NO  ");
		sql.append("left join icustomer.KA_ACTUAL_PICTURE d on a.sid = d.ACTUAL_REF_SID  ");
		sql.append("WHERE  c.year_month = ? and d.ACTUAL_REF_SID is null");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		String today = formatter.format(new Date());
		List<Map<String, Object>> list = this.getiCustomerJdbcOperations()
				.queryForList(sql.toString(), new Object[] { today });
		logger.info("querySFA2KaActualTch  list size: " + list.size());
		return list;
	}

	// /**
	// * 根据文件编码获得回传文件的详细信息
	// * @param fileSid
	// * @return
	// */
	// public VisitFileUpload queryFileUploadBySid(long fileSid) {
	// VisitFileUpload photo = null;
	// StringBuffer sql = new StringBuffer();
	// sql.append("SELECT FILE_SID,FILE_NAME,ORI_FILE_NAME,FILE_TYPE,FILE_SIZE,FILE_PATH,FILE_STATUS  ");
	// sql.append("FROM FILE_UPLOAD_TBL WHERE FILE_SID =? ");
	// List<Map<String, Object>> list = this.getWantcompOperations()
	// .queryForList(sql.toString(), new Object[] { fileSid });
	// for (Map<String, Object> file : list) {
	// photo = new VisitFileUpload();
	// photo.setOriFileName(file.get("ORI_FILE_NAME").toString());
	// photo.setPath(file.get("FILE_PATH").toString());
	// photo.setFileName(file.get("FILE_NAME").toString());
	// }
	// return photo;
	// }

	/**
	 * 获取特陈资料对应的回传照片
	 * 
	 * @param sdActualList
	 * @return
	 * @throws ParseException
	 */
	public List<VisitFileUpload> queryTchVisitPhoto(
			List<Map<String, Object>> sdActualList) throws ParseException {
		List<VisitFileUpload> fileList = new ArrayList<VisitFileUpload>();
		StringBuffer sql = new StringBuffer();
		sql.append("select FILE_SID,FILE_NAME,ORI_FILE_NAME,FILE_TYPE,FILE_SIZE,FILE_PATH,FILE_STATUS,a.UPDATOR,a.UPDATE_DATE ");
		sql.append("from SFA2.VISIT_PHOTO a inner join sfa2.KA_SD_ACTUAL_DETAIL b on a.ACTUAL_DISPLAY_SID = b.SID  ");
		sql.append("inner join wantcomp.FILE_UPLOAD_TBL c on a.PHOTO_SID = c.FILE_SID where b.STORE_DISPLAY_SID = ? ");
		// 遍历每笔特陈资料
		for (Map<String, Object> sd : sdActualList) {
			BigDecimal storeDisplaySid = new BigDecimal(sd.get(
					"STORE_DISPLAY_SID").toString());
			BigDecimal actualDisplaySid = new BigDecimal(sd.get("SID")
					.toString());
			// 获取该笔特陈对应的拜访照片
			List<Map<String, Object>> photoList = this.getSfa2JdbcOperations()
					.queryForList(sql.toString(),
							new Object[] { storeDisplaySid });
			logger.info("storeDisplaySid: " + storeDisplaySid
					+ " has photo size: " + photoList.size());
			for (Map<String, Object> photo : photoList) {
				// 根据拜访照片的sid查询该照片的详细信息
				VisitFileUpload visit = new VisitFileUpload();
				visit.setStoreDisplaySid(storeDisplaySid);
				visit.setActualDisplaySid(actualDisplaySid);
				visit.setOriFileName(photo.get("ORI_FILE_NAME").toString());
				visit.setPath(photo.get("FILE_PATH").toString());
				visit.setFileName(photo.get("FILE_NAME").toString());
				visit.setCreateUser(photo.get("UPDATOR").toString());
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				visit.setCreateDate(format.parse(photo.get("UPDATE_DATE")
						.toString()));
				fileList.add(visit);
			}
		}

		logger.info("queryTchVisitPhoto fileList size: " + fileList.size());
		return fileList;
	}

	/**
	 * 从服务器下载文件并保存至DB
	 * 
	 * @param list
	 * @throws Exception
	 */
	public void downLoadTaskPicture(List<VisitFileUpload> list)
			throws Exception {
		for (VisitFileUpload file : list) {
			String filePath = file.getPath();
			String filepath1 = filePath.substring(0, filePath.length() - 14);
			String filepath2 = filePath.substring(filePath.length() - 14, filePath.length());
			if (REAL_PATH.equals(filepath1)) {
				filePath = MAP_PATH + filepath2;
			} else if (OLD_REAL_PATH.equals(filepath1)) {
				filePath = OLD_MAP_PATH + filepath2;
			}

			filePath = filePath	+ file.getFileName();
			// 构造TaskPicture ,并下载文件
			TaskPicture pic = new TaskPicture();
			pic.setSid(getPrimaryKey());
			logger.info("storeDisplaySid：" + file.getStoreDisplaySid()+ ", file path: " + filePath + " download start...");

			// 下载照片，如有异常catch，并记录异常编码，然后继续执行
			byte[] filebyte = null;
			try {
				filebyte = FileUtils.readFileToByteArray(new File(filePath));
			} catch (Exception e) {
				logger.error("storeDisplaySid：" + file.getStoreDisplaySid()	+ ", file path: " + filePath + " download failed..."
						+ e.getMessage());
				continue;
			}
			// 如果照片不存在，则继续进行循环
			if (filebyte.length <= 0) {
				logger.error("storeDisplaySid：" + file.getStoreDisplaySid()	+ ", file path: " + filePath + " download length..."+ filebyte.length);
				continue;
			}
			pic.setContent(filebyte);
			logger.info("file path: " + file.getPath()	+ " download end, and content size: "+ pic.getContent().length);
			pic.setName(file.getOriFileName());
			pic.setCreateUser(file.getCreateUser());
			pic.setCreateDate(file.getCreateDate());
			pic.setStatus("0");
			int count = insertPictures(new BeanPropertySqlParameterSource(pic));
			// 如果照片保存成功，则将特陈和照片的关系建立
			if (count > 0) {
				SdActualPicture sdPic = new SdActualPicture();
				sdPic.setActualDisplaySid(file.getActualDisplaySid()
						.longValue());
				sdPic.setPictureSid(pic.getSid());
				int actualCount = insertSDActualPicture(new BeanPropertySqlParameterSource(
						sdPic));
				logger.info("KaSdActualPicture : ActualDisplaySid("+ sdPic.getActualDisplaySid() + "), PictureSid("+ sdPic.getPictureSid() + ") insert success");
				if (actualCount <= 0) {
					// 如果特好照片关系表未正确保存，则删除taskpicture中的照片
					deleteErrorPic(pic);
				}
			}

		}

		// 保存至DB
	}

	/**
	 * 
	 * 将数据插入新资料库
	 * 
	 * @param hisTaskImages
	 *            需要插入的资料
	 * @return
	 */
	public int insertPictures(SqlParameterSource taskImages) {
		return this
				.getiCustomerJdbcOperations()
				.update("INSERT INTO TASK_PICTURE(SID, NAME, CONTENT, STATUS, CREATE_USER, CREATE_DATE) "
						+ "VALUES (:sid, :name, :content, :status, :createUser, :createDate)",
						taskImages);
	}

	public int insertSDActualPicture(SqlParameterSource sdPic) {
		return this.getiCustomerJdbcOperations().update(
				"INSERT INTO SD_ACTUAL_PICTURE  "
						+ "VALUES (:actualDisplaySid, :pictureSid)", sdPic);
	}

	/**
	 * 获取TASK_PICTURE 的主键
	 * 产生规则：总共19位，以W开头，拼接上TASK_PICTURE_SEQ.nextval，不够19位数字的话，从W后面开始补0
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getPrimaryKey() throws Exception {
		int sequenceLeftPadLength = this.primaryKeyLength
				- this.stringSequencePrefix.length();

		if (sequenceLeftPadLength < 0) {
			throw new Exception(new IllegalArgumentException(
					"字串序列的前缀不可大于PK的长度！"));
		}
		String primaryKey = this.stringSequencePrefix
				+ StringUtils.leftPad(getPrimaryKeySequenceNextValue()
						.toString(), sequenceLeftPadLength,
						this.sequenceLeftPadString);
		return primaryKey;
	}

	private Long getPrimaryKeySequenceNextValue() {
		String sql = "select TASK_PICTURE_SEQ.nextval from dual";

		return this.getiCustomerJdbcOperations().queryForLong(sql);
	}

	private void deleteErrorPic(TaskPicture pic) {
		this.getiCustomerJdbcOperations().update(
				"DELETE FROM TASK_PICTURE WHERE SID = ? ", pic.getSid());
		logger.info(String.format("delete not pic >>> [%s]-[%s]", pic.getSid(),
				pic.getName()));
	}

	private String getRealPath() {
		return "/eppput";
	}

	private String getRealUrl() {
		return "http://i.want-want.com/img";
	}
}
