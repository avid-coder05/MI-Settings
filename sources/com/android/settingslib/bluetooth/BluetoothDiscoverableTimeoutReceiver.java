package com.android.settingslib.bluetooth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes2.dex */
public class BluetoothDiscoverableTimeoutReceiver extends BroadcastReceiver {
    public static void cancelDiscoverableAlarm(Context context) {
        Log.d("BluetoothDiscoverableTimeoutReceiver", "cancelDiscoverableAlarm(): Enter");
        Intent intent = new Intent("android.bluetooth.intent.DISCOVERABLE_TIMEOUT");
        intent.setClass(context, BluetoothDiscoverableTimeoutReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, 603979776);
        if (broadcast != null) {
            ((AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM)).cancel(broadcast);
        }
    }

    public static void setDiscoverableAlarm(Context context, long j) {
        Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): alarmTime = " + j);
        Intent intent = new Intent("android.bluetooth.intent.DISCOVERABLE_TIMEOUT");
        intent.setClass(context, BluetoothDiscoverableTimeoutReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        if (broadcast != null) {
            alarmManager.cancel(broadcast);
            Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): cancel prev alarm");
        }
        alarmManager.set(0, j, PendingIntent.getBroadcast(context, 0, intent, MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE));
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !intent.getAction().equals("android.bluetooth.intent.DISCOVERABLE_TIMEOUT")) {
            return;
        }
        LocalBluetoothAdapter localBluetoothAdapter = LocalBluetoothAdapter.getInstance();
        if (localBluetoothAdapter == null || localBluetoothAdapter.getState() != 12) {
            Log.e("BluetoothDiscoverableTimeoutReceiver", "localBluetoothAdapter is NULL!!");
            return;
        }
        Log.d("BluetoothDiscoverableTimeoutReceiver", "Disable discoverable...");
        localBluetoothAdapter.setScanMode(21);
    }
}
