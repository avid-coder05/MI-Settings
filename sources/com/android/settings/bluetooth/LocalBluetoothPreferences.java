package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

/* loaded from: classes.dex */
final class LocalBluetoothPreferences {
    private LocalBluetoothPreferences() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getDiscoverableEndTimestamp(Context context) {
        return getSharedPreferences(context).getLong("discoverable_end_timestamp", 0L);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("bluetooth_settings", 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void persistDiscoverableEndTimestamp(Context context, long j) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        edit.putLong("discoverable_end_timestamp", j);
        edit.apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void persistSelectedDeviceInPicker(Context context, String str) {
        SharedPreferences.Editor edit = getSharedPreferences(context).edit();
        edit.putString("last_selected_device", str);
        edit.putLong("last_selected_device_time", System.currentTimeMillis());
        edit.apply();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean shouldShowDialogInForeground(Context context, String str, String str2) {
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
        if (localBtManager == null) {
            Log.v("LocalBluetoothPreferences", "manager == null - do not show dialog.");
            return false;
        } else if (localBtManager.isForegroundActivity()) {
            return true;
        } else {
            if ((context.getResources().getConfiguration().uiMode & 5) == 5) {
                Log.v("LocalBluetoothPreferences", "in appliance mode - do not show dialog.");
                return false;
            }
            long currentTimeMillis = System.currentTimeMillis();
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            if (sharedPreferences.getLong("discoverable_end_timestamp", 0L) + 60000 > currentTimeMillis) {
                return true;
            }
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter == null || (!defaultAdapter.isDiscovering() && defaultAdapter.getDiscoveryEndMillis() + 60000 <= currentTimeMillis)) {
                if (str == null || !str.equals(sharedPreferences.getString("last_selected_device", null)) || sharedPreferences.getLong("last_selected_device_time", 0L) + 60000 <= currentTimeMillis) {
                    if (TextUtils.isEmpty(str2) || !str2.equals(context.getString(17039995))) {
                        Log.v("LocalBluetoothPreferences", "Found no reason to show the dialog - do not show dialog.");
                        return false;
                    }
                    Log.v("LocalBluetoothPreferences", "showing dialog for packaged keyboard");
                    return true;
                }
                return true;
            }
            return true;
        }
    }
}
