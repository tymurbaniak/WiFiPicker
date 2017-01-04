package pl.tymur.wifipicker;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by user on 11.11.2016.
 */

public class WiFiScanService extends Service {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private WifiManager wifi;
    private List<ScanResult> mWifiList;
    private WifiReceiver mWifiReceiver;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private LatLng latLng;
    DbHelper mdb;
    int id = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("****Scan", "Rozpoczęto skanowanie");
        mNotifyManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(WiFiScanService.this);
        mBuilder.setContentTitle("WiFiPicker")
                .setContentText("Skanowanie sieci wifi")
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon);
        mdb = new DbHelper(WiFiScanService.this);
        wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        mWifiReceiver = new WifiReceiver();
        IntentFilter wifiAndLocation = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiAndLocation.addAction("Position");
        registerReceiver(mWifiReceiver, wifiAndLocation);
        wifi.startScan();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("WiFiScanService", "Service desroyed");
        unregisterReceiver(mWifiReceiver);
        super.onDestroy();
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mWifiList = wifi.getScanResults();
            Log.i("***WifiReceiver", "Zeskanowano sieć: " + mWifiList.get(0).SSID);
            Log.i("wifiscanlist", wifi.getScanResults().get(0).SSID);
            Log.i("WiFiScanReceiver", "Working");

            if(intent.hasExtra("Latitude") && intent.hasExtra("Longitude")){
                for(int i = 0; i < mWifiList.size(); i++){
                    ScanResult networkData = mWifiList.get(i);
                    mdb.insertWifiNetworks(networkData.BSSID,
                            networkData.SSID,
                            networkData.capabilities,
                            networkData.frequency,
                            networkData.level,
                            Double.parseDouble(intent.getExtras().get("Latitude").toString()),
                            Double.parseDouble(intent.getExtras().get("Longitude").toString()),
                            false);
                }
            }
        }
    }
}
