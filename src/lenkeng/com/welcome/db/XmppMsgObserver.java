package lenkeng.com.welcome.db;


import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.util.SoundUtil;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

/*
 * $Id: XmppMsgObserver.java 72 2014-02-09 03:28:03Z gww $
 */

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
		
		
		//notice home activity chanage message icon
		handler.sendEmptyMessage(Constant.HANADLER_NOTICE_MSG);
	}
}
