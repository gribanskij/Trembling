package com.gribanskij.trembling.dialogs;

import android.support.v4.app.DialogFragment;

public interface NoticeDialogListener {
    void onDialogPositiveClick(DialogFragment dialog, int id, int radius, double lat, double lon);

    void onDialogNegativeClick(DialogFragment dialog, int id);
}
