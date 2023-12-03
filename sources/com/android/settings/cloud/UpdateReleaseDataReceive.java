package com.android.settings.cloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemProperties;
import android.util.Log;

/* loaded from: classes.dex */
public class UpdateReleaseDataReceive extends BroadcastReceiver {
    private static final Uri URI_CLOUD_DEVICE_RELEASED_NOTIFY = Uri.parse("content://com.android.settings.cloud.CloudSettings/device_released");

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.i("htmlviewercloudcontrol", "settings app : UpdateReleaseDataReceive onReceive");
        try {
        } catch (Throwable th) {
            Log.e("htmlviewercloudcontrol", "settings app : UpdateReleaseDataReceive  onReceive error : " + th.toString());
        }
        if (SystemProperties.getBoolean("persist.sys.released", false)) {
            return;
        }
        SystemProperties.set("persist.sys.released", "true");
        context.getContentResolver().notifyChange(URI_CLOUD_DEVICE_RELEASED_NOTIFY, null);
    }
}
