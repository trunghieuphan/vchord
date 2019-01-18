package com.hatgroup.vchord.repo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.hatgroup.vchord.daos.MelodyDao;
import com.hatgroup.vchord.daos.MelodyDao.Properties;
import com.hatgroup.vchord.entities.Melody;

import de.greenrobot.dao.query.QueryBuilder;

public class MelodyRepo extends BaseRepo{

	MelodyDao melodyDao;
	
	public MelodyRepo (Context context) {
		super(context);
		if (daoSession != null) {
        	melodyDao = daoSession.getMelodyDao();
        }        
	}
	
	private QueryBuilder<Melody> getQueryMelodies(String id, String name) {
		QueryBuilder<Melody> qb = null;
		if (melodyDao != null) {
			qb = melodyDao.queryBuilder(); 
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
	
	public List<Melody> getMelodies(String id, String name){
		QueryBuilder<Melody> qb = getQueryMelodies(id, name);		
		List<Melody> melodies = new ArrayList<Melody>();
		if (qb != null) {
			melodies = qb.orderAsc(Properties.Name).list();
		}
		return melodies;
	}
}
