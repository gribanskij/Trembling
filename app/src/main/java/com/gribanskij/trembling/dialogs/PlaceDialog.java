package com.gribanskij.trembling.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.gribanskij.trembling.R;

public class PlaceDialog extends DialogFragment {

    private static String PLACE = "place";
    private NoticeDialogListener mListener;
    private int placeId;


    public static DialogFragment newInstance(int placeId) {
        DialogFragment fragment = new PlaceDialog();
        Bundle args = new Bundle();
        args.putInt(PLACE, placeId);
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


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            placeId = savedInstanceState.getInt(PLACE);
        } else {
            placeId = getArguments().getInt(PLACE);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.location)

                .setSingleChoiceItems(R.array.place_labels, placeId, (dialogInterface, i) -> {

                    placeId = i;

                })

                .setPositiveButton(R.string.ok, (dialog, id) -> {

                    mListener.onDialogPositiveClick(PlaceDialog.this, placeId, 0, 0, 0);

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {

                });

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PLACE, placeId);
    }
}
