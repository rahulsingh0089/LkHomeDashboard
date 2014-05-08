package lenkeng.com.welcome;

import java.util.ArrayList;

import java.util.List;

import lenkeng.com.welcome.adapter.AppQueryAdapter;
import lenkeng.com.welcome.adapter.MyViewPagerAdapter;
import lenkeng.com.welcome.bean.AppInfo;
import lenkeng.com.welcome.util.AnimationFactory;
import lenkeng.com.welcome.util.Constant;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;
import lenkeng.com.welcome.view.MyScollView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


@SuppressLint("NewApi")
public class ViewPagerFactory {

	private static final String TAG = "ViewPagerFactory";

	private boolean isTest = false;

	private Activity activity;
	private ViewPager vp_movie;
	private ViewPager vp_app;
	private ViewPager vp_game;
	private ViewPager vp_user;
	private ViewPager vp_setting;
	private ViewGroup mViewGroup;
	private LayoutInflater inflater;

	private LinearLayout ll_recommend;
	private LinearLayout ll_movie;
	private LinearLayout ll_app;
	private LinearLayout ll_game;
	private LinearLayout ll_user;
	private LinearLayout ll_setting;

	private GridView gv_move;
	private GridView gv_app;
	private GridView gv_game;
	private GridView gv_user;
	private GridView gv_setting;

	private MyScollView moveScroll;
	private MyScollView userScroll;
	private MyScollView appScroll;
	private MyScollView gameScroll;
	private MyScollView settingScroll;

	/*private ImageView movie_arrow_right;
	private ImageView movie_arrow_left;
	private ImageView app_arrow_right;
	private ImageView app_arrow_left;
	private ImageView game_arrow_right;
	private ImageView game_arrow_left;
	// private ImageView scan_arrow_right;
	// private ImageView scan_arrow_left;
	private ImageView user_arrow_right;
	private ImageView user_arrow_left;*/
	// private ImageView setting_arrow_right;
	// private ImageView setting_arrow_left;

	private MyViewPagerAdapter vp_movie_adapter;
	private MyViewPagerAdapter vp_app_adapter;
	private MyViewPagerAdapter vp_game_adapter;
	private MyViewPagerAdapter vp_user_adapter;
	private MyViewPagerAdapter vp_setting_adapter;

	private int movieCurrentPageNumber = 0;
	private int appCurrentPageNumber = 0;
	private int gameCurrentPageNumber = 0;
	private int userCurrentPageNumber = 0;
	private int settingCurrentPageNumber = 0;

	private boolean isMovieFirstShow = true;
	private boolean isAppFirstShow = true;
	private boolean isGameFirstShow = true;
	private boolean isUserFirstShow = true;
	private boolean isSettingFirstShow = true;

	private int movie_item_index = 0;
	private int app_item_index = 0;
	private int game_item_index = 0;
	private int user_item_index = 0;
	private int setting_item_index = 0;

	private List<AppInfo> movieList;
	private List<AppInfo> appList;
	private List<AppInfo> gameList;
	private List<AppInfo> userList;
	private List<AppInfo> settingList;

	//private LKHomeUtil homeUtil;
	private OnKeyListener keListener;
	private OnItemClickListener appClickedListener;
	private OnItemSelectedListener appSelectedListener;
	//private OnPageChangeListener pageChanageListener;
	private OnFocusChangeListener focusChangedListener;
	private OnLongClickListener longClickListener;
	//private OnScrollListener onScrollListener;

	public ViewPagerFactory(Activity activity) {
		this.activity = activity;
		//this.homeUtil = new LKHomeUtil(activity);
		inflater = activity.getLayoutInflater();
		findViewPager();
	}

