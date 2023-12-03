package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.SystemSettings$System;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import miui.bluetooth.ble.MiBleProfile;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public final class MiuiBTUtils {
    private static String TAG = "MiuiBTUtils";

    private MiuiBTUtils() {
    }

    public static String getBluetoothName() {
        String str = SystemProperties.get(SystemSettings$System.RO_MARKET_NAME, (String) null);
        if (TextUtils.isEmpty(str)) {
            str = Build.MODEL;
        }
        return SystemProperties.get("persist.sys.bt_local_name", str);
    }

    public static String getRegion() {
        try {
            String str = SystemProperties.get("ro.miui.region", "");
            return "".equals(str) ? SystemProperties.get("ro.product.locale.region", "") : str;
        } catch (Exception e) {
            Log.e(TAG, "getRegion Exception: ", e);
            return "";
        }
    }

    public static void gotoBleProfile(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(MiBleProfile.ACTION_SELECT_DEVICE);
        intent.putExtra("com.android.bluetooth.ble.DeviceProfileFragment", true);
        intent.putExtra("com.android.bluetooth.ble.device", bluetoothDevice);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "Error when goto BLE profile: " + e);
        }
    }

    public static boolean isCustomizedOperator() {
        String str = SystemProperties.get("ro.miui.customized.region");
        Log.d(TAG, "customized_region: " + str);
        return "fr_orange".equals(str);
    }

    public static boolean isNearByBluetoothDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        if (cachedBluetoothDevice == null) {
            return false;
        }
        short rssi = cachedBluetoothDevice.getRssi();
        return rssi > ("mediatek".equals(FeatureParser.getString("vendor")) ? (short) -65 : (short) -60) && rssi != 0;
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getApplicationContext().getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
    }

    public static boolean isRarelyUsedBluetoothDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        int majorDeviceClass;
        if (cachedBluetoothDevice == null) {
            return false;
        }
        BluetoothClass btClass = cachedBluetoothDevice.getBtClass();
        if (btClass == null || btClass.getMajorDeviceClass() == 512) {
            return true;
        }
        return (btClass.doesClassMatch(1) || btClass.doesClassMatch(0) || (majorDeviceClass = btClass.getMajorDeviceClass()) == 1280 || majorDeviceClass == 1536) ? false : true;
    }

    public static boolean isVisibleDevice(boolean z, CachedBluetoothDevice cachedBluetoothDevice) {
        return z || cachedBluetoothDevice.hasHumanReadableName();
    }

    public static void setBluetoothName(String str) {
        SystemProperties.set("persist.sys.bt_local_name", str);
    }
}
