package com.android.settings.device;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

/* loaded from: classes.dex */
public class UpdateBroadcastManager {
    public static int getAppsAutoUpdateSuperscript(Context context) {
        return (getSuperscriptMap(context.getContentResolver()) & 4) == 0 ? 0 : 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getSuperscriptCount(int i) {
        int i2 = 0;
        while (i > 0) {
            i2++;
            i &= i - 1;
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getSuperscriptMap(ContentResolver contentResolver) {
        try {
            return Settings.Global.getInt(contentResolver, "com.android.settings.superscript_map");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void toggleSuperscript(Context context, Bundle bundle) {
        updateSuperscript(context, 1, bundle.getBoolean("state", false));
    }

    public static void updateSuperscript(Context context, final int i, final boolean z) {
        final Context applicationContext = context.getApplicationContext();
        new AsyncTask<Void, Void, Integer>() { // from class: com.android.settings.device.UpdateBroadcastManager.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Integer doInBackground(Void... voidArr) {
                ContentResolver contentResolver = applicationContext.getContentResolver();
                int superscriptMap = UpdateBroadcastManager.getSuperscriptMap(contentResolver);
                boolean z2 = z;
                int i2 = i;
                int i3 = z2 ? i2 | superscriptMap : (~i2) & superscriptMap;
                Settings.Global.putInt(contentResolver, "com.android.settings.superscript_map", i3);
                Settings.Global.putInt(contentResolver, "com.android.settings.superscript_count", UpdateBroadcastManager.getSuperscriptCount(i3));
                return Integer.valueOf(i3);
            }
        }.execute(new Void[0]);
    }
}
