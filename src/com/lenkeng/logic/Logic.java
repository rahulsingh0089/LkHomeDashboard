package com.lenkeng.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.db.DownloadBean;
import lenkeng.com.welcome.db.DownloadDao;
import lenkeng.com.welcome.db.UploadDao;
import lenkeng.com.welcome.server.LKService;
import lenkeng.com.welcome.upload.HttpException;
import lenkeng.com.welcome.upload.HttpRequest.HttpMethod;
import lenkeng.com.welcome.upload.HttpUtils;
import lenkeng.com.welcome.upload.RequestCallBack;
import lenkeng.com.welcome.upload.RequestParams;
import lenkeng.com.welcome.upload.ResponseInfo;
import lenkeng.com.welcome.upload.UploadBean;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeCache;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.LkecDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lenkeng.api.ApiClient;
import com.lenkeng.appmarket.comment.ApkCommentParam;
import com.lenkeng.bean.ApkBean;
import com.lenkeng.bean.DataEntity;
import com.lenkeng.bean.ImplInter;
import com.lenkeng.bean.ParamRunnable;
import com.lenkeng.bean.URLs;
import com.lenkeng.tools.Constants;
import com.lenkeng.tools.ThreadPoolUtil;
import com.lenkeng.tools.Util;

public class Logic {
	private ApiClient client;
	private Context context;
	
	public static final int DownLoadSize = 5;
	public static Object bitLock = new Object();
	public static Object insLock = new Object();
	private static Object downloadLock=new Object();
	private static Object uploadbeanLock=new Object();
	private DownloadDao downloadDao;
	private AppStoreDao mAppStoreDao;
	private UploadDao uploadDao;
	
	public static final int RESULT_CHECK_UPLOAD=10001;
	public static final int RESULT_UPLOAD_COMPLETE = 10002;
	public static final int RESULT_START_UPLOAD = 10003;
	
	
	
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
	/**
	 * 存放上传数据库的集合
	 */
	private static final HashMap<String,UploadBean> uploadBeanMap = new HashMap<String, UploadBean>();
	
	/**
	 * 存放上传进度的集合
	 */
	private static final Map<String, Integer> percentMap=new HashMap<String, Integer>();
	
	private static final String TAG = "Logic";
			
	public static final String NEED_UPDATE_UPLOAD_BTN = "action_need_update_upload_btn";

	
	public static final String ACTION_NEED_REFRESH_COMMENT = "action_need_refresh_comment";
	public static final int RESULT_GET_COMMENT = 10004;
	public static final int RESULT_SUBMIT_COMMENT = 10005;
	
	public static final String ACTION_RESULT_COMMIT_COMMON = "action_result_commit_common";
	
	private static Logic instance;
	public static synchronized Logic getInstance(Context context){
		if(instance==null){
			instance=new Logic(context);
		}
		return instance;
	}
	
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RESULT_CHECK_UPLOAD: //检测上传请求完成
				Bundle data=msg.getData();
				String name=data.getString("name");
				String pkg=data.getString("pkg");
				
				removeCheckUploadRecord(pkg);
				
