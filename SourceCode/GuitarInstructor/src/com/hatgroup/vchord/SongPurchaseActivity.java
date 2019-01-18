package com.hatgroup.vchord;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.daos.DaoSession;
import com.hatgroup.vchord.daos.SongDao;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.HttpClient;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;
import com.hatgroup.vchord.utils.WebService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SongPurchaseActivity extends SherlockFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_purchase_layout);
		
		final ListView listView = (ListView)findViewById(R.id.filterableList);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
			}
		});
		
		CheckBox chkSelectAll = (CheckBox)findViewById(R.id.chkSelectAll);
		chkSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					((MyArrayListAdapter)listView.getAdapter()).selectAll();
				}
				else{
					((MyArrayListAdapter)listView.getAdapter()).deselectAll();
				}
				int count = listView.getCount();
				for(int i=0; i<count; i++){
					View listItem = listView.getChildAt(i);
					if(listItem != null){
						CheckBox chkPurchaseItem = (CheckBox)listItem.findViewById(R.id.chkItem);
						chkPurchaseItem.setChecked(isChecked);						
					}
				}
			}
		});
		
		Button btnPurchaseSongs = (Button)findViewById(R.id.btnPurchaseSongs);
		btnPurchaseSongs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				purchaseSongs();
			}
		});
		
		loadSongFromWS();
	}

	private void purchaseSongs(){
		ListView listView = (ListView)findViewById(R.id.filterableList);
		String ids = ((MyArrayListAdapter)listView.getAdapter()).getSelectedIds();
		if(ids.length() == 0){
			showAlertDialog("Chưa chọn bài hát");
			return;
		}
		RequestParams reqParams = new RequestParams();
		reqParams.put("imei", Utilities.getEMEI(this));
		reqParams.put("song", ids);
		
		HttpClient.getInstance().post(Constants.PURCHASE_SONG_URL, reqParams, new AsyncHttpResponseHandler() {
		
			Activity activity = SongPurchaseActivity.this;
			ProgressDialog progressDialog = null;
			
			@Override
			public void onStart() {
				super.onStart();
				progressDialog = ProgressDialog.show(SongPurchaseActivity.this, 
						ThemeUtils.getString(getApplicationContext(),R.string.text_title_progress), 
						ThemeUtils.getString(getApplicationContext(), R.string.text_connecting));
			}

			private void stopProgressDialog(){
				if(progressDialog != null){
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
						
			@Override
			public void onFailure(Throwable t, String respMessage) {
				super.onFailure(t, respMessage);
				stopProgressDialog();
				//Toast.makeText(activity, ThemeUtils.getString(activity, R.string.msg_error_network),Toast.LENGTH_SHORT).show();
				showAlertDialog(ThemeUtils.getString(activity, R.string.msg_error_network));
			}
			
			@Override
			public void onSuccess(String response) {
				super.onSuccess(response);
				stopProgressDialog();
				Map<String, String> results = Utilities.jsonToMap(response);
				if(!Utilities.IsNullOrEmpty(results.get("msgcode"))){
					//Toast.makeText(activity, results.get("message"),Toast.LENGTH_SHORT).show();
					showAlertDialog(results.get("message"));
				}
				else{
					List<Song> songs = Utilities.jsonToSongs(response);
					try{
						insertSongs(songs);
					}
					catch(Exception ex){
						showAlertDialog("Cập nhật dữ liệu thất bại: " + ex.getMessage());
					}
				}
			}
		});
	}
	
	private void loadSongFromWS(){
		runOnUiThread(new Runnable() {
			public void run() {
				String json = WebService.performGet(Constants.LIST_NEW_SONG_URL);
				List<PurchasedSong> songList = jsonToSongList(json);
				ListView listView = (ListView)findViewById(R.id.filterableList);
				MyArrayListAdapter adapter = new MyArrayListAdapter(SongPurchaseActivity.this, R.layout.song_filterable_list_item, songList);
				listView.setAdapter(adapter);
			}
		});
	}
	
	private void insertSongs(final List<Song> songs){
		SongRepo songRepo = new SongRepo(this);
		DaoSession daoSession = songRepo.getDaoSession();
		final SongDao songDao = daoSession.getSongDao();
		daoSession.runInTx(new Runnable() {
			@Override
			public void run() {
				for (Song newSong : songs) {
					songDao.insert(newSong);
				}
			}
		});
		
	}
	
	private void showAlertDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(SongPurchaseActivity.this);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	
	private class MyArrayListAdapter extends ArrayAdapter<PurchasedSong> {
		
		private List<PurchasedSong> listData;
		private Set<Integer> selectedIds;
		
		public MyArrayListAdapter(Context context, int resource, List<PurchasedSong> listData) {
			super(context, resource, listData);
			this.listData = listData;
			selectedIds = new LinkedHashSet<Integer>();
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			final PurchasedSong song = listData.get(position);
			ViewHolder viewHolder;
			if (v == null) {
				LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.song_filterable_list_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.txtSongTitle = (TextView)v.findViewById(R.id.txtSongTitle);
				viewHolder.txtSongPrice = (TextView)v.findViewById(R.id.txtSongPrice);
				viewHolder.txtSongId = (TextView)v.findViewById(R.id.txtSongId);
				viewHolder.chkItem = (CheckBox)v.findViewById(R.id.chkItem);
				viewHolder.chkItem.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View checkbok) {
						CheckBox theCheckBox = (CheckBox)checkbok;
						ViewGroup parentView = (ViewGroup)checkbok.getParent();
						String id = ((TextView)parentView.findViewById(R.id.txtSongId)).getText().toString();
						if(theCheckBox.isChecked()){							
							selectedIds.add(Integer.parseInt(id));
						}
						else{
							selectedIds.remove(Integer.parseInt(id));								
						}
					}
				});
				v.setTag(viewHolder);
			} 
			else {
				viewHolder = (ViewHolder) v.getTag();
			}
			if (song != null) {
				viewHolder.txtSongTitle.setText(song.getTitle());
				viewHolder.txtSongPrice.setText(song.getPrice()+"");	
				viewHolder.txtSongId.setText(song.getId()+"");
				boolean isSelected = selectedIds.contains(song.getId());
				viewHolder.chkItem.setChecked(isSelected);
			}
			return v;
		}
		
		public void selectAll(){
			selectedIds.clear();
			for(PurchasedSong song : listData){
				selectedIds.add(song.getId());
			}
		}
		
		public void deselectAll(){
			selectedIds.clear();
		}
	
		public String getSelectedIds(){
			StringBuilder sb = new StringBuilder();
			for(int id : selectedIds){
				sb.append(id);
				sb.append(",");
			}
			return sb.toString();
		}
	}

	private static class ViewHolder {
		TextView txtSongTitle;
		TextView txtSongPrice;
		CheckBox chkItem;
		TextView txtSongId;
	}
	
	private static List<PurchasedSong> jsonToSongList(String json){
		List<PurchasedSong> songs = new ArrayList<PurchasedSong>();
		try {
			JSONArray jArray = new JSONArray(json);
			JSONObject jsonObject = null;
			for (int i = 0; i < jArray.length(); i++) {
				jsonObject = jArray.getJSONObject(i);
				PurchasedSong song = new PurchasedSong();
				int id = jsonObject.getInt("id");
				String title = jsonObject.getString("title");
				double price = jsonObject.getDouble("price");
				
				song.setId(id);
				song.setTitle(title);
				song.setPrice(price);
				songs.add(song);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	private static class PurchasedSong implements Comparable<PurchasedSong>{
		private int id;
		private String title;
		private double price;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		@Override
		public int hashCode(){
			return id;
		}
		@Override
		public boolean equals(Object o){
			if(o != null && o instanceof PurchasedSong){
				return this.id == ((PurchasedSong)o).getId();
			}
			return false;
		}
		@Override
		public int compareTo(PurchasedSong another) {
			if(this.getTitle() != null && another.getTitle() != null){
				return this.getTitle().compareTo(another.getTitle());
			}
			return 0;
		}
	}
}



//android.database.sqlite.SQLiteConstraintException: PRIMARY KEY must be unique (code 19)