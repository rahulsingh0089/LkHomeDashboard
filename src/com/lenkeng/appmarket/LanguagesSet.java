package com.lenkeng.appmarket;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class LanguagesSet extends Activity implements OnItemClickListener {
	private static final String TAG = "LanguagesSet";
	private ListView language;
	private String[] locales;
	private LocateAdapter adapter;
	private int finalSize = 0;
	private LayoutInflater inflater;
	private boolean DEBUG = true;
	private LocaleInfo[] localeInfos;
	private Locale locale;

	
	private ListView mImListView;
	private InputmethodAdapter mImAdapter;
	private String mCurrentInputmethodId;
	private List<InputMethodInfo> imList;
	private Context mContext;
	private InputMethodManager mImm;
	private PackageManager pm;
	private List<String> enabledImList;
	 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.language_inputmethod);
		
		
		initData();
		initwidget();
		locales = Resources.getSystem().getAssets().getLocales();
		locale = getResources().getConfiguration().locale;

		String[] specialLocaleCodes = getResources().getStringArray(
				com.android.internal.R.array.special_locale_codes);
		String[] specialLocaleNames = getResources().getStringArray(
				com.android.internal.R.array.special_locale_names);
		Arrays.sort(locales);
		final LocaleInfo[] preprocess = new LocaleInfo[locales.length];
