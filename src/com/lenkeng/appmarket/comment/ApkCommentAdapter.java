package com.lenkeng.appmarket.comment;

import java.util.ArrayList;

import lenkeng.com.welcome.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class ApkCommentAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<ApkCommentParam> mData;
	
	public ApkCommentAdapter(Context context, ArrayList<ApkCommentParam> data){
		mContext=context;
		mData=data;
		
	}
	
	public void updateData(ArrayList<ApkCommentParam> data){
		mData=data;
		notifyDataSetChanged();
	}
	
	
	//无限循环,返回最大值
	public int getCount() {
		if(mData.size()<2){
			return mData.size();
		}
		return Integer.MAX_VALUE;
	}
	
	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0%mData.size());
	}
	
	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	@Override
	public View getView(int postition, View convertView, ViewGroup parent) {
		ViewHoler viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHoler();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_apk_commen, parent,false);
			viewHolder.tv_devId = (TextView) convertView.findViewById(R.id.tv_devId);
			viewHolder.tv_content = (TextView) convertView.findViewById(R.id.adapter_list_layout_tv);
			viewHolder.rbar_level = (RatingBar) convertView.findViewById(R.id.rbar_level);
			
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHoler) convertView.getTag();
		}
		
		viewHolder.tv_devId.setText(mData.get(postition % mData.size()).getDevId());
		viewHolder.tv_content.setText(mData.get(postition % mData.size()).getContent());
		viewHolder.rbar_level.setRating(mData.get(postition % mData.size()).getLevel());
		return convertView;
	}
	
	static class ViewHoler{
		TextView tv_devId;
		TextView tv_content;
		RatingBar rbar_level;
	}
	

}
