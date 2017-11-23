package lenkeng.com.welcome.db;

import java.util.ArrayList;
import java.util.List;

import lenkeng.com.welcome.util.Constant;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClientActionDao {
	private Context context;
	private AppDbHelper helper;
	private SQLiteDatabase sdb;
	private SharedPreferences sp;
	public ClientActionDao(Context context) {
		this.context = context;
		//helper = new AppDbHelper(context);
	}

	// 查找用户运行过的APP
	public boolean findAppByUserRun(String packagename) {
		boolean result = false;
		sdb = helper.getReadableDatabase();
		Cursor c = sdb.rawQuery("SELECT * FROM " + Constant.CLIENT_ACTION_TAB
				+ " WHERE packagename =?", new String[] { packagename });
		if (c.moveToFirst()) {
			result = true;
		}
		closeDB(sdb, c);
		return result;
	}

	// 将用户运行过的APP数据添加到数据库
	public void addUserRunData(String appname, String packagename) {
		int times=sp.getInt("counter", 0);
		if(findAppByUserRun(packagename)){
			updateUserRunData(times, packagename);
		}else{
			sdb = helper.getWritableDatabase();
			sdb.execSQL("INSERT INTO " + Constant.CLIENT_ACTION_TAB
					+ " (appname,packagename,times) VALUES(?,?,?) ", new Object[] {
					appname, packagename, times });
			closeDB(sdb, null);
		}
	}
	
	//更新用户运行过的APP次数
	public void updateUserRunData(int times ,String packagename){
		sdb =helper.getWritableDatabase();
		sdb.execSQL("UPDATE "+Constant.CLIENT_ACTION_TAB +" SET times =? "+" WHERE packagename =?", new Object[]{times,packagename});
		closeDB(sdb, null);
	}
	
	//查找用户运行次数最高的三条数据
	public List<String> getUserRunDatas(){
		List<String> mList=new ArrayList<String>();
		sdb=helper.getReadableDatabase();
		Cursor c=sdb.rawQuery("SELECT * FROM (SELECT * FROM "+Constant.CLIENT_ACTION_TAB+" ORDER BY times DESC ) LIMIT 0,3", null);
		while(c.moveToNext()){
			String s=c.getString(0)+","+c.getString(1)+","+c.getInt(2);
			mList.add(s);
		}
		closeDB(sdb, c);
		return mList;
	}
	
	
	public void closeDB(SQLiteDatabase sdb, Cursor c) {
		if (sdb != null && sdb.isOpen()) {
			sdb.close();
		}
		if(c !=null ){
			c.close();
		}
		
	}
}
