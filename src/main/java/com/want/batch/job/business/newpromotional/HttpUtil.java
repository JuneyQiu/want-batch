package com.want.batch.job.business.newpromotional;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @author 
 * @version 1.0
 */
public class HttpUtil {

	//private static final Log logger = LogFactory.getLog(HttpUtil.class);

	private static final String HTTP_METHOD_POST = "POST";

	private static final String defaultContentEncoding = "UTF-8";

	private static final String HTTP_REQUEST_FLG = "act";// BW通过参数act来区别HTTP请求

	private static final String HTTP_REQUEST_PHASE = "phase";

	/**
	 * NOTICE:add a mark field "flag" to tell which WebService to call. <br>
	 * recommend not to use the "phase" field again in the future.
	 * */
	private static final String HTTP_REQUEST_MARK = "flag";

	public static void postAscRequest(String strUrl, String content, String encode) throws IOException {
		if (encode == null)
			encode = defaultContentEncoding;
		byte[] buf = content.getBytes(encode);
		URL url = new URL(strUrl);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Content-Length", String.valueOf(buf.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=" + encode);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		out.write(buf);
		out.close();
		BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != -1)
			outputStream.write(b);
		in.close();
		outputStream.close();
		//String result = new String(outputStream.toByteArray(), encode);
		//logger.debug("length: " + result.length() + ", result: " + result);
	}
	
	@SuppressWarnings("static-access")
	public static void postRequestWithoutCheck(String strUrl, String content, String encode){
		//String result="";
		try{
			if (encode == null)
				encode = defaultContentEncoding;
			byte[] buf = content.getBytes(encode);
			URL url = new URL(strUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   
	
			httpConn.setRequestProperty("Content-Length", String.valueOf(buf.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=" + encode);
			httpConn.setRequestMethod("POST");
			
			httpConn.setFollowRedirects(true); 
			httpConn.setInstanceFollowRedirects(true); 
			
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			out.write(buf);
			out.close();
			BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int b;
			while ((b = in.read()) != -1)
				outputStream.write(b);
			in.close();
			outputStream.close();
			//String result = new String(outputStream.toByteArray(), encode);
		}catch(Exception e){}
	}	
	
	
	@SuppressWarnings("static-access")
	public static String postRequest(String strUrl, String content, String encode) throws IOException,
			MalformedURLException, ProtocolException {
		if (encode == null)
			encode = defaultContentEncoding;
		byte[] buf = content.getBytes(encode);
		URL url = new URL(strUrl);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   

		httpConn.setRequestProperty("Content-Length", String.valueOf(buf.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=" + encode);
		httpConn.setRequestMethod("POST");		
		httpConn.setFollowRedirects(true); 
		httpConn.setInstanceFollowRedirects(true);
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		out.write(buf);
		out.close();
		BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != -1)
			outputStream.write(b);
		in.close();
		outputStream.close();
		String result = new String(outputStream.toByteArray(), encode);
		//logger.debug("length: " + result.length() + ", result: " + result);
		return result;
	}

	public static String postRequest(String strUrl, String content, String act, String encode) throws IOException,
			MalformedURLException, ProtocolException {
		return postRequest("", 0, strUrl, content, act, encode);
	}

	public static String postRequest(String mark, int phase, String strUrl, String content, String act, String encode)
			throws IOException, MalformedURLException, ProtocolException {
		if (encode == null)
			encode = defaultContentEncoding;
		byte[] buf = content.getBytes(encode);
		URL url = new URL(strUrl);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		// httpConn.setRequestProperty("Accept-Encoding", encode);
		httpConn.setRequestProperty("Content-Length", String.valueOf(buf.length));
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=" + encode);
		httpConn.setRequestProperty(HTTP_REQUEST_FLG, act);
		httpConn.setRequestProperty(HTTP_REQUEST_PHASE, phase + "");
		httpConn.setRequestProperty(HTTP_REQUEST_MARK, mark == null ? "" : mark);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();
		out.write(buf);
		out.close();
		BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != -1)
			outputStream.write(b);
		in.close();
		outputStream.close();
		String result = new String(outputStream.toByteArray(), encode);
		//logger.debug("length: " + result.length() + ", result: " + result);
		return result;
	}

	public static String fatchDatas(String urlString) throws IOException {
		HttpURLConnection urlConnection = null;
		URL url = new URL(urlString);

		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod(HTTP_METHOD_POST);
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setConnectTimeout(600000);
		urlConnection.getOutputStream().flush();
		urlConnection.getOutputStream().close();

		return makeContent(urlConnection);
	}

	private static String makeContent(HttpURLConnection urlConnection) throws IOException {
		try {
			BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String ecod = urlConnection.getContentEncoding();
			if (ecod == null)
				ecod = defaultContentEncoding;

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int b;
			while ((b = in.read()) != -1)
				out.write(b);
			in.close();
			out.close();
			return new String(out.toByteArray(), ecod);
		} catch (IOException e) {
			throw e;
		}
	}
}
