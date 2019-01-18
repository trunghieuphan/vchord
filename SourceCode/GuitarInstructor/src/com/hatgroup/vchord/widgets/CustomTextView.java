package com.hatgroup.vchord.widgets;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Parser.ParsedObject;

public class CustomTextView extends TextView {
	
	ParsedObject po;
	private static float initialSize;
	private static float scale;
	
	public CustomTextView(Context context, ParsedObject po) {
		super(context);
		this.po = po;

		int appearanceResId = ThemeUtils.getResId(context.getTheme(), R.attr.text_song_detail);
		setTextAppearance(context, appearanceResId);
		
		if(scale == 0){
			DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
			scale = metrics.density;
		}
		if(initialSize == 0){
			initialSize = getTextSize() / scale;
		}
	}

	public void setTextContent(CharSequence text) {
		super.setText(text);
	}
	
	public ParsedObject getParsedObject(){
		return po;
	}
	
	public String toString(){
		return getText().toString();
	}
	
	public static float getInitialTextSize(){
		return initialSize;
	}
	
	public float getCurrentSize(){
		return getTextSize() / scale;
	}
	
	public static float getScale(){
		return scale;
	}
}
