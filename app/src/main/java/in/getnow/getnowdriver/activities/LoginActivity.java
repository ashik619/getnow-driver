package in.getnow.getnowdriver.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.getnow.getnowdriver.R;
import in.getnow.getnowdriver.networkhandlers.APIClient;
import in.getnow.getnowdriver.networkhandlers.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.passWord)
    EditText passWord;
    @BindView(R.id.okButton)
    Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginApiCall();
            }
        });
    }
    private void loginApiCall(){
        String _phone = phoneNumber.getText().toString().trim();
        String  _pass = passWord.getText().toString().trim();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        JsonObject object = new JsonObject();
        object.addProperty("phoneNumber",_phone);
        object.addProperty("password",_pass);
        Call<JsonObject> call = apiInterface.loginUser(object);
        //Log.e("URL",call.request().url().toString());
        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this,"","Logging In",true);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
               // Log.e("RESP",response.code()+"");
                if(response.isSuccessful()){
                    if(response.body().get("success").getAsBoolean()){
                        JsonObject data = response.body().getAsJsonObject("driver");
                        //Log.e("RESP",data.toString());
                        SharedPreferences.Editor editor = getSharedPreferences("DATA",MODE_PRIVATE).edit();
                        editor.putInt("id",data.get("userId").getAsInt()).apply();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this,SplashActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },500);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                //Log.e("RESP","fail"+t.getMessage());

            }
        });

    }
}
