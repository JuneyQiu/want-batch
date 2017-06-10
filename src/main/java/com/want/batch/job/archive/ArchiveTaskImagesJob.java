/**
 * 
 */
package com.want.batch.job.archive;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;
import com.want.component.compression.ImageCompression;

/**
 * @author Timothy
 */
@Component
public class ArchiveTaskImagesJob extends AbstractWantJob {

	private static final String BACKUP = "1";

	@Autowired
	private ImageCompression imageCompression;

	private int archiveOnceCount = 20;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.want.batch.job.WantJob#execute()
	 */
	@Override
	public void execute() {
		// 同步数据
//		archiveNewPictures();

		 archivePictures();

		// deletePictures();
	}

	private void archivePictures() {

		int archiveCount = this.getiCustomerJdbcOperations().queryForInt(
				"SELECT COUNT(1) FROM TASK_PICTURE WHERE BACKUP IS NULL");

		logger.info("archive count : " + archiveCount);

		// 任何一次发生异常不影响下一次压缩
		for (int i = 0; i < archiveCount; i += archiveOnceCount) {
			try {
				archiveOnceTaskPictures();
			} catch (Exception e) {
				logger.error("archive onec error >>> ", e);
			}
		}
	}

	private void archiveOnceTaskPictures() {

		List<ArchiveTaskImagesJob.TaskImage> archiveTaskImages = this
				.getiCustomerJdbcOperations()
				.query("SELECT * FROM TASK_PICTURE WHERE BACKUP IS NULL AND ROWNUM <= ? ORDER BY CREATE_DATE",
						taskImageRowMapping(), archiveOnceCount);

		List<String> taskPicSids = new ArrayList<String>();
		List<SqlParameterSource> hisTaskImages = new ArrayList<SqlParameterSource>();

		// if 图片 > 100k, 压缩图片
		for (TaskImage taskImage : archiveTaskImages) {
			if (checkImage(taskImage.name)) {
				if (isJpg(taskImage.name) && taskImage.content.length > 100000) {
					try {
						taskImage.content = imageCompression
								.compress(taskImage.content);
						taskPicSids.add(taskImage.sid);
						hisTaskImages.add(new BeanPropertySqlParameterSource(
								taskImage));
						logger.info(String.format("Compress >>> [%s]-[%s]",
								taskImage.getSid(), taskImage.getName()));
					} catch (Exception e) {
						logger.error("JPG Pic error ... " + taskImage.name, e);
						deleteErrorPic(taskImage);
					}
				} else {
					taskPicSids.add(taskImage.sid);
					hisTaskImages.add(new BeanPropertySqlParameterSource(
							taskImage));
					logger.info(String.format("didn't compress >>> [%s]-[%s]",
							taskImage.sid, taskImage.name));
				}
			}
			// 验证是否是图片，并对不是图片的数据做删除
			else {
				deleteErrorPic(taskImage);
			}
		}

		archivePictures(hisTaskImages);

		// 将压缩后的图片更新到现有数据库，并记录已归档
		int[] updateCounts = this
				.getiCustomerJdbcOperations()
				.batchUpdate(
						String.format(
								"UPDATE TASK_PICTURE SET BACKUP = '%s', CONTENT = :content WHERE SID = :sid",
								BACKUP),
						hisTaskImages.toArray(new SqlParameterSource[] {}));
		logger.info("update source images >>> " + updateCounts.length);
		// syncPictures(taskPicSids);
	}


	/**
	 * 将归档图片塞入历史资料库
	 * 
	 * @param hisTaskImages
	 */
	private void archivePictures(List<SqlParameterSource> hisTaskImages) {

		int insertHisCounts;

		try {
			insertHisCounts = insertPicturesToHis(hisTaskImages, false);
		}

		// 当发生异常时，尝试先删除后新增的方式在进行一次插入
		catch (Exception e) {
			insertHisCounts = insertPicturesToHis(hisTaskImages, true);
		}

		logger.info("archive images >>> " + insertHisCounts);
	}

