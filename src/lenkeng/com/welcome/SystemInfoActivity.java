package lenkeng.com.welcome;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;

import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import org.apache.http.conn.util.InetAddressUtils;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.provider.Settings.System;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
public class SystemInfoActivity extends Activity {
	 public static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName(
			 "com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");
	private TextView ez_ui;
	private TextView local_model;
	private TextView local_id;
	private TextView system_version;
	private TextView local_ip;
	private TextView avi_memory;
	private TextView avi_sdcard;
	private ContentResolver mContentResolver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_info);
		//initTitleView();
		mContentResolver=getContentResolver();
		ez_ui= (TextView) this.findViewById(R.id.ez_ui_version);
		local_model=(TextView) this.findViewById(R.id.local_model);
		local_id=(TextView) this.findViewById(R.id.local_id);
		system_version=(TextView) this.findViewById(R.id.system_version);
		local_ip=(TextView) this.findViewById(R.id.local_ip);
		avi_memory=(TextView) this.findViewById(R.id.local_memory);
		avi_sdcard=(TextView) this.findViewById(R.id.local_availble);
		getSystemInfo();
		
	
	}

	

	private void getSystemInfo() {
		
		ez_ui.setText("en-version-"+LKHomeUtil.getVersionNmae(getPackageName()));
		local_model.setText(Build.MODEL);
		local_id.setText(getSystemVersion());
		system_version.setText(Build.FIRMWARE);
		local_ip.setText(getLocalIpAddress());
		 avi_memory.setText(getData());
			avi_sdcard.setText(getSDCard());
		Logger.v("ez2", "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		Logger.v("ez2", "★★★★★★★★★★★★★★★★★     2014年1月22日     ★★★★★★★★★★★★★★★");
		Logger.v("ez2", "★★★★★★★★★★★★★★★★★      versionCode- "+LKHomeUtil.getVersionNmae(getPackageName())+"   ★★★★★★★★★★★★★★★");
		Logger.v("ez2", "★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
		Logger.v("ez2", "----------------------------------------------------------------------");
	}

	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return "0.0.0.0";
	}
	private String getSystemVersion(){
		String s=LKHomeUtil.getUserID();
		if(null == s){
			s="";
		}
		return s;
	}
	private String getID(){
		String s=getSystemVersion();
		if(null ==s){
			return "0";
		}else{
			s=s.substring(4);
		}
		//byte[] ba=s.getBytes(); ByteBuffer.wrap(ba).getLong()
		return  s;
	}
	// the system was reset
	public void reset(View v) {
	}
	public String  getAvailMemory() {// 获取android当前可用内存大小

		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		String s=Formatter.formatFileSize(this, mi.availMem);
		return s;
	}
	
	private String getData(){
		
		
		long value = Settings.Global.getInt(
	                mContentResolver,
	                Settings.Global.SYS_STORAGE_THRESHOLD_PERCENTAGE,
	                10);
		StatFs stat=new StatFs(Environment.getDataDirectory().getAbsolutePath());
		 
		 long blockSize   = stat.getBlockSize();
         long availBlocks = stat.getAvailableBlocks();
         long availsize   = blockSize * availBlocks;
		
		//long size=stat.getBlockSize() *stat.getAvailableBlocks();
		long total=stat.getBlockSize()*stat.getBlockCount();
		
		long low=total*value/100;
		//if(availsize > low){
			availsize=availsize-low;
		//}else{
			//availsize =0;
		//}
			Logger.d("awk", "---value----"+Formatter.formatFileSize(this, 20*1024*1024));
	   if(availsize < 20*1024*1024){
		   return "<20M";
	   }
		return Formatter.formatFileSize(this, availsize);
	}
	
	private String getSDCard(){ 
		
		 StatFs statfs = new StatFs("/mnt/sdcard");
         long blockSize   = statfs.getBlockSize();
         long availBlocks = statfs.getAvailableBlocks();
         long availsize   = blockSize * availBlocks;
         if(availsize < 20 *1024*1024){
        	 return "<20M";
         }
		return Formatter.formatFileSize(this, availsize);
	}
	
	
}
