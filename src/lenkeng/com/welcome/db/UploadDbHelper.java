package lenkeng.com.welcome.db;

import lenkeng.com.welcome.util.Constant;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class UploadDbHelper extends SQLiteOpenHelper {

	public UploadDbHelper(Context context) {
		super(context, Constant.UPLOAD_DATABASE, null, Constant.UPLOAD_DBVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="CREATE TABLE UPLOAD (id INTEGER PRIMARY KEY AUTOINCREMENT,packageName TEXT ,state INTEGER,devId INTEGER,lastUpdate LONG,appName TEXT,failReason TEXT)";
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
