package com.lenkeng.tools;

import java.io.File;

public class Constants {
	
	public static final String  APP_NAME			= ".AppMarket";
	public static final boolean DEBUG 				= true;
	
	//public static  		String		EXTERNAL_DIR 		= Util.getExternalStoragePath()+File.separator+APP_NAME;
	public static  		String		EXTERNAL_DIR 		= "/mnt/sdcard"+File.separator+APP_NAME;
	public static  		String		APK_DIR 			= EXTERNAL_DIR+File.separator+"apk";
	public static  		String		IMG_DIR 			= EXTERNAL_DIR+File.separator+"imgs";
	
	public static boolean isDuandianDownlaod=false;
	
	
}