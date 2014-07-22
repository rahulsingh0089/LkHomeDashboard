package lenkeng.com.welcome.util;

import com.lenkeng.bean.URLs;

import android.app.Application;
import lenkeng.com.welcome.LKHomeApp;
import lenkeng.com.welcome.R;




//鍏ㄥ眬甯搁噺绫伙紝鍙繘琛屽叏灞�父閲忕殑閰嶇疆
public class Constant {
	public static final String LKHOME_DATABASE = "AppData";
	public static final String XMPP_DATABASE_NAME = "XmppMsgdb";
	public static final String APPSTORE_TABLE_NAME = "app_store";
	public static final String CLIENT_ACTION_TAB = "client_action";
	public static final String APPSTORE_TABLE_BANNER = "appstore_banner";
	public static final String APPSTORE_TABLE_SORT = "appstore_download_sort";
	public static final String XMPP_TABALE_NAME = "xmpp_msg";
	public static final String APPINFO_TABLE_NAME = "appinfo";
	public static final String XMPP_AUTHORITY = "lenkeng.com.welcome.db.xmppdb";
	public static final String APP_AUTHORITY = "lenkeng.com.welcome.db.appinfodb";
	public static final String XMPP_URI = "content://lenkeng.com.welcome.db.xmppdb/xmpp_msg";
	public static final String APPINFO_URI = "content://lenkeng.com.welcome.db.appinfodb/appinfo";
	public static final int XMPP_VERSION = 2;
	public static final int APP_VERSION = 1;
	public static final int HANADLER_NOTICE_MSG = 0XFF01;
	public static final int HANADLER_FLASH_MSG = 0XFF02;
	public static final int HANDLER_DOWNLOAD_WEATHER = 0XFF03;
	public static final int HANDLER_DOWNLOAD_RECOMMEND_APP = 0XFF04;
	public static final int HANDLER_INSTALL_COMPLETE = 0XFF05;
	public static final String CLASSIFY_MOVIE = "4";
	public static final String CLASSIFY_GAME = "2";
	public static final String CLASSIFY_APPLICATION = "3";
	public static final String CLASSIFY_MYSELF = "myself";
	public static final String CLASSIFY_USER = "5";
	public static final String CLASSIFY_SCAN = "scan";
	public static final String CLASSIFY_SETTING = "setting";
	public static final String CLASSIFY_RECOMMEND = "recommend";
	public static final String ACTION_FLUSH_HOME = "flush";
	public static final String ACTION_INSTALED = "installed";
	public static final String ACTION_UNINSTALED = "uninstalled";
	public static final String ACTION_DOWNLOAD_COMPLETE = "download_complete";
	public static final String ACTION_IMG_DOWNLOAD_COMPLETE = "img_download_complete";
	public static final String ACTION_WEATHERDATA_DOWNLOAD_COMPLETE = "weatherdata_download_complete";
	public static final String ACTION_APPINFO_DOWNLOAD_COMPLETE = "appinfo_download_complete";
	public static final String ACTION_START_UPGRADED = "start_upgrade";
	public static final String ACTION_DOWNLOAD_FAIL = "download_fail";
	//public static final String SERVER_IP = "120.236.0.99";
	public static final int SERVER_PORT = 5222;
	public static final String MODEL_EZTV_2="eztv2";
	public static final String MODEL_EZTV_3="eztv3";
	public static final String DOWNLOADURL = "http://"+getHost()+"/AppMarket/apk/";
	public static final String DEFAULT_WEATHER_URL = "http://m.weather.com.cn/data/101010100.html";
	public static  String UPLOAD_URL = "http://"+getHost()+"/AppMarket/android/upLoadInfo.do";
	public static  String TICKET_URL = "http://"+getHost()+"/AppMarket/android/ticket.do";
	public static final String RECOMMEND_IMG = "http://"+getHost()+"/AppMarket/";
	public static final String UPGRADE_APK_URL = "http://"+getHost()+"/AppMarket";
	public static final String APPSTORE_MODE_BANNER = "appstore_recommend_banner";
	public static final String APPSTORE_MODE_SORT = "appstore_download_sort";
	public static final String APPSTORE_MODE_HOME_RECOMMEND = "home_recommend";
	public static final String APPSTORE_MODE_STANDARD = "appstore_standard";
	public static final String MORE = "鏇村";
	public static final String DEFAULT_CITY = "鍖椾含";
	public static final int PEER_PAPGE_NUMBER = 10;
	public static final boolean NEED_CHECK_VIDEOMSG=true;
	
	
	public static final int COLOR_WHITE=0XFFFFFFFF;
	public static final int COLOR_BLACK=0XFF000000;
	
