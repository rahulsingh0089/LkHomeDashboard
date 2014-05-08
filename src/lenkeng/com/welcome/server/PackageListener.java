
package lenkeng.com.welcome.server;

import java.io.File;

import com.lenkeng.logic.Logic;
import com.lenkeng.tools.ThreadPoolUtil;

import lenkeng.com.welcome.LKHomeApp;
import lenkeng.com.welcome.db.AppDataDao;
import lenkeng.com.welcome.db.AppStoreDao;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

public class PackageListener extends BroadcastReceiver {
	//private SharedPreferences sp = LKHomeApp.getAppContext().getSharedPreferences("config", Context.MODE_PRIVATE);
    private AppDataDao appDao =  AppDataDao.getInstance(LKHomeApp.getAppContext());;
  //  private String packageName;
 //   private String appname;
 //   private int version;
//    private String filename;
//    private String flag;
    private AppStoreDao appStoreDao=AppStoreDao.getInstance(LKHomeApp.getAppContext());;
  //  private String HDIcon;
    private boolean isUninstall=false;
    @Override
    public void onReceive(final Context context,final Intent intent) {
        // TODO Auto-generated method stub
        // String flag=sp.getString("flag", "");
        String action = intent.getAction();
        
        // String filename=LKService.FtoPMap.get(packageName);
        
        // 当有新APP被安装时，将其包名及类型添加到数据库
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
        	long bef=System.currentTimeMillis();
           ThreadPoolUtil.execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String packageName = intent.getDataString().substring(8);
		        String appname = LKHomeUtil.getLabel(packageName);
		        //version = lkUtil.getVersion(packageName);
		       int  version = LKHomeUtil.getVersion(packageName);
		       String flag="";
		       String HDIcon="";
		       if(LKService.DOWNLOAD_APPS.containsKey(packageName)){
		    	   flag=LKService.DOWNLOAD_APPS.get(packageName).getCategory();
		    	   HDIcon=LKService.DOWNLOAD_APPS.get(packageName).getHDIcon();
		    	   if(HDIcon !=null){
		    		   HDIcon=HDIcon.substring(HDIcon.lastIndexOf("/") + 1);
		    	   }
		       }else{
		    	   flag=appStoreDao.getStyle(packageName);
		    	   HDIcon=appStoreDao.getIconName(packageName);
		    	   if(HDIcon !=null){
		    		   HDIcon=HDIcon.substring(HDIcon.lastIndexOf("/") + 1);
		    	   }
		       }
				
				if ("".equals(flag) || null == flag) {
		               /* Intent intent_choose =new Intent(context,ClassifyAcitivity.class);
		                intent_choose.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		                intent_choose.putExtra("packageName", packageName);
		                intent_choose.putExtra("appname", appname);
		                intent_choose.putExtra("version", version);
		                intent_choose.putExtra("filename", filename);
		                intent_choose.putExtra("flag", flag);
		                context.startActivity(intent_choose);*/
		            	if(!"".equals(LKHomeUtil.getPreApkIcon(packageName))){
		            		flag=LKHomeUtil.appStyles.get(packageName);
		            	}else{
		            		flag=Constant.CLASSIFY_USER;
		            	}
		            }
		            if(!"com.lkhome.lkhomeupgrade".equals(packageName) || !"com.lenkeng.wifiman".equals(packageName)){
		            	 appDao.addApp(packageName, flag, appname,HDIcon, version);
		            }
		            if ("lenkeng.com.welcome".equals(packageName)){
		            	File f=new File(Environment.getExternalStorageDirectory()+"/appinfo/LKHome.apk");
		            	if(f.exists()){
		            		f.delete();
		            	}
		            }
		            Intent flushHome = new Intent();
		            flushHome.putExtra("installFlag", "install");
		           // flushHome.setAction(Constant.ACTION_FLUSH_HOME);
		            flushHome.setAction(Constant.ACTION_INSTALED);
		            flushHome.putExtra("packageName", packageName);
		            flushHome.putExtra("apkStyle", flag);
		            context.sendBroadcast(flushHome);
		            
					//===add by xgh		
		           Logic.getInstance(context).deleteDownloadBean(packageName); //删除下载记录和文件
		           //===end
			}
		});
            long aft=System.currentTimeMillis();
            Logger.d("ww", "$$$------ACTION_PACKAGE_ADDED----"+(aft-bef));
        }else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
        	long bef=System.currentTimeMillis();
        	
            ThreadPoolUtil.execute(new Runnable() {
				
				@Override
				public void run() {
					String packageName = intent.getDataString().substring(8);
			        String appname = LKHomeUtil.getLabel(packageName);
			        //version = lkUtil.getVersion(packageName);
			        int version = LKHomeUtil.getVersion(packageName);
			        String flag="";
			        if(LKService.DOWNLOAD_APPS.containsKey(packageName)){
			        	flag=LKService.DOWNLOAD_APPS.get(packageName).getCategory();
			        }else{
			        	flag =appDao.getStyle(packageName);
			        }
					//String flag =appDao.getStyle(packageName);
			        //String flag=LKHomeUtil.DOWNLOAD_APPS.get(packageName).getCategory();
			        //String HDIcon=appStoreDao.getIconName(packageName);
					// TODO Auto-generated method stub
					 appDao.removeApp(packageName);
			            if ("lenkeng.com.welcome".equals(packageName)) {
			                /*
			                 * android.content.SharedPreferences.Editor editor =sp.edit();
			                 * editor.putInt("lenkeng.com.welcome", 3); editor.commit();
			                 */
			               appDao.addApp(packageName, "", appname, "",version);
			            }
			            if("".equals(flag) || null == flag){
			            	if(!"".equals(LKHomeUtil.getPreApkIcon(packageName))){
			            		flag=LKHomeUtil.appStyles.get(packageName);
			            	}else{
			            		flag=Constant.CLASSIFY_USER;
			            	}
			            }
			            Intent flushHome = new Intent();
			            flushHome.putExtra("installFlag", "uninstall");
			            //flushHome.setAction(Constant.ACTION_FLUSH_HOME);
			            flushHome.setAction(Constant.ACTION_INSTALED);
			            flushHome.putExtra("packageName", packageName);
			            flushHome.putExtra("apkStyle", flag);
			            context.sendBroadcast(flushHome);
				}
			});
            
            long aft=System.currentTimeMillis();
            Logger.d("ww", "$$$------ACTION_PACKAGE_REMOVED---"+(aft-bef));
        }else if("unistall_demo".equals(action)){
        	isUninstall=true;
        }
    }

}
