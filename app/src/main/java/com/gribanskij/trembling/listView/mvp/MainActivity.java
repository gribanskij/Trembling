package com.gribanskij.trembling.listView.mvp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.gribanskij.trembling.R;
import com.gribanskij.trembling.app.App;
import com.gribanskij.trembling.dialogs.InfoDialog;
import com.gribanskij.trembling.dialogs.LocationDialog;
import com.gribanskij.trembling.dialogs.MagnitudeDialog;
import com.gribanskij.trembling.dialogs.NoticeDialogListener;
import com.gribanskij.trembling.dialogs.PlaceDialog;
import com.gribanskij.trembling.dialogs.TimeDialog;
import com.gribanskij.trembling.forecast.mvp.ForecastActivity;
import com.gribanskij.trembling.mapView.mvp.MapsActivity;
import com.gribanskij.trembling.model.ViewEarthquake;
import com.gribanskij.trembling.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity implements MainContract.View, NoticeDialogListener, SharedPreferences.OnSharedPreferenceChangeListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String POSITION_IN_LIST = "position_in_list";

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private static final String URL_APP = "https://play.google.com/store/apps/details?id=com.gribanskij.trembling";
    private static final String URI_APP = "market://details?id=com.gribanskij.trembling";
    public static String ACTIVITY_TAG = MainActivity.class.getSimpleName();
    @Inject
    public MainPresenter presenter;
    @Inject
    public FusedLocationProviderClient mFusedLocationClient;
    @Inject
    public SharedPreferences sharedPreferences;

    private TextView empty;
    private RecyclerView recyclerView;
    private List<ViewEarthquake> list;
    private SwipeRefreshLayout refreshLayout;
    private Button magnitude_button;
    private Button time_button;
    private ImageButton place_image_button;
    private double my_device_lon;
    private double my_device_lat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
        App.getApp(this).getHolder().getMainComponent().inject(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        initView();
        setAd(true);
        getLocation();
        presenter.attachView(this);
        presenter.onActivityReady();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_probability) {
            Intent intent = new Intent(getApplicationContext(), ForecastActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_map) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra(POSITION_IN_LIST, 0);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_notification) {
            boolean shouldStartAlarm = !Utils.isServiceAlarmOn(this);
            Utils.setServiceAlarm(this, shouldStartAlarm);
            if (shouldStartAlarm) {
                showMessage(R.string.notification_is_on);
            } else {
                showMessage(R.string.notification_is_off);
            }
            return true;
        }

        if (id == R.id.action_rate) {
            onClickRateThisApp();
            return true;
        }

        if (id == R.id.action_info) {
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = new InfoDialog();
            fragment.show(manager, ACTIVITY_TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_list);
        }

        empty = findViewById(R.id.empty_text);
        magnitude_button = findViewById(R.id.magnitude_button);
        time_button = findViewById(R.id.time_button);
        place_image_button = findViewById(R.id.place_button);
        ImageButton location = findViewById(R.id.location_button);


        magnitude_button.setOnClickListener(view -> {
            int mag_value_id = sharedPreferences.getInt(getResources().getString(R.string.magnitude_value), 2);
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = MagnitudeDialog.newInstance(mag_value_id);
            fragment.show(manager, ACTIVITY_TAG);
        });

        time_button.setOnClickListener(view -> {
            int time_value_id = sharedPreferences.getInt(getResources().getString(R.string.time_interval_value), 1);
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = TimeDialog.newInstance(time_value_id);
            fragment.show(manager, ACTIVITY_TAG);

        });

        place_image_button.setOnClickListener(view -> {
            int place_id = sharedPreferences.getInt(getResources().getString(R.string.local_place), 0);
            FragmentManager manager = getSupportFragmentManager();
            DialogFragment fragment = PlaceDialog.newInstance(place_id);
            fragment.show(manager, ACTIVITY_TAG);

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
            fragment.show(manager, ACTIVITY_TAG);

        });

        refreshLayout = findViewById(R.id.swiperefresh);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(() -> {
            presenter.refresh();
        });


        recyclerView = findViewById(R.id.earthquakeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        RecyclerView.Adapter<MainActivity.Holder> adapter = new MainActivity.EarthquakeAdaptor(list);
        recyclerView.setAdapter(adapter);

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
    public void showProgress() {
        refreshLayout.setRefreshing(true);

    }

    @Override
    public void hideProgress() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(int messageResId) {

        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();

    }

    @Override
    public void updateEathquakeList(List<ViewEarthquake> list) {

        this.list.clear();
        this.list.addAll(list);
        recyclerView.getAdapter().notifyDataSetChanged();

        if (getSupportActionBar() != null) {

            //StringBuilder builder = new StringBuilder();
            //builder.append(Integer.toString(list.size()));
            //builder.append(" earthquakes");
            int number = list.size();
            String numberOfEarthquakes = getResources().getQuantityString(R.plurals.numberOfEarthquakes, number, number);
            getSupportActionBar().setSubtitle(numberOfEarthquakes);
        }
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
    public boolean getNetworkAvailableAndConnected() {
        return Utils.isNetworkAvailableConnected(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        tuneToolbar();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        if (isFinishing()) {
            App.getApp(this).getHolder().clearMainComponent();
            presenter.destroy();
        }
        presenter = null;
        sharedPreferences = null;
        mFusedLocationClient = null;
        super.onDestroy();
    }


    private void onClickRateThisApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URI_APP));
        if (!isActivityStarted(intent)) {
            intent.setData(Uri.parse(URL_APP));
            if (!isActivityStarted(intent)) {
                Toast.makeText(this, R.string.market_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isActivityStarted(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        presenter.refresh();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(ACTIVITY_TAG, "PERMISSION_GRANTED");
                getLocation();
            } else {
                Toast.makeText(this, R.string.error_location_massage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setAd(boolean set) {

        if (set) {
            FrameLayout layout = findViewById(R.id.ad_container);
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId("ca-app-pub-2516270421259135/9041623299");
            layout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }


    private class EarthquakeAdaptor extends RecyclerView.Adapter<MainActivity.Holder> {

        private List<ViewEarthquake> list;
        private DecimalFormat decimalFormat;

        EarthquakeAdaptor(List<ViewEarthquake> list) {
            this.list = list;
            this.decimalFormat = new DecimalFormat("0.0");
        }

        @NonNull
        @Override
        public MainActivity.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earthquake_list_item, parent, false);
            view.setOnClickListener(view1 -> {
                Bundle extra = (Bundle) view1.getTag();
                int position_in_list = extra.getInt(POSITION_IN_LIST, 0);

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(POSITION_IN_LIST, position_in_list);
                startActivity(intent);
            });

            return new MainActivity.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MainActivity.Holder holder, int position) {

            ViewEarthquake viewEarthquake = list.get(position);
            holder.extra.putInt(POSITION_IN_LIST, position);

            String mag = viewEarthquake.getMag();

            holder.magnitude.setText(decimalFormat.format(Double.valueOf(mag)));
            holder.primary_location.setText(viewEarthquake.getCountry());
            holder.location_offset.setText(viewEarthquake.getPlace());
            holder.date.setText(viewEarthquake.getDate());
            holder.time.setText(viewEarthquake.getTime());


            GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitude.getBackground();
            int magnitudeColor = getMagnitudeColor(mag);
            magnitudeCircle.setColor(magnitudeColor);

        }

        @Override
        public int getItemCount() {
            if (list == null) return 0;
            return list.size();
        }

        public List<ViewEarthquake> getData() {
            return list;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        private int getMagnitudeColor(String mag) {

            double magnitude = Double.valueOf(mag);
            int magnitudeColorResourceId;
            int magnitudeFloor = (int) Math.floor(magnitude);
            switch (magnitudeFloor) {
                case 0:
                case 1:
                    magnitudeColorResourceId = R.color.magnitude1;
                    break;
                case 2:
                    magnitudeColorResourceId = R.color.magnitude2;
                    break;
                case 3:
                    magnitudeColorResourceId = R.color.magnitude3;
                    break;
                case 4:
                    magnitudeColorResourceId = R.color.magnitude4;
                    break;
                case 5:
                    magnitudeColorResourceId = R.color.magnitude5;
                    break;
                case 6:
                    magnitudeColorResourceId = R.color.magnitude6;
                    break;
                case 7:
                    magnitudeColorResourceId = R.color.magnitude7;
                    break;
                case 8:
                    magnitudeColorResourceId = R.color.magnitude8;
                    break;
                case 9:
                    magnitudeColorResourceId = R.color.magnitude9;
                    break;
                default:
                    magnitudeColorResourceId = R.color.magnitude10plus;
                    break;
            }

            return ContextCompat.getColor(getApplicationContext(), magnitudeColorResourceId);
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        TextView magnitude;
        TextView location_offset;
        TextView primary_location;
        TextView date;
        TextView time;

        Bundle extra = new Bundle();


        Holder(View itemView) {
            super(itemView);
            magnitude = itemView.findViewById(R.id.magnitude);
            location_offset = itemView.findViewById(R.id.location_offset);
            primary_location = itemView.findViewById(R.id.primary_location);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            itemView.setTag(extra);
        }
    }
}
