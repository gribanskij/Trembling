package com.gribanskij.trembling.mapView.mvp;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gribanskij.trembling.R;
import com.gribanskij.trembling.app.App;
import com.gribanskij.trembling.dialogs.LocationDialog;
import com.gribanskij.trembling.dialogs.MagnitudeDialog;
import com.gribanskij.trembling.dialogs.NoticeDialogListener;
import com.gribanskij.trembling.dialogs.PlaceDialog;
import com.gribanskij.trembling.dialogs.TimeDialog;
import com.gribanskij.trembling.listView.mvp.MainActivity;
import com.gribanskij.trembling.model.ViewEarthquake;
import com.gribanskij.trembling.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, MapsContract.View, NoticeDialogListener, SharedPreferences.OnSharedPreferenceChangeListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int PERMISSION_REQUEST_LOCATION = 0;
    public static String LOG_TAG = MapsActivity.class.getSimpleName();
    @Inject
    public MapsPresenter presenter;
    @Inject
    public FusedLocationProviderClient mFusedLocationClient;
    @Inject
    public SharedPreferences sharedPreferences;


    private GoogleMap mMap;
    private MapView mapView;


    private TextView empty;

    private int index;

    private List<ViewEarthquake> list;
    private List<Marker> markers;
    private ProgressBar progressBar;
    private Button magnitude_button;
    private Button time_button;
    private ImageButton place_image_button;
    private double my_device_lon;
    private double my_device_lat;


    /**
     * Assigns a color based on the given magnitude
     */
    private static float magnitudeToColor(double magnitude) {
        if (magnitude < 1.0) {
            return BitmapDescriptorFactory.HUE_CYAN;
        } else if (magnitude < 2.0) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (magnitude < 3.0) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else if (magnitude < 4.0) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else if (magnitude < 5.0) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        }
        return BitmapDescriptorFactory.HUE_RED;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApp(this).getHolder().getMapsComponent().inject(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        index = getIntent().getIntExtra(MainActivity.POSITION_IN_LIST, 0);
        initView(savedInstanceState);
        getLocation();
        presenter.attachView(this);
    }

    private void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        empty = findViewById(R.id.empty_text);
        progressBar = findViewById(R.id.map_progressbar);

        magnitude_button = findViewById(R.id.magnitude_button);
        time_button = findViewById(R.id.time_button);
        place_image_button = findViewById(R.id.place_button);
        ImageButton location = findViewById(R.id.location_button);


        ImageButton forward = findViewById(R.id.forward_button);
        ImageButton back = findViewById(R.id.back_button);
        ImageButton refresh = findViewById(R.id.refresh_button);


        refresh.setOnClickListener(view -> presenter.refresh());
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewForward();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewBack();
            }
        });


        magnitude_button.setOnClickListener(view -> {
            int mag_value_id = sharedPreferences.getInt(getResources().getString(R.string.magnitude_value), 2);
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = MagnitudeDialog.newInstance(mag_value_id);
            fragment.show(manager, LOG_TAG);
        });

        time_button.setOnClickListener(view -> {
            int time_value_id = sharedPreferences.getInt(getResources().getString(R.string.time_interval_value), 1);
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = TimeDialog.newInstance(time_value_id);
            fragment.show(manager, LOG_TAG);

        });

        place_image_button.setOnClickListener(view -> {
            int place_id = sharedPreferences.getInt(getResources().getString(R.string.local_place), 0);
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = PlaceDialog.newInstance(place_id);
            fragment.show(manager, LOG_TAG);

        });

        location.setOnClickListener(view -> {
            //Default lat and lon is USA California coordinates
            long lat = sharedPreferences.getLong(getResources().getString(R.string.place_lat_value)
                    , Double.doubleToLongBits(36.778259));
            long lon = sharedPreferences.getLong(getResources().getString(R.string.place_lon_value)
                    , Double.doubleToLongBits(-119.417931));
            int r = sharedPreferences.getInt(getResources().getString(R.string.place_radius_value)
                    , 500);


            double lat_double = Double.longBitsToDouble(lat);
            double lon_double = Double.longBitsToDouble(lon);

            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = LocationDialog.newInstance(lat_double, lon_double, r, my_device_lat, my_device_lon);
            fragment.show(manager, LOG_TAG);

        });

    }

    private void tuneToolbar() {

        int mag_value = sharedPreferences.getInt(getResources().getString(R.string.magnitude_value), 2);
        String mag_label = getResources().getStringArray(R.array.min_magnitude_labels)[mag_value];
        magnitude_button.setText(mag_label);

        int time_value = sharedPreferences.getInt(getResources().getString(R.string.time_interval_value), 1);
        String time_label = getResources().getStringArray(R.array.time_interval_labels)[time_value];
        time_button.setText(time_label);

        int place_current = sharedPreferences.getInt(getResources().getString(R.string.local_place), 0);
        if (place_current == 0) {
            place_image_button.setImageResource(R.drawable.ic_language_black_24dp);
        } else {
            place_image_button.setImageResource(R.drawable.ic_place_black_24dp);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) mMap = googleMap;
        presenter.mapReady();
    }


    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        presenter.detachView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        if (isFinishing()) {
            App.getApp(this).getHolder().clearMapsComponent();
            presenter.destroy();
        }
        presenter = null;
        sharedPreferences = null;
        mFusedLocationClient = null;
        super.onDestroy();
    }


    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showEmpty() {
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        empty.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showMessage(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateMap(List<ViewEarthquake> earthquakes) {

        list = earthquakes;
        markers = new ArrayList<>();
        mMap.clear();

        //init index of event that we must show to default.
        if (index > list.size()) index = 0;

        if (getSupportActionBar() != null) {
            int number = earthquakes.size();
            String numberOfEarthquakes = getResources().getQuantityString(R.plurals.numberOfEarthquakes, number, number);
            getSupportActionBar().setSubtitle(numberOfEarthquakes);
        }

        StringBuilder builder = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();

        for (ViewEarthquake e : list) {
            double lat = Double.parseDouble(e.getLat());
            double lon = Double.parseDouble(e.getLon());

            builder.append("M");
            builder.append(e.getMag());
            builder.append(":  ");
            builder.append(e.getPlace());

            stringBuilder.append(e.getDate());
            stringBuilder.append("  ");
            stringBuilder.append(e.getTime());

            LatLng event = new LatLng(lat, lon);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(event).title(builder.toString())
                    .snippet(stringBuilder.toString())
                    .icon(BitmapDescriptorFactory.defaultMarker(magnitudeToColor(Double.valueOf(e.getMag())))));
            builder.setLength(0);
            stringBuilder.setLength(0);
            markers.add(marker);
        }

        int type_query = sharedPreferences.getInt(getString(R.string.local_place), 0);
        if (type_query == 0) {

            mMap.moveCamera(CameraUpdateFactory.zoomTo(1));
            if (list != null && list.size() != 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(getEvent(list.get(index))));
                Marker marker = markers.get(index);
                marker.showInfoWindow();
            }
        } else {

            mMap.moveCamera(CameraUpdateFactory.zoomTo(1));
            int r = sharedPreferences.getInt(getString(R.string.place_radius_value), 500);
            long temp = sharedPreferences.getLong(getString(R.string.place_lon_value), Double.doubleToLongBits(-119.417931));
            double longitude = Double.longBitsToDouble(temp);
            temp = sharedPreferences.getLong(getString(R.string.place_lat_value), Double.doubleToLongBits(36.778259));
            double latitude = Double.longBitsToDouble(temp);

            //Add circle
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(r * 1000)
                    .strokeColor(Color.RED));

            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        index = 0;
    }

    @Override
    public boolean getNetworkAvailableAndConnected() {
        return Utils.isNetworkAvailableConnected(this);
    }

    private void viewForward() {
        if (list == null || list.size() == 0) return;

        if (index-- <= 0) {
            index = list.size() - 1;
        }
        viewPoint(index);
    }

    private void viewBack() {
        if (list == null || list.size() == 0) return;

        if (index++ >= list.size() - 1) {
            index = 0;
        }
        viewPoint(index);
    }

    private void viewPoint(int i) {
        mMap.moveCamera(CameraUpdateFactory.zoomTo(5));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(getEvent(list.get(i))));
        Marker marker = markers.get(i);
        marker.showInfoWindow();
    }


    private LatLng getEvent(ViewEarthquake e) {
        double lat = Double.parseDouble(e.getLat());
        double lon = Double.parseDouble(e.getLon());
        return new LatLng(lat, lon);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tuneToolbar();
        mapView.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        presenter.refresh();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int id, int r, double lat, double lon) {
        if (dialog.getClass() == MagnitudeDialog.class) {
            presenter.onChangeMag(id);
            String mag_label = getResources().getStringArray(R.array.min_magnitude_labels)[id];
            magnitude_button.setText(mag_label);

        } else {
            if (dialog.getClass() == TimeDialog.class) {
                presenter.onChangeTimeInterval(id);
                String time_label = getResources().getStringArray(R.array.time_interval_labels)[id];
                time_button.setText(time_label);
            } else {
                if (dialog.getClass() == PlaceDialog.class) {
                    presenter.onChangePlace(id);
                    if (id == 0) {
                        place_image_button.setImageResource(R.drawable.ic_language_black_24dp);
                    } else {
                        place_image_button.setImageResource(R.drawable.ic_place_black_24dp);
                    }
                } else {
                    if (dialog.getClass() == LocationDialog.class) {
                        presenter.onChangeLocation(r, lat, lon);
                    }
                }
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int id) {

    }

    private void getLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            my_device_lat = location.getLatitude();
                            my_device_lon = location.getLongitude();
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }
}
