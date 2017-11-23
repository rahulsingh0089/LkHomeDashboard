package lenkeng.com.welcome.db;

import java.io.File;

import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.Logger;

import com.lenkeng.bean.ApkBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract.Helpers;

public class DownloadDao {
   
	private static final String TAG = "DownloadDao";
	private Context mContext;
	private DownloadDbHelper dbHelper;
    private static Object lock=new Object();
	
   public DownloadDao(Context mContext) {
	   super();
	this.mContext = mContext;
	dbHelper=new DownloadDbHelper(mContext);
	
   }
   
   public void addRecord(DownloadBean bean){
	   synchronized (lock) {
		
	
	   Logger.e(TAG, "--------11------插入数据---bean="+bean);
	   String sql="INSERT INTO DOWNLOAD (packageName," +
	   									"url," +
	   									"savePath," +
	   									"state," +
	   									"size," +
	   									"current," +
	   									"md5" +
	   									") VALUES(?,?,?,?,?,?,?)";
	   Object [] param=new Object[]{
			      bean.getPackageName(),
			      bean.getUrl(),
			      bean.getSavePath(),
			      Constant.APK_STATE_DOWNLOADING,
			      bean.getSize(),
			      bean.getCurrent(),
			      bean.getMd5()
			   
	   };
	   
	   
	   SQLiteDatabase db=dbHelper.getWritableDatabase();
	   db.execSQL(sql, param);
	   closeDB(db, null);
	   }
   }
   
	public void updateDownloadBeanState(String packageName, int state) {
		synchronized (lock) {

			String sql = "UPDATE DOWNLOAD SET state=? WHERE packageName=?";
			Object[] params = new Object[] { state, packageName };

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql, params);
			closeDB(db, null);

			Logger.e(TAG, "----------更新状态 : state=" + state + ",pkg="
					+ packageName);
		}
	}

	public void updateDownloadPosition(String packageName, long position) {
		synchronized (lock) {

			String sql = "UPDATE DOWNLOAD SET current=? WHERE packageName=?";
			Object[] params = new Object[] { position, packageName };

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql, params);
			closeDB(db, null);
			// Logger.e(TAG,"----------更新下载进度: position="+position+",pkg="+packageName);
		}
	}

	public DownloadBean findDownloadBeanByPackageName(String packageName) {
		DownloadBean bean = null;
		synchronized (lock) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();

			String sql = "SELECT * FROM DOWNLOAD WHERE packageName = ?";
			String[] params = new String[] { packageName };

			Cursor c = db.rawQuery(sql, params);
			if (c.moveToNext()) {
				bean = cursor2DownloadBean(c);

			}

			closeDB(db, c);

		}
		return bean;
	}

	private DownloadBean cursor2DownloadBean(Cursor c) {
		DownloadBean bean = new DownloadBean();
		bean.setPackageName(c.getString(c.getColumnIndex("packageName")));
		bean.setUrl(c.getString(c.getColumnIndex("url")));
		bean.setSavePath( c.getString(c.getColumnIndex("savePath")));
		bean.setState(c.getInt(c.getColumnIndex("state")));
		bean.setSize( c.getLong(c.getColumnIndex("size")));
		bean.setCurrent(  c.getLong(c.getColumnIndex("current")));
		bean.setMd5( c.getString(c.getColumnIndex("md5")));

		return bean;
	}

	private void closeDB(SQLiteDatabase db, Cursor c) {
		if (db != null && db.isOpen()) {
			if (c != null) {
				c.close();
			}
			// db.endTransaction();
			db.close();
		}

	}

	public void deleteDownloadBean(String packageName) {

		synchronized (lock) {

			String sql = "DELETE FROM DOWNLOAD WHERE packageName=?";
			Object[] params = new Object[] { packageName };

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql, params);

			closeDB(db, null);
			Logger.e(TAG, "----------删除记录:,pkg=" + packageName);
		}
	}

	public String findSavePathByPackageName(String packageName) {
		String savePath = "";
		synchronized (lock) {

			String sql = "SELECT savePath FROM DOWNLOAD WHERE packageName = ?";
			String[] params = new String[] { packageName };

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor c= db.rawQuery(sql, params);
			 
			if (c.moveToNext()) {
				savePath=c.getString(c.getColumnIndex("savePath"));

			}
			
			closeDB(db, c);

			Logger.e(TAG, "----------查询savePath:,pkg=" + packageName+",savePath="+savePath);
		}
		return savePath;
	}

}
