package com.lenkeng.appmarket.comment;

import java.util.ArrayList;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.view.LKDialog;

import com.lenkeng.logic.Logic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.LkecDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ApkCommonActivity extends Activity implements OnClickListener {
    protected static final String TAG = "ApkCommonActivity";
	private Context mContext;
	private Logic mLogic;
	private TextView tv_devId;
	private AppInfo mApp;
	private BroadcastReceiver mLoadApkCommentReceiver;
    private ArrayList<ApkCommentParam> commentList=new ArrayList<ApkCommentParam>();
    private ApkCommentDetailAdapter mCommentDetailAdapter;
	private ListView lv_detailCommen;
    
	private Button btn_edit_commen;
	private ProgressBar pb_load_commen;
	private TextView tv_empty_commen;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		   //getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,WindowManager.LayoutParams.FLAG_DIM_BEHIND);
           //getWindow().setDimAmount(0.8f);
		
        setContentView(R.layout.apk_commen);
		mApp=(AppInfo) getIntent().getSerializableExtra("mApp");
		
		mContext=this;
		mLogic = Logic.getInstance(getApplicationContext());
		initWidget();
	
		initLoadApkCommentReceiver();
		mLogic.loadApkComment(mApp.getPackage_name(), 0, 20);
		
		pb_load_commen.setVisibility(View.VISIBLE);
	}


	private void initWidget() {
		
		btn_edit_commen=(Button) findViewById(R.id.btn_edit_apkcommen);
		btn_edit_commen.setOnClickListener(this);
		
		tv_devId=(TextView) findViewById(R.id.tv_devId);
		tv_devId.setText(LkecDevice.getDeviceId());
		
		
		mCommentDetailAdapter=new ApkCommentDetailAdapter(mContext, commentList);
		lv_detailCommen=(ListView) findViewById(R.id.lv_detailCommen);
		lv_detailCommen.setAdapter(mCommentDetailAdapter);
		lv_detailCommen.setSelector(R.drawable.select_bg_common_detail);
		
		pb_load_commen=(ProgressBar) findViewById(R.id.pb_load_commen);
		
		tv_empty_commen=(TextView) findViewById(R.id.tv_empty_commen);
	}


	private void initData() {
		
	
		
	}

	private void initLoadApkCommentReceiver() {
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(Logic.ACTION_NEED_REFRESH_COMMENT);
		mLoadApkCommentReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				pb_load_commen.setVisibility(View.GONE);
				
			ArrayList<ApkCommentParam> comments=	intent.getParcelableArrayListExtra("comments");
			
			     commentList=comments;
			     if(commentList==null || commentList.size()==0){
			    	 tv_empty_commen.setVisibility(View.VISIBLE);
			     }else{
			    	 
			    	 tv_empty_commen.setVisibility(View.GONE);
			    	 mCommentDetailAdapter.updateData(commentList);
			     }
				 Logger.e(TAG, "-----收到加载完评论的广播...评论="+comments);
			}
		};
		this.registerReceiver(mLoadApkCommentReceiver, tFilter);
		
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_edit_apkcommen:
			Intent it=new Intent(mContext,EditCommonActivity.class);
			it.putExtra("mApp", mApp);
			mContext.startActivity(it);
			
			break;

		default:
			break;
		}
		
	}
	
	
	@Override
	protected void onDestroy() {
		mContext.unregisterReceiver(mLoadApkCommentReceiver);
		super.onDestroy();
	}
}
