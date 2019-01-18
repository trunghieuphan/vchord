package com.hatgroup.vchord.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.daos.DaoSession;
import com.hatgroup.vchord.daos.SongDao;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.ListUtils;
import com.hatgroup.vchord.utils.Utilities;
import com.hatgroup.vchord.utils.WebService;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Activity context;

	public ExpandableListAdapter(Activity context) {
		this.context = context;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(Constants.HELP_TOPIC_LAYOUT[groupPosition], null);
		if (groupPosition == 0) {
			TextView tv = (TextView) convertView.findViewById(R.id.theIntroductionContent);
			tv.setText(Html.fromHtml(context.getResources().getString(R.string.help_intro)));
		} else if (groupPosition == 2) {
			View btnUpdateSongs = convertView.findViewById(R.id.btnUpdateSongs);
			btnUpdateSongs.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Check for update is allowed
					if (Constants.IS_UPDATE_ALLOWED) {
						update();
					} else {
						showAlertDialog(getString(R.string.lbl_warning), getString(R.string.lbl_feature_not_available));
					}
				}
			});
			//Load last updated date for songs
			final TextView txtSongLastUpdated = (TextView)convertView.findViewById(R.id.txtSongLastUpdated);
	    	context.runOnUiThread(new Runnable() {
				public void run() {
	    	    	SongRepo songRepo = new SongRepo(context);
	    	    	txtSongLastUpdated.setText(songRepo.getLastUpdateDate());
				}
			});
		}
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	public Object getGroup(int groupPosition) {
		return Constants.HELP_TOPICS[groupPosition];
	}

	public int getGroupCount() {
		return Constants.HELP_TOPICS.length;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String topicName = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.group_item, null);
		}
		TextView item = (TextView) convertView.findViewById(R.id.theTopic);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(topicName);
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private void update() {

		AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

			ProgressDialog progressDialog;

			@Override
			public void onPreExecute() {
				progressDialog = ProgressDialog.show(context, getString(R.string.lbl_please_wait),
						getString(R.string.lbl_downloading_data));
			}

			@Override
			protected String doInBackground(String... args) {
				final SongRepo songRepo = new SongRepo(context);
				final String songLastUpdatedDate = songRepo.getLastUpdateDate();
				final List<Song> downloadedSongs = new ArrayList<Song>();
				if (songLastUpdatedDate != null) {
					String url = Constants.SONG_UPDATE_URL + songLastUpdatedDate + "&imei=" + Utilities.getEMEI(context);
					String songJson = WebService.performGet(url);
					if (songJson != null && !songJson.equals("") && !songJson.equals("[]")) {
						downloadedSongs.addAll(Utilities.jsonToSongs(songJson));
					}
				}

				DaoSession daoSession = songRepo.getDaoSession();
				if (daoSession != null) {
					final SongDao songDao = daoSession.getSongDao();
					try {
						daoSession.runInTx(new Runnable() {

							private void processNewSongs() {
								//Collect new ids
								List<Integer> ids = ListUtils.getSongIdsList(downloadedSongs);
								//TODO: the existing songs in local should be compared with download version
								//to make sure it is really needed to update (compare update_date)
								List<Song> existedSongs = new ArrayList<Song>();
								if (ids.size() > 0) {
									existedSongs.addAll(songRepo.getOutdatedSongs(downloadedSongs));
								}
								// Update old songs info
								ListUtils.updateSongInfo(downloadedSongs, existedSongs);
								// Update songs info into DB, use transaction
								for (Song oldSong : existedSongs) {
									songDao.update(oldSong);
								}
								
								//TODO: new songs (free and charged should be purchased not by update)
								// Add new songs
								//List<Song> addedSongs = ListUtils.compareAndGetNewSongs(downloadedSongs,existedSongs);
								//for (Song newSong : addedSongs) {
								//	songDao.insert(newSong);
								//}
								// Remove all new songs after everything success
								downloadedSongs.clear();
							}

							@Override
							public void run() {
								processNewSongs();
							}
						});

					} catch (Exception e) {
						Log.e("Updated songs failed: ", e.getMessage());
					}
				}
				return (downloadedSongs.size() > 0 ? getString(R.string.mess_update_failed) : 
					getString(R.string.mess_update_success));
			}

			@Override
			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				showAlertDialog("", result);
			}
		};
		asyncTask.execute("");
	}

	private void showAlertDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		// builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private String getString(int resId){
		return context.getResources().getString(resId);
	}
}