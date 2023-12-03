package com.android.settings.search.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.settings.cloud.CloudDataUpdateInjector;
import com.android.settings.search.appseparate.SeparateAppSearchHelper;
import com.android.settingslib.search.SearchUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
public class UpdateReceiver extends BroadcastReceiver {
    public static final String ACTION_CLOUD_DATA_UPDATE = "miui.intent.action.ACTION_CLOUD_DATA_UPDATE";
    public static final String ACTION_SEPARATE_APP_SEARCH_RESULT_UPDATE = "miui.intent.action.SEPARATE_APP_SEARCH_RESULT_UPDATE";
    private static ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        EXECUTOR.execute(new Runnable() { // from class: com.android.settings.search.provider.UpdateReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                String action = intent.getAction();
                if (action.equals("android.intent.action.PACKAGE_ADDED") || action.equals("android.intent.action.PACKAGE_REMOVED") || action.equals("android.intent.action.PACKAGE_CHANGED")) {
                    SearchUtils.clearPackageExistedCache();
                }
                boolean equals = action.equals("miui.intent.action.SETTINGS_SEARCH_INIT");
                SettingsTreeHelper settingsTreeHelper = SettingsTreeHelper.getInstance(context, equals);
                boolean equals2 = action.equals(UpdateReceiver.ACTION_SEPARATE_APP_SEARCH_RESULT_UPDATE);
                if (equals2) {
                    SeparateAppSearchHelper.forceUpdate(context, true);
                }
                boolean equals3 = action.equals(UpdateReceiver.ACTION_CLOUD_DATA_UPDATE);
                if (equals3) {
                    CloudDataUpdateInjector.onCloudDataUpdate(context.getApplicationContext());
                }
                if (settingsTreeHelper == null || equals || equals2 || equals3) {
                    return;
                }
                settingsTreeHelper.onReceive(context.getApplicationContext(), intent);
            }
        });
    }
}
