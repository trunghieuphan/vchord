package com.hatgroup.vchord.repo;

import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hatgroup.vchord.daos.DaoMaster;
import com.hatgroup.vchord.daos.DaoSession;
import com.hatgroup.vchord.daos.DatabaseInitializer;

public abstract class BaseRepo {
	
	Context context;
	static SQLiteDatabase db = null;
	static DaoMaster daoMaster;
	static DaoSession daoSession;
	
	public BaseRepo (Context context) {
		this.context = context;
        try {
        	if(db == null){
        		DatabaseInitializer datain = new DatabaseInitializer(context);
            	datain.createDatabase();
    			db = datain.openDatabase();
        	}
		} catch (IOException e) {				
			e.printStackTrace();
		}
        if(daoMaster == null)
        	daoMaster = new DaoMaster(db);
        if(daoSession == null)
        	daoSession = daoMaster.newSession();      
	}
	
	/*
	protected void openDatabase() {
		
		DatabaseInitializer dataIn = new DatabaseInitializer(context);		
		try {
			dataIn.createDatabase();
			db = dataIn.openDatabase();
		} catch (IOException e) {				
			Log.e("Create db: ", e.getMessage());
		}
		
		daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
	}
	
	protected void closeDatabase() {
		db.close();
	}
	*/
	
	public DaoSession getDaoSession(){
		return daoSession;
	}
	
	public void closeDB(){
		if(db != null){
			db.close();
			db = null;
			daoMaster = null;
			daoSession = null;
		}
	}
}