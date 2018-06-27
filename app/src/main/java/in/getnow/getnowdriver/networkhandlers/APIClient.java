package in.getnow.getnowdriver.networkhandlers;

/*
 * Created by ashik619 on 23/11/16.
 */

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    //production url
    private static final String BASE_URL = "http://34.208.166.249:80/";


    //test url
    //private static final String BASE_URL = "http://lssaascrmdev.us-west-2.elasticbeanstalk.com/";

    private static Retrofit retrofit = null;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL.substring(0,BASE_URL.length()-1))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}