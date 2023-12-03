package com.android.settings.cloudbackup;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class ConnectionCloudBackupHelper {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject != null && jSONObject.has("CKBluetooth")) {
            boolean optBoolean = jSONObject.optBoolean("CKBluetooth");
            if (optBoolean && !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                BluetoothAdapter.getDefaultAdapter().enable();
            } else if (optBoolean || !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            } else {
                BluetoothAdapter.getDefaultAdapter().disable();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("CKBluetooth", BluetoothAdapter.getDefaultAdapter().isEnabled());
        } catch (JSONException unused) {
            Log.e("ConnectionCloudBackupHelper", "Build JSON failed. ");
            CloudBackupException.trackException();
        }
        return jSONObject;
    }
}
