package com.hatgroup.vchord.adapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.hatgroup.vchord.entities.Song;

public class BaseItemAdapter extends ArrayAdapter<Song> {

	List<Song> list;

	public BaseItemAdapter(Context context, int textViewResourceId,
			List<Song> objects) {
		super(context, textViewResourceId, objects);
		this.list = objects;
	}

	public List<Song> getDataList() {
		return list;
	}
}