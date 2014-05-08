package lenkeng.com.welcome.util;

import java.io.File;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.server.LKService;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.lenkeng.bean.ApkBean;
import com.lenkeng.logic.Logic;
import com.lenkeng.tools.Constants;

public class LKHomeCache {

	protected static final String TAG = "LKHomeCache";
	// public static Map<String, SoftReference<Bitmap>> bitmapCache = null;
	public static LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(
			20);
	public static LruCache<String, Bitmap> launchCache = new LruCache<String, Bitmap>(
			40);
	public static Object lock = new Object();

	public static void initCache() {
		bitmapCache.setWeakRemoveListener(new WeakListener());
	}

	public static synchronized void addCacheBitmap(String key, Bitmap bitmap) {
		/*
		 * if(bitmap==null){ initCache(); }
		 */
		// Log.e(TAG, "~~~~ bitmapcache="+bitmapCache.size());
		/*
		 * if(!bitmapCache.containsKey(key)){ SoftReference<Bitmap> srBitmap =
		 * new SoftReference<Bitmap>(bitmap); bitmapCache.put(key, srBitmap);
		 * 
		 * }
		 */

		if (bitmapCache.get(key) == null) {
			bitmapCache.put(key, bitmap);
		}
	}

	public static synchronized Bitmap getBitmapBykey(String key) {
		if (bitmapCache == null) {
			return null;
		}
		// Log.e(TAG,
		// "~~~~~~getBitmap(),bitmap="+bitmapCache.size()+",key="+key);

		/*
		 * SoftReference<Bitmap> srBitmap = bitmapCache.get(key); if (srBitmap
		 * != null) { Bitmap bitmap = srBitmap.get(); if (bitmap != null) {
		 * return bitmap; }else{ bitmapCache.remove(key); return null; } }
		 * return null;
		 */
		return bitmapCache.get(key);
	}

	public static synchronized boolean containsKey(String url) {
		if (bitmapCache == null) {
			return false;
		}
		if (bitmapCache.get(url) != null) {
			return true;
		}

		return false;
	}

	/*
	 * public static void clearCache(){ if(bitmapCache!=null){
	 * synchronized(lock){ new Thread(){ public void run() { for(Entry<String,
	 * SoftReference<Bitmap>> entry: bitmapCache.entrySet()){ Bitmap
	 * bitmap=entry.getValue().get(); if(bitmap!=null&& ! bitmap.isRecycled()){
	 * bitmap.recycle(); bitmap=null; entry=null; } }
	 * 
	 * bitmapCache.clear(); System.gc(); };}.start(); } } }
	 */

	static class WeakListener implements LruCache.OnWeakRemoveListener {
		@Override
		public void onWeakRemove(Object object) {
			try {
				if (object != null && object instanceof Bitmap) {

					Log.e(TAG, "--------销毁缓存,bitmap=" + object);
					((Bitmap) object).recycle();
				}
			} catch (Exception e) {
			}
		}
	}

	public synchronized static Bitmap loadLaunchBitmap(AppInfo info, Context con) {
		Bitmap bitmap = null;
		String packageName = info.getPackage_name();
		String hdIcon = info.getHDIcon();
		//if ("com.coldworks.coldjoke_letv".equals(packageName))
			
		if (packageName == null || "".equals(packageName)) {
			return null;
		}

		//if ("com.coldworks.coldjoke_letv".equals(packageName))
			
		if (launchCache == null) {
			return null;
		}

		bitmap = launchCache.get(packageName);
		if (bitmap != null) {
			// Log.e(TAG,
			// "------1111从缓存中获取bitmap, packageName="+packageName+",hdIcon="+hdIcon);
			return bitmap;
		}
		//if ("com.coldworks.coldjoke_letv".equals(packageName))
			

		if (bitmap == null) { // 缓存中没有

			if (!"".equals(LKHomeUtil.getPreApkIcon(packageName))) { // 预装apk
				bitmap = LKHomeUtil.decodeBitmapFromFile(
						LKHomeUtil.getPreApkIcon(packageName), 210, 200);
				// Log.e(TAG,
				// "------1111从预装目录加载bitmap="+bitmap+", packageName="+packageName+",hdIcon="+hdIcon);
				//if ("com.coldworks.coldjoke_letv".equals(packageName))
					

			} else {

				if (hdIcon != null && !"".equals(hdIcon)) {
					File imgFile = new File(Constants.IMG_DIR + File.separator,
							hdIcon);
					//if ("com.coldworks.coldjoke_letv".equals(packageName))
						
					if (imgFile != null && imgFile.exists()) {
						bitmap = LKHomeUtil.decodeBitmapFromFile(
								imgFile.getAbsolutePath(), 210, 200);
						//if ("com.coldworks.coldjoke_letv".equals(packageName))
							
					} else {
						// Log.e(TAG, "@@@@@@@@@ 需要从网络下载图片: file-="+hdIcon);
						// new Logic(con).downLoadImg(hdIcon);
						String HDIcon="";
						if (LKService.DOWNLOAD_APPS.containsKey(packageName)) {
							HDIcon = LKService.DOWNLOAD_APPS.get(
									packageName).getHDIcon();
						} else {
							HDIcon = AppStoreDao.getInstance(con)
									.getIconName(packageName);
						}
						//if ("com.coldworks.coldjoke_letv".equals(packageName))
							Logic.getInstance(con)
									.downLoadImg(HDIcon);
						
					}
					// Log.e(TAG,
					// "------1111从SD卡加载bitmap="+bitmap+", packageName="+packageName+",hdIcon="+hdIcon);
				}
			}
			if (bitmap != null) {
				launchCache.put(packageName, bitmap);
				return bitmap;
			}
		}
		return bitmap;

	}

}
