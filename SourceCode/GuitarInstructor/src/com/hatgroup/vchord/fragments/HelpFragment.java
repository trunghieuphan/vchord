package com.hatgroup.vchord.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.adapters.ExpandableListAdapter;
import com.hatgroup.vchord.common.Constants;

public class HelpFragment extends TheFragment{
	
	ExpandableListView expListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.help_page_layout, container, false);
		expListView = (ExpandableListView) layout.findViewById(R.id.topics_list);
		final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(getActivity());
		expListView.setAdapter(expListAdapter);
		//setGroupIndicatorToRight();
		return layout;
	}		

	@SuppressWarnings("unused")
	private void setGroupIndicatorToRight() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		//expListView.setIndicatorBounds(width - getDipsFromPixel(35), width - getDipsFromPixel(5));
		expListView.setIndicatorBounds(500, 550);
	}
	
	// Convert pixel to dip
	public int getDipsFromPixel(float pixels) {
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}
	
	public String getFragmentName(){
		return Constants.TAB_LABELS[2];
	}

}
