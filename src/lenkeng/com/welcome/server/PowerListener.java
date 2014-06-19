package lenkeng.com.welcome.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.jivesoftware.smack.XMPPConnection;

import lenkeng.com.welcome.MainHomeActivity;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;


public class PowerListener extends BroadcastReceiver {
	private static final String SYS_FILE = "/sys/bus/platform/drivers/usb20_otg/force_usb_mode";
	private File file=new File(SYS_FILE);
	@SuppressLint("NewApi")
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
				Write2File(file, "2");
			}
			//Settings.Global.putInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0);
			
		}else{
			Write2File(file, "1");
		}
		/*if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
			
		}else if(Intent.ACTION_SHUTDOWN.equals(action)){
			
		}else if(Intent.ACTION_POWER_DISCONNECTED.equals(action)){
		}*/

	}
	public void Write2File(File file,String mode) {
        if((file == null) || (!file.exists()) || (mode == null)) return ;

        try {
            FileOutputStream fout = new FileOutputStream(file);
            PrintWriter pWriter = new PrintWriter(fout);
            pWriter.println(mode);
            pWriter.flush();
            pWriter.close();
            fout.close();
        } catch(Exception re) {

        }
    }
	
}
