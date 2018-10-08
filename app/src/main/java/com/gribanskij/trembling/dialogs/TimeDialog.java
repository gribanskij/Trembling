package com.gribanskij.trembling.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.gribanskij.trembling.R;

public class TimeDialog extends DialogFragment {

    private static String CHECKED_ITEM = "checked";
    private NoticeDialogListener mListener;
    private int choice;

    public static DialogFragment newInstance(int checkedItem) {
        DialogFragment fragment = new TimeDialog();
        Bundle args = new Bundle();
        args.putInt(CHECKED_ITEM, checkedItem);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            choice = savedInstanceState.getInt(CHECKED_ITEM, 1);
        } else {
            Bundle arguments = getArguments();
            choice = arguments.getInt(CHECKED_ITEM, 1);
        }

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

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_titel_time)
                .setSingleChoiceItems(R.array.time_interval_labels, choice, (dialogInterface, i) -> {

                    choice = i;

                })
                .setPositiveButton(R.string.ok, (dialog, id) -> {

                    mListener.onDialogPositiveClick(TimeDialog.this, choice, 0, 0, 0);

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {

                });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHECKED_ITEM, choice);
    }
}
