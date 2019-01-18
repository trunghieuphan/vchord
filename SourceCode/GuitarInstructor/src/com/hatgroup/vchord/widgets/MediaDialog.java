package com.hatgroup.vchord.widgets;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.hatgroup.vchord.R;
import com.hatgroup.vchord.utils.Utilities;

public class MediaDialog extends Dialog implements
		android.view.View.OnClickListener, SeekBar.OnSeekBarChangeListener,
		OnCompletionListener {

	Context context;
	private ImageButton btnPlay;
	private SeekBar songProgressBar;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();
	
	private String url;

	public MediaDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_player_layout);
		ImageButton dialogButton = (ImageButton) findViewById(R.id.btnClose);
		// if close button is clicked, close the custom dialog
		dialogButton.setOnClickListener(this);
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				if(mHandler != null){
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler = null;
				}
				if(mp != null){
					mp.release();
					mp = null;					
				}
			}
		});

		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

		mp = new MediaPlayer();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important

		/**
		 * Play button click event plays a song and changes button to pause
		 * image pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check for already playing
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				} else {
					// Resume song
					if (mp != null) {
						mp.start();
						// Changing button image to stop button
						btnPlay.setImageResource(R.drawable.ic_action_pause_music);
					}
				}
			}
		});
		
		playSong(url);
	}

	public void showMyDialog(String songURL) {
		this.url = songURL.replace("\n", "");
		show();
	}

	public void playSong(String songPath) {
		try {
			//Play song
			mp.reset();
			mp.setDataSource(url);
			mp.prepare();
			mp.start();

			//Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.ic_action_pause_music);

			//set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);

			//Updating progress bar
			updateProgressBar();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			if(mp != null){
				long totalDuration = mp.getDuration();
				long currentDuration = mp.getCurrentPosition();
				// Displaying Total Duration time
				songTotalDurationLabel.setText(""
						+ Utilities.milliSecondsToTimer(totalDuration));
				// Displaying time completed playing
				songCurrentDurationLabel.setText(""
						+ Utilities.milliSecondsToTimer(currentDuration));
				// Updating progress bar
				int progress = (int) (Utilities.getProgressPercentage(
						currentDuration, totalDuration));
				songProgressBar.setProgress(progress);
				// Running this thread after 100 milliseconds
				mHandler.postDelayed(this, 100);
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnClose) {
			//mHandler.removeCallbacks(mUpdateTimeTask);
			//mp.release();
			//mp = null;
			dismiss();
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = Utilities.progressToTimer(seekBar.getProgress(),
				totalDuration);
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		// update timer progress again
		updateProgressBar();
	}

	/**
	 * On Song Playing completed
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
	}
}