	/**
	 * 
	 * 将数据插入历史资料库
	 * 
	 * @param hisTaskImages
	 *            需要插入的资料
	 * @param delete
	 *            看是否需要先删除，后新增
	 * @return
	 */
	private int insertPicturesToHis(List<SqlParameterSource> hisTaskImages,
			boolean delete) {

		if (delete) {
			int deleteRepeatPictureCount = this.getArchiveJdbcOperations()
					.batchUpdate("DELETE FROM TASK_PICTURE WHERE SID = :sid",
							hisTaskImages.toArray(new SqlParameterSource[] {})).length;
			logger.info("Delete repeat pics from hisdb >>> "
					+ deleteRepeatPictureCount);
		}

		return this
				.getArchiveJdbcOperations()
				.batchUpdate(
						"INSERT INTO TASK_PICTURE "
								+ "VALUES (:sid, :name, :description, :content, :status, :createUser, :createDate, :updateUser, :updateDate,:compression,:backup)",
						hisTaskImages.toArray(new SqlParameterSource[] {})).length;
	}

	private void deleteErrorPic(TaskImage taskImage) {
		this.getiCustomerJdbcOperations().update(
				"DELETE FROM TASK_PICTURE WHERE SID = ? ", taskImage.sid);
		logger.info(String.format("delete not pic >>> [%s]-[%s]",
				taskImage.sid, taskImage.name));
	}

	private boolean isJpg(String name) {
		return StringUtils.endsWithIgnoreCase(name, ".jpeg")
				|| StringUtils.endsWithIgnoreCase(name, ".jpg");
	}

	private RowMapper<ArchiveTaskImagesJob.TaskImage> taskImageRowMapping() {
		return new RowMapper<ArchiveTaskImagesJob.TaskImage>() {

			@Override
			public TaskImage mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				TaskImage taskImage = new TaskImage();
				taskImage.sid = rs.getString("SID");
				taskImage.name = rs.getString("NAME");
				taskImage.description = rs.getString("DESCRIPTION");
				taskImage.content = rs.getBytes("CONTENT");
				taskImage.status = rs.getString("STATUS");
				taskImage.createUser = rs.getString("CREATE_USER");
				taskImage.createDate = rs.getTimestamp("CREATE_DATE");
				taskImage.updateUser = rs.getString("UPDATE_USER");
				taskImage.updateDate = rs.getTimestamp("UPDATE_DATE");
				// 20121214 add by TaoJie
				taskImage.compression = rs.getInt("COMPRESSION");
				taskImage.backup = rs.getString("BACKUP");
				// 20121214 add by TaoJie
				return taskImage;
			}
		};
	}

	private boolean checkImage(String name) {
		return StringUtils.endsWithIgnoreCase(name, ".jpeg")
				|| StringUtils.endsWithIgnoreCase(name, ".jpg")
				|| StringUtils.endsWithIgnoreCase(name, ".gif")
				|| StringUtils.endsWithIgnoreCase(name, ".png")
				|| StringUtils.endsWithIgnoreCase(name, ".bmp")
				|| StringUtils.endsWithIgnoreCase(name, ".tif")
				|| StringUtils.endsWithIgnoreCase(name, ".tiff");
	}

	@SuppressWarnings("unused")
	private class TaskImage {
		private String sid;
		private String name;
		private String description;
		private byte[] content;
		private String status;
		private String createUser;
		private Date createDate;
		private String updateUser;
		private Date updateDate;
		// 20121214 add by TaoJie
		private int compression;
		private String backup;

		public int getCompression() {
			return compression;
		}

		public void setCompression(int compression) {
			this.compression = compression;
		}

		public String getBackup() {
			return backup;
		}

		public void setBackup(String backup) {
			this.backup = backup;
		}

		// 20121214 add by TaoJie

		public String getSid() {
			return sid;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public byte[] getContent() {
			return content;
		}

		public String getStatus() {
			return status;
		}

		public String getCreateUser() {
			return createUser;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public String getUpdateUser() {
			return updateUser;
		}

		public Date getUpdateDate() {
			return updateDate;
		}
	}

	private String yearMonth = "2013";

	String message = "移动TASK_PICTURE" + yearMonth + "数据到TASK_PICTURE_NEW异常";
}
