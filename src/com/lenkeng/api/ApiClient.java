package com.lenkeng.api;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.DownloadDao;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
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
import android.util.Log;

import com.lenkeng.bean.ApkBean;
import com.lenkeng.bean.DataEntity;
import com.lenkeng.bean.ImplInter;
import com.lenkeng.tools.Constants;

public class ApiClient {
	
	
	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	private final static int RETRY_TIME = 3;
	private static final String TAG = "ApiClient";
   //=====add by xgh
	private DownloadDao dao;
	
	
	
	public ApiClient(DownloadDao dao) {
		super();
		this.dao = dao;
	}
	//====end
	
	private static HttpClient getHttpClient() {   
		BasicHttpParams localBasicHttpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 10000);
	    HttpConnectionParams.setSoTimeout(localBasicHttpParams, 10000);
	    HttpConnectionParams.setSocketBufferSize(localBasicHttpParams, 4096);
	    HttpClientParams.setRedirecting(localBasicHttpParams, false);
	    HttpProtocolParams.setUserAgent(localBasicHttpParams, "LKClient/Android-1.0");

        HttpClient httpClient = new DefaultHttpClient(localBasicHttpParams);
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
									
								//if(bean.getProgress()%2==0){
								// dao.updateDownloadPosition(bean.getPackageName(), load); //TODO...用于断点下载的记录
								//	}
									
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
				httpClient = null;
			}
			
		} while (time<RETRY_TIME);
		
		return tBit;
	}
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
				httpClient = null;
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
	/*public String getHttpResponseByPost(String url,Map<String, String> params) {
		Logger.i("xgh", "ApiClient.getHttpResponseByPost(),url="+url);
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String responseBody = "";
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
				httpClient = null;
			}
			
		} while (time<RETRY_TIME);
		return responseBody;
	}*/
	
	 /*private static HttpPost postForm(String url, Map<String, String> params){  
         
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
	*/
	
	
	
	
	
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
					httpClient = null;
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
	
}
