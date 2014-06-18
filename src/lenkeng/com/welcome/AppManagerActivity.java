package lenkeng.com.welcome;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.util.StringUtils;

import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements
		OnItemClickListener, OnItemSelectedListener {
	// private static final int MOVE_SUCCESSED=1;
	// private static final int MOVE_FAILED=2;
	private GridView gv_appList;
	private List<String> installApp;
	private MyAdapter adapter;
	// private ImageView iv_scoll_fade;
	private ViewHolder holder;
	private ViewHolder moveHolder;
	private int isSelected = -1;
	// private ViewHolder tempHolder;
	// private ImageButton ib_del;
	// private ImageButton ib_move;
	// private PopupMenu appManagerPopu;
	private String appManagerPackage = "";
	// private LayoutInflater inflater;
	private boolean isMoveSucceed = true;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			// ib_move.setEnabled(false);
			// ib_move.setBackgroundResource(R.drawable.move_to_def);
			String packageName = (String) msg.obj;
			if (isMoveSucceed) {
				LKHomeUtil.showToast(getApplicationContext(),
						R.string.moveSuccessfully);
			} else {
				LKHomeUtil
						.showToast(getApplicationContext(), R.string.moveFail);
			}
			if (isSDcardApp(packageName)) {
				// holder.bt_moveTo.setEnabled(false);
				moveHolder.tv_move.setText(getString(R.string.moveTo));
			} else {
				// holder.bt_moveTo.setEnabled(true);
				moveHolder.tv_move.setText(getString(R.string.moveToSD));
			}
			moveHolder.bt_moveTo.setEnabled(true);
			moveHolder.tv_move.setEnabled(true);
			// adapter.notifyDataSetChanged();
		};
	};

	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String packagename = (String) msg.obj;
				forceStopPackage(packagename);
				uninstallAPK(packagename);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// inflater = getLayoutInflater();
		setContentView(R.layout.app_manager);

		gv_appList = (GridView) this.findViewById(R.id.appList);
		gv_appList.setOnKeyListener(gridKeyListener);
		installApp = getUserInstallApp();
		adapter = new MyAdapter();
		gv_appList.setScrollBarFadeDuration(Integer.MAX_VALUE);
		gv_appList.setAdapter(adapter);
		gv_appList.setOnItemClickListener(this);
		gv_appList.setOnItemSelectedListener(this);
		adapter.setSelect(0,true);
		// getCacheSize("com.jingdong.app.tv");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_INSTALED);
		registerReceiver(uninstall, filter);
	}

	public void back(View v) {
		finish();
	}

	private class MyAdapter extends BaseAdapter {
		boolean isNeedDataChanage = false;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return installApp.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return installApp.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public void setSelect(int selected, boolean isNeedDataChanage) {
			if (isSelected != selected) {
				isSelected = selected;
			}
			this.isNeedDataChanage = isNeedDataChanage;
			notifyDataSetChanged();
		}

		public void setSelectNo(int selected) {
			if (isSelected != selected) {
				isSelected = selected;
			}
		}

		@Override
		public View getView(int arg0, View contentView, ViewGroup arg2) {
			// TODO Auto-generated method stub

			String packagename = installApp.get(arg0);
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = View.inflate(AppManagerActivity.this,
						R.layout.app_manager_item, null);
				holder.iv_icon = (ImageView) contentView
						.findViewById(R.id.app_manager_icon);
				holder.tv_label = (TextView) contentView
						.findViewById(R.id.app_manager_label);
				holder.ll_handle_item = (LinearLayout) contentView
						.findViewById(R.id.handle_item);
				holder.bt_delete = (ImageButton) contentView
						.findViewById(R.id.delete);
				holder.bt_moveTo = (ImageButton) contentView
						.findViewById(R.id.moveTo);
				holder.ll_appManager_up = (LinearLayout) contentView
						.findViewById(R.id.app_manager_up);
				holder.rl_bg = (LinearLayout) contentView
						.findViewById(R.id.appSelectedBg);
				holder.tv_move = (TextView) contentView
						.findViewById(R.id.tv_move);
				holder.tv_delete = (TextView) contentView
						.findViewById(R.id.tv_delete);

				holder.position = arg0;

				contentView.setTag(holder);
			} else {
				holder = (ViewHolder) contentView.getTag();
			}
			Logger.i("kao", "------  isSelected  ---" + isSelected
					+ "----  arg0  --- " + arg0);
			if (isSelected == arg0) {
				if(isNeedDataChanage){
					holder.ll_handle_item.setVisibility(View.VISIBLE);
				}else{
					holder.ll_handle_item.setVisibility(View.INVISIBLE);
				}
				holder.rl_bg
						.setBackgroundResource(R.drawable.app_manager_selector);
			} else {
				holder.ll_handle_item.setVisibility(View.INVISIBLE);
				holder.rl_bg.setBackgroundResource(0);
			}
			if (isSDcardApp(packagename)) {
				// holder.bt_moveTo.setEnabled(false);
				holder.tv_move.setText(getString(R.string.moveTo));
			} else {
				// holder.bt_moveTo.setEnabled(true);
				holder.tv_move.setText(getString(R.string.moveToSD));

			}
			// holder.bt_delete.setOnClickListener(clickListener);
			// holder.bt_moveTo.setOnClickListener(clickListener);
			holder.iv_icon.setBackgroundDrawable(LKHomeUtil
					.getIcon(packagename));
			holder.tv_label.setText(LKHomeUtil.getLabel(packagename));
			holder.bt_delete.setOnClickListener(new MyOnClick(packagename));
			holder.bt_moveTo.setOnClickListener(new MyOnClick(packagename));
			holder.bt_delete.setOnKeyListener(btKeyListener);
			holder.bt_moveTo.setOnKeyListener(btKeyListener);
			holder.bt_moveTo.setEnabled(true);
			holder.tv_delete.setOnClickListener(new MyOnClick(packagename));
			holder.tv_move.setOnClickListener(new MyOnClick(packagename));
			holder.bt_delete.setTag(holder);
			holder.bt_moveTo.setTag(holder);
			holder.tv_move.setTag(holder);
			holder.tv_move.setEnabled(true);
			contentView.setOnHoverListener(hoverListener);
			return contentView;
		}
	}

	static class ViewHolder {

		ImageView iv_icon;
		TextView tv_label;
		LinearLayout ll_handle_item;
		ImageButton bt_moveTo;
		ImageButton bt_delete;
		LinearLayout ll_appManager_up;
		LinearLayout rl_bg;
		TextView tv_move;
		TextView tv_delete;
		int position;
	}

	class MyOnClick implements OnClickListener {
		String packagename;

		public MyOnClick(String packagename) {
			this.packagename = packagename;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.delete || v.getId() == R.id.tv_delete) {
				Message msg = Message.obtain();
				msg.obj = packagename;
				handler2.sendMessage(msg);

			} else if (v.getId() == R.id.moveTo || v.getId() == R.id.tv_move) {
				moveHolder = (ViewHolder) v.getTag();
				moveApptoSdcard(packagename);
				moveHolder.bt_moveTo.setEnabled(false);
				moveHolder.tv_move.setEnabled(false);
				moveHolder.tv_move.setText(getString(R.string.moving));
			}
		}

	}

	OnHoverListener hoverListener = new OnHoverListener() {

		@Override
		public boolean onHover(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			try {
				ViewHolder holder_ = (ViewHolder) v.getTag();
				String packagename = installApp.get(holder_.position);
				/*
				 * if(holder_ !=null ){
				 * holder_.ll_handle_item.setVisibility(View.INVISIBLE); }
				 * holder_= (ViewHolder) v.getTag(); if(event.getAction() ==
				 * MotionEvent.ACTION_HOVER_ENTER){
				 * holder_.ll_handle_item.setVisibility(View.VISIBLE); }else
				 * if(event.getAction() == MotionEvent.ACTION_HOVER_EXIT){
				 * holder_.ll_handle_item.setVisibility(View.INVISIBLE); }
				 */

				switch (event.getAction()) {
				case MotionEvent.ACTION_HOVER_ENTER:
					// if(v.isHovered()){
					if (packagename != null
							&& !getAppManagerPackage().equals(packagename)) {
						// disMissAppManagerPopu(packagename);
						holder.ll_appManager_up.setBackgroundResource(0);
					}
					holder_.ll_appManager_up
							.setBackgroundResource(R.drawable.app_manager_selector);
					// }
					break;
				case MotionEvent.ACTION_HOVER_EXIT:
					holder_.ll_appManager_up.setBackgroundResource(0);

					// holder_.ll_handle_item.setVisibility(View.INVISIBLE);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		/*
		 * ViewHolder holder = (ViewHolder) view.getTag();
		 * holder.ll_appManager_up.setBackgroundResource(0); //
		 * showAppManagerPopu(view, installApp.get(position));
		 * initAppManagerPopu(view);
		 */

		/*
		 * if(holder !=null){ holder.ll_appManager_up.setBackgroundResource(0);
		 * }
		 */
		Logger.e("fra", "---- app manager selection  "+position);
		holder = (ViewHolder) view.getTag();
		// seleHolder=(ViewHolder) view.getTag();
		adapter.setSelect(position,false);
		// holder.ll_appManager_up.setBackgroundResource(R.drawable.app_manager_selector);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		// uninstallAPK(installApp.get(position));
		// adapter.setSelect(position);
		/*
		 * ViewHolder holder = (ViewHolder) arg1.getTag();
		 * holder.ll_appManager_up.setBackgroundResource(0); //
		 * showAppManagerPopu(arg1, installApp.get(position));
		 * initAppManagerPopu(arg1);
		 */
		adapter.setSelect(position,true);
		Logger.i("kao", "---------   AppManager  onItemClick  ----  "
				+ position);
	}

	/**
	 * uninstall apk file
	 * 
	 * @param packageName
	 */
	public void uninstallAPK(String packageName) {
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE,
				packageURI);
		uninstallIntent.putExtra(Intent.EXTRA_UNINSTALL_ALL_USERS, true);
		startActivity(uninstallIntent);

	}

	private BroadcastReceiver uninstall = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String str = intent.getStringExtra("installFlag");
			if (str != null && "uninstall".equals(str)) {
				installApp = getUserInstallApp();
				adapter.notifyDataSetChanged();
				/*
				 * if (appManagerPopu != null) { appManagerPopu.dismiss(); }
				 */
			}

			Logger.e(
					"tag",
					"$$$-----uninstall-----"
							+ intent.getStringExtra("installFlag"));
		}
	};

	public List<String> getUserInstallApp() {
		List<String> userApp = new ArrayList<String>();
		PackageManager manager = getApplicationContext().getPackageManager();
		List<PackageInfo> infos = manager.getInstalledPackages(0);
		for (PackageInfo packInfo : infos) {
			String pName = packInfo.packageName;

			if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {

			} else {
				userApp.add(pName);
			}
		}
		return userApp;
	}

	/*
	 * public void getCacheSize(final String packageName) { PackageManager
	 * pManager = getPackageManager(); pManager.getPackageSizeInfo(packageName,
	 * new IPackageStatsObserver.Stub() {
	 * 
	 * @Override public void onGetStatsCompleted(PackageStats pStats, boolean
	 * succeeded) throws RemoteException { // TODO Auto-generated method stub
	 * long size = pStats.cacheSize; long data = pStats.dataSize; long code =
	 * pStats.codeSize; String s = Formatter.formatFileSize(
	 * AppManagerActivity.this, size + data + code); Logger.i("gww3",
	 * "----s-----" + s); } }); }
	 */

	/*
	 * class PackageMoveObserver extends { public void packageMoved(String
	 * packageName, int returnCode) throws RemoteException { final Message msg =
	 * mHandler.obtainMessage(PACKAGE_MOVE); msg.arg1 = returnCode;
	 * mHandler.sendMessage(msg); } }
	 */

	/*
	 * @Override public void setOutsideTouchable(boolean touchable) { // TODO
	 * Auto-generated method stub super.setOutsideTouchable(true); }
	 * 
	 * @Override public void setAnimationStyle(int animationStyle) { // TODO
	 * Auto-generated method stub super.setAnimationStyle(R.style.popu); }
	 * 
	 * @Override public void setBackgroundDrawable(Drawable background) { //
	 * TODO Auto-generated method stub super.setBackgroundDrawable(new
	 * ColorDrawable(0)); }
	 */

	/*
	 * public void showAppManagerPopu(final View view, final String packagename)
	 * { if (view == null) { return; } final int RIGHT_COUNT = 0; final int
	 * LEFT_COUNT = 0; disMissAppManagerPopu(packagename);
	 * setAppManagerPackage(packagename); // appManagerPopu.showAsDropDown(v);
	 * if (isSDcardApp(packagename)) { ib_move.setEnabled(false);
	 * 
	 * } else { ib_move.setEnabled(true); } ib_del.setOnClickListener(new
	 * View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub Uri uri = Uri.parse("package:" + packagename); Intent intent = new
	 * Intent(Intent.ACTION_DELETE, uri); startActivity(intent); } });
	 * 
	 * ib_move.setOnClickListener(new View.OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub // 这里是移动到设备操作 moveApptoSdcard(packagename); } });
	 * 
	 * }
	 */

	public void disMissAppManagerPopu(String pacakgename) {
		if (!getAppManagerPackage().equals(pacakgename)) {
			/*
			 * if (appManagerPopu != null) { appManagerPopu.dismiss(); }
			 */
		}
	}

	public void setAppManagerPackage(String packagename) {
		this.appManagerPackage = packagename;
	}

	public String getAppManagerPackage() {
		return appManagerPackage;
	}

	public void moveApptoSdcard(String packagename) {

		try {
			PackageManager mPm = getPackageManager();
			/*
			 * if (mPackageMoveObserver == null) { mPackageMoveObserver = new
			 * PackageMoveObserver(); }
			 */
			
			ApplicationInfo info = mPm.getPackageInfo(packagename,
					PackageManager.GET_UNINSTALLED_PACKAGES).applicationInfo;
			int moveFlags = (info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0 ? PackageManager.MOVE_INTERNAL
					: PackageManager.MOVE_EXTERNAL_MEDIA;
			mPm.movePackage(packagename, moveObserver, moveFlags);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isSDcardApp(String packageName) {
		PackageManager pm = getPackageManager();
		ApplicationInfo appInfo;
		try {
			appInfo = pm.getApplicationInfo(packageName, 0);

			if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	IPackageMoveObserver moveObserver = new IPackageMoveObserver.Stub() {

		@Override
		public void packageMoved(String packageName, int returnCode)
				throws RemoteException {
			// TODO Auto-generated method stub
			if (returnCode == PackageManager.MOVE_SUCCEEDED) {
				isMoveSucceed = true;
			} else {
				isMoveSucceed = false;
			}
			Message msg = Message.obtain();
			msg.obj = packageName;
			handler.sendMessage(msg);

		}
	};

	int DELETE_COUNTER = 0;
	int MOVE_COUNTER = 1;
	private OnKeyListener btKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			Logger.i("tag", "------------v------");
			if (v.getId() == R.id.delete
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
						|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
						|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					DELETE_COUNTER++;
					if (DELETE_COUNTER == 2) {
						holder = (ViewHolder) v.getTag();
						holder.ll_handle_item.setVisibility(View.INVISIBLE);
						holder.rl_bg.setBackgroundResource(0);
						DELETE_COUNTER = 1;
					}
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					DELETE_COUNTER = 1;
				}
			} else if (v.getId() == R.id.moveTo
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
						|| keyCode == KeyEvent.KEYCODE_DPAD_LEFT
						|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					MOVE_COUNTER++;
					if (MOVE_COUNTER == 2) {
						holder = (ViewHolder) v.getTag();
						holder.ll_handle_item.setVisibility(View.INVISIBLE);
						holder.rl_bg.setBackgroundResource(0);
						MOVE_COUNTER = 1;
					}
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					MOVE_COUNTER = 1;
				}
			}
			Logger.i("tag", "----DELETE_COUNTER---" + DELETE_COUNTER
					+ "------MOVE_COUNTER-----" + MOVE_COUNTER);
			return false;
		}
	};

	private OnKeyListener gridKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Logger.i("tag", "-grid---DELETE_COUNTER---" + DELETE_COUNTER
					+ "------MOVE_COUNTER-----" + MOVE_COUNTER);
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				AppManagerActivity.this.finish();
			}

			if (v instanceof GridView) {
				View tempView = ((GridView) v).getSelectedView();
				if (tempView != null) {
					holder = (ViewHolder) ((GridView) v).getSelectedView()
							.getTag();
				}
				if (holder.ll_handle_item.getVisibility() == View.VISIBLE) {
					holder.bt_moveTo.requestFocus();
					return true;

				} else {
					return false;
				}
			} else {
				return false;
			}

		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(uninstall);
	}

	private void forceStopPackage(String pkgName) {
		// ActivityManager am = (ActivityManager)
		// getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager am = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(pkgName)
					&& info.baseActivity.getPackageName().equals(pkgName)) {
				// find it, break
				am.forceStopPackage(pkgName);
				break;
			}
		}
	}
	public void clearDefault(View v){
		PackageManager manager=getPackageManager();
		List<PackageInfo> infos=manager.getInstalledPackages(0);
		for(PackageInfo info:infos){
			manager.clearPackagePreferredActivities(info.packageName);
			//Logger.d("awk", "-----  clearPackagePreferredActivities  "+info.packageName );
		}
		Toast.makeText(this, getString(R.string.clearCompleted), 0).show();
	}
}
