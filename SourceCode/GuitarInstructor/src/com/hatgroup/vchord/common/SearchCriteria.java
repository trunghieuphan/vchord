package com.hatgroup.vchord.common;

import java.io.Serializable;

public class SearchCriteria implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mTitle;
	private Boolean mIsSong;
	private Boolean mIsSinger;
	private Integer mLevel;
	private Integer mRhythm;
	private Integer mMelody;
	private Boolean mIsFavorite;
	
	public SearchCriteria() {
		this("", true, false, 0, 0, 0, false);
	}
	
	public SearchCriteria(String title, Boolean isSong, Boolean isSinger,
			Integer level, Integer rhythm, Integer melody, Boolean isFavorite) {
		super();
		this.mTitle = title;
		this.mIsSong = isSong;
		this.mIsSinger = isSinger;
		this.mLevel = level;
		this.mRhythm = rhythm;
		this.mMelody = melody;
		this.mIsFavorite = isFavorite;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public Boolean getIsSong() {
		return mIsSong;
	}

	public void setIsSong(Boolean isSong) {
		this.mIsSong = isSong;
	}

	public Boolean getIsSinger() {
		return mIsSinger;
	}

	public void setIsSinger(Boolean isSinger) {
		this.mIsSinger = isSinger;
	}

	public Integer getLevel() {
		return mLevel;
	}

	public void setLevel(Integer level) {
		this.mLevel = level;
	}

	public Integer getRhythm() {
		return mRhythm;
	}

	public void setRhythm(Integer rhythm) {
		this.mRhythm = rhythm;
	}
	
	public Integer getMelody() {
		return mMelody;
	}

	public void setMelody(Integer melody) {
		this.mMelody = melody;
	}
	
	public Boolean getIsFavorite() {
		return mIsFavorite;
	}

	public void setIsFavorite(Boolean isFavorite) {
		this.mIsFavorite = isFavorite;
	}
}