	public static final String ACTION_LANGUAGE_SETTING="com.lenkeng.language";
	public static final String ACTION_NETWORK_SETTING="com.lenkeng.network";
	public static final String ACTION_SCREEN_SETTING="com.lenkeng.screen";
	public static final String ACTION_HOT_SETTING="com.lenkeng.hot";
	public static final String ACTION_APP_MARKET="com.lenkeng.appmarket";
	
	public static final int BITMAP_FORM_FILE = 1;
	public static final int BITMAP_FORM_APK = 2;
	
	
	/*public static int[] SCAN_ICONS = new int[] {
			R.drawable.ic_launcher_browser, R.drawable.scan_xinlang,
			R.drawable.scan_dangdang, R.drawable.scan_baidu,
			R.drawable.scan_youku, R.drawable.scan_tenxun,
			R.drawable.scan_wangyi, R.drawable.scan_tiexue,
			R.drawable.scan_fenghuang, R.drawable.scan_car

	};*/
	
	public static final int ORITATION_HORIZONTAL=13; //鏄剧ず鏂瑰悜_姘村钩
	public static final int ORITATION_VERTICAL=14; //鏄剧ず鏂瑰悜_绔栫洿
	
	
	public static String getHost(){
		//String host=getContext().getString(R.string.host);
		//String host="192.168.16.254:8080";
		
		return URLs.getMarketHost();
	}
	
	public static Application getContext(){
		return (Application) LKHomeApp.getAppContext();
	}
	
	
	
	/*public static final int[] SCAN_NAMES = new int[] { R.string.scan_e,
			R.string.XinLang, R.string.DangDang, R.string.BaiDu,
			R.string.YouKu, R.string.Tencent, R.string.WangYi, R.string.TieXue,
			R.string.Ifeng, R.string.TaiPinYang };*/
	/*
	 * public static int[] SETTINGS_NAME = new int[] { R.string.Net,
	 * R.string.Wifi, R.string.Show, R.string.Weather,
	 * R.string.AppManagerSetting, R.string.SystemUpdate, R.string.SystemInfo };
	 */
	public static final int[] SETTING_ICONS = new int[] {

			R.drawable.kuandaibohao, 
			R.drawable.wuxianlianjie,
			R.drawable.huamianshezhi,
			R.drawable.yingyongguanli, 
			 R.drawable.setting_speed,
			R.drawable.setting_clear,
			R.drawable.language,
			R.drawable.xitongshengji,
			R.drawable.xitongxinxi
	};
/*	public static final int[] SETTING_ICONS = new int[] {
		R.drawable.kuandaibohao, R.drawable.wuxianlianjie,
		R.drawable.huamianshezhi,R.drawable.xitongshengji,
		R.drawable.yingyongguanli, 
		R.drawable.xitongxinxi, R.drawable.setting_speed,
		R.drawable.setting_clear,R.drawable.tianqishezhi,
		R.drawable.language};
*/	
	
	public static final String[] SETTING_ACTION = new String[] {
			"com.lenkeng.network", 
			"com.lenkeng.hot", 
			"com.lenkeng.screen",
			"lenkeng.com.appManager",
			"lenkeng.com.speed", 
			"lenkeng.com.clear", 
			"lenkeng.com.language",
			"com.lenkeng.upgrade.gww",
			"lenkeng.com.systemInfo"
	};
	/*public static final String[] SETTING_ACTION = new String[] {
		"com.lenkeng.network", "com.lenkeng.hot", "com.lenkeng.screen",
		"com.lenkeng.upgrade.gww",
		"lenkeng.com.appManager","lenkeng.com.systemInfo",
		"lenkeng.com.speed", "lenkeng.com.clear", "lenkeng.com.weatherSetting","lenkeng.com.language"};*/
	
