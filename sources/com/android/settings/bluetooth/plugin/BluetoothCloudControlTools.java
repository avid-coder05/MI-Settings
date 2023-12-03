package com.android.settings.bluetooth.plugin;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.bluetooth.FitSplitUtils;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;
import java.io.File;

/* loaded from: classes.dex */
public class BluetoothCloudControlTools {
    private static int HEADSETPLUGIN_INITED;
    public static final int PROC_USER_ID;
    private static final String VERISON_URI;
    private Context mContext;
    private int mStatus;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("content://com.android.bluetooth.ble.app.headset.provider");
        String str = File.separator;
        sb.append(str);
        sb.append("plugin_update");
        sb.append(str);
        sb.append("plugin_version_info.json");
        VERISON_URI = sb.toString();
        HEADSETPLUGIN_INITED = 1;
        PROC_USER_ID = Process.myUserHandle().hashCode();
    }

    public BluetoothCloudControlTools(Context context, int i) {
        this.mContext = context;
        this.mStatus = i;
    }

    public boolean checkNewInfo() {
        if (SystemProperties.getBoolean("persist.bluetooth.disablemifastconnect", false)) {
            Log.d("BluetoothCloudControlTools", "do not try to check update for user forbid");
            return false;
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            if (this.mStatus != HEADSETPLUGIN_INITED) {
                Log.d("BluetoothCloudControlTools", "settings plugin not start");
                return false;
            }
            ContentResolver contentResolver = this.mContext.getContentResolver();
            StringBuilder sb = new StringBuilder();
            int i = PROC_USER_ID;
            sb.append(i);
            sb.append("#");
            sb.append("mi_bt_plugin_switch");
            if (Settings.Global.getInt(contentResolver, sb.toString(), 0) == 1) {
                Settings.Global.getInt(this.mContext.getContentResolver(), i + "#BLUETOOTHHEADSETPLUGIN", -1);
                if (Settings.Global.getInt(this.mContext.getContentResolver(), i + "#mi_bt_found_new_plugins", 0) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public void handlePreferenceTreeClick() {
        try {
            SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
            String currentSplitInfoVersion = splitInfoManagerService != null ? splitInfoManagerService.getCurrentSplitInfoVersion() : null;
            Log.i("BluetoothCloudControlTools", "BluetoothCloudControlPreferenceController handlePreferenceTreeClick get click event current_settings_verison = " + currentSplitInfoVersion);
            if (this.mContext != null) {
                Intent intent = new Intent();
                intent.setClassName("com.xiaomi.bluetooth", "com.android.bluetooth.ble.MiuiBluetoothUpdateActivity");
                intent.putExtra("CHECK_TRIGGER", 0);
                intent.putExtra("SETTINGS_VERSION", currentSplitInfoVersion);
                intent.putExtra("SETTINGS_MIUIX_VERSION", "settings_miuix_version_1");
                intent.setFlags(268468224);
                if (FitSplitUtils.isFitSplit()) {
                    intent.removeFlags(268435456);
                    intent.addMiuiFlags(16);
                }
                this.mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("BluetoothCloudControlTools", "error " + e);
        }
    }
}
