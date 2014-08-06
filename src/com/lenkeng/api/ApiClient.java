package com.lenkeng.api;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.DownloadDao;
import lenkeng.com.welcome.upload.FileDownloadHandler;
import lenkeng.com.welcome.upload.GZipDecompressingEntity;
import lenkeng.com.welcome.upload.HttpException;
import lenkeng.com.welcome.upload.HttpUtils;
import lenkeng.com.welcome.upload.IOUtils;
import lenkeng.com.welcome.upload.OtherUtils;
import lenkeng.com.welcome.upload.RequestCallBackHandler;
import lenkeng.com.welcome.upload.ResponseInfo;
import lenkeng.com.welcome.upload.RetryHandler;
import lenkeng.com.welcome.upload.SimpleSSLSocketFactory;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.lenkeng.bean.ApkBean;
import com.lenkeng.bean.DataEntity;
import com.lenkeng.bean.ImplInter;
import com.lenkeng.logic.Logic;
import com.lenkeng.tools.Constants;

public class ApiClient {
	
	
	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	private final static int RETRY_TIME = 3;
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";
	private static final String TAG = "ApiClient";
   //=====add by xgh
	private DownloadDao dao;
	private Logic mLogic;
	
	/**
	 * 暂停下载的集合,存放包名
	 */
	private static final List<String> downloadPausedList = new ArrayList<String>();
	private final FileDownloadHandler mFileDownloadHandler = new FileDownloadHandler();
	
	public ApiClient(Logic logic,DownloadDao dao) {
		this.dao = dao;
		mLogic=logic;
	}
	//====end
	
