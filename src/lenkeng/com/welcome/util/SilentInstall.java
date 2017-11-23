package lenkeng.com.welcome.util;

import java.io.File;


import lenkeng.com.welcome.R;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import android.content.pm.IPackageInstallObserver;
import com.lenkeng.api.SilentInstallListener;
import com.lenkeng.bean.ApkBean;



/**
 * 静默安装
 * 
 * @author SuenJer (-_—|) <h1>非请勿改(#‵′)</h1>
 */
public class SilentInstall {

	final static int INSTALL_COMPLETE = 1;
	final static int SUCCEEDED = 1;
	private static final String TAG = "SilentInstall";
	private ApkBean mbean;
	private static String FACTORY_MODE = "8811101214";
	Context context;
	String apkPath;
	private SilentInstallListener mListener;

	// 安装结果
	private Handler mHandler;

	public SilentInstall(Context context, String path,
			SilentInstallListener listener) {
		this.context = context;
		this.apkPath = path;
		this.mListener = listener;

		mHandler = new Handler(context.getMainLooper()) {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case INSTALL_COMPLETE:
					String packageName = (String) msg.obj;
					if (msg.arg1 == SUCCEEDED) {
						if (mListener != null) {
							mListener.onSilentInstallComplete(packageName,
									apkPath);
						}
						try {
							Intent intent = SilentInstall.this.context
									.getPackageManager()
									.getLaunchIntentForPackage(packageName);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							if (intent != null
									&& "lenkeng.com.welcome"
											.equals(packageName)) {
								SilentInstall.this.context
										.startActivity(intent);
								Log.i("gww", "home is started");
							}

						} catch (Exception e) {
							e.printStackTrace();
							LKHomeUtil.showToast(SilentInstall.this.context, R.string.text_insall_err);
						}
					} else {
						Logger.i("ken", "install faile code=" + msg.arg1);

						if (mListener != null) {
							mListener.onSlientInstallFail(packageName,msg.arg1);
						}
					}
					break;
				}
			}
		};

	}

	public SilentInstall(Context context, String path) {
		this(context, path, null);
		this.context = context;
		this.apkPath = path;
	}

	

	public void installPackage() {
		PackageInstallObserver observer = new PackageInstallObserver();
		PackageManager pm = context.getPackageManager();
		pm.installPackage(Uri.parse("file://" + apkPath), observer,
				PackageManager.INSTALL_REPLACE_EXISTING, null);
	}

	// 实现IPackageInstallObserver接口
	class PackageInstallObserver extends IPackageInstallObserver.Stub {

		public void packageInstalled(String packageName, int returnCode) {
			Message msg = mHandler.obtainMessage(INSTALL_COMPLETE);
			msg.obj = packageName;
			msg.arg1 = returnCode;
			mHandler.sendMessage(msg);
		}
	};

}
