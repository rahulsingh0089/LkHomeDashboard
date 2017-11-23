package com.lenkeng.appmarket.comment;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.content.Context;
import android.text.Spanned;
import android.text.InputFilter.LengthFilter;

public class LKLengFilter extends LengthFilter {

	private static final String TAG = "LKLengFilter";

	private Context mContext;
	
	public LKLengFilter(int length,Context context) {
		super(length);
		mMax=length;
		mContext=context;
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		 //int keep = mMax - (dest.length() - (dend - dstart));
		int keep = mMax - (getCharacterNum(dest.toString()) - (dend - dstart));

         if (keep <= 0) {
        	 String temp=mContext.getString(R.string.text_commen_too_length);
         	LKHomeUtil.showToast(mContext,temp );
        	// Logger.e(TAG, "~~~~~~~~~keep 小于0,keep="+keep);
        	 
             return "";
         } else if (keep >= end - start) {
        	// Logger.e(TAG, "~~~~~~~~~keep >= end - start,keep="+keep);
             return null; // keep original
         } else {
             keep += start;
             if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                 --keep;
                 if (keep == start) {
                     return "";
                 }
             }
             return source.subSequence(start, keep);
         }
	}
	
	 private int mMax;
	 
	 
	 public  int getCharacterNum(String content){
	        if(content.equals("")||null == content){
	            return 0;
	        }else {
	            return content.length()+getChineseNum(content);
	        }
	        
	    }
	  
	  
	  public  int getChineseNum(String s){
	        int num = 0;
	        char[] myChar = s.toCharArray();
	        for(int i=0;i<myChar.length;i++){
	            if((char)(byte)myChar[i] != myChar[i]){
	                num++;
	            }
	        }
	        return num;
	    }
	 
	 
}
