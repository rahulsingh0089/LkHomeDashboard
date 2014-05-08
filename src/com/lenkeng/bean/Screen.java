package com.lenkeng.bean;

import java.io.Serializable;
/*
 * $Id: Screen.java 4 2013-12-12 04:19:52Z kf $
 */
public class Screen implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Screen [url=" + url + "]";
	}

}
