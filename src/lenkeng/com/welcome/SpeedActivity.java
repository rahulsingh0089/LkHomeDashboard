package lenkeng.com.welcome;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.bean.TaskInfo;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Instrumentation;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SpeedActivity extends Activity implements OnItemClickListener,
		OnCheckedChangeListener, OnClickListener {
	private String TAG = "SpeedActivity";
	private static final int GET_CACHE_COMPLETE = 0;
	// private static final int KILL_TASK_COMPLETE = 1;
	private static final long TOTAL_SIZE = 1024 * 1024 * 900;
	// private long killCacheSize = 0;
	private ListView lv_task;
	private MyAdapter adapter;
	private LayoutInflater inflater;
	private List<TaskInfo> infos;
	private List<TaskInfo> selectedInfos;
	private ActivityManager aManager;
	private CheckBox cb_choiceAll;
	private RelativeLayout rl_mycheck;
	private TextView tv_speed_succedd;
	private TextView tv_no_user_task;
	private LinearLayout buttom_bt;
	private ProgressBar pb;
	private Button bt_speed;
	private Button bt_back;
	List<PackageInfo> packageInfos;
	private long systemMemory = 0;

	private Handler handler = new Handler() {

		@SuppressLint("NewApi")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case GET_CACHE_COMPLETE:
				pb.setVisibility(View.GONE);
				// cb_choiceAll.setVisibility(View.GONE);
				rl_mycheck.setVisibility(View.GONE);
				lv_task.setVisibility(View.GONE);
				tv_speed_succedd.setVisibility(View.VISIBLE);

				tv_speed_succedd.setText(SpeedActivity.this
						.getString(R.string.text_speed_success_start)
						+ Formatter.formatFileSize(SpeedActivity.this, ram)
						+ SpeedActivity.this
								.getString(R.string.text_speed_success_middle)
						+ LKHomeUtil.formatPercent(ram, TOTAL_SIZE)
						+ SpeedActivity.this
								.getString(R.string.text_speed_success_end));
				ram = 0;
				break;

			default:
				break;
			}
		};
	};

	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speed);
		/*
		 * IntentFilter filter=new IntentFilter();
		 * filter.addAction(Intent.ACTION_SCREEN_OFF); registerReceiver(new
		 * ScreenReceiver(), filter);
		 */
		inflater = getLayoutInflater();
		aManager = (ActivityManager) getSystemService(Service.ACTIVITY_SERVICE);
		initView();

	}

	private void initView() {
		bt_back = (Button) this.findViewById(R.id.bt_back);
		bt_speed = (Button) this.findViewById(R.id.bt_speed);
		bt_speed.setOnFocusChangeListener(focusChangeListener);
		bt_speed.setOnHoverListener(hoverListener);
		bt_speed.requestFocus();
		bt_back.setOnFocusChangeListener(focusChangeListener);
		bt_back.setOnHoverListener(hoverListener);
		lv_task = (ListView) this.findViewById(R.id.lv_task);
		lv_task.setOnItemClickListener(this);
		cb_choiceAll = (CheckBox) this.findViewById(R.id.selected_all);
		rl_mycheck = (RelativeLayout) this.findViewById(R.id.my_check);
		cb_choiceAll.setOnCheckedChangeListener(this);
		cb_choiceAll.setOnClickListener(this);
		tv_speed_succedd = (TextView) this.findViewById(R.id.speed_succed);
		selectedInfos = new ArrayList<TaskInfo>();
		pb = (ProgressBar) this.findViewById(R.id.loading_process);
		tv_no_user_task = (TextView) this.findViewById(R.id.no_user_task);
		buttom_bt = (LinearLayout) this.findViewById(R.id.buttom_bt);
		pb.setVisibility(View.GONE);
		infos = getRunningTaskInfos();
		if (infos.size() == 0) {
			tv_no_user_task.setVisibility(View.VISIBLE);
			// cb_choiceAll.setVisibility(View.INVISIBLE);
			rl_mycheck.setVisibility(View.INVISIBLE);
			buttom_bt.setVisibility(View.INVISIBLE);
		}
		adapter = new MyAdapter();
		lv_task.setAdapter(adapter);
		cb_choiceAll.performClick();
	}

	OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (bt_back.isFocused()) {
				bt_back.setBackgroundResource(R.drawable.clear_bt_back);
				bt_speed.setBackgroundResource(R.drawable.clear_def);
			} else if (bt_speed.isFocused()) {
				bt_back.setBackgroundResource(R.drawable.clear_bt_back_def);
				bt_speed.setBackgroundResource(R.drawable.clear_now);
			} else {
				bt_back.setBackgroundResource(R.drawable.clear_bt_back_selector);
				bt_speed.setBackgroundResource(R.drawable.clear_bt_clear);
			}
		}
	};
	OnHoverListener hoverListener = new OnHoverListener() {

		@Override
		public boolean onHover(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			/*
			 * if(bt_back.isHovered()){
			 * bt_back.setBackgroundResource(R.drawable.clear_bt_back);
			 * bt_speed.setBackgroundResource(R.drawable.clear_def); }else
			 * if(bt_speed .isHovered()){
			 * bt_back.setBackgroundResource(R.drawable.clear_bt_back_def);
			 * bt_speed.setBackgroundResource(R.drawable.clear_now); }
			 * Logger.i("gww3", "-----------------------hover--------");
			 * bt_back.setBackgroundResource(R.drawable.clear_bt_back_selector);
			 * bt_speed.setBackgroundResource(R.drawable.clear_bt_clear);
			 */

			return false;
		}
	};

	private void killBackgroundProcess() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		// 获得正在运行的所有进程
		List<TaskInfo> taskInfos = getRunningTaskInfos();
		for (ActivityManager.RunningAppProcessInfo info : taskInfos) {
			am.killBackgroundProcesses(info.processName);
		}
	}

	public List<TaskInfo> getRunningTaskInfos() {
		List<TaskInfo> infos = new ArrayList<TaskInfo>();
		ActivityManager manager = (ActivityManager) getSystemService(Service.ACTIVITY_SERVICE);
		PackageManager pManager = getPackageManager();
		List<RunningAppProcessInfo> rapInfos = manager.getRunningAppProcesses();

		TaskInfo ti;
		for (RunningAppProcessInfo rap : rapInfos) {
			ApplicationInfo ai = null;
			try {
				ai = pManager.getPackageInfo(rap.processName, 0).applicationInfo;
				String packageName = rap.processName;
				if (isSystemTask(ai) && !"system".equals(packageName)
						&& !"lenkeng.com.welcome".equals(packageName)
						|| "com.android.music".equals(packageName)) {
					if( isInputMethodApp(SpeedActivity.this, packageName)){
						continue;
					}
					ti = new TaskInfo();
					ti.setTaskname(ai.loadLabel(pManager).toString());
					ti.setTaskicon(ai.loadIcon(pManager));
					ti.setPid(rap.pid);
					ti.setPackname(packageName);
					MemoryInfo[] memoryInfos = manager
							.getProcessMemoryInfo(new int[] { rap.pid });
					int memorysize = memoryInfos[0].getTotalPrivateDirty();
					ti.setMemorysize(memorysize * 1024);
					infos.add(ti);
				} else {

					MemoryInfo[] memoryInfos = manager
							.getProcessMemoryInfo(new int[] { rap.pid });
					int memorysize = memoryInfos[0].getTotalPrivateDirty();
					systemMemory += memorysize;

				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				String packname = rap.processName;
				/*
				 * if (!"system".equals(packname)&& isSystemTask(ai)) { ti = new
				 * TaskInfo(); ti.setPackname(packname);
				 * ti.setTaskname(packname); Drawable appicon =
				 * getResources().getDrawable( R.drawable.ic_launcher);
				 * ti.setTaskicon(appicon); int pid = rap.pid; ti.setPid(pid);
				 * MemoryInfo[] memoryinfos = manager .getProcessMemoryInfo(new
				 * int[] { pid }); int memorysize =
				 * memoryinfos[0].getTotalPrivateDirty();
				 * ti.setMemorysize(memorysize); //infos.add(ti); }
				 */
			}
		}
		return infos;
	}

	public static boolean isSystemTask(ApplicationInfo info) {

		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = inflater.inflate(R.layout.task_item, null);
				vh.iv_task_icon = (ImageView) convertView
						.findViewById(R.id.task_icon);
				vh.tv_task_name = (TextView) convertView
						.findViewById(R.id.task_name);
				vh.cb_task_isSelected = (CheckBox) convertView
						.findViewById(R.id.cb_task_checked);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			TaskInfo info = infos.get(position);
			vh.iv_task_icon.setBackgroundDrawable(info.getTaskicon());
			vh.tv_task_name.setText(info.getTaskname());
			if (info.isIschecked()) {
				vh.cb_task_isSelected.setChecked(true);
			} else {
				vh.cb_task_isSelected.setChecked(false);
			}
			return convertView;
		}
	}

	static class ViewHolder {
		ImageView iv_task_icon;
		TextView tv_task_name;
		CheckBox cb_task_isSelected;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		cb_choiceAll.setChecked(false);
		CheckBox cb_selected = (CheckBox) view
				.findViewById(R.id.cb_task_checked);
		if (!cb_selected.isChecked()) {
			cb_selected.setChecked(true);
			infos.get(position).setIschecked(true);
			selectedInfos.add((TaskInfo) adapter.getItem(position));
		} else {
			cb_selected.setChecked(false);
			infos.get(position).setIschecked(false);
			selectedInfos.remove((TaskInfo) adapter.getItem(position));
		}
		if(infos.size()==selectedInfos.size()){
			cb_choiceAll.setChecked(true);
		}
	
	}

	public void back(View v) {
		this.finish();
	}

	int ram = 0;

	public void speed(View v) {
		/*
		 * for(int i=0;i<temp;i++){ TaskInfo info=infos.get(i);
		 * if(info.isIschecked()){
		 * aManager.killBackgroundProcesses(info.getPackname()); }else{
		 * info.setIschecked(false); } }
		 */
		if (selectedInfos.size() != 0) {
			pb.setVisibility(View.VISIBLE);
		} else {
			LKHomeUtil.showToast(getApplicationContext(),
					R.string.text_can_noreleast);
			return;
		}
		int infoSize = infos.size();
		for (int i = 0; i < selectedInfos.size(); i++) {

			TaskInfo info = selectedInfos.get(i);
			forceStopPackage(info.getPackname());
			aManager.killBackgroundProcesses(info.getPackname());
			infos.remove(info);
			ram += info.getMemorysize();
			Logger.e(
					"kao",
					"----packagename--" + info.getPackname() + "--pid---  "
							+ info.getPid() + "----mem--  "
							+ info.getMemorysize());
			// int size=getAvailMemory(info.getPid());
		}
		adapter.notifyDataSetChanged();
		// killBackgroundProcess() ;
		// queryToatalCache();
		if (cb_choiceAll.isChecked() || selectedInfos.size() == infoSize) {
			Message msg = Message.obtain();
			msg.what = GET_CACHE_COMPLETE;
			handler.sendMessage(msg);
		} else {
			pb.setVisibility(View.GONE);
			Toast.makeText(
					this,
					SpeedActivity.this
							.getString(R.string.text_speed_success_start)
							+ Formatter.formatFileSize(SpeedActivity.this, ram)
							+ SpeedActivity.this
									.getString(R.string.text_speed_success_middle)
							+ LKHomeUtil.formatPercent(ram, TOTAL_SIZE)
							+ SpeedActivity.this
									.getString(R.string.text_speed_success_end),
					0).show();
			// LKHomeUtil.showToast(getApplicationContext(),
			// R.string.text_speed_success_start);
			ram = 0;
		}
		selectedInfos.clear();

	}

	@SuppressLint("NewApi")
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}

	public static String formatSize(long size) {

		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}

	/*
	 * if (mClearCacheObserver == null) { mClearCacheObserver = new
	 * ClearCacheObserver(); } mPm.deleteApplicationCacheFiles(packageName,
	 * mClearCacheObserver);
	 * 
	 * class ClearCacheObserver extends IPackageDataObserver.Stub { public void
	 * onRemoveCompleted(final String packageName, final boolean succeeded) {
	 * final Message msg = mHandler.obtainMessage(CLEAR_CACHE); msg.arg1 =
	 * succeeded ? OP_SUCCESSFUL:OP_FAILED; mHandler.sendMessage(msg); } }
	 */

	int tempCounter = 0;

	public void getCacheSize(final String packageName, final int index) {
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

						long total = size + data + code;

						/*
						 * if(index ==0){ }else if(index ==1){
						 * 
						 * }
						 */
						// killCacheSize+=total;

						if (packageInfos.size() == tempCounter) {
							tempCounter = 0;
							Message msg = Message.obtain();
							msg.what = GET_CACHE_COMPLETE;
							handler.sendMessage(msg);

						}

					}
				});
	}

	public int getAvailMemory(int pid) {// 获取android当前可用内存大小

		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		int[] pids = new int[] { pid };
		MemoryInfo[] info = am.getProcessMemoryInfo(pids);
		int size = info[0].getTotalPss();
		int totalPri = info[0].getTotalPrivateDirty();
		int totalSha = info[0].getTotalSharedDirty();
		int naPri = info[0].nativePrivateDirty;
		int naP = info[0].nativePss;
		int naSha = info[0].nativeSharedDirty;
		int dalPri = info[0].dalvikPrivateDirty;
		int dalP = info[0].dalvikPss;
		int dalSha = info[0].dalvikSharedDirty;
		int oPri = info[0].otherPrivateDirty;
		int oP = info[0].otherPss;
		int oSha = info[0].otherSharedDirty;
		Logger.e("kao", "---TotalPss---" + size + "---totalPri--  " + totalPri
				+ "---totalSha--- " + totalSha);
		Logger.e("kao", "---naPri---" + naPri + "---naP--  " + naP
				+ "---naSha--- " + naSha);
		Logger.e("kao", "---dalPri---" + dalPri + "---dalP--  " + dalP
				+ "---dalSha--- " + dalSha);
		Logger.e("kao", "---oPri---" + oPri + "---oP--  " + oP + "---oSha--- "
				+ oSha);
		Logger.e("kao", "---info[].length---   " + info.length);
		// android.app.ActivityManager.MemoryInfo mi = new
		// android.app.ActivityManager.MemoryInfo();
		// am.getMemoryInfo(mi);

		return size;
	}

	// 获得总内存
	public static long getmem_TOLAL() {
		long mTotal;
		// /proc/meminfo读出的内核信息进行解释
		String path = "/proc/meminfo";
		String content = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path), 8);
			String line;
			if ((line = br.readLine()) != null) {
				content = line;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// beginIndex
		int begin = content.indexOf(':');
		// endIndex
		int end = content.indexOf('k');
		// 截取字符串信息

		content = content.substring(begin + 1, end).trim();
		mTotal = Integer.parseInt(content);
		return mTotal;
	}

	private void queryToatalCache() {
		PackageManager manager = getPackageManager();
		packageInfos = manager.getInstalledPackages(0);
		for (PackageInfo packInfo : packageInfos) {
			String pName = packInfo.packageName;
			getCacheSize(pName, 0);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (cb_choiceAll.isChecked()) {
			for (int i = 0; i < infos.size(); i++) {
				TaskInfo info = infos.get(i);
				info.setIschecked(true);
				selectedInfos.add(info);
			}
		} else {
			for (int i = 0; i < infos.size(); i++) {
				TaskInfo info = infos.get(i);
				info.setIschecked(false);
				selectedInfos.clear();
			}
		}
		adapter.notifyDataSetChanged();
	}

	private void forceStopPackage(String pkgName) {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		am.forceStopPackage(pkgName);
	}

	public ScreenReceiver getScreenReceiver() {
		return new ScreenReceiver();
	}

	public class ScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// killBackgroundProcess();
			if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				ActivityManager manager = (ActivityManager) context
						.getSystemService(Service.ACTIVITY_SERVICE);
				PackageManager pManager = context.getPackageManager();
				List<RunningAppProcessInfo> rapInfos = manager
						.getRunningAppProcesses();
				pause();

				for (int i = 0; i < rapInfos.size(); i++) {
					try {
						ApplicationInfo ai = null;
						RunningAppProcessInfo rap = rapInfos.get(i);
						ai = pManager.getPackageInfo(rap.processName, 0).applicationInfo;
						String packageName = rap.processName;
						
						if (isSystemTask(ai)|| LKHomeUtil.appStyles.containsKey(packageName)) {
							if ("system".equals(packageName)|| isInputMethodApp(context, packageName)) {
								continue;
							}
							manager.forceStopPackage(packageName);
							manager.killBackgroundProcesses(packageName);
							Logger.e("kao", "-----speed----packagename---"
									+ packageName);
						}
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// pause();
			} else {
				// start();

			}

		}

	}

	private KeyEvent simulateKeyEvent(int action, int keyCode) {
		KeyEvent event = new KeyEvent(action, keyCode);
		return event;

	}

	private long getRunMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	private void pause() {
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");

		LKHomeApp.getAppContext().sendBroadcast(i);
	}

	private void start() {
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "play");
		LKHomeApp.getAppContext().sendBroadcast(i);
	}

	public static boolean isInputMethodApp(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		boolean isInputMethodApp = false;
		try {
			PackageInfo pkgInfo = pm.getPackageInfo(packageName,
					PackageManager.GET_SERVICES);
			ServiceInfo[] sInfo = pkgInfo.services;
			if (sInfo != null) {
				for (int i = 0; i < sInfo.length; i++) {
					ServiceInfo serviceInfo = sInfo[i];
					if (serviceInfo.permission != null
							&& serviceInfo.permission
									.equals("android.permission.BIND_INPUT_METHOD")) {
						isInputMethodApp = true;
						break;
					}
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isInputMethodApp;
	}

}
