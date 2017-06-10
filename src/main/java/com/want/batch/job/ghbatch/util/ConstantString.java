// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.util;

// ~ Comments
// ==================================================

/**
 * 
 * 系統中常用的 string 固定字串. <br>
 * e.g. : <br>
 * ConstantString.EMPTY = "";<br>
 * ConstantString.POINT = ".";<br>
 * ConstantString.OBLIQUE = "/";<br>
 * ConstantString.BASELINE = "_";<br>
 * ConstantString.COLON = ":";<br>
 * ConstantString.COMMA = ",";<br>
 * ConstantString.SPACE = " ";<br>
 * ConstantString.EQUAL_SIGN = "=";<br>
 * ConstantString.LESS_SIGN = "<";<br>
 * ConstantString.GREATER_SIGN = ">";<br>
 * ConstantString.QUOTE = "'";<br>
 * ConstantString.SEMICOLON = ";";<br>
 * ConstantString.DASH = "-";<br>
 * ConstantString.OPPOSITE_OBLIQUE = "\";<br>
 * ConstantString.getInstance().getEmpty = "";<br>
 * ConstantString.getInstance().getPoint = ".";<br>
 * ConstantString.getInstance().getOblique = "/";<br>
 * ConstantString.getInstance().getBaseline = "_";<br>
 * ConstantString.getInstance().getColon = ":";<br>
 * ConstantString.getInstance().getComma = ",";<br>
 * ConstantString.getInstance().getSpace = " ";<br>
 * ConstantString.getInstance().getEqualSign = "=";<br>
 * ConstantString.getInstance().getLessSign = "<";<br>
 * ConstantString.getInstance().getGreaterSign = ">";<br>
 * ConstantString.getInstance().getQuote = "'";<br>
 * ConstantString.getInstance().getDoubleQuotes = """;<br>
 * ConstantString.getInstance().getSemicolon = ";";<br>
 * ConstantString.getInstance().getDash = "-";<br>
 * ConstantString.getInstance().getOppositeOblique = "\";<br>
 * 
 * <pre>
 * 歷史紀錄：
 * 2009/4/16 Timothy
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
public final class ConstantString {

	// ~ Static Fields
	// ==================================================

	private static final ConstantString instance = new ConstantString();

	/**
	 * "". Timothy, 2009/4/16
	 */
	public static final String EMPTY = "";

	/**
	 * ".". Timothy, 2009/4/16
	 */
	public static final String POINT = ".";

	/**
	 * "/". Timothy, 2009/4/16
	 */
	public static final String OBLIQUE = "/";

	/**
	 * "_". Timothy, 2009/4/21
	 */
	public static final String BASELINE = "_";

	/**
	 * ":". Timothy, 2009/4/23
	 */
	public static final String COLON = ":";

	/**
	 * ",". Timothy, 2009/5/6
	 */
	public static final String COMMA = ",";

	/**
	 * " ". Timothy, 2009/5/6
	 */
	public static final String SPACE = " ";

	/**
	 * "=". Timothy, 2009/5/6
	 */
	public static final String EQUAL_SIGN = "=";

/**
	 * "<". Timothy, 2009/5/6
	 */
	public static final String LESS_SIGN = "<";

	/**
	 * ">". Timothy, 2009/5/6
	 */
	public static final String GREATER_SIGN = ">";

	/**
	 * "'". Timothy, 2009/5/6
	 */
	public static final String QUOTE = "'";

	/**
	 * """. Timothy, 2009/5/6
	 */
	public static final String DOUBLE_QUOTES = "\"";

	/**
	 * ";". Timothy, 2009/5/6
	 */
	public static final String SEMICOLON = ";";

	/**
	 * "-". Timothy, 2009/5/6
	 */
	public static final String DASH = "-";

	/**
	 * "\". Timothy, 2009/5/14
	 */
	public static final String OPPOSITE_OBLIQUE = "\\";

	/**
	 * "\". Timothy, 2010/2/3
	 */
	public static final String PROPERTIES_SUFFIX = ".properties";

	// number (0~9)
	public static final String ZERO = "0";
	public static final String ONE = "1";
	public static final String TWO = "2";
	public static final String THREE = "3";
	public static final String FOUR = "4";
	public static final String FIVE = "5";
	public static final String SIX = "6";
	public static final String SEVEN = "7";
	public static final String EIGHT = "8";
	public static final String NINE = "9";

	// ~ Fields
	// ==================================================

	// ~ Constructors
	// ==================================================

	private ConstantString() {

	}

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2009/4/16 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public static final ConstantString getInstance() {

		return ConstantString.instance;
	}

	/**
	 * "".
	 * 
	 * <pre>
	 * 2009/4/16 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getEmpty() {

		return ConstantString.EMPTY;
	}

	/**
	 * ".".
	 * 
	 * <pre>
	 * 2009/4/16 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getPoint() {

		return ConstantString.POINT;
	}

	/**
	 * "/".
	 * 
	 * <pre>
	 * 2009/4/16 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getOblique() {

		return ConstantString.OBLIQUE;
	}

	/**
	 * "_".
	 * 
	 * <pre>
	 * 2009/4/21 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getBaseline() {

		return ConstantString.BASELINE;
	}

	/**
	 * ":".
	 * 
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getColon() {

		return ConstantString.COLON;
	}

	/**
	 * ",".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getComma() {

		return ConstantString.COMMA;
	}

	/**
	 * " ".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getSpace() {

		return ConstantString.SPACE;
	}

	/**
	 * "=".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getEqualSign() {

		return ConstantString.EQUAL_SIGN;
	}

/**
	 * "<".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getLessSign() {

		return ConstantString.LESS_SIGN;
	}

	/**
	 * ">".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getGreaterSign() {

		return ConstantString.GREATER_SIGN;
	}

	/**
	 * "'".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getQuote() {

		return ConstantString.QUOTE;
	}

	/**
	 * """.
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getDoubleQuotes() {

		return ConstantString.DOUBLE_QUOTES;
	}

	/**
	 * ";".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getSemicolon() {

		return ConstantString.SEMICOLON;
	}

	/**
	 * "-".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getDash() {

		return ConstantString.DASH;
	}

	/**
	 * "\".
	 * 
	 * <pre>
	 * 2009/5/6 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getOppositeOblique() {

		return ConstantString.OPPOSITE_OBLIQUE;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getPropertiesSuffix() {

		return ConstantString.PROPERTIES_SUFFIX;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getZero() {

		return ConstantString.ZERO;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getOne() {

		return ConstantString.ONE;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getTwo() {

		return ConstantString.TWO;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getThree() {

		return ConstantString.THREE;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getFour() {

		return ConstantString.FOUR;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getFive() {

		return ConstantString.FIVE;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getSix() {

		return ConstantString.SIX;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getSeven() {

		return ConstantString.SEVEN;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getEight() {

		return ConstantString.EIGHT;
	}

	/**
	 * <pre>
	 * 2009/4/23 Timothy
	 * </pre>
	 * 
	 * @return
	 */
	public String getNine() {

		return ConstantString.NINE;
	}

}
