package com.lenkeng.bean;

import java.io.Serializable;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.DownloadBean;

import android.os.Parcel;
import android.os.Parcelable;

public class ApkBean  implements Parcelable{
	
	public static final int STATE_START=0;
	public static final int STATE_DOWNLOADING=1;
	public static final int STATE_COMPLETE=2;
	public static final int STATE_ERR=3;
	public static final int STATE_PROGRESS=4;
	public static final int STATE_QUEUE_ENOUNGH=5;
	public static final int STATE_ERR_NO_SPACE=6;
	public static final int STATE_PAUSED=7;
	
	
	
	private String url;
	private String savePath;
	//0: start
	//1:downloading
	//2:complete
	//3:error
	//4:progress
	//5:downloding queue is Enough
	
	private int status;
	private String name;
	private String packageName;
	private long progress;
	private String md5;
	private long size;
	private String hdIcon;
	//private String style;
	private String category;
	private long current;
	private long realSize;
	
	
	
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	
	
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getHdIcon() {
		return hdIcon;
	}

	public void setHdIcon(String hdIcon) {
		this.hdIcon = hdIcon;
	}


	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	
	
	public long getRealSize() {
		return realSize;
	}

	public void setRealSize(long realSize) {
		this.realSize = realSize;
	}

	public ApkBean(){
		
	}
	
  /* public ApkBean(DownloadBean dbean){
		this.packageName=dbean.getPackageName();
		this.md5=dbean.getMd5();
		dbean.get
	}*/





	public static final Parcelable.Creator<ApkBean> CREATOR = new Creator<ApkBean>() {

		@Override
		public ApkBean[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ApkBean[size];
		}

		@Override
		public ApkBean createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ApkBean(source);
		}
	};

	
	
	public ApkBean(Parcel source) {
		url=source.readString();
		savePath=source.readString();
		status=source.readInt();
		name=source.readString();
		packageName=source.readString();
		progress=source.readLong();
		md5=source.readString();
		size=source.readLong();
		hdIcon=source.readString();
		category=source.readString();
		current=source.readLong();
		realSize=source.readLong();
		
		
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(url);
		dest.writeString(savePath);
		dest.writeString(packageName);
		dest.writeString(name);
		dest.writeInt(status);
		dest.writeLong(progress);
		dest.writeString(md5);
		dest.writeLong(size);
		dest.writeString(hdIcon);
		dest.writeString(category);
		dest.writeLong(current);
		dest.writeLong(realSize);
		
	}
	public void readFromParcel(Parcel _reply) {
		// TODO Auto-generated method stub
		name=_reply.readString();
		savePath=_reply.readString();
		url=_reply.readString();
		packageName=_reply.readString();
		status=_reply.readInt();
		progress=_reply.readLong();
		md5=_reply.readString();
		size=_reply.readLong();
		hdIcon=_reply.readString();
		category=_reply.readString();
	}
	
	public AppInfo buidAppInfo(){
		AppInfo info=new AppInfo();
		info.setHDIcon(this.hdIcon);
		info.setPackage_name(this.packageName);
		info.setName(this.name);
		info.setMd5(this.md5);
		info.setCategory(this.category);
		return info;
	}

	
	
	
	
	@Override
	public String toString() {
		return "ApkBean [url=" + url + ", savePath=" + savePath + ", status="
				+ status + ", name=" + name + ", packageName=" + packageName
				+ ", progress=" + progress + ", md5=" + md5 + ", size=" + size
				+ ", hdIcon=" + hdIcon + ", category=" + category +",current="+current+",realSize="+realSize+ "]";
	}
	
	
	

}
