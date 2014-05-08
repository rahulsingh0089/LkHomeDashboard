package com.lenkeng.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Util {
	public static File getExternalStorageDir() {
		return Environment.getExternalStorageDirectory();
	}
	public static String getExternalStoragePath() {
		return getExternalStorageDir().getAbsolutePath();
	}
	public static String getExternalStorageState() {
		return Environment.getExternalStorageState();
	}
	public static boolean isExternalStorageEnable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;

	}
	
	public static void writeBitmap2File(Bitmap bitmap,File file)
	{
		if(bitmap==null || file ==null) return;
		FileOutputStream fos =null;
		try {
			File parent = file.getParentFile();
			if(parent!=null && !parent.exists())
			{
				Util.initExternalDir();
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if(fos!=null)
			{
				try {
					fos.close();
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	public static void initExternalDir()
	{
		if(Util.isExternalStorageEnable())
		{
			File external = new File(Constants.EXTERNAL_DIR);
			if(!external.exists())
			{
				external.mkdirs();
			}
			//check the cache whether exist
			File apk = new File(Constants.APK_DIR);
			if(!apk.exists())
			{
				apk.mkdirs();
			}
			//check the log dir
			File imgs = new File(Constants.IMG_DIR);
			if(!imgs.exists())
			{
				imgs.mkdirs();
			}
		}
	}
	
	public static boolean isEmpty(final List<? extends Object> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(final Set<? extends Object> sets) {
		if (sets == null || sets.isEmpty()) {
			return true;
		}
		return false;
	}
	public static boolean isEmpty(final Map<? extends Object,? extends Object> map) {
		if (map == null || map.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(final String text) {
		return TextUtils.isEmpty(text);
	}

	public static boolean isNumeric(final String str) {
		if (isEmpty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static int getSceenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getSceenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static float getSceenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static void hideSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
		}
	}

	public static void showSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}
	
	
/*	public static Toast mToast;
	public static void showToast(Context context, String msg){
			if(mToast==null){
				mToast=Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			}else{
				 mToast.setText(msg);
			}
	        mToast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 60);
			mToast.show();
		}

	public static void showToast(Context context,int resId){
		String msg=context.getString(resId);
		showToast(context, msg);
	}*/
	
	
}
