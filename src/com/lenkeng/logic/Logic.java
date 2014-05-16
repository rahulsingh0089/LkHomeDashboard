package com.lenkeng.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.db.DownloadBean;
import lenkeng.com.welcome.db.DownloadDao;
import lenkeng.com.welcome.db.AppStoreDao;

import lenkeng.com.welcome.server.LKService;
import lenkeng.com.welcome.upload.HttpException;
import lenkeng.com.welcome.upload.HttpRequest.HttpMethod;
import lenkeng.com.welcome.upload.HttpUtils;
import lenkeng.com.welcome.upload.RequestCallBack;
import lenkeng.com.welcome.upload.RequestParams;
import lenkeng.com.welcome.upload.ResponseInfo;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeCache;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Entity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.LkecDevice;
import android.os.Bundle;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lenkeng.api.ApiClient;
import com.lenkeng.bean.ApkBean;
import com.lenkeng.bean.DataEntity;
import com.lenkeng.bean.ImplInter;
import com.lenkeng.bean.ParamRunnable;
import com.lenkeng.bean.URLs;
import com.lenkeng.tools.Constants;
import com.lenkeng.tools.ThreadPoolUtil;
import com.lenkeng.tools.UploadThreadPoolUtil;
import com.lenkeng.tools.Util;

public class Logic {
	private ApiClient client;
	private Context context;
	private Handler mHandler;
	public static final int DownLoadSize = 5;
	public static Object bitLock = new Object();
	public static Object insLock = new Object();
	private static Object downloadLock=new Object();
	private DownloadDao downloadDao;
	private AppStoreDao mAppStoreDao;
	
	public static final int RESULT_CHECK_UPLOAD=10001;
	public static final int RESULT_UPLOAD_COMPLETE = 10002;
	protected static final int RESULT_START_UPLOAD = 10003;
	
	/**
	 * 正在下载的集合,存放下载apk的名字
	 */
	private static final List<String> downloadMaps = new ArrayList<String>();
	
	/**
	 * 正在检测上传状态的集合,存放包名
	 */
	private static final List<String> checkUploadList = new ArrayList<String>();
	
	/**
	 * 等待上传的集合,存放包名
	 */
	private static final List<String> waitUploadList = new ArrayList<String>();
	
	/**
	 * 正在上传的集合,存放包名
	 */
	private static final List<String> uploadList = new ArrayList<String>();
	
	private static final String TAG = "Logic";

	private static Logic instance;
	public static synchronized Logic getInstance(Context context){
		if(instance==null){
			instance=new Logic(context);
		}
		return instance;
	}
	

