package lenkeng.com.welcome.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.Logger;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lenkeng.bean.Screen;
import com.lenkeng.logic.Logic;


public class AppStoreDao {
	private String TAG = "AppStoreDao";
	private Context context;
	private AppDbHelper helper;
	private static Object LOCK = new Object();

	private static AppStoreDao dao;
	
	private AppStoreDao(Context context) {
		this.context = context;
		helper = new AppDbHelper(context);

	}
	public static synchronized AppStoreDao getInstance(Context context){
		if(dao == null){
			dao =new AppStoreDao(context);
		}
		return dao;
	}
	// 查看是否已存在该条APP数据
	public boolean findApp(String packagename) {
		synchronized (LOCK) {
			boolean isExist = false;
			SQLiteDatabase db = helper.getReadableDatabase();
			//db.enableWriteAheadLogging();
			Cursor c = db.rawQuery("SELECT * FROM "
					+ Constant.APPSTORE_TABLE_NAME + " WHERE package_name = ?",
					new String[] { packagename });
			if (c.moveToFirst()) {
				isExist = true;
			} else {
				isExist = false;
			}
			closeDB(db, c);
			return isExist;
		}

	}

	public boolean findRecApp(int recoIndex) {
		synchronized (LOCK) {
			boolean isExist = false;
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT * FROM "
					+ Constant.APPSTORE_TABLE_NAME + " WHERE recomm_index = ?",
					new String[] { recoIndex + "" });
			if (c.moveToFirst()) {
				isExist = true;
			} else {
				isExist = false;
			}
			closeDB(db, c);
			return isExist;
		}
	}

	public AppInfo getAppInfoByPackageName(String packagename) {

		synchronized (LOCK) {
			SQLiteDatabase db = helper.getReadableDatabase();
			//db.enableWriteAheadLogging();
			Cursor c = db.rawQuery("SELECT * FROM "
					+ Constant.APPSTORE_TABLE_NAME + " WHERE package_name = ?",
					new String[] { packagename });
			AppInfo info = null;
			if (c.moveToFirst()) {
				info = new AppInfo();
				info.setSummary(c.getString(0));
				info.setRecomm_index(c.getInt(1));
				info.setPackage_name(c.getString(2));
				info.setDownloads(c.getInt(3));
				info.setUrl(c.getString(4));
				info.setVersion(c.getString(5));
				info.setCategory(c.getString(6));
				info.setBanner_big(c.getString(7));
				info.setBanner_small(c.getString(8));
				info.setHDIcon(c.getString(9));
				info.setName(c.getString(10));
				info.setRating(c.getInt(11));
				info.setIcon(c.getString(12));
				info.setRecommImage(c.getString(13));
				info.setStyle(c.getString(14));
				info.setPraise(c.getInt(17));
				info.setReject(c.getInt(18));
				info.setMd5(c.getString(19));
				info.setOperateType(c.getInt(20));

			}
			closeDB(db, c);
			return info;
		}
	}

	// 添加一条APP数据到APPSTORE数据库
	public void addAppToStore(AppInfo info, String style) {

		

		if (style.equals(Constant.APPSTORE_MODE_HOME_RECOMMEND) && findRecApp(info.getRecomm_index()) ) {
				updateRecommendData(info);
				Logger.e(
						"gww",
						"$$$---TAG---addAppToStroe---rec_index---"
								+ info.getRecomm_index());
				Logger.e(
						"gww",
						"$$$---TAG---addAppToStroe---packagename---"
								+ info.getPackage_name());
			return;
		} else {

			if (findApp(info.getPackage_name())) {
				deleteAppFromStroe(info.getPackage_name());
			}
			synchronized (LOCK) {
				//Logger.e("kao", "-------addAppToStore----  "+info);
				SQLiteDatabase db = helper.getWritableDatabase();
				//db.enableWriteAheadLogging();
				db.execSQL(
						"INSERT INTO "
								+ Constant.APPSTORE_TABLE_NAME
								+ " (summary,recomm_index,package_name,downloads,url,"
								+ "version,category,banner_big,banner_small,HDIcon,name,rating,icon,recommImage,style,imgs,size,praise,reject,md5,operateType)"
								+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ",
						new String[] { info.getSummary(),
								String.valueOf(info.getRecomm_index()),
								info.getPackage_name(),
								String.valueOf(info.getDownloads()),
								info.getUrl(), info.getVersion(),
								info.getCategory(), info.getBanner_big(),
								info.getBanner_small(), info.getHDIcon(),
								info.getName(),
								String.valueOf(info.getRating()),
								info.getIcon(), info.getRecommImage(),
								info.getStyle(), info.imgs2String(),
								info.getSize() + "",
								String.valueOf(info.getPraise()),
								String.valueOf(info.getReject()) + "",
								String.valueOf(info.getMd5()) + "",
								String.valueOf(info.getOperateType())

						});
				Logger.d("kao", "$_ 2 _$"+info.getName()+"  $$$  "+info.getCategory());
				closeDB(db, null);
				//Logger.e("kao", "-------addAppToStore----  end");
			}
		}
	}

	public void UpdateApp(AppInfo info, boolean flag) {
		synchronized (LOCK) {
			SQLiteDatabase db = helper.getWritableDatabase();
			//db.enableWriteAheadLogging();
			db.execSQL("UPDATE " + Constant.APPSTORE_TABLE_NAME
					+ " SET praise=?,reject=?  WHERE package_name=?",
					new String[] { (info.getPraise()) + "",
							(info.getReject()) + "", info.getPackage_name() });
			closeDB(db, null);
		}
	}

	public void updateRecommendData(AppInfo info) {

		synchronized (LOCK) {
			/*
			 * + " (summary,recomm_index,package_name,downloads,url," +
			 * "version,category,banner_big,banner_small,HDIcon,name,rating,icon,recommImage,style,imgs,size,praise,reject)"
			 * + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
			 */SQLiteDatabase db = helper.getWritableDatabase();

			db.execSQL(
					"UPDATE "
							+ Constant.APPSTORE_TABLE_NAME
							+ " SET summary=?,recomm_index=?,package_name=?,downloads=?,"
							+ "url=?,version=?,category=?,banner_big =?, banner_small=?,HDIcon=?,name=?,rating=?,"
							+ "icon =?,recommImage =?,style=?,imgs=?,size=?,praise =?,reject=? ,md5=?,operateType=? WHERE recomm_index=? ",
					new String[] { info.getSummary(),
							String.valueOf(info.getRecomm_index()),
							info.getPackage_name(),
							String.valueOf(info.getDownloads()), info.getUrl(),
							info.getVersion(), info.getCategory(),
							info.getBanner_big(), info.getBanner_small(),
							info.getHDIcon(), info.getName(),
							String.valueOf(info.getRating()), info.getIcon(),
							info.getRecommImage(), info.getStyle(),
							info.imgs2String(), info.getSize() + "",
							String.valueOf(info.getPraise()),
							String.valueOf(info.getReject()) + "",
							String.valueOf(info.getMd5()) + "",
							String.valueOf(info.getOperateType()),
							info.getRecomm_index() + "" });
			
			closeDB(db, null);
		}
	}

	// 删除一条APP数据
	public void deleteAppFromStroe(String packagename) {
		synchronized (LOCK) {
			SQLiteDatabase db = helper.getWritableDatabase();
			db.execSQL("DELETE FROM " + Constant.APPSTORE_TABLE_NAME
					+ " WHERE package_name=?", new String[] { packagename });
			closeDB(db, null);
		}
	}

	// 查询出相应类型的APP数据
	/*
	 * public List<AppInfo> getAppInfos(String category, String style) {
	 * List<AppInfo> infos = new ArrayList<AppInfo>(); SQLiteDatabase db =
	 * helper.getReadableDatabase(); Cursor c = db.rawQuery("SELECT * FROM " +
	 * Constant.APPSTORE_TABLE_NAME + " WHERE category=? and style=?", new
	 * String[] { category, style });
	 * 
	 * while (c.moveToNext()) { AppInfo info = new AppInfo();
	 * info.setSummary(c.getString(0)); info.setRecomm_index(c.getInt(1));
	 * info.setPackage_name(c.getString(2)); info.setDownloads(c.getInt(3));
	 * info.setUrl(c.getString(4)); info.setVersion(c.getString(5));
	 * info.setCategory(c.getString(6)); info.setBanner_big(c.getString(7));
	 * info.setBanner_small(c.getString(8)); info.setHDIcon(c.getString(9));
	 * info.setName(c.getString(10)); info.setRating(c.getInt(11));
	 * info.setIcon(c.getString(12)); info.setRecommImage(c.getString(13));
	 * info.setStyle(c.getString(14));
	 * 
	 * infos.add(info); } if (db.isOpen()) { c.close(); db.close(); } return
	 * infos; }
	 */

	public List<AppInfo> getAppInfos(String style) {

		synchronized (LOCK) {
			List<AppInfo> infos = new ArrayList<AppInfo>();
			SQLiteDatabase db = new AppDbHelper(context).getReadableDatabase();
			//db.enableWriteAheadLogging();
			Cursor c = db.rawQuery("SELECT * FROM "
					+ Constant.APPSTORE_TABLE_NAME
					+ " WHERE style=? order by recomm_index asc ",
					new String[] { style });
			while (c.moveToNext()) {
				AppInfo info = new AppInfo();
				info.setSummary(c.getString(0));
				info.setRecomm_index(c.getInt(1));
				info.setPackage_name(c.getString(2));
				info.setDownloads(c.getInt(3));
				info.setUrl(c.getString(4));
				info.setVersion(c.getString(5));
				info.setCategory(c.getString(6));
				info.setBanner_big(c.getString(7));
				info.setBanner_small(c.getString(8));
				info.setHDIcon(c.getString(9));
				info.setName(c.getString(10));
				info.setRating(c.getInt(11));
				info.setIcon(c.getString(12));
				info.setRecommImage(c.getString(13));
				info.setStyle(c.getString(14));
				String imgs = c.getString(15);
				String[] ss = imgs.split(",");
				List<Screen> ls = new ArrayList<Screen>();
				for (int i = 0; i < ss.length; i++) {
					Screen tScreen = new Screen();
					tScreen.setUrl(ss[i]);
					ls.add(tScreen);
				}
				info.setImgs(ls);
				info.setSize(c.getLong(16));
				info.setPraise(c.getInt(17));
				info.setReject(c.getInt(18));
				info.setMd5(c.getString(19));
				info.setOperateType(c.getInt(20));
				infos.add(info);
			}
			closeDB(db, c);
			return infos;
		}

	}

	// 获取末安装APP的类型
	public String getStyle(String packagename) {

		synchronized (LOCK) {
			String result = "";
			SQLiteDatabase db = helper.getReadableDatabase();
			//db.enableWriteAheadLogging();
			Cursor c = db.rawQuery("SELECT category FROM "
					+ Constant.APPSTORE_TABLE_NAME + " WHERE package_name=?",
					new String[] { packagename });
			if (c.moveToFirst()) {
				result = c.getString(0);
			}
			closeDB(db, c);
			return result;
		}
	}

	// 获取末安装APP的版本
	public String getVersion(String packagename) {
		synchronized (LOCK) {
			String result = "";
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT version FROM "
					+ Constant.APPSTORE_TABLE_NAME + " WHERE package_name",
					new String[] { packagename });
			if (c.moveToFirst()) {
				result = c.getString(0);
			}
			closeDB(db, c);
			return result;
		}
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

	public Map<String, String> getIconNames() {

		synchronized (LOCK) {
			Map<String, String> icons = new HashMap<String, String>();
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT package_name,HDIcon FROM "
					+ Constant.APPSTORE_TABLE_NAME, null);
			while (c.moveToNext()) {
				String packagename = c.getString(0);
				String filenames = c.getString(1);
				filenames = filenames.substring(filenames.lastIndexOf("/") + 1);
				icons.put(packagename, filenames);
			}
			closeDB(db, c);
			return icons;
		}
	}

	public String getIconName(String packagename) {

		synchronized (LOCK) {
			String result = "";
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT HDIcon FROM "
					+ Constant.APPSTORE_TABLE_NAME + " WHERE package_name=?",
					new String[] { packagename });

			if (c.moveToFirst()) {
				result = c.getString(0);
				/*if (result != null) {
					result = result.substring(result.lastIndexOf("/") + 1);
					Logger.i("gww", "------HDIcon-result---" + result);
				}*/
			}
			closeDB(db, c);
			return result;
		}
	}

	public void deleteRec() {
		synchronized (LOCK) {
			SQLiteDatabase db = helper.getWritableDatabase();
			db.execSQL("DELETE FROM " + Constant.APPSTORE_TABLE_NAME
					+ " WHERE style=?",
					new String[] { Constant.APPSTORE_MODE_HOME_RECOMMEND });
			closeDB(db, null);
		}
	}
}
