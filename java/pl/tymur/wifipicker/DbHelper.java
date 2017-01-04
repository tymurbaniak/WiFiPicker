package pl.tymur.wifipicker;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by user on 12.11.2016.
 */

public class DbHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String DB_PATH;
    private SQLiteDatabase mDB;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "wifipicker.db";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String NUMERIC_TYPE = " NUMERIC";
    public static final String REAL_TYPE = " REAL";
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    /**
     * WIFINETWORKS - TABLE WITH NOT TRIANGULATED POSITION OF WIFI NETWORKS - 1ST TABLE
     */
    public static final String WIFINETWORKS = "WIFINETWORKS";
    public static final String WIFINETWORKS_ID = "ID";
    public static final String WIFINETWORKS_BSSID = "BSSID";
    public static final String WIFINETWORKS_SSID = "SSID";
    public static final String WIFINETWORKS_CAPABILITIES = "CAPABILITIES";
    public static final String WIFINETWORKS_FREQUENCY = "FREQUENCY";
    public static final String WIFINETWORKS_LEVEL = "LEVEL";
    public static final String WIFINETWORKS_TIMESTAMP = "TIMESTAMP";
    public static final String WIFINETWORKS_LATITUDE = "LATITUDE";
    public static final String WIFINETWORKS_LONGITUDE = "LONGITUTDE";
    public static final String WIFINETWORKS_TRIANGULATED = "TRIANGULATED";
    /**
     * WNTRIANGULATED - TABLE WITH TRIANGULATED POSITION OF WIFI NETWORKS - 2ND TABLE
     */
    public static final String WNTRIANGULATED = "WNTRIANGULATED";
    public static final String WNTRIANGULATED_ID = "ID";
    public static final String WNTRIANGULATED_BSSID = "BSSID";
    public static final String WNTRIANGULATED_SSID = "SSID";
    public static final String WNTRIANGULATED_CAPABILITIES = "CAPABILITIES";
    public static final String WNTRIANGULATED_FREQUENCY = "FREQUENCY";
    public static final String WNTRIANGULATED_LEVEL = "LEVEL";
    public static final String WNTRIANGULATED_TIMESTAMP = "TIMESTAMP";
    public static final String WNTRIANGULATED_LATITUDE = "LATITUDE";
    public static final String WNTRIANGULATED_LONGITUDE = "LONGITUDE";
    /**
     * TABLES CREATION
     */
    public static final String SQL_CREATE_WIFINETWORKS =
            "CREATE TABLE " + WIFINETWORKS + " (" +
                    WIFINETWORKS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    WIFINETWORKS_BSSID + TEXT_TYPE + COMMA_SEP +
                    WIFINETWORKS_SSID + TEXT_TYPE + COMMA_SEP +
                    WIFINETWORKS_CAPABILITIES + TEXT_TYPE + COMMA_SEP +
                    WIFINETWORKS_FREQUENCY + INTEGER_TYPE + COMMA_SEP +
                    WIFINETWORKS_LEVEL + INTEGER_TYPE + COMMA_SEP +
                    WIFINETWORKS_TIMESTAMP + "DATETIME DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                    WIFINETWORKS_LATITUDE + REAL_TYPE + COMMA_SEP +
                    WIFINETWORKS_LONGITUDE + REAL_TYPE + COMMA_SEP +
                    WIFINETWORKS_TRIANGULATED + NUMERIC_TYPE +
                    ", FOREIGN KEY ("+WIFINETWORKS_TRIANGULATED+") REFERENCES "+WNTRIANGULATED+"("+WNTRIANGULATED_ID+"));";

    public static final String SQL_CREATE_WNTRIANGULATED =
            "CREATE TABLE " + DbHelper.WNTRIANGULATED + " (" +
                    WNTRIANGULATED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    WNTRIANGULATED_BSSID + TEXT_TYPE + COMMA_SEP +
                    WNTRIANGULATED_SSID + TEXT_TYPE + COMMA_SEP +
                    WNTRIANGULATED_CAPABILITIES + TEXT_TYPE + COMMA_SEP +
                    WNTRIANGULATED_FREQUENCY + INTEGER_TYPE + COMMA_SEP +
                    WNTRIANGULATED_LEVEL + INTEGER_TYPE + COMMA_SEP +
                    WNTRIANGULATED_TIMESTAMP + "DATETIME DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                    WNTRIANGULATED_LATITUDE + REAL_TYPE + COMMA_SEP +
                    WNTRIANGULATED_LONGITUDE + REAL_TYPE + ");";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void opendatabase() {
        //Open the database
        String mypath = DB_PATH + DATABASE_NAME;
        mDB = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WIFINETWORKS);
        db.execSQL(SQL_CREATE_WNTRIANGULATED);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, newVersion, oldVersion);
    }
    public boolean insertWTriangulated (String bssid, String ssid, String capabilities, int frequency, int level, double latitude, double longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WIFINETWORKS_BSSID, bssid);
        contentValues.put(WIFINETWORKS_SSID, ssid);
        contentValues.put(WIFINETWORKS_FREQUENCY, frequency);
        contentValues.put(WIFINETWORKS_CAPABILITIES, capabilities);
        contentValues.put(WIFINETWORKS_LEVEL, level);
        contentValues.put(WIFINETWORKS_LATITUDE, latitude);
        contentValues.put(WIFINETWORKS_LONGITUDE, longitude);
        db.insert(WIFINETWORKS, null, contentValues);
        return true;
    }
    public boolean insertWifiNetworks (String bssid, String ssid, String capabilities, int frequency, int level, double latitude, double longitude, boolean triangulated){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WIFINETWORKS_BSSID, bssid);
        contentValues.put(WIFINETWORKS_SSID, ssid);
        contentValues.put(WIFINETWORKS_FREQUENCY, frequency);
        contentValues.put(WIFINETWORKS_CAPABILITIES, capabilities);
        contentValues.put(WIFINETWORKS_LEVEL, level);
        contentValues.put(WIFINETWORKS_LATITUDE, latitude);
        contentValues.put(WIFINETWORKS_LONGITUDE, longitude);
        contentValues.put(WIFINETWORKS_TRIANGULATED, triangulated);
        db.insert(WIFINETWORKS, null, contentValues);
        return true;
    }
    public Cursor getData(String table, int id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+table+" WHERE ID="+id+"", null);
    }
    public int numberOfRows(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, table);
    }
    public boolean updateWifiNetworksData (int id, String bssid, String ssid, String capabilities, int frequency, int level, double latitude, double longitude, boolean triangulated){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WIFINETWORKS_BSSID, bssid);
        contentValues.put(WIFINETWORKS_SSID, ssid);
        contentValues.put(WIFINETWORKS_FREQUENCY, frequency);
        contentValues.put(WIFINETWORKS_CAPABILITIES, capabilities);
        contentValues.put(WIFINETWORKS_LEVEL, level);
        contentValues.put(WIFINETWORKS_LATITUDE, latitude);
        contentValues.put(WIFINETWORKS_LONGITUDE, longitude);
        contentValues.put(WIFINETWORKS_TRIANGULATED, triangulated);
        db.update(WIFINETWORKS, contentValues, "ID = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updateWTriangulated (int id, String bssid, String ssid, String capabilities, int frequency, int level, double latitude, double longitude){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WNTRIANGULATED_BSSID, bssid);
        contentValues.put(WNTRIANGULATED_SSID, ssid);
        contentValues.put(WNTRIANGULATED_FREQUENCY, frequency);
        contentValues.put(WNTRIANGULATED_CAPABILITIES, capabilities);
        contentValues.put(WNTRIANGULATED_LEVEL, level);
        contentValues.put(WNTRIANGULATED_LATITUDE, latitude);
        contentValues.put(WNTRIANGULATED_LONGITUDE, longitude);
        db.update(WNTRIANGULATED, contentValues, "ID = ? ", new String[]{Integer.toString(id)});
        return true;
    }
    public Integer DeleteNetwork (String table, Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table, "ID = ?", new String[]{Integer.toString(id)});
    }
    public ArrayList<String[]> getAllTriangulatedNetworks(){
        ArrayList<String[]> networklist = new ArrayList<String[]>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+WNTRIANGULATED+"", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            networklist.add(new String[]{res.getString(res.getColumnIndex(WNTRIANGULATED_ID)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_SSID)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_BSSID)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_CAPABILITIES)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_FREQUENCY)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_TIMESTAMP)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_LEVEL)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_LATITUDE)),
                    res.getString(res.getColumnIndex(WNTRIANGULATED_LONGITUDE))});
            res.moveToNext();
        }
        res.close();
        return networklist;
    }
    public ArrayList<WiFiNetwork> getAllNetworks(){
        ArrayList<WiFiNetwork> networklist = new ArrayList<WiFiNetwork>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+WIFINETWORKS+"", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            networklist.add(new WiFiNetwork(res.getString(res.getColumnIndex(WIFINETWORKS_BSSID)),
                    res.getString(res.getColumnIndex(WIFINETWORKS_CAPABILITIES)),
                    Integer.parseInt(res.getString(res.getColumnIndex(WIFINETWORKS_FREQUENCY))),
                    Long.parseLong(res.getString(res.getColumnIndex(WIFINETWORKS_ID))),
                    Double.parseDouble(res.getString(res.getColumnIndex(WIFINETWORKS_LATITUDE))),
                    Integer.parseInt(res.getString(res.getColumnIndex(WIFINETWORKS_LEVEL))),
                    Double.parseDouble(res.getString(res.getColumnIndex(WIFINETWORKS_LONGITUDE))),
                    res.getString(res.getColumnIndex(WIFINETWORKS_SSID)),
                    "Data",
                    Boolean.parseBoolean(res.getString(res.getColumnIndex(WIFINETWORKS_TRIANGULATED)))));
            res.moveToNext();
        }
        res.close();
        return networklist;
    }
}