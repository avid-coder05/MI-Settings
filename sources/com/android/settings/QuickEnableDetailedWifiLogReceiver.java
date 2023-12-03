package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import com.android.settings.development.LogdSizePreferenceController;
import com.android.settings.development.SelectLogLevelPreferenceController;
import com.android.settingslib.util.ToastUtil;

/* loaded from: classes.dex */
public class QuickEnableDetailedWifiLogReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!"android.provider.Telephony.SECRET_CODE".equals(intent.getAction()) || intent.getData() == null || intent.getData().getHost() == null || !"94341".equals(intent.getData().getHost())) {
            return;
        }
        int i = Settings.Global.getInt(context.getContentResolver(), "detailed_wifi_log_enabled", 0);
        LogdSizePreferenceController logdSizePreferenceController = new LogdSizePreferenceController(context);
        SelectLogLevelPreferenceController selectLogLevelPreferenceController = new SelectLogLevelPreferenceController(context);
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (i != 0) {
            logdSizePreferenceController.writeLogdSizeOption(null);
            selectLogLevelPreferenceController.writeLogdLevelOption("Warn");
            wifiManager.setVerboseLoggingEnabled(false);
            ToastUtil.show(context, R.string.detailed_wifi_logging_disabled, 1);
            Settings.Global.putInt(context.getContentResolver(), "detailed_wifi_log_enabled", 0);
            return;
        }
        String[] stringArray = context.getResources().getStringArray(R.array.select_logd_size_values);
        logdSizePreferenceController.writeLogdSizeOption(stringArray[stringArray.length - 1]);
        selectLogLevelPreferenceController.writeLogdLevelOption("Verbose");
        wifiManager.setVerboseLoggingEnabled(true);
        ToastUtil.show(context, R.string.detailed_wifi_logging_enabled, 1);
        Settings.Global.putInt(context.getContentResolver(), "detailed_wifi_log_enabled", 1);
    }
}
