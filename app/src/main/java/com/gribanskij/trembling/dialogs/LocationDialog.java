package com.gribanskij.trembling.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.gribanskij.trembling.R;

public class LocationDialog extends DialogFragment {


    private static String RADIUS = "radius";
    private static String LAT = "latitude";
    private static String LON = "longitude";
    private static String LON_MY = "longitude_my";
    private static String LAT_MY = "latitude_my";

    private NoticeDialogListener mListener;

    private double lat_value;
    private double lon_value;
    private int radius_value;
    private double my_device_lat;
    private double my_device_lon;


    private EditText lat_edit;
    private EditText lon_edit;
    private EditText radius_edit;
    private CheckBox location_check;


    public static DialogFragment newInstance(double lat, double lon, int r, double lat_device, double lon_device) {
        DialogFragment fragment = new LocationDialog();
        Bundle args = new Bundle();
        args.putInt(RADIUS, r);
        args.putDouble(LAT, lat);
        args.putDouble(LON, lon);
        args.putDouble(LON_MY, lon_device);
        args.putDouble(LAT_MY, lat_device);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            radius_value = savedInstanceState.getInt(RADIUS);
            lat_value = savedInstanceState.getDouble(LAT);
            lon_value = savedInstanceState.getDouble(LON);
            my_device_lat = savedInstanceState.getDouble(LAT_MY);
            my_device_lon = savedInstanceState.getDouble(LON_MY);
        } else {
            radius_value = getArguments().getInt(RADIUS);
            lat_value = getArguments().getDouble(LAT);
            lon_value = getArguments().getDouble(LON);
            my_device_lon = getArguments().getDouble(LON_MY);
            my_device_lat = getArguments().getDouble(LAT_MY);
        }


        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.place_dialog, null);

        radius_edit = view.findViewById(R.id.radius);
        lat_edit = view.findViewById(R.id.lat);
        lon_edit = view.findViewById(R.id.lon);
        location_check = view.findViewById(R.id.my_location);

        radius_edit.setText(String.valueOf(radius_value));
        lat_edit.setText(String.valueOf(lat_value));
        lon_edit.setText(String.valueOf(lon_value));

        location_check.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                lat_edit.setText(String.valueOf(lat_value));
                lon_edit.setText(String.valueOf(lon_value));
            } else {
                lat_edit.setText(String.valueOf(my_device_lat));
                lon_edit.setText(String.valueOf(my_device_lon));
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.location)
                .setTitle(R.string.my_location)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialog, id) -> {

                    radius_value = Integer.valueOf(radius_edit.getText().toString());
                    lat_value = Double.valueOf(lat_edit.getText().toString());
                    lon_value = Double.valueOf(lon_edit.getText().toString());

                    mListener.onDialogPositiveClick(LocationDialog.this, 0, radius_value, lat_value, lon_value);

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {

                });


        return builder.create();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(LAT, lat_value);
        outState.putDouble(LON, lon_value);
        outState.putInt(RADIUS, radius_value);
        outState.putDouble(LAT_MY, my_device_lat);
        outState.putDouble(LON_MY, my_device_lon);
    }
}





