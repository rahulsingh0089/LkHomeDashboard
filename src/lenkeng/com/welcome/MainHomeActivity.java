package lenkeng.com.welcome;

import java.io.File;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.util.Log;
import lenkeng.com.welcome.adapter.AppQueryAdapter;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.bean.WeatherInfo;
import lenkeng.com.welcome.bean.XmppMessage;
import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.db.ClientActionDao;
import lenkeng.com.welcome.server.IService;
import lenkeng.com.welcome.server.LKService;
import lenkeng.com.welcome.util.AnimationFactory;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeCache;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.util.SPUtil;
import lenkeng.com.welcome.util.SoundUtil;
import lenkeng.com.welcome.util.XmppDbUtil;
import lenkeng.com.welcome.view.LKToast;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenkeng.bean.URLs;
import com.lenkeng.logic.Logic;
import com.lenkeng.tools.ThreadPoolUtil;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainHomeActivity extends Activity implements OnClickListener {

	public static final String ACTION_MSG = "com.lenkeng.wifiman.handler";

	
	private String TAG = "MainHomeActivity";
	private int[] REC_CN = new int[] { R.drawable.re01, R.drawable.re02,
			R.drawable.re03, R.drawable.re04, R.drawable.re05, R.drawable.re06 };
	private int[] REC_EN = new int[] { R.drawable.rec01, R.drawable.rec02,
			R.drawable.rec03, R.drawable.rec04, R.drawable.rec05, R.drawable.rec06 };
	
	private static final int HOME_REC_CLICKED = 0;
	public static final int APP_ITEM_CLICKED = 1;
	private static boolean IS_MOUSE_CLICK = false;
	private static int REC_INDEX = 1;
	public static final int WIFI_AP_STATE_DISABLED = 11;
	public static final int WIFI_AP_STATE_ENABLED = 13;
	public static final int WIFI_AP_STATE_FAILED = 14;
	
	//private static boolean IS_EXIT = false;
	private static boolean IS_MSG = false;
	private static boolean IS_NEWINTENT = false;
	private static boolean IS_NEW_MSG = false;
	private static boolean IS_LONGCLICK = false;
	private static boolean IS_APP_FROM_BUTTON = false;
	private static boolean IS_BUTTON_CLICK = false;
	private static boolean MOUSE_UP;
	private static int MOVIE_FRAME_INDEX = 0;
	private static String FACTORY_MODE = "8811101214";
	private Button[] buttons;

	// private LinearLayout ll_app;
	private RelativeLayout ll_app;
	public LinearLayout ll_recommend;
	private TextView tv_date;
	//public PopupWindow popupWindow;
	//public TextView weather_city;
	//private Button bt_big;
	// private DisplayManagerAw mDisplayManagerAw;

	private Button bt_message;
	//public ImageView iv_weather;
	//public TextView tv_weatherdata;
	private TextView msgNum;
	private ImageButton[] imgButton;
	private TextView tv_titleTimer;
	public ImageView iv_wifiFlag;
	private ImageView iv_wifiAp;
	public ImageView iv_move_frame;

	private ViewPagerFactory vpf;
	public static MyPopupFactory mpf;
	public AnimationFactory animationFactory;
	private LayoutInflater inflater;

	//private LinearLayout ll_weatherSetting;
	private RelativeLayout rl_msg;

	private IService iser;
	// public List<AppInfo> allAppInfos;
	//private LKHomeUtil homeUtil;
	//private ClientActionDao caDao;
	List<AppInfo> recInfos = null;
	//private SharedPreferences sp;
	//private WeatherInfo weatherInfo = null;
	//private int index = 0;
	//private String cityName;
	private XmppDbUtil xmppUtil;
	// private int item_index;
	public String style_flag = Constant.CLASSIFY_RECOMMEND;
	public static MainHomeActivity instance;
	private SoundUtil su;
	private Intent service;
	private MyConn conn;
	private WakeLock lock;

	static boolean cycleFlag = true;
	//private Map<View, Integer> BUTTONS_HOVERED = new HashMap<View, Integer>();
	private Map<String,View> STYLE_VIEW = new HashMap<String, View>();

	private ContentResolver cr;
	private BroadcastReceiver mCameraStateReceiver,speedReceiver,clearReceiver;
	private IntentFilter mCameraStateFilter;
	private Context mContext;
	private AppDataDao tDao;
	private AppStoreDao storeDao;
	
	
	
	
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.HANDLER_DOWNLOAD_WEATHER:
				// setWeatherInfo(false);

				//showWeatherData();
				break;
			case Constant.HANDLER_DOWNLOAD_RECOMMEND_APP:
				// Logger.i("gww",
				// "-------------------the recommend download complete----------");
				AppInfo recIndex = (AppInfo) msg.obj;
				initImageButton(recIndex);
				break;
			case Constant.HANADLER_NOTICE_MSG:

				showMsg();
				break;
			case Constant.HANDLER_INSTALL_COMPLETE:

				break;
			default:
				break;
			}
		}

	};

	private void showMsg() {
		int num = XmppDbUtil.getNewMsgList().size();
		// rl_msg.setVisibility(View.VISIBLE);

		if (num == 0) {

			/*
			 * IS_NEW_MSG = false; if (bt_message.isFocused()) {
			 * bt_message.setBackgroundResource(R.drawable.new_4); } else {
			 * bt_message.setBackgroundResource(R.drawable.new_2); }
			 * msgNum.setVisibility(View.INVISIBLE);
			 */

			rl_msg.setVisibility(View.VISIBLE);

			/*
			 * RelativeLayout.LayoutParams params = new
			 * RelativeLayout.LayoutParams(
			 * RelativeLayout.LayoutParams.WRAP_CONTENT,
			 * RelativeLayout.LayoutParams.WRAP_CONTENT);
			 * rl_msg.setLayoutParams(params); params.topMargin = 50;
			 * params.leftMargin=720;
			 */

			msgNum.setVisibility(View.INVISIBLE);

			if (bt_message.isFocused()) {
				IS_NEW_MSG = true;
				bt_message.setBackgroundResource(R.drawable.new_4);
			} else {
				bt_message.setBackgroundResource(R.drawable.new_2);
			}

		} else {
			if (num > 99) {
				num = 99;
			}
			rl_msg.setVisibility(View.VISIBLE);
			if (bt_message.isFocused()) {
				IS_NEW_MSG = true;
				bt_message.setBackgroundResource(R.drawable.new_3);
			} else {
				bt_message.setBackgroundResource(R.drawable.new_1);
			}
			msgNum.setVisibility(View.VISIBLE);
			msgNum.setText(num + "");
		}
	}

	private void showWeatherData() {
		
		String Langue = getString(R.string.langue);
		// if ("EN".equals(Langue)) {
		//weather_city.setVisibility(View.INVISIBLE);
		//tv_weatherdata.setVisibility(View.INVISIBLE);
		//iv_weather.setVisibility(View.INVISIBLE);
		// } else {
		// weather_city.setVisibility(View.VISIBLE);
		// tv_weatherdata.setVisibility(View.VISIBLE);
		// iv_weather.setVisibility(View.VISIBLE);
		// }

		/*
		 * cityName = sp.getString("city", Constant.DEFAULT_CITY);
		 * 
		 * File f = new File(Environment.getExternalStorageDirectory().getPath()
		 * + "/weather/" + cityName); Logger.v("tag", "$$$---" + TAG +
		 * "----fName---" + f.getAbsolutePath()); try { weatherInfo =
		 * LKHomeUtil.jsonWeatherFile(f); index = 0; // index = (int)
		 * LKHomeUtil.dayIndex(weatherInfo.getDate_y()); Logger.v("tag",
		 * "$$$---" + TAG + "---dayIndex---" + index); // if (index >= 0 &&
		 * index <= 4) { int img = weatherInfo.getImage()[index]; if (img == 99)
		 * { img = 9; } weather_city.setText(weatherInfo.getCity()); String
		 * args[] = weatherInfo.getTemp()[index].split("~"); String small =
		 * args[0].substring(0, args[0].lastIndexOf("℃")); String big =
		 * args[1].substring(0, args[1].lastIndexOf("℃")); int i_small =
		 * Integer.parseInt(small); int i_big = Integer.parseInt(big); if (i_big
		 * > i_small) { tv_weatherdata.setText(args[0] + "~" + args[1]); } else
		 * { tv_weatherdata.setText(args[1] + "~" + args[0]); }
		 * 
		 * // fb.display(iv_weather, //
		 * IMG_URL+weatherInfo.getImage()[index]+".gif");
		 * iv_weather.setBackgroundDrawable(getResources().getDrawable(
		 * Constant.DRABLES[img])); // } else { // f.delete(); // } } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_backup_);

		//homeUtil = new LKHomeUtil(this);
		instance = this;
		mContext = this;

		
		long bef=System.currentTimeMillis();

		lock();
		init();
		// 注册后台服务，进行数据操作
		service = new Intent(this, LKService.class);
		startService(service);

		conn = new MyConn();
		bindService(service, conn, Context.BIND_AUTO_CREATE);
		// initViewPager();
		registReceiver();

		checkApkInstallState();

		long aft =System.currentTimeMillis();
		Logger.e("kao", "-----Home onCreate------  "+(aft - bef));
		
		
		//启动定时检查上传状态
		Intent it=new Intent(getApplicationContext(),LKService.class);
		it.putExtra("CMD", LKService.CMD_LOOP_CHECK_UPLOAD_STATE);
		startService(it);
		
		/*
		 * Logger.i("ez2",
		 * "--------------------------------------------------------------------"
		 * ); addBugLog("xgh:#218,摄像头开关，开或关的时候发出提示音,2014-1-4 svn: 29");
		 * addBugLog("xgh:#255,屏幕大小断电不会保存,2014-1-4 svn: 29");
		 * addBugLog("xgh:#227,[应用商店]安装游戏时出现进度条消失,无法继续下载异常,2014-1-6 svn: 31");
		 * addBugLog
		 * ("xgh:#233,[应用商店]搜索中点击一个已显示的应用,进入详情查看,然后提出,应用整排向下移位,2014-1-13 svn: 39"
		 * ); addBugLog("xgh:#260,图标下加入应用名称，方便用户识别,2014-1-13 svn: 40");
		 * Logger.i(
		 * "ez2","#259,1.0.5底层和1.0.5LKHome在第一次烧完机或者recovery后摄像头挡板和推送消息无提示音.#");
		 * Logger.i("ez2","#266 推送信息提示#"); Logger.i("ez2", "# 修改默认推荐图片#");
		 * Logger.i("ez2",
		 * "---------------------------------------------------------------------"
		 * );
		 */

		/*Logger.i("ez2",
				"--------------------------------------------------------------------");
		Logger.i("ez2",
				"--------------------------------------------------------------------");

		addBugLog("xgh:#263,添加预装应用的赞踩 2014-1-16 svn:57");
		addBugLog("xgh:更改切换语言时不更换服务器地址, 根据固件版本选择服务器地址  2014-1-17 svn:58");
		addBugLog("xgh:#271,添加输入法切换功能,2014-1-21 svn:60");
		addBugLog("xgh:#275,同时一键安装2个应用后,下载第3个,点击一键安装无进度出现");
		addBugLog("xgh:#222,视频留言提醒功能");
		addBugLog("xgh:#269,用户首次开机未联网首页展示");
		addBugLog("xgh:#273,在英文下,摄像头挡板提示音还是中文.");
		addBugLog("xgh:#281,应用商店反复切换导航,launcher崩溃,内存溢出,2014-1-21 svn:62");

		addBugLog("xgh:,解决应用商城多个焦点的bug,2014-1-26 svn:66");
		addBugLog("xgh:,#310,解决应用商城多个焦点的bug,2014-1-26 svn:66");
		addBugLog("xgh:,#313/311,应用商城图标错乱,2014-2-7 svn:68");
		addBugLog("xgh:,#312,改善Launcher内存溢出,2014-2-9 svn:71");
		addBugLog("xgh:,修改下载时网络断开,apk一直显示最后的进度,不能操作的bug,2014-2-11 svn:89");
		addBugLog("xgh:,优化应用商城切换类别响应慢的bug,2014-2-14 svn:97");
		addBugLog("xgh:,优化Launcher图片bitmap加载,加入缓存,2014-2-17 svn:98");
		addBugLog("xgh:,修改应用详情,apk下载状态,安装完成没有写入数据库的apk写入数据库,发送广播,2014-2-18 svn:103");

		Logger.i("ez2",
				"---------------------------------------------------------------------");
		Logger.i("ez2",
				"---------------------------------------------------------------------");*/

	}

	private void checkApkInstallState() {
		final String packageName = SPUtil.getInstallRecord(mContext);
		Log.e(TAG, "~~~~~~~~~ Launcher读取的packageName=" + packageName);

		if (packageName != null && !"".equals(packageName)) { // 安装信息未清除

			SPUtil.putInstallRecord(mContext, "");
			ThreadPoolUtil.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean daoResult = tDao.findApp(packageName);
					boolean result2 = LKHomeUtil.isInstalled(packageName);

					if (result2 && !daoResult) {// 数据库中没有,packageManager中有

						String appname = LKHomeUtil.getLabel(packageName);
						int version = LKHomeUtil.getVersion(packageName);
						String flag = storeDao.getStyle(packageName);
						String HDIcon = storeDao.getIconName(packageName);
						if(HDIcon !=null){
							HDIcon=HDIcon.substring(HDIcon.lastIndexOf("/") + 1);
						}
						tDao.addApp(packageName, flag, appname, HDIcon, version);
						
						Intent flushHome = new Intent();
						flushHome.putExtra("installFlag", "install");
						flushHome.setAction(Constant.ACTION_INSTALED);
						flushHome.putExtra("packageName", packageName);
						flushHome.putExtra("apkStyle", flag);
						mContext.sendBroadcast(flushHome);

						Logger.e(TAG, "~~~~~~数据库不存在,PM中存在,补充记录....");
					}
				}
			});
			
		}

	}

	private void registReceiver() {
		
		
		IntentFilter filter = new IntentFilter();
		// wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		// filter.addAction(Constant.ACTION_INSTALED);
		filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
		filter.addAction("com.lenkeng.newdata");
		filter.addAction(Logic.NEED_UPDATE_UPLOAD_BTN);
		registerReceiver(wifiListener, filter);

		IntentFilter screenfilter = new IntentFilter();
		screenfilter.addAction(Intent.ACTION_SCREEN_OFF);
		screenfilter.addAction(Intent.ACTION_SCREEN_ON);
		screenfilter.addAction("hasNewMsg");
		screenfilter.addAction(Intent.ACTION_TIME_TICK);
		screenfilter.addAction(Intent.ACTION_DATE_CHANGED);
		screenfilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		
		speedReceiver =new SpeedActivity().getScreenReceiver();
		clearReceiver=new ClearActivity.ScreenReceiver();
		registerReceiver(speedReceiver , screenfilter);
		registerReceiver(clearReceiver, screenfilter);
		registerReceiver(videoMsg, screenfilter);
		
		/*
		 * IntentFilter local = new IntentFilter();
		 * local.addAction(Intent.ACTION_LOCALE_CHANGED);
		 * registerReceiver(langueReceiver, local);
		 */
		handler.postDelayed(new Runnable() {

			public void run() {
				initCameraStateReceiver();
			}
		}, 10000);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (Constant.NEED_CHECK_VIDEOMSG) {
					Intent i = new Intent();
					i.setAction("checkVideo");
					sendBroadcast(i);
					Logger.d("kao", "----- check video  1 ----");
				}
			}
		}, 15000);
	}
	SimpleDateFormat sdfd = new SimpleDateFormat("MM-dd-yyyy");
	
	SimpleDateFormat sdft = new SimpleDateFormat(" HH:mm");
	private BroadcastReceiver videoMsg = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				PowerManager manager=(PowerManager) getSystemService(Service.POWER_SERVICE);
				Logger.e("gw", "--------isScreen on----"+manager.isScreenOn());
				
				if(manager.isScreenOn()){
					if (Constant.NEED_CHECK_VIDEOMSG  ){
						Intent i = new Intent();
						i.setAction("checkVideo");
						sendBroadcast(i);
						Logger.d("kao", "------ check video 2 -----");
					}
					
					Intent ota=new Intent();
					ota.setAction("ota");
					sendBroadcast(ota);
				}
				
				//buttons[0].performClick();
			} else if ("hasNewMsg".equals(intent.getAction())) {

				//synchronized (intent) {
					su.playVideoMsg();
				//}
				// new SoundUtil(MainHomeActivity.this).playerMessage();
			} else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())
					|| Intent.ACTION_DATE_CHANGED.equals(intent.getAction())
					|| Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
				//Logger.e("kao", "------action----"+intent.getAction());
				if(tv_titleTimer.getVisibility()==View.VISIBLE){
					sdfd.setTimeZone(TimeZone.getDefault());
					sdft.setTimeZone(TimeZone.getDefault());
					tv_titleTimer.setText(sdfd.format(new Date())+"   "+sdft.format(new Date()));
				}else{
					initUpTitle();
				}
			}
		}
	};

	
	
	public boolean isFactoryMode() {

		PackageManager manager = getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo("com.system.factorylock", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (info == null) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * // 获取显示的宽度百分比 private int getDisplayPercent_w() { return
	 * mDisplayManagerAw.getDisplayAreaPercent(0,
	 * Constant.ORITATION_HORIZONTAL); }
	 * 
	 * // 获取显示高度的百分比 private int getDisplayPercent_h() { return
	 * mDisplayManagerAw.getDisplayAreaPercent(0, Constant.ORITATION_VERTICAL);
	 * }
	 */

	private void lock() {
		PowerManager p = (PowerManager) getSystemService(Context.POWER_SERVICE);
		lock = p.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"com.lenkeng.welcome");
		lock.acquire();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// startDemolauncher();
		IS_NEWINTENT = true;
		String s = intent.getStringExtra("lanucher");

		// IS_APP_FROM_BUTTON = true;
		if ("live".equals(s)) {
			// buttons[5].setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.bt_setting));
			// onButtonClick(buttons[5]);

			onClick(buttons[4]);
			buttons[4].requestFocus();
		} else if ("movie".equals(s)) {
			// buttons[1].setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.bt_setting));
			// onButtonClick(buttons[1]);
			onClick(buttons[1]);
			buttons[1].requestFocus();
		} else if ("apps".equals(s)) {
			// buttons[2].setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.bt_setting));
			// onButtonClick(buttons[2]);
			onClick(buttons[2]);
			buttons[2].requestFocus();
		} else if ("game".equals(s)) {
			// buttons[3].setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.bt_setting));
			// onButtonClick(buttons[3]);
			onClick(buttons[3]);
			buttons[3].requestFocus();
		} else if (null == s) {
			onClick(buttons[0]);
			buttons[0].requestFocus();
			Logger.i("tag", getCurrentFocus() + "");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// checkAPK();
		cycleFlag = false;
	}

	private void checkAPK() {
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/appinfo/LKHome.apk");
		if (f.exists()) {
			Intent i = new Intent(this, LKService.class);
			startService(i);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub

		if (hasFocus) {

			/*
			 * IntentFilter avFilter = new IntentFilter();
			 * 
			 * //remove by kf this function move to framework /*IntentFilter
			 * avFilter = new IntentFilter();
			 * avFilter.addAction(Intent.ACTION_HDMISTATUS_CHANGED);
			 * avFilter.addAction(Intent.ACTION_TVDACSTATUS_CHANGED);
			 * registerReceiver(avReceiver, avFilter);
			 */
			Logger.e("gw", "----------- window focus ---");
		}

	}

	@Override
	protected void onResume() {

		super.onResume();
		// startDemolauncher();
		if (LKHomeUtil.isNetConnected()) {
			if (LKHomeUtil.getConnectedStyle() == 1) {
				iv_wifiFlag.setBackgroundResource(R.drawable.signal_1);
			} else {
				iv_wifiFlag.setBackgroundResource(R.drawable.connected);
			}
			// iv_wifiFlag.setBackgroundResource(R.drawable.wifi_conn);
		} else {
			iv_wifiFlag.setBackgroundResource(R.drawable.signal_5);
		}

		// handler.sendEmptyMessage(Constant.HANDLER_DOWNLOAD_RECOMMEND_APP);
		handler.sendEmptyMessage(Constant.HANDLER_DOWNLOAD_WEATHER);
		handler.sendEmptyMessage(Constant.HANADLER_NOTICE_MSG);
		//IS_EXIT = true;

		// if (!IS_MSG && !IS_NEWINTENT) {
		if (!IS_MSG) {
			if (!Constant.CLASSIFY_RECOMMEND.equals(style_flag)) {
				// showFirstItem();

				Logger.e("ez2", "$$$---onresum---");
			} else {
				if (mpf.REC_INDEX == -1) {
					//buttons[0].performClick();
				}
				/*
				 * for (int i = 0; i < imgButton.length; i++) {
				 * imgButton[i].clearFocus(); } if (mpf.REC_INDEX != -1) {
				 * imgButton[mpf.REC_INDEX - 1].requestFocus(); } else { //
				 * onClick(buttons[0]); // IS_APP_FROM_BUTTON=true;
				 * 
				 * }
				 */
			}
		}
	}

	private void checkNetconnect() {
		try {
			Settings.Global.getInt(cr, LKHomeUtil.CURRENT_NET_TYPE);
		} catch (SettingNotFoundException e) { // 没有设置过网络
			e.printStackTrace();
			try {
				ComponentName componetName = new ComponentName(
						"com.lenkeng.wifiman",
						"com.lenkeng.wifiman.ui.NetworkActivity");
				Intent ini = new Intent();
				ini.setComponent(componetName);
				this.startActivity(ini);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		unbindService(conn);
		unregisterReceiver(mCameraStateReceiver);
		unregisterReceiver(wifiListener);
		unregisterReceiver(clearReceiver);
		unregisterReceiver(speedReceiver);
		// unregisterReceiver(langueReceiver);
		unregisterReceiver(videoMsg);
		lock.release();
		mpf.disMiss();
	}

	// init the view and object
	private void init() {
		long bef =System.currentTimeMillis();
		initObject();
		initUpTitle();
		// iv_wifiFlag.setOnClickListener(this);

		// init object

		// initViewPager();
		initRecButton();
		initDownButton();
		long aft=System.currentTimeMillis();
		Logger.e("kao", "----init----   "+(aft - bef) );
		// ++++++++===
		// ActivityManager am = (ActivityManager)
		// getSystemService(Context.ACTIVITY_SERVICE);
		// int memClassBytes = am.getMemoryClass() * 1024 * 1024;
		// int cacheSize1 = memClassBytes / 8;

		/*
		 * final int maxMemory = (int) (Runtime.getRuntime().maxMemory() /
		 * 1024); final int cacheSize = maxMemory / 8;
		 * 
		 * 
		 * Log.e(TAG, "~~~~~~~~~~~cacheSize2="+cacheSize);
		 * 
		 * LKHomeCache.initCache(cacheSize);
		 */
	}

	private void initObject() {
		long bef=System.currentTimeMillis();
		cr = getContentResolver();
		xmppUtil = new XmppDbUtil(this, handler);
		su = new SoundUtil(MainHomeActivity.this);
		inflater = getLayoutInflater();
		// init view
		// caDao = new ClientActionDao(this);
		tDao =  AppDataDao.getInstance(mContext);
		storeDao = AppStoreDao.getInstance(LKHomeApp.getAppContext());
		// allAppInfos = appDao.getPackageName(Constant.CLASSIFY_MOVIE);
		// allAppInfos = appDao.getInstallAppInfo(Constant.CLASSIFY_MOVIE);
		// appGrid.setAdapter(adapter);
		//sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		
		//LKHomeUtil.systemAppClassify();
		vpf = new ViewPagerFactory(this,handler);
		mpf = new MyPopupFactory(this,handler);
		animationFactory = new AnimationFactory(this,vpf,mpf);
		
		long aft=System.currentTimeMillis();
		Logger.e("kao", "----initObject----   "+(aft - bef));
	}

	private void initRecButton() {
		long bef=System.currentTimeMillis();
		ll_app = (RelativeLayout) this.findViewById(R.id.home_backup_ll);
		ll_recommend = (LinearLayout) this
				.findViewById(R.id.home_backup_ll_recommend);
		imgButton = new ImageButton[] {
				(ImageButton) this.findViewById(R.id.Recommend_one),
				(ImageButton) this.findViewById(R.id.Recommend_two),
				(ImageButton) this.findViewById(R.id.Recommend_three),
				(ImageButton) this.findViewById(R.id.Recommend_four),
				(ImageButton) this.findViewById(R.id.Recommend_five),
				(ImageButton) this.findViewById(R.id.Recommend_six) };
		String systemVersion=Build.FIRMWARE;
		for (int i = 0; i < imgButton.length; i++) {
			imgButton[i].setOnClickListener(recOnclick);
			imgButton[i].setOnFocusChangeListener(recoFocusListener);
			imgButton[i].setOnKeyListener(keListener);
			// imgButton[i].setOnHoverListener(rec_onHoverListener);
			if(systemVersion.contains("zh")){
				imgButton[i].setBackgroundResource(REC_CN[i]);
			}else{
				imgButton[i].setBackgroundResource(REC_EN[i]);
			}
		}
		
		// buttons[0].requestFocus();
		// initImageButton();
		rl_msg = (RelativeLayout) this.findViewById(R.id.rl_msg_back);
		rl_msg.setVisibility(View.INVISIBLE);
		long aft=System.currentTimeMillis();
		Logger.e("kao", "------initRecButton---  "+(aft - bef));
	}

	private void initUpTitle() {
		long bef= System.currentTimeMillis();
		String date=sdfd.format(new Date());
		String time=sdft.format(new Date());
		//Typeface tf = Typeface.createFromAsset(getAssets(), "fzzy.ttf");
		tv_titleTimer = (TextView) this.findViewById(R.id.title_timer);
		if(LKHomeUtil.isNetConnected()){
			try {
				Date targetDate=sdfd.parse("03-01-2014");
				Date currentDate=sdfd.parse(date);
				long t=currentDate.getTime();
				long t2=targetDate.getTime();
				if(t<=t2){
					tv_titleTimer.setVisibility(View.INVISIBLE);
				}else{
					tv_titleTimer.setVisibility(View.VISIBLE);
					Logger.d("kao", "------tv_titleTimer.gettext------"+tv_titleTimer.getText());
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			tv_titleTimer.setVisibility(View.INVISIBLE);
		}
		
		sdfd.setTimeZone(TimeZone.getDefault());
		sdft.setTimeZone(TimeZone.getDefault());
		tv_titleTimer.setText(sdfd.format(new Date())+"   "+sdft.format(new Date()));
		
		iv_wifiFlag = (ImageView) this.findViewById(R.id.WifiFlag);
		iv_wifiAp=(ImageView) this.findViewById(R.id.WifiAp);
		long aft=System.currentTimeMillis();
		Logger.e("kao", "----initUpTitle--   "+(aft - bef));
	}

	private void initDownButton() {
		// iv_arrow_right = (ImageView) this.findViewById(R.id.arrow_right);
		// iv_arrow_left = (ImageView) this.findViewById(R.id.arrow_left);
		long bef =System.currentTimeMillis();
		msgNum = (TextView) this.findViewById(R.id.msgNumber);
		buttons = new Button[] {
				(Button) this.findViewById(R.id.home_backup_bt_recommend),
				(Button) this.findViewById(R.id.home_backup_bt_movie),
				(Button) this.findViewById(R.id.home_backup_bt_application),
				(Button) this.findViewById(R.id.home_backup_bt_game),
				(Button) this.findViewById(R.id.home_backup_bt_live),
				(Button) this.findViewById(R.id.home_backup_bt_setting) };
		// R.drawable.bt_setting));
		
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setOnClickListener(this);
			buttons[i].setOnFocusChangeListener(focusChangedListener);
			buttons[i].setOnKeyListener(buttonsKeyListener);
			STYLE_VIEW.put(Constant.CLASSIFIES[i], buttons[i]);
			// buttons[i].setOnHoverListener(buttonHoverListener);
			// BUTTONS_HOVERED.put(buttons[i], Constant.BUTTON_HOVERD[i]);
		}
		
		buttons[0].performClick();
		iv_move_frame = (ImageView) this.findViewById(R.id.move_frame);
		bt_message = (Button) this.findViewById(R.id.home_btMsg);
		bt_message.setOnFocusChangeListener(focusChangedListener);
		bt_message.setOnKeyListener(keListener);
		long aft =System.currentTimeMillis();
		Logger.e("kao", "-----initDownButton---   "+(aft - bef));
	}

	private void initViewPager() {
		
		
		long bef=System.currentTimeMillis();
		vpf.setListener(keListener, appClickedListener, appSelectedListener,
				chanageListener, focusChangedListener, longClickListener,
				onScrollListener);
		vpf.createMoviePager(iser.getPackageName(Constant.CLASSIFY_MOVIE));
		vpf.createAppPager(iser.getPackageName(Constant.CLASSIFY_APPLICATION));
		vpf.createGamePager(iser.getPackageName(Constant.CLASSIFY_GAME));
		vpf.createUserPager(iser.getPackageName(Constant.CLASSIFY_USER));
		vpf.createSettingPager(iser.getPackageName(Constant.CLASSIFY_SETTING));
		long aft = System.currentTimeMillis();
		Logger.e("kao", "------initViewPager-----   "+(aft - bef));
		
	}

	/*
	 * private int getItemIndex() { int itemIndex =
	 * vpf.getCurrentViewPager(style_flag).getCurrentItem(); return itemIndex *
	 * 10; }
	 */

	int times = 0;
	int times2 = 0;

	@SuppressWarnings("deprecation")
	private void initImageButton(AppInfo info) {
		try {
			/*
			 * imgButton[i - 1].setImageBitmap(LKHomeUtil
			 * .getLoacalBitmap("/sdcard/appinfo/recommend" + i));
			 */
			
			/*recInfos = iser.getAppInfos();
			Logger.e("gww2", "------recInfos.size----"+recInfos.size()+"  recIndex  =  "
					+ recIndex);
			AppInfo info = recInfos.get(recIndex - 1);*/
			//Logger.e("gww2", "    info  "+info);
			
			String recPath;
			String localPath;
			recPath = info.getRecommImage();
			localPath = "/sdcard/appinfo/"
					+ recPath.substring(recPath.lastIndexOf("/") + 1,
							recPath.lastIndexOf(".")) + info.getRecomm_index();
			Logger.e("gww2", "------initImageButton----localPath-----"
					+ localPath);
			BitmapDrawable bd;
			Drawable d = LKHomeUtil.getLocalDrawableRec(localPath);
			Logger.e("gww2", "------initImageButton----d-----" + d);
			if (d != null) {
				imgButton[info.getRecomm_index() - 1].setBackground(d);
				Logger.e("gww2", "------initImageButton----info.package-----"
						+ info.getPackage_name());
			}
			imgButton[info.getRecomm_index() - 1].setTag(info);
			/*
			 * if (recIndex == 1) { bd = new
			 * BitmapDrawable(LKHomeUtil.decodeBitmapFromFile(localPath,
			 * 400,400)); } else if (recIndex == 3) { bd = new
			 * BitmapDrawable(LKHomeUtil.decodeBitmapFromFile(localPath,
			 * 400,200)); } else { bd = new
			 * BitmapDrawable(LKHomeUtil.decodeBitmapFromFile(localPath,
			 * 210,200)); } if (bd != null) { imgButton[recIndex -
			 * 1].setBackground(bd); imgButton[recIndex - 1].setTag(info); }
			 */

			/*
			 * try { int index = 1; String recPath; String localPath; for
			 * (AppInfo info : recInfos) { index = info.getRecomm_index();
			 * recPath = info.getRecommImage(); localPath = "/sdcard/appinfo/" +
			 * recPath.substring(recPath.lastIndexOf("/") + 1,
			 * recPath.lastIndexOf(".")) + index; BitmapDrawable bd; if (index
			 * == 1) { bd = new
			 * BitmapDrawable(LKHomeUtil.decodeBitmapFromFile(localPath,
			 * 400,400)); } else if (index == 3) { bd = new
			 * BitmapDrawable(LKHomeUtil.decodeBitmapFromFile(localPath,
			 * 400,200)); } else { bd = new
			 * BitmapDrawable(LKHomeUtil.decodeBitmapFromFile(localPath,
			 * 210,200)); } times2++; Logger.e("ez2",
			 * "$$$---initImageButton---end----times2---"+times2); if (bd !=
			 * null) { imgButton[index - 1].setBackground(bd); imgButton[index -
			 * 1].setTag(info); } else { imgButton[index - 1]
			 * .setBackgroundResource(DEFAULT_ICONS[index - 1]); imgButton[index
			 * - 1].setTag(null); } } } catch (Exception e) { imgButton[index -
			 * 1] .setBackgroundResource(DEFAULT_ICONS[index - 1]);
			 * imgButton[index - 1].setTag(null); e.printStackTrace(); }
			 */
			/*
			 * for (AppInfo info : recInfos) { try { int index =
			 * info.getRecomm_index(); String rec = info.getRecommImage();
			 * String path = "/sdcard/appinfo/" +
			 * rec.substring(rec.lastIndexOf("/") + 1, rec.lastIndexOf(".")) +
			 * info.getRecomm_index(); imgButton[index - 1].setBackground(new
			 * BitmapDrawable( LKHomeUtil.getLoacalBitmap(path)));
			 * imgButton[index - 1].setTag(info); } catch (Exception e) { //
			 * imgButton[info.getRecomm_index() - 1]
			 * .setBackgroundResource(R.drawable.default1);
			 * imgButton[info.getRecomm_index() - 1].setTag(null); }
			 * recButtonMapApp.put(imgButton[index], info); }
			 */

			// imgButton[5].setBackgroundResource(R.drawable.wuxianlianjie);

			/*
			 * switch (info.getRecomm_index()) { case 1: try {
			 * 
			 * String rec = info.getRecommImage(); String path =
			 * "/sdcard/appinfo/" + rec.substring(rec.lastIndexOf("/") + 1,
			 * rec.lastIndexOf("."))+ rec; imgButton[0].setBackground(new
			 * BitmapDrawable(LKHomeUtil .getLoacalBitmap(path))); } catch
			 * (Exception e) {
			 * imgButton[0].setBackgroundResource(R.drawable.default1); } break;
			 * case 2: try { String rec = info.getRecommImage(); String path =
			 * "/sdcard/appinfo/" + rec.substring(rec.lastIndexOf("/") + 1,
			 * rec.lastIndexOf("."))+ rec; imgButton[1].setBackground(new
			 * BitmapDrawable(LKHomeUtil .getLoacalBitmap(path))); } catch
			 * (Exception e) {
			 * imgButton[1].setBackgroundResource(R.drawable.default3); } break;
			 * case 3: try { String rec = info.getRecommImage(); String path =
			 * "/sdcard/appinfo/" + rec.substring(rec.lastIndexOf("/") + 1,
			 * rec.lastIndexOf("."))+ rec; imgButton[2].setBackground(new
			 * BitmapDrawable(LKHomeUtil .getLoacalBitmap(path))); } catch
			 * (Exception e) {
			 * imgButton[2].setBackgroundResource(R.drawable.default2); } break;
			 * case 4: try { String rec = info.getRecommImage(); String path =
			 * "/sdcard/appinfo/" + rec.substring(rec.lastIndexOf("/") + 1,
			 * rec.lastIndexOf("."))+ rec; imgButton[3].setBackground(new
			 * BitmapDrawable(LKHomeUtil .getLoacalBitmap(path))); } catch
			 * (Exception e) {
			 * imgButton[3].setBackgroundResource(R.drawable.default3); } break;
			 * case 5: try { String rec = info.getRecommImage(); String path =
			 * "/sdcard/appinfo/" + rec.substring(rec.lastIndexOf("/") + 1,
			 * rec.lastIndexOf("."))+ rec; imgButton[4].setBackground(new
			 * BitmapDrawable(LKHomeUtil .getLoacalBitmap(path))); } catch
			 * (Exception e) {
			 * imgButton[4].setBackgroundResource(R.drawable.default3); } break;
			 * case 6: try {
			 * imgButton[5].setBackgroundResource(R.drawable.wuxianlianjie); }
			 * catch (Exception e) {
			 * imgButton[5].setBackgroundResource(R.drawable.default3); } break;
			 * 
			 * default: break; }
			 */

		} catch (Exception e2) {
			e2.printStackTrace();
		}

		/*
		 * if (recInfos != null && recInfos.size() != 0) {
		 * 
		 * for (int i = 0; i < recInfos.size(); i++) { int recomIndex =
		 * recInfos.get(i).getRecomm_index(); String rec =
		 * recInfos.get(i).getRecommImage(); String path = "/sdcard/appinfo/" +
		 * rec.substring(rec.lastIndexOf("/") + 1, rec.lastIndexOf(".")) +
		 * recInfos.get(i).getRecomm_index(); try {
		 * imgButton[i].setBackgroundDrawable(); } catch (Exception e) {
		 * Auto-generated catch block File f=new File(path); f.delete();
		 * e.printStackTrace(); imgButton[i].setBackground(getResources()
		 * .getDrawable(DEFAULT_ICONS[i])); } imgButton[5]
		 * .setBackgroundResource(R.drawable.wuxianlianjie); } } else { for (int
		 * i = 0; i < imgButton.length; i++) {
		 * imgButton[i].setBackgroundDrawable(getResources()
		 * .getDrawable(DEFAULT_ICONS[i])); } // imgButton[5] //
		 * .setBackgroundResource(R.drawable.wuxianlianjie); }
		 */
	}

	public void gotoMessage(View v) {
		Intent intent = new Intent(this, MessageActivity.class);
		startActivity(intent);
	}

	private OnFocusChangeListener recoFocusListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			//Logger.e("kao", "$$$$-------recoFocusListener----"
			//		+ getCurrentFocus());
			AppInfo info = (AppInfo) v.getTag();

			int index = 0;
			if (hasFocus) {
				
				switch (v.getId()) {
				case R.id.Recommend_one:
					index = 1;
					break;
				case R.id.Recommend_two:
					index = 2;
					break;
				case R.id.Recommend_three:
					index = 3;
					break;
				case R.id.Recommend_four:
					index = 4;
					break;
				case R.id.Recommend_five:
					index = 5;
					break;
				case R.id.Recommend_six:
					index = 6;
					break;
				default:
					break;
				}
				
				mpf.showPopupWindow(v, info, Constant.CLASSIFY_RECOMMEND, index);

				/*
				 * switch (v.getId()) { case R.id.Recommend_one: if (hasFocus &&
				 * info != null) { try { mpf.showPopupWindow(v, info,
				 * Constant.CLASSIFY_RECOMMEND, 1); // mpf.setPopupParams(v,
				 * recInfos.get(0), // Constant.CLASSIFY_RECOMMEND, 1); } catch
				 * (Exception e) { mpf.showPopupWindow(v, null,
				 * Constant.CLASSIFY_RECOMMEND, 1); // mpf.setPopupParams(v, //
				 * null,Constant.CLASSIFY_RECOMMEND, 1); e.printStackTrace(); }
				 * } break; case R.id.Recommend_two: if (hasFocus && info !=
				 * null) { try { mpf.showPopupWindow(v, info,
				 * Constant.CLASSIFY_RECOMMEND, 2); // mpf.setPopupParams(v, //
				 * recInfos.get(1),Constant.CLASSIFY_RECOMMEND, 2); } catch
				 * (Exception e) { mpf.showPopupWindow(v, null,
				 * Constant.CLASSIFY_RECOMMEND, 2); // mpf.setPopupParams(v, //
				 * null,Constant.CLASSIFY_RECOMMEND, 2); e.printStackTrace(); }
				 * } break; case R.id.Recommend_three: if (hasFocus && info !=
				 * null) { try { mpf.showPopupWindow(v, info,
				 * Constant.CLASSIFY_RECOMMEND, 3); // mpf.setPopupParams(v, //
				 * recInfos.get(2),Constant.CLASSIFY_RECOMMEND, 3); } catch
				 * (Exception e) { mpf.showPopupWindow(v, null,
				 * Constant.CLASSIFY_RECOMMEND, 3); // mpf.setPopupParams(v, //
				 * null,Constant.CLASSIFY_RECOMMEND, 3); e.printStackTrace(); }
				 * } break; case R.id.Recommend_four: if (hasFocus && info !=
				 * null) { try { mpf.showPopupWindow(v, info,
				 * Constant.CLASSIFY_RECOMMEND, 4); // mpf.setPopupParams(v, //
				 * recInfos.get(3),Constant.CLASSIFY_RECOMMEND, 4); } catch
				 * (Exception e) { mpf.showPopupWindow(v, null,
				 * Constant.CLASSIFY_RECOMMEND, 4); // mpf.setPopupParams(v, //
				 * null,Constant.CLASSIFY_RECOMMEND, 4); e.printStackTrace(); }
				 * } break; case R.id.Recommend_five: if (hasFocus && info !=
				 * null) { try { mpf.showPopupWindow(v, info,
				 * Constant.CLASSIFY_RECOMMEND, 5); // mpf.setPopupParams(v, //
				 * recInfos.get(4),Constant.CLASSIFY_RECOMMEND, 5); } catch
				 * (Exception e) { mpf.showPopupWindow(v, null,
				 * Constant.CLASSIFY_RECOMMEND, 5); // mpf.setPopupParams(v, //
				 * null,Constant.CLASSIFY_RECOMMEND, 5); e.printStackTrace(); }
				 * } break; case R.id.Recommend_six: if (hasFocus && info !=
				 * null) { try { mpf.showPopupWindow(v, info,
				 * Constant.CLASSIFY_RECOMMEND, 6); // mpf.setPopupParams(v, //
				 * recInfos.get(5),Constant.CLASSIFY_RECOMMEND, 6); } catch
				 * (Exception e) { mpf.showPopupWindow(v, null,
				 * Constant.CLASSIFY_RECOMMEND, 6); // mpf.setPopupParams(v, //
				 * null,Constant.CLASSIFY_RECOMMEND, 6); e.printStackTrace(); }
				 * } break; default: break; }
				 */
			}
		}
	};
	private OnFocusChangeListener focusChangedListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean arg1) {
			/*
			 * if (popupWindow != null) { popupWindow.dismiss(); popupWindow =
			 * null; }
			 */
			//Logger.e("kao", "$$$---onFocusChange---" + v);
			iv_move_frame.setVisibility(View.INVISIBLE);
			if (mpf != null) {
				mpf.disMiss();
			}
			/*
			 * if (IS_MOUSE_CLICK) { IS_MOUSE_CLICK = false; return; }
			 */
			switch (v.getId()) {
			case R.id.home_backup_bt_recommend:
			case R.id.home_backup_bt_live:
			case R.id.home_backup_bt_movie:
			case R.id.home_backup_bt_application:
			case R.id.home_backup_bt_game:
			case R.id.home_backup_bt_setting:
				IS_MSG = false;
				if (v.isFocused()) {
					if (IS_APP_FROM_BUTTON) {
						onClick(v);
						IS_APP_FROM_BUTTON = false;
					} else {
						onButtonClick(v);
					}
				} else {

				}
				break;
			case R.id.home_btMsg:
				/*
				 * if (v.isFocused()) { // focusMutual(v); IS_MSG = true;
				 * v.setBackgroundResource(R.drawable.new_3); } else {
				 * v.setBackgroundResource(R.drawable.new_1); }
				 */
				if (v.isFocused()) {
					IS_MSG = true;
				}
				showMsg();
				break;
			case R.id.home_app_list:
				if (v.isFocused()) {
					vpf.setCurrentFistShow(style_flag, false);
					if (MOUSE_UP) {
						MOUSE_UP = false;
						showFirstItem();
						iv_move_frame.clearAnimation();
					}
				} else {
					mpf.clearAnim();
				}
				break;
			default:
				break;
			}
		}
	};

	/*
	 * int lastPosition; boolean clicked = false; int state = 0; int
	 * startPositon; int endPositon; int rowHeight; int rows;
	 */

	int tempPageNum = 0;
	int currentPage = 0;
	int firstVisible = 0;
	int SCROLL_STATE;
	boolean DRAG_OR_ROLL;

	private OnScrollListener onScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			mpf.disMiss();

			// gww
			SCROLL_STATE = scrollState;
			view.setSelection(firstVisible);
			/*
			 * // ====add by xgh===== state = scrollState; if (scrollState == 1)
			 * { startPositon = 0; clicked = true; } else if (scrollState == 0)
			 * {
			 * 
			 * clicked = false; int scrollOrientation = endPositon -
			 * startPositon; int scrollPostion = Math.abs(endPositon); rows =
			 * scrollPostion / rowHeight;// 滚动的总行数 int modCount =
			 * Math.abs(endPositon % rowHeight);// 余数 if (scrollOrientation > 0)
			 * {// 向下 if (modCount >= rowHeight / 2) {// 余数超过半行 rows += 1; } }
			 * else {// 向上 if (modCount >= rowHeight / 2) { // 余数超过半行 rows += 1;
			 * } } view.setSelection(rows * 5);
			 * 
			 * } iii=scrollState; // =========end============= Logger.v("tag",
			 * "$$$---"
			 * +TAG+"---onScrollStateChanged---scrollState---"+scrollState);
			 */
			//Logger.e("kao", "$$$--onScrollStateChanged---" + scrollState);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			mpf.disMiss();
			firstVisible = firstVisibleItem;

			// gww
			int index = tempPageNum - firstVisibleItem;

			if (SCROLL_STATE == SCROLL_STATE_IDLE) {
				if (visibleItemCount > 10) {
					if (index <= 0) {
						view.setSelection(firstVisibleItem + 5);
					} else {
						view.setSelection(firstVisibleItem);
					}
				}
				// Logger.v("tag",
				// "$$$---"+TAG+"---SCROLL_STATE_IDLE---"+(firstVisibleItem+5));
			}
			// if(SCROLL_STATE == 0){
			currentPage = (firstVisibleItem / 10);
			// if (tempPageNum != 0) {
			if (index <= 0) {
				// if(firstVisibleItem %10 ==0){
				vpf.startScrollNode(style_flag, currentPage, 0);
				// }
				/*
				 * if (currentPage >= 1 && firstVisibleItem % 10 != 5) {
				 * 
				 * }
				 */
			} else {
				// if(firstVisibleItem%10 == 0){
				vpf.startScrollNode(style_flag, currentPage + 1, 1);
				// }
				/*
				 * if (currentPage > 0 && firstVisibleItem % 10 != 5) {
				 * 
				 * }
				 */
			}
			// }

			tempPageNum = firstVisibleItem;
			// /}

			
			/*
			 * // ======add by xgh=======
			 * 
			 * int fistTop = 0; View v = ((GridView) view).getChildAt(0); if (v
			 * != null) { fistTop = v.getTop() - v.getHeight() *
			 * (firstVisibleItem / 5); rowHeight = v.getHeight(); // Log.e(TAG,
			 * // "===line 905 : first="+firstVisibleItem+",visiblecount="+
			 * visibleItemCount+",total="+totalItemCount+",top="+fistTop); }
			 * 
			 * if (!clicked) {// 鼠标滚轮的滚动 int scrollCount = fistTop -
			 * lastPosition; // Log.e(TAG, "====line 942 滚动数量:"+scrollCount); if
			 * (scrollCount > 60 && scrollCount < 200) { int p =
			 * firstVisibleItem; // Log.e(TAG, "===line 944 需要滑动到: "+p);
			 * view.setSelection(p); } else if (scrollCount < -60) {
			 * view.setSelection(firstVisibleItem + 5); } lastPosition =
			 * fistTop;
			 * 
			 * } else {// 鼠标左键点击的拖动 if (state == 1 && startPositon == 0) { // 按下
			 * startPositon = fistTop;
			 * 
			 * } endPositon = fistTop; }
			 */
			// ====end===

		}
	};

	private OnItemClickListener appClickedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			IS_MOUSE_CLICK = true;
			// vpf.getCurrentAdapter(style_flag).getPrimaryItem().requestFocus();
			mpf.clearAnim();
			AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
			mpf.openUserApp(style_flag, appInfo);

		}

	};
	private OnLongClickListener longClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			IS_LONGCLICK = true;
			return false;
		}
	};
	private OnItemSelectedListener appSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> view, View v, int position,
				long arg3) {
			Logger.e("ez2", "$$$---onItemSelected---" + v);
			IS_MOUSE_CLICK = false;
			if (!vpf.currentPageIsFistShow(style_flag)) {
				showFirstItem();
			}
			/*
			 * if (IS_BUTTON_CLICK) { IS_BUTTON_CLICK = false;
			 * iv_move_frame.clearAnimation(); }
			 */
			// ImageView iv=(ImageView) v.findViewById(R.id.home_app_ico);

			/*
			 * iv.setBackgroundResource(R.drawable.frame); Animation
			 * a=AnimationUtils.loadAnimation(MainHomeActivity.this,
			 * R.anim.item); a.setAnimationListener(animationListener);
			 * iv.setAnimation(a);
			 */
			/*
			 * Logger.i("gww3", "------selected---item----"); GridView
			 * g=(GridView)
			 * vpf.getCurrentAdapter(style_flag).getPrimaryItem().findViewById
			 * (R.id.home_app_list); AppQueryAdapter a=(AppQueryAdapter)
			 * g.getAdapter(); a.setSelect(position);
			 * 
			 * //int temp = vpf.getCurrentViewPager(style_flag).getChildCount();
			 * if (itemMouseClick) { //temp--; //if (temp == 0) {
			 * //itemMouseClick = false; //} return; } if
			 * (!Constant.CLASSIFY_RECOMMEND.equals(style_flag) &&
			 * !vpf.currentPageIsFistShow(style_flag)) { if (popupWindow !=
			 * null) { popupWindow.dismiss(); popupWindow = null; } showPopUp(v,
			 * position, APP_ITEM_CLICKED); } selected_position = position;
			 */
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};

	public OnPageChangeListener chanageListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {

			// pageAdapter.notifyDataSetChanged();
			// selected_position = 0;
			// judgeStyle();
			// showFirstItem();
			//vpf.setCurrentIndex(style_flag, arg0);
			//vpf.showArrow(style_flag);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private boolean simulateKeyEvent(int action, int keyCode) {
		KeyEvent event = new KeyEvent(action, keyCode);
		if (action == KeyEvent.ACTION_DOWN) {
			return onKeyDown(event.getKeyCode(), event);
		} else {
			return false;
		}

	}

	protected void showFirstItem() {
		if (Constant.CLASSIFY_RECOMMEND.equals(style_flag)) {
			return;
		}
		GridView g = vpf.getCurrentAdapter(style_flag).getPrimaryItem();
		if (g != null) {
			View v = g.getSelectedView();
			if (null != v) {
				v.requestFocus();
				AppInfo info = (AppInfo) g.getSelectedItem();
				// mpf.showPopupWindow(v, info, style_flag,
				// g.getSelectedItemPosition());
				mpf.setPopupParams(v, info, style_flag,
						g.getSelectedItemPosition());
				// showPopUp(v, g.getSelectedItemPosition(), APP_ITEM_CLICKED);
				// appSelectedListener.onItemSelected(null, v,
				// g.getSelectedItemPosition(), g.getSelectedItemId());
			}
		}
	}

	

	public String getCurrentStyleFlag() {
		return style_flag;
	}

	private OnHoverListener buttonHoverListener = new OnHoverListener() {

		@Override
		public boolean onHover(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (animationFactory.getCurrentUnderline() == null) {
				animationFactory.setCurrentUnderLine((Button) v);
			}
			if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
				// animationFactory.startUnderLineAnition(
				// animationFactory.getCurrentUnderline(),v, v);
			} else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
			}
			return false;
		}
	};
	private OnHoverListener rec_onHoverListener = new OnHoverListener() {
		@Override
		public boolean onHover(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
				recoFocusListener.onFocusChange(v, true);
				animationFactory.clearUnderLineAnimation();
			} else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
				// mpf.disMiss();
			}
			return false;
		}
	};
	private OnClickListener recOnclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			mpf.setAppInfo((AppInfo) v.getTag());
			mpf.onClick(v);
			mpf.REC_INDEX = 0;
			// homeRecClicked(v, iser.getAppInfos());
			// mpf.onClick(v);
			/*
			 * switch (v.getId()) { case R.id.Recommend_one: try {
			 * mpf.setAppInfo(recInfos.get(0)); mpf.onClick(v); } catch
			 * (Exception e) { e.printStackTrace(); } break; case
			 * R.id.Recommend_two: try { mpf.setAppInfo(recInfos.get(1));
			 * mpf.onClick(v); } catch (Exception e) { e.printStackTrace(); }
			 * break; case R.id.Recommend_three: try {
			 * mpf.setAppInfo(recInfos.get(2)); mpf.onClick(v); } catch
			 * (Exception e) { e.printStackTrace(); } break; case
			 * R.id.Recommend_four: try { mpf.setAppInfo(recInfos.get(3));
			 * mpf.onClick(v); } catch (Exception e) { e.printStackTrace(); }
			 * break; case R.id.Recommend_five: try {
			 * mpf.setAppInfo(recInfos.get(4)); mpf.onClick(v); } catch
			 * (Exception e) { e.printStackTrace(); } break; case
			 * R.id.Recommend_six: try { AppInfo appinfo = new AppInfo();
			 * appinfo.setRecomm_index(6); mpf.setAppInfo(appinfo);
			 * mpf.onClick(v); } catch (Exception e) { default: break; }
			 */
		}
	};

	private void buttonEvent(View v) {
		if (v.isFocused()) {
		} else if (v.isPressed()) {
		}
	}

	@Override
	public void onClick(View v) {
		IS_BUTTON_CLICK = true;
		onButtonClick(v);
		// startAnition(v, v, iv_move_frame, v);
		animationFactory.startUnderLineAnition(v, v, v);
		animationFactory.startScaleAnimation(v,
				animationFactory.getScaleBigButton());
		// startScaleAnimation(v, bt_big);
		/*
		 * if(!IS_APP_FROM_BUTTON ){ if(bt_big !=null){ startScaleAnimation(v,
		 * bt_big); } }else{ mpf.clearAnim(); }
		 */
		vpf.setCurrentFistShow(style_flag, false);
		buttonEvent(v);
	}

	/*
	 * private void focusMutual(View v) { switch (v.getId()) { case
	 * R.id.home_backup_bt_recommend:
	 * buttons[0].setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.recommend_up));
	 * buttons[1].setBackgroundResource(R.drawable.movie_def);
	 * buttons[2].setBackgroundResource(R.drawable.app_def);
	 * buttons[3].setBackgroundResource(R.drawable.game_def);
	 * buttons[4].setBackgroundResource(R.drawable.user_def);
	 * buttons[5].setBackgroundResource(R.drawable.setting_def); break; case
	 * R.id.home_backup_bt_movie:
	 * buttons[1].setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.movie_up));
	 * buttons[0].setBackgroundResource(R.drawable.recommend_def);
	 * buttons[2].setBackgroundResource(R.drawable.app_def);
	 * buttons[3].setBackgroundResource(R.drawable.game_def);
	 * buttons[4].setBackgroundResource(R.drawable.user_def);
	 * buttons[5].setBackgroundResource(R.drawable.setting_def); break; case
	 * R.id.home_backup_bt_application:
	 * buttons[2].setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.app_up));
	 * buttons[1].setBackgroundResource(R.drawable.movie_def);
	 * buttons[0].setBackgroundResource(R.drawable.recommend_def);
	 * buttons[3].setBackgroundResource(R.drawable.game_def);
	 * buttons[4].setBackgroundResource(R.drawable.user_def);
	 * buttons[5].setBackgroundResource(R.drawable.setting_def);
	 * 
	 * break; case R.id.home_backup_bt_game:
	 * buttons[3].setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.game_up));
	 * buttons[1].setBackgroundResource(R.drawable.movie_def);
	 * buttons[2].setBackgroundResource(R.drawable.app_def);
	 * buttons[0].setBackgroundResource(R.drawable.recommend_def);
	 * buttons[4].setBackgroundResource(R.drawable.user_def);
	 * buttons[5].setBackgroundResource(R.drawable.setting_def);
	 * 
	 * break; case R.id.home_backup_bt_live:
	 * buttons[4].setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.user_up));
	 * buttons[1].setBackgroundResource(R.drawable.movie_def);
	 * buttons[2].setBackgroundResource(R.drawable.app_def);
	 * buttons[3].setBackgroundResource(R.drawable.game_def);
	 * buttons[5].setBackgroundResource(R.drawable.setting_def);
	 * buttons[0].setBackgroundResource(R.drawable.recommend_def); break; case
	 * R.id.home_backup_bt_setting:
	 * buttons[5].setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.setting_up));
	 * buttons[1].setBackgroundResource(R.drawable.movie_def);
	 * buttons[2].setBackgroundResource(R.drawable.app_def);
	 * buttons[3].setBackgroundResource(R.drawable.game_def);
	 * buttons[4].setBackgroundResource(R.drawable.user_def);
	 * buttons[0].setBackgroundResource(R.drawable.recommend_def);
	 * 
	 * break; case R.id.home_btMsg:
	 * buttons[1].setBackgroundResource(R.drawable.movie_def);
	 * buttons[2].setBackgroundResource(R.drawable.app_def);
	 * buttons[3].setBackgroundResource(R.drawable.game_def);
	 * buttons[4].setBackgroundResource(R.drawable.user_def);
	 * buttons[0].setBackgroundResource(R.drawable.recommend_def);
	 * buttons[5].setBackgroundResource(R.drawable.setting_def); break; default:
	 * break; } }
	 */

	private void onButtonClick(View v) {
		// focusMutual(v);
		/*if (v == buttons[0]) {
			ll_recommend.setVisibility(View.VISIBLE);
			// ll_recommend.setAnimation(animationFactory.showAppAnimation());
			ll_app.setVisibility(View.GONE);
			// ll_app.setAnimation(animationFactory.hideAppAnimation());
			style_flag = Constant.CLASSIFY_RECOMMEND;
			// iv_move_frame.setVisibility(View.VISIBLE);
		} else {
			// if (v != weather_city && v != bt_message) {
			// ll_app.setAnimation(vpf.showAnimation());
			ll_app.setVisibility(View.VISIBLE);
			ll_recommend.setVisibility(View.GONE);

			// ll_recommend.setAnimation(vpf.hideAnimation());
		}*/
		switch (v.getId()) {
		case R.id.home_backup_bt_recommend:
			style_flag=Constant.CLASSIFY_RECOMMEND;
			vpf.showCurrentPager(style_flag);
			break;

		case R.id.home_backup_bt_movie:
			style_flag=Constant.CLASSIFY_MOVIE;
			vpf.showCurrentPager(Constant.CLASSIFY_MOVIE);
			// startAnition(buttons[1].getX(), buttons[1].getX(),
			// iv_move_frame);
			break;
		case R.id.home_backup_bt_application:
			style_flag=Constant.CLASSIFY_APPLICATION;
			vpf.showCurrentPager(Constant.CLASSIFY_APPLICATION);
			// startAnition(buttons[2].getX(), buttons[2].getX(),
			// iv_move_frame);
			break;
		case R.id.home_backup_bt_game:
			style_flag=Constant.CLASSIFY_GAME;
			vpf.showCurrentPager(Constant.CLASSIFY_GAME);
			// startAnition(buttons[3].getX(), buttons[3].getX(),
			// iv_move_frame);
			break;

		case R.id.home_backup_bt_setting:
			style_flag=Constant.CLASSIFY_SETTING;
			vpf.showCurrentPager(Constant.CLASSIFY_SETTING);
			// startAnition(buttons[5].getX(), buttons[5].getX(),
			// iv_move_frame);
			break;
		case R.id.home_backup_bt_live:
			style_flag=Constant.CLASSIFY_USER;
			vpf.showCurrentPager(Constant.CLASSIFY_USER);
			// startAnition(buttons[4].getX(), buttons[4].getX(),
			// iv_move_frame);
			break;
		default:
			break;
		}
	}

	

	// 获取接口对象，调用后台服务里的方法
	private class MyConn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			long ber=java.lang.System.currentTimeMillis();
			iser = (IService) service;
			// initImageButton();
			recInfos = iser.getAppInfos();
			/*if (recInfos != null) {
				for (int i = 1; i <= recInfos.size(); i++) {
					Message msg = Message.obtain();
					msg.what = Constant.HANDLER_DOWNLOAD_RECOMMEND_APP;
					msg.arg1 = i;
					handler.sendMessage(msg);
				}
			}*/
			for(AppInfo info:recInfos){
				initImageButton(info);
			}
			initViewPager();
			long aft=java.lang.System.currentTimeMillis();
			Logger.e("kao", "$$$-----onServiceConnected----"+(aft-ber));
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	}

	// private void getPageNumber() {
	/*
	 * if (Constant.CLASSIFY_SETTING.equals(style_flag)) { if ((settings.length
	 * % 10) != 0) { pageNumber = (int) (settings.length / 10) + 1; } else {
	 * pageNumber = (int) settings.length / 10; } } else if
	 * (Constant.CLASSIFY_SCAN.equals(style_flag)) { if
	 * ((Constant.SCANURL.length % 10) != 0) { pageNumber = (int)
	 * (Constant.SCANURL.length / 10) + 1; } else { pageNumber = (int)
	 * Constant.SCANURL.length / 10; } } else { if ((allAppInfos.size() % 10) !=
	 * 0) { pageNumber = (int) (allAppInfos.size() / 10) + 1; } else {
	 * pageNumber = (int) allAppInfos.size() / 10; } }
	 */
	// }
	// 注册WIFI广播监听网络状态

	int i = 0;
	private BroadcastReceiver wifiListener = new BroadcastReceiver() {
		int[] wifi_icons = new int[] { R.drawable.signal_4,
				R.drawable.signal_3, R.drawable.signal_2, R.drawable.signal_1 };
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logger.e("kao", "-----wifiListener---  action "+intent.getAction());
			if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)
					|| WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
				i++;
				// if (i == 1) {
				if (LKHomeUtil.isNetConnected()) {
					
					// handler.sendEmptyMessage(Constant.HANDLER_DOWNLOAD_WEATHER);
					if (LKHomeUtil.getConnectedStyle() == 1) {
						iv_wifiFlag.setBackgroundResource(R.drawable.signal_1);
					} else {
						iv_wifiFlag.setBackgroundResource(R.drawable.connected);
					}
				} else {
					iv_wifiFlag.setBackgroundResource(R.drawable.signal_5);

					// if(LKHomeUtil.conn !=null){
					// LKHomeUtil.conn.disconnect();
					// }
				}
				// }
				if (i == 2) {
					i = 0;
				}
			} else if ("com.lenkeng.newdata".equals(action)) {
				long bef = System.currentTimeMillis();
				installed(intent);
				long aft = System.currentTimeMillis();
				Logger.d("ww", "$$$------wifiListener------" + (aft - bef));
			}else if("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)){
				WifiManager wifiManager=(WifiManager) getSystemService(Service.WIFI_SERVICE);
				int apState=wifiManager.getWifiApState();
				if(apState == WIFI_AP_STATE_DISABLED || apState == WIFI_AP_STATE_FAILED){
					Logger.e("kao", "----wifi ap is disabled or start failed----  "+apState);
					iv_wifiAp.setVisibility(View.GONE);
				}else if(apState == WIFI_AP_STATE_ENABLED){
					Logger.e("kao", "---- wifi ap is enabled ----  "+apState);
					iv_wifiAp.setVisibility(View.VISIBLE);
				}
			}else if(Logic.NEED_UPDATE_UPLOAD_BTN.equals(action)){ //需要更新上传按钮的广播
				//List<AppInfo> allAppInfos = iser.getPackageName(Constant.CLASSIFY_USER);
				/*AppQueryAdapter adapter=new AppQueryAdapter(MainHomeActivity.this);
				adapter.setItemsData(allAppInfos, 0);
				adapter.setStyleFlag(Constant.CLASSIFY_USER);
				adapter.notifyDataSetChanged();*/
				//vpf.createViewPager(Constant.CLASSIFY_USER, allAppInfos);
				//vpf.getCurrentViewPager(Constant.CLASSIFY_USER).setCurrentItem(
				//		vpf.getCurrentPageNumber(Constant.CLASSIFY_USER));
				GridView g =vpf.getCurrentAdapter(Constant.CLASSIFY_USER).getPrimaryItem();
				if (g != null) {
					AppQueryAdapter adapter =(AppQueryAdapter) g.getAdapter();
					//adapter.setItemsData(allAppInfos, 0);
					//adapter.setStyleFlag(Constant.CLASSIFY_USER);
					adapter.notifyDataSetChanged();
					/* int select=g.getSelectedItemPosition();
					 g.setSelection(select);
					if (g.getSelectedView() != null) {
						g.getSelectedView().requestFocus();
					}*/
				}
				
				mpf.disMiss();
			}
		}
	};

	private void installed(Intent intent) {

		String apkStyle = intent.getStringExtra("apkStyle");
		List<AppInfo> allAppInfos = iser.getPackageName(apkStyle);
		
		vpf.createViewPager(apkStyle, allAppInfos);
		// vpf.setCurrentIndex(apkStyle, page);
		String installFlag = intent.getStringExtra("installFlag");

		Logger.e("kao", "$$$---" + TAG + "---installed---installFlag---"
				+ installFlag + "---apkStyle---" + apkStyle);
		if ("install".equals(installFlag) && apkStyle.equals(style_flag)) {

			vpf.getCurrentViewPager(apkStyle).setCurrentItem(
					vpf.getCurrentPageNumber(apkStyle));
			GridView g = vpf.getCurrentAdapter(apkStyle).getPrimaryItem();
			if (g != null) {
				if (g.getCount() < 2) {
					g.setSelection(0);
				} else {
					g.setSelection(g.getCount() - 2);
				}
				// selected_position=g.getSelectedItemPosition();
				if (g.getSelectedView() != null) {
					g.getSelectedView().requestFocus();
				}
			}
		} else {
		}

		// vpf.showCurrentPager(apkStyle);
		/*
		 * if(Constant.CLASSIFY_MOVIE.equals(apkStyle)){
		 * buttons[1].requestFocus(); }else
		 * if(Constant.CLASSIFY_APPLICATION.equals(apkStyle)){
		 * buttons[2].requestFocus(); }else
		 * if(Constant.CLASSIFY_GAME.equals(apkStyle)){
		 * buttons[3].requestFocus(); }else
		 * if(Constant.CLASSIFY_USER.equals(apkStyle)){
		 * buttons[5].requestFocus(); }
		 */
	}

	/*
	 * // 热门推荐控件放大时的点击事件 private void homeRecClicked(View v, List<AppInfo> info)
	 * {
	 * 
	 * List<AppInfo> infos = info;
	 * 
	 * if(null ==infos || infos.size()==0){ Intent intent =new
	 * Intent(MainHomeActivity.this,AppStore.class); startActivity(intent); }
	 * 
	 * try { for (int i = 0; i < imgButton.length; i++) { if (v == imgButton[i])
	 * { AppInfo appInfo = infos.get(i); REC_INDEX = i; if (i == 5) {
	 * goSettingIntent("com.lenkeng.hot"); } else { if
	 * (LKHomeUtil.isInstalled(appInfo.getPackage_name())) { // 安装了该程序则打开 Intent
	 * intent = MainHomeActivity.this .getPackageManager()
	 * .getLaunchIntentForPackage( appInfo.getPackage_name()); if (intent !=
	 * null) { MainHomeActivity.this.startActivity(intent); } } else { Intent
	 * intent = new Intent(MainHomeActivity.this, DetailActivity.class);
	 * intent.putExtra("appinfo", appInfo); startActivity(intent); } } } } }
	 * catch (Exception e) { e.printStackTrace(); // Intent intent = new
	 * Intent(MainHomeActivity.this, // DetailActivity.class); //
	 * startActivity(intent); } }
	 */

	/*
	 * private void appItemSwap(int befforePosition, int afterPosition) { String
	 * temp = allAppInfos.get(befforePosition); allAppInfos.set(befforePosition,
	 * allAppInfos.get(afterPosition)); allAppInfos.set(afterPosition, temp); //
	 * appStoreDao.updateItemPosition(befforePosition, afterPosition);
	 * appDao.updateItemPosition(befforePosition, afterPosition); }
	 */

	/************************************************************************************************/
	// 遥控操作时，每个控件被放大

	/*
	 * int[] location = null; View popup_show_view = null;
	 * android.widget.PopupWindow.OnDismissListener dismiss = new
	 * android.widget.PopupWindow.OnDismissListener() {
	 * 
	 * @Override public void onDismiss() {
	 * popup_show_view.setVisibility(View.VISIBLE); } };
	 */

	/*
	 * public void showPopUp(final Object obj, final int position, final int
	 * handStyle) { // popupWindow=new PopupWindow();
	 * 
	 * View view = inflater.inflate(R.layout.popu_item, null); final
	 * RelativeLayout layout = (RelativeLayout) view
	 * .findViewById(R.id.popu_back); // layout.setLayoutParams(new
	 * LayoutParams(8, 8)); final ImageButton iv = (ImageButton) view
	 * .findViewById(R.id.popu_app_ico); TextView tv = (TextView)
	 * view.findViewById(R.id.popu_app_name);
	 * tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
	 * if (handStyle == APP_ITEM_CLICKED) {
	 * 
	 * initPopuItem(obj, position, layout, iv, tv); } else {
	 * initPopuRecommend(obj, position, layout, iv, tv); } if (popup_show_view
	 * != null) { popupWindow = new PopupWindow(popup_show_view.getWidth() +
	 * 150, popup_show_view.getHeight() + 150); } if (popupWindow == null) {
	 * return; } popupWindow.setContentView(view); //
	 * popupWindow.setFocusable(true); popupWindow.setOutsideTouchable(true);
	 * popupWindow.setAnimationStyle(R.style.popu);
	 * popupWindow.setBackgroundDrawable(new ColorDrawable(0));
	 * popupWindow.setOnDismissListener(dismiss); location = new int[2];
	 * show(popup_show_view, position, handStyle);
	 * 
	 * OnClickListener popupOnclick = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { stub switch (handStyle) { case
	 * HOME_REC_CLICKED: // ImageView v=(ImageView)obj;
	 * homeRecClicked((ImageButton) obj, recInfos); break; case
	 * APP_ITEM_CLICKED: itemPopuClick(position); break; default: break; } } };
	 * layout.setOnClickListener(popupOnclick); //
	 * layout.setOnLongClickListener(appAdapter_longclick); //
	 * iv.setOnLongClickListener(appAdapter_longclick);
	 * iv.setOnClickListener(popupOnclick);
	 * 
	 * }
	 */

	/*
	 * private void initPopuRecommend(final Object obj, final int position,
	 * final RelativeLayout layout, final ImageButton iv, TextView tv) { try {
	 * recInfos = iser.getAppInfos(); popup_show_view = (ImageButton) obj; if
	 * (popup_show_view == null) { return; } if (null != recInfos &&
	 * recInfos.size() != 0) { tv.setText(recInfos.get(position).getName()); }
	 * switch (position) { case 0: RelativeLayout.LayoutParams rl = new
	 * RelativeLayout.LayoutParams( 440, 440); rl.topMargin = 83; rl.leftMargin
	 * = 80; iv.setLayoutParams(rl);
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_1));
	 * 
	 * iv.setBackgroundDrawable(LKHomeUtil.zoomBitmap(
	 * popup_show_view.getBackground(), 412)); break; case 1:
	 * RelativeLayout.LayoutParams rl_1 = new RelativeLayout.LayoutParams( 219,
	 * 214); rl_1.topMargin = 67; rl_1.leftMargin = 65;
	 * iv.setLayoutParams(rl_1); iv.setBackgroundDrawable(LKHomeUtil.zoomBitmap(
	 * popup_show_view.getBackground(), 211));
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_2)); break; case 2: RelativeLayout.LayoutParams
	 * rl_5 = new RelativeLayout.LayoutParams( 418, 210); rl_5.topMargin = 67;
	 * rl_5.leftMargin = 125; iv.setLayoutParams(rl_5);
	 * iv.setBackgroundDrawable(LKHomeUtil.zoomBitmap(
	 * popup_show_view.getBackground(), 205));
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_2)); break; case 3: RelativeLayout.LayoutParams
	 * rl_2 = new RelativeLayout.LayoutParams( 219, 214); rl_2.topMargin = 67;
	 * rl_2.leftMargin = 65; iv.setLayoutParams(rl_2);
	 * iv.setBackgroundDrawable(LKHomeUtil.zoomBitmap(
	 * popup_show_view.getBackground(), 211));
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_2)); break; case 4: RelativeLayout.LayoutParams
	 * rl_3 = new RelativeLayout.LayoutParams( 219, 214); rl_3.topMargin = 67;
	 * rl_3.leftMargin = 65; iv.setLayoutParams(rl_3);
	 * iv.setBackgroundDrawable(LKHomeUtil.zoomBitmap(
	 * popup_show_view.getBackground(), 211));
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_2)); break; case 5: RelativeLayout.LayoutParams
	 * rl_4 = new RelativeLayout.LayoutParams( 219, 214); rl_4.topMargin = 67;
	 * rl_4.leftMargin = 65; iv.setLayoutParams(rl_4);
	 * iv.setBackgroundDrawable(LKHomeUtil.zoomBitmap(
	 * popup_show_view.getBackground(), 211));
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_2)); tv.setText("无线路由"); break; default: break; }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	/*
	 * private void initPopuItem(final Object obj, final int position, final
	 * RelativeLayout layout, final ImageButton iv, TextView tv) {
	 * popup_show_view = (View) obj; if (popup_show_view == null) { return; }
	 * ImageButton ib = (ImageButton) popup_show_view
	 * .findViewById(R.id.home_app_ico); TextView title = (TextView)
	 * popup_show_view .findViewById(R.id.home_app_name);
	 * 
	 * if (!Constant.CLASSIFY_USER.equals(style_flag)) {
	 * 
	 * if (title.getText().toString().equals(getString(R.string.More))) {
	 * popup_show_view.setVisibility(View.INVISIBLE); }
	 * 
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_2)); RelativeLayout.LayoutParams rl_1 = new
	 * RelativeLayout.LayoutParams( 227, 222); rl_1.topMargin = 71;
	 * rl_1.leftMargin = 69; iv.setLayoutParams(rl_1);
	 * iv.setBackgroundDrawable(ib.getBackground().mutate()); } else {
	 * layout.setBackgroundDrawable(getResources().getDrawable(
	 * R.drawable.rec_shadow_2)); RelativeLayout.LayoutParams rl_1 = new
	 * RelativeLayout.LayoutParams( 227, 222); rl_1.topMargin = 71;
	 * rl_1.leftMargin = 69; iv.setLayoutParams(rl_1);
	 * iv.setBackgroundDrawable(getResources().getDrawable(
	 * Constant.ITEM_BACKS[position % Constant.ITEM_BACKS.length]));
	 * iv.setImageDrawable(LKHomeUtil.zoomBitmap(ib.getBackground() .mutate(),
	 * 200)); } if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
	 * tv.setVisibility(View.INVISIBLE); }
	 * tv.setText(title.getText().toString()); }
	 */

	/*
	 * private void show(View v, int position, int style) { if (style ==
	 * APP_ITEM_CLICKED) { popupWindow.setWidth(365);
	 * popupWindow.setHeight(365); popupWindow.showAtLocation(v,
	 * Gravity.NO_GRAVITY, (int) v.getX() + 2, (int) v.getY() + 35); } else {
	 * switch (position) { case 0: popupWindow.setWidth(600);
	 * popupWindow.setHeight(610); popupWindow.showAtLocation(v,
	 * Gravity.NO_GRAVITY, v.getLeft() - 100, v.getTop()); break; case 1:
	 * 
	 * popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, v.getRight() +
	 * v.getWidth() + 63, v.getBottom() - 155); break; case 2:
	 * popupWindow.setWidth(670); popupWindow.setHeight(345);
	 * popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, v.getRight() + 110,
	 * v.getTop() + 50); break;
	 * 
	 * case 3: popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, v.getRight() +
	 * v.getWidth() + 63, v.getBottom() + 50); break; case 4:
	 * popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, v.getRight() +
	 * v.getWidth() + 63, v.getBottom() + 50); break; case 5:
	 * popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, v.getRight() +
	 * v.getWidth() + 63, v.getBottom() + 50); break;
	 * 
	 * default: break; } } }
	 */
	/*
	 * private void showHandle(View v) {
	 * 
	 * AlertDialog.Builder builder = new Builder(this);
	 * builder.setTitle(getString(R.string.Dialog_title)); LinearLayout
	 * ll_handler = new LinearLayout(this); ll_handler.setLayoutParams(new
	 * LayoutParams( android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
	 * android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
	 * ll_handler.setOrientation(LinearLayout.VERTICAL); final Button b_move =
	 * new Button(this); // b_move.setBackgroundDrawable(null);
	 * b_move.setText(getString(R.string.Dialog_move)); b_move.setWidth(500);
	 * final Button b_unload = new Button(this); //
	 * b_unload.setBackgroundDrawable(null);
	 * b_unload.setText(getString(R.string.Dialog_unload));
	 * ll_handler.addView(b_move); ll_handler.addView(b_unload);
	 * builder.setView(ll_handler);
	 * 
	 * OnClickListener clicked = new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { stub
	 * 
	 * } }; b_move.setOnClickListener(clicked);
	 * b_unload.setOnClickListener(clicked); builder.create().show(); }
	 */

	/************************************************************************************************/
	// 遥控操作时，每个条目被放大后的点击事件
	/*
	 * private void itemPopuClick(final int position) {
	 * 
	 * if (Constant.CLASSIFY_SETTING.equals(style_flag)) { switch (position) {
	 * case 0:// wireless route goSettingIntent("com.lenkeng.network"); break;
	 * case 1:// wireless connected
	 * 
	 * goSettingIntent("com.lenkeng.hot"); break; case 2:// screen show setting
	 * goSettingIntent("com.lenkeng.screen"); break; case 3:// weather setting
	 * if (Environment.getExternalStorageState().equals(
	 * Environment.MEDIA_MOUNTED)) { Intent intent = new Intent(this,
	 * WeatherSettingActivity.class); startActivity(intent); } break; case 4:
	 * Intent intent = new Intent(MainHomeActivity.this,
	 * AppManagerActivity.class); startActivity(intent); break; case 5:// system
	 * update Intent intent_ = MainHomeActivity.this.getPackageManager()
	 * .getLaunchIntentForPackage("com.lkhome.lkhomeupgrade"); if (intent_ !=
	 * null) { MainHomeActivity.this.startActivity(intent_); }
	 * 
	 * break; case 6: Intent systemInfoIntent = new Intent(this,
	 * SystemInfoActivity.class); startActivity(systemInfoIntent); break;
	 * default: break; } } else if (Constant.CLASSIFY_SCAN.equals(style_flag)) {
	 * scanIntent(position); } else { openUserApp(position); } }
	 */

	/*
	 * private void openUserApp(final int position) { if ("".equals(style_flag)
	 * || null == style_flag) { return; } List<AppInfo> allAppInfos =
	 * vpf.getAllInfos(style_flag); String packageName = allAppInfos.get(
	 * vpf.getCurrentIndex(style_flag) + position).getPackage_name(); String
	 * appname = LKHomeUtil.getLabel(packageName); if
	 * (packageName.equals(Constant.MORE)) { Intent intent = new
	 * Intent(MainHomeActivity.this, MainActivity.class);
	 * intent.putExtra("flag", style_flag); startActivity(intent); } else {
	 * 
	 * if (Constant.CLASSIFY_USER.equals(style_flag)) { // obtain the user app
	 * data int counter = sp.getInt("counter", 0); counter++; Editor edit =
	 * sp.edit(); edit.putInt("counter", counter); edit.commit();
	 * caDao.addUserRunData(appname, packageName); } Intent intent =
	 * MainHomeActivity.this.getPackageManager()
	 * .getLaunchIntentForPackage(packageName);
	 * 
	 * if (intent != null) { MainHomeActivity.this.startActivity(intent); } } }
	 */
	/*
	 * public void goSettingIntent(String action) { try { Intent route_intent =
	 * new Intent(action);
	 * route_intent.addCategory("android.intent.category.DEFAULT");
	 * route_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 */

	/*
	 * private void scanIntent(final int position) { Intent intent = new
	 * Intent(); intent.setAction("android.intent.action.VIEW"); Uri content_url
	 * = Uri.parse(Constant.SCANURL[vpf .getCurrentIndex(style_flag) +
	 * position]); intent.setData(content_url);
	 * intent.setClassName("com.android.browser",
	 * "com.android.browser.BrowserActivity"); startActivity(intent); }
	 */
   
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (IS_LONGCLICK) {
			IS_LONGCLICK = false;
			return false;
		}
		/*if (!Constant.CLASSIFY_RECOMMEND.equals(style_flag)) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_PAGE_UP:
				int up = vpf.getCurrentViewPager(style_flag).getCurrentItem();
				up--;
				// vpf.getCurrentViewPager(style_flag).setCurrentItem(up);
				break;
			case KeyEvent.KEYCODE_PAGE_DOWN:
				int down = vpf.getCurrentViewPager(style_flag).getCurrentItem();
				down++;
				// vpf.getCurrentViewPager(style_flag).setCurrentItem(down);
				break;
			default:
				break;
			}
		}*/
		return super.onKeyUp(keyCode, event);
	}

	//long rightDownTime = 0, upDownTime = 0, indexTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		/*if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			rightDownTime = event.getDownTime();
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			upDownTime = event.getDownTime();
			indexTime = Math.abs(rightDownTime - upDownTime);
			Logger.e("ez2", "$$$---onKeyDown---" + keyCode + "----indexTime---"
					+ indexTime);
		}*/	
		if(keyCode == KeyEvent.KEYCODE_BACK){
			onClick(STYLE_VIEW.get(style_flag));
			STYLE_VIEW.get(style_flag).requestFocus();
		}
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_TAB
				|| keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
				|| keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
			return true;
		}
		
		return myKeyEvent(keyCode, event);

	}

	private boolean myKeyEvent(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			MOUSE_UP = true;
		} else {
			MOUSE_UP = false;
		}
		if (keyCode == 82) {
			mpf.clearAnim();
			mpf.disMiss();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	/*String ss = "";

	private boolean buildString(String key) {
		ss += key;
		if (ss.length() > "8811101214".length()) {
			ss = "";
		}
		if ("8811101214".equals(ss)) {
			Editor edit = sp.edit();
			edit.putString("isFactory", ss);
			edit.commit();
			// startDemolauncher();
			ss = "";
			
			 * if(!isDemoLauncherInstall()){ new SilentInstall(this,
			 * "/sdcard/appinfo/DemoLauncher.apk").installPackage(); }
			 
			return true;
		} else {
			return false;
		}
	}*/

	/**
	 * the view of recommend listener
	 */
	int RIGHT_CLICKED_TIMES = 0;

	

	public OnKeyListener keListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			switch (v.getId()) {
			case R.id.home_app_list:
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					IS_APP_FROM_BUTTON = false;
					return vpf.isLastItem(style_flag, v);
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					IS_APP_FROM_BUTTON = true;
				} else {
					IS_APP_FROM_BUTTON = false;
				}
				break;
			case R.id.home_btMsg:
				if (keyCode == KeyEvent.KEYCODE_DPAD_UP
						|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					animationFactory.startUnderLineAnition(buttons[5],
							buttons[5], v);
					animationFactory
							.startScaleAnimation(buttons[5], buttons[5]);
				}
				break;
			case R.id.Recommend_one:
			case R.id.Recommend_four:
			case R.id.Recommend_five:
				IS_APP_FROM_BUTTON = true;
				break;
			case R.id.Recommend_three:
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return true;
				}
				break;
			case R.id.Recommend_six:
				IS_APP_FROM_BUTTON = true;
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					return true;
				}
				break;
			default:
				break;
			}
			return false;
		}
	};
	private OnKeyListener buttonsKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				mpf.clearAnim();
				mpf.disMiss();
				switch (v.getId()) {
				case R.id.home_backup_bt_recommend:
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						animationFactory.startUnderLineAnition(buttons[0],
								buttons[1], buttons[1]);
						animationFactory.startScaleAnimation(buttons[1],
								buttons[0]);
						// vpf.showCurrentPager(Constant.CLASSIFY_MOVIE);
						// vpf.getCurrentViewPager(style_flag).setAnimation(AnimationUtils.loadAnimation(MainHomeActivity.this,R.anim.left_screen));
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						if (AnimationFactory.isUnderLineEnd) {
							iv_move_frame.clearAnimation();
						} else {
							return true;
						}
					}

					break;
				case R.id.home_backup_bt_movie:

					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						animationFactory.startUnderLineAnition(buttons[1],
								buttons[2], buttons[2]);
						animationFactory.startScaleAnimation(buttons[2],
								buttons[1]);
						// vpf.showCurrentPager(Constant.CLASSIFY_APPLICATION);
						// vpf.getCurrentViewPager(style_flag).setAnimation(AnimationUtils.loadAnimation(MainHomeActivity.this,R.anim.left_screen));
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						animationFactory.startUnderLineAnition(buttons[1],
								buttons[0], buttons[0]);
						animationFactory.startScaleAnimation(buttons[0],
								buttons[1]);
						// vpf.showCurrentPager(Constant.CLASSIFY_RECOMMEND);
						// vpf.getCurrentViewPager(style_flag).setAnimation(AnimationUtils.loadAnimation(MainHomeActivity.this,R.anim.right_screen));
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

						if (AnimationFactory.isUnderLineEnd) {
							iv_move_frame.clearAnimation();
						} else {
							return true;
						}
					}
					break;
				case R.id.home_backup_bt_application:
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						animationFactory.startUnderLineAnition(buttons[2],
								buttons[3], buttons[3]);
						animationFactory.startScaleAnimation(buttons[3],
								buttons[2]);
						// vpf.getCurrentViewPager(style_flag).setAnimation(AnimationUtils.loadAnimation(MainHomeActivity.this,R.anim.left_screen));
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						animationFactory.startUnderLineAnition(buttons[2],
								buttons[1], buttons[1]);
						animationFactory.startScaleAnimation(buttons[1],
								buttons[2]);
						// vpf.getCurrentViewPager(style_flag).setAnimation(AnimationUtils.loadAnimation(MainHomeActivity.this,R.anim.right_screen));
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						if (AnimationFactory.isUnderLineEnd) {
							iv_move_frame.clearAnimation();
						} else {
							return true;
						}
					}
					break;
				case R.id.home_backup_bt_game:
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						animationFactory.startUnderLineAnition(buttons[3],
								buttons[4], buttons[4]);
						animationFactory.startScaleAnimation(buttons[4],
								buttons[3]);
						// vpf.getCurrentViewPager(style_flag).setAnimation(AnimationUtils.loadAnimation(MainHomeActivity.this,R.anim.left_screen));
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						animationFactory.startUnderLineAnition(buttons[3],
								buttons[2], buttons[2]);
						animationFactory.startScaleAnimation(buttons[2],
								buttons[3]);
						// vpf.getCurrentViewPager(style_flag).setAnimation(AnimationUtils.loadAnimation(MainHomeActivity.this,R.anim.right_screen));
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						if (AnimationFactory.isUnderLineEnd) {
							iv_move_frame.clearAnimation();
						} else {
							return true;
						}
					}
					break;
				case R.id.home_backup_bt_live:
					if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						GridView gv = vpf.getCurrentAdapter(style_flag)
								.getPrimaryItem();
						if (gv == null) {
							return true;
						} else {
							if (AnimationFactory.isUnderLineEnd) {
								iv_move_frame.clearAnimation();
							} else {
								return true;
							}
						}
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						animationFactory.startUnderLineAnition(buttons[4],
								buttons[5], buttons[5]);
						animationFactory.startScaleAnimation(buttons[5],
								buttons[4]);
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						animationFactory.startUnderLineAnition(buttons[4],
								buttons[3], buttons[3]);
						animationFactory.startScaleAnimation(buttons[3],
								buttons[4]);
					}
					break;
				case R.id.home_backup_bt_setting:
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						animationFactory.startUnderLineAnition(buttons[5],
								buttons[4], buttons[4]);
						animationFactory.startScaleAnimation(buttons[4],
								buttons[5]);
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						if (rl_msg.getVisibility() != View.GONE) {
							animationFactory.getScaleBigButton().clearAnimation();
							iv_move_frame.clearAnimation();
						} else {
							return true;
						}
					} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						if (AnimationFactory.isUnderLineEnd) {
							iv_move_frame.clearAnimation();
						} else {
							return true;
						}
					}
					break;
				default:
					break;
				}
			}
			return false;
		}
	};

	BroadcastReceiver langueReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			File f = new File("/sdcard/appinfo");
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			LKService.recSize.clear();
			iser.clearInfos();
			Logger.i("tag", "------local chanaged---" + getRecoUrl());
			Logger.i("tag", "------URLs.getHost()---" + URLs.getMarketHost());
		}
	};

	public String getRecoUrl() {
		String RECOMMEND_APPINFOS = "http://" + URLs.getMarketHost()
				+ "/AppMarket/android/getRecommended.do";
		return RECOMMEND_APPINFOS;
	}

	public String getCheckVersion() {
		String CHECK_VERSION_URL = "http://" + URLs.getMarketHost()
				+ "/AppMarket/android/checkVersion.do?version=";
		return CHECK_VERSION_URL;
	}

	// 获取显示的宽度百分比
	private int getDisplayPercent_w() {
		try {
			return Settings.Global.getInt(cr,
					LKHomeUtil.DISPLAY_PERCENT_HORIZONTAL);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return 95; // 默认的显示百分比

	}

	// 获取显示高度的百分比
	private int getDisplayPercent_h() {
		try {
			return Settings.Global.getInt(cr,
					LKHomeUtil.DISPLAY_PERCENT_VERTICAL);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return 95; // 默认的显示百分比

	}

	private void initCameraStateReceiver() {
		mCameraStateFilter = new IntentFilter();
		mCameraStateFilter
				.addAction("android.intent.action.CAMERA_SWITCH_EVENT");

		mCameraStateReceiver = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {

				final int cameraState = intent.getIntExtra(
						"android.intent.extra.CAMERA_SWITCH_STATE", 0);

				// Log.e(TAG,
				// "@@@@@@@line 2117 收到摄像头盖子变化广播: state="+cameraState);
				showCaremaCoverState(cameraState);
				/*
				 * handler.postDelayed(new Runnable() {
				 * 
				 * @Override public void run() {
				 * showCaremaCoverState(cameraState); //checkNetconnect(); } },
				 * 1000);
				 */

			}
		};

		this.registerReceiver(mCameraStateReceiver, mCameraStateFilter);

	}

	private void showCaremaCoverState(int state) {
		if (state == 1) {// ON
			handler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (ACCESSIBILITY_SERVICE) {
						LKToast.camerOpen(LKHomeApp.getAppContext());
						Logger.d("tag", "------HOUR---" + LKHomeUtil.getHour());
						su.playerON();
						// LKHomeUtil.showToast(MainHomeActivity.this,
						// R.string.camera_cover_on);
					}
				}
			});

		} else if (state == 0) {// OFF
			handler.post(new Runnable() {

				@Override
				public void run() {
					synchronized (ACCESSIBILITY_SERVICE) {
						LKToast.camerClose(LKHomeApp.getAppContext());
						su.playerOFF();
						// LKHomeUtil.showToast(MainHomeActivity.this,
						// R.string.camera_cover_off);
					}

				}
			});
		}
	}

	/**
	 * 获取当前摄像头盖的打开/关闭状态
	 * 
	 * @return
	 */
	private int getCameraCoverState() {
		int state = 0;
		try {
			state = Integer.parseInt(SystemProperties
					.get("sys.camera.switch.state"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return state;
	}

	public void addBugLog(String msg) {
		if (LKHomeUtil.DEBUG) {
			Log.i("ez2", msg);
		}
	}
}
