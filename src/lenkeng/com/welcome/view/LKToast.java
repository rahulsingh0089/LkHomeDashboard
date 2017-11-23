package lenkeng.com.welcome.view;

import lenkeng.com.welcome.R;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class LKToast {
	private static Toast close;
	private static Toast open;

	public static void show(Context context, String msg, int show) {

	}

	public static void camerClose(Context context) {
		LinearLayout layout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400,
				50);
		params.gravity = Gravity.CENTER;
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.setGravity(Gravity.CENTER);
		TextView tv = new TextView(context);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(15);
		tv.setText(R.string.camera_cover_off);
		tv.setLayoutParams(params2);
		layout.setLayoutParams(params);
		layout.setBackgroundResource(R.drawable.close);
		layout.addView(tv);
		// Toast toast=new Toast(context);
		// if(close == null){
		close = new Toast(context);
		// }

		close.setDuration(0);
		close.setMargin(0, 0.1f);
		close.setView(layout);
		if (open != null) {
			open.cancel();
		}
		close.show();
	}

	public static void camerOpen(Context context) {

		LinearLayout layout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400,
				50);
		params.gravity = Gravity.CENTER;
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.setGravity(Gravity.CENTER);
		TextView tv = new TextView(context);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(15);
		tv.setText(R.string.camera_cover_on);
		tv.setLayoutParams(params2);
		layout.setLayoutParams(params);
		layout.setBackgroundResource(R.drawable.open);
		layout.addView(tv);
		// Toast toast=new Toast(context);
		open = new Toast(context);

		open.setDuration(0);
		open.setMargin(0, 0.1f);
		open.setView(layout);
		if (close != null) {
			close.cancel();
		}
		open.show();

	}

}
