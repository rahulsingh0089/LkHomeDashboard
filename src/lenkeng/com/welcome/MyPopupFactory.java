package lenkeng.com.welcome;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.db.ClientActionDao;
import lenkeng.com.welcome.util.AnimationFactory;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.view.LKDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lenkeng.appmarket.DetailActivity;
import com.lenkeng.appmarket.MainActivity;

@SuppressLint("NewApi")
public class MyPopupFactory implements android.view.View.OnClickListener {
	private String TAG="MyPopupFactory";
	public static int REC_INDEX = -1;
	private static View view;
	private static AppInfo appInfo;
	private static String style_flag;
	private Activity context;
	public static PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	private View convertView;
	private RelativeLayout rl_popu;
	private ImageView popu_icon;
	private RelativeLayout.LayoutParams rl_normal;
	//private ClientActionDao caDao;
	private SharedPreferences sp;
	private View small;
	private ImageView iv_movie;
	//private Animation appAnim;
	//private View tempView;
	//private int position;
	private TranslateAnimation translateAnimation;
	private LKDialog dialog;
	//private LKHomeUtil homeUtil;
	
	public MyPopupFactory(Activity context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		//homeUtil=new LKHomeUtil(context);
		//caDao = new ClientActionDao(context);
		inflater = context.getLayoutInflater();
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		// initPopup();
		iv_movie = (ImageView) context.findViewById(R.id.frame_move);
		//appAnim = AnimationUtils.loadAnimation(context, R.anim.appitem_anim);
	}

