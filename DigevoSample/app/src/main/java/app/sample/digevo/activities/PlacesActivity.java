package app.sample.digevo.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.sample.digevo.R;
import app.sample.digevo.adapters.CallInfoWindowAdapter;
import app.sample.digevo.network.ClientApi;
import app.sample.digevo.network.entities.Call;
import app.sample.digevo.network.responses.LastCallsResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlacesActivity extends ActionBarActivity implements GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener {

    public static final int ZOOM = 12;
    public static final int BEARING = 90;
    public static final int TILT = 40;
    public static final int PADDING = 24; //padding from borders of the screen to fit all markers
    public static final int PERIOD = 60;
    public static final int INITIAL_DELAY = 3; //wait a few seconds until map is loaded
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<Marker> mMarkers;
    private ArrayList<Call> mCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        setUpMapIfNeeded();

        //each minute get last calls
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        getLastCalls();
                    }
                }, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);

        Toast.makeText(this, getString(R.string.searching_places_please_wait), Toast.LENGTH_LONG).show();
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
        mMap.setInfoWindowAdapter(new CallInfoWindowAdapter(this));

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

        if(ClientApi.hasActiveInternetConnection(this)) {

            ClientApi.getLastCalls(new Callback<LastCallsResponse>() {
                @Override
                public void success(LastCallsResponse lastCallsResponse, Response response) {
                    if (lastCallsResponse != null) {

                        mMap.clear();

                        if (mMarkers == null) {
                            mMarkers = new ArrayList<Marker>();
                            mCalls = new ArrayList<Call>();
                        } else {
                            mMarkers.clear();
                            mCalls.clear();
                        }

                        mCalls.addAll(lastCallsResponse.getData().getCalls());

                        //show and fit all points on the map
                        for (Call call : lastCallsResponse.getData().getCalls()) {

                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(call.getLatitude(), call.getLongitude()))
                                    .title(call.getTitle())
                                    .snippet(call.getContent() + "\n" + call.getTimestamp().toString()));

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
                public void failure(final RetrofitError error) {
                    if(error.getMessage() != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlacesActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PlacesActivity.this, getString(R.string.something_went_wrong_searching_again), Toast.LENGTH_LONG).show();
                            }
                        });

                        //try searching again
                        getLastCalls();
                    }
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.please_check_your_internet_connection), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        Bundle extras = new Bundle();
        extras.putDouble("destination_latitude", marker.getPosition().latitude);
        extras.putDouble("destination_longitude", marker.getPosition().longitude);
        startActivity(new Intent(PlacesActivity.this, PlaceDestinationActivity.class)
                .putExtras(extras));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //center on the marker
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 16));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list:

                if(mCalls == null)
                    return true;

                if(mCalls.size() == 0)
                    return true;

                Intent intent = new Intent(PlacesActivity.this, CallsListActivity.class);
                intent.putExtra("calls", mCalls);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}