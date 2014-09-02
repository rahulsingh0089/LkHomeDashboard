
package lenkeng.com.welcome.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import lenkeng.com.welcome.R;
import lenkeng.com.welcome.util.LKHomeUtil;
import lenkeng.com.welcome.util.Logger;







/**
 * 自定义DigitalClock输出格式
 * 
 * @author SuenJer
 */
public class DigitalClock extends LinearLayout {
    private static final String TAG="DigitalClock";
    private TextView tv_time;
    private TextView tv_date;
    private LayoutInflater inflater;
    private LKHomeUtil lk;
    private Typeface tf;
    String day;
	SimpleDateFormat sdfd;
	String time;
	SimpleDateFormat sdft;
    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        
        lk= LKHomeUtil.getInstance(context.getApplicationContext());
        tf=Typeface.createFromAsset(context.getAssets(), "fzzy.ttf");
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.date_view, this);
        
        day = new String ("MM-dd-yyyy");
        sdfd=new SimpleDateFormat(day);
        time = new String (" HH:mm");
        sdft=new SimpleDateFormat(time);
        
        tv_time=(TextView) findViewById(R.id.Time);
        tv_time.setTypeface(tf);
        tv_time.setText(sdft.format(new Date()));
      //  tv_time.setText(LKHomeUtil.getCurrentHour()+" :"+LKHomeUtil.getCurrentMinute());
        tv_date=(TextView) findViewById(R.id.Date);
        tv_date.setTypeface(tf);
        tv_date.setText(sdfd.format(new Date()));
        
       // tv_date.setText(LKHomeUtil.getCurrentDate());
        //IntentFilter filter=new IntentFilter();
       // filter.addAction(Intent.ACTION_TIME_TICK);
       // filter.addAction(Intent.ACTION_DATE_CHANGED);
        //context.registerReceiver(timeChanaged, filter);
    }


   
    
  
   /* Calendar mCalendar;
    private final static String m12 = "h:mm aa"; // h:mm:ss aa
    private final static String m24 = "k:mm"; // k:mm:ss
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat;

    public DigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        // Resources r = context.getResources();

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);
        setFormat();
    }

    
    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        *//**
         * requests a tick on the next hard-second boundary
         *//*
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());

                // setText(new SimpleDateFormat("yyyy-MM-dd").format(new
                // Date())+"  "+ DateFormat.format(mFormat, mCalendar));
                setText(LKHomeUtil.getCurrentDate() );
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    *//**
     * Pulls 12/24 mode from system settings
     *//*
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }*/
    BroadcastReceiver timeChanaged=new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
        	
        	
        	
            //tv_time.setText(LKHomeUtil.getCurrentHour()+" :"+LKHomeUtil.getCurrentMinute());
        	
            String tempTime = sdfd.format(new Date());
            String temptime2 = sdft.format(new Date());
            tv_time.setText(temptime2);
			tv_date.setText(tempTime);
            
            
        }
    };
}
