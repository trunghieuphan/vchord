package com.hatgroup.vchord.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hatgroup.vchord.entities.Rhythm;

public class RhythmAdapter extends BaseAdapter {

	private List<Rhythm> mRhythms;
	private Context mContext;
	private int mTextViewResourceId;
	
	public RhythmAdapter(Context context, int textViewResourceId, List<Rhythm> rhythms) {		
		this.mRhythms = rhythms;
		this.mContext = context;
		this.mTextViewResourceId = textViewResourceId;
	}
	
	@Override
    public int getCount() {    	
        return mRhythms.size();
    }

    @Override
    public Rhythm getItem(int index) {    	
        return mRhythms.get(index);
    }     
      
    @Override
    public View getView(int position, View v, ViewGroup parent) {
    	ViewHolder viewHolder;
    	if (v == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(mTextViewResourceId, parent, false);
 
            viewHolder = new ViewHolder();            
            viewHolder.textView = (TextView)v;                
 
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
 
        Rhythm rhythm = mRhythms.get(position);  
         
        if (rhythm != null) {
        	String name =rhythm.getName();
            viewHolder.textView.setText(name);
        }
		return v;    	
    }
	
    static class ViewHolder {
    	TextView textView;
    }

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
    
}
