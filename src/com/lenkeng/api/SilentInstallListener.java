package com.lenkeng.api;
/*
 * $Id: SilentInstallListener.java 129 2014-02-26 07:36:07Z gww $
 */
public interface SilentInstallListener {

	public void onSilentInstallComplete(String packageName,String apkPath);
	
	public void onSlientInstallFail(String packageName,int returnCode);
}
