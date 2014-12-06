package app.sample.digevo.network.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by romantolmachev on 6/12/14.
 */
public class Call implements Serializable{

    @SerializedName("id")
    long id;

    @SerializedName("call_title")
    String title;

    @SerializedName("call_content")
    String content;

    @SerializedName("call_machines")
    String machines;

    @SerializedName("call_lat")
    double latitude;

    @SerializedName("call_lon")
    double longitude;

    @SerializedName("call_hash")
    String hash;

    @SerializedName("call_timestamp")
    Date timestamp;

    @SerializedName("call_code")
    String code;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getMachines() {
        return machines;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getHash() {
        return hash;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }
}
