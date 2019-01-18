package com.hatgroup.vchord.utils;
import com.loopj.android.http.AsyncHttpClient;

public class HttpClient{
	
	private static AsyncHttpClient instance;
	
	public static synchronized AsyncHttpClient getInstance(){
		if (instance == null){
			instance = new AsyncHttpClient();
			instance.setUserAgent("Mozilla/5.0");
		}
		return instance;
	}
}


