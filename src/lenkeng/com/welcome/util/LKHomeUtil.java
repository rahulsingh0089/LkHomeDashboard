package lenkeng.com.welcome.util;

import java.io.BufferedReader;




import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import lenkeng.com.welcome.LKHomeApp;
import lenkeng.com.welcome.MainHomeActivity;
import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.bean.ScanInfo;
import lenkeng.com.welcome.bean.WeatherInfo;
import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.view.LKToast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PatternMatcher;
import android.os.SystemProperties;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;
import android.hardware.Camera.Size;
import android.hardware.LkecDevice;
//import com.softwinner.Gpio;
import com.lenkeng.bean.Screen;
import com.lenkeng.bean.URLs;
import com.lenkeng.tools.Util;

 

//全局工具类
@SuppressLint("SdCardPath")
public class LKHomeUtil {
	
    public static boolean DEBUG=true;
    public static String PROPERTY_FILE;
    public static final String PROPERTY_FILE_A20="/mnt/extsd/debug.properties";
	public static final String PROPERTY_FILE_RK="/mnt/external_sd/debug.properties";
	
    private static String preInstallUrl="/data/preinstallres/";
	private static final String TAG = "LKHomeUtil";
	private static LkecDevice lk_device = new LkecDevice();
	private static Context context;
	private Timer mTimer;
	public static LinkedHashMap<String, String> addAppMap =new LinkedHashMap<String, String>();;
	private static Map<String, String> packageMapRes;
	public static Map<String,Integer> mapDrawable;
	private static AppDataDao appDao;
	private static ConnectionConfiguration config;
	public static XMPPConnection conn = null;
	private SharedPreferences sp;
	public static Time time;
	private static AppStoreDao appStoreDao;
	private static List<ScanInfo> scanInfos;
	private static String upgradeLog;
	private static String upgradeUrl;
	private static Map<Integer, SoftReference<AppInfo>> mapCache;
	private static ConnectivityManager manager;

	public static final String CURRENT_NET_TYPE = "LKsetting_netButton_id";
	public static final String DISPLAY_PERCENT_HORIZONTAL = "display_percent_horizontal";// 水平百分比
	public static final String DISPLAY_PERCENT_VERTICAL = "display_percent_vertical"; // 垂直百分比
	public static final Map<String,Boolean> MOVE_MAP=new HashMap<String,Boolean>();
	
	
	//需要删除的app集合, 如果是更新app的位置,先删除,再添加
	public static LinkedHashMap<String,String> removeAppMap=new LinkedHashMap<String, String>();
	public static LinkedHashMap<String,String> updateAppStyleMap=new LinkedHashMap<String, String>();
	
	//描述某个app在某个类别下的优先位置.: key: "包名"+"="+"类别字符串"; value: 优先级: 1为最大,值越大,优先级越低
	public static LinkedHashMap<String,Integer> updateAppPriorityMap=new LinkedHashMap<String, Integer>();
	
	//相同包名,需要添加到不同栏目下的app
	public static LinkedHashMap<String, String> extraAppMap =new LinkedHashMap<String, String>();
	
	
	
