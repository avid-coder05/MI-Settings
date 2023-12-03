package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;

/* loaded from: classes.dex */
public class OneTrackManager {
    public static void trackHelpClick(Context context, String str) {
        if (context == null) {
            Log.e("OneTrackManager", "ctx is null");
            return;
        }
        try {
            SplitInfoManager splitInfoManagerService = SplitInfoManagerService.getInstance();
            if (splitInfoManagerService == null) {
                Log.e("OneTrackManager", "buildIntent SplitInfoManager is null!");
                return;
            }
            Intent intent = new Intent("onetrack.action.TRACK_EVENT");
            intent.setPackage("com.miui.analytics");
            intent.putExtra("APP_ID", "31000000416");
            intent.putExtra("EVENT_NAME", "bluetooth_connection_help_click");
            intent.putExtra("PACKAGE", "com.xiaomi.bluetooth");
            intent.setFlags(1);
            intent.putExtra("version", splitInfoManagerService.getCurrentSplitInfoVersion());
            intent.putExtra("plugin_name", str);
            context.startServiceAsUser(intent, UserHandle.CURRENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
