package com.hatgroup.vchord.repo;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.util.Log;

import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.common.SearchCriteria;
import com.hatgroup.vchord.daos.SongDao;
import com.hatgroup.vchord.daos.SongDao.Properties;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.utils.ListUtils;
import com.hatgroup.vchord.utils.Utilities;

import de.greenrobot.dao.query.QueryBuilder;


public class SongRepo extends BaseRepo{	

	SongDao songDao;
	
	public SongRepo (Context context) {
		super(context);
        if (daoSession != null) {
        	songDao = daoSession.getSongDao();
        }
	}
	
	public List<Song> getFavoriteSongs() {
		 List<Song> songList = new ArrayList<Song>();
	        
	        try {
	            QueryBuilder<Song> qb = songDao.queryBuilder();	            	            		            	
            	qb.where(SongDao.Properties.Favorite.eq(1)).orderAsc(SongDao.Properties.Title).build();	
            	songList = qb.list();
	           
	        } catch (Exception e){
	        	Log.e("Query Exception: ", e.getMessage());	        	
	        }
	       return songList;
	}
			
	private QueryBuilder<Song> getQuerySongs(String title, Boolean isSong, Boolean isSinger, Integer rhythm, Integer level, Integer melody, Boolean isFavorite) {
		QueryBuilder<Song> qb = null;
	    if (songDao != null) {	    	
	    	 qb = songDao.queryBuilder();
	        try {
	        	
	        	if (!title.equals("")){
	        		if (isSong) {
		        		qb.where(Properties.Unsigned_title.like(title + "%")).build();
		        	}
	        		else {
        				if (!isSinger) {				        		
	        				qb.where(Properties.Unsigned_composer_name.like(title + "%")).build();
			        	} 		        	
	        			else {	        			
	        				qb.where(Properties.Unsigned_singer_name.like(title + "%")).build();
			        	}
	        		}	
	        	}	
	        	if (rhythm != 0 ) {
	        		qb.where(Properties.Rhythm_id.eq(rhythm)).build();
	        	}
	        	if (level != 0) { 
	        		qb.where(Properties.Level_id.eq(level)).build();
	        	}	
	        	if (melody != 0) {
	        		qb.where(Properties.Melody_id.eq(melody)).build();
	        	}
	        	if (isFavorite)
	        	{
	        		qb.where(Properties.Favorite.eq(1)).build();	        		
	        	}
	        		            
	        } catch (Exception e){
	        	Log.e("Query Exception: ", e.getMessage());        	
	        }
	    }	  
	    return qb;
	}
	
	public List<Song> getLimitSongs(int page, SearchCriteria songModel) {
		List<Song> songs = new ArrayList<Song>();
		
		if (songModel != null) {
			String title = Utilities.removeDiacritic(songModel.getTitle());
			
			QueryBuilder<Song> qb = getQuerySongs(title, songModel.getIsSong(), songModel.getIsSinger(), songModel.getRhythm(), songModel.getLevel(), songModel.getMelody(),songModel.getIsFavorite());
			
			if (qb != null) {
				songs = qb.offset(page*Constants.MAX_LIST_ITEM_ON_PAGE).limit(Constants.MAX_LIST_ITEM_ON_PAGE).orderAsc(Properties.Unsigned_title).list();
			}
		}				
		return songs;
	}
	
	// triet.dinh
	public long getCountLimitSongs(SearchCriteria songModel)
	{
		long count = 0;
		if (songModel != null) {
//			String title = Utilities.deAccent(songModel.getTitle());
			String title = Utilities.removeDiacritic(songModel.getTitle());
			//title = Utilities.deAccent(title);
			
			QueryBuilder<Song> qb = getQuerySongs(title, songModel.getIsSong(), songModel.getIsSinger(), songModel.getRhythm(), songModel.getLevel(), songModel.getMelody(), songModel.getIsFavorite());
			count = qb.count();

		}			
		return count;
	}
	
	public int getTotalSongs(SearchCriteria songModel) {
		int total = 0;
		
		if (songModel != null) {
			QueryBuilder<Song> qb = getQuerySongs(songModel.getTitle(), songModel.getIsSong(), songModel.getIsSinger(), songModel.getRhythm(), songModel.getLevel(), songModel.getMelody(), songModel.getIsFavorite());
			
			if (qb != null) {
				total = (int) qb.count();
			}
		}				
		return total;
	}
	
	public List<Song> getAllSongs(List<Integer> filterIds) {
		List<Song> songList = new ArrayList<Song>();
	    if (daoSession != null) {
	    	songDao = daoSession.getSongDao();
	        try {
	        	QueryBuilder<Song> qb = songDao.queryBuilder();
	        	if(filterIds != null){
	        		qb.where(Properties.Id.in(filterIds)).build();
	        	}
	        	songList = qb.list();
	        } catch (Exception e){
	        	Log.e("Query Exception: ", e.getMessage());        	
	        }
	    }
	    return songList;
	}	
	
	public List<Song> getOutdatedSongs(List<Song> downloadedSongs) {
		List<Integer> downloadedIds = ListUtils.getSongIdsList(downloadedSongs);
	    CopyOnWriteArrayList<Song> songList = new CopyOnWriteArrayList<Song>();
		if (daoSession != null) {
	    	songDao = daoSession.getSongDao();
	        try {
	        	QueryBuilder<Song> qb = songDao.queryBuilder();
	        	if(downloadedIds != null){
	        		qb.where(Properties.Id.in(downloadedIds)).build();
	        	}
	        	songList.addAll(qb.list());
	        	//exclude the song which no need to update (check update_date no change)
	        	for(Song localSong : songList){
	        		Song downloadedSong = ListUtils.findSongInlist(localSong.getId().intValue(), downloadedSongs);
	        		if(downloadedSong != null && localSong.getUpdate_date() != null && downloadedSong.getUpdate_date() != null){
	        			//Update_date no change, no need to update, exclude it
	        			if(localSong.getUpdate_date().equals(downloadedSong.getUpdate_date())){
	        				songList.remove(localSong);
	        			}
	        		}
	        	}
	        } catch (Exception e){
	        	Log.e("Query Exception: ", e.getMessage());        	
	        }
	    }
	    return songList;
	}
	
