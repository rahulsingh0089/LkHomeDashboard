package lenkeng.com.welcome.server;

import java.util.List;

import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.bean.ScanInfo;
/*
 * $Id: IService.java 4 2013-12-12 04:19:52Z kf $
 */
public interface IService {
	public List<AppInfo> getPackageName(String str);
	//public XMPPConnection getConnection();
	public List<AppInfo> getAppInfos();
	public List<ScanInfo> getScanInfos();
	public List<AppInfo> getRecAppInfos9();
	public void clearInfos();
} 
