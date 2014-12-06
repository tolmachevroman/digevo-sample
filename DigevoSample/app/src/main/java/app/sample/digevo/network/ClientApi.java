package app.sample.digevo.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.sample.digevo.network.responses.LastCallsResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by romantolmachev on 6/12/14.
 */
public class ClientApi {

    private static ClientApiDefinitions service;
    static RestAdapter restAdapter;
    static RestAdapter.Log restLogs;

    public static void  initialize() {

        restLogs = new RestAdapter.Log() {
            @Override
            public void log(String s) {
                Log.d("CLIENT API", s);
            }
        };

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(Urls.ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .setLog(restLogs)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        service = restAdapter.create(ClientApiDefinitions.class);
    }

    public static void getLastCalls(final Callback<LastCallsResponse> callback) {
        service.getLastCalls(new Callback<LastCallsResponse>() {
            @Override
            public void success(LastCallsResponse lastCallsResponse, Response response) {
                callback.success(lastCallsResponse, response);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("failure, error: " + error);
                callback.failure(error);
            }
        });
    }
}
