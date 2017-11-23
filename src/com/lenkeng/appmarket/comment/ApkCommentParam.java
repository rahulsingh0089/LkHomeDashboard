package com.lenkeng.appmarket.comment;

import java.io.Serializable;

import com.lenkeng.bean.ApkBean;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ApkCommentParam implements Parcelable{

	private int id;
	/**
	 * 评论用户id(设备id)
	 */
	private String devId;
	/**
	 * 打分等级
	 */
	private int level;
	/**
	 * 评论内容
	 */
	private String content;
	/**
	 * 评论创建时间(使用long传输)
	 */
	private long createTime;
	/**
	 * 关联的apk
	 */
	private int apkId;
	
	/**
	 * 评论关联的包名
	 */
	private String pkg;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getApkId() {
		return apkId;
	}
	public void setApkId(int apkId) {
		this.apkId = apkId;
	}
	
	
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	@Override
	public String toString() {
		return "CommentParam [id=" + id + ", devId=" + devId + ", level="
				+ level + ", content=" + content + ", createTime=" + createTime
				+ ", apkId=" + apkId + "]";
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(devId);
		dest.writeInt(level);
		dest.writeString(content);
		dest.writeLong(createTime);
		dest.writeInt(apkId);
		dest.writeString(pkg);
	}
	
	
	
	public static final Parcelable.Creator<ApkCommentParam> CREATOR = new Creator<ApkCommentParam>() {

		@Override
		public ApkCommentParam[] newArray(int size) {
			return new ApkCommentParam[size];
		}

		@Override
		public ApkCommentParam createFromParcel(Parcel source) {
			return new ApkCommentParam(source);
		}
	};
	
	public ApkCommentParam(){
		
	}
	
	
	public ApkCommentParam(Parcel source){
		devId=source.readString();
		level=source.readInt();
		content=source.readString();
		createTime=source.readLong();
		apkId=source.readInt();
		pkg=source.readString();
		
	}
	
	
}
