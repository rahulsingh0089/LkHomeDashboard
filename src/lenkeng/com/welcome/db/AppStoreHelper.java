package lenkeng.com.welcome.db;

import lenkeng.com.welcome.util.Constant;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppStoreHelper extends SQLiteOpenHelper {
	private static int 	APP_VERSION=1;
	public AppStoreHelper(Context context) {
		super(context, "storedata.db", null, APP_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql1 = "CREATE TABLE "
                + Constant.APPSTORE_TABLE_NAME
                + "(summary TEXT,recomm_index INTEGER,package_name TEXT,"
                +"downloads INTEGER,url TEXT,version TEXT,category TEXT,"
                +"banner_big TEXT,banner_small TEXT,"
                +"HDIcon TEXT,name TEXT,rating INTEGER,icon TEXT,recommImage TEXT,style TEXT ,imgs TEXT,size INTEGER,praise INTEGER,reject INTEGER,md5 TEXT,operateType INTEGER)";
        db.execSQL(sql1);
        
        /*String sql2="CREATE TABLE "+Constant.CLIENT_ACTION_TAB +" (appname TEXT,packagename TEXT,times INTEGER)";
        db.execSQL(sql2);*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//onCreate(db);
		int indexVersion=newVersion - oldVersion;
		switch (indexVersion) {
		case 1:
			//String sql_1="ALTER TABLE "+Constant.APPSTORE_TABLE_NAME+" ADD operateType DEFAULT 0 ";
			//db.execSQL(sql_1);
			break;

		default:
			break;
		}
		
	}
}
