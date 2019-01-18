package com.hatgroup.vchord.common;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.hatgroup.vchord.MusicContainerActivity;
import com.hatgroup.vchord.entities.Song;

public class ImageClickListener implements OnClickListener {

	private Song song;
	Context ctx;
	
	public ImageClickListener(Context ctx, Song song) {
		this.ctx = ctx;
		this.song = song;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(ctx, MusicContainerActivity.class);
		intent.putExtra("theSong", song);
		ctx.startActivity(intent);
	}
}
