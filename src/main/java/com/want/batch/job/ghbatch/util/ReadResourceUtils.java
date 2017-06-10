// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

// ~ Comments
// ==================================================

/**
 * 
 * 獲取系統資源文件.
 * 
 * <pre>
 * 歷史紀錄：
 * 2008-7-8 Timothy
 * 	新建檔案
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
 * 
 * @version $Rev$
 * 
 *          <p/>
 *          $Id$
 * 
 */
public final class ReadResourceUtils {

	// ~ Static Fields
	// ==================================================

	/**
	 * Timothy, 2009/4/9 <br>
	 * 获取 classpath 下的 project.properties
	 */
	private static Properties projectProperties = ReadResourceUtils.getProperties("project");

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	private ReadResourceUtils() {

	}

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2008-3-29 Timothy
	 * 	获取 classpath 下的 project.properties
	 * </pre>
	 * 
	 * @return
	 */
	public static Properties getProjectProperties() {

		return ReadResourceUtils.projectProperties;
	}

	/**
	 * <pre>
	 * 2008-3-29 Timothy
	 * 	获取 properties 资源
	 * </pre>
	 * 
	 * @param propertiesFileName
	 * @return
	 */
	public static Properties getProperties(String propertiesFileName) {

		Properties properties = new Properties();

		try {

			InputStream propertiesFileStream = ReadResourceUtils.class.getResourceAsStream(ReadResourceUtils
				.getClasspathPropertiesName(propertiesFileName));

			properties.load(propertiesFileStream);

			propertiesFileStream.close();
		}
		catch (IOException ioe) {

			throw new RuntimeException("Could not found properties file ：["
					+ ReadResourceUtils.getClasspathPropertiesName(propertiesFileName)
					+ "]", ioe);
		}

		return properties;
	}

	/**
	 * 
	 * <pre>
	 * 2008-3-29 Timothy
	 * 	获取 classpath properties name
	 * </pre>
	 * 
	 * @param propertiesFileName
	 * @return
	 */
	private static String getClasspathPropertiesName(String propertiesFileName) {

		String result = StringUtils.replace(propertiesFileName, ConstantString.OPPOSITE_OBLIQUE, ConstantString.OBLIQUE);

		// 前加 "/"，后加 ".properties" 後綴
		result = ConstantString.OBLIQUE + result + ConstantString.PROPERTIES_SUFFIX;

		// 消除 "/" 重複
		result = VSStringUtils.exceptSuccessiveRepeat(result, ConstantString.OBLIQUE);

		// 消除 ".properties" 重複
		result = VSStringUtils.exceptSuccessiveRepeat(result, ConstantString.PROPERTIES_SUFFIX.toString());

		return result;
	}

}
