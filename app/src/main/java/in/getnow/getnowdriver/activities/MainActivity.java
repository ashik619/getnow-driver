package in.getnow.getnowdriver.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.getnow.getnowdriver.R;
import in.getnow.getnowdriver.networkhandlers.APIClient;
import in.getnow.getnowdriver.networkhandlers.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.busNameText)
    TextView busNameText;
    @BindView(R.id.routeText)
    TextView routeText;
    @BindView(R.id.ownerText)
    TextView ownerText;
    @BindView(R.id.tripButton1)
    Button tripButton1;
    @BindView(R.id.tripButton2)
    Button tripButton2;
    @BindView(R.id.tripButton3)
    Button tripButton3;
    private int driverId = 1;
    private int busId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("DATA",MODE_PRIVATE);
        driverId = preferences.getInt("id",0);
        getTripDetails();
        tripButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTripApiCall(1,busId);
            }
        });
        tripButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTripApiCall(2,busId);
            }
        });
        tripButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTripApiCall(0,busId);
            }
        });
    }

    private void getTripDetails() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Loading In", true);
        Call<JsonObject> call = apiInterface.getTripDetails(driverId + "");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                Log.e("RESP", response.code() + "");
                if (response.isSuccessful()) {
                    if (response.body().get("success").getAsBoolean()) {
                        Log.e("RESP", response.body().toString());
                        populateData(response.body());

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Log.e("RESP", "fail" + t.getMessage());
            }
        });

    }

    private void startTripApiCall(final int type, int busId) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", "Changing status", true);
        JsonObject root = new JsonObject();
        root.addProperty("status",type);
        root.addProperty("busId",busId);
        Call<JsonObject> call = apiInterface.startTrip(root);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                Log.e("RESP", response.code() + "");
                if(response.isSuccessful()){
                    Log.e("LOG", response.body().toString());
                    if(type>0) {
                        startService(new Intent(MainActivity.this, LocationService.class));
                    }else {
                        stopService(new Intent(MainActivity.this, LocationService.class));
                    }
                    getTripDetails();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }
    private Spanned getBold(String text){
        return Html.fromHtml("<b>"+text+"<b>");
    }
    private void populateData(JsonObject body){
        JsonObject data = body.getAsJsonObject("data");
        JsonObject bus = data.getAsJsonObject("bus");
        JsonObject route = data.getAsJsonObject("route");
        JsonObject owner = data.getAsJsonObject("owner");
        busId = bus.get("busId").getAsInt();
        busNameText.setText(getBold("Bus Name :")+bus.get("name").getAsString());
        String src = route.get("srcName").getAsString();
        String dst = route.get("dstName").getAsString();
        tripButton1.setText("Start trip\n" + src + " to " + dst);
        tripButton2.setText("Start trip\n" + dst + " to " + src);
        routeText.setText(src + "--> to -->" + dst);
        String ownerName = owner.get("name").getAsString();
        ownerText.setText(getBold("Owner : " )+ ownerName);
        switch (bus.get("status").getAsInt()){
            case 0:{
                tripButton3.setVisibility(View.GONE);
                tripButton2.setVisibility(View.VISIBLE);
                tripButton1.setVisibility(View.VISIBLE);
                routeText.setText("No trips started \n click button to start trip.");
                if(isMyServiceRunning(LocationService.class)){
                    stopService(new Intent(MainActivity.this, LocationService.class));
                }
                break;
            }
            case 1:{
                tripButton3.setVisibility(View.VISIBLE);
                tripButton2.setVisibility(View.GONE);
                tripButton1.setVisibility(View.GONE);
                routeText.setText(src+ " --> to --> "+dst);
                if(!isMyServiceRunning(LocationService.class)){
                    startService(new Intent(MainActivity.this,LocationService.class));
                }
                break;
            }
            case 2:{
                tripButton3.setVisibility(View.VISIBLE);
                tripButton2.setVisibility(View.GONE);
                tripButton1.setVisibility(View.GONE);
                routeText.setText(dst+ " --> to --> "+src);
                if(!isMyServiceRunning(LocationService.class)){
                    startService(new Intent(MainActivity.this,LocationService.class));
                }
                break;
            }
        }
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
