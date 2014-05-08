package com.lenkeng.appmarket;

import java.io.File;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.db.DownloadBean;
import lenkeng.com.welcome.db.DownloadDao;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.util.SPUtil;
import lenkeng.com.welcome.util.SilentInstall;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lenkeng.adapter.GalleryAdapter;
import com.lenkeng.api.SilentInstallListener;
import com.lenkeng.bean.ApkBean;
import com.lenkeng.bean.ImplInter;
import com.lenkeng.bean.InterfaceStub;
import com.lenkeng.bean.Screen;
import com.lenkeng.bean.URLs;
import com.lenkeng.logic.Logic;
import com.lenkeng.service.MarketService;
import com.lenkeng.tools.Constants;
import com.lenkeng.tools.ThreadPoolUtil;


public class DetailActivity extends Activity implements OnClickListener,
		 SilentInstallListener {
	private static final String TAG = "DetailActivity";
	public static String ACTION_PROGRESS="com.lenkeng.progress";
	public static String ACTION_DOWNLOAD_ERR="com.lenkeng.downloaderr";
	public static String ACTION_DOWNLOAD_COMPLETE="com.lenkeng.download.complete";
	private static final String ACTION_INSTALLING = "com.lenkeng.installing";
	private static final String ACTION_INSTALL_ERR = "com.lenkeng.install.err";

	public static boolean isInstall=false;
	
	private Boolean mExist;
    private DownloadDao downloadDao;
	private DownloadBean downloadBean;
	private AppInfo mApp;
	private Button btn_run,btn_del,btn_back,btn_oneKey;
	private ImageView iv_icon,iv_thumb1,iv_thumb2,iv_thumb3;
	private TextView tv_title, tv_operateType,tv_summary,tv_size,tv_progress;
	private HorizontalScrollView scroll;
	
	@SuppressWarnings("deprecation")
	private Logic mLogic;

	boolean downloading;
	private static Map<String, Long> mCurrentMap = new HashMap<String, Long>();
	public static ArrayList<String> installList=new ArrayList<String>();
	public static String currentInstallPkg=""; //正在安装的apk
	public static String currentInstallName=""; //正在安装的apk名字
	
	private static String currentApkFile;
	private static boolean isInstalling=false;

	private BroadcastReceiver mHideInstallReceiver, mSystemInstallReceiver,mProRec;
	private Intent ini;
	private long progress_degree=0;
    private  String currentUrl;
    private String apkFileName;
    private String currentProgressKey;
	private InterfaceStub mInterStub;
    private Context mContext;
    
    
	private Handler mhandler = new Handler() {
		String msgStr="";
		
		
		public void handleMessage(android.os.Message msg) {
			final ApkBean bean = (ApkBean) msg.obj;

			String tUrl=mApp.getUrl().substring(bean.getUrl().lastIndexOf("/") + 1);
		    String tKey=tUrl.substring(tUrl.lastIndexOf("=") + 1, tUrl.length())+ ".apk";
		    
			switch (bean.getStatus()) {
			case ApkBean.STATE_START:  //开始下载
				
				msgStr=getString(R.string.text_download_start);
				//LKHomeUtil.showToast(mContext, msgStr);
				break;
				
			case ApkBean.STATE_DOWNLOADING: //正在下载
				
				msgStr=bean.getName() + getString(R.string.text_downloading);
				LKHomeUtil.showToast(mContext, msgStr);
				break;
				
			case ApkBean.STATE_COMPLETE: //下载完成
				Log.e(TAG, "------收到下载完成Meassage....");
				
				mCurrentMap.remove(tKey);
				
				//====edit by xgh
				msgStr=bean.getName() + getString(R.string.text_download_complete);
				LKHomeUtil.showToast(mContext, msgStr);
				systemInstall();
				//====end
				
				break;
				
			case ApkBean.STATE_ERR: //下载错误
			
				mCurrentMap.remove(tKey);
				
				msgStr=bean.getName()+ getString(R.string.text_download_error);
				LKHomeUtil.showToast(mContext, msgStr);
				break;
				
			case ApkBean.STATE_PROGRESS: //更新进度
				break;
				
			case ApkBean.STATE_QUEUE_ENOUNGH: //下载任务已满
				
				LKHomeUtil.showToast(mContext, R.string.text_download_full);
				tv_progress.setVisibility(View.GONE);
				break;
				
			case ApkBean.STATE_ERR_NO_SPACE: //空间不够
				
				LKHomeUtil.showToast(mContext, R.string.download_no_size);
				tv_progress.setVisibility(View.GONE);
				
				
				break;
			default:
				break;
			}
		}





	};
	
	
	private void startInstall(final ApkBean bean) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				silentInstall(bean);
			}
		}).start();
		
		
		
	}
	
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		mContext=this;
		initWidget();
		mLogic = Logic.getInstance(getApplicationContext());
		downloadDao=mLogic.getDownloadDao();
		bindService();


		initApkHideInstallReceiver();
		//initApkSystemInstallReceiver();
		initDownLoadProgressReceiver();
		
		mApp = (AppInfo) getIntent().getExtras().get("appinfo");
		downloadBean=downloadDao.findDownloadBeanByPackageName(mApp.getPackage_name());
	}
	
	
	
	
	@Override
	protected void onResume() {
		
		mApp = (AppInfo) getIntent().getExtras().get("appinfo");
		
		
		if (mApp != null) {
		currentUrl=mApp.getUrl().substring(mApp.getUrl().lastIndexOf("/") + 1);
		currentProgressKey=currentUrl.substring(currentUrl.lastIndexOf("=") + 1, currentUrl.length())+ ".apk";
		

			tv_title.setText(mApp.getName());
			tv_summary.setText(mApp.getSummary());
			int operateType=mApp.getOperateType();
			if(operateType==AppInfo.OPERATE_TYPE_ALL){
				
				tv_operateType.setText(getString(R.string.operate_type)+getString(R.string.operate_type_all));
			}else if(operateType==AppInfo.OPERATE_TYPE_IR){
				tv_operateType.setText(getString(R.string.operate_type)+getString(R.string.operate_type_ir));
				
			}else if(operateType==AppInfo.OPERATE_TYPE_MOUSE){
				tv_operateType.setText(getString(R.string.operate_type)+getString(R.string.operate_type_mouse));
				
			}else if(operateType==AppInfo.OPERATE_TYPE_GAME_HANDLE){
				tv_operateType.setText(getString(R.string.operate_type)+getString(R.string.operate_type_game_handle));
				
			}
			
			
			
			long size = mApp.getSize();
			DecimalFormat df = new DecimalFormat("0.00");
			tv_size.setText(getString(R.string.text_size)
					+ df.format((float) size / 1024) + getString(R.string.m));
			
			String hdUrl = mApp.getHDIcon();

			if (hdUrl != null && hdUrl.contains("http")) {
				mApp.setHDIcon("/icon/"
						+ hdUrl.substring(hdUrl.lastIndexOf("/") + 1,
								hdUrl.length()));
			}
			
			String apkUrl = mApp.getUrl();
			if (apkUrl != null && apkUrl.contains("http")) {
				mApp.setUrl("/android/"
						+ apkUrl.substring(apkUrl.lastIndexOf("/") + 1,
								apkUrl.length()));
			}

			String iconUrl = mApp.getIcon();
			mLogic.asView(iconUrl, iv_icon, mhandler);
			
			mLogic.downLoadImg(mApp.getHDIcon());
			
			List<Screen> screens=mApp.getImgs();
			if(screens.size()>=3){
				
				String imgUrl1=screens.get(0).getUrl();
				mLogic.asView(imgUrl1, iv_thumb1, mhandler);
				
				String imgUrl2=screens.get(1).getUrl();
				mLogic.asView(imgUrl2, iv_thumb2, mhandler);
				
				String imgUrl3=screens.get(2).getUrl();
				mLogic.asView(imgUrl3, iv_thumb3, mhandler);
				
				
			}
			
			
			checkApkState();
			
			if(mLogic.isContainUrl(currentUrl)  && mCurrentMap.containsKey(currentProgressKey) ){ //当前apk正在下载
				
				Long progress=mCurrentMap.get(currentProgressKey);
				String s = String.format(getString(R.string.text_degree),progress);
				
				tv_progress.setText(s);
				tv_progress.setVisibility(View.VISIBLE);
				
				//btn_oneKey.setBackgroundResource(R.drawable.detail_left_enable);
				btn_oneKey.setClickable(true);
			}else if(currentInstallPkg.equals(mApp.getPackage_name())){//正在安装
				
				tv_progress.setText(R.string.installing);
				tv_progress.setVisibility(View.VISIBLE);
				//btn_oneKey.setBackgroundResource(R.drawable.detail_left_enable);
				btn_oneKey.setClickable(false);
				
			}else if( checkApkFileState(downloadBean)){ //等待安装,英文版需要手动安装,one_key可以点击
				  
				tv_progress.setVisibility(View.GONE);
				//btn_oneKey.setBackgroundResource(R.drawable.detail_left_selector);
				btn_oneKey.setText(R.string.text_install);
				btn_oneKey.setClickable(true);
				
				
			}
			
		}

		
		super.onResume();
	}

	private boolean checkApkFileState(DownloadBean downloadBean) {
		if(downloadBean==null ){
			return false;
		}
		if(downloadBean.getState()!=Constant.APK_STATE_DOWNLOAD_COMPLETE){
			return false;
		}
		
		String md5=downloadBean.getMd5();
		String file=downloadBean.getSavePath();
			
		File checkFile=new File(file);
		if(checkFile==null ||! checkFile.exists()){
			return false;
		}
		
		return true;
	}
	

	/**
	 * 隐式安装监听器
	 */
	private void initApkHideInstallReceiver() {
		
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(Constant.ACTION_INSTALED);
		mHideInstallReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String flag=intent.getStringExtra("installFlag");
				final String packageName=intent.getStringExtra("packageName");
				if("install".equals(flag)){
					isInstalling=false;
					Logger.e(TAG, "-----隐式安装apk完成.intent="+intent.getExtras()+",action="+intent.getAction());
					
					String msg=LKHomeUtil.getLabel(packageName)+mContext.getString(R.string.text_insall_ok);
					LKHomeUtil.showToast(mContext, msg);
				}else if("uninstall".equals(flag)){
					
					Logger.e(TAG, "================卸载app成功,package="+packageName+",mapp.pkg="+mApp.getPackage_name());
					
					if(packageName.equals(mApp.getPackage_name())){
						btn_oneKey.setText(R.string.text_download);
					}
					
				}
				
				if(packageName.equals(mApp.getPackage_name())){
					
					checkApkState();
				}
				
			}
		};
		this.registerReceiver(mHideInstallReceiver, tFilter);
	}

	/**
	 * 系统显示安装监听器
	 */