	static {

		Log.e(TAG, "add app--++-");
		
		
		//======================需要删除的应用集合,需要更换app 栏目,先删除,再添加,然后更改优先级===================
		//removeAppMap.put("com.android.vending",Constant.CLASSIFY_USER);
		//removeAppList.add("com.filmoncom.android.stb");
				
		
		
		
		//==============需要添加到数据库的app列表, 如果数据库已经存在该包名且style相同的app,直接返回==============
		packageMapRes = new HashMap<String, String>();
		mapDrawable=new HashMap<String, Integer>();
		
		//packageMapRes.put("com.android.browser", R.drawable.browser);
		addAppMap.put("com.softwinner.update", Constant.CLASSIFY_USER);
		addAppMap.put("com.android.vending", Constant.CLASSIFY_USER);
		addAppMap.put("com.android.settings", Constant.CLASSIFY_USER);

		

		try {
			String[] strs=getPreApk();
			for(int i=0;i<strs.length;i++){
				String[] pakSty=strs[i].split("\\$");
				String packagename=pakSty[0];
				String sty=pakSty[1];
				addAppMap.put(packagename, sty);
				packageMapRes.put(packagename, preInstallUrl+packagename+".png");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		//-----------英文版独有------
		addAppMap.put("com.android.email", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.android.email", R.drawable.email);
		addAppMap.put("com.lenkeng.video", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.lenkeng.video", R.drawable.video_icon);
		addAppMap.put("com.android.browser", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.android.browser", R.drawable.browser);
		addAppMap.put("com.lenkeng.filebrowser", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.lenkeng.filebrowser", R.drawable.icon_filebrowser);
	 
		addAppMap.put("com.softwinner.TvdFileManager", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.softwinner.TvdFileManager", R.drawable.file_manager);
		addAppMap.put("com.android.music", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.android.music", R.drawable.music);
		addAppMap.put("com.android.soundrecorder", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.android.soundrecorder", R.drawable.recoder);
		addAppMap.put("com.android.rockchip", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.android.rockchip", R.drawable.file_manager);
		//appStyles.put("android.rk.RockVideoPlayer", Constant.CLASSIFY_APPLICATION);
		addAppMap.put("com.android.gallery3d", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.android.gallery3d", R.drawable.video_player);
		
		addAppMap.put("com.adobe.flashplayer", Constant.CLASSIFY_APPLICATION);
		mapDrawable.put("com.adobe.flashplayer", R.drawable.flashplayer);

		//--------------------------
	//============额外需要添加的app===============
	//	extraAppMap.put("com.android.vending", Constant.CLASSIFY_USER);
		
		

		//===============需要修改app类别的应用集合==================
		//updateAppStyleMap.put("com.filmoncom.android.stb", Constant.CLASSIFY_USER);
		
		
		
		//================修改应用的显示位置====================
	//	updateAppPriorityMap.put("com.filmoncom.android.stb"+","+Constant.CLASSIFY_APPLICATION, 1);
	//	updateAppPriorityMap.put("air.BattleCamAndroid"+","+Constant.CLASSIFY_APPLICATION, 2);
	//	updateAppPriorityMap.put("com.android.vending"+","+Constant.CLASSIFY_APPLICATION, 3);
	//	updateAppPriorityMap.put("com.android.vending"+","+Constant.CLASSIFY_USER, 1);
		
	}

	private static void getDebugFileDir() {
		String platform = SystemProperties.get("ro.hardware");
		if(platform.startsWith("rk")){ //rk
			PROPERTY_FILE=PROPERTY_FILE_RK;
			
		}else if(platform.startsWith("sun")){ //a20
			PROPERTY_FILE=PROPERTY_FILE_A20;
		}
	}

	public static String getPreApkIcon(String packagename) {
		//Log.e(TAG, "-----5555-- getPreApkIcon, packagenme="+packagename+",packaeMap="+packageMapRes);
		
		if (packageMapRes.containsKey(packagename)) {
			return packageMapRes.get(packagename);
		} else {
			return "";
		}
	}
	
	public static boolean isPreApp(String packagename) {
		return addAppMap.containsKey(packagename);
	}

	
	
	//改为单例模式
	private static LKHomeUtil instance;
	public static synchronized LKHomeUtil getInstance(Context context){
		if(instance==null){
			instance=new LKHomeUtil(context);
		}
		return instance;
	}
	
	
	/**
	 * @param context
	 */
	private LKHomeUtil(Context context) {
		this.context = context;
		appDao =  AppDataDao.getInstance(context);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		time = new Time(Time.getCurrentTimezone());
		appStoreDao = AppStoreDao.getInstance(LKHomeApp.getAppContext());
		mapCache = new HashMap<Integer, SoftReference<AppInfo>>();
		manager = (ConnectivityManager) context
				.getSystemService(Service.CONNECTIVITY_SERVICE);
	}

	public void installDemolauncher() {
		File f = new File("/sdcard/appinfo/DemoLauncher.apk");
		if (f.exists()) {

		} else {
			new File("/sdcard/appinfo").mkdir();
			try {
				AssetManager manager = context.getAssets();
				InputStream is = manager.open("DemoLauncher.apk");
				FileOutputStream fos = new FileOutputStream(f);
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				fos.flush();
				fos.close();
				is.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// judge the wifi whether connetied or not
	public static boolean isNetConnected() {
		if (manager == null) {
			return false;
		}

		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected() && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public static int getConnectedStyle() {
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (null != info && info.getType() == ConnectivityManager.TYPE_WIFI) {
			return 1;
		} else {
			return 0;
		}
	}

	public static int getWifiLevel() {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		int level = WifiManager.calculateSignalLevel(info.getRssi(), 4);
		return level;
	}

	// 通过包名获取应用程序的图标
	public static Drawable getIcon(String packageName) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pInfo;
			pInfo = pm.getPackageInfo(packageName, 1);
			ApplicationInfo aInfo = pInfo.applicationInfo;
			Drawable icon = aInfo.loadIcon(pm);
			return icon;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 通过包名获取应用的名字
	public static String getLabel(String packageName) {
		PackageManager pm;
		PackageInfo pInfo;
		ApplicationInfo aInfo;
		String label;
		try {
			pm = context.getPackageManager();
			pInfo = pm.getPackageInfo(packageName, 1);
			aInfo = pInfo.applicationInfo;
			label = aInfo.loadLabel(pm).toString();

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return label;
	}

	// 进行图片放大缩小的方法
	public static Drawable resizeImage(Bitmap bitmap, float h) {
		if (bitmap != null) {
			// load the origial Bitmap
			Bitmap BitmapOrg = bitmap;

			int width = BitmapOrg.getWidth();
			int height = BitmapOrg.getHeight();

			float w = (width / height) * h;
			/*
			 * float newWidth = w; float newHeight = h;
			 */

			// calculate the scale
			float scaleWidth = w / width;
			float scaleHeight = h / height;

			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// resize the Bitmap
			matrix.postScale(scaleWidth, scaleHeight);
			// if you want to rotate the Bitmap
			// matrix.postRotate(45);

			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
					height, matrix, true);

			// make a Drawable from Bitmap to allow to set the Bitmap
			// to the ImageView, ImageButton or what ever
			return new BitmapDrawable(resizedBitmap);
		} else {
			return null;
		}
	}

	// 启动一个定时器，进行定时操作
	public  void startTimer() {
		mTimer = new Timer();
		 
		// mTimer.notify();
		TimerTask mTimerTask = new TimerTask() {
			int status=0;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// v.setVisibility(View.GONE);
				//handler.sendEmptyMessage(Constant.HANADLER_FLASH_MSG);
				Intent check = new Intent();
				check.setAction("checkVideo");
				LKHomeApp.getAppContext().sendBroadcast(check);
			}
		};
		mTimer.schedule(mTimerTask, 1000*60*60, 1000*60*60);
	}
	// 关闭定时器
	public void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
	}

	// 判断一个应用程序是否已经安装
	public static boolean isInstalled(String packageName) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(packageName, 0);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if (info == null) {
			return false;
		} else {
			return true;
		}
	}
	
	
	// 判断一个应用程序是否已经安装
		public static boolean isInstalled(Context con,String packageName) {
			PackageManager manager = con.getPackageManager();
			PackageInfo info = null;
			try {
				info = manager.getPackageInfo(packageName, 0);
			} catch (Exception e) {
				
			}
			if (info == null) {
				return false;
			} else {
				return true;
			}
		}
	
	

	// 系统默认的APP分类
	public static void systemAppClassify() {
		//3.删除
		removeAppRecorder();
		//1.新增
		addAppRecorder();
		
		/*//2.修改
		updateAppRecorder();*/
		
		//3.修改优先级
		updateAppPriority();
		
		
		
	}


	private static void updateAppPriority() {
		
		Set<String> set = updateAppPriorityMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String tempStr = it.next();
			String packageName = tempStr.split(",")[0];
			String style = tempStr.split(",")[1];
			int priority=updateAppPriorityMap.get(tempStr);
			appDao.updateAppPriority(packageName, style, priority);
		}
	}

	private static void updateAppRecorder() {
		Set<String> set = updateAppStyleMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String packageName = it.next();
			appDao.updateAppStyle(packageName,updateAppStyleMap.get(packageName));
		}
		
	}

	private static void removeAppRecorder() {
		
		Set<String> set = removeAppMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String packageName = it.next();
			appDao.removeApp(packageName,removeAppMap.get(packageName));
		}
		
		
	}
	
	
	
	
	private static void addAppRecorder() {
		
		Set<String> set = addAppMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String packageName = it.next();
			if (isInstalled(packageName)) {
				appDao.addApp(packageName, addAppMap.get(packageName),
						getLabel(packageName), null, getVersion(packageName));
			}
		}
		
		
		//相同包名添加到不同栏目下
		Set<String> set2 = extraAppMap.keySet();
		Iterator<String> it2 = set2.iterator();
		while (it2.hasNext()) {
			String packageName = it2.next();
			if (isInstalled(packageName)) {
				appDao.addApp(packageName, extraAppMap.get(packageName),
						getLabel(packageName), null, getVersion(packageName));
			}
		}
		
		
	}

	

	//
	public static XMPPConnection getConnection() {
		

		if (conn != null && conn.isConnected() && conn.isAuthenticated()) {
			return conn;
		} else {
			try {
				config = new ConnectionConfiguration(URLs.getXmppHost(), Constant.SERVER_PORT);
				config.setRosterLoadedAtLogin(false);
				config.setReconnectionAllowed(true);
				config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
				config.setSASLAuthenticationEnabled(true);
				config.setTruststorePath("/system/etc/security/cacerts/cacerts.bks");
				config.setTruststoreType("bks");
				conn = new XMPPConnection(config);
				conn.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			return conn;
		}
	}

	// get the current versioncode
	public static int getVersion(String packageName) {
		int version = 0;
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			version = info.versionCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	public static String getVersionNmae(String packageName) {
		String version = "";
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			version = info.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	public static String getDir(String packageName) {
		String appDir = "";
		PackageManager manager = context.getPackageManager();
		try {
			ApplicationInfo aInfo = manager.getApplicationInfo(packageName, 0);
			appDir = aInfo.sourceDir;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appDir;
	}

	public static List<AppInfo> getInstallApp() {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> infos = manager.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packInfo : infos) {
			AppInfo info = new AppInfo();
			info.setPackage_name(packInfo.packageName);
			appInfos.add(info);
		}
		return appInfos;
	}

	// 加载网络图片
	public Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			InputStream isr = null;
			if (conn.getResponseCode() == 200) {
				isr = conn.getInputStream();
				bitmap = BitmapFactory.decodeStream(isr);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	// 加载本地图片
	public static Bitmap getLoacalBitmap(String url)
			throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(url);
		return BitmapFactory.decodeStream(fis);
	}
	
	public static Drawable getLocalDrawableRec(String url) {
		// FileInputStream fis = new FileInputStream(url);
		if (null == url) {
			Log.e(TAG, "~~~~~~~ url is null");

			return null;
		} else {
			File f = new File(url);
			if (!f.exists()) {
				return null;
			} else {
				//return BitmapDrawable.createFromPath(f.getAbsolutePath());
				return BitmapDrawable.createFromPath(url);
			}
		}
	}
	public static Drawable getLocalDrawable(String url) {
		// FileInputStream fis = new FileInputStream(url);
		if (null == url) {
			return null;
		} else {
			File f = new File("/sdcard/.AppMarKet/imgs", url);
			if (!f.exists()) {
				return null;
			} else {
				return BitmapDrawable.createFromPath(f.getAbsolutePath());
			}
		}
	}

	public static void addSoftReference(List<AppInfo> infos) {
		for (int i = 0; i < infos.size(); i++) {
			SoftReference<AppInfo> app = new SoftReference<AppInfo>(
					infos.get(i));
			mapCache.put(i, app);
		}
	}

	public static AppInfo getSoftReference(int key) {
		SoftReference<AppInfo> app = mapCache.get(key);
		if (null != app) {
			return app.get();
		} else {
			return null;
		}

	}

	// 获取未安装apk文件的包名
	public String getPackageName(String path) {
		String packageName = "";
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);
		ApplicationInfo appInfo = null;
		if (info != null) {
			appInfo = info.applicationInfo;
			packageName = appInfo.packageName;
		}
		return packageName;
	}

	// 获取未安装apk文件的版本号
	public static int getVersionByfile(String path) {

		File f = new File(path);
		if (!f.exists()) {
			return -1;
		} else {
			int version = -1;
			if (path != null) {
				PackageManager manager = context.getPackageManager();
				PackageInfo info = manager.getPackageArchiveInfo(f.getPath(),
						PackageManager.GET_ACTIVITIES);
				version = info.versionCode;
			}
			return version;
		}
	}

	// get the current hour
	public static String getCurrentHour() {
		time.setToNow();
		String hour = time.hour + "";
		if (time.hour < 10) {
			hour = "0" + time.hour;
		}
		return hour;
	}

	public static String getHour() {

		Date currentTime = new Date();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String dateString = formatter.format(currentTime);

		String hour;

		hour = dateString.substring(11, 13);

		return hour;
	}

	// get the current minute
	public static String getCurrentMinute() {
		time.setToNow();
		String minute = time.minute + "";
		if (time.minute < 10) {
			minute = "0" + time.minute;
		}
		return minute;
	}

	// get the current week
	public static int getWeek() {
		time.setToNow();
		int week = time.weekDay;
		return week;
	}

	// get the current date
	/*
	 * public static String getCurrentDate() { time.setToNow(); // String str1 =
	 * getCurrentHour() + " : " + getCurrentMinute() + " ";
	 * 
	 * String str2 = (time.month + 1) + context.getString(R.string.text_month) +
	 * time.monthDay + context.getString(R.string.text_day);
	 * 
	 * String str3 = "  " + context.getString(Constant.WEEKDAYS[getWeek()]);
	 * 
	 * return str2 + str3; }
	 */

	public static List<AppInfo> jsonAppInfoFile(String url, String mode)
			throws Exception {
		int rec_num = 0;
		/*
		 * StringBuilder builder = new StringBuilder(); String body = null;
		 * FileInputStream fis = new FileInputStream(file); BufferedReader
		 * reader = new BufferedReader(new InputStreamReader(fis)); String line
		 * = null; while ((line = reader.readLine()) != null) {
		 * builder.append(line + "\n"); } reader.close(); body =
		 * builder.toString(); List<AppInfo> appInfos=new ArrayList<AppInfo>();
		 * JSONObject jsonObj = new JSONObject(body);
		 */
		HttpClient client = new DefaultHttpClient();
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 2000); // 链接超时2秒
		HttpConnectionParams.setSoTimeout(params, 2000); // 请求超时2秒

		Log.e(TAG, "!!!!!!!!!!!!!!!!! 获取推荐信息: url="+url);
		
		HttpGet get = new HttpGet(url);
		get.setParams(params);
		HttpResponse httpResponse = client.execute(get);
		// 解析开始
		JSONTokener parser = new JSONTokener(EntityUtils.toString(httpResponse
				.getEntity()));
		JSONObject jsonObject = (JSONObject) parser.nextValue();
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		if (Constant.APPSTORE_MODE_HOME_RECOMMEND.equals(mode)) {

		}
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			AppInfo appInfo = new AppInfo();

			appInfo.setSummary(obj.getString("summary"));
			appInfo.setCategory(obj.getString("category"));
			appInfo.setDownloads(obj.getInt("downloads"));
			appInfo.setIcon(obj.getString("icon"));
			appInfo.setSize(obj.getLong("size"));
			appInfo.setPackage_name(obj.getString("package_name"));
			appInfo.setName(obj.getString("name"));
			appInfo.setVersion(obj.getString("version"));
			appInfo.setHDIcon(obj.getString("HDIcon"));
			appInfo.setMd5(obj.getString("md5"));
			appInfo.setOperateType(obj.getInt("operateType"));  
			appInfo.setRealSize(obj.getLong("realSize"));
			Logger.e(TAG, "~~~~~~~~~~~~~~~~operateType="+appInfo.getOperateType()+",json="+obj.getInt("operateType"));
			// appInfo.setRecommImage(context.getString(R.string.url_str)+obj.getString("recommImage"));
			appInfo.setUrl(URLs.getBASE()+ obj.getString("url"));

			JSONArray imgs = obj.getJSONArray("imgs");
			List<Screen> tList = new ArrayList<Screen>();

			for (int j = 0; j < imgs.length(); j++) {
				Screen tScreen = new Screen();
				String u = imgs.getJSONObject(j).getString("url");
				tScreen.setUrl(u);
				tList.add(tScreen);
			}
			appInfo.setImgs(tList);

			appInfo.setStyle(mode);
			if (Constant.APPSTORE_MODE_HOME_RECOMMEND.equals(mode)) {
				appInfo.setRecommImage(URLs.getBASE()
						+ obj.getString("recommImage"));

				appInfo.setRecomm_index(obj.getInt("recomm_index"));
				rec_num++;
				Logger.e(TAG, "----------------json mode--------" + mode);
			} else if (Constant.APPSTORE_MODE_BANNER.equals(mode)) {
				appInfo.setBanner_big(URLs.getBASE()
						+ obj.getString("banner_big"));
				appInfo.setBanner_small(URLs.getBASE()
						+ obj.getString("banner_small"));
				Logger.e(
						TAG,
						"-----json--info.getbannersmall---"
								+ appInfo.getBanner_small());
				Logger.e(TAG, "----------------json mode--------" + mode);
			} else if (Constant.APPSTORE_MODE_STANDARD.equals(mode)) {
				Logger.e(TAG, "----------------json mode--------" + mode);
			}

			
			
			// appUninstallDao.addAppInfo(appInfo);
			appInfos.add(appInfo);
			if (appInfo.getRecomm_index() <= 6 && appInfo.getRecomm_index() > 0) {
				appStoreDao.addAppToStore(appInfo,
						Constant.APPSTORE_MODE_HOME_RECOMMEND);
			}

		}
		RECO_NUM = rec_num;
		return appInfos;
	}

	private static int RECO_NUM = 1;

	public int getRecommendNums() {
		return RECO_NUM;
	}

	public static List<ScanInfo> jsonScanInfo(String url) throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 2000); // 链接超时2秒
		HttpConnectionParams.setSoTimeout(params, 2000); // 请求超时2秒

		HttpGet get = new HttpGet(url);
		get.setParams(params);
		HttpResponse httpResponse = client.execute(get);
		// 解析开始
		JSONTokener parser = new JSONTokener(EntityUtils.toString(httpResponse
				.getEntity()));
		JSONObject jsonObject = (JSONObject) parser.nextValue();
		JSONArray jsonArray = jsonObject.getJSONArray("data");

		List<ScanInfo> scanInfos = new ArrayList<ScanInfo>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			ScanInfo scanInfo = new ScanInfo();
			scanInfo.setTitle(obj.getString("title"));
			scanInfo.setImg(URLs.getBASE()
					+ obj.getString("image"));
			scanInfo.setUrl(obj.getString("url"));
			scanInfos.add(scanInfo);
		}
		return scanInfos;
	}

	public static WeatherInfo jsonWeatherFile(File file) throws Exception {
		StringBuilder builder = new StringBuilder();
		String body = null;
		FileInputStream fis = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		reader.close();
		body = builder.toString();
		// Logger.v("tag", "$$$---"+TAG+"---body---"+body);
		// Logger.v("tag", "$$$---"+TAG+"---file---"+file.getAbsolutePath());
		JSONObject jsonObj = new JSONObject(body).getJSONObject("weatherinfo");
		WeatherInfo weatherInfo = null;
		if (body != null) {
			weatherInfo = new WeatherInfo();
			weatherInfo.setCity(jsonObj.getString("city"));
			weatherInfo.setCity_en(jsonObj.getString("city_en"));
			weatherInfo.setDate_y(jsonObj.getString("date_y"));
			weatherInfo.setWeek(jsonObj.getString("week"));
			String[] dates = new String[] {
					getWillDate(0, jsonObj.getString("date_y")),
					getWillDate(1, jsonObj.getString("date_y")),
					getWillDate(2, jsonObj.getString("date_y")),
					getWillDate(3, jsonObj.getString("date_y")),
					getWillDate(4, jsonObj.getString("date_y")) };
			weatherInfo.setDates(dates);
			String[] temps = new String[] { jsonObj.getString("temp1"),
					jsonObj.getString("temp2"), jsonObj.getString("temp3"),
					jsonObj.getString("temp4"), jsonObj.getString("temp5") };

			weatherInfo.setTemp(temps);
			String[] tempFs = new String[] { jsonObj.getString("tempF1"),
					jsonObj.getString("tempF2"), jsonObj.getString("tempF3"),
					jsonObj.getString("tempF4"), jsonObj.getString("tempF5") };
			weatherInfo.setTempF(tempFs);

			int[] imgs = new int[] {
					Integer.parseInt(jsonObj.getString("img1")),
					Integer.parseInt(jsonObj.getString("img3")),
					Integer.parseInt(jsonObj.getString("img5")),
					Integer.parseInt(jsonObj.getString("img7")),
					Integer.parseInt(jsonObj.getString("img9")) };
			weatherInfo.setImage(imgs);
			String[] weathers = new String[] { jsonObj.getString("weather1"),
					jsonObj.getString("weather2"),
					jsonObj.getString("weather3"),
					jsonObj.getString("weather4"),
					jsonObj.getString("weather5") };
			String[] winds = new String[] { jsonObj.getString("wind1"),
					jsonObj.getString("wind2"), jsonObj.getString("wind3"),
					jsonObj.getString("wind4"), jsonObj.getString("wind5") };
			weatherInfo.setWind(winds);
			weatherInfo.setWeather(weathers);
		}
		return weatherInfo;
	}

	public static long dayIndex(String startDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// 需要将时间转换成2012-11-02格式
		startDate = startDate.replace("年", "-");
		startDate = startDate.replace("月", "-");
		long index = 0;
		try {
			// 目标日期
			Date beginDate = df.parse(startDate);
			// 当前日期，其实getDate9()方法为获取当前系统日期
			Date endDate = df.parse(df.format(new Date()));
			index = endDate.getTime() - beginDate.getTime();
			index = index / (1000 * 60 * 60 * 24);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return index;
	}

	public static String getWillDate(int wichDay, String startDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		// 需要将时间转换成2012-11-02格式
		startDate = startDate.replace("年", "-");
		startDate = startDate.replace("月", "-");
		String s = "";
		Date date;
		try {
			date = df.parse(startDate);
			long targetDate = 1000 * 60 * 60 * 24 * wichDay;

			date.setTime(date.getTime() + targetDate);
			s = df.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	public static void makeCacheDir() {
		// File sysWeDao=new File("/system/weather");
		File weather = new File("/sdcard/weather");
		File AppInfo = new File("/sdcard/appinfo");
		/*
		 * if(!sysWeDao.exists()){ boolean b=sysWeDao.mkdir(); Logger.v("tag",
		 * "$$$---"+TAG+"---makeCacheDir---"+b); }
		 */
		if (!weather.exists()) {
			weather.mkdir();
			weather.setExecutable(false);

		}
		if (!AppInfo.exists()) {
			AppInfo.mkdir();
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// 获取用户安装的应用
	public static List<String> getUserInstallApp() {
		List<String> userApp = new ArrayList<String>();
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> infos = manager.getInstalledPackages(0);
		for (PackageInfo packInfo : infos) {
			String pName = packInfo.packageName;

			if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {

			} else {
				userApp.add(pName);
			}
		}
		return userApp;
	}

	
	/**
	 * 光照效果
	 * 
	 * @param bmp
	 *            光照中心x坐标
	 * @param centerX
	 *            光照中心要坐标
	 * @param centerY
	 * @return
	 */
	public static Bitmap sunshine(Bitmap bmp, int centerX, int centerY) {
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;
		int radius = Math.min(centerX, centerY);

		final float strength = 150F; // 光照强度 100~150
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(
						centerX - k, 2));
				if (distance < radius * radius) {
					// 按照距离大小计算增加的光照值
					int result = (int) (strength * (1.0 - Math.sqrt(distance)
							/ radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	// 放大缩小图片
	public static Drawable zoomBitmap(Drawable d, int h) {
		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap bitmap = bd.getBitmap();
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float w;
			float scaleWidht;
			float scaleHeight;
			if (width > height) {
				w = (width / height) * h;

			} else {
				w = (height / width) * h;
			}
			scaleWidht = ((float) w / width);
			scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix, true);

			return new BitmapDrawable(newbmp);
		} else {
			return null;
		}

	}

	public static boolean isTopActivy() {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = manager.getRunningTasks(1).get(0).topActivity;
		String packageName = cn.getPackageName();
		if (packageName != null && context.getPackageName().equals(packageName)) {
			return true;
		} else {
			return false;
		}
	}

	// 将Drawable转化为Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public List<AppInfo> getAppList(int page, String style_flag,
			List<AppInfo> allAppInfos) {
		List<AppInfo> mList = new ArrayList<AppInfo>();
		// scan_list = new ArrayList<String>();
		// setting_list = new ArrayList<String>();
		int i = page * 10;
		int iEnd = i + 10;
		if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			/*
			 * while ((i < Constant.SETTING_ICONS.length) && (i < iEnd)) {
			 * AppInfo info = new AppInfo();
			 * info.setPackage_name(Constant.SETTING_ACTION[i]); //
			 * info.setName(context.getString(Constant.SETTINGS_NAME[i]));
			 * info.setHDIcon(null); mList.add(info); i++; }
			 */
			for (int j = 0; j < Constant.SETTING_ICONS.length; j++) {
				AppInfo info = new AppInfo();
				info.setPackage_name(Constant.SETTING_ACTION[j]);
				// info.setName(context.getString(Constant.SETTINGS_NAME[i]));
				info.setHDIcon(String.valueOf(Constant.SETTING_ICONS[j]));
				mList.add(j, info);
			}
		} else if (!Util.isEmpty(style_flag) && !"recommend".equals(style_flag)) {
			while ((i < allAppInfos.size()) && (i < iEnd)) {
				mList.add(allAppInfos.get(i));
				i++;
			}
		}
		return mList;
	}

	public static Bitmap decodeSampledBitmapFromResource(InputStream is) {
		// First decode with inJustDecodeBounds=true to check dimensions
		BitmapFactory.Options options = new BitmapFactory.Options();
		// Calculate inSampleSize
		options.inJustDecodeBounds = false;
		options.inSampleSize = 4;
		return BitmapFactory.decodeStream(is, null, options);
	}

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap getBitmapByString(InputStream stream) {

		/*
		 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 * image.compress(Bitmap.CompressFormat.PNG, 100, baos); if(
		 * baos.toByteArray().length / 1024>1024)
		 * {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
		 * baos.reset();//重置baos即清空baos
		 * image.compress(Bitmap.CompressFormat.PNG, 50,
		 * baos);//这里压缩50%，把压缩后的数据存放到baos中 } ByteArrayInputStream isBm = new
		 * ByteArrayInputStream(baos.toByteArray());
		 */
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(stream, null, newOpts);
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;
		float ww = 400f;
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		newOpts.inJustDecodeBounds = false;
		// isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(stream, null, newOpts);
		return bitmap;// 压缩好比例大小后再进行质量压缩
	}

	// 检测是否有升级版本
	public boolean checkVersion(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(50000);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			is = conn.getInputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1) {
				String s = new String(buffer, 0, len);
				sb.append(s);
			}
			is.close();
		}

		JSONObject obj = new JSONObject(sb.toString());
		boolean needUpdate = obj.getBoolean("need");
		if (needUpdate) {
			/*
			 * JSONObject obj_data=obj.getJSONObject("data");
			 * updateInfo=obj_data.getString("apklog"); Logger.i("gww",
			 * "-----log---"+updateInfo);
			 */
			upgradeLog = obj.getString("log");
			upgradeUrl = obj.getString("url");
		}
		return needUpdate;
	}

	public void onIntentSelected(String com) {
		// Build a reasonable intent filter, based on what matched.
		PackageManager pm = context.getPackageManager();
		IntentFilter filter = new IntentFilter();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		List<ResolveInfo> resoInfos = pm.queryIntentActivities(intent, 0);
		if (intent.getAction() != null) {
			filter.addAction(intent.getAction());
		}
		Set<String> categories = intent.getCategories();
		if (categories != null) {
			for (String cat : categories) {
				filter.addCategory(cat);
			}
		}
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		if (resoInfos != null) {
			ComponentName defaultLauncher = new ComponentName(
					"com.example.testlauncher", com);
			ResolveInfo ri = null;
			for (int i = 0; i < resoInfos.size(); i++) {
				ResolveInfo info = resoInfos.get(i);
				if (defaultLauncher.getClassName().equals(
						info.activityInfo.name)) {
					ri = info;
				}
			}
			int cat = ri.match & IntentFilter.MATCH_CATEGORY_MASK;
			Uri data = intent.getData();
			if (cat == IntentFilter.MATCH_CATEGORY_TYPE) {
				String mimeType = intent.resolveType(context);
				if (mimeType != null) {
					try {
						filter.addDataType(mimeType);
					} catch (IntentFilter.MalformedMimeTypeException e) {
						// Log.w("ResolverActivity", e);
						filter = null;
					}
				}
			}

			if (data != null && data.getScheme() != null) {
				// We need the data specification if there was no type,
				// OR if the scheme is not one of our magical "file:"
				// or "content:" schemes (see IntentFilter for the reason).
				if (cat != IntentFilter.MATCH_CATEGORY_TYPE
						|| (!"file".equals(data.getScheme()) && !"content"
								.equals(data.getScheme()))) {
					filter.addDataScheme(data.getScheme());

					// Look through the resolved filter to determine which
					// part
					// of it matched the original Intent.
					Iterator<IntentFilter.AuthorityEntry> aIt = ri.filter
							.authoritiesIterator();
					if (aIt != null) {
						while (aIt.hasNext()) {
							IntentFilter.AuthorityEntry a = aIt.next();
							if (a.match(data) >= 0) {
								int port = a.getPort();
								filter.addDataAuthority(a.getHost(),
										port >= 0 ? Integer.toString(port)
												: null);
								break;
							}
						}
					}
					Iterator<PatternMatcher> pIt = ri.filter.pathsIterator();
					if (pIt != null) {
						String path = data.getPath();
						while (path != null && pIt.hasNext()) {
							PatternMatcher p = pIt.next();
							if (p.match(path)) {
								filter.addDataPath(p.getPath(), p.getType());
								break;
							}
						}
					}
				}
			}

			if (filter != null) {
				final int N = resoInfos.size();
				ComponentName[] set = new ComponentName[N];
				int bestMatch = 0;
				for (int i = 0; i < N; i++) {
					ResolveInfo r = resoInfos.get(i);
					set[i] = new ComponentName(r.activityInfo.packageName,
							r.activityInfo.name);
					if (r.match > bestMatch)
						bestMatch = r.match;
				}
				pm.addPreferredActivity(filter, bestMatch, set,
						new ComponentName("com.example.testlauncher", com));
			}
		}
	}

	/*
	 * public static void slideview(final float p1, final float p2,long
	 * durationMillis,long delayMillis,final View view ) { TranslateAnimation
	 * animation = new TranslateAnimation(p1, p2, 0, 0);
	 * animation.setInterpolator(new OvershootInterpolator());
	 * animation.setDuration(durationMillis);
	 * animation.setStartOffset(delayMillis); animation.setAnimationListener(new
	 * Animation.AnimationListener() {
	 * 
	 * @Override public void onAnimationStart(Animation animation) { }
	 * 
	 * @Override public void onAnimationRepeat(Animation animation) { }
	 * 
	 * @Override public void onAnimationEnd(Animation animation) { int left =
	 * view.getLeft()+(int)(p2-p1); int top = view.getTop(); int width =
	 * view.getWidth(); int height = view.getHeight(); view.clearAnimation();
	 * view.layout(left, top, left+width, top+height); } });
	 * view.startAnimation(animation); }
	 */
	public static String formatPercent(long y, long z) {

		String baifenbi = "";// 接受百分比的值
		double baiy = y * 1.0;
		double baiz = z * 1.0;
		double fen = baiy / baiz;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);
		// DecimalFormat df1 = new DecimalFormat("##.00%");
		// baifenbi= df1.format(fen);
		baifenbi = nf.format(fen);
		return baifenbi;
	}

	public String getUpgradeLog() {
		return upgradeLog;
	}

	public String getUpgradeUrl() {
		return Constant.UPGRADE_APK_URL + upgradeUrl;
	}

	// 读取当前用户名
	public static String getUserName() {

		String s = Build.FIRMWARE+"_"+lk_device.getDeviceId();
		return s;
	}
	public static String getUserID() {

		String s = lk_device.getDeviceId();
		return s;
	}
	/*
	 * public static String getUserName(){ String s=lk_device.getDeviceId();
	 * if(null ==s){ return "0"; }else{ s=s.substring(4); } //byte[]
	 * ba=s.getBytes(); ByteBuffer.wrap(ba).getLong() return s; }
	 */
	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	public static boolean checkDownloadFileSuccessed(String FileSha, File file) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			FileInputStream fis = new FileInputStream(file.getAbsoluteFile());
			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				hexString.append(Integer.toString((mdbytes[i] & 0xff) + 0x100,
						16).substring(1));
			}
			// compare with sha hash code
			String md5FromFile = hexString.toString();

			Logger.e(TAG, "~~~~~~~~~~~~~~ md5校验, FileSha="+FileSha+",真实md5="+md5FromFile);
			
			if (FileSha != null && FileSha.equalsIgnoreCase(md5FromFile)) {
				return true;
			} else {
				// isSucceed = false;
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public static Bitmap decodeBitmapFromFile(String absolutePath,
			int reqWidth, int reqHeight) {
		Bitmap bm = null;

		try {
			File f=new File(absolutePath);
			if(!f.exists()){
				return bm;
			}
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(absolutePath, options);
			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(absolutePath, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bm;
	}

	private static int calculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	// 控制toast弹出

	public static Toast mToast;

	public static void showToast(Context context, String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
		} else {
			mToast.setText(msg);
		}
		mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);// 显示位置
		mToast.show();
	}

	public static void showToast(Context context, int resId) {
		String msg = context.getString(resId);
		showToast(context, msg);
	}
	public static String[] getPreApk(){
		String[] result=new String[]{};
		File f=new File(preInstallUrl+"lkpreapk.txt");
		try {
			FileInputStream fis=new FileInputStream(f);
			BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
			StringBuilder builder=new StringBuilder();
			String line=null;
			while((line=reader.readLine())!=null){
				builder.append(line);
			}
			reader.close();
			return builder.toString().split("#");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static String getIpfromRealm(String realm){
		String result="";
	        try {
	        	InetAddress address = java.net.InetAddress.getByName(realm);
	        	result = address.getHostAddress();//得到字符串形式的ip地址
	        	URL url=new URL(Constant.DEFAULT_WEATHER_URL);
	        	String host=url.getHost();
	        	int port=url.getPort();
	        	Logger.e("tag", "-----getIpfromRealm---host---"+host+"---port---"+port);
	        } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	        } 
	        return result;
	}
	
	public static String readData(String key) {  
		getDebugFileDir();
	    Properties props = new Properties();   
	    try {   
	    	File debugFile=new File(PROPERTY_FILE);
	    	if(! debugFile.exists()){
	    		return "";
	    	}
	        InputStream in = new BufferedInputStream(new FileInputStream(   
	                PROPERTY_FILE));   
	        props.load(in);   
	        in.close();   
	        String value = props.getProperty(key);   
	        return value;   
	    } catch (Exception e) {   
	        e.printStackTrace();   
	        return null;   
	    }   
	}
	public static void startUserLancher(Context context,String packagename){
		
		Log.e(TAG, "=======5=======startUserLancher(), pkg="+packagename);
		if (LKHomeUtil.isInstalled(packagename)) {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(packagename);
            if (intent != null) {
                context.startActivity(intent);
            }
        }
	}
	public static boolean isFactoryMode(){
		//[ro.product.manufacturer]: [rockchip]
		
		//rk: [ro.hardware]: [rk30board],/mnt/external_sd
		//a20:[ro.hardware]: [sun7i],/mnt/extsd
		
	   //a20
		//[ro.product.brand]: [softwinners]
		//[ro.product.manufacturer]: [unknown]
		String str=readData("debug");
		if(str!=null && str.equals("1")){
			return true;
		}
		return false;
		
	}

	public static String formatDate(Date date,String pattern) {
		SimpleDateFormat sf=new SimpleDateFormat(pattern);
		return sf.format(date);
		
	}
	
}
