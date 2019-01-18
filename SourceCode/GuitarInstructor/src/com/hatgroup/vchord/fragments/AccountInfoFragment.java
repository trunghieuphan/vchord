package com.hatgroup.vchord.fragments;

import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.hatgroup.vchord.R;
import com.hatgroup.vchord.adapters.KeyValueArrayAdapter;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.utils.HttpClient;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;
import com.hatgroup.vchord.utils.WebService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AccountInfoFragment extends TheFragment {

	private View layout;
	TextView txtPIN, txtBalance;
	IcsSpinner cbbCardType;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		layout = inflater.inflate(R.layout.field_set_layout, container, false);
		txtPIN = (TextView)layout.findViewById(R.id.txtPin);
		txtBalance = (TextView)layout.findViewById(R.id.txtBalance);
		cbbCardType = (IcsSpinner)layout.findViewById(R.id.cbbCardType);
		
		
		KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(getActivity(), R.layout.search_spinner_item);
		//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.setEntries(getResources().getStringArray(R.array.mobile_cardtype_values));
		adapter.setEntryValues(getResources().getStringArray(R.array.mobile_cardtype_keys));
		
		cbbCardType.setAdapter(adapter);

		registerActions();
		loadAccountInfo();
		return layout;
	}

	private void loadAccountInfo() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				String imei = Utilities.getEMEI(getActivity());
				TextView txtImei = (TextView) layout.findViewById(R.id.txtEmei);
				txtImei.setText(imei);

				if (imei != null && !imei.equals("")) {
					try {
						String json = WebService
								.performGet(Constants.ACC_INFO_URL + imei);
						if (json == null || json.equals("")) {
							Toast.makeText(
									getActivity(),
									ThemeUtils.getString(getActivity(),
											R.string.msg_error_network),
									Toast.LENGTH_SHORT).show();
						} else {
							txtBalance.setText(Utilities.extractStringFromJson(json, "balance"));
						}
					} catch (Exception ex) {
					}
				}
			}
		});
	}

	private void registerActions() {
		View btnSubmit = layout.findViewById(R.id.btnSubmitCard);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deposit();
			}
		});
	}
	
	private void deposit(){
		if(txtPIN.getText().toString().equals("")){
			Toast.makeText(getActivity(), ThemeUtils.getString(getActivity(), R.string.msg_warning_pin_blank),
					Toast.LENGTH_SHORT).show();
			return;
		}
		RequestParams reqParams = new RequestParams();
		reqParams.put("imei", Utilities.getEMEI(getActivity()));
		reqParams.put("pin_card", txtPIN.getText().toString());
		
		KeyValueArrayAdapter adapter = (KeyValueArrayAdapter) cbbCardType.getAdapter();
		String type_card = adapter.getEntryValue(cbbCardType.getSelectedItemPosition());
		reqParams.put("type_card", type_card);
		
		HttpClient.getInstance().post(Constants.DEPOSIT_URL, reqParams, new AsyncHttpResponseHandler() {
		
			@Override
			public void onFailure(Throwable t, String respMessage) {
				super.onFailure(t, respMessage);
				Toast.makeText(getActivity(), ThemeUtils.getString(getActivity(), R.string.msg_error_network),
						Toast.LENGTH_SHORT).show();
			}
		
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				Map<String, String> results = Utilities.jsonToMap(response);
				
				if(!Utilities.IsNullOrEmpty(results.get("msgcode"))){
					Toast.makeText(getActivity(), results.get("message"),Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getActivity(), getString(R.string.msg_info_deposit_success),Toast.LENGTH_SHORT).show();
					//Update balance on UI
					loadAccountInfo();
				}
			}
		});
	}
}
