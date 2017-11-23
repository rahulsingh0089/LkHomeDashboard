package lenkeng.com.welcome.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/*
 * $Id: WeatherDataDao.java 33 2014-01-09 07:06:54Z gww $
 */

public class WeatherDataDao {
    private static final String path="/sdcard/weather/chinacity.db";
    private Context context;
    public WeatherDataDao(Context context){
        this.context=context;
        try {
			getSqLiteDatabase().close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public SQLiteDatabase getSqLiteDatabase(){
    	
        SQLiteDatabase db=null;
        File f=new File(path);
        if(f.exists()){
            return SQLiteDatabase.openOrCreateDatabase(f, null);
        }else{
            new File("/sdcard/weather").mkdir();
            try {
                AssetManager manager=context.getAssets();
                InputStream is=manager.open("chinacity.db");
                FileOutputStream fos=new FileOutputStream(f);
                int len=0;
                byte[] buffer=new byte[1024];
                while((len=is.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
                db=getSqLiteDatabase();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return db;
    }
    
    
    
    public String[] getProvinceArray(String language){
        String[] strs=new String[]{};
        SQLiteDatabase db=getSqLiteDatabase();
        List<String> province=new ArrayList<String>();
        Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM province_table", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(cursor ==null){
			return null;
		}
        while(cursor.moveToNext()){
            
            if("EN".equals(language)){
                province.add(cursor.getString(1));
            }else{
                province.add(cursor.getString(2));
            }
        }
        if(db !=null && db.isOpen()){
        	 cursor.close();
             db.close();
        }
       
        return  province.toArray(strs);
    }
    
    public String[] getTownArray(String province,String language){
        SQLiteDatabase db=getSqLiteDatabase();
        String[] strs=new String[]{};
        Set<String > towns=new TreeSet<String>();
        Cursor cursor=db.rawQuery("SELECT town_id,town FROM city_table where province_id=? or province=?" , new String[]{province,province});
        while(cursor.moveToNext()){
            if("EN".equals(language)){
                towns.add(cursor.getString(0));
            }else{
                towns.add(cursor.getString(1));
            }
        }
        if(db !=null && db.isOpen()){
        	cursor.close();
            db.close();
        }
        return towns.toArray(strs);
    }
    
    public List<String> getCityArray(String province,String town,String language){
        SQLiteDatabase db=getSqLiteDatabase();
        List<String> cities=new ArrayList<String>();
        Cursor cursor=db.rawQuery("SELECT city_id,city FROM city_table where province_id=? or province=? and town_id=? or town=?", new String[]{province,province,town,town});
        while(cursor.moveToNext()){
            
            if("EN".equals(language)){
                cities.add(cursor.getString(0));
            }else{
                cities.add(cursor.getString(1));
            }
        }
        if(db !=null && db.isOpen()){
        	cursor.close();
            db.close();
        }
        return cities;
    }
    
    public String getWeatherID(String province,String town,String city){
        String result="";
        SQLiteDatabase db=getSqLiteDatabase();
        Cursor cursor=db.rawQuery("select WEATHER_ID from city_table where PROVINCE_ID=? or PROVINCE=?and TOWN_ID=? or TOWN=? and CITY_ID=? or CITY=? ", new String[]{province,province,town,town,city,city});
        if(cursor.moveToFirst()){
            result= cursor.getString(0);
        }
        if(db !=null && db.isOpen()){
        	cursor.close();
            db.close();
        }
        return result;
    }
}
