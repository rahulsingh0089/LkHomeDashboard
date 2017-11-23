package lenkeng.com.welcome.bean;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.graphics.drawable.Drawable;
/*
 * $Id: TaskInfo.java 4 2013-12-12 04:19:52Z kf $
 */
public class TaskInfo extends RunningAppProcessInfo {
	private String taskName;
	private Drawable taskIcon;
	private int pid; // process id 进程的id
	private int memorysize;
	private boolean ischecked;
	private String packname;
	
	private boolean systemTask;
	
	
	
	public boolean isSystemTask() {
		return systemTask;
	}
	public void setSystemTask(boolean systemTask) {
		this.systemTask = systemTask;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public String getTaskname() {
		return taskName;
	}
	public void setTaskname(String taskname) {
		this.taskName = taskname;
	}
	public Drawable getTaskicon() {
		return taskIcon;
	}
	public void setTaskicon(Drawable appicon) {
		this.taskIcon = appicon;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getMemorysize() {
		return memorysize;
	}
	public void setMemorysize(int memorysize) {
		this.memorysize = memorysize;
	}
	public boolean isIschecked() {
		return ischecked;
	}
	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}
}
