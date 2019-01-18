package com.hatgroup.vchord.fragments;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.hatgroup.vchord.R;
import com.hatgroup.vchord.adapters.KeyValueArrayAdapter;
import com.hatgroup.vchord.adapters.KeyValueArrayAdapter.KeyValue;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.daos.DaoSession;
import com.hatgroup.vchord.daos.SongDao;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.HttpClient;
import com.hatgroup.vchord.utils.ListUtils;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SongPurchaseFragment extends TheFragment {

	private View layout;
	EditText inputSearch;
	IcsSpinner cbbPriceFilter;

	@Override
	public void onPause() {
		super.onPause();
		inputSearch.setText("");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		layout = inflater.inflate(R.layout.song_purchase_layout, container,
				false);
		// setContentView(R.layout.song_purchase_layout);

		final ListView listView = (ListView) layout
				.findViewById(R.id.filterableList);
		listView.setEmptyView(layout.findViewById(R.id.emptyElement));
		listView.setTextFilterEnabled(true);

		inputSearch = (EditText) layout.findViewById(R.id.txtSearchBox);
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				if (listView.getAdapter() != null) {
					Filter theFilter = ((MyArrayListAdapter) listView.getAdapter()).getFilter();
					theFilter.filter(cs.toString());
				}
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		
		cbbPriceFilter = (IcsSpinner)layout.findViewById(R.id.cbbPriceFilter);
		KeyValueArrayAdapter adapter = new KeyValueArrayAdapter(getActivity(), R.layout.search_spinner_item);
		adapter.setEntries(getResources().getStringArray(R.array.price_list_filter));
		adapter.setEntryValues(getResources().getStringArray(R.array.price_list_filter));
		cbbPriceFilter.setAdapter(adapter);
		cbbPriceFilter.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(IcsAdapterView<?> parent, View view,
					int position, long id) {
				if(listView.getAdapter() != null){
					Filter theFilter = ((MyArrayListAdapter) listView.getAdapter()).getFilter();
					theFilter.filter(inputSearch.getText().toString());
				}
			}
			@Override
			public void onNothingSelected(IcsAdapterView<?> parent) {
			}
		});

		CheckBox chkSelectAll = (CheckBox) layout
				.findViewById(R.id.chkSelectAll);
		chkSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					((MyArrayListAdapter) listView.getAdapter()).selectAll();
				} else {
					((MyArrayListAdapter) listView.getAdapter()).deselectAll();
				}
				int count = listView.getCount();
				for (int i = 0; i < count; i++) {
					View listItem = listView.getChildAt(i);
					if (listItem != null) {
						CheckBox chkPurchaseItem = (CheckBox) listItem
								.findViewById(R.id.chkItem);
						chkPurchaseItem.setChecked(isChecked);
					}
				}
				updateTotal();
			}
		});

		Button btnPurchaseSongs = (Button) layout
				.findViewById(R.id.btnPurchaseSongs);
		btnPurchaseSongs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				purchaseSongs();
			}
		});

		loadSongFromWS();
		return layout;
	}

	private void updateTotal(){
		//Calculate the total
		ListView listView = (ListView) layout.findViewById(R.id.filterableList);
		Set<Integer> selectedIds = ((MyArrayListAdapter)listView.getAdapter()).getSelectedIdsSet();
		List<PurchasedSong> fullList = ((MyArrayListAdapter)listView.getAdapter()).getFullList();
		int total = 0;
		for(int id : selectedIds){
			PurchasedSong song = ListUtils.findSongInlist(id, fullList, "");
			if(song != null){
				total += song.getPrice();
			}
		}
		TextView txtTotal = (TextView)layout.findViewById(R.id.txtTotal);
		txtTotal.setText(total + "");
	}

	private void updateTotal(int value){
		//Calculate the total
		TextView txtTotal = (TextView)layout.findViewById(R.id.txtTotal);
		int total = Utilities.parseInt(txtTotal.getText().toString());
		txtTotal.setText(total + value + "");
	}
	
	private void purchaseSongs() {
		ListView listView = (ListView) layout.findViewById(R.id.filterableList);
		String ids = ((MyArrayListAdapter) listView.getAdapter())
				.getSelectedIds();
		if (ids.length() == 0) {
			Toast.makeText(getActivity(),
					 ThemeUtils.getString(getActivity(),
					 R.string.msg_require_select_item),Toast.LENGTH_SHORT).show();
			return;
		}
		RequestParams reqParams = new RequestParams();
		reqParams.put("imei", Utilities.getEMEI(getActivity()));
		reqParams.put("song", ids);

		HttpClient.getInstance().post(Constants.PURCHASE_SONG_URL, reqParams,
				new AsyncHttpResponseHandler() {

					Activity activity = getActivity();
					ProgressDialog progressDialog = null;

					@Override
					public void onStart() {
						super.onStart();
						progressDialog = ProgressDialog.show(activity,
								ThemeUtils.getString(activity,
										R.string.text_title_progress),
								ThemeUtils.getString(activity,
										R.string.text_connecting));
					}

					private void stopProgressDialog() {
						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;
						}
					}

					@Override
					public void onFailure(Throwable t, String respMessage) {
						super.onFailure(t, respMessage);
						stopProgressDialog();
						 Toast.makeText(activity,
						 ThemeUtils.getString(activity,
						 R.string.msg_error_network),Toast.LENGTH_SHORT).show();
//						showAlertDialog(ThemeUtils.getString(activity,
//								R.string.msg_error_network));
					}

					@Override
					public void onSuccess(String response) {
						super.onSuccess(response);
						stopProgressDialog();
						Map<String, String> results = Utilities
								.jsonToMap(response);
						if (!Utilities.IsNullOrEmpty(results.get("msgcode"))) {
							 Toast.makeText(activity,
							 results.get("message"),Toast.LENGTH_SHORT).show();
							//showAlertDialog(results.get("message"));
						} else {
							List<Song> songs = Utilities.jsonToSongs(response);
							try {
								insertSongs(songs);
							} catch (Exception ex) {
								Toast.makeText(activity,
										 ThemeUtils.getString(activity,
										 R.string.msg_error_update),Toast.LENGTH_SHORT).show();
//								
//								showAlertDialog("Cập nhật dữ liệu thất bại: "
//										+ ex.getMessage());
							}
						}
					}
				});
	}

	private void loadSongFromWS() {

		HttpClient.getInstance().get(Constants.LIST_NEW_SONG_URL,
				new AsyncHttpResponseHandler() {
					Activity activity = getActivity();
					ProgressDialog progressDialog = null;

					@Override
					public void onStart() {
						super.onStart();
						progressDialog = ProgressDialog.show(activity,
								ThemeUtils.getString(activity,
										R.string.text_title_progress),
								ThemeUtils.getString(activity,
										R.string.text_connecting));
					}

					private void stopProgressDialog() {
						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;
						}
					}

					@Override
					public void onFailure(Throwable t, String respMessage) {
						super.onFailure(t, respMessage);
						stopProgressDialog();
//						showAlertDialog(ThemeUtils.getString(activity,
//								R.string.msg_error_network));
						Toast.makeText(activity,
								 ThemeUtils.getString(activity,
								 R.string.msg_error_network),Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(String json) {
						super.onSuccess(json);
						stopProgressDialog();
						List<PurchasedSong> songList = jsonToSongList(json);
						//Filter again to remove already existed songs (bought already)
						List<Integer> ids = ListUtils.getSongIdsList(songList, "");
						final SongRepo songRepo = new SongRepo(SongPurchaseFragment.this.getActivity());
						List<Song> conflicts = songRepo.getAllSongs(ids);
						ids = ListUtils.getSongIdsList(conflicts);
						CopyOnWriteArrayList<PurchasedSong> shortedList = new CopyOnWriteArrayList<PurchasedSong>(songList);
						for(PurchasedSong song : shortedList){
							if(ids.contains(song.getId())){
								shortedList.remove(song);
							}
						}
						// List<PurchasedSong> songList = createMockupData();
						ListView listView = (ListView) layout
								.findViewById(R.id.filterableList);
						MyArrayListAdapter adapter = new MyArrayListAdapter(
								getActivity(),
								R.layout.song_filterable_list_item, new ArrayList<PurchasedSong>(shortedList));
						listView.setAdapter(adapter);
					}

//					private List<PurchasedSong> createMockupData() {
//						// Listview Data
//						String products[] = { "Dell Inspiron", "HTC One X",
//								"HTC Wildfire S", "HTC Sense",
//								"HTC Sensation XE", "iPhone 4S",
//								"Samsung Galaxy Note 800", "Samsung Galaxy S3",
//								"MacBook Air", "Mac Mini", "MacBook Pro" };
//
//						List<PurchasedSong> songList = new ArrayList<PurchasedSong>();
//						for (int i = 0; i < products.length; i++) {
//							PurchasedSong song = new PurchasedSong();
//							song.setId(i + 1);
//							song.setPrice(1);
//							song.setTitle(products[i]);
//							songList.add(song);
//						}
//						return songList;
//					}
				});
		/*
		 * getActivity().runOnUiThread(new Runnable() { public void run() {
		 * String json = WebService.performGet(Constants.LIST_NEW_SONG_URL);
		 * List<PurchasedSong> songList = jsonToSongList(json); ListView
		 * listView = (ListView)layout.findViewById(R.id.filterableList);
		 * MyArrayListAdapter adapter = new MyArrayListAdapter(getActivity(),
		 * R.layout.song_filterable_list_item, songList);
		 * listView.setAdapter(adapter); } });
		 */
	}

	private void insertSongs(final List<Song> songs) {
		SongRepo songRepo = new SongRepo(getActivity());
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

//	private void showAlertDialog(String message) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		builder.setMessage(message);
//		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//			}
//		});
//		AlertDialog alertDialog = builder.create();
//		alertDialog.show();
//	}

	private class MyArrayListAdapter extends ArrayAdapter<PurchasedSong> {

		private List<PurchasedSong> fullList;
		private ArrayList<PurchasedSong> filterList;
		private Set<Integer> selectedIds;
		private TheFilter theFilter;
		private Object mLock = new Object();

		public MyArrayListAdapter(Context context, int resource,
				List<PurchasedSong> listData) {
			super(context, resource, listData);
			this.fullList = listData;
			this.filterList = new ArrayList<PurchasedSong>(listData);
			selectedIds = new LinkedHashSet<Integer>();
		}

		@Override
		public void add(PurchasedSong item) {
			filterList.add(item);
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			final PurchasedSong song = filterList.get(position);
			ViewHolder viewHolder;
			if (v == null) {
				LayoutInflater li = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.song_filterable_list_item, parent,
						false);
				viewHolder = new ViewHolder();
				viewHolder.txtSongTitle = (TextView) v
						.findViewById(R.id.txtSongTitle);
				viewHolder.txtSongPrice = (TextView) v
						.findViewById(R.id.txtSongPrice);
				viewHolder.txtSongId = (TextView) v
						.findViewById(R.id.txtSongId);
				viewHolder.txtUpdateDate = (TextView) v
						.findViewById(R.id.txtUpdateDate);
				viewHolder.chkItem = (CheckBox) v.findViewById(R.id.chkItem);
				viewHolder.chkItem.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View checkbok) {
						CheckBox theCheckBox = (CheckBox) checkbok;
						ViewGroup parentView = (ViewGroup) checkbok.getParent();
						String id = ((TextView) parentView
								.findViewById(R.id.txtSongId)).getText()
								.toString();
						String price = ((TextView) parentView
								.findViewById(R.id.txtSongPrice)).getText()
								.toString();
						if (theCheckBox.isChecked()) {
							selectedIds.add(Integer.parseInt(id));
						} else {
							selectedIds.remove(Integer.parseInt(id));
						}
						if(!"0".equals(String.valueOf(price))){
							updateTotal(Utilities.parseInt(price) * (theCheckBox.isChecked() ? 1 : -1));
						}
					}
				});
				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) v.getTag();
			}
			if (song != null) {
				viewHolder.txtSongTitle.setText(song.getTitle());
				viewHolder.txtSongPrice.setText(song.getPrice() + "");
				viewHolder.txtSongId.setText(song.getId() + "");
				viewHolder.txtUpdateDate.setText(song.getUpdateDate());
				boolean isSelected = selectedIds.contains(song.getId());
				viewHolder.chkItem.setChecked(isSelected);
			}
			return v;
		}

		@Override
		public int getCount() {
			return filterList.size();
		}

		@Override
		public PurchasedSong getItem(int position) {
			return filterList.get(position);
		}

		@Override
		public int getPosition(PurchasedSong item) {
			return filterList.indexOf(item);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Implementing the Filterable interface.
		 */
		public Filter getFilter() {
			if (theFilter == null) {
				theFilter = new TheFilter();
			}
			return theFilter;
		}

		public void selectAll() {
			selectedIds.clear();
			for (PurchasedSong song : fullList) {
				selectedIds.add(song.getId());
			}
		}

		public void deselectAll() {
			selectedIds.clear();
		}

		public String getSelectedIds() {
			StringBuilder sb = new StringBuilder();
			for (int id : selectedIds) {
				sb.append(id);
				sb.append(",");
			}
			return sb.toString();
		}

		public Set<Integer> getSelectedIdsSet() {
			return selectedIds;
		}
		
		public List<PurchasedSong> getFullList(){
			return fullList;
		}
		
		@SuppressLint("DefaultLocale")
		private class TheFilter extends Filter {

			protected FilterResults performFiltering(CharSequence prefix) {
				// Initiate our results object
				FilterResults results = new FilterResults();
				ArrayList<PurchasedSong> newItems = new ArrayList<PurchasedSong>();
				String price = null;
				if(cbbPriceFilter != null && cbbPriceFilter.getSelectedItemPosition() > 0){
					price = ((KeyValue)cbbPriceFilter.getSelectedItem()).value.replace("đ", "");
				}

				if (prefix == null || prefix.length() == 0) {
					synchronized (mLock) {
						for(PurchasedSong song : fullList){
							if(price == null || String.valueOf(song.getPrice()).equals(price)){
								newItems.add(song);
							}
						}
					}
				} else {
					// Compare lower case strings
					String prefixString = prefix.toString().toLowerCase();
					for(PurchasedSong item : fullList) {
						String itemName = item.getuTitle().toString().toLowerCase();		
						if (itemName.contains(prefixString)) {
							if(price == null || String.valueOf(item.getPrice()).equals(price)){
								newItems.add(item);	
							}
						}
						/*
						 * if (itemName.startsWith(prefixString)) {
						 * newItems.add(item); } else { final String[] words =
						 * itemName.split(" "); final int wordCount =
						 * words.length; for (int k = 0; k < wordCount; k++) {
						 * if (words[k].startsWith(prefixString)) {
						 * newItems.add(item); break; } } }
						 */
					}
				}
				// Set and return
				results.values = newItems;
				results.count = newItems.size();				
				return results;
			}

			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence prefix, FilterResults results) {
				// noinspection unchecked
				filterList = (ArrayList<PurchasedSong>) results.values;
				// Let the adapter know about the updated list
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}

	}

	private static class ViewHolder {
		TextView txtSongTitle;
		TextView txtSongPrice;
		CheckBox chkItem;
		TextView txtSongId;
		TextView txtUpdateDate;
	}

	private static List<PurchasedSong> jsonToSongList(String json) {		
		List<PurchasedSong> songs = new ArrayList<PurchasedSong>();
		try {
			JSONArray jArray = new JSONArray(json);
			JSONObject jsonObject = null;
			for (int i = 0; i < jArray.length(); i++) {
				jsonObject = jArray.getJSONObject(i);
				PurchasedSong song = new PurchasedSong();
				int id = jsonObject.getInt("id");
				String title = jsonObject.getString("title");
				int price = jsonObject.getInt("price");
				String u_Title = jsonObject.getString("unsigned_title");
				String update_date = jsonObject.getString("update_date");

				song.setId(id);
				song.setTitle(title);
				song.setPrice(price);
				song.setuTitle(u_Title);
				song.setUpdateDate(update_date);
				songs.add(song);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return songs;
	}

	public static class PurchasedSong implements Comparable<PurchasedSong> {
		private int id;
		private String title;
		private int price;
		private String uTitle;

		public String getuTitle() {
			return uTitle;
		}

		public void setuTitle(String uTitle) {
			this.uTitle = uTitle;
		}

		public String getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		private String updateDate;

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

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		@Override
		public String toString() {
			return this.uTitle;
		}

		@Override
		public boolean equals(Object o) {
			if (o != null && o instanceof PurchasedSong) {
				return this.id == ((PurchasedSong) o).getId();
			}
			return false;
		}

		@Override
		public int compareTo(PurchasedSong another) {
			if (this.getTitle() != null && another.getTitle() != null) {
				return this.getTitle().compareTo(another.getTitle());
			}
			return 0;
		}
	}

}