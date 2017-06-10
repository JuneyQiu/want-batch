package com.want.batch.job.temp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class SyncTaskPictureRenameFromHistory extends AbstractWantJob {

	private int syncDays = 180;
	private int onceSyncCount = 1000;
	private DateTime finalDate = new DateTime(2011, 12, 29, 0, 0, 0, 0);

	@Override
	public void execute() {
		
		if (syncDays < 0) {
			return;
		}

		DateTime beginDate = new DateTime()
			.minusDays(syncDays)
			.withHourOfDay(0)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0);

		syncTaskPictureSid181(finalDate);
		syncUpdateTaskPictureSids(beginDate, finalDate);

		String importSidsSql = new StringBuilder()
			.append("SELECT SID ")
			.append("FROM TASK_PICTURE_SID ")
			.append("WHERE SYNC IS NULL ")
			.append("  AND ROWNUM <= ? ")
			.toString();

		int syncCount = this.getArchiveJdbcOperations().queryForInt("SELECT count(1) FROM TASK_PICTURE_SID");
		int syncIndex = 0;
		int syncFailedCount = 0;
		
		String updateSyncStatusSql = "UPDATE TASK_PICTURE_SID SET SYNC = ? WHERE SID IN ('%s')";
		
		long startTime = System.currentTimeMillis();
		
		for (int i = 0; i < syncCount ; i += onceSyncCount) {
			
			// 查询本次需要同步的sid
			List<Map<String, Object>> importSidMaps = 
				this.getArchiveJdbcOperations().queryForList(importSidsSql, onceSyncCount);
			List<String> importSids = new ArrayList<String>();
			
			for (Map<String, Object> importSidMap : importSidMaps) {
				importSids.add(importSidMap.get("SID").toString());
			}
			
			// 查询本次需要同步的实际数据
			List<TaskImage> importTaskImages = this.getArchiveJdbcOperations().query(
				new StringBuilder()
					.append("SELECT * ")
					.append("FROM TASK_PICTURE ")
					.append(String.format("WHERE SID IN ('%s')", StringUtils.join(importSids, "','")))
					.toString(), 
				this.taskImageRowMapping());
			syncIndex += importTaskImages.size();
			logger.info(String.format("sync importTaskImages >>> %s/%s/%s", importTaskImages.size(), syncIndex, syncCount));
			
			// 同步实际数据
			logger.info("sync task pic ... ");
			List<String> imporFailedSids = new ArrayList<String>();
			for (TaskImage taskImage : importTaskImages) {
				try {
					this.getiCustomerJdbcOperations().update(
						new StringBuilder()
							.append("INSERT INTO TASK_PICTURE_RENAME ")
							.append("VALUES (:sid, :name, :description, :content, :status, :createUser, :createDate, ")
							.append("  :updateUser, :updateDate, :compression, :backup )")
							.toString(), 
						new BeanPropertySqlParameterSource(taskImage));
				}

				// 发生任何一场记录一笔失败，但不中断同步
				catch (Exception e) {
					logger.error(String.format("sync image[] error >>> ", taskImage.sid), e);
					imporFailedSids.add(taskImage.sid);
				}
			}
			logger.info(String.format("sync task pic over ... [s:%s],[f:%s]/[a:%s]", 
				(importSids.size() - imporFailedSids.size()), 
				imporFailedSids.size(), 
				importSids.size()));
			
			// 同步完成后更新记录同步过的状态
			int updateCount = this.getArchiveJdbcOperations().update(
				String.format(updateSyncStatusSql, StringUtils.join(importSids, "','")), "Y");

			// 有失败状况... 
			int updateFailedCount = 0;
			if (imporFailedSids.size() > 0) {
				updateFailedCount = this.getArchiveJdbcOperations().update(
						String.format(updateSyncStatusSql, StringUtils.join(importSids, "','")), "N");
			}
			
			logger.info(String.format("update sid status over ... [s:%s],[f:%s]/[a:%s]", 
					(updateCount - updateFailedCount), 
					updateFailedCount, 
					updateCount));
			
			syncFailedCount += updateFailedCount;
			
			logger.info(String.format("sync process >>> [s:%s],[f:%s]/[a:%s]", 
					syncIndex, 
					syncFailedCount, 
					syncCount));
			
			BigDecimal spentTime = BigDecimal.valueOf(System.currentTimeMillis() - startTime);
			BigDecimal avgTime = spentTime.divide(BigDecimal.valueOf(syncIndex), RoundingMode.HALF_UP);
			BigDecimal planTime = avgTime.multiply(BigDecimal.valueOf(syncCount));
			BigDecimal residueTime = planTime.subtract(spentTime);
			
			if (syncIndex % 10000 == 0) {
				
				this.getMailService()
					.to("song_wenlei@want-want.com", "Yang_Weilei@want-want.com")
					.subject(String.format("sync task pic process >>> [s:%s],[f:%s]/[a:%s]", 
							syncIndex, 
							syncFailedCount, 
							syncCount))
					.content(String.format(
						"已花费：%s / 剩余：%s / 预计共需要：%s", 
						formatTime(spentTime), formatTime(residueTime), formatTime(planTime)))
					.send();
			}
		}
		
		this.getMailService()
			.to("song_wenlei@want-want.com", "Yang_Weilei@want-want.com")
			.subject("sync task pic process >>> finished~~~")
			.content(String.format("共花费：%s", formatTime(BigDecimal.valueOf(System.currentTimeMillis() - startTime))))
			.send();
	}

	private String formatTime(BigDecimal time) {
		int timeSeconds = time.divide(BigDecimal.valueOf(1000)).intValue();
		int hours = timeSeconds / 3600;
		int minutes = (timeSeconds % 3600) / 60;
		int seconds = timeSeconds % 60;
		return String.format("%s:%s:%s", hours, minutes, seconds);
	}

	private void syncTaskPictureSid181(DateTime finalDate) {
		
		long startTime = System.currentTimeMillis();
		
		logger.info("delete from TASK_PICTURE_SID181 & 181.TASK_PICTURE_SID start ... ");
		this.getArchiveJdbcOperations().update("delete from TASK_PICTURE_SID181");
		this.getiCustomerJdbcOperations().update("delete from TASK_PICTURE_SID");
		logger.info("delete from TASK_PICTURE_SID181 & 181.TASK_PICTURE_SID end~ ");
		
		logger.info("update 181 temp TASK_PICTURE_SID start ... ");
		int syncTempTaskPictureSidCount = this.getiCustomerJdbcOperations().update(
			"INSERT INTO TASK_PICTURE_SID (SID, CREATE_DATE) " + 
			"SELECT SID, CREATE_DATE FROM TASK_PICTURE_RENAME WHERE CREATE_DATE < ?", finalDate.toDate());
		logger.info("update 181 temp TASK_PICTURE_SID end~ " + syncTempTaskPictureSidCount);
		
		int syncTaskPic181Count = 0;
		while(true) {
			List<Map<String, Object>> sidMaps = this.getiCustomerJdbcOperations().queryForList(
				"SELECT SID, CREATE_DATE FROM TASK_PICTURE_SID WHERE SYNC IS NULL AND ROWNUM < ?", onceSyncCount * 10);
			
			// 当查不到数据代表已经全部同步完成~
			if (sidMaps.isEmpty()) {
				break;
			}
			
			List<String> sids = new ArrayList<String>();

			logger.info("insert TASK_PICTURE_SID181 start ... ");
			int j = 0;
			for (Map<String, Object> sidMap : sidMaps) {
				j += this.getArchiveJdbcOperations().update(
					"INSERT INTO TASK_PICTURE_SID181(SID, CREATE_DATE) VALUES (?, ?) ", sidMap.get("SID"), sidMap.get("CREATE_DATE"));
				sids.add(sidMap.get("SID").toString());
				
				// 每 1000 笔更新一次
				if (sids.size() == 1000) {
					this.getiCustomerJdbcOperations().update(
						String.format("UPDATE TASK_PICTURE_SID SET SYNC = 'Y' WHERE SID IN ('%s')", StringUtils.join(sids, "','")));
					sids.clear();
				}
			}
			this.getiCustomerJdbcOperations().update(
					String.format("UPDATE TASK_PICTURE_SID SET SYNC = 'Y' WHERE SID IN ('%s')", StringUtils.join(sids, "','")));
			
			syncTaskPic181Count += j;
			logger.info(String.format("insert TASK_PICTURE_SID181 end~ [%s/%s/%s]", j, syncTaskPic181Count, syncTempTaskPictureSidCount));
			
		}
		
		this.getMailService()
			.to("song_wenlei@want-want.com")
			.subject("sync task pic sid finished~~~")
			.content(String.format("共花费：%s", formatTime(BigDecimal.valueOf(System.currentTimeMillis() - startTime))))
			.send();
	}

	private void syncUpdateTaskPictureSids(DateTime beginDate, DateTime finalDate) {
		
		logger.info("delete from TASK_PICTURE_SID start ... ");
		this.getArchiveJdbcOperations().update("delete from TASK_PICTURE_SID");
		logger.info("delete from TASK_PICTURE_SID end~");
		
		logger.info("insert TASK_PICTURE_SID start ... ");
		int insertCount = this.getArchiveJdbcOperations().update(
			new StringBuilder()
				.append("INSERT INTO TASK_PICTURE_SID (SID, CREATE_DATE) ")
				.append("SELECT SID, CREATE_DATE ")
				.append("FROM TASK_PICTURE ")
				.append("WHERE CREATE_DATE > ? ")
				.append("  AND CREATE_DATE < ? ")
				.append("  AND SID NOT IN (SELECT SID FROM TASK_PICTURE_SID181) ")
				.toString(), 
			beginDate.toDate(), finalDate.toDate());
		logger.info("insert TASK_PICTURE_SID end~" + insertCount);
	}

	public void setSyncDays(int syncDays) {
		this.syncDays = syncDays;
	}

	private RowMapper<SyncTaskPictureRenameFromHistory.TaskImage> taskImageRowMapping() {
		return new RowMapper<SyncTaskPictureRenameFromHistory.TaskImage>() {
			
			@Override
			public TaskImage mapRow(ResultSet rs, int rowNum) throws SQLException {
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
				return taskImage;
			}
		};
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
		private Integer compression;
		private String backup = "1";

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

		public Integer getCompression() {
			return compression;
		}

		public String getBackup() {
			return backup;
		}
	}
}
