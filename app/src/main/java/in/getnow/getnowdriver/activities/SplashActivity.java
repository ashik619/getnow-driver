package in.getnow.getnowdriver.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.getnow.getnowdriver.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences preferences = getSharedPreferences("DATA",MODE_PRIVATE);
        if(preferences.getInt("id",0)>0){
            if(!runtimePermissionsRequest()){
                goHome();
            }
        }else {
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
            finish();
        }

    }

    private boolean runtimePermissionsRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if((grantResults[0]==PackageManager.PERMISSION_GRANTED) && (grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                goHome();
            }else runtimePermissionsRequest();

        }
    }
    private void goHome(){
        startActivity(new Intent(SplashActivity.this,MainActivity.class));
        finish();
    }
}