	public Song getByTitle(String title) {
		QueryBuilder<Song> qb = null;
		if (songDao != null) {
			qb = songDao.queryBuilder();
			try {
	            if (!title.equals("") && !title.equals("1")) {	            		            	
	            	qb.where(SongDao.Properties.Title.like(title));	  
	            }	            
	        } catch (Exception e){
	        	Log.e("Query Exception: ", e.getMessage());	        	
	        }
		}       
        return qb.unique();
	}
	
	public Song getSongById(int id){
		Song song = null;
		if (daoSession != null) {
	    	songDao = daoSession.getSongDao();
	        try {
	        	QueryBuilder<Song> qb = songDao.queryBuilder();
	        	qb.where(Properties.Id.eq(id));
	        	song = qb.unique();
	        } catch (Exception e){
	        	Log.e("Query Exception: ", e.getMessage());        	
	        }
	    }
	    return song;
	}
	
	public boolean updateSong(Song updatedSong){
		if (daoSession != null) {
	    	songDao = daoSession.getSongDao();
	        try {
	        	songDao.update(updatedSong);
	        	return true;
	        } catch (Exception e){
	        	Log.e("Update entity (id="+updatedSong.getId()+") failed", e.getMessage());        	
	        }
	    }
		return false;
	}	
	
	public String getLastUpdateDate(){
		try{
			QueryBuilder<Song> qb = songDao.queryBuilder();
			List<Song> songs = qb.orderDesc(SongDao.Properties.Update_date).limit(1).list();
			if(songs.size() > 0){
				return songs.get(0).getUpdate_date();
			}
		}
		catch(Exception e){
		}
		return "";
	}
	
	public boolean syncInTx(final List<Song> newSongs){
		if (daoSession != null) {
	    	songDao = daoSession.getSongDao();
	        try {
	        	daoSession.runInTx(new Runnable() {
	        		
	        		private void processNewSongs(){
	        			//Load songs from DB
						List<Integer> ids = ListUtils.getSongIdsList(newSongs);
						List<Song> oldSongs = getAllSongs(ids);
						//Update old songs info
						ListUtils.updateSongInfo(newSongs, oldSongs);
						//Update songs info into DB, use transaction
						for(Song oldSong : oldSongs){
							songDao.update(oldSong);
						}
						//Add new songs
						List<Song> addedSongs = ListUtils.compareAndGetNewSongs(newSongs, oldSongs);
						
						for(Song newSong : addedSongs){
							songDao.insert(newSong);
						}
						//Remove all new songs after everything success
						newSongs.clear();
	        		}
	        		
					@Override
					public void run() {
						processNewSongs();
					}
				});
	        	
	        	return true;
	        } catch (Exception e){
	        	Log.e("Updated songs failed: ", e.getMessage());        	
	        }
	    }
		return false;
	}
	
	/*
	 * New implement: AutoCompleteTextView
	 * */	
	public QueryBuilder<Song> getQuerySongs(String title, Boolean isSong, Boolean isSinger) {
		QueryBuilder<Song> qb = null;
		if (songDao != null) {
			qb = songDao.queryBuilder();
			try {				
	            if (!title.equals("") && !title.equals("1")) {
	            	if (isSong) {
	            		qb.where(SongDao.Properties.Unsigned_title.like(title + "%")).build();
	            	} 
	            	else {
	            		if (isSinger) {
	            			qb.where(SongDao.Properties.Unsigned_singer_name.like(title + "%")).build();
	            		}
	            		else {
	            			qb.where(SongDao.Properties.Unsigned_composer_name.like(title + "%")).build();
	            		}
	            	}
	            		  
	            }	            
	        } catch (Exception e){
	        	Log.e("Query Exception: ", e.getMessage());	        	
	        }
		}       
        return qb;
	}
	
	public int getTotalSongs(String title, Boolean isSong, Boolean isSinger){
		QueryBuilder<Song> qb = getQuerySongs(title, isSong, isSinger);
		int total = 0;
		if (qb != null) {
			total = (int)qb.count();
		}		
		return total;
	}
	
	public List<Song> getLimitSongs(int page, String title, Boolean isSong, Boolean isSinger){
		QueryBuilder<Song> qb = getQuerySongs(title, isSong, isSinger);
		List<Song> songs = new ArrayList<Song>();
		if (qb != null) {
			songs = qb.offset(page*Constants.MAX_LIST_ITEM_ON_PAGE).limit(Constants.MAX_LIST_ITEM_ON_PAGE).list();
		}		
		return songs;
	}
	
	public List<String> getLimitSongTitles(int page, String title, Boolean isSong, Boolean isSinger) {
		List<Song> songList = getLimitSongs(page, title, isSong, isSinger);
        List<String> songTitleList = new ArrayList<String>();
	   
	    	if (isSong) {
	    		 for (Song song : songList) {
	    			songTitleList.add(song.getTitle());
	    		 }
	    	}
	    	else {
	    		if (isSinger) { 
	    			for (Song song: songList) {
	    				songTitleList.add(song.getSinger_name());
	    			}
	    		}
	    		else {
	    			for (Song song: songList) {
	    				songTitleList.add(song.getComposer_name());
	    			}
	    		}
	    	}
	    
        return songTitleList;
	}
	
}
