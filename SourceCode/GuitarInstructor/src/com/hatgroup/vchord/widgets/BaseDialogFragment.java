package com.hatgroup.vchord.widgets;

import java.util.Map;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.utils.HttpClient;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class BaseDialogFragment extends DialogFragment{
	
	public BaseDialogFragment(){
	}

	/**
	 * Read from DB to know the feature is has been purchased 
	 * (activated) or not
	 */
	
	public static boolean flag = false;
	
	public boolean isActivated(){
		return flag;
	}

	/**
	 * Purchase feature 
	 */
	public void purchase(String feature, String ref, final Runnable callBack){
		final FragmentActivity activity = getActivity();
		
		RequestParams reqParams = new RequestParams();
		reqParams.put("imei", Utilities.getEMEI(activity));
		reqParams.put("item", feature);
		//If buy song, we have one more parameter 'ref'
		if(ref != null && !ref.trim().equals("")){
			reqParams.put("ref", ref);
		}
		
		HttpClient.getInstance().post(Constants.PURCHASE_URL, reqParams, new AsyncHttpResponseHandler() {
		
			@Override
			public void onFailure(Throwable t, String respMessage) {
				super.onFailure(t, respMessage);
				Toast.makeText(activity, ThemeUtils.getString(activity, R.string.msg_error_network),
						Toast.LENGTH_SHORT).show();
			}
		
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				Map<String, String> results = Utilities.jsonToMap(response);
				//{"imei":"355136054832921","item":"LIC_SCROLL","ref":null,"price":10000,"qty":1,"amount":10000,"create_date":"2013-09-06","id":"9"}
				if(results.containsKey("id")){
					//Update local db, for now hard-coded by switch on flag
					flag = true;
					Toast.makeText(activity, ThemeUtils.getString(activity, R.string.msg_activate_success), Toast.LENGTH_SHORT).show();
					if(callBack != null){
						callBack.run();
					}
				}
				else{
					//Update failed, show error description
					Toast.makeText(activity, ThemeUtils.getString(activity, R.string.msg_activate_fail),
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}
}

