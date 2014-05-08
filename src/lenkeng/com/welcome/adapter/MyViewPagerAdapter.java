package lenkeng.com.welcome.adapter;

import java.util.List;

import lenkeng.com.welcome.R;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
/*
 * $Id: MyViewPagerAdapter.java 4 2013-12-12 04:19:52Z kf $
 */
public class MyViewPagerAdapter extends PagerAdapter {
	private List<View> views;
	private LinearLayout mCurrentViewPager;
	private GridView gv;
	public MyViewPagerAdapter(){
		
	}
	public void setViews(List<View> views){
		this.views=views;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		// view = null;
		container.removeView(views.get(position));
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(views.get(position));
		return views.get(position);
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.setPrimaryItem(container, position, object);
		mCurrentViewPager=(LinearLayout)object;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	
	/*@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}*/
	
	
	public GridView getPrimaryItem(){
		if(mCurrentViewPager !=null){
			gv=(GridView) mCurrentViewPager.findViewById(R.id.home_app_list);
		}
		return gv;
	}
}
