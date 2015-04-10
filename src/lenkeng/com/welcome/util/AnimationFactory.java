package lenkeng.com.welcome.util;

import lenkeng.com.welcome.MainHomeActivity;
import lenkeng.com.welcome.MyPopupFactory;
import lenkeng.com.welcome.R;
import lenkeng.com.welcome.ViewPagerFactory;
import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;





public class AnimationFactory {
	private static Activity context;
	private static ImageView iv_move_frame;
	private ViewPagerFactory vpf;
	private MyPopupFactory mpf;
	private Button bt_big;
	private Button currentUnderline;
	public static boolean isAppAnimationEnd =true;
	public static boolean isUnderLineEnd=true;
	
	public AnimationFactory(Activity activity,ViewPagerFactory vpf,MyPopupFactory mpf) {
		this.context = activity;
		iv_move_frame = (ImageView) context.findViewById(R.id.move_frame);
		this.vpf=vpf;
		this.mpf=mpf;
	}
	public void startUnderLineAnition(final View v1, final View v2,final View button) {
		
		TranslateAnimation animation = new TranslateAnimation(v1.getLeft()-10, v2.getLeft()-10,
				0, 0);
		animation.setInterpolator(context,
				android.R.anim.accelerate_decelerate_interpolator);
		animation.setDuration(0);
		animation.setFillAfter(true);
		setCurrentUnderLine((Button) v2);
		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
				isUnderLineEnd=true;
				//Logger.e("ez2", "----startUnderLineAnition---start----"+isUnderLineEnd);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				/*vpf.showCurrentPager(((MainHomeActivity)context).getCurrentStyleFlag());
				mpf.clearAnim();
				mpf.disMiss();*/
				isUnderLineEnd=false;
				//Logger.e("ez2", "----startUnderLineAnition---end----"+isUnderLineEnd);
			}
		});
		iv_move_frame.startAnimation(animation);
	}
	
	public void startScaleAnimation(final View big, View small){
		//ScaleAnimation bigScaleAnimation=new ScaleAnimation(1f, 1.12f, 1f,1.12f);
		if(small ==null){
			small =big;
		}
		bt_big=(Button) big;
		ScaleAnimation bigScaleAnimation=new ScaleAnimation(1, 1.4f, 1, 1.4f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 1);
		bigScaleAnimation.setInterpolator(context,android.R.anim.accelerate_decelerate_interpolator);
		bigScaleAnimation.setDuration(200);
		bigScaleAnimation.setFillAfter(true);
		//vpf.showAnimation(big);
		//vpf.hideAnimation(small);
		bigScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				Logger.d("awk", " startScaleAnimation dismiss ");
				mpf.disMiss();
				mpf.clearAnim();
			} 
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				big.requestFocus();
			}
		});
		big.startAnimation(bigScaleAnimation);
		ScaleAnimation smallScaleAnimation=new ScaleAnimation(1.4f, 1, 1.4f,1,ScaleAnimation.RELATIVE_TO_SELF,0.5f,ScaleAnimation.RELATIVE_TO_SELF, 1);
		smallScaleAnimation.setInterpolator(context,android.R.anim.accelerate_decelerate_interpolator);
		smallScaleAnimation.setDuration(300);
		if(small !=big){
			small.startAnimation(smallScaleAnimation);
		}
	}
	public static AlphaAnimation showAppAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		//alphaAnimation.setFillAfter(true);
		alphaAnimation.setInterpolator(context,
				android.R.anim.accelerate_decelerate_interpolator);
		alphaAnimation.setDuration(0);
		alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				isAppAnimationEnd =false;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				isAppAnimationEnd=true;
			}
		});
		return alphaAnimation;
	}

	public static AlphaAnimation hideAppAnimation() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		//alphaAnimation.setFillAfter(true);
		alphaAnimation.setInterpolator(context,
				android.R.anim.accelerate_decelerate_interpolator);
		alphaAnimation.setDuration(0);
		return alphaAnimation;
	}
	public Button getScaleBigButton(){
		return bt_big;
	}
	public static void clearUnderLineAnimation(){
		iv_move_frame.clearAnimation();
	}
	public Button getCurrentUnderline(){
		return currentUnderline;
	}
	public void setCurrentUnderLine(Button bt){
		this.currentUnderline =bt;
	}
}