	private void findViewPager() {
		vp_movie = (ViewPager) activity.findViewById(R.id.vp_move);
		vp_app = (ViewPager) activity.findViewById(R.id.vp_app);
		vp_game = (ViewPager) activity.findViewById(R.id.vp_game);
		vp_user = (ViewPager) activity.findViewById(R.id.vp_user);
		vp_setting = (ViewPager) activity.findViewById(R.id.vp_settting);

		ll_recommend = (LinearLayout) activity
				.findViewById(R.id.home_backup_ll_recommend);
		ll_movie = (LinearLayout) activity
				.findViewById(R.id.home_backup_ll_movie);
		ll_app = (LinearLayout) activity.findViewById(R.id.home_backup_ll_app);
		ll_game = (LinearLayout) activity
				.findViewById(R.id.home_backup_ll_game);
		ll_user = (LinearLayout) activity
				.findViewById(R.id.home_backup_ll_user);
		ll_setting = (LinearLayout) activity
				.findViewById(R.id.home_backup_ll_setting);

		
		
		
		moveScroll = (MyScollView) activity.findViewById(R.id.moveScroll);
		userScroll = (MyScollView) activity.findViewById(R.id.userScroll);
		appScroll = (MyScollView) activity.findViewById(R.id.appScroll);
		gameScroll = (MyScollView) activity.findViewById(R.id.gameScroll);
		settingScroll = (MyScollView) activity.findViewById(R.id.settingScroll);

		/*movie_arrow_right = (ImageView) activity
				.findViewById(R.id.movie_arrow_right);
		movie_arrow_left = (ImageView) activity
				.findViewById(R.id.movie_arrow_left);
		app_arrow_right = (ImageView) activity
				.findViewById(R.id.app_arrow_right);
		app_arrow_left = (ImageView) activity.findViewById(R.id.app_arrow_left);
		game_arrow_right = (ImageView) activity
				.findViewById(R.id.game_arrow_right);
		game_arrow_left = (ImageView) activity
				.findViewById(R.id.game_arrow_left);
		// scan_arrow_right = (ImageView) activity
		// .findViewById(R.id.scan_arrow_right);
		// scan_arrow_left = (ImageView) activity
		// .findViewById(R.id.scan_arrow_left);
		user_arrow_right = (ImageView) activity
				.findViewById(R.id.user_arrow_right);
		user_arrow_left = (ImageView) activity
				.findViewById(R.id.user_arrow_left);*/
		// setting_arrow_right = (ImageView) activity
		// .findViewById(R.id.setting_arrow_right);
		// setting_arrow_left = (ImageView) activity
		// .findViewById(R.id.setting_arrow_left);

	}

	/*
	 * private void initViewPager() { item_index = 0; mViewPager = (ViewPager)
	 * this.findViewById(R.id.myViewPager); pageAdapter = new MyPageAdapter();
	 * mViewPager.setAdapter(pageAdapter);
	 * mViewPager.setOnPageChangeListener(new GuidePageChangeListener()); }
	 */

	public void setListener(OnKeyListener listener,
			OnItemClickListener appClickedListener,
			OnItemSelectedListener appSelectedListener,
			OnPageChangeListener pageChanageListener,
			OnFocusChangeListener focusChangedListener,
			OnLongClickListener longClickListener,
			OnScrollListener onScrollListener) {
		this.keListener = listener;
		this.appClickedListener = appClickedListener;
		this.appSelectedListener = appSelectedListener;
		//this.pageChanageListener = pageChanageListener;
		this.focusChangedListener = focusChangedListener;
		this.longClickListener = longClickListener;
		//this.onScrollListener = onScrollListener;
	}