				String result=(String) msg.obj;
				if("1".equals(result)){ //等待上传
					String str= "\""+name+"\""+context.getString(R.string.wait_upload);
					LKHomeUtil.showToast(context, str);
					performUpload(pkg, name);
					
				}else if("2".equals(result)){ //该apk还在等待审核
					String str= "\""+name+"\""+context.getString(R.string.upload_wait_verify);
					LKHomeUtil.showToast(context, str);
					
					//发送需要更新上传按钮的广播
					sendUpdateUploadBtnBroadCast(pkg);
					
					
				}else if("3".equals(result)){ //该apk审核已经通过,代表已经上架
					String str= "\""+name+"\""+context.getString(R.string.upload_verify_pass);
					LKHomeUtil.showToast(context, str);
					
					//发送需要更新上传按钮的广播
					sendUpdateUploadBtnBroadCast(pkg);
					
					
				}else if("4".equals(result)){ //该apk审核未通过
			/*		String str= "\""+name+"\""+context.getString(R.string.upload_verify_fail);
					LKHomeUtil.showToast(context, str);*/
					
					
					String temp1= "\""+name+"\"";
				    String temp=context.getString(R.string.upload_verify_fail);
				    String str=String.format(temp, temp1);
					LKHomeUtil.showToast(context, str);
					
					//发送需要更新上传按钮的广播
					sendUpdateUploadBtnBroadCast(pkg);
					
					
				}else if("5".equals(result)){ //同包名的apk已经存在
					String temp1= "\""+name+"\"";
				    String temp=context.getString(R.string.apk_exist);
				    String str=String.format(temp, temp1);
					LKHomeUtil.showToast(context, str);
					
					//发送需要更新上传按钮的广播
					sendUpdateUploadBtnBroadCast(pkg);
					
					
				}else  { //连接服务器异常
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
					
					//发送更新广播,通知"其他"栏目更新view
					sendUpdateUploadBtnBroadCast(pkg);
					
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
	
	

	private Logic(Context con) {
		this.context = con;
		//=====edit by xgh
		downloadDao=new DownloadDao(con);
		uploadDao=new UploadDao(con);
        client = new ApiClient(this,downloadDao);
		//====end
		
		LKHomeCache.initCache();
		mAppStoreDao=AppStoreDao.getInstance(con);
	    updateUploadCacheMap();
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
		
		if (!isDownloading(key)) {
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

	/**
	 * 非断点下载apk
	 * @param bean
	 * @param inter
	 */
  public void downLoadApk(final ApkBean bean, ImplInter inter) {
		
		final String tUrl = bean.getUrl().substring(bean.getUrl().lastIndexOf("/") + 1, bean.getUrl().length());

		//Log.e(TAG, "===========下载请求: url="+tUrl+",下载任务数="+downloadMaps+",线程="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId());
		if (isDownloading(tUrl)) { //url 已经在下载
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
						
						
						 removeFromDownLoadList(tUrl);
						  
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
			    // removeFromDownLoadList(tUrl);
			  }
			});
		}
	}
	
	
	
