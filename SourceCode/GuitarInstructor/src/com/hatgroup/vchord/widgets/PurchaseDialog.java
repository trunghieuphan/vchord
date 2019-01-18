package com.hatgroup.vchord.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.utils.ThemeUtils;

public class PurchaseDialog extends BaseDialogFragment {

	View layout;
	String item;
	Runnable callBack;
	
	public PurchaseDialog(String item, Runnable callBack) {
		this.item = item;
		this.callBack = callBack;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle(ThemeUtils.getString(getActivity(), R.string.lbl_warning));
		layout = inflater.inflate(R.layout.purchase_dialog_layout, null);
		builder.setView(layout)
				// Add action buttons
				.setPositiveButton(R.string.text_activate,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								purchase(item, "", callBack);
							}
						})
				.setNegativeButton(R.string.text_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								PurchaseDialog.this.getDialog().cancel();
							}
						});

		AlertDialog dialog = builder.create();

		return dialog;

	}
}
