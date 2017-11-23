package com.lenkeng.adapter;

import java.util.List;


import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
/*
 * $Id: MarketPageAdapter.java 62 2014-01-21 12:29:04Z xgh $
 */
public class MarketPageAdapter extends PagerAdapter {
	private static final String TAG = "MarketPageAdapter";
	List<View> mViewList;

	
	public MarketPageAdapter(List<View> list){
	this.mViewList=list;	
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mViewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view=mViewList.get(position);
		
		container.addView(view);
		return mViewList.get(position);   //���ص�ǰҪ��ʾ��view
	}
	
	public void destroyItem(ViewGroup container, int position, Object object) {
		View view=mViewList.get(position);
		
		container.removeView(view);
	}
}
