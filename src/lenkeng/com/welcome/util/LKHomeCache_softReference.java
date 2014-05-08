package lenkeng.com.welcome.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

public class LKHomeCache_softReference {

	protected static final String TAG = "LKHomeCache";
	public static Map<String, SoftReference<Bitmap>> bitmapCache = null;
	public static Object lock=new Object();
	
	public static void initCache(){
		if(bitmapCache==null){
			bitmapCache = new HashMap<String, SoftReference<Bitmap>>();
		}
	}
	
	public static synchronized void addCacheBitmap(String key, Bitmap bitmap){
		if(bitmap==null){
			initCache();
		}
		//Log.e(TAG, "~~~~ bitmapcache="+bitmapCache.size());
		if(!bitmapCache.containsKey(key)){
			SoftReference<Bitmap> srBitmap = new SoftReference<Bitmap>(bitmap);
			bitmapCache.put(key, srBitmap);
			
		}
	}
	
	public static synchronized Bitmap getBitmapBykey(String key){
		if(bitmapCache==null){
			return null;
		}
		//Log.e(TAG, "~~~~~~getBitmap(),bitmap="+bitmapCache.size()+",key="+key);
		
		SoftReference<Bitmap> srBitmap = bitmapCache.get(key);
		if (srBitmap != null) {
			Bitmap bitmap = srBitmap.get();
			if (bitmap != null) {
				return bitmap;
			}else{
				bitmapCache.remove(key);
				return null;
			}
		}
		return null;
		
	}

	public static synchronized boolean containsKey(String url) {
		if(bitmapCache==null){
			return false;
		}
		if(bitmapCache.containsKey(url)){
			return true;
		}
		
		return false;
	}
	
	/*public static void clearCache(){
		if(bitmapCache!=null){
		synchronized(lock){
			new Thread(){
			public void run() {
				for(Entry<String, SoftReference<Bitmap>> entry: bitmapCache.entrySet()){
					Bitmap bitmap=entry.getValue().get();
					if(bitmap!=null&& ! bitmap.isRecycled()){
						bitmap.recycle();
						bitmap=null;
						entry=null;
					}
				}
				
				bitmapCache.clear();
				System.gc();
			};}.start();
		 }
		}
	}*/
	
	
}
