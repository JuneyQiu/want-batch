package com.want.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTrace {

	public static String get(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		} catch (Exception e2) {
			return e.getMessage();
		}
	}

}
