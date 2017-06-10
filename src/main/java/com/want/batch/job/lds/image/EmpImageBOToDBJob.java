package com.want.batch.job.lds.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.AbstractWantJob;

@Component
public class EmpImageBOToDBJob extends AbstractWantJob {

	private static final Log logger = LogFactory
			.getLog(EmpImageBOToDBJob.class);

	public static String remoteImageBOPath = "\\\\10.0.0.26\\empimages";
//	public static String localImageBOPath = "D:\\20150204";
	public static final int IMAGE_MAX_SIZE = 60000;
	@Autowired
	private DataSource portalDataSource;// 注入

	@Override
	public void execute() throws Exception {

		// 远程抓取并压缩
		// itdb.compressImageBO(remoteImageBOPath, localImageBOPath);
		//
		File[] files = new File(remoteImageBOPath).listFiles();
		Connection conn = portalDataSource.getConnection();

		Map<String, ImageBO> dbEmployeeImages = getDBEmployeeImages(conn);
		List<String> dbEmployee = getDBEmployees(conn);
		dbEmployee.add("00000000");
		
		ImageBO idb = null;
		InputStream inputStream = null;
		byte[] buf = null;

		for (int i = 0; i < files.length; i++) {
			idb = new ImageBO();
			idb.setImg_emp_id(files[i].getName().split("\\.")[0]);

			if (dbEmployee.contains(idb.getImg_emp_id())) {
				
				boolean a = idb.getImg_emp_id().compareTo("00000000") == 0;
				ImageBO db = dbEmployeeImages.get(idb.getImg_emp_id());

				if (db == null)
					idb.setModifyType(ImageBO.INSERT);
				else {
					long dbLastModified = Long.parseLong(db
							.getImg_modify_time());
					if (dbLastModified < files[i].lastModified()) {
						idb.setImg_modify_time(Long.toString(files[i]
								.lastModified()));
						idb.setModifyType(ImageBO.UPDATE);
					}
				}

				if (idb.getModifyType() == ImageBO.INSERT
						|| idb.getModifyType() == ImageBO.UPDATE) {

					try {
						inputStream = new FileInputStream(files[i]);
						buf = new byte[inputStream.available()];
						inputStream.read(buf);
						idb.setImg_content(buf);
						// idb.setImg_content(itdb.getImageBOByRadix(files[i],
						// 8));
						idb.setImg_modify_time(String.valueOf(new Date()
								.getTime()));
						idb.setImg_modify_emp("system");
						setImageBOToDatabaseBatch(conn, idb);
					} catch (Exception e) {
						logger.error("fail to insert / update "
								+ idb.getImg_emp_id() + " for "
								+ e.getMessage());
					}

				}
			}
		}
		
		compressImages(conn);
		

		conn.close();
		
	}

	public void setImageBOToDatabaseBatch(Connection conn, ImageBO idb)
			throws SQLException {

		PreparedStatement pstmt = null;

		if (idb.getModifyType() == ImageBO.INSERT) {
			pstmt = conn
					.prepareStatement("INSERT INTO HRORG.EMG_IMAGE (IMG_ID,IMG_EMP_ID,IMG_CONTENT,IMG_MODIFY_TIME,IMG_MODIFY_EMP) VALUES (HRORG.IMG_ID_SEQ.NEXTVAL,?,?,?,?)");
			pstmt.setString(1, idb.getImg_emp_id());
			pstmt.setBytes(2, idb.getImg_content());
			pstmt.setString(3, idb.getImg_modify_time());
			pstmt.setString(4, idb.getImg_modify_emp());
			pstmt.execute();
			pstmt.close();
			logger.info("inserted: " + idb.getImg_emp_id());
		} else if (idb.getModifyType() == ImageBO.UPDATE) {
			pstmt = conn
					.prepareStatement("UPDATE HRORG.EMG_IMAGE SET IMG_CONTENT=?,IMG_MODIFY_TIME=?,IMG_MODIFY_EMP=? WHERE IMG_EMP_ID=?");
			pstmt.setBytes(1, idb.getImg_content());
			pstmt.setString(2, idb.getImg_modify_time());
			pstmt.setString(3, idb.getImg_modify_emp());
			pstmt.setString(4, idb.getImg_emp_id());
			pstmt.execute();
			pstmt.close();
			logger.info("updated: " + idb.getImg_emp_id());
		}

	}