	private Logic(Context con) {
		this.context = con;
		//=====edit by xgh
		downloadDao=new DownloadDao(con);
        client = new ApiClient(downloadDao);
		//====end
		
		LKHomeCache.initCache();
		mAppStoreDao=AppStoreDao.getInstance(con);
	mHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RESULT_CHECK_UPLOAD: //检测上传请求完成
					Bundle data=msg.getData();
					String name=data.getString("name");
					String pkg=data.getString("pkg");
					
					removeCheckUploadRecord(pkg);
					
					String result=(String) msg.obj;
					if("1".equals(result)){ //同包名的apk已经存在
						String str= "\""+name+"\""+context.getString(R.string.apk_exist);
						LKHomeUtil.showToast(context, str);
						
					}else if("0".equals(result)){ //等待上传
						String str= "\""+name+"\""+context.getString(R.string.wait_upload);
						LKHomeUtil.showToast(context, str);
						performUpload(pkg, name);
					}else { //连接服务器异常
						LKHomeUtil.showToast(context, context.getString(R.string.server_error));
						
					}
					
					
					break;

				case RESULT_START_UPLOAD: //开始上传
					
					 data=msg.getData();
					 name=data.getString("name");
					 pkg=data.getString("pkg");
					
					 removeWaitUploadRecord(pkg);
					 addUploadRecord(pkg);
					 
					 String str= "\""+name+"\""+context.getString(R.string.start_upload);
					 LKHomeUtil.showToast(context, str);
					
					break;		
					
				case RESULT_UPLOAD_COMPLETE:  //上传完成
					
					 data=msg.getData();
					 name=data.getString("name");
					 pkg=data.getString("pkg");
					
					 removeWaitUploadRecord(pkg);
					 removeUploadRecord(pkg);
					 
					 result=(String) msg.obj;
					if("1".equals(result)){  //上传成功
						 str= "\""+name+"\""+context.getString(R.string.upload_success);
						LKHomeUtil.showToast(context, str);
					}else if("2".equals(result)){ //没有找到apk文件
						
						 str= "\""+name+"\""+context.getString(R.string.upload_error_no_file);
						LKHomeUtil.showToast(context, str);
						
					}else{
						// str= "\""+name+"\""+"上传失败,请稍后再试";
						LKHomeUtil.showToast(context, context.getString(R.string.server_error));
					}
					
					
					break;	
				
					
				default:
					break;
				}
				
				
			}
			
		};
	}

	public Context getContext() {
		return this.context;
		
	}

	/*private Logic(ApiClient cli) {
		this.client = cli;
		LKHomeCache.initCache();
		
	}*/

	private void getSpectDataWithParam(Handler handler, String url,
			HashMap<String, String> param) {

		ThreadPoolUtil.execute(new ParamRunnable(url, handler, param) {

			@Override
			public void run() {
				DataEntity lives = null;
			
				try {
					lives = client.getDataEntityWithParam(getStr(), mParam);
					Message msg = new Message();
					msg.what = 0;// �ɹ�
					msg.obj = lives;
					getHandler().sendMessage(msg);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (lives == null) {
					Message fail = new Message();
					fail.what = 1;// ʧ��
					getHandler().sendMessage(fail);
				}
			}
		});

	}

	private void getSpectData(Handler handler, String url) {

		ThreadPoolUtil.execute(new ParamRunnable(url, handler) {

			@Override
			public void run() {
				DataEntity lives = null;
				try {
					lives = client.getDataEntity(getStr());
					Message msg = new Message();
					msg.what = 0;// �ɹ�
					msg.obj = lives;
					getHandler().sendMessage(msg);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (lives == null) {
					Message fail = new Message();
					fail.what = 1;// ʧ��
					getHandler().sendMessage(fail);
				}
			}
		});
	}

	@SuppressLint("NewApi")
	private Bitmap decodeFromFile(ImageView imageView, File absoluteFile,
			String url) {

		Bitmap bitmap = BitmapFactory
				.decodeFile(absoluteFile.getAbsolutePath());
		
		imageView.post(new ParamRunnable(bitmap, imageView) {

			@Override
			public void run() {
				getImageView().setImageBitmap(getBitMap());
			}
		});
		return bitmap;

	}

	private void addCacheBitmap(String key, Bitmap bitmap) {
		LKHomeCache.addCacheBitmap(key, bitmap);
		/*if (!bitmapCache.containsKey(key)) {
			SoftReference<Bitmap> srBitmap = new SoftReference<Bitmap>(bitmap);
			bitmapCache.put(key, srBitmap);
		}*/

	}

	private void add2DownLoadList(String key) {
		
		if (!isContainUrl(key)) {
			addDownloadUrl(key);
		}
	}

	private void removeFromDownLoadList(String key) {
		 removeDownloadUrl(key);
	}

	public Bitmap getCahceBitmap(String key) {
		return LKHomeCache.getBitmapBykey(key);
		/*SoftReference<Bitmap> srBitmap = bitmapCache.get(key);
		if (srBitmap != null) {
			Bitmap bitmap = srBitmap.get();
			if (bitmap != null) {
				return bitmap;
			}
		}
		return null;*/
	}

	public void getMovieApps(Handler handler_movie) {
		getSpectData(handler_movie, URLs.getURL_CATEGORY_MOVIE());
	}

	public void getAppApps(Handler handler) {
		getSpectData(handler, URLs.getURL_CATEGORY_APP());
	}

	public void getGameApps(Handler handler) {
		getSpectData(handler, URLs.getURL_CATEGORY_GAME());

	}

	public void getSearchApps(Handler handler, HashMap<String, String> param) {
		getSpectDataWithParam(handler, URLs.getURL_CATEGORY_SEARCH(), param);
	}

	public void getApps(Handler handler, HashMap<String, String> param, int page) {
		switch (page) {
		case 0:
			getMovieApps(handler, param);
			break;
		case 1:
			getAppApps(handler, param);
			break;
		case 2:
			getGameApps(handler, param);
			break;
		case 3:
			getSearchApps(handler, param);
			break;

		default:
			break;
		}
	}

	public void getMovieApps(Handler handler, HashMap<String, String> param) {
		getSpectDataWithParam(handler, URLs.getURL_CATEGORY_MOVIE(), param);
	}

	public void getAppApps(Handler handler, HashMap<String, String> param) {
		getSpectDataWithParam(handler, URLs.getURL_CATEGORY_APP(), param);
	}

	public void getGameApps(Handler handler, HashMap<String, String> param) {
		getSpectDataWithParam(handler, URLs.getURL_CATEGORY_GAME(), param);

	}

	public boolean setTicket(HashMap<String, String> param) {
		return doActionWithParam(URLs.getURL_TICKET(), param);
	}

	// end with param

	private boolean doActionWithParam(String urlTicket,
			HashMap<String, String> param) {
		boolean flag = false;

		try {
			flag = client.actionWithParam(urlTicket, param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	public void downLoadApk(final ApkBean bean, ImplInter inter) {
		
		final String tUrl = bean.getUrl().substring(bean.getUrl().lastIndexOf("/") + 1, bean.getUrl().length());

		//Log.e(TAG, "===========下载请求: url="+tUrl+",下载任务数="+downloadMaps+",线程="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId());
		if (isContainUrl(tUrl)) { //url 已经在下载
			bean.setStatus(ApkBean.STATE_DOWNLOADING);
			try {
				inter.downloadStatus(bean);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (getDownloadSize() >= DownLoadSize) { //下载任务达到5个
			bean.setStatus(ApkBean.STATE_QUEUE_ENOUNGH);
			try {
				inter.downloadStatus(bean);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { //可以下载
			
			add2DownLoadList(tUrl);
		 ThreadPoolUtil.execute(new ParamRunnable(bean, inter) {

				@Override
		 public void run() {
					
			String apkName=tUrl.substring(tUrl.lastIndexOf("=")+1,tUrl.length())+".apk";
			File localFile = new File(Constants.APK_DIR + File.separator + apkName);
			
			//该url对应的文件存在且md5码相同,不用重新下载
			Log.e(TAG, "======= 下载apk ,localFile="+localFile+",验证结果="+LKHomeUtil.checkDownloadFileSuccessed(bean.getMd5(), localFile));
			if (localFile.exists() && LKHomeUtil.checkDownloadFileSuccessed(bean.getMd5(), localFile)) { // 本地存在
				
				bean.setStatus(ApkBean.STATE_COMPLETE);
				bean.setSavePath(localFile.getAbsolutePath());
				
				//===add by xgh====
				if(downloadDao.findDownloadBeanByPackageName(bean.getPackageName())==null){
					downloadDao.addRecord(new DownloadBean(bean));
				}
				downloadDao.updateDownloadBeanState(bean.getPackageName(), Constant.APK_STATE_DOWNLOAD_COMPLETE);
				//=====end
				
				try {
					mListener.downloadStatus(bean);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			 } else { //需要下载


						 //Log.e(TAG, "下载apk线程名="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId());
					
						if(!hasEnoughSize(mApkBean.getSize())){
							
							try {
								mApkBean.setStatus(ApkBean.STATE_ERR_NO_SPACE);// 不够空间
								mListener.downloadStatus(mApkBean);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							 removeFromDownLoadList(tUrl);
							return;
						}
						
						//记录到应用数据库,安装完成后要读取style
						
						AppInfo mApp=mApkBean.buidAppInfo();
						LKService.DOWNLOAD_APPS.put(mApp.getPackage_name(), mApp);
						mAppStoreDao.addAppToStore(mApp, Constant.APPSTORE_MODE_STANDARD);

						//====add by xgh
                       	DownloadBean dbean=new DownloadBean(mApkBean);
						dbean.setSavePath(localFile.getAbsolutePath());
						downloadDao.addRecord(dbean);
						//=====end
						
						
						File apk = null;
						if (client != null) {
							String xUrl = URLs.getBASE() + mApkBean.getUrl();
							apk = client.getHttpResponseAsFile(xUrl, mListener,
									mApkBean);
						}
						if (apk != null) {
							File parent = apk.getParentFile();
							if (parent == null || !parent.exists()) {
								Util.initExternalDir();
							}
							mApkBean.setStatus(ApkBean.STATE_COMPLETE);// complete
							mApkBean.setSavePath(apk.getAbsolutePath());
							
							//====add by xgh 记录到下载数据库
							downloadDao.updateDownloadBeanState(mApkBean.getPackageName(), Constant.APK_STATE_DOWNLOAD_COMPLETE);
							//====end
							
							/*//记录到应用数据库,安装完成后要读取style
							AppInfo mApp=mApkBean.buidAppInfo();
							mAppStoreDao.addAppToStore(mApp, Constant.APPSTORE_MODE_STANDARD);*/
							
							try {
								mListener.downloadStatus(mApkBean);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						} else {
							//removeFromDownLoadList(tUrl);
							try {
								mApkBean.setStatus(ApkBean.STATE_ERR);// error
								mListener.downloadStatus(mApkBean);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
			     removeFromDownLoadList(tUrl);
			  }
			});
		}
	}

	public void asView(String url, ImageView imageView, final Handler mhandler) {
		//Logger.e("gww", "---url---"+url);
		if (Util.isEmpty(url)) {
			imageView.setImageResource(R.drawable.adefault);
			return;
		}
		if (imageView == null)
			return;
		
		String imageName = url.substring(url.lastIndexOf("/") + 1);
		imageView.setTag(imageName);
		
			Bitmap bitmap = getCahceBitmap(imageName);
			synchronized (bitLock) {
				if (bitmap != null) {
					if(imageName.equals(imageView.getTag())){
						
					 // Logger.e(TAG, "~~~~~~11111从缓存获取bitmap="+bitmap+"...imamgName="+imageName+",tag="+imageView.getTag());
					  imageView.setImageBitmap(bitmap);
					}
					return;
				}
			}

		imageView.setImageResource(R.drawable.adefault);
		ThreadPoolUtil.execute(new ParamRunnable(url, imageView) {

			@Override
			public void run() {
				// Log.e(TAG, "加载图片线程名="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId());
				
				final String imageName = getUrl().substring(getUrl().lastIndexOf("/") + 1,
						getUrl().length());
				File localFile = new File(Constants.IMG_DIR + File.separator
						+ imageName);
				Bitmap tBitMap =null;
				
				if(localFile.exists()){
					
					tBitMap=  decodeFromFile(getImageView(),localFile.getAbsoluteFile(), imageName);
				}
				
				if (tBitMap!=null) {//从本地成功加载
					synchronized (bitLock) {
						mhandler.post(new ParamRunnable(tBitMap, getImageView()) {

							@Override
							public void run() {
								if(imageName.equals(getImageView().getTag())){
									//Logger.e(TAG, "~~~~~~~2222从本地加载图片,bitmap="+getBitMap()+", imamgName="+imageName+",tag="+getImageView().getTag());
									
									getImageView().setImageBitmap(getBitMap());
								}
							}
						});
					}
					addCacheBitmap(imageName, tBitMap);
				} else {
					
					tBitMap = client.getFileAsBitMap(URLs.getBASE()+ getUrl());
					
					if(tBitMap!=null){ // 从网络成功加载
						synchronized (bitLock) {
							  mhandler.post(new ParamRunnable(tBitMap, getImageView()) {
								
								@Override
								public void run() {
									if(imageName.equals(getImageView().getTag())){
										//Logger.e(TAG, "~~~~~~~~33333从网络加载图片,bitmap="+getBitMap()+", imamgName="+imageName+",tag="+getImageView().getTag());
										
										getImageView().setImageBitmap(getBitMap());
									}
								}
							});
						}
						File cacheFile = new File(Constants.IMG_DIR
								+ File.separator + imageName);
						Util.writeBitmap2File(tBitMap, cacheFile);
						addCacheBitmap(imageName, tBitMap);
					}
					
				}
			}
		});
	}

	public void downLoadImg(String filePath){
		
		ThreadPoolUtil.execute(new ParamRunnable(filePath) {

			@Override
			public void run() {

				String tUrl = getUrl().substring(getUrl().lastIndexOf("/") + 1,
						getUrl().length());
				File localFile = new File(Constants.IMG_DIR + File.separator
						+ tUrl);
				
				//Log.e(TAG, "~~~~~~~localFile="+localFile+",是否存在?"+localFile.exists());
				if (localFile.exists()) {
					return ;
				} else {
					
					//Log.e(TAG, "~~~~~~~~~需要从网络加载高清图,url="+URLs.getBASE()+getUrl());
					Bitmap tBitmap = client.getFileAsBitMap(URLs.getBASE()+ getUrl());
					File cacheFile = new File(Constants.IMG_DIR+ File.separator + tUrl);
					Util.writeBitmap2File(tBitmap, cacheFile);
				}
			}
		});
		
		
	}
	
	
	public void getRecommApps(Handler handler) {
		getSpectData(handler, URLs.getURL_RECOMMEND());
	}

	public void asViewCompress(String url, ImageView imageView,
			final Handler mhandler) {
		System.out.println(Thread.currentThread().getName() + "asViewCOmpress");
		if (Util.isEmpty(url)) {
			imageView.setImageResource(R.drawable.adefault);
			return;
		}
		if (imageView == null)
			return;

		if (LKHomeCache.containsKey(url)) {
			Bitmap bitmap = getCahceBitmap(url);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				return;
			}
		}

		ThreadPoolUtil.execute(new ParamRunnable(url, imageView) {

			@Override
			public void run() {

				String tUrl = getUrl().substring(getUrl().lastIndexOf("/") + 1,
						getUrl().length());
				File localFile = new File(Constants.IMG_DIR + File.separator
						+ tUrl);
				Bitmap tBitMap = decodeFromFile(getImageView(),
						localFile.getAbsoluteFile(), tUrl);
				if (localFile.exists()) {

					synchronized (bitLock) {

						mhandler.post(new ParamRunnable(tBitMap, getImageView()) {

							@Override
							public void run() {
								getImageView().setImageBitmap(getBitMap());
								
							}
						});
					}
					addCacheBitmap(tUrl, tBitMap);
				} else {
					mhandler.post(new ParamRunnable(tBitMap, getImageView()) {

						@Override
						public void run() {
							getImageView()
									.setImageResource(R.drawable.adefault);
						}
					});

					// Bitmap tBitmap = client.getFileAsBitMapComprasse(URLs.getBASE()+ getUrl());
					 Bitmap tBitmap = client.getFileAsBitMap(URLs.getBASE()+ getUrl());
					
					mhandler.post(new ParamRunnable(tBitmap, getImageView()) {

						@Override
						public void run() {
							getImageView().setImageBitmap(getBitMap());
						}
					});

					File cacheFile = new File(Constants.IMG_DIR
							+ File.separator + tUrl);
					Logger.i("tag", "-----------------------------------------------"+cacheFile.getAbsolutePath());
					Util.writeBitmap2File(tBitmap, cacheFile);
					addCacheBitmap(tUrl, tBitmap);
				}
			}
		});
	}

	public AppInfo syncApp(HashMap<String, String> param) {
		AppInfo app = null;
		try {
			//String url="http://192.168.16.254:8080/AppMarketServer/android/syncApp.do";
			//app = client.getAppEntity(url,param);
			app = client.getAppEntity(URLs.getURL_SYNCAPP(),param );
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		return app;
	}

	
	/**
	 * 异步更新"已安装"图标
	 * @param packageName
	 * @param appFlag
	 * @param mHandler
	 */
	public void updateInstalledView(String packageName, ImageView imageView,
			Handler mHandler) {
		
		if(packageName==null){
			imageView.setVisibility(View.GONE);
		}
		
		if (imageView == null)
			return;

		imageView.setTag(packageName);
		
		
	}
	

	public boolean hasEnoughSize(long size) { //size的单位是k
		
		
		StatFs statfs = new StatFs("/mnt/sdcard");
        long blockSize   = statfs.getBlockSize();
        long availBlocks = statfs.getAvailableBlocks();
        long availsize   = blockSize * availBlocks;
       
        Logger.e(TAG, "====== 可用内存="+availsize+", apk大小="+size*1024);
        
        if(size*1024 * 1.1 >=availsize ){
        	
        	return false;
        }else{
        	return true;
        }
	}
	
	public boolean isContainUrl(String url){
		synchronized (downloadLock) {
			return downloadMaps.contains(url);
			
		}
		
	}
	
	public int getDownloadSize(){
		synchronized (downloadLock) {
			return downloadMaps.size();
			
		}
	}
	
	public void addDownloadUrl(String url){
		synchronized (downloadLock) {
			 downloadMaps.add(url);
		}
	}
	
	public void removeDownloadUrl(String url){
		synchronized (downloadLock) {
			 downloadMaps.remove(url);
			
		}
	}
	
	/**
	 * 上传apk
	 * @param pkg
	 * @param name
	 */
	public void uploadApk(String pkg,String name ) {
		if(checkUploadList.contains(pkg)){
			LKHomeUtil.showToast(context, "\""+name+"\""+context.getString(R.string.connet_server_for_check));
		   return;
		}
		if(waitUploadList.contains(pkg)){
			LKHomeUtil.showToast(context,  "\""+name+"\""+context.getString(R.string.wait_upload));
			return;
		}
		if(uploadList.contains(pkg)){
			LKHomeUtil.showToast(context,  "\""+name+"\""+context.getString(R.string.uploading));
			return;
		}
		
		checkUploadApk( pkg,name);
		
		
	}


	private void checkUploadApk( final String pkg,final String name ) {
		addCheckUploadRecord(pkg);
		LKHomeUtil.showToast(context, "\""+name+"\""+context.getString(R.string.connet_server_for_check));
		
		final String url=URLs.getURL_API_HOST()+"/checkClientUpload.action";
		final Map<String , String> params=new HashMap<String, String>();
		params.put("packageName", pkg);
		
		ThreadPoolUtil.execute(new Runnable() {

			@Override
		public void run() {
				 String result="";
		try {
			 String checkResult=client.getHttpResponseByPost(url,params);
			 Logger.e(TAG, "---------收到网络请求结果: checkResult="+checkResult);
			  if("error".equals(checkResult)){
				  result="-1";
			  }else{
				  ObjectMapper om=new ObjectMapper();
				   result=om.readTree(checkResult).get("result").asText();
				  
			  }
				
				
				
			} catch (IOException e) {
				e.printStackTrace();
			
			}finally{
				Bundle data=new Bundle();
				data.putString("pkg", pkg);
				data.putString("name", name);
				Message msg=Message.obtain();
				msg.what=RESULT_CHECK_UPLOAD;
				msg.obj=result;
				msg.setData(data);
				mHandler.sendMessage(msg);
				
				
			}
			
			
			
		}
		
	  });
	}
	
	HttpUtils http = new HttpUtils();
	private void performUpload(final String pkg,final String name){
		
		addWaitUploadRecord(pkg);
		
	
	Logger.e(TAG, "----点击了上传按钮,pkg="+pkg);
	uploadByXutils(pkg, name,LkecDevice.getDeviceId());
	
	}
	
	/*private void uploadByClient(final String pkg,final String name){
		UploadThreadPoolUtil.execute(new Runnable() {
			
			@Override
			public void run() {
				

				Logger.e(TAG, "----点击了上传按钮,pkg="+pkg);
				File uploadFile=null;
				File apks=new File("/data/app");
				if(apks.exists()){
					File[] childs=apks.listFiles();
					if(childs!=null && childs.length>0){
						for(int i=0;i<childs.length;i++){
							File apk=childs[i];
							if(apk.getName().startsWith(pkg)){
								uploadFile=apk;
								break;
							}
						}
					}
				}
				String sdcardPath="/mnt/asec/";
				if(uploadFile==null){ //安装在sd卡
					File sdInstallPath=new File(sdcardPath);
					if(sdInstallPath.exists()){
						File[] pkgDirs=sdInstallPath.listFiles();
						if(pkgDirs!=null && pkgDirs.length>0){
							for(int i=0;i<pkgDirs.length;i++){
								 File pkgDir=pkgDirs[i];
								 if(pkgDir.getName().startsWith(pkg)){
									 File apk=new File(pkgDir,"pkg.apk");
									 if(apk.exists()){
										 uploadFile=apk;
									 }
									 break;
								 }
							}
						}
					}
				}
				Logger.e(TAG, "xgh, ~~~~~~~需要上传的文件大小=="+uploadFile.length()+",file="+uploadFile+",pkg="+pkg);
				
				if(uploadFile==null || !uploadFile.exists()){ //找不到apk文件
					
					Bundle data=new Bundle();
					data.putString("pkg", pkg);
					data.putString("name", name);
					
					Message msg=Message.obtain();
					msg.what=RESULT_UPLOAD_COMPLETE;
					msg.obj="2";
					msg.setData(data);
					mHandler.sendMessage(msg);
					
				}else{
					String uploadUrl="http://192.168.16.254:8080/AppMarket/android/clientUpload.action";
					RequestParams params = new RequestParams();
					//params.addHeader("packageName", pkg);
					params.addBodyParameter("packageName", pkg);
					params.addBodyParameter("appName", name);
					params.addBodyParameter("uploadApkFile", uploadFile);
					
					Map<String , String> params=new HashMap<String, String>();
					params.put("packageName", pkg);
					params.put("appName", name);
					
					String result=client.uploadFile(uploadUrl,uploadFile,params);
					
					Logger.e(TAG, "=====上传结果===="+result);
				}
				
				
			}
		});
		
		
	}
	*/
	
	private void uploadByXutils(final String pkg,final String name,final String deviceId){
		
		File uploadFile=null;
		File apks=new File("/data/app");
		if(apks.exists()){
			File[] childs=apks.listFiles();
			if(childs!=null && childs.length>0){
				for(int i=0;i<childs.length;i++){
					File apk=childs[i];
					if(apk.getName().startsWith(pkg)){
						uploadFile=apk;
						break;
					}
				}
			}
		}
		String sdcardPath="/mnt/asec/";
		if(uploadFile==null){ //安装在sd卡
			File sdInstallPath=new File(sdcardPath);
			if(sdInstallPath.exists()){
				File[] pkgDirs=sdInstallPath.listFiles();
				if(pkgDirs!=null && pkgDirs.length>0){
					for(int i=0;i<pkgDirs.length;i++){
						 File pkgDir=pkgDirs[i];
						 if(pkgDir.getName().startsWith(pkg)){
							 File apk=new File(pkgDir,"pkg.apk");
							 if(apk.exists()){
								 uploadFile=apk;
							 }
							 break;
						 }
					}
				}
			}
		}
		
			if(uploadFile!=null){
				Logger.e(TAG, "xgh, ~~~~~~~需要上传的文件大小=="+uploadFile.length()+",file="+uploadFile+",pkg="+pkg);
				String uploadUrl=URLs.getURL_API_HOST()+"/clientUpload.action";
				RequestParams params = new RequestParams();
				//params.addHeader("packageName", pkg);
				params.addBodyParameter("packageName", pkg);
				params.addBodyParameter("appName", name);
				params.addBodyParameter("deviceId", deviceId);
				params.addBodyParameter("uploadApkFile", uploadFile);
				
				http.send(HttpMethod.POST,uploadUrl, params,new RequestCallBack<String>() {
				        @Override
				        public void onStart() {
				        	Logger.e(TAG, "====上传开始");
				        	
				        	Bundle data=new Bundle();
							data.putString("pkg", pkg);
							data.putString("name", name);
				        	Message msg=Message.obtain();
				        	msg.what=RESULT_START_UPLOAD;
				        	msg.setData(data);
				        	mHandler.sendMessage(msg);
				        	
				        	
				        }
				        @Override
				        public void onLoading(long total, long current, boolean isUploading) {
				            if (isUploading) {
				            	Logger.e(TAG, "-----正在上传:"+(current*100/total)+"%");
				            } else {
				            }
				        }
				        @Override
				        public void onSuccess(ResponseInfo<String> responseInfo) {
				        	
				        	Bundle data=new Bundle();
							data.putString("pkg", pkg);
							data.putString("name", name);
				        	Message msg=Message.obtain();
				        	msg.what=RESULT_UPLOAD_COMPLETE;
				        	msg.obj="1";
				        	msg.setData(data);
				        	mHandler.sendMessage(msg);
				        	
				        	
				        	
				        	
				        	Logger.e(TAG, "-------上传成功");
				        }
				        @Override
				        public void onFailure(HttpException error, String msg) {
				        	removeUploadRecord(pkg);
				        	
				        	
				        	Logger.e(TAG, "------上传失败");
				        }
				});
			}else{//找不到需要上传的apk文件
				Bundle data=new Bundle();
				data.putString("pkg", pkg);
				data.putString("name", name);
	        	Message msg=Message.obtain();
	        	msg.what=RESULT_UPLOAD_COMPLETE;
	        	msg.obj="2";
	        	msg.setData(data);
	        	mHandler.sendMessage(msg);
				
			}
		
	}
	
	/*public int copyFile(String fromFile, String toFile) {
		try {
			
			File sourFile=new File(fromFile);
			Logger.e(TAG, "-----复制文件,fromFile="+fromFile+",toFile="+toFile+",源文件大小="+sourFile.length());
			InputStream fosfrom = new FileInputStream(sourFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				Logger.e(TAG, "-------写文件,size="+c);
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;
		} catch (Exception ex) {
			return -1;
		}
	}*/
	
	public  void addCheckUploadRecord(String pkg){
		synchronized (checkUploadList) {
			if(!checkUploadList.contains(pkg)){
				checkUploadList.add(pkg);
			}
		}
	}
	public  void removeCheckUploadRecord(String pkg){
		synchronized (checkUploadList) {
			if(checkUploadList.contains(pkg)){
				checkUploadList.remove(pkg);
			}
		}
	}
	
	public void addUploadRecord(String pkg){
		synchronized (uploadList) {
			if(!uploadList.contains(pkg)){
				uploadList.add(pkg);
			}
		}
	}
	public void removeUploadRecord(String pkg){
		synchronized (uploadList) {
			if(uploadList.contains(pkg)){
				uploadList.remove(pkg);
			}
		}
	}
	
	public void addWaitUploadRecord(String pkg){
		synchronized (waitUploadList) {
			if(!waitUploadList.contains(pkg)){
				waitUploadList.add(pkg);
			}
		}
	}
	public void removeWaitUploadRecord(String pkg){
		synchronized (waitUploadList) {
			if(waitUploadList.contains(pkg)){
				waitUploadList.remove(pkg);
			}
		}
	}
	
	
	
		//======add by xgh
	public DownloadDao getDownloadDao( ){
		return downloadDao;
	}

	
	public void deleteDownloadBean(String packageName){
		String savePath=downloadDao.findSavePathByPackageName(packageName);
		if(savePath!=null){
			File file=new File(savePath);
			if(file!=null && file.exists()){
				file.delete();
			}
			downloadDao.deleteDownloadBean(packageName);
		}
		
		
	}
	
	//====end

	
}
