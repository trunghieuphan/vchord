package com.hatgroup.vchord.fragments;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hatgroup.vchord.MusicContainerActivity;
import com.hatgroup.vchord.R;
import com.hatgroup.vchord.adapters.SongListAdapter;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.common.EndlessScrollListener;
import com.hatgroup.vchord.common.SearchCriteria;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;

public class SongListFragment extends TheFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = this.getArguments();
		final SearchCriteria songModel = (SearchCriteria) bundle
				.getSerializable("SONG_MODEL");

		View view = inflater.inflate(R.layout.song_list_layout, container,
				false);
		final ListView songListView = (ListView) view
				.findViewById(R.id.songListView);

		// add header view of list
		TextView headerView = new TextView(getActivity());
		int padding = (int) getResources()
				.getDimension(R.dimen.padding_default);
		float textsize = getResources().getDimension(R.dimen.size_text_small);
		headerView.setPadding(padding, padding, padding, padding);
		headerView.setTextSize(textsize);
		//headerView.setGravity(Gravity.CENTER_HORIZONTAL);
		songListView.addHeaderView(headerView);

		// SongRepo songRepo = new SongRepo(getActivity());
		// if (songModel != null) {
		// songModel.setTitle(Utilities.removeDiacritic(songModel.getTitle()));
		// }
		// List<Song> songList = songRepo.getLimitSongs(0, songModel);
		// int total = songRepo.getTotalSongs(songModel);
		//
		// if(songList.size() > 0) {
		// SongListAdapter adapter = new SongListAdapter(getActivity(),
		// R.layout.song_list_item, songList, total, songModel);
		// songListView.setAdapter(adapter);
		// }
		// else {
		// TextView textView = new TextView(getActivity());
		// songListView.setVisibility(View.GONE);
		// String resultNotFound =
		// getResources().getString(R.string.result_not_found);
		// textView.setText(resultNotFound);
		// ((LinearLayout)view).addView(textView);
		// }

		final SongRepo songRepo = new SongRepo(getActivity());
		final List<Song> songList = songRepo.getLimitSongs(0, songModel);
		final long total = songRepo.getCountLimitSongs(songModel);
		final SongListAdapter adapter = new SongListAdapter(getActivity(),
				R.layout.song_list_item, songList);
		songListView.setAdapter(adapter);

		EndlessScrollListener l = new EndlessScrollListener() {

			@Override
			protected void loadMoreData(int page) {
				// TODO Auto-generated method stub
				songList.addAll(songRepo.getLimitSongs(page, songModel));
				adapter.notifyDataSetChanged();
			}

			@Override
			protected boolean hasMoreDataToLoad() {
				return total > songList.size();
			}
		};
		songListView.setOnScrollListener(l);

		songListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Song selectedSong = (Song) songListView.getAdapter().getItem(
						position);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Constants.SERIALIZED_SONG, selectedSong);
				Intent intent = new Intent(getActivity(),
						MusicContainerActivity.class);
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
			}
		});

		// set value for header view
		headerView.setText(Html.fromHtml(String.format(
				getString(R.string.text_search_result), total)));
		return view;
	}
}
