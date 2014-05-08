package com.lenkeng.bean;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;
/*
 * $Id: ParamRunnable.java 4 2013-12-12 04:19:52Z kf $
 */
public class ParamRunnable implements Runnable {

	private String mStr;
	private Handler mHandler;
	private Bitmap mBitMap;
	private ImageView mImageView;
	private String mUrl;
	private int mRes;
	public ApkBean mApkBean;
	public ImplInter mListener;
	public Map<String,String> mParam;
	public int progress;
	public long total;
	
	
	public ParamRunnable(String url,ImageView imageView){
		this.mUrl=url;
		this.mImageView=imageView;
	}
	
	public ParamRunnable(String url){
		this.mUrl=url;
	}
	
	
	public ParamRunnable(ApkBean bean,ImplInter face){
		this.mApkBean=bean;
		this.mListener=face;
	}
	
	public ParamRunnable(String str,Handler hand){
		this.mStr=str;
		this.mHandler=hand;
	}
	public ParamRunnable(String str,Handler hand,HashMap<String,String> param){
		this.mStr=str;
		this.mHandler=hand;
		this.mParam=param;
	}
	
	public ParamRunnable(String url,HashMap<String,String> param){
		this.mUrl=url;
		this.mParam=param;
	}
	
	public ParamRunnable(int  resouse,ImageView imageView){
		this.mRes=resouse;
		this.mImageView=imageView;
	}
	
	public ParamRunnable(Bitmap bm,ImageView imageView){
		this.mBitMap=bm;
		this.mImageView=imageView;
		
	}
	
	public ParamRunnable(int load,long total) {
		this.progress=load;
		this.total=total;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	
	public int getRes(){
		return this.mRes;
	}
	public ImageView getImageView(){
		return this.mImageView;
	}
	public Bitmap getBitMap(){
		return this.mBitMap;
	}
	public String getStr(){
		return this.mStr;
	}
	public Handler getHandler(){
		return this.mHandler;
	}
	public String getUrl(){
		return this.mUrl;
	}

}
