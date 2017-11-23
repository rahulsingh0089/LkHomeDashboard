package com.lenkeng.bean;
import com.lenkeng.bean.ImplInter;
import com.lenkeng.bean.ApkBean;
interface InterfaceStub
{
	void downLoad(inout ApkBean apkBean);
    void setListener(ImplInter listener);
}