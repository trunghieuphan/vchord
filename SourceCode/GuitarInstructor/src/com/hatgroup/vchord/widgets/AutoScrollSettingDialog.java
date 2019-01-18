package com.hatgroup.vchord.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hatgroup.vchord.MusicContainerActivity;
import com.hatgroup.vchord.R;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.SongSetting;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;

public class AutoScrollSettingDialog extends BaseDialogFragment {
	Context context;
	Song song;
	SongSetting songSetting;
	View layout;

	public AutoScrollSettingDialog(Context context, Song song) {
		this.song = song;
		this.context = context;
		songSetting = SongSetting.getInstance(song.getSetting());
		this.setCancelable(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		builder.setTitle(ThemeUtils.getString(getActivity(),
				R.string.text_title_autoscroll));

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		layout = inflater.inflate(R.layout.autoscroll_setting, null);

		builder.setView(layout);
		
		//Add action buttons
		boolean isActivated = isActivated();
		if(isActivated){
			builder.setPositiveButton(R.string.text_start,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						startClicked();
					}
				});						
		}
		else{
			//Show warning message if have not activated
			TextView txtNotActivated = (TextView)layout.findViewById(R.id.txtNotActivated);
			txtNotActivated.setTextSize(getResources().getDimension(R.dimen.size_text_small));
			txtNotActivated.setVisibility(View.VISIBLE);
			builder.setPositiveButton(R.string.text_activate,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Runnable callBack = new Runnable() {
							public void run() {
								AutoScrollSettingDialog autoscrollSettingDialog = new AutoScrollSettingDialog(getActivity(), song);
								autoscrollSettingDialog.show(AutoScrollSettingDialog.this.getActivity().getSupportFragmentManager(), 
										AutoScrollSettingDialog.class.getName());
							}
						};
						purchase(Constants.LIC_SCROLL, "", callBack);
					}
			});			
		}
		builder.setNegativeButton(R.string.text_cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					AutoScrollSettingDialog.this.getDialog()
							.cancel();
				}
		});

		AlertDialog dialog = builder.create();

		dialog.setCanceledOnTouchOutside(true);

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub

				if (context instanceof MusicContainerActivity) {
					((MusicContainerActivity) context).reloadSongFromRepo();
					((MusicContainerActivity) context).runAutoScroll();
				}
			}
		});
				

		final TextView seekBarValue = (TextView) layout
				.findViewById(R.id.seekBarValueTextView);

		final SeekBar seekBar = (SeekBar) layout
				.findViewById(R.id.verticalSeekbar);
		final EditText txtDelay = (EditText) layout.findViewById(R.id.txtDelay);

		int tempValue = songSetting
				.getSettingValueAsInt(SongSetting.SCROLL_SPEED);
		if (tempValue > MusicContainerActivity.MAX_SCROLL_SPEED) {
			tempValue = 0;
		}
		seekBar.setProgress(tempValue);
		seekBarValue.setText("" + tempValue);

		int delayScrollTime = songSetting
				.getSettingValueAsInt(SongSetting.DELAY_SCROLL_TIME);
		txtDelay.setText(delayScrollTime + "");

		// Vertical SeekBar
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				seekBarValue.setText("" + progress);
			}
		});

		return dialog;

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		// super.onDismiss(dialog);
		if (context instanceof MusicContainerActivity) {
			((MusicContainerActivity) context).reloadSongFromRepo();
			((MusicContainerActivity) context).runAutoScroll();
		}
	}

	private void startClicked() {
		final TextView seekBarValue = (TextView) layout
				.findViewById(R.id.seekBarValueTextView);
		final EditText txtDelay = (EditText) layout.findViewById(R.id.txtDelay);

		// Calculate new delayValue
		calDelayValue(Integer.parseInt(seekBarValue.getText().toString()));

		songSetting.updateSetting(SongSetting.SCROLL_SPEED, seekBarValue
				.getText().toString());
		songSetting.updateSetting(SongSetting.DELAY_SCROLL_TIME,
				Utilities.parseInt(txtDelay.getText().toString()) + "");

		// Save to DB
		updateSettings();

		this.dismiss();

	}

	private void calDelayValue(int seekBarValue) {
		MusicContainerActivity.DELAY_VALUE = (long) (MusicContainerActivity.MAX_SCROLL_SPEED - seekBarValue)
				* MusicContainerActivity.TIME_STEP
				+ MusicContainerActivity.INITIAL_DELAY_VALUE;

	}

	private void updateSettings() {
		SongRepo songRepo = new SongRepo(getActivity().getApplicationContext());
		// Persist to DB
		song.setSetting(songSetting.toString());
		songRepo.updateSong(song);
	}

}
