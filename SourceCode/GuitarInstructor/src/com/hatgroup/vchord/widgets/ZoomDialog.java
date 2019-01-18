package com.hatgroup.vchord.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.MusicContainerActivity;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.SongSetting;
import com.hatgroup.vchord.utils.ThemeUtils;

public class ZoomDialog extends DialogFragment {
	Context context;
	Song song;
	SongSetting songSetting;
	View layout;

	public ZoomDialog(Context context, Song song) {
		this.song = song;
		this.context = context;
		songSetting = SongSetting.getInstance(song.getSetting());
		this.setCancelable(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle(ThemeUtils.getString(getActivity(), R.string.text_title_zoom));
		layout = inflater.inflate(R.layout.zoom_dialog_layout, null);

		builder.setView(layout)
				// Add action buttons
				.setPositiveButton(R.string.text_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								okClicked();
							}
						})
				.setNegativeButton(R.string.text_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ZoomDialog.this.getDialog().cancel();
							}
						});

		AlertDialog dialog = builder.create();

		dialog.setCanceledOnTouchOutside(true);

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (context instanceof MusicContainerActivity) {
					//((MusicContainerActivity) context).reloadSongFromRepo();
					//((MusicContainerActivity) context).runAutoScroll();
				}
			}
		});

		final TextView seekBarValue = (TextView)layout.findViewById(R.id.txtZoomingRatio);

		final SeekBar seekBar = (SeekBar)layout.findViewById(R.id.zoomingBar);

		int tempValue = songSetting.getSettingValueAsInt(SongSetting.ZOOM);
		seekBar.setProgress(tempValue);
		seekBarValue.setText("" + tempValue);

		// Vertical SeekBar
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				seekBarValue.setText("" + progress);
			}
		});

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (context instanceof MusicContainerActivity) {
		}
	}

	private void okClicked() {
		// Save to DB
		updateSettings();
		this.dismiss();

	}

	private void updateSettings() {
		SongRepo songRepo = new SongRepo(getActivity().getApplicationContext());
		// Persist to DB
		song.setSetting(songSetting.toString());
		songRepo.updateSong(song);
	}

}
