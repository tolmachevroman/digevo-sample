package app.sample.digevo.network.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by romantolmachev on 6/12/14.
 */
public class Data {

    @SerializedName("calls")
    List<Call> calls;

    public List<Call> getCalls() { return calls; }
}
