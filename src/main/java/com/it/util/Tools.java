package com.it.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 提供一些工具方法
 * @author yilihjy Email:yilihjy@gmail.com
 * @version 1.0.0
 *
 */
public class Tools {
    /**
	 * 发送get请求，返回内容字符串
	 * @param url 请求urll
	 * @param charsetName 字符码
	 * @return 响应内容字符串
	 */
	public static String sendHTTPGET(String url,String charsetName){
		String result =null;
		HttpGet httpGet = new HttpGet(url);
		try (
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(httpGet);)
		{
			int status = response.getStatusLine().getStatusCode();
			if(status == 200){
				HttpEntity entity = response.getEntity();
				result = InputStreamToString(entity.getContent(),charsetName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 将如"2017-01-07 14:07:35"或"2017-01-07"这样的字符串转换为LocalDateTime对象
	 * @param time 时间字符串
	 * @return {@link LocalDateTime}对象
	 */
	public static LocalDateTime string2LocalDateTime(String time){
		LocalDateTime result;
		String[] times = time.split(" ");
		if(times.length == 1){
			times = time.split("-");
			result = LocalDateTime.of(Integer.parseInt(times[0]), Integer.parseInt(times[1]), Integer.parseInt(times[2]), 15, 0);
		}else{
			String[] day = times[0].split("-");
			String[] daytime = times[1].split(":");
			result = LocalDateTime.of(Integer.parseInt(day[0]), Integer.parseInt(day[1]), Integer.parseInt(day[2]), Integer.parseInt(daytime[0]), Integer.parseInt(daytime[1]), Integer.parseInt(daytime[2]));
		}
		return result;
	}
	/**
	 * 将{@link InputStream}转换为{@link String}
	 * @param in {@link InputStream}
	 * @param charsetName 字符串编码
	 * @return 返回String字符串
	 * @throws UnsupportedEncodingException 不支持的编码
	 * @throws IOException io错误
	 */
	public static String InputStreamToString(InputStream in,String charsetName) throws UnsupportedEncodingException, IOException{
		StringBuffer sb = new StringBuffer();
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = in.read(b)) != -1){
		sb.append(new String(b, 0, len, charsetName));}
		return sb.toString();
	}
	

	
	/**
	 * 将{@link LocalDateTime}转换为旧式{@link Date}
	 * @param localDateTime {@link LocalDateTime}对象
	 * @return {@link Date}对象
	 */
	public static Date LocalDateTime2Date(LocalDateTime localDateTime) {
	    ZoneId zone = ZoneId.of("UTC");
	    Instant instant = localDateTime.atZone(zone).toInstant();
	    return Date.from(instant);
	}
	

}