	protected Map<String, ImageBO> getDBEmployeeImages(Connection conn)
			throws SQLException {
		Map<String, ImageBO> employees = new HashMap<String, ImageBO>();
		PreparedStatement pstmt = conn
				.prepareStatement("SELECT IMG_EMP_ID, IMG_MODIFY_TIME FROM HRORG.EMG_IMAGE");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
			employees.put(
					rs.getString("IMG_EMP_ID"),
					new ImageBO(rs.getString("IMG_EMP_ID"), rs
							.getString("IMG_MODIFY_TIME")));

		pstmt.close();
		return employees;
	}

	private void compressImages(Connection conn)
			throws SQLException {
		Map<String, ImageBO> employees = new HashMap<String, ImageBO>();
		List<String> largeImageIds = new ArrayList<String>();
		PreparedStatement pstmt = conn
				.prepareStatement("SELECT IMG_EMP_ID, LENGTH(IMG_CONTENT) AS LEN FROM HRORG.EMG_IMAGE");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String id = rs.getString("IMG_EMP_ID");
			int size = rs.getInt("LEN");
			logger.debug(id + ": " + size);
			if (size > IMAGE_MAX_SIZE) 
				largeImageIds.add(id);
			/*
			employees.put(
					rs.getString("IMG_EMP_ID"),
					new ImageBO(rs.getString("IMG_EMP_ID"), rs
							.getString("IMG_MODIFY_TIME"), content));
			*/
		}
		pstmt.close();
		
		PreparedStatement getPstmt = conn
				.prepareStatement("SELECT IMG_CONTENT FROM HRORG.EMG_IMAGE WHERE IMG_EMP_ID=?");

		PreparedStatement setPstmt = conn
				.prepareStatement("UPDATE HRORG.EMG_IMAGE SET IMG_CONTENT=?  WHERE IMG_EMP_ID=?");

		for (String id: largeImageIds) {
			getPstmt.setString(1, id);
			rs = getPstmt.executeQuery();
			if (rs.next()) {
				InputStream in = rs.getBinaryStream("IMG_CONTENT");
				try {
					ImgCompress imgCom = new ImgCompress(in);
					byte[] content = imgCom.resizeFix(400, 400);
					in.close();
					rs.close();
					
					logger.info(id + " compressing");
					setPstmt.setBytes(1, content);
					setPstmt.setString(2, id);
					setPstmt.executeUpdate();
					logger.info(id + " compressed as " + content.length);
				} catch (IOException e) {
					logger.error(e.getMessage() + " for " + id);
				}
			}
		}

	}

	protected List<String> getDBEmployees(Connection conn) throws SQLException {
		List<String> employees = new ArrayList<String>();
		PreparedStatement pstmt = conn
				.prepareStatement("SELECT EMP_ID FROM HRORG.EMP");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
			employees.add(rs.getString("EMP_ID"));

		pstmt.close();
		return employees;
	}

	public String getImageBOByRadix(File imageFile, int radix) {

		try {
			FileInputStream fis = new FileInputStream(imageFile);
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();

			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = fis.read(buff)) != -1) {
				bos.write(buff, 0, len);
			}
			// 得到图片的字节数组
			byte[] result = bos.toByteArray();

			String ss = new BigInteger(1, result).toString(radix);
			System.out.println("++++" + ss.length());

			/*
			 * 将十六进制串保存到txt文件中
			 */

			// PrintWriter pw = new PrintWriter(new
			// FileWriter("d://today.txt"));
			// pw.println(ss);
			// pw.close();
			return ss;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * public void compressImage(String fromPath, String toPath) { File folder =
	 * new File(fromPath); File[] fileList = folder.listFiles(); String bName =
	 * null; for (int i = 0; i < fileList.length; i++) { try { bName =
	 * fileList[i].getName(); if ((bName.length() == 12 || bName.length() == 13)
	 * && (StringUtils.endsWithIgnoreCase(bName, ".jpeg") || StringUtils
	 * .endsWithIgnoreCase(bName, ".jpg"))) { ImgCompress imgCom = new
	 * ImgCompress(fileList[i]); if (fileList[i].length() > 100000) { //
	 * System.out.println(bName + "需要压缩:" + // fileList[i].length());
	 * imgCom.resizeFix(600, 400, toPath + File.separator + bName); } else { //
	 * System.out.println(bName + "无需压缩:" + // fileList[i].length());
	 * imgCom.fileChannelCopy(fileList[i], new File(toPath + File.separator +
	 * bName)); } } } catch (Exception e) { // TODO Auto-generated catch block
	 * System.out.println(bName); System.out.println(e.getMessage()); } } }
	 */
}
