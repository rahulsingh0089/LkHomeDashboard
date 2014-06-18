package lenkeng.com.welcome;

import java.lang.reflect.Method;

import java.util.List;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ClearActivity extends Activity {
	private static final int GET_CACHE_COMPLETE=0;
	private static final int CLEAR_CACHE_COMPLETE=1;
	private  int TOTAL_CACHE=0;
	private TextView tv_memory;
	private TextView tv_worn;
	private TextView tv_clear_complete;
	private ProgressBar pb;
	private Button bt_clear;
	List<PackageInfo> packageInfos ;
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_CACHE_COMPLETE :
				tv_memory.setText(getString(R.string.text_can_releast)+Formatter.formatFileSize(ClearActivity.this, TOTAL_CACHE));
				break;
			case CLEAR_CACHE_COMPLETE:
				bt_clear.setEnabled(true);
				pb.setVisibility(View.GONE);
				tv_worn.setVisibility(View.INVISIBLE);
				tv_clear_complete.setVisibility(View.VISIBLE);
				tv_memory.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clear);
		tv_memory = (TextView) this.findViewById(R.id.memory);
		pb = (ProgressBar) this.findViewById(R.id.loading_process);
		pb.setVisibility(View.GONE);
		try {
			getAllAppCache(this.getClass().getMethod("getCacheSize", String.class));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tv_worn =(TextView) this.findViewById(R.id.worn);
		tv_clear_complete=(TextView) this.findViewById(R.id.clear_complete);
		
	}

	public void clear(View v) {
		if(tv_clear_complete.getVisibility()==View.VISIBLE){
			
			return;
		}
		bt_clear=(Button) v;
		bt_clear.setEnabled(false);
		pb.setVisibility(View.VISIBLE);
		try {
			getAllAppCache(this.getClass().getMethod("clearCache", String.class));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void back(View v) {
		this.finish();
	}

	public void clearCache(String packageName) {
		PackageManager pManager = getPackageManager();
		// pManager.deleteApplicationCacheFiles(packageName, null);
	
		Method[] ms = PackageManager.class.getMethods();
		for (int i = 0; i < ms.length; i++) {

			if (!"org.xbmc.xbmc".equals(packageName) && "deleteApplicationCacheFiles".equals(ms[i].getName()) ) {
				try {
					ms[i].invoke(pManager, new Object[] { packageName,dataObserver });
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	int tempCounter=0;
	IPackageDataObserver dataObserver=new IPackageDataObserver.Stub() {
		
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub
			tempCounter++;
			if(packageInfos.size()==tempCounter){
				tempCounter=0;
				handler.sendEmptyMessage(CLEAR_CACHE_COMPLETE);
			}
		}
	};
	public void getCacheSize(final String packageName) {
		PackageManager pManager = getPackageManager();
		pManager.getPackageSizeInfo(packageName,
				new IPackageStatsObserver.Stub() {

					@Override
					public void onGetStatsCompleted(PackageStats pStats,
							boolean succeeded) throws RemoteException {
						// TODO Auto-generated method stub
						long size = pStats.cacheSize;
						long data = pStats.dataSize;
						long code = pStats.codeSize;
						
						TOTAL_CACHE+=size;
						handler.sendEmptyMessage(GET_CACHE_COMPLETE);
					}
				});
	}

	private void getAllAppCache(Method m) {
		PackageManager manager = getPackageManager();
		packageInfos= manager.getInstalledPackages(0);
		
		for (PackageInfo packInfo : packageInfos) {
			String pName = packInfo.packageName;
			if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {

			} else {
				
			}
			try {
				m.invoke(this, new Object[]{pName});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static class ScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			PackageManager manager = context.getPackageManager();
			List<PackageInfo> packageInfos= manager.getInstalledPackages(0);
				for (PackageInfo packInfo : packageInfos) {
					String pName = packInfo.packageName;
					try {
						if(!"org.xbmc.xbmc".equals(pName)){
							manager.deleteApplicationCacheFiles(pName, null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}
			
		}
	}
}
