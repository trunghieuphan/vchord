package com.hatgroup.vchord.adapters;

import java.util.List;

import com.hatgroup.vchord.entities.Level;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LevelAdapter extends BaseAdapter{
	private List<Level> mLevels;
	private Context mContext;
	private int mTextViewResourceId;
	
	public LevelAdapter(Context context, int textViewResourceId, List<Level> levels) {		
		this.mLevels = levels;
		this.mContext = context;
		this.mTextViewResourceId = textViewResourceId;
	}
	
	@Override
    public int getCount() {    	
        return mLevels.size();
    }

    @Override
    public Level getItem(int index) {    	
        return mLevels.get(index);
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
 
    	Level level = mLevels.get(position);  
         
        if (level != null) {        	
            viewHolder.textView.setText(level.getName());
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