//country=US language=en
		//country=CN language=zh


		for (int i = 0; i < locales.length; i++) {
			final String s = locales[i];
			final int len = s.length();
			if (len == 5) {
				String language = s.substring(0, 2);
				String country = s.substring(3, 5);
				if ((country.equals("CN")&&language.equals("zh"))||country.equals("US")&&language.equals("en")) {
				
				final Locale l = new Locale(language, country);
				if (finalSize == 0) {

					preprocess[finalSize++] = new LocaleInfo(
							toTitleCase(l.getDisplayLanguage(l)), l);

				} else {
					if (preprocess[finalSize - 1].locale.getLanguage().equals(
							language)) {

						preprocess[finalSize - 1].label = toTitleCase(getDisplayName(
								preprocess[finalSize - 1].locale,
								specialLocaleCodes, specialLocaleNames));

						preprocess[finalSize++] = new LocaleInfo(
								toTitleCase(getDisplayName(l,
										specialLocaleCodes, specialLocaleNames)),
								l);

					} else {

						String displayName;
						if (s.equals("zz_ZZ")) {
							displayName = "Pseudo...";
						} else {
							displayName = toTitleCase(l.getDisplayLanguage(l));
						}

						preprocess[finalSize++] = new LocaleInfo(displayName, l);
					}
				}
				
				}
			}
		}

		localeInfos = new LocaleInfo[finalSize];
		for (int i = 0; i < finalSize; i++) {
			localeInfos[i] = preprocess[i];
		}
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapter = new LocateAdapter(this,
				R.layout.language_item,
				R.id.language,
				R.id.current, localeInfos, locale);
		language.setAdapter(adapter);
		language.setOnItemClickListener(this);
		
		
		addBugLog("xgh,# 移植语言设置...");
		
	}

	private void initData() {
		  mContext=this;
		  mCurrentInputmethodId  = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
		  pm = getPackageManager();
		  imList=getInputMethodList();
		  
		  String enabled  = Settings.Secure.getString(getContentResolver(),Settings.Secure.ENABLED_INPUT_METHODS);
		  String disabled=Settings.Secure.getString(getContentResolver(),Settings.Secure.DISABLED_SYSTEM_INPUT_METHODS);
		   
		  enabledImList=string2List(enabled);
		  
		  
		  Log.e(TAG, "line 157 enable="+enabled+",disable="+disabled+",current="+mCurrentInputmethodId);
		  
	}

	private List<String> string2List(String enabled) {
		ArrayList<String> temp=new ArrayList<String>();
		if(enabled==null || enabled.equals("")){
			return temp;
		}
		
		String[] array=enabled.split(":");
		for(int i=0;i<array.length;i++){
			temp.add(array[i]);
		}
		
		return temp;
	}

	private void initwidget() {
		//语言
		language = (ListView) findViewById(R.id.languages);
		
		
		//输入法
		mImListView=(ListView) findViewById(R.id.inputmethod);
		mImAdapter=new InputmethodAdapter(mContext, imList);
		mImListView.setAdapter(mImAdapter);
		mImListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position<0 || position>=imList.size()){
					return;
				}
				
				InputMethodInfo imi=imList.get(position);
				if(!isInputmethodEnabled(imi)){
					//TODO..弹出警告框
					
				   enableInputmethod(imi);
				}
				
				updateDefaultInputmethod(imi);
				
				
			}

			

			
		});
		
	}

	/**
	 * 将新的输入法保存到Enabled
	 * @param imi
	 */
	private void enableInputmethod(InputMethodInfo imi) {
		StringBuilder builder=new StringBuilder();
		String enabled  = Settings.Secure.getString(getContentResolver(),Settings.Secure.ENABLED_INPUT_METHODS);
        builder.append(enabled);
        builder.append(":");
        builder.append(imi.getId());
        
        Settings.Secure.putString(getContentResolver(),Settings.Secure.ENABLED_INPUT_METHODS,builder.toString());
		
	}

	
	private boolean isInputmethodEnabled(InputMethodInfo imi) {
		if(imi==null){
			return false;
		}
		
		String id=imi.getId();
		for(String s:enabledImList){
			if(s.equals(id)){
				return true;
			}
		}
		
		return false;
	}
	
	
	private void updateDefaultInputmethod(InputMethodInfo imi) {
		if(imi==null){
			return;
		}
		
		if(mCurrentInputmethodId.equals(imi.getId())){
			return;
		}
		
	 mCurrentInputmethodId=imi.getId();	
	 Settings.Secure.putString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD,mCurrentInputmethodId==null? "" : mCurrentInputmethodId);
	
	 List<InputMethodInfo> imiList=getInputMethodList();
	 mImAdapter.updateData(imiList);
	 
	 
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Locale theLanguage = localeInfos[arg2].getLocale();
		LKHomeUtil.showToast(getApplicationContext(), R.string.text_changing);
		updateLocate(theLanguage);
		adapter.notifyDataSetChanged();

	}

	private static String getDisplayName(Locale l, String[] specialLocaleCodes,
			String[] specialLocaleNames) {
		String code = l.toString();

		for (int i = 0; i < specialLocaleCodes.length; i++) {
			if (specialLocaleCodes[i].equals(code)) {
				return specialLocaleNames[i];
			}
		}

		return l.getDisplayName(l);
	}

	private static String toTitleCase(String s) {
		if (s.length() == 0) {
			return s;
		}

		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	class LocateAdapter extends BaseAdapter {
		private Context con;
		private int layoutid;
		private int textid;
		private int imageid;
		private LocaleInfo[] infos;
		private Locale locale;
		private Holder holder;

		public LocateAdapter(Context con, int layoutid, int textid,
				int imageid, LocaleInfo[] infos, Locale locale) {
			this.con = con;
			this.layoutid = layoutid;
			this.textid = textid;
			this.imageid = imageid;
			this.infos = infos;
			this.locale = locale;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return infos[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new Holder();
				convertView = inflater.inflate(this.layoutid, parent, false);
				holder.text = (TextView) convertView.findViewById(this.textid);
				holder.image = (ImageView) convertView
						.findViewById(this.imageid);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			LocaleInfo item = (LocaleInfo) getItem(position);
			holder.text.setText(item.toString());
			holder.text.setTextLocale(item.getLocale());
			if (item.locale.getCountry().equals(this.locale.getCountry())
					&& item.getLocale().getLanguage()
							.equals(this.locale.getLanguage())) {
				holder.image.setVisibility(View.VISIBLE);
				holder.text.setTextColor(Constant.COLOR_BLACK);
			} else {
				holder.text.setTextColor(Constant.COLOR_WHITE);
				holder.image.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

	}

	class Holder {
		TextView text;
		ImageView image;
	}

	public static class LocaleInfo implements Comparable<LocaleInfo> {
		static final Collator sCollator = Collator.getInstance();

		String label;
		Locale locale;

		public LocaleInfo(String label, Locale locale) {
			this.label = label;
			this.locale = locale;
		}

		public String getLabel() {
			return label;
		}

		public Locale getLocale() {
			return locale;
		}

		@Override
		public String toString() {
			return this.label;
		}

		@Override
		public int compareTo(LocaleInfo another) {
			return sCollator.compare(this.label, another.label);
		}
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("ken", "--2---=" + " -MainActivity.onResume()");
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i("ken", "--5---="+ " -MainActivity.onStart()");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("ken", "--3---=" + " -MainActivity.onPause()");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("ken", "--4---=" + " -MainActivity.onDestroy()");
	}
	
	

	private void updateLocate(Locale locale) {

		try {
			IActivityManager am = ActivityManagerNative.getDefault();
			Configuration config = am.getConfiguration();

			// Will set userSetLocale to indicate this isn't some passing
			// default - the user
			// wants this remembered
			config.setLocale(locale);

			am.updateConfiguration(config);
			// Trigger the dirty bit for the Settings Provider.
			BackupManager.dataChanged("com.android.providers.settings");
		} catch (RemoteException e) {
			// Intentionally left blank
		}

	}
	
	
	//======================InputMethod==============================
	
	private List<InputMethodInfo> getInputMethodList(){
		
		  mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 
		/*  imList=mImm.getInputMethodList();

	      for(InputMethodInfo imi:imList){
	    	  //String id=imi.getId();
	    	 // String packageName=imi.getPackageName();
	    	 // String setting=imi.getSettingsActivity();
	    	 // String label = imi.loadLabel(pm).toString();
	    	  //inmNameList.add(label);
	    	 // Log.e(TAG, "=====输入法列表id:="+id+",pkgname="+packageName+",setting="+setting);
	      }*/
		  return mImm.getInputMethodList();
	      
	     
	    
	}
	
	public static CharSequence getCurrentInputMethodName(Context context, ContentResolver resolver,
            InputMethodManager imm, List<InputMethodInfo> imis, PackageManager pm) {
        if (resolver == null || imis == null) return null;
        final String currentInputMethodId = Settings.Secure.getString(resolver,
                Settings.Secure.DEFAULT_INPUT_METHOD);
        if (TextUtils.isEmpty(currentInputMethodId)) return null;
        for (InputMethodInfo imi : imis) {
            if (currentInputMethodId.equals(imi.getId())) {
                final InputMethodSubtype subtype = imm.getCurrentInputMethodSubtype();
                final CharSequence imiLabel = imi.loadLabel(pm);
                final CharSequence summary = subtype != null
                        ? TextUtils.concat(subtype.getDisplayName(context,
                                    imi.getPackageName(), imi.getServiceInfo().applicationInfo),
                                            (TextUtils.isEmpty(imiLabel) ?
                                                    "" : " - " + imiLabel))
                        : imiLabel;
                return summary;
            }
        }
        return null;
    }
	
	
	
	
	class InputmethodAdapter extends BaseAdapter{

		private Context mContext;
		private List<InputMethodInfo> imiList;
		private LayoutInflater inflater;
		private PackageManager pm ;

		
		public InputmethodAdapter(Context mContext, List<InputMethodInfo> imList) {
			super();
			this.mContext = mContext;
			this.imiList = imList;
			inflater=LayoutInflater.from(mContext);
			pm=mContext.getPackageManager();
		}

		public void updateData(List<InputMethodInfo> data){
			if(data!=null){
				imiList=data;
				notifyDataSetChanged();
			}
			
		}
		
		
		@Override
		public int getCount() {
			return imiList==null? 0 : imiList.size();
		}

		@Override
		public Object getItem(int position) {
			return imiList==null? null : imiList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if(convertView==null){
				convertView=inflater.inflate(R.layout.language_item, parent,false);
				holder=new ViewHolder();
				
				holder.tv_name=(TextView) convertView.findViewById(R.id.language);
			    holder.iv_current=(ImageView) convertView.findViewById(R.id.current);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			
			
			if(position>=0 && position<imiList.size()){
			
			    InputMethodInfo imin=imiList.get(position);
				String label = imin.loadLabel(pm).toString();
				Drawable dw=  imin.loadIcon(pm);
				
				//holder.iv_icon.setImageDrawable(dw);
				holder.tv_name.setText(label);
				
				if(imin.getId().equals(mCurrentInputmethodId)){
					holder.tv_name.setTextColor(Constant.COLOR_BLACK);
					holder.iv_current.setVisibility(View.VISIBLE);
				}else{
					holder.tv_name.setTextColor(Constant.COLOR_WHITE);
					holder.iv_current.setVisibility(View.GONE);
				}
				
			
			  
			}
			
			
			
			return convertView;
		}

		
		 class ViewHolder{
			 TextView tv_name;
			 ImageView iv_current;
			 ImageButton ib_set;
		 }
		
	}
	
	
	public void addBugLog(String msg) {
		if(LKHomeUtil.DEBUG){
			Log.i("ez2", msg);
		}

	}
	
	
}
