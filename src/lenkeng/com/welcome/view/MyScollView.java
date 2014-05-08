package lenkeng.com.welcome.view;

import java.util.Map;

import org.jivesoftware.smack.util.PacketParserUtils;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.util.Logger;

import android.annotation.TargetApi;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyScollView extends ScrollView {
	private static final String TAG = "MyScollView";
	private static final int SCROLL_ORIENTATION_UP = 0;
	private static final int SCROLL_ORIENTATION_DOWN = 1;
	private static final int SCROLL_ORIENTATION_NO=2;
	private Context context;
	private LinearLayout scrollChild;
	private LinearLayout.LayoutParams params;
	private LinearLayout.LayoutParams child_params;
	private LinearLayout.LayoutParams first_child_params;
	private int pointNum;
	private int oldCount=0;
	private boolean isCreated;
	public int getPointNum() {
		return pointNum;
	}

	public void setPointNum(int pointNum) {
		this.pointNum = pointNum;
	}

	public MyScollView(Context context) {
		super(context);

		Log.e(TAG, "---MyScollView    1----");
	}

	
	public MyScollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		scrollChild = new LinearLayout(context);
		//scrollChild.setLayoutParams(params);
		scrollChild.setBackgroundResource(R.drawable.scroll_bg);
		scrollChild.setOrientation(LinearLayout.VERTICAL);
		scrollChild.setFocusable(false);
		Log.e(TAG, "---MyScollView    2----");

		child_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		
		child_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		child_params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
		child_params.topMargin = 60;
		child_params.gravity=Gravity.CENTER;
		
		first_child_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		first_child_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		first_child_params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
		first_child_params.topMargin = 1;
		first_child_params.gravity=Gravity.CENTER;
		addView(scrollChild);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private void addNode() {
		int count = scrollChild.getChildCount();
		TextView tv=new TextView(context);
		tv.setTextSize(10);
		tv.setText(count+1 + "");
		tv.setTag(count+1);
		tv.setId(count+1);
		tv.setBackgroundResource(R.drawable.node);
		tv.setLayoutParams(child_params);
		tv.setFocusable(false);
		scrollChild.addView(tv, count);
		oldCount=scrollChild.getChildCount();
	}

	public void createNode(int count) {
		//if(isConuntChanaged(count)){
		if(count>2){
			scrollChild.setVisibility(View.VISIBLE);
		}else{
			scrollChild.setVisibility(View.INVISIBLE);
			return;
		}
		count=count/2;
			scrollChild.removeAllViews();
			Logger.i("kao", "--------createNode---1----"+count);
			for (int i = 1; i < count+1; i++) {
				Logger.i("kao", "--------createNode----2---"+count);
				TextView tv = new TextView(context);
				tv.setTextSize(15);
				tv.setText(i + "");
				tv.setTag(i);
				tv.setId(i);
				
				if(i ==1){
					tv.setLayoutParams(first_child_params);
					tv.setBackgroundResource(R.drawable.node);
				}else{
					tv.setLayoutParams(child_params);
					tv.setBackgroundResource(R.drawable.node_def);
				}
				tv.setFocusable(false);
				tv.setWidth(25);
				tv.setHeight(25);
				tv.setGravity(Gravity.CENTER);
				scrollChild.addView(tv, i - 1);
				// Log.d(TAG, "--layout child---"+layout.get);
			}
		//}
		oldCount=scrollChild.getChildCount();
		isCreated=true;
	}
	public void addNewNode(int count){
		if(isConuntChanaged(count)){
			addNode();
		}
	}
	
	public boolean isConuntChanaged(int newCount){
		if(oldCount != newCount){
			return true;
		}else{
			return false;
		}
	}
	
	public void startScoll(int page, int orientation) {
		if (orientation == SCROLL_ORIENTATION_DOWN) {
			backPage(page);
		} else if (orientation == SCROLL_ORIENTATION_UP) {
			toPage(page);
		}else if(orientation ==SCROLL_ORIENTATION_NO){
			if(isCreated){
				backPage(page);
			}
		}
	}
	public void showNum(int page,int orientation){
		if (orientation == SCROLL_ORIENTATION_DOWN) {
			//backPage(page);
			for(int i=page;i<scrollChild.getChildCount();i++){
				scrollChild.getChildAt(i).setVisibility(View.GONE);
			}
		} else if (orientation == SCROLL_ORIENTATION_UP) {
			//toPage(page);
			for(int i=0;i<page;i++){
				scrollChild.getChildAt(i).setVisibility(View.VISIBLE);
			}
		}
	}
	private void toPage(int page) {
		
		
		TextView from = (TextView) scrollChild.getChildAt(page);
		//TextView to = (TextView) scrollChild.getChildAt(0);
		if (from == null || page==0) {
			return;
		}
		from.setBackgroundResource(R.drawable.node);
		TranslateAnimation animation_bef = new TranslateAnimation(0, 0,
				 0,-60*page);
		animation_bef.setInterpolator(context,
				android.R.anim.accelerate_interpolator);
		animation_bef.setDuration(50);
		animation_bef.setFillAfter(true);
		from.startAnimation(animation_bef);
		//Logger.e("kao", "$$$---"+TAG+"---toPage---"+page);
		
		/*TextView from2 = (TextView) scrollChild.getChildAt(2);
		TextView to2 = (TextView) scrollChild.getChildAt(1);
		TranslateAnimation animation_bef2 = new TranslateAnimation(0, 0,
				 0,-120);
		animation_bef2.setInterpolator(context,
				android.R.anim.accelerate_interpolator);
		animation_bef2.setDuration(0);
		animation_bef2.setFillAfter(true);
		from2.startAnimation(animation_bef2);*/
		
		if(isCreated){
			if((page--) >1){
				toPage(page--);
			}else{
				page=0;
			}
		}
		isCreated=false;
	}
	
	private void backPage(int page) {
		
		TextView from = (TextView) scrollChild.getChildAt(page);
		//TextView to = (TextView) scrollChild.getChildAt(page-1);
		if(from ==null){
			return;
		}
		from.setBackgroundResource(R.drawable.node_def);
		float m=350-25*scrollChild.getChildCount()-60*page;
		
		TranslateAnimation animation_back = new TranslateAnimation(0, 0,
				-60*page, m);
/*		TranslateAnimation animation_back = new TranslateAnimation(0, 0,
				0, 50*(scrollChild.getChildCount()-page));
*/		animation_back.setInterpolator(context,
				android.R.anim.accelerate_interpolator);
		animation_back.setDuration(50);
		animation_back.setFillAfter(true);
		from.startAnimation(animation_back);
		//Logger.e("kao", "$$$---"+TAG+"--backPage----"+(page));
		
		if(isCreated){
			
			if((page++)<scrollChild.getChildCount()){
				backPage(page++);
			}else{
				page=scrollChild.getChildCount()-1;
			}
			isCreated=false;
		}
		
		
	}
}
