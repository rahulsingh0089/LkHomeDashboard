package com.lenkeng.bean;

import java.io.Serializable;

import lenkeng.com.welcome.LKHomeApp;
import lenkeng.com.welcome.R;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.app.Application;
import android.os.SystemProperties;
import android.util.Log;

public class URLs implements Serializable {
	
	private static final String TAG = "Urls";
	public  static String HTTP = "http://";
	//private  static String DOMAIN="AppMarketServer";
	private  static String DOMAIN="AppMarket";
	private  static String URL_SPLITTER = "/";
	private  static String PLAT_FORM="android";
	public  static String URL_API_HOST;
	
	
	public static String getURL_API_HOST() {
		return  HTTP + getMarketHost() + URL_SPLITTER+DOMAIN+URL_SPLITTER+PLAT_FORM;
	}

	public static void setURL_API_HOST(String uRL_API_HOST) {
		URL_API_HOST = uRL_API_HOST;
	}

	
	public static String getBASE() {
		return  HTTP + getMarketHost() + URL_SPLITTER+DOMAIN;
	}

	
/*国内版本
 * */
 
  	public static String getMarketHost(){
		
		String host="";
		if(LKHomeUtil.isFactoryMode()){
			host="192.168.16.223:8080";
		}else{
			
			if(isZhVertion()){
				host=getContext().getString(R.string.market_host_zh);
			}else{
				host=getContext().getString(R.string.market_host_en);
			}
		}
		
		
		Logger.e(TAG, "xgh ,----------------获取的market服务器="+host);
		return host;
	}
	
	/**
	 * 国外版本
	 * @return
	 */
	/*public static String getHost(){
		String firmware=getString("ro.product.firmware");
		String host="";
		if(! isZhVertion(firmware)){
			host=getContext().getString(R.string.host_zh);
		}else{
			host=getContext().getString(R.string.host_en);
		}
		
		return host;
	}*/
	
	public static String getXmppHost(){
		
		String host="";
		if(isZhVertion()){
			host=getContext().getString(R.string.xmpp_host_zh);
		}else{
			host=getContext().getString(R.string.xmpp_host_en);
		}
		
		Logger.e(TAG, "xgh ,----------------获取的xmpp服务器="+host);
		
		return host;
		
	}
  	
  	
	
	public static boolean isZhVertion( ) {
		
		String firmware=getRomVersion("ro.product.firmware");
		if(firmware!=null && firmware.contains("zh")){ //TODO...测试英文版本
			return true;
		}
		return false;
	}

	public static Application getContext(){
		return (Application) LKHomeApp.getAppContext();
		
	}
	
	//获取分类，不需要参数
	public  static String  URL_CATEGORYS;
	
	public static String getURL_CATEGORYS() {
		return getURL_API_HOST()+URL_SPLITTER+"getCategorys.do";
	}

	//分类获取应用信息，需要参数
	///android/getCategory.do?category=类别&page=N
	public  static String  URL_CATEGORY; 
	
	//推荐接口，得到5个，需要参数
	///android/getRecommended.do?category=xxx
	public  static String  URL_RECOMMEND; 
	
	//得到banner，限3条,不需要参数
	///android/getBanners.do
	public  static String  URL_BANNER; 
	
	public static String getURL_BANNER() {
		return getURL_API_HOST()+URL_SPLITTER+"getBanners.do";
	}

	public static void setURL_BANNER(String uRL_BANNER) {
		URL_BANNER = uRL_BANNER;
	}

	//热门下载,不需要参数
	///android/popuDownload.do
	public  static String  URL_POPUDOWN; 
	
	public static int getCATEGORY_APP() {
		return CATEGORY_APP;
	}

	public static void setCATEGORY_APP(int cATEGORY_APP) {
		CATEGORY_APP = cATEGORY_APP;
	}

	//搜索,需要参数
	////android/searchApp.do?key=xxxx&page=N
	public  static String  URL_SEARCH; 
	// 定制
	public  static int CATEGORY_GAME=2;
	public  static int CATEGORY_APP=3;
	public  static int CATEGORY_MOVIE=4;
	
	//游戏
	public  static String  URL_CATEGORY_GAME; 
	
