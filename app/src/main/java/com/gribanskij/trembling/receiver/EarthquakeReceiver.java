package com.gribanskij.trembling.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gribanskij.trembling.R;
import com.gribanskij.trembling.services.EarthquakeService;
import com.gribanskij.trembling.utils.Utils;

public class EarthquakeReceiver extends BroadcastReceiver {

    public static final String ACTION_SHOW_NOTIFICATION = "com.gribanskij.trembling.show_notification";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Utils.setServiceAlarm(context, true);
            return;
        }

        if (intent.getAction().equals(Utils.ACTION_START_SERVICE)) {
            if (!Utils.isNetworkAvailableConnected(context)) {
                Utils.sendResult(context, context.getResources().getString(R.string.information_not_available));
                return;
            }
            //if internet is available - start background service.
            EarthquakeService.startActionFetch(context, "reserve1", "reserve2");
            return;
        }

        if (intent.getAction().equals(ACTION_SHOW_NOTIFICATION)) {
            String massage = intent.getStringExtra(Utils.FETCH_RESULT);
            Utils.createNotificationChannel(context);
            Utils.createNotification(context, massage);
        }
    }

}
