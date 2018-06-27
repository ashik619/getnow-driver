package in.getnow.getnowdriver.activities;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;

import in.getnow.getnowdriver.R;
import in.getnow.getnowdriver.networkhandlers.APIClient;
import in.getnow.getnowdriver.networkhandlers.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by dilip on 27/1/18.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private GoogleApiClient mGoogleApiClient;
    private NotificationManager notificationManager;
    private Call<JsonObject> call;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LOC","start");
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Location Service is Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(3195, notification);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        return START_STICKY;
    }
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("LOC","conn");
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        if(Build.VERSION.SDK_INT>=23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }else LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LOC",location.getLatitude()+"");
        if(call != null) {
            if (call.isExecuted()) {
                call.cancel();
            }
        }
        postBusLocation(location);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LOC","fail");

    }
    private void postBusLocation(Location location){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        JsonObject root = new JsonObject();
        root.addProperty("lat",location.getLatitude());
        root.addProperty("lng",location.getLongitude());
        root.addProperty("id",4);
        call = apiInterface.postBusLocation(root);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("RESP_LOC", response.code() + "");
                if(response.isSuccessful()){
                    Log.e("LOG", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }
}
