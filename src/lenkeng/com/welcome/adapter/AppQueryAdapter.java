package lenkeng.com.welcome.adapter;

import java.lang.ref.SoftReference;
import java.util.List;

import com.lenkeng.logic.Logic;
import lenkeng.com.welcome.LKHomeApp;
import lenkeng.com.welcome.MyPopupFactory;
import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.util.AnimationFactory;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeCache;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.util.SoundUtil;
import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

// 主界面APP显示的数据适配器
public class AppQueryAdapter extends BaseAdapter {
	private static final String TAG = "AppQueryAdapter";
	private AppInfo info;
	public ViewHolder holder;
	private Activity context;
	private List<AppInfo> mList;
	//private Handler mHandler;
	//private SharedPreferences sp;
	//private AppStoreDao appStoreDao;
	//private int page = 0;
	private int isSelected;
	private String style_flag;
	//private MyPopupFactory mpf;
	//private String Langue;
	private Logic mLogic;
	
	
	public AppQueryAdapter(Activity context) {
		this.context = context;
		//this.mHandler=handler;
		mLogic=Logic.getInstance(context);
		//sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		//appStoreDao =AppStoreDao.getInstance(LKHomeApp.getAppContext());
		//mpf=new MyPopupFactory(context);
		//Langue=context.getString(R.string.langue);
	}

