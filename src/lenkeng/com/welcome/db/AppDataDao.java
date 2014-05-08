package lenkeng.com.welcome.db;

import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lenkeng.com.welcome.MainHomeActivity;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.bean.ScanInfo;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.Logger;

//主界面应用程序列表数据DAO
public class AppDataDao {
	private Context context;
	private AppDbHelper helper;
	// private SharedPreferences sp;
	private static Object LOCK = new Object();

	private AppDataDao(Context context) {
		this.context = context;
		helper = new AppDbHelper(context);
		// sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	private static AppDataDao instance;

	public static synchronized AppDataDao getInstance(Context context) {
		if (instance == null) {
			instance = new AppDataDao(context);
		}
		return instance;
	}

	// 查询某个应用程序是否已安装
	public boolean findApp(String packageName) {
		synchronized (LOCK) {
			boolean result = true;
			SQLiteDatabase db = helper.getReadableDatabase();
			// db.enableWriteAheadLogging();
			Cursor c = db.rawQuery("SELECT * FROM app where packageName=?",
					new String[] { packageName });
			if (c.moveToFirst()) {
				result = true;
			} else {
				result = false;
			}
			if (db != null && db.isOpen()) {
				c.close();
				db.close();
			}
			return result;
		}
	}

	// 将APP包名及类型添加到数据库
	/*
	 * public void addApp(String packageName ,String style){
	 * if(findApp(packageName)){ return; } SQLiteDatabase
	 * db=helper.getWritableDatabase();
	 * db.execSQL("INSERT INTO app(packageName,style) VALUES (?,?)",new
	 * Object[]{packageName,style}); if(db.isOpen() || db !=null){ db.close(); }
	 * Logger.i("gww", "app add succeefully"); }
	 */

	public void addApp(String packageName, String style, String appname,
			String icons, int version) {
		if (findApp(packageName)) {
			return;
		}

		synchronized (LOCK) {
			SQLiteDatabase db = helper.getWritableDatabase();
			// db.enableWriteAheadLogging();
			db.execSQL(
					"INSERT INTO app(packageName,style,appname,HDIcon,version) VALUES (?,?,?,?,?)",
					new Object[] { packageName, style, appname, icons, version });
			if (null != db && db.isOpen()) {
				db.close();
			}
		}

		// Logger.i("gww", "app add succeefully");
	}

	// 移除一个APP数据
	public void removeApp(String packageName) {

		synchronized (LOCK) {
			SQLiteDatabase db = helper.getWritableDatabase();
			// db.enableWriteAheadLogging();
			db.execSQL("DELETE FROM app WHERE packageName=?",
					new String[] { packageName });
			if (db != null && db.isOpen()) {
				db.close();
			}
		}

	}

	// 获取某一分类下的所有APP
	public List<String> getPackageName(String style) {
		synchronized (LOCK) {
			List<String> names = new ArrayList<String>();
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT packageName FROM app where style=?",
					new String[] { style });
			while (c.moveToNext()) {
				names.add(c.getString(0));
			}
			if (db != null && db.isOpen()) {
				c.close();
				db.close();
			}
			return names;
		}

	}

	public List<AppInfo> getInstallAppInfo(String style) {

		synchronized (LOCK) {
			List<AppInfo> infos = new ArrayList<AppInfo>();
			SQLiteDatabase db = helper.getReadableDatabase();
			// db.enableWriteAheadLogging();
			Cursor c = db.rawQuery(
					"SELECT packageName,appname,HDIcon FROM app where style=?",
					new String[] { style });
			while (c.moveToNext()) {
				AppInfo info = new AppInfo();
				info.setPackage_name(c.getString(0));
				info.setName(c.getString(1));
				info.setHDIcon(c.getString(2));
				infos.add(info);
			}

			if (db != null && db.isOpen()) {
				c.close();
				db.close();
			}
			return infos;
		}
	}

	public int getVersion(String appname) {

		synchronized (LOCK) {
			int version = -1;
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT version FROM app WHERE appname =?",
					new String[] { appname });
			if (c.moveToFirst()) {
				version = c.getInt(0);
			}
			if (db != null && db.isOpen()) {
				c.close();
				db.close();
			}

			return version;
		}

	}

	public String getStyle(String packagename) {

		synchronized (LOCK) {
			String style="";
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT style FROM app WHERE packageName =?",
					new String[] { packagename });
			if (c.moveToFirst()) {
				style = c.getString(0);
			}
			if (db != null && db.isOpen()) {
				c.close();
				db.close();
			}

			return style;
		}

	}

	/*
	 * public void updateItemPosition(int befforePosition, int afterPosition) {
	 * 
	 * List<AppInfo> infos = MainHomeActivity.instance.allAppInfos; if (null ==
	 * infos || infos.size() == 0) { return; } Logger.i("gww",
	 * "---------before---"
	 * +infos.get(befforePosition)+"----after --"+infos.get(afterPosition)); int
	 * beforeId=getId(infos.get(befforePosition)); int
	 * afterId=getId(infos.get(afterPosition)); List<String> before_list =
	 * search(infos.get(befforePosition)); List<String> after_list =
	 * search(infos.get(afterPosition));
	 * 
	 * SQLiteDatabase db = helper.getWritableDatabase(); db.execSQL(
	 * "UPDATE app SET packageName=?,style=?,appname=?,version=?  WHERE id=" +
	 * afterId + "", new Object[] { before_list.get(0), before_list.get(1),
	 * before_list.get(2), before_list.get(3) }); db.execSQL(
	 * "UPDATE app SET packageName=?,style=?,appname=?,version=?  WHERE id=" +
	 * beforeId + "", new Object[] { after_list.get(0), after_list.get(1),
	 * after_list.get(2), after_list.get(3) }); }
	 */

	public List<String> search(String packageName) {
		synchronized (LOCK) {
			List<String> temp_list = new ArrayList<String>();
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM app WHERE packageName=?",
					new String[] { packageName });
			if (c.moveToFirst()) {
				temp_list.add(c.getString(0));
				temp_list.add(c.getString(1));
				temp_list.add(c.getString(2));
				temp_list.add(c.getString(3));
			}
			if (db != null && db.isOpen()) {
				c.close();
				db.close();
			}
			return temp_list;
		}
	}

	// 通过包名查询ID号
	private int getId(String packagename) {

		synchronized (LOCK) {
			int id = -1;
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT id FROM app WHERE packageName =? ",
					new String[] { packagename });
			if (c.moveToFirst()) {
				id = c.getInt(0);
			}
			if (db != null && db.isOpen()) {
				c.close();
				db.close();
			}
			return id;
		}

	}
}
