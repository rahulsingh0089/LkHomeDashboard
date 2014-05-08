package lenkeng.com.welcome.server;

import org.jivesoftware.smack.XMPPConnection;

import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/*
 * $Id: PowerListener.java 4 2013-12-12 04:19:52Z kf $
 */
public class PowerListener extends BroadcastReceiver {
	private XMPPConnection conn;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
		if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
			
		}else if(Intent.ACTION_SHUTDOWN.equals(action)){
			conn=LKHomeUtil.getConnection();
			if(conn !=null){
				conn.disconnect();
			}
		}else if(Intent.ACTION_POWER_DISCONNECTED.equals(action)){
		}

	}
	
}
