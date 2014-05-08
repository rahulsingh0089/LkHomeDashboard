package lenkeng.com.welcome;

import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.util.Constant;
import android.app.Application;

public class MyApplication extends Application {
    public String username="";
	private AppDataDao appDao;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		appDao= AppDataDao.getInstance(getApplicationContext());
	}
	
}