	public static final int[] ITEM_BACKS = new int[] { R.drawable.back_color_1,
			R.drawable.back_color_2, R.drawable.back_color_3,
			R.drawable.back_color_4, R.drawable.back_color_5,
			R.drawable.back_color_6, R.drawable.back_color_7,
			R.drawable.back_color_8, R.drawable.back_color_9,
			R.drawable.back_color_10 };
	/*public static final int[] BT_DEF_ICONS = new int[] {
			R.drawable.recommend_def, R.drawable.movie_def, R.drawable.app_def,
			R.drawable.game_def, R.drawable.user_def, R.drawable.setting_def };*/
	
	/*public static final int[] BIG_ICONS = new int[] { R.drawable.rec_big,
			R.drawable.movie_big, R.drawable.app_big, 
			 R.drawable.setting_big };*/
	/*public static final int[] SMALL_ICONS = new int[] { R.drawable.rec_,
			R.drawable.movie_, R.drawable.app_, R.drawable.game_,
			R.drawable.user_, R.drawable.setting_ };*/
	/*public static final int[] BUTTON_HOVERD = new int[] { R.drawable.rec_hover,
			R.drawable.movie_hover, R.drawable.app_hover,
			R.drawable.game_hover, R.drawable.user_hover,
			R.drawable.setting_hover 
			};*/

	/*public static final int[] BT_UP_ICONS = new int[] {
			R.drawable.recommend_up, R.drawable.movie_up, R.drawable.app_up,
			R.drawable.game_up, R.drawable.user_up, R.drawable.setting_up };*/
	
	/*public static final int[] DRABLES = new int[] { R.drawable.a_0,
			R.drawable.a_1, R.drawable.a_2, R.drawable.a_3, R.drawable.a_4,
			R.drawable.a_5, R.drawable.a_6, R.drawable.a_7, R.drawable.a_8,
			R.drawable.a_9, R.drawable.a_10, R.drawable.a_11, R.drawable.a_12,
			R.drawable.a_13, R.drawable.a_14, R.drawable.a_15, R.drawable.a_16,
			R.drawable.a_17, R.drawable.a_18, R.drawable.a_19, R.drawable.a_20,
			R.drawable.a_21, R.drawable.a_22, R.drawable.a_23, R.drawable.a_24,
			R.drawable.a_25, R.drawable.a_26, R.drawable.a_27, R.drawable.a_28,
			R.drawable.a_29, R.drawable.a_30, R.drawable.a_31 };*/
	
	/*public static final String[] SCANURL = new String[] { "about:blank",
			"http://www.sina.com.cn", "http://www.dangdang.com",
			"http://www.baidu.com", "http://www.youku.com",
			"http://www.qq.com", "http://www.163.com",
			"http://www.tiexue.net/", "http://www.ifeng.com/",
			"http://www.pcauto.com.cn/" };*/
	public static final String[] CLASSIFIES = new String[] { 
								CLASSIFY_RECOMMEND,
		                        CLASSIFY_MOVIE,
		                        CLASSIFY_APPLICATION, 
		                        CLASSIFY_GAME, 
		                        CLASSIFY_USER,
		                        CLASSIFY_SETTING 
    };
	
	public static final String DOWNLOAD_DATABASE = "download.db";
	public static final int DOWNLOAD_DBVERSION = 1;
	
	public static final int APK_STATE_DOWNLOADING=1;//APK正在下载
	public static final int APK_STATE_DOWNLOAD_COMPLETE=2; //APK下载完成
	
	
	public static final String UPLOAD_DATABASE = "upload.db";
	public static final int UPLOAD_DBVERSION = 1;
	public static final int UPLOAD_STATE_CANUPLOAD=1;//APK可以上传
	public static final int UPLOAD_STATE_WAITVERFY=2;//APK等待审核
	public static final int UPLOAD_STATE_VERFY_PASS=3;//APK审核通过
	public static final int UPLOAD_STATE_VERFY_FAIL=4;//APK审核不通过
	public static final int UPLOAD_STATE_EXITED = 5;//apk已经存在,不需要上传
	
	
	
}
