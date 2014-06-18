package com.lenkeng.appmarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeCache;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenkeng.adapter.AppAdapter;
import com.lenkeng.adapter.GameAdapter;
import com.lenkeng.adapter.MarketPageAdapter;
import com.lenkeng.adapter.MovieAdapter;
import com.lenkeng.adapter.SearchAdapter;
import com.lenkeng.adapter.WrapAdapter;
import com.lenkeng.bean.DataEntity;
import com.lenkeng.bean.URLs;
import com.lenkeng.logic.Logic;
import com.lenkeng.ui.LKPager;


public class MainActivity extends Activity implements OnFocusChangeListener,
		OnPageChangeListener, OnItemClickListener, OnClickListener,
		OnKeyListener ,OnCheckedChangeListener{

	private static final String TAG = "MainActivity";
	public static final int DATA_TYPE_MOVIE=0;
	public static final int DATA_TYPE_APP=1;
	public static final int DATA_TYPE_GAME=2;
	public static final int DATA_TYPE_SEARCH=3;
	
	private MarketPageAdapter pageAdapter;
	private Logic logic;
	private Boolean flag;

	// widget
	private RadioGroup ui_radio_group;
	private RadioButton rb_cate_movie;
	private RadioButton rb_cate_app;
	private RadioButton rb_cate_game;
	private RadioButton rb_cate_search;

	// ui controll
	private LinearLayout ll_controller;
	private Button btn_up;
	private Button btn_down;
	private TextView tv_currPage;
	private ImageView iv_cate;
	private Button btn_search;
	private EditText et_search_input;
	private TextView tv_search_title;
	private TextView tv_no_result;

	// views
	private LayoutInflater inflater;
	private LKPager body;
	private View view_movie;
	private View view_app;
	private View view_game;
	private View view_search;
	private View ll_root;
	private List<View> viewList = new ArrayList<View>();

	// progress widget
	private RelativeLayout progress_live;
	private RelativeLayout progress_movie;
	private RelativeLayout progress_app;
	private RelativeLayout progress_game;
	private RelativeLayout progress_search;

	// helper define
	private int currentPage;
	private AppInfo currentApp;
	// find the grid
	private GridView grid_movie;
	private GridView grid_app;
	private GridView grid_game;
	private GridView grid_search;

	// adatper
	// private DataAdapter adapter;
	private MovieAdapter adapter_movie;
	private AppAdapter adapter_app;
	private GameAdapter adapter_game;
	private SearchAdapter adapter_search;

	private DataEntity entity_movie;
	private DataEntity entity_app;
	private DataEntity entity_game;
	private DataEntity entity_search;

	// main data wrapper
	private Map<Integer, DataEntity> map_movie = new HashMap<Integer, DataEntity>();
	private Map<Integer, DataEntity> map_app = new HashMap<Integer, DataEntity>();
	private Map<Integer, DataEntity> map_game = new HashMap<Integer, DataEntity>();
	private List<Map<Integer, DataEntity>> manager_map = new ArrayList<Map<Integer, DataEntity>>();
	private BroadcastReceiver mHideInstallReceiver;
	// controller

	private static int mSubIndex = -1;
	private boolean isFirstRun = false;

	private boolean isLoadding=false;
	
	// define hander
	Handler handler_movie = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(currentPage==0){
				isLoadding=false;
			}
			
			switch (msg.what) {
			case 0:// get entity from web
				entity_movie = (DataEntity) (msg.obj);
				map_movie.put(entity_movie.getCurrentPage(), entity_movie);
				adapter_movie.setDataEntity(entity_movie, grid_movie);
				
				progress_movie.setVisibility(View.GONE);
				grid_movie.setVisibility(View.VISIBLE);
				freshControllerLayout(entity_movie);

				break;
			case 1:
				Log.i("ken", "failere");
				break;

			default:
				break;
			}
		}

	};
	Handler handler_app = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			if(currentPage==1){
				isLoadding=false;
			}
			
			switch (msg.what) {
			case 0:
				entity_app = (DataEntity) (msg.obj);
				map_app.put(entity_app.getCurrentPage(), entity_app);
				adapter_app.setDataEntity(entity_app, grid_app);
				
				progress_app.setVisibility(View.GONE);
				grid_app.setVisibility(View.VISIBLE);
				freshControllerLayout(entity_app);
				break;
			case 1:
				Log.i("ken", "failere");
				break;

			default:
				break;
			}
		}

	};

	Handler handler_game = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			if(currentPage==2){
				isLoadding=false;
			}
			
			switch (msg.what) {
			case 0:
				entity_game = (DataEntity) (msg.obj);
				map_game.put(entity_game.getCurrentPage(), entity_game);
				adapter_game.setDataEntity(entity_game, grid_game);
				
				progress_game.setVisibility(View.GONE);
				grid_game.setVisibility(View.VISIBLE);
				freshControllerLayout(entity_game);
				break;
			case 1:
				Log.i("ken", "failere");
				break;

			default:
				break;
			}
		}

	};
	Handler handler_search = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			if(currentPage==3){
				isLoadding=false;
			}
			
			progress_search.setVisibility(View.GONE);
			btn_search.setClickable(true);
			
			switch (msg.what) {
			case 0:
				entity_search = (DataEntity) (msg.obj);
				if (entity_search != null && entity_search.getData().size() > 0) {
					adapter_search.setDataEntity(entity_search, grid_search);
					tv_search_title.setText(flag ? R.string.text_search_result
							: R.string.text_recommend);
					grid_search.setVisibility(View.VISIBLE);
					tv_no_result.setVisibility(View.GONE);
					
					btn_search.setNextFocusDownId(R.id.gridView_search);
					et_search_input.setNextFocusDownId(R.id.gridView_search);
					rb_cate_search.setNextFocusUpId(R.id.gridView_search);
				//	freshControllerLayout(entity_search);
				} else {
					grid_search.setVisibility(View.GONE);
					tv_no_result.setVisibility(View.VISIBLE);
					btn_search.setNextFocusDownId(R.id.radio4);
					et_search_input.setNextFocusDownId(R.id.radio4);
					rb_cate_search.setNextFocusUpId(R.id.search_input);
				}
				// ui_search_input.requestFocus();

				break;
			case 1:
				Log.i("ken", "failere");
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		map_movie.clear();
		map_game.clear();


		isFirstRun = true;
		currentPage = 0;
		initWidget();
		logic = Logic.getInstance(getApplicationContext());
		initAdapter();
		initApkHideInstallReceiver();
	}

	/**
	 * 隐式安装监听器
	 */
	private void initApkHideInstallReceiver() {
		
		IntentFilter tFilter = new IntentFilter();
		tFilter.addAction(Constant.ACTION_INSTALED);
		mHideInstallReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String flag=intent.getStringExtra("installFlag");
				String packageName=intent.getStringExtra("packageName");
				if("install".equals(flag)){
					Logger.e(TAG, "-----隐式安装apk完成.intent="+intent.getExtras()+",action="+intent.getAction());
				    switch (currentPage) {
					case 0: 
						adapter_movie.notifyDataSetChanged();
						break;
					case 1:
						adapter_app.notifyDataSetChanged();
						break;
					case 2:
						adapter_game.notifyDataSetChanged();
						break;

					default:
						break;
					}
				}
				
			}
		};
		this.registerReceiver(mHideInstallReceiver, tFilter);
	}
	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case 92:
			pageUp();
			break;
		case 93:
			pageDown();
			break;

		default:
			break;
		}

		return super.onKeyUp(keyCode, event);
	}

	private void initAdapter() {
		adapter_movie = new MovieAdapter(this, logic,appItemListener);
		adapter_app = new AppAdapter(this, logic,appItemListener);
		adapter_game = new GameAdapter(this, logic,appItemListener);
		adapter_search = new SearchAdapter(this, logic,appItemListener);
		// set adapter
		grid_game.setAdapter(adapter_game);
		grid_app.setAdapter(adapter_app);
		grid_movie.setAdapter(adapter_movie);
		grid_search.setAdapter(adapter_search);
		// set onitemclicker listener
		grid_movie.setOnItemClickListener(this);
		grid_app.setOnItemClickListener(this);
		grid_game.setOnItemClickListener(this);
		grid_search.setOnItemClickListener(this);
		// set key listener
		grid_movie.setOnKeyListener(this);
		grid_app.setOnKeyListener(this);
		grid_game.setOnKeyListener(this);

	}

	private void showThePage(int curPage) {

	
		body.setCurrentItem(curPage, false);
		
		switch (curPage) {
		case 0:
			initMovieLayout();
			break;
		case 1:
			initAppLayout();
			break;
		case 2:
			initGameLayout();
			break;
		case 3:
			initSearchLayout();
			break;
		default:
			break;
		}

	}

	int counter=0;
	
	private void initSearchLayout() {
		// ui_search_input.requestFocus();
		if (isFirstRun) {
			et_search_input.requestFocus();
			isFirstRun = false;
			progress_search.setVisibility(View.GONE);
			logic.getRecommApps(handler_search);
			flag = false;
		}else{
			progress_search.setVisibility(View.GONE);
			ll_controller.setVisibility(View.GONE);
		}
		tv_no_result.setVisibility(View.GONE);
		// if (entity_search == null || entity_search.getData().size() < 0) {
		
		
		// }
		// ui_search_input.requestFocus();
	}

	private void initGameLayout() {

		DataEntity tGame = manager_map.get(currentPage).get(
				mSubIndex != -1 ? mSubIndex : 1);
		if (tGame != null) {
			handMessage(handler_game, tGame);

		} else if (entity_game == null || entity_game.getData().size() <= 0) {
			progress_game.setVisibility(View.VISIBLE);
			logic.getGameApps(handler_game);
		}

		if (entity_game != null) {
			tv_currPage.setText(String.valueOf(entity_game.getCurrentPage()));
		}
	}

	private void initAppLayout() {
		DataEntity tApp = manager_map.get(currentPage).get(
				mSubIndex != -1 ? mSubIndex : 1);
		if (tApp != null) {
			handMessage(handler_app, tApp);

		} else if (entity_app == null || entity_app.getData().size() <= 0) {
			progress_app.setVisibility(View.VISIBLE);
			logic.getAppApps(handler_app);
		}
		if (entity_app != null) {
			tv_currPage.setText(String.valueOf(entity_app.getCurrentPage()));
		}
	}

	private void initMovieLayout() {
		DataEntity tMovie = manager_map.get(currentPage).get(
				mSubIndex != -1 ? mSubIndex : 1);
		if (tMovie != null) {
			handMessage(handler_movie, tMovie);
		} else if (entity_movie == null || entity_movie.getData().size() <= 0) {
			progress_movie.setVisibility(View.VISIBLE);
			logic.getMovieApps(handler_movie);
		}
		if (entity_movie != null) {
			tv_currPage.setText(String.valueOf(entity_movie.getCurrentPage()));
		}
	}

	private void initWidget() {
		// load widget

		ll_root=findViewById(R.id.root);
		ll_root.setOnKeyListener(this);
		
		manager_map.add(map_movie);
		manager_map.add(map_app);
		manager_map.add(map_game);

		iv_cate = (ImageView) findViewById(R.id.imageView1);
		ui_radio_group = (RadioGroup) findViewById(R.id.radioGroup1);
		ui_radio_group.setOnFocusChangeListener(this);
		
		rb_cate_movie = (RadioButton) findViewById(R.id.radio1);
		rb_cate_movie.setOnFocusChangeListener(this);
		rb_cate_movie.setOnClickListener(this);
		rb_cate_movie.setOnCheckedChangeListener(this);

		rb_cate_app = (RadioButton) findViewById(R.id.radio2);
		rb_cate_app.setOnFocusChangeListener(this);
		rb_cate_app.setOnClickListener(this);
		rb_cate_app.setOnCheckedChangeListener(this);
		
		rb_cate_game = (RadioButton) findViewById(R.id.radio3);
		rb_cate_game.setOnFocusChangeListener(this);
		rb_cate_game.setOnClickListener(this);
		rb_cate_game.setOnCheckedChangeListener(this);
		

		rb_cate_search = (RadioButton) findViewById(R.id.radio4);
		rb_cate_search.setOnFocusChangeListener(this);
		rb_cate_search.setOnClickListener(this);
		rb_cate_search.setOnCheckedChangeListener(this);

		// page controller
		ll_controller = (LinearLayout) findViewById(R.id.page_control);
		btn_up = (Button) findViewById(R.id.up);
		btn_down = (Button) findViewById(R.id.down);
		tv_currPage = (TextView) ll_controller.findViewById(R.id.currPage);
		btn_up.setOnClickListener(this);
		btn_down.setOnClickListener(this);

		
		// load Views

		body = (LKPager) findViewById(R.id.body);
		// body.setHovered(false);

		inflater = getLayoutInflater();
		view_movie = inflater.inflate(R.layout.movie, null);
		view_app = inflater.inflate(R.layout.app, null);
		view_game = inflater.inflate(R.layout.game, null);
		view_search = inflater.inflate(R.layout.search, null);
		btn_search = (Button) view_search.findViewById(R.id.do_search);
		btn_search.setOnClickListener(this);
		et_search_input = (EditText) view_search
				.findViewById(R.id.search_input);
		et_search_input.setOnKeyListener(keyListener);
		tv_search_title = (TextView) view_search
				.findViewById(R.id.msg_search_title);

		// progres find
		progress_movie = (RelativeLayout) view_movie
				.findViewById(R.id.movie_loading);
		progress_app = (RelativeLayout) view_app.findViewById(R.id.app_loading);
		progress_game = (RelativeLayout) view_game
				.findViewById(R.id.game_loading);
		progress_search = (RelativeLayout) view_search
				.findViewById(R.id.search_loading);

		tv_no_result = (TextView) view_search.findViewById(R.id.no_result);
		viewList.add(view_movie);
		viewList.add(view_app);
		viewList.add(view_game);
		viewList.add(view_search);
		pageAdapter = new MarketPageAdapter(viewList);
		body.setAdapter(pageAdapter);
		body.setOnPageChangeListener(this);

		// find the grids
		grid_movie = (GridView) view_movie.findViewById(R.id.gridView_movie);
		grid_movie.setOnItemClickListener(this);
		grid_movie.setNextFocusDownId(R.id.radio1);
		
		grid_app = (GridView) view_app.findViewById(R.id.gridView_app);
		grid_app.setOnItemClickListener(this);
		grid_app.setNextFocusDownId(R.id.radio2);
		
		grid_game = (GridView) view_game.findViewById(R.id.gridView_game);
		grid_game.setOnItemClickListener(this);
		grid_game.setNextFocusDownId(R.id.radio3);
		
		grid_search = (GridView) view_search.findViewById(R.id.gridView_search);
		grid_search.setOnItemClickListener(this);
		grid_search.setNextFocusDownId(R.id.radio4);


		
		
		
	}

	@SuppressLint("InlinedApi")
	private OnKeyListener keyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if(event.getAction()==KeyEvent.ACTION_UP){
				
				switch (keyCode) {
				case KeyEvent.KEYCODE_ENTER:
				//case KeyEvent.KEYCODE_DPAD_CENTER:
					
					btn_search.performClick();
					
					return true;
					
				default:
					break;
				}
			}
			return false;
		}
	};


	@Override
	protected void onResume() {
		super.onResume();
		Intent tIni = getIntent();
		int tFlag = currentPage;
		if (tIni.getStringExtra("flag") != null) {
			tFlag = Integer.parseInt(tIni.getStringExtra("flag"));
			switch (tFlag) {
			case 4:
				currentPage = 0;
				break;
			case 3:
				
				currentPage = 1;
				break;
				
			case 2:
				currentPage = 2;
				break;

			default:
				break;
			}
			((RadioButton) ui_radio_group.getChildAt(currentPage))
					.performClick();
			getIntent().removeExtra("flag");
		}

		showThePage(currentPage);
	}

	private void notifyAllGrid() {
		adapter_app.notifyDataSetChanged();
		adapter_game.notifyDataSetChanged();
		adapter_movie.notifyDataSetChanged();
		adapter_search.notifyDataSetChanged();

	}

	protected void onDestroy() {
		unregisterReceiver(mHideInstallReceiver);
		super.onDestroy();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			switch (v.getId()) {
			case R.id.radio1:
				currentPage = 0;
				/*View movie = grid_movie.getSelectedView();
				if (movie != null && hasFocus) {
					//movie.setBackgroundResource(R.drawable.market_item_def_bg);
				}*/
				break;
			case R.id.radio2:
				currentPage = 1;
				/*View app = grid_app.getSelectedView();
				if (app != null && hasFocus) {
					//app.setBackgroundResource(R.drawable.market_item_def_bg);
				}*/
				break;
			case R.id.radio3:
				currentPage = 2;
				/*View game = grid_game.getSelectedView();
				if (game != null && hasFocus) {
					//game.setBackgroundResource(R.drawable.market_item_def_bg);
				}*/
				break;
			case R.id.radio4:
				currentPage = 3;
				//View search = grid_search.getSelectedView();
				//if (search != null && hasFocus) {
					//search.setBackgroundResource(R.drawable.market_item_def_bg);
				//}
				break;
			default:
				break;
			}
			// if (hasFocus) {
			((RadioButton) ui_radio_group.getChildAt(currentPage))
					.setChecked(true);
			/*mSubIndex = 1;
			showThePage(currentPage);*/
			// }
		}
	}
	

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

		switch (arg0) {
		case 0:
			rb_cate_movie.requestFocus();
			freshControllerLayout(entity_movie);
			break;
		case 1:
			rb_cate_app.requestFocus();
			freshControllerLayout(entity_app);
			break;
		case 2:
			rb_cate_game.requestFocus();
			freshControllerLayout(entity_game);
			break;
		case 3:
			rb_cate_search.requestFocus();
			freshControllerLayout(entity_search);
			break;

		default:
			break;
		}

	}

	private void freshControllerLayout(DataEntity entity) {
		if(currentPage==3){ //搜索页不显示翻页按钮
			
			ll_controller.setVisibility(View.GONE);
			
		}else if (entity != null ) {
			int tCurrPage = entity.getCurrentPage();
			int tAmount = entity.getPageAmount();

			tv_currPage.setText(String.valueOf(tCurrPage));

			if (tCurrPage == tAmount && tCurrPage == 1) {
				ll_controller.setVisibility(View.GONE);
			} else if (tAmount == 0) {
				ll_controller.setVisibility(View.GONE);
			} else {
				if (tAmount == tCurrPage) {
					ll_controller.setVisibility(View.VISIBLE);
					btn_up.setVisibility(View.VISIBLE);
					btn_down.setVisibility(View.INVISIBLE);
				} else if (tCurrPage == 1) {
					ll_controller.setVisibility(View.VISIBLE);
					btn_up.setVisibility(View.INVISIBLE);
					btn_down.setVisibility(View.VISIBLE);

				} else {
					ll_controller.setVisibility(View.VISIBLE);
					btn_up.setVisibility(View.VISIBLE);
					btn_down.setVisibility(View.VISIBLE);
				}

			}

		} else {
			ll_controller.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		AppInfo tAppInfo = null;
		switch (currentPage) {
		case 0:
			tAppInfo = entity_movie.getData().get(arg2);
			mSubIndex = entity_movie.getCurrentPage();
			break;
		case 1:
			tAppInfo = entity_app.getData().get(arg2);
			mSubIndex = entity_app.getCurrentPage();
			break;
		case 2:
			tAppInfo = entity_game.getData().get(arg2);
			mSubIndex = entity_game.getCurrentPage();
			break;
		case 3:
			tAppInfo = entity_search.getData().get(arg2);
			break;

		default:
			break;
		}
		// arg0.requestFocus();
		Intent tIni = new Intent(this, DetailActivity.class);
		tIni.putExtra("appinfo", tAppInfo);
		this.startActivity(tIni);
		
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.radio1:  //影视
			currentPage = 0;
			((RadioButton) ui_radio_group.getChildAt(currentPage))
					.setChecked(true);
			mSubIndex = 1;
			showThePage(currentPage);
			break;
		case R.id.radio2:  //应用
			mSubIndex = 1;
			currentPage = 1;
			((RadioButton) ui_radio_group.getChildAt(currentPage))
					.setChecked(true);
			showThePage(currentPage);

			break; 
		case R.id.radio3:  //游戏
			currentPage = 2;
			mSubIndex = 1;
			((RadioButton) ui_radio_group.getChildAt(currentPage))
					.setChecked(true);
			showThePage(currentPage);

			break;
		case R.id.radio4: //搜索
			currentPage = 3;
			mSubIndex = 1;
			((RadioButton) ui_radio_group.getChildAt(currentPage))
					.setChecked(true);
			showThePage(currentPage);

			break;

		case R.id.up:
			pageUp();
			break;
		case R.id.down:
			pageDown();
			break;

		case R.id.do_search: //搜素按钮
			String key = et_search_input.getText().toString().trim();
			if(key.length()==0){
				return;
			}
			
			closeKeyBoard();
			
			tv_no_result.setVisibility(View.GONE);
			grid_search.setVisibility(View.GONE);
			progress_search.setVisibility(View.VISIBLE);
			btn_search.setClickable(false);
			
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("key", key);
			logic.getSearchApps(handler_search, param);
			flag = true;
			break;
		default:
			break;
		}
	}

	private void pageDown() {
		
		isLoadding=true;
		Logger.e(TAG, "------修改isloading="+isLoadding);
		
		switch (currentPage) {
		case 0:
			progress_movie.setVisibility(View.VISIBLE);
			grid_movie.setVisibility(View.GONE);
			getPageAppByFlag(entity_movie, handler_movie, true, currentPage);
			break;
		case 1:
			progress_app.setVisibility(View.VISIBLE);
			grid_app.setVisibility(View.GONE);
			getPageAppByFlag(entity_app, handler_app, true, currentPage);
			break;
		case 2:
			progress_game.setVisibility(View.VISIBLE);
			grid_game.setVisibility(View.GONE);
			getPageAppByFlag(entity_game, handler_game, true, currentPage);
			break;
		case 3:
			//progress_search.setVisibility(View.VISIBLE);
			//grid_search.setVisibility(View.GONE);
			getPageAppByFlag(entity_search, handler_search, true, currentPage);
			break;

		default:
			break;
		}
	}

	private void pageUp() {
		switch (currentPage) {
		case 0:
			getPageAppByFlag(entity_movie, handler_movie, false, currentPage);
			break;
		case 1:
			getPageAppByFlag(entity_app, handler_app, false, currentPage);
			break;
		case 2:
			getPageAppByFlag(entity_game, handler_game, false, currentPage);
			break;
		case 3:
			getPageAppByFlag(entity_search, handler_search, false, currentPage);
			break;

		default:
			break;
		}
	}

	/*
	 * flag =true +1 falg =false -1
	 */

	private void getPageAppByFlag(DataEntity entity, Handler handler,
			boolean flag, int page) {
		try {
			int tCurrentPage = entity.getCurrentPage();
			HashMap<String, String> param = new HashMap<String, String>();
			int tIndex = tCurrentPage + (flag ? 1 : -1);
			int tAmount = entity.getPageAmount();
			if (tIndex > tAmount || tIndex < 1) {
				return;
			}
			DataEntity data = manager_map.get(page).get(tIndex);
			if (data != null) {
				handMessage(handler, data);
				return;
			} else {
				param.put("page", String.valueOf(tIndex));
				logic.getApps(handler, param, page);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handMessage(Handler handler, DataEntity data) {
		Message msg = new Message();
		msg.what = 0;// �ɹ�
		msg.obj = data;
		handler.sendMessage(msg);
	}

	@Override
	public boolean onKey(View view, int keycode, KeyEvent event) {
		
	/*	if(keycode==KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN && view==ll_root){
			Logger.e(TAG, "======按钮键盘向下按钮,isLoading="+isLoadding);
			if(isLoadding){
				return true;
			}
		}*/
		
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			GridView tGv = (GridView) view;
			int tPosition = tGv.getSelectedItemPosition();
			int tGridAmount = tGv.getAdapter().getCount();
			int tColum = 3;
			WrapAdapter tWrapAdapter = (WrapAdapter) tGv.getAdapter();
			DataEntity tData = tWrapAdapter.getDataEntity();
			int tCurrPage = tData.getCurrentPage();
			int tAmount = tData.getPageAmount();

			if (tAmount > 1) {
				if (keycode == KeyEvent.KEYCODE_DPAD_DOWN) {

					if (tPosition >= 5 && tPosition <= 9
							&& tCurrPage != tAmount) {
						pageDown();
						return true;
					} else {
						return false;
					}
				} else if (keycode == KeyEvent.KEYCODE_DPAD_UP) {
					if (tPosition < 5 && tPosition >= 0) {
						pageUp();
						// return true;
					}
				}
				

			}
		}
		return false;
	}
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Logger.e(TAG, "=======dispatchKeyEvent, isLoading="+isLoadding);
		if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN && event.getAction()==KeyEvent.ACTION_DOWN&& isLoadding){
     	    
         return true;
     }
     return super.dispatchKeyEvent(event);
}
		
	
/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN && event.getAction() == KeyEvent.ACTION_DOWN && isLoadding ){
			Logger.e(TAG, "======按钮键盘向下按钮,isLoading="+isLoadding);
				return true;
		}else{
			
			return super.onKeyDown(keyCode, event);
		}
	}*/
	
	
	/**
	 * 监听Adapter的点击事件
	 * @author Administrator
	 *
	 */
	public interface OnAppItemClickListener{
		public void onAppItemClick(int position,int dataType);
	}
	
  private OnAppItemClickListener appItemListener=new OnAppItemClickListener() {
		
		@Override
		public void onAppItemClick(int position,int dataType) {
			onItemClick(null, null, position, 0);
		}
	};
	
	private void closeKeyBoard() {
		InputMethodManager imm =   (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(et_search_input!=null){
			
			imm.hideSoftInputFromWindow(et_search_input.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		
		}


	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		mSubIndex = 1;
		showThePage(currentPage);
		
		
	}
	
	
}
