package lenkeng.com.welcome;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lenkeng.com.welcome.bean.XmppMessage;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.util.XmppDbUtil;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MessageActivity extends Activity implements OnClickListener  {
	private String TAG="MessageActivity";
	private TextView bt_msg;
	private Button bt_next;
	private Button bt_up;
	private Button bt_del;
	private Button bt_clear;
	private List<XmppMessage> msg_list;
	private int COUNTER=0;
	private TextView tv_msg_time;
	private TextView tv_msg_status;
	private boolean isImgMsg=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		initViewAndObject();
		IntentFilter filter=new IntentFilter();
		filter.addAction("com.lenkeng.action.newmsg");
		registerReceiver(receiver, filter);
		
	}
	@SuppressLint("NewApi")
	private BroadcastReceiver receiver=new BroadcastReceiver() {
		
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			 String action=intent.getAction();
			 if("com.lenkeng.action.newmsg".equals(action)){
				 try {
					msg_list=XmppDbUtil.getAllMsgList();
					 XmppMessage msg=msg_list.get(0);
					 bt_msg.setText(msg.getContent());
					 bt_msg.setBackground(null);
					 tv_msg_time.setText(formatTime(Long.valueOf(msg.getMsgTime())));
					 XmppDbUtil.updateReadStatus(msg);
				 } catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			 }
		}
	};
	private void initViewAndObject() {
	    bt_msg=(TextView) this.findViewById(R.id.Msg);
	    bt_msg.setText(getString(R.string.click_check));
	    bt_msg.setMovementMethod(ScrollingMovementMethod.getInstance());
	    bt_next=(Button) this.findViewById(R.id.next);
	    bt_up=(Button) this.findViewById(R.id.up);
	    bt_del=(Button) this.findViewById(R.id.msg_del);
	    bt_clear=(Button) this.findViewById(R.id.msg_clear);
	    tv_msg_time=(TextView) this.findViewById(R.id.msg_time);
	    tv_msg_status=(TextView) this.findViewById(R.id.msg_status);
	    bt_next.setOnClickListener(this);
	    bt_up.setOnClickListener(this);
	    bt_del.setOnClickListener(this);
	    bt_clear.setOnClickListener(this);
	    msg_list=XmppDbUtil.getAllMsgList();
	    
	    if(msg_list.size() ==0){
	    	bt_msg.setText(getString(R.string.noMsg));
	    }else{
	    	checkMsg(0);
	    }
	}
	
	@SuppressLint("NewApi")
	private void checkMsg(int index){
		 try {
				//	List<XmppMessage> msg_list=XmppDbUtil.getNewMsgList();
				//	if(msg_list.size()==0){
				//	    bt_msg.setText(getString(R.string.noMsg));
				//	    return;
				//	}
			    	if(msg_list.size()!=0){
			    		XmppMessage xmsg=msg_list.get(index);
						if("msg".equals(xmsg.getStyle())){
						    bt_msg.setBackground(null);
						    bt_msg.setText(xmsg.getContent());
						}else if("img".equals(xmsg.getStyle())){
						    try {
								String url=xmsg.getContent();
								String imgName=url.substring(url.lastIndexOf("/")+1);
								String path="/sdcard/image/"+xmsg.getMsgTime()+imgName;
								bt_msg.setBackground(Drawable.createFromPath(path));
								bt_msg.setText("");
							} catch (Exception e) {
								// TODO Auto-generated catch block
							}
						}
						tv_msg_time.setText(formatTime(Long.valueOf(xmsg.getMsgTime())));
						//tv_msg_status.setText(xmsg.isRead()+"");
						XmppDbUtil.updateReadStatus(xmsg);
			    	}
					
					//bt_msg.setText("共    "+msg_list.size()+"   条  / 当前   "+msg_list.indexOf(xmsg)+" 条");
					//msg_list.remove(0);
					//XmppDbUtil.delete(xmsg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	private  String formatTime(long time) {

		Date date = new Date();
		date.setTime(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sdate = sdf.format(date);
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		String stime = sdf2.format(date);
		String result = sdate + "\n" + stime;
		return result;
	}
	public void next(View v){
	   /* try {
			List<XmppMessage> msg_list=XmppDbUtil.getNewMsgList();
			if(msg_list.size()==0){
			    bt_msg.setText(getString(R.string.noMsg));
			    return;
			}
	    		XmppMessage xmsg=msg_list.get(0);
				if("msg".equals(xmsg.getStyle())){
				    bt_msg.setBackgroundDrawable(null);
				    bt_msg.setText(xmsg.getContent());
				}else if("img".equals(xmsg.getStyle())){
				    try {
						String url=xmsg.getContent();
						String imgName=url.substring(url.lastIndexOf("/"));
						String path="/sdcard/image/"+imgName;
						bt_msg.setBackgroundDrawable(Drawable.createFromPath(path));
						bt_msg.setText("");
	   
						File f=new File(path);
						if(f.exists()){
						    f.delete();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				}
			
			//bt_msg.setText("共    "+msg_list.size()+"   条  / 当前   "+msg_list.indexOf(xmsg)+" 条");
			msg_list.remove(0);
			XmppDbUtil.delete(xmsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
            next(bt_msg);
        }
       return super.onKeyUp(keyCode, event);
    }

    
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next:
			try {
				if(msg_list.size()>0){
					COUNTER++;
					if(COUNTER>msg_list.size()-1){
						COUNTER=msg_list.size()-1;
					}
					checkMsg(COUNTER);
				}
				Logger.i("tag", "$$$---"+TAG+"---COUNTER++   = "+COUNTER+"---MSG_SIZE   "+msg_list.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.up:
			try {
				if(msg_list.size()>0){
					COUNTER--;
					if(COUNTER<0){
						COUNTER=0;
					}
					checkMsg(COUNTER);
				}
				Logger.i("tag", "$$$---"+TAG+"---COUNTER--   = "+COUNTER+"---MSG_SIZE   "+msg_list.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.msg_del:
			delete(false, COUNTER);
			/*try {
				if(msg_list.size()==0){
					return;
				}
				XmppMessage xmsg=msg_list.get(COUNTER);
				if("img".equals(xmsg.getStyle())){
					String url=xmsg.getContent();
					String imgName=url.substring(url.lastIndexOf("/")+1);
					String path="/sdcard/image/"+xmsg.getMsgTime()+imgName;
					bt_msg.setBackground(null);
					File f=new File(path);
					if(f.exists()){
					    f.delete();
					}
				}
				int result=XmppDbUtil.delete(xmsg);
				if(result !=-1){
					msg_list.remove(COUNTER);
					msg_list=XmppDbUtil.getAllMsgList();
					if(msg_list.size()>0){
						COUNTER--;
						if(COUNTER<0){
							COUNTER=0;
						}
						checkMsg(COUNTER);
					}else{
						bt_msg.setText(getString(R.string.noMsg));
						tv_msg_time.setText("");
					}
				}
				Logger.i("tag", "$$$---"+TAG+"---COUNTER DEL   = "+COUNTER+"---MSG_SIZE   "+msg_list.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			break;
		case R.id.msg_clear:
			if(msg_list!=null){
				for(int i=0;i<msg_list.size();i++){
					delete(true, i);
				}
			}
			msg_list.clear();
			bt_msg.setText(getString(R.string.noMsg));
			tv_msg_time.setText("");
			bt_msg.setBackground(null);
			break;
		default:
			break;
		}
	};
	@SuppressLint("NewApi")
	private void delete(boolean delAll,int index){
		try {
			if(msg_list==null || msg_list.size()==0){
				return;
			}
			XmppMessage xmsg=msg_list.get(index);
			if("img".equals(xmsg.getStyle())){
				String url=xmsg.getContent();
				String imgName=url.substring(url.lastIndexOf("/")+1);
				String path="/sdcard/image/"+xmsg.getMsgTime()+imgName;
				bt_msg.setBackground(null);
				File f=new File(path);
				if(f.exists()){
				    f.delete();
				}
			}
			int result=XmppDbUtil.delete(xmsg);
			if(result !=-1){
				if(!delAll){
					msg_list.remove(index);
					msg_list=XmppDbUtil.getAllMsgList();
					if(msg_list.size()>0){
						COUNTER--;
						if(COUNTER<0){
							COUNTER=0;
						}
						checkMsg(COUNTER);
					}else{
						bt_msg.setText(getString(R.string.noMsg));
						tv_msg_time.setText("");
					}
				}else{
					COUNTER=0;
				}
			}
			Logger.i("tag", "$$$---"+TAG+"---COUNTER DEL   = "+COUNTER+"---MSG_SIZE   "+msg_list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
}
