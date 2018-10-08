package com.gribanskij.trembling.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.gribanskij.trembling.R;

public class WarningDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.warning_layout, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_titel_warning)
                .setView(view)
                .setPositiveButton(R.string.ok, null);
        return builder.create();

    }
}