	/**
	 * 断点下载apk
	 * @param apkBean
	 * @param inter
	 */
	public void downLoadApkByDuandian(final ApkBean apkBean, ImplInter inter) {
		
		final String tUrl = apkBean.getUrl().substring(apkBean.getUrl().lastIndexOf("/") + 1, apkBean.getUrl().length());

		Log.e(TAG, "===========下载请求: url="+tUrl+",下载任务数="+downloadMaps+",线程="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId());
	
		if (getDownloadSize() >= DownLoadSize) { //下载任务达到5个
			apkBean.setStatus(ApkBean.STATE_QUEUE_ENOUNGH);
			try {
				inter.downloadStatus(apkBean);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
		} 
		
		
		//开始下载
		
		if (! isDownloading(tUrl)) {
		   add2DownLoadList(tUrl);
		}
		
		 ThreadPoolUtil.execute(new ParamRunnable(apkBean, inter) {

				@Override
		 public void run() {
					
			String apkName=tUrl.substring(tUrl.lastIndexOf("=")+1,tUrl.length())+".apk";
			File localFile = new File(Constants.APK_DIR + File.separator + apkName);
			
			
			//Log.e(TAG, "======= 下载apk ,localFile="+localFile+",验证结果="+LKHomeUtil.checkDownloadFileSuccessed(apkBean.getMd5(), localFile));
			if (localFile.exists() && LKHomeUtil.checkDownloadFileSuccessed(apkBean.getMd5(), localFile)) {//该url对应的文件存在且md5码相同,不用重新下载
				
				apkBean.setStatus(ApkBean.STATE_COMPLETE);
				apkBean.setSavePath(localFile.getAbsolutePath());
				
				//===add by xgh====
				if(downloadDao.findDownloadBeanByPackageName(apkBean.getPackageName())==null){
					Logger.e(TAG, "========创建downloadBean ,apkBean="+apkBean);
					downloadDao.addRecord(new DownloadBean(apkBean));
				}
				downloadDao.updateDownloadBeanState(apkBean.getPackageName(), Constant.APK_STATE_DOWNLOAD_COMPLETE);
				//=====end
				
				try {
					mListener.downloadStatus(apkBean);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			 } else { //本地不存在完整的apk文件,需要下载


						 //Log.e(TAG, "下载apk线程名="+Thread.currentThread().getName()+",id="+Thread.currentThread().getId());
					
						
						
						//记录到应用数据库,安装完成后要读取style
						
						AppInfo mApp=apkBean.buidAppInfo();
						LKService.DOWNLOAD_APPS.put(mApp.getPackage_name(), mApp);
						mAppStoreDao.addAppToStore(mApp, Constant.APPSTORE_MODE_STANDARD);
						
						
						
						//根据包名读取下载数据库记录
						DownloadBean dbean=downloadDao.findDownloadBeanByPackageName(apkBean.getPackageName());
						if(dbean!=null){ //有该包名对应的下载记录
							String filePath=dbean.getSavePath();
							
							File tempFile = new File(filePath+".tmp");
							if(tempFile!=null &&  tempFile.exists()){ //临时文件存在,使用记录的进度
							    
								//临时文件存在,且数据库记录已经完成,判断md5值,如果不相等,即下载错误,删除文件,记录位置清零
								if(dbean.getCurrent()==apkBean.getRealSize()){
									if(! LKHomeUtil.checkDownloadFileSuccessed(apkBean.getMd5(), tempFile)){
										tempFile.delete();
										apkBean.setCurrent(0);
										downloadDao.updateDownloadPosition(apkBean.getPackageName(), 0);
									}
									
									
								}else{
									
									apkBean.setCurrent(dbean.getCurrent());
								}
								
								Logger.e(TAG, "~~~~~~~数据库有记录,临时文件存在,curent="+apkBean.getCurrent());
								
							}else{//临时文件不存在,进度清0
								downloadDao.updateDownloadPosition(apkBean.getPackageName(), 0);
								apkBean.setCurrent(0);
								
								Logger.e(TAG, "~~~~~~~数据库有记录,临时文件不存在,curent清零");
							}
							
							
						}else{ //无下载记录
							
							 dbean=new DownloadBean(apkBean);
							dbean.setSavePath(localFile.getAbsolutePath());
							dbean.setCurrent(0);
							downloadDao.addRecord(dbean);
							
						}
						
						
						if(!hasEnoughSizeByDuandian(apkBean.getRealSize()-apkBean.getCurrent())){
							
							try {
								apkBean.setStatus(ApkBean.STATE_ERR_NO_SPACE);// 不够空间
								mListener.downloadStatus(apkBean);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
							 removeFromDownLoadList(tUrl);
							return;
						}
						
						
						File apk = null;
						if (client != null) {
							String xUrl = URLs.getBASE() + apkBean.getUrl();
							apk = client.getHttpResponseAsFileByDuandian(xUrl, mListener,
									apkBean);
						}
						
						//removeFromDownLoadList(tUrl); //下载完成才删除下载记录
						
						if (apk != null) { // 文件下载完成
							
							File parent = apk.getParentFile();
							if (parent == null || !parent.exists()) {
								Util.initExternalDir();
							}
							apkBean.setStatus(ApkBean.STATE_COMPLETE);// complete
							apkBean.setSavePath(apk.getAbsolutePath());
							
							//====add by xgh 记录到下载数据库
							downloadDao.updateDownloadBeanState(apkBean.getPackageName(), Constant.APK_STATE_DOWNLOAD_COMPLETE);
							//====end
							
							try {
								mListener.downloadStatus(apkBean);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						} else { // 文件下载未完成
							
							try {
								apkBean.setStatus(ApkBean.STATE_PAUSED);// error
								mListener.downloadStatus(apkBean);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
			     removeFromDownLoadList(tUrl); //本地文件存在或者不存在,执行完后删除记录
			  }
			});
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
        
        if(size*1024 >=availsize ){
        	
        	return false;
        }else{
        	return true;
        }
	}
	

	public boolean hasEnoughSizeByDuandian(long size) { //size的单位是byte
		
		
		StatFs statfs = new StatFs("/mnt/sdcard");
        long blockSize   = statfs.getBlockSize();
        long availBlocks = statfs.getAvailableBlocks();
        long availsize   = blockSize * availBlocks;
       
        Logger.e(TAG, "====== 可用内存="+availsize+", apk大小剩余大小="+size);
        
        if(size >=availsize ){
        	
        	return false;
        }else{
        	return true;
        }
	}
	
	public boolean isDownloading(String url){
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
		UploadBean upBean=findUploadBeanBypkg(pkg,name);//uploadDao.findUploadBeanByPackageName(pkg);
		/*if(upBean==null){
			upBean=new UploadBean(pkg,name);
		}*/
		
		Logger.e(TAG, "~~~1111111, 获取的上传状态:state="+getUploadState(pkg)+",包名="+pkg);
		switch (getUploadState(pkg)) {
		case Constant.UPLOAD_STATE_CANUPLOAD: //可以上传
			handByUpload(upBean);
			break;
		case Constant.UPLOAD_STATE_WAITVERFY: //等待审核
			checkUploadApk(upBean);
			//checkUploadApkBatch();
			break;
		case Constant.UPLOAD_STATE_VERFY_PASS: //审核通过
			LKHomeUtil.showToast(context, "\""+name+"\""+context.getString(R.string.upload_verify_pass));
			break;
		case Constant.UPLOAD_STATE_VERFY_FAIL: //审核失败
			
			handVerifyFail(upBean);
			
		    String str=String.format(context.getString(R.string.upload_verify_fail), "\""+name+"\"");
			LKHomeUtil.showToast(context, str);
			
			break;

		default:
			break;
		}
		
		
		
		
	}

	
	/**
	 * 处理审核失败
	 * @param upBean
	 */
	private void handVerifyFail(final UploadBean upBean){
		
		ThreadPoolUtil.execute(new Runnable() {

			@Override
		  public void run() {
				   
				   uploadDao.updateUploadBeanState(upBean.getPackageName(), Constant.UPLOAD_STATE_EXITED);
				   updateUploadCacheMap();
				   sendUpdateUploadBtnBroadCast(upBean.getPackageName());
			}
		
	  });
		
		
	}
	
	
    /**
     * 根据包名到服务器查找当前apk的审核状态
     * @param upBean
     */
	private void handByWaitVerify(UploadBean upBean) {
		final String url=URLs.getURL_API_HOST()+"/checkClientUpload.action";
		final Map<String , String> params=new HashMap<String, String>();
		final String pkg=upBean.getPackageName();
		final String name=upBean.getAppName();
		
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
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			
			}finally{
				//处理上传查询状态
				if("2".equals(result)){//"2"代表该apk还在审核中
					uploadDao.updateUploadBeanState(pkg, Constant.UPLOAD_STATE_WAITVERFY);
				}else if("3".equals(result)){
					uploadDao.updateUploadBeanState(pkg, Constant.UPLOAD_STATE_VERFY_PASS);
				}else if("4".equals(result)){
					uploadDao.updateUploadBeanState(pkg, Constant.UPLOAD_STATE_VERFY_FAIL);
				}
				updateUploadCacheMap();
				
				
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

	private UploadBean findUploadBeanBypkg(String pkg,String appName) {
		 UploadBean bean=uploadBeanMap.get(pkg);
		 if(bean==null){//如果缓存中没有改上传对象,先加入缓存,在异步中写入数据库
			 bean=new UploadBean(pkg,appName);
			
			 putUploadBean(pkg, bean);
		 }
		return bean;
	}


	private void handByUpload(UploadBean upBean) {
		String pkg=upBean.getPackageName();
		String name=upBean.getAppName();
		
		if(checkUploadList.contains(pkg)){
			LKHomeUtil.showToast(context, "\""+name+"\""+context.getString(R.string.connet_server_for_check));
		   return;
		}
		if(waitUploadList.contains(pkg)){
			LKHomeUtil.showToast(context,  "\""+name+"\""+context.getString(R.string.wait_upload));
			return;
		}
		if(uploadList.contains(pkg)){
			LKHomeUtil.showToast(context,  "\""+name+"\""+String.format(context.getString(R.string.uploading),percentMap.get(pkg)+"%"));
			return;
		}
		
		checkUploadApk( upBean);
		
	}


	/**
	 * 单个查询上传apk的状态
	 * @param upbean
	 */
	private void checkUploadApk( final UploadBean upbean ) {
		final String pkg=upbean.getPackageName();
		final String name=upbean.getAppName();
		
		addCheckUploadRecord(pkg);
		LKHomeUtil.showToast(context, "\""+name+"\""+context.getString(R.string.connet_server_for_check));
		
		final String url=URLs.getURL_API_HOST()+"/checkClientUpload.action";
		final Map<String , String> params=new HashMap<String, String>();
		params.put("packageName", pkg);
		params.put("deviceId", upbean.getDevId());
		
		ThreadPoolUtil.execute(new Runnable() {

			@Override
		public void run() {
				 String result="";
		try {
			
			//添加新的记录到数据库
			UploadBean temp=uploadDao.findUploadBeanByPackageName(pkg);
			if(temp==null){
				uploadDao.addRecord(upbean);
			}
			
			 String checkResult=client.getHttpResponseByPost(url,params);
			 Logger.e(TAG, "---------收到网络请求结果: checkResult="+checkResult);
			  if("error".equals(checkResult)){
				  result="-1";
			  }else{
				  ObjectMapper om=new ObjectMapper();
				   result=om.readTree(checkResult).get("result").asText();
				  
			  }
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			
			}finally{
				//处理上传查询状态
				if(result!=null && !("-1".equals(result))){//返回码不是-1,代表和服务器通讯成功
					int state=1;
					try {
						 state=Integer.parseInt(result);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				   
				   uploadDao.updateUploadBeanState(pkg, state);
				   updateUploadCacheMap();
				
				
				}
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
	
	
	/**
	 * 批量查询上传apk的状态(只查询"审核中"状态的apk)
	 * @param upbean
	 */
	public void checkUploadApkBatch() {
		
		ThreadPoolUtil.execute(new Runnable() {

			@Override
		public void run() {
				 String result="-1";
		try {
			
			ArrayList<UploadBean> checkList=uploadDao.getAllWaitVerifyRecord();
			
			if(checkList.size()==0){ //如果本地数据库没有记录需要更新,不发起网络请求
				Logger.e(TAG, "-------checkUploadApkBatch(), 本地记录中等待审核的记录为空,不发起网络请求...");
				return ;
			}
			
			//批量查询的josn字符串,只包含包名,devId单独传
			String batchJsonParam=generateBatchQueryJson(checkList);
			
		     Logger.e(TAG, "--------批量查询uploadBean状态, jsonParam="+batchJsonParam);
			
			final String url=URLs.getURL_API_HOST()+"/checkClientUploadBatch.action";
			final Map<String , String> params=new HashMap<String, String>();
			params.put("batchJsonParam", batchJsonParam);
			params.put("deviceId", LkecDevice.getDeviceId());
			
			 String checkResult=client.getHttpResponseByPost(url,params);
			 Logger.e(TAG, "---------收到网络请求结果: checkResult="+checkResult);
			  if("error".equals(checkResult)){
				  result="-1";
			  }else{
				  result=checkResult;
			  }
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			
			}finally{
				//处理上传查询状态
				if(!("-1".equals(result))){//返回码不是-1,代表和服务器通讯成功
					
				   ArrayList<UploadBean> resultList=jsonArray2UploadBeanList(result);
				   if(resultList!=null && resultList.size()>0){
					   
					   for(UploadBean bean: resultList){
						   uploadDao.updateUploadBeanState(bean.getPackageName(), bean.getState());
						   
					   }
					   updateUploadCacheMap();
					   sendUpdateUploadBtnBroadCast("");
				   }
				}
			}
			
		}

		
	  });
	}
	
	
	/**
	 * 将UploadBean集合转换为查询josn
	 * @param checkList
	 * @return 只传递包名
	 */
	private String generateBatchQueryJson(ArrayList<UploadBean> checkList) {
		if(checkList==null || checkList.size()==0){
			return "[]";
		}

		
		ObjectMapper om=new ObjectMapper();
		String queryJson="{}";
		ArrayNode root=om.createArrayNode();
		
		for(UploadBean bean: checkList){
			ObjectNode node=om.createObjectNode();
			
			node.put("packageName",bean.getPackageName());
			root.add(node);
		}
		
		queryJson= root.toString();
		return queryJson;
	}

	
	/**
	 * 将服务器返回的批量检测上传apk状态的json转为对象
	 * @param result
	 * @return
	 */
	private ArrayList<UploadBean> jsonArray2UploadBeanList(String result) {
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<UploadBean> resultList=new ArrayList<UploadBean>();
		
		UploadBean bean=null;
		try {
			JsonNode root=objectMapper.readTree(result);
			for(int i=0;i<root.size();i++){
				JsonNode node=root.get(i);
				String pkg=node.get("packageName").asText();
				int state=node.get("state").asInt();
				bean=new UploadBean();
				bean.setPackageName(pkg);
				bean.setState(state);
				
				resultList.add(bean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	
	private void updateUploadCacheMap() {
		synchronized(uploadbeanLock){
			uploadBeanMap.clear();
			ArrayList<UploadBean> uploadList=uploadDao.getAllRecord();
			for(UploadBean bean: uploadList){
				uploadBeanMap.put(bean.getPackageName(), bean);
			}
		}
		//Logger.e(TAG, "--------更新上传记录缓存, uploadBeanMap="+uploadBeanMap);
		
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
				        	percentMap.put(pkg, 0);
				        	
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
				            	percentMap.put(pkg, (int) (current*100/total));
				            	Logger.e(TAG, "-----正在上传:"+(current*100/total)+"%");
				            } else {
				            }
				        }
				        @Override
				        public void onSuccess(ResponseInfo<String> responseInfo) {
				        	
				        	percentMap.remove(pkg);
				        	
				        	//更新上传数据库,更新缓存
				        	uploadDao.updateUploadBeanState(pkg, Constant.UPLOAD_STATE_WAITVERFY);
				        	updateUploadCacheMap();
				        	
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
				        	
				        	percentMap.put(pkg, 0);
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
	
	/**
	 * 加载指定apk的评论
	 * @param apkId
	 * @param start
	 * @param count
	 */
	public void loadApkComment( final String pkg,  final int start, final int count){
		ThreadPoolUtil.execute(new Runnable() {

			@Override
		public void run() {
		try {
			Logger.e(TAG, "---------loadApkComment(), 发起查询评论的请求....pkg="+pkg);
			
			String paramJson=generateLoadApkCommentJson(pkg,start,count);
			if(paramJson==null){
				return;
			}
			
			final String url=URLs.getURL_API_HOST()+"/findComments.action";
			final Map<String , String> params=new HashMap<String, String>();
			params.put("paramJson", paramJson);
			
			 String checkResult=client.getHttpResponseByPost(url,params);
			// Logger.e(TAG, "----loadApkComment()-----收到网络请求结果: checkResult="+checkResult);
			  if(! "error".equals(checkResult)){ //服务器正常返回
				  
				  ArrayList<ApkCommentParam> comments=jsonArray2ApkCommentParams(checkResult);
				  
				 // Logger.e(TAG, "---------得到的评论集合="+comments);
				  //发送广播,携带数据,通知界面更新
				  Intent it=new Intent(ACTION_NEED_REFRESH_COMMENT);
				  it.putParcelableArrayListExtra("comments", comments);
				  context.sendBroadcast(it);
				  
			  }
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			
			}
			
		}

			

		
	  });
		
	}
	
	
	protected ArrayList<ApkCommentParam> jsonArray2ApkCommentParams(String checkResult) {
		ObjectMapper om=new ObjectMapper();
		ArrayList<ApkCommentParam> commentList=new ArrayList<ApkCommentParam>();
		try {
			JsonNode root=om.readTree(checkResult);
			for(int i=0;i<root.size();i++){
				ApkCommentParam param=om.readValue(root.get(i), ApkCommentParam.class);
				commentList.add(param);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return commentList;
	}

	/**
	 * 提交apk评论,结果通过广播发送
	 * @param common
	 */
	public void submitApkComment( final ApkCommentParam common,final Handler handler){
		ThreadPoolUtil.execute(new Runnable() {

			@Override
		public void run() {
		try {
			Logger.e(TAG, "---------submitApkComment(), common="+common);
			ObjectMapper om=new ObjectMapper();
			String paramJson=om.writeValueAsString(common);
			if(paramJson==null){
				return;
			}
			
			final String url=URLs.getURL_API_HOST()+"/addApkComment.action";
			final Map<String , String> params=new HashMap<String, String>();
			params.put("paramJson", paramJson);
			
			 String checkResult=client.getHttpResponseByPost(url,params);
			// Logger.e(TAG, "----loadApkComment()-----收到网络请求结果: checkResult="+checkResult);
				  
			 String result="-1";
			  if("error".equals(checkResult)){
				  result="-1";
			  }else{
				   result=om.readTree(checkResult).get("result").asText();
				  
			  }
			 
			 Message msg=Message.obtain();
			 msg.what=RESULT_SUBMIT_COMMENT;
			 msg.obj=result;
			 
			 handler.sendMessage(msg);	  
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			
			}
			
		}

			

		
	  });
		
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
	
	public void putUploadBean(String pkg,UploadBean bean){
		synchronized (uploadbeanLock) {
			uploadBeanMap.put(pkg, bean);
		}
	}
	
	public void removeUploadBeanByPkg(String pkg){
		synchronized (uploadbeanLock) {
			uploadBeanMap.remove(pkg);
			uploadDao.deletUploadByPkg(pkg);
		}
	}
	
	
	
	
	public void addDownloadPausedRecord(String pkg){
		client.addDownloadPausedRecord(pkg);
	}
	public void removeDownloadPausedRecord(String pkg){
		client.removeDownloadPausedRecord(pkg);
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


	public boolean isDownloadPaused(String pkg) {
		return client.isDownloadPaused(pkg);
	}


	public ApkBean buildDownloadApkBean(String packageName) {
		DownloadBean dbean=downloadDao.findDownloadBeanByPackageName(packageName);
		return null;
	}


	public int getUploadState(String packageName) {
		int state;
		if(uploadBeanMap.get(packageName)==null){
			state=  Constant.UPLOAD_STATE_CANUPLOAD;
		}else{
			state= uploadBeanMap.get(packageName).getState();
		}
		//Logger.e(TAG, "-------获取上传状态state="+state+", 缓存大小="+uploadBeanMap.size());
		return state;
	}
	
	private void sendUpdateUploadBtnBroadCast(String pkg) {
		//Intent it=new Intent(NEED_UPDATE_UPLOAD_BTN);
		Intent i=new Intent(NEED_UPDATE_UPLOAD_BTN);
		
	/*	Intent i = new Intent();
		i.setAction("com.lenkeng.newdata");
		i.putExtra("apkStyle", Constant.CLASSIFY_USER);
		i.putExtra("installFlag","install");*/
		
		
		i.putExtra("pkg", pkg);
		context.sendBroadcast(i);
	}
	
	
	private String generateLoadApkCommentJson(String pkg, int start,int count) {
		ObjectMapper om=new ObjectMapper();
	    ObjectNode node=	om.createObjectNode();
	    node.put("pkg", pkg);
	    node.put("start", start);
	    node.put("count", count);
	    
	    
		try {
			return om.writeValueAsString(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	//====end

	
}
