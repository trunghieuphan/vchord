package com.hatgroup.vchord.utils;

import java.util.ArrayList;
import java.util.List;

import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.fragments.SongPurchaseFragment.PurchasedSong;

public class ListUtils{

	public static List<Integer> getSongIdsList(List<Song> songs){
		List<Integer> ids = new ArrayList<Integer>();
		for(Song song : songs){
			ids.add(song.getId());
		}
		return ids;
	}

	public static List<Integer> getSongIdsList(List<PurchasedSong> songs, String args){
		List<Integer> ids = new ArrayList<Integer>();
		for(PurchasedSong song : songs){
			ids.add(song.getId());
		}
		return ids;
	}
	
	public static void updateSongInfo(List<Song> newSongs, List<Song> oldSongs){
		for(Song oldSong : oldSongs){
			Song newSong = findSongInlist(oldSong.getId(), newSongs);
			if(newSong != null){
				oldSong.setTitle(newSong.getTitle());
				oldSong.setUnsigned_title(newSong.getUnsigned_title());  
				oldSong.setLyric(newSong.getLyric());
				oldSong.setRhythm_id(newSong.getRhythm_id());
				oldSong.setComposer_name(newSong.getComposer_name());
				oldSong.setUnsigned_composer_name(newSong.getUnsigned_composer_name());
				oldSong.setSinger_name(newSong.getSinger_name());
				oldSong.setUnsigned_singer_name(newSong.getUnsigned_singer_name());
				oldSong.setTone(newSong.getTone());
				
				oldSong.setLevel_id(newSong.getLevel_id());
				oldSong.setMelody_id(newSong.getMelody_id());
				oldSong.setUpdate_date(newSong.getUpdate_date());
				oldSong.setMusic_link(newSong.getMusic_link());				
			}
		}
	}
	
	public static List<Song> compareAndGetNewSongs(List<Song> newSongs, List<Song> oldSongs){
		List<Song> news = new ArrayList<Song>();
		for(Song newSong : newSongs){
			if(findSongInlist(newSong.getId(), oldSongs) == null){
				news.add(newSong);
			}
		}
		return news;
	}
	
	public static Song findSongInlist(int id, List<Song> songs){
		for(Song song : songs){
			if(id  == song.getId()){
				return song;
			}
		}
		return null;
	}

	public static PurchasedSong findSongInlist(int id, List<PurchasedSong> songs, String args){
		for(PurchasedSong song : songs){
			if(id  == song.getId()){
				return song;
			}
		}
		return null;
	}
	
}
