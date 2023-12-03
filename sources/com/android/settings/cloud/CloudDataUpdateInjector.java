package com.android.settings.cloud;

import android.app.UiModeManager;
import android.content.Context;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settingslib.search.KeywordsCloudConfigHelper;

/* loaded from: classes.dex */
public class CloudDataUpdateInjector {
    public static boolean isSupportNightModeByCloud(Context context) {
        return true;
    }

    public static void onCloudDataUpdate(Context context) {
        updateKeywordsCloudConfig(context);
        updateNightModeState(context);
    }

    private static void updateKeywordsCloudConfig(Context context) {
        KeywordsCloudConfigHelper.getInstance(context).updateKeywordsCloudConfig(context);
    }

    private static void updateNightModeState(Context context) {
        MiuiUtils.notifyNightModeShowStateChange(context);
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        if (isSupportNightModeByCloud(context) || uiModeManager.getNightMode() == 1) {
            return;
        }
        Log.w("CloudDataUpdateInjector", "close night mode by cloud service");
        uiModeManager.setNightMode(1);
    }
}
