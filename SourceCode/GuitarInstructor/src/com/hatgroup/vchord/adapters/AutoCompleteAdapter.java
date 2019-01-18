package com.hatgroup.vchord.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.hatgroup.vchord.common.SearchCriteria;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.Utilities;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	
	ArrayList<String> results;
	
	private SearchCriteria mSongModel;
	private int mTextViewResourceId;
	private Context mContext;
	private int mPage = 0;
	private int mTotal = 0;
	
	private Boolean mMayHaveMorePages = false;
	private CharSequence mConstraint = "";
	
	SongRepo songRepo;
//	ArtistRepo artistRepo;
	
	public AutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.mTextViewResourceId = textViewResourceId;
        this.mContext = context;
        
        
        
        results = new ArrayList<String>();
	}
	
	public void setSongModel(SearchCriteria songModel) {
		this.mSongModel = songModel;
		
//		if (mSongModel.getIsSong()) {
        	songRepo = new SongRepo(this.mContext);
//        }
//        else {
//        	artistRepo = new ArtistRepo(this.mContext);
//        }
	}
    
    @Override
    public int getCount() {    	
        return results.size();
    }

    @Override
    public String getItem(int index) {    	
        return results.get(index);
    }
    
    @Override
    public View getView(int position, View v, ViewGroup parent) {        
        ViewHolder viewHolder;
        
        // If user scroll to bottom of List        
        if (position == (getCount()-1) && mMayHaveMorePages) {        	
        	// increment page
        	mPage++;
        	//Toast.makeText(mContext, "Get list count: " + mPage, 100).show();	
        	// Call filter() to get more songs        	
        	getFilter().filter(mConstraint);        	
        }
 
        if (v == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            v = li.inflate(mTextViewResourceId, parent, false);
 
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) v;                  
            v.setTag(viewHolder);
        } 
        else {
            viewHolder = (ViewHolder) v.getTag();
        }
        String songTitle = results.get(position);
        if (songTitle != null) {
            viewHolder.titleTextView.setText(songTitle);         
        }
        
        return v;
    }   
          
	static class ViewHolder {	
		TextView titleTextView;		
	}

	@Override
	public Filter getFilter() {	
		Filter nameFilter = new Filter() {
			
//	        public String convertResultToString(Object resultValue) {
//	            String str = ((Song)resultValue).getTitle(); 
//	            return str;
//	        }
		
	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	        	
	            if(constraint != null) {	            	
	            	String titleOrName = Utilities.removeDiacritic(constraint.toString()); 
//	            	titleOrName = Utilities.deAccent(titleOrName) ;
	            		            	
	            	// If user type a new constraint
	            	if (!mConstraint.equals(constraint)) {
	            		mPage = 0;
	            		
//	            		if (mSongModel.getIsSong()) {	            		
//		            		mTotal = songRepo.getTotalSongs(titleOrName, mSongModel.getLevel(), mSongModel.getRhythm(), mSongModel.getMelody());
	            			mTotal = songRepo.getTotalSongs(titleOrName,  mSongModel.getIsSong(), mSongModel.getIsSinger());
//		            	}
//		            	else {	            		
//		            		mTotal = artistRepo.getTotalArtists(titleOrName, mSongModel.getIsSinger());
//		            	}
	            	}
	            	
	            	mConstraint = constraint;
	            	
	            	mMayHaveMorePages = false;
	            	if (mTotal > getCount()){
	            		mMayHaveMorePages = true;
	            	}
	            		            	           	
	            	List<String> tempTitles = new ArrayList<String>();
	        		if (mMayHaveMorePages || mPage == 0){
	        			
//	        			if (mSongModel.getIsSong()) {	        				
//	        				tempTitles = (ArrayList<String>) songRepo.getLimitSongTitles(mPage, titleOrName, mSongModel.getLevel(), mSongModel.getRhythm(), mSongModel.getMelody());
	        				tempTitles = (ArrayList<String>) songRepo.getLimitSongTitles(mPage, titleOrName, mSongModel.getIsSong(), mSongModel.getIsSinger());
//	        			}
//	        			else {
//	        				tempTitles = (ArrayList<String>) artistRepo.getLimitArtistNames(mPage, titleOrName, mSongModel.getIsSinger());
//	        			}	        			
	        		}	        		
	            	
	        		FilterResults filterResults = new FilterResults();
	        		filterResults.values = tempTitles;
	        		filterResults.count = tempTitles.size();
	                return filterResults;
	            } else {
	            	mPage = 0;
	                return new FilterResults();
	            }
	        }
	        @Override
	        protected void publishResults(CharSequence constraint, FilterResults fresults) {	        	
	        	if (fresults!= null && fresults.count > 0) {
	        		if (mPage == 0) {
	        			// if user types keyword
	        			// not clear if user scroll
	        			results.clear();	        			
	        		}	        		
	        		ArrayList<String> temps = (ArrayList<String>)fresults.values;
	        		for (String temp : temps) {
						if (results.indexOf(temp) == -1) {
							results.add(temp);
						}
					}
	        		Collections.sort(results);
//	        		results.addAll((ArrayList<String>)fresults.values);
	                notifyDataSetChanged();	                
	            } 
//	        	else {
//	                Log.println(Log.INFO, "Results", "-");
//	                notifyDataSetInvalidated();
//	            }
	        }
	    };
	    
		return nameFilter;
	}
	
}