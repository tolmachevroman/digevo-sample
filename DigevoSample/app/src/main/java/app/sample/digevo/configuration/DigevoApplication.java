package app.sample.digevo.configuration;

import android.app.Application;

import app.sample.digevo.network.ClientApi;

/**
 * Created by romantolmachev on 6/12/14.
 */
public class DigevoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ClientApi.initialize();
    }

}