	public static String getURL_SPLITTER() {
		return URL_SPLITTER;
	}

	public static void setURL_SPLITTER(String uRL_SPLITTER) {
		URL_SPLITTER = uRL_SPLITTER;
	}

	public static String getURL_RECOMMEND() {
		return getURL_API_HOST()+URL_SPLITTER+"getRecommended.do";
	}

	public static void setURL_RECOMMEND(String uRL_RECOMMEND) {
		URL_RECOMMEND = uRL_RECOMMEND;
	}

	public static String getURL_POPUDOWN() {
		return getURL_API_HOST()+URL_SPLITTER+"popuDownload.do";
	}

	public static void setURL_POPUDOWN(String uRL_POPUDOWN) {
		URL_POPUDOWN = uRL_POPUDOWN;
	}

	public static String getURL_SEARCH() {
		return getURL_API_HOST()+URL_SPLITTER+"searchApp.do";
	}

	public static void setURL_SEARCH(String uRL_SEARCH) {
		URL_SEARCH = uRL_SEARCH;
	}

	public static String getURL_CATEGORY_GAME() {
		return getURL_API_HOST()+URL_SPLITTER+"getCategory.do?category="+CATEGORY_GAME;
	}

	public static void setURL_CATEGORY_GAME(String uRL_CATEGORY_GAME) {
		URL_CATEGORY_GAME = uRL_CATEGORY_GAME;
	}

	public static String getURL_CATEGORY_MOVIE() {
		return getURL_API_HOST()+URL_SPLITTER+"getCategory.do?category="+CATEGORY_MOVIE;
	}

	public static void setURL_CATEGORY_MOVIE(String uRL_CATEGORY_MOVIE) {
		URL_CATEGORY_MOVIE = uRL_CATEGORY_MOVIE;
	}

	public static String getURL_CATEGORY_SEARCH() {
		return getURL_API_HOST()+URL_SPLITTER+"searchApp.do";
	}

	public static void setURL_CATEGORY_SEARCH(String uRL_CATEGORY_SEARCH) {
		URL_CATEGORY_SEARCH = uRL_CATEGORY_SEARCH;
	}

	public static String getURL_CATEGORY_TEST() {
		return getURL_API_HOST()+URL_SPLITTER+"getCategory.do?category="+1;
	}

	public static void setURL_CATEGORY_TEST(String uRL_CATEGORY_TEST) {
		URL_CATEGORY_TEST = uRL_CATEGORY_TEST;
	}

	public static String getURL_TICKET() {
		return getURL_API_HOST()+URL_SPLITTER+"ticket.do";
	}

	public static void setURL_TICKET(String uRL_TICKET) {
		URL_TICKET = uRL_TICKET;
	}

	public static String getURL_SYNCAPP() {
		return getURL_API_HOST()+URL_SPLITTER+"syncApp.do";
	}

	public static void setURL_SYNCAPP(String uRL_SYNCAPP) {
		URL_SYNCAPP = uRL_SYNCAPP;
	}

	public static void setURL_CATEGORYS(String uRL_CATEGORYS) {
		URL_CATEGORYS = uRL_CATEGORYS;
	}

	//应用
	public  static String  URL_CATEGORY_APP; 
	public static String getURL_CATEGORY_APP() {
		return getURL_API_HOST()+URL_SPLITTER+"getCategory.do?category="+CATEGORY_APP;
	}

	public static void setURL_CATEGORY_APP(String uRL_CATEGORY_APP) {
		URL_CATEGORY_APP = uRL_CATEGORY_APP;
	}

	//影音
	public  static String  URL_CATEGORY_MOVIE; 
	//搜索
	public  static String  URL_CATEGORY_SEARCH; 
	
	//测试
	public  static String  URL_CATEGORY_TEST; 
	
	//投票
	public  static String  URL_TICKET; 
	//public  static String  URL_TICKET="http://192.168.16.98/AppMarketServer/android/ticket.do";
	
	public  static String  URL_SYNCAPP; 
	//public  static String  URL_TICKET="http://192.168.16.98/AppMarketServer/android/syncApp.do";
	
	
	public static String getRomVersion(String property) {
		return SystemProperties.get(property, "unknown");
	}
}
