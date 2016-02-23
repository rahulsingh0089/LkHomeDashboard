package lenkeng.com.welcome;

import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class NetSettingGuide extends Activity {
	private static final String TAG = "NetSettingGuide";
	private SharedPreferences sp;
	
	private BroadcastReceiver netStateReceiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_setting_guide);
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		
		initNetStateReceiver();
	
	}
	
	private void initNetStateReceiver() {
		
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		
		
		netStateReceiver=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
					
					if ( LKHomeUtil.isNetConnected()) {
						//develop 版本不用打开客户的app
						/* LKHomeUtil.startUserLancher(context, Constant.USER_APP_PKGNAME);*/
						finish();
					}
				}
			}
		};
		
		registerReceiver(netStateReceiver, filter);
	}

	public void wifiSetting(View v){
		Intent route_intent = new Intent(Constant.SETTING_ACTION[0]);
		route_intent.addCategory("android.intent.category.DEFAULT");
		route_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(route_intent);
		
		try {
			Settings.Global.putInt(getContentResolver(),"LKsetting_netButton_id", 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constant.IS_NEED_START=true;
		finish();
	}
	
	public void wiredSetting(View v){
		Log.e(TAG, "=====5-4==== wiredSetting()");
		//develop 版本不用打开客户的app
		//LKHomeUtil.startUserLancher(this, Constant.USER_APP_PKGNAME);
		
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(netStateReceiver);
		sp.edit().putBoolean("first", false).commit();
		super.onDestroy();
	}
}	
