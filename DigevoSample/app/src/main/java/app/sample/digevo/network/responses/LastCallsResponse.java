package app.sample.digevo.network.responses;

import com.google.gson.annotations.SerializedName;

import app.sample.digevo.network.entities.Data;

/**
 * Created by romantolmachev on 6/12/14.
 */
public class LastCallsResponse {

    @SerializedName("data")
    Data data;

    public Data getData() {
        return data;
    }
}
