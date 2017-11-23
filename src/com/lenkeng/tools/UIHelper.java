package com.lenkeng.tools;


import lenkeng.com.welcome.util.LKHomeUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lenkeng.appmarket.MainActivity;

public class UIHelper {

	public static void showHome(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	public static void ToastMessage(Context cont, String msg) {
		LKHomeUtil.showToast(cont, msg);
		//Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		LKHomeUtil.showToast(cont, msg);
		//Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		LKHomeUtil.showToast(cont, msg);
		//Toast.makeText(cont, msg, time).show();
	}

}
