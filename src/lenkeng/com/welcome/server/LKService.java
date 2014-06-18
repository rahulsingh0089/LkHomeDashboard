package lenkeng.com.welcome.server;

import java.io.File;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import lenkeng.com.welcome.LKHomeApp;
import lenkeng.com.welcome.MainHomeActivity;
import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.bean.ScanInfo;
import lenkeng.com.welcome.bean.WeatherInfo;
import lenkeng.com.welcome.bean.XmppMessage;
import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.util.MyDownloadThreadManager;
import lenkeng.com.welcome.util.SilentInstall;
import lenkeng.com.welcome.util.SoundUtil;
import lenkeng.com.welcome.util.XmppDbUtil;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lenkeng.bean.URLs;
import com.lenkeng.logic.Logic;
import com.lenkeng.tools.ThreadPoolUtil;
import com.lenkeng.tools.Util;
import android.provider.Settings;
import android.app.INotificationManager;
import android.os.ServiceManager;

@SuppressLint("SdCardPath")
public class LKService extends Service implements OnClickListener,
		OnFocusChangeListener {
	public static final long PEER_TIME = 1000 * 30;
	// private static final String UPGRADE = "upgrade";
	private static final String ACTION_UPGRADE_LANUCHER = "upgrade";
	private static final String TAG = "LKService";
	public static final Map<String,AppInfo> DOWNLOAD_APPS=new HashMap<String, AppInfo>();
	private AppDataDao appDao;
	private XmppDbUtil xmpUtil;
	private LKHomeUtil lkUtil;
	// private SoundUtil soundUtil;
	private AppStoreDao appStoreDao;
	private XMPPConnection conn = null;
	private Handler handler, msgHandler;
	public static LKHomeInterface lkInterface;
	private View detail;
	private SharedPreferences ticket;
	private Editor editor;
	private WindowManager mWindowManager = null;
	private LayoutParams mLayoutParams = null;
	private String ticket_package;
	IntentFilter voteFilter;
	private List<AppInfo> movieList=new ArrayList<AppInfo>();
	private List<AppInfo> appList =new ArrayList<AppInfo>(); 
	private List<AppInfo> gameList =new ArrayList<AppInfo>();
	private List<AppInfo> userList=new ArrayList<AppInfo>();

	private Map<String, List<AppInfo>> classzMap = new HashMap<String, List<AppInfo>>();
	public static Map<String, Long> recSize = new HashMap<String, Long>();
	private SharedPreferences sp;
	private ChatManager manager;
	private String filename;
	private int version;

	private AppSync task;

	private float actionDownY;
	private float actionDownX;
	private int downX;
	private int downY;

	// public static Map<String, String> FtoPMap = new HashMap<String,
	// String>();
	// public static Map<String, String> FtoSMap = new HashMap<String,
	// String>();
	// public static Map<String, String> iconMaps = new HashMap<String,
	// String>();
	public static int currentPage;
	private String userName = "";
	// private MyDownloadThreadManager tm;
	public static List<MyDownloadThreadManager> dtmList = new LinkedList<MyDownloadThreadManager>();
	public List<AppInfo> appInfos;
	public List<ScanInfo> scanInfos;
	//public List<AppInfo> bannerInfos;
	public List<AppInfo> recSortInfos;
	private BroadcastReceiver voteReceiver;
	private ActivityManager mActivityManager;
	private int parise;
	private int reject;
	private boolean popuing = false;
	private AppInfo info;
	private RemoveRun removeRun;
	private Logic mLogic;
	private Button btn_like;
	private Button btn_hate;

	private TextView tv_mess, tv_name, tv_praise, tv_reject;
	private ImageView iconView;

	protected static final int ENABLE_LIKE = 1001;
	protected static final int ENABLE_HATE = 1002;
	protected static final int OPERATE_LIKE = 1;
	protected static final int OPERATE_HATE = 2;

	class RemoveRun implements Runnable {

		@Override
		public void run() {
			if (popuing) {
				mWindowManager.removeView(detail);
				popuing = false;

			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		long bef = System.currentTimeMillis();
		LKHomeUtil.systemAppClassify();
		querryAppData(true, null);
		long aft = System.currentTimeMillis();
		Logger.e("kao", "$$$------onBind---" + (aft - bef));
		// bindService(new Intent(LKHomeInterface.class.getName()), new
		// MyConn(),
		// Context.BIND_AUTO_CREATE);
		return new MyBind();

	}

	class MyConn implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			lkInterface = LKHomeInterface.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	}

	private void querryAppData(boolean all, String style_flag) {

		if (all) {
			ProgressBar bar=new ProgressBar(getApplicationContext());
			bar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));
			
			movieList = appDao.getInstallAppInfo(Constant.CLASSIFY_MOVIE);
			appList = appDao.getInstallAppInfo(Constant.CLASSIFY_APPLICATION);
			gameList = appDao.getInstallAppInfo(Constant.CLASSIFY_GAME);
			userList = appDao.getInstallAppInfo(Constant.CLASSIFY_USER);
		} else {
			if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
				movieList = appDao.getInstallAppInfo(Constant.CLASSIFY_MOVIE);
			} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
				appList = appDao
						.getInstallAppInfo(Constant.CLASSIFY_APPLICATION);
			} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
				gameList = appDao.getInstallAppInfo(Constant.CLASSIFY_GAME);
			} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
				userList = appDao.getInstallAppInfo(Constant.CLASSIFY_USER);
			}
		}
		// Log.e(TAG, "~~~~~~从数据库获取记录: moveList="+movieList);

		/*
		 * moviesList = appDao.getPackageName(Constant.CLASSIFY_MOVIE);
		 * gamesList = appDao.getPackageName(Constant.CLASSIFY_GAME); appList =
		 * appDao.getPackageName(Constant.CLASSIFY_APPLICATION); myselList =
		 * appDao.getPackageName(Constant.CLASSIFY_MYSELF); liveList =
		 * appDao.getPackageName(Constant.CLASSIFY_USER); scanList =
		 * appDao.getPackageName(Constant.CLASSIFY_SCAN);
		 */
		classzMap.put(Constant.CLASSIFY_MOVIE, movieList);
		classzMap.put(Constant.CLASSIFY_GAME, gameList);
		classzMap.put(Constant.CLASSIFY_APPLICATION, appList);
		classzMap.put(Constant.CLASSIFY_USER, userList);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		long bef = System.currentTimeMillis();
		mLogic = Logic.getInstance(getApplicationContext());
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_INSTALED);
		filter.addAction(Constant.ACTION_DOWNLOAD_COMPLETE);
		// filter.addAction(Constant.ACTION_IMG_DOWNLOAD_COMPLETE);
		filter.addAction(Constant.ACTION_WEATHERDATA_DOWNLOAD_COMPLETE);
		// filter.addAction(Constant.ACTION_APPINFO_DOWNLOAD_COMPLETE);
		filter.addAction(ACTION_UPGRADE_LANUCHER);
		registerReceiver(installOrRemove, filter);

		removeRun = new RemoveRun();
		msgHandler = new Handler() {

			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {

				case 4:

					ComponentName cn = mActivityManager.getRunningTasks(1).get(
							0).topActivity;
					String packageName = cn.getPackageName();
					ticket_package = packageName;
					if (!packageName.equals("lenkeng.com.welcome")) {
						
				   ((TextView)detail.findViewById(R.id.textView1)).setText(getApplicationContext().getResources().getString(R.string.evaluate));
				   ((Button)detail.findViewById(R.id.like)).setText(getApplicationContext().getResources().getString(R.string.parise));
				   ((Button)detail.findViewById(R.id.hate)).setText(getApplicationContext().getResources().getString(R.string.reject));
						/*
						 * try { mWindowManager.addView(detail, mLayoutParams);
						 * popuing = true;
						 * 
						 * } catch (Exception e) { e.printStackTrace(); }
						 */
						task = new AppSync(packageName, getBaseContext(),
								(TextView) detail.findViewById(R.id.mess));
						task.execute();
					} else {
						popuing = false;

					}
					break;
				case 0: // 根据packageName查找到AppInfo

					if (info == null) {

						popuing = false;
						LKHomeUtil.showToast(getApplicationContext(),
								R.string.ticket_unsuport);
						break;
					}

					try {
						mWindowManager.addView(detail, mLayoutParams);
						popuing = true;

					} catch (Exception e) {
						e.printStackTrace();
					}

					mLogic.asView(info.getHDIcon(), iconView, handler);
					tv_name.setText(info.getName());
					tv_praise.setText(String.valueOf(info.getPraise()));
					tv_reject.setText(String.valueOf(info.getReject()));

					if (reject < 3 || parise < 3) {// 可以操作

						tv_mess.setVisibility(View.GONE);

						if (reject < 3) {
							btn_hate.setClickable(true);
						} else {
							btn_hate.setClickable(false);
						}

						if (parise < 3) {
							btn_like.setClickable(true);
						} else {
							btn_like.setClickable(false);
						}

					} else {// 不可操作

						tv_mess.setText(R.string.ticket_used);
						tv_mess.setVisibility(View.VISIBLE);

						btn_like.setClickable(false);
						btn_hate.setClickable(false);

					}
					postDelayed(removeRun, 5000);

					break;
				case OPERATE_LIKE: // TODO...赞

					if (parise < 3) {
						detail.findViewById(R.id.mess).setVisibility(
								View.VISIBLE);
						parise();
					} else {
						((TextView) detail.findViewById(R.id.mess))
								.setText(R.string.parise_used);
						detail.findViewById(R.id.mess).setVisibility(
								View.VISIBLE);
						msgHandler.sendEmptyMessage(ENABLE_LIKE);
					}
					removeCallbacks(removeRun);
					postDelayed(removeRun, 3000);

					break;

				case OPERATE_HATE:// TODO...踩

					if (reject < 3) {
						detail.findViewById(R.id.mess).setVisibility(
								View.VISIBLE);
						reject();
					} else {
						((TextView) detail.findViewById(R.id.mess))
								.setText(R.string.reject_used);
						detail.findViewById(R.id.mess).setVisibility(
								View.VISIBLE);
						msgHandler.sendEmptyMessage(ENABLE_HATE);
					}
					removeCallbacks(removeRun);
					postDelayed(removeRun, 3000);

				case ENABLE_LIKE:// enable 赞
					btn_like.setEnabled(true);

					break;

				case ENABLE_HATE:
					btn_hate.setEnabled(true);
					break;
				default:
					break;
				}

			}
		};
		ticket = getSharedPreferences("ticket", Context.MODE_PRIVATE);

		editor = ticket.edit();

		LayoutInflater mInflater = LayoutInflater.from(this);
		detail = mInflater.inflate(R.layout.popudetail, null);
		tv_mess = (TextView) detail.findViewById(R.id.mess);
		tv_name = (TextView) detail.findViewById(R.id.name);
		tv_praise = (TextView) detail.findViewById(R.id.praise);
		tv_reject = (TextView) detail.findViewById(R.id.reject);
		iconView = (ImageView) detail.findViewById(R.id.popuIcon);

		btn_like = (Button) detail.findViewById(R.id.like);
		btn_hate = (Button) detail.findViewById(R.id.hate);
		// ImageView pupuIcon = (ImageView) detail.findViewById(R.id.popuIcon);
		btn_like.setOnClickListener(this);
		btn_like.setOnFocusChangeListener(this);
		btn_hate.setOnClickListener(this);
		btn_hate.setOnFocusChangeListener(this);
		//btn_like.setOnKeyListener(keyListener);
		//btn_hate.setOnKeyListener(keyListener);
		mLayoutParams = new LayoutParams();
		mLayoutParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		mLayoutParams.format = PixelFormat.RGBA_8888;
		mLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
		mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		mLayoutParams.x = 350;
		mLayoutParams.y = 100;
		mLayoutParams.width = 600;
		mLayoutParams.height = 300;

		initVoteReceiver();

		init();

		new LoginLinstener().start();
		// new WeatherListener().start();
		new AppInfoListener().start();

		long aft = System.currentTimeMillis();
		Logger.e("kao", "--LKService---oncreate---  " + (aft - bef));
		// new ClientActionListener(getApplicationContext()).start();
		//
		/*File file = new File(Environment.getDataDirectory()
				+ "/system.notfirstrun");
		if (!file.exists()) {
			lkUtil.startTimer(null);
		}*/
		lkUtil.startTimer();
	}
	private OnKeyListener keyListener=new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(keyCode ==KeyEvent.KEYCODE_BACK){
				
			}
			return false;
		}
	};
	private void setNotificationsEnabled(boolean enabled,String packageName) {
        INotificationManager nm = INotificationManager.Stub.asInterface(
                ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        try {
            nm.setNotificationsEnabledForPackage(packageName, enabled);
        } catch (android.os.RemoteException ex) {
        }
    }
	/**
	 * 监听"赞/踩"按钮广播
	 */
	private void initVoteReceiver() {
		voteReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if (!LKHomeUtil.isNetConnected()) {
					LKHomeUtil.showToast(getApplicationContext(),
							R.string.ticket_error);
					return;
				}

				if (!popuing) {
					android.os.Message msg = new android.os.Message();
					msg.what = 4;

					msgHandler.sendMessage(msg);
					popuing = true;
				}
			}
		};

		voteFilter = new IntentFilter();
		voteFilter.addAction("android.intent.action.VOTE");
		registerReceiver(voteReceiver, voteFilter);
	}

	/**
	 * 根据packageName获取Appinfo,先从服务器查找,如无到本地数据库查找
	 * 
	 * @author Administrator
	 * 
	 */
	class AppSync extends AsyncTask<String, Integer, String> {

		String packagename = "";
		TextView text = null;
		Context context = null;
		AppInfo app = null;

		public AppSync() {

		}

		public AppSync(String packagename, Context context, TextView text) {
			this.packagename = packagename;
			this.context = context;
			this.text = text;
		}

		public AppSync(Context context, TextView text) {
			this.context = context;
			this.text = text;
		}

		public void setPackage(String packagename) {
			this.packagename = packagename;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			text.setVisibility(View.VISIBLE);
			text.setText(R.string.waitting);
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("packagename", this.packagename);
			app = new AppInfo();
			app = mLogic.syncApp(param);

			if (app != null) {

				return getString(R.string.ticket_updateed);
			} else {
				return "";
			}
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);

			parise = ticket.getInt(ticket_package + "_parise", 0);
			reject = ticket.getInt(ticket_package + "_reject", 0);

			info = app;
			/*
			 * if (app == null) {//不去本地数据库查找 //info =
			 * appStoreDao.getAppInfoByPackageName(this.packagename); } else {
			 * info = app; }
			 */
			android.os.Message msg2 = new android.os.Message();
			msg2.what = 0;
			msg2.obj = info;
			msgHandler.sendMessage(msg2);

		}

	}

	private void init() {
		LKHomeUtil.makeCacheDir();
		userName = LKHomeUtil.getUserName();
		/*
		 * tm=new MyDownloadThreadManager(getApplicationContext()); tm.start();
		 */
		appDao =  AppDataDao.getInstance(getApplicationContext());
		appStoreDao = AppStoreDao.getInstance(getApplicationContext());
		if (MainHomeActivity.instance != null) {
			handler = MainHomeActivity.instance.handler;
		}
		// xmpUtil = new XmppDbUtil(getApplicationContext(), handler);
		xmpUtil = new XmppDbUtil();
		lkUtil = LKHomeApp.homeUtil;
		// soundUtil = new SoundUtil(getApplicationContext());
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	int upgradeFlag = 0;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// bindRemoteService();
		return super.onStartCommand(intent, flags, startId);
	}

	private class MyBind extends Binder implements IService {
		@Override    
		public List<AppInfo> getPackageName(String str) {
			// TODO Auto-generated method stub
			AppInfo info = new AppInfo();
			info.setPackage_name(Constant.MORE);
			info.setName(Constant.MORE);
			info.setHDIcon("more");
			List<AppInfo> temp = classzMap.get(str);

			if (null != temp && !Constant.CLASSIFY_USER.equals(str)) {
				if (temp.size() == 0
						|| !Constant.MORE.equals(temp.get(temp.size() - 1)
								.getName())) {
					temp.add(temp.size(), info);
				}
			}
			return temp;

			// return appDao.getPackageName(str);
		}

		@Override
		public List<AppInfo> getAppInfos() {
			// TODO Auto-generated method stub
			return appInfos;
		}

		@Override
		public List<ScanInfo> getScanInfos() {
			// TODO Auto-generated method stub
			return scanInfos;
		}

		@Override
		public List<AppInfo> getRecAppInfos9() {
			// TODO Auto-generated method stub
			if (recSortInfos != null || recSortInfos.size() != 0) {
				return recSortInfos;
			} else {
				return appStoreDao.getAppInfos(Constant.APPSTORE_MODE_SORT);
			}
		}

		@Override
		public void clearInfos() {
			// TODO Auto-generated method stub
			appInfos.clear();
		}
	}

	// 登陆服务器，监听推送消息
	private class LoginLinstener extends Thread {
		private ChatManager manager;
		private boolean isNeedRegisted = true;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			conn = LKHomeUtil.getConnection();

			while (true) {
				// 如果没有网络则不去做事
				if (!LKHomeUtil.isNetConnected()) {
					try {
						Logger.i("gww", " net't connected.....");
						Thread.sleep(PEER_TIME);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}

				// 注册一个新用户
				try {
					if (isNeedRegisted) {
						AccountManager acManager = conn.getAccountManager();
						acManager.createAccount(userName, "123456");
						isNeedRegisted = false;
						Logger.i("gww", "---register---");
					}
				} catch (XMPPException e2) {
					// TODO Auto-generated catch block
					try {
						int code = e2.getXMPPError().getCode();
						if (code == 409) {
							isNeedRegisted = false;
						} else {
							isNeedRegisted = true;
							continue;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				} catch (IllegalStateException e) {
					conn = LKHomeUtil.getConnection();
				} catch (Exception e3) {
					e3.printStackTrace();
					continue;
				}

				// 判断用户有没有登陆
				try {
					if (!conn.isAuthenticated()) {
						Logger.i("gww", "-----isAuthenticated--login before--"
								+ conn.isAuthenticated());
						userName = LKHomeUtil.getUserName();
						Logger.i("gww", "------getUserName---" + userName);
						conn.login(userName, "123456");
						isNeedRegisted = false;
						manager = conn.getChatManager();
						manager.addChatListener(listener);
						Logger.i("gww", "-----isAuthenticated--login after--"
								+ conn.isAuthenticated());
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					// conn.disconnect();
					e1.printStackTrace();
					conn = LKHomeUtil.getConnection();
				}
				try {
					Thread.sleep(PEER_TIME);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	int temp = 0;

	/*private class WeatherListener extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int i = 30;
			int j = 0;
			while (true) {

				if (!LKHomeUtil.isNetConnected()) {
					try {
						if (Constant.NEED_CHECK_VIDEOMSG) {
							temp++;
							if (temp == 120) {
								Intent check = new Intent();
								check.setAction("checkVideo");
								sendBroadcast(check);
								Logger.v("tag", "$$$---" + TAG
										+ "--WEATHER---CHeckVideo");
								temp = 0;
							}
						}
						Thread.sleep(PEER_TIME);
						continue;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				String cityName = sp.getString("city", Constant.DEFAULT_CITY);
				File file = new File("/sdcard/weather/" + cityName);
				if (!file.exists() || file.length() == 0) {
					weatherfileListener(true);
					
					 * try { sleep(3000); continue; } catch
					 * (InterruptedException e) { // TODO Auto-generated catch
					 * block e.printStackTrace(); }
					 
				}
				j = sp.getInt("weatherCount", 0);
				j++;

				if (Constant.NEED_CHECK_VIDEOMSG) {
					if (j == 60 || j == 120) {
						Intent check = new Intent();
						check.setAction("checkVideo");
						sendBroadcast(check);
					}
				}

				if (j % 3 == 0) {
					weatherfileListener(false);
					Logger.v("tag", "$$$---" + TAG
							+ "---weatehrListener---checkweathercatchfile");
				} else if (j >= 120) {
					j = 0;
					weatherfileListener(true);
				}
				Editor edit = sp.edit();
				edit.putInt("weatherCount", j);
				edit.commit();
				i = 60 - LKHomeUtil.time.second;
				Logger.v("tag", "$$$---" + TAG + "---weatehrListener---" + j);
				try {
					Thread.sleep(1000 * i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}*/

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (conn != null) {
			conn.disconnect();
		}
	}

	@SuppressLint("NewApi")
	private void netWorkTimer() {
			Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME,0);
			Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME,1);

			Settings.Global.putInt(getContentResolver(),Settings.Global.AUTO_TIME_ZONE, 0);
			Settings.Global.putInt(getContentResolver(),Settings.Global.AUTO_TIME_ZONE, 1);
	}

	private class AppInfoListener extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int i = 30;
			int j = 1;
			while (true) {
				
				/*
				 * if (null == appInfos && f.exists()) { appInfos =
				 * appinfofileListener(false); appInfoImageCache(appInfos,
				 * isRefreshPic); }
				 */
				
				
				try {
					if (appInfos == null) {
						appInfos = appStoreDao
								.getAppInfos(Constant.APPSTORE_MODE_HOME_RECOMMEND);
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (!LKHomeUtil.isNetConnected()) {
					try {
						sleep(PEER_TIME);
						continue;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				//netWorkTimer();
				
				// appInfos
				// =appUninstallDao.getAppInfos();lkUtil.getRecommendNums()
				// appInfos.size() < lkUtil.getRecommendNums()
				try {
					if (null == appInfos
							|| appInfos.size() < lkUtil.getRecommendNums()
							|| isExistRecPic()) {
						getRecommendData();
						appInfos = appStoreDao
								.getAppInfos(Constant.APPSTORE_MODE_HOME_RECOMMEND);
						Logger.e(TAG, "$$$---lkUtil.getRecommendNums()"
								+ lkUtil.getRecommendNums());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					/*
					 * appInfos = appStoreDao
					 * .getAppInfos(Constant.APPSTORE_MODE_HOME_RECOMMEND); if
					 * (MainHomeActivity.instance != null) {
					 * MainHomeActivity.instance.handler
					 * .sendEmptyMessage(Constant
					 * .HANDLER_DOWNLOAD_RECOMMEND_APP); }
					 */
				}

				/*
				 * scanInfos=scanDao.getScanInfos(); Logger.i("gww",
				 * "--------LKService: appDao.getScanInfos()--SIZE "
				 * +scanInfos.size()); if(null == scanInfos ||
				 * scanInfos.size()==0){ try { // getScanInfoData(); } catch
				 * (Exception e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } }
				 * bannerInfos=appStoreDao.getAppInfos(Constant
				 * .APPSTORE_MODE_BANNER); Logger.i("gww",
				 * "--------APPSTORE_MODE_BANNER--SIZE "+bannerInfos.size());
				 * if(null ==bannerInfos ||bannerInfos.size()==0){ try {
				 * getRecommendBannerData(); } catch (Exception e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } }
				 * recSortInfos
				 * =appStoreDao.getAppInfos(Constant.APPSTORE_MODE_SORT);
				 * if(null ==recSortInfos ||recSortInfos.size()==0){ try { //
				 * getRecSortData(); } catch (Exception e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } }
				 */
				j = sp.getInt("count", 0);
				j++;
				if (j >= 240) {
					try {
						if (j == 240) {
							File ff = new File("/mnt/sdcard/appinfo/");
							File[] files = ff.listFiles();
							for (File fff : files) {
								fff.delete();
							}
						}
						getRecommendData();
						appInfos = appStoreDao
								.getAppInfos(Constant.APPSTORE_MODE_HOME_RECOMMEND);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					j = 0;
					/*
					 * try { getScanInfoData(); } catch (Exception e) { // TODO
					 * Auto-generated catch block e.printStackTrace(); } try {
					 * getRecommendBannerData(); } catch (Exception e) { // TODO
					 * Auto-generated catch block e.printStackTrace(); } try {
					 * // getRecSortData(); } catch (Exception e) { // TODO
					 * Auto-generated catch block e.printStackTrace(); }
					 */
				}
				Logger.i("gww", "---LKService:-j---" + j
						+ "----------appinfos----" + appInfos.size());
				Logger.i(
						"gww",
						"--lkUtil.getRecommendNums()---"
								+ lkUtil.getRecommendNums());
				Editor edit = sp.edit();
				edit.putInt("count", j);
				edit.commit();
				i = 60 - LKHomeUtil.time.second;
				try {
					sleep(1000 * i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// 获取浏览链接数据
	/*
	 * private void getScanInfoData() throws Exception { scanInfos =
	 * LKHomeUtil.jsonScanInfo(getString(R.string.scan_url)); for (int i = 0; i
	 * < scanInfos.size(); i++) { MyDownloadThreadManager mdtd = new
	 * MyDownloadThreadManager( getApplicationContext()); dtmList.add(mdtd);
	 * File f = new File("/sdcard/appinfo/scaninfo" + i); mdtd.setFile(f);
	 * mdtd.setUrl(scanInfos.get(i).getImg());
	 * mdtd.setAction(Constant.ACTION_IMG_DOWNLOAD_COMPLETE); //
	 * mdtd.setHandler(MainHomeActivity.instance.handler); //
	 * mdtd.setHandlerWhat(Constant.HANDLER_DOWNLOAD_RECOMMEND_APP);
	 * mdtd.startDownload(); } }
	 */

	// 获取应用商店里推荐图片的数据
	/*
	 * private void getRecommendBannerData() throws Exception { bannerInfos =
	 * LKHomeUtil.jsonAppInfoFile( getString(R.string.app_store_img_rec),
	 * Constant.APPSTORE_MODE_BANNER); for (AppInfo info : bannerInfos) {
	 * MyDownloadThreadManager mdtd = new MyDownloadThreadManager(
	 * getApplicationContext()); dtmList.add(mdtd); String name =
	 * info.getBanner_small().substring( info.getBanner_small().lastIndexOf("/")
	 * + 1); File f = new File("/sdcard/appinfo/banner" + name);
	 * mdtd.setFile(f); mdtd.setUrl(info.getBanner_small());
	 * mdtd.setAction(Constant.ACTION_IMG_DOWNLOAD_COMPLETE); //
	 * mdtd.setHandler(MainHomeActivity.instance.handler); //
	 * mdtd.setHandlerWhat(Constant.HANDLER_DOWNLOAD_RECOMMEND_APP);
	 * mdtd.startDownload(); } }
	 */

	// 判断缓存的推荐图片是否存在
	private boolean isExistRecPic() {
		boolean isExist = false;
		for (AppInfo appinfo : appInfos) {

			if (!"".equals(appinfo.getRecommImage())
					&& appinfo.getRecommImage() != null) {
				String path = "/mnt/sdcard/appinfo/"
						+ appinfo.getRecommImage().substring(
								appinfo.getRecommImage().lastIndexOf("/") + 1,
								appinfo.getRecommImage().lastIndexOf("."))
						+ appinfo.getRecomm_index();
				File f = new File(path);
				long downloadSize = recSize.get(f.getName()) == null ? -1
						: recSize.get(f.getName());

				// Logger.e("tag", "$$$---"+TAG+"--filename---"+f.getName());
				// Logger.e("tag",
				// "$$$---"+TAG+"--packagename---"+appinfo.getPackage_name());
				// Logger.e("kao", "$$$---" + TAG + "--downloadSize---"
				// + downloadSize);
				// Logger.e("tag", "$$$---"+TAG+"--filelength---"+f.length());
				// Logger.e("tag",
				// "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

				if (!f.exists() || f.length() != downloadSize) {
					isExist = true;
					break;
				} else {
					isExist = false;
				}
			}

		}

		/*
		 * for (int i = 1; i < 7; i++) { File f = new
		 * File("/sdcard/appinfo/recommend" + i); if (!f.exists()) { isExist =
		 * true; Logger.i("gww", "----isExistRecPic: run times---" + i); break;
		 * } else { isExist = false; } }
		 */
		return isExist;
	}

	// 获取首页推荐列表数据
	private void getRecommendData() throws Exception {
		appInfos = LKHomeUtil.jsonAppInfoFile(
				MainHomeActivity.instance.getRecoUrl(),
				Constant.APPSTORE_MODE_HOME_RECOMMEND);
		for (AppInfo appinfo : appInfos) {
			String path = "/mnt/sdcard/appinfo/"
					+ appinfo.getRecommImage().substring(
							appinfo.getRecommImage().lastIndexOf("/") + 1,
							appinfo.getRecommImage().lastIndexOf("."))
					+ appinfo.getRecomm_index();
			MyDownloadThreadManager mdtd = new MyDownloadThreadManager(
					getApplicationContext());
			dtmList.add(mdtd);
			File f = new File(path);
			mdtd.setFile(f);
			mdtd.setUrl(appinfo.getRecommImage());
			mdtd.setAction(Constant.ACTION_IMG_DOWNLOAD_COMPLETE);
			mdtd.setHandler(MainHomeActivity.instance.handler);
			mdtd.setRecIndex(appinfo);
			mdtd.setHandlerWhat(Constant.HANDLER_DOWNLOAD_RECOMMEND_APP);
			mdtd.startDownload();
		}
		/*
		 * for (AppInfo appinfo : appInfos) { MyDownloadThreadManager mdtd = new
		 * MyDownloadThreadManager( getApplicationContext()); dtmList.add(mdtd);
		 * String
		 * name=appinfo.getHDIcon().substring(appinfo.getHDIcon().lastIndexOf
		 * ("/")); File f = new File("/sdcard/appinfo/recIcon" +name);
		 * mdtd.setFile(f); Logger.i("gww",
		 * "----appinfo.getHDIcon()------"+appinfo.getHDIcon());
		 * mdtd.setUrl(appinfo.getHDIcon());
		 * mdtd.setAction(Constant.ACTION_IMG_DOWNLOAD_COMPLETE); //
		 * mdtd.setHandler(MainHomeActivity.instance.handler); //
		 * mdtd.setHandlerWhat(Constant.HANDLER_DOWNLOAD_RECOMMEND_APP);
		 * mdtd.startDownload(); }
		 */
	}

	private void weatherfileListener(boolean flush) {
		// 定位天气数据的缓存文件
		String cityName = sp.getString("city", Constant.DEFAULT_CITY);
		String url = sp.getString("weatherUrl", Constant.DEFAULT_WEATHER_URL);
		String path = "/sdcard/weather/" + cityName;
		File file = new File(path);
		// 以JSON的方式来解析此文件，文件有异常时，则会解析错误
		try {

			WeatherInfo w = LKHomeUtil.jsonWeatherFile(file);
			if (LKHomeUtil.dayIndex(w.getDate_y()) >= 1 || flush) {
				throw new RuntimeException();
			}

		} catch (Exception e1) {
			// 解析错误时，说明缓存文档有异常，则获取网络数据进行修复
			MyDownloadThreadManager mdtd = new MyDownloadThreadManager(
					getApplicationContext());
			dtmList.add(mdtd);
			mdtd.setFile(file);
			mdtd.setUrl(url);
			mdtd.setAction(Constant.ACTION_WEATHERDATA_DOWNLOAD_COMPLETE);
			if (MainHomeActivity.instance != null) {
				mdtd.setHandler(MainHomeActivity.instance.handler);
			}
			mdtd.setHandlerWhat(Constant.HANDLER_DOWNLOAD_WEATHER);
			mdtd.startDownload();
		}
	}

	private static final char portType = 'h';
	private static final int portNum = 21;
	private static final int OFF = 1;
	private static final int ON = 0;

	int count = 0;

	/*
	 * private void playerSound() { Gpio.writeGpio(portType, portNum, ON); final
	 * Timer timer = new Timer(); timer.schedule(new TimerTask() {
	 * 
	 * @Override public void run() { // TODO Auto-generated method stub count++;
	 * if (count == 3) { count = 0; timer.cancel(); Gpio.writeGpio(portType,
	 * portNum, OFF); } } }, 0, 500);
	 * 
	 * }
	 */

	// 进行推送消息的监听，当有推送消息到达时触发该监听器
	private ChatManagerListener listener = new ChatManagerListener() {
		@Override
		public void chatCreated(Chat chat, boolean arg1) {
			// TODO Auto-generated method stub
			chat.addMessageListener(new MessageListener() {
				@Override
				public void processMessage(Chat chat, Message msg) {
					/*
					 * Logger.i("gww", "---msg1---" + msg.getBody()); String[]
					 * strs = msg.getBody().split("\\|"); Logger.i("gww",
					 * "---msg--" + strs[0] + " " + strs[1] + " " + strs[2]);
					 * XmppMessage xmMsg = new XmppMessage();
					 * xmMsg.setTitle(strs[0]); xmMsg.setContent(strs[1]);
					 * xmMsg.setTime(new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")
					 * .format(new Date())); xmMsg.setStyle(strs[2]);
					 * xmpUtil.addNewMsg(xmMsg);
					 */
					String[] strs = msg.getBody().split("\\|");

					MyDownloadThreadManager tm = new MyDownloadThreadManager(
							getApplicationContext());
					// tm.start();
					if ("msg".equals(strs[0])) {
						XmppMessage xmsg = new XmppMessage();
						xmsg.setStyle(strs[0]);
						xmsg.setContent(strs[1]);
						xmsg.setRead(-1);
						xmsg.setMsgTime(String.valueOf(System
								.currentTimeMillis()));

						// xmsg.setVariable(strs[2]);
						xmpUtil.addNewMsg(xmsg);
					} else if ("img".equals(strs[0])) {
						File f = new File("/sdcard/image");
						if (!f.exists()) {
							f.mkdir();
						}
						Logger.e(
								TAG,
								"------strs[1].contains HTTP--"
										+ strs[1].contains("http"));
						if (!strs[1].contains("http")) {
							return;
						}
						XmppMessage xmsg = new XmppMessage();
						xmsg.setStyle(strs[0]);
						xmsg.setContent(strs[1]);
						xmsg.setRead(-1);
						xmsg.setMsgTime(String.valueOf(System
								.currentTimeMillis()));
						// xmsg.setVariable(strs[2]);
						xmpUtil.addNewMsg(xmsg);
						String imgName = strs[1].substring(strs[1]
								.lastIndexOf("/"));
						String path = "/sdcard/image/" + imgName;
						File f2 = new File(path);
						if (f2.exists()) {
							return;
						}
						tm.setFile(f2);
						tm.setUrl(strs[1]);
						// tm.setAction(Constant.ACTION_IMG_DOWNLOAD_COMPLETE);
						tm.startDownload();
					} else if ("apk".equals(strs[0])) {
						boolean isTop = LKHomeUtil.isTopActivy();
						String check = MainHomeActivity.instance
								.getCheckVersion()
								+ LKHomeUtil.getVersion(getPackageName());
						Logger.i(
								"gww3",
								"-------getVersion---"
										+ LKHomeUtil
												.getVersion(getPackageName()));
						try {
							if (lkUtil.checkVersion(check) && !isTop) {
								/*
								 * String path = "/sdcard/appinfo/" +
								 * lkUtil.getUpgradeUrl().substring(
								 * lkUtil.getUpgradeUrl() .lastIndexOf("?") +
								 * 1);
								 */
								String path = "/sdcard/appinfo/LKHome.apk";
								File f = new File(path);
								if (f.exists()) {
									f.delete();
								}
								tm.setFile(f);
								tm.setUrl(lkUtil.getUpgradeUrl());
								tm.setAction(ACTION_UPGRADE_LANUCHER);
								tm.startDownload();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// filename=strs[1].substring(0,
						// strs[1].lastIndexOf("."));
						/*
						 * filename = strs[1]; // version =
						 * Integer.parseInt(strs[2]); //
						 * if(version>localVersion){ File fileDir = new
						 * File("/sdcard/appinfo"); if (!fileDir.exists()) {
						 * fileDir.mkdir(); } Logger.i("gww",
						 * "-----lkservice is start download----"); String path
						 * = "/sdcard/appinfo/" + strs[1]; File f = new
						 * File(path); tm.setFile(f);
						 * tm.setUrl(Constant.DOWNLOADURL + strs[1]);
						 * tm.setAction(Constant.ACTION_DOWNLOAD_COMPLETE);
						 * tm.startDownload();
						 */
						// tm.run();
						// }
					} else if ("ota".equals(strs[0])) {

						//bindRemoteServiceOta();
					}
					dtmList.add(tm);
				}
			});
		}
	};
	private BroadcastReceiver installOrRemove = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			// TODO Auto-generated method stub

			String action = intent.getAction();
			// String packageName=intent.getStringExtra("packageName");
			// String currentStyle=sp.getString("flag", "");
			if (action.equals(Constant.ACTION_INSTALED)) {
				final String flag = intent.getStringExtra("apkStyle");

				try {
					ThreadPoolUtil.execute(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							long bef = System.currentTimeMillis();
							querryAppData(false, flag);
							Intent i = new Intent();
							i.setAction("com.lenkeng.newdata");
							i.putExtra("apkStyle", flag);
							i.putExtra("installFlag",
									intent.getStringExtra("installFlag"));
							sendBroadcast(i);
							long aft = System.currentTimeMillis();
							Logger.d("ww", "$$$-----installOrRemove---"
									+ (aft - bef));
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// handler.sendEmptyMessage(Constant.HANDLER_INSTALL_COMPLETE);
			} else if (Constant.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

				upgradeFlag = 0;
				// update();

			} else if (ACTION_UPGRADE_LANUCHER.equals(action)) {

				/*
				 * String path = "/sdcard/appinfo/" +
				 * lkUtil.getUpgradeUrl().substring(
				 * lkUtil.getUpgradeUrl().lastIndexOf("/") + 1); Logger.i("gww",
				 * "------------launcher upgrade-----------" + path); boolean
				 * isTop = LKHomeUtil.isTopActivy(); Logger.i("gww3",
				 * "-------isTop---" + isTop); //if (!isTop) { new
				 * SilentInstall(getApplicationContext(),
				 * path).installPackage(); //}
				 */
				boolean isTop = LKHomeUtil.isTopActivy();
				// if (!isTop) {
				bindRemoteService();
				// }
			}

			try {
				for (MyDownloadThreadManager tm : dtmList) {
					tm = null;
				}
				dtmList.clear();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	};

	private void bindRemoteServiceOta() {
		/*
		 * final String path = "/sdcard/appinfo/" +
		 * lkUtil.getUpgradeUrl().substring(
		 * lkUtil.getUpgradeUrl().lastIndexOf("/") + 1);
		 */

		ServiceConnection serConn = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				lkInterface = LKHomeInterface.Stub.asInterface(service);
				try {
					lkInterface.checkOta();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		bindService(new Intent(LKHomeInterface.class.getName()), serConn,
				Context.BIND_AUTO_CREATE);
	}

	private void bindRemoteService() {
		/*
		 * final String path = "/sdcard/appinfo/" +
		 * lkUtil.getUpgradeUrl().substring(
		 * lkUtil.getUpgradeUrl().lastIndexOf("/") + 1);
		 */
		final String path = "/sdcard/appinfo/LKHome.apk";

		ServiceConnection serConn = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				lkInterface = LKHomeInterface.Stub.asInterface(service);
				try {
					lkInterface.install(path);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		bindService(new Intent(LKHomeInterface.class.getName()), serConn,
				Context.BIND_AUTO_CREATE);
	}

	private void update() {

		String path = "/sdcard/appinfo/" + filename;
		// String path = "/sdcard/appinfo/"
		// + LKHomeUtil.getUpgradeUrl().substring(
		// LKHomeUtil.getUpgradeUrl().lastIndexOf("/") + 1);
		int installVersion = 0;
		String packageName = lkUtil.getPackageName(path);

		// int installVersion=appDao.getVersion(filename);
		if (LKHomeUtil.isInstalled(packageName)) {
			installVersion = LKHomeUtil.getVersion(packageName);
		}
		int updateVersion = LKHomeUtil.getVersionByfile(path);
		// tm.run();
		// new SilentInstall(getApplicationContext(),
		// path).installPackage();

		if (updateVersion > installVersion) {
			new SilentInstall(getApplicationContext(), path).installPackage();
		} else {
			new File(path).delete();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.like:
			btn_like.setEnabled(false);
			msgHandler.sendEmptyMessage(OPERATE_LIKE);
			break;
		case R.id.hate:
			btn_hate.setEnabled(false);
			msgHandler.sendEmptyMessage(OPERATE_HATE);
			break;

		default:
			break;
		}

	}

	protected void parise() {

		((TextView) detail.findViewById(R.id.mess)).setText(R.string.ticketing);

		new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("packagename", ticket_package);
				param.put("ticket", "0");
				boolean flag = mLogic.setTicket(param);

				// Log.e(TAG, "line 1203 flag="+flag);
				if (flag) {

					parise++;

					msgHandler.post(new Runnable() {

						@Override
						public void run() {
							((TextView) detail.findViewById(R.id.praise)).setText(String
									.valueOf(info.getPraise() + 1));
							detail.findViewById(R.id.mess).setVisibility(
									View.GONE);
						}
					});
					editor.putInt(ticket_package + "_parise", parise);
					editor.commit();

					// Log.e(TAG,
					// "====line 1219 key="+ticket_package+"_parise"+",value="+parise);
					// Log.e(TAG,
					// "====line 1220,读取配置: value="+ticket.getInt(ticket_package
					// + "_parise", -1));

					info.setPraise(info.getPraise() + 1);
					appStoreDao.UpdateApp(info, true);
				}

				msgHandler.sendEmptyMessage(ENABLE_LIKE);
			}
		}).start();

	}

	protected void reject() {
		((TextView) detail.findViewById(R.id.mess)).setText(R.string.ticketing);
		new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("packagename", ticket_package);
				param.put("ticket", "1");
				boolean flag = mLogic.setTicket(param);

				if (flag) {
					reject++;
					msgHandler.post(new Runnable() {

						@Override
						public void run() {
							((TextView) detail.findViewById(R.id.reject)).setText(String
									.valueOf(info.getReject() + 1));
							detail.findViewById(R.id.mess).setVisibility(
									View.GONE);
						}
					});
					editor.putInt(ticket_package + "_reject", reject);
					editor.commit();
					info.setReject(info.getReject() + 1);
					appStoreDao.UpdateApp(info, false);

				}

				msgHandler.sendEmptyMessage(ENABLE_HATE);
			}
		}).start();
	}

	@Override
	public void onFocusChange(View v, boolean arg1) {
		if (arg1) {
			switch (v.getId()) {
			case R.id.like:
			case R.id.hate:
				msgHandler.removeCallbacks(removeRun);
				msgHandler.postDelayed(removeRun, 5000);
				break;

			default:
				break;
			}
		}
	}

	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
