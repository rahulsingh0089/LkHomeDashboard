package lenkeng.com.welcome.view;

import android.view.View;
import android.widget.PopupWindow;
/*
 * $Id: MyPopup.java 4 2013-12-12 04:19:52Z kf $
 */
public class MyPopup extends PopupWindow {
	private View view;
	private int position;
	public MyPopup(View view,int position) {
		// TODO Auto-generated constructor stub
		this.view=view;
		this.position=position;
	}
	@Override
	public void setContentView(View contentView) {
		// TODO Auto-generated method stub
		super.setContentView(contentView);
		setContentView(view);
	}
	@Override
	public void setWidth(int width) {
		// TODO Auto-generated method stub
		super.setWidth(width);
		
	}
}
