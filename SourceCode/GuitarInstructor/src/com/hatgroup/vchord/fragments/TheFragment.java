package com.hatgroup.vchord.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class TheFragment extends SherlockFragment {

	private int layoutId;

	public TheFragment(int layoutId) {
		this.layoutId = layoutId;
	}

	public TheFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		View rootView = inflater.inflate(layoutId, container, false);
		return rootView;
	}

	public static TheFragment create(int layoutId) {
		TheFragment fragment = new TheFragment(layoutId);
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public String getFragmentName() {
		return "";
	}
}
