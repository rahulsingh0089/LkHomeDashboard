package lenkeng.com.welcome.db;




import java.io.File;

import lenkeng.com.welcome.util.Constant;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;



//推送消息的数据库监听类
public class XmppMsgContentProvider extends ContentProvider {
	
	private static  UriMatcher macher =new UriMatcher(UriMatcher.NO_MATCH);
	static{
		
		macher.addURI(Constant.XMPP_AUTHORITY, Constant.XMPP_TABALE_NAME,1);
		macher.addURI(Constant.XMPP_AUTHORITY, Constant.XMPP_TABALE_NAME+"/#", 2);
	}
	
	private DatabaseHelper helper;
	private class DatabaseHelper extends SQLiteOpenHelper{
		public DatabaseHelper(Context context) {
			super(context, Constant.XMPP_DATABASE_NAME, null, Constant.XMPP_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			//String sql="CREATE TABLE "+Constant.XMPP_TABALE_NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT,style text,content text) ";
			String sql="CREATE TABLE "+Constant.XMPP_TABALE_NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT,style text,content text,msgtime text,isread text) ";
			db.execSQL(sql);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			//db.execSQL("DROP TABLE IF EXISTS "+Constant.XMPP_TABALE_NAME);
			//String sql="CREATE TABLE "+Constant.XMPP_TABALE_NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT,style text,content text,msgtime text,isread text) ";
			//db.execSQL(sql);
		}
		
	}
	
	@Override
	public int delete(Uri uri, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=helper.getWritableDatabase();
		
		//db.execSQL("DELETE FROM "+Constant.TABALE_NAME +" WHERE time="+arg1);
		db.delete(Constant.XMPP_TABALE_NAME, "style=? and msgtime=?",arg2);
		//getContext().getContentResolver().notifyChange(uri, null);
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
			if(!isExit()){
				onCreate();
			}
			SQLiteDatabase db=helper.getWritableDatabase();
			db.insert(Constant.XMPP_TABALE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			
			// TODO Auto-generated catch block
		return null;
	}
	private boolean isExit(){
		File path=getContext().getDatabasePath(Constant.XMPP_DATABASE_NAME);
		if(path.exists()){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		
		helper=new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=helper.getReadableDatabase();
		Cursor c=db.query(Constant.XMPP_TABALE_NAME, null, null, null, null, null, "isread asc");
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String str, String[] strs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db=helper.getWritableDatabase();
		String sql="UPDATE "+Constant.XMPP_TABALE_NAME +" set isread= 0 where id= "+str;
		db.execSQL(sql);
		return 0;
	}

}
