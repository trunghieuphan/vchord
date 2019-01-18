package com.hatgroup.vchord.repo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.hatgroup.vchord.daos.RhythmDao;
import com.hatgroup.vchord.daos.RhythmDao.Properties;
import com.hatgroup.vchord.entities.Rhythm;

import de.greenrobot.dao.query.QueryBuilder;

public class RhythmRepo extends BaseRepo{

	RhythmDao rhythmDao;
	
	public RhythmRepo (Context context) {
		super(context);
        if (daoSession != null) {
        	rhythmDao = daoSession.getRhythmDao();
        }        
	}
	
	private QueryBuilder<Rhythm> getQueryRhythms(String id, String name) {
		QueryBuilder<Rhythm> qb = null;
		if (rhythmDao != null) {
			qb = rhythmDao.queryBuilder(); 
			try {
				if (!id.equals("") && !id.equals(0) ) {
					qb.where(Properties.Id.eq(id)).build();
				}
				
				if (!name.equals("")) {
					qb.where(Properties.Name.eq(name)).build();
				}
			} catch (Exception e) {
				Log.e("Query Exception: ", e.getMessage());
			}
		}
		return qb;
	}
	
	public List<Rhythm> getRhythms(String id, String name){
		QueryBuilder<Rhythm> qb = getQueryRhythms(id, name);		
		List<Rhythm> rhythms = new ArrayList<Rhythm>();
		if (qb != null) {
			rhythms = qb.list();
		}
		return rhythms;
	}
}
