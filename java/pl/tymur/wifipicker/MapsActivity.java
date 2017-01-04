package pl.tymur.wifipicker;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker currLocationMarker;
    private BroadcastReceiver broadcastReceiver;
    public boolean permissions;
    DbHelper mDB;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDB = new DbHelper(MapsActivity.this);
        runtimePermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isMyServiceRunning(LocationService.class)){
            stopService(new Intent(this, LocationService.class));
        }
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    private boolean runtimePermissions() {
        if(Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }else{
            startService(new Intent(this, LocationService.class));
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            if(broadcastReceiver == null){
                Log.v("**********************","New receiver");
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i("Receiver", "Received data from service");
                        latLng = new LatLng(Double.parseDouble(intent.getExtras().get("Latitude").toString()), Double.parseDouble(intent.getExtras().get("Longitude").toString()));
                        currentLocationMarkerRefresh();
                    }
                };
            }
            IntentFilter filter = new IntentFilter("Position");
            registerReceiver(broadcastReceiver, filter);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            }else{
                runtimePermissions();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("debug", "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.new_map:
                return true;
            case R.id.start_scan:
                if(!isMyServiceRunning(WiFiScanService.class) && (item.getTitle().toString()!=getResources().getString(R.string.stop_scan))){
                    item.setTitle(R.string.stop_scan);
                    Log.i("debug", "start_scan");
                    startService(new Intent(this, WiFiScanService.class));
                }else{
                    item.setTitle(R.string.start_scan);
                    Log.i("debug", "stop_scan");
                    stopService(new Intent(this, WiFiScanService.class));
                }
                return true;
            case R.id.settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            //noinspection MissingPermission
            mMap.setMyLocationEnabled(true);
            buildGoogleApiClient();
            mGoogleApiClient.connect();
            loadNetworksMarkers();
    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    public void currentLocationMarkerRefresh(){
        Toast.makeText(this,"Refresh",Toast.LENGTH_LONG).show();
        Log.i("MarkerRefresh", "Refreshing");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }
    public void loadNetworksMarkers(){
        for(int i=0; i < mDB.getAllNetworks().size(); i++){
            WiFiNetwork currentNetwork = mDB.getAllNetworks().get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(currentNetwork.getLatitude(), currentNetwork.getLongitude()));
            markerOptions.title(currentNetwork.getSsid());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
