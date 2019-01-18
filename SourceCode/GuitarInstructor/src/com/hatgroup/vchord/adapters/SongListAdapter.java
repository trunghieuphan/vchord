package com.hatgroup.vchord.adapters;

//
//import java.util.List;
//
//import com.hatgroup.vchord.R;
//import com.guitar.instructor.common.SongModel;
//import com.guitar.instructor.entities.Artist;
//import com.guitar.instructor.entities.Song;
//import com.guitar.instructor.repo.SongRepo;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class SongListAdapter extends ArrayAdapter<Song> {
//	
//    private List<Song> songList;
//    private int mTextViewResourceId;
//    private Context mContext;
//    private int mPage = 0;    
//    private int mTotal = 0;
//    private SongRepo mSongRepo;
//    private SongModel mSongModel;
//     
//    public SongListAdapter(Context context, int textViewResourceId,
//            List<Song> songList, int total, SongModel songModel) {
//        super(context, textViewResourceId, songList);        
//        this.songList = songList;
//        this.mTextViewResourceId = textViewResourceId;
//        this.mContext = context;        
//        this.mTotal = total; 
//        this.mSongModel = songModel;
//        mSongRepo = new SongRepo(context);
//    }
// 
//    @Override
//    public int getCount() {    	
//        return songList.size();
//    }
//
//    @Override
//    public Song getItem(int index) {    	
//        return songList.get(index);
//    } 
//    
//    @Override
//    public View getView(int position, View v, ViewGroup parent) {
//        // Keeps reference to avoid future findViewById()
//        ViewHolder viewHolder;        
//        int count = songList.size();
//        if (position == (count -1) && mTotal > count) {
////        	 List<Song> songs = mSongRepo.get
//        	mPage++;
//        	//Toast.makeText(mContext, "Get list count: " + mPage, 100).show();	
//        	List<Song> tempSongs = mSongRepo.getLimitSongs(mPage, mSongModel);
//        	if (tempSongs.size() > 0) {
//        		songList.addAll(tempSongs);
//        		this.notifyDataSetChanged();
//        	}
//        }
//        
//        if (v == null) {
//            LayoutInflater li = (LayoutInflater) mContext.getSystemService(
//                    Context.LAYOUT_INFLATER_SERVICE);
//            v = li.inflate(mTextViewResourceId, parent, false);
// 
//            viewHolder = new ViewHolder();            
//            viewHolder.titleTextView = (TextView) v.findViewById(R.id.titleSongListItemTextView);
//            viewHolder.singerTextView = (TextView) v.findViewById(R.id.singerTextView);
// 
//            v.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) v.getTag();
//        }
// 
//        Song song = songList.get(position);        
//        if (song != null) {
//            viewHolder.titleTextView.setText(song.getTitle());
//            Artist artist = song.getSongsinger();
//            if (artist != null) {
//            	String artistName = artist.getName();
//            	viewHolder.singerTextView.setText(artistName);
////            	if (!artistName.equals("")) {
////            		viewHolder.singerTextView.setText(artistName);
////            	}
////            	else {
////            		viewHolder.singerTextView.setText("");
////            	}
//            }
//            else {
//            	viewHolder.singerTextView.setText("");
//            }
//        }
////        else {
////        	viewHolder.titleTextView.setText("Không tìm thấy kết quả!");
////        }
//        return v;
//    }
//	
//	static class ViewHolder {	
//		TextView titleTextView;		
//		TextView singerTextView;
//	}
//}

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hatgroup.vchord.R;
//import com.guitar.instructor.entities.Artist;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.utils.Utilities;

public class SongListAdapter extends ArrayAdapter<Song> {
	private List<Song> listData;
	private Context context;

	public SongListAdapter(Context context, int resource, List<Song> listData) {
		super(context, resource, listData);
		this.context = context;
		this.listData = listData;

	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// Keeps reference to avoid future findViewById()
		SongViewHolder viewHolder;

		if (v == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.song_list_item, parent, false);

			viewHolder = new SongViewHolder();
			viewHolder.txtSongName = (TextView) v
					.findViewById(R.id.txtSongName);
			viewHolder.txtSingerName = (TextView) v
					.findViewById(R.id.txtSingerName);

			v.setTag(viewHolder);
		} else {
			viewHolder = (SongViewHolder) v.getTag();
		}

		Song song = listData.get(position);
		if (song != null) {
			viewHolder.txtSongName.setText(song.getTitle());
//			Artist singer = song.getSongsinger();
			String singerName = song.getSinger_name();
			viewHolder.txtSingerName.setText(!Utilities.IsNullOrEmpty(singerName) ? singerName
					 : Constants.EMPTY_SINGER_NAME);

		}
		return v;
	}

	static class SongViewHolder {
		TextView txtSongName;
		TextView txtSingerName;
	}
}
