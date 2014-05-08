package lenkeng.com.welcome.db;

import java.io.Serializable;

import com.lenkeng.bean.ApkBean;

import android.os.Parcelable;

public class DownloadBean implements Serializable{

	public DownloadBean() {
		super();
	}
	
	public DownloadBean(ApkBean apk) {
		super();
		this.packageName=apk.getPackageName();
		this.url=apk.getUrl();
		this.savePath=apk.getSavePath();
		this.size=apk.getSize();
		this.current=this.size*apk.getProgress();
		this.md5=apk.getMd5();
	}
	


	private int id;
	private String packageName;
	private String url;
	private String savePath;
	private int state;
	private long size;
	private long current;
	private String md5;
	
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String toString() {
		return "DownloadBean [id=" + id + ", packageName=" + packageName
				+ ", url=" + url + ", savePath=" + savePath + ", state="
				+ state + ", size=" + size + ", current=" + current + ", md5="
				+ md5 + "]";
	}
	
	
	
	
}
