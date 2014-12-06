package app.sample.digevo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import app.sample.digevo.R;

/**
 * Created by romantolmachev on 6/12/14.
 */
public class CallInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mView;

    public CallInfoWindowAdapter(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        TextView title = (TextView) mView.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView contentAndTimestamp = (TextView) mView.findViewById(R.id.content_and_timestamp);
        contentAndTimestamp.setText(marker.getSnippet());

        return mView;
    }
}