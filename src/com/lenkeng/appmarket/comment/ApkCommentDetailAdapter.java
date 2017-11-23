package com.lenkeng.appmarket.comment;

import java.sql.Date;
import java.util.ArrayList;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class ApkCommentDetailAdapter extends BaseAdapter {

	private static final String TAG = "ApkCommentDetailAdapter";
	private Context mContext;
	private ArrayList<ApkCommentParam> mData;
	
	public ApkCommentDetailAdapter(Context context, ArrayList<ApkCommentParam> data){
		mContext=context;
		mData=data;
		
	}
	
	public void updateData(ArrayList<ApkCommentParam> data){
		mData=data;
		notifyDataSetChanged();
	}
	
	
	public int getCount() {
		return mData==null ? 0 : mData.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	@Override
	public View getView(int postition, View convertView, ViewGroup parent) {
		
		ViewHoler viewHolder = null;
		ApkCommentParam commen=null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_apk_commen_detail, parent,false);
			viewHolder = new ViewHoler();
			viewHolder.tv_devId = (TextView) convertView.findViewById(R.id.tv_devId);
			viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_commen_content);
			viewHolder.rbar_level = (RatingBar) convertView.findViewById(R.id.rbar_level);
			viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
			
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHoler) convertView.getTag();
		}
		
		commen=mData.get(postition);
		
		if(commen!=null){
			viewHolder.tv_devId.setText(commen.getDevId());
			viewHolder.tv_content.setText(commen.getContent());
			viewHolder.rbar_level.setRating(commen.getLevel());
			viewHolder.tv_date.setText(LKHomeUtil.formatDate(new Date(commen.getCreateTime()),"yyy-MM-dd"));
			
		}
		return convertView;
	}
	
	static class ViewHoler{
		TextView tv_devId;
		TextView tv_content;
		RatingBar rbar_level;
		TextView tv_date;
	}
	

}
