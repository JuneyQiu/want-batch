// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.util;

import org.apache.commons.lang.StringUtils;

// ~ Comments
// ==================================================

/**
 * 
 * String utils of VisualSoft.
 * 
 * <pre>
 * 歷史紀錄：
 * 2009/4/9 Timothy
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
 * @version $Rev$
 * 
 *          <p/>
 *          $Id$
 * 
 */
public final class VSStringUtils {

	// ~ Static Fields
	// ==================================================

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	private VSStringUtils() {

	}

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2009/4/9 Timothy
	 * 	去除連續的重複字串，最終每部份重複字串只保留1位字符
	 *  e.g. :
	 *    VSStringUtils.expectSuccessiveRepeat(null) = null;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;&quot;)   = &quot;&quot;;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;122333444455555&quot;) = &quot;12345&quot;;
	 * </pre>
	 * 
	 * @param string
	 * @return
	 */
	public static String exceptSuccessiveRepeat(String string) {

		if (StringUtils.isEmpty(string)) {
			return string;
		}

		char[] charArray = string.toCharArray();
		char moveChar = charArray[0];
		StringBuilder result = new StringBuilder().append(moveChar);

		for (char currentChar : string.toCharArray()) {

			if (moveChar != currentChar) {
				result.append(currentChar);
				moveChar = currentChar;
			}
		}

		return result.toString();
	}

	/**
	 * <pre>
	 * 2009/4/10 Timothy
	 * 	去除指定的連續的重複字串，最終每部份重複字串只保留1個
	 *  e.g. :
	 *    VSStringUtils.expectSuccessiveRepeat(null, &quot;not null&quot;) = null;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;&quot;, &quot;not null&quot;)   = &quot;&quot;;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;not null&quot;, null) = &quot;not null&quot;;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;not null&quot;, &quot;&quot;)   = &quot;not null&quot;;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;abbcccbba&quot;, &quot;c&quot;) = &quot;abbcbba&quot;;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;abcbcd&quot;, &quot;bc&quot;) = &quot;abcd&quot;;
	 * </pre>
	 * 
	 * @param string
	 * @param repeatString
	 * @return
	 */
	public static String exceptSuccessiveRepeat(final String string, String repeatString) {

		if (StringUtils.isEmpty(string) || StringUtils.isEmpty(repeatString)) {
			return string;
		}

		StringBuilder result = new StringBuilder();

		// 當 string 字串起始存在 repeatString時，在result加入一個repeatString
		if (StringUtils.left(string, repeatString.length()).equals(repeatString)) {

			result.append(repeatString);
		}

		// 使用 spilt 規則去除重複，但在最終可能會多添加一個repeatString。此部份在返回結果中判斷是否去除！
		for (String spiltString : StringUtils.splitByWholeSeparator(string, repeatString)) {

			result.append(spiltString).append(repeatString);
		}

		// 出去冗餘的 repeatString
		String resultString = StringUtils.removeEnd(result.toString(), repeatString);

		// 查看 string 字串是否以 repeatString 結束，若不以 string結束，截取最終的repeatString
		if (!StringUtils.right(string, repeatString.length()).equals(repeatString)) {

			resultString = StringUtils.removeEnd(resultString, repeatString);
		}

		return resultString;
	}

	/**
	 * <pre>
	 * 2009/4/10 Timothy
	 * 	去除指定的連續的重複字串，最終每部份重複字串只保留1個
	 *  e.g. :
	 *    VSStringUtils.expectSuccessiveRepeat(null, 'c') = null;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;&quot;, 'c')   = &quot;&quot;;
	 *    VSStringUtils.expectSuccessiveRepeat(&quot;abbcccbba&quot;, 'c') = &quot;abbcbba&quot;;
	 * </pre>
	 * 
	 * @param string
	 * @param repeatChar
	 * @return
	 */
	public static String exceptSuccessiveRepeat(final String string, char repeatChar) {

		return VSStringUtils.exceptSuccessiveRepeat(string, String.valueOf(repeatChar));
	}

}
