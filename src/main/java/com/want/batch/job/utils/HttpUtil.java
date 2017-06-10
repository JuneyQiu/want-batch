package com.want.batch.job.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpUtil {
	static Log log = LogFactory.getLog(HttpUtil.class);
	
	private HttpUtil(){
	}
	
	public static String post(String url, String content, String contentType/*text/xml*/, String encoding, int timeoutSec) throws Exception{
		HttpClient client = new HttpClient();
		if(timeoutSec > 0){
			client.getHttpConnectionManager().getParams().setSoTimeout(timeoutSec*1000);
		}

		PostMethod method = new PostMethod(url);
		try {
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

			RequestEntity entity = new StringRequestEntity(content, contentType, encoding);
            method.setRequestEntity(entity);			
			
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("APP Error, http status: " + method.getStatusLine());
			}
			
			return method.getResponseBodyAsString();
			/*
			byte[] responseBody = method.getResponseBody();
			return new String(responseBody,encoding);
			*/
		} catch (HttpException e) {
			log.error("HTTP Error:", e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error:", e);
			throw e;
		} finally {
			method.releaseConnection();
		}
	}
	
	public static String post(String url, Map<String, Object> params, String encoding,int timeoutSec) throws Exception {
		HttpClient client = new HttpClient();
		if(timeoutSec > 0){
			client.getHttpConnectionManager().getParams().setSoTimeout(timeoutSec*1000);
		}

		PostMethod method = new PostMethod(url);
		try {
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
			
			if(params != null && params.size() > 0){
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				Iterator<Entry<String, Object>> it = params.entrySet().iterator();
				while(it.hasNext()){
					Entry<String, Object> e = it.next();
					String name = e.getKey();
					String val = (e.getValue()==null)? "":e.getValue().toString();
					
					pairs.add(new NameValuePair(name, URIUtil.encodeQuery(val)));
				}
				method.setRequestBody(pairs.toArray(new NameValuePair[pairs.size()]));
			}

			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("APP Error, http status: " + method.getStatusLine());
			}
			
			return method.getResponseBodyAsString();
			/*
			byte[] responseBody = method.getResponseBody();
			return new String(responseBody,encoding);
			*/
		} catch (HttpException e) {
			log.error("HTTP Error:", e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error:", e);
			throw e;
		} finally {
			method.releaseConnection();
		}
	}
	
	//get method
	public static String get(String url, Map<String, Object> params, String encoding,int timeoutSec) throws Exception {
		HttpClient client = new HttpClient();
		if(timeoutSec > 0){
			client.getHttpConnectionManager().getParams().setSoTimeout(timeoutSec*1000);
		}

		GetMethod method = new GetMethod(url);
		try {
			method.setFollowRedirects(true);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
			
			if(params != null && params.size() > 0){
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				Iterator<Entry<String, Object>> it = params.entrySet().iterator();
				while(it.hasNext()){
					Entry<String, Object> e = it.next();
					String name = e.getKey();
					String val = (e.getValue()==null)? "":e.getValue().toString();
					
					pairs.add(new NameValuePair(name, URIUtil.encodeQuery(val)));
				}
				method.setQueryString(pairs.toArray(new NameValuePair[pairs.size()]));
			}
		
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("APP Error, http status: " + method.getStatusLine());
			}
			
			return method.getResponseBodyAsString();
			/*
			byte[] responseBody = method.getResponseBody();
			return new String(responseBody,encoding);
			*/
		} catch (HttpException e) {
			log.error("HTTP Error:", e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error:", e);
			throw e;
		} finally {
			method.releaseConnection();
		}
	}

	public static void download(String url, File filepath, int timeoutSec) throws Exception {
		HttpClient client = new HttpClient();
		if(timeoutSec > 0){
			client.getHttpConnectionManager().getParams().setSoTimeout(timeoutSec*1000);
		}

		GetMethod method = new GetMethod(url);
		try {
			method.setFollowRedirects(true);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
			
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("APP Error, http status: " + method.getStatusLine());
			}
			
			File dir = filepath.getParentFile();
			if(!dir.exists()){
				dir.mkdirs();
			}
			IOUtils.copy(method.getResponseBodyAsStream(), new FileOutputStream(filepath));
		} catch (HttpException e) {
			log.error("HTTP Error:", e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error:", e);
			throw e;
		} finally {
			method.releaseConnection();
		}
	}
	
	public static byte[] download(String url, int timeoutSec) throws Exception {
		HttpClient client = new HttpClient();
		if(timeoutSec > 0){
			client.getHttpConnectionManager().getParams().setSoTimeout(timeoutSec*1000);
		}
		GetMethod method = new GetMethod(url);
		try {
			method.setFollowRedirects(true);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
			
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("APP Error, http status: " + method.getStatusLine());
			}
			
			return method.getResponseBody();
		} catch (HttpException e) {
			log.error("HTTP Error:", e);
			throw e;
		} catch (IOException e) {
			log.error("IO Error:", e);
			throw e;
		} finally {
			method.releaseConnection();
		}
	}
	
	public static void download(String url, String filepath, int timeoutSec) throws Exception {
		download(url, new File(filepath), timeoutSec);
	}
	
	public static void main(String[] args) {
		try {
			byte[] download = null;
			download = HttpUtil.download("http://i.want-want.com/img/201305/14/10/6e367eed-68c8-491c-9a8b-d987c6a83bf5.jpg", 6);
			System.out.println(download.length);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}