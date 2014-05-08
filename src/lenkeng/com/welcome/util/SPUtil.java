package lenkeng.com.welcome.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
	public static final String FILE_INSTALL="file_installing";
	public static final String INSTALLING_KEY="install_key";
	
	private static SharedPreferences sp_file_install;
	
	
	public static void putInstallRecord(Context context,String pakcageName){
		if(sp_file_install==null){
			sp_file_install=context.getSharedPreferences(FILE_INSTALL, Context.MODE_PRIVATE);
		}
		
		sp_file_install.edit().putString(INSTALLING_KEY, pakcageName).commit();
	}
	
	public static String getInstallRecord(Context context){
		if(sp_file_install==null){
			sp_file_install=context.getSharedPreferences(FILE_INSTALL, Context.MODE_PRIVATE);
		}
		
		return sp_file_install.getString(INSTALLING_KEY, "");
		
	}
	
	
	
}
