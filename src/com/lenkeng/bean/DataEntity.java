package com.lenkeng.bean;

import java.io.Serializable;
import java.util.List;

import lenkeng.com.welcome.bean.AppInfo;
/*
 * $Id: DataEntity.java 4 2013-12-12 04:19:52Z kf $
 */
public class DataEntity implements Serializable {
	private int pageAmount;
	private int currentPage;
	private List<AppInfo> data;
	public int getPageAmount() {
		return pageAmount;
	}
	public void setPageAmount(int pageAmount) {
		this.pageAmount = pageAmount;
	}
	@Override
	public String toString() {
		return "DateEntity [pageAmount=" + pageAmount + ", currentPage="
				+ currentPage + ", data=" + data + "]";
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public List<AppInfo> getData() {
		return data;
	}
	public void setData(List<AppInfo> data) {
		this.data = data;
	}

}
