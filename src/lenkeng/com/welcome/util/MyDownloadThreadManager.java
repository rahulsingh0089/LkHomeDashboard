package lenkeng.com.welcome.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.server.LKService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;



public class MyDownloadThreadManager {
	private static final int RECONN_TIMES = 3;
	private static final int RECONN_PEER_TIMES = 1000 * 30;
	private File file;
	private String url;
	private Context context;
	boolean isStarted;
	private String action;
	private Handler handler;
	private int what;
	private long fileSize = 0;
	private AppInfo info;
	public MyDownloadThreadManager(Context context) {
		this.context = context;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public void setRecIndex(AppInfo info){
		this.info=info;
	}
	public void startDownload() {
		isStarted = true;
		run();
	}

	public void stopDownload() {
		isStarted = false;
		run();
	}

	public void setHandlerWhat(int what) {
		this.what = what;
	}

	public boolean isRunning() {
		return isStarted;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void run() {
		// TODO Auto-generated method stub
		int reConnectingTimes = 0;
		while (isStarted) {
			try {
				// Logger.i("gww", "----url---"+url);
				getFile(url, file);
				isStarted = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reConnectingTimes++;
				if (file != null) {
					file.delete();
				}
				if (reConnectingTimes == RECONN_TIMES) {
					isStarted = false;
					Logger.i("gww", "----------------download times---------"
							+ reConnectingTimes);
					Intent downloadFail = new Intent(context, LKService.class);
					downloadFail.setAction(Constant.ACTION_DOWNLOAD_FAIL);
					context.startService(downloadFail);
					return;
				}

				try {
					Thread.sleep(RECONN_PEER_TIMES);
					continue;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void getFile(String path, File file) throws Exception {
		URL url = new URL(path);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		InputStream is = null;
		// conn.setConnectTimeout(50000);
		if (conn.getResponseCode() == 200) {

			is = conn.getInputStream();
			fileSize = conn.getContentLength();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				Logger.i("gww", "------current download----" + len);
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
			if (conn.getContentLength() != file.length()) {
				// throw new RuntimeException();
			}
			if (null != action) {
				Intent intent = new Intent(action);
				intent.putExtra(file.getName(), fileSize);

				if (Constant.ACTION_IMG_DOWNLOAD_COMPLETE.equals(action)
						&& fileSize != 0) {
					LKService.recSize.remove(file.getName());
					LKService.recSize.put(file.getName(), fileSize);
					
				}
				context.sendBroadcast(intent);
			}
			if (null != handler) {
				switch (what) {
				case Constant.HANDLER_DOWNLOAD_RECOMMEND_APP:
					Logger.e("kao", "$$---file.size--" + file.length()
							+ "---conn.getContentLength()--" + fileSize);
					
					if (file.length() == fileSize) {
						Message msg=Message.obtain();
						msg.what=what;
						msg.obj=info;
						handler.sendMessage(msg);
					} else {
						file.delete();
					}
					break;
				case 0:
					handler.sendEmptyMessage(0);
					Logger.d("awk", "  pic msg download complete");
					break;
				default:
					break;
				}
			}
		}
	}
}