	private int getPageNumber(String style_flag, List<AppInfo> allAppInfos) {
		int pageNumber = 0;
		if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			if ((Constant.SETTING_ICONS.length % 10) != 0) {
				pageNumber = (int) (Constant.SETTING_ICONS.length / 10) + 1;
			} else {
				pageNumber = (int) Constant.SETTING_ICONS.length / 10;
			}
		} else {
			if ((allAppInfos.size() % 10) != 0) {
				pageNumber = (int) (allAppInfos.size() / 10) + 1;
			} else {
				pageNumber = (int) allAppInfos.size() / 10;
			}
		}
		return pageNumber;
	}

	private int getNodeNum(String style_flag, List<AppInfo> allAppInfos) {
		int pageNumber = 0;
		if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			if ((Constant.SETTING_ICONS.length % 5) != 0) {
				pageNumber = (int) (Constant.SETTING_ICONS.length / 5) + 1;
			} else {
				pageNumber = (int) Constant.SETTING_ICONS.length / 5;
			}
		} else {
			if ((allAppInfos.size() % 5) != 0) {
				pageNumber = (int) (allAppInfos.size() / 5) + 1;
			} else {
				pageNumber = (int) allAppInfos.size() / 5;
			}
		}
		Logger.i("kao", "$$$-----getNodeNum---" + pageNumber);
		//pageNumber = pageNumber / 2;

		return pageNumber;
	}

	public void createMoviePager(List<AppInfo> allAppInfos) {
		List<View> views = new ArrayList<View>();
		int pageNum = getPageNumber(Constant.CLASSIFY_MOVIE, allAppInfos);
		movieCurrentPageNumber = pageNum;
		movieList = allAppInfos;
		movie_item_index = 0;
		// for (int i = 0; i < pageNum; i++) {
		for (int i = 0; i < 1; i++) {
			mViewGroup = (ViewGroup) inflater.inflate(
					R.layout.home_view_paper_item, null);
			gv_move = (GridView) mViewGroup.findViewById(R.id.home_app_list);
			gv_move.setOnKeyListener(keListener);
			AppQueryAdapter adapter = new AppQueryAdapter(activity);
			adapter.setStyleFlag(Constant.CLASSIFY_MOVIE);
			// adapter.setItemsData(homeUtil.getAppList(i,Constant.CLASSIFY_MOVIE,
			// allAppInfos), i);
			gv_move.setScrollBarFadeDuration(Integer.MAX_VALUE);
			adapter.setItemsData(allAppInfos, i);
			// adapter.setItemsData(allAppInfos, i);
			gv_move.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			gv_move.setOnScrollListener(moveScrollListener);
			gv_move.setOnFocusChangeListener(focusChangedListener);
			gv_move.setOnItemSelectedListener(appSelectedListener);
			gv_move.setOnItemClickListener(appClickedListener);
			gv_move.setNextFocusDownId(R.id.home_backup_bt_movie);
			gv_move.setNextFocusRightId(0);
			views.add(mViewGroup);
		}
		// if(pageNum>1){
		// moveScroll.createNode(getNodeNum(Constant.CLASSIFY_MOVIE,
		// allAppInfos));

		// }
		// showArrow(Constant.CLASSIFY_MOVIE);
		vp_movie_adapter = new MyViewPagerAdapter();
		vp_movie_adapter.setViews(views);
		vp_movie.setAdapter(vp_movie_adapter);
		// vp_movie.setCurrentItem(0);
		moveScroll.createNode(getNodeNum(Constant.CLASSIFY_MOVIE, allAppInfos));
		//vp_movie.setOnPageChangeListener(pageChanageListener);

	}

	public void createAppPager(List<AppInfo> allAppInfos) {
		List<View> views = new ArrayList<View>();
		int pageNum = getPageNumber(Constant.CLASSIFY_APPLICATION, allAppInfos);
		appCurrentPageNumber = pageNum;
		app_item_index = 0;
		appList = allAppInfos;
		// for (int i = 0; i < pageNum; i++) {
		for (int i = 0; i < 1; i++) {
			mViewGroup = (ViewGroup) inflater.inflate(
					R.layout.home_view_paper_item, null);
			gv_app = (GridView) mViewGroup.findViewById(R.id.home_app_list);
			gv_app.setOnKeyListener(keListener);
			AppQueryAdapter adapter = new AppQueryAdapter(activity);
			adapter.setStyleFlag(Constant.CLASSIFY_APPLICATION);
			// adapter.setItemsData(homeUtil.getAppList(i,Constant.CLASSIFY_APPLICATION,
			// allAppInfos), i);
			gv_app.setScrollBarFadeDuration(Integer.MAX_VALUE);
			adapter.setItemsData(allAppInfos, i);
			gv_app.setAdapter(adapter);
			//adapter.notifyDataSetChanged();
			gv_app.setOnScrollListener(appScrollListener);
			gv_app.setOnFocusChangeListener(focusChangedListener);
			gv_app.setOnItemSelectedListener(appSelectedListener);
			gv_app.setOnItemClickListener(appClickedListener);
			gv_app.setNextFocusDownId(R.id.home_backup_bt_application);
			gv_app.setNextFocusRightId(0);
			views.add(mViewGroup);
		}
		// if(pageNum >1){

		// }
		// showArrow(Constant.CLASSIFY_APPLICATION);
		vp_app_adapter = new MyViewPagerAdapter();
		vp_app_adapter.setViews(views);
		vp_app.setAdapter(vp_app_adapter);
		// vp_app.setCurrentItem(0);
		appScroll.createNode(getNodeNum(Constant.CLASSIFY_APPLICATION,
				allAppInfos));
		//vp_app.setOnPageChangeListener(pageChanageListener);
	}

	public void createGamePager(List<AppInfo> allAppInfos) {
		List<View> views = new ArrayList<View>();
		int pageNum = getPageNumber(Constant.CLASSIFY_GAME, allAppInfos);
		gameCurrentPageNumber = pageNum;
		game_item_index = 0;
		gameList = allAppInfos;
		// for (int i = 0; i < pageNum; i++) {
		for (int i = 0; i < 1; i++) {
			mViewGroup = (ViewGroup) inflater.inflate(
					R.layout.home_view_paper_item, null);
			gv_game = (GridView) mViewGroup.findViewById(R.id.home_app_list);
			gv_game.setOnKeyListener(keListener);
			AppQueryAdapter adapter = new AppQueryAdapter(activity);
			adapter.setStyleFlag(Constant.CLASSIFY_GAME);
			gv_game.setScrollBarFadeDuration(Integer.MAX_VALUE);
			// adapter.setItemsData(homeUtil.getAppList(i,
			// Constant.CLASSIFY_GAME, allAppInfos),i);
			adapter.setItemsData(allAppInfos, i);
			gv_game.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			gv_game.setOnScrollListener(gameScrollListener);
			gv_game.setOnFocusChangeListener(focusChangedListener);
			gv_game.setOnItemSelectedListener(appSelectedListener);
			gv_game.setOnItemClickListener(appClickedListener);
			gv_game.setNextFocusDownId(R.id.home_backup_bt_game);
			gv_game.setNextFocusRightId(0);
			views.add(mViewGroup);
		}
		// if(pageNum >1){
		// gameScroll.createNode(getNodeNum(Constant.CLASSIFY_GAME,
		// allAppInfos));

		// }
		// showArrow(Constant.CLASSIFY_GAME);
		vp_game_adapter = new MyViewPagerAdapter();
		vp_game_adapter.setViews(views);
		vp_game.setAdapter(vp_game_adapter);
		// vp_game.setCurrentItem(0);
		gameScroll.createNode(getNodeNum(Constant.CLASSIFY_GAME, allAppInfos));
		//vp_game.setOnPageChangeListener(pageChanageListener);
	}

	/*
	 * public void createScanPager(List<AppInfo> allAppInfos) { List<View> views
	 * = new ArrayList<View>(); int pageNum =
	 * getPageNumber(Constant.CLASSIFY_SCAN, allAppInfos); scanCurrentPageNumber
	 * = pageNum; scanList=allAppInfos; for (int i = 0; i < pageNum; i++) {
	 * mViewGroup = (ViewGroup) inflater.inflate( R.layout.home_view_paper_item,
	 * null); appGrid = (GridView) mViewGroup.findViewById(R.id.home_app_list);
	 * appGrid.setOnKeyListener(keListener); AppQueryAdapter adapter = new
	 * AppQueryAdapter(activity); adapter.setItemsData( homeUtil.getAppList(i,
	 * Constant.CLASSIFY_SCAN, allAppInfos), i); appGrid.setAdapter(adapter);
	 * adapter.notifyDataSetChanged();
	 * appGrid.setOnFocusChangeListener(focusChangedListener);
	 * appGrid.setOnItemSelectedListener(appSelectedListener);
	 * appGrid.setOnItemClickListener(appClickedListener);
	 * appGrid.setNextFocusDownId(R.id.home_backup_bt_scan);
	 * views.add(mViewGroup); Logger.i("gww2", "------ViewPagerFactory---" +
	 * views.size()); } showArrow(Constant.CLASSIFY_SCAN); vp_scan_adapter = new
	 * MyViewPagerAdapter(); vp_scan_adapter.setViews(views);
	 * vp_scan.setAdapter(vp_scan_adapter); //vp_scan.setCurrentItem(0);
	 * vp_scan.setOnPageChangeListener(pageChanageListener); }
	 */

	public void createUserPager(List<AppInfo> allAppInfos) {
		List<View> views = new ArrayList<View>();
		if (isTest) {
			allAppInfos = LKHomeUtil.getInstallApp();
		}
		int pageNum = getPageNumber(Constant.CLASSIFY_USER, allAppInfos);
		userCurrentPageNumber = pageNum;
		user_item_index = 0;
		userList = allAppInfos;
		// for (int i = 0; i < pageNum; i++) {

		for (int i = 0; i < 1; i++) {
			mViewGroup = (ViewGroup) inflater.inflate(
					R.layout.home_view_paper_item, null);
			gv_user = (GridView) mViewGroup.findViewById(R.id.home_app_list);
			gv_user.setOnKeyListener(keListener);
			AppQueryAdapter adapter = new AppQueryAdapter(activity);
			adapter.setStyleFlag(Constant.CLASSIFY_USER);
			gv_user.setScrollBarFadeDuration(Integer.MAX_VALUE);
			// adapter.setItemsData(homeUtil.getAppList(i,
			// Constant.CLASSIFY_USER, allAppInfos),i);
			adapter.setItemsData(allAppInfos, i);

			//adapter.notifyDataSetChanged();
			gv_user.setAdapter(adapter);
			gv_user.setOnScrollListener(userScrollListener);
			gv_user.setOnFocusChangeListener(focusChangedListener);
			gv_user.setOnItemSelectedListener(appSelectedListener);
			gv_user.setOnItemClickListener(appClickedListener);
			gv_user.setOnLongClickListener(longClickListener);
			gv_user.setNextFocusDownId(R.id.home_backup_bt_live);
			gv_user.setNextFocusRightId(0);
			views.add(mViewGroup);
		}
		// if(pageNum>1){
		// userScroll.createNode(getNodeNum(Constant.CLASSIFY_USER,
		// allAppInfos));

		// }
		// showArrow(Constant.CLASSIFY_USER);
		vp_user_adapter = new MyViewPagerAdapter();
		vp_user_adapter.setViews(views);
		vp_user.setAdapter(vp_user_adapter);
		// vp_user.setCurrentItem(0);
		userScroll.createNode(getNodeNum(Constant.CLASSIFY_USER, allAppInfos));
		//vp_user.setOnPageChangeListener(pageChanageListener);
	}

	public void createSettingPager(List<AppInfo> allAppInfos) {
		List<View> views = new ArrayList<View>();
		int pageNum = getPageNumber(Constant.CLASSIFY_SETTING, allAppInfos);
		settingCurrentPageNumber = pageNum;
		settingList = allAppInfos;
		// for (int i = 0; i < pageNum; i++) {
		for (int i = 0; i < 1; i++) {
			mViewGroup = (ViewGroup) inflater.inflate(
					R.layout.home_view_paper_item, null);
			gv_setting = (GridView) mViewGroup.findViewById(R.id.home_app_list);
			gv_setting.setOnKeyListener(keListener);
			AppQueryAdapter adapter = new AppQueryAdapter(activity);
			gv_setting.setScrollBarFadeDuration(Integer.MAX_VALUE);
			adapter.setStyleFlag(Constant.CLASSIFY_SETTING);
			adapter.setItemsData(LKHomeApp.homeUtil.getAppList(i,
					Constant.CLASSIFY_SETTING, allAppInfos), i);
			gv_setting.setAdapter(adapter);
			//adapter.notifyDataSetChanged();
			gv_setting.setOnScrollListener(settingScrollListener);
			gv_setting.setOnFocusChangeListener(focusChangedListener);
			gv_setting.setOnItemSelectedListener(appSelectedListener);
			gv_setting.setOnItemClickListener(appClickedListener);
			gv_setting.setNextFocusDownId(R.id.home_backup_bt_setting);
			gv_setting.setNextFocusRightId(0);
			views.add(mViewGroup);
		}
		// if(pageNum >1){
		// settingScroll.createNode(getNodeNum(Constant.CLASSIFY_SETTING,
		// allAppInfos));

		// }
		// showArrow(Constant.CLASSIFY_SETTING);
		vp_setting_adapter = new MyViewPagerAdapter();
		vp_setting_adapter.setViews(views);
		vp_setting.setAdapter(vp_setting_adapter);
		// vp_setting.setCurrentItem(0);
		settingScroll.createNode(getNodeNum(Constant.CLASSIFY_SETTING, allAppInfos));
		//vp_setting.setOnPageChangeListener(pageChanageListener);
	}

	int tempPageNum = 0;
	int currentPage = 0;
	int moveFirstVisible = 0,appFirstVisible = 0,gameFirstVisible = 0,userFirstVisible = 0,settingFirstVisible = 0;
	int SCROLL_STATE;
	OnScrollListener appScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			// gww
			MainHomeActivity.mpf.disMiss();
			SCROLL_STATE = scrollState;
			view.setSelection(appFirstVisible);
			//Logger.e("kao", "$$$--------appScrollListener---onScrollStateChanged-");
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			appFirstVisible=firstVisibleItem;
			appScorll(Constant.CLASSIFY_APPLICATION, view, firstVisibleItem,
					visibleItemCount, SCROLL_STATE_IDLE);
			//Logger.e("kao", "$$$--------appScrollListener---onScroll-");
		}

	};

	private OnScrollListener moveScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			MainHomeActivity.mpf.disMiss();
			SCROLL_STATE = scrollState;
			view.setSelection(moveFirstVisible);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
		
			moveFirstVisible=firstVisibleItem;
			
			appScorll(Constant.CLASSIFY_MOVIE, view, firstVisibleItem,
					visibleItemCount, SCROLL_STATE_IDLE);
			//Logger.e("kao", "$$$--------moveScrollListener---onScroll-");
		}
	};
	private OnScrollListener gameScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			MainHomeActivity.mpf.disMiss();
			SCROLL_STATE = scrollState;
			view.setSelection(gameFirstVisible);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			gameFirstVisible=firstVisibleItem;
			appScorll(Constant.CLASSIFY_GAME, view, firstVisibleItem,
					visibleItemCount, SCROLL_STATE_IDLE);
			//Logger.e("kao", "$$$--------gameScrollListener---onScroll-");
		}
	};
	private OnScrollListener userScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			MainHomeActivity.mpf.disMiss();
			SCROLL_STATE = scrollState;
			view.setSelection(userFirstVisible);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			userFirstVisible=firstVisibleItem;
			appScorll(Constant.CLASSIFY_USER, view, firstVisibleItem,
					visibleItemCount, SCROLL_STATE_IDLE);
			//Logger.e("kao", "$$$--------userScrollListener---onScroll-");
		}
	};

	private OnScrollListener settingScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			MainHomeActivity.mpf.disMiss();
			SCROLL_STATE = scrollState;
			view.setSelection(settingFirstVisible);
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			settingFirstVisible=firstVisibleItem;
			appScorll(Constant.CLASSIFY_SETTING, view, firstVisibleItem,
					visibleItemCount, SCROLL_STATE_IDLE);
			//Logger.e("kao", "$$$--------settingScrollListener---onScroll-");
		}
	};

	private void appScorll(String style_flag, AbsListView view,
			int firstVisibleItem, int visibleItemCount, int SCROLL_STATE_IDLE) {
		MainHomeActivity.mpf.disMiss();
		int index = tempPageNum - firstVisibleItem;
		if (SCROLL_STATE == SCROLL_STATE_IDLE) {
			if (visibleItemCount > 10) {
				if (index <= 0) {
					view.setSelection(firstVisibleItem + 10);
				} else {
					view.setSelection(firstVisibleItem-5);
				}
			}
		}
		currentPage = (firstVisibleItem / 10);
		if (index < 0) {
			startScrollNode(style_flag, currentPage, 0);
			//Logger.e("kao", "$$$------topage()-----"+style_flag);
		}else if(index == 0){
			startScrollNode(style_flag, currentPage+1, 2);
		}else if(index >0) {
			startScrollNode(style_flag, currentPage + 1, 1);
			//Logger.e("kao", "$$$------backpage()-----"+style_flag);
		}
		tempPageNum = firstVisibleItem;
	}

	public void createViewPager(String style_flag, List<AppInfo> allAppInfos) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			createMoviePager(allAppInfos);
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			createAppPager(allAppInfos);
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			createGamePager(allAppInfos);
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			createUserPager(allAppInfos);
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			createSettingPager(allAppInfos);
		}
	}

	public ViewPager getCurrentViewPager(String style_flag) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			return vp_movie;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			return vp_app;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			return vp_game;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			return vp_user;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			return vp_setting;
		} else {
			return null;
		}
	}

	public void startScrollNode(String style_flag, int page, int orientation) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			moveScroll.startScoll(page, orientation);
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			appScroll.startScoll(page, orientation);
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			gameScroll.startScoll(page, orientation);
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			userScroll.startScoll(page, orientation);
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			settingScroll.startScoll(page, orientation);
		}
	}

	public void setCurrentIndex(String style_flag, int page) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			movie_item_index = page * 10;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			app_item_index = page * 10;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			game_item_index = page * 10;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			user_item_index = page * 10;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			setting_item_index = page * 10;
		}
	}

	public int getCurrentIndex(String style_flag) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			return movie_item_index;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			return app_item_index;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			return game_item_index;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			return user_item_index;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			return setting_item_index;
		} else {
			return 0;
		}
	}

	public MyViewPagerAdapter getCurrentAdapter(String style_flag) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			return vp_movie_adapter;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			return vp_app_adapter;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			return vp_game_adapter;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			return vp_user_adapter;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			return vp_setting_adapter;
		} else {
			return null;
		}
	}

	public int getCurrentPageNumber(String style_flag) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			return movieCurrentPageNumber;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			return appCurrentPageNumber;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			return gameCurrentPageNumber;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			return userCurrentPageNumber;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			return settingCurrentPageNumber;
		} else {
			return 0;
		}
	}

	public List<AppInfo> getAllInfos(String style_flag) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			return movieList;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			return appList;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			return gameList;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			return userList;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			return settingList;
		} else {
			return null;
		}
	}

	public void setAllInfos(String style_flag, List<AppInfo> allAppInfos) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			movieList = allAppInfos;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			appList = allAppInfos;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			gameList = allAppInfos;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			userList = allAppInfos;
		}
	}

	public boolean currentPageIsFistShow(String style_flag) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			return isMovieFirstShow;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			return isAppFirstShow;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			return isGameFirstShow;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			return isUserFirstShow;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			return isSettingFirstShow;
		} else {
			return true;
		}
	}

	public void setCurrentFistShow(String style_flag, boolean value) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			isMovieFirstShow = value;
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			isAppFirstShow = value;
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			isGameFirstShow = value;
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			isUserFirstShow = value;
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			isSettingFirstShow = value;
		}
	}

	public boolean isLastItem(String style_flag, View v) {
		boolean result = false;
		GridView gv = (GridView) v;
		int position = gv.getSelectedItemPosition()
				+ getCurrentIndex(style_flag);
		List<AppInfo> infos = getAllInfos(style_flag);
		if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			if (position == Constant.SETTING_ICONS.length - 1
					|| position % 5 == 4) {
				result = true;
			} else {
				result = false;
			}
		} else {
			if (position == infos.size() - 1 || position % 5 == 4) {
				result = true;
			} else {
				result = false;
			}
		}

		return result;
	}

	public void notifyDataChanged() {
		vp_movie_adapter.notifyDataSetChanged();
		vp_game_adapter.notifyDataSetChanged();
		vp_app_adapter.notifyDataSetChanged();
		vp_user_adapter.notifyDataSetChanged();
		vp_setting_adapter.notifyDataSetChanged();
	}

	/*public void showArrow(String style_flag) {
		int page = getCurrentIndex(style_flag) / 10;
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			if (movieCurrentPageNumber > 1) {
				if (page == 0) {
					movie_arrow_right.setVisibility(View.VISIBLE);
					movie_arrow_left.setVisibility(View.INVISIBLE);
				} else if (page == movieCurrentPageNumber - 1) {
					movie_arrow_right.setVisibility(View.INVISIBLE);
					movie_arrow_left.setVisibility(View.VISIBLE);
				} else {
					movie_arrow_right.setVisibility(View.VISIBLE);
					movie_arrow_left.setVisibility(View.VISIBLE);
				}
			} else {
				movie_arrow_right.setVisibility(View.INVISIBLE);
				movie_arrow_left.setVisibility(View.INVISIBLE);
			}
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			if (appCurrentPageNumber > 1) {
				if (page == 0) {
					app_arrow_right.setVisibility(View.VISIBLE);
					app_arrow_left.setVisibility(View.INVISIBLE);
				} else if (page == appCurrentPageNumber - 1) {
					app_arrow_right.setVisibility(View.INVISIBLE);
					app_arrow_left.setVisibility(View.VISIBLE);
				} else {
					app_arrow_right.setVisibility(View.VISIBLE);
					app_arrow_left.setVisibility(View.VISIBLE);
				}
			} else {
				app_arrow_right.setVisibility(View.INVISIBLE);
				app_arrow_left.setVisibility(View.INVISIBLE);
			}

		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			if (gameCurrentPageNumber > 1) {
				if (page == 0) {
					game_arrow_right.setVisibility(View.VISIBLE);
					game_arrow_left.setVisibility(View.INVISIBLE);
				} else if (page == gameCurrentPageNumber - 1) {
					game_arrow_right.setVisibility(View.INVISIBLE);
					game_arrow_left.setVisibility(View.VISIBLE);
				} else {
					game_arrow_right.setVisibility(View.VISIBLE);
					game_arrow_left.setVisibility(View.VISIBLE);
				}
			} else {
				game_arrow_right.setVisibility(View.INVISIBLE);
				game_arrow_left.setVisibility(View.INVISIBLE);
			}

		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			if (userCurrentPageNumber > 1) {
				if (page == 0) {
					user_arrow_right.setVisibility(View.VISIBLE);
					user_arrow_left.setVisibility(View.INVISIBLE);
				} else if (page == userCurrentPageNumber - 1) {
					user_arrow_right.setVisibility(View.INVISIBLE);
					user_arrow_left.setVisibility(View.VISIBLE);
				} else {
					user_arrow_right.setVisibility(View.VISIBLE);
					user_arrow_left.setVisibility(View.VISIBLE);
				}
			} else {
				user_arrow_right.setVisibility(View.INVISIBLE);
				user_arrow_left.setVisibility(View.INVISIBLE);
			}
		}
	}*/

	public void showAnimation(View v) {
		switch (v.getId()) {
		case R.id.home_backup_bt_movie:
			// ll_movie.setAnimation(showAnimation());
			ll_movie.setAnimation(AnimationFactory.showAppAnimation());
			break;
		case R.id.home_backup_bt_application:
			// ll_app.setAnimation(showAnimation());
			ll_app.setAnimation(AnimationFactory.showAppAnimation());
			break;
		case R.id.home_backup_bt_game:
			ll_game.setAnimation(AnimationFactory.showAppAnimation());
			break;
		case R.id.home_backup_bt_live:
			ll_user.setAnimation(AnimationFactory.showAppAnimation());
			break;
		case R.id.home_backup_bt_setting:
			ll_setting.setAnimation(AnimationFactory.showAppAnimation());
			break;

		default:
			break;
		}
	}

	public void hideAnimation(View v) {

		switch (v.getId()) {
		case R.id.home_backup_bt_movie:
			ll_movie.setAnimation(AnimationFactory.hideAppAnimation());
			break;
		case R.id.home_backup_bt_application:
			ll_app.setAnimation(AnimationFactory.hideAppAnimation());
			break;
		case R.id.home_backup_bt_game:
			ll_game.setAnimation(AnimationFactory.hideAppAnimation());
			break;
		case R.id.home_backup_bt_live:
			ll_user.setAnimation(AnimationFactory.hideAppAnimation());
			break;
		case R.id.home_backup_bt_setting:
			ll_setting.setAnimation(AnimationFactory.hideAppAnimation());
			break;

		default:
			break;
		}
	}

	public void showCurrentPager(String style_flag) {
		if (Constant.CLASSIFY_MOVIE.equals(style_flag)) {
			ll_movie.setVisibility(View.VISIBLE);
			// ll_movie.setAnimation(showAnimation());
			ll_app.setVisibility(View.GONE);
			// ll_app.setAnimation(hideAnimation());
			ll_game.setVisibility(View.GONE);
			// ll_game.setAnimation(hideAnimation());
			ll_user.setVisibility(View.GONE);
			// ll_user.setAnimation(hideAnimation());
			ll_setting.setVisibility(View.GONE);
			// ll_setting.setAnimation(hideAnimation());
			ll_recommend.setVisibility(View.GONE);
		} else if (Constant.CLASSIFY_APPLICATION.equals(style_flag)) {
			ll_movie.setVisibility(View.GONE);
			// ll_movie.setAnimation(hideAnimation());
			ll_app.setVisibility(View.VISIBLE);
			// ll_app.setAnimation(showAnimation());
			ll_game.setVisibility(View.GONE);
			// ll_game.setAnimation(hideAnimation());
			ll_user.setVisibility(View.GONE);
			// ll_user.setAnimation(hideAnimation());
			ll_setting.setVisibility(View.GONE);
			// ll_setting.setAnimation(hideAnimation());
			ll_recommend.setVisibility(View.GONE);
		} else if (Constant.CLASSIFY_GAME.equals(style_flag)) {
			ll_movie.setVisibility(View.GONE);
			// ll_movie.setAnimation(hideAnimation());
			ll_app.setVisibility(View.GONE);
			// ll_app.setAnimation(hideAnimation());
			ll_game.setVisibility(View.VISIBLE);
			// ll_game.setAnimation(showAnimation());
			ll_user.setVisibility(View.GONE);
			// ll_user.setAnimation(hideAnimation());
			ll_setting.setVisibility(View.GONE);
			// ll_setting.setAnimation(hideAnimation());
			ll_recommend.setVisibility(View.GONE);
		} else if (Constant.CLASSIFY_USER.equals(style_flag)) {
			ll_movie.setVisibility(View.GONE);
			// ll_movie.setAnimation(hideAnimation());
			ll_app.setVisibility(View.GONE);
			// ll_app.setAnimation(hideAnimation());
			ll_game.setVisibility(View.GONE);
			// ll_game.setAnimation(hideAnimation());
			ll_user.setVisibility(View.VISIBLE);
			// ll_user.setAnimation(showAnimation());
			ll_setting.setVisibility(View.GONE);
			// ll_setting.setAnimation(hideAnimation());
			ll_recommend.setVisibility(View.GONE);
		} else if (Constant.CLASSIFY_SETTING.equals(style_flag)) {
			ll_movie.setVisibility(View.GONE);
			// ll_movie.setAnimation(hideAnimation());
			ll_app.setVisibility(View.GONE);
			// ll_app.setAnimation(hideAnimation());
			ll_game.setVisibility(View.GONE);
			// ll_game.setAnimation(hideAnimation());
			ll_user.setVisibility(View.GONE);
			// ll_user.setAnimation(hideAnimation());
			ll_setting.setVisibility(View.VISIBLE);
			// ll_setting.setAnimation(showAnimation());
			ll_recommend.setVisibility(View.GONE);
		} else if (Constant.CLASSIFY_RECOMMEND.equals(style_flag)) {
			ll_movie.setVisibility(View.GONE);
			// ll_movie.setAnimation(hideAnimation());
			ll_app.setVisibility(View.GONE);
			// ll_app.setAnimation(hideAnimation());
			ll_game.setVisibility(View.GONE);
			// ll_game.setAnimation(hideAnimation());
			ll_user.setVisibility(View.GONE);
			// ll_user.setAnimation(hideAnimation());
			ll_setting.setVisibility(View.GONE);
			// ll_setting.setAnimation(showAnimation());
			ll_recommend.setVisibility(View.VISIBLE);
		}
		// showArrow(style_flag);
	}
}
