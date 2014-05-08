package lenkeng.com.welcome;

import java.util.ArrayList;


import java.util.List;

import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class TestLauncherActivity extends Activity implements OnItemClickListener {
	
	private static String UNINSTALL_DEMOLAUNCHER="unistall_demo";
	private static String FACTORY_MODE="8811101214";
	private TextView tv_lkid;
	private GridView gv_apps;
	private List<ApplicationInfo> aiList;
	private LayoutInflater inflater;
	private PackageManager pm;
	private EditText et_pwd;
	private static List<String> tagetList=new ArrayList<String>();
	private String factory;
	private SharedPreferences sp;
	private ApplicationInfoAdapter adapter;
	private LKHomeUtil lkUtil;
	static {
		tagetList.add("com.android.gallery3d");
		tagetList.add("com.android.browser");
		tagetList.add("com.softwinner.TvdFileManager");
		tagetList.add("yuku.luyinji.full");
		tagetList.add("com.android.settings");
		tagetList.add("com.android.soundrecorder");
		tagetList.add("com.android.music");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*PackageManager pm = getPackageManager();
        ComponentName name = new ComponentName("com.lenkeng.welcome", "com.lenkeng.welcome.MainHomeActivity");
        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);*/
		//setDefault();
		//onIntentSelected(true);
		lkUtil= LKHomeUtil.getInstance(this.getApplicationContext());
		//lkUtil.onIntentSelected("lenkeng.com.welcome.TestLauncherActivity");
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		pm=getPackageManager();
		aiList=new ArrayList<ApplicationInfo>();
		adapter=new ApplicationInfoAdapter();
		inflater=getLayoutInflater();
		gv_apps=(GridView) this.findViewById(R.id.allApps);
		gv_apps.setAdapter(adapter);
		gv_apps.setOnItemClickListener(this);
		tv_lkid=(TextView) this.findViewById(R.id.lkid);
		et_pwd=(EditText) this.findViewById(R.id.pwd);
		tv_lkid.setText(getUserName());
		factory=getIntent().getStringExtra("factory");
		
	}
	public void uninstall(View v) {
		String s=et_pwd.getText().toString();
		if("lk777".equals(s)){
			Intent intent=new Intent();
			intent.setAction(UNINSTALL_DEMOLAUNCHER);
			sendBroadcast(intent);
			uninstall(getPackageName());
		}
	}

	public void uninstall(String packageName) {

		Uri packageURI = Uri.parse("package:" + packageName);

		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,

		packageURI);

		startActivity(uninstallIntent);

	}
	public  List<ApplicationInfo> getUserInstallApp() {
		
		List<ApplicationInfo> temps=pm.getInstalledApplications(PackageManager.GET_SHARED_LIBRARY_FILES);
		
		for (ApplicationInfo packInfo : temps) {
			String packageName=packInfo.packageName;
			if(tagetList.contains(packageName)){
				aiList.add(packInfo);
			}
		}
		return aiList;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_DEL){
    		ss="";
    	}else{
    		buildString(keyCode+"");
    	}
    	if(keyCode ==KeyEvent.KEYCODE_BACK){
    		return true;
    	}else{
    		return super.onKeyDown(keyCode, event);
    	}
    }
    String ss="";
	private void buildString(String key){
		ss+=key;
		if(ss.length() >"101013131616".length() ){
			ss="";
		}
		if("101013131616".equals(ss)){
			Editor eidt=sp.edit();
			eidt.putString("isFactory", "");
			eidt.commit();
			this.finish();
			ss="";
			/*File f=new File("/sdcard/appinfo/DemoLauncher.apk");
			if(f.exists()){
				f.delete();
			}*/
			//lkUtil.onIntentSelected("lenkeng.com.welcome.MainHomeActivity");
		}
		Log.i("tag", "----ss---"+ss);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		ApplicationInfo info=(ApplicationInfo) adapter.getItem(position);
		Intent intent = getPackageManager()
				.getLaunchIntentForPackage(info.packageName);

		if (intent != null) {
			startActivity(intent);
		}
		
	}
	
	public static String getUserName() {

		String s = LKHomeUtil.getUserName();
		return s;
	}
	
	private BroadcastReceiver packageListener=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public class ApplicationInfoAdapter extends BaseAdapter{
		public ApplicationInfoAdapter(){
			getUserInstallApp();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return aiList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return aiList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh=null;
			if(convertView == null){
				vh=new ViewHolder();
				convertView =inflater.inflate(R.layout.app_items, null);
				vh.iv_con=(ImageView) convertView.findViewById(R.id.appIcon);
				vh.tv_name=(TextView) convertView.findViewById(R.id.appName);
				convertView.setTag(vh);
			}else{
				vh=(ViewHolder) convertView.getTag();
			}
			ApplicationInfo info=aiList.get(position);
			vh.iv_con.setBackgroundDrawable(info.loadIcon(pm));
			vh.tv_name.setText(info.loadLabel(pm));
			return convertView;
		}
		
	}
	static class ViewHolder{
		ImageView iv_con;
		TextView tv_name;
	}
}
