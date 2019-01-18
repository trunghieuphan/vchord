package com.hatgroup.vchord.utils;

import java.util.HashMap;
import java.util.Map;

public class SongSetting {
	public static final String TONES = "TONES";
	public static final String SCROLL_SPEED = "SCROLL_SPEED";
	public static final String DELAY_SCROLL_TIME = "DELAY_SCROLL_TIME";
	public static final String ZOOM = "ZOOM";
	
	public static String SONG_SETTING_SEP = ";";
	private Map<String, String> mapSettings; 
	
	private SongSetting(String settingString){
		parseSettings(settingString);
	}
	
	public static SongSetting getInstance(String setting){
		return new SongSetting(setting);
	}
	
	private Map<String, String> parseSettings(String strSetting){
    	mapSettings = new HashMap<String, String>();
    	if(strSetting != null && strSetting.length() > 0){
    		String[] token = strSetting.split(SONG_SETTING_SEP);
    		for(String s : token){
    			if(s.trim().equals(""))	continue;
    			String key = s.split("=")[0].trim();
    			String value = s.split("=").length >=2 ? s.split("=")[1].trim() : "";
    			mapSettings.put(key, value);
    		}
    	}
    	return mapSettings;
    }
	
	public void updateSetting(String key, String value){
		mapSettings.put(key, value);
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer();
		for(String key : mapSettings.keySet()){
			result.append(key);
			result.append("=");
			result.append(mapSettings.get(key));
			result.append(SONG_SETTING_SEP);
		}
		return result.toString();
	}
	
	public String getSettingValue(String key){
		return mapSettings.get(key);
	}
	
	public int getSettingValueAsInt(String key){
		return Utilities.parseInt(getSettingValue(key));
	}
	
	public float getSettingValueAsFloat(String key){
		return Utilities.parseFloat(getSettingValue(key));
	}

}
