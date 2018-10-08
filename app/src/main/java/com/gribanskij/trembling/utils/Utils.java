package com.gribanskij.trembling.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.gribanskij.trembling.R;
import com.gribanskij.trembling.listView.mvp.MainActivity;
import com.gribanskij.trembling.receiver.EarthquakeReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utils {

    public static final String ACTION_START_SERVICE = "com.gribanskij.trembling.start_service";
    public static final String CHANNEL_ID = "trembling_notification_channel";
    public static final int NOTIFICATION_ID = 1140;
    public static final String FETCH_RESULT = "fetch.result";
    private static final int REQUEST_CODE = 121;
    private static String TAG = Utils.class.getSimpleName();
    private static int TEST_INTERVAL = 1000 * 60;

    // set and cancel serviceAlarm
    public static void setServiceAlarm(Context context, boolean isOn) {

        Intent intent = new Intent(context, EarthquakeReceiver.class);
        intent.setAction(ACTION_START_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_DAY,
                    AlarmManager.INTERVAL_HALF_DAY, alarmIntent);
            enableReceiver(context);
            Log.i(TAG, "Alarm is on");
        } else {
            alarmMgr.cancel(alarmIntent);
            alarmIntent.cancel();
            disableReceiver(context);
            Log.i(TAG, "Alarm is cancel");
        }
    }


    private static void enableReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, EarthquakeReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private static void disableReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, EarthquakeReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    public static boolean isNetworkAvailableConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = manager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && manager.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = new Intent(context, EarthquakeReceiver.class);
        intent.setAction(ACTION_START_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE);
        return alarmIntent != null;
    }


    public static void createNotification(Context context, String message) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_trembling_round)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static Bitmap largeIcon(Context context) {
        Resources resource = context.getResources();
        return BitmapFactory.decodeResource(resource, R.mipmap.ic_launcher_trembling_round);
    }

    public static Notification createNotificationForBackground(Context context, String message) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_trembling_round)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        return mBuilder.build();
    }

    public static void sendResult(Context context, String result) {
        Intent i = new Intent(context, EarthquakeReceiver.class);
        i.setAction(EarthquakeReceiver.ACTION_SHOW_NOTIFICATION);
        i.putExtra(FETCH_RESULT, result);
        context.sendBroadcast(i);
    }


    public static int getMagnitudePref(SharedPreferences preferences, Resources resources) {
        int mag_value_id = preferences.getInt(resources.getString(R.string.magnitude_value), 2);
        int[] magnitude_value = resources.getIntArray(R.array.min_magnitude_value);
        return magnitude_value[mag_value_id];
    }

    public static int getRadiusPref(SharedPreferences preferences, Resources resources) {
        return preferences.getInt(resources.getString(R.string.place_radius_value), 500);
    }

    public static double getLonPref(SharedPreferences preferences, Resources resources) {
        long temp = preferences.getLong(resources.getString(R.string.place_lon_value), Double.doubleToLongBits(-119.417931));
        return Double.longBitsToDouble(temp);
    }

    public static double getLatPref(SharedPreferences preferences, Resources resources) {
        Long temp = preferences.getLong(resources.getString(R.string.place_lat_value), Double.doubleToLongBits(36.778259));
        return Double.longBitsToDouble(temp);
    }

    public static String getStartTimePref(SharedPreferences preferences, Resources resources) {

        long ms_per_hour = 60 * 60 * 1000;
        int interval_id = preferences.getInt(resources.getString(R.string.time_interval_value), 1);
        int[] time_interval_value = resources.getIntArray(R.array.time_interval_value);
        int time_interval = time_interval_value[interval_id];
        long start_time_ms = new Date().getTime() - time_interval * ms_per_hour;
        return getFormatter().format(new Date(start_time_ms));
    }

    public static String getEndTime() {
        return getFormatter().format(new Date());
    }

    public static int getTypeQuery(SharedPreferences preferences, Resources resources) {
        return preferences.getInt(resources.getString(R.string.local_place), 0);
    }

    private static SimpleDateFormat getFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
    }

    private static long getPresentDay() {
        return new Date().getTime();
    }

}
