package com.hatgroup.vchord.fragments;

//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//
//import com.guitar.instructor.MusicContainerActivity;
//import com.hatgroup.vchord.R;
//import com.guitar.instructor.adapters.FavoriteItemAdapter;
//import com.guitar.instructor.adapters.SongListAdapter;
//import com.guitar.instructor.common.Constants;
//import com.guitar.instructor.entities.Song;
//import com.guitar.instructor.repo.SongRepo;
//
//public class FavouriteFragment extends TheFragment {
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.favorites_page_layout, container, false);
//		return view;
//	}
//
//	@Override
//	public void onStart() {
//		loadData();
//		super.onStart();
//	}
//	
//	private void loadData(){
//		final List<Song> songs = new ArrayList<Song>();
//		final ListView listFavorite = (ListView)getActivity().findViewById(R.id.listFavorite);
//		listFavorite.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
//				Song selectedSong = (Song)listFavorite.getAdapter().getItem(position);
//				Intent intent = new Intent(getActivity(), MusicContainerActivity.class);
//				intent.putExtra(Constants.SERIALIZED_SONG, selectedSong);
//				getActivity().startActivity(intent);
//			}
//		});
//		songs.addAll(pullDataFromDB());
//		ListAdapter adapter = new FavoriteItemAdapter(listFavorite.getContext(), R.layout.row_layout, songs);
//		listFavorite.setAdapter(adapter);
//		
////		int total = songRepo.getTotalSongs(songModel);
////		ListAdapter adapter = new SongListAdapter(listFavorite.getContext(), total, R.layout.song_list_item, songs);
////		listFavorite.setAdapter(adapter);
//	}
//	/*
//	private class DownloadSongsTask extends AsyncTask<String, Void, String> {
//		String json = "";
//		@Override
//		public void onPreExecute(){
//			progressDialog = ProgressDialog.show(FavouriteFragment.this.getActivity(), 
//					"working...", "Request data from Web Service");
//		}
//	    @Override
//	    protected String doInBackground(String... urls) {
//	    	if(!Constants.USE_LOCAL_DATA){
//	    		json = WebService.performGet(Constants.SONG_URL);	
//	    	}
//	    	return null;
//		}
//	    @Override
//	    protected void onPostExecute(String result) {
//	    	if(progressDialog != null && progressDialog.isShowing()){
//				progressDialog.dismiss();
//			}
//			List<Song> songs = Collections.emptyList();
//			if(Constants.USE_LOCAL_DATA){
//				songs = getLocalData();
//			}
//			else{
//				songs = Utilities.jsonToSongs(json);
//			}
//			final ListView listFavorite = (ListView)getActivity().findViewById(R.id.listFavorite);
//			ListAdapter adapter = new FavoriteItemAdapter(listFavorite.getContext(), R.layout.row_layout, songs);
//			listFavorite.setAdapter(adapter);
//	    }	    
//	  }
//	  
//	  private List<Song> getLocalData(){
//		List<Song> songs = new ArrayList<Song>();
//		Song song = new Song();
//		song.setId(1+"");
//		song.setTitle("Tinh thoi xot xa");
//		songs.add(song);
//		song = new Song();
//		song.setId(2+"");
//		song.setTitle("Hon da co don");
//		songs.add(song);
//		return songs;
//	}
//*/
//	
//	public String getFragmentName(){
//		return Constants.TAB_LABELS[1];
//	}
//
//	
//	private List<Song> pullDataFromDB(){
//		SongRepo songRepo = new SongRepo(this.getActivity());
//		//List<Song> songs = songRepo.getAllSongs(null);
//		List<Song> songs = songRepo.getFavoriteSongs();
//		return songs;
//	}
//}

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

public class FavouriteFragment extends TheFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final SearchCriteria songModel = new SearchCriteria();
		songModel.setIsFavorite(true);

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