	public void initPopup() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
		mPopupWindow = new PopupWindow();
		mPopupWindow.setAnimationStyle(R.style.popu);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		convertView = inflater.inflate(R.layout.popu_item, null);
		rl_popu = (RelativeLayout) convertView.findViewById(R.id.popu_back);
		popu_icon = (ImageView) rl_popu.findViewById(R.id.popu_app_ico);
		mPopupWindow.setContentView(rl_popu);
		popu_icon.setOnClickListener(this);

	}

	
	

	

	public void showPopupWindow(View v, AppInfo info, String style_flag,
			int postion) {
		clearAnim();
		MyPopupFactory.style_flag = style_flag;
		appInfo = info;
		//tempView = v;
		//this.position = postion;
		if (Constant.CLASSIFY_RECOMMEND.equals(style_flag)) {
			createRecPopup(v, info, postion);
		} else {
			createAppPopup(v, info, style_flag, postion);
		}
		
	}

	public void createAppPopup(View v, final AppInfo info, String style_flag,
			int position) {
		initPopup();
		ImageView icon = (ImageView) v.findViewById(R.id.home_app_ico);
		rl_normal = new RelativeLayout.LayoutParams(227, 222);
		// rl_normal.topMargin = 71;
		// rl_normal.leftMargin = 69;
		rl_normal.topMargin = 3;
		rl_normal.leftMargin = 4;
		
		popu_icon.setLayoutParams(rl_normal);
		if (Constant.CLASSIFY_USER.equals(style_flag)) {
			popu_icon.setBackgroundResource(Constant.ITEM_BACKS[position
			                  % Constant.ITEM_BACKS.length]);
			popu_icon.setImageDrawable(LKHomeUtil.zoomBitmap(icon
					.getBackground().mutate(), 100));
		} else if (!Constant.CLASSIFY_RECOMMEND.equals(style_flag)) {

			popu_icon.setBackground(LKHomeUtil.zoomBitmap(icon
					.getBackground().mutate(), 227));
			popu_icon.setImageDrawable(null);
		}
		// if(Constant.CLASSIFY_SETTING.equals(style_flag)){
		// }else{
		// popu_name.setVisibility(View.VISIBLE);
		// }
		rl_popu.setBackgroundResource(R.drawable.temp_frame);
		popu_icon.requestFocus();
		// mPopupWindow.setHeight(365);
		// mPopupWindow.setWidth(365);
		mPopupWindow.setHeight(230);
		mPopupWindow.setWidth(235);
		// mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (int)v.getX()+15,
		// (int)v.getY()+35);
		mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (int) v.getX() + 80,
				(int) v.getY() + 105);
		if(Constant.MORE.equals(info.getPackage_name())){
			v.setVisibility(View.INVISIBLE);
			mPopupWindow.setOnDismissListener(new PopuDissListener(v,info));
		}
	}

	public void createRecPopup(View v, AppInfo info, int index) {

		initPopup();
		popu_icon.setImageDrawable(null);
		switch (index) {
		case 1:
			RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
					470, 441);
			// rl.topMargin = 83;
			// rl.leftMargin = 85;
			rl.topMargin = 2;
			rl.leftMargin = 2;
			popu_icon.setLayoutParams(rl);
			// rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.rec_shadow_1));
			rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.rec_shadow_1_));

			popu_icon.setBackgroundDrawable(LKHomeUtil.zoomBitmap(v
					.getBackground().mutate(), 405));
			mPopupWindow.setWidth(475);
			mPopupWindow.setHeight(445);
			mPopupWindow.update();
			//mPopupWindow.setOnDismissListener(new PopuDissListener(v,info));
			mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, v.getLeft()-15,
					v.getTop() + 100);
			//v.setVisibility(View.INVISIBLE);
			break;
		case 2:
			RelativeLayout.LayoutParams rl_1 = new RelativeLayout.LayoutParams(
					228, 223);
			// rl_1.topMargin = 70;
			// rl_1.leftMargin = 68;
			rl_1.topMargin = 3;
			rl_1.leftMargin = 4;
			popu_icon.setLayoutParams(rl_1);
			popu_icon.setBackgroundDrawable(LKHomeUtil.zoomBitmap(v
					.getBackground().mutate(), 211));
			// rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.rec_shadow_2));
			rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.rec_shadow_2_));
			mPopupWindow.setHeight(230);
			mPopupWindow.setWidth(235);
			//mPopupWindow.setOnDismissListener(new PopuDissListener(v));
			mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
					v.getRight() + 325, v.getTop() + 105);
			break;
		case 3:
			RelativeLayout.LayoutParams rl_5 = new RelativeLayout.LayoutParams(
					437, 212);
			// rl_5.topMargin = 83;
			// rl_5.leftMargin = 82;
			rl_5.topMargin = 2;
			rl_5.leftMargin = 2;
			popu_icon.setLayoutParams(rl_5);
			popu_icon.setBackgroundDrawable(LKHomeUtil.zoomBitmap(v
					.getBackground().mutate(), 205));
			// rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.rec_shadow_3));
			rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.rec_shadow_3_));

			mPopupWindow.setWidth(440);
			mPopupWindow.setHeight(218);
			//mPopupWindow.setOnDismissListener(new PopuDissListener(v));
			mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
					v.getRight() + 115, v.getTop() + 113);
			break;
		case 4:
			RelativeLayout.LayoutParams rl_2 = new RelativeLayout.LayoutParams(
					228, 223);
			// rl_2.topMargin = 70;
			// rl_2.leftMargin = 68;
			rl_2.topMargin = 3;
			rl_2.leftMargin = 4;
			popu_icon.setLayoutParams(rl_2);
			popu_icon.setBackgroundDrawable(LKHomeUtil.zoomBitmap(v
					.getBackground().mutate(), 211));
			// rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.rec_shadow_2));
			rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.rec_shadow_2_));
			mPopupWindow.setHeight(230);
			mPopupWindow.setWidth(235);
			//mPopupWindow.setOnDismissListener(new PopuDissListener(v));
			mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
					v.getRight() + 325, v.getBottom() + 110);
			break;
		case 5:
			RelativeLayout.LayoutParams rl_3 = new RelativeLayout.LayoutParams(
					228, 223);
			// rl_3.topMargin = 70;
			// rl_3.leftMargin = 68;
			rl_3.topMargin = 3;
			rl_3.leftMargin = 4;
			popu_icon.setLayoutParams(rl_3);
			popu_icon.setBackgroundDrawable(LKHomeUtil.zoomBitmap(v
					.getBackground().mutate(), 211));
			// rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.rec_shadow_2));
			rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.rec_shadow_2_));
			mPopupWindow.setHeight(230);
			mPopupWindow.setWidth(235);
			//mPopupWindow.setOnDismissListener(new PopuDissListener(v));
			mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
					v.getRight() + 325, v.getBottom() + 110);
			break;
		case 6:
			RelativeLayout.LayoutParams rl_4 = new RelativeLayout.LayoutParams(
					228, 223);
			rl_4.topMargin = 3;
			rl_4.leftMargin = 4;
			// rl_4.topMargin = 70;
			// rl_4.leftMargin = 68;
			popu_icon.setLayoutParams(rl_4);
			popu_icon.setBackgroundDrawable(LKHomeUtil.zoomBitmap(v
					.getBackground().mutate(), 211));
			// rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
			// R.drawable.rec_shadow_2));
			rl_popu.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.rec_shadow_2_));
			mPopupWindow.setHeight(230);
			mPopupWindow.setWidth(235);
		//	mPopupWindow.setOnDismissListener(new PopuDissListener(v));
			mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
					v.getRight() + 325, v.getBottom() + 110);
			break;
		default:
			break;
		}
	}

	public void disMiss() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}

	public void setAppInfo(AppInfo info) {
		style_flag = Constant.CLASSIFY_RECOMMEND;
		appInfo = info;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(!LKHomeUtil.isNetConnected()&& appInfo==null ){
			if(dialog !=null && dialog.isShowing()){
				return;
			}
			dialog=new LKDialog(context, context.getString(R.string.NetError), clickListener, R.style.MyDialog);
			dialog.show();
			return;
		}else if(LKHomeUtil.isNetConnected() && appInfo==null){
			LKHomeUtil.showToast(context, R.string.text_trying);
		}
		if (null != appInfo) {
			openUserApp(style_flag, appInfo);
		}
	}

	OnClickListener clickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId()==R.id.yes){
				try {
					if(!LKHomeUtil.isNetConnected()){
						goSettingIntent("com.lenkeng.network");
					}
					dialog.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			}else if(v.getId() ==R.id.no){
				dialog.dismiss();
			}
			
		}
	};
	public void openUserApp(String style_flag, AppInfo appInfo) {
		String packageName = appInfo.getPackage_name();
		if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			try {
				goSettingIntent(packageName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (Constant.CLASSIFY_RECOMMEND.equals(style_flag)) {
			//try {
				//REC_INDEX = appInfo.getRecomm_index();
				//if (REC_INDEX == 6) {
					//goSettingIntent(Constant.SETTING_ACTION[1]);
				//} else {
					if (LKHomeUtil.isInstalled(packageName)) {
						Intent intent = context.getPackageManager()
								.getLaunchIntentForPackage(packageName);

						if (intent != null) {
							context.startActivity(intent);
						}
					} else {
						
						try {
							goSettingIntent(appInfo.getPackage_name());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Intent intent = new Intent(context,
									DetailActivity.class);
							Log.e(TAG, "line 336 传递的url="+appInfo.getUrl());
							intent.putExtra("appinfo", appInfo);
							context.startActivity(intent);
						}
					}
				//}
		//	} catch (Exception e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				
		//	}
		} else {
			if (packageName.equals(Constant.MORE)) {
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("flag", style_flag);
				context.startActivity(intent);
			} else {
				if (Constant.CLASSIFY_USER.equals(style_flag)) {
					// obtain the user app data
					int counter = sp.getInt("counter", 0);
					counter++;
					Editor edit = sp.edit();
					edit.putInt("counter", counter);
					edit.commit();
					//caDao.addUserRunData(appInfo.getName(), packageName);
				}
				Intent intent = context.getPackageManager()
						.getLaunchIntentForPackage(packageName);

				if (intent != null) {
					context.startActivity(intent);
				}
			}
		}
	}

	private void goSettingIntent(String action) throws Exception {
	//	try {
			// if(action.equals("com.lenkeng.upgrade")){
			// Intent intent =
			// context.getPackageManager().getLaunchIntentForPackage("com.lkhome.lkhomeupgrade");
			// context.startActivity(intent);
			// }else{
			Intent route_intent = new Intent(action);
			route_intent.addCategory("android.intent.category.DEFAULT");
			route_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(route_intent);
			// }
	//	} catch (Exception e) {
			// TODO: handle exception
		//	e.printStackTrace();
	//	}
	}

	public void setPopupParams(View v, AppInfo info, String style_flag,
			int postion) {
		popuBigAnimation(info, style_flag, postion, v);
	}

	private void popuBigAnimation(final AppInfo info, final String style,
			final int postion, final View big) {

		// ImageView v=new ImageView(context);
		// rl_popu.setBackgroundResource(R.drawable.temp_frame);
		if (small == null) {
			translateAnimation = new TranslateAnimation(big.getX()-5, big.getX()-5,
					big.getY(), big.getY());
		} else {
			translateAnimation = new TranslateAnimation(small.getX()-5,
					big.getX()-5, small.getY(), big.getY());
		}

		translateAnimation.setFillAfter(true);
		translateAnimation.setDuration(300);
		// ScaleAnimation scaleAnimation=new ScaleAnimation(0.9f, 1.15f, 0.9f,
		// 1.15f, ScaleAnimation.RELATIVE_TO_SELF, 0.0f,
		// ScaleAnimation.RELATIVE_TO_SELF, 0.6f);
		// scaleAnimation.setFillAfter(true);
		// set.addAnimation(scaleAnimation);
		// set.addAnimation(translateAnimation);
		// set.setDuration(200);
		// set.setFillAfter(true);
		// set.setInterpolator(context,android.R.anim.accelerate_decelerate_interpolator);
		translateAnimation
				.setAnimationListener(new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						if (rl_popu != null) {
							rl_popu.setBackgroundResource(0);
						}
						disMiss();
						AnimationFactory.clearUnderLineAnimation();
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub

					}
					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
							showPopupWindow(big, info, style, postion);
							rl_popu.setBackgroundResource(R.drawable.temp_frame);
					}
				});
		iv_movie.startAnimation(translateAnimation);
		small = big;
	}

	

	public void clearAnim() {
		small = null;
		iv_movie.clearAnimation();
	}

	public boolean isShowing() {
		if (mPopupWindow != null) {
			return mPopupWindow.isShowing();
		} else {
			return false;
		}
	}
	class PopuDissListener implements PopupWindow.OnDismissListener{
		View v;
		AppInfo info;
		public PopuDissListener(View v,AppInfo info){
			this.v =v;
			this.info=info;
		}
		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			v.setVisibility(View.VISIBLE);
			
			Logger.d("gww", "$$$---TAG------onDismiss---v--"+v);
		}
		
	}
}
