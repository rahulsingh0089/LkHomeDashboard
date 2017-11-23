package lenkeng.com.welcome.util;

import android.util.Log;





//全局LOG日志管理，打开关闭全局LOG
public class Logger {
	private static final int LOG_LEVEL=5;
	private static final int LOG_VERBOSE=0;
	private static final int LOG_DEBUG=1;
	private static final int LOG_INFO=2;
	private static final int LOG_WARN=3;
	private static final int LOG_ERROR=4;
	public static void  v(String tag,String msg){
		if(LOG_LEVEL>LOG_VERBOSE){
			Log.v(tag, msg);
		}
	}
	public static void d(String tag,String msg){
		if(LOG_LEVEL>LOG_DEBUG){
			Log.d(tag, msg);
		}
	}
	public static void i(String tag,String msg){
		if(LOG_LEVEL>LOG_INFO){
			Log.i(tag, msg);
		}
	}
	public static void w(String tag,String msg){
		if(LOG_LEVEL>LOG_WARN){
			Log.w(tag, msg);
		}
	}
	public static void e(String tag,String msg){
		if(LOG_LEVEL>LOG_ERROR){
			Log.e(tag, msg);
		}
	}
	
}
