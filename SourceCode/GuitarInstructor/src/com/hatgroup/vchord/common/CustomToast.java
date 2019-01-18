package com.hatgroup.vchord.common;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.utils.ThemeUtils;

public class CustomToast {

	public enum MsgType {
		ERROR, INFO // ; is optional
	}

	public static void show(Activity activity, int msgId, MsgType msgType) {
		// Retrieve the layout inflator
		LayoutInflater inflater = activity.getLayoutInflater();
		// Assign the custom layout to view
		// Parameter 1 - Custom layout XML
		// Parameter 2 - Custom layout ID present in linear layout tag of XML
		View layout = null;

		// LinearLayout message_layout = (LinearLayout)
		// layout.findViewById(R.id.toast_message_layout);
		if (msgType == msgType.ERROR) {
			layout = inflater.inflate(R.layout.toast_error_layout, null);
			LinearLayout message_layout = (LinearLayout) layout
					.findViewById(R.id.toast_message_layout);

			message_layout.getBackground().setAlpha(150);

			// message_layout.setBackgroundColor(activity.getResources().getColor(
			// color.DarkRed));
			// message_layout.getBackground().setAlpha(10);

		} else if (msgType == MsgType.INFO) {
			layout = inflater.inflate(R.layout.toast_info_layout, null);

			LinearLayout message_layout = (LinearLayout) layout
					.findViewById(R.id.toast_message_layout);

			message_layout.getBackground().setAlpha(150);

			// message_layout.setBackgroundColor(activity.getResources().getColor(
			// color.DarkGreen));
			// message_layout.getBackground().setAlpha(200);

		}

		layout.setBackgroundColor(activity.getResources().getColor(
				android.R.color.transparent));

		TextView text = (TextView) layout.findViewById(R.id.toastMessage);
		// Set the Text to show in TextView
		text.setText(ThemeUtils.getString(activity, msgId));

		// Return the application context
		Toast toast = new Toast(activity.getApplicationContext());
		// Set toast gravity to bottom
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		// Set toast duration
		toast.setDuration(Toast.LENGTH_SHORT);
		// Set the custom layout to Toast
		toast.setView(layout);
		// Display toast
		toast.show();

	}

}
