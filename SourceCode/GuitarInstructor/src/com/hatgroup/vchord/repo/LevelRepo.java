package com.hatgroup.vchord.repo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.hatgroup.vchord.daos.LevelDao;
import com.hatgroup.vchord.daos.LevelDao.Properties;
import com.hatgroup.vchord.entities.Level;

import de.greenrobot.dao.query.QueryBuilder;

public class LevelRepo extends BaseRepo{

	LevelDao levelDao;
	
	public LevelRepo (Context context) {
		
		super(context);
        if (daoSession != null) {
        	levelDao = daoSession.getLevelDao();
        }       
	}
	
	private QueryBuilder<Level> getQueryLevels(String id, String name) {
		QueryBuilder<Level> qb = null;
		if (levelDao != null) {
			qb = levelDao.queryBuilder(); 
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
	
	public List<Level> getLevels(String id, String name){
		QueryBuilder<Level> qb = getQueryLevels(id, name);		
		List<Level> rhythms = new ArrayList<Level>();
		if (qb != null) {
			rhythms = qb.list();
		}
		return rhythms;
	}
}