	public void setItemsData(List<AppInfo> items, int page) {
		this.mList = items;
		//this.page = page;
		/*if("EN".equals(Langue) && Constant.CLASSIFY_SETTING.equals(style_flag)){
			mList.remove(8);
		}*/
		
		
	}
	public void setStyleFlag(String style_flag){
		this.style_flag=style_flag;
	}
	@Override
	public int getCount() {
		
		return mList.size();
	}
	public void setSelect(int selected){
		if(isSelected != selected){
		  isSelected=selected;
		  notifyDataSetChanged();
		}
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		info=mList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.home_app_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.home_app_name);
			holder.ico = (ImageButton) convertView
					.findViewById(R.id.home_app_ico);
			holder.btn_upload=(ImageView) convertView.findViewById(R.id.btn_upload);
			holder.btn_upload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AppInfo app=(AppInfo) v.getTag();
					String pkg=app.getPackage_name();
					String appName=app.getName();
					mLogic.uploadApk(pkg,appName);
				}
			});
			// holder.ico.setFocusable(false);
			// holder.title.setFocusable(false);
			//holder.ico.setLayoutParams(new RelativeLayout.LayoutParams(145,140));
			holder.ll_itemBack = (RelativeLayout) convertView.findViewById(R.id.item_back);
			holder.info=info;;
			holder.position=position;
			holder.face=(LinearLayout) convertView.findViewById(R.id.face);
			convertView.setTag(holder);
			// convertView.setOnHoverListener(this);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// ResolveInfo res = resInfo.get(position);
		if(isSelected==position){
			//Animation shakeAnim = AnimationUtils.loadAnimation(
					//context, R.anim.item);
			//holder.ll_itemBack.startAnimation(shakeAnim);
		}else{
			
		}
		clissifyApp(position);
		holder.ico.setFocusable(false);
		holder.ico.setClickable(false);
		holder.btn_upload.setTag(info);
		//convertView.setOnHoverListener(hoverListener);
		
		
		return convertView;
	}

	private OnHoverListener hoverListener= new OnHoverListener() {
		@Override
		public boolean onHover(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			ViewHolder holder=(ViewHolder) v.getTag();
			switch (event.getAction()) {
			case MotionEvent.ACTION_HOVER_ENTER:
				//mpf.disMiss();
				//mpf.showPopupWindow(v, holder.info, style_flag, holder.position);
				if(v.getY()>=0 && v.getY()<=205  && AnimationFactory.isAppAnimationEnd){
					//mpf.showPopupWindow(v, holder.info, style_flag, holder.position);
				}
				//holder.face.setBackgroundResource(R.drawable.app_hovered);
				break;
			case MotionEvent.ACTION_HOVER_EXIT:
				//mpf.disMiss();
			//	holder.face.setBackgroundColor(0x00000000);
				holder.face.setBackgroundResource(0);
				break;
			default:
				break;
			}
			return false;
		}
	};
	
	@SuppressWarnings("unused")
	@SuppressLint("NewApi")
	
	private void clissifyApp(int position) {
		
		if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			holder.ll_itemBack.setBackgroundResource(Integer.parseInt(info.getHDIcon()));
			holder.ico.setBackgroundResource(Integer.parseInt(info.getHDIcon()));
			//holder.title.setVisibility(View.INVISIBLE);
			//holder.ico.setVisibility(View.INVISIBLE);

		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			//holder.title.setVisibility(View.INVISIBLE);
			//holder.title.setText(info.getName());
			
			/*Drawable d=LKHomeUtil.getIcon(info.getPackage_name());
			if(d == null){
				holder.ico.setBackgroundResource(R.drawable.ic_launcher);
			}else{
				holder.ico.setBackground(d);
			}*/
			
			if(LKHomeUtil.mapDrawable.containsKey(info.getPackage_name())){
				holder.ico.setVisibility(View.INVISIBLE);
				holder.ico.setBackgroundResource(LKHomeUtil.mapDrawable.get(info.getPackage_name()));
				holder.ll_itemBack.setBackgroundResource(LKHomeUtil.mapDrawable.get(info.getPackage_name()));
				
				Log.e(TAG, "~~~!!!!!! pkg="+info.getPackage_name()+",resId="+LKHomeUtil.mapDrawable.get(info.getPackage_name()));
			}else{
				
				 holder.ico.setVisibility(View.VISIBLE);
				 Drawable d=LKHomeUtil.getIcon(info.getPackage_name());
					if(d == null){
						holder.ico.setBackgroundResource(R.drawable.ic_launcher);
					}else{
						holder.ico.setBackground(d);
					}
				
				holder.ll_itemBack.setBackgroundResource(Constant.ITEM_BACKS[position% Constant.ITEM_BACKS.length]);
				 
			}
			
			
			
			if(!LKHomeUtil.addAppMap.containsKey( info.getPackage_name())){//用户安装的应用
				//holder.btn_upload.setVisibility(View.VISIBLE);
				
				if(mLogic.getUploadState(info.getPackage_name())==Constant.UPLOAD_STATE_EXITED){ //apk已经存在,不显示图标
					holder.btn_upload.setVisibility(View.GONE);
				}else if(mLogic.getUploadState(info.getPackage_name())==Constant.UPLOAD_STATE_CANUPLOAD){//apk可以上传
					holder.btn_upload.setVisibility(View.VISIBLE);
					holder.btn_upload.setBackgroundResource(R.drawable.select_upload_button);
					
				}else if(mLogic.getUploadState(info.getPackage_name())==Constant.UPLOAD_STATE_WAITVERFY){//apk等待审核
					holder.btn_upload.setVisibility(View.VISIBLE);
					holder.btn_upload.setBackgroundResource(R.drawable.select_upload_waitverify);
					
				}else if(mLogic.getUploadState(info.getPackage_name())==Constant.UPLOAD_STATE_VERFY_PASS){//apk审核通过
					holder.btn_upload.setVisibility(View.VISIBLE);
					holder.btn_upload.setBackgroundResource(R.drawable.select_upload_verify_pass);
					
				}else if(mLogic.getUploadState(info.getPackage_name())==Constant.UPLOAD_STATE_VERFY_FAIL){//apk审核不通过
					holder.btn_upload.setVisibility(View.VISIBLE);
					holder.btn_upload.setBackgroundResource(R.drawable.select_upload_verify_fail);
					
				}
				
				
				
			}else{
				holder.btn_upload.setVisibility(View.GONE);
			}
			
		} else {
			//holder.title.setVisibility(View.INVISIBLE);
			if (info.getPackage_name().equals(Constant.MORE)) { //电子市场
				if(Constant.CLASSIFY_APPLICATION.equals(style_flag)){
					//holder.ico.setVisibility(View.INVISIBLE);
					holder.ico.setBackgroundResource(R.drawable.app_store);
					//holder.title.setText(context.getString(R.string.More));
					holder.ll_itemBack.setBackgroundResource(R.drawable.app_store);
				}else if(Constant.CLASSIFY_GAME.equals(style_flag)){
					//holder.ico.setVisibility(View.INVISIBLE);
					holder.ico.setBackgroundResource(R.drawable.game_store);
					//holder.title.setText(context.getString(R.string.More));
					holder.ll_itemBack.setBackgroundResource(R.drawable.game_store);
				}else if(Constant.CLASSIFY_MOVIE.equals(style_flag)){
					//holder.ico.setVisibility(View.INVISIBLE);
					holder.ico.setBackgroundResource(R.drawable.tv_store);
					//holder.title.setText(context.getString(R.string.More));
					holder.ll_itemBack.setBackgroundResource(R.drawable.tv_store);
				}
			} else {
				//holder.title.setText(info.getName());
				
				
				//=========使用缓存管理图片===========
				Bitmap bit=null;
				/*if(info.getHDIcon()!=null && !"".equals(info.getHDIcon())){
					bit=LKHomeUtil.decodeBitmapFromFile("/sdcard/.AppMarKet/imgs/"+info.getHDIcon(), 210, 200);
				}	
				if(bit==null && !"".equals(LKHomeUtil.getPreApkIcon(info.getPackage_name()))){
					bit=LKHomeUtil.decodeBitmapFromFile(LKHomeUtil.getPreApkIcon(info.getPackage_name()), 210, 200);
				}*/
				
				bit=LKHomeCache.loadLaunchBitmap(info,context);
				
				//====================
				
				//Drawable d = new BitmapDrawable();
				//Drawable d = LKHomeUtil.getLocalDrawable(info.getHDIcon());
				if (bit != null) {
					Drawable d =new BitmapDrawable(bit);
					//holder.ico.setVisibility(View.GONE);
					holder.ico.setBackground(d);
					holder.ll_itemBack.setBackground(d);
				} else {
					//int resId=LKHomeUtil.getPreApkIcon(info.getPackage_name());
					//---------------英文版独有------------------
					/*if("com.lenkeng.video".equals(info.getPackage_name())){
						//holder.ico.setVisibility(View.INVISIBLE);
						holder.ico.setBackgroundResource(R.drawable.video_icon);
						holder.ll_itemBack.setBackgroundResource(R.drawable.video_icon);
					}else if("com.android.email".equals(info.getPackage_name())){
						//holder.ico.setVisibility(View.INVISIBLE);
						holder.ico.setBackgroundResource(R.drawable.email);
						holder.ll_itemBack.setBackgroundResource(R.drawable.email);
					}else if("com.lenkeng.filebrowser".equals(info.getPackage_name())){
						//holder.ico.setVisibility(View.INVISIBLE);
						holder.ico.setBackgroundResource(R.drawable.icon_filebrowser);
						holder.ll_itemBack.setBackgroundResource(R.drawable.icon_filebrowser);
					}else if("com.android.browser".equals(info.getPackage_name())){
						//holder.ico.setVisibility(View.INVISIBLE);
						holder.ico.setBackgroundResource(R.drawable.browser);
						holder.ll_itemBack.setBackgroundResource(R.drawable.browser);
					}*/
					if(LKHomeUtil.mapDrawable.containsKey(info.getPackage_name())){
						holder.ico.setVisibility(View.INVISIBLE);
						holder.ico.setBackgroundResource(LKHomeUtil.mapDrawable.get(info.getPackage_name()));
						holder.ll_itemBack.setBackgroundResource(LKHomeUtil.mapDrawable.get(info.getPackage_name()));
					}
					 else{
						holder.ico.setVisibility(View.VISIBLE);
						holder.ico.setBackground(LKHomeUtil.getIcon(info.getPackage_name()));
						holder.ll_itemBack.setBackgroundResource(Constant.ITEM_BACKS[position
												% Constant.ITEM_BACKS.length]);
					}
					//---------------------end---------------------
				}
			}
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static class ViewHolder {
		TextView title;
		ImageButton ico;
		ImageView icon_big;
		ImageView btn_upload;
		RelativeLayout ll_itemBack;
		LinearLayout face;
		AppInfo info;
		int position;
	}
	
}
