package app.sample.digevo.activities;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import app.sample.digevo.R;
import app.sample.digevo.network.ClientApi;
import app.sample.digevo.network.entities.Call;
import app.sample.digevo.network.responses.LastCallsResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlacesActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener {

    public static final int ZOOM = 12;
    public static final int BEARING = 90;
    public static final int TILT = 40;
    public static final int PADDING = 24; //padding from borders of the screen to fit all markers
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<Marker> mMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        setUpMapIfNeeded();
        getLastCalls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        //enable show my location
        mMap.setMyLocationEnabled(true);

        //respond on info window touch
        mMap.setOnInfoWindowClickListener(this);

        //zoom a bit to my position
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), ZOOM));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(ZOOM)                   // Sets the zoom
                    .bearing(BEARING)                // Sets the orientation of the camera to east
                    .tilt(TILT)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

    }

    private void getLastCalls() {

        ClientApi.getLastCalls(new Callback<LastCallsResponse>() {
            @Override
            public void success(LastCallsResponse lastCallsResponse, Response response) {
                if (lastCallsResponse != null) {

                    if (mMarkers == null) {
                        mMarkers = new ArrayList<Marker>();
                    } else {
                        mMarkers.clear();
                    }

                    //fit zoom to show all points on the map
                    for (Call call : lastCallsResponse.getData().getCalls()) {

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(call.getLatitude(), call.getLongitude()))
                                .title(call.getTitle())
                                .snippet(call.getContent() + " " + call.getTimestamp().toString()));

                        mMarkers.add(marker);
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : mMarkers) {
                        builder.include(marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);

                    mMap.animateCamera(cameraUpdate);

                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //center on the marker
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 16));
        return true;
    }
}