	private static DefaultHttpClient getHttpClient() {   
		HttpParams params = new BasicHttpParams();
		
	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 10000);
	    HttpConnectionParams.setTcpNoDelay(params, true);
	    HttpConnectionParams.setSocketBufferSize(params, 4096);
	    HttpClientParams.setRedirecting(params, false);
	    HttpProtocolParams.setUserAgent(params, "LKClient/Android-1.0");
	  
	    ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(100));

	    SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SimpleSSLSocketFactory.getSocketFactory(), 443));
	    
       // DefaultHttpClient httpClient = new DefaultHttpClient(params);
        DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
        httpClient.setHttpRequestRetryHandler(new RetryHandler(RETRY_TIME));

    /*    httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
                if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });*/

     
        
        
		return httpClient;
	}	
	private  HttpGet  getHttpGet(String url) {
		HttpGet  httpGet = new HttpGet(url);
		return httpGet;
	}
	
	
	public DataEntity getDataEntity(String url) throws JsonParseException, JsonMappingException, IOException{
		Logger.e(TAG, "xgh, 访问url="+url);
		
		DataEntity entity=null;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String responseBody = getHttpResponse(url);
		entity=objectMapper.readValue(responseBody, DataEntity.class);
		return entity;
	}
	
	public AppInfo getAppEntity(String url,Map<String,String> params) throws  JsonParseException, JsonMappingException, IOException{
		url=urlWithParam(url, params);
		AppInfo info=null;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String responseBody = getHttpResponse(url);
		info=objectMapper.readValue(responseBody, AppInfo.class);
		return info;
	}
	
	/*public AppInfo getSearchAppEntity(String url,Map<String,String> params) throws  JsonParseException, JsonMappingException, IOException{
		AppInfo info=null;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		String responseBody = getHttpResponseByPost(url,params);
		info=objectMapper.readValue(responseBody, AppInfo.class);
		return info;
	}*/
	
	
	public DataEntity getDataEntityWithParam(String url,Map<String,String> params) throws JsonParseException, JsonMappingException, IOException{
		DataEntity entity=null;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		url=urlWithParam(url, params);
		String responseBody = getHttpResponse(url);
		entity=objectMapper.readValue(responseBody, DataEntity.class);
		
		
		return entity;
	}
	
	
	public boolean actionWithParam(String url ,Map<String,String> params) throws UnsupportedEncodingException{
		url=urlWithParam(url, params);
		 return actionHttpGet(url);
	}
	
	
	
	private boolean actionHttpGet(String url) {
		
		boolean flag=false;
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		int time=0;
		String responseBody="";
		try {
			httpClient=getHttpClient();
			httpGet=getHttpGet(url);
			HttpResponse  response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
				flag=false;
			}else{
				flag=true;
				 responseBody = EntityUtils.toString(response.getEntity());
				/* if (responseBody.trim().equals("true")) {
					 flag=true;
				}else{
					flag=false;
				}*/
			}
		} catch (IOException e) {
			e.printStackTrace();
			time++;
			if(time < RETRY_TIME) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {} 
			}
		}finally{
			httpClient = null;
			return flag;
		}
	}
	
	
	public Bitmap getFileAsBitMap(String url){
		return getHttpResponseAsBitMap(url);
	}
	
	
	/*public File getHttpResponseAsFile_(String url,ImplInter mListener,ApkBean bean){
		Logger.i("gww","---download url----"+url);
		String tmp="tmp";
		String fileName=url.substring(url.lastIndexOf("/")+1,url.length());
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		File file=new File(Constants.APK_DIR,tmp+fileName);
		File f=null;
		InputStream stream =null;
		FileOutputStream fos=null;
		bean.setUrl(fileName);
		long length=0;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		byte[] buffer = new byte[1024];
		int time=0;
		int len=-1;
		bean.setStatus(0);
		try {
			mListener.downloadStatus(bean);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		bean.setStatus(4);
		do {
			try {
				
				httpClient=getHttpClient();
				httpGet=getHttpGet(url);
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
					return null;
					
				}else{
					stream=response.getEntity().getContent();
					length =response.getEntity().getContentLength();
					long load=0;
					while( (len = stream.read(buffer)) != -1 ){
						fos.write(buffer, 0, len);
						bean.setProgress(load*100/length);
						load+=len;
						try {
							mListener.setProgress(bean);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
					
					long fileSize=file.length();
					if (length==fileSize) {
						f=new File(Constants.APK_DIR,fileName);
						file.renameTo(f);
						break;
					}
						
				}
			} catch (IOException e) {
				e.printStackTrace();
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					
					continue;
				}
			}finally{
				try {
					if(stream!=null){
					    stream.close();
					}
					if (fos!=null) {
					    fos.close();
					}
					boolean deleted=file.delete();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} while (time<RETRY_TIME);
		return f;
	}*/
	
	
	/**
	 * 从指定url下载apk文件
	 * @param url
	 * @param mListener
	 * @param bean
	 * @return
	 */
	public File getHttpResponseAsFile(String url, ImplInter mListener,
			ApkBean bean) {
		String tmp = "tmp";
		String fileName = "";
		String md5=bean.getMd5();
		int tempProgres=0; //记录临时的进度
		
		//String x = url.substring(url.lastIndexOf("/") + 1, url.length());

		fileName = url.substring(url.lastIndexOf("=") + 1, url.length())
				+ ".apk";
		HttpClient httpClient = null;
		HttpGet httpGet = null;

		File dir = new File(Constants.APK_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File f = null;
		File file = new File(Constants.APK_DIR, tmp + fileName);
		
		InputStream stream = null;
		FileOutputStream fos = null;
		bean.setUrl(fileName);
		long length = 0;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		byte[] buffer = new byte[4096];
		int time = 0;
		int len = -1;
		bean.setStatus(ApkBean.STATE_START);
		try {
			mListener.downloadStatus(bean);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		bean.setStatus(ApkBean.STATE_PROGRESS);
		do {
			try {
				httpClient = getHttpClient();
				
				Logger.e(TAG, "-------得到的httpClient="+httpClient);
				httpGet = getHttpGet(url);

				// httpGet=getHttpGet(validateURL);
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					return null;
				} else {


					stream = response.getEntity().getContent();


					length = response.getEntity().getContentLength();
					
					if(length==-1){
						return null;
					}
					long load = 0;
					while ((len = stream.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
						load += len;
						bean.setProgress(load * 100 / length);
						
						try {
							if(bean.getProgress()>tempProgres){
								if(bean.getProgress()>0 && bean.getProgress()<=100){
									
								mListener.setProgress(bean);
								tempProgres=(int) bean.getProgress();
								}
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

					long fileSize = file.length();
					//Log.e(TAG, "下载完成 数量:" + load + ",length=" + length
					//		+ ",fileSize=" + fileSize);
					/*if (length == fileSize) {
						f = new File(Constants.APK_DIR, fileName);
						file.renameTo(f);
						break;
					}*/
					
					
					if (LKHomeUtil.checkDownloadFileSuccessed(md5, file)) {
						f = new File(Constants.APK_DIR, fileName);
						file.renameTo(f);
						break;
					}
				}
			} catch (Exception e) { //捕获网络异常
				e.printStackTrace();
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}

					continue;
				}
			} finally {
				httpClient.getConnectionManager().shutdown();
				try {
					if (stream != null) {
						stream.close();
					}
					if (fos != null) {
						fos.close();
					}
					boolean deleted = file.delete();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} while (time < RETRY_TIME);
		
		// toDownload();

		return f;
	}
	
	
	
	/**
	 * 从指定url下载apk文件
	 * @param url
	 * @param mListener
	 * @param bean
	 * @return
	 */
	public File getHttpResponseAsFileByDuandian(String url, ImplInter mListener,
			ApkBean bean) {
		String tmp = ".tmp";
		String fileName = "";
		String md5=bean.getMd5();
		int tempProgres=0; //记录临时的进度
		Logger.e(TAG, "======ApiClient...收到下载请求. url="+url);
		//String x = url.substring(url.lastIndexOf("/") + 1, url.length());

		fileName = url.substring(url.lastIndexOf("=") + 1, url.length())
				+ ".apk";
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		InputStream stream = null;
		//FileOutputStream fos = null;
		File f = null;
		RandomAccessFile raf=null;
		
		File dir = new File(Constants.APK_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File file = new File(Constants.APK_DIR,  fileName+tmp);
		try {
		 raf=new RandomAccessFile(file,"rw");
			raf.setLength(bean.getRealSize());
			raf.seek(bean.getCurrent());
			Logger.e(TAG, "~~~~~~~~~!!!创建随机存储文件,length="+bean.getRealSize()+",current="+bean.getCurrent());
			
			//raf.close();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
		
		
		bean.setUrl(fileName);
		//long length = 0;
	/*	try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}*/
		byte[] buffer = new byte[4096];
		int len = -1;
		int time = 0;
		bean.setStatus(ApkBean.STATE_START);
		try {
			mListener.downloadStatus(bean);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		bean.setStatus(ApkBean.STATE_PROGRESS);
		//do {
			try {
				httpClient = getHttpClient();
				
				Logger.e(TAG, "-------得到的httpClient="+httpClient);
				HttpPost post = new HttpPost(url);
				post.setHeader("Range", "bytes=" + bean.getCurrent() + "-" );
				
				HttpResponse response = httpClient.execute(post);
				
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					return null;
				} else {


					stream = response.getEntity().getContent();
					
					
					long available = response.getEntity().getContentLength();
					Logger.e(TAG, "============avaliale="+available);
					
				/*	if(length==-1){
						return null;
					}*/
					long load = bean.getCurrent();
					while ((len = stream.read(buffer)) > 0 ) {
						
						if(isDownloadPaused(bean.getPackageName())){ //当前下载url已经paused
							bean.setStatus(ApkBean.STATE_PAUSED);
							mListener.downloadStatus(bean);
							break;
						}
						
						
						//Logger.e(TAG, "^^^^^^^^^^当前文件指针="+raf.getFilePointer());
						raf.write(buffer, 0, len);
						//Logger.e(TAG, "&&&&&&&&&之后文件指针="+raf.getFilePointer());
						//Logger.e(TAG, "+++++len++++ "+len);
						
						load += len;
						bean.setProgress(load * 100 / bean.getRealSize());
						
						try {
							if(bean.getProgress()>tempProgres){
								if(bean.getProgress()>0 && bean.getProgress()<=100){
									
							   //=====add by xgh==
								if(bean.getProgress()%2==0){
								 dao.updateDownloadPosition(bean.getPackageName(), load); //TODO...用于断点下载的记录
									}
								
								//=====end====
								
								mListener.setProgress(bean);
								tempProgres=(int) bean.getProgress();
								}
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

					
					if(load==bean.getRealSize()){ //内容字节数下载完成,判断md5是否相同
						
						if (LKHomeUtil.checkDownloadFileSuccessed(md5, file)) {
							f = new File(Constants.APK_DIR, fileName);
							file.renameTo(f);
						}
					}
				}
			} catch (Exception e) { //捕获网络异常
				e.printStackTrace();
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}

					//continue;
				}
			} finally {
				httpClient.getConnectionManager().shutdown();
				try {
					if (stream != null) {
						stream.close();
					}
					if (raf!=null) {
						raf.close();
					}
					//boolean deleted = file.delete();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		//} while (time < RETRY_TIME);
		
		// toDownload();

		return f;
	}

	/**
	 * 从指定url解析bitmap
	 * @param url
	 * @return
	 */
	public Bitmap getHttpResponseAsBitMap(String url) {
		
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		Bitmap tBit=null;
		InputStream stream =null;
		int time=0;
		do {
			try {
				httpClient=getHttpClient();
				httpGet=getHttpGet(url);
				  HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
					//Log.i("ken","380++++download state error");
				}else{
					stream=response.getEntity().getContent();
					tBit=BitmapFactory.decodeStream(stream);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
			}finally{
				if(stream!=null){
					try {
						stream.close();
					} catch (Exception e) {
						e.printStackTrace();
					};
				}
				//关闭连接
				httpClient.getConnectionManager().shutdown();
			}
			
		} while (time<RETRY_TIME);
		
		return tBit;
	}
	
	/**
	 * 执行get请求
	 * @param url
	 * @return
	 */
	public String getHttpResponse(String url) {
		Logger.i("gww", "url="+url);
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		String responseBody = "";
		int time=0;
		do {
			try {
				httpClient=getHttpClient();
				httpGet=getHttpGet(url);
				HttpResponse  response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
					//Log.i("ken","414++++download state error");
				}else{
					responseBody=EntityUtils.toString(response.getEntity());
					
				break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
			}finally{
				httpClient.getConnectionManager().shutdown();
			}
			
		} while (time<RETRY_TIME);
		return responseBody;
	}
	
	/**
	 * 使用post请求服务器,获取json数据,用于传递中文数据
	 * @param url
	 * @param params
	 * @return
	 */
	public String getHttpResponseByPost(String url,Map<String, String> params) {
		Logger.i("xgh", "ApiClient.getHttpResponseByPost(),url="+url);
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String responseBody = "error";
		int time=0;
		do {
			try {
				httpClient=getHttpClient();
				httpPost=postForm(url,params);
				
				HttpResponse  response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
					//Log.i("ken","414++++download state error");
				}else{
					responseBody=EntityUtils.toString(response.getEntity());
					
				break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
			}finally{
				httpClient.getConnectionManager().shutdown();
				//httpClient = null;
			}
			
		} while (time<RETRY_TIME);
		return responseBody;
	}
	
	 private static HttpPost postForm(String url, Map<String, String> params){  
         
	        HttpPost httpost = new HttpPost(url);  
	        List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
	          
	        Set<String> keySet = params.keySet();  
	        for(String key : keySet) {  
	            nvps.add(new BasicNameValuePair(key, params.get(key)));  
	        }  
	          
	        try {  
	            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));  
	        } catch (UnsupportedEncodingException e) {  
	            e.printStackTrace();  
	        }  
	          
	        return httpost;  
	    }  
	/* private static HttpPost postForm(String url,File uploadFile, Map<String, String> params){  
		 
		 HttpPost httpost = new HttpPost(url);  
		 List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
		 
		 
		 MultipartEntity mpEntity = new MultipartEntity(); //文件传输
		   ContentBody cbFile = new FileBody(uploadFile);
		   mpEntity.addPart("uploadApkFile", cbFile);
		   
		   Set<String> keySet = params.keySet();  
		   for(String key : keySet) {  
			   try {
				mpEntity.addPart(key, new StringBody(params.get(key)));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		   }  
		   
		  httpost.setEntity(mpEntity);  
		 
		 return httpost;  
	 }  */
	
	
	
	
	
	
	public  List<AppInfo> getListByUrlWithParam(String url,Map<String,String> params) throws UnsupportedEncodingException{
		List<AppInfo> list=null;
		url=urlWithParam(url,params);
		String responseBody = getHttpResponse(url);
		list=JSON2List(responseBody);
		return list;
	}
	private String urlWithParam(String url,Map<String, String> params)
			throws UnsupportedEncodingException {
		StringBuilder para=new StringBuilder();
		for (Map.Entry<String, String>entry:params.entrySet()) {
			para.append(entry.getKey());
			para.append("=");
			para.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
			para.append("&");
		}
		if(para.length()>0) para.deleteCharAt(para.length()-1);
		if(url.contains("do?")){
			return url+"&"+para.toString();
		}
		return url+"?"+para.toString();
	}
	
	private static List<AppInfo> JSON2List(String responseBody) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		List<AppInfo> list=new ArrayList<AppInfo>();
		try {
			JSONObject data=new JSONObject(responseBody);
			JSONArray jsonArray=(JSONArray) data.get("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj=jsonArray.getJSONObject(i);
				AppInfo app=objectMapper.readValue(obj.toString(), AppInfo.class);
				list.add(app);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	public  List<AppInfo> getListByUrl(String url){
		List<AppInfo> list=null;
		String responseBody = getHttpResponse(url);
		list=JSON2List(responseBody);
		return list;
	}
	public Bitmap getFileAsBitMapComprasse(String url) {
		return getHttpResponseAsBitMapComprasses(url);
	}
	private Bitmap getHttpResponseAsBitMapComprasses(String url) {
		{
			HttpClient httpClient = null;
			HttpGet httpGet = null;
			Bitmap tBit=null;
			InputStream stream =null;
			int time=0;
			
			do {
				try {
					httpClient=getHttpClient();
					httpGet=getHttpGet(url);
					  HttpResponse response = httpClient.execute(httpGet);
					if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
						//Log.i("ken","511++++download state error");
					}else{
						stream=response.getEntity().getContent();
						tBit=decodeSampledBitmapFromResource(stream);
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					time++;
					if(time < RETRY_TIME) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {} 
						continue;
					}
				}finally{
					httpClient.getConnectionManager().shutdown();
					//httpClient = null;
				}
				
			} while (time<RETRY_TIME);
			
			return tBit;
		}
	}
	
	public  Bitmap decodeSampledBitmapFromResource(InputStream is) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds=false;
		options.inSampleSize=4;
		return BitmapFactory.decodeStream(is, null, options);
	}

	/*public String uploadFile(String url,File uploadFile,Map<String, String> params){
		Logger.i("xgh", "ApiClient.getHttpResponseByPost(),url=" + url);
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String responseBody = "error";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpPost = postForm(url,uploadFile, params);

				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					 Log.e(TAG,"414++++download state error");
				} else {
					responseBody = EntityUtils.toString(response.getEntity());
					Log.e(TAG,"成功返回, responseBody="+responseBody);

					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
			} finally {
				httpClient.getConnectionManager().shutdown();
				// httpClient = null;
			}

		} while (time < RETRY_TIME);

		return responseBody;

	}*/
	
/*	public File handleEntity(HttpEntity entity,
			RequestCallBackHandler callBackHandler, String target,
			boolean isResume, String responseFileName) throws IOException {
		if (entity == null || TextUtils.isEmpty(target)) {
			return null;
		}

		File targetFile = new File(target);

		if (!targetFile.exists()) {
			File dir = targetFile.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			targetFile.createNewFile();
		}

		long current = 0;
		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;

		try {

			if (isResume) {
				current = targetFile.length();
				fileOutputStream = new FileOutputStream(target, true);
			} else {
				fileOutputStream = new FileOutputStream(target);
			}

			long total = entity.getContentLength() + current;

			if (callBackHandler != null
					&& !callBackHandler.updateProgress(total, current, true)) {
				return targetFile;
			}

			inputStream = entity.getContent();
			BufferedInputStream bis = new BufferedInputStream(inputStream);

			byte[] tmp = new byte[4096];
			int len;
			while ((len = bis.read(tmp)) != -1) {
				fileOutputStream.write(tmp, 0, len);
				current += len;
				if (callBackHandler != null) {
					if (!callBackHandler.updateProgress(total, current, false)) {
						return targetFile;
					}
				}
			}
			fileOutputStream.flush();
			if (callBackHandler != null) {
				callBackHandler.updateProgress(total, current, true);
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(fileOutputStream);
		}

		if (targetFile.exists() && !TextUtils.isEmpty(responseFileName)) {
			File newFile = new File(targetFile.getParent(), responseFileName);
			while (newFile.exists()) {
				newFile = new File(targetFile.getParent(),
						System.currentTimeMillis() + responseFileName);
			}
			return targetFile.renameTo(newFile) ? newFile : targetFile;
		} else {
			return targetFile;
		}
	}*/
	
	
	
	/**
	 * 暂停下载,将指定的包名添加到下载集合,下载线程会循环判断正在下载的pkg是否包含在暂停集合中,如果包含,就退出下载循环,发送下载暂停的状态
	 * @param pkg
	 */
	public void addDownloadPausedRecord(String pkg){
		synchronized (downloadPausedList) {
			
			Logger.e(TAG, "========添加断点下载暂停记录.... pkg="+pkg+",集合="+downloadPausedList);
			if(!downloadPausedList.contains(pkg)){
				downloadPausedList.add(pkg);
			}
		}
	}
	public void removeDownloadPausedRecord(String pkg){
		synchronized (downloadPausedList) {
			if(downloadPausedList.contains(pkg)){
				downloadPausedList.remove(pkg);
			}
		}
	}
	
	public boolean isDownloadPaused(String pkg){
		return downloadPausedList.contains(pkg);
	}
	
	
}
