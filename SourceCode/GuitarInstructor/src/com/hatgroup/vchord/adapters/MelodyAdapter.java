package com.hatgroup.vchord.adapters;

import java.util.List;

import com.hatgroup.vchord.entities.Melody;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MelodyAdapter extends BaseAdapter {
	private List<Melody> mMelodies;
	private Context mContext;
	private int mTextViewResourceId;
	
	public MelodyAdapter(Context context, int textViewResourceId, List<Melody> melodies) {		
		this.mMelodies = melodies;
		this.mContext = context;
		this.mTextViewResourceId = textViewResourceId;
	}
	
	@Override
    public int getCount() {    	
        return mMelodies.size();
    }

    @Override
    public Melody getItem(int index) {    	
        return mMelodies.get(index);
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
 
    	Melody melody = mMelodies.get(position);  
         
        if (melody != null) {        	
            viewHolder.textView.setText(melody.getName());
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
