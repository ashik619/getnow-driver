package in.getnow.getnowdriver.networkhandlers;

/*
 * Created by DilipAti on 23/11/16.
 */


import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    // For the Login
    @POST("/api/driver/driverLogin")
    Call<JsonObject> loginUser(@Body JsonObject request);

    // Getting customer detail from dashboard to make the call
    @GET("/api/driver/getDriverTripDetails")
    Call<JsonObject> getTripDetails(@Query("id") String id);

    @POST("/api/driver/startTrip")
    Call<JsonObject> startTrip(@Body JsonObject request);

    @POST("/api/bus/updateCurrentLoc")
    Call<JsonObject> postBusLocation(@Body JsonObject request);



}