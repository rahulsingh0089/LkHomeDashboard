package com.lenkeng.bean;
import com.lenkeng.bean.ApkBean;
interface ImplInter
{
    void downloadStatus(out ApkBean bean);
    void setProgress(in ApkBean bean);
}