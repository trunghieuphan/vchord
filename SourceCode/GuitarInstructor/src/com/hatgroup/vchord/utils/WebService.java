package com.hatgroup.vchord.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StrictMode;

@SuppressLint("NewApi")
public class WebService {

	public static String performGet(String url) {
		InputStream is = null;
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(is == null){
			return "";
		}
		return inputStreamToString(is);
	}

/*	public static String inputStreamToString(InputStream is){
		String result = null;
		StringBuilder sb = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
*/
	public static String inputStreamToString(InputStream is){
		String result = null;
		try {
			OutputStream myOutput = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = is.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	myOutput.flush();
	    	myOutput.close();
	    	is.close();
	    	result = myOutput.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String performPost(String url, List<BasicNameValuePair> params) {
		InputStream is = null;
		
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			if (params != null && params.size() > 0) {
				httppost.setEntity(new UrlEncodedFormEntity(params));
			}
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = inputStreamToString(is);
		return result;
	}

}
