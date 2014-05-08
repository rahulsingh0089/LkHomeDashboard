package com.lenkeng.adapter;

import com.lenkeng.bean.DataEntity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/*
 * $Id: WrapAdapter.java 4 2013-12-12 04:19:52Z kf $
 */
public  abstract class WrapAdapter extends BaseAdapter {

	public abstract DataEntity getDataEntity();

}
