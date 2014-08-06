package lenkeng.com.welcome.db;


import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.Logger;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;



//监听推送消息，当有新的推送消息收到时通知消息界面进行更新
public class XmppMsgObserver extends ContentObserver {
	private Handler handler;
	private Context context;
	public XmppMsgObserver(Handler handler,Context context) {
		super(handler);
		// TODO Auto-generated constructor stub
		this.handler=handler;
		this.context=context;
	}
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Logger.i("gww", "a new message was send succefully");
		
		Intent intent=new Intent();
		intent.setAction("com.lenkeng.action.newmsg");
		context.sendBroadcast(intent);
		//notice home activity chanage message icon
		handler.sendEmptyMessage(Constant.HANADLER_NOTICE_MSG);
	}
}
