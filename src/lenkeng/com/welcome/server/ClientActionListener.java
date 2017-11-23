package lenkeng.com.welcome.server;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import lenkeng.com.welcome.db.ClientActionDao;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
/*
 * $Id: ClientActionListener.java 4 2013-12-12 04:19:52Z kf $
 */
public class ClientActionListener extends Thread {

	private Context context;
	private ClientActionDao caDao;

	public ClientActionListener(Context context) {
		this.context = context;
		caDao = new ClientActionDao(context);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (true) {
			try {
				int rebackCode=doPost(Constant.UPLOAD_URL, getRunningAppData());
				Thread.sleep(20000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				try {
					Thread.sleep(3000);
					continue;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				e.printStackTrace();
			}
		}
	}

	private String getRunningAppData() throws Exception {
		List<String> datas = caDao.getUserRunDatas();
		JSONArray array = new JSONArray();
		for (String event : datas) {
			JSONObject obj = new JSONObject();
			String[] strs = event.split(",");

			obj.put("appname", strs[0]);
			obj.put("packagename", strs[1]);
			obj.put("times", strs[2]);

			array.put(obj);
		}
		return array.toString();
	}

	private int doPost(String stringUrl, String json) throws Exception {
		HttpPost post = new HttpPost(stringUrl);
		HttpResponse httpResponse = null;
		StringEntity entity = new StringEntity(json, HTTP.UTF_8);
		entity.setContentType("application/json");
		post.setEntity(entity);
		httpResponse = new DefaultHttpClient().execute(post);
		return httpResponse.getStatusLine().getStatusCode();
	}


	/*public static byte[] postXml(String path, String xml, String encoding)
			throws Exception {
		byte[] data = xml.getBytes(encoding);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "text/xml; charset=" + encoding);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setConnectTimeout(5 * 1000);
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		outStream.close();
		if (conn.getResponseCode() == 200) {
			// return readStream(conn.getInputStream());
		}
		return null;
	}*/

}
