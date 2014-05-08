package com.lenkeng.service;



import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.lenkeng.bean.ApkBean;
import com.lenkeng.bean.ImplInter;
import com.lenkeng.bean.InterfaceStub;
import com.lenkeng.logic.Logic;

public class MarketService extends Service {
	
	private static final String TAG = "MarketService";
	
	private Logic mLogic;
	
	ImplInter  implInterface;
	private ApkBean mProgressBean;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new ImplStub();
	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
	    
		super.onCreate();
		mLogic=Logic.getInstance(getApplicationContext());
	}
	

	public class ImplStub extends InterfaceStub.Stub{

		


		@Override
		public void downLoad(ApkBean apkBean) throws RemoteException {
			System.out.println("321 md5="+apkBean.getMd5()+",下载bean="+apkBean);
			mLogic.downLoadApk(apkBean,implInterface);
			
		}

		@Override
		public void setListener(ImplInter listener) throws RemoteException {
			implInterface=listener;
			
		}
		
		
		public ApkBean getProgressBean(){
			return mProgressBean;
		}
		
	}
	
	
}
