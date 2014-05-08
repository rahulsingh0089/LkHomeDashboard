package lenkeng.com.welcome;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lenkeng.com.welcome.bean.WeatherInfo;
import lenkeng.com.welcome.db.WeatherDataDao;
import lenkeng.com.welcome.server.LKService;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.util.MyDownloadThreadManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherSettingActivity extends Activity {
	private Spinner sp_province;
	private Spinner sp_town;
	private Spinner sp_city;
	private Button weather_confirm;
	public TextView weather_city;
	public TextView tv_weatherdata;
	public ImageView iv_wifiFlag;
	private TextView tv_city;
	private TextView tv_prompt;
	private GridView gv_weather;
	private LinearLayout ll_weatherSetting;
	private ArrayAdapter<String> adapter_province;
	private ArrayAdapter<String> adapter_town;
	private ArrayAdapter<String> adapter_city;
	private WeatherDataDao weatherDao;
	private SharedPreferences sp;
	private String cityName;
	private WeatherInfo weatherInfo;
	private WeatherAdapter adapter;
	private Typeface tf;
	private int index;
	
	private TextView tv_today_temp;
	private TextView tv_today_date;
	private TextView tv_today_weather;
	private TextView tv_today_wind;
	
	public  int[] DRABLES_BIGS = new int[] { R.drawable.a_0_big,
		R.drawable.a_1_big, R.drawable.a_2_big, R.drawable.a_3_big,
		R.drawable.a_4_big, R.drawable.a_5_big, R.drawable.a_6_big,
		R.drawable.a_7_big, R.drawable.a_8_big, R.drawable.a_9_big,
		R.drawable.a_10_big, R.drawable.a_11_big, R.drawable.a_12_big,
		R.drawable.a_13_big, R.drawable.a_14_big, R.drawable.a_15_big,
		R.drawable.a_16_big, R.drawable.a_17_big, R.drawable.a_18_big,
		R.drawable.a_19_big, R.drawable.a_20_big, R.drawable.a_21_big,
		R.drawable.a_22_big, R.drawable.a_23_big, R.drawable.a_24_big,
		R.drawable.a_25_big, R.drawable.a_26_big, R.drawable.a_27_big,
		R.drawable.a_28_big, R.drawable.a_29_big, R.drawable.a_30_big,
		R.drawable.a_31_big };
	public  int[] WEEKDAYS = new int[] { R.string.Sunday,
		R.string.Monday, R.string.Tuesday, R.string.Wednesday,
		R.string.Thursday, R.string.Friday, R.string.Satuday };
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.HANDLER_DOWNLOAD_WEATHER:
				// setWeatherInfo(false);
				showWeatherData();
				break;

			default:
				break;
			}
		}

	};

	private void showWeatherData() {
		cityName = sp.getString("city", Constant.DEFAULT_CITY);
		File f = new File("/sdcard/weather/" + cityName);
		try {
			weatherInfo = LKHomeUtil.jsonWeatherFile(f);
			gv_weather.setAdapter(adapter);
			adapter.setAdapterData(weatherInfo);
			adapter.notifyDataSetChanged();
			index = (int) LKHomeUtil.dayIndex(weatherInfo.getDate_y());
			Logger.e("tag", "$$$---showWeatherData---index---"+index);
			if (index >= 0 && index <= 5) {
				int img = weatherInfo.getImage()[index];
				if (img == 99) {
					img = 9;
				}
				//weather_city.setText(weatherInfo.getCity());
				//tv_city.setText(weatherInfo.getCity());
				String args[] = weatherInfo.getTemp()[index].split("~");
				String small = args[0].substring(0, args[0].lastIndexOf("℃"));
				String big = args[1].substring(0, args[1].lastIndexOf("℃"));
				int i_small = Integer.parseInt(small);
				int i_big = Integer.parseInt(big);
				if (i_big > i_small) {
					//tv_weatherdata.setText(args[0] + "~" + args[1]);
					tv_today_temp.setText(args[0] + "~" + args[1]);
				} else {
					tv_today_temp.setText(args[1] + "~" + args[0]);
				}
				tv_today_weather.setText(weatherInfo.getWeather()[index]);
				tv_today_wind.setText(weatherInfo.getWind()[index]);
				tv_today_date.setText(weatherInfo.getDates()[index] +"  "+getWeak(weatherInfo.getDates()[index]));
				// fb.display(iv_weather,
				// IMG_URL+weatherInfo.getImage()[index]+".gif");
				//iv_weather.setBackgroundDrawable(getResources().getDrawable(
				//		Constant.DRABLES[img]));
			} else {
				//f.delete();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_setting);
		weatherDao = new WeatherDataDao(this);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		String s = Environment.getExternalStorageState();
		if (s.equals(Environment.MEDIA_MOUNTED) && s != null) {
			initSpinner();
			handler.sendEmptyMessage(Constant.HANDLER_DOWNLOAD_WEATHER);
		}
	}

	// the pop is used to setting weather
	public void init() {
		//initTitle();
		tv_today_date =(TextView) this.findViewById(R.id.today_date);
		tv_today_temp=(TextView) this.findViewById(R.id.today_temp);	
		tv_today_weather=(TextView) this.findViewById(R.id.today_weather);
		tv_today_wind=(TextView) this.findViewById(R.id.today_wind);
		
		tf=Typeface.createFromAsset(getAssets(), "pingguolihei.ttf");
		ll_weatherSetting=(LinearLayout) this.findViewById(R.id.ll_weathersetting);
		tv_prompt=(TextView) this.findViewById(R.id.tv_prompt);
		tv_city = (TextView) this.findViewById(R.id.city);
		tv_city.setText(sp.getString("city", "北京"));
		tv_city.setTypeface(tf);
		tv_city.requestFocus();
		tv_city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ll_weatherSetting.setVisibility(View.VISIBLE);
				tv_prompt.setVisibility(View.GONE);
				sp_province.requestFocus();
			}
		});
		gv_weather=(GridView) this.findViewById(R.id.gv_weather);
		gv_weather.setFocusable(false);
		adapter=new WeatherAdapter();
		//tv_weatherInfo = (TextView) this.findViewById(R.id.weather_info);
		//tv_tempratrue = (TextView) this.findViewById(R.id.temperature_info);
		handler.sendEmptyMessage(Constant.HANDLER_DOWNLOAD_WEATHER);
		
	}

	private void initCurrentCity() {
		
	}

	private void initTitle() {
		weather_city = (TextView) this.findViewById(R.id.WeatherCity);
		weather_city.setFocusable(false);
		//weather_city.setText(MainHomeActivity.instance.weather_city.getText());
		//iv_weather = (ImageView) this.findViewById(R.id.WeatherFlag);
	//	iv_weather.setBackgroundDrawable(MainHomeActivity.instance.iv_weather
		//		.getBackground());
		tv_weatherdata = (TextView) this.findViewById(R.id.WeatherData);
		//tv_weatherdata.setText(MainHomeActivity.instance.tv_weatherdata
		//		.getText());
		iv_wifiFlag = (ImageView) this.findViewById(R.id.WifiFlag);
		iv_wifiFlag.setBackgroundDrawable(MainHomeActivity.instance.iv_wifiFlag
				.getBackground());
	}

	private void initSpinner() {
		
		weather_confirm = (Button) this.findViewById(R.id.weather_bt_confirm);
		weather_confirm.setOnClickListener(getcityListener);
		sp_province = (Spinner) this.findViewById(R.id.Province);
		sp_province.setOnItemSelectedListener(province_listener);
		sp_province.requestFocus();
		sp_town = (Spinner) this.findViewById(R.id.Town);
		sp_town.setOnItemSelectedListener(town_listener);
		sp_city = (Spinner) this.findViewById(R.id.City);
		sp_city.setOnItemSelectedListener(city_listener);
		String[] strs = weatherDao.getProvinceArray("CN");
		
		if (null != strs) {
			adapter_province = new ArrayAdapter<String>(this,
					R.layout.spinner_text, strs);
			adapter_province
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_province.setAdapter(adapter_province);
			int defPosition=adapter_province.getPosition(sp.getString("province", Constant.DEFAULT_CITY));
			sp_province.setSelection(defPosition);
		}

	}

	public void province(View v) {
		sp_province.performClick();
	}

	public void town(View v) {
		sp_town.performClick();
	}

	public void city(View v) {
		sp_city.performClick();
	}

	private OnClickListener getcityListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if (!LKHomeUtil.isNetConnected()) {
				LKHomeUtil.showToast(getApplicationContext(), R.string.net_worn_weather);
				return;
			}
			setWeatherInfo(true);
			tv_city.setText(sp_city.getSelectedItem() + "");
			ll_weatherSetting.setVisibility(View.INVISIBLE);
			tv_prompt.setVisibility(View.VISIBLE);
		}
	};

	// 设置天气预报
	private void setWeatherInfo(boolean isNeedSave) {

		if (isNeedSave) {
			String province = sp_province.getSelectedItem() + "";
			String town = sp_town.getSelectedItem() + "";
			String city = sp_city.getSelectedItem() + "";
			String weather_id = weatherDao.getWeatherID(province, town, city);
			String setUrl = "http://m.weather.com.cn/data/" + weather_id
					+ ".html";
			Editor edit = sp.edit();
			edit.putString("province", province);
			edit.putString("town", town);
			edit.putString("city", city);
			edit.putString("weatherUrl", setUrl);
			edit.commit();
			Logger.e("tag", "$$$---setUrl---"+setUrl);
		}
		
		final File f = new File("/sdcard/weather/" + sp.getString("city", Constant.DEFAULT_CITY));
		if (f.exists() && f.length() !=0) {
			try {
				// weatherInfo = LKHomeUtil.jsonNativeFile(f);
				handler.sendEmptyMessage(Constant.HANDLER_DOWNLOAD_WEATHER);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			new Thread( new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//File f = new File("/sdcard/weather/" + sp.getString("city", Constant.DEFAULT_CITY));
					MyDownloadThreadManager mdtm = new MyDownloadThreadManager(
							WeatherSettingActivity.this);
					LKService.dtmList.add(mdtm);
					mdtm.setFile(f);
					mdtm.setUrl(sp
							.getString("weatherUrl", Constant.DEFAULT_WEATHER_URL));
					mdtm.setAction(Constant.ACTION_WEATHERDATA_DOWNLOAD_COMPLETE);
					mdtm.setHandlerWhat(Constant.HANDLER_DOWNLOAD_WEATHER);
					mdtm.setHandler(handler);
					mdtm.startDownload();
				}
			}).start();
		}
	}

	
	private OnItemSelectedListener province_listener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			// TODO Auto-generated method stub
			String[] strs = weatherDao.getTownArray(
					adapter_province.getItem(position), "CN");
			
			if (null != strs) {
				adapter_town = new ArrayAdapter<String>(
						WeatherSettingActivity.this, R.layout.spinner_text,
						strs);
				adapter_town
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_town.setAdapter(adapter_town);
				int defPosition=adapter_town.getPosition(sp.getString("town", Constant.DEFAULT_CITY));
				sp_town.setSelection(defPosition);
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};
	private OnItemSelectedListener town_listener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			// TODO Auto-generated method stub
			List<String> strs = weatherDao.getCityArray(
					adapter_province.getItem(position),
					adapter_town.getItem(position), "CN");
			
			if (null != strs) {
				adapter_city = new ArrayAdapter<String>(
						WeatherSettingActivity.this, R.layout.spinner_text,
						strs);
				adapter_city
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_city.setAdapter(adapter_city);
				int defPosition=adapter_city.getPosition(sp.getString("city",Constant.DEFAULT_CITY));
				sp_city.setSelection(defPosition);
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};
	private OnItemSelectedListener city_listener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			// weather_data.setText(adapter_city.getItem(arg2));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};
	
	
	private class WeatherAdapter extends BaseAdapter{
		
		WeatherInfo info=new WeatherInfo();
		public WeatherAdapter(){
		}
		public void setAdapterData(WeatherInfo info){
			this.info=info;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(info.getWeather() !=null){
				return info.getWeather().length;
			}else{
				return 0;
			}
			
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return info;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh;
			if(convertView == null){
				vh=new ViewHolder();
				convertView =WeatherSettingActivity.this.getLayoutInflater().inflate(R.layout.weather_item, null);
				vh.tv_date=(TextView) convertView.findViewById(R.id.item_date);
				vh.iv_weather_cion=(ImageView) convertView.findViewById(R.id.item_weather_icon);
				vh.tv_temp=(TextView) convertView.findViewById(R.id.item_temp);
				TextPaint tp=vh.tv_temp.getPaint();
				tp.setFakeBoldText(true);
				vh.tv_weather=(TextView) convertView.findViewById(R.id.item_weather);
				TextPaint tp2=vh.tv_weather.getPaint();
				tp2.setFakeBoldText(true);
				vh.tv_wind=(TextView) convertView.findViewById(R.id.item_wind);
				vh.iv_wichDay=(TextView) convertView.findViewById(R.id.wichDay);
				vh.weather_item=(LinearLayout) convertView.findViewById(R.id.weather_item);
				convertView.setTag(vh);
			}else{
				vh=(ViewHolder) convertView.getTag();
			}
			
			try {
				
				int temp=(int) LKHomeUtil.dayIndex(info.getDates()[position]);
			    if(temp == -1){
					vh.iv_wichDay.setText(getString(R.string.tomorrow));
				}else if(temp == -2){
					vh.iv_wichDay.setText(getString(R.string.AfterTomorrow));
				}else{
					vh.iv_wichDay.setVisibility(View.INVISIBLE);
				}
				
				Drawable drawable=getResources().getDrawable(DRABLES_BIGS[info.getImage()[position]]);
				vh.iv_weather_cion.setBackground(drawable);
				vh.tv_date.setTypeface(tf);
				vh.tv_date.setText(info.getDates()[position]+" "+getWeak(info.getDates()[position]));
				vh.tv_temp.setTypeface(tf);
				
				String args[] = info.getTemp()[position].split("~");
				String small = args[0].substring(0, args[0].lastIndexOf("℃"));
				String big = args[1].substring(0, args[1].lastIndexOf("℃"));
				int i_small = Integer.parseInt(small);
				int i_big = Integer.parseInt(big);
				if (i_big > i_small) {
					//tv_weatherdata.setText(args[0] + "~" + args[1]);
					vh.tv_temp.setText(args[0] + "~" + args[1]);
				} else {
					//tv_weatherdata.setText(args[1] + "~" + args[0]);
					vh.tv_temp.setText(args[1] + "~" + args[0]);
				}
				vh.tv_weather.setTypeface(tf);
				vh.tv_weather.setText(info.getWeather()[position]);
				vh.tv_wind.setTypeface(tf);
				vh.tv_wind.setText(info.getWind()[position]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
	}
	
	static class ViewHolder{
		TextView tv_date;
		TextView iv_wichDay;
		ImageView iv_weather_cion;
		TextView tv_temp;
		TextView tv_weather;
		TextView tv_wind;
		LinearLayout weather_item;
	}
	private String getWeak(String date){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		int week = 0;
		try {
			d = df.parse(date);
			Calendar c=Calendar.getInstance();
			c.setTime(d);
			week=c.get(Calendar.DAY_OF_WEEK)-1;
			if(week<0){
				week=0;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getString(WEEKDAYS[week]);
	}
}
