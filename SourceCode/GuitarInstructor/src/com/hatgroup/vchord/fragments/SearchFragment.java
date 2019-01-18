package com.hatgroup.vchord.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.hatgroup.vchord.R;
import com.hatgroup.vchord.adapters.AutoCompleteAdapter;
import com.hatgroup.vchord.adapters.LevelAdapter;
import com.hatgroup.vchord.adapters.MelodyAdapter;
import com.hatgroup.vchord.adapters.RhythmAdapter;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.common.SearchCriteria;
import com.hatgroup.vchord.entities.Level;
import com.hatgroup.vchord.entities.Melody;
import com.hatgroup.vchord.entities.Rhythm;
import com.hatgroup.vchord.repo.LevelRepo;
import com.hatgroup.vchord.repo.MelodyRepo;
import com.hatgroup.vchord.repo.RhythmRepo;
import com.hatgroup.vchord.utils.Utilities;

public class SearchFragment extends TheFragment {

	private AutoCompleteAdapter mAutoAdapter;
	private SearchCriteria mSongModel;	
	private AutoCompleteTextView txtSearch;
	private RadioButton rdSong;
	private RadioButton rdSinger;
	private RadioButton rdAuthor;
	private IcsSpinner drpRhythm;
	private IcsSpinner drpDifficulty;
	private IcsSpinner drpMelody;
	private RhythmAdapter mRhythmAdapter;
	private MelodyAdapter mMelodyAdapter;
	private LevelAdapter mLevelAdapter;
	private Integer mRhythmId = 0;
	private Integer mMelodyId = 0;
	private Integer mLevelId = 0;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		View view = inflater.inflate(R.layout.search_page_layout, container, false);
		
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Utilities.hideSoftKeyboard(v);
				return false;
			}
		});
		
		final Button btnSearch = (Button) view.findViewById(R.id.btnSearch);
		txtSearch = (AutoCompleteTextView) view.findViewById(R.id.txtAutoCompleteSearch);
		rdSong = (RadioButton) view.findViewById(R.id.radio_search_by_song);
		rdSinger = (RadioButton) view.findViewById(R.id.radio_search_by_singer);
		rdAuthor = (RadioButton) view.findViewById(R.id.radio_search_by_author);
		
		drpRhythm = (IcsSpinner) view.findViewById(R.id.drop_down_rhythm);
		fillDataRhythmSpinner();
		
		drpDifficulty = (IcsSpinner) view.findViewById(R.id.drop_down_difficulty);
		fillDataLevelSpinner();
		
		drpMelody = (IcsSpinner) view.findViewById(R.id.drop_down_melody);
		fillDataMelodySpinner();
		
		txtSearch.setThreshold(1);
		txtSearch.setAdapter(getAutocompleteAdapter());

		rdSong.setOnClickListener(new RadioButton.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				txtSearch.setHint(getString(R.string.txt_song_name_hint));
				
				txtSearch.setAdapter(getAutocompleteAdapter());
			}
		});
		
		rdSinger.setOnClickListener(new RadioButton.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				txtSearch.setHint(getString(R.string.txt_singer_name_hint));
				txtSearch.setAdapter(getAutocompleteAdapter());
			}
		});
		
		rdAuthor.setOnClickListener(new RadioButton.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				txtSearch.setHint(getString(R.string.txt_author_name_hint));
				txtSearch.setAdapter(getAutocompleteAdapter());
			}
		});	
		
	
		drpDifficulty.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(IcsAdapterView<?> parent, View view,
					int position, long id) {
				Level level = mLevelAdapter.getItem(position);
				if (level != null) {
					mLevelId = level.getId();
				}
				
			}

			@Override
			public void onNothingSelected(IcsAdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		drpRhythm.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(IcsAdapterView<?> parent, View view,
					int position, long id) {
				Rhythm rhythm = mRhythmAdapter.getItem(position);
				if (rhythm != null) {
					mRhythmId = rhythm.getId();
				}
				
			}

			@Override
			public void onNothingSelected(IcsAdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		drpMelody.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(IcsAdapterView<?> parent, View view,
					int position, long id) {
				Melody melody = mMelodyAdapter.getItem(position);
				if (melody != null) {
					mMelodyId = melody.getId();
				}
			}

			@Override
			public void onNothingSelected(IcsAdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
			
		btnSearch.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();				
				bundle.putSerializable("SONG_MODEL", getSongModel());
				
				SongListFragment fragment = new SongListFragment();
				fragment.setArguments(bundle);
				
	    		FragmentTransaction trans = SearchFragment.this.getFragmentManager().beginTransaction();	    				
	    		trans.addToBackStack(null);
	    		trans.replace(R.id.fragment_container, fragment);
	    		trans.commit();
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		txtSearch.setAdapter(getAutocompleteAdapter());
	}
	
	private AutoCompleteAdapter getAutocompleteAdapter() {
		if (mAutoAdapter == null) {
			mAutoAdapter = new AutoCompleteAdapter(getActivity(), R.layout.search_autocomplete_item);
		}
		mAutoAdapter.setSongModel(getSongModel());
		return mAutoAdapter;
	}
	
	private SearchCriteria getSongModel() {
		if (mSongModel == null) {
			mSongModel = new SearchCriteria();
		}
		
		Boolean isSinger = rdSinger.isChecked();
		Boolean isSong = rdSong.isChecked();
//		String level = getResources().getStringArray(R.array.level_array_values)[drpDifficulty.getSelectedItemPosition()];
		//String rhythm = getResources().getStringArray(R.array.rhythm_array_values)[drpRhythm.getSelectedItemPosition()];		
//		String melody = getResources().getStringArray(R.array.melody_array_values)[drpMelody.getSelectedItemPosition()];		
		String title = txtSearch.getText().toString();
		
		mSongModel.setIsSinger(isSinger);
		mSongModel.setIsSong(isSong);
		mSongModel.setLevel(mLevelId);
		mSongModel.setRhythm(mRhythmId);
		mSongModel.setMelody(mMelodyId);
		mSongModel.setTitle(title);	
		
		return mSongModel;
	}
	
	private void fillDataRhythmSpinner(){
		List<Rhythm> rhythms = new ArrayList<Rhythm>();
		
		Rhythm rhythm = new Rhythm();
		rhythm.setId(0);
		rhythm.setName(getResources().getString(R.string.rhythm_prompt));		
		rhythms.add(rhythm);
		
		RhythmRepo rhythmRepo = new RhythmRepo(getActivity());
		List<Rhythm> tempRhythms = rhythmRepo.getRhythms("", "");
		rhythms.addAll(tempRhythms);
		mRhythmAdapter = new RhythmAdapter(getActivity(),R.layout.search_spinner_item, rhythms);			
		drpRhythm.setAdapter(mRhythmAdapter);
	}
	
	private void fillDataMelodySpinner(){
		List<Melody> melodies = new ArrayList<Melody>();
		
		Melody melody = new Melody();
		melody.setId(0);
		melody.setName(getResources().getString(R.string.melody_prompt));		
		melodies.add(melody);
		
		MelodyRepo melodyRepo = new MelodyRepo(getActivity());
		List<Melody> tempMelodies = melodyRepo.getMelodies("", "");
		melodies.addAll(tempMelodies);
		mMelodyAdapter = new MelodyAdapter(getActivity(),R.layout.search_spinner_item, melodies);			
		drpMelody.setAdapter(mMelodyAdapter);
	}
	
	private void fillDataLevelSpinner(){
		List<Level> levels = new ArrayList<Level>();
		
		Level level = new Level();
		level.setId(0);
		level.setName(getResources().getString(R.string.difficulty_prompt));		
		levels.add(level);
		
		LevelRepo levelRepo = new LevelRepo(getActivity());
		List<Level> tempLevels = levelRepo.getLevels("", "");
		levels.addAll(tempLevels);
		mLevelAdapter = new LevelAdapter(getActivity(),R.layout.search_spinner_item, levels);			
		drpDifficulty.setAdapter(mLevelAdapter);
	}
	
	public String getFragmentName(){
		return Constants.TAB_LABELS[0];
	}

}
