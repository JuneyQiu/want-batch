package com.want.batch.job.stock_collect.util.Scheduler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipExcel {
	// public ZipExcel() {
	// }

	Logger logger = Logger.getLogger(ZipExcel.class);

	public void zipExcel(String mkdirPath) {

		final int BUFFER = 51200;

		String zipname = null;

		// String filename = null;
		try {

			BufferedInputStream origin = null;

			// String rarPath = AWFConfig._iworkConf.getDocumentPath();
			String rarPath = mkdirPath;
			if (!new File(rarPath).isDirectory()) {
				new File(rarPath).mkdir();
				new File(rarPath).mkdirs();
			} else {
				new File(rarPath).mkdirs();
			}
			zipname = rarPath.substring(0, rarPath.length() - 1) + ".zip";

			FileOutputStream dest = new FileOutputStream(zipname);
			// ����org.apache.tools.zip��ʵ��һ��ZIP�����ʵ�����
			org.apache.tools.zip.ZipOutputStream out = new org.apache.tools.zip.ZipOutputStream(
					new BufferedOutputStream(dest));

			byte data[] = new byte[BUFFER];
			File f = new File(mkdirPath);
			File files[] = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER);

				org.apache.tools.zip.ZipEntry entry = new org.apache.tools.zip.ZipEntry(
						files[i].getName());
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}

				origin.close();
			}

			out.close();

			// int zipMdFive = EncryptFileUtil.encryptFile(zipname);
		} catch (Exception e) {

			e.printStackTrace();
		}
		// return zipname;

	}

	private ZipOutputStream zipOut;
	private static int bufSize; // size of bytes
	private byte[] buf;
	private int readedBytes;

	public ZipExcel() {
		this(512);
	}

	public ZipExcel(int bufSize) {
		this.bufSize = bufSize;
		this.buf = new byte[this.bufSize];
	}

	public void setBufSize(int bufSize) {
		this.bufSize = bufSize;
	}

	public boolean deletefile(String delpath) throws FileNotFoundException,
			IOException {
		try {
			File file = new File(delpath);
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + "\\" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
					} else if (delfile.isDirectory()) {
						deletefile(delpath + "\\" + filelist[i]);
					}
				}
				file.delete();
			}
		} catch (FileNotFoundException e) {
			// Log.debug("deletefile() Exception:" + e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	public boolean deleteDirectory(String dir) {
		if (null == dir || "".equals(dir)) {
			logger.debug("目录不存在");
			return false;
		}
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		logger.debug("dirFile:" + dirFile.getAbsolutePath());
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			logger.debug("删除目录失败" + dir + "目录不存在！");
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			logger.debug("删除目录失败");
			return false;
		}

		// 删除当前目录
		if (dirFile.delete()) {
			// logger.info("删除目录"+dir+"成功！");
			return true;
		} else {
			// logger.info("删除目录"+dir+"失败！");
			return false;
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			// logger.info("删除单个文件"+fileName+"成功！");
			return true;
		} else {
			// logger.info("删除单个文件"+fileName+"失败！");
			return false;
		}
	}

}
