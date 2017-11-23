package lenkeng.com.welcome.db;

import java.util.ArrayList;

import lenkeng.com.welcome.upload.UploadBean;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.Logger;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UploadDao {

	private static final String TAG = "UploadDao";
	private Context mContext;
	private UploadDbHelper dbHelper;
    private static Object lock=new Object();
    
    public UploadDao(Context context) {
    	this.mContext=context;
    	dbHelper=new UploadDbHelper(mContext);
    	
	}
    
    
    /**
     * 添加一条上传记录
     * @param bean
     */
    public void addRecord(UploadBean bean){
 	   synchronized (lock) {
 		
 	
 	   Logger.e(TAG, "--------11------插入数据---bean="+bean);
 	   String sql="INSERT INTO UPLOAD (packageName," +
 	   									"state," +
 	   									"devId," +
 	   									"lastUpdate,"+
 	   								     "appName,"+
 	   								     "failReason"+
 	   									") VALUES(?,?,?,?,?,?)";
 	   Object [] param=new Object[]{
 			      bean.getPackageName(),
 			      bean.getState(),
 			      bean.getDevId(),
 			      bean.getLastUpdate(),
 			      bean.getAppName(),
 			      bean.getFailReason()
 			   
 	   };
 	   
 	   
 	   SQLiteDatabase db=dbHelper.getWritableDatabase();
 	   db.execSQL(sql, param);
 	   closeDB(db, null);
 	   }
    } 
    
    /**
     * 根据包名更新上传记录的状态
     * @param packageName
     * @param state
     */
	public void updateUploadBeanState(String packageName, int state) {
		synchronized (lock) {

			String sql = "UPDATE UPLOAD SET state=? WHERE packageName=?";
			Object[] params = new Object[] { state, packageName };

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql, params);
			closeDB(db, null);

			//Logger.e(TAG, "----------更新状态 : state=" + state + ",pkg="+ packageName);
		}
	}
	
	/**
	 * 根据包名更新上传记录的状态
	 * @param packageName
	 * @param state
	 */
	public void updateUploadBeanState(String packageName, int state,String failReason) {
		synchronized (lock) {
			
			String sql = "UPDATE UPLOAD SET state=? , failReason=? WHERE packageName=?";
			Object[] params = new Object[] { state,failReason, packageName };
			
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql, params);
			closeDB(db, null);
			
			//Logger.e(TAG, "----------更新状态 : state=" + state + ",pkg="+ packageName);
		}
	}
    
	/**
	 * 根据包名查找上传记录
	 * @param packageName
	 * @return
	 */
	public UploadBean findUploadBeanByPackageName(String packageName) {
		UploadBean bean = null;
		synchronized (lock) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();

			String sql = "SELECT * FROM UPLOAD WHERE packageName = ?";
			String[] params = new String[] { packageName };

			Cursor c = db.rawQuery(sql, params);
			if (c.moveToNext()) {
				bean = cursor2UploadBean(c);

			}

			closeDB(db, c);

		}
		return bean;
	}
	
	/**
	 * 查询所有上传记录
	 * @return
	 */
	public  ArrayList<UploadBean> getAllRecord(){
		ArrayList<UploadBean> uploadList=new ArrayList<UploadBean>();
		synchronized (lock) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();

			String sql = "SELECT * FROM UPLOAD";
			String[] params = new String[] { };

			Cursor c = db.rawQuery(sql, params);
			while (c.moveToNext()) {
				UploadBean bean= cursor2UploadBean(c);
				uploadList.add(bean);
			}

			closeDB(db, c);

		}
		Logger.e(TAG, "=======查询所有记录,数量:"+uploadList.size());
		return uploadList;
	}
	
	/**
	 * 查询审核中的所有记录
	 * @return
	 */
	public  ArrayList<UploadBean> getAllWaitVerifyRecord(){
		ArrayList<UploadBean> waitVerifyuploadList=new ArrayList<UploadBean>();
		synchronized (lock) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();

			String sql = "SELECT * FROM UPLOAD WHERE state=?";
			String[] params = new String[] {String.valueOf(Constant.UPLOAD_STATE_WAITVERFY) };

			Cursor c = db.rawQuery(sql, params);
			while (c.moveToNext()) {
				UploadBean bean= cursor2UploadBean(c);
				waitVerifyuploadList.add(bean);
			}

			closeDB(db, c);

		}
		Logger.e(TAG, "=======查询审核中的所有记录,数量:"+waitVerifyuploadList.size());
		return waitVerifyuploadList;
	}
	
	
	
	private UploadBean cursor2UploadBean(Cursor c) {
		UploadBean bean = new UploadBean();
		bean.setPackageName(c.getString(c.getColumnIndex("packageName")));
		bean.setState(c.getInt(c.getColumnIndex("state")));
		bean.setDevId( c.getString(c.getColumnIndex("devId")));
		bean.setLastUpdate(c.getLong(c.getColumnIndex("lastUpdate")));
		bean.setAppName(c.getString(c.getColumnIndex("appName")));
		bean.setFailReason(c.getString(c.getColumnIndex("failReason")));

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


	public void deletUploadByPkg(String packageName) {
		synchronized (lock) {

			String sql = "DELETE FROM UPLOAD WHERE packageName = ?";
			Object[] params = new Object[] { packageName };

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL(sql, params);

			closeDB(db, null);
			Logger.e(TAG, "----------删除记录:,pkg=" + packageName);
		}		
	}  
    
    
}
