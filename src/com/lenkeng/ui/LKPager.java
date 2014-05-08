package com.lenkeng.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/*
 * $Id: LKPager.java 4 2013-12-12 04:19:52Z kf $
 */
public class LKPager extends ViewPager {

	public LKPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean arrowScroll(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected boolean canScroll(View arg0, boolean arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		return false;
	}


	public LKPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}	
	

}
