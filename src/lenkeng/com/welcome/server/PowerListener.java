package lenkeng.com.welcome.server;

import org.jivesoftware.smack.XMPPConnection;

import lenkeng.com.welcome.MainHomeActivity;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/*
 * $Id: PowerListener.java 4 2013-12-12 04:19:52Z kf $
 */
public class PowerListener extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
		
		if(Intent.ACTION_MEDIA_MOUNTED.equals(action)){
			if(LKHomeUtil.isFactoryMode()){
				
				if(LKHomeUtil.getConnection()!=null){
					LKHomeUtil.getConnection().disconnect();
					Logger.e("awk", "-----  conn.isconnect  "+LKHomeUtil.getConnection().isConnected());
					Logger.e("awk", "-----  conn.isAuthenticated  "+LKHomeUtil.getConnection().isAuthenticated());
					
					
				}
			}
			
		}
		/*if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
			
		}else if(Intent.ACTION_SHUTDOWN.equals(action)){
			
		}else if(Intent.ACTION_POWER_DISCONNECTED.equals(action)){
		}*/

	}
	
}