/*     private void initApkSystemInstallReceiver() {
		
		IntentFilter tFilter = new IntentFilter();
		tFilter.addDataScheme("package");
		tFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		tFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mSystemInstallReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
					checkApkState();
				
			}
		};
		this.registerReceiver(mSystemInstallReceiver, tFilter);
	}*/
	
	
	
	
	private void initDownLoadProgressReceiver() {
		
		
		IntentFilter tFilter2 = new IntentFilter();
		tFilter2.addAction(ACTION_PROGRESS);
		tFilter2.addAction(ACTION_DOWNLOAD_ERR);
		tFilter2.addAction(ACTION_DOWNLOAD_COMPLETE);
		tFilter2.addAction(ACTION_INSTALLING);
		tFilter2.addAction(ACTION_INSTALL_ERR);
		
		mProRec = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String action=arg1.getAction();
				String pkg=arg1.getStringExtra("packageName");
				
				if(ACTION_PROGRESS.equals(action)){ //更新下载进度
					if(mApp.getPackage_name().equals(pkg)){
						
						progress_degree = arg1.getLongExtra("prorgess", 0);
						tv_progress.setVisibility(View.VISIBLE);
						String s = String.format(getString(R.string.text_degree),progress_degree);
						tv_progress.setText(s); 
						
						btn_oneKey.setClickable(true);
						//btn_oneKey.setBackgroundResource(R.drawable.detail_left_enable);
						
					}
				}else if(ACTION_DOWNLOAD_ERR.equals(action)){//下载错误,需要从广播才能更新UI
					if(mApp.getPackage_name().equals(pkg)){
						tv_progress.setVisibility(View.GONE);
						
						btn_oneKey.setClickable(true);
						//btn_oneKey.setBackgroundResource(R.drawable.detail_left_selector);
						
					}
					
				}else if(ACTION_DOWNLOAD_COMPLETE.equals(action)){ //更新为"安装",one_key可以点击
					
					Logger.e(TAG, "@@@@@@@@@@@@收到下载完成广播, mapp.pkg="+mApp.getPackage_name()+",pkg="+pkg);
					if(mApp.getPackage_name().equals(pkg)){ //当前显示的app下载完成
						
						//downloadBean=mLogic.getDownloadBeanByPkg(pkg);
						
						tv_progress.setVisibility(View.GONE);
						btn_oneKey.setClickable(true);
						btn_oneKey.setText(R.string.text_install);
						//btn_oneKey.setBackgroundResource(R.drawable.detail_left_selector);
						
					}
				}else if(ACTION_INSTALLING.equals(action)){ //正在安装
					if(mApp.getPackage_name().equals(pkg)){
						tv_progress.setVisibility(View.VISIBLE);
						btn_oneKey.setClickable(false);
						tv_progress.setText(R.string.installing);
						//btn_oneKey.setBackgroundResource(R.drawable.detail_left_enable);
					}
					
				}else if(ACTION_INSTALL_ERR.equals(action)){ //安装失败
					
					if(mApp.getPackage_name().equals(pkg)){
						
						tv_progress.setVisibility(View.GONE);
						btn_oneKey.setClickable(true);
						//btn_oneKey.setBackgroundResource(R.drawable.detail_left_selector);
					}
					
					
				}
				
			}

		};
		this.registerReceiver(mProRec, tFilter2);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onPause() {
		//Logger.e("kao", "11111111111 onPause()");
		super.onPause();
	}

	private  void checkApkState() {
		 btn_back.setClickable(true);
		 btn_oneKey.setClickable(true);
		String package_name = mApp.getPackage_name();
		boolean tExist =checkApkExist(this, package_name);
		Log.e(TAG, "line 352 package_name="+package_name+",exit="+tExist);
		if (tExist) {
			btn_oneKey.setVisibility(View.GONE);
			btn_run.setVisibility(View.VISIBLE);
			btn_run.requestFocus();
			btn_del.setVisibility(View.VISIBLE);
			tv_progress.setVisibility(View.GONE);
		} else {
			btn_oneKey.setVisibility(View.VISIBLE);
			btn_oneKey.setClickable(true);
			//btn_oneKey.setBackgroundResource(R.drawable.detail_left_selector);
			//ui_oneKey.requestFocus();
			
			btn_run.setVisibility(View.GONE);
			btn_del.setVisibility(View.GONE);
			tv_progress.setVisibility(View.GONE);
		}
		
	}

	private void initWidget() {

		btn_run = (Button) findViewById(R.id.btn_run);
		btn_del = (Button) findViewById(R.id.btn_del);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_oneKey = (Button) findViewById(R.id.btn_onekey);
		iv_icon = (ImageView) findViewById(R.id.imageView2);
		tv_title = (TextView) findViewById(R.id.textView1);
		tv_size = (TextView) findViewById(R.id.size);
		tv_progress = (TextView) findViewById(R.id.progress);
		tv_summary = (TextView) findViewById(R.id.textView2);
		btn_run.setOnClickListener(this);
		btn_del.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_oneKey.setOnClickListener(this);
		iv_thumb1=(ImageView) findViewById(R.id.iv_thumb1);
		iv_thumb2=(ImageView) findViewById(R.id.iv_thumb2);
		iv_thumb3=(ImageView) findViewById(R.id.iv_thumb3);
		
		scroll=(HorizontalScrollView) findViewById(R.id.scroll);
		scroll.setHorizontalFadingEdgeEnabled(false);
		
		tv_operateType=(TextView)findViewById(R.id.tv_operateType);
		
	}

	
	
	public boolean checkApkExist(Context context, String packageName) {
		return LKHomeUtil.isInstalled(context,packageName);
		
	}

	public void startDownLoad() throws RemoteException {

		
		if (mApp != null) {
			
			ApkBean apkBean = this.mApp.buildApkBean();
			//Log.e(TAG, "line 354 apkBean="+apkBean +",-------mApp="+mApp);
			
			if(apkBean.getStatus()!=apkBean.STATE_QUEUE_ENOUNGH){
				
				String tUrl=apkBean.getUrl().substring(apkBean.getUrl().lastIndexOf("/") + 1);
				String key=tUrl.substring(tUrl.lastIndexOf("=") + 1)+ ".apk";
				
				if(mCurrentMap.get(key)==null && mLogic.getDownloadSize()<Logic.DownLoadSize){
					mCurrentMap.put(key, 0L);
					String s = String.format(getString(R.string.text_degree),0L);
					tv_progress.setText(s); 
				}
				mInterStub.downLoad(apkBean);
				
			}
		    
		}
	}

	private void bindService() {
		bindService(new Intent(this, MarketService.class), serviceConnection,
				Context.BIND_AUTO_CREATE);
		startService(new Intent(this, MarketService.class));
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mInterStub = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mInterStub = InterfaceStub.Stub.asInterface(service);
			try {
				mInterStub.setListener(new ImplInterface());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	public class ImplInterface extends ImplInter.Stub {

		@Override
		public void downloadStatus(ApkBean bean) throws RemoteException { //需要发送广播才能更新UI
			
			
		  if(bean.getStatus()==ApkBean.STATE_ERR){ //下载错误
			  
			  Intent it=new Intent(ACTION_DOWNLOAD_ERR);
			  it.putExtra("url", bean.getUrl());
			  it.putExtra("packageName", bean.getPackageName());
			  sendBroadcast(it);
			  
		  }else if(bean.getStatus()==ApkBean.STATE_COMPLETE){ //下载完成
			  
			  Intent it=new Intent(ACTION_DOWNLOAD_COMPLETE);
			  it.putExtra("url", bean.getUrl());
			  it.putExtra("packageName", bean.getPackageName());
			  sendBroadcast(it);
			  
		  }
			  
			  Message msg = new Message();
			  msg.obj = bean;
			  mhandler.sendMessage(msg);
		  
		}

		@Override
		public void setProgress(ApkBean bean) throws RemoteException {
			mCurrentMap.put(bean.getUrl(), bean.getProgress());
	
			Intent intent=new Intent(ACTION_PROGRESS);
			intent.putExtra("url", bean.getUrl()); 
			intent.putExtra("packageName", bean.getPackageName());
			intent.putExtra("prorgess", bean.getProgress());
			
			//发送广播更新UI
			sendBroadcast(intent);  
			
			
			//发送msg更新数据库记录
			  Message msg = new Message();
			  msg.obj = bean;
			  mhandler.sendMessage(msg);
			
		}

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(mHideInstallReceiver);
		//this.unregisterReceiver(mSystemInstallReceiver);
		this.unregisterReceiver(mProRec);
		this.unbindService(serviceConnection);
		Logger.e("kao", "------onDestroy----"+this);
	}

	public void uninstallAPK(String packageName) {
		/*Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri);
		startActivity(intent);*/
		Uri packageURI = Uri.parse("package:"+packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageURI);
        uninstallIntent.putExtra(Intent.EXTRA_UNINSTALL_ALL_USERS, true);
        startActivity(uninstallIntent);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_run:
			// run
			try {
				this.startActivity(this.getPackageManager()
						.getLaunchIntentForPackage(mApp.getPackage_name()));
			} catch (Exception e) {
				Toast.makeText(this, getText(R.string.text_program_not_ready),
						3).show();
				view.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_del:
			// del
			try {
				uninstallAPK(mApp.getPackage_name());
			} catch (Exception e) {
			}
			break;
		case R.id.btn_back:
			// back
			this.finish();
			break;
		case R.id.btn_onekey:
			// one key
			int downloadsize=mLogic.getDownloadSize();
			String tUrl = mApp.getUrl().substring(mApp.getUrl().lastIndexOf("/") + 1, mApp.getUrl().length());
			Logger.e(TAG, "~~~~~~~~~下载中的任务数="+downloadsize);
			
			//btn_oneKey.setClickable(false);
				try {
			   if(((Button)view).getText().equals(getString(R.string.text_install))){//安装
				   
				  systemInstall();
				  btn_back.setClickable(false);
				  btn_oneKey.setClickable(false);
			   }
			   else if(currentInstallPkg.equals(mApp.getPackage_name())){ //正在安装
					
					LKHomeUtil.showToast(mContext, R.string.installing);
				}else if(installList.contains(mApp.getPackage_name())){//等待安装
					
					LKHomeUtil.showToast(mContext, R.string.wait_install);
					
				}else if(mLogic.isContainUrl(tUrl)){//正在下载
					String msgStr="\""+mApp.getName() +" \""+ getString(R.string.text_downloading);
					//String msgStr=mApp.getName() + getString(R.string.text_downloading);
					LKHomeUtil.showToast(mContext, msgStr);
				}else if(downloadsize>= Logic.DownLoadSize){ //下载任务数达到了5个
					
					LKHomeUtil.showToast(mContext, R.string.text_download_full);
					
				}/*else if(!checkApkFile(parseApkFileNameFromUrl(mApp.getUrl()), mApp.getMd5()) && !hasEnoughSize()){ //存储空间不够,且需要下载
				 
					LKHomeUtil.showToast(mContext, R.string.download_no_size);
				}*/else{
					tv_progress.setVisibility(View.VISIBLE);
					startDownLoad();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

	}


/*	private boolean hasEnoughSize() {g
		
		
		StatFs statfs = new StatFs("/mnt/sdcard");
        long blockSize   = statfs.getBlockSize();
        long availBlocks = statfs.getAvailableBlocks();
        long availsize   = blockSize * availBlocks;
       
        Logger.e(TAG, "====== 可用内存="+availsize+", apk大小="+mApp.getSize()*1024);
        
        if(mApp.getSize()*1024 * 1.1 >=availsize ){
        	
        	return false;
        }else{
        	return true;
        }
	}*/



	
	
	
	/*public   void systemInstallApk(final Context context, final String fileName) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				systemInstall(context, fileName);
				
			}
		}).start();
	}*/
	
	public   void systemInstall( ){
		
		ThreadPoolUtil.execute(new Runnable() {
			
			@Override
			public void run() {
			    DownloadBean	dlbean=downloadDao.findDownloadBeanByPackageName(mApp.getPackage_name());
				Logger.e(TAG, "=================a开始安装, dlbean="+dlbean+",pkg="+mApp.getPackage_name());
				String fileName=dlbean.getSavePath();
				Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file://" + fileName),
						"application/vnd.android.package-archive");
				intent.putExtra("filename", fileName);
				mContext.startActivity(intent);
			}
		});
	}

	/**
	 * 国内版寂寞安装
	 */
	public   void  silentInstall(final ApkBean bean) { 
		
		synchronized (Logic.insLock) {
			
			currentInstallPkg=bean.getPackageName();
			currentInstallName=bean.getName();
			
			SPUtil.putInstallRecord(mContext, currentInstallPkg);
			
			
			Logger.e(TAG, "&&&&&&&&&&&拥有安装锁,安装列表="+installList);
			
			Intent it=new Intent(ACTION_INSTALLING);
			it.putExtra("packageName", bean.getPackageName());
			mContext.sendBroadcast(it);
			
			mhandler.post(new Runnable() { //不能放到广播中,需要在后台安装时弹出提示
				
				@Override
				public void run() {
					
					String msgStr=bean.getName()+ getString(R.string.text_start_insall);
					LKHomeUtil.showToast(mContext, msgStr);
					
				}
			});
			
			if(!isInstall){
				isInstall=true;
			}
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					SilentInstall silent = new SilentInstall(DetailActivity.this,
							bean.getSavePath(), DetailActivity.this);
					silent.installPackage();
					
					
				};
			}).start();
			
			
			while(isInstall){
				try {
					Thread.currentThread().sleep(500);
					Logger.e(TAG, "~~~~~~~~~是否在安装: install="+isInstall);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Logger.e(TAG, "&&&&&&&&&&&&&释放安装锁");
		}
		
	}

	
	@Override
	public void onSilentInstallComplete(String packageName,String filePath) {//TODO...
		
		File apk=new File(filePath);
		if(apk!=null && apk.exists()){  //TODO...注释掉了删除apk
			apk.delete();
		}
		SPUtil.putInstallRecord(mContext, "");
		
		installList.remove(packageName);
		
		
		isInstall=false;
		currentInstallPkg="";
		currentInstallName="";
		Logger.e(TAG, "=====line 537  收到静默安装成功>......packageName="+packageName+",安装列表="+installList);
	}
	
	@Override
	public void onSlientInstallFail(String packageName,int returnCode) {//安装失败时收不到packageName
		installList.remove(currentInstallPkg);
		
		SPUtil.putInstallRecord(mContext, "");
		
		Intent it=new Intent(ACTION_INSTALL_ERR);
		it.putExtra("packageName", currentInstallPkg);
		mContext.sendBroadcast(it);
		
		if(returnCode == -4 || returnCode == -18){
			String msg="\""+currentInstallName+"\" "+mContext.getString(R.string.text_insall_err);
			LKHomeUtil.showToast(mContext, msg);
		}else{
			String msg="\""+currentInstallName+"\" "+mContext.getString(R.string.install_fail);
			LKHomeUtil.showToast(mContext, msg);
		}
		
		isInstall=false;
		currentInstallPkg="";
		currentInstallName="";
		
		Logger.e(TAG, "=====line 618  收到静默安装失败>......packageName="+packageName+",安装列表="+installList+"===  returnCode  = "+returnCode);
		
	}
	
	
	

/*	public void installByCMD(String filePath){
		
		Process install; 
		try {
			install = Runtime.getRuntime().exec("/system/bin/pm install  " + filePath);
			int iSuccess = install.waitFor();
			Log.e(TAG, "---------命令安装, result="+iSuccess);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}*/

	
/*	public static void InstallAPK(String filename){
	    File file = new File(filename); 
	    if(file.exists()){
	        try {   
	            String command;
	            //filename = StringUtil.insertEscape(filename);
	            command = "adb install -r " + filename;
	            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
	            proc.waitFor();
	        } catch (Exception e) {
	        e.printStackTrace();
	        }
	     }
	  }*/

	
	
}
