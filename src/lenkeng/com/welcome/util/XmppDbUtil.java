package lenkeng.com.welcome.util;

import java.util.ArrayList;
import java.util.List;

import lenkeng.com.welcome.bean.XmppMessage;
import lenkeng.com.welcome.db.XmppMsgObserver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;





//消息推送工具类
public class XmppDbUtil {
	private static ContentResolver resolver;
	private static Uri uri;
	private Context context;
	private Handler handler;
	public XmppDbUtil(){}
	public XmppDbUtil(Context context ,Handler handler){
		this.context=context;
		this.handler=handler;
		uri = Uri.parse(Constant.XMPP_URI);
		resolver=context.getContentResolver();
		resolver.registerContentObserver(uri, true, new XmppMsgObserver(handler,context));
	}
	
	//向数据库添加一条推送消息
	public void addNewMsg(XmppMessage xmsg) {
		/*if(getAllMsgList().size()>30){
			return;
		}*/
		ContentValues values = new ContentValues();
		values.put("style", xmsg.getStyle());
		values.put("content", xmsg.getContent());
		values.put("msgtime", xmsg.getMsgTime());
		values.put("isread", xmsg.isRead());
		//values.put("variable", xmsg.getVariable());
		resolver.insert(uri, values);
	}

	//获取所有的推送消息
	public static List<XmppMessage> getAllMsgList() {
		List<XmppMessage> xmppMsgs = new ArrayList<XmppMessage>();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			XmppMessage xmsg = new XmppMessage();
			xmsg.setMsgId(cursor.getInt(0));
			xmsg.setStyle(cursor.getString(1));
			xmsg.setContent(cursor.getString(2));
			xmsg.setMsgTime(cursor.getString(3));
			xmsg.setRead(cursor.getInt(4));
			xmppMsgs.add(xmsg);
		}
		cursor.close();
		return xmppMsgs;
	}
	//删除一条推送消息
	public static int delete(XmppMessage xmsg) {
	    String[] strs=new String[]{xmsg.getStyle(),xmsg.getMsgTime()};
		return resolver.delete(uri, null, strs);
	}
	
	//获取未读取的推送消息
	public static List<XmppMessage> getNewMsgList(){
		List<XmppMessage> xmppMsgs = new ArrayList<XmppMessage>();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			int isRead=cursor.getInt(4);
			if(isRead ==-1){
				XmppMessage xmsg = new XmppMessage();
				xmsg.setMsgId(cursor.getInt(0));
				xmsg.setStyle(cursor.getString(1));
				xmsg.setContent(cursor.getString(2));
				xmsg.setMsgTime(cursor.getString(3));
				xmsg.setRead(-1);
				xmppMsgs.add(xmsg);
			}
		}
		cursor.close();
		return xmppMsgs;
	}
	
	//更新推送消息状态
	public static void updateReadStatus(XmppMessage xmsg){
		if(xmsg.isRead()==-1){
			ContentValues values=new ContentValues();
			values.put("isread", 0);
			resolver.update(uri, null, xmsg.getMsgId()+"",null);
		}
	}
}
