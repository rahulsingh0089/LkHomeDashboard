package lenkeng.com.welcome.upload;

import java.io.Serializable;
import java.util.Date;

import android.hardware.LkecDevice;

import lenkeng.com.welcome.util.Constant;

public class UploadBean implements Serializable{

	private String packageName;
	private int state;
	private String devId;
	private long lastUpdate;
    private String appName;
	private String failReason;
    
    
    public UploadBean() {
    	
    }
	public UploadBean(String pkg, String name) {
		this();
		this.packageName=pkg;
		this.appName=name;
		this.state=Constant.UPLOAD_STATE_CANUPLOAD;
		this.devId=LkecDevice.getDeviceId();
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public long getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	
	
	
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadBean other = (UploadBean) obj;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UploadBean [packageName=" + packageName + ", state=" + state
				+ ", devId=" + devId + ", lastUpdate=" + lastUpdate
				+ ", appName=" + appName + ", failReason=" + failReason + "]";
	}
	
	
	
	
	
}
