package lenkeng.com.welcome;

import com.lenkeng.tools.ThreadPoolUtil;

import lenkeng.com.welcome.db.AppDbHelper;
import lenkeng.com.welcome.util.LKHomeUtil;
import android.app.Application;
import android.content.Context;


public class LKHomeApp extends Application {
	private static Context context;
	private static AppDbHelper helper;
	public static LKHomeUtil homeUtil;

	public void onCreate() {
		super.onCreate();
		LKHomeApp.context = getApplicationContext();
		homeUtil =  LKHomeUtil.getInstance(getApplicationContext());
		/*ThreadPoolUtil.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LKHomeUtil.systemAppClassify();
			}
		});*/
	}

	public static Context getAppContext() {
		return LKHomeApp.context;
	}

	public static AppDbHelper getAppDbHelper() {
		if (helper == null) {
			helper = new AppDbHelper(context);
		}
		return helper;
	}
}
