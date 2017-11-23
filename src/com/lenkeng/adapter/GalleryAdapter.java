package com.lenkeng.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.util.Logger;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lenkeng.bean.Screen;
import com.lenkeng.logic.Logic;
import com.lenkeng.tools.Constants;



@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class GalleryAdapter extends BaseAdapter {
	private Logic mLogic;
	private Activity mCon;
	private List<Screen> mScreen;
	private int mSelected;
	private ArrayList<View> views = new ArrayList<View>();
	private HorizontalScrollView horizontalScrollView;
	private LinearLayout layout;
	private boolean isFirstRun=true;
	public GalleryAdapter(Activity context) {
		this.mCon = context;
	}

	public GalleryAdapter(Activity context, List<Screen> screens) {
		this.mCon = context;
		this.mScreen = screens;
		mLogic = Logic.getInstance(mCon);
		if(isFirstRun){
			initScroll();
			isFirstRun=false;
		}
		
	}

	private void initScroll() {
		horizontalScrollView = (HorizontalScrollView) mCon.findViewById(R.id.scroll);
		layout = (LinearLayout) mCon.findViewById(R.id.scoller);
		for (int i = 0; i < mScreen.size(); i++) {
			// View
			// convertView=LayoutInflater.from(mCon).inflate(lenkeng.com.welcome.R.layout.gallery_item,
			// null);
			// ImageView iv=(ImageView)
			// convertView.findViewById(lenkeng.com.welcome.R.id.icon);
			// mLogic.asViewCompress(mScreen.get(i).getUrl(), iv, new
			// Handler());

			if (mScreen.get(i).getUrl() != null
					&& !"".equals(mScreen.get(i).getUrl())) {
				String tUrl = mScreen
						.get(i)
						.getUrl()
						.substring(
								mScreen.get(i).getUrl().lastIndexOf("/") + 1,
								mScreen.get(i).getUrl().length());
				File localFile = new File(Constants.IMG_DIR + File.separator
						+ tUrl);
				layout.addView(getImageView(localFile.getAbsolutePath(),i));
				// views.add(convertView);
				Logger.d("tag", "-----views.size--" + mScreen.get(i).getUrl());
			}
		}
		// horizontalScrollView.addChildrenForAccessibility(views);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (this.mScreen != null) {
			return this.mScreen.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public void setSelect(int sel) {
		if (this.mSelected != sel) {
			mSelected = sel;
			notifyDataSetChanged();
		}
	}

	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {

		ViewHolder vh;
		Logger.i("tag", "----gallery convertView---" + convertView);
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(mCon).inflate(
					lenkeng.com.welcome.R.layout.gallery_item, null);
			vh.iv = (ImageView) convertView
					.findViewById(lenkeng.com.welcome.R.id.icon);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		// mLogic.asView(mScreen.get(arg0).getUrl(), iv, new Handler());
		// vh.iv.setScaleType(ImageView.ScaleType.FIT_XY);
		//if (isDefault(vh.iv)) {
			mLogic.asViewCompress(mScreen.get(arg0).getUrl(), vh.iv,
					new Handler());
		//}
		/*
		 * if(arg0==mSelected){ vh.iv.setLayoutParams(new
		 * Gallery.LayoutParams(450,256)); }else{ vh.iv.setLayoutParams(new
		 * Gallery.LayoutParams(300, 170)); }
		 */
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
	}

	public boolean isDefault(ImageView iv) {
		Drawable drawable = mCon.getResources()
				.getDrawable(R.drawable.adefault);
		if (iv.getBackground() == drawable || iv.getBackground() == null) {
			return true;
		} else {
			return false;
		}
	}

	private View getImageView(String absolutePath,int index) {

		Bitmap bitmap = decodeBitmapFromFile(absolutePath, 420, 270);
		LinearLayout layout = new LinearLayout(mCon);
		layout.setLayoutParams(new LayoutParams(420, 270));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(mCon);
		imageView.setLayoutParams(new LayoutParams(400, 250));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bitmap);
		layout.addView(imageView);
		mLogic.asViewCompress(mScreen.get(index).getUrl(), imageView,
				new Handler());
		return layout;
	}

	private Bitmap decodeBitmapFromFile(String absolutePath, int reqWidth,
			int reqHeight) {
		Bitmap bm = null;

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(absolutePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(absolutePath, options);

		return bm;
	}

	private int calculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}
}
