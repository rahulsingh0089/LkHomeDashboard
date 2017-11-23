package com.lenkeng.adapter;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.util.LKHomeUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenkeng.adapter.MovieAdapter.ViewHolder;
import com.lenkeng.appmarket.DetailActivity;
import com.lenkeng.appmarket.MainActivity;
import com.lenkeng.appmarket.MainActivity.OnAppItemClickListener;
import com.lenkeng.bean.DataEntity;
import com.lenkeng.logic.Logic;

public class SearchAdapter extends WrapAdapter {
	private DataEntity mSearchApps;
	private LayoutInflater mInflater;
	private Logic mLogic;
	private Handler mHandler;
	private AppDataDao mDao;
	private GridView gv;
	private ViewHolder vHolder;
	private boolean isNeedChanagedBg = true;
	private Context mContext;
	private OnAppItemClickListener mListener;

	public SearchAdapter(Context con, Logic logic,OnAppItemClickListener listener) {
		mInflater = LayoutInflater.from(con);
		this.mLogic = logic;
		mContext=con;
		mListener=listener;
		this.mHandler = new Handler();
		mDao =  AppDataDao.getInstance(con.getApplicationContext());
	}

	@Override
	public int getCount() {
		if (mSearchApps != null) {
			return mSearchApps.getData().size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View convertView, ViewGroup arg2) {
		// count++

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.grid_item, null);
			holder = new ViewHolder();
			holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.appFlag = (ImageView) convertView.findViewById(R.id.cover);
			holder.appItemBack =(LinearLayout) convertView.findViewById(R.id.market_item_bg);
			holder.appName=(TextView) convertView.findViewById(R.id.app_name);
			
			convertView.setTag(holder);
			//convertView.setOnHoverListener(hoverListener);
			
			convertView.setClickable(true);
			convertView.setBackgroundResource(R.drawable.market_item_hover_selector);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		
		
		final AppInfo app = this.mSearchApps.getData().get(arg0);
		holder.appName.setText(app.getName());
		
		mLogic.asView(app.getIcon(), holder.appIcon, mHandler);
		
		if(LKHomeUtil.isInstalled(mContext,app.getPackage_name())){
			
			holder.appFlag.setVisibility(View.VISIBLE);
		}else{
			
			holder.appFlag.setVisibility(View.GONE);
		}
		
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener!=null){
					mListener.onAppItemClick(arg0,MainActivity.DATA_TYPE_SEARCH);
				}
				
				/*Intent tIni = new Intent(mContext, DetailActivity.class);
				tIni.putExtra("appinfo", app);
				mContext.startActivity(tIni);*/
				
				
			}
		});
		
		
		return convertView;
	}

	public void setDataEntity(DataEntity dataEntity, GridView v) {
		this.mSearchApps = dataEntity;
		this.notifyDataSetChanged();
		this.gv = v;
		//gv.setOnItemSelectedListener(itemSelectedListener);
		//gv.setOnFocusChangeListener(focusChangeListener);
	}

	public DataEntity getDataEntity() {
		return this.mSearchApps;
	}

	class ViewHolder {
		public ImageView appIcon;
		public ImageView appFlag;
		public LinearLayout appItemBack;
		public TextView appName;
		
	}

	/*private OnHoverListener hoverListener = new OnHoverListener() {

		@Override
		public boolean onHover(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (vHolder != null && isNeedChanagedBg) {
				vHolder.appItemBack
						.setBackgroundResource(R.drawable.market_item_def_bg);
				isNeedChanagedBg = false;
			}
			vHolder = (ViewHolder) v.getTag();
			if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
				vHolder.appItemBack.setVisibility(View.VISIBLE);
				vHolder.appItemBack
						.setBackgroundResource(R.drawable.market_appitem_bg);
			} else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
				vHolder.appItemBack
						.setBackgroundResource(R.drawable.market_item_def_bg);
			}
			return false;

		}
	};*/
	/*private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if (vHolder != null) {
				vHolder.appItemBack
						.setBackgroundResource(R.drawable.market_item_def_bg);
			}
			vHolder = (ViewHolder) view.getTag();
			vHolder.appItemBack
					.setBackgroundResource(R.drawable.market_appitem_bg);
			isNeedChanagedBg = true;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};*/
	/*private OnFocusChangeListener focusChangeListener=new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(vHolder !=null){
				if(hasFocus){
					vHolder.appItemBack
					.setBackgroundResource(R.drawable.market_appitem_bg);
				}else{
					vHolder.appItemBack
					.setBackgroundResource(R.drawable.market_item_def_bg);
				}
			}
		}
	};*/
}
