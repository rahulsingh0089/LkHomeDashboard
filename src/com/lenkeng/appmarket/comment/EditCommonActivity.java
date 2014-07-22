package com.lenkeng.appmarket.comment;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import com.lenkeng.logic.Logic;

import android.app.Activity;
import android.content.Context;
import android.hardware.LkecDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class EditCommonActivity extends Activity implements OnClickListener {
    private static final String TAG = "EditCommonActivity";
	private Context mContext;
	private Logic mLogic;
	private TextView tv_devId;
	private AppInfo mApp;
	private Button btn_commen_submit;
	private Button btn_commen_cancle;
	private RatingBar rbar_level;
	private EditText et_content;
	private ProgressBar pb_comit_common;
	
	
	public static final int RESULT_COMIT_COMMEN=1000;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Logic.RESULT_SUBMIT_COMMENT://如果提交成功,弹出toast提示,关闭界面
				
				
				String result=(String) msg.obj;
				Logger.e(TAG, "~~~~~~~~~~handler中收到result="+result);
				
				if("-1".equals(result)){ //访问服务器异常
					LKHomeUtil.showToast(mContext, R.string.server_error);
					
				}else if("1".equals(result)){ //提交成功,加载新的评论数据,自动刷新评论列表界面,关闭当前界面
					LKHomeUtil.showToast(mContext, R.string.toast_submit_commen_success);
					mLogic.loadApkComment(mApp.getPackage_name(), 0, 20);
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							showProgress(false);
							finish();
							
						}
					}, 1000);
					
				}else if("0".equals(result)){ //提交失败
					LKHomeUtil.showToast(mContext, R.string.toast_submit_commen_fail);
				
			}

				
				break;

			default:
				break;
			}
			
			
		};
		
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		  //getWindow().setDimAmount(0.8f);
		
        setContentView(R.layout.edit_commen);
		
		
        mApp=(AppInfo) getIntent().getSerializableExtra("mApp");
		
		mContext=this;
		mLogic = Logic.getInstance(getApplicationContext());
		initWidget();
		
		
	}


	private void initWidget() {
		tv_devId=(TextView) findViewById(R.id.tv_devId);
		tv_devId.setText(LkecDevice.getDeviceId());
		
		
		rbar_level=(RatingBar) findViewById(R.id.rbar_common_level);
		rbar_level.setFocusableInTouchMode(true);
		rbar_level.requestFocus();
		rbar_level.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar rbar, float rating, boolean flag) {
				//Logger.e(TAG, "~~~~~~~~~~~ratingbar值改变, ratint="+rating+",flag="+flag);
				if(rating==0){
					rbar.setRating(1);
				}
				int temp=(int) rbar.getRating();
				switch (temp) {
				case 1:
					 et_content.setHint(R.string.common_hint_level_1);
					break;
				case 2:
					et_content.setHint(R.string.common_hint_level_2);
					break;
				case 3:
					et_content.setHint(R.string.common_hint_level_3);
					break;
				case 4:
					et_content.setHint(R.string.common_hint_level_4);
					break;
				case 5:
					et_content.setHint(R.string.common_hint_level_5);
					break;

				default:
					break;
				}
				
			}
		});
		
		
		et_content=(EditText) findViewById(R.id.et_commen_content);
		
		btn_commen_submit=(Button) findViewById(R.id.btn_commen_submit);
		btn_commen_submit.setOnClickListener(this);
		
		btn_commen_cancle=(Button) findViewById(R.id.btn_commen_cancle);
		btn_commen_cancle.setOnClickListener(this);
		
		pb_comit_common=(ProgressBar) findViewById(R.id.pb_submit_common);
	}


	private void initData() {
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commen_submit:
			
			String text=et_content.getText().toString();
			Logger.e(TAG, "点击了提交评论按钮....");
			ApkCommentParam common=new ApkCommentParam();
			common.setPkg(mApp.getPackage_name());
			
			if(text.trim().length()>0){//如果用户没有手动输入文字,用提示语
				
				common.setContent(et_content.getText().toString());
			}else{
				common.setContent(et_content.getHint().toString());
		      }
			common.setLevel((int) rbar_level.getRating());
			common.setDevId(tv_devId.getText().toString());
			
			submitApkCommon(common);
			
			break;
		case R.id.btn_commen_cancle:
			finish();
			break;

		default:
			break;
		}
		
	}


	private void submitApkCommon(ApkCommentParam param) {
	
		showProgress(true);
		
		//异步提交评论,提交结果会通知Handler
		mLogic.submitApkComment(param,mHandler);
		
		
	}
	
	
	private void showProgress(boolean needShow){
		if(needShow){
			pb_comit_common.setVisibility(View.VISIBLE);
			btn_commen_submit.setEnabled(false);
		}else{
			pb_comit_common.setVisibility(View.GONE);
			btn_commen_submit.setEnabled(true);
			
		}
		
	}
}
