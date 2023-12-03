package com.android.settings;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;

/* loaded from: classes.dex */
public class MiuiOptionUtils$Wireless {
    public static int touchAirplaneState(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        int i2 = Settings.Global.getInt(contentResolver, "airplane_mode_on", 0);
        if (i == -1 || i == i2) {
            return i2;
        }
        Settings.Global.putInt(contentResolver, "airplane_mode_on", i);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.addFlags(536870912);
        intent.putExtra("state", i != 0);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
        return i;
    }

    public static int touchBluetoothState(int i) {
        int i2;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        int state = defaultAdapter.getState();
        if (state == 10) {
            i2 = 0;
        } else if (state != 12) {
            return -1;
        } else {
            i2 = 1;
        }
        if (i != -1 && i != i2) {
            if (i != 0) {
                defaultAdapter.enable();
            } else {
                defaultAdapter.disable();
            }
            return i;
        } else if (state == 11) {
            return 1;
        } else {
            if (state == 13) {
                return 0;
            }
            return i2;
        }
    }

    public static int touchGPSState(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        boolean isLocationProviderEnabled = Settings.Secure.isLocationProviderEnabled(contentResolver, "gps");
        if (i == -1 || i == isLocationProviderEnabled) {
            return isLocationProviderEnabled ? 1 : 0;
        }
        Settings.Secure.setLocationProviderEnabled(contentResolver, "gps", i != 0);
        return i;
    }
}
