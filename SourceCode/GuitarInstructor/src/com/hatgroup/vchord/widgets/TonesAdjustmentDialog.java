package com.hatgroup.vchord.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hatgroup.vchord.MusicContainerActivity;
import com.hatgroup.vchord.R;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.SongSetting;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;

public class TonesAdjustmentDialog extends BaseDialogFragment implements OnClickListener{
	View myView;
	Song song;
	SongSetting songSetting;
	Context context;
	TextView txtToneValue;
	boolean isActivated;

	public TonesAdjustmentDialog(Context context, Song song) {
		this.song = song;
		this.context = context;
		songSetting = SongSetting.getInstance(song.getSetting());

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		builder.setTitle(ThemeUtils.getString(getActivity(),
				R.string.text_title_tone_adjustment));

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		myView = inflater
				.inflate(R.layout.tones_adjustment_dialog_layout, null);

		builder.setView(myView);
		isActivated = isActivated();
		if(isActivated){
			builder.setPositiveButton(R.string.text_ok,this);	
		}
		else{
			//Show warning message if have not activated
			TextView txtNotActivated = (TextView)myView.findViewById(R.id.txtNotActivated);
			txtNotActivated.setTextSize(getResources().getDimension(R.dimen.size_text_small));
			txtNotActivated.setVisibility(View.VISIBLE);
			builder.setPositiveButton(R.string.text_activate,this);
		}
		builder.setNegativeButton(R.string.text_cancel,this);
		//builder.setNeutralButton(R.string.text_reset,this);
		
		
		AlertDialog dialog = builder.create();
		
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		ImageButton btnUp = (ImageButton) myView.findViewById(R.id.btnUp);
		btnUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onUpDownClick(v);	
			}
		});

		ImageButton btnDown = (ImageButton) myView.findViewById(R.id.btnDown);
		btnDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onUpDownClick(v);	
			}
		});

		txtToneValue = (TextView) myView.findViewById(R.id.txtToneValue);
		String value = songSetting.getSettingValue(SongSetting.TONES);
		if (value == null || value.equals("")) {
			value = "0";
		}
		txtToneValue.setText(value);

		return dialog;
	}


	public void onUpDownClick(View v) {
		String textValue = txtToneValue.getText().toString().replace("+", "");
		int value = Integer.parseInt(textValue);
		if (v.getId() == R.id.btnUp) {
			value++;
		} else if (v.getId() == R.id.btnDown) {
			value--;
		}
		String prefix = value > 0 ? "+" : "";
		txtToneValue.setText(prefix + value);

	}

	private void saveTonesAdjustment(String value) {
		songSetting.updateSetting(SongSetting.TONES, value);
		SongRepo songRepo = new SongRepo(context);
		// Persist to DB
		song.setSetting(songSetting.toString());
		boolean updated = songRepo.updateSong(song);
		if (updated) {
			if (context instanceof MusicContainerActivity) {
				int adjustValue = txtToneValue.getText().toString()
						.startsWith("-") ? (-1)
						* Utilities.parseInt(txtToneValue.getText().toString()
								.replace("-", "")) : Utilities
						.parseInt(txtToneValue.getText().toString()
								.replace("+", ""));
				((MusicContainerActivity) context).convertChords(adjustValue);
			}
		}
		dismiss();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		int i = 0;
		
	}
	
	public void normalDismis()
	{
		super.dismiss();
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == dialog.BUTTON_NEGATIVE)
		{
			TonesAdjustmentDialog.this.getDialog().cancel();
            super.dismiss();
			
		}else if (which == dialog.BUTTON_NEUTRAL)
		{			
			txtToneValue.setText("0");
			saveTonesAdjustment(txtToneValue.getText().toString());
			
		} else if (which == dialog.BUTTON_POSITIVE)
		{
			if(isActivated){
				saveTonesAdjustment(txtToneValue.getText().toString());
	            super.dismiss();				
			}
			else{
				//Do purchase
				Runnable callBack = new Runnable() {
					public void run() {
						TonesAdjustmentDialog tonesAdjustmentDialog = new TonesAdjustmentDialog(getActivity(), song);
						tonesAdjustmentDialog.show(TonesAdjustmentDialog.this.getActivity().getSupportFragmentManager(), 
								TonesAdjustmentDialog.class.getName());
					}
				};
				purchase(Constants.LIC_TONE, "", callBack);
			}
		}
	}
}
