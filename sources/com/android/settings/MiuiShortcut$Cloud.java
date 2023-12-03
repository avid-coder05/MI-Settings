package com.android.settings;

import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import com.android.internal.os.BackgroundThread;

/* loaded from: classes.dex */
final class MiuiShortcut$Cloud {
    private static boolean showAlipayHealthCode;
    private static boolean showWeChatHealthCode;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateCacheValue$0(Context context) {
        Settings.System.putInt(context.getContentResolver(), "showAlipayHealthCode", MiuiSettings.SettingsCloudData.getCloudDataBoolean(context.getContentResolver(), "mst_GestureShortcut", "showAlipayHealthCode", true) ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateCacheValue(Context context) {
        final Context applicationContext = context.getApplicationContext();
        showAlipayHealthCode = Settings.System.getInt(applicationContext.getContentResolver(), "showAlipayHealthCode", 1) == 1;
        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.settings.MiuiShortcut$Cloud$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MiuiShortcut$Cloud.lambda$updateCacheValue$0(applicationContext);
            }
        });
    }
}
