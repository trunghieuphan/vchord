package com.hatgroup.vchord.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.entities.Song;

public class Utilities {

	public static List<Song> jsonToSongs(String json) {
		ArrayList<Song> songs = new ArrayList<Song>();
		try {
			JSONArray jArray = new JSONArray(json);
			songs.addAll(jsonArrayToSongs(jArray));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return songs;
	}
	
	public static List<Song> jsonArrayToSongs(JSONArray jArray) {
		ArrayList<Song> songs = new ArrayList<Song>();
		try {
			JSONObject json_data = null;
			for (int i = 0; i < jArray.length(); i++) {
				json_data = jArray.getJSONObject(i);
				Song song = populateData(json_data);
				if(song != null){
					songs.add(song);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return songs;
	}
		
	private static Song populateData(JSONObject json_data){
		try{
			//For update
			int id = json_data.getInt("id");
			String title = json_data.getString("title");
			String unsigned_title = json_data.getString("unsigned_title");
			String lyric = json_data.getString("lyric");
			Integer rhythm_id = extractIntFromJSon(json_data, "rhythm_id");
			Integer levelId = extractIntFromJSon(json_data, "level_id");
			Integer melodyId = extractIntFromJSon(json_data, "melody_id");
			String updateDate = json_data.getString("update_date");
			String musicLink = json_data.getString("music_link");
			String singerName = json_data.getString("singer_name");
			String unsignedSingerName = json_data.getString("unsigned_singer_name");
			String composerName = json_data.getString("composer_name");
			String unsignedComposerName = json_data.getString("unsigned_composer_name");
			String tone = json_data.getString("tone");
			//String setting = json_data.getString("setting");
			
			Song song = new Song();
			song.setId(id);
			song.setTitle(title);
			song.setUnsigned_title(unsigned_title);
			song.setLyric(lyric);
			song.setRhythm_id(rhythm_id);
			song.setSinger_name(singerName);
			song.setUnsigned_singer_name(unsignedSingerName);
			song.setComposer_name(composerName);
			song.setUnsigned_composer_name(unsignedComposerName);
			song.setLevel_id(levelId);
			song.setMelody_id(melodyId);
			song.setUpdate_date(updateDate);
			song.setMusic_link(musicLink);
			song.setTone(tone);
			
			return song;
		}
		catch(JSONException je){
			je.printStackTrace();
			return null;
		}
	}
	
	public static String extractStringFromJson(String json, String key){
		try {
			JSONObject jo = new JSONObject(json);
			return jo.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getEMEI(Context context){
		String imei = "";
		try
		{
			// Get IMEI.
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			
			
			
		}
		catch(Exception e)
		{
			imei = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			Log.i("imei", e.getMessage());
		}
		
		return imei;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> jsonToMap(String json){
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject jo = new JSONObject(json);
			Iterator<Object> it = jo.keys();
			Object key = null;
			while(it.hasNext()){
				key = it.next();
				map.put(key.toString(), jo.getString(key.toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/*
	private static final String changeChord(String chord, int updown) {

		List<String> majorChords = asList("[A]", "[Bb]", "[B]", "[C]", "[C#]",
				"[D]", "[D#]", "[E]", "[F]", "[F#]", "[G]", "[G#]");
		List<String> minorChords = asList("[Am]", "[A#m]", "[Bm]", "[Cm]",
				"[C#m]", "[Dm]", "[D#m]", "[Em]", "[Fm]", "[F#m]", "[Gm]",
				"[G#m]");
		List<String> major7Chords = asList("[A7]", "[Bb7]", "[B7]", "[C7]",
				"[C#7]", "[D7]", "[D#7]", "[E7]", "[F7]", "[F#7]", "[G7]",
				"[G#7]");
		List<String> minor7Chords = asList("[Am7]", "[A#m7]", "[Bm7]", "[Cm7]",
				"[C#m7]", "[Dm7]", "[D#m7]", "[Em7]", "[Fm7]", "[F#m7]",
				"[Gm7]", "[G#m7]");

		List<List<String>> lists = new ArrayList<List<String>>();
		lists.add(majorChords);
		lists.add(minorChords);
		lists.add(major7Chords);
		lists.add(minor7Chords);

		String newChord = "";
		for (List<String> list : lists) {
			int index = -1;
			index = list.indexOf(chord);
			if (index != -1) {
				int maxIndex = list.size() - 1;
				// up
				if (updown == Constants.UP) {
					if (index < maxIndex) {
						newChord = list.get(index + 1);
					} else {
						newChord = list.get(0);
					}
				}
				// down
				if (updown == Constants.DOWN) {
					if (index == 0) {
						newChord = list.get(maxIndex);
					} else {
						newChord = list.get(index - 1);
					}
				}
				break;
			}
		}

		return newChord;
	}
	*/

	public static String changeChord(String chord, int adjustValue) {
		String newChord = "";
		String[] chords = null;
		int currentIndex = -1;
		for(String[] arr : Constants.CHORDS){
			currentIndex = Arrays.asList(arr).indexOf(chord);
			if(currentIndex != -1){
				chords = arr;
				break;
			}
		}
		if(chords != null){
			adjustValue = adjustValue % chords.length;
			int index = 0;
			int currentPos = currentIndex + adjustValue;
			index = currentPos % chords.length;
			if(index < 0){
				index = chords.length + index;
			}
			newChord = chords[index];
		}
		return newChord.equals("") ? chord : newChord;
	}
	
	public static String convertChords(String content, int adjustValue) {

		String patternText = "\\[(.*?)\\]";

		Pattern pattern = Pattern.compile(patternText);
		Matcher matcher = pattern.matcher(content);

		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {

			String chord = changeChord(matcher.group(), adjustValue);
			matcher.appendReplacement(sb, Matcher.quoteReplacement(chord));
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public static String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   finalTimerString = hours + ":";
		   }
		   
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		   finalTimerString = finalTimerString + minutes + ":" + secondsString;
		
		// return timer string
		return finalTimerString;
	}
	
	public static int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public static int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	
	public static int parseInt(Object obj){
		try{
			if(obj == null)
				return 0;
			if(obj.toString().startsWith("+")){
				return Integer.parseInt((obj + "").replace("+", ""));
			}
			if(obj.toString().startsWith("-")){
				return Integer.parseInt((obj + "").replace("-", "")) * -1;
			}
			return Integer.parseInt((obj + ""));
		}
		catch(NumberFormatException ne){
			return 0;
		}
	}

	public static float parseFloat(Object obj){
		try{
			if(obj == null)
				return 0;
			if(obj.toString().startsWith("+")){
				return Float.parseFloat((obj + "").replace("+", ""));
			}
			if(obj.toString().startsWith("-")){
				return Float.parseFloat((obj + "").replace("-", "")) * -1;
			}
			return Float.parseFloat((obj + ""));
		}
		catch(NumberFormatException ne){
			return 0;
		}
	}
	
	   /**
	 * Mirror of the unicode table from 00c0 to 017f without diacritics.
	 */
	private static final String tab00c0 = "AAAAAAACEEEEIIII" +
	    "DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
	    "aaaaaaaceeeeiiii" +
	    "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
	    "AaAaAaCcCcCcCcDd" +
	    "DdEeEeEeEeEeGgGg" +
	    "GgGgHhHhIiIiIiIi" +
	    "IiJjJjKkkLlLlLlL" +
	    "lLlNnNnNnnNnOoOo" +
	    "OoOoRrRrRrSsSsSs" +
	    "SsTtTtTtUuUuUuUu" +
	    "UuUuWwYyYZzZzZzF";
	
	/**
	 * Returns string without diacritics - 7 bit approximation.
	 *
	 * @param source string to convert
	 * @return corresponding string without diacritics
	 */
	public static String removeDiacritic(String source) {
	    char[] vysl = new char[source.length()];
	    char one;
	    for (int i = 0; i < source.length(); i++) {
	        one = source.charAt(i);
	        if (one >= '\u00c0' && one <= '\u017f') {
	            one = tab00c0.charAt((int) one - '\u00c0');
	        }
	        vysl[i] = one;
	    }
	    String result = new String(vysl);
	    result = deAccent(result);
	    return result;
	}	
	
	@SuppressLint("NewApi")
	public static String deAccent(String str) {
	    String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
//	    String t = nfdNormalizedString.replaceAll("[^\\p{ASCII}]", "");
//	    return nfdNormalizedString.replaceAll("[^\\p{ASCII}]", "");
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
	private static Integer extractIntFromJSon(JSONObject jsonObj, String key){
		try {
			return jsonObj.getInt(key);
		} catch (JSONException e) {}
		return null; 
	}
	
	public static void hideSoftKeyboard(View view) {
//	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	    
	    InputMethodManager in = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	
	public static boolean IsNullOrEmpty(String str)
	{
		return !(str!= null && str.length() != 0 );
	}
}
