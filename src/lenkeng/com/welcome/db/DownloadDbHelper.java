package lenkeng.com.welcome.db;

import lenkeng.com.welcome.util.Constant;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DownloadDbHelper extends SQLiteOpenHelper {

	public DownloadDbHelper(Context context) {
		super(context, Constant.DOWNLOAD_DATABASE, null, Constant.DOWNLOAD_DBVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="CREATE TABLE DOWNLOAD (packageName TEXT,url TEXT,savePath TEXT,state INTEGER,size INTEGER,current INTEGER,md5 TEXT,id INTEGER PRIMARY KEY AUTOINCREMENT)";
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
