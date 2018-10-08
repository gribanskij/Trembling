package com.gribanskij.trembling.forecast.mvp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gribanskij.trembling.R;
import com.gribanskij.trembling.app.App;
import com.gribanskij.trembling.dialogs.WarningDialog;
import com.gribanskij.trembling.model.dto_hazard.Xmlresponse;
import com.gribanskij.trembling.utils.Utils;

import javax.inject.Inject;


public class ForecastActivity extends AppCompatActivity implements ForecastContract.View, OnMapReadyCallback {


    public static final String ACTIVITY_TAG = ForecastActivity.class.getSimpleName();


    public static final int MY_LOCATION_REQUEST_CODE = 1;
    private final int MONTH = 1;
    private final int YEAR = 2;
    private final int YEARS = 3;
    private final int ERROR = 4;
    private final int MONTH_ERROR = 5;
    private final int YEAR_ERROR = 6;
    private final int YEARS_ERROR = 7;
    @Inject
    public ForecastPresenter presenter;
    private GoogleMap mMap;

    private TextView probability_one_month;
    private TextView probability_one_year;
    private TextView probability_three_years;


    private ProgressBar progressBar;

    private ProgressBar one_month_progress;
    private ProgressBar one_year_progress;
    private ProgressBar three_year_progress;

    private TextView description;
    private TextView one_month_text;
    private TextView one_year_text;
    private TextView three_year_text;
    private TextView error_textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getApp(this).getHolder().getForecastComponent().inject(this);
        initView();
        initMap();
        presenter.attachView(this);

    }

    private void initView() {
        setContentView(R.layout.activity_forecast);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_forecast);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        probability_one_month = findViewById(R.id.value_one_month);
        probability_one_year = findViewById(R.id.value_one_year);
        probability_three_years = findViewById(R.id.value_three_years);

        one_year_progress = findViewById(R.id.progressbar_one_year);
        one_month_progress = findViewById(R.id.progressbar_one_month);
        three_year_progress = findViewById(R.id.progressbar_three_year);

        description = findViewById(R.id.description);

        one_month_text = findViewById(R.id.text_one_month);
        one_year_text = findViewById(R.id.text_one_year);
        three_year_text = findViewById(R.id.text_three_years);

        error_textView = findViewById(R.id.error_text);

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.queryLocation();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
        */

    }

    private void initMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map_forecast);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.hint_search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onQueryTextSubmit(searchView.getQuery().toString());
                searchView.clearFocus();

                three_year_text.setVisibility(View.VISIBLE);
                one_year_text.setVisibility(View.VISIBLE);
                one_month_text.setVisibility(View.VISIBLE);
                error_textView.setVisibility(View.INVISIBLE);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.warning) {
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = new WarningDialog();
            fragment.show(manager, ACTIVITY_TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress(int period) {

        switch (period) {

            case MONTH: {
                probability_one_month.setVisibility(View.INVISIBLE);
                one_month_progress.setVisibility(View.VISIBLE);
                break;
            }
            case YEAR: {
                probability_one_year.setVisibility(View.INVISIBLE);
                one_year_progress.setVisibility(View.VISIBLE);
                break;
            }
            case YEARS: {
                probability_three_years.setVisibility(View.INVISIBLE);
                three_year_progress.setVisibility(View.VISIBLE);
                break;
            }
            default:
        }
    }

    @Override
    public void hideProgress(int period) {
        switch (period) {

            case MONTH: {
                one_month_progress.setVisibility(View.INVISIBLE);
                probability_one_month.setVisibility(View.VISIBLE);
                break;
            }
            case YEAR: {
                one_year_progress.setVisibility(View.INVISIBLE);
                probability_one_year.setVisibility(View.VISIBLE);
                break;
            }
            case YEARS: {
                three_year_progress.setVisibility(View.INVISIBLE);
                probability_three_years.setVisibility(View.VISIBLE);
                break;
            }
            default:
        }
    }

    @Override
    public void showMessage(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProbability(Xmlresponse xmlresponse, int period) {

        switch (period) {

            case MONTH: {
                probability_one_month.setText(xmlresponse.getForecast().getProb());
                one_month_text.setVisibility(View.VISIBLE);
                break;
            }
            case YEAR: {
                probability_one_year.setText(xmlresponse.getForecast().getProb());
                one_year_text.setVisibility(View.VISIBLE);
                break;
            }
            case YEARS: {
                probability_three_years.setText(xmlresponse.getForecast().getProb());
                three_year_text.setVisibility(View.VISIBLE);
                break;
            }

            case MONTH_ERROR: {
                probability_one_month.setText(getResources().getString(R.string.no_data));
                one_month_text.setVisibility(View.VISIBLE);
                break;

            }
            case YEAR_ERROR: {
                probability_one_year.setText(getResources().getString(R.string.no_data));
                one_month_text.setVisibility(View.VISIBLE);
                break;

            }
            case YEARS_ERROR: {
                probability_three_years.setText(getResources().getString(R.string.no_data));
                one_month_text.setVisibility(View.VISIBLE);
                break;

            }
            default:
        }

    }

    @Override
    public void showPlaceOnMap(Xmlresponse xmlresponse) {
        mMap.clear();
        double lat = Double.parseDouble(xmlresponse.getLocation().getLat());
        double lon = Double.parseDouble(xmlresponse.getLocation().getLng());
        String place = xmlresponse.getLocation().getPlace();


        //Add marker
        LatLng event = new LatLng(lat, lon);
        Marker marker = mMap.addMarker(new MarkerOptions().position(event).title(place));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(event));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(5));
        marker.showInfoWindow();

        //Add circle
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lon))
                .radius(70000)
                .strokeColor(Color.RED));
    }

    @Override
    public boolean getNetworkAvailableAndConnected() {
        return Utils.isNetworkAvailableConnected(this);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();

        if (isFinishing()) {
            App.getApp(this).getHolder().clearForecastComponent();
            presenter.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        presenter.onActivityReady();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {


            }
            // Permission was denied. Display an error message.
        }

    }

    private void setMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }

            // Show rationale and request permission.
        }

    }
}
