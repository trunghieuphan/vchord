package com.hatgroup.vchord.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.util.TypedValue;

public class ThemeUtils {
	public static int getResId(Theme theme, int attr)
	{
		TypedValue typedValue = new TypedValue(); 
		theme.resolveAttribute(attr, typedValue, true);
		
		return typedValue.resourceId;
		
	}
	
	public static String getString(Context context, int stringId)
	{
		return context.getResources().getString(stringId);
		
	}
	
	public static String getString(Activity activity, int stringId)
	{
		return activity.getApplicationContext().getResources().getString(stringId);
		
	}
